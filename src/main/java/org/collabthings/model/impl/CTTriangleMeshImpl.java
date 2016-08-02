package org.collabthings.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;

public class CTTriangleMeshImpl implements CTTriangleMesh {

	private List<CTTriangle> ts = new ArrayList<CTTriangle>();
	private List<Vector3f> vs = new ArrayList<Vector3f>();

	@Override
	public List<CTTriangle> getTriangles() {
		return ts;
	}

	@Override
	public List<Vector3f> getVectors() {
		return vs;
	}
}
