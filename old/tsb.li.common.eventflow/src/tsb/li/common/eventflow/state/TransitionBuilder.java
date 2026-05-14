package tsb.li.common.eventflow.state;

import java.util.function.Predicate;

public class TransitionBuilder<S, E> {
    private final Transition<S, E> transition;
    private final StateDefinitionBuilder<S, E> parent;

    public TransitionBuilder(Transition<S, E> transition, StateDefinitionBuilder<S, E> parent) {
        this.transition = transition;
        this.parent = parent;
    }
    
    public TransitionBuilder<S, E> ifCondition(Predicate<TransitionContext<S, E>> condition) {
        transition.setCondition(condition);
        return this;
    }

    public TransitionBuilder<S, E> goTo(S targetState) {
        transition.setTargetState(targetState);
        return this;
    }

    public StateDefinitionBuilder<S, E> doAction(Runnable action) {
        transition.setAction(action);
        return parent;
    }
    
    public StateDefinitionBuilder<S, E> end() {
        return parent;
    }
}