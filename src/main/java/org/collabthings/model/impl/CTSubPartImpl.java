package org.collabthings.model.impl;

import org.collabthings.CTClient;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;

import waazdoh.common.MStringID;
import waazdoh.common.WObject;

public final class CTSubPartImpl implements CTSubPart {
	private CTPart part;
	private final LVector p = new LVector();
	private final LVector n = new LVector(0, 1, 0);
	private double angle = 0;
	private final CTClient client;
	private LTransformation transformation;
	private MStringID partid;

	/**
	 * @param nparent
	 * @param env
	 */
	public CTSubPartImpl(final CTPartImpl nparent, final CTClient env) {
		this.client = env;
	}

	@Override
	public void save() {
		if (part != null) {
			part.save();
		}
	}

	@Override
	public void publish() {
		if (part != null) {
			part.publish();
		}
	}

	public CTPart getPart() {
		if (part == null) {
			if (partid == null) {
				// new part
				part = this.client.getObjectFactory().getPart();
			} else {
				part = this.client.getObjectFactory().getPart(partid);
			}
		}

		return part;
	}

	public void parse(WObject bpart) {
		partid = bpart.getIDValue("id");
		part = null;
		p.set(bpart.get("p"));
		n.set(bpart.get("n"));
		angle = bpart.getDoubleValue("a");
	}

	public void getBean(WObject bpart) {
		if (part != null) {
			partid = part.getID().getStringID();
		}

		bpart.setAttribute("id", "" + partid);
		bpart.add("p", p.getBean());
		bpart.add("n", n.getBean());
		bpart.addValue("a", angle);
	}

	@Override
	public void setPart(CTPart part2) {
		partid = null;
		this.part = (CTPartImpl) part2;
	}

	@Override
	public LTransformation getTransformation() {
		if (transformation == null) {
			transformation = new LTransformation(getLocation(), getNormal(), angle); // TODO
																						// fix
																						// angle
		}
		return transformation;
	}

	@Override
	public void setOrientation(LVector location, LVector normal, double angle) {
		this.p.set(location);
		this.n.set(normal);
		this.n.normalize();
		transformation = null;
		this.angle = angle;
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
	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public String toString() {
		return "SubPart[" + p + "][" + n + "]";
	}
}