package tsb.li.common.eventflow.state;

@FunctionalInterface
public interface StateTransitionListener<S, E> {
    void onTransition(S from, E event, S to);
}
