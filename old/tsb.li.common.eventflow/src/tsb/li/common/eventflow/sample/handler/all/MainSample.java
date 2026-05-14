package tsb.li.common.eventflow.sample.handler.all;

import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.sample.basic.local.LocalEventPublisherSample;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * Local and Global HandlerMainSample:
 * 
 * Local 이벤트와 전역이벤트를 기반으로 하는 핸들러 예제 실행 클래스입니다.
 * 구독 및 구독 취소 처리까지 예제로 구현되었습니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		// Publisher 생성
        LocalEventPublisherSample publisher = new LocalEventPublisherSample();

        // 핸들러(구독자) 등록
		SampleAllEventHandler sampleHandler = new SampleAllEventHandler(publisher.getEventDispatcher());		
		
		// Local 이벤트 발행
		publisher.getEventDispatcher().dispatchEvent(new SampleEvent(777));
		
		// Global 이벤트 발행
		EventHub.getInstance().dispatchEvent(new SampleEvent(123));
		
		
		// Local 이벤트 구독 취소
		sampleHandler.unregisterLocalSampleEvent();
		
		// Local 이벤트 발행
		System.out.println("[local Publisher] SampleEvent Published!");
		publisher.getEventDispatcher().dispatchEvent(new SampleEvent(777));
		
		
		// Global 이벤트 구독 취소
		sampleHandler.unregisterGlobalSampleEvent();
				
		// Global 이벤트 발행
		System.out.println("[Global Publisher] SampleEvent Published!");
		EventHub.getInstance().dispatchEvent(new SampleEvent(123));
    }
}
