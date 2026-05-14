package tsb.li.common.eventflow.sample.sequence.complete;

import tsb.li.common.eventflow.task.ProcessBase;

public class CompleteProcess extends ProcessBase {
	
    public CompleteProcess() {
        
    }

    @Override
    protected void onInit() {
    	addSeq(new TestCompSeq1());
    	addSeq(new TestCompSeq2());
    	addSeq(new TestCompSeq3());
    }
}
