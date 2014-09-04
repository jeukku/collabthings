package org.libraryofthings.model;

import org.libraryofthings.math.LVector;

public interface LOTRuntimeObject {

	LVector getLocation();

	LVector getAbsoluteLocation();

	void setParent(LOTRuntimeObject parent);

	void step(double dtime);

	void stop();

	String getParameter(String name);

}
