package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.integrationtests.ReallySimpleSuperheroRobot;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTEnvironmentImpl;
import org.libraryofthings.model.LOTFactory;
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

		runenv.addTask(s);
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
		runenv.addTask(s);
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));
		assertEquals(testvalue, runenv.getParameter("testparam"));
	}

	public void testSimpleRobotSimulation() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTFactory factory = new LOTFactory(client);

		LOTFactoryState factorystate = factory.initRuntimeEnvironment(client,
				env);
		RunEnvironment rune = factorystate.getRunEnvironment();

		ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(rune);
		factorystate.addToolUser(robot);
		LOTToolState toolstate = factorystate.addTool("tool", new LOTTool(
				client));
		//
		LOTScript script = new LOTScript(client);
		String nscript = "function info(){} function run(e, factory) { factory.getTool('tool').moveTo(e.getVector(10,0,0), e.getVector(0,1,0)); } ";
		assertTrue(script.setScript(nscript));
		rune.addTask(script, factorystate, null);
		LOTSimulation s = new LOTSimpleSimulation(rune);

		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));
		//
		LVector l = toolstate.getLocation();
		assertReallyClose(new LVector(10, 0, 0), l);
	}

	public void testCallTool() {
		LOTClient client = getNewClient();
		LOTEnvironment env = new LOTEnvironmentImpl(client);

		LOTFactory f = new LOTFactory(client);
		LOTScript startscript = new LOTScript(client);
		f.addScript("start", startscript);

		LOTScript taskscript = new LOTScript(client);
		String nscript = "function info(){} function run(e, factory, values) { "
				+ "e.log().info('calling tooltest'); factory.getTool('tool').call('tooltest', values); } ";
		assertTrue(taskscript.setScript(nscript));
		f.addScript("factorytest", taskscript);

		LOTFactoryState factorystate = f.initRuntimeEnvironment(client, env);
		RunEnvironment rune = factorystate.getRunEnvironment();
		//
		LOTTool tool = new LOTTool(client);
		LOTScript testscript = new LOTScript(client);
		tool.addScript("tooltest", testscript);
		String testscriptvalue = "testvalue" + Math.random();
		testscript
				.setScript("function info() {} function run(e, runo, values) { e.setParameter('testfromtool', '"
						+ testscriptvalue + "'); }");
		//
		LOTToolState toolstate = factorystate.addTool("tool", tool);
		//

		factorystate.addTask("factorytest", null);
		//
		LOTSimulation s = new LOTSimpleSimulation(rune);
		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));
		//
		assertNotNull(rune.getParameter("testfromtool"));
		assertEquals(testscriptvalue, rune.getParameter("testfromtool"));
	}
}
