package org.collabthings.model.run;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTScript;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

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

	private Map<String, String> storageread = new HashMap<>();
	
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
		return state;
	}

	@Override
	public LOTRunEnvironment getRunEnvironment() {
		LOTScript s = getEnvironment().getScript("init");
		try {
			return (LOTRunEnvironment) s.getInvocable().invokeFunction("run", this);
		} catch (NoSuchMethodException | ScriptException e) {
			getLogger().error(this, "getRunEnvironment", e);
			return null;
		}
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
		save();
		
		env.publish();
		o.publish();

		client.publish("/builder/latest", this);
		client.publish("/builder/" + getName() + "/" + LOTClient.getDateTime(), this);
		client.publish("/builder/" + getName() + "/latest", this);
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

		getLogger().info(po.toText());

		return po;
	}

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
