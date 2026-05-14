package tsb.li.common.eventflow.sample.basic.global;

import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * GlobalEventPublisherSample:
 * 별도의 Dispatcher 인스턴스를 만들지 않고, EventHub 싱글톤을 통해
 * SampleEvent 및 OtherSampleEvent 를 전역(Global)으로 발행하는 예제입니다.
 */
public class GlobalEventPublisherSample {
	
	/**
     * SampleEvent 발행 예제
     */
	public void publishSampleEvent(int sampleNumber) {
		System.out.println("[Global Publisher] SampleEvent Published: testNumber = " + sampleNumber);
    	EventHub.getInstance().dispatchEvent(new SampleEvent(sampleNumber));
    }
	
	/**
     * OtherSampleEvent 발행 예제
     */
	public void publishOtherSampleEvent(int sampleNumber, String sampleString) {
		System.out.println("[Global Publisher] OtherSampleEvent Published: testNumber = " + sampleNumber +
                ", testString = " + sampleString);
    	EventHub.getInstance().dispatchEvent(new OtherSampleEvent(sampleNumber, sampleString));
    }    
}
