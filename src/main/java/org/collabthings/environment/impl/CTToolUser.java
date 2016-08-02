package org.collabthings.environment.impl;

import com.jme3.math.Vector3f;
import com.jme3.math.Transform;
import org.collabthings.model.CTRuntimeObject;

public interface CTToolUser extends CTRuntimeObject {

	void move(Vector3f l, Vector3f n, double angle);

	void setTool(CTToolState ctToolState);

	boolean isAvailable(CTToolState toolstate);

	CTEvents getEvents();

	CTToolState getTool();

	Transform getTransformation();

}
