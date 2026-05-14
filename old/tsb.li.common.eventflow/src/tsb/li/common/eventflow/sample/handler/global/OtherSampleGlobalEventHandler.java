package tsb.li.common.eventflow.sample.handler.global;

import tsb.li.common.eventflow.event.base.EventHandlerBase;
import tsb.li.common.eventflow.sample.event.OtherSampleEvent;

/**
 * OtherSampleGlobalEventHandler:
 * 
 * 전역 이벤트 허브(EventHub)를 통해 발행되는 OtherSampleEvent 를 처리하는 핸들러입니다.
 * 
 * 이 핸들러는 하나의 이벤트 타입에 대한 처리만 담당합니다.
 * 다른 이벤트를 처리하려면 별도의 핸들러 클래스를 만들어야 하며
 * 동일한 이벤트에 대한 추가적인 핸들러 처리는 또한 별도의 핸들러를 구현해서 처리해야 합니다.
 */
public class OtherSampleGlobalEventHandler extends EventHandlerBase<OtherSampleEvent> {
	
	public OtherSampleGlobalEventHandler () {
		initEventListener();
	}
	
	@Override
	protected void handleEvent(OtherSampleEvent event) {
		System.out.println("[OtherSampleEventHandler] Global Event Received OtherSampleEvent: testNumber = " +
                event.getTestNumber() + ", testString = " + event.getTestString());
    }

	@Override
	protected void initEventListener() {
		registerListener(OtherSampleEvent.class, this::handleEvent);		
	}

}
