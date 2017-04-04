package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTMapOfPieces;
import org.collabthings.model.CTMapPiece;
import org.collabthings.model.CTMapOfPieces.CTMapPieceType;
import org.xml.sax.SAXException;

import waazdoh.common.WStringID;

public final class TestMapsOfPieces extends CTTestCase {

	private CTClient client;
	private CTMapOfPieces map;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		disableNetwork();

		client = getNewClient(false);
		assertNotNull(client);
		//
		map = client.getObjectFactory().getMapOfPieces();

	}

	public void testSetName() throws IOException, SAXException {
		map.setName("testing mapsofpieces");
		publishAndCompare();
	}

	public void testAddPiece() throws IOException, SAXException {
		CTMapPieceType typeAA = map.addType(CTMapOfPieces.CTMapPieceType.getType("AA"));
		map.setRoot(typeAA);
		CTMapPiece p = map.getRoot().newLink();
		assertNotNull(p);

		publishAndCompare();
	}

	private void publishAndCompare() {
		map.publish();
		log.info("published a map \n" + map.getObject().toYaml());

		CTClient benv = getNewClient(false);
		assertNotNull(benv);
		WStringID bmapid = map.getID().getStringID();
		CTMapOfPieces bmap = benv.getObjectFactory().getMapOfPieces(bmapid);

		waitObject(bmap);
		assertEquals(map.getObject().toText(), bmap.getObject().toText());
	}
}
