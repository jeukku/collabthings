package org.libraryofthings.unittests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOT3DModel;
import org.libraryofthings.model.impl.LOT3DModelImpl;
import org.xml.sax.SAXException;

import waazdoh.client.model.WData;
import waazdoh.util.ConditionWaiter;
import waazdoh.util.MStringID;
import waazdoh.util.xml.XML;

public final class Test3DModel extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTClient env = getNewClient();
		assertNotNull(env);
		//
		LOT3DModel am = new LOT3DModelImpl(env);
		am.setName("TEST");
		am.setTranslation(new LVector(1, 0, 1));
		am.setScale(10);

		am.publish();
		//
		LOTClient benv = getNewClient();
		assertNotNull(benv);
		LOT3DModelImpl bm = new LOT3DModelImpl(benv);
		bm.load(am.getID().getStringID());
		assertEquals(am.getName(), bm.getName());
		assertEquals(am, bm);
	}

	public void testSaveAndLoadBinary() throws IOException, SAXException {
		LOTClient env = getNewClient(true);
		assertNotNull(env);
		LOTClient benv = getNewClient(true);
		assertNotNull(benv);
		benv.getBinarySource().waitUntilReady();
		//
		LOT3DModelImpl s = new LOT3DModelImpl(env);
		s.setName("TEST");
		String testbinarydatastring = "TESTIBINARYDATA";
		s.getBinary().add(new String(testbinarydatastring).getBytes());
		s.getBinary().setReady();
		s.publish();
		//
		LOT3DModel bs = new LOT3DModelImpl(benv);
		bs.load(s.getID().getStringID());
		assertEquals(s.getName(), bs.getName());
		//
		waitObject(bs);
		//
		String sdata = readString(bs.getBinary().getInputStream());
		assertEquals(testbinarydatastring, sdata);
	}

	public void testFailBinary() throws IOException, SAXException {
		LOTClient env = getNewClient();
		try {
			LOT3DModelImpl m = new LOT3DModelImpl(env);
			m.load(new MStringID());
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
	}

	public void testImport() throws IOException, SAXException {

		LOTClient env = getNewClient();
		LOT3DModelImpl m = new LOT3DModelImpl(env);
		assertTrue(m.importModel(getClass().getResourceAsStream(cubemodelpath)));
		new ConditionWaiter(() -> m.isReady(), 5000);
		//
		assertTrue(m.isReady());
		assertTrue(!m.getChildBinaries().isEmpty());
		//
		InputStream is = m.getModelStream();
		WData b = new WData(new XML(new InputStreamReader(is)));
		WData imgtxt = b.find("ImageTexture");
		String nurl = imgtxt.getAttribute("url");
		assertNotNull(nurl);
		// make sure binary is saved to disk.
		env.getBinarySource().clearMemory(0);
		assertTrue("file should exist " + nurl, new File(nurl).exists());
	}
}
