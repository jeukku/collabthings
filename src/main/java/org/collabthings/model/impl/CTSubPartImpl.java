package org.collabthings.model.impl;

import org.collabthings.CTClient;
import org.collabthings.math.CTMath;
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
	private CTPartImpl parent;
	private String name;

	/**
	 * @param nparent
	 * @param env
	 */
	public CTSubPartImpl(final CTPartImpl nparent, final CTClient env) {
		this.parent = nparent;
		this.client = env;
		defaultName();
	}

	@Override
	public void save() {
		if (part != null) {
			part.save();
		}
	}

	public String getNamePath() {
		return parent.getShortname() + "->" + name;
	}

	public String getName() {
		return name;
	}

	@Override
	public void setName(String text) {
		this.name = text;
	}

	@Override
	public void set(CTSubPart nsubpart) {
		CTSubPartImpl subpart = (CTSubPartImpl) nsubpart;
		part = subpart.part;
		p.set(subpart.p);
		n.set(subpart.n);
		angle = subpart.angle;
	}

	@Override
	public void publish() {
		if (part != null) {
			part.publish();
		}
	}

	public CTPart getPart() {
		if (part == null) {
			if (partid == null || !partid.isId()) {
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
		name = bpart.getValue("name");

		defaultName();
	}

	private void defaultName() {
		if (name == null || "null".equals(name)) {
			if (parent != null) {
				name = "sub" + parent.getSubParts().size();
			} else {
				name = "sub";
			}
		}
	}

	public void getBean(WObject bpart) {
		if (part != null) {
			partid = part.getID().getStringID();
		}

		bpart.setAttribute("id", "" + partid);
		bpart.add("p", p.getBean());
		bpart.add("n", n.getBean());
		bpart.addValue("a", angle);
		bpart.addValue("name", "" + name);
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
		return CTMath.limitAngle(angle);
	}

	@Override
	public void setAngle(double angle) {
		this.angle = CTMath.limitAngle(angle);
	}

	@Override
	public String toString() {
		return "SubPart[" + p + "][" + n + "]";
	}
}