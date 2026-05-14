package tsb.li.common.eventflow.state;

import java.util.function.Predicate;

public class Transition<S, E> {
	private S targetState;
    private Runnable action;
    private Predicate<TransitionContext<S, E>> condition;
    
    public Transition() {
    	// Builder에서 나중에 설정
    }

    public Transition(S targetState, Runnable action) {
        this.targetState = targetState;
        this.action = action;
    }
    
    public S getTargetState() {
        return targetState;
    }

    public void setTargetState(S targetState) {
        this.targetState = targetState;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
    
    public void setCondition(Predicate<TransitionContext<S, E>> condition) {
        this.condition = condition;
    }

    public boolean isAllowed(TransitionContext<S, E> context) {
        return condition == null || condition.test(context);
    }
}