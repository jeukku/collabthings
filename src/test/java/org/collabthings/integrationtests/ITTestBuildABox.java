package org.collabthings.integrationtests;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptException;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.LOTToolException;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTAttachedFactory;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTSubPart;
import org.collabthings.model.LOTTool;
import org.collabthings.model.impl.LOTScriptImpl;
import org.collabthings.model.run.LOTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.LOTRunEnvironmentBuilderImpl;
import org.collabthings.simulation.LOTSimpleSimulation;
import org.collabthings.simulation.LOTSimulation;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import waazdoh.common.MStringID;

public final class ITTestBuildABox extends LOTTestCase {

	private static final int PARTS_IN_A_BOX = 6;
	private static final int MAX_SIMULATION_RUNTIME = 600000;
	//
	private LOTPart box;
	private LLog log = LLog.getLogger(this);

	public synchronized void testBoxLine() throws NoSuchMethodException,
			IOException, SAXException, ScriptException, LOTToolException,
			InterruptedException {
		LOTClient client = getNewClient();

		LOTFactory factory = client.getObjectFactory().getFactory();
		setupFactoryThatUsesBoxes(factory, client);

		factory.setToolUserSpawnLocation(new LVector(20, 0, 20));
		factory.setName("boxsetfactory");

		factory.publish();
		//
		LOTPart line = getLineOfBoxes(client);
		//
		LOTRunEnvironmentBuilder builder = new LOTRunEnvironmentBuilderImpl(
				client);
		builder.getEnvironment().addScript(
				"init",
				loadScript(new LOTScriptImpl(client),
						"linefactory_runenv_init.js"));
		builder.getEnvironment().addScript(
				"addorder",
				loadScript(new LOTScriptImpl(client),
						"linefactory_runenv_order.js"));
		builder.getEnvironment().setParameter("partid", line.getID());
		
		builder.setName("boxsetfactorybuilder");
		
		builder.publish();

		LOTRunEnvironment runenv = builder.getRunEnvironment();
		assertNotNull(runenv);
		//
		LOTSimulation simulation = new LOTSimpleSimulation(runenv, true);

		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));
		//
		LOTFactoryState factorystate = ((LOTFactoryState) runenv
				.getRunObject("boxsetfactory"));
		LOTPart builtline = factorystate.getPool().getPart("" + line.getID());
		assertNotNull(builtline);
		//

		assertTrue(builtline.isAnEqualPart(line));
	}

	private LOTPart getLineOfBoxes(LOTClient client) {
		LOTPart line = client.getObjectFactory().getPart();
		line.setName("line");
		assertNotNull(line);
		//
		for (int i = 0; i < 3; i++) {
			LOTSubPart sb = line.newSubPart();
			sb.setPart(box);
			sb.setOrientation(new LVector(i * 2, 0, 0), new LVector(0, 1, 0),
					Math.PI / 2);
		}

		line.publish();

		return line;
	}

	public void testBuildABox() throws NoSuchMethodException, ScriptException,
			IOException {
		LOTRunEnvironment runenv = testBox();
		assertNotNull(runenv);
	}

	private LOTFactory setupFactoryThatUsesBoxes(LOTFactory factory,
			LOTClient client) throws IOException, NoSuchMethodException,
			ScriptException {
		createAssemblyFactory(factory, client);
		log.info("factory that uses boxes " + factory);

		factory.setBoundingBox(new LVector(-100, 0, -100), new LVector(100, 10,
				100));
		factory.getEnvironment().setVectorParameter("storage",
				new LVector(15, 1, 50));
		factory.getEnvironment().setVectorParameter("buildingpartlocation",
				new LVector(15, 1, 0));

		LOTAttachedFactory boxfactory1 = factory.addFactory("source");
		createBoxFactory(boxfactory1.getFactory(), client);
		boxfactory1.setLocation(new LVector(-5, 0, 10));

		LOTAttachedFactory boxfactory2 = factory.addFactory("source2",
				boxfactory1.getFactory());
		boxfactory2.setLocation(new LVector(-5, 0, -10));

		return factory;
	}

	public LOTRunEnvironment testBox() throws NoSuchMethodException,
			ScriptException, IOException {
		LOTClient client = getNewClient();
		assertNotNull(client);

		LOTFactory boxfactory = client.getObjectFactory().getFactory();
		createBoxFactory(boxfactory, client);

		LOTRunEnvironmentBuilder builder = new LOTRunEnvironmentBuilderImpl(
				client);

		builder.getEnvironment().addScript(
				"init",
				loadScript(new LOTScriptImpl(client),
						"boxfactory_runenv_init.js"));
		builder.getEnvironment().addScript(
				"addorder",
				loadScript(new LOTScriptImpl(client),
						"boxfactory_runenv_order.js"));
		builder.publish();

		LOTRunEnvironment runenv = builder.getRunEnvironment();

		//
		LOTSimulation simulation = new LOTSimpleSimulation(runenv, true);
		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));
		//
		String partid = boxfactory.getEnvironment().getParameter("partid");
		log.info(runenv.printOut().toText());

		LOTPart nbox = ((LOTFactoryState) runenv.getRunObject("boxfactory"))
				.getPool().getPart(partid);
		assertNotNull(nbox);
		//
		LOTPart modelpart = client.getObjectFactory().getPart(
				new MStringID(partid));
		assertNotNull(modelpart);
		//
		assertBuiltBox(nbox, modelpart);
		//
		return runenv;
	}

	private LOTFactory createBoxFactory(LOTFactory boxfactory, LOTClient client)
			throws NoSuchMethodException, ScriptException, IOException {
		createAssemblyFactory(boxfactory, client);
		boxfactory.setName("boxfactory");
		log.info("Boxfactory " + boxfactory);

		// TODO picking up plates, moving them and leaving them somewhere
		// Create a plate object
		LOTPart square = client.getObjectFactory().getPart();
		square.setName("square");
		square.setBoundingBox(new LVector(-1, 0, -1), new LVector(1, 0.1, 1));
		log.info("Square " + square);
		square.publish();

		// Create a box object
		LOTPart box = createBox(client, square);
		box.setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));
		box.publish();

		boxfactory.getEnvironment().setParameter("fillpool", "2");
		boxfactory.getEnvironment().setParameter("partid", box.getID());
		boxfactory.getEnvironment().setVectorParameter("storage",
				new LVector(10, 3, 2));
		boxfactory.getEnvironment().setVectorParameter("buildingpartlocation",
				new LVector(0, 1, 0));

		log.info("box " + box);

		// Create a plate source
		LOTFactory squarefactory = createPlateSource(
				boxfactory.addFactory("source").getFactory(), client, square);
		squarefactory.setName("squarefactory");
		squarefactory.setBoundingBox(new LVector(-3, 0, -3), new LVector(3, 3,
				3));
		squarefactory.getEnvironment().setVectorParameter("storage",
				new LVector(-3, 1, 0));
		squarefactory.getEnvironment().setVectorParameter(
				"buildingpartlocation", new LVector(-1, 1, 0));

		boxfactory.setBoundingBox(new LVector(-10, 0, -10), new LVector(10, 10,
				10));
		log.info("platesource " + squarefactory + " with square " + square);
		log.info("square bean " + square.getObject());

		boxfactory.publish();
		return boxfactory;
	}

	private LOTFactory createAssemblyFactory(LOTFactory factory,
			LOTClient client) throws IOException, NoSuchMethodException,
			ScriptException {
		// Create a tool to pick up plates
		LOTTool tool = getPickupTool(client);
		factory.getEnvironment().addTool("pickuptool", tool);
		// scripts

		loadScript(factory.addScript("moveandattach"),
				"assembly_moveandattach.js");
		loadScript(factory.addScript("build"), "assembly_build.js");
		loadScript(factory.addScript("order"), "assembly_order.js");
		loadScript(factory.addScript("start"), "assembly_start.js");
		return factory;
	}

	private void assertBuiltBox(LOTPart nbox, LOTPart destinationpart) {
		assertTrue(destinationpart.getSubParts().size() == PARTS_IN_A_BOX);
		// Checking out everything is in place
		List<LOTSubPart> boxsubparts = nbox.getSubParts();
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

	private LOTFactory createPlateSource(LOTFactory platesource,
			LOTClient client, LOTPart square) throws NoSuchMethodException,
			ScriptException, IOException {
		platesource.setName("platesource");

		platesource.getEnvironment().setParameter("plateid", square.getID());
		loadScript(platesource.addScript("order"), "platesource_order.js");
		loadScript(platesource.addScript("build"), "platesource_build.js");

		return platesource;
	}

	private LOTTool getPickupTool(LOTClient client) throws IOException,
			NoSuchMethodException, ScriptException {
		LOTTool tool = client.getObjectFactory().getTool();
		loadScript(tool.addScript("pickup"), "assembly_pickup.js");
		loadScript(tool.addScript("attach"), "assembly_attach.js");
		loadScript(tool.addScript("draw"), "assembly_drawtool.js");
		return tool;
	}

	private LOTPart createBox(LOTClient env, LOTPart square) throws IOException {
		box = env.getObjectFactory().getPart();
		box.setName("BOX");
		for (int i = 0; i < PARTS_IN_A_BOX; i++) {
			LOTSubPart wall = box.newSubPart();
			wall.setPart(square);
		}

		int partindex = 0;
		box.getSubParts()
				.get(partindex++)
				.setOrientation(new LVector(0, -1, 0), new LVector(0, -1, 0),
						Math.PI);
		box.getSubParts()
				.get(partindex++)
				.setOrientation(new LVector(-1, 0, 0), new LVector(-1, 0, 0),
						Math.PI);
		box.getSubParts()
				.get(partindex++)
				.setOrientation(new LVector(1, 0, 0), new LVector(1, 0, 0),
						Math.PI);
		box.getSubParts()
				.get(partindex++)
				.setOrientation(new LVector(0, 0, -1), new LVector(0, 0, -1),
						Math.PI);
		box.getSubParts()
				.get(partindex++)
				.setOrientation(new LVector(0, 0, 1), new LVector(0, 0, 1),
						Math.PI);
		box.getSubParts()
				.get(partindex++)
				.setOrientation(new LVector(0, 1, 0), new LVector(0, 1, 0),
						Math.PI);

		box.setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));

		return box;
	}

	private LOTScript loadScript(LOTScript lots, String scriptname)
			throws IOException {
		String s = loadATestScript(scriptname);
		lots.setScript(s);
		lots.setName(scriptname);
		assertNotNull(lots.getScript());
		return lots;
	}
}
