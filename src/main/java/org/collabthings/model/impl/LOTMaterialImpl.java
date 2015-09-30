package org.collabthings.model.impl;

import java.util.StringTokenizer;

import org.collabthings.model.LOTMaterial;

import waazdoh.common.WObject;

public class LOTMaterialImpl implements LOTMaterial {

	private double[] color = new double[] { 1.0, 1.0, 0.0 };

	public LOTMaterialImpl() {
	}

	@Override
	public WObject getBean() {
		WObject d = new WObject("material");
		d.addValue("color", "" + color[0] + ", " + color[1] + ", " + color[2]);
		return d;
	}

	public LOTMaterialImpl(WObject wObject) {
		if (wObject != null) {
			String t = wObject.getValue("color");
			if (t != null) {
				StringTokenizer st = new StringTokenizer(t, ",");
				color[0] = Double.parseDouble(st.nextToken());
				color[1] = Double.parseDouble(st.nextToken());
				color[2] = Double.parseDouble(st.nextToken());
			}
		}
	}

	@Override
	public double[] getColor() {
		return color;
	}
}
