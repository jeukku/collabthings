package org.collabthings.model.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.collabthings.CTClient;
import org.collabthings.math.LVector;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.scene.CTGroup;
import org.collabthings.scene.StlMeshImporter;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class CTOpenSCADImpl implements ServiceObjectData, CTOpenSCAD, CTModel {
	private static final String VARIABLE_NAME = "name";
	private static final String SCRIPT = "value";
	private static final String VARIABLE_SCALE = "scale";
	//
	private ServiceObject o;
	private String script;

	//
	private LLog log = LLog.getLogger(this);
	private final CTClient client;
	//
	private static int namecounter = 0;
	private String name;
	private String info;

	private StringBuffer error;
	private final CTBinaryModel model;

	private int loadedscadhash = 0;
	private LVector translation = new LVector();
	private double scale = 1;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 * @param nmodel
	 */
	public CTOpenSCADImpl(final CTClient env) {
		this.client = env;
		o = new ServiceObject(CTModel.SCAD, env.getClient(), this, env.getVersion(), env.getPrefix());
		setName("OpenSCAD" + (CTOpenSCADImpl.namecounter++));
		StringBuffer b = new StringBuffer();
		b.append("// created " + new Date() + " by " + env.getService().getUser().getUsername() + "\n");
		b.append("// Version " + env.getVersion() + "\n");
		b.append("color(\"red\")\n");
		b.append("  rotate_extrude()\n");
		b.append("    translate([1, 0])\n");
		b.append("      square(1);\n");
		setScript(b.toString());

		model = new CT3DModelImpl(env);
	}

	@Override
	public CTBinaryModel getModel() {
		if (loadedscadhash != getScript().hashCode()) {
			loadModel();
			loadedscadhash = getScript().hashCode();
		}
		return model;
	}

	@Override
	public void addTo(CTGroup g) {
		try {
			StlMeshImporter i = new StlMeshImporter();
			i.read(getModel().getModelFile());
			CTTriangleMesh mesh = i.getImport();

			g.add(mesh);

		} catch (SAXException | IOException e) {
			log.error(this, "addTo", e);
		}
	}

	@Override
	public int hashCode() {
		return getObject().toText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CTOpenSCADImpl) {
			CTOpenSCADImpl o = (CTOpenSCADImpl) obj;
			return o.getObject().toText().equals(getObject().toText());
		} else {
			return false;
		}
	}

	@Override
	public boolean importModel(File file) {
		throw new RuntimeException("not supported");
	}

	@Override
	public String getModelType() {
		return CTModel.SCAD;
	}

	private void loadModel() {
		try {
			File stl = createSTL();
			model.importModel(stl);
			model.setTranslation(getTranslation());
			model.setScale(getScale());
		} catch (IOException e) {
			log.error(this, "loadModel", e);
		} catch (InterruptedException e) {
			log.error(this, "loadModel", e);
		}
	}

	private File createSTL() throws IOException, FileNotFoundException, InterruptedException {
		String path = client.getPreferences().get("software.openscad.path", "openscad");
		File tempfile = File.createTempFile("collabthings", ".scad");
		FileOutputStream fos = new FileOutputStream(tempfile);
		String s = getScript();
		byte[] bs = s.getBytes();
		fos.write(bs, 0, bs.length);
		fos.close();

		log.info("SCAD file " + tempfile);
		log.info("OpenSCAD " + path);

		File stlfile = File.createTempFile(getID().toString(), ".stl");
		String command = path + " -o " + stlfile;
		// command += " -D 'quality=\"production\"' ";
		command += " " + tempfile.getAbsolutePath();
		log.info("Running " + command);
		Process e = Runtime.getRuntime().exec(command);

		readStream(e.getErrorStream());
		readStream(e.getInputStream());

		e.waitFor();

		log.info("Exit value " + e.exitValue());
		return stlfile;
	}

	private void readStream(InputStream errorStream) {
		BufferedReader es = new BufferedReader(new InputStreamReader(errorStream));

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
			} catch (Exception e1) {
				log.error(es, "loadModel", e1);
			}
		}, "OpenSCAD readStream").start();
	}

	private void appendError(String line) {
		if (error == null) {
			error = new StringBuffer();
		}
		error.append(line);
	}

	@Override
	public WObject getObject() {
		WObject b = o.getBean();
		getBean(b);
		return b;
	}

	public void getBean(WObject b) {
		b.setBase64Value(SCRIPT, script);
		b.addValue(VARIABLE_NAME, name);
		b.addValue(VARIABLE_SCALE, scale);
	}

	@Override
	public boolean parse(final WObject bean) {
		script = bean.getBase64Value(SCRIPT);
		this.name = bean.getValue(VARIABLE_NAME);
		this.scale = bean.getDoubleValue(VARIABLE_SCALE);

		return name != null && script != null;
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public void setScript(final String nscript) {
		this.script = nscript;
		error = null;
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
		return o;
	}

	@Override
	public ObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void publish() {
		getServiceObject().publish();
	}

	@Override
	public void save() {
		getServiceObject().save();
	}

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
	}

	@Override
	public LVector getTranslation() {
		return translation;
	}

	@Override
	public void setTranslation(LVector translation) {
		this.translation = translation;
	}
}
