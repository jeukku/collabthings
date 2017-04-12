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
package org.collabthings.model;

import java.util.List;

import com.jme3.math.Vector3f;

public interface CTTriangleMesh {

	List<CTTriangle> getTriangles();

	List<Vector3f> getVectors();

	void add(Vector3f vector3f);

	void add(CTTriangle ctTriangle);

}
