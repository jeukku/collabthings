package org.collabthings.environment.impl;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTScriptRunner;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;

public class LOTScriptRunnerImpl implements LOTScriptRunner {
	private final LOTScript script;
	private final LOTRunEnvironment runenv;
	private final LOTRuntimeObject runo;

	private LLog log = LLog.getLogger(this);
	private String error;

	public LOTScriptRunnerImpl(LOTScript s, LOTRunEnvironment runenv,
			LOTRuntimeObject runtimeobject) {
		this.script = s;
		this.runenv = runenv;
		this.runo = runtimeobject;
	}

	@Override
	public String toString() {
		return "ScriptRunner[" + script + "]";
	}

	public boolean run() {
		return run(new LOTValues());
	}

	/**
	 * Invokes run -function in the script.
	 * 
	 * @param o2
	 * 
	 */
	public boolean run(LOTValues values) {
		if (script != null) {
			LOTScriptInvoker inv = new LOTScriptInvoker(script);
			boolean ret = inv.run("run", runenv, runo, values);
			error = inv.getError();
			return ret;
		} else {
			return false;
		}
	}

	@Override
	public String getError() {
		return error;
	}

}
