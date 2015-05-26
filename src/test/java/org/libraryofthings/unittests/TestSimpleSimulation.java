package org.libraryofthings.unittests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTScriptRunner;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.environment.impl.LOTPartState;
import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.environment.impl.LOTScriptRunnerImpl;
import org.collabthings.environment.impl.LOTToolState;
import org.collabthings.environment.impl.ReallySimpleSuperheroRobot;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTAttachedFactory;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTValues;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.model.impl.LOTFactoryImpl;
import org.collabthings.model.impl.LOTScriptImpl;
import org.collabthings.model.impl.LOTToolImpl;
import org.collabthings.simulation.LOTSimpleSimulation;
import org.collabthings.simulation.LOTSimulation;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

public class TestSimpleSimulation extends LOTTestCase {

	private static final int MAX_SIMUALTION_RUNTIME = 2000000;

	public void testSimpleTransformation() {
		LOTClient client = getNewClient();
		LOTEnvironment env = new LOTEnvironmentImpl(client);

		LOTFactory f1 = client.getObjectFactory().getFactory();
		f1.setBoundingBox(new LVector(-10, 0, -10), new LVector(10, 10, 10));

		LOTAttachedFactory f2 = f1.addFactory("f2");
		f2.getFactory().setBoundingBox(new LVector(-3, 0, -3),
				new LVector(3, 3, 3));
		f2.setLocation(new LVector(5, 0, 0));
		f2.setOrientation(new LVector(1, 1, 0), Math.PI / 6);

		LOTAttachedFactory f21 = f2.getFactory().addFactory("f21");
		f21.getFactory().setBoundingBox(new LVector(-1, 0, -1),
				new LVector(1, 1, 1));
		f21.setLocation(new LVector(2, 0, 0));

		LOTAttachedFactory f3 = f1.addFactory("f3");
		f3.getFactory().setBoundingBox(new LVector(-2, 0, -2),
				new LVector(2, 1.5, 2));
		f3.setLocation(new LVector(-3, 2, -3));

		LOTFactoryState f1s = new LOTFactoryState(client, env, "f1s", f1);

		LOTFactoryState f2s = f1s.getFactory("f2");
		LOTFactoryState f21s = f2s.getFactory("f21");
		LOTPartState p = f21s.newPart();
		p.getPart().newSCAD();

		// to zoom out the view
		p.setLocation(new LVector(20, 0, 0));

		LOTRunEnvironment runenv = f1s.getRunEnvironment();

		final Map<String, Object> valuesmap = new HashMap<String, Object>();

		String name = "testvaluename";
		String value = "testvalue";

		runenv.addTask(new LOTScriptRunner() {

			@Override
			public boolean run(LOTValues values) {
				valuesmap.put(name, values.get(name));

				long st = System.currentTimeMillis();
				while ((System.currentTimeMillis() - st) < 10000) {
					try {
						wait(200);
					} catch (Exception e) {
					}
				}
				return true;
			}

			@Override
			public String getError() {
				return null;
			}
		}, new LOTValues(name, value));

		assertNotNull(runenv.getInfo());

		LOTSimulation simulation = new LOTSimpleSimulation(runenv, true);
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));

		assertEquals(value, valuesmap.get("testvaluename"));
	}

	public void testFailingScript() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTScriptImpl s = new LOTScriptImpl(client);
		s.setScript("function test() {}");

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironment runenv = new LOTRunEnvironmentImpl(client, env);

		LOTScriptRunner runner = new LOTScriptRunnerImpl(s, runenv, null);
		runenv.addTask(runner);
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

		LOTRunEnvironment runenv = new LOTRunEnvironmentImpl(client, env);
		LOTScriptRunner runner = new LOTScriptRunnerImpl(s, runenv, null);
		runenv.addTask(runner);
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));
		assertEquals(testvalue, runenv.getParameter("testparam"));
	}

	public void testSimpleRobotSimulation() throws IOException, SAXException {
		LOTClient client = getNewClient();

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTFactoryImpl factory = new LOTFactoryImpl(client);
		factory.setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));
		LOTFactoryState factorystate = new LOTFactoryState(client, env,
				"testfactory", factory);
		LOTRunEnvironment rune = factorystate.getRunEnvironment();

		ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(rune,
				factorystate);
		factorystate.addToolUser(robot);
		LOTPartState p = factorystate.newPart();
		p.getPart().newSubPart();

		LOTToolState toolstate = factorystate.addTool("tool", new LOTToolImpl(
				client));
		//
		LOTScriptImpl script = new LOTScriptImpl(client);
		String nscript = "function info(){} function run(e, factory) { factory.newPart(); factory.getTool('tool').moveTo(e.getVector(10,0,0), e.getVector(0,1,0), 6); } ";
		script.setScript(nscript);
		assertTrue(script.isOK());
		LOTScriptRunner runner = new LOTScriptRunnerImpl(script, rune,
				factorystate);
		rune.addTask(runner);
		LOTSimulation s = new LOTSimpleSimulation(rune, true);

		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));

		assertFalse(factorystate.getParts().isEmpty());
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
		taskscript.setScript(nscript);
		assertTrue(taskscript.isOK());
		f.addScript("factorytest", taskscript);

		LOTFactoryState factorystate = new LOTFactoryState(client, env,
				"testfactory", f);
		LOTRunEnvironment rune = factorystate.getRunEnvironment();
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
