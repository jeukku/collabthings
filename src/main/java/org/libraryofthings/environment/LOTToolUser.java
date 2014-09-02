package org.libraryofthings.environment;

import org.libraryofthings.math.LVector;

public interface LOTToolUser extends LOTRuntimeObject {

	void move(LVector l, LVector n);

	void setTool(LOTToolState lotToolState);

	void step(double dtime);

	LVector getLocation();

	LVector getAbsoluteLocation();

	boolean isAvailable(LOTToolState toolstate);
}
