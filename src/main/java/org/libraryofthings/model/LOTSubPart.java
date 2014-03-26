package org.libraryofthings.model;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.math.LVector;

import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public final class LOTSubPart {
	/**
	 * 
	 */
	private final LOTPart parent;
	private LOTPart part;
	private final LVector p = new LVector();
	private final LVector n = new LVector();

	/**
	 * @param nparent
	 * @param env
	 */
	public LOTSubPart(final LOTPart nparent, final LOTEnvironment env) {
		this.parent = nparent;
		part = new LOTPart(env);
	}

	public LOTPart getPart() {
		return part;
	}

	public void parse(JBean bpart) {
		MStringID partid = bpart.getIDValue("id");
		part = this.parent.env.getObjectFactory().getPart(partid);
		p.set(bpart.get("p"));
		n.set(bpart.get("n"));
	}

	public void getBean(JBean bpart) {
		bpart.addValue("id", part.getServiceObject().getID());
		bpart.add("p", p.getBean());
		bpart.add("n", n.getBean());
	}

	public void setPart(LOTPart part2) {
		this.part = part2;
	}

	public void setOrientation(LVector location, LVector normal) {
		this.p.set(location);
		this.n.set(normal);
	}

	public LVector getNormal() {
		return p;
	}

	public LVector getLocation() {
		return n;
	}

	@Override
	public String toString() {
		return "SubPart[" + p + "][" + n + "]";
	}
}