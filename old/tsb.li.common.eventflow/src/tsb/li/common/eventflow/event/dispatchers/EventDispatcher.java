package tsb.li.common.eventflow.event.dispatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.data.ListenerWrapper;
import tsb.li.common.eventflow.util.Constants;

public class EventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    private final Map<Class<? extends EventBase>, List<ListenerWrapper<? extends EventBase>>> listeners = new HashMap<>();
    private long lastCleanUpTime = 0;

    public synchronized <T extends EventBase> void registerListener(Class<T> eventType, String name, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                 .add(new ListenerWrapper<>(name, listener));
        logger.debug("Registered listener [{}] for event type [{}]", name, eventType.getSimpleName());

    }
    
    public synchronized void unregisterListenerByName(String name) {
        for (List<ListenerWrapper<? extends EventBase>> wrappers : listeners.values()) {
            wrappers.removeIf(wrapper -> {
                Consumer<? extends EventBase> refListener = wrapper.getListener();
                if (refListener == null) {
                    logger.info("Listener [{}] was already garbage collected.", wrapper.getName());
                    return true;
                }
                if (wrapper.getName().equals(name)) {
                	logger.debug("Listener [{}] unregistered.", name);
                    return true;
                }
                return false;
            });
        }
    }

    @SuppressWarnings("unchecked")
	public synchronized void dispatchEvent(EventBase event) {
    	cleanUpListenersIfNecessary();
        List<ListenerWrapper<? extends EventBase>> wrappers = listeners.get(event.getClass());
        if (wrappers != null) {
            for (ListenerWrapper<? extends EventBase> wrapper : wrappers) {
                Consumer<EventBase> listener = (Consumer<EventBase>) wrapper.getListener();                
                if (listener != null) {
                	try {
                        listener.accept(event);
                    } catch (Exception e) {
                    	logger.warn("Listener [{}] threw exception during local event [{}]: {}", wrapper.getName(), event.getClass().getSimpleName(), e.getMessage(), e);
                    }
                }
            }
        }
    }

    private void cleanUpListenersIfNecessary() {
        long now = System.currentTimeMillis();
        if (now - lastCleanUpTime > Constants.CLEAN_UP_INTERVAL) {
            cleanUpListeners();
            lastCleanUpTime = now;
        }
    }

    private void cleanUpListeners() {
        for (List<ListenerWrapper<? extends EventBase>> wrappers : listeners.values()) {
            wrappers.removeIf(wrapper -> {
                Consumer<? extends EventBase> listener = wrapper.getListener();
                if (listener == null) {
                    logger.info("Garbage collected listener removed. Original name: [{}]", wrapper.getName());
                    return true;
                }
                return false;
            });
        }
    }
}