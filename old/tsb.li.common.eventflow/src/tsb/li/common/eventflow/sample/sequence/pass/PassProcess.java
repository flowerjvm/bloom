package tsb.li.common.eventflow.sample.sequence.pass;

import tsb.li.common.eventflow.task.ProcessBase;

public class PassProcess extends ProcessBase {
	
    public PassProcess() {
        
    }

    @Override
    protected void onInit() {
    	addSeq(new TestPassSeq1());
    	addSeq(new TestPassSeq2());
    	addSeq(new TestPassSeq3());
    }
}
