package org.collabthings.application.handlers;

import org.collabthings.application.CTInstructionHandler;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;
import org.collabthings.util.LLog;

public class CTEnvHandler implements CTInstructionHandler {
	private LLog log = LLog.getLogger(this);

	@Override
	public void handle(ApplicationLine instruction, CTRunEnvironment env) {
		String action = instruction.get("action");
		if ("set".equals(action)) {
			String key = instruction.get("key");
			String value = instruction.get("value");
			log.info("setting env value " + key + " -> " + value);
			env.setParameter(key, value);
		} else {
			throw new RuntimeException("unknown action " + action);
		}
	}
}
