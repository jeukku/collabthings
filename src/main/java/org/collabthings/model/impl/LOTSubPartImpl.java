package org.collabthings.model.impl;

import org.collabthings.LOTClient;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTSubPart;

import waazdoh.common.MStringID;
import waazdoh.common.WData;

public final class LOTSubPartImpl implements LOTSubPart {
	private final LOTPart parent;
	private LOTPart part;
	private final LVector p = new LVector();
	private final LVector n = new LVector();
	private double angle = 0;
	private final LOTClient client;
	private LTransformation transformation;

	/**
	 * @param nparent
	 * @param env
	 */
	public LOTSubPartImpl(final LOTPartImpl nparent, final LOTClient env) {
		this.parent = nparent;
		this.client = env;
		part = new LOTPartImpl(env);
	}

	public LOTPart getPart() {
		return part;
	}

	public void parse(WData bpart) {
		MStringID partid = bpart.getIDValue("id");
		part = this.client.getObjectFactory().getPart(partid);
		p.set(bpart.get("p"));
		n.set(bpart.get("n"));
	}

	public void getBean(WData bpart) {
		bpart.addValue("id", part.getID());
		bpart.add(p.getBean("p"));
		bpart.add(n.getBean("n"));
	}

	@Override
	public void setPart(LOTPart part2) {
		this.part = (LOTPartImpl) part2;
	}

	@Override
	public LTransformation getTransformation() {
		if (transformation == null) {
			transformation = new LTransformation(getLocation(), getNormal(),
					angle); // TODO
							// fix
							// angle
		}
		return transformation;
	}

	public void setOrientation(LVector location, LVector normal, double angle) {
		setOrientation(location, normal);
		this.angle = angle;
	}

	@Override
	public void setOrientation(LVector location, LVector normal) {
		this.p.set(location);
		this.n.set(normal);
		transformation = null;
	}

	public LVector getNormal() {
		return n;
	}

	public LVector getLocation() {
		return p;
	}

	public double getAngle() {
		return angle;
	}

	@Override
	public String toString() {
		return "SubPart[" + p + "][" + n + "]";
	}
}