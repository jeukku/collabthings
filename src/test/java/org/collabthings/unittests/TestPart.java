package org.collabthings.unittests;

import java.io.IOException;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.model.impl.CTPartImpl;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import waazdoh.common.WStringID;
import waazdoh.common.WObject;
import waazdoh.common.vo.ObjectVO;

public final class TestPart extends CTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTPart part = new CTPartImpl(env);
		part.setName("testing changing name");
		part.setShortname("testshortname");
		part.save();
		//
		CTBinaryModel m = part.newBinaryModel();
		m.setType("test");
		String testbinarydatastring = "TESTIBINARYPARTDATA";
		m.setContent(new String(testbinarydatastring).getBytes(CTClient.CHARSET));
		//
		CTSubPart subpart = part.newSubPart();
		subpart.getPart();
		subpart.setName("subpartname");

		CTSubPart subpart2 = part.newSubPart();
		subpart2.set(subpart);
		subpart.setName("subpartname2");

		CTSubPart subsub = subpart2.getPart().newSubPart();
		subsub.getPart().newSubPart().setName("thesub");
		subsub.getPart().getResourceUsage().set("resource", 10.0);

		part.save();
		part.publish();
		//
		CTClient benv = getNewClient(true);
		assertNotNull(benv);
		WStringID bpartid = part.getID().getStringID();
		CTPart bpart = benv.getObjectFactory().getPart(bpartid);
		assertEquals(part.getName(), bpart.getName());
		waitObject(bpart);
		//
		assertEquals(m, bpart.getModel());
		//
		CTSubPart bsubpart = bpart.getSubParts().get(0);
		assertEquals(bsubpart.getPart().getID(), subpart.getPart().getID());
		assertEquals(subpart.toString(), bsubpart.toString());

		assertTrue(bsubpart.getNamePath(), bsubpart.getNamePath().indexOf("testshortname") == 0);
		assertTrue(bsubpart.getNamePath(), bsubpart.getNamePath().indexOf("subpartname") > 0);

		//
		assertEquals(bpart.getObject().toYaml(),
				benv.getObjectFactory().getPart(bpart.getID().getStringID()).getObject().toYaml());

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
		List<ObjectVO> items = c2.getService().getObjects().search(search, 0, 100);
		assertTrue(items.size() > 0);

		String string = part.getID().toString();
		assertEquals(string, items.get(0).getId());
	}

	public CTPart testSubPartOrientation() throws IOException, SAXException {
		CTClient e = getNewClient();
		CTPartImpl p = new CTPartImpl(e);
		CTSubPart subpart = p.newSubPart();
		subpart.setPart(new CTPartImpl(e));
		subpart.setOrientation(new Vector3f(10, 10, 10), new Vector3f(0, 1, 0), 1);
		//
		assertEquals(subpart.getLocation().toString(), new Vector3f(10, 10, 10).toString());
		assertEquals(subpart.getNormal().toString(), new Vector3f(0, 1, 0).toString());

		return p;
	}

	public void testPartBookmark() {
		CTClient e = getNewClient();
		CTPart bookmarkedpart = e.getObjectFactory().getPart();
		String name = "bookmarkedpart" + System.currentTimeMillis();
		bookmarkedpart.setName(name);
		CTPart part = e.getObjectFactory().getPart();
		CTSubPart sp = part.newSubPart();
		assertNotNull(sp.getPart());
		sp.setPartBookmark(name);
		part.publish();
		//
		CTClient b = getNewClient();
		CTPart bpart = b.getObjectFactory().getPart(part.getID().getStringID());
		assertNotNull(bpart);
	}

	public void testLoadRandomID() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNull(e.getObjectFactory().getPart(new WStringID()));
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
		Vector3f av = new Vector3f(-1, -1, -1);
		Vector3f bv = new Vector3f(1, 1, 1);
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
		bm.importModel(CTBinaryModel.VALUE_TYPE_X3D, getClass().getResourceAsStream(cubemodelpath));
		assertNotNull(p.getModel());
		assertTrue(bm.getContent().length > 0);
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
			newSubPart.setOrientation(new Vector3f(i, i, i), new Vector3f(i, i, i), 1);
		}
	}

	public void testResources() {
		CTClient client = getNewClient();
		CTPart a = client.getObjectFactory().getPart();
		CTPart ab = a.newSubPart().getPart();
		ab.getResourceUsage().set("resource1", 1.0);

		CTPart bb = a.newSubPart().getPart();
		bb.getResourceUsage().set("resource2", 2.0);

		CTPart cb = a.newSubPart().getPart();
		cb.newSubPart().getPart().getResourceUsage().set("resource1", 3.0);

		a.publish();

		assertReallyClose(1.0, ab.getResourceUsage().get("resource1"));
		assertReallyClose(2.0, bb.getResourceUsage().get("resource2"));
		assertReallyClose(0.0, cb.getResourceUsage().get("resource1"));
		assertReallyClose(3.0, cb.getResourceUsage().getTotal("resource1"));

		Double res1 = a.getResourceUsage().getTotal("resource1");
		Double res2 = a.getResourceUsage().getTotal("resource2");
		assertReallyClose(4.0, res1);
		assertReallyClose(2.0, res2);
	}
}
