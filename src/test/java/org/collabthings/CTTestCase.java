package org.collabthings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.collabthings.impl.CTClientImpl;
import org.collabthings.model.CTObject;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import junit.framework.TestCase;
import waazdoh.client.BinarySource;
import waazdoh.client.ipfs.IPFSRunner;
import waazdoh.client.ipfs.IPFSServiceClient;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.client.utils.ConditionWaiter;
import waazdoh.client.utils.StaticTestPreferences;
import waazdoh.client.utils.WPreferences;

public class CTTestCase extends TestCase {
	private static final int DEFAULT_WAITTIME = 100;
	private static final int MAX_OBJECT_WAITTIME = 60000;
	private static final String PREFERENCES_RUNAGAINSTSERVICE = "ct.test.useservice";
	//
	public static final double ACCEPTED_DIFFERENCE = 0.000001;

	protected String cubemodelpath = "/models/cube.x3d";

	//
	private Set<CTClient> clients = new HashSet<CTClient>();
	protected LLog log = LLog.getLogger(this);
	private int usercounter = 0;
	private FileBeanStorage beanstorage;
	protected CTClient clientb;
	protected CTClient clienta;
	protected long starttime;
	private boolean enablenetwork = true;

	@Override
	protected void tearDown() throws Exception {
		log.info("**************** STOP TEST " + getName() + " ************** ");

		// startThreadChecker();

		StaticTestPreferences.clearPorts();
		for (CTClient e : clients) {
			log.info("** stopping " + e + " of " + clients);
			e.stop();
		}

		log.info("**************** STOPPED TEST " + getName() + " ************** ");

		clienta = null;
		clientb = null;
	}

	protected void disableNetwork() {
		this.enablenetwork = false;
	}

	@Override
	protected void setUp() throws Exception {
		log.info("**************** SETUP TEST " + getName() + " ************** ");
		super.setUp();

		starttime = System.currentTimeMillis();
	}

	protected void createTwoClients() {
		clientb = getNewClient(true);
		clienta = getNewClient();
		assertNotNull(clienta);
		assertNotNull(clientb);

		ConditionWaiter.wait(() -> {
			return clientb.isRunning() && clienta.isRunning() && clienta.getBinarySource().isRunning()
					&& clientb.isRunning();
		}, getWaitTime());
	}

	public CTClient getNewClient() {
		return getNewClient(false);
	}

	public CTClient getNewClient(boolean forcebind) {
		// do not bind if the first one.
		boolean bind = usercounter > 0 || forcebind ? true : false;

		String username = "test_username_" + (usercounter) + "@localhost";
		usercounter++;
		try {
			return getNewEnv(username, bind && enablenetwork);
		} catch (MalformedURLException | SAXException e) {
			assertNull(e);
			return null;
		}
	}

	public CTClient getNewEnv(String username, boolean bind) throws MalformedURLException, SAXException {
		//
		WPreferences p = new StaticTestPreferences("cttests", username);
		beanstorage = new FileBeanStorage(p);

		String ipfspath = p.get(IPFSServiceClient.IPFS_LOCALPATH, WPreferences.LOCAL_PATH_DEFAULT + "." + username + "/ipfs");
		IPFSRunner runner = new IPFSRunner(p, username);

		if (runner.run()) {
			BinarySource binarysource = enablenetwork ? getBinarySource(p, bind) : new StaticBinarySource();
			CTClient c = new CTClientImpl(p, binarysource, beanstorage, new IPFSServiceClient(p));
			return c;
		} else {
			return null;
		}
	}

	public BinarySource getBinarySource(WPreferences p, boolean bind) {
		BinarySource testsource = new StaticBinarySource();

		return testsource;
	}

	private String getSession(WPreferences p) {
		return p.get(WPreferences.PREFERENCES_SESSION, "");
	}

	public void testTrue() {
		assertTrue(true);
	}

	public void waitObject(CTObject obj) {
		long st = System.currentTimeMillis();
		while (!obj.isReady()) {
			//
			doWait(DEFAULT_WAITTIME);
			if (System.currentTimeMillis() - st > MAX_OBJECT_WAITTIME) {
				throw new RuntimeException("Giving up");
			}
		}
	}

	private synchronized void doWait(int i) {
		try {
			wait(i);
		} catch (InterruptedException e) {
			log.error(this, "doWait", e);
		}
	}

	protected String loadATestScript(String string) throws IOException {
		String path = "src/test/js/" + string;
		return loadTextFile(path);
	}

	protected String loadATestFile(String s) throws FileNotFoundException, IOException {
		String path = "src/test/resources/" + s;
		return loadTextFile(path);
	}

	private String loadTextFile(String path) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		StringWriter sw = new StringWriter();
		//
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			sw.write(line);
			sw.write("\n");
		}

		br.close();
		sw.close();
		return sw.getBuffer().toString();
	}

	protected void assertReallyClose(Vector3f a, Vector3f b) {
		assertReallyClose(a.x, b.x);
		assertReallyClose(a.y, b.y);
		assertReallyClose(a.z, b.z);
	}

	protected void assertReallyClose(double valuea, double valueb) {
		assertTrue("expecting " + valuea + ", but is " + valueb, Math.abs(valuea - valueb) < ACCEPTED_DIFFERENCE);
	}

	protected String readString(InputStream inputStream) throws IOException {
		InputStreamReader r = new InputStreamReader(inputStream);
		StringBuilder sb = new StringBuilder();
		char cs[] = new char[20000];
		while (true) {
			int c = r.read(cs);
			if (c <= 0) {
				break;
			}
			sb.append(cs, 0, c);
		}
		return sb.toString();
	}

	private int getWaitTime() {
		return 40000;
	}

}
