package tsb.li.common.eventflow.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateDefinition<S, E> {
    private final S state;
    private final Map<E, List<Transition<S, E>>> transitions = new HashMap<>();
    private Runnable entryAction;
    private Runnable exitAction;

    StateDefinition(S state) {
        this.state = state;
    }
    
    public S getState() {
        return state;
    }
    
    public List<Transition<S, E>> getTransitions(E event) {
        return transitions.getOrDefault(event, Collections.emptyList());
    }

    public void addTransition(E event, Transition<S, E> transition) {
    	transitions.computeIfAbsent(event, k -> new ArrayList<>()).add(transition);
    }
    
    public Runnable getEntryAction() {
        return entryAction;
    }
    
    public void setEntryAction(Runnable action) {
        this.entryAction = action;
    }
    
    public Runnable getExitAction() {
        return exitAction;
    }
    
    public void setExitAction(Runnable action) {
        this.exitAction = action;
    } 
}