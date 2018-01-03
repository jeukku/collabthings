/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.model.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import org.collabthings.CTClient;
import org.collabthings.CTListener;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.scene.StlMeshImporter;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.WBinaryID;
import waazdoh.client.model.objects.WBinary;
import waazdoh.datamodel.WData;
import waazdoh.datamodel.WObject;
import waazdoh.datamodel.WObjectID;
import waazdoh.datamodel.WStringID;
import waazdoh.datamodel.WXML;

public class CT3DModelImpl implements CTBinaryModel, ServiceObjectData {
	private static final String BEANNAME = "model3d";
	private static final String PARAM_SCALE = "scale";
	private static final String PARAM_TRANSLATION = "translation";
	private static final String PARAM_NAME = "name";
	private static final String PARAM_BINARYID = "binaryid";
	private static final String PARAM_TYPE = "type";
	//
	private static int counter = 1;
	//
	private final ServiceObject o;
	private String name = "3dmodel" + getCount();

	private WBinaryID binaryid;
	private final CTClient env;
	private final LLog log;
	private final List<WBinary> childbinaries = new ArrayList<>();
	private double scale = 1.0;
	private Vector3f translation = new Vector3f();
	private String type;

	private CTTriangleMesh mesh;
	private boolean disabled;

	public CT3DModelImpl(final CTClient nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this, nenv.getVersion(), nenv.getPrefix());
		log = LLog.getLogger(this);
	}

	private static int getCount() {
		return CT3DModelImpl.counter++;
	}

	@Override
	public String toString() {
		return "Model[" + getName() + "]";
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
	public WObject getObject() {
		WObject b = o.getBean();
		getBean(b.add("content"));
		return b;
	}

	public void getBean(WObject b) {
		b.addValue(PARAM_NAME, name);
		b.addValue(PARAM_BINARYID, "" + getBinaryID());
		b.addValue(PARAM_SCALE, scale);
		b.add(PARAM_TRANSLATION, CTMath.getBean(translation));
		b.addValue(PARAM_TYPE, "" + type);
		//
		for (WBinary binary : childbinaries) {
			b.addToList("binaries", binary.getID().toString());
		}
	}

	private WBinaryID getBinaryID() {
		return binaryid;
	}

	@Override
	public boolean parse(WObject main) {
		WObject bean = main.get("content");
		if (bean == null) {
			bean = main;
		}

		name = bean.getValue("name");
		binaryid = new WBinaryID(bean.getIDValue(PARAM_BINARYID));

		scale = bean.getDoubleValue(PARAM_SCALE);
		translation = CTMath.parseVector(bean.get(PARAM_TRANSLATION));
		type = bean.getValue(PARAM_TYPE);
		//

		List<String> bchildbinaries = bean.getList("binaries");
		for (String bchildbinary : bchildbinaries) {
			WBinaryID childbinaryid = new WBinaryID(bchildbinary);
			addChildBinary(env.getBinarySource().getOrDownload(childbinaryid));
		}
		//
		return true;
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public WObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public boolean isReady() {
		WBinary binary = getBinary();
		if (binary != null && binary.isReady()) {
			return isBinariesReady();
		} else {
			return false;
		}
	}

	private boolean isBinariesReady() {
		for (WBinary b : childbinaries) {
			if (!b.isReady()) {
				return false;
			}
		}
		return true;
	}

	private WBinary getBinary() {
		if (binaryid != null && binaryid.isId()) {
			return this.env.getBinarySource().getOrDownload(binaryid);
		} else {
			log.error("BinaryID null. Cannot load binary");
			return null;
		}
	}

	@Override
	public void setName(String n) {
		this.name = n;
		o.modified();
	}

	@Override
	public String getName() {
		return name;
	}

	public WBinary newBinary() {
		String comment = "CT3DModel";
		String extension = getType();
		binaryid = env.getBinarySource().newBinary(comment, extension).getID();
		o.modified();
		return getBinary();
	}

	@Override
	public void publish() {
		save();
		//
		if (binaryid != null) {
			WBinary binary = getBinary();
			if (binary == null) {
				return;
			}

			binary.publish();
		}

		for (WBinary b : childbinaries) {
			b.publish();
		}
		o.publish();

	}

	@Override
	public void save() {
		if (binaryid != null) {
			WBinary bin = getBinary();
			if (bin != null) {
				bin.setReady();
				bin.save();
				binaryid = bin.getID();
			}
		}
		for (WBinary b : childbinaries) {
			b.save();
		}
		getServiceObject().save();
	}

	public void setReady() {
		WBinary bin = getBinary();
		if (bin != null) {
			bin.setReady();
			binaryid = bin.getID();
		}
	}

	@Override
	public boolean importModel(String type, InputStream is) {
		try {
			Reader r = new InputStreamReader(is, CTConstants.CHARSET);
			try {
				return importModel(r, type);
			} finally {
				r.close();
			}
		} catch (IOException | SAXException e) {
			log.error(this, "importModel InputStream", e);
			return false;
		}
	}

	@Override
	public boolean importModel(File file) throws IOException {
		log.info("Importing " + file);

		try (Reader fr = new InputStreamReader(new FileInputStream(file), CTConstants.CHARSET)) {
			String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
			log.info("Import model extension " + extension);
			return importModel(fr, extension);
		} catch (SAXException e) {
			log.error(this, "import model", e);
			return false;
		}
	}

	private boolean importModel(Reader fr, String extension) throws IOException, SAXException {
		setType(extension);

		mesh = null;

		o.modified();

		if (CTConstants.VALUE_TYPE_STL.equals(extension)) {
			return importSTL(fr);
		} else if (org.collabthings.model.impl.CTConstants.VALUE_TYPE_X3D.equals(extension)) {
			return importX3D(fr);
		} else {
			log.info("Unknown extension " + extension);
			return false;
		}
	}

	@Override
	public String getModelType() {
		return CT3DModelImpl.PARAM_TYPE;
	}

	@Override
	public void setType(String ntype) {
		this.type = ntype;
		newBinary();
	}

	@Override
	public String getType() {
		return type;
	}

	private boolean importSTL(Reader fr) throws IOException, SAXException {
		StringBuilder sb = readFile(fr);
		newBinary();
		WBinary bin = getBinary();
		if (bin != null) {
			bin.load(new ByteArrayInputStream(sb.toString().getBytes(CTConstants.CHARSET)));
			bin.setReady();
			binaryid = bin.getID();

			log.info("imported " + getBinary());

			return true;
		} else {
			log.error("couldn't import because binary does not exist");
			return false;
		}
	}

	private boolean importX3D(Reader fr) throws IOException, SAXException {
		StringBuilder sb = readFile(fr);

		String s = sb.toString();
		log.fine("importing string " + s);
		//
		s = s.replace("http://www.web3d.org", findSpecificationsResources());
		log.fine("importing converted string " + s);
		//
		WXML xml = new WXML(s);
		WData b = new WData(xml);
		log.fine("importing " + b.toText());
		if (importX3D(b)) {
			newBinary();
			WBinary bin = getBinary();
			if (bin != null) {
				bin.load(new ByteArrayInputStream(b.toXML().toString().getBytes(CTConstants.CHARSET)));
				bin.setReady();
				binaryid = bin.getID();
				return true;
			}
		}

		return false;
	}

	private static StringBuilder readFile(Reader fr) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] chb = new char[1000];
		while (true) {
			// reading the whole file
			int count = fr.read(chb);
			if (count < 0) {
				break;
			}

			sb.append(chb, 0, count);
		}

		fr.close();
		return sb;
	}

	private String findSpecificationsResources() throws IOException {
		Enumeration<URL> systemResources = ClassLoader.getSystemResources("specifications");
		List<String> searchlist = new ArrayList<>();
		while (systemResources.hasMoreElements()) {
			URL u = systemResources.nextElement();
			String pathname = u.getFile();
			searchlist.add(pathname);
			File f = new File(pathname);
			if (f.isDirectory()) {
				log.info("found specifications in " + f);
				return f.getParent().replace('\\', '/');
			}
		}
		log.info("ERROR: specifications not found locally (" + searchlist + ")");
		return null;
	}

	private boolean importX3D(WData b) throws IOException {
		String urlattribute = b.getAttribute("url");
		if (urlattribute != null) {
			importReplaceURLAttribute(b, urlattribute);
		}
		//
		List<WData> cs = b.getChildren();
		for (WData cb : cs) {
			if (!importX3D(cb)) {
				return false;
			}
		}
		//
		return true;
	}

	private void importReplaceURLAttribute(WData b, String urlattribute) throws IOException {
		log.info("Found URL attribute " + urlattribute);
		log.info("current dir " + new File(".").getAbsolutePath());
		//
		String surl = new StringTokenizer(urlattribute).nextToken();
		surl = surl.replace("\"", "");
		log.info("loading binary " + surl);
		File file = new File(surl);
		InputStream is;
		if (file.exists()) {
			is = new FileInputStream(file);
		} else {
			log.info("Getting resource " + surl);
			is = getClass().getResourceAsStream(surl);
		}
		//
		String extensions = surl.substring(surl.lastIndexOf('.') + 1);
		WBinary childbinary = this.env.getBinarySource().newBinary(surl, extensions);
		childbinary.importStream(is);
		addChildBinary(childbinary);
		b.setAttribute("url", childbinary.getID().toString());
	}

	private void addChildBinary(WBinary binary) {
		childbinaries.add(binary);
	}

	public List<WBinary> getChildBinaries() {
		return new ArrayList<>(childbinaries);
	}

	@Override
	public File getModelFile() throws IOException {
		File f = null;
		if (getType() != null) {
			WBinary binary = getBinary();
			if (binary != null) {
				f = File.createTempFile(
						"" + Long.toString(System.currentTimeMillis()) + "_" + binary.getID().toString(),
						"." + binary.getExtension());
				if (f == null) {
					return null;
				}

				if (!f.delete()) {
					log.info("delete failed " + f.getAbsolutePath());
				}

				if (getType().equals(org.collabthings.model.impl.CTConstants.VALUE_TYPE_X3D)) {
					getX3DFile(f.toPath());
				} else {
					Files.copy(binary.getInputStream(), f.toPath());
				}
			} else {
				return null;
			}
		}
		return f;
	}

	private boolean getX3DFile(Path path) throws IOException {
		InputStream is = getX3DStream();
		if (is != null) {
			Files.copy(is, path);
			return true;
		} else {
			return false;
		}
	}

	private InputStream getX3DStream() {
		if (isReady()) {
			WXML xml;
			WBinary binary = getBinary();
			if (binary != null) {
				try (InputStreamReader reader = new InputStreamReader(binary.getInputStream(), CTConstants.CHARSET)) {
					xml = new WXML(reader);
					log.info("getModelStream parsing " + xml);
					WData b = new WData(xml);
					// FIXME TODO
					b.find("X3D").setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema-instance");

					convertX3DURLs(b);
					log.info("getModelStream returning " + b.toXML().toString());
					return new BufferedInputStream(
							new ByteArrayInputStream(b.toXML().toString().getBytes(CTConstants.CHARSET)));
				} catch (IOException | SAXException e) {
					log.error(this, "getModelStream", e);
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private void convertX3DURLs(WData b) {
		String surl = b.getAttribute("url");
		if (surl != null) {
			WBinary cbin = env.getBinarySource().getOrDownload(new WBinaryID(surl));
			//
			String path = cbin.getFile().getAbsolutePath();
			String stextureurl = path.replace('\\', '/');
			b.setAttribute("url", stextureurl);
		}
		//
		List<WData> cbs = b.getChildren();
		for (WData cb : cbs) {
			convertX3DURLs(cb);
		}
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
		return translation;
	}

	@Override
	public void setTranslation(Vector3f t) {
		translation.set(t);
	}

	@Override
	public int hashCode() {
		return getObject().hashCode();
	}

	@Override
	public boolean equals(Object b) {
		if (b != null && b.getClass().equals(this.getClass())) {
			CT3DModelImpl bmodel = (CT3DModelImpl) b;
			return getObject().equals(bmodel.getObject());
		} else {
			return false;
		}
	}

	@Override
	public synchronized CTTriangleMesh getTriangleMesh() {
		if (mesh == null) {
			createTriangleMesh();
		}

		return mesh;
	}

	private void createTriangleMesh() {
		if (CTConstants.VALUE_TYPE_STL.equals(getType())) {
			StlMeshImporter i = new StlMeshImporter();
			try {
				i.setFile(getModelFile());
				mesh = i.getImport();
			} catch (IOException e) {
				log.error(this, "createTriangleMesh", e);

			}
		} else {
			mesh = new CTTriangleMeshImpl();
		}
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
		// TODO Auto-generated method stub
	}

	@Override
	public byte[] getContent() {
		try {
			WBinary binary = getBinary();
			if (binary != null) {
				return binary.getContent();
			} else {
				return null;
			}
		} catch (IOException e) {
			log.info("ERROR " + e);
			return null;
		}
	}

	@Override
	public void setContent(byte[] bytes) {
		try {
			WBinary bin = getBinary();
			if (bin != null) {
				bin.importStream(new ByteArrayInputStream(bytes));
				binaryid = bin.getID();
			}
		} catch (IOException e) {
			log.info("ERROR " + e);
		}
	}

}
