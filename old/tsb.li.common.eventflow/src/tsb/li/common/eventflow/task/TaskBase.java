package tsb.li.common.eventflow.task;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tsb.li.common.eventflow.event.base.AbstractEventSubscriber;
import tsb.li.common.eventflow.event.base.EventHubBase;
import tsb.li.common.eventflow.event.data.ProcessFinishedEvent;
import tsb.li.common.eventflow.event.manager.EventHub;
import tsb.li.common.eventflow.util.Constants.TaskState;
import tsb.li.common.eventflow.util.UniqueIDGenerator;

public abstract class TaskBase extends AbstractEventSubscriber implements Runnable {
	
    private static final Logger logger = LoggerFactory.getLogger(TaskBase.class);

	
	private final String objectID; // 인스턴스 객체의 고유 ID
	
	protected final List<ProcessBase> processes = new CopyOnWriteArrayList<>();
	private final Queue<ProcessBase> processesToAdd = new ConcurrentLinkedQueue<>();
    private final Queue<ProcessBase> processesToRemove = new ConcurrentLinkedQueue<>();
    
    private Thread thread;
    private int threadInterval = 100;
    
    private volatile TaskState state = TaskState.CREATED;
    
    public TaskBase() {
        this(null);
    }
    
    public TaskBase(EventHubBase hub) {
    	super(hub == null ? EventHub.getInstance() : hub);
    	
        this.objectID = UniqueIDGenerator.generateID(this.getClass().getSimpleName());
        logger.debug("Created Task: {}", this.objectID);
    }
    
    public void handleProcessFinished(ProcessFinishedEvent event) {
    	
    	if (!processes.contains(event.getProcess())) {
            return;
        }
    	
    	logger.debug("Handle finish process event: {} will be removed", event.getProcess().getProcessName());
    	removeProcess(event.getProcess());    	
    }
    
    public void setThreadInterval(int interval) {
        this.threadInterval = interval;
    }
    
    public int getThreadInterval() {
        return this.threadInterval;
    }
    
    public void addProcess(ProcessBase process) {
    	processesToAdd.add(process);
    }
    
    public void removeProcess(ProcessBase process) {
    	processesToRemove.add(process);
    }
    
    public void replaceProcess(ProcessBase oldProcess, ProcessBase newProcess) {
        if (oldProcess != null && newProcess != null) {
        	processesToRemove.add(oldProcess);
        	processesToAdd.add(newProcess);                     
        }
    }
    
    public void init() {
        if (state.isSameOrAfter(TaskState.INITIALIZED)) {
        	logger.warn("{} already initialized.", getClass().getSimpleName());
            return;
        }        
        logger.debug("{} intialize start.", getClass().getSimpleName());        
        super.registerListener(ProcessFinishedEvent.class, this::handleProcessFinished);
        onInit();        
        state = TaskState.INITIALIZED;        
        logger.debug("{} initialized.", getClass().getSimpleName());
    }
    
    public void start() {
    	if (state.isBefore(TaskState.INITIALIZED)) {
    		logger.warn("Task needs to be initialized. Current state: {}", state);
            return;
        }
    	
        if (thread == null || !thread.isAlive()) {
        	state = TaskState.RUNNING;
        	logger.debug("{} is started!", getClass().getSimpleName());
        	
            thread = new Thread(this);
            thread.start();
            
        } else {
        	logger.warn("{} is already started!", getClass().getSimpleName());
        }
    }
    
    public void pause() {
    	if (state == TaskState.RUNNING) {
            state = TaskState.PAUSED;
            logger.debug("{} paused.", getClass().getSimpleName());
        } else {
        	logger.warn("{} is not running. Current state: {}", getClass().getSimpleName(), state);
        }
    }

    public void resume() {
    	if (state == TaskState.PAUSED) {
            state = TaskState.RUNNING;
            logger.debug("{} resumed.", getClass().getSimpleName());
        } else {
        	logger.warn("{} is not paused. Current state: {}", getClass().getSimpleName(), state);
        }
    }
    
    /**
     * thread abort 후에 다시 start는 가능하지만 초기화를 다시 하는 것은 불가능하도록 설계함
     * init을 다시 해야하는 상황이면 abort 후에 해당 task 다시 만들어서 초기화 해서 사용할것
     */
    public void abort() {    	
    	if (state == TaskState.ABORTED) return;
    	
    	state = TaskState.ABORTED;
    	
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public String getObjectID() {
    	return objectID;
    }

    @Override
    public void run() {
    	try {
    		logger.debug("{} run() started.", getClass().getSimpleName());
    		
    		while (state == TaskState.RUNNING || state == TaskState.PAUSED) {
    			
    			processPendingChanges();
            	
            	if (state == TaskState.PAUSED) {
                    try {
                        Thread.sleep(threadInterval);
                        continue;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        state = TaskState.ABORTED;
                        break;
                    }
                }
            	
                for (ProcessBase process : processes) {
                	process.execute();
                }                
                
                try {
                	Thread.sleep(threadInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    state = TaskState.ABORTED;
                    break;
                }
            }
    		logger.debug("{} run() aborted.", getClass().getSimpleName());
		} catch (Exception e) {
			logger.error("Task Run Error in {}: {}", getClass().getSimpleName(), e.getMessage(), e);
			state = TaskState.ABORTED;
		}        
    }
    
    private void processPendingChanges() {
    	
    	while (!processesToRemove.isEmpty()) {
            ProcessBase process = processesToRemove.poll();
            if (process != null) {
                process.clearSequences(); // 반드시 Seq를 먼저 해제
                processes.remove(process);
                logger.debug("Removed process: {}", process.getProcessObjectID());
            }
        } 
    	
    	while (!processesToAdd.isEmpty()) {
            ProcessBase process = processesToAdd.poll();
            if (process != null) {
            	process.init();
                processes.add(process);
                logger.debug("Added process: {}", process.getProcessObjectID());
            }
        }               
    }
    
    /**
     * ProcessBase 형식으로 리턴
     */
    public ProcessBase findProcess(Class<? extends ProcessBase> processType, String id) {
        return processes.stream()
                .filter(processType::isInstance)
                .filter(p -> p.getProcessID().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * ProcessBase 형식으로 특정 ID와 동일한 첫번째 프로세스 리턴
     */
    public ProcessBase findProcessByID(String id) {
        return processes.stream()
                .filter(p -> p.getProcessID().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * ProcessBase를 구현한 특정 구현체로 리턴
     */
    public <T extends ProcessBase> T findSpecificProcess(Class<T> processType, String id) {
        return processes.stream()
                .filter(processType::isInstance)
                .map(processType::cast)
                .filter(p -> p.getProcessID().equals(id))
                .findFirst()
                .orElse(null);
    }    
    
    public boolean isRunning() {
        return state.is(TaskState.RUNNING);
    }

    public boolean isInitialized() {
        return state.isSameOrAfter(TaskState.INITIALIZED);
    }

    public TaskState getState() {
        return state;
    }
    
    /**
     * 이 메서드는 TaskBase의 init() 흐름에서 내부적으로만 호출됩니다.
     * 외부에서 호출하지 마십시오.
     * 외부에서 실시간으로 각각의 Task를 생성하여 사용시 init()을 호출하여 사용하세요.
     */
    protected abstract void onInit();
}
