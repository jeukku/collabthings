package org.collabthings.unittests;

import static org.collabthings.application.CTApplicationLines.part;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.application.CTApplicationLines;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTConnector;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTVectorGroup;

public final class TestConnectors extends CTTestCase {

	public void testSaveAndLoad() {
		CTClient client = getNewClient();
		CTConnector con = client.getObjectFactory().getConnector();
		con.getApplication().addLine(CTApplicationLines.envSet("test", "test"));
		con.publish();
		
		CTClient clientb = getNewClient();
		CTConnector conb = clientb.getObjectFactory().getConnector(con.getID().getStringID());

		assertEquals(con.getObject().toText(), conb.getObject().toText());
	}

	public void testSubpartConnector() {
		CTClient client = getNewClient();
		CTPart parta = client.getObjectFactory().getPart();

		parta.newSubPart().setName("a");
		parta.newSubPart().setName("b");

		String conn = "conn";
		parta.addVectorGroup(conn);
		CTVectorGroup avg = parta.getVectorGroup(conn);
		avg.addVector().set(0, 0, 0);
		avg.addVector().set(1, 0, 0);

		CTConnector acon = parta.addSubpartConnector("a->b");
		CTApplication aconapp = acon.getApplication();
		aconapp.addLine(part().getSub("a").location().env("loc_a"));

		parta.publish();

		CTClient clientb = getNewClient();
		CTPart partb = clientb.getObjectFactory().getPart(parta.getID().getStringID());
		assertNotNull(partb);
		assertEquals(parta.getObject().toText(), partb.getObject().toText());

		assertNotNull(partb.getVectorGroup(conn));
		assertEquals(2, partb.getVectorGroup(conn).size());
		assertEquals(parta.getVectorGroup(conn).get(0), partb.getVectorGroup(conn).get(0));
	}
}
