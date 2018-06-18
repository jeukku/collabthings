package org.collabthings.unittests;

import static org.junit.Assert.assertNotEquals;

import org.collabthings.CTTestCase;
import org.collabthings.StaticObjectsService;

public final class TestStaticObjectsService extends CTTestCase {

	public void testHash() {
		StaticObjectsService s1 = new StaticObjectsService(null);
		String a = s1.write("", "a").getHash();
		String b = s1.write("", "b").getHash();
		assertNotEquals(a, b);

		StaticObjectsService s2 = new StaticObjectsService(null);
		String b2 = s2.write("RANDOMDERP", "b").getHash();
		assertEquals(b, b2);
		assertNotEquals(a, b2);
	}

}
