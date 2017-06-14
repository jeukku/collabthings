/*******************************************************************************
 * Copyright (c) 2017 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.model;

import com.jme3.math.Vector3f;

import waazdoh.common.WObject;

public interface CTViewingProperties {

	void getObject(WObject add);

	Vector3f getLookAt();

	void setLookAt(Vector3f lookat);

}
