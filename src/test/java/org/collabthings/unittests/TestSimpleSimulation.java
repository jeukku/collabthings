package org.collabthings.unittests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.environment.CTScriptRunner;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.environment.impl.CTPartState;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.environment.impl.CTRuntimeError;
import org.collabthings.environment.impl.CTScriptRunnerImpl;
import org.collabthings.environment.impl.CTToolState;
import org.collabthings.environment.impl.ReallySimpleSuperheroRobot;
import org.collabthings.math.LVector;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTValues;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.model.impl.CTScriptImpl;
import org.collabthings.model.impl.CTToolImpl;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.simulation.CTSimpleSimulation;
import org.collabthings.simulation.CTSimulation;
import org.xml.sax.SAXException;

public class TestSimpleSimulation extends CTTestCase {

	private static final int MAX_SIMUALTION_RUNTIME = 2000000;

	public void testSimpleTransformation() throws FileNotFoundException, IOException {
		CTClient client = getNewClient();
		CTEnvironment env = new CTEnvironmentImpl(client);

		CTFactory f1 = client.getObjectFactory().getFactory();
		f1.setBoundingBox(new LVector(-10, 0, -10), new LVector(10, 10, 10));

		CTAttachedFactory f2 = f1.addFactory("f2");
		f2.getFactory().setBoundingBox(new LVector(-3, 0, -3), new LVector(3, 3, 3));
		f2.setLocation(new LVector(5, 0, 0));
		f2.setOrientation(new LVector(1, 1, 0), Math.PI / 6);

		CTAttachedFactory f21 = f2.getFactory().addFactory("f21");
		f21.getFactory().setBoundingBox(new LVector(-1, 0, -1), new LVector(1, 1, 1));
		f21.setLocation(new LVector(2, 1, 0));

		CTAttachedFactory f3 = f1.addFactory("f3");
		f3.getFactory().setBoundingBox(new LVector(-2, 0, -2), new LVector(2, 1.5, 2));
		f3.setLocation(new LVector(-3, 2, -3));

		CTAttachedFactory f4 = f1.addFactory();
		f4.getFactory().setBoundingBox(new LVector(-1, 0, -1), new LVector(1, 1, 1));
		f4.setLocation(new LVector(20, 0, 0));

		CTFactoryState f1s = new CTFactoryState(client, env, "f1s", f1);
		f1s.newPart().getPart().newSubPart();
		f1s.newPart().getPart().setBoundingBox(new LVector(), new LVector(1, 1, 1));

		CTFactoryState f2s = f1s.getFactory("f2");
		CTPartState f2part = f2s.newPart();
		f2part.getPart().setName("p2");
		CTOpenSCAD scad2 = f2part.getPart().newSCAD();
		scad2.setScript(loadATestFile("scad/test.scad"));
		scad2.setScale(0.03);

		CTFactoryState f21s = f2s.getFactory("f21");
		CTPartState p21s = f21s.newPart();
		p21s.getPart().setName("p21");

		CTOpenSCAD scad21 = p21s.getPart().newSCAD();
		scad21.setScript(loadATestFile("scad/test.scad"));
		scad21.setScale(0.04);

		CTRunEnvironment runenv = f1s.getRunEnvironment();

		final Map<String, Object> valuesmap = new HashMap<String, Object>();

		String name = "testvaluename";
		String value = "testvalue";

		runenv.addListener(new RunEnvironmentListener() {

			@Override
			public void taskFailed(CTRunEnvironment runenv, CTEnvironmentTask task) {
			}

			@Override
			public void event(CTRuntimeEvent e) {
				assertNotNull(e);
				assertNotNull(e.getName());
				assertNotNull(e.getValues());
				assertNotNull(e.getObject());
				assertTrue(("" + e).indexOf("test") >= 0);
			}
		});

		runenv.addTask(new CTScriptRunner() {

			@Override
			public boolean run(CTValues values) {
				valuesmap.put(name, values.get(name));

				long st = System.currentTimeMillis();
				while ((System.currentTimeMillis() - st) < 10000) {

					f2s.getOrientation().set(new LVector(1, 1, 0), System.currentTimeMillis() / 700.0);
					f21s.getOrientation().set(new LVector(0, 1, 0), System.currentTimeMillis() / 500.0);
					p21s.getOrientation().set(new LVector(0, 1, 0), System.currentTimeMillis() / 800.0);

					try {
						wait(20);
					} catch (Exception e) {
					}
				}

				runenv.recordEvent(f1s, "test", values);

				return true;
			}

			@Override
			public String getError() {
				return null;
			}
		}, new CTValues(name, value));

		assertNotNull(runenv.getInfo());

		CTSimulation simulation = new CTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));

		assertEquals(value, valuesmap.get("testvaluename"));
	}

	public void testFailingScript() throws IOException, SAXException {
		CTClient client = getNewClient();

		CTScriptImpl s = new CTScriptImpl(client);
		s.setScript("function test() {}");

		CTEnvironment env = new CTEnvironmentImpl(client);
		CTRunEnvironment runenv = new CTRunEnvironmentImpl(client, env);

		CTScriptRunner runner = new CTScriptRunnerImpl(s, runenv, null);
		runenv.addTask(runner);
		CTSimulation simulation = new CTSimpleSimulation(runenv);
		assertFalse(simulation.run(MAX_SIMUALTION_RUNTIME));
	}

	public void testSimpleScript() throws IOException, SAXException {
		CTClient client = getNewClient();

		CTEnvironment env = new CTEnvironmentImpl(client);
		String testvalue = "testvalue" + System.currentTimeMillis();
		//
		CTScriptImpl s = new CTScriptImpl(client);
		s.setScript(
				"function info() {} function run(env, params) { env.setParameter('testparam', '" + testvalue + "'); }");

		CTRunEnvironment runenv = new CTRunEnvironmentImpl(client, env);
		CTScriptRunner runner = new CTScriptRunnerImpl(s, runenv, null);
		runenv.addTask(runner);
		CTSimulation simulation = new CTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMUALTION_RUNTIME));
		assertEquals(testvalue, runenv.getParameter("testparam"));
	}

	public void testSimpleRobotSimulation() throws IOException, SAXException {
		CTClient client = getNewClient();

		CTEnvironment env = new CTEnvironmentImpl(client);
		CTFactoryImpl factory = new CTFactoryImpl(client);
		factory.setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));
		CTFactoryState factorystate = new CTFactoryState(client, env, "testfactory", factory);
		CTRunEnvironment rune = factorystate.getRunEnvironment();

		ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(rune, factorystate);
		factorystate.addToolUser(robot);
		factorystate.addToolUser(new ReallySimpleSuperheroRobot(rune, factorystate));

		CTPartState p = factorystate.newPart();
		p.getPart().newSubPart();

		CTToolState toolstate = factorystate.addTool("tool", new CTToolImpl(client));
		//
		CTScriptImpl script = new CTScriptImpl(client);
		String nscript = "function info(){} function run(e, factory) { factory.newPart(); factory.getTool('tool').moveTo(e.getVector(10,0,0), e.getVector(0,1,0), 6); } ";
		script.setScript(nscript);
		assertTrue(script.isOK());
		CTScriptRunner runner = new CTScriptRunnerImpl(script, rune, factorystate);
		rune.addTask(runner);
		CTSimulation s = new CTSimpleSimulation(rune);

		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));

		assertFalse(factorystate.getParts().isEmpty());
		//
		LVector l = toolstate.getLocation();
		assertReallyClose(new LVector(10, 0, 0), l);
	}

	public void testCallTool() throws CTRuntimeError {
		CTClient client = getNewClient();
		CTEnvironment env = new CTEnvironmentImpl(client);

		CTFactoryImpl f = new CTFactoryImpl(client);
		CTScriptImpl startscript = new CTScriptImpl(client);
		f.addScript("start", startscript);

		CTScriptImpl taskscript = new CTScriptImpl(client);
		String nscript = "function info(){} function run(e, factory, values) { "
				+ "e.log().info('calling tooltest'); factory.getTool('tool').call('tooltest', values); } ";
		taskscript.setScript(nscript);
		assertTrue(taskscript.isOK());
		f.addScript("factorytest", taskscript);

		CTFactoryState factorystate = new CTFactoryState(client, env, "testfactory", f);
		CTRunEnvironment rune = factorystate.getRunEnvironment();
		//
		CTToolImpl tool = new CTToolImpl(client);
		CTScriptImpl testscript = new CTScriptImpl(client);
		tool.addScript("tooltest", testscript);
		String testscriptvalue = "testvalue" + Math.random();
		testscript.setScript("function info() {} function run(e, runo, values) { e.setParameter('testfromtool', '"
				+ testscriptvalue + "'); }");
		//
		CTToolState toolstate = factorystate.addTool("tool", tool);
		assertNotNull(toolstate);
		//

		factorystate.addTask("factorytest", null);
		//
		CTSimulation s = new CTSimpleSimulation(rune);
		assertTrue(s.run(MAX_SIMUALTION_RUNTIME));
		//
		assertNotNull(rune.getParameter("testfromtool"));
		assertEquals(testscriptvalue, rune.getParameter("testfromtool"));
	}
}
