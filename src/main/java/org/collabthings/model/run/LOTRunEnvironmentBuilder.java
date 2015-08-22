package org.collabthings.model.run;

import org.collabthings.environment.LOTRunEnvironment;
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

	void setName(String name);

	String readStorage(String path);

}
