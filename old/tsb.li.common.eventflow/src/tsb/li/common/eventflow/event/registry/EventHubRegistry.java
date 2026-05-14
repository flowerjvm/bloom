package tsb.li.common.eventflow.event.registry;

import java.util.function.Consumer;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.base.EventHubBase;
import tsb.li.common.eventflow.event.data.ListenerRegistration;

/**
 * EventHub를 통해 구독하는 쪽에서 반드시 Registry를 사용해서 구독을 관리하도록.
 * EventHub를 직접 사용  X
 * Registry에서 반드시 listener를 가지고 있어야 Dispatcher에서 listener가 GC에 의해 삭제되지 않는다.
 * Registry가 사라지게 되면 Dispatcher에 있는 listener는 weakReference이기 때문에 GC에 의해 삭제된다.
 */
public class EventHubRegistry extends AbstractListenerRegistry {
	private final EventHubBase eventHub;
	
	public EventHubRegistry(EventHubBase eventHub) {
        this.eventHub = eventHub;
    }

    /**
     * EventHub에 특정 이벤트 타입의 리스너를 등록.
     */
	@Override
    public <T extends EventBase> void registerListener(String id, Class<T> eventType, Consumer<T> listener) {
		String resolvedKey = getListenerName(id, eventType);
    	addListener(eventType, resolvedKey, listener);
    	eventHub.registerListener(eventType, resolvedKey, listener);        
    }
    
    /**
     * EventHub에 특정 이벤트 타입으로 등록된 모든 리스너를 구독 해제.
     *
     */
	@Override
    public <T extends EventBase> void unregisterListener(String id, Class<T> eventType) {
    	String resolvedKey = getListenerName(id, eventType);
    	eventHub.unregisterListenerByName(eventType, resolvedKey);
    	removeListener(eventType, resolvedKey);
    }
    
    /**
     * EventHub에 등록했모든 리스너를 구독 해제.
     * 해당 함수는 ProcessBase에서 자동으로 호출.
     */
	@Override
    @SuppressWarnings("unchecked")
    public <T extends EventBase> void unregisterAllListeners() {
        for (ListenerRegistration<?> registration : getRegisteredListeners()) {
        	eventHub.unregisterListenerByName((Class<T>)registration.getEventType(), registration.getKey());
        }        
        getRegisteredListeners().clear();
    }
}
