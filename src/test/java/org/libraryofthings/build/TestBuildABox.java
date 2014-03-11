package org.libraryofthings.build;

import java.io.IOException;

import javax.script.ScriptException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.libraryofthings.simulation.LOTSimulationEnvironment;
import org.xml.sax.SAXException;

public final class TestBuildABox extends LOTTestCase {

	private static final int PARTS_IN_A_BOX = 6;

	public void testBox() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);

		// Create a plate object
		LOTPart square = new LOTPart(env);
		square.setName("wall");

		// Create a box object
		LOTPart box = env.getObjectFactory().getPart();
		box.setName("BOX");
		for (int i = 0; i < PARTS_IN_A_BOX; i++) {
			LOTSubPart wall = box.newSubPart();
			wall.setPart(square);
		}

		int partindex = 0;
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, -1, 0), new LVector(0, 1, 0));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(-1, 0, 0), new LVector(-1, 0, 0));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(1, 0, 0), new LVector(1, 0, 0));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, 0, -1), new LVector(0, 0, -1));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, 0, 1), new LVector(0, 0, 1));
		box.getSubParts().get(partindex++)
				.setOrientation(new LVector(0, 1, 0), new LVector(0, 1, 0));

		// TODO picking up plates, moving them and leaving them somewhere
		// Create a plate source
		LOTTool partsource = env.getObjectFactory().getTool();

		// Create a tool to pick up plates
		LOTTool tool = env.getObjectFactory().getTool();

		//
		LOTScript assembyscript = getAssemblyScript(tool, box, env);

		LOTPart destinationpart = env.getObjectFactory().getPart();

		RunEnvironment runenv = new LOTSimulationEnvironment(env);
		runenv.setParameter("partid", box.getServiceObject().getID());
		runenv.addPart("destinationpart", destinationpart);
		runenv.addTool("source", partsource);
		runenv.addTool("tool", tool);
		runenv.addScript("MoveAndAttach",
				loadScript(env, "buildabox_moveandattach.js"));
		//
		partsource.addScript("need", getSourceNeedScript(env));
		//
		assembyscript.run(runenv);
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		simulation.run();
		//
		assertTrue(destinationpart.getSubParts().size() == PARTS_IN_A_BOX);
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
