package org.collabthings.environment.impl;

import org.collabthings.LOTToolException;
import org.collabthings.environment.SimulationView;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTRuntimeObject;

public interface LOTToolUser extends LOTRuntimeObject {

	void move(LVector l, LVector n, double angle);

	void setTool(LOTToolState lotToolState);

	void step(double dtime);

	// LVector getLocation();

	boolean isAvailable(LOTToolState toolstate);

	void callDraw(SimulationView view, LTransformationStack tstack)
			throws LOTToolException;

	LOTEvents getEvents();

	LOTToolState getTool();

	LTransformation getTransformation();

}
