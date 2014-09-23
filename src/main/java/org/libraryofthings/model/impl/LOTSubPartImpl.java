package org.libraryofthings.model.impl;

import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;

import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public final class LOTSubPartImpl implements LOTSubPart {
	/**
	 * 
	 */
	private final LOTPart parent;
	private LOTPart part;
	private final LVector p = new LVector();
	private final LVector n = new LVector();
	private final LOTClient client;

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

	public void parse(JBean bpart) {
		MStringID partid = bpart.getIDValue("id");
		part = this.client.getObjectFactory().getPart(partid);
		p.set(bpart.get("p"));
		n.set(bpart.get("n"));
	}

	public void getBean(JBean bpart) {
		bpart.addValue("id", part.getID());
		bpart.add("p", p.getBean());
		bpart.add("n", n.getBean());
	}

	@Override
	public void setPart(LOTPart part2) {
		this.part = (LOTPartImpl) part2;
	}

	public void setOrientation(LVector location, LVector normal) {
		this.p.set(location);
		this.n.set(normal);
	}

	public LVector getNormal() {
		return n;
	}

	public LVector getLocation() {
		return p;
	}

	@Override
	public String toString() {
		return "SubPart[" + p + "][" + n + "]";
	}
}