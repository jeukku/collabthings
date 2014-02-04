package org.libraryofthings.model;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class LOTScript implements ServiceObjectData {
	private static final String SCRIPT = "value";
	private static final String BEANNAME = "script";
	//
	private ServiceObject o;
	private String script;
	private Invocable inv;

	//
	private LLog log = LLog.getLogger(this);

	public LOTScript(LOTEnvironment env) {
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
	}

	public LOTScript(LOTEnvironment env, MStringID id) {
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		return b;
	}

	@Override
	public boolean parseBean(JBean bean) {
		String sscript = bean.getBase64Value(SCRIPT);
		return load(sscript);
	}

	private boolean load(String s) {
		ScriptEngine e = new ScriptEngineManager()
				.getEngineByName("JavaScript");
		try {
			e.eval(s);
			inv = (Invocable) e;
			// invoke the global function named "hello"
			log.info("load a script " + inv.invokeFunction("info"));
			this.script = s;
			return true;
		} catch (ScriptException e1) {
			log.error(this, "load", e1);
		} catch (NoSuchMethodException e1) {
			log.error(this, "load", e1);
		}
		//
		log.info("failed to load script");
		script = null;
		//
		return false;
	}

	public boolean setScript(String string) {
		return load(string);
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public String getScript() {
		return script;
	}

	public boolean isOK() {
		return inv != null;
	}

	/**
	 * 
	 * @return Return value of info -function in the script. 
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public String getInfo() throws NoSuchMethodException, ScriptException {
		return "" + inv.invokeFunction("info");
	}

	/**
	 * Invokes run -function in the script.
	 * @param RuntimeEnvironment
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public void run(final RunEnvironment runenv) throws NoSuchMethodException,
			ScriptException {
		inv.invokeFunction("run", runenv);
	}
}
