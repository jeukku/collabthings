package org.collabthings.application.lines;

import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;

public class EnvSetApplicationLine extends ApplicationLine {

	public EnvSetApplicationLine(String string, String value) {
		put("a", "env");
		put("action", "set");
		put("key", string);
		put("value", value);
	}

}
