package org.collabthings.application.handlers;

import org.collabthings.application.CTInstructionHandler;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;
import org.collabthings.util.LLog;

public class CTLogHandler implements CTInstructionHandler {

	public static final String MSG = "msg";

	@Override
	public void handle(ApplicationLine instruction, CTRunEnvironment rune) {
		String msg = instruction.get(MSG);

		for (String n : rune.getObjectNames()) {
			String target = "$" + n + ";";
			if (msg.contains(target)) {
				msg = msg.replace(target, rune.getObject(n).toString());
			}
		}

		LLog.getLogger(this).info("APP LOG " + msg);
	}
}
