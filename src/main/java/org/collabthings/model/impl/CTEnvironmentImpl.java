/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.core.ServiceObject;
import org.collabthings.core.ServiceObjectData;
import org.collabthings.datamodel.WObject;
import org.collabthings.datamodel.WObjectID;
import org.collabthings.datamodel.WStringID;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTTool;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

public class CTEnvironmentImpl implements CTEnvironment, ServiceObjectData {
	private static final String BEANNAME = "env";
	private static final String VALUENAME_APPLICATIONS = "applications";
	private static final String VALUENAME_TOOLS = "tools";
	private static final String VALUENAME_PARAMS = "params";
	private static final String VALUENAME_VPARAMS = "vparams";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_VALUE = "value";
	//
	private CTClient client;
	private ServiceObject o;
	//
	private Map<String, CTApplication> applications;
	private Map<String, CTTool> tools = new HashMap<>();
	private Map<String, String> parameters = new HashMap<>();
	private Map<String, Vector3f> vparameters = new HashMap<>();
	private LLog log;

	private String name = "env";
	private WObject bean;

	public CTEnvironmentImpl(CTClient nclient) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
	}

	public CTEnvironmentImpl(CTClient nclient, WStringID idValue) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
		o.load(idValue);
	}

	@Override
	public boolean load(WStringID id) {
		return o.load(id);
	}

	@Override
	public String toString() {
		return "Environment";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public WObject getObject() {
		WObject b = o.getBean();

		b.addValue(VALUENAME_NAME, getName());

		getApplicationsBean(b);
		getToolsBean(b);
		getParametersBean(b);
		getVectorParametersBean(b);

		return b;
	}

	private void getToolsBean(WObject b) {
		Set<String> toolnames = tools.keySet();
		for (String string : toolnames) {
			CTTool s = getTool(string);
			WObject toolo = new WObject();
			toolo.addValue(VALUENAME_NAME, string);
			toolo.addValue("id", s.getID());
			b.addToList(VALUENAME_TOOLS, toolo);
		}
	}

	private void getApplicationsBean(WObject b) {
		synchronized (getApplicationsSet()) {
			Set<String> applicationnames = getApplicationsSet().keySet();
			for (String string : applicationnames) {
				CTApplication s = getApplication(string);
				if (s != null) {
					WObject so = new WObject();
					so.addValue(VALUENAME_NAME, string);
					so.addValue("id", s.getID().toString());
					b.addToList(VALUENAME_APPLICATIONS, so);
				} else {
					log().warning("application \"" + string + "\" null");
				}
			}
		}
	}

	private void getVectorParametersBean(WObject b) {
		Set<String> names = vparameters.keySet();
		for (String string : names) {
			Vector3f v = getVectorParameter(string);
			WObject sbean = new WObject();
			sbean.addValue(VALUENAME_NAME, string);
			sbean.add(VALUENAME_VALUE, CTMath.getBean(v));
			b.addToList(VALUENAME_VPARAMS, sbean);
		}
	}

	private void getParametersBean(WObject b) {
		Set<String> names = parameters.keySet();
		for (String string : names) {
			String s = getParameter(string);
			WObject sbean = new WObject();
			sbean.addValue(VALUENAME_NAME, string);
			sbean.addValue(VALUENAME_VALUE, s);
			b.addToList(VALUENAME_PARAMS, sbean);
		}
	}

	@Override
	public boolean parse(WObject bean) {
		this.bean = bean;

		parseTools(bean);
		parseParameters(bean);
		parseVParameters(bean);
		parseApplications(bean);

		name = bean.getValue(VALUENAME_NAME);

		return true;
	}

	private Map<String, CTApplication> getApplicationsSet() {
		if (applications == null) {
			parseApplications(bean);
		}
		return applications;
	}

	private void parseApplications(WObject bean2) {
		applications = new HashMap<>();
		if (bean != null) {
			List<WObject> sbeans = bean.getObjectList(VALUENAME_APPLICATIONS);
			for (WObject sbean : sbeans) {
				String appname = sbean.getValue(VALUENAME_NAME);
				WStringID id = sbean.getIDValue("id");
				CTApplicationImpl app = new CTApplicationImpl(client);
				app.load(id);
				getApplicationsSet().put(appname, app);
			}
		}
	}

	private void parseTools(WObject bean) {
		List<WObject> tbeans = bean.getObjectList(VALUENAME_TOOLS);
		for (WObject b : tbeans) {
			String toolname = b.getValue(VALUENAME_NAME);
			WStringID id = b.getIDValue("id");
			CTToolImpl tool = new CTToolImpl(client, id);
			tools.put(toolname, tool);
		}
	}

	private void parseParameters(WObject bean) {
		List<WObject> pbeans = bean.getObjectList(VALUENAME_PARAMS);
		for (WObject b : pbeans) {
			String param = b.getValue(VALUENAME_NAME);
			String value = b.getValue(VALUENAME_VALUE);
			parameters.put(param, value);
		}
	}

	private void parseVParameters(WObject bean) {
		List<WObject> pbeans = bean.getObjectList(VALUENAME_VPARAMS);
		for (WObject b : pbeans) {
			String vname = b.getValue(VALUENAME_NAME);
			WObject value = b.get(VALUENAME_VALUE);
			Vector3f v = CTMath.parseVector(value);
			vparameters.put(vname, v);
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void renameApplication(String oldname, String newname) {
		synchronized (getApplicationsSet()) {
			CTApplication s = getApplicationsSet().remove(oldname);
			getApplicationsSet().put(newname, s);
		}
	}

	@Override
	public void deleteApplication(String string) {
		synchronized (getApplicationsSet()) {
			getApplicationsSet().remove(string);
		}
	}

	@Override
	public void addApplication(String applicationname, CTApplication ctApplication) {
		synchronized (getApplicationsSet()) {
			getApplicationsSet().put(applicationname, ctApplication);
		}
	}

	@Override
	public CTApplication getApplication(String string) {
		synchronized (getApplicationsSet()) {
			return getApplicationsSet().get(string);
		}
	}

	@Override
	public Set<String> getApplications() {
		synchronized (getApplicationsSet()) {
			return this.getApplicationsSet().keySet();
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
	public void setParameter(String string, WObjectID id) {
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
		for (CTApplication s : getApplicationsSet().values()) {
			s.save();
		}

		for (CTTool tool : this.tools.values()) {
			tool.save();
		}

		o.save();
	}

	@Override
	public void publish() {
		for (CTApplication s : getApplicationsSet().values()) {
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
	public WObjectID getID() {
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

		po.append(0, getObject().toYaml());

		return po;
	}
}
