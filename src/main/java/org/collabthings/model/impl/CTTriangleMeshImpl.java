package org.collabthings.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.math.LVector;
import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;

public class CTTriangleMeshImpl implements CTTriangleMesh {

	private List<CTTriangle> ts = new ArrayList<CTTriangle>();
	private List<LVector> vs = new ArrayList<LVector>();

	@Override
	public List<CTTriangle> getTriangles() {
		return ts;
	}

	@Override
	public List<LVector> getVectors() {
		return vs;
	}

}
