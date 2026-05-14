package tsb.li.common.eventflow.sample.sequence.pass;

import tsb.li.common.eventflow.task.TaskRegistry;

/**
 * 단방향 시퀀스를 진행합니다. 
 * 정해진 시퀀스 클래스를 순서대로 PASS하면서 주기적으로 프로세스가 멈추지 않고 순환합니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		TaskRegistry registry = new TaskRegistry();
		
		registry.registerTask(new PassTask(), 100);
		registry.initTasks();
		registry.startTasks();
    }
}
