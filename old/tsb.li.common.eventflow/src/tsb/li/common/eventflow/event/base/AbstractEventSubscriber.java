package tsb.li.common.eventflow.event.base;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.registry.EventHubRegistry;

public abstract class AbstractEventSubscriber implements Identifiable {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractEventSubscriber.class);
	
	// Event Methods for EventHub System
	private final EventHubRegistry eventRegistry;
	
	protected AbstractEventSubscriber(EventHubBase hub) {
		eventRegistry = new EventHubRegistry(hub);
	}
	
    /**
     * EventHubRegistry를 통해 EventHub에 특정 이벤트 타입의 리스너를 등록.
     * 자식 클래스에서 해당 메서드를 호출해서 리스너를 등록.    
     */
    protected <T extends EventBase> void registerListener(Class<T> eventType, Consumer<T> listener) {
    	eventRegistry.registerListener(getObjectID(), eventType, listener);
    }
    
    /**
     * 특정 이벤트 타입의 특정 리스너를 EventHubRegistry를 통해  EventHub에서 구독 해제.
     * 자식 클래스에서 해당 메서드를 호출해서 특정 리스너를 헤재.
     * 해당 EventType에 등록된 모든 리스너가 해제.
     */
    protected <T extends EventBase> void unregisterListener(Class<T> eventType) {
    	eventRegistry.unregisterListener(getObjectID(), eventType);
    }
    
    /**
     * EventHubRegistry를 통해 EventHub에 등록했던 모든 리스너를 구독 해제.
     */ 
    public <T extends EventBase> void unregisterAllListeners() {
    	eventRegistry.unregisterAllListeners();
    	logger.debug("{} unregistered all listeners OK!", getClass().getSimpleName());
        unregisterAllLocalListeners();
    }
    
    /**
     * 아래와 같이 특정 리스너를 삭제하는 것은 현재 구조상 불가능.
     * 특정 리스너 삭제를 위해서는 등록시 매번 메서드의 이름 자체를 String으로 넘겨야 하는데 이것은 관리가 어렵다.
     */
    //protected <T extends EventBase> void unregisterListener(Class<T> eventType, Consumer<T> listener)
    
    public abstract String getObjectID();
    
    protected abstract void initEventListener();    
    
    // Event Methods For Local
    /**
     * 모든 로컬 리스너를 디스패처에서 구독 해제.
     * 
     * 자식 클래스에서 구현해야 하며
     * 디스패처에서 관리 중인 모든 리스너를 제거.
     * 해당 함수는 부모 클래스에서 자동으로 호출.
     */
    protected abstract void unregisterAllLocalListeners();
}
