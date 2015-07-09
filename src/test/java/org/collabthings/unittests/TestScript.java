package org.collabthings.unittests;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.environment.impl.LOTScriptRunnerImpl;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTScript;
import org.collabthings.model.impl.LOTEnvironmentImpl;

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
		LOTScript s = getAndTestWorkingScriptExample(client);
		s.publish();
		//
		LOTClient benv = getNewClient();
		assertNotNull(benv);
		LOTScript bs = benv.getObjectFactory().getScript(
				s.getID().getStringID());
		assertNotNull(bs);
		bs = benv.getObjectFactory().getScript(s.getID().getStringID());
		assertNotNull(bs);

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client, env);
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(s, runenv, null);
		runner.run();

		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
		//
		assertEquals(THIS_SHOULD_WORK, bs.getInfo());
	}

	private LOTScript getAndTestWorkingScriptExample(LOTClient env) {
		LOTScript s = getWorkingScriptExample(env);
		assertNotNull(s);
		s.save();
		return s;
	}

	private LOTScript getWorkingScriptExample(LOTClient env) {
		return getScript(env, SCRIPT_TEMPLATE
				+ "function run(env) { env.setParameter(\"testvalue\", \""
				+ SCRIPT_ENV_TEST_VALUE + "\" ); } ");
	}

	private LOTScript getScript(LOTClient env, String script) {
		LOTScript s = env.getObjectFactory().getScript();
		s.setScript(script);
		if (s.isOK()) {
			return s;
		} else {
			return null;
		}
	}

	public void testRuntimeEnvironmentParameters() {
		LOTClient client = getNewClient();
		LOTScript s = getScript(client, SCRIPT_TEMPLATE
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
			LOTScript s = getScript(
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
		LOTScript s = getScript(env, FAILING_SCRIPT);
		assertNull(s);
	}

	public void testMissingMethod() {
		LOTClient env = getNewClient();
		LOTScript s = getScript(env, "function fail() {}");
		assertNull(s);
	}

	public void testMissingRunMethod() {
		LOTClient client = getNewClient();
		LOTScript s = getScript(client, "function info() {}");
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client,
				new LOTEnvironmentImpl(client));
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(s, runenv, null);
		runner.run();

		assertFalse(runner.run());
	}

}
