package org.collabthings.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTListener;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.util.CTListeners;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public class CTHeightmapImpl implements CTHeightmap, CTModel, ServiceObjectData {
	private CTClient client;
	private ServiceObject o;
	//
	private double scale;
	private Vector3f tr;
	private boolean disabled = false;
	private CTListeners changelisteners = new CTListeners();
	private CTScript script;
	private CTTriangleMesh mesh;
	private String name;

	public CTHeightmapImpl(CTClient client) {
		this.client = client;
		o = new ServiceObject(CTModel.HEIGHTMAP, client.getClient(), this, client.getVersion(), client.getPrefix());
	}

	@Override
	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public ObjectID getID() {
		return o.getID();
	}

	@Override
	public void publish() {
		o.publish();
	}

	@Override
	public void save() {
		o.save();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public CTTriangleMesh getTriangleMesh() {
		if (mesh == null) {
			mesh = new CTTriangleMeshImpl();
			List<Vector3f> vs = mesh.getVectors();
			List<CTTriangle> triangles = mesh.getTriangles();

			int isize = 100;
			float size = 100000.0f;

			for (int ix = 0; ix < isize; ix++) {
				for (int iz = 0; iz < isize; iz++) {
					float xa = (ix - isize / 2) * size;
					float za = (iz - isize / 2) * size;

					float ya = (float) (Math.sin(xa / size / isize * 13) * (size * isize / 10));
					ya += (float) (Math.sin(za / size / isize * 13) * (size * isize / 10));

					int iv = vs.size();
					vs.add(new Vector3f(xa, ya, za));
				}
			}

			for (int ixa = 0; ixa < isize - 1; ixa++) {
				for (int iza = 0; iza < isize - 1; iza++) {
					int ixb = ixa + 1;
					int izb = iza + 1;
					int via = ixa * isize + iza;
					int vib = ixa * isize + iza + 1;
					int vic = ixa * isize + iza + isize;
					int vid = ixa * isize + iza + isize + 1;

					triangles.add(new CTTriangle(via, vic, vib, null));
					triangles.add(new CTTriangle(vid, vib, vic, null));
				}
			}
		}
		return this.mesh;
	}

	@Override
	public boolean importModel(File file) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getModelType() {
		return CTModel.HEIGHTMAP;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getScale() {
		return scale;
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public Vector3f getTranslation() {
		return tr;
	}

	@Override
	public void setTranslation(Vector3f translation) {
		tr = new Vector3f(translation);
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public void setDisabled(boolean b) {
		disabled = true;
	}

	@Override
	public void addChangeListener(CTListener l) {
		changelisteners.add(l);
	}

	@Override
	public boolean parse(WObject bean) {
		WObject content = bean.get("content");
		scale = content.getDoubleValue("scale");
		name = content.getValue("name");
		script = client.getObjectFactory().getScript(content.getIDValue("script"));
		return true;
	}

	@Override
	public WObject getObject() {
		WObject ob = o.getBean();
		WObject c = ob.add("content");
		c.addValue("scale", scale);
		if (script != null) {
			c.addValue("script", script.getID());
		}
		return ob;
	}

}
