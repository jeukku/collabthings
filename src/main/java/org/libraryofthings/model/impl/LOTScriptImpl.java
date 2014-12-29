package org.libraryofthings.model.impl;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.scripting.LOTJavaScriptLoader;
import org.libraryofthings.scripting.ScriptLoader;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.WData;
import waazdoh.util.MStringID;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class LOTScriptImpl implements ServiceObjectData, LOTScript {
	private static final String SCRIPT = "value";
	private static final String BEANNAME = "script";
	//
	private ServiceObject o;
	private String script;

	//
	private LLog log = LLog.getLogger(this);
	private final LOTClient client;
	//
	private static int namecounter = 0;
	private String name;
	private String info;

	private Invocable inv;
	private String error;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 */
	public LOTScriptImpl(final LOTClient env) {
		this.client = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this,
				env.getVersion(), env.getPrefix());
		setName("script" + (LOTScriptImpl.namecounter++));
		setScript("function run(env, values) { env.log().info('Running ' + this); } function info() { return 'default script'; } ");
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		b.addValue("name", name);
		return b;
	}

	@Override
	public boolean parseBean(final WData bean) {
		String sscript = bean.getBase64Value(SCRIPT);
		this.name = bean.getValue("name");
		return setScript(sscript);
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public boolean setScript(final String nscript) {
		this.script = nscript;
		inv = null;
		return getInvocable() != null;
	}

	public Invocable getInvocable() {
		if (inv == null) {
			try {
				ScriptLoader loader = LOTJavaScriptLoader.get(this.client);
				inv = loader.load(script);
				if (inv != null) {
					// invoke the global function named "hello"
					info = "" + inv.invokeFunction("info");
					log.info("load a script " + info);

					// TODO
					// to test for syntax errors.
					try {
						inv.invokeFunction("run", null, null, null);
					} catch (ScriptException e) {
						if (e.getMessage().indexOf(
								"TypeError: null has no such function") == 0) {
							// that's just fine
						} else {
							log.info("got scriptexception " + e.getMessage());
							throw (e);
						}
					}

					error = null;
				} else {
					script = null;
				}
			} catch (ScriptException | NoSuchMethodException e) {
				log.error(this, "getInvocable", e);
				error = "" + e.getMessage();
				script = null;
				return null;
			}
		}
		return inv;
	}

	@Override
	public String getError() {
		return error;
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	public waazdoh.client.model.ObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void publish() {
		getServiceObject().publish();
	}

	@Override
	public void save() {
		getServiceObject().save();
	}

	public String getScript() {
		return script;
	}

	/**
	 * 
	 * @return true, if script is usable.
	 */
	public boolean isOK() {
		return inv != null;
	}

	/**
	 * 
	 * @return Return value of info -function in the script.
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return "LOTScript[" + this.name + "][" + info + "]";
	}

	public void setName(String name) {
		this.name = name;
	}
}
