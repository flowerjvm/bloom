package tsb.li.common.eventflow.sample.sequence.complete;

import tsb.li.common.eventflow.task.TaskRegistry;

/**
 * Complete 처리를 하면서 시퀀스를 하나씩 단계적으로 처리하는 예제입니다.
 * Complete 처리된 시퀀스는 더이상 프로세스내에 존재하지 않으며
 * 모든 시퀀스가 Complete 처리되면 해당 프로세스에는 아무런 시퀀스가 없는 상태입니다.
 * 모든 시퀀스가 없는 상태이면 자동으로 Task에 있던 해당 프로세스는 삭제됩니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		TaskRegistry registry = new TaskRegistry();
		
		registry.registerTask(new CompleteTask(), 100);
		registry.initTasks();
		registry.startTasks();
    }
}
