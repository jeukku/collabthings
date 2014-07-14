package org.libraryofthings.unittests;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTScriptException;

public final class TestScript extends LOTTestCase {

	private static final String THIS_SHOULD_WORK = "this should work.";
	private static final String SCRIPT_TEMPLATE = "function info() { return \""
			+ THIS_SHOULD_WORK + "\"; } \n";
	private static final String SCRIPT_ENV_TEST_VALUE = "testvalue "
			+ Math.random();
	private static final String FAILING_SCRIPT = "FAIL";

	public void testSaveAndLoad() throws LOTScriptException {
		LOTClient env = getNewEnv();
		assertNotNull(env);
		//
		LOTScript s = getAndTestWorkingScriptExample(env);
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

	private LOTScript getAndTestWorkingScriptExample(LOTClient env) {
		LOTScript s = getWorkingScriptExample(env);
		assertNotNull(s);
		s.getServiceObject().save();
		return s;
	}

	private LOTScript getWorkingScriptExample(LOTClient env) {
		return getScript(env, SCRIPT_TEMPLATE
				+ "function run(env) { env.setParameter(\"testvalue\", \""
				+ SCRIPT_ENV_TEST_VALUE + "\" ); } ");
	}

	private LOTScript getScript(LOTClient env, String script) {
		LOTScript s = new LOTScript(env);
		if (s.setScript(script)) {
			return s;
		} else {
			return null;
		}
	}

	public void testRuntimeEnvironmentParameters() throws LOTScriptException {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, SCRIPT_TEMPLATE
				+ "function run(e) { e.setParameter('test', 'testvalue'); }");
		assertNotNull(s);
		RunEnvironment e = new LOTRunEnvironmentImpl(env);
		s.run(e);
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testFailLoad() {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, FAILING_SCRIPT);
		assertNull(s);
	}

	public void testMissingMethod() {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, "function fail() {}");
		assertNull(s);
	}

	public void testMissingRunMethod() {
		LOTClient env = getNewEnv();
		LOTScript s = getScript(env, "function info() {}");
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(env);
		assertFalse(s.run(runenv));
	}

	public void testFailAtLoadingLibraries() throws LOTScriptException {
		LOTClient env = getNewEnv();
		env.getPreferences().set(LOTScript.PREFERENCES_SCRIPTSPATH, "FAILPATH");
		LOTScript s = getWorkingScriptExample(env);
		assertNull(s);
	}
}
