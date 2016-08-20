package org.collabthings.model;

import java.util.List;

import com.jme3.math.Vector3f;

public interface CTTriangleMesh {

	List<CTTriangle> getTriangles();

	List<Vector3f> getVectors();

}
