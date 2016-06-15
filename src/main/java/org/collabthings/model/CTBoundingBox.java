package org.collabthings.model;

import org.collabthings.math.LVector;

import waazdoh.common.WObject;

public class CTBoundingBox {
	public static final String BEAN_NAME = "bbox";
	private LVector a;
	private LVector b;

	public CTBoundingBox(LVector a, LVector b) {
		this.a = a;
		this.b = b;
	}

	public CTBoundingBox(WObject beanboundingbox) {
		set(beanboundingbox);
	}

	public LVector getA() {
		return a;
	}

	public LVector getB() {
		return b;
	}

	public WObject getBean() {
		WObject bean = new WObject();
		WObject beana = a.getBean();
		bean.add("a", beana);
		WObject beanb = b.getBean();
		bean.add("b", beanb);
		return bean;
	}

	public void set(WObject bbbox) {
		a = new LVector(bbbox.get("a"));
		b = new LVector(bbbox.get("b"));
	}

	public void set(LVector a2, LVector b2) {
		a.set(a2);
		b.set(b2);
	}
}
