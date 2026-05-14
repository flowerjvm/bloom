package tsb.li.common.eventflow.sample.sequence.redirectwithComplete;

import tsb.li.common.eventflow.task.TaskBase;

public class RedirectTask extends TaskBase {

    @Override
    protected void onInit() {
    	addProcess(new RedirectWithCompleteProcess());
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