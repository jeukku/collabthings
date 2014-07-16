package org.libraryofthings.model;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.scripting.JavaScriptLoader;
import org.libraryofthings.scripting.ScriptLoader;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class LOTScript implements ServiceObjectData {
	private static final String SCRIPT = "value";
	private static final String BEANNAME = "script";
	public static final String PREFERENCES_SCRIPTSPATH = "lot.script.path";
	//
	private ServiceObject o;
	private String script;
	private Invocable inv;

	//
	private LLog log = LLog.getLogger(this);
	private final LOTClient env;
	//
	private static int namecounter = 0;
	private String name;
	private String info;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 */
	public LOTScript(final LOTClient env) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this,
				env.getVersion(), env.getPrefix());
		setName("script" + (LOTScript.namecounter++));
		setScript("function run(env) { env.log().info('Running ' + this); } function info() { return 'default script'; } ");
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		b.addValue("name", name);
		return b;
	}

	@Override
	public boolean parseBean(final JBean bean) {
		String sscript = bean.getBase64Value(SCRIPT);
		this.name = bean.getValue("name");
		return load(sscript);
	}

	public boolean load(MStringID id) {
		return o.load(id);
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
			ScriptLoader loader = new JavaScriptLoader(this.env
					.getPreferences().get(LOTScript.PREFERENCES_SCRIPTSPATH,
							"./"));
			inv = loader.load(s);
			if (inv != null) {
				// invoke the global function named "hello"
				info = "" + inv.invokeFunction("info");
				log.info("load a script " + info);
				this.script = s;
				return true;
			} else {
				script = null;
				return false;
			}
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
	public String getInfo() throws LOTScriptException {
		try {
			return "" + inv.invokeFunction("info");
		} catch (NoSuchMethodException | ScriptException e) {
			log.error(this, this.name, e);
			throw new LOTScriptException(e);
		}
	}

	/**
	 * Invokes run -function in the script.
	 * 
	 */
	public boolean run(Object... params) {
		try {
			inv.invokeFunction("run", params);
			return true;
		} catch (NoSuchMethodException | ScriptException e1) {
			handleException(e1);
			return false;
		}
	}

	private void handleException(Exception e1) {
		log.info("Error in script " + script);
		log.info("Error in script " + script + " exception " + e1);
		log.error(this, "run", e1);
	}

	@Override
	public String toString() {
		return "LOTScript[" + this.name + "][" + info + "]";
	}

	public void setName(String name) {
		this.name = name;
	}
}
