package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.impl.LOTPartImpl;
import org.xml.sax.SAXException;

import waazdoh.client.model.WData;
import waazdoh.util.MStringID;

public final class TestPart extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTClient env = getNewClient(true);
		assertNotNull(env);
		//
		LOTPart part = new LOTPartImpl(env);
		part.setName("testing changing name");
		part.save();
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
		LOTClient benv = getNewClient(true);
		assertNotNull(benv);
		MStringID bpartid = part.getID().getStringID();
		LOTPart bpart = benv.getObjectFactory().getPart(bpartid);
		assertEquals(part.getName(), bpart.getName());
		waitObject(bpart);
		//
		String sdata = readString(bpart.getModel().getBinary().getInputStream());
		assertEquals(testbinarydatastring, sdata);
		//
		LOTSubPart bsubpart = part.getSubParts().get(0);
		assertEquals(bsubpart.getPart().getID(), subpart.getPart().getID());
		assertEquals(subpart.toString(), bsubpart.toString());
		//
		assertEquals(bpart,
				benv.getObjectFactory().getPart(bpart.getID().getStringID()));
	}

	public void testSubPartOrientation() throws IOException, SAXException {
		LOTClient e = getNewClient();
		LOTPartImpl p = new LOTPartImpl(e);
		LOTSubPart subpart = p.newSubPart();
		subpart.setPart(new LOTPartImpl(e));
		subpart.setOrientation(new LVector(10, 10, 10), new LVector(0, 1, 0));
		//
		assertEquals(subpart.getLocation().toString(),
				new LVector(10, 10, 10).toString());
		assertEquals(subpart.getNormal().toString(),
				new LVector(0, 1, 0).toString());
	}

	public void testLoadRandomID() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNull(e.getObjectFactory().getPart(new MStringID()));
	}

	public void testParseFalseBean() throws IOException, SAXException {
		LOTClient e = getNewClient();
		LOTPartImpl p = (LOTPartImpl) e.getObjectFactory().getPart();
		WData bean = new WData("part");
		bean.add("parts");
		p.parseBean(bean);
	}

	public void testBoundingBox() {
		LOTClient c = getNewClient();
		LOTPart p = new LOTPartImpl(c);
		LVector av = new LVector(-1, -1, -1);
		LVector bv = new LVector(1, 1, 1);
		p.setBoundingBox(av, bv);
		p.publish();
		//
		LOTPart pb = getNewClient().getObjectFactory().getPart(p.getID().getStringID());
		LOTBoundingBox bounding = pb.getBoundingBox();
		assertEquals(bounding.getA(), av);
		assertEquals(bounding.getB(), bv);
	}

	public void testImportModel() throws IOException, SAXException {
		LOTClient e = getNewClient();
		LOTPart p = e.getObjectFactory().getPart();
		p.importModel(getClass().getResourceAsStream(cubemodelpath));
		assertNotNull(p.getModel());
		assertTrue(p.getModel().getBinary().length() > 0);
	}

	public void testEqualPart() {
		LOTClient client = getNewClient();
		LOTPart a = client.getObjectFactory().getPart();
		LOTPart b = client.getObjectFactory().getPart();
		LOTPart c = client.getObjectFactory().getPart();

		setupSubparts(a, c);
		setupSubparts(b, c);
		//
		assertFalse(a.isAnEqualPart(c));
		assertFalse(b.isAnEqualPart(c));
		assertTrue(a.isAnEqualPart(b));
		assertTrue(b.isAnEqualPart(a));
	}

	private void setupSubparts(LOTPart a, LOTPart c) {
		for (int i = 0; i < 10; i++) {
			LOTSubPart newSubPart = a.newSubPart();
			newSubPart.setPart(c);
			newSubPart.setOrientation(new LVector(i, i, i),
					new LVector(i, i, i));
		}
	}
}
