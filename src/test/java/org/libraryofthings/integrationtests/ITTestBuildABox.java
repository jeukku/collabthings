package org.libraryofthings.integrationtests;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTEnvironmentImpl;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.model.LOTValues;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.simulation.LOTSimulation;
import org.xml.sax.SAXException;

import waazdoh.util.MStringID;

public final class ITTestBuildABox extends LOTTestCase {

	private static final int PARTS_IN_A_BOX = 6;
	private static final int MAX_SIMULATION_RUNTIME = 60000;
	//
	private LOTPart box;
	private LLog log = LLog.getLogger(this);

	public void testBoxLine() throws NoSuchMethodException, IOException,
			SAXException, ScriptException, LOTToolException {
		LOTClient client = getNewClient();

		LOTEnvironment env = new LOTEnvironmentImpl(client);

		LOTFactory factory = setupFactoryThatUsesBoxes(client);
		LOTFactoryState factorystate = factory.initRuntimeEnvironment(client,
				env);
		RunEnvironment runenv = factorystate.getRunEnvironment();

		factory.setLocation(new LVector(0, 0, 0));
		//
		LOTPart line = runenv.getClient().getObjectFactory().getPart();
		line.setName("line");
		assertNotNull(line);
		//
		for (int i = 0; i < 10; i++) {
			LOTSubPart sb = line.newSubPart();
			sb.setPart(box);
			sb.setOrientation(new LVector(i * 2, 0, 0), new LVector(0, 1, 0));
		}
		//
		factorystate.call("order", new LOTValues("partid", line.getID()));
		//
		LOTSimulation simulation = new LOTSimpleSimulation(runenv, true);

		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));
		//
		LOTPart builtline = factorystate.getPool().getPart("" + line.getID());
		assertNotNull(builtline);
	}

	public void testBuildABox() throws NoSuchMethodException, ScriptException,
			IOException {
		RunEnvironment runenv = testBox();
		assertNotNull(runenv);
	}

	private LOTFactory setupFactoryThatUsesBoxes(LOTClient client)
			throws IOException, NoSuchMethodException, ScriptException {
		LOTFactory factory = createAssemblyFactory(client);
		log.info("factory that uses boxes " + factory);

		LOTFactory boxfactory = createBoxFactory(client);
		boxfactory.setLocation(new LVector(20, 0, 0));
		factory.addFactory("source", boxfactory);

		factory.addFactory("boxfactory", boxfactory);

		return factory;
	}

	public RunEnvironment testBox() throws NoSuchMethodException,
			ScriptException, IOException {
		LOTClient client = getNewClient();
		assertNotNull(client);

		LOTFactory boxfactory = createBoxFactory(client);

		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTFactoryState factorystate = boxfactory.initRuntimeEnvironment(
				client, env);
		RunEnvironment runenv = factorystate.getRunEnvironment();

		//
		runenv.addTask(getCallOrderScript(client), factorystate, null);
		//
		String partid = boxfactory.getEnvironment().getParameter("partid");
		//
		LOTSimulation simulation = new LOTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));

		//
		LOTPart nbox = factorystate.getPool().getPart(partid);
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

	private LOTScript getCallOrderScript(LOTClient env) {
		LOTScript s = new LOTScript(env);
		s.setScript("function run(env, factory, values) { env.log().info('calling order ' + factory + ' values ' + values); factory.call('order'); } function info() { return 'calling order'; }");
		s.setName("order");
		return s;
	}

	private LOTFactory createBoxFactory(LOTClient client)
			throws NoSuchMethodException, ScriptException, IOException {
		LOTFactory boxfactory = createAssemblyFactory(client);
		boxfactory.setName("boxfactory");
		log.info("Boxfactory " + boxfactory);

		// TODO picking up plates, moving them and leaving them somewhere
		// Create a plate object
		LOTPart square = client.getObjectFactory().getPart();
		square.setName("square");
		log.info("Square " + square);

		// Create a box object
		LOTPart box = createBox(client, square);
		boxfactory.getEnvironment().setParameter("partid",
				box.getServiceObject().getID());
		log.info("box " + box);

		// Create a plate source
		LOTFactory platesource = createPlateSource(client, square);
		platesource.setName("platesource");
		boxfactory.addFactory("source", platesource);

		log.info("platesource " + platesource + " with square " + square);
		log.info("square bean " + square.getBean());
		return boxfactory;
	}

	private LOTFactory createAssemblyFactory(LOTClient client)
			throws IOException, NoSuchMethodException, ScriptException {
		LOTFactory factory = new LOTFactory(client);
		// Create a tool to pick up plates
		LOTTool tool = getPickupTool(client);
		factory.getEnvironment().addTool("pickuptool", tool);
		// scripts
		factory.addScript("moveandattach",
				loadScript(client, "assembly_moveandattach.js"));
		factory.addScript("build", loadScript(client, "assembly_build.js"));
		factory.addScript("order", loadScript(client, "assembly_order.js"));
		factory.addScript("start", loadScript(client, "assembly_start.js"));
		return factory;
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

	private LOTFactory createPlateSource(LOTClient client, LOTPart square)
			throws NoSuchMethodException, ScriptException, IOException {
		LOTFactory partsource = new LOTFactory(client);
		partsource.setName("platesource");

		partsource.getEnvironment().setParameter("plateid",
				square.getServiceObject().getID());
		partsource.addScript("order",
				loadScript(client, "platesource_order.js"));
		partsource.addScript("build",
				loadScript(client, "platesource_build.js"));

		return partsource;
	}

	private LOTTool getPickupTool(LOTClient client) throws IOException,
			NoSuchMethodException, ScriptException {
		LOTTool tool = client.getObjectFactory().getTool();
		tool.addScript("pickup", loadScript(client, "assembly_pickup.js"));
		tool.addScript("attach", loadScript(client, "assembly_attach.js"));
		return tool;
	}

	private LOTPart createBox(LOTClient env, LOTPart square) {
		box = env.getObjectFactory().getPart();
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
