package org.collabthings.unittests;

import java.util.Date;
import java.util.UUID;

import org.collabthings.CTTestCase;
import org.collabthings.app.AppPreferences;

public final class TestAppPreferences extends CTTestCase {

	public void testInit() {
		AppPreferences p = new AppPreferences();
		String string = "" + new Date();
		p.set("tested", string);
		assertEquals(string, p.get("tested", string));
	}

	public void testDefaultValue() {
		AppPreferences p = getPreferences();
		String testvalue = "testvalue_" + UUID.randomUUID();
		assertEquals(testvalue, p.get("testvalue/test/2", testvalue));

		assertEquals(p.get("resource.value", "none"), "hello");
	}

	public void testDefaultValueFails() {
		AppPreferences p = new AppPreferences("testapppreferences/" + UUID.randomUUID(), "fail.ini");
		String testvalue = "testvalue_" + UUID.randomUUID();
		assertEquals(testvalue, p.get("testvalue/test/2", testvalue));

		assertEquals(p.get("resource.value", "none"), "none");
	}

	public void testWrite() {
		AppPreferences p = getPreferences();
		String testvalue = "testvalue_" + UUID.randomUUID();
		String name = "testvalue/test/" + UUID.randomUUID();
		p.set(name, testvalue);
		assertEquals(testvalue, p.get(name, "FAIL_" + UUID.randomUUID()));
	}

	public void testBoolean() {
		AppPreferences p = getPreferences();
		String name = "testvalue/test/" + UUID.randomUUID();
		p.set(name, false);
		assertFalse(p.getBoolean(name, true));
		p.set(name, true);
		assertTrue(p.getBoolean(name, false));
	}

	public void testDouble() {
		AppPreferences p = getPreferences();
		String name = "testvalue/test/" + UUID.randomUUID();
		p.set(name, "1.0");
		assertEquals(1.0, p.getDouble(name, 2.0));
		assertEquals(3.0, p.getDouble(name + "new", 3.0));
	}

	public void testInteger() {
		AppPreferences p = getPreferences();
		String name = "testvalue/test/" + UUID.randomUUID();
		p.set(name, "1");
		assertEquals(1, p.getInteger(name, 2));
		assertEquals(3, p.getInteger(name + "new", 3));
	}

	private AppPreferences getPreferences() {
		AppPreferences p = new AppPreferences("testapppreferences/" + UUID.randomUUID());
		return p;
	}

}
