package org.libraryofthings.unittests;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.impl.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.impl.LOTScriptRunnerImpl;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.model.impl.LOTScriptImpl;
import org.libraryofthings.scripting.LOTJavaScriptLoader;

public final class TestScript extends LOTTestCase {

	private static final String THIS_SHOULD_WORK = "this should work.";
	private static final String SCRIPT_TEMPLATE = "function info() { return \""
			+ THIS_SHOULD_WORK + "\"; } \n";
	private static final String SCRIPT_ENV_TEST_VALUE = "testvalue "
			+ Math.random();
	private static final String FAILING_SCRIPT = "FAIL";

	public void testSaveAndLoad() {
		LOTClient client = getNewClient();
		assertNotNull(client);
		//
		LOTScriptImpl s = getAndTestWorkingScriptExample(client);
		s.publish();
		//
		LOTClient benv = getNewClient();
		assertNotNull(benv);
		LOTScriptImpl bs = new LOTScriptImpl(benv);
		assertTrue(bs.load(s.getID().getStringID()));

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client, env);
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(s, runenv, null);
		runner.run();

		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
		//
		//
		assertEquals(THIS_SHOULD_WORK, bs.getInfo());
	}

	private LOTScriptImpl getAndTestWorkingScriptExample(LOTClient env) {
		LOTScriptImpl s = getWorkingScriptExample(env);
		assertNotNull(s);
		s.save();
		return s;
	}

	private LOTScriptImpl getWorkingScriptExample(LOTClient env) {
		return getScript(env, SCRIPT_TEMPLATE
				+ "function run(env) { env.setParameter(\"testvalue\", \""
				+ SCRIPT_ENV_TEST_VALUE + "\" ); } ");
	}

	private LOTScriptImpl getScript(LOTClient env, String script) {
		LOTScriptImpl s = new LOTScriptImpl(env);
		if (s.setScript(script)) {
			return s;
		} else {
			return null;
		}
	}

	public void testRuntimeEnvironmentParameters() {
		LOTClient client = getNewClient();
		LOTScriptImpl s = getScript(client, SCRIPT_TEMPLATE
				+ "function run(e) { e.setParameter('test', 'testvalue'); }");
		assertNotNull(s);
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironment e = new LOTRunEnvironmentImpl(client, env);
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(s, e, null);
		runner.run();
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testRestrictedWords() {
		LOTClient c = getNewClient();

		try {
			LOTScriptImpl s = getScript(
					c,
					SCRIPT_TEMPLATE
							+ "function run(e) { r = new java.io.FileWriter('/jstest.txt'); r.flush(); }");
			assertNull(s);
		} catch (SecurityException ex) {
			assertNotNull(ex);
		}
	}

	public void testFailLoad() {
		LOTClient env = getNewClient();
		LOTScriptImpl s = getScript(env, FAILING_SCRIPT);
		assertNull(s);
	}

	public void testMissingMethod() {
		LOTClient env = getNewClient();
		LOTScriptImpl s = getScript(env, "function fail() {}");
		assertNull(s);
	}

	public void testMissingRunMethod() {
		LOTClient client = getNewClient();
		LOTScriptImpl s = getScript(client, "function info() {}");
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client,
				new LOTEnvironmentImpl(client));
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(s, runenv, null);
		runner.run();

		assertFalse(runner.run());
	}

	public void testFailAtLoadingLibraries() {
		LOTClient env = getNewClient();
		LOTJavaScriptLoader.reset();
		env.getPreferences().set(LOTScriptImpl.PREFERENCES_SCRIPTSPATH,
				"FAILPATH");
		LOTScriptImpl s = getWorkingScriptExample(env);
		assertNull(s);
		LOTJavaScriptLoader.reset();
	}
}
