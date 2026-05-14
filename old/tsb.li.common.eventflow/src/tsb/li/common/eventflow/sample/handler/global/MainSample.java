package tsb.li.common.eventflow.sample.handler.global;

import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * GlobalHandlerMainSample:
 * 
 * 전역 이벤트 허브(EventHub)를 기반으로 하는 핸들러 예제 실행 클래스입니다.
 */
public class MainSample {
	
	public static void main(String[] args) {

        // 핸들러(구독자) 등록
		SampleGlobalEventHandler sampleHandler = new SampleGlobalEventHandler();
		OtherSampleGlobalEventHandler otherSampleHandler = new OtherSampleGlobalEventHandler();

        // 이벤트 발행 (전역 EventHub 사용)
        EventHub.getInstance().dispatchEvent(new SampleEvent(777));
        EventHub.getInstance().dispatchEvent(new OtherSampleEvent(888, "Handler Test Event"));
    }

}
