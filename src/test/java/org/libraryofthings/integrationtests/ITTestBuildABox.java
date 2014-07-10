package org.libraryofthings.integrationtests;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.libraryofthings.simulation.LOTSimulationEnvironment;
import org.xml.sax.SAXException;

public final class ITTestBuildABox extends LOTTestCase {

	private static final int PARTS_IN_A_BOX = 6;
	private static final int MAX_SIMULATION_RUNTIME = 60000;

	public RunEnvironment testBox() throws NoSuchMethodException,
			ScriptException, IOException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);

		// Create a plate object
		LOTPart square = new LOTPart(env);
		square.setName("wall");

		// Create a box object
		LOTPart box = createBox(env, square);

		// TODO picking up plates, moving them and leaving them somewhere
		// Create a plate source
		LOTTool partsource = createPartSource(env);

		// Create a tool to pick up plates
		LOTTool tool = getPickupTool(env);

		LOTScript assembyscript = getAssemblyScript(tool, box, env);

		RunEnvironment runenv = new LOTSimulationEnvironment(env);
		runenv.addToolUser(new ReallySimpleSuperheroRobot(env, runenv));

		LOTSubPart destinationsubpart = runenv.getBasePart().getPart()
				.getPart().newSubPart();

		LOTPart destinationpart = env.getObjectFactory().getPart();

		runenv.setParameter("partid", box.getServiceObject().getID());
		runenv.addPart("destinationpart", destinationsubpart);
		runenv.addTool("source", partsource);
		runenv.addTool("tool", tool);
		runenv.addScript("MoveAndAttach",
				loadScript(env, "buildabox_moveandattach.js"));
		//
		runenv.addTask(assembyscript);
		//
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		simulation.run(MAX_SIMULATION_RUNTIME);
		//
		assertBuiltBox(box, destinationpart);
		//
		return runenv;
	}

	public void testBoxStack() throws NoSuchMethodException, IOException,
			SAXException, ScriptException {
		RunEnvironment env = testBox();
		//

		assertNotNull(env);
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

	private LOTTool createPartSource(LOTEnvironment env)
			throws NoSuchMethodException, ScriptException {
		LOTTool partsource = env.getObjectFactory().getTool();
		partsource.addScript("need", getSourceNeedScript(env));

		return partsource;
	}

	private LOTTool getPickupTool(LOTEnvironment env) throws IOException,
			NoSuchMethodException, ScriptException {
		LOTTool tool = createPartSource(env);
		tool.addScript("pickup", loadScript(env, "buildabox_pickup.js"));
		tool.addScript("attach", loadScript(env, "buildabox_attach.js"));
		return tool;
	}

	private LOTPart createBox(LOTEnvironment env, LOTPart square) {
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

	private LOTScript getSourceNeedScript(LOTEnvironment env)
			throws NoSuchMethodException, ScriptException {
		LOTScript script = new LOTScript(env);
		String s = "function info() { return \"need script in part source\"; } ";
		s += "function run(e, o) { ";
		s += "  e.log().info(\"NEED \" + o);";
		s += "}";

		script.setScript(s);
		return script;
	}

	private LOTScript getAssemblyScript(LOTTool tool, LOTPart box,
			LOTEnvironment env) throws NoSuchMethodException, ScriptException,
			IOException {
		String scriptname = "buildabox_assembly.js";
		LOTScript lots = loadScript(env, scriptname);
		return lots;
	}

	private LOTScript loadScript(LOTEnvironment env, String scriptname)
			throws IOException {
		String s = loadATestScript(scriptname);
		//
		LOTScript lots = new LOTScript(env);
		lots.setScript(s);
		assertNotNull(lots.getScript());
		return lots;
	}
}
