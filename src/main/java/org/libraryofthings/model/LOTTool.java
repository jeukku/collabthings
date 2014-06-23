package org.libraryofthings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.RunEnvironment;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public final class LOTTool implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "tool";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_SCRIPTS = "scripts";
	//
	public static int counter = 0;
	//
	private ServiceObject o;
	private String name = "tool" + (LOTTool.counter++);
	private LOT3DModel model;
	private Map<String, LOTScript> scripts = new HashMap<String, LOTScript>();
	private LOTEnvironment env;

	public LOTTool(final LOTEnvironment nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
	}

	public LOTTool(final LOTEnvironment nenv, final MStringID id) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
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
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		if (modelid != null) {
			model = new LOT3DModel(env);
			model.load(modelid);
		}
		//
		JBean ssbean = bean.get(VALUENAME_SCRIPTS);
		List<JBean> sbeans = ssbean

		.getChildren();
		for (JBean sbean : sbeans) {
			String scriptname = sbean.getValue("name");
			MStringID id = sbean.getIDValue("id");
			LOTScript script = new LOTScript(env);
			script.load(id);
			scripts.put(scriptname, script);
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

	public void setName(final String nname) {
		this.name = nname;
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

	public void call(final RunEnvironment runenv, final String scriptname,
			Object params[]) throws LOTToolException {
		LOTScript script = getScript(scriptname);

		if (script != null) {
			script.run(runenv, params);
		} else {
			throw new LOTToolException("Script called '" + scriptname
					+ "' does not exist in " + this);
		}
	}

	@Override
	public String toString() {
		return "LOTTool[" + name + "]";
	}
}
