package tsb.li.common.eventflow.state;

public class TransitionContext<S, E> {
    private final S currentState;
    private final E event;
    private final Object payload;

    public TransitionContext(S currentState, E event, Object payload) {
        this.currentState = currentState;
        this.event = event;
        this.payload = payload;
    }

    public S getCurrentState() {
        return currentState;
    }

    public E getEvent() {
        return event;
    }

    public <T> T getPayload(Class<T> type) {
        return type.cast(payload);
    }
}