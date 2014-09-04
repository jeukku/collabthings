package org.libraryofthings.model;

import org.libraryofthings.environment.RunEnvironment;

public interface LOTScript extends LOTObject {

	boolean isOK();

	boolean run(RunEnvironment runenv, LOTRuntimeObject runtimeobject,
			LOTValues values);

	String getScript();

	boolean setScript(String string);

	void setName(String scriptname);

}
