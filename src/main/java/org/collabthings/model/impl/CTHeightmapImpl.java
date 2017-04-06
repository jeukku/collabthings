package org.collabthings.model.impl;

import java.io.File;
import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTEvent;
import org.collabthings.CTListener;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.util.CTListeners;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.WStringID;
import waazdoh.common.WObjectID;
import waazdoh.common.WObject;

public class CTHeightmapImpl implements CTHeightmap, ServiceObjectData {
	private static final String PARAM_SCRIPT = "script";
	private static final String PARAM_NAME = "name";

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

	private int resolutionx = 100;
	private int resolutionz = 100;
	private float sizex = 10000000.0F;
	private float sizez = 10000000.0F;

	private CTListeners listeners = new CTListeners();
	private String error;

	private int loadedscadhash;

	public CTHeightmapImpl(CTClient client) {
		this.client = client;
		o = new ServiceObject(CTModel.HEIGHTMAP, client.getClient(), this, client.getVersion(), client.getPrefix());
	}

	@Override
	public boolean load(WStringID id) {
		return o.load(id);
	}

	@Override
	public long getModified() {
		return o.getModified();
	}

	@Override
	public WObjectID getID() {
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
		if (mesh == null || getScript().hashCode() != loadedscadhash) {
			mesh = new CTTriangleMeshImpl();

			for (int ix = 0; ix < resolutionx; ix++) {
				for (int iz = 0; iz < resolutionz; iz++) {
					double xa = (double) ix / resolutionx * sizex - sizex / 2.0;
					double za = (double) iz / resolutionz * sizez - sizez / 2.0;

					double ya = Math.sin(xa * 1.1) * (sizex / 80.0);
					ya += Math.sin(za * 1.3) * (sizez / 80.0);

					mesh.add(new Vector3f((float) xa, (float) ya, (float) za));
				}
			}

			for (int ixa = 0; ixa < resolutionx - 1; ixa++) {
				for (int iza = 0; iza < resolutionz - 1; iza++) {
					int ifirst = ixa * resolutionz;
					int via = ifirst + iza;
					int vib = ifirst + iza + 1;
					int vic = ifirst + iza + resolutionz;
					int vid = ifirst + iza + resolutionz + 1;

					mesh.add(new CTTriangle(via, vic, vib, null));
					mesh.add(new CTTriangle(vid, vib, vic, null));
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
	public void setName(String n) {
		this.name = n;
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
		name = content.getValue(PARAM_NAME);

		script = content.getBase64Value(PARAM_SCRIPT);
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
		c.addValue(PARAM_NAME, name);

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
		changed(new CTEvent("script set"));
	}

	@Override
	public String getError() {
		if (error == null) {
			return null;
		} else {
			return error;
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

	private void changed(CTEvent e) {
		listeners.fireEvent(e);
	}

}
