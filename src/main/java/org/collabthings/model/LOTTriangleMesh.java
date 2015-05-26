package org.collabthings.model;

import java.util.List;

import org.collabthings.math.LVector;

public interface LOTTriangleMesh {

	List<LOTTriangle> getTriangles();

	List<LVector> getVectors();

}
