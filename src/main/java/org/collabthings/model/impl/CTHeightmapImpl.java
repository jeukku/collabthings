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
	private static final String SCRIPT = "script";

	private CTClient client;
	private ServiceObject o;
	//
	private double scale;
	private Vector3f tr;
	private boolean disabled = false;
	private CTListeners changelisteners = new CTListeners();
	private String script;
	private CTTriangleMesh mesh;
	private String name;

	private int resolutionx = 100, resolutionz = 100;
	private float sizex = 10000.0f, sizez = 10000.0f;

	private CTListeners listeners = new CTListeners();
	private String error;

	private int loadedscadhash;

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

			for (int ix = 0; ix < resolutionx; ix++) {
				for (int iz = 0; iz < resolutionz; iz++) {
					float xa = (ix - sizex / 2.0f) / resolutionx;
					float za = (iz - sizez / 2.0f) / resolutionz;

					float ya = (float) (Math.sin(xa * 13) * (sizex / 10));
					ya += (float) (Math.sin(za * 13) * (sizez / 10));

					int iv = vs.size();
					vs.add(new Vector3f(xa * sizex, ya, za * sizez));
				}
			}

			for (int ixa = 0; ixa < resolutionx - 1; ixa++) {
				for (int iza = 0; iza < resolutionz - 1; iza++) {
					int ixb = ixa + 1;
					int izb = iza + 1;
					int ifirst = ixa * resolutionz;
					int via = ifirst + iza;
					int vib = ifirst + iza + 1;
					int vic = ifirst + iza + resolutionz;
					int vid = ifirst + iza + resolutionz + 1;

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

		script = content.getBase64Value(SCRIPT);
		loadedscadhash = getScript().hashCode();

		resolutionx = content.getIntValue("resolutionx");
		resolutionz = content.getIntValue("resolutionz");
		sizex = (float) content.getDoubleValue("sizex");
		sizez = (float) content.getDoubleValue("sizez");

		return true;
	}

	@Override
	public WObject getObject() {
		WObject ob = o.getBean();
		WObject c = ob.add("content");
		c.addValue("scale", scale);
		if (script != null) {
			c.setBase64Value("script", script);
		}

		c.addValue("resolutionx", resolutionx);
		c.addValue("resolutionz", resolutionz);

		return ob;
	}

	@Override
	public void setScript(final String nscript) {
		this.script = nscript;
		error = null;
		changed();
	}

	@Override
	public String getError() {
		if (error == null) {
			return null;
		} else {
			return error.toString();
		}
	}

	@Override
	public String getScript() {
		return script;
	}

	@Override
	public boolean isOK() {
		return error == null;
	}

	public int getResolutionx() {
		return resolutionx;
	}

	public void setResolutionx(int resolutionx) {
		this.resolutionx = resolutionx;
	}

	public int getResolutionz() {
		return resolutionz;
	}

	public void setResolutionz(int resolutionz) {
		this.resolutionz = resolutionz;
	}

	public float getSizex() {
		return sizex;
	}

	public void setSizex(float sizex) {
		this.sizex = sizex;
	}

	public float getSizez() {
		return sizez;
	}

	public void setSizez(float sizez) {
		this.sizez = sizez;
	}

	private void changed() {
		listeners.fireEvent();
	}

	private boolean isChanged() {
		return loadedscadhash != getScript().hashCode();
	}

}
