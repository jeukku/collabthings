package org.collabthings.model;

import org.collabthings.math.LTransformation;

public interface LOTRuntimeObject {

	LTransformation getTransformation();

	void step(double dtime);

	void stop();

	String getParameter(String name);

	String getName();

}
