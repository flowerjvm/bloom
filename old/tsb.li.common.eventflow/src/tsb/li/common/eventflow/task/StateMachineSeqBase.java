package tsb.li.common.eventflow.task;

import tsb.li.common.eventflow.state.StateMachine;

public abstract class StateMachineSeqBase<S extends Enum<S>, E extends Enum<E>> extends SeqBase {
    protected StateMachine<S, E> fsm;

    public StateMachineSeqBase() {
        super(); // SeqBase 생성자 호출
    }

    @Override
    protected void action(int seqNo) {
    	if (fsm == null) {
            throw new IllegalStateException("FSM is not initialized. Call setFSM() or assign fsm before running.");
        }
    	action();
    }

    @Override
    protected boolean checkCompletion() {
        return fsm != null && fsm.getCurrentState() == getFinalState();
    }

    protected abstract void action();    
    protected abstract S getFinalState();
}
