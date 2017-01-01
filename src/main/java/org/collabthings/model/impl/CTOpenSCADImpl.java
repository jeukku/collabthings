package org.collabthings.model.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.collabthings.CTClient;
import org.collabthings.CTEvent;
import org.collabthings.CTListener;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.util.CTListeners;
import org.collabthings.util.LLog;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.WStringID;
import waazdoh.common.WObjectID;
import waazdoh.common.WObject;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class CTOpenSCADImpl implements ServiceObjectData, CTOpenSCAD {
	private static final String VARIABLE_NAME = "name";
	private static final String VARIABlE_SCRIPT = "value";
	private static final String VARIABLE_SCALE = "scale";
	private static final String VARIABLE_MODEL = "model";
	//
	private ServiceObject so;
	private String script;

	//
	private LLog log = LLog.getLogger(this);
	private final CTClient client;
	//
	private static int namecounter;
	private String name;
	private String info;

	private StringBuilder error;
	private final CTBinaryModel model;

	private int loadedscadhash;
	private Vector3f translation = new Vector3f();
	private double scale = 1;
	private boolean disabled;
	private CTListeners listeners = new CTListeners();

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 * @param nmodel
	 */
	public CTOpenSCADImpl(final CTClient env) {
		this.client = env;
		so = new ServiceObject(CTModel.SCAD, env.getClient(), this, env.getVersion(), env.getPrefix());
		setName("OpenSCAD" + (CTOpenSCADImpl.namecounter++));
		StringBuilder b = new StringBuilder();
		b.append("// created " + new Date() + " by " + env.getService().getUser().getUsername() + "\n");
		b.append("// Version " + env.getVersion() + "\n");
		b.append("color(\"red\")\n");
		b.append("  rotate_extrude()\n");
		b.append("    translate([1000, 0])\n");
		b.append("      square(400);\n");
		script = b.toString();

		model = new CT3DModelImpl(env);
	}

	@Override
	public long getModified() {
		return so.getModified();
	}

	@Override
	public synchronized CTModel getModel() {
		if (isChanged()) {
			if (createModel()) {
				loadedscadhash = getScript().hashCode();
				changed(new CTEvent("model created"));
			}
		}

		return model;
	}

	private void changed(CTEvent e) {
		listeners.fireEvent(e);
	}

	private boolean isChanged() {
		return loadedscadhash != getScript().hashCode();
	}

	@Override
	public int hashCode() {
		return getObject().toYaml().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CTOpenSCADImpl) {
			CTOpenSCADImpl o = (CTOpenSCADImpl) obj;
			return o.getObject().toYaml().equals(getObject().toYaml());
		} else {
			return false;
		}
	}

	@Override
	public boolean importModel(File file) throws IOException {
		StringBuilder b = new StringBuilder();
		Files.readAllLines(Paths.get(file.getAbsolutePath())).forEach(l -> {
			b.append(l);
			b.append("\n");
		});

		setScript(b.toString());
		return true;
	}

	@Override
	public String getModelType() {
		return CTModel.SCAD;
	}

	private boolean createModel() {
		try {
			File stl = createSTL();
			model.importModel(stl);
			model.setTranslation(getTranslation());
			model.setScale(getScale());
			return true;
		} catch (IOException | InterruptedException e) {
			log.error(this, "createModel", e);
			client.errorEvent(CTClient.ERROR_OPENSCADFAILED, e);
		}
		return false;
	}

	private File createSTL() throws IOException, InterruptedException {
		String path = client.getPreferences().get(CTClient.PREFERENCES_OPENSCADPATH, "openscad");
		File tempfile = File.createTempFile("collabthings", ".scad");
		FileOutputStream fos = new FileOutputStream(tempfile);
		String s = getScript();
		byte[] bs = s.getBytes(CTClient.CHARSET);
		fos.write(bs, 0, bs.length);
		fos.close();

		log.info("SCAD file " + tempfile);
		log.info("OpenSCAD " + path);

		File stlfile = File.createTempFile(getID().toString(), ".stl");
		String command = path + " -o " + stlfile;
		command += " " + tempfile.getAbsolutePath();
		log.info("Running " + command);
		Process e = Runtime.getRuntime().exec(command);

		readStream(e.getErrorStream());
		readStream(e.getInputStream());

		e.waitFor();

		log.info("Exit value " + e.exitValue());
		return stlfile;
	}

	private void readStream(InputStream errorStream) throws UnsupportedEncodingException {
		BufferedReader es = new BufferedReader(new InputStreamReader(errorStream, CTClient.CHARSET));

		new Thread(() -> {
			try {
				while (true) {
					String line;
					line = es.readLine();
					if (line == null) {
						break;
					}

					log.info("line :" + line);
					appendError(line + "\n");
				}
			} catch (IOException e1) {
				log.error(es, "loadModel", e1);
			}
		}, "OpenSCAD readStream").start();
	}

	private void appendError(String line) {
		if (error == null) {
			error = new StringBuilder();
		}
		error.append(line);
	}

	@Override
	public WObject getObject() {
		WObject b = so.getBean();
		getBean(b.add("content"));
		return b;
	}

	private void getBean(WObject b) {
		b.setBase64Value(VARIABlE_SCRIPT, script);
		b.addValue(VARIABLE_NAME, name);
		b.addValue(VARIABLE_SCALE, scale);
		if (model != null) {
			b.addValue(VARIABLE_MODEL, model.getID());
		}
	}

	@Override
	public boolean parse(final WObject main) {
		WObject content = main.get("content");
		if (content == null) {
			content = main;
		}

		script = content.getBase64Value(VARIABlE_SCRIPT);
		loadedscadhash = getScript().hashCode();

		this.name = content.getValue(VARIABLE_NAME);
		this.scale = content.getDoubleValue(VARIABLE_SCALE);

		WStringID varmodel = content.getIDValue(VARIABLE_MODEL);
		if (!model.load(varmodel)) {
			log.info("Loading model failed. Creating it.");
			createModel();
		}

		if (!model.getID().getStringID().equals(varmodel)) {
			log.info("OpenSCAD loading model with id does not not equal with " + varmodel);
			log.info("service has "
					+ this.client.getClient().getObjects().read(varmodel.toString()).toObject().toYaml());
			log.info("model now " + model.getObject().toYaml());
			createModel();
		}
		return name != null && script != null;
	}

	@Override
	public boolean load(WStringID id) {
		return so.load(id);
	}

	@Override
	public void setScript(final String nscript) {
		this.script = nscript;
		error = null;
		changed(new CTEvent("script set"));
		so.modified();
	}

	@Override
	public String getError() {
		if (error == null) {
			return null;
		} else {
			return error.toString();
		}
	}

	private ServiceObject getServiceObject() {
		return so;
	}

	@Override
	public WObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public boolean isReady() {
		return isChanged() || model.isReady();
	}

	@Override
	public void publish() {
		getModel().publish();
		getServiceObject().publish();
	}

	@Override
	public void save() {
		getServiceObject().save();
	}

	@Override
	public String getScript() {
		return script;
	}

	@Override
	public boolean isOK() {
		return error == null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SCAD[" + this.name + "][" + info + "]";
	}

	@Override
	public void setName(String name) {
		this.name = name;
		changed(new CTEvent("name set"));
	}

	@Override
	public CTTriangleMesh getTriangleMesh() {
		return getModel().getTriangleMesh();
	}

	@Override
	public double getScale() {
		return scale;
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;
		changed(new CTEvent("scale set"));
	}

	@Override
	public Vector3f getTranslation() {
		return translation;
	}

	@Override
	public void setTranslation(Vector3f translation) {
		this.translation = translation;
		changed(new CTEvent("Translation set"));
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public void setDisabled(boolean b) {
		this.disabled = b;
	}

	@Override
	public void addChangeListener(CTListener l) {
		listeners.add(l);
	}
}
