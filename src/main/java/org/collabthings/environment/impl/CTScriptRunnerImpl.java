package org.collabthings.environment.impl;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTScriptRunner;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;

public class CTScriptRunnerImpl implements CTScriptRunner {
	private final CTScript script;
	private final CTRunEnvironment runenv;
	private final CTRuntimeObject runo;

	private LLog log = LLog.getLogger(this);
	private String error;

	public CTScriptRunnerImpl(CTScript s, CTRunEnvironment runenv, CTRuntimeObject runtimeobject) {
		this.script = s;
		this.runenv = runenv;
		this.runo = runtimeobject;
	}

	@Override
	public String toString() {
		return "ScriptRunner[" + script + "]";
	}

	public boolean run() {
		return run(new CTValues());
	}

	/**
	 * Invokes run -function in the script.
	 * 
	 * @param o2
	 * 
	 */
	public boolean run(CTValues values) {
		if (script != null) {
			CTScriptInvoker inv = new CTScriptInvoker(script);
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
