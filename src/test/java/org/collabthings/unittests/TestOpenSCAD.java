package org.collabthings.unittests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTScriptRunner;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.model.LOTBinaryModel;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTOpenSCAD;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTValues;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.model.impl.LOTPartImpl;
import org.collabthings.simulation.LOTSimpleSimulation;
import org.collabthings.simulation.LOTSimulation;
import org.xml.sax.SAXException;

import waazdoh.common.MStringID;
import waazdoh.common.MTimedFlag;

public final class TestOpenSCAD extends LOTTestCase {

	private static final int MAX_RUNTIME = 3000;

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTClient env = getNewClient(true);
		assertNotNull(env);
		//
		LOTPart part = new LOTPartImpl(env);
		LOTOpenSCAD scad = part.newSCAD();
		scad.setName("testing changing name");

		scad.setScript(loadATestFile("scad/test.scad"));

		LOTBinaryModel m = scad.getModel();
		assertNotNull(m);

		part.publish();

		LOTClient benv = getNewClient(true);
		assertNotNull(benv);
		MStringID bpartid = part.getID().getStringID();
		LOTPart bpart = benv.getObjectFactory().getPart(bpartid);
		assertNotNull(bpart);

		assertEquals(part.getName(), bpart.getName());
		waitObject(bpart);

		LOTOpenSCAD bscad = (LOTOpenSCAD) bpart.getModel();
		assertNotNull(bscad);
		assertEquals(scad.getScript(), bscad.getScript());
	}

	public void testView() throws FileNotFoundException, IOException {
		LOTClient client = getNewClient(true);
		assertNotNull(client);
		LOTEnvironment env = new LOTEnvironmentImpl(client);

		LOTFactory f = client.getObjectFactory().getFactory();
		LOTFactoryState fs = new LOTFactoryState(client, env, "test", f);
		LOTOpenSCAD scad = fs.newPart().getPart().newSCAD();
		scad.setScript(loadATestFile("scad/test.scad"));

		Map<String, String> map = new HashMap<String, String>();

		LOTRunEnvironment runenv = fs.getRunEnvironment();
		runenv.addTask(new LOTScriptRunner() {

			@Override
			public boolean run(LOTValues values) {
				map.put("run", "true");
				new MTimedFlag(1000).waitTimer();
				return true;
			}

			@Override
			public String getError() {
				// TODO Auto-generated method stub
				return null;
			}
		});

		LOTSimulation simulation = new LOTSimpleSimulation(runenv, true);
		simulation.run(MAX_RUNTIME);

		assertEquals("true", map.get("run"));
	}
}
