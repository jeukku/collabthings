package org.libraryofthings.simulation;

import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.environment.LOTTask;

public class LOTSimpleSimulation implements LOTSimulation {

	private RunEnvironment env;
	private LLog log = LLog.getLogger(this);

	public LOTSimpleSimulation(RunEnvironment runenv) {
		this.env = runenv;
	}

	@Override
	public boolean run() {
		List<LOTTask> tasks = env.getTasks();
		for (LOTTask task : tasks) {
			if (!task.run(env)) {
				return false;
			}
		}
		return true;
	}
}
