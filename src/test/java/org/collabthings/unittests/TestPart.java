package org.collabthings.unittests;

import java.io.IOException;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.math.LVector;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.model.impl.CTPartImpl;
import org.xml.sax.SAXException;

import waazdoh.common.MStringID;
import waazdoh.common.WObject;

public final class TestPart extends CTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTPart part = new CTPartImpl(env);
		part.setName("testing changing name");
		part.save();
		//
		CTBinaryModel m = part.newBinaryModel();
		m.setType("test");
		String testbinarydatastring = "TESTIBINARYPARTDATA";
		m.getBinary().add(new String(testbinarydatastring).getBytes());
		//
		CTSubPart subpart = part.newSubPart();
		subpart.getPart();
		//
		part.save();
		part.publish();
		//
		CTClient benv = getNewClient(true);
		assertNotNull(benv);
		MStringID bpartid = part.getID().getStringID();
		CTPart bpart = benv.getObjectFactory().getPart(bpartid);
		assertEquals(part.getName(), bpart.getName());
		waitObject(bpart);
		//
		assertEquals(m, bpart.getModel());
		//
		CTSubPart bsubpart = bpart.getSubParts().get(0);
		assertEquals(bsubpart.getPart().getID(), subpart.getPart().getID());
		assertEquals(subpart.toString(), bsubpart.toString());
		//
		assertEquals(bpart.getObject().toText(),
				benv.getObjectFactory().getPart(bpart.getID().getStringID()).getObject().toText());

		assertEquals(bpart, benv.getObjectFactory().getPart(bpart.getID().getStringID()));
	}

	public void testPublishAndSearch() {
		String search = "searchtest";
		for (int c = 0; c < 20; c++) {
			search = search + (char) ('a' + (int) (Math.random() * 20));
		}

		CTClient c = getNewClient();
		CTPart part = new CTPartImpl(c);
		String name = search + " " + System.currentTimeMillis();
		part.setName("testing changing name " + name);
		part.publish();
		//
		CTClient c2 = getNewClient();
		List<String> items = c2.getService().getObjects().search(search, 0, 100);
		assertTrue(items.size() > 0);
		assertTrue(items.contains(part.getID().toString()));
	}

	public CTPart testSubPartOrientation() throws IOException, SAXException {
		CTClient e = getNewClient();
		CTPartImpl p = new CTPartImpl(e);
		CTSubPart subpart = p.newSubPart();
		subpart.setPart(new CTPartImpl(e));
		subpart.setOrientation(new LVector(10, 10, 10), new LVector(0, 1, 0), 1);
		//
		assertEquals(subpart.getLocation().toString(), new LVector(10, 10, 10).toString());
		assertEquals(subpart.getNormal().toString(), new LVector(0, 1, 0).toString());

		return p;
	}

	public void testLoadRandomID() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNull(e.getObjectFactory().getPart(new MStringID()));
	}

	public void testParseFalseBean() throws IOException, SAXException {
		CTClient e = getNewClient();
		CTPartImpl p = (CTPartImpl) e.getObjectFactory().getPart();
		WObject bean = new WObject("part");
		bean.addValue("parts", 0);
		boolean success = p.parse(bean);
		assertFalse(success);
	}

	public void testBoundingBox() {
		CTClient c = getNewClient();
		CTPart p = new CTPartImpl(c);
		LVector av = new LVector(-1, -1, -1);
		LVector bv = new LVector(1, 1, 1);
		p.setBoundingBox(av, bv);
		p.publish();
		//
		CTPart pb = getNewClient().getObjectFactory().getPart(p.getID().getStringID());
		CTBoundingBox bounding = pb.getBoundingBox();
		assertEquals(bounding.getA(), av);
		assertEquals(bounding.getB(), bv);
	}

	public void testImportModel() throws IOException, SAXException {
		CTClient e = getNewClient();
		CTPart p = e.getObjectFactory().getPart();
		CTBinaryModel bm = p.newBinaryModel();
		bm.importModel(CTBinaryModel.TYPE_X3D, getClass().getResourceAsStream(cubemodelpath));
		assertNotNull(p.getModel());
		assertTrue(bm.getBinary().length() > 0);
	}

	public void testEqualPart() {
		CTClient client = getNewClient();
		CTPart a = client.getObjectFactory().getPart();
		CTPart b = client.getObjectFactory().getPart();
		CTPart c = client.getObjectFactory().getPart();

		setupSubparts(a, c);
		setupSubparts(b, c);

		assertFalse(a.isAnEqualPart(c));
		assertFalse(b.isAnEqualPart(c));
		assertTrue(a.isAnEqualPart(b));
		assertTrue(b.isAnEqualPart(a));
	}

	private void setupSubparts(CTPart a, CTPart c) {
		for (int i = 0; i < 10; i++) {
			CTSubPart newSubPart = a.newSubPart();
			newSubPart.setPart(c);
			newSubPart.setOrientation(new LVector(i, i, i), new LVector(i, i, i), 1);
		}
	}
}
