package org.libraryofthings.environment;


public interface RunEnvironmentListener {

	void taskFailed(RunEnvironment runenv, LOTTask task);

}
