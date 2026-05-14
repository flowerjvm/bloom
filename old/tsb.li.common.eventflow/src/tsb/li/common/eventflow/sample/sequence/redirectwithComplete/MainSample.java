package tsb.li.common.eventflow.sample.sequence.redirectwithComplete;

import tsb.li.common.eventflow.task.TaskRegistry;

/**
 * Redirection 기반 시퀀스 구현으로 양방향 시퀀스 진행이 가능합니다.
 * 시퀀스를 클래스 단위로 쪼개서 원하는 시퀀스 클래스로 이동하면서 프로세스를 진행합니다.
 * 다른 시퀀스로 redirect 후에 해당 시퀀스는 complete 처리됩니다.
 * 모든 시퀀스가 Complete 처리되면 해당 프로세스에는 아무런 시퀀스가 없는 상태입니다.
 * 모든 시퀀스가 없는 상태이면 자동으로 Task에 있던 해당 프로세스는 삭제됩니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		TaskRegistry registry = new TaskRegistry();
		
		registry.registerTask(new RedirectTask(), 100);
		registry.initTasks();
		registry.startTasks();
    }
}
