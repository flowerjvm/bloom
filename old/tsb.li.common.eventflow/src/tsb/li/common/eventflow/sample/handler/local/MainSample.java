package tsb.li.common.eventflow.sample.handler.local;

import tsb.li.common.eventflow.sample.basic.local.LocalEventPublisherSample;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * GlobalHandlerMainSample:
 * 
 * Local 이벤트를 기반으로 하는 핸들러 예제 실행 클래스입니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		// Publisher 생성
        LocalEventPublisherSample publisher = new LocalEventPublisherSample();

        // 핸들러(구독자) 등록
		SampleLocalEventHandler sampleHandler = new SampleLocalEventHandler(publisher.getEventDispatcher());
		OtherSampleLocalEventHandler otherSampleHandler = new OtherSampleLocalEventHandler(publisher.getEventDispatcher());

        // Local 이벤트 발행 방법 1
		publisher.getEventDispatcher().dispatchEvent(new SampleEvent(777));
		publisher.getEventDispatcher().dispatchEvent(new OtherSampleEvent(888, "Handler Test Event"));
		
		// Local 이벤트 발행 방법 2
		publisher.publishSampleEvent(123);
		publisher.publishOtherSampleEvent(456, "Handler Test Event2");
    }

}
