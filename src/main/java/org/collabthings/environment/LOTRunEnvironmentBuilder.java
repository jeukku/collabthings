package org.collabthings.environment;

import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTObject;
import org.collabthings.util.PrintOut;

public interface LOTRunEnvironmentBuilder extends LOTObject {

	void save();

	boolean isReady();

	LOTEnvironment getEnvironment();

	LOTRunEnvironment getRunEnvironment();

	PrintOut printOut();

	String getName();

}
