package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.impl.CTPool;
import org.collabthings.model.CTPart;
import org.xml.sax.SAXException;

public final class TestPool extends CTTestCase {

	public void testgetNull() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNotNull(e);
		//
		CTPool p = new CTPool(null, null);
		assertNull(p.getPart("null"));
	}

	public void testCount() {
		CTPool p = new CTPool(null, null);
		assertEquals(0, p.countParts("test"));
	}

	public void testAddPartGetPart() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNotNull(e);

		CTPool pool = new CTPool(null, null);
		pool.addPart("test", e.getObjectFactory().getPart());

		assertEquals(1, pool.countParts("test"));
		//
		CTPart peekPart = pool.peekPart("test");
		assertNotNull(peekPart);
		CTPart part = pool.getPart("test");
		assertNotNull(part);
		assertSame(peekPart, part);

		assertNull(pool.peekPart("test"));
		assertNull(pool.getPart("test"));

		assertNull(pool.getPart("test_null"));
	}

	public void testAddPartGetPartTwice() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNotNull(e);

		CTPool pool = new CTPool(null, null);
		pool.addPart("test", e.getObjectFactory().getPart());
		//
		assertNotNull(pool.getPart("test"));
		assertNull(pool.getPart("test_null"));
		assertNull(pool.getPart("test"));
	}

	public void testAddPartGetPartDouble() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNotNull(e);

		CTPool pool = new CTPool(null, null);
		pool.addPart("test1", e.getObjectFactory().getPart());
		pool.addPart("test2", e.getObjectFactory().getPart());
		//
		assertNotNull(pool.getPart("test1"));
		assertNotNull(pool.getPart("test2"));
		assertNull(pool.getPart("test_null"));
	}

	public void testWaitForAPart() throws IOException, SAXException {
		final CTClient e = getNewClient();
		assertNotNull(e);

		final CTPool pool = new CTPool(null, null);
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
