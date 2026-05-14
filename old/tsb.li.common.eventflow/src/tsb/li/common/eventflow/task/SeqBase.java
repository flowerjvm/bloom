package tsb.li.common.eventflow.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.AbstractEventSubscriber;
import tsb.li.common.eventflow.event.base.EventHubBase;
import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.util.UniqueIDGenerator;
import tsb.li.common.eventflow.util.Constants.SeqState;

public abstract class SeqBase extends AbstractEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(SeqBase.class);

	private final String objectID; // 인스턴스 객체의 고유 ID
	private int seqNo = 0;
	private long startTicks = 0;
    private SeqState state = SeqState.CREATED;
    private boolean isPreCheckSeq = false;
    
    private Class<? extends SeqBase> redirectTargetSeq = null;
    
    public SeqBase() {
    	this(null);
    }
    
    public SeqBase(EventHubBase hub) {    	
    	super(hub == null ? EventHub.getInstance() : hub);
    	
        this.objectID = UniqueIDGenerator.generateID(this.getClass().getSimpleName());
        logger.debug("Created Seq: {}", this.objectID);
    }
    
    public final void init() {
        if (state.isSameOrAfter(SeqState.INITIALIZED)) {
            logger.debug("{} already initialized. (state={})", getClass().getSimpleName(), state);
            return;
        }        
        logger.debug("{} initialize start.", getClass().getSimpleName());        
        onInit();
        state = SeqState.IDLE;        
        logger.debug("{} initialized.", getClass().getSimpleName());
    }

    public int getSeqNo() {
        return seqNo;
    }

    protected void setNextSeq(int seqNo) {
        this.seqNo = seqNo;
    }
    
    public SeqState getState() {
        return state;
    }
    
    protected void setState(SeqState newState) {
        this.state = newState;
    }
    
    public void execute() {
        if (canExecute()) {
            state = SeqState.RUNNING;
            
            action(this.seqNo);
            
            if (checkCompletion()) {
                state = SeqState.COMPLETED;
                logger.debug("{} completed by checkCompletion()", getClass().getSimpleName());
            }
        }
    }
    
    public String getObjectID() {
    	return objectID;
    }    
    
    protected void startTick() {
        startTicks = System.currentTimeMillis();
    }
    
    protected long getElapsedTicks() {
        return System.currentTimeMillis() - startTicks;
    }
    
    public Class<? extends SeqBase> getRedirectTargetSeq() {
        return redirectTargetSeq;
    }
    
    public void setNextSeq(Class<? extends SeqBase> targetClass) {
    	setNextSeq(targetClass, false);
    }
    
    public void setNextSeq(Class<? extends SeqBase> targetClass, boolean completeCurrent) {
        this.redirectTargetSeq = targetClass;
        this.state = completeCurrent ? SeqState.REDIRECT_REQ_WITH_COMPLETED : SeqState.REDIRECT_REQ;
        logger.debug("{} Go to → {} ({})", getClass().getSimpleName(), targetClass.getSimpleName(),
                     completeCurrent ? " with complete" : "with resume");
    }
    
    public boolean isPreCheckSeq() {
		return isPreCheckSeq;
	}

	public void setPreCheckSeq(boolean isPreCheckSeq) {
		this.isPreCheckSeq = isPreCheckSeq;
	}
    
    public void reset() {
    	// 디스패처 등록 순서때문에 삭제 했다가 다시 등록해야 할듯..
    	super.unregisterAllListeners();
    	initEventListener();
    	initHandlingMembers();
    	redirectTargetSeq = null;
    	state = SeqState.IDLE;
    	seqNo = 0;
    }
    
    private boolean canExecute() {
        if (state == SeqState.IDLE || state == SeqState.RUNNING) {
            return true;
        }

        if (isPreCheckSeq) {
            return state == SeqState.PRECHECK_HOLD || state == SeqState.PASS;
        }

        return false;
    }
    
    protected abstract void action(int seqNo);
    
    protected abstract void initHandlingMembers();
    
    protected abstract boolean checkCompletion();
    
    
    
    
    /**
     * 이 메서드는 ProcessBase에 추가시 자동으로 호출됩니다.
     * 외부에서 호출하지 마십시오.
     */
    protected abstract void onInit();
}
