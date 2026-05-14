package tsb.li.common.eventflow.sample.basic.global;

/**
 * GlobalMainSample:
 * 전역(EventHub 기반) 이벤트 시스템을 사용하는 발행/구독 예제 실행 클래스입니다.
 * Dispatcher 인스턴스를 직접 만들지 않고, EventHub.getInstance()를 통해 발행/구독합니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		// Publisher 생성
        GlobalEventPublisherSample publisher = new GlobalEventPublisherSample();
        
        // Subscriber 생성
        GlobalEventSubscriberSample subscriber = new GlobalEventSubscriberSample();
        
        // 이벤트 발행
        publisher.publishSampleEvent(123);
        publisher.publishOtherSampleEvent(456, "Hello Global Event System");
    }

}
