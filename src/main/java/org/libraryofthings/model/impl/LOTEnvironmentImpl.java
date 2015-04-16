package org.libraryofthings.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

public class LOTEnvironmentImpl implements LOTEnvironment, ServiceObjectData {
	private static final String BEANNAME = "env";
	private static final String VALUENAME_SCRIPTS = "scripts";
	private static final String VALUENAME_TOOLS = "tools";
	private static final String VALUENAME_PARAMS = "params";
	private static final String VALUENAME_VPARAMS = "vparams";
	private static final String VALUENAME_MAPITEM = "item";
	//
	private LOTClient client;
	private ServiceObject o;
	//
	private Map<String, LOTScript> scripts;
	private Map<String, LOTTool> tools = new HashMap<>();
	private Map<String, String> parameters = new HashMap<>();
	private Map<String, LVector> vparameters = new HashMap<>();
	private LLog log;

	private String name = "env";
	private WData bean;

	public LOTEnvironmentImpl(LOTClient nclient) {
		this.client = nclient;
		scripts = new HashMap<String, LOTScript>();
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(),
				nclient.getPrefix());
	}

	public LOTEnvironmentImpl(LOTClient nclient, MStringID idValue) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(),
				nclient.getPrefix());
		o.load(idValue);
	}

	@Override
	public String toString() {
		return "Environment";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();

		b.addValue("name", getName());

		getScriptsBean(b);
		getToolsBean(b);
		getParametersBean(b);
		getVectorParametersBean(b);

		return b;
	}

	private void getToolsBean(WData b) {
		WData toolbean = b.add(VALUENAME_TOOLS);
		Set<String> toolnames = tools.keySet();
		for (String string : toolnames) {
			LOTTool s = getTool(string);
			WData sbean = toolbean.add("tool");
			sbean.addValue("name", string);
			sbean.addValue("id", s.getID());
		}
	}

	private void getScriptsBean(WData b) {
		synchronized (getScriptsSet()) {
			WData scriptbean = b.add(VALUENAME_SCRIPTS);
			Set<String> scriptnames = getScriptsSet().keySet();
			for (String string : scriptnames) {
				LOTScript s = getScript(string);
				if (s != null) {
					WData sbean = scriptbean.add("script");
					sbean.addValue("name", string);
					sbean.addValue("id", s.getID());
				} else {
					getLogger().warning("script \"" + string + "\" null");
				}
			}
		}
	}

	private void getVectorParametersBean(WData b) {
		WData bean = b.add(VALUENAME_VPARAMS);
		Set<String> names = vparameters.keySet();
		for (String string : names) {
			LVector v = getVectorParameter(string);
			WData sbean = bean.add(VALUENAME_MAPITEM);
			sbean.addValue("name", string);
			sbean.add(v.getBean("value"));
		}
	}

	private void getParametersBean(WData b) {
		WData bean = b.add(VALUENAME_PARAMS);
		Set<String> names = parameters.keySet();
		for (String string : names) {
			String s = getParameter(string);
			WData sbean = bean.add(VALUENAME_MAPITEM);
			sbean.addValue("name", string);
			sbean.addValue("value", s);
		}
	}

	@Override
	public boolean parseBean(WData bean) {
		this.bean = bean;

		parseTools(bean);
		parseParameters(bean);
		parseVParameters(bean);

		name = bean.getValue("name");

		return true;
	}

	private Map<String, LOTScript> getScriptsSet() {
		if (scripts == null) {
			parseScripts(bean);
		}

		return scripts;
	}

	private void parseScripts(WData bean) {
		scripts = new HashMap<>();
		WData ssbean = bean.get(VALUENAME_SCRIPTS);
		List<WData> sbeans = ssbean.getChildren();
		for (WData sbean : sbeans) {
			String scriptname = sbean.getValue("name");
			MStringID id = sbean.getIDValue("id");
			LOTScriptImpl script = new LOTScriptImpl(client);
			script.load(id);
			getScriptsSet().put(scriptname, script);
		}
	}

	private void parseTools(WData bean) {
		WData tbean = bean.get(VALUENAME_TOOLS);
		List<WData> tbeans = tbean.getChildren();
		for (WData b : tbeans) {
			String name = b.getValue("name");
			MStringID id = b.getIDValue("id");
			LOTToolImpl tool = new LOTToolImpl(client, id);
			tools.put(name, tool);
		}
	}

	private void parseParameters(WData bean) {
		WData pbean = bean.get(VALUENAME_PARAMS);
		List<WData> pbeans = pbean.getChildren();
		for (WData b : pbeans) {
			String name = b.getValue("name");
			String value = b.getValue("value");
			parameters.put(name, value);
		}
	}

	private void parseVParameters(WData bean) {
		WData pbean = bean.get(VALUENAME_VPARAMS);
		List<WData> pbeans = pbean.getChildren();
		for (WData b : pbeans) {
			String name = b.getValue("name");
			WData value = b.get("value");
			LVector v = new LVector(value);
			vparameters.put(name, v);
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void renameScript(String oldname, String newname) {
		synchronized (getScriptsSet()) {
			LOTScript s = getScriptsSet().remove(oldname);
			getScriptsSet().put(newname, s);
		}
	}

	@Override
	public void deleteScript(String string) {
		synchronized (getScriptsSet()) {
			getScriptsSet().remove(string);
		}
	}

	@Override
	public void addScript(String scriptname, LOTScript lotScript) {
		synchronized (getScriptsSet()) {
			getScriptsSet().put(scriptname, lotScript);
		}
	}

	@Override
	public LOTScript getScript(String string) {
		synchronized (getScriptsSet()) {
			return getScriptsSet().get(string);
		}
	}

	@Override
	public Set<String> getScripts() {
		synchronized (getScriptsSet()) {
			return this.getScriptsSet().keySet();
		}
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
	public Set<String> getTools() {
		return tools.keySet();
	}

	@Override
	public void setParameter(String string, ObjectID id) {
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
	public void setVectorParameter(String string, LVector v) {
		vparameters.put(string, new LVector(v));
	}

	@Override
	public LVector getVectorParameter(String string) {
		return vparameters.get(string);
	}

	@Override
	public void save() {
		for (LOTScript s : getScriptsSet().values()) {
			s.save();
		}

		for (LOTTool tool : this.tools.values()) {
			tool.save();
		}

		o.save();
	}

	@Override
	public void publish() {
		for (LOTScript s : getScriptsSet().values()) {
			s.publish();
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
	public ObjectID getID() {
		return getServiceObject().getID();
	}

	private LLog getLogger() {
		if (log == null) {
			this.log = LLog.getLogger(this);
		}
		return log;
	}
}
