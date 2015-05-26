package org.collabthings.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.math.LVector;
import org.collabthings.model.LOTTriangle;
import org.collabthings.model.LOTTriangleMesh;

public class LOTTriangleMeshImpl implements LOTTriangleMesh {

	private List<LOTTriangle> ts = new ArrayList<LOTTriangle>();
	private List<LVector> vs = new ArrayList<LVector>();

	@Override
	public List<LOTTriangle> getTriangles() {
		return ts;
	}

	@Override
	public List<LVector> getVectors() {
		return vs;
	}

}
