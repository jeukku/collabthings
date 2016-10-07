package org.collabthings.model.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import org.collabthings.CTClient;
import org.collabthings.CTListener;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.scene.StlMeshImporter;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.BinaryID;
import waazdoh.client.model.objects.Binary;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;
import waazdoh.common.WObject;
import waazdoh.common.XML;

public class CT3DModelImpl implements CTBinaryModel, ServiceObjectData, CTModel {
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

	private BinaryID binaryid;
	private final CTClient env;
	private final LLog log;
	private final List<Binary> childbinaries = new ArrayList<>();
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
	public boolean load(MStringID id) {
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
		for (Binary binary : childbinaries) {
			b.addToList("binaries", binary.getID().toString());
		}
	}

	private BinaryID getBinaryID() {
		return binaryid;
	}

	@Override
	public boolean parse(WObject main) {
		WObject bean = main.get("content");
		if (bean == null) {
			bean = main;
		}

		name = bean.getValue("name");
		binaryid = new BinaryID(bean.getIDValue(PARAM_BINARYID));

		scale = bean.getDoubleValue(PARAM_SCALE);
		translation = CTMath.parseVector(bean.get(PARAM_TRANSLATION));
		type = bean.getValue(PARAM_TYPE);
		//

		List<String> bchildbinaries = bean.getList("binaries");
		for (String bchildbinary : bchildbinaries) {
			BinaryID childbinaryid = new BinaryID(bchildbinary);
			addChildBinary(env.getBinarySource().getOrDownload(childbinaryid));
		}
		//
		return true;
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
		if (getBinary() != null && getBinary().isReady()) {
			return isBinariesReady();
		} else {
			return false;
		}
	}

	private boolean isBinariesReady() {
		for (Binary b : childbinaries) {
			if (!b.isReady()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Binary getBinary() {
		if (binaryid != null) {
			return this.env.getBinarySource().getOrDownload(binaryid);
		} else {
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

	public Binary newBinary() {
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
		o.publish();
		if (binaryid != null) {
			getBinary().publish();
		}

		for (Binary b : childbinaries) {
			b.publish();
		}
	}

	@Override
	public void save() {
		if (binaryid != null) {
			getBinary().setReady();
			getBinary().save();
		}
		for (Binary b : childbinaries) {
			b.save();
		}
		getServiceObject().save();
	}

	@Override
	public boolean importModel(String type, InputStream is) {
		try {
			Reader r = new InputStreamReader(is);
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
	public boolean importModel(File file) {
		log.info("Importing " + file);
		Reader fr = null;
		try {
			fr = new FileReader(file);
			String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
			log.info("Import model extension " + extension);
			boolean m = importModel(fr, extension);
			fr.close();
			return m;
		} catch (IOException | SAXException e) {

			log.error(this, "import model", e);

			return false;
		}
	}

	private boolean importModel(Reader fr, String extension) throws IOException, SAXException {
		setType(extension);

		mesh = null;

		o.modified();

		if (CTBinaryModel.TYPE_STL.equals(extension)) {
			return importSTL(fr);
		} else if (CTBinaryModel.TYPE_X3D.equals(extension)) {
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
		getBinary().load(new ByteArrayInputStream(sb.toString().getBytes(CTClient.CHARSET)));
		getBinary().setReady();
		return true;
	}

	private boolean importX3D(Reader fr) throws IOException, SAXException {
		StringBuilder sb = readFile(fr);

		String s = sb.toString();
		log.fine("importing string " + s);
		//
		s = s.replace("http://www.web3d.org", findSpecificationsResources());
		log.fine("importing converted string " + s);
		//
		XML xml = new XML(s);
		WData b = new WData(xml);
		log.fine("importing " + b.toText());
		if (importX3D(b)) {
			newBinary();
			getBinary().load(new ByteArrayInputStream(b.toXML().toString().getBytes(CTClient.CHARSET)));
			getBinary().setReady();
			return true;
		} else {
			return false;
		}
	}

	private StringBuilder readFile(Reader fr) throws IOException {
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
			String pathname = u.getFile().toString();
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
		Binary childbinary = this.env.getBinarySource().newBinary(surl, extensions);
		childbinary.importStream(is);
		addChildBinary(childbinary);
		b.setAttribute("url", childbinary.getID().toString());
	}

	private void addChildBinary(Binary binary) {
		childbinaries.add(binary);
	}

	public List<Binary> getChildBinaries() {
		return childbinaries;
	}

	@Override
	public File getModelFile() throws SAXException, IOException {
		File f = null;
		if (getType() != null) {
			f = File.createTempFile("" + System.currentTimeMillis() + "_" + getBinary().getID().toString(),
					"." + getBinary().getExtension());
			if (f != null) {
				if (!f.delete()) {
					log.info("delete failed " + f.getAbsolutePath());
				}

				if (getType().equals(CTBinaryModel.TYPE_X3D)) {
					return getX3DFile(f);
				} else {
					Files.copy(getBinary().getInputStream(), f.toPath());
				}
			}
		}
		return f;
	}

	private File getX3DFile(File f) throws SAXException, IOException {
		InputStream is = getX3DStream();
		if (is != null) {
			Files.copy(is, f.toPath());
			return f;
		} else {
			return null;
		}
	}

	private InputStream getX3DStream() throws SAXException {
		if (isReady()) {
			XML xml;
			try {
				xml = new XML(new InputStreamReader(getBinary().getInputStream()));
				log.info("getModelStream parsing " + xml);
				WData b = new WData(xml);
				// FIXME TODO
				b.find("X3D").setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema-instance");

				convertX3DURLs(b);
				log.info("getModelStream returning " + b.toXML().toString());
				return new BufferedInputStream(
						new ByteArrayInputStream(b.toXML().toString().getBytes(CTClient.CHARSET)));
			} catch (IOException e) {
				log.error(this, "getModelStream", e);
				return null;
			}
		} else {
			return null;
		}
	}

	private void convertX3DURLs(WData b) {
		String surl = b.getAttribute("url");
		if (surl != null) {
			Binary cbin = env.getBinarySource().getOrDownload(new BinaryID(surl));
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
		if (CTBinaryModel.TYPE_STL.equals(getType())) {
			StlMeshImporter i = new StlMeshImporter();
			try {
				i.setFile(getModelFile());
				mesh = i.getImport();
			} catch (SAXException | IOException e) {
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
}
