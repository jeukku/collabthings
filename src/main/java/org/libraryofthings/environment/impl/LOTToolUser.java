package org.libraryofthings.environment.impl;

import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTRuntimeObject;

public interface LOTToolUser extends LOTRuntimeObject {

	void move(LVector l, LVector n);

	void setTool(LOTToolState lotToolState);

	void step(double dtime);

	LVector getLocation();

	LVector getAbsoluteLocation();

	boolean isAvailable(LOTToolState toolstate);
}
