package org.collabthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

public final class TestFactory extends LOTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		LOTClient env = getNewClient();
		assertNotNull(env);
		//
		env.getObjectFactory().getFactory();
		LOTFactory f = env.getObjectFactory().getFactory();
		assertNotNull(f);
		f.save();
		//
		assertNotNull(env.getObjectFactory()
				.getFactory(f.getID().getStringID()));
		assertEquals(f,
				env.getObjectFactory().getFactory(f.getID().getStringID()));
	}

	public void testSaveAndLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTClient env = getNewClient(true);
		assertNotNull(env);
		//
		LOTFactory f = env.getObjectFactory().getFactory();
		f.setName("testing changing name");
		f.save();
		//
		LOTScript lotScript = f.addScript("test");
		lotScript
				.setScript("function info() { return \"testing tool script\"; }");
		// model
		f.setModel(env.getObjectFactory().getModel());
		//
		f.setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));
		f.setToolUserSpawnLocation(new LVector(4, 4, 4));
		//
		f.save();
		f.publish();
		//
		LOTClient benv = getNewClient(true);
		assertNotNull(benv);
		LOTFactory bfact = benv.getObjectFactory().getFactory(
				f.getID().getStringID());
		assertEquals(bfact.getName(), f.getName());
		waitObject(bfact);
		//
		LOTScript bscript = bfact.getScript("test");
		assertNotNull(bscript);
		assertEquals(lotScript.getScript(), bscript.getScript());
		//
		assertEquals(bfact.getBoundingBox().getA(), f.getBoundingBox().getA());
		assertEquals(bfact.getBoundingBox().getB(), f.getBoundingBox().getB());
		//
		assertEquals(f.getToolUserSpawnLocation(),
				bfact.getToolUserSpawnLocation());

		assertEquals(f.getBean().toText(), bfact.getBean().toText());
		assertEquals(f.hashCode(), bfact.hashCode());
	}

	public void testNotEqual() {
		LOTClient ac = getNewClient();
		LOTFactory af = ac.getObjectFactory().getFactory();
		af.publish();
		LOTClient bc = getNewClient();
		LOTFactory bf = bc.getObjectFactory().getFactory(
				af.getID().getStringID());

		LLog.getLogger(af).info(af.getBean().toText());
		LLog.getLogger(bf).info(bf.getBean().toText());
		LLog.getLogger(af).info(af.printOut().toText());
		LLog.getLogger(bf).info(bf.printOut().toText());

		assertEquals(af.getBean().toText(), bf.getBean().toText());
		assertNotSame(af, bf);
		assertTrue(af.getBean().equals(bf.getBean()));
		af.setName("test");
		assertFalse(af.getBean().equals(bf.getBean()));
		bf.setName("test");
		assertEquals(af, bf);
		assertTrue(af.getBean().equals(bf.getBean()));
	}

	public void testChildFactory() {
		LOTClient c = getNewClient();
		LOTFactory f = c.getObjectFactory().getFactory();
		String childfactoryid = "testchildfactory";
		LOTFactory childf = f.addFactory(childfactoryid).getFactory();
		String childfactoryname = "some child factory";
		childf.setName(childfactoryname);
		childf.addScript("testscript");
		f.publish();
		//
		LOTClient bc = getNewClient();
		LOTFactory bf = bc.getObjectFactory().getFactory(
				f.getID().getStringID());
		assertEquals(f.getBean().toText(), bf.getBean().toText());
		LOTFactory bchildf = bf.getFactory(childfactoryid).getFactory();
		assertNotNull(bchildf);
		assertEquals(childfactoryname, bchildf.getName());
		assertEquals(childf.getBean().toText(), bchildf.getBean().toText());
	}

	public void testBoundingBox() {
		LOTClient c = getNewClient();
		LOTFactory f = c.getObjectFactory().getFactory();
		f.setBoundingBox(new LVector(-10, 0, -10), new LVector(10, 1, 10));
		f.publish();
		LOTClient bc = getNewClient();
		LOTFactory bf = bc.getObjectFactory().getFactory(
				f.getID().getStringID());
		LOTBoundingBox bbox = bf.getBoundingBox();
		assertEquals(f.getBoundingBox().getBean().toText(), bbox.getBean()
				.toText());
	}

	public void testBoundingBoxInstance() {
		LOTClient c = getNewClient();
		LOTFactory f = c.getObjectFactory().getFactory();
		LVector va = f.getBoundingBox().getA();
		f.setBoundingBox(new LVector(-10, 0, -10), new LVector(10, 1, 10));
		f.setBoundingBox(new LOTBoundingBox(new LVector(), new LVector()));
		assertSame(va, f.getBoundingBox().getA());
	}

	public void testCallUnknownScript() throws IOException, SAXException {
		LOTClient e = getNewClient();
		LOTFactory factory = e.getObjectFactory().getFactory();
		assertNull(factory.getScript("FAIL"));
	}

	public void testAddGetScript() {
		LOTClient c = getNewClient();
		LOTTool tool = c.getObjectFactory().getTool();
		LOTScript s = tool.addScript("testscript");
		assertNotNull(tool.getScript("testscript"));
	}
}
