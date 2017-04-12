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

import org.collabthings.math.CTMath;

import com.jme3.math.Vector3f;

import waazdoh.common.WObject;

public class CTBoundingBox {
	public static final String BEAN_NAME = "bbox";
	private Vector3f a;
	private Vector3f b;

	public CTBoundingBox(Vector3f a, Vector3f b) {
		this.a = a;
		this.b = b;
	}

	public CTBoundingBox(WObject beanboundingbox) {
		set(beanboundingbox);
	}

	public Vector3f getA() {
		return a;
	}

	public Vector3f getB() {
		return b;
	}

	public WObject getBean() {
		WObject bean = new WObject();
		WObject beana = CTMath.getBean(a);
		bean.add("a", beana);
		WObject beanb = CTMath.getBean(b);
		bean.add("b", beanb);
		return bean;
	}

	public void set(WObject bbbox) {
		a = CTMath.parseVector(bbbox.get("a"));
		b = CTMath.parseVector(bbbox.get("b"));
	}

	public void set(Vector3f a2, Vector3f b2) {
		a.set(a2);
		b.set(b2);
	}
}
