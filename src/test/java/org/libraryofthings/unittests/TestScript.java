package org.libraryofthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.model.LOTScript;
import org.xml.sax.SAXException;

public final class TestScript extends LOTTestCase {

	private static final String THIS_SHOULD_WORK = "this should work.";
	private static final String SCRIPT_TEMPLATE = "function info() { return \""
			+ THIS_SHOULD_WORK + "\"; } \n";
	private static final String SCRIPT_ENV_TEST_VALUE = "testvalue "
			+ Math.random();
	private static final String FAILING_SCRIPT = "FAIL";

	public void testSaveAndLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTClient env = getNewEnv();
		assertNotNull(env);
		//
		LOTScript s = getWorkingScriptExample(env);
		s.getServiceObject().publish();
		//
		LOTClient benv = getNewEnv();
		assertNotNull(benv);
		LOTScript bs = new LOTScript(benv);
		assertTrue(bs.load(s.getServiceObject().getID().getStringID()));

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(benv);
		bs.run(runenv);
		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
		//
		//
		assertEquals(THIS_SHOULD_WORK, bs.getInfo());
	}

	private LOTScript getWorkingScriptExample(LOTClient env)
			throws NoSuchMethodException, ScriptException {
		LOTScript s = getScript(env, SCRIPT_TEMPLATE
				+ "function run(env) { env.setParameter(\"testvalue\", \""
				+ SCRIPT_ENV_TEST_VALUE + "\" ); } ");
		s.getServiceObject().save();
		return s;
	}

	private LOTScript getScript(LOTClient env, String script) {
		LOTScript s = new LOTScript(env);
		s.setScript(script);
		return s;
	}

	public void testRuntimeEnvironmentParameters() throws IOException,
			SAXException, NoSuchMethodException, ScriptException {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, SCRIPT_TEMPLATE
				+ "function run(e) { e.setParameter('test', 'testvalue'); }");
		assertNotNull(s);
		RunEnvironment e = new LOTRunEnvironmentImpl(env);
		s.run(e);
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testFailLoad() throws IOException, SAXException {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, FAILING_SCRIPT);
		assertNull(s.getScript());
	}

	public void testMissingMethod() throws IOException, SAXException {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, "function fail() {}");
		assertNull(s.getScript());
	}

	public void testMissingRunMethod() throws IOException, SAXException {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, "function info() {}");
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(env);
		assertFalse(s.run(runenv));
	}

	public void testFailAtLoadingLibraries() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTClient env = getNewEnv();
		env.getPreferences().set(LOTScript.PREFERENCES_SCRIPTSPATH, "FAILPATH");
		LOTScript s = getWorkingScriptExample(env);
		assertFalse(s.isOK());
	}
}
