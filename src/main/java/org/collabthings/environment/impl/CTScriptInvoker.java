package org.collabthings.environment.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.collabthings.model.CTScript;
import org.collabthings.util.LLog;

public class CTScriptInvoker {

	private CTScript script;
	private LLog log = LLog.getLogger(script);
	private String error;

	public CTScriptInvoker(CTScript script) {
		this.script = script;
	}

	public boolean run(final String name, final Object... args) {
		Boolean ret = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

			public Boolean run() {
				try {

					Invocable i = script.getInvocable();
					if (i != null) {
						i.invokeFunction(name, args);
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

	}

	private void handleException(Exception e1, Object... args) {
		this.error = "ERROR " + e1 + " in script " + script + " called in " + args;
		log.info("Error in script " + script);
		log.info("Error in script " + script + " exception " + e1);
		log.error(this, "run", e1);
	}

	public String getError() {
		return error;
	}
}
