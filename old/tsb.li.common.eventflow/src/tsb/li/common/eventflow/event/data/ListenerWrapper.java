package tsb.li.common.eventflow.event.data;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import tsb.li.common.eventflow.event.base.EventBase;

public class ListenerWrapper<T extends EventBase> {
	private final String name;
    private final WeakReference<Consumer<T>> listener;

    public ListenerWrapper(String name, Consumer<T> listener) {
        this.name = name;
        this.listener = new WeakReference<>(listener);
    }

    public String getName() {
        return name;
    }

    public Consumer<T> getListener() {
        return listener.get();
    }
}
