package tsb.li.common.eventflow.sample.event;

import tsb.li.common.eventflow.event.base.EventBase;

/**
 * SampleEvent:
 * 숫자 하나를 담은 단순한 이벤트입니다.
 */
public class SampleEvent extends EventBase {
	
	private final int testNumber;
	
	public SampleEvent(int testNumber) {
		this.testNumber = testNumber;
	}

	public int getTestNumber() {
		return testNumber;
	}
}
