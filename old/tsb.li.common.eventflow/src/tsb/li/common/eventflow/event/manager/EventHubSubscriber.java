package tsb.li.common.eventflow.event.manager;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.base.EventHubBase;
import tsb.li.common.eventflow.event.registry.EventHubRegistry;

public class EventHubSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(EventHubSubscriber.class);

	private final EventHubRegistry eventRegistry;
	private final String objectID;
	
	 /**
     * 사용할 전역 이벤트허브 설정, 없으면 null로 넣으면되며, 기본 EventHub를 사용하게 된다.
     */
	
	public EventHubSubscriber(String objectID) {
        this(objectID, null);
    }
	
	public EventHubSubscriber(String objectID, EventHubBase hub) {
        this.objectID = objectID;
        eventRegistry = new EventHubRegistry(hub == null ? EventHub.getInstance() : hub);
    }
	
    /**
     * EventHubRegistry를 통해 EventHub에 특정 이벤트 타입의 리스너를 등록.
     */
    public <T extends EventBase> void registerListener(Class<T> eventType, Consumer<T> listener) {
    	eventRegistry.registerListener(objectID, eventType, listener);
    }
    
    /**
     * 특정 이벤트 타입의 특정 리스너를 EventHubRegistry를 통해  EventHub에서 구독 해제.
     * 해당 EventType에 등록된 모든 리스너가 해제.
     */
    public <T extends EventBase> void unregisterListener(Class<T> eventType) {
    	eventRegistry.unregisterListener(objectID, eventType);
    }
    
    /**
     * EventHubRegistry를 통해 EventHub에 등록했던 모든 리스너를 구독 해제.
     */ 
    public <T extends EventBase> void unregisterAllListeners() {
    	eventRegistry.unregisterAllListeners();
        logger.debug("{} [{}] unregistered all listeners OK!", getClass().getSimpleName(), objectID);
    }
    
    public String getObjectID() {
        return objectID;
    }
}
