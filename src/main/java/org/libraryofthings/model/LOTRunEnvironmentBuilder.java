package org.libraryofthings.model;

import org.libraryofthings.PrintOut;
import org.libraryofthings.environment.LOTRunEnvironment;

import waazdoh.common.ObjectID;

public interface LOTRunEnvironmentBuilder {

	void save();

	boolean isReady();

	LOTEnvironment getEnvironment();

	LOTRunEnvironment getRunEnvironment();

	void publish();

	ObjectID getID();

	PrintOut printOut();

}
