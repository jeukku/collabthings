package org.libraryofthings.unittests;

import junit.framework.TestCase;

import org.collabthings.model.LOTMaterial;
import org.collabthings.model.impl.LOTMaterialImpl;

import waazdoh.common.WData;

public class TestMaterial extends TestCase {

	public void testColor() {
		WData d = new WData("m");
		d.setValue("0,1,2");

		LOTMaterial m = new LOTMaterialImpl(d);
		assertEquals(0.0, m.getColor()[0]);
		assertEquals(1.0, m.getColor()[1]);
		assertEquals(2.0, m.getColor()[2]);
	}
}
