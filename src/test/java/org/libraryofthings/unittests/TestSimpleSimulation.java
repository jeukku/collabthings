package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.integrationtests.ReallySimpleSuperheroRobot;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTEnvironmentImpl;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.xml.sax.SAXException;

public class TestSimpleSimulation extends LOTTestCase {

	private static final int MAX_SIMUALTION_RUNTIME = 2000000;

	public void testFailingScript() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTScript s = new LOTScript(client);
		s.setScript("function test() {}");

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		RunEnvironment runenv = new LOTRunEnvironmentImpl(client, env);

		runenv.addTask(s, "test_fail");
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertFalse(simulation.run(MAX_SIMUALTION_RUNTIME));
	}

	public void testSimpleScript() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		String testvalue = "testvalue" + System.currentTimeMillis();
		//
		LOTScript s = new LOTScript(client);
		s.setScript("function info() {} function run(env, params) { env.setParameter('testparam', '"
				+ testvalue + "'); }");

		RunEnvironment runenv = new LOTRunEnvironmentImpl(client, env);
		runenv.addTask(s, "test");
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));
		assertEquals(testvalue, runenv.getParameter("testparam"));
	}

	public void testSimpleRobotSimulation() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		RunEnvironment rune = new LOTRunEnvironmentImpl(client, env);

		ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(
				client, rune);
		rune.addToolUser(robot);
		LOTToolState toolstate = rune.addTool("tool", new LOTTool(client));
		//
		LOTScript script = new LOTScript(client);
		String nscript = "function info(){} function run(e) { e.getTool('tool').moveTo(e.getVector(10,0,0), e.getVector(0,1,0)); } ";
		assertTrue(script.setScript(nscript));
		rune.addTask(script, (Object[]) null);
		LOTSimulation s = new LOTSimpleSimulation(rune);

		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));
		//
		LVector l = toolstate.getLocation();
		assertReallyClose(new LVector(10, 0, 0), l);
	}

	public void testCallTool() {
		LOTClient client = getNewClient();
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		RunEnvironment rune = new LOTRunEnvironmentImpl(client, env);
		//
		LOTTool tool = new LOTTool(client);
		LOTScript testscript = new LOTScript(client);
		tool.addScript("test", testscript);
		String testscriptvalue = "testvalue" + Math.random();
		testscript
				.setScript("function info() {} function run(e) { e.getParent().setParameter('test', '"
						+ testscriptvalue + "'); }");
		//
		LOTToolState toolstate = rune.addTool("tool", tool);
		//
		LOTScript taskscript = new LOTScript(client);
		String nscript = "function info(){} function run(e) { e.getTool('tool').call('test'); } ";
		assertTrue(taskscript.setScript(nscript));
		rune.addTask(taskscript, null);
		//
		LOTSimulation s = new LOTSimpleSimulation(rune);
		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));
		//
		assertEquals(testscriptvalue, rune.getParameter("test"));
	}
}
