package org.libraryofthings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.libraryofthings.model.LOT3DModel;
import org.xml.sax.SAXException;

import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;
import waazdoh.cutils.xml.XML;

public final class Test3DModel extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOT3DModel s = new LOT3DModel(env);
		s.setName("TEST");
		s.getServiceObject().publish();
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOT3DModel bs = new LOT3DModel(benv);
		bs.load(s.getServiceObject().getID().getStringID());
		assertEquals(s.getName(), bs.getName());
	}

	public void testSaveAndLoadBinary() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOT3DModel s = new LOT3DModel(env);
		s.setName("TEST");
		String testbinarydatastring = "TESTIBINARYDATA";
		s.getBinary().add(new String(testbinarydatastring).getBytes());
		s.getBinary().setReady();
		s.publish();
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOT3DModel bs = new LOT3DModel(benv);
		bs.load(s.getServiceObject().getID().getStringID());
		assertEquals(s.getName(), bs.getName());
		//
		waitObject(bs);
		//
		String sdata = new String(bs.getBinary().asByteBuffer());
		assertEquals(testbinarydatastring, sdata);
	}

	public void testFailBinary() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		try {
			LOT3DModel m = new LOT3DModel(env);
			m.load(new MStringID());
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
	}

	public void testImport() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		LOT3DModel m = new LOT3DModel(env);
		assertTrue(m.importModel(new File("src/test/resources/models/cube.x3d")
				.toURI().toURL()));
		assertTrue(m.isReady());
		assertTrue(m.getChildBinaries().size() > 0);
		//
		InputStream is = m.getModelStream();
		JBean b = new JBean(new XML(new InputStreamReader(is)));
		JBean imgtxt = b.find("ImageTexture");
		String nurl = imgtxt.getAttribute("url");
		assertNotNull(nurl);
		assertTrue(new File(nurl).exists());
	}
}
