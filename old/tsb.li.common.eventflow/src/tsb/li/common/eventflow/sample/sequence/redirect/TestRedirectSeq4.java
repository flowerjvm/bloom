package tsb.li.common.eventflow.sample.sequence.redirect;

import tsb.li.common.eventflow.task.SeqBase;
import tsb.li.common.eventflow.util.Constants.SeqState;

public class TestRedirectSeq4 extends SeqBase {
    
    public TestRedirectSeq4() {
    	
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
    			System.out.println("TestRedirectSeq4 Start!");
    			super.setNextSeq(10);
    			super.startTick();
    		}
    		break;
    	
    	case 10:
    		{
    			if (super.getElapsedTicks() > 5000) {
    				System.out.println("TestRedirectSeq4 Redirect to <1>!");
    				super.setNextSeq(TestRedirectSeq1.class);
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