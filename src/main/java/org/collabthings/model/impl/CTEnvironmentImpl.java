package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public class CTEnvironmentImpl implements CTEnvironment, ServiceObjectData {
	private static final String BEANNAME = "env";
	private static final String VALUENAME_SCRIPTS = "scripts";
	private static final String VALUENAME_TOOLS = "tools";
	private static final String VALUENAME_PARAMS = "params";
	private static final String VALUENAME_VPARAMS = "vparams";
	private static final String VALUENAME_MAPITEM = "item";
	//
	private CTClient client;
	private ServiceObject o;
	//
	private Map<String, CTScript> scripts;
	private Map<String, CTTool> tools = new HashMap<>();
	private Map<String, String> parameters = new HashMap<>();
	private Map<String, Vector3f> vparameters = new HashMap<>();
	private LLog log;

	private String name = "env";
	private WObject bean;

	public CTEnvironmentImpl(CTClient nclient) {
		this.client = nclient;
		scripts = new HashMap<String, CTScript>();
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
	}

	public CTEnvironmentImpl(CTClient nclient, MStringID idValue) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
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
			CTTool s = getTool(string);
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
				CTScript s = getScript(string);
				if (s != null) {
					WObject so = new WObject();
					so.addValue("name", string);
					so.addValue("id", s.getID().toString());
					b.addToList(VALUENAME_SCRIPTS, so);
				} else {
					log().warning("script \"" + string + "\" null");
				}
			}
		}
	}

	private void getVectorParametersBean(WObject b) {
		Set<String> names = vparameters.keySet();
		for (String string : names) {
			Vector3f v = getVectorParameter(string);
			WObject sbean = new WObject();
			sbean.addValue("name", string);
			sbean.add("value", CTMath.getBean(v));
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
	public boolean parse(WObject bean) {
		this.bean = bean;

		parseTools(bean);
		parseParameters(bean);
		parseVParameters(bean);
		parseScripts(bean);

		name = bean.getValue("name");

		return true;
	}

	private Map<String, CTScript> getScriptsSet() {
		if (scripts == null) {
			parseScripts(bean);
		}

		return scripts;
	}

	private void parseScripts(WObject bean) {
		scripts = new HashMap<>();
		if (bean != null) {
			List<WObject> sbeans = bean.getObjectList(VALUENAME_SCRIPTS);
			for (WObject sbean : sbeans) {
				String scriptname = sbean.getValue("name");
				MStringID id = sbean.getIDValue("id");
				CTScriptImpl script = new CTScriptImpl(client);
				script.load(id);
				getScriptsSet().put(scriptname, script);
			}
		}
	}

	private void parseTools(WObject bean) {
		List<WObject> tbeans = bean.getObjectList(VALUENAME_TOOLS);
		for (WObject b : tbeans) {
			String toolname = b.getValue("name");
			MStringID id = b.getIDValue("id");
			CTToolImpl tool = new CTToolImpl(client, id);
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
			Vector3f v = CTMath.parseVector(value);
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
			CTScript s = getScriptsSet().remove(oldname);
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
	public void addScript(String scriptname, CTScript ctScript) {
		synchronized (getScriptsSet()) {
			getScriptsSet().put(scriptname, ctScript);
		}
	}

	@Override
	public CTScript getScript(String string) {
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
	public void addTool(String string, CTTool tool) {
		tools.put(string, tool);
	}

	@Override
	public CTTool getTool(String string) {
		return tools.get(string);
	}

	@Override
	public Set<String> getTools() {
		return tools.keySet();
	}

	@Override
	public void renameTool(String oldname, String newname) {
		synchronized (tools) {
			CTTool t = tools.remove(oldname);
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
	public void setVectorParameter(String string, Vector3f v) {
		vparameters.put(string, new Vector3f(v));
	}

	@Override
	public Vector3f getVectorParameter(String string) {
		return vparameters.get(string);
	}

	@Override
	public void save() {
		for (CTScript s : getScriptsSet().values()) {
			s.save();
		}

		for (CTTool tool : this.tools.values()) {
			tool.save();
		}

		o.save();
	}

	@Override
	public void publish() {
		for (CTScript s : getScriptsSet().values()) {
			s.publish();
		}

		for (CTTool tool : this.tools.values()) {
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

	public LLog log() {
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
