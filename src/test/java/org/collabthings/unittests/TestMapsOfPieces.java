package org.collabthings.unittests;

import java.io.IOException;
import java.util.Date;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTMapOfPieces;
import org.collabthings.model.CTMapOfPieces.CTMapPieceType;
import org.collabthings.model.CTMapPiece;
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
		CTMapOfPieces bmap = publishAndCompare();
		assertEquals(map.getName(), bmap.getName());
	}

	public void testAddPiece() throws IOException, SAXException {
		CTMapPieceType typeAA = map.getPieceType("AA");
		CTMapPieceType typeBB = map.getPieceType("BB");
		typeBB.getObject().addValue("test", "" + new Date());
		CTMapPiece p = map.addPiece(typeAA);
		assertNotNull(p);

		CTMapPiece link = p.newLink(map.addPiece(typeAA));
		assertNotNull(link);

		CTMapOfPieces bmap = publishAndCompare();
		assertTrue(bmap.getObject().toText().contains("BB"));
	}

	private CTMapOfPieces publishAndCompare() {
		map.publish();
		log.info("published a map \n" + map.getObject().toYaml());

		CTClient benv = getNewClient(false);
		assertNotNull(benv);
		WStringID bmapid = map.getID().getStringID();
		CTMapOfPieces bmap = benv.getObjectFactory().getMapOfPieces(bmapid);

		log.info("got a map\n" + bmap.getObject().toYaml());

		waitObject(bmap);
		assertEquals(map.getObject().toText(), bmap.getObject().toText());

		return bmap;
	}
}
