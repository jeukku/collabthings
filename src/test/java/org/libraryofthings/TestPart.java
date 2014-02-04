package org.libraryofthings;

import java.io.IOException;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;
import org.xml.sax.SAXException;

public final class TestPart extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOTPart part = new LOTPart(env);
		part.setName("testing changing name");
		assertTrue(part.getServiceObject().save());
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
		LOTPart bpart = benv.getObjectFactory().getPart(
				part.getServiceObject().getID().getStringID());
		assertEquals(bpart.getName(), part.getName());
		waitObject(bpart);
		//
		String sdata = new String(bpart.getModel().getBinary().asByteBuffer());
		assertEquals(testbinarydatastring, sdata);
		//
		LOTSubPart bsubpart = part.getSubParts().get(0);
		assertEquals(bsubpart.getPart().getServiceObject().getID(), subpart
				.getPart().getServiceObject().getID());
		
		assertEquals(bpart, benv.getObjectFactory().getPart(bpart.getServiceObject().getID().getStringID()));
	}

}
