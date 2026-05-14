package tsb.li.common.eventflow.state;


public class StateDefinitionBuilder<S, E> {
    private final StateDefinition<S, E> definition;

    public StateDefinitionBuilder(StateDefinition<S, E> definition) {
        this.definition = definition;
    }

    public TransitionBuilder<S, E> when(E event) {
        Transition<S, E> transition = new Transition<>();
        definition.addTransition(event, transition);
        return new TransitionBuilder<>(transition, this);
    }

    public StateDefinitionBuilder<S, E> entry(Runnable action) {
        definition.setEntryAction(action);
        return this;
    }

    public StateDefinitionBuilder<S, E> exit(Runnable action) {
        definition.setExitAction(action);
        return this;
    }
}