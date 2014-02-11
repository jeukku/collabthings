package org.libraryofthings.build;

import java.io.IOException;

import javax.script.ScriptException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTSimulationEnvironment;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;
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
		//
		partsource.addScript("need", getSourceNeedScript(env));
		//
		assembyscript.run(runenv);
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
			LOTEnvironment env) throws NoSuchMethodException, ScriptException {
		String s = "";
		s += "function info() { return \"testing box -building\"; }";
		s += "function run(e) { ";
		s += "	var part = e.getPart(e.getParameter('partid'));";
		s += "	var destinationpart = e.getPart('destinationpart');";
		//
		s += "  e.log().info(\"script going to a loop!!!\");";
		s += "  _.each(part.getSubParts().toArray(), function(subpart) {";
		s += "     e.log().info('script test ' + subpart);";
		s += "     moveAndAttach(e, subpart, destinationpart);";
		s += "  });";
		s += "  e.log().info(\"script end!!!\");";
		s += "}";

		s += "function moveAndAttach(e, subpart, destpart) {";
		s += "  var tool = e.getTool('tool');";
		s += "  var partsource = e.getTool('source');";
		s += "  partsource.call(e, 'need', subpart);";
		s += "  tool.moveTo(partsource.getLocation());";
		s += "  destpart.addSubPart(subpart);";
		s += "  e.log().info(\"moveAndAttach done\");";
		s += "}";
		//
		LOTScript lots = new LOTScript(env);
		lots.setScript(s);

		return lots;
	}
}
