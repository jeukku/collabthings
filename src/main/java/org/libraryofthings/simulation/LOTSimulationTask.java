package org.libraryofthings.simulation;

import javax.script.ScriptException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.model.LOTScript;

public class LOTSimulationTask {

	private LOTEnvironment env;
	private LOTScript s;
	private Object[] params;

	public LOTSimulationTask(LOTEnvironment env, LOTScript s, Object ... params) {
		this.env = env;
		this.s = s;
		this.params = params;
	}

	public void run(RunEnvironment runenv) throws NoSuchMethodException, ScriptException {
		s.run(runenv, params);
	}

}
