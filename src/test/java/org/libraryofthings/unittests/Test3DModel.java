package org.libraryofthings.unittests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.model.LOT3DModel;
import org.libraryofthings.model.impl.LOT3DModelImpl;
import org.xml.sax.SAXException;

import waazdoh.client.model.JBean;
import waazdoh.util.ConditionWaiter;
import waazdoh.util.MStringID;
import waazdoh.util.xml.XML;

public final class Test3DModel extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTClient env = getNewClient();
		assertNotNull(env);
		//
		LOT3DModel s = new LOT3DModelImpl(env);
		s.setName("TEST");
		s.publish();
		//
		LOTClient benv = getNewClient();
		assertNotNull(benv);
		LOT3DModelImpl bs = new LOT3DModelImpl(benv);
		bs.load(s.getID().getStringID());
		assertEquals(s.getName(), bs.getName());
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
		String sdata = new String(bs.getBinary().asByteBuffer());
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
		assertTrue(m
				.importModel(new File("src/test/resources/models/cube.x3d")));
		new ConditionWaiter(() -> m.isReady(), 5000);
		//
		assertTrue(m.isReady());
		assertTrue(!m.getChildBinaries().isEmpty());
		//
		InputStream is = m.getModelStream();
		JBean b = new JBean(new XML(new InputStreamReader(is)));
		JBean imgtxt = b.find("ImageTexture");
		String nurl = imgtxt.getAttribute("url");
		assertNotNull(nurl);
		// make sure binary is saved to disk.
		env.getBinarySource().clearMemory(0);
		assertTrue("file should exist " + nurl, new File(nurl).exists());
	}
}
