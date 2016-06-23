package org.collabthings.model.impl;

import java.util.StringTokenizer;

import org.collabthings.model.CTMaterial;

import waazdoh.common.WObject;

public class CTMaterialImpl implements CTMaterial {

	private double[] color = new double[] { 1.0, 1.0, 0.0 };

	public CTMaterialImpl() {
		double r, g, b;
		do {
			r = Math.random();
			g = Math.random();
			b = Math.random();
		} while ((r + g + b) / 3 > 0.8);
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}

	@Override
	public WObject getBean() {
		WObject d = new WObject("material");
		d.addValue("color", "" + color[0] + ", " + color[1] + ", " + color[2]);
		return d;
	}

	public CTMaterialImpl(WObject wObject) {
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
