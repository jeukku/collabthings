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

package org.collabthings.model.impl;

import org.collabthings.math.CTMath;
import org.collabthings.model.CTViewingProperties;

import com.jme3.math.Vector3f;

import waazdoh.common.WObject;

public class CTViewingPropertiesImpl implements CTViewingProperties {

	private Vector3f lookat;

	public CTViewingPropertiesImpl(WObject oviewingproperties) {
		lookat = CTMath.parseVector(oviewingproperties.get("lookat"));
	}

	public CTViewingPropertiesImpl() {
		lookat = new Vector3f();
	}

	@Override
	public void getObject(WObject add) {
		add.add("lookat", CTMath.getBean(lookat));
	}

	@Override
	public Vector3f getLookAt() {
		return lookat;
	}

	@Override
	public void setLookAt(Vector3f lookat) {
		this.lookat = lookat;
	}

}
