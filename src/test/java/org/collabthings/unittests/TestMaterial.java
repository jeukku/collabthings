package org.collabthings.unittests;

import org.collabthings.model.CTMaterial;
import org.collabthings.model.impl.CTMaterialImpl;

import junit.framework.TestCase;
import waazdoh.datamodel.WObject;

public class TestMaterial extends TestCase {

	public void testColor() {
		WObject d = new WObject("m");
		d.addValue("color", "0,1,2");

		CTMaterial m = new CTMaterialImpl(d);
		assertEquals(0.0, m.getColor()[0]);
		assertEquals(1.0, m.getColor()[1]);
		assertEquals(2.0, m.getColor()[2]);
	}
}
