package tsb.li.common.eventflow.sample.handler.all;

import tsb.li.common.eventflow.event.base.EventHandlerBase;
import tsb.li.common.eventflow.event.dispatchers.EventDispatcher;
import tsb.li.common.eventflow.sample.event.SampleEvent;

/**
 * SampleLocalEventHandler:
 * 
 * Local 및 Global을 통해 발행되는 SampleEvent 를 처리하는 핸들러입니다.
 * 
 * 이 핸들러는 하나의 이벤트 타입에 대한 처리만 담당합니다.
 * 다른 이벤트를 처리하려면 별도의 핸들러 클래스를 만들어야 하며
 * 동일한 이벤트에 대한 추가적인 핸들러 처리는 또한 별도의 핸들러를 구현해서 처리해야 합니다.
 */
public class SampleAllEventHandler extends EventHandlerBase<SampleEvent> {
	
	public SampleAllEventHandler (EventDispatcher dispatcher) {
		super(dispatcher);
		initEventListener();
	}
	
	@Override
	protected void handleEvent(SampleEvent event) {
		System.out.println("[SampleEventHandler] Local Event Received SampleEvent: testNumber = " + event.getTestNumber());
    }
	
	protected void handleGlobalEvent(SampleEvent event) {
		System.out.println("[SampleEventHandler] Global Event Received SampleEvent: testNumber = " + event.getTestNumber());
    }

	@Override
	protected void initEventListener() {
		registerLocalListener(SampleEvent.class, this::handleEvent);
		registerListener(SampleEvent.class, this::handleGlobalEvent);
	}
	
	/**
	 * 아래와 같은 방식으로 특정 이벤트 타입에 대해서 Local 디스패처에 구독취소 가능
	 * 아래 예시는 외부에서 구독취소 하기 위해서 임시 구현
	 */
	public void unregisterLocalSampleEvent() {
		unregisterLocalListener(SampleEvent.class);
	}
	
	/**
	 * 아래와 같은 방식으로 특정 이벤트 타입에 대해서 Global Event에 구독취소 가능
	 * 아래 예시는 외부에서 구독취소 하기 위해서 임시 구현
	 */
	public void unregisterGlobalSampleEvent() {
		unregisterListener(SampleEvent.class);
	}
}
