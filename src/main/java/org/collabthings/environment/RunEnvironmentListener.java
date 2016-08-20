package org.collabthings.environment;

public interface RunEnvironmentListener {

	void taskFailed(CTRunEnvironment runenv, CTEnvironmentTask task);

	void event(CTRuntimeEvent e);

}
