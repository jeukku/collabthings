package org.libraryofthings.simulation;

import java.util.List;

import javax.script.ScriptException;

import org.libraryofthings.RunEnvironment;

import waazdoh.cutils.MLogger;

public class LOTSimpleSimulation implements LOTSimulation {

	private RunEnvironment env;
	private MLogger log = MLogger.getLogger(this);

	public LOTSimpleSimulation(RunEnvironment runenv) {
		this.env = runenv;
	}

	@Override
	public void run() {
		try {
			List<LOTSimulationTask> tasks = env.getTasks();
			for (LOTSimulationTask task : tasks) {
				task.run(env);
			}
		} catch (NoSuchMethodException e) {
			log.error(e);
		} catch (ScriptException e) {
			log.error(e);
		}
	}
}
