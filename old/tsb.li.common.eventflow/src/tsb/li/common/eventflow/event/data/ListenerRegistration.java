package tsb.li.common.eventflow.event.data;

import java.util.function.Consumer;

import tsb.li.common.eventflow.event.base.EventBase;


public class ListenerRegistration<T extends EventBase> {
	private final Class<? extends T> eventType;
	private final String key;
	private final Consumer<T> listener;

    public ListenerRegistration(Class<? extends T> eventType, String key, Consumer<T> listener) {
        this.eventType = eventType;
        this.key = key;
        this.listener = listener;
    }

    public Class<? extends T> getEventType() {
        return eventType;
    }

    public String getKey() {
        return key;
    }

	public Consumer<T> getListener() {
		return listener;
	}
}
