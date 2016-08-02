package org.collabthings.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTListener;
import org.collabthings.math.CTMath;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import waazdoh.common.MStringID;
import waazdoh.common.WObject;

public final class CTSubPartImpl implements CTSubPart {
	private CTPart part;
	private Vector3f p = new Vector3f();
	private Vector3f n = new Vector3f(0, 1, 0);
	private double angle = 0;
	private final CTClient client;
	private Transform transformation;
	private MStringID partid;
	private CTPartImpl parent;
	private String name;
	private List<CTListener> listeners = new ArrayList<>();

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
		changed();
	}

	@Override
	public void set(CTSubPart nsubpart) {
		CTSubPartImpl subpart = (CTSubPartImpl) nsubpart;
		part = subpart.part;
		p.set(subpart.p);
		n.set(subpart.n);
		angle = subpart.angle;
		changed();
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
			part.addChangeListener(() -> changed());
		}

		return part;
	}

	public void parse(WObject bpart) {
		partid = bpart.getIDValue("id");
		part = null;
		p = CTMath.parseVector(bpart.get("p"));
		n = CTMath.parseVector(bpart.get("n"));
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
		bpart.add("p", CTMath.getBean(p));
		bpart.add("n", CTMath.getBean(n));
		bpart.addValue("a", angle);
		bpart.addValue("name", "" + name);
	}

	@Override
	public void setPart(CTPart part2) {
		partid = null;
		this.part = (CTPartImpl) part2;
		changed();
		part.addChangeListener(() -> changed());
	}

	@Override
	public Transform getTransformation() {
		if (transformation == null) {
			transformation = new LOrientation(getLocation(), getNormal(), (float) angle).getTransformation(); // TODO
			// fix
			// angle
		}
		return transformation;
	}

	@Override
	public void setOrientation(Vector3f location, Vector3f normal, double angle) {
		this.p.set(location);
		this.n.set(normal);
		this.n.normalize();
		transformation = null;
		this.angle = angle;
		changed();
	}

	public Vector3f getNormal() {
		return n;
	}

	public Vector3f getLocation() {
		return p;
	}

	public double getAngle() {
		return CTMath.limitAngle(angle);
	}

	@Override
	public void setAngle(double angle) {
		this.angle = CTMath.limitAngle(angle);
		changed();
	}

	private void changed() {
		transformation = null;
		listeners.stream().forEach((l) -> l.event());
	}

	@Override
	public String toString() {
		return "SubPart[" + p + "][" + n + "]";
	}

	public void addChangeListener(CTListener l) {
		listeners.add(l);
	}
}