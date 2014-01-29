package org.libraryofthings.build;

import java.io.IOException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;
import org.xml.sax.SAXException;

import waazdoh.cutils.MID;

public class TestBuildABox extends LOTTestCase {

	public void testBox() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);

		// Create a plate object
		LOTPart square = new LOTPart(env);
		square.setName("wall");

		// Create a box object
		LOTPart box = new LOTPart(env);
		box.setName("BOX");
		for (int i = 0; i < 6; i++) {
			LOTSubPart wall = box.newSubPart();
			wall.setPart(square);
		}

		box.getSubParts().get(0)
				.setOrientation(new LVector(0, -1, 0), new LVector(0, 1, 0));
		box.getSubParts().get(1)
				.setOrientation(new LVector(-1, 0, 0), new LVector(-1, 0, 0));
		box.getSubParts().get(2)
				.setOrientation(new LVector(1, 0, 0), new LVector(1, 0, 0));
		box.getSubParts().get(3)
				.setOrientation(new LVector(0, 0, -1), new LVector(0, 0, -1));
		box.getSubParts().get(4)
				.setOrientation(new LVector(0, 0, 1), new LVector(0, 0, 1));
		box.getSubParts().get(5)
				.setOrientation(new LVector(0, 1, 0), new LVector(0, 1, 0));

		// TODO picking up plates, moving them and leaving them somewhere

		// Create a tool to pick up plates
		LOTTool tool = new LOTTool(env);

		//
		LOTScript assembyscript = getAssemblyScript(tool, box, env);

		LOTPart destinationpart = new LOTPart(env);

		/*
		RunEnvironment runenv = SimualtionEnvironment(env);
		runenv.setParameter("partid", box.getServiceObject().getID());
		runenv.addPart("destinationpart", destinationpart);

		assembyscript.run(runenv);

		// TODO make it a box.
		assertTrue("is a box?", false);
		*/
	}

	private RunEnvironment SimualtionEnvironment(LOTEnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	private LOTScript getAssemblyScript(LOTTool tool, LOTPart box,
			LOTEnvironment env) {
		String s = "";
		s += "function run() { ";
		s += "	part = getEnv().getPart(getParameters('partid'));";
		s += "	destinationpart = getEnv().getPart(getParameters('destinationpartid'));";
		//
		s += "  part.getSubParts().foreach(function(subpart) {";
		s += "     moveAndAttach(subpart, destinationpart)";
		s += "  });";
		s += "}";

		//
		LOTScript lots = new LOTScript(env);
		lots.setScript(s);

		return lots;
	}
}
