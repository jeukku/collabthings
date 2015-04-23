package org.libraryofthings.environment;




public interface RunEnvironmentListener {

	void taskFailed(LOTRunEnvironment runenv, LOTTask task);

	void event(LOTRuntimeEvent e);

}
