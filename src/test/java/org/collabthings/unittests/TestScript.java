package org.collabthings.unittests;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.environment.impl.CTScriptRunnerImpl;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTScript;
import org.collabthings.model.impl.CTEnvironmentImpl;

public final class TestScript extends CTTestCase {

	private static final String THIS_SHOULD_WORK = "this should work.";
	private static final String SCRIPT_TEMPLATE = "function info() { return \"" + THIS_SHOULD_WORK + "\"; } \n";
	private static final String SCRIPT_ENV_TEST_VALUE = "testvalue " + Math.random();
	private static final String FAILING_SCRIPT = "FAIL";

	public void testSaveAndLoad() {
		CTClient client = getNewClient();
		assertNotNull(client);
		//
		CTScript s = getAndTestWorkingScriptExample(client);
		s.publish();
		//
		CTClient benv = getNewClient();
		assertNotNull(benv);
		CTScript bs = benv.getObjectFactory().getScript(s.getID().getStringID());
		assertNotNull(bs);
		bs = benv.getObjectFactory().getScript(s.getID().getStringID());
		assertNotNull(bs);

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		CTEnvironment env = new CTEnvironmentImpl(client);
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(client, env);
		CTScriptRunnerImpl runner = new CTScriptRunnerImpl(s, runenv, null);
		runner.run();

		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
		//
		assertEquals(THIS_SHOULD_WORK, bs.getInfo());
	}

	private CTScript getAndTestWorkingScriptExample(CTClient env) {
		CTScript s = getWorkingScriptExample(env);
		assertNotNull(s);
		s.save();
		return s;
	}

	private CTScript getWorkingScriptExample(CTClient env) {
		return getScript(env, SCRIPT_TEMPLATE + "function run(env) { env.setParameter(\"testvalue\", \""
				+ SCRIPT_ENV_TEST_VALUE + "\" ); } ");
	}

	private CTScript getScript(CTClient env, String script) {
		CTScript s = env.getObjectFactory().getScript();
		s.setScript(script);
		if (s.isOK()) {
			return s;
		} else {
			return null;
		}
	}

	public void testRuntimeEnvironmentParameters() {
		CTClient client = getNewClient();
		CTScript s = getScript(client, SCRIPT_TEMPLATE + "function run(e) { e.setParameter('test', 'testvalue'); }");
		assertNotNull(s);
		CTEnvironment env = new CTEnvironmentImpl(client);
		CTRunEnvironment e = new CTRunEnvironmentImpl(client, env);
		CTScriptRunnerImpl runner = new CTScriptRunnerImpl(s, e, null);
		runner.run();
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testRestrictedWords() {
		CTClient c = getNewClient();

		try {
			CTScript s = getScript(c,
					SCRIPT_TEMPLATE + "function run(e) { r = new java.io.FileWriter('/jstest.txt'); r.flush(); }");
			assertNull(s);
		} catch (SecurityException ex) {
			assertNotNull(ex);
		}
	}

	public void testFailLoad() {
		CTClient env = getNewClient();
		CTScript s = getScript(env, FAILING_SCRIPT);
		assertNull(s);
	}

	public void testMissingMethod() {
		CTClient env = getNewClient();
		CTScript s = getScript(env, "function fail() {}");
		assertNull(s);
	}

	public void testMissingRunMethod() {
		CTClient client = getNewClient();
		CTScript s = getScript(client, "function info() {}");
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(client, new CTEnvironmentImpl(client));
		CTScriptRunnerImpl runner = new CTScriptRunnerImpl(s, runenv, null);
		runner.run();

		assertFalse(runner.run());
	}

}
