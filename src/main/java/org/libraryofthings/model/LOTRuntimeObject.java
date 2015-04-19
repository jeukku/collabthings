package org.libraryofthings.model;

import org.libraryofthings.math.LTransformation;

public interface LOTRuntimeObject {

	LTransformation getTransformation();

	void step(double dtime);

	void stop();

	String getParameter(String name);

	String getName();

}
