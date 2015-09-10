package org.collabthings.environment.impl;

import org.collabthings.LOTToolException;
import org.collabthings.environment.RunEnvironmentDrawer;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTRuntimeObject;

public interface LOTToolUser extends LOTRuntimeObject {

	void move(LVector l, LVector n, double angle);

	void setTool(LOTToolState lotToolState);

	boolean isAvailable(LOTToolState toolstate);

	void callDraw(RunEnvironmentDrawer view, LTransformationStack tstack)
			throws LOTToolException;

	LOTEvents getEvents();

	LOTToolState getTool();

	LTransformation getTransformation();

}
