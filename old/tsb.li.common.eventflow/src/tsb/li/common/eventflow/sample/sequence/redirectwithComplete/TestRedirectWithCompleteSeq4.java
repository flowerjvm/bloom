package tsb.li.common.eventflow.sample.sequence.redirectwithComplete;

import tsb.li.common.eventflow.task.SeqBase;
import tsb.li.common.eventflow.util.Constants.SeqState;

public class TestRedirectWithCompleteSeq4 extends SeqBase {
    
    public TestRedirectWithCompleteSeq4() {
    	
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
    			System.out.println("TestRedirectWithCompleteSeq4 Start!");
    			super.setNextSeq(10);
    			super.startTick();
    		}
    		break;
    	
    	case 10:
    		{
    			if (super.getElapsedTicks() > 5000) {
    				// 가장 마지막은 Complete 처리하여 프로세스 최종 완료
    				super.setState(SeqState.COMPLETED);
        			System.out.println("TestRedirectWithCompleteSeq4 is Completed!");
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