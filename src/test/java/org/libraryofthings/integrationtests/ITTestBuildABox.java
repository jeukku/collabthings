package org.libraryofthings.integrationtests;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTEnvironmentImpl;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.xml.sax.SAXException;

import waazdoh.util.MStringID;

public final class ITTestBuildABox extends LOTTestCase {

	private static final int PARTS_IN_A_BOX = 6;
	private static final int MAX_SIMULATION_RUNTIME = 60000;

	public RunEnvironment testBox() throws NoSuchMethodException,
			ScriptException, IOException {
		LOTClient client = getNewClient();
		assertNotNull(client);

		LOTTool boxfactory = createBoxFactory(client);

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		RunEnvironment runenv = new LOTRunEnvironmentImpl(client, env);
		runenv.addToolUser(new ReallySimpleSuperheroRobot(client, runenv));
		runenv.addTool("boxfactory", boxfactory);
		//
		runenv.addTask(getCallOrderScript(client));
		//
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));
		//
		RunEnvironment boxfactoryenv = runenv.getTool("boxfactor")
				.getEnvironment();
		//
		LOTPart box = boxfactoryenv.getPool().getPart("box");
		LOTPart destinationpart = client.getObjectFactory().getPart(
				new MStringID(boxfactory.getEnvironment()
						.getParameter("partid")));
		assertBuiltBox(box, destinationpart);
		//
		return runenv;
	}

	private LOTScript getCallOrderScript(LOTClient env) {
		LOTScript s = new LOTScript(env);
		s.setScript("function run(env) { env.log().info('calling order'); env.getTool('boxfactory').call('order'); } function info() { return 'calling order'; }");
		s.setName("order");
		return s;
	}

	private LOTTool createBoxFactory(LOTClient env)
			throws NoSuchMethodException, ScriptException, IOException {
		// Create a plate object
		LOTPart square = new LOTPart(env);
		square.setName("wall");
		// Create a box object
		LOTPart box = createBox(env, square);
		//
		LOTTool boxfactory = new LOTTool(env);
		// TODO picking up plates, moving them and leaving them somewhere
		// Create a plate source
		LOTTool partsource = createPartSource(env);
		// Create a tool to pick up plates
		LOTTool tool = getPickupTool(env);

		boxfactory.getEnvironment().setParameter("partid",
				box.getServiceObject().getID());
		// tools
		boxfactory.getEnvironment().addTool("source", partsource);
		boxfactory.getEnvironment().addTool("tool", tool);
		// scripts
		boxfactory.addScript("MoveAndAttach",
				loadScript(env, "buildabox_moveandattach.js"));
		boxfactory.addScript("Assembly",
				loadScript(env, "buildabox_assembly.js"));
		boxfactory.addScript("order", loadScript(env, "buildabox_order.js"));

		return boxfactory;
	}

	public void testBoxStack() throws NoSuchMethodException, IOException,
			SAXException, ScriptException, LOTToolException {
		RunEnvironment env = testBox();
		//
		LOTToolState boxfactory = env.getTool("boxfactory");
		boxfactory.call("need");

		assertNull(env);
	}

	private void assertBuiltBox(LOTPart box, LOTPart destinationpart) {
		assertTrue(destinationpart.getSubParts().size() == PARTS_IN_A_BOX);
		// Checking out everything is in place
		List<LOTSubPart> boxsubparts = box.getSubParts();
		List<LOTSubPart> destsubparts = destinationpart.getSubParts();

		for (int i = 0; i < boxsubparts.size(); i++) {
			LOTSubPart boxsubpart = boxsubparts.get(i);
			LOTSubPart destsubpart = destsubparts.get(i);
			String boxlstring = boxsubpart.getLocation().toString();
			String destlstring = destsubpart.getLocation().toString();
			assertEquals("subpart index " + i, boxlstring, destlstring);
			assertEquals(boxsubpart.getNormal().toString(), destsubpart
					.getNormal().toString());
		}
	}

	private LOTTool createPartSource(LOTClient env)
			throws NoSuchMethodException, ScriptException {
		LOTTool partsource = env.getObjectFactory().getTool();
		partsource.addScript("need", getSourceNeedScript(env));

		return partsource;
	}

	private LOTTool getPickupTool(LOTClient env) throws IOException,
			NoSuchMethodException, ScriptException {
		LOTTool tool = createPartSource(env);
		tool.addScript("pickup", loadScript(env, "buildabox_pickup.js"));
		tool.addScript("attach", loadScript(env, "buildabox_attach.js"));
		return tool;
	}

	private LOTPart createBox(LOTClient env, LOTPart square) {
		LOTPart box = env.getObjectFactory().getPart();
		box.setName("BOX");
		for (int i = 0; i < PARTS_IN_A_BOX; i++) {
			LOTSubPart wall = box.newSubPart();
			wall.setPart(square);
		}

		int partindex = 0;
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, -2, 0), new LVector(0, 1, 0));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(-2, 0, 0), new LVector(-1, 0, 0));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(2, 0, 0), new LVector(1, 0, 0));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, 0, -2), new LVector(0, 0, -1));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, 0, 2), new LVector(0, 0, 1));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, 2, 0), new LVector(0, 1, 0));
		return box;
	}

	private LOTScript getSourceNeedScript(LOTClient env)
			throws NoSuchMethodException, ScriptException {
		LOTScript script = new LOTScript(env);
		String s = "function info() { return \"need script in part source\"; } ";
		s += "function run(e, o) { ";
		s += "  e.log().info(\"NEED \" + o);";
		s += "}";

		script.setScript(s);
		return script;
	}

	private LOTScript loadScript(LOTClient env, String scriptname)
			throws IOException {
		String s = loadATestScript(scriptname);
		//
		LOTScript lots = new LOTScript(env);
		lots.setScript(s);
		lots.setName(scriptname);
		assertNotNull(lots.getScript());
		return lots;
	}
}
