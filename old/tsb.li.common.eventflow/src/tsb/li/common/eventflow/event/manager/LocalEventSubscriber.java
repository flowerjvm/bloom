package tsb.li.common.eventflow.event.manager;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.dispatchers.EventDispatcher;
import tsb.li.common.eventflow.event.registry.EventDispatcherRegistry;

public class LocalEventSubscriber {
	private static final Logger logger = LoggerFactory.getLogger(LocalEventSubscriber.class);
	
	private final EventDispatcherRegistry eventRegistry;
	private final String objectID;
	
	public LocalEventSubscriber(String objectID, EventDispatcher dispatcher) {
        this.objectID = objectID;
        this.eventRegistry = new EventDispatcherRegistry(dispatcher);
    }
	
    /**
     * EventDispatcherRegistry를 통해 LocalEvent에 특정 이벤트 타입의 리스너를 등록.
     */
    public <T extends EventBase> void registerListener(Class<T> eventType, Consumer<T> listener) {
    	eventRegistry.registerListener(objectID, eventType, listener);
    }
    
    /**
     * 특정 이벤트 타입의 특정 리스너를 EventDispatcherRegistry를 통해  LocalEvent에서 구독 해제.
     * 해당 EventType에 등록된 모든 리스너가 해제.
     */
    public <T extends EventBase> void unregisterListener(Class<T> eventType) {
    	eventRegistry.unregisterListener(objectID, eventType);
    }
    
    /**
     * EventDispatcherRegistry를 통해 LocalEvent에 등록했던 모든 리스너를 구독 해제.
     */ 
    public <T extends EventBase> void unregisterAllListeners() {
    	eventRegistry.unregisterAllListeners();
    	logger.debug("{} [{}] unregistered all listeners OK!", getClass().getSimpleName(), objectID);
    }
    
    public String getObjectID() {
        return objectID;
    }
}
