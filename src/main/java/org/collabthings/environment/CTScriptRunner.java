package org.collabthings.environment;

import org.collabthings.model.CTValues;

public interface CTScriptRunner {

	boolean run(CTValues values);

	String getError();

}
