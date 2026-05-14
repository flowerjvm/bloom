package tsb.li.common.eventflow.sample.basic.local;

/**
 * LocalMainSample:
 * 로컬(EventDispatcher 기반) 이벤트 시스템을 사용하는 발행/구독 예제 실행 클래스입니다.
 * Dispatcher 인스턴스를 직접 생성하여 발행자와 구독자가 동일한 Dispatcher를 공유합니다.
 */
public class MainSample {

	public static void main(String[] args) {
		
        // Publisher 생성
        LocalEventPublisherSample publisher = new LocalEventPublisherSample();
        
        // Subscriber 생성
        LocalEventSubscriberSample subscriber = new LocalEventSubscriberSample(publisher.getEventDispatcher());

        // 이벤트 발행
        publisher.publishSampleEvent(100);
        publisher.publishOtherSampleEvent(200, "Hello Local Event System");
    }
}
