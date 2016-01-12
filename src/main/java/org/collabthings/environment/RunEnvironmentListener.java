package org.collabthings.environment;




public interface RunEnvironmentListener {

	void taskFailed(LOTRunEnvironment runenv, LOTEnvironmentTask task);

	void event(LOTRuntimeEvent e);

}
