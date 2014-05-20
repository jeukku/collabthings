package org.libraryofthings.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.xml.sax.SAXException;

import waazdoh.client.Binary;
import waazdoh.client.MBinaryID;
import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;
import waazdoh.cutils.xml.XML;

public final class LOT3DModel implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "model3d";
	private static final String NAME = "name";
	private static final String BINARYID = "binaryid";
	//
	private final ServiceObject o;
	private String name = "";
	private MBinaryID binaryid;
	private final LOTEnvironment env;
	private LLog log = LLog.getLogger(this);
	private List<Binary> childbinaries = new LinkedList<Binary>();

	public LOT3DModel(final LOTEnvironment nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
		newBinary();
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(NAME, name);
		b.addValue(BINARYID, getBinaryID().toString());
		//
		JBean bb = b.add("binaries");
		for (Binary binary : childbinaries) {
			bb.add("binary").setValue(binary.getID().toString());
		}
		return b;
	}

	private MBinaryID getBinaryID() {
		return binaryid;
	}

	@Override
	public boolean parseBean(JBean bean) {
		name = bean.getValue("name");
		binaryid = new MBinaryID(bean.getIDValue(BINARYID));
		//
		JBean bs = bean.get("binaries");
		List<JBean> bchildbinaries = bs.getChildren();
		for (JBean bchildbinary : bchildbinaries) {
			MBinaryID childbinaryid = new MBinaryID(bchildbinary.getText());
			addChildBinary(env.getBinarySource().getOrDownload(childbinaryid));
		}
		//
		return true;
	}

	public ServiceObject getServiceObject() {
		return o;
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

	public Binary getBinary() {
		return this.env.getBinarySource().getOrDownload(binaryid);
	}

	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return name;
	}

	public Binary newBinary() {
		String comment = "LOT3DModel";
		String extension = "xml";
		binaryid = env.getBinarySource().newBinary(comment, extension).getID();
		return getBinary();
	}

	public void publish() {
		o.publish();
		getBinary().publish();
		for (Binary b : childbinaries) {
			b.publish();
		}
	}

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

	public boolean importModel(File file) throws SAXException, IOException {
		XML xml = new XML(new FileReader(file));
		JBean b = new JBean(xml);
		log.info("importing " + b.toText());
		if (importModel(b)) {
			newBinary();
			getBinary().set(0, b.toXML().toString().getBytes());
			getBinary().setReady();
			return true;
		} else {
			return false;
		}
	}

	private boolean importModel(JBean b) throws IOException {
		String urlattribute = b.getAttribute("url");
		if (urlattribute != null) {
			importReplaceURLAttribute(b, urlattribute);
		}
		//
		List<JBean> cs = b.getChildren();
		for (JBean cb : cs) {
			if (!importModel(cb)) {
				return false;
			}
		}
		//
		return true;
	}

	private void importReplaceURLAttribute(JBean b, String urlattribute)
			throws IOException, FileNotFoundException {
		log.info("Found URL attribute " + urlattribute);
		log.info("current dir " + new File(".").getAbsolutePath());
		//
		String surl = new StringTokenizer(urlattribute).nextToken();
		surl = surl.replace("\"", "");
		log.info("loading binary " + surl);
		File file = new File(surl);
		//
		String extensions = surl.substring(surl.lastIndexOf('.') + 1);
		Binary childbinary = this.env.getBinarySource().newBinary(surl,
				extensions);
		childbinary.importStream(new FileInputStream(file));
		addChildBinary(childbinary);
		b.setAttribute("url", childbinary.getID().toString());
	}

	private void addChildBinary(Binary binary) {
		childbinaries.add(binary);
	}

	public List<Binary> getChildBinaries() {
		return childbinaries;
	}

	public File getModelFile() throws SAXException, IOException {
		InputStream is = getModelStream();
		File f = File.createTempFile("" + System.currentTimeMillis() + "_"
				+ getBinary().getID().toString(), getBinary().getExtension());
		f.delete();
		Files.copy(is, f.toPath());
		return f;
	}

	public InputStream getModelStream() throws SAXException {
		if (isReady()) {
			XML xml = new XML(new String(getBinary().asByteBuffer()));
			log.info("getModelStream parsing " + xml);
			JBean b = new JBean(xml);
			// FIXME TODO
			b.find("X3D").setAttribute("xmlns:xsd",
					"http://www.w3.org/2001/XMLSchema-instance");

			convertURLs(b);
			log.info("getModelStream returning " + b.toXML().toString());
			return new BufferedInputStream(new ByteArrayInputStream(b.toXML()
					.toString().getBytes()));
		} else {
			return null;
		}
	}

	private void convertURLs(JBean b) {
		String surl = b.getAttribute("url");
		if (surl != null) {
			Binary cbin = env.getBinarySource().getOrDownload(
					new MBinaryID(surl));
			b.setAttribute("url", env.getBinarySource().getBinaryFile(cbin)
					.getAbsolutePath());
		}
		//
		List<JBean> cbs = b.getChildren();
		for (JBean cb : cbs) {
			convertURLs(cb);
		}
	}
}
