package org.collabthings.environment.impl;

import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.CTRuntimeObject;

public interface CTToolUser extends CTRuntimeObject {

	void move(LVector l, LVector n, double angle);

	void setTool(CTToolState ctToolState);

	boolean isAvailable(CTToolState toolstate);

	CTEvents getEvents();

	CTToolState getTool();

	LTransformation getTransformation();

}
