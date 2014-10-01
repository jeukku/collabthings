package org.libraryofthings.model;

import org.libraryofthings.math.LVector;

public interface LOTRuntimeObject {

	LVector getLocation();

	void step(double dtime);

	void stop();

	String getParameter(String name);

}
