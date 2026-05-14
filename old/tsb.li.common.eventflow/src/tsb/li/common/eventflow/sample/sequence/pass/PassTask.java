package tsb.li.common.eventflow.sample.sequence.pass;

import tsb.li.common.eventflow.task.TaskBase;

public class PassTask extends TaskBase {

    @Override
    protected void onInit() {
    	addProcess(new PassProcess());
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