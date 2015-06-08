package org.collabthings.model;

import org.collabthings.PrintOut;
import org.collabthings.math.LOrientation;

public interface LOTRuntimeObject {

	LOrientation getOrientation();

	void step(double dtime);

	void stop();

	String getParameter(String name);

	String getName();

	PrintOut printOut();

}
