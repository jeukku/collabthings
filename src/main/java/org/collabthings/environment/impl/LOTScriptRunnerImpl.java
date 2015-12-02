package org.collabthings.environment.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.script.Invocable;
import javax.script.ScriptException;

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
			Boolean ret = AccessController
					.doPrivileged(new PrivilegedAction<Boolean>() {

						public Boolean run() {
							try {

								Invocable i = script.getInvocable();
								if (i != null) {
									i.invokeFunction("run", runenv, runo,
											values);
									return true;
								} else {
									return false;
								}
							} catch (NoSuchMethodException | ScriptException e1) {
								handleException(e1);
								return false;
							} catch (Exception e) {
								handleException(e);
								return false;
							}
						}
					});
			return ret;
		} else {
			return false;
		}
	}

	private void handleException(Exception e1) {
		this.error = "ERROR " + e1 + " in script " + script + " called in "
				+ runenv + " in object " + runo;
		log.info("Error in script " + script);
		log.info("Error in script " + script + " exception " + e1);
		log.error(this, "run", e1);
	}

	@Override
	public String getError() {
		return error;
	}

}
