package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.LOTClient;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

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
	private WObject bean;

	public LOTEnvironmentImpl(LOTClient nclient) {
		this.client = nclient;
		scripts = new HashMap<String, LOTScript>();
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
	public WObject getObject() {
		WObject b = o.getBean();

		b.addValue("name", getName());

		getScriptsBean(b);
		getToolsBean(b);
		getParametersBean(b);
		getVectorParametersBean(b);

		return b;
	}

	private void getToolsBean(WObject b) {
		Set<String> toolnames = tools.keySet();
		for (String string : toolnames) {
			LOTTool s = getTool(string);
			WObject o = new WObject();
			o.addValue("name", string);
			o.addValue("id", s.getID());
			b.addToList(VALUENAME_TOOLS, o);
		}
	}

	private void getScriptsBean(WObject b) {
		synchronized (getScriptsSet()) {
			Set<String> scriptnames = getScriptsSet().keySet();
			for (String string : scriptnames) {
				LOTScript s = getScript(string);
				if (s != null) {
					WObject so = new WObject();
					so.addValue("name", string);
					so.addValue("id", s.getID().toString());
					b.addToList(VALUENAME_SCRIPTS, so);
				} else {
					getLogger().warning("script \"" + string + "\" null");
				}
			}
		}
	}

	private void getVectorParametersBean(WObject b) {
		Set<String> names = vparameters.keySet();
		for (String string : names) {
			LVector v = getVectorParameter(string);
			WObject sbean = new WObject();
			sbean.addValue("name", string);
			sbean.add("value", v.getBean());
			b.addToList(VALUENAME_VPARAMS, sbean);
		}
	}

	private void getParametersBean(WObject b) {
		Set<String> names = parameters.keySet();
		for (String string : names) {
			String s = getParameter(string);
			WObject sbean = new WObject();
			sbean.addValue("name", string);
			sbean.addValue("value", s);
			b.addToList(VALUENAME_PARAMS, sbean);
		}
	}

	@Override
	public boolean parseBean(WObject bean) {
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

	private void parseScripts(WObject bean) {
		scripts = new HashMap<>();
		List<WObject> sbeans = bean.getObjectList(VALUENAME_SCRIPTS);
		for (WObject sbean : sbeans) {
			String scriptname = sbean.getValue("name");
			MStringID id = sbean.getIDValue("id");
			LOTScriptImpl script = new LOTScriptImpl(client);
			script.load(id);
			getScriptsSet().put(scriptname, script);
		}
	}

	private void parseTools(WObject bean) {
		List<WObject> tbeans = bean.getObjectList(VALUENAME_TOOLS);
		for (WObject b : tbeans) {
			String toolname = b.getValue("name");
			MStringID id = b.getIDValue("id");
			LOTToolImpl tool = new LOTToolImpl(client, id);
			tools.put(toolname, tool);
		}
	}

	private void parseParameters(WObject bean) {
		List<WObject> pbeans = bean.getObjectList(VALUENAME_PARAMS);
		for (WObject b : pbeans) {
			String param = b.getValue("name");
			String value = b.getValue("value");
			parameters.put(param, value);
		}
	}

	private void parseVParameters(WObject bean) {
		List<WObject> pbeans = bean.getObjectList(VALUENAME_VPARAMS);
		for (WObject b : pbeans) {
			String name = b.getValue("name");
			WObject value = b.get("value");
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
	public void renameTool(String oldname, String newname) {
		synchronized (tools) {
			LOTTool t = tools.remove(oldname);
			tools.put(newname, t);
		}
	}

	@Override
	public void deleteTool(String string) {
		synchronized (tools) {
			tools.remove(string);
		}
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
	public Set<String> getParameters() {
		return parameters.keySet();
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

	@Override
	public PrintOut printOut() {
		PrintOut po = new PrintOut();

		po.append(0, getObject().toText());

		return po;
	}
}
