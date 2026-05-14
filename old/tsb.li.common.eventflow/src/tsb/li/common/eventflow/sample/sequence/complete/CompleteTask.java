package tsb.li.common.eventflow.sample.sequence.complete;

import tsb.li.common.eventflow.task.TaskBase;

public class CompleteTask extends TaskBase {

    @Override
    protected void onInit() {
    	addProcess(new CompleteProcess());
    }
    
    @Override
	protected void initEventListener() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void unregisterAllLocalListeners() {
		// TODO Auto-generated method stub		
	}    
}