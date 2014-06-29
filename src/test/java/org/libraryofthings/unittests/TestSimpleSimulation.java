package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.libraryofthings.simulation.LOTSimulationEnvironment;
import org.xml.sax.SAXException;

public class TestSimpleSimulation extends LOTTestCase {

	public void testFailingScript() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();

		LOTScript s = new LOTScript(env);
		s.setScript("function test() {}");

		RunEnvironment runenv = new LOTSimulationEnvironment(env);
		runenv.addTask(s, "test_fail");
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertFalse(simulation.run());
	}

	public void testSimpleScript() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();

		String testvalue = "testvalue" + System.currentTimeMillis();
		//
		LOTScript s = new LOTScript(env);
		s.setScript("function info() {} function run(env, params) { env.setParameter('testparam', '"
				+ testvalue + "'); }");

		RunEnvironment runenv = new LOTSimulationEnvironment(env);
		runenv.addTask(s, "test");
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertTrue(simulation.run());
		assertEquals(testvalue, runenv.getParameter("testparam"));
	}
}
