package org.libraryofthings.model;

import org.libraryofthings.PrintOut;
import org.libraryofthings.environment.LOTRunEnvironment;

public interface LOTRunEnvironmentBuilder extends LOTObject {

	void save();

	boolean isReady();

	LOTEnvironment getEnvironment();

	LOTRunEnvironment getRunEnvironment();

	PrintOut printOut();

	String getName();

}
