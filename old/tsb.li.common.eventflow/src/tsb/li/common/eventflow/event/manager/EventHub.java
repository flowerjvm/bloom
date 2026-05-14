package tsb.li.common.eventflow.event.manager;

import tsb.li.common.eventflow.event.base.EventHubBase;

/**
 * Singleton
 * EventHub는 전역적인(Global) 이벤트 통신을 담당한다.
 */
public class EventHub extends EventHubBase {
    private static final EventHub instance = new EventHub();

    private EventHub() {}
    
    public static EventHub getInstance() {
        return instance;
    }    
}
