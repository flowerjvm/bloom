package tsb.li.common.eventflow.sample.basic.local;

import tsb.li.common.eventflow.event.dispatchers.EventDispatcher;
import tsb.li.common.eventflow.event.manager.LocalEventSubscriber;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * LocalEventSubscriberSample:
 * 외부에서 Dispatcher를 주입받아 SampleEvent와 OtherSampleEvent를 구독하는 예제입니다.
 */
public class LocalEventSubscriberSample {
	
	/**
	 * Subscriber를 통해서 특정 Dispather에 구독하여야 합니다.
	 */
	LocalEventSubscriber eventSubscriber = null;
	
	/**
     * Dispatcher 주입을 받아 구독자 생성 및 리스너 등록
     */
	public LocalEventSubscriberSample(EventDispatcher dispatcher) {
		eventSubscriber = new LocalEventSubscriber(LocalEventSubscriberSample.class.getSimpleName(), dispatcher);
		initEventListener();		
	}
	
	/**
     * SampleEvent 수신 처리
     */
	private void handleSampleEvent(SampleEvent event) {
		System.out.println("[Local Subscriber] SampleEvent Received : testNumber = " + event.getTestNumber());
    }
	
	/**
     * OtherSampleEvent 수신 처리
     */
	private void handleOtherSampleEvent(OtherSampleEvent event) {
		System.out.println("[Local Subscriber] OtherSampleEvent Received : testNumber = " + event.getTestNumber() +
                ", testString = " + event.getTestString());
    }
	
	/**
     * 이벤트 리스너 등록
     */
	private void initEventListener() {
		eventSubscriber.registerListener(SampleEvent.class, this::handleSampleEvent);
		eventSubscriber.registerListener(OtherSampleEvent.class, this::handleOtherSampleEvent);
	}	
}
