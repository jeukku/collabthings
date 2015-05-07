package org.collabthings.model;

import org.collabthings.PrintOut;
import org.collabthings.environment.LOTRunEnvironment;

public interface LOTRunEnvironmentBuilder extends LOTObject {

	void save();

	boolean isReady();

	LOTEnvironment getEnvironment();

	LOTRunEnvironment getRunEnvironment();

	PrintOut printOut();

	String getName();

}
