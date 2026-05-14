package tsb.li.common.eventflow.state;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachine<S extends Enum<S>, E extends Enum<E>> {
	
	private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    private final Map<S, StateDefinition<S, E>> stateMap;
    private S currentState;
    
    private StateTransitionListener<S, E> transitionListener;

    public StateMachine(Class<S> stateClass) {
        this.stateMap = new EnumMap<>(stateClass);
    }

    public StateDefinitionBuilder<S, E> state(S state) {
        StateDefinition<S, E> definition = new StateDefinition<>(state);
        stateMap.put(state, definition);
        return new StateDefinitionBuilder<>(definition);
    }

    public void start(S initialState) {
        this.currentState = initialState;
        StateDefinition<S, E> def = stateMap.get(initialState);
        if (def != null && def.getEntryAction() != null) {
            def.getEntryAction().run();
        }
    }
    
    public void fire(E event) {
        fire(event, null);
    }

    public void fire(E event, Object payload) {
    	if (currentState == null) {
            logger.warn("FSM not started yet.");
            return;
        }

        StateDefinition<S, E> def = stateMap.get(currentState);
        if (def == null) {
            logger.error("State [{}] not defined", currentState);
            return;
        }

        List<Transition<S, E>> transitions = def.getTransitions(event);
        if (transitions == null || transitions.isEmpty()) {
            logger.debug("No transitions defined for event [{}] in state [{}]", event, currentState);
            return;
        }
        
        TransitionContext<S, E> context = new TransitionContext<>(currentState, event, payload);
        
        for (Transition<S, E> transition : transitions) {
            if (transition.isAllowed(context)) {
                S from = currentState;
                S to = transition.getTargetState();

                if (def.getExitAction() != null) def.getExitAction().run();
                if (transition.getAction() != null) transition.getAction().run();

                currentState = to;
                logger.info("Transitioned: [{}] --({})--> [{}]", from, event, to);

                StateDefinition<S, E> nextDef = stateMap.get(to);
                if (nextDef != null && nextDef.getEntryAction() != null) {
                    nextDef.getEntryAction().run();
                }

                if (transitionListener != null) {
                    transitionListener.onTransition(from, event, to);
                }
                return; // transition matched, exit
            }
        }
        
        logger.debug("No matching transition condition for event [{}] in state [{}]", event, currentState);
    }

    public S getCurrentState() {
        return currentState;
    }
    
    public void setTransitionListener(StateTransitionListener<S, E> listener) {
        this.transitionListener = listener;
    }
}
