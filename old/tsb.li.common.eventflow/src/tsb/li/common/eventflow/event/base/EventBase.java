package tsb.li.common.eventflow.event.base;

public abstract class EventBase {
    private final long timestamp;

    public EventBase() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public String getEventName() {
        return this.getClass().getSimpleName();
    }
}
