package org.collabthings.unittests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.impl.CT3DModelImpl;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import waazdoh.client.utils.ConditionWaiter;
import waazdoh.common.MStringID;
import waazdoh.common.WData;
import waazdoh.common.XML;

public final class Test3DModel extends CTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		CTClient env = getNewClient();
		assertNotNull(env);
		//
		CTBinaryModel am = new CT3DModelImpl(env);
		am.setName("TEST");
		am.setTranslation(new Vector3f(1, 0, 1));
		am.setScale(10);

		log.info("amodel " + am.getObject().toText());
		am.publish();
		log.info("amodel " + am.getObject().toText());

		CTClient benv = getNewClient();
		assertNotNull(benv);
		CT3DModelImpl bm = new CT3DModelImpl(benv);
		bm.load(am.getID().getStringID());
		assertEquals(am.getName(), bm.getName());
		log.info("amodel " + am.getObject().toText());
		log.info("bmodel " + bm.getObject().toText());

		assertEquals(am.getObject().toText(), bm.getObject().toText());
		assertEquals(am, bm);
	}

	public void testSaveAndLoadBinary() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		CTClient benv = getNewClient(true);
		assertNotNull(benv);
		benv.getBinarySource().waitUntilReady();
		//
		CT3DModelImpl s = new CT3DModelImpl(env);
		s.setName("TEST");
		s.setType("test");
		String testbinarydatastring = "TESTIBINARYDATA";
		s.getBinary().add(new String(testbinarydatastring).getBytes());
		s.getBinary().setReady();
		s.publish();
		//
		CTBinaryModel bs = new CT3DModelImpl(benv);
		bs.load(s.getID().getStringID());
		assertEquals(s.getName(), bs.getName());
		//
		waitObject(bs);
		//
		String sdata = readString(bs.getBinary().getInputStream());
		assertEquals(testbinarydatastring, sdata);
	}

	public void testFailBinary() throws IOException, SAXException {
		CTClient env = getNewClient();
		try {
			CT3DModelImpl m = new CT3DModelImpl(env);
			m.load(new MStringID());
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
	}

	public void testImport() throws IOException, SAXException {

		CTClient env = getNewClient();
		CT3DModelImpl m = new CT3DModelImpl(env);
		assertTrue(m.importModel("x3d", getClass().getResourceAsStream(cubemodelpath)));
		ConditionWaiter.wait(() -> m.isReady(), 5000);
		//
		assertTrue(m.isReady());
		assertTrue(!m.getChildBinaries().isEmpty());
		//
		InputStream is = new FileInputStream(m.getModelFile());
		WData b = new WData(new XML(new InputStreamReader(is)));
		WData imgtxt = b.find("ImageTexture");
		String nurl = imgtxt.getAttribute("url");
		assertNotNull(nurl);
		// make sure binary is saved to disk.
		env.getBinarySource().clearMemory(0);
		assertTrue("file should exist " + nurl, new File(nurl).exists());
	}
}
