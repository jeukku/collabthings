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

package org.collabthings.environment.impl;

import org.collabthings.model.CTRuntimeObject;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public interface CTToolUser extends CTRuntimeObject {

	void move(Vector3f l, Vector3f n, double angle);

	void setTool(CTToolState ctToolState);

	boolean isAvailable(CTToolState toolstate);

	CTEvents getEvents();

	CTToolState getTool();

	Transform getTransformation();

}
