package tsb.li.common.eventflow.event.registry;

import java.util.function.Consumer;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.base.Identifiable;
import tsb.li.common.eventflow.event.data.ListenerRegistration;
import tsb.li.common.eventflow.event.dispatchers.EventDispatcher;

/**
 * EventDispatcher를 구독하는 쪽에서 반드시 Registry를 사용해서 구독을 관리하도록 구현한다.
 * EventDispatcher를 직접 사용  X
 * Registry에서 반드시 listener를 가지고 있어야 Dispatcher에서 listener가 GC에 의해 삭제되지 않는다.
 * Registry가 사라지게 되면 Dispatcher에 있는 listener는 weakReference이기 때문에 GC에 의해 삭제된다.
 * 
 * EventDispatcher는 국소적인 지역적(Local) 이벤트 통신을 담당한다.
 */
public class EventDispatcherRegistry extends AbstractListenerRegistry {
    private final EventDispatcher eventDispatcher;
    
    public EventDispatcherRegistry(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }
    
    /**
     * EventDispatcher에 특정 이벤트 타입의 리스너를 등록.
     * obj에는 각 인스턴스의 고유한 ID를 제공 해야함.
     */
    public <T extends EventBase> void registerListener(Identifiable obj, Class<T> eventType, Consumer<T> listener) {
        registerListener(obj.getObjectID(), eventType, listener);
    }
    
    /**
     * EventDispatcher에 특정 이벤트 타입으로 등록된 리스너를 해제.
     * obj에는 각 인스턴스의 고유한 ID를 제공 해야함.
     */
    public <T extends EventBase> void unregisterListener(Identifiable obj, Class<T> eventType) {
        unregisterListener(obj.getObjectID(), eventType);
    }
    
    /**     
     * Identifiable를 구현 한 경우에는 키가 반드시 제공되지만
     * 그 외에 객체에 구현시 키를 각자 구현하여서 String으로 넣어줄 것
     */
    @Override
    public <T extends EventBase> void registerListener(String id, Class<T> eventType, Consumer<T> listener) {
        String resolvedKey = getListenerName(id, eventType);
        addListener(eventType, resolvedKey, listener);
        eventDispatcher.registerListener(eventType, resolvedKey, listener);
    }

    /**     
     * Identifiable를 구현 한 경우에는 키가 반드시 제공되지만
     * 그 외에 객체에 구현시 키를 각자 구현하여서 String으로 넣어줄 것
     */
    @Override
    public <T extends EventBase> void unregisterListener(String id, Class<T> eventType) {
        String resolvedKey = getListenerName(id, eventType);
        eventDispatcher.unregisterListenerByName(resolvedKey);
        removeListener(eventType, resolvedKey);
    }

    /**
     * EventDispatcherRegistry에 등록된 모든 리스너를 해제.
     */
    @Override
    public <T extends EventBase> void unregisterAllListeners() {
        for (ListenerRegistration<?> registration : getRegisteredListeners()) {
            eventDispatcher.unregisterListenerByName(registration.getKey());
        }
        getRegisteredListeners().clear();
    }
}
