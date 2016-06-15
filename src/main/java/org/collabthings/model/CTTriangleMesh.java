package org.collabthings.model;

import java.util.List;

import org.collabthings.math.LVector;

public interface CTTriangleMesh {

	List<CTTriangle> getTriangles();

	List<LVector> getVectors();

}
