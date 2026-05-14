package tsb.li.common.eventflow.sample.basic.local;

import tsb.li.common.eventflow.event.dispatchers.EventDispatcher;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * LocalEventPublisherSample:
 * 로컬 Dispatcher를 통해 SampleEvent 및 OtherSampleEvent 를 발행하는 예제입니다.
 */
public class LocalEventPublisherSample {
	
	/**
	 * 외부에 이벤트를 발행할 Dispatcher를 선언합니다.
	 */
	private final EventDispatcher eventSampleDispatcher = new EventDispatcher();
	
	public LocalEventPublisherSample() {
		
	}	
	
	/**
     * 외부에서 Dispatcher를 구독자에게 전달할 수 있도록 Getter 제공
     */
	public EventDispatcher getEventDispatcher() {
        return eventSampleDispatcher;
    }	

	/**
     * SampleEvent 발행 메서드
     */
    public void publishSampleEvent(int sampleNumber) {
    	System.out.println("[local Publisher] SampleEvent Published: testNumber = " + sampleNumber);
    	eventSampleDispatcher.dispatchEvent(new SampleEvent(sampleNumber));
    }
    
    /**
     * OtherSampleEvent 발행 메서드
     */
    public void publishOtherSampleEvent(int sampleNumber, String sampleString) {
    	System.out.println("[Global Publisher] OtherSampleEvent Published: testNumber = " + sampleNumber +
                ", testString = " + sampleString);
    	eventSampleDispatcher.dispatchEvent(new OtherSampleEvent(sampleNumber, sampleString));
    }
}
