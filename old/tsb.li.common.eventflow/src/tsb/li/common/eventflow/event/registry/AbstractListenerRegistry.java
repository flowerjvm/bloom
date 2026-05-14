package tsb.li.common.eventflow.event.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.data.ListenerRegistration;

public abstract class AbstractListenerRegistry {

    private final List<ListenerRegistration<?>> registeredListeners = new ArrayList<>();

    protected <T extends EventBase> void addListener(Class<T> eventType, String key, Consumer<T> listener) {
        registeredListeners.add(new ListenerRegistration<>(eventType, key, listener));
    }

    protected <T extends EventBase> void removeListener(Class<T> eventType, String key) {
        registeredListeners.removeIf(registration ->
            registration.getEventType().equals(eventType) && registration.getKey().equals(key)
        );
    }

    protected <T extends EventBase> String getListenerName(String id, Class<T> eventType) {
        return id + "-" + eventType.getSimpleName();
    }

    protected List<ListenerRegistration<?>> getRegisteredListeners() {
        return registeredListeners;
    }
    
    public abstract <T extends EventBase> void registerListener(String id, Class<T> eventType, Consumer<T> listener);
    public abstract <T extends EventBase> void unregisterListener(String id, Class<T> eventType);
    public abstract <T extends EventBase> void unregisterAllListeners();
}
