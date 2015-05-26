package org.libraryofthings.unittests;

import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.model.LOTBinaryModel;
import org.collabthings.model.LOTOpenSCAD;
import org.collabthings.model.LOTPart;
import org.collabthings.model.impl.LOTPartImpl;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

import waazdoh.common.MStringID;

public final class TestOpenSCAD extends LOTTestCase {

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
}
