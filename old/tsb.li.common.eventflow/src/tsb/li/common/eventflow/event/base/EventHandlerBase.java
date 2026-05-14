package tsb.li.common.eventflow.event.base;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.dispatchers.EventDispatcher;
import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.event.registry.EventDispatcherRegistry;
import tsb.li.common.eventflow.util.UniqueIDGenerator;

public abstract class EventHandlerBase<T extends EventBase> extends AbstractEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(EventHandlerBase.class);

	private final String objectID; // 인스턴스 객체의 고유 ID	
	private final EventDispatcherRegistry eventRegistry;
	
	public EventHandlerBase() {
        this(null);
    }
	
	public EventHandlerBase(EventDispatcher dispatcher) {
        this(dispatcher, null);
    }
	
	public EventHandlerBase(EventDispatcher dispatcher, EventHubBase hub) {
        this(dispatcher, hub, null);
    }
	
	private EventHandlerBase(EventDispatcher dispatcher, EventHubBase hub, String objectID) {
		// GlobalBus 설정
		super(hub == null ? EventHub.getInstance() : hub);
		
		if (objectID == null) {
            objectID = UniqueIDGenerator.generateID(getClass().getSimpleName());
        }		
        this.objectID = objectID;
        
        // LocalBus 설정
        if (dispatcher != null) this.eventRegistry = new EventDispatcherRegistry(dispatcher);
        else this.eventRegistry = null;  // Dispatcher가 없는 경우는 Local Dispatcher를 사용하지 않는 경우..
        
        logger.debug("Created EventHandler [{}] ({})", getClass().getSimpleName(), this.objectID);
    }
	
	@Override
	public String getObjectID() {
    	return objectID;
    }
	
	
	protected void registerLocalListener(Class<T> eventType, Consumer<T> listener) {
        if (eventRegistry != null) {
            eventRegistry.registerListener(this, eventType, listener);
            logger.debug("{} [{}] registered local listener for event: {}", getClass().getSimpleName(), objectID, eventType.getSimpleName());
        } else {
            logger.warn("{} [{}] failed to register local listener: eventRegistry is null", getClass().getSimpleName(), objectID);

        }
    }
	
	protected void unregisterLocalListener(Class<T> eventType) {
        if (eventRegistry != null) {
            eventRegistry.unregisterListener(this, eventType);
            logger.debug("{} [{}] unregistered local listener for event: {}", getClass().getSimpleName(), objectID, eventType.getSimpleName());

        } else {
            logger.warn("{} [{}] failed to unregister local listener: eventRegistry is null", getClass().getSimpleName(), objectID);
        }
    }
	
	@Override
	protected void unregisterAllLocalListeners() {
		if (eventRegistry != null) {
			eventRegistry.unregisterAllListeners();
            logger.debug("{} [{}] unregistered all local listeners", getClass().getSimpleName(), objectID);

		}		
	}	
	
	protected abstract void handleEvent(T event);
}
