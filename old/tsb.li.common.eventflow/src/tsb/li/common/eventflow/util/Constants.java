package tsb.li.common.eventflow.util;

public class Constants {
	
	public static final int CLEAN_UP_INTERVAL = 180000; // ms
	public static final int MAX_COUNTER = 9999;	
	
	public enum TaskState {
	    CREATED(0),       // 생성됨
	    INITIALIZED(1),   // init() 완료
	    PAUSED(2),        // 일시 정지
	    RUNNING(3),       // 실행 중
	    ABORTED(4);       // 종료됨

	    private final int order;

	    TaskState(int order) {
	        this.order = order;
	    }

	    public int getOrder() {
	        return order;
	    }
	    
	    public boolean is(TaskState other) {
	        return this == other;
	    }

	    public boolean isBefore(TaskState other) {
	        return this.order < other.order;
	    }

	    public boolean isAfter(TaskState other) {
	        return this.order > other.order;
	    }	    

	    public boolean isSameOrAfter(TaskState other) {
	        return this.order >= other.order;
	    }
	}

	public enum SeqState {
	    CREATED(0),
	    INITIALIZED(1),
	    IDLE(2),
	    START(3),
	    PAUSED(4),
	    RUNNING(5),
	    PASS(6),
	    COMPLETED(7),
	    ERROR(8),
		REDIRECT_REQ(9),
		REDIRECT_REQ_WITH_COMPLETED(10),
		PRECHECK_HOLD (11);

	    private final int order;

	    SeqState(int order) {
	        this.order = order;
	    }

	    public int getOrder() {
	        return order;
	    }
	    
	    public boolean is(SeqState other) {
	        return this == other;
	    }

	    public boolean isBefore(SeqState other) {
	        return this.order < other.order;
	    }

	    public boolean isAfter(SeqState other) {
	        return this.order > other.order;
	    }	    

	    public boolean isSameOrAfter(SeqState other) {
	        return this.order >= other.order;
	    }
	}
}
