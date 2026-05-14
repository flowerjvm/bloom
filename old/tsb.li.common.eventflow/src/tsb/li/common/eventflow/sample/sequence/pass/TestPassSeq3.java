package tsb.li.common.eventflow.sample.sequence.pass;

import tsb.li.common.eventflow.task.SeqBase;
import tsb.li.common.eventflow.util.Constants.SeqState;

public class TestPassSeq3 extends SeqBase {
    
    public TestPassSeq3() {
    	
    }
    
    @Override
    protected void onInit() {
		initEventListener();
	}	

    @Override
    protected void action(int seqNo) {
    	
    	switch (seqNo) {
    	
        case 0:
        	{
        		System.out.println("TestPassSeq3 Start!");
        		super.setNextSeq(10);
    			super.startTick();
        	}
        	break;
        	
        case 10:
        	{
        		if (super.getElapsedTicks() > 5000) {
        			System.out.println("TestPassSeq3 Pass!");
            		setState(SeqState.PASS);
    			}
        	}
        	break;
        }
    }

    @Override
	protected void initHandlingMembers() {
		// TODO Auto-generated method stub		
	}
    
    @Override
    protected boolean checkCompletion() {
        return false;
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