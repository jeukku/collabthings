package org.collabthings.unittests;

import org.collabthings.LOTClient;

import junit.framework.TestCase;

public class TestVersions extends TestCase {

	public void testVersion() {
		assertTrue(LOTClient.checkVersion("0.0.2"));
		assertFalse(LOTClient.checkVersion("0.0.1"));
		assertTrue(LOTClient.checkVersion("0.0.10"));
		assertTrue(LOTClient.checkVersion("100.0.10"));
	}
}
