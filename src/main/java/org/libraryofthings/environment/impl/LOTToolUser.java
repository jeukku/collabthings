package org.libraryofthings.environment.impl;

import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.SimulationView;
import org.libraryofthings.math.LTransformationStack;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTRuntimeObject;

public interface LOTToolUser extends LOTRuntimeObject {

	void move(LVector l, LVector n);

	void setTool(LOTToolState lotToolState);

	void step(double dtime);

	LVector getLocation();

	boolean isAvailable(LOTToolState toolstate);

	void callDraw(SimulationView view, LTransformationStack tstack)
			throws LOTToolException;
}
