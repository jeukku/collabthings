package org.collabthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import waazdoh.common.WStringID;

public final class TestFactory extends CTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		CTClient env = getNewClient();
		assertNotNull(env);
		//
		env.getObjectFactory().getFactory();
		CTFactory f = env.getObjectFactory().getFactory();
		assertNotNull(f);
		f.save();
		//
		assertNotNull(env.getObjectFactory().getFactory(f.getID().getStringID()));
		assertEquals(f, env.getObjectFactory().getFactory(f.getID().getStringID()));
	}

	public void testSaveAndLoad() throws IOException, SAXException, NoSuchMethodException, ScriptException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTFactory f = env.getObjectFactory().getFactory();
		f.setName("testing changing name");
		f.save();
		//
		CTScript ctScript = f.addScript("test");
		ctScript.setScript("function info() { return \"testing tool script\"; }");
		// model
		f.setModel(env.getObjectFactory().getModel());
		//
		f.setBoundingBox(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
		f.setToolUserSpawnLocation(new Vector3f(4, 4, 4));
		//
		f.save();
		f.publish();
		//
		CTClient benv = getNewClient(true);
		assertNotNull(benv);
		CTFactory bfact = benv.getObjectFactory().getFactory(f.getID().getStringID());
		assertEquals(bfact.getName(), f.getName());
		waitObject(bfact);
		//
		CTScript bscript = bfact.getScript("test");
		assertNotNull(bscript);
		assertEquals(ctScript.getScript(), bscript.getScript());
		//
		assertEquals(bfact.getBoundingBox().getA(), f.getBoundingBox().getA());
		assertEquals(bfact.getBoundingBox().getB(), f.getBoundingBox().getB());
		//
		assertEquals(f.getToolUserSpawnLocation(), bfact.getToolUserSpawnLocation());

		assertEquals(f.getObject().toYaml(), bfact.getObject().toYaml());
		assertEquals(f.hashCode(), bfact.hashCode());
	}

	public void testNotEqual() {
		CTClient ac = getNewClient();
		CTFactory af = ac.getObjectFactory().getFactory();
		af.publish();
		CTClient bc = getNewClient();
		CTFactory bf = bc.getObjectFactory().getFactory(af.getID().getStringID());

		LLog.getLogger(af).info(af.getObject().toYaml());
		LLog.getLogger(bf).info(bf.getObject().toYaml());
		LLog.getLogger(af).info(af.printOut().toText());
		LLog.getLogger(bf).info(bf.printOut().toText());

		assertEquals(af.getObject().toYaml(), bf.getObject().toYaml());
		assertNotSame(af, bf);
		assertTrue(af.getObject().equals(bf.getObject()));
		af.setName("test");
		assertFalse(af.getObject().equals(bf.getObject()));
		bf.setName("test");
		assertEquals(af, bf);
		assertTrue(af.getObject().equals(bf.getObject()));
	}

	public void testBookmarkChildFactory() {
		CTClient c = getNewClient();
		CTFactory f = c.getObjectFactory().getFactory();
		String childfactoryid = "testchildfactory";
		CTAttachedFactory addFactory = f.addFactory(childfactoryid);

		String childfactoryname = "some child factory";
		String bookmark = c.getService().getUser().getUsername() + "/factory/" + childfactoryname + "/latest";

		addFactory.setBookmark(bookmark);

		CTFactory childf = addFactory.getFactory();
		childf.setName(childfactoryid);

		childf.setName(childfactoryname);
		childf.addScript("testscript");
		LLog.getLogger(this).info("publishing first " + f.getObject().toYaml());

		f.publish();

		childf.setToolUserSpawnLocation(new Vector3f(1, 1, 1));
		childf.publish();

		CTClient bc = getNewClient();
		String clientausername = c.getService().getUser().getUsername();
		assertNotNull(clientausername);

		String publishedchildfactory = bc.getPublished(bookmark);
		assertNotNull(publishedchildfactory);

		WStringID factoryid = f.getID().getStringID();
		CTFactory bf = bc.getObjectFactory().getFactory(factoryid);

		LLog.getLogger(this).info("first " + f.getObject().toYaml());
		LLog.getLogger(this).info("second " + bf.getObject().toYaml());

		assertEquals(f.getObject().toYaml(), bf.getObject().toYaml());
		CTFactory bchildf = bf.getFactory(childfactoryid).getFactory();
		assertNotNull(bchildf);
		assertEquals(childfactoryname, bchildf.getName());
		assertEquals(childf.getObject().toYaml(), bchildf.getObject().toYaml());
	}

	public void testChildFactory() {
		CTClient c = getNewClient();
		CTFactory f = c.getObjectFactory().getFactory();
		String childfactoryid = "testchildfactory";
		CTFactory childf = f.addFactory(childfactoryid).getFactory();
		String childfactoryname = "some child factory";
		childf.setName(childfactoryname);
		childf.addScript("testscript");
		f.publish();
		//
		CTClient bc = getNewClient();
		CTFactory bf = bc.getObjectFactory().getFactory(f.getID().getStringID());
		assertEquals(f.getObject().toYaml(), bf.getObject().toYaml());
		CTFactory bchildf = bf.getFactory(childfactoryid).getFactory();
		assertNotNull(bchildf);
		assertEquals(childfactoryname, bchildf.getName());
		assertEquals(childf.getObject().toYaml(), bchildf.getObject().toYaml());
	}

	public void testBoundingBox() {
		CTClient c = getNewClient();
		CTFactory f = c.getObjectFactory().getFactory();
		f.setBoundingBox(new Vector3f(-10, 0, -10), new Vector3f(10, 1, 10));
		f.publish();
		CTClient bc = getNewClient();
		CTFactory bf = bc.getObjectFactory().getFactory(f.getID().getStringID());
		CTBoundingBox bbox = bf.getBoundingBox();
		assertEquals(f.getBoundingBox().getBean().toYaml(), bbox.getBean().toYaml());
	}

	public void testBoundingBoxInstance() {
		CTClient c = getNewClient();
		CTFactory f = c.getObjectFactory().getFactory();
		Vector3f va = f.getBoundingBox().getA();
		f.setBoundingBox(new Vector3f(-10, 0, -10), new Vector3f(10, 1, 10));
		f.setBoundingBox(new CTBoundingBox(new Vector3f(), new Vector3f()));
		assertSame(va, f.getBoundingBox().getA());
	}

	public void testCallUnknownScript() throws IOException, SAXException {
		CTClient e = getNewClient();
		CTFactory factory = e.getObjectFactory().getFactory();
		assertNull(factory.getScript("FAIL"));
	}

	public void testAddGetScript() {
		CTClient c = getNewClient();
		CTTool tool = c.getObjectFactory().getTool();
		CTScript s = tool.addScript("testscript");
		assertNotNull(s);
		assertNotNull(tool.getScript("testscript"));
	}
}
