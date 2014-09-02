package org.libraryofthings.unittests;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTEnvironmentImpl;
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
		LOTClient client = getNewClient();
		assertNotNull(client);
		//
		LOTScript s = getAndTestWorkingScriptExample(client);
		s.getServiceObject().publish();
		//
		LOTClient benv = getNewClient();
		assertNotNull(benv);
		LOTScript bs = new LOTScript(benv);
		assertTrue(bs.load(s.getServiceObject().getID().getStringID()));

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client, env);
		bs.run(runenv, null);
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
		LOTClient client = getNewClient();
		LOTScript s = getScript(client, SCRIPT_TEMPLATE
				+ "function run(e) { e.setParameter('test', 'testvalue'); }");
		assertNotNull(s);
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		RunEnvironment e = new LOTRunEnvironmentImpl(client, env);
		s.run(e, null);
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testFailLoad() {
		LOTClient env = getNewClient();
		LOTScript s = getScript(env, FAILING_SCRIPT);
		assertNull(s);
	}

	public void testMissingMethod() {
		LOTClient env = getNewClient();
		LOTScript s = getScript(env, "function fail() {}");
		assertNull(s);
	}

	public void testInvokeUnknownFuntion() {
		LOTClient client = getNewClient();
		LOTScript s = getAndTestWorkingScriptExample(client);
		boolean exceptioncaught = false;
		try {
			s.invoke("FOO");
		} catch (LOTScriptException e) {
			assertNotNull(s);
			exceptioncaught = true;
		}
		//
		assertTrue(exceptioncaught);
	}

	public void testMissingRunMethod() {
		LOTClient client = getNewClient();
		LOTScript s = getScript(client, "function info() {}");
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client,
				new LOTEnvironmentImpl(client));
		assertFalse(s.run(runenv, null));
	}

	public void testFailAtLoadingLibraries() throws LOTScriptException {
		LOTClient env = getNewClient();
		env.getPreferences().set(LOTScript.PREFERENCES_SCRIPTSPATH, "FAILPATH");
		LOTScript s = getWorkingScriptExample(env);
		assertNull(s);
	}
}
