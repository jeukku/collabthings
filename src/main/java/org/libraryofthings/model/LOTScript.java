package org.libraryofthings.model;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public final class LOTScript implements ServiceObjectData {
	private static final String SCRIPT = "value";
	private static final String BEANNAME = "script";
	//
	private ServiceObject o;
	private String script;

	public LOTScript(LOTEnvironment env) {
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
	}

	public LOTScript(LOTEnvironment env, MStringID id) {
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		return b;
	}

	@Override
	public boolean parseBean(JBean bean) {
		script = bean.getBase64Value(SCRIPT);
		return script != null;
	}

	public void setScript(String string) {
		this.script = string;
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public String getScript() {
		return script;
	}

	public void run(RunEnvironment runenv) {
		// TODO Auto-generated method stub
		
	}
}
