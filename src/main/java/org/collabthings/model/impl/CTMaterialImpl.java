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

import java.util.StringTokenizer;

import org.collabthings.model.CTMaterial;

import waazdoh.common.WObject;

public class CTMaterialImpl implements CTMaterial {

	private static final int INDEX_RED = 0;
	private static final int INDEX_GREEN = 1;
	private static final int INDEX_BLUE = 2;
	private static final double MINIMUM_AVERAGE_RANDOM_COLOR_VALUE = 0.8;
	private double[] color = new double[] { 1.0, 1.0, 0.0 };

	public CTMaterialImpl() {
		double r, g, b;
		do {
			r = Math.random();
			g = Math.random();
			b = Math.random();
		} while ((r + g + b) / 3 > MINIMUM_AVERAGE_RANDOM_COLOR_VALUE);
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}

	@Override
	public WObject getBean() {
		WObject d = new WObject("material");
		d.addValue("color",
				"" + Double.toString(color[0]) + ", " + Double.toString(color[1]) + ", " + Double.toString(color[2]));
		return d;
	}

	public CTMaterialImpl(WObject wObject) {
		if (wObject != null) {
			String t = wObject.getValue("color");
			if (t != null) {
				StringTokenizer st = new StringTokenizer(t, ",");
				color[INDEX_RED] = Double.parseDouble(st.nextToken());
				color[INDEX_GREEN] = Double.parseDouble(st.nextToken());
				color[INDEX_BLUE] = Double.parseDouble(st.nextToken());
			}
		}
	}

	@Override
	public double[] getColor() {
		return color.clone();
	}
}
