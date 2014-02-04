package org.libraryofthings;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import javax.script.ScriptException;

import org.libraryofthings.model.LOTScript;
import org.xml.sax.SAXException;

public final class TestScript extends LOTTestCase {

	private static final String THIS_SHOULD_WORK = "this should work.";
	private static final String SCRIPT_ENV_TEST_VALUE = "testvalue " + Math.random();

	public void testSaveAndLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOTScript s = getWorkingScript(env);
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOTScript bs = new LOTScript(benv, s.getServiceObject().getID()
				.getStringID());

		assertEquals(s.getScript(), bs.getScript());
		assertTrue(s.isOK());
		//
		LOTSimulationEnvironment runenv = new LOTSimulationEnvironment();
		bs.run(runenv);
		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
		//
		//
		assertEquals(THIS_SHOULD_WORK, bs.getInfo());
	}

	private LOTScript getWorkingScript(LOTEnvironment env) {
		LOTScript s = new LOTScript(env);
		s.setScript("function info() { return \"" + THIS_SHOULD_WORK + "\"; } function run(env) { env.setParameter(\"testvalue\", \"" + SCRIPT_ENV_TEST_VALUE + "\" ); } ");
		assertTrue(s.getServiceObject().save());
		return s;
	}

	private LOTScript getFailingScript(LOTEnvironment env) {
		LOTScript s = new LOTScript(env);
		assertFalse(s.setScript("failing script"));
		return s;
	}

	public void testFailLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTEnvironment env = getNewEnv();
		LOTScript s = getFailingScript(env);
		assertFalse(s.isOK());
	}
}
