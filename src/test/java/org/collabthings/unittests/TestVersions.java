package org.collabthings.unittests;

import org.collabthings.CTClient;

import junit.framework.TestCase;

public class TestVersions extends TestCase {

	public void testVersion() {
		assertTrue(CTClient.checkVersion("0.0.2"));
		assertFalse(CTClient.checkVersion("0.0.1"));
		assertTrue(CTClient.checkVersion("0.0.10"));
		assertTrue(CTClient.checkVersion("100.0.10"));
	}
}
