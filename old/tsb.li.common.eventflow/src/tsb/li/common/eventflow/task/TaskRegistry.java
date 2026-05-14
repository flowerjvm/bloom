package tsb.li.common.eventflow.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRegistry {
	
    private static final Logger logger = LoggerFactory.getLogger(TaskRegistry.class);

    private final List<TaskBase> tasks = new ArrayList<>();
   
    public void registerTask(TaskBase task, int interval) {
    	if (!tasks.contains(task)) {
            task.setThreadInterval(interval);
            tasks.add(task);
            logger.debug("Task registered: {}", task.getObjectID());
        } else {
        	logger.warn("Task already registered: {}", task.getObjectID());
        }
    }
    
    public void initTasks() {
    	logger.debug("Task intialize start!");
        for (TaskBase task : tasks) {
            task.init();
        }
    }
    
    public void startTasks() {
    	logger.debug("Task start!");
        for (TaskBase task : tasks) {
            task.start();
        }
    }
    
    public void pauseTasks() {
    	logger.debug("Task paused!");
        for (TaskBase task : tasks) {
            task.pause();
        }
    }
    
    public void abortTasks() {
    	logger.debug("Task aborted!");
        for (TaskBase task : tasks) {
            task.abort();
        }
    }
}
