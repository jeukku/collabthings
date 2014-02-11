package org.libraryofthings.model;

import java.util.LinkedList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.scripting.JavaScriptLoader;
import org.libraryofthings.scripting.ScriptLoader;

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
	private String string;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 */
	public LOTScript(final LOTEnvironment env) {
		o = new ServiceObject(BEANNAME, env.getClient(), this,
				env.getVersion(), env.getPrefix());
	}

	/**
	 * Loads a script with id.
	 * 
	 * @param env
	 * @param id
	 */
	public LOTScript(final LOTEnvironment env, final MStringID id) {
		o = new ServiceObject(BEANNAME, env.getClient(), this,
				env.getVersion(), env.getPrefix());
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		return b;
	}

	@Override
	public boolean parseBean(final JBean bean) {
		String sscript = bean.getBase64Value(SCRIPT);
		try {
			return load(sscript);
		} catch (NoSuchMethodException e) {
			log.error(this, "parseBean", e);
			return false;
		} catch (ScriptException e) {
			log.error(this, "parseBean", e);
			return false;
		}
	}

	/**
	 * Tries to parse the script using a ScriptLoader and calls info -function
	 * in script.
	 * 
	 * @param Script
	 *            as a String
	 * @return True, if success.
	 * @throws ScriptException
	 * @throws NoSuchMethodException
	 */
	private boolean load(final String s) throws ScriptException,
			NoSuchMethodException {
		ScriptLoader loader = new JavaScriptLoader();
		inv = loader.load(s);
		// invoke the global function named "hello"
		log.info("load a script " + inv.invokeFunction("info"));
		this.script = s;
		return true;
	}

	public boolean setScript(final String nscript)
			throws NoSuchMethodException, ScriptException {
		return load(nscript);
	}

	public ServiceObject getServiceObject() {
		return o;
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
	public String getInfo() throws NoSuchMethodException, ScriptException {
		return "" + inv.invokeFunction("info");
	}

	/**
	 * Invokes run -function in the script.
	 * 
	 * @param RuntimeEnvironment
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public void run(final RunEnvironment runenv) throws NoSuchMethodException,
			ScriptException {
		inv.invokeFunction("run", runenv);
	}

	public void run(RunEnvironment e, Object... params)
			throws NoSuchMethodException, ScriptException {
		inv.invokeFunction("run", e, params);
	}
}
