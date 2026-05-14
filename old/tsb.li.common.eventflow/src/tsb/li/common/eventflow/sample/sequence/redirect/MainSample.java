package tsb.li.common.eventflow.sample.sequence.redirect;

import tsb.li.common.eventflow.task.TaskRegistry;

/**
 * Redirection 기반 시퀀스 구현으로 양방향 시퀀스 진행이 가능합니다.
 * 시퀀스를 클래스 단위로 쪼개서 원하는 시퀀스 클래스로 이동하면서 프로세스를 진행합니다.
 */
public class MainSample {
	
	public static void main(String[] args) {
		
		TaskRegistry registry = new TaskRegistry();
		
		registry.registerTask(new RedirectTask(), 100);
		registry.initTasks();
		registry.startTasks();
    }
}
