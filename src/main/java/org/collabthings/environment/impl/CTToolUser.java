package org.collabthings.environment.impl;

import org.collabthings.model.CTRuntimeObject;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public interface CTToolUser extends CTRuntimeObject {

	void move(Vector3f l, Vector3f n, double angle);

	void setTool(CTToolState ctToolState);

	boolean isAvailable(CTToolState toolstate);

	CTEvents getEvents();

	CTToolState getTool();

	Transform getTransformation();

}
