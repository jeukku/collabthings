package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;

public final class TestPartBuilder extends CTTestCase {

	public void testSaveAndLoad() throws IOException {
		CTClient client = getNewClient();
		assertNotNull(client);
		//
		CTPartBuilder pba = client.getObjectFactory().getPartBuilder();
		assertNotNull(pba);

		String name = "PartBuilderTest";
		pba.setName(name);

		String stext = loadATestApplication("partbuilder/test.yml");
		CTApplication s = client.getObjectFactory().getApplication();
		s.setApplication(stext);
		pba.setApplication(s);

		pba.publish();

		//
		clientb = getNewClient();

		CTPartBuilder pbb = clientb.getObjectFactory().getPartBuilder(pba.getID().getStringID());
		log.info("pba " + pba.getApplication().getObject().toText());
		log.info("pbb " + pbb.getApplication().getObject().toText());
		
		assertEquals(pba.getApplication().getObject().toText(), pbb.getApplication().getObject().toText());
		assertEquals(pba.getObject().toText(), pbb.getObject().toText());

		CTPart p = clientb.getObjectFactory().getPart();
		boolean success = pbb.run(p);
		assertNull(pbb.getError());
		assertTrue(success);
		assertEquals(name, pbb.getName());
		assertEquals(1, p.getSubParts().size());
	}

}
