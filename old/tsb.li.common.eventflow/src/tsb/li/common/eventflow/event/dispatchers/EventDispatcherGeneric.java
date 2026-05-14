package tsb.li.common.eventflow.event.dispatchers;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.event.data.ListenerWrapper;
import tsb.li.common.eventflow.util.Constants;

public class EventDispatcherGeneric<T extends EventBase> {
    private static final Logger logger = LoggerFactory.getLogger(EventDispatcherGeneric.class);

	private static final ExecutorService sharedExecutorService = Executors.newFixedThreadPool(4);    
	private final CopyOnWriteArrayList<ListenerWrapper<T>> listeners = new CopyOnWriteArrayList<>();
	private long lastCleanUpTime = 0;
	
	static {
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	        logger.info("Shutdown hook triggered: ExecutorService shutting down.");
	        sharedExecutorService.shutdown();
	        try {
	            if (!sharedExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
	                logger.warn("ExecutorService did not terminate in time. Forcing shutdown...");
	                sharedExecutorService.shutdownNow();
	            }
	        } catch (InterruptedException e) {
	            logger.error("Interrupted during shutdown. Forcing shutdown now...");
	            sharedExecutorService.shutdownNow();
	        }
	    }));
	}
    
    public void registerListener(String name, Consumer<T> listener) {
    	unregisterListenerByName(name);
        listeners.add(new ListenerWrapper<>(name, listener));
        logger.debug("Registered generic listener [{}]", name);
    }
    
    public void unregisterListenerByName(String name) {
        listeners.removeIf(wrapper -> {        	
            Consumer<T> refListener = wrapper.getListener();
            if (refListener == null) {
                logger.info("Generic listener [{}] already garbage collected", wrapper.getName());
                return true;
            }            
            if (wrapper.getName().equals(name)) {
            	logger.debug("Unregistered generic listener [{}]", name);
                return true;
            }
            return false;
        });
    }
    
    public void dispatchEvent(T event) {    	
    	cleanUpListenersIfNecessary();    	
    	for (ListenerWrapper<T> wrapper : listeners) {
            Consumer<T> listener = wrapper.getListener();
            if (listener != null) {
            	try {
                    listener.accept(event);
                } catch (Exception e) {
                	logger.error("Error in generic listener [{}] during event [{}]", wrapper.getName(), event.getClass().getSimpleName(), e);
                }
            }
        }
    }
    
    public void dispatchEventAsync(T event) {    	
        cleanUpListenersIfNecessary();
        sharedExecutorService.submit(() -> {
            for (ListenerWrapper<T> wrapper : listeners) {
                Consumer<T> listener = wrapper.getListener();
                if (listener != null) {
                    try {
                        listener.accept(event);
                    } catch (Exception e) {
                        logger.error("Error in generic async listener [{}] during event [{}]", wrapper.getName(), event.getClass().getSimpleName(), e);
                    }
                }
            }
        });
    }
    
    private void cleanUpListenersIfNecessary() {
        long now = System.currentTimeMillis();
        if (now - lastCleanUpTime > Constants.CLEAN_UP_INTERVAL) {
            cleanUpListeners();
            lastCleanUpTime = now;
        }
    }
    
    private void cleanUpListeners() {
    	listeners.removeIf(wrapper -> {
            Consumer<T> listener = wrapper.getListener();
            if (listener == null) {
                logger.info("Removed GC-collected generic listener [{}]", wrapper.getName());
                return true;
            }
            return false;
        });
    }
}
