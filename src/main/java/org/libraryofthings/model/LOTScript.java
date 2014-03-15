package org.libraryofthings.model;

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
		return load(sscript);
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
	private boolean load(final String s) {
		try {
			ScriptLoader loader = new JavaScriptLoader();
			inv = loader.load(s);
			// invoke the global function named "hello"
			log.info("load a script " + inv.invokeFunction("info"));
			this.script = s;
			return true;
		} catch (NoSuchMethodException e) {
			log.error(this, "parseBean", e);
			script = null;
			return false;
		} catch (ScriptException e) {
			log.error(this, "parseBean", e);
			script = null;
			return false;
		}
	}

	public boolean setScript(final String nscript) {
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
	 * @param env
	 * 
	 * @param RuntimeEnvironment
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public void run(final RunEnvironment runenv) {
		try {
			inv.invokeFunction("run", runenv);
		} catch (NoSuchMethodException | ScriptException e) {
			handleException(e);
		}
	}

	public boolean run(final RunEnvironment e, Object... params) {
		try {
			inv.invokeFunction("run", e, params);
			return true;
		} catch (NoSuchMethodException | ScriptException e1) {
			handleException(e1);
			return false;
		}
	}

	private void handleException(Exception e1) {
		log.info("Error in script " + script);
		log.error(this, "run", e1);
	}
}
