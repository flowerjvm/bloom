# Bloom

Bloom is a lightweight, pure-Java runtime event bus for internal application
events.

It is intended for in-process communication between runtime objects that should
not know about each other directly. Bloom is small on purpose: it gives you
typed publish/subscribe, explicit subscription handles, predictable dispatch
rules, and optional Spring integration without making Spring the owner of the
event model.

The important word is runtime. A Bloom bus is a normal Java object: create one,
pass it to a component, scope it to a feature, wrap it with an async executor,
replace it in a test, or let a workflow step subscribe for only the time it is
active.

## What Bloom Is For

- Publishing domain events inside one JVM.
- Decoupling modules without introducing a message broker.
- Creating event buses at runtime for a component, workflow, test, or adapter.
- Dynamically subscribing and unsubscribing handlers while objects are alive.
- Building framework adapters that need a minimal event bus SPI.
- Keeping tests deterministic with a local in-memory bus.
- Wiring Spring bean methods with `@Subscribe` when the application happens to
  run inside Spring.

Bloom is not a distributed event system, persistent queue, retry engine, or
transaction manager. If an event must survive process failure or cross service
boundaries, use a real messaging system and adapt it separately.

## Why Not Just Spring Events?

Spring already has application events, and they are useful when the event bus is
part of the Spring `ApplicationContext`. Bloom exists for a different center of
gravity: runtime-scoped internal events.

Use Spring events when:

- the publisher and listener are Spring-managed beans,
- the event is naturally application-context-wide,
- lifecycle and listener discovery should be owned by Spring,
- you are happy to depend on Spring infrastructure in that layer.

Use Bloom when:

- the code should run without Spring,
- an event bus needs to be created per runtime object, test, workflow, or module,
- subscriptions should be explicit handles that can be closed immediately,
- a component should decide exactly when it starts and stops listening,
- the same event SPI should work in core Java, Spring, and framework adapters.

In other words, Spring events are application-context events. Bloom events are
runtime object events. Bloom can be used from Spring, but it does not require
Spring to define the event boundary.

## Modules

- `bloom-core`: dependency-free event bus API and implementations.
- `bloom-spring`: Spring Framework integration with `@EnableBloom` and
  `@Subscribe`.

## Core Concepts

### EventBus

`EventBus` is the main API:

```java
EventBus bus = LocalEventBus.create();

Subscription subscription = bus.subscribe(OrderPlaced.class, event -> {
    System.out.println("order placed: " + event.orderId());
});

bus.publish(new OrderPlaced("order-1"));
subscription.close();
```

Events are plain Java objects. They do not need to extend a base class or
implement a marker interface.

### Exact Type Matching

Bloom dispatches by exact runtime class:

```java
bus.subscribe(ParentEvent.class, event -> handleParent(event));
bus.subscribe(ChildEvent.class, event -> handleChild(event));

bus.publish(new ChildEvent());
```

Only the `ChildEvent` handler receives the event above. A handler registered for
`ParentEvent.class` does not receive subclasses. This keeps dispatch predictable
and avoids accidental broad subscriptions.

### Subscriptions

`subscribe(...)` returns a `Subscription`. Keep it if the listener has a shorter
lifetime than the bus:

```java
Subscription sub = bus.subscribe(CacheInvalidated.class, this::invalidate);

// later
sub.close();
```

Closing a subscription is idempotent.

### Handler Errors

`LocalEventBus` isolates handler failures. One failing handler does not prevent
other handlers from receiving the same event.

```java
LocalEventBus bus = LocalEventBus.create();

bus.onListenerError((event, handler, cause) -> {
    // log, count, or surface listener failures here
});
```

If no error handler is installed, listener failures are ignored.

## Synchronous Dispatch

`LocalEventBus` dispatches on the publishing thread:

```java
LocalEventBus bus = LocalEventBus.create();
bus.subscribe(UserRegistered.class, event -> sendWelcomeMail(event.userId()));

bus.publish(new UserRegistered("u-1"));
```

Use this when you want deterministic behavior and simple tests.

## Asynchronous Dispatch

`AsyncEventBus` wraps another bus and schedules `publish(...)` on an executor.
Bloom does not own or shut down the executor; the caller owns its lifecycle.

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
EventBus bus = new AsyncEventBus(LocalEventBus.create(), executor);

bus.subscribe(OrderPaid.class, event -> reserveInventory(event.orderId()));
bus.publish(new OrderPaid("order-1"));

