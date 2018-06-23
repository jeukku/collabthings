package org.collabthings.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.collabthings.application.handlers.CTEnvHandler;
import org.collabthings.application.handlers.CTLogHandler;
import org.collabthings.application.handlers.CTSetHandler;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTApplication;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;
import org.collabthings.util.LLog;

public class CTApplicationRunner {
	private LLog log = LLog.getLogger(this);
	private CTApplication app;
	private Map<String, CTInstructionHandler> handlers = new HashMap<>();

	public CTApplicationRunner(CTApplication app) {
		this.app = app;

		initHandlers();
	}

	private void initHandlers() {
		handlers.put("log", new CTLogHandler());
		handlers.put("env", new CTEnvHandler());
		handlers.put("set", new CTSetHandler());
	}

	public void run(CTRunEnvironment rune) {
		List<ApplicationLine> content = app.getContent();

		for (ApplicationLine s : content) {
			handle(rune, s);
		}
	}

	public void handle(CTRunEnvironment rune, ApplicationLine line) {
		String name = line.get(ApplicationLine.ACTION);

		log.info("RUN: " + name + " -> " + line);

		CTInstructionHandler h = handlers.get(name);
		if (h != null) {
			h.handle(line, rune);
		} else {
			throw new RuntimeException("Instruction \"" + name + "\" not implemented");
		}
	}

}
