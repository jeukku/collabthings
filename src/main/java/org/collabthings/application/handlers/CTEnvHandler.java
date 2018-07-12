package org.collabthings.application.handlers;

import org.collabthings.application.CTInstructionHandler;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTValues;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;
import org.collabthings.util.LLog;

public class CTEnvHandler implements CTInstructionHandler {
	private LLog log = LLog.getLogger(this);

	@Override
	public void handle(ApplicationLine line, CTRunEnvironment env, CTValues values) {
		String action = line.get("action");
		if ("set".equals(action)) {
			String key = line.get("key");
			String value = line.get("value");
			log.info("setting env value " + key + " -> " + value);
			env.setParameter(key, value);
		} else {
			throw new RuntimeException("unknown action " + action);
		}
	}

	public final static class EnvSetApplicationLine extends ApplicationLine {

		public EnvSetApplicationLine(String string, String value) {
			put(ApplicationLine.ACTION, "env");
			put("action", "set");
			put("key", string);
			put("value", value);
		}

	}

}