executor.shutdown();
```

Subscriptions still belong to the delegate bus. Only publishing is scheduled
asynchronously.

## Subscriber Helpers

Bloom handlers can be written as lambdas for small cases, but you can also make
event handling explicit with classes. This is useful when a handler owns
dependencies, has a lifecycle, or should be easy to find from an IDE or by an
AI coding assistant.

### Implement `EventHandler<E>` Directly

`EventHandler<E>` is a functional interface, so a handler class only needs one
method:

```java
public final class SendWelcomeMailHandler implements EventHandler<UserRegistered> {
    private final MailService mailService;

    public SendWelcomeMailHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void handle(UserRegistered event) {
        mailService.sendWelcomeMail(event.userId());
    }
}

EventBus bus = LocalEventBus.create();
SendWelcomeMailHandler handler = new SendWelcomeMailHandler(mailService);

Subscription sub = bus.subscribe(UserRegistered.class, handler);
bus.publish(new UserRegistered("u-1"));
```

Use this style when the subscription is owned by application setup code.

### Extend `AbstractTypedEventHandler<E>`

Use `AbstractTypedEventHandler<E>` when the handler object should know its own
event type and manage its own subscription:

```java
public final class InventoryReservedHandler
        extends AbstractTypedEventHandler<InventoryReserved> {

    private final InventoryProjection projection;

    public InventoryReservedHandler(InventoryProjection projection) {
        super(InventoryReserved.class);
        this.projection = projection;
    }

    @Override
    protected void onEvent(InventoryReserved event) {
        projection.markInventoryReserved(event.orderId());
    }
}

InventoryReservedHandler handler = new InventoryReservedHandler(projection);
handler.subscribeTo(bus);

// later, when this handler is no longer needed
handler.close();
```

This keeps the "one handler handles one event type" rule visible in the class
itself.

### Group Related Subscriptions

Use `AbstractEventSubscriber` when one object owns several subscriptions:

```java
public final class OrderProjection extends AbstractEventSubscriber {
    private final OrderReadModel readModel;

    public OrderProjection(EventBus bus, OrderReadModel readModel) {
        this.readModel = readModel;
        on(bus, OrderPlaced.class, this::onOrderPlaced);
        on(bus, OrderPaid.class, this::onOrderPaid);
        on(bus, OrderCancelled.class, this::onOrderCancelled);
    }

    private void onOrderPlaced(OrderPlaced event) {
        readModel.create(event.orderId());
    }

    private void onOrderPaid(OrderPaid event) {
        readModel.markPaid(event.orderId());
    }

    private void onOrderCancelled(OrderCancelled event) {
        readModel.markCancelled(event.orderId());
    }
}
```

Calling `close()` releases every tracked subscription:

```java
OrderProjection projection = new OrderProjection(bus, readModel);

// later
projection.close();
```

Use this style for read models, in-memory projections, adapters, or components
that naturally listen to a small group of related events.

For very small examples, lambdas are still fine:

```java
bus.subscribe(OrderPlaced.class, event -> readModel.create(event.orderId()));
```

For production code, prefer named handler classes when that makes ownership,
dependencies, and lifecycle clearer.

## Spring Integration

`bloom-spring` does not turn Bloom into Spring application events. It simply
registers Spring bean methods as handlers on a Bloom `EventBus`.

Add `bloom-spring`, enable Bloom, and annotate bean methods:

```java
@Configuration
@EnableBloom
class AppConfig {
}

@Component
class OrderEventHandlers {

    @Subscribe
    public void on(OrderPlaced event) {
        // method must have exactly one parameter
    }
}
```

`@EnableBloom` imports a default `EventBus` if none exists and registers the bean
post-processor that scans `@Subscribe` methods.

You can also provide your own `EventBus` bean if you want a specific runtime
scope or an async wrapper.

## Design Rules

- Keep event classes small and immutable.
- Treat Bloom events as in-memory notifications, not durable facts.
- Decide the bus scope deliberately: global JVM bus, feature-local bus,
  workflow-local bus, test-local bus, or Spring bean.
- Subscribe to concrete event classes; dispatch is exact-type by design.
- Keep handler work short when using `LocalEventBus`; long work should move to
  an executor or a service boundary.
- Always close subscriptions owned by temporary objects.
- Prefer one event type per handler method or handler class.
- Install an error handler in production if listener failures should be visible.

## Build

```bash
mvn test
mvn install
```
