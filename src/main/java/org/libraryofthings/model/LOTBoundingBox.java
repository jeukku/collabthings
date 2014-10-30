package org.libraryofthings.model;

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
		set(beanboundingbox);
	}

	public LVector getA() {
		return a;
	}

	public LVector getB() {
		return b;
	}

	public JBean getBean() {
		JBean bean = new JBean(BEAN_NAME);
		JBean beana = a.getBean("a");
		bean.add(beana);
		JBean beanb = b.getBean("b");
		beanb.setName("b");
		bean.add(beanb);
		return bean;
	}

	public void set(JBean bean) {
		a = new LVector(bean.get("a"));
		b = new LVector(bean.get("b"));
	}

	public void set(LVector a2, LVector b2) {
		a.set(a2);
		b.set(b2);
	}
}
