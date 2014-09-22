package org.libraryofthings.model.impl;

import org.libraryofthings.math.LVector;

import waazdoh.client.model.JBean;

public class LOTBoundingBox {
	public static final String BEAN_NAME = "bbox";
	private LVector a;
	private LVector b;

	public LOTBoundingBox(LVector a, LVector b) {
		this.a = a;
		this.b = b;
	}

	public LOTBoundingBox(JBean beanboundingbox) {
		a = new LVector(beanboundingbox.get("a"));
		b = new LVector(beanboundingbox.get("b"));
	}

	public LVector getA() {
		return a.copy();
	}

	public LVector getB() {
		return b.copy();
	}

	public JBean getBean() {
		JBean bean = new JBean(BEAN_NAME);
		JBean beana = a.getBean();
		beana.setName("a");
		bean.add(beana);
		JBean beanb = b.getBean();
		beanb.setName("b");
		bean.add(beanb);
		return bean;
	}
}
