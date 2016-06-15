package org.collabthings.model;

import org.collabthings.math.LOrientation;
import org.collabthings.util.PrintOut;

public interface CTRuntimeObject {

	LOrientation getOrientation();

	void step(double dtime);

	void stop();

	String getParameter(String name);

	String getName();

	PrintOut printOut();

}
