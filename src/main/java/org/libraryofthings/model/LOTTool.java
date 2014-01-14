package org.libraryofthings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.libraryofthings.LOTEnvironment;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public class LOTTool implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "script";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_SCRIPTS = "scripts";
	//
	private ServiceObject o;
	private String name;
	private LOT3DModel model;
	private Map<String, LOTScript> scripts = new HashMap<String, LOTScript>();
	private LOTEnvironment env;

	public LOTTool(LOTEnvironment env) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
	}

	public LOTTool(LOTEnvironment env, MStringID id) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.setBase64Value(VALUENAME_NAME, getName());
		if (model != null) {
			b.addValue(VALUENAME_MODELID, model.getServiceObject().getID());
		}
		//
		JBean ssbean = b.add(VALUENAME_SCRIPTS);
		Set<String> scriptnames = scripts.keySet();
		for (String string : scriptnames) {
			LOTScript s = getScript(string);
			JBean sbean = ssbean.add("script");
			sbean.addValue("name", string);
			sbean.addValue("id", s.getServiceObject().getID());
		}

		return b;
	}

	public LOTScript getScript(String string) {
		return scripts.get(string);
	}

	@Override
	public boolean parseBean(JBean bean) {
		setName(bean.getBase64Value(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		if (modelid != null) {
			model = new LOT3DModel(env, modelid);
		}
		//
		JBean ssbean = bean.get(VALUENAME_SCRIPTS);
		List<JBean> sbeans = ssbean

		.getChildren();
		for (JBean sbean : sbeans) {
			String scriptname = sbean.getValue("name");
			MStringID id = sbean.getIDValue("id");
			scripts.put(scriptname, new LOTScript(env, id));
		}
		//
		return getName() != null;
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isReady() {
		if (model != null && !model.isReady()) {
			return false;
		}

		return true;
	}

	public void addScript(String scriptname, LOTScript lotScript) {
		scripts.put(scriptname, lotScript);
	}

	public void save() {
		if (model != null) {
			model.save();
		}

		for (LOTScript s : scripts.values()) {
			s.getServiceObject().save();
		}

		getServiceObject().save();
	}

	public void publish() {
		if (model != null) {
			model.publish();
		}

		for (LOTScript s : scripts.values()) {
			s.getServiceObject().publish();
		}

		getServiceObject().publish();
	}

	public LOT3DModel getModel() {
		return model;
	}

	public void newModel() {
		model = new LOT3DModel(env);
	}

}
