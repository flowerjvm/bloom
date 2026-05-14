package tsb.li.common.eventflow.task;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.EventHubBase;
import tsb.li.common.eventflow.event.data.ProcessFinishedEvent;
import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.util.UniqueIDGenerator;

public abstract class ProcessBase {
    private static final Logger logger = LoggerFactory.getLogger(ProcessBase.class);
    
    private SeqBase preCheckSeq = null;
    private final Deque<SeqBase> seqQueue = new LinkedList<>();
    private final String id;       // 사용자가 지정한 ID
    private final String objectID; // 인스턴스 객체의 고유 ID
    private final String name;     // 자식클래스의 이름
    private boolean initialized = false; // 초기화 여부 체크
    
    private EventHubBase hub;
    
    public ProcessBase() {
        this("", null);
    }
    
    public ProcessBase(String id) {
        this(id, null);
    }

    public ProcessBase(String id, EventHubBase hub) {
        this.id = id;        
        this.objectID = UniqueIDGenerator.generateID(this.getClass().getSimpleName());
        this.name = this.getClass().getSimpleName();
        
        if (hub == null) this.hub = EventHub.getInstance();
		else this.hub = hub;
        
        logger.debug("Created Process: {} (id={})", name, objectID);

    }
    
    public final void init() {
        if (initialized) {
            logger.warn("{} already initialized.", objectID);
            return;
        }        
        logger.debug("{} intialize start.", objectID);
        onInit();
        initialized = true;        
        logger.debug("{} initialized.", objectID);
    }
    
    public void setPreCheckSeq(SeqBase seq) {
        this.preCheckSeq = seq;
        if (this.preCheckSeq != null) {
        	this.preCheckSeq.setPreCheckSeq(true);
            this.preCheckSeq.init();
            logger.debug("Registered PreCheckSeq [{}] to Process [{}]", seq.getClass().getSimpleName(), objectID);
        }
    }
    
    public void addSeq(SeqBase seq) {
    	seq.init();
        seqQueue.add(seq);
        logger.debug("Added Seq [{}] to Process [{}]", seq.getClass().getSimpleName(), objectID);
    }
    
    public void clearSequences() {
        for (SeqBase seq : seqQueue) {
            seq.unregisterAllListeners();
        }
        seqQueue.clear();
        logger.debug("Cleared all sequences in Process [{}]", objectID);
    }
    
    public SeqBase getCurrentSequence() {
        return seqQueue.peek();
    }
    
    public void execute() {
    	
    	if (preCheckSeq != null) {
            preCheckSeq.execute();

            switch (preCheckSeq.getState()) {
                case REDIRECT_REQ:
                	handleRedirectFromPreCheck();
                    return;
                    
                case PRECHECK_HOLD:
                    return;
                    
                case PASS:
                    break;
                    
                case COMPLETED:
                    logger.debug("PreCheckSequence [{}] completed and removed from Process [{}]", preCheckSeq.getClass().getSimpleName(), objectID);
                	preCheckSeq.unregisterAllListeners();
                	preCheckSeq = null;
                    break;
                    
                default:
                    break;
            }
        }
    	
        SeqBase currentSeq = seqQueue.peek();
        
        if (currentSeq != null) {

            switch (currentSeq.getState()) {

                case COMPLETED:
                    currentSeq.unregisterAllListeners();
                    seqQueue.poll();
                    logger.debug("Sequence [{}] completed and removed from Process [{}]", currentSeq.getClass().getSimpleName(), objectID);
                    break;

                case PASS:
                    seqQueue.poll();
                    currentSeq.reset();
                    seqQueue.add(currentSeq);
                    logger.debug("Sequence [{}] passed and re-queued in Process [{}]", currentSeq.getClass().getSimpleName(), objectID);
                    break;

                case REDIRECT_REQ:
                	handleRedirect(currentSeq, true);
                    break;
                    
                case REDIRECT_REQ_WITH_COMPLETED:
                	handleRedirect(currentSeq, false);
                    break;

                default:
                    currentSeq.execute();
                    break;
            }
        } else {
        	logger.debug("Process [{}] has no more sequences. Dispatching finish event.", objectID);
        	hub.dispatchEvent(new ProcessFinishedEvent(this));
        }
    }
    
    public String getProcessID() {
        return id;
    }
    
    public String getProcessObjectID() {
        return objectID;
    }
    
    public String getProcessName() {
        return name;
    }
    
    public boolean isEmpty() {
        return seqQueue.isEmpty();
    }
    
    private SeqBase findAndRemoveSeqByType(Class<? extends SeqBase> type) {
        Iterator<SeqBase> it = seqQueue.iterator();
        while (it.hasNext()) {
            SeqBase seq = it.next();
            if (seq.getClass().equals(type)) {
                it.remove();
                return seq;
            }
        }
        return null;
    }
    
    private void handleRedirect(SeqBase currentSeq, boolean requeueCurrent) {
        seqQueue.poll();
        Class<? extends SeqBase> redirectClass = currentSeq.getRedirectTargetSeq();

        if (redirectClass != null) {
            SeqBase existing = findAndRemoveSeqByType(redirectClass);
            if (existing != null) {
                seqQueue.addFirst(existing);
                logger.debug("Redirecting to existing Seq [{}] in Process [{}]", redirectClass.getSimpleName(), objectID);
            } else {
            	logger.warn("Redirect target Seq [{}] not found in Process [{}]", redirectClass.getSimpleName(), objectID);
            }
        } else {
        	logger.warn("No redirect target specified in Seq [{}] of Process [{}]", currentSeq.getClass().getSimpleName(), objectID);
        }

        if (requeueCurrent) {
            currentSeq.reset();
            seqQueue.add(currentSeq);
            logger.debug("Re-queued current Seq [{}] after redirect in Process [{}]", currentSeq.getClass().getSimpleName(), objectID);
        } else {
            currentSeq.unregisterAllListeners();
            logger.debug("Current Seq [{}] completed (redirect+complete) in Process [{}]", currentSeq.getClass().getSimpleName(), objectID);
        }        
    }
    
    private void handleRedirectFromPreCheck() {
        Class<? extends SeqBase> redirectClass = preCheckSeq.getRedirectTargetSeq();
        
        SeqBase beforeRedirect = seqQueue.peek();

        if (redirectClass != null && beforeRedirect != null && !redirectClass.equals(beforeRedirect.getClass())) {
            SeqBase target = findAndRemoveSeqByType(redirectClass);
            if (target != null) {
                seqQueue.addFirst(target);
                beforeRedirect.reset();
                logger.debug("Redirecting to [{}] from PreCheckSeq in Process [{}]", redirectClass.getSimpleName(), objectID);
            } else {
                logger.warn("Redirect target [{}] not found from PreCheckSeq in Process [{}]", redirectClass.getSimpleName(), objectID);
            }
        } else {
        	logger.debug("Skip redirect because it's the same as the current seq: {}", 
                    redirectClass != null ? redirectClass.getSimpleName() : "null");        	
        }
        
        preCheckSeq.reset();
    }
    
    /**
     * 이 메서드는 ProcessBase의 init() 흐름에서 내부적으로만 호출됩니다.
     * 외부에서 호출하지 마십시오.
     */
    protected abstract void onInit();
}
