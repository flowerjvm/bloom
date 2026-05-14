package tsb.li.common.eventflow.sample.event;

import tsb.li.common.eventflow.event.base.EventBase;

/**
 * OtherSampleEvent:
 * 숫자와 문자열을 함께 전달하는 이벤트입니다.
 */
public class OtherSampleEvent extends EventBase {

	private final int testNumber;
	private final String testString;
	
	public OtherSampleEvent(int testNumber, String testString) {
		this.testNumber = testNumber;
		this.testString = testString;
	}

	public int getTestNumber() {
		return testNumber;
	}

	public String getTestString() {
		return testString;
	}	
}
