package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.ReallySimpleSuperheroRobot;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.model.impl.LOTFactoryImpl;
import org.libraryofthings.model.impl.LOTScriptImpl;
import org.libraryofthings.model.impl.LOTToolImpl;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.xml.sax.SAXException;

public class TestSimpleSimulation extends LOTTestCase {

	private static final int MAX_SIMUALTION_RUNTIME = 2000000;

	public void testFailingScript() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTScriptImpl s = new LOTScriptImpl(client);
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
		LOTScriptImpl s = new LOTScriptImpl(client);
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
		LOTFactoryImpl factory = new LOTFactoryImpl(client);

		LOTFactoryState factorystate = new LOTFactoryState(client, env,
				"testfactory", factory);
		RunEnvironment rune = factorystate.getRunEnvironment();

		ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(rune);
		factorystate.addToolUser(robot);
		LOTToolState toolstate = factorystate.addTool("tool", new LOTToolImpl(
				client));
		//
		LOTScriptImpl script = new LOTScriptImpl(client);
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

		LOTFactoryImpl f = new LOTFactoryImpl(client);
		LOTScriptImpl startscript = new LOTScriptImpl(client);
		f.addScript("start", startscript);

		LOTScriptImpl taskscript = new LOTScriptImpl(client);
		String nscript = "function info(){} function run(e, factory, values) { "
				+ "e.log().info('calling tooltest'); factory.getTool('tool').call('tooltest', values); } ";
		assertTrue(taskscript.setScript(nscript));
		f.addScript("factorytest", taskscript);

		LOTFactoryState factorystate = new LOTFactoryState(client, env,
				"testfactory", f);
		RunEnvironment rune = factorystate.getRunEnvironment();
		//
		LOTToolImpl tool = new LOTToolImpl(client);
		LOTScriptImpl testscript = new LOTScriptImpl(client);
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
