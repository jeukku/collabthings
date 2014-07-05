package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.integrationtests.ReallySimpleSuperheroRobot;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.libraryofthings.simulation.LOTSimulationEnvironment;
import org.xml.sax.SAXException;

public class TestSimpleSimulation extends LOTTestCase {

	private static final int MAX_SIMUALTION_RUNTIME = 20000;

	public void testFailingScript() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();

		LOTScript s = new LOTScript(env);
		s.setScript("function test() {}");

		RunEnvironment runenv = new LOTSimulationEnvironment(env);
		runenv.addTask(s, "test_fail");
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertFalse(simulation.run(MAX_SIMUALTION_RUNTIME));
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
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));
		assertEquals(testvalue, runenv.getParameter("testparam"));
	}

	public void testSimpleRobotSimulation() throws IOException, SAXException {
		LOTEnvironment e = getNewEnv();
		RunEnvironment rune = new LOTSimulationEnvironment(e);

		ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(e,
				rune);
		rune.addToolUser(robot);
		LOTToolState toolstate = rune.addTool("tool", new LOTTool(e));
		//
		LOTScript script = new LOTScript(e);
		String nscript = "function info(){} function run(e) { e.getTool('tool').moveTo(e.getVector(10,0,0), e.getVector(0,1,0)); } ";
		assertTrue(script.setScript(nscript));
		rune.addTask(script, (Object[]) null);
		LOTSimulation s = new LOTSimpleSimulation(rune);

		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));
		//
		LVector l = toolstate.getLocation();
		assertReayllyClose(new LVector(10, 0, 0), l);
	}
}
