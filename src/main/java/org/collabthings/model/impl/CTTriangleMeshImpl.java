/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

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

	@Override
	public void add(Vector3f nv) {
		vs.add(nv);
	}

	@Override
	public void add(CTTriangle tri) {
		this.ts.add(tri);
	}

}
