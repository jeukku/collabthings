package org.libraryofthings.model.impl;

import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.PrintOut;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTRunEnvironmentBuilder;
import org.libraryofthings.model.LOTScript;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

public class LOTRunEnvironmentBuilderImpl implements LOTRunEnvironmentBuilder, ServiceObjectData {
	private static final String BEANNAME = "runenvbuilder";
	//
	private LOTClient client;
	private ServiceObject o;
	//
	private LOTEnvironment env;
	private LLog log;

	private String name = "envbuilder";
	private LOTRunEnvironment runenvironment;

	public LOTRunEnvironmentBuilderImpl(LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(),
				nclient.getPrefix());
	}

	public LOTRunEnvironmentBuilderImpl(LOTClient nclient, MStringID idValue) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(),
				nclient.getPrefix());
		o.load(idValue);
	}

	public LOTFactoryState createFactoryState(String name, String id) {
		LOTFactory f = client.getObjectFactory().getFactory(new MStringID(id));
		LOTFactoryState state = new LOTFactoryState(client, env, name, f);
		this.runenvironment = state.getRunEnvironment();
		return state;
	}

	@Override
	public LOTRunEnvironment getRunEnvironment() {
		if (runenvironment == null) {
			LOTScript s = getEnvironment().getScript("init");
			try {
				s.getInvocable().invokeFunction("run", this);
				// lets trust that script creates the runenvironment some how
			} catch (NoSuchMethodException | ScriptException e) {
				getLogger().error(this, "getRunEnvironment", e);
			}
		}
		return runenvironment;
	}

	@Override
	public LOTEnvironment getEnvironment() {
		return env;
	}

	@Override
	public ObjectID getID() {
		return this.o.getID();
	}

	@Override
	public String toString() {
		return "RunEnvironmentBuilder";
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
		b.addValue("envid", env.getID());

		return b;
	}

	@Override
	public boolean parseBean(WData bean) {
		name = bean.getValue("name");
		env = new LOTEnvironmentImpl(client, bean.getIDValue("envid"));
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
		env.publish();
		o.publish();

		client.publish("last/builder", this);
		client.publish("/builders/" + getName() + "/" + LOTClient.getDateTime(), this);
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

	public PrintOut printOut() {
		PrintOut po = new PrintOut();
		po.append("RunEnvBuilder");
		po.append("Bean");
		po.append(1, getBean().toText());

		po.append("Env");
		po.append(1, env.printOut());

		po.append("RunEnv");
		if (this.runenvironment != null) {
			po.append(1, this.runenvironment.printOut());
		}

		getLogger().info(po.toText());

		return po;
	}
}
