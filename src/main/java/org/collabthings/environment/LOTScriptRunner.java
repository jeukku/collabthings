package org.collabthings.environment;

import org.collabthings.model.LOTValues;

public interface LOTScriptRunner {

	boolean run(LOTValues values);

	String getError();

}
