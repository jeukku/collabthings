package org.collabthings.model.impl;

import java.util.Date;

import javax.script.Invocable;

import org.collabthings.LLog;
import org.collabthings.LOTClient;
import org.collabthings.model.LOTOpenSCAD;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class LOTOpenSCADImpl implements ServiceObjectData, LOTOpenSCAD {
	private static final String VARIABLE_NAME = "name";
	private static final String SCRIPT = "value";
	private static final String BEANNAME = "openscad";
	//
	private ServiceObject o;
	private String script;

	//
	private LLog log = LLog.getLogger(this);
	private final LOTClient client;
	//
	private static int namecounter = 0;
	private String name;
	private String info;

	private Invocable inv;
	private String error;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 */
	public LOTOpenSCADImpl(final LOTClient env) {
		this.client = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this,
				env.getVersion(), env.getPrefix());
		setName("OpenSCAD" + (LOTOpenSCADImpl.namecounter++));
		setScript("// created " + new Date() + " by "
				+ env.getService().getUser().getUsername() + "\n"
				+ "// Version " + env.getVersion());
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		b.addValue(VARIABLE_NAME, name);
		return b;
	}

	@Override
	public boolean parseBean(final WData bean) {
		script = bean.getBase64Value(SCRIPT);
		inv = null;
		this.name = bean.getValue(VARIABLE_NAME);
		return name != null && script != null;
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public void setScript(final String nscript) {
		this.script = nscript;
		inv = null;
	}

	@Override
	public String getError() {
		return error;
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	public ObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void publish() {
		getServiceObject().publish();
	}

	@Override
	public void save() {
		getServiceObject().save();
	}

	public String getScript() {
		return script;
	}

	@Override
	public boolean isOK() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "LOTScript[" + this.name + "][" + info + "]";
	}

	public void setName(String name) {
		this.name = name;
	}
}
