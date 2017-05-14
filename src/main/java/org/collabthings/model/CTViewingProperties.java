package org.collabthings.model;

import com.jme3.math.Vector3f;

import waazdoh.common.WObject;

public interface CTViewingProperties {

	void getObject(WObject add);

	Vector3f getLookAt();

	void setLookAt(Vector3f lookat);

}
