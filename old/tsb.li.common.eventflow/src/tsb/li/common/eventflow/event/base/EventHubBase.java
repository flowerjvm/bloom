package tsb.li.common.eventflow.event.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import tsb.li.common.eventflow.event.dispatchers.EventDispatcherGeneric;

/**
 * 해당 클래스를 상속하여서 Global 이벤트를 구현한다.
 * 상속 후 스프링 bean이나 싱글턴으로 구현하여서 코드 전역적으로 접근, 관리 및 주입가능
 */
public abstract class EventHubBase {
    private final Map<Class<?>, EventDispatcherGeneric<?>> dispatchers = new ConcurrentHashMap<>();

    protected EventHubBase() {}

    @SuppressWarnings("unchecked")
    protected <T extends EventBase> EventDispatcherGeneric<T> getDispatcher(Class<T> eventType) {
        return (EventDispatcherGeneric<T>) dispatchers.computeIfAbsent(eventType, key -> new EventDispatcherGeneric<>());
    }

    public <T extends EventBase> void registerListener(Class<T> eventType, String name, Consumer<T> listener) {
        getDispatcher(eventType).registerListener(name, listener);
    }

    public <T extends EventBase> void unregisterListenerByName(Class<T> eventType, String name) {
        getDispatcher(eventType).unregisterListenerByName(name);
    }

    public <T extends EventBase> void dispatchEvent(T event) {
        @SuppressWarnings("unchecked")
        Class<T> eventType = (Class<T>) event.getClass();
        getDispatcher(eventType).dispatchEvent(event);
    }

    public <T extends EventBase> void dispatchEventAsync(T event) {
        @SuppressWarnings("unchecked")
        Class<T> eventType = (Class<T>) event.getClass();
        getDispatcher(eventType).dispatchEventAsync(event);
    }
}