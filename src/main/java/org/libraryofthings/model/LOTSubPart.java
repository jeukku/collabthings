package org.libraryofthings.model;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.libraryofthings.LOTEnvironment;

import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public class LOTSubPart {
	/**
	 * 
	 */
	private final LOTPart parent;
	private LOTPart part;
	private RealVector p = new ArrayRealVector(3);

	/**
	 * @param parent
	 * @param env 
	 */
	LOTSubPart(LOTPart parent, LOTEnvironment env) {
		this.parent = parent;
		part = new LOTPart(env);
	}

	public LOTPart getPart() {
		return part;
	}

	public void parse(JBean bpart) {
		MStringID partid = bpart.getIDValue("id");
		part = this.parent.env.getObjectFactory().getPart(partid);
	}

	public void getBean(JBean bpart) {
		bpart.addValue("id", part.getServiceObject().getID());
	}
}