package org.libraryofthings;

import java.io.IOException;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;
import org.xml.sax.SAXException;

import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public final class TestPart extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
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
		LOTEnvironment benv = getNewEnv();
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

		assertEquals(
				bpart,
				benv.getObjectFactory().getPart(
						bpart.getServiceObject().getID().getStringID()));
	}

	public void testLoadRandomID() throws IOException, SAXException {
		try {
			LOTEnvironment e = getNewEnv();
			e.getObjectFactory().getPart(new MStringID());
		} catch (Exception e) {
			assertNotNull(e);
		}
	}

	public void testParseFalseBean() throws IOException, SAXException {
		LOTEnvironment e = getNewEnv();
		LOTPart p = e.getObjectFactory().getPart();
		JBean bean = new JBean("part");
		bean.add("parts");
		p.parseBean(bean);
	}
}
