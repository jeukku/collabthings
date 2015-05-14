package org.collabthings.model.impl;

import org.collabthings.model.LOTMaterial;

public class LOTMaterialImpl implements LOTMaterial {

	private double[] color = new double[] { 1.0, 1.0, 0.0 };

	@Override
	public double[] getColor() {
		return color;
	}
}
