package org.collabthings.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;

import com.jme3.math.Vector3f;

public class CTTriangleMeshImpl implements CTTriangleMesh {
	private final List<CTTriangle> ts = new ArrayList<>();
	private final List<Vector3f> vs = new ArrayList<>();

	@Override
	public List<CTTriangle> getTriangles() {
		return new ArrayList<>(ts);
	}

	@Override
	public List<Vector3f> getVectors() {
		return new ArrayList<>(vs);
	}
}
