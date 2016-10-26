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

package org.collabthings.model.run.impl;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.collabthings.CTClient;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTScript;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.util.CResourcesReader;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;
import org.collabthings.util.ShortHashID;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public class CTRunEnvironmentBuilderImpl implements CTRunEnvironmentBuilder, ServiceObjectData {
	private static final String BEANNAME = "runenvbuilder";
	//
	private CTClient client;
	private ServiceObject o;
	//
	private CTEnvironment env;
	private LLog log;

	private String name = "envbuilder";

	private Map<String, String> storageread = new HashMap<>();

	public CTRunEnvironmentBuilderImpl(CTClient nclient) {
		this.client = nclient;
		env = new CTEnvironmentImpl(nclient);
		CTScript initscript = nclient.getObjectFactory().getScript();
		initscript.setScript("function run() {}\nfunction info() {}\n");

		CResourcesReader r = new CResourcesReader("templates/runenvbuilder.js");
		if (r.isSuccess()) {
			initscript.setScript(r.getText());
		}

		env.addScript("init", initscript);

		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
	}

	public CTRunEnvironmentBuilderImpl(CTClient nclient, MStringID idValue) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
		o.load(idValue);
	}

	@Override
	public boolean load(MStringID id) {
		return o.load(id);
	}

	public CTFactoryState createFactoryState(String name, String id) {
		CTFactory f = client.getObjectFactory().getFactory(new MStringID(id));
		CTFactoryState state = new CTFactoryState(client, env, name, f);
		return state;
	}

	@Override
	public CTRunEnvironment getRunEnvironment() {
		CTScript s = getEnvironment().getScript("init");
		try {
			return (CTRunEnvironment) s.getInvocable().invokeFunction("run", this);
		} catch (NoSuchMethodException | ScriptException e) {
			getLogger().error(this, "getRunEnvironment", e);
			return null;
		}
	}

	@Override
	public CTEnvironment getEnvironment() {
		return env;
	}

	@Override
	public ObjectID getID() {
		return this.o.getID();
	}

	@Override
	public String toString() {
		return "REB[" + new ShortHashID(getID().getStringID()) + "]";
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

		b.addValue("name", getName());
		b.addValue("envid", env.getID());

		return b;
	}

	@Override
	public boolean parse(WObject bean) {
		name = bean.getValue("name");
		env = new CTEnvironmentImpl(client, bean.getIDValue("envid"));
		return true;
	}

	@Override
	public boolean isReady() {
		return env.isReady();
	}

	@Override
	public void save() {
		env.save();
		o.save();
	}

	@Override
	public void publish() {
		save();

		env.publish();
		o.publish();

		client.publish(getName(), this);
	}

	public ServiceObject getServiceObject() {
		return o;
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
		po.append("RunEnvBuilder");
		po.append("Bean");
		po.append(1, getObject().toYaml());

		po.append("Env");
		po.append(1, env.printOut());

		po.append("RunEnv");

		getLogger().info(po.toText());

		return po;
	}

	@Override
	public String readStorage(String path) {
		String username = path.substring(0, path.indexOf('/'));
		if ("self".equals(username)) {
			username = client.getService().getUser().getUsername();
		}

		String npath = path.substring(path.indexOf('/') + 1);
		String value = client.getStorage().readStorage(this.client.getService().getUser(username), npath);

		addStorageRead(path, value);

		return value;
	}

	private void addStorageRead(String path, String value) {
		storageread.put(path, value);
	}
}
