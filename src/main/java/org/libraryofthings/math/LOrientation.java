package org.libraryofthings.math;

import waazdoh.client.model.JBean;

public class LOrientation {
	private static final String VALUENAME_ORIENTATION_LOCATION = "location";
	private static final String VALUENAME_ORIENTATION_NORMAL = "normal";
	private static final String VALUENAME_ORIENTATION_ANGLE = "angle";

	public final LVector location = new LVector();
	public final LVector normal = new LVector(0, 1, 0);
	public double angle = 0.0;

	public LOrientation(JBean ob) {
		location.set(new LVector(ob.get(VALUENAME_ORIENTATION_LOCATION)));
		normal.set(new LVector(ob.get(VALUENAME_ORIENTATION_NORMAL)));
		angle = ob.getDoubleValue(VALUENAME_ORIENTATION_ANGLE);
	}

	public LOrientation() {
		//
	}

	@Override
	public String toString() {
		return "[O:(" + location + ")(" + normal + ")(" + angle + ")]";
	}

	public JBean getBean(String beannameOrientation) {
		JBean ob = new JBean(beannameOrientation);
		ob.add(location.getBean(VALUENAME_ORIENTATION_LOCATION));
		ob.add(normal.getBean(VALUENAME_ORIENTATION_NORMAL));
		ob.addValue(VALUENAME_ORIENTATION_ANGLE, angle);
		return ob;
	}

	public void set(LVector n, double d) {
		normal.set(n);
		angle = d;
	}
}
