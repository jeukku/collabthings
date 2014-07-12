package org.libraryofthings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.libraryofthings.LOTClient;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.client.model.MID;
import waazdoh.util.MStringID;

public class LOTEnvironmentImpl implements LOTEnvironment, LOTObject,
		ServiceObjectData {
	private static final String BEANNAME = "env";
	private static final String VALUENAME_SCRIPTS = "scripts";
	private static final String VALUENAME_TOOLS = "tools";
	private static final String VALUENAME_PARAMS = "parameters";
	//
	private LOTClient client;
	private ServiceObject o;
	//
	private Map<String, LOTScript> scripts = new HashMap<>();
	private Map<String, LOTTool> tools = new HashMap<>();
	private Map<String, String> parameters = new HashMap<>();

	public LOTEnvironmentImpl(LOTClient nclient) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
	}

	public LOTEnvironmentImpl(LOTClient nclient, MStringID idValue) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		o.load(idValue);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();

		getScriptsBean(b);
		getToolsBean(b);
		getParametersBean(b);

		return b;
	}

	private void getToolsBean(JBean b) {
		JBean toolbean = b.add(VALUENAME_TOOLS);
		Set<String> toolnames = tools.keySet();
		for (String string : toolnames) {
			LOTTool s = getTool(string);
			JBean sbean = toolbean.add("tool");
			sbean.addValue("name", string);
			sbean.addValue("id", s.getServiceObject().getID());
		}
	}

	private void getScriptsBean(JBean b) {
		JBean scriptbean = b.add(VALUENAME_SCRIPTS);
		Set<String> scriptnames = scripts.keySet();
		for (String string : scriptnames) {
			LOTScript s = getScript(string);
			JBean sbean = scriptbean.add("script");
			sbean.addValue("name", string);
			sbean.addValue("id", s.getServiceObject().getID());
		}
	}

	private void getParametersBean(JBean b) {
		JBean bean = b.add(VALUENAME_PARAMS);
		Set<String> names = parameters.keySet();
		for (String string : names) {
			String s = getParameter(string);
			JBean sbean = bean.add("parameter");
			sbean.addValue("name", string);
			sbean.addValue("value", s);
		}
	}

	@Override
	public boolean parseBean(JBean bean) {
		parseScripts(bean);
		parseTools(bean);
		parseParameters(bean);
		return true;
	}

	private void parseScripts(JBean bean) {
		JBean ssbean = bean.get(VALUENAME_SCRIPTS);
		List<JBean> sbeans = ssbean.getChildren();
		for (JBean sbean : sbeans) {
			String scriptname = sbean.getValue("name");
			MStringID id = sbean.getIDValue("id");
			LOTScript script = new LOTScript(client);
			script.load(id);
			scripts.put(scriptname, script);
		}
	}

	private void parseTools(JBean bean) {
		JBean tbean = bean.get(VALUENAME_TOOLS);
		List<JBean> tbeans = tbean.getChildren();
		for (JBean b : tbeans) {
			String name = b.getValue("name");
			MStringID id = b.getIDValue("id");
			LOTTool tool = new LOTTool(client, id);
			tools.put(name, tool);
		}
	}

	private void parseParameters(JBean bean) {
		JBean pbean = bean.get(VALUENAME_PARAMS);
		List<JBean> pbeans = pbean.getChildren();
		for (JBean b : pbeans) {
			String name = b.getValue("name");
			String value = b.getValue("value");
			parameters.put(name, value);
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void addScript(String scriptname, LOTScript lotScript) {
		scripts.put(scriptname, lotScript);
	}

	@Override
	public LOTScript getScript(String string) {
		return scripts.get(string);
	}

	@Override
	public void addTool(String string, LOTTool tool) {
		tools.put(string, tool);
	}

	@Override
	public LOTTool getTool(String string) {
		return tools.get(string);
	}

	@Override
	public void setParameter(String string, MID id) {
		setParameter(string, id.toString());
	}

	@Override
	public void setParameter(String string, String value) {
		parameters.put(string, value);
	}

	@Override
	public String getParameter(String string) {
		return parameters.get(string);
	}

	@Override
	public void save() {
		for (LOTScript s : scripts.values()) {
			s.getServiceObject().save();
		}

		for (LOTTool tool : this.tools.values()) {
			tool.save();
		}

		o.save();
	}

	@Override
	public void publish() {
		for (LOTScript s : scripts.values()) {
			s.getServiceObject().publish();
		}

		for (LOTTool tool : this.tools.values()) {
			tool.publish();
		}

		o.publish();
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public MID getID() {
		return getServiceObject().getID();
	}
}
