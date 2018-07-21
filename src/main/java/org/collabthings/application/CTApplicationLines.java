package org.collabthings.application;

import org.collabthings.application.handlers.CTEnvHandler.EnvSetApplicationLine;
import org.collabthings.application.handlers.CTLogHandler.LogApplicationLine;
import org.collabthings.application.handlers.CTPartHandler;
import org.collabthings.application.handlers.CTPartHandler.CTPartApplicationLine;
import org.collabthings.application.handlers.CTSetHandler.SetCallApplicationLine;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;

public class CTApplicationLines {
	public static EnvSetApplicationLine envSet(String key, String value) {
		return new EnvSetApplicationLine(key, value);
	}

	public static LogApplicationLine log(String message) {
		return new LogApplicationLine(message);
	}

	public static SetCallApplicationLine call(String name, String source, String method) {
		return new SetCallApplicationLine(name, source, method);
	}

	public static ApplicationLine factoryGet(String string, String string2, String string3) {
		// TODO Auto-generated method stub
		return null;
	}

	public final static CTPartApplicationLine part() {
		return new CTPartHandler().part();
	}
}
