package org.libraryofthings;

import java.io.IOException;

import javax.script.ScriptException;

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
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOTScript s = getWorkingScriptExample(env);
		s.getServiceObject().publish();
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOTScript bs = new LOTScript(benv, s.getServiceObject().getID()
				.getStringID());

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		LOTSimulationEnvironment runenv = new LOTSimulationEnvironment(benv);
		bs.run(runenv);
		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
		//
		//
		assertEquals(THIS_SHOULD_WORK, bs.getInfo());
	}

	private LOTScript getWorkingScriptExample(LOTEnvironment env)
			throws NoSuchMethodException, ScriptException {
		LOTScript s = getWorkingScript(env, SCRIPT_TEMPLATE
				+ "function run(env) { env.setParameter(\"testvalue\", \""
				+ SCRIPT_ENV_TEST_VALUE + "\" ); } ");
		s.getServiceObject().save();
		return s;
	}

	private LOTScript getFailingScript(LOTEnvironment env, String script) {
		LOTScript s = new LOTScript(env);
		s.setScript(script);
		return s;
	}

	private LOTScript getWorkingScript(LOTEnvironment env, String script) {
		LOTScript s = new LOTScript(env);
		s.setScript(script);
		return s;
	}

	public void testRuntimeEnvironmentParameters() throws IOException,
			SAXException, NoSuchMethodException, ScriptException {
		LOTEnvironment env = getNewEnv();
		LOTScript s = getWorkingScript(env, SCRIPT_TEMPLATE
				+ "function run(e) { e.setParameter('test', 'testvalue'); }");
		assertNotNull(s);
		RunEnvironment e = new LOTSimulationEnvironment(env);
		s.run(e);
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testFailLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		LOTScript s = getFailingScript(env, FAILING_SCRIPT);
		assertNull(s.getScript());
	}

	public void testMissingMethod() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		LOTScript s = getFailingScript(env, "function fail() {}");
		assertNull(s.getScript());
	}
}
