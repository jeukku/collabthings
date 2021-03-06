package org.collabthings.unittests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.model.impl.CTPartImpl;
import org.xml.sax.SAXException;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public final class TestMultipleGears extends CTTestCase {

	private static final int MAX_RUNTIME = 3000;
	private Map<String, CTPart> gearcontent = new HashMap<>();

	public void testGears() throws IOException, SAXException {
		CTClient ctclient = getNewClient(true);
		assertNotNull(ctclient);

		//
		CTPart mainpart = new CTPartImpl(ctclient);

		for (int c = 0; c < 10; c++) {
			float z = -15 + c * 3;

			CTPart gear = LoadGear(ctclient, 10 + c, 10 + c);

			for (int i = 0; i < 5; i++) {
				CTSubPart sp = mainpart.newSubPart();
				sp.setPart(gear);

				float angle = 9;
				Vector3f normal = new Vector3f(0, 0, 1);
				Vector3f location = new Vector3f(28, 0, z);

				Transform t = new LOrientation(new Vector3f(0, 0, 1), (float) (i / 5.0 * Math.PI * 2))
						.getTransformation();
				location = t.transformVector(location, new Vector3f());
				sp.setOrientation(location, normal, angle);
			}

			CTSubPart center = mainpart.newSubPart();
			center.setOrientation(new Vector3f(0, 0, z), new Vector3f(0, 0, 1), 0);
			CTPart centerpart = LoadGear(ctclient, 15 + c, 15 + c);
			center.setPart(centerpart);
		}

		mainpart.setName("multiplegears");
		mainpart.publish();
	}

	private CTPart LoadGear(CTClient env, float radius, int toothcount) throws FileNotFoundException, IOException {
		String name = "r" + radius + "t" + toothcount;
		if (gearcontent.get(name) == null) {
			String f = loadATestFile("scad/simplegearR_T.scad");
			f = f.replace("_TOOTHCOUNT_", "" + toothcount);
			f = f.replace("_RADIUS_", "" + radius);

			CTPart gear = new CTPartImpl(env);
			CTOpenSCAD scad = gear.newSCAD();
			scad.setName("simplegear");
			gear.setName("testing simplegear model");
			scad.setApplication(f);

			gearcontent.put(name, gear);
		}

		return gearcontent.get(name);
	}
}
