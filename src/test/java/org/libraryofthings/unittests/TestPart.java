package org.libraryofthings.unittests;

import java.io.File;
import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;
import org.xml.sax.SAXException;

import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public final class TestPart extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTClient env = getNewEnv();
		assertNotNull(env);
		//
		LOTPart part = new LOTPart(env);
		part.setName("testing changing name");
		part.getServiceObject().save();
		//
		part.newModel();
		String testbinarydatastring = "TESTIBINARYPARTDATA";
		part.getModel().getBinary()
				.add(new String(testbinarydatastring).getBytes());
		//
		LOTSubPart subpart = part.newSubPart();

		//
		part.save();
		part.publish();
		//
		LOTClient benv = getNewEnv();
		assertNotNull(benv);
		MStringID bpartid = part.getServiceObject().getID().getStringID();
		LOTPart bpart = benv.getObjectFactory().getPart(bpartid);
		assertEquals(part.getName(), bpart.getName());
		waitObject(bpart);
		//
		String sdata = new String(bpart.getModel().getBinary().asByteBuffer());
		assertEquals(testbinarydatastring, sdata);
		//
		LOTSubPart bsubpart = part.getSubParts().get(0);
		assertEquals(bsubpart.getPart().getServiceObject().getID(), subpart
				.getPart().getServiceObject().getID());
		assertEquals(subpart.toString(), bsubpart.toString());
		//
		assertEquals(
				bpart,
				benv.getObjectFactory().getPart(
						bpart.getServiceObject().getID().getStringID()));
	}

	public void testSubPartOrientation() throws IOException, SAXException {
		LOTClient e = getNewEnv();
		LOTPart p = new LOTPart(e);
		LOTSubPart subpart = p.newSubPart();
		subpart.setPart(new LOTPart(e));
		subpart.setOrientation(new LVector(10, 10, 10), new LVector(0, 1, 0));
		//
		assertEquals(subpart.getLocation().toString(),
				new LVector(10, 10, 10).toString());
		assertEquals(subpart.getNormal().toString(),
				new LVector(0, 1, 0).toString());
	}

	public void testSubPartOrientation2() throws IOException, SAXException {
		LOTClient e = getNewEnv();
		LOTPart p = new LOTPart(e);
		LOTSubPart subpart = new LOTSubPart(p, e);
		subpart.setPart(new LOTPart(e));
		subpart.setOrientation(new LVector(10, 10, 10), new LVector(0, 1, 0));
		LOTSubPart bsubpart = p.addSubPart(subpart);
		//
		assertReallyClose(subpart.getLocation(), bsubpart.getLocation());
		assertReallyClose(subpart.getNormal(), bsubpart.getNormal());
	}

	public void testLoadRandomID() throws IOException, SAXException {
		LOTClient e = getNewEnv();
		assertNull(e.getObjectFactory().getPart(new MStringID()));
	}

	public void testParseFalseBean() throws IOException, SAXException {
		LOTClient e = getNewEnv();
		LOTPart p = e.getObjectFactory().getPart();
		JBean bean = new JBean("part");
		bean.add("parts");
		p.parseBean(bean);
	}

	public void testImportModel() throws IOException, SAXException {
		LOTClient e = getNewEnv();
		LOTPart p = e.getObjectFactory().getPart();
		p.importModel(new File("src/test/resources/models/cube.x3d"));
		assertNotNull(p.getModel());
		assertTrue(p.getModel().getBinary().length() > 0);
	}
}
