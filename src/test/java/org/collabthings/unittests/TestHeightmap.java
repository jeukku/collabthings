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
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.model.CTValues;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTPartImpl;
import org.collabthings.simulation.CTSimpleSimulation;
import org.collabthings.simulation.CTSimulation;
import org.xml.sax.SAXException;

public final class TestHeightmap extends CTTestCase {

	private static final int MAX_RUNTIME = 3000;

	public void testSaveAndLoad() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTPart part = new CTPartImpl(env);
		CTHeightmap hm = part.newHeightmap();
		hm.setName("hm");
		part.setName("testing hm model");

		hm.setApplication(loadATestFile("scad/test.scad"));

		CTTriangleMesh tm = hm.getTriangleMesh();
		assertNotNull(tm);

		part.publish();

		String partid = part.getID().toString();

		CTClient benv = getNewClient(true);
		assertNotNull(benv);
		WStringID bpartid = part.getID().getStringID();
		CTPart bpart = benv.getObjectFactory().getPart(bpartid);
		CTHeightmap bhm = bpart.getHeightmap();
		assertNotNull(bhm);

		assertEquals(hm.getName(), bhm.getName());
		waitObject(bhm);

		CTTriangleMesh btm = bhm.getTriangleMesh();
		assertNotNull(btm);
		assertEquals(hm.getApplication(), bhm.getApplication());

		String ascadyaml = hm.getObject().toYaml();
		String bscadyaml = bhm.getObject().toYaml();
		assertEquals(ascadyaml, bscadyaml);

		assertEquals(hm.getObject().toYaml(), bhm.getObject().toYaml());
		assertEquals(bpartid, partid);

		assertEquals(tm.getVectors().size(), btm.getVectors().size());
		assertEquals(tm.getTriangles().size(), btm.getTriangles().size());
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

		CTApplication app = client.getObjectFactory().getApplication();
		CTRunEnvironment runenv = fs.getRunEnvironment();
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
