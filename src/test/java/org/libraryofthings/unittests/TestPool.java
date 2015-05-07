package org.libraryofthings.unittests;

import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.environment.impl.LOTPool;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

public final class TestPool extends LOTTestCase {

	public void testgetNull() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);
		//
		LOTPool p = new LOTPool(null, null);
		assertNull(p.getPart("null"));
	}

	public void testAddPartGetPart() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);

		LOTPool pool = new LOTPool(null, null);
		pool.addPart("test", e.getObjectFactory().getPart());
		//
		assertNotNull(pool.getPart("test"));
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
