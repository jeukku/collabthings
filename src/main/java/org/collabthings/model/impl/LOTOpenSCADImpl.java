package org.collabthings.model.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.collabthings.LLog;
import org.collabthings.LOTClient;
import org.collabthings.model.LOT3DModel;
import org.collabthings.model.LOTOpenSCAD;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class LOTOpenSCADImpl implements ServiceObjectData, LOTOpenSCAD {
	private static final String VARIABLE_NAME = "name";
	private static final String SCRIPT = "value";
	private static final String BEANNAME = "openscad";
	//
	private ServiceObject o;
	private String script;

	//
	private LLog log = LLog.getLogger(this);
	private final LOTClient client;
	//
	private static int namecounter = 0;
	private String name;
	private String info;

	private StringBuffer error;
	private final LOT3DModel model;

	private int loadedscadhash = 0;

	/**
	 * Creates a new script with random ID.
	 * 
	 * @param env
	 */
	public LOTOpenSCADImpl(final LOTClient env) {
		this.client = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this,
				env.getVersion(), env.getPrefix());
		setName("OpenSCAD" + (LOTOpenSCADImpl.namecounter++));
		StringBuffer b = new StringBuffer();
		b.append("// created " + new Date() + " by "
				+ env.getService().getUser().getUsername() + "\n");
		b.append("// Version " + env.getVersion() + "\n");
		b.append("color(\"red\")\n");
		b.append("  rotate_extrude()\n");
		b.append("    translate([1, 0])\n");
		b.append("      square(1);\n");
		setScript(b.toString());

		model = new LOT3DModelImpl(client);
	}

	@Override
	public LOT3DModel getModel() {
		if (loadedscadhash != getScript().hashCode()) {
			loadModel();
			loadedscadhash = getScript().hashCode();
		}
		return model;
	}

	@Override
	public int hashCode() {
		return getBean().toText().hashCode();
	}

	private void loadModel() {
		try {
			File stl = createSTL();
			model.importModel(stl);
		} catch (IOException e) {
			log.error(this, "loadModel", e);
		} catch (InterruptedException e) {
			log.error(this, "loadModel", e);
		}
	}

	private File createSTL() throws IOException, FileNotFoundException,
			InterruptedException {
		String path = client.getPreferences().get("software.openscad.path",
				"openscad");
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
		BufferedReader es = new BufferedReader(new InputStreamReader(
				errorStream));

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
		}).start();
	}

	private void appendError(String line) {
		if (error == null) {
			error = new StringBuffer();
		}
		error.append(line);
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();
		b.setBase64Value(SCRIPT, script);
		b.addValue(VARIABLE_NAME, name);
		return b;
	}

	@Override
	public boolean parseBean(final WData bean) {
		script = bean.getBase64Value(SCRIPT);
		this.name = bean.getValue(VARIABLE_NAME);
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

	public void setName(String name) {
		this.name = name;
	}
}
