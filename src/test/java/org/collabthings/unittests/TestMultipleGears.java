package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.model.impl.CTPartImpl;
import org.xml.sax.SAXException;

public final class TestMultipleGears extends CTTestCase {

	private static final int MAX_RUNTIME = 3000;

	public void testGears() throws IOException, SAXException {
		CTClient env = getNewClient(true);
		assertNotNull(env);
		//
		CTPart gear = new CTPartImpl(env);
		CTOpenSCAD scad = gear.newSCAD();
		scad.setName("simplegear");
		gear.setName("testing simplegear model");
		scad.setScript(loadATestFile("scad/simplegear10_10.scad"));

		CTBinaryModel m = scad.getModel();
		assertNotNull(m);

		CTPart mainpart = new CTPartImpl(env);

		for (int i = 0; i < 5; i++) {
			CTSubPart sp = mainpart.newSubPart();
			sp.setPart(gear);

			double angle = 9;
			LVector normal = new LVector(0, 0, 1);
			LVector location = new LVector(28, 0, 0);

			LTransformation t = LTransformation.getRotate(new LVector(0, 0, 1), (double) i / 5 * Math.PI * 2);
			location = t.transform(location);
			sp.setOrientation(location, normal, angle);
		}

		CTSubPart center = mainpart.newSubPart();
		center.setOrientation(new LVector(), new LVector(0, 0, 1), 0);
		CTPart centerpart = env.getObjectFactory().getPart();
		center.setPart(centerpart);
		CTOpenSCAD centerscad = centerpart.newSCAD();
		centerscad.setName("simplegear");
		centerpart.setName("testing simplegear model");
		centerscad.setScript(loadATestFile("scad/simplegear15_15.scad"));

		mainpart.setName("multiplegears");
		mainpart.publish();
	}

}
