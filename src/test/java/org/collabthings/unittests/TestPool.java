package org.collabthings.unittests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.impl.LOTPool;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBinaryModel;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTMaterial;
import org.collabthings.model.LOTModel;
import org.collabthings.model.LOTOpenSCAD;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTSubPart;
import org.collabthings.model.impl.LOTPartImpl;
import org.xml.sax.SAXException;

import waazdoh.common.ObjectID;
import waazdoh.common.WData;

public final class TestPool extends LOTTestCase {

	public void testgetNull() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);
		//
		LOTPool p = new LOTPool(null, null);
		assertNull(p.getPart("null"));
	}

	public void testCount() {
		LOTPool p = new LOTPool(null, null);
		assertEquals(0, p.countParts("test"));
	}

	public void testAddPartGetPart() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);

		LOTPool pool = new LOTPool(null, null);
		pool.addPart("test", e.getObjectFactory().getPart());
		
		assertEquals(1, pool.countParts("test"));
		//
		LOTPart peekPart = pool.peekPart("test");
		assertNotNull(peekPart);
		LOTPart part = pool.getPart("test");
		assertNotNull(part);
		assertSame(peekPart, part);

		assertNull(pool.peekPart("test"));
		assertNull(pool.getPart("test"));

		assertNull(pool.getPart("test_null"));
	}

	public void testAddPartGetPartTwice() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);

		LOTPool pool = new LOTPool(null, null);
		pool.addPart("test", e.getObjectFactory().getPart());
		//
		assertNotNull(pool.getPart("test"));
		assertNull(pool.getPart("test_null"));
		assertNull(pool.getPart("test"));
	}

	public void testAddPartGetPartDouble() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);

		LOTPool pool = new LOTPool(null, null);
		pool.addPart("test1", e.getObjectFactory().getPart());
		pool.addPart("test2", e.getObjectFactory().getPart());
		//
		assertNotNull(pool.getPart("test1"));
		assertNotNull(pool.getPart("test2"));
		assertNull(pool.getPart("test_null"));
	}

	public void testWaitForAPart() throws IOException, SAXException {
		final LOTClient e = getNewClient();
		assertNotNull(e);

		final LOTPool pool = new LOTPool(null, null);
		new Thread(() -> {
			pool.addPart("test", e.getObjectFactory().getPart());
		}).start();
		pool.waitForPart("test", 3000);
		// to test when pool exists, but is empty
		new Thread(() -> {
			pool.addPart("test", e.getObjectFactory().getPart());
		}).start();
		pool.waitForPart("test", 3000);
		//
		assertNotNull(pool.getPart("test"));
	}
}
