package tsb.li.common.eventflow.event.data;

import tsb.li.common.eventflow.event.base.EventBase;
import tsb.li.common.eventflow.task.ProcessBase;

/**
 * Process가 Finished 되었음 알리는 이벤트입니다.
 */
public class ProcessFinishedEvent extends EventBase {

	private final ProcessBase process;
	
	public ProcessFinishedEvent(ProcessBase process) {
		this.process = process;
	}

	public ProcessBase getProcess() {
		return process;
	}
}
