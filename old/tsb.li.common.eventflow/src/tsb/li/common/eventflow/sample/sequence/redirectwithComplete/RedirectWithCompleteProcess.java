package tsb.li.common.eventflow.sample.sequence.redirectwithComplete;

import tsb.li.common.eventflow.task.ProcessBase;

public class RedirectWithCompleteProcess extends ProcessBase {
	
    public RedirectWithCompleteProcess() {
        
    }

    /**
     * Redirection은 Type 기반으로 Seq 클래스를 변경하기 때문에
     * Redirection 사용시 동일한 시퀀스 타입을 중복해서 사용하면 안됩니다.
     * 동일한 시퀀스 타입은 하나만 사용해야합니다.
     * 기본적으로는 아래 순서를 따르지만 redirection 중에 순서는 바뀝니다.
     * 항상 RedirectTo 메서드를 호출해서 움직어야합니다.
     */
    @Override
    protected void onInit() {
    	addSeq(new TestRedirectWithCompleteSeq1());
    	addSeq(new TestRedirectWithCompleteSeq2());
    	addSeq(new TestRedirectWithCompleteSeq3());
    	addSeq(new TestRedirectWithCompleteSeq4());
    }
}
