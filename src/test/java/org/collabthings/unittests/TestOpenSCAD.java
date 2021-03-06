package org.collabthings.unittests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.application.CTApplicationRunner;
import org.collabthings.core.utils.WTimedFlag;
import org.collabthings.datamodel.WStringID;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTValues;
import org.collabthings.model.impl.CTApplicationImpl;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTPartImpl;
import org.collabthings.simulation.CTSimpleSimulation;
import org.collabthings.simulation.CTSimulation;
import org.xml.sax.SAXException;

public final class TestOpenSCAD extends CTTestCase {

	private static final int MAX_RUNTIME = 3000;

	public void testSaveAndLoad() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTPart part = new CTPartImpl(env);
		CTOpenSCAD scad = part.newSCAD();
		scad.setName("scad");
		part.setName("testing scad model");

		scad.setApplication(loadATestFile("scad/test.scad"));

		CTModel m = scad.getModel();
		assertNotNull(m);

		part.publish();

		String partid = part.getID().toString();

		CTClient benv = getNewClient(true);
		assertNotNull(benv);
		WStringID bpartid = part.getID().getStringID();
		CTPart bpart = benv.getObjectFactory().getPart(bpartid);
		assertNotNull(bpart);

		assertEquals(part.getName(), bpart.getName());
		waitObject(bpart);

		CTOpenSCAD bscad = (CTOpenSCAD) bpart.getModel();
		assertNotNull(bscad);
		assertEquals(scad.getApplication(), bscad.getApplication());

		String ascadyaml = scad.getObject().toYaml();
		String bscadyaml = bscad.getObject().toYaml();
		assertEquals(ascadyaml, bscadyaml);

		assertEquals(part.getObject().toYaml(), bpart.getObject().toYaml());
		assertEquals(bpartid, partid);

		assertEquals(720, bscad.getModel().getTriangleMesh().getVectors().size());
		assertEquals(240, bscad.getModel().getTriangleMesh().getTriangles().size());
	}

	public void testGear() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTPart part = new CTPartImpl(env);
		CTOpenSCAD scad = part.newSCAD();
		scad.setName("gear");
		part.setName("testing gear model");

		scad.setApplication(loadATestFile("scad/gears_helical.scad"));

		CTModel m = scad.getModel();
		assertNotNull(m);

		part.publish();
	}

	public void testView() throws FileNotFoundException, IOException {
		CTClient client = getNewClient(true);
		assertNotNull(client);
		CTEnvironment env = new CTEnvironmentImpl(client);

		CTFactory f = client.getObjectFactory().getFactory();
		CTFactoryState fs = new CTFactoryState(client, env, "test", f);
		CTOpenSCAD scad = fs.newPart().getPart().newSCAD();
		scad.setApplication(loadATestFile("scad/test.scad"));

		Map<String, String> map = new HashMap<String, String>();

		CTRunEnvironment runenv = fs.getRunEnvironment();
		CTApplication app = new CTApplicationImpl(client);

		runenv.addTask(new CTApplicationRunner(app) {
			@Override
			public boolean run(CTRunEnvironment rune, CTValues values) {
				map.put("run", "true");
				new WTimedFlag(1000).waitTimer();
				return true;
			}
		});

		CTSimulation simulation = new CTSimpleSimulation(runenv);
		simulation.run(MAX_RUNTIME);

		assertEquals("true", map.get("run"));
	}
}
