package org.libraryofthings.model;

import org.libraryofthings.LOTEnvironment;

import waazdoh.client.Binary;
import waazdoh.client.MBinaryID;
import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MID;
import waazdoh.cutils.xml.JBean;

public class LOT3DModel implements ServiceObjectData {
	private static final String BEANNAME = "model3d";
	private static final String NAME = "name";
	private static final String BINARYID = "binaryid";
	//
	private ServiceObject o;
	private String name = "";
	private MBinaryID binaryid;
	private LOTEnvironment env;

	public LOT3DModel(LOTEnvironment env) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
	}

	public LOT3DModel(LOTEnvironment env, MID id) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
		o.load(id.getStringID());
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(NAME, name);
		if (binaryid != null) {
			b.addValue(BINARYID, binaryid.toString());
		}
		//
		return b;
	}

	@Override
	public boolean parseBean(JBean bean) {
		name = bean.getValue("name");
		return true;
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public Binary getBinary() {
		if (env != null && binaryid != null) {
			return this.env.getBinarySource().getOrDownload(binaryid);
		} else {
			return null;
		}
	}

	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return name;
	}

}
