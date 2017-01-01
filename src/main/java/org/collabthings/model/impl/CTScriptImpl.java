package org.collabthings.model.impl;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.collabthings.CTClient;
import org.collabthings.model.CTScript;
import org.collabthings.scripting.CTJavaScriptLoader;
import org.collabthings.scripting.ScriptLoader;
import org.collabthings.util.LLog;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.WStringID;
import waazdoh.common.WObjectID;
import waazdoh.common.WObject;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class CTScriptImpl implements ServiceObjectData, CTScript {
	private static final String VALUENAME_SCRIPT = "value";
	private static final String BEANNAME = "script";
	//
	private ServiceObject o;
	private String script;

	//
	private LLog log = LLog.getLogger(this);
	private final CTClient client;
	//
	private static int namecounter;
	private String name;
	private String info;

	private Invocable inv;
	private String error;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 */
	public CTScriptImpl(final CTClient env) {
		this.client = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.getVersion(), env.getPrefix());
		setName("script" + CTScriptImpl.namecounter);
		CTScriptImpl.namecounter++;
		setScript(
				"function run(env, values) { env.log().info('Running ' + this); } function info() { return 'default script'; } ");
	}

	@Override
	public WObject getObject() {
		WObject b = o.getBean();
		b.setBase64Value(VALUENAME_SCRIPT, script);
		b.addValue("name", name);
		return b;
	}

	@Override
	public boolean parse(final WObject bean) {
		script = bean.getBase64Value(VALUENAME_SCRIPT);
		inv = null;
		this.name = bean.getValue("name");
		return name != null && script != null;
	}

	@Override
	public boolean load(WStringID id) {
		return o.load(id);
	}

	@Override
	public void setScript(final String nscript) {
		this.script = processScript(nscript);
		inv = null;
	}

	private String processScript(final String nscript) {
		String s = nscript;
		s = s.replace("$SELF", this.client.getService().getUser().getUsername());
		return s;
	}

	@Override
	public Invocable getInvocable() {
		if (inv == null) {
			try {
				ScriptLoader loader = CTJavaScriptLoader.get(this.client);
				inv = loader.load(script);
				if (inv != null) {
					info = "" + inv.invokeFunction("info");
					log.info("load a script " + info);
					error = null;
				} else {
					script = "Invocable null";
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

	@Override
	public WObjectID getID() {
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

	@Override
	public String getScript() {
		return script;
	}

	/**
	 * 
	 * @return true, if script is usable.
	 */

	@Override
	public boolean isOK() {
		return getInvocable() != null;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return Return value of info -function in the script.
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	@Override
	public String getInfo() {
		getInvocable();
		return info;
	}

	@Override
	public String toString() {
		return "CTScript[" + this.name + "][" + info + "]";
	}

	@Override
	public void setName(String name) {
		this.name = processScript(name);
	}
}
