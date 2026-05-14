package tsb.li.common.eventflow.sample.basic.global;

import tsb.li.common.eventflow.event.manager.EventHubSubscriber;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * GlobalEventSubscriberSample:
 * EventHubSubscriber를 통해서 EventHub 싱글톤에 직접 구독자로 등록하여,
 * 전역적으로 발행되는 이벤트를 수신하는 예제입니다.
 */
public class GlobalEventSubscriberSample {

	// 전역 이벤트 허브에 등록되는 Subscriber
	private final EventHubSubscriber eventSubscriber = new EventHubSubscriber(MainSample.class.getSimpleName());
	
	public GlobalEventSubscriberSample() {
		initEventListener();
	}
	
	/**
     * SampleEvent 수신 처리
     */
	private void handleSampleEvent(SampleEvent event) {
		System.out.println("[Global Subscriber] SampleEvent Received : testNumber = " + event.getTestNumber());
    }
	
	/**
     * OtherSampleEvent 수신 처리
     */
	private void handleOtherSampleEvent(OtherSampleEvent event) {
		System.out.println("[Global Subscriber] OtherSampleEvent Received : testNumber = " + event.getTestNumber() +
                ", testString = " + event.getTestString());
    }
	
	/**
     * EventHub에 이벤트 리스너 등록
     */
	private void initEventListener() {
		eventSubscriber.registerListener(SampleEvent.class, this::handleSampleEvent);
		eventSubscriber.registerListener(OtherSampleEvent.class, this::handleOtherSampleEvent);
	}	
}
