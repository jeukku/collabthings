package org.collabthings.environment;




public interface RunEnvironmentListener {

	void taskFailed(LOTRunEnvironment runenv, LOTTask task);

	void event(LOTRuntimeEvent e);

}
