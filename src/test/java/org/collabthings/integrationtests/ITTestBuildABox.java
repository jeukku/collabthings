package org.collabthings.integrationtests;

import static org.junit.Assert.assertNotEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.CTToolException;
import org.collabthings.datamodel.StorageAreaSearchVO;
import org.collabthings.datamodel.WStringID;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CTApplicationImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.CTRunEnvironmentBuilderImpl;
import org.collabthings.simulation.CTSimpleSimulation;
import org.collabthings.simulation.CTSimulation;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

public final class ITTestBuildABox extends CTTestCase {

	private static final int PARTS_IN_A_BOX = 6;
	private static final int MAX_SIMULATION_RUNTIME = 600000;
	//
	private CTPart box;
	private LLog log = LLog.getLogger(this);
	private CTPart square;

	public synchronized void testBoxLine()
			throws NoSuchMethodException, IOException, SAXException, CTToolException, InterruptedException {
		CTClient client = getNewClient();
		info("client " + client);

		CTFactory factory = client.getObjectFactory().getFactory();
		setupFactoryThatUsesBoxes(factory, client);
		info("factory setup done");

		factory.setToolUserSpawnLocation(new Vector3f(20, 0, 20));
		factory.setName("boxsetfactory");

		factory.publish();
		info("Factory published");
		//
		CTPart line = getLineOfBoxes(client);
		info("line of boxes " + line);
		//
		CTRunEnvironmentBuilder builder = new CTRunEnvironmentBuilderImpl(client);
		builder.getEnvironment().addApplication("init",
				loadApplication(new CTApplicationImpl(client), "linefactory_runenv_init.js"));
		builder.getEnvironment().addApplication("addorder",
				loadApplication(new CTApplicationImpl(client), "linefactory_runenv_order.js"));
		builder.getEnvironment().setParameter("partid", line.getID());

		builder.setName("boxsetfactorybuilder");
		info("Builder created");

		builder.publish();
		info("Builder published");

		CTRunEnvironment runenv = builder.getRunEnvironment();
		assertNotNull(runenv);
		info("RunEnv " + runenv);
		//
		CTSimulation simulation = new CTSimpleSimulation(runenv);
		info("Simulation " + simulation);

		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));
		//
		CTFactoryState factorystate = ((CTFactoryState) runenv.getRunObject("boxsetfactory"));
		CTPart builtline = factorystate.getPool().getPart("" + line.getID());
		assertNotNull(builtline);
		//

		assertTrue(builtline.isAnEqualPart(line));
	}

	private CTPart getLineOfBoxes(CTClient client) {
		info("line of boxes");

		CTPart line = client.getObjectFactory().getPart();
		line.setName("line");
		assertNotNull(line);
		//
		for (int i = 0; i < 3; i++) {
			CTSubPart sb = line.newSubPart();
			sb.setPart(box);
			sb.setOrientation(new Vector3f(i * 2, 0, 0), new Vector3f(0, 1, 0), Math.PI / 2);
		}

		info("line of boxes publishing");

		line.publish();

		info("line of boxes done");

		return line;
	}

	public void testBuildABox() throws NoSuchMethodException, IOException {
		CTRunEnvironment runenv = testBox();
		assertNotNull(runenv);
	}

	private CTFactory setupFactoryThatUsesBoxes(CTFactory factory, CTClient client)
			throws IOException, NoSuchMethodException {
		createAssemblyFactory(factory, client);
		info("factory that uses boxes " + factory);

		factory.setBoundingBox(new Vector3f(-100, 0, -100), new Vector3f(100, 10, 100));
		factory.getEnvironment().setVectorParameter("storage", new Vector3f(15, 1, 50));
		factory.getEnvironment().setVectorParameter("buildingpartlocation", new Vector3f(15, 1, 0));

		CTAttachedFactory boxfactory1 = factory.addFactory("source");
		createBoxFactory(boxfactory1.getFactory(), client);
		boxfactory1.setLocation(new Vector3f(-5, 0, 10));

		CTAttachedFactory boxfactory2 = factory.addFactory("source2", boxfactory1.getFactory());
		boxfactory2.setLocation(new Vector3f(-5, 0, -10));

		return factory;
	}

	public CTRunEnvironment testBox() throws NoSuchMethodException, IOException {
		CTClient client = getNewClient();
		assertNotNull(client);

		CTFactory boxfactory = client.getObjectFactory().getFactory();
		createBoxFactory(boxfactory, client);

		info("creating builder");

		CTRunEnvironmentBuilder builder = new CTRunEnvironmentBuilderImpl(client);

		builder.getEnvironment().addApplication("init",
				loadApplication(new CTApplicationImpl(client), "boxfactory_runenv_init.js"));
		builder.getEnvironment().addApplication("addorder",
				loadApplication(new CTApplicationImpl(client), "boxfactory_runenv_order.js"));

		info("publishing builder");
		builder.publish();

		CTRunEnvironment runenv = builder.getRunEnvironment();

		//
		CTSimulation simulation = new CTSimpleSimulation(runenv);
		assertTrue(simulation.run(MAX_SIMULATION_RUNTIME));
		//
		String partid = boxfactory.getEnvironment().getParameter("partid");
		info(runenv.printOut().toText());

		CTPart nbox = ((CTFactoryState) runenv.getRunObject("boxfactory")).getPool().getPart(partid);
		assertNotNull(nbox);
		//
		CTPart modelpart = client.getObjectFactory().getPart(new WStringID(partid));
		assertNotNull(modelpart);
		//
		assertBuiltBox(nbox, modelpart);
		//
		return runenv;
	}

	private CTFactory createBoxFactory(CTFactory boxfactory, CTClient client)
			throws NoSuchMethodException, IOException {
		createAssemblyFactory(boxfactory, client);
		boxfactory.setName("boxfactory");
		info("Boxfactory " + boxfactory);

		// TODO picking up plates, moving them and leaving them somewhere
		CTPart square = getSquare(client);

		// Create a box object
		CTPart box = createBox(client, square);
		box.setBoundingBox(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));

		info("publishing box");
		box.publish();

		boxfactory.getEnvironment().setParameter("fillpool", "2");
		boxfactory.getEnvironment().setParameter("partid", box.getID());
		boxfactory.getEnvironment().setVectorParameter("storage", new Vector3f(10, 3, 2));
		boxfactory.getEnvironment().setVectorParameter("buildingpartlocation", new Vector3f(0, 1, 0));

		info("box " + box);

		// Create a plate source
		CTFactory squarefactory = createPlateSource(boxfactory.addFactory("source").getFactory(), client, square);
		squarefactory.setName("squarefactory");
		squarefactory.setBoundingBox(new Vector3f(-3, 0, -3), new Vector3f(3, 3, 3));
		squarefactory.getEnvironment().setVectorParameter("storage", new Vector3f(-3, 1, 0));
		squarefactory.getEnvironment().setVectorParameter("buildingpartlocation", new Vector3f(-1, 1, 0));

		boxfactory.setBoundingBox(new Vector3f(-10, 0, -10), new Vector3f(10, 10, 10));
		info("platesource " + squarefactory + " with square " + square);
		info("square bean " + square.getObject());

		boxfactory.publish();

		info("boxfactory done");

		return boxfactory;
	}

	private CTPart getSquare(CTClient client) throws FileNotFoundException, IOException {
		// Create a plate object
		if (this.square == null) {
			this.square = client.getObjectFactory().getPart();
			square.setName("square");
			CTOpenSCAD scad = square.newSCAD();
			scad.setApplication(loadATestFile("scad/square.scad"));
			scad.setScale(0.2);

			square.setBoundingBox(new Vector3f(-1, 0, -1), new Vector3f(1, 0.1f, 1));
			info("Square " + square);
			square.publish();
		}
		return square;
	}

	private CTFactory createAssemblyFactory(CTFactory factory, CTClient client)
			throws IOException, NoSuchMethodException {
		// Create a tool to pick up plates
		CTTool tool = getPickupTool(client);
		factory.getEnvironment().addTool("pickuptool", tool);
		// applications

		loadApplication(factory.addApplication("moveandattach"), "assembly_moveandattach.js");
		loadApplication(factory.addApplication("build"), "assembly_build.js");
		loadApplication(factory.addApplication("order"), "assembly_order.js");
		loadApplication(factory.addApplication("start"), "assembly_start.js");
		return factory;
	}

	private void assertBuiltBox(CTPart nbox, CTPart destinationpart) {
		assertTrue(destinationpart.getSubParts().size() == PARTS_IN_A_BOX);
		// Checking out everything is in place
		List<CTSubPart> boxsubparts = nbox.getSubParts();
		List<CTSubPart> destsubparts = destinationpart.getSubParts();

		for (int i = 0; i < boxsubparts.size(); i++) {
			CTSubPart boxsubpart = boxsubparts.get(i);
			CTSubPart destsubpart = destsubparts.get(i);
			String boxlstring = boxsubpart.getLocation().toString();
			String destlstring = destsubpart.getLocation().toString();
			assertEquals("subpart index " + i, boxlstring, destlstring);
			assertEquals(boxsubpart.getNormal().toString(), destsubpart.getNormal().toString());
		}
	}

	private CTFactory createPlateSource(CTFactory platesource, CTClient client, CTPart square)
			throws NoSuchMethodException, IOException {
		platesource.setName("platesource");

		square.publish();

		StorageAreaSearchVO searchvo = new StorageAreaSearchVO();
		searchvo.setSearchTerm(square.getID().toString());

		List<String> searchValue = client.getService().getStorageArea().searchValue(searchvo);

		info("createPlateSource square search result " + searchValue);
		platesource.getEnvironment().setParameter("bmplate", searchValue.get(0));
		loadApplication(platesource.addApplication("order"), "platesource_order.js");
		loadApplication(platesource.addApplication("build"), "platesource_build.js");

		return platesource;
	}

	private void info(String string) {
		log.info("dt:" + (System.currentTimeMillis() - starttime) + " " + string);
	}

	private CTTool getPickupTool(CTClient client) throws IOException, NoSuchMethodException {
		CTTool tool = client.getObjectFactory().getTool();
		loadApplication(tool.addApplication("pickup"), "assembly_pickup.js");
		loadApplication(tool.addApplication("attach"), "assembly_attach.js");
		loadApplication(tool.addApplication("draw"), "assembly_drawtool.js");
		return tool;
	}

	private CTPart createBox(CTClient env, CTPart square) throws IOException {
		info("creating box");

		box = env.getObjectFactory().getPart();
		CTOpenSCAD scad = box.newSCAD();
		scad.setApplication(loadATestFile("scad/cube.scad"));
		scad.setScale(2);

		box.setName("BOX");
		for (int i = 0; i < PARTS_IN_A_BOX; i++) {
			CTSubPart wall = box.newSubPart();
			wall.setPart(square);
		}

		int partindex = 0;
		box.getSubParts().get(partindex++).setOrientation(new Vector3f(0, -1, 0), new Vector3f(0, -1, 0), Math.PI);
		box.getSubParts().get(partindex++).setOrientation(new Vector3f(-1, 0, 0), new Vector3f(-1, 0, 0), Math.PI);
		box.getSubParts().get(partindex++).setOrientation(new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), Math.PI);
		box.getSubParts().get(partindex++).setOrientation(new Vector3f(0, 0, -1), new Vector3f(0, 0, -1), Math.PI);
		box.getSubParts().get(partindex++).setOrientation(new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), Math.PI);
		box.getSubParts().get(partindex++).setOrientation(new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), Math.PI);

		box.setBoundingBox(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));

		info("box created");

		return box;
	}

	private CTApplication loadApplication(CTApplication cts, String applicationname) throws IOException {
		String s = loadATestApplication(applicationname);
		cts.setApplication(s);
		cts.setName(applicationname);
		assertNotNull(cts.getLines());
		assertNotEquals(0, cts.getLines());
		return cts;
	}
}
