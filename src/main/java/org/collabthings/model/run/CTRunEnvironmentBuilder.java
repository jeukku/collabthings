package org.collabthings.model.run;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTObject;
import org.collabthings.util.PrintOut;

public interface CTRunEnvironmentBuilder extends CTObject {

	void save();

	boolean isReady();

	CTEnvironment getEnvironment();

	CTRunEnvironment getRunEnvironment();

	PrintOut printOut();

	String getName();

	void setName(String name);

	String readStorage(String path);

}
