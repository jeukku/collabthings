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

import junit.framework.TestCase;

import org.collabthings.impl.LOTClientImpl;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTObject;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import waazdoh.client.BinarySource;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.client.utils.ConditionWaiter;
import waazdoh.client.utils.ThreadChecker;
import waazdoh.common.WPreferences;
import waazdoh.common.client.RestServiceClient;
import waazdoh.common.client.ServiceClient;
import waazdoh.common.vo.AppLoginVO;
import waazdoh.cp2p.P2PBinarySource;
import waazdoh.cp2p.P2PServer;
import waazdoh.testing.StaticService;
import waazdoh.testing.StaticTestPreferences;

public class LOTTestCase extends TestCase {
	private static final int DEFAULT_WAITTIME = 100;
	private static final int MAX_OBJECT_WAITTIME = 60000;
	private static final String PREFERENCES_RUNAGAINSTSERVICE = "lot.test.useservice";
	//
	private static final double ACCEPTED_DIFFERENCE = 0.000000000001;

	protected String cubemodelpath = "/models/cube.x3d";

	//
	private Set<LOTClient> clients = new HashSet<LOTClient>();
	LLog log = LLog.getLogger(this);
	private int usercounter = 0;
	private FileBeanStorage beanstorage;
	protected LOTClient clientb;
	protected LOTClient clienta;

	@Override
	protected void tearDown() throws Exception {
		log.info("**************** STOP TEST " + getName() + " ************** ");

		new ThreadChecker(() -> {
			boolean running = false;
			for (LOTClient e : clients) {
				if (e.isRunning()) {
					running = true;
				}
			}

			return running;
		});

		StaticTestPreferences.clearPorts();
		for (LOTClient e : clients) {
			log.info("** stopping " + e + " of " + clients);
			e.stop();
		}

		log.info("**************** STOPPED TEST " + getName()
				+ " ************** ");

		clienta = null;
		clientb = null;
	}

	@Override
	protected void setUp() throws Exception {
		log.info("**************** SETUP TEST " + getName()
				+ " ************** ");
		super.setUp();
	}

	protected void createTwoClients() {
		clientb = getNewClient(true);
		clienta = getNewClient();
		assertNotNull(clienta);
		assertNotNull(clientb);

		new ConditionWaiter(() -> {
			return clientb.isRunning() && clienta.isRunning()
					&& clienta.getBinarySource().isRunning()
					&& clientb.isRunning();
		}, getWaitTime());
	}

	public LOTClient getNewClient() {
		return getNewClient(false);
	}

	public LOTClient getNewClient(boolean forcebind) {
		// do not bind if the first one.
		boolean bind = usercounter > 0 || forcebind ? true : false;

		String username = "test_username_" + (usercounter) + "@localhost";
		WPreferences p = new StaticTestPreferences("lottests", username);
		usercounter++;
		try {
			return getNewEnv(username, bind);
		} catch (MalformedURLException | SAXException e) {
			assertNull(e);
			return null;
		}
	}

	public LOTClient getNewEnv(String email, boolean bind)
			throws MalformedURLException, SAXException {
		//
		WPreferences p = new StaticTestPreferences("lottests", email);

		BinarySource binarysource = getBinarySource(p, bind);
		LOTClient c = new LOTClientImpl(p, binarysource, beanstorage,
				getTestService(email, p, binarysource));

		boolean setsession = c.getClient().setSession(getSession(p));
		if (setsession) {
			clients.add(c);
			return c;
		} else {
			AppLoginVO applogin = c.getClient().requestAppLogin();
			String apploginid = applogin.getId();
			String url = applogin.getUrl();
			log.info("applogin url " + url
					+ (url.charAt(url.length() - 1) == '/' ? "" : "/")
					+ apploginid);

			new ConditionWaiter(() -> {
				AppLoginVO al = c.getClient().checkAppLogin(apploginid);
				return al.getSessionid() != null;
			}, 100000);

			applogin = c.getClient().checkAppLogin(applogin.getId());

			if (applogin != null && applogin.getSessionid() != null) {
				p.set("session", applogin.getSessionid());
				return c;
			} else {
				return null;
			}
		}
	}

	public BinarySource getBinarySource(WPreferences p, boolean bind) {
		beanstorage = new FileBeanStorage(p);
		if (bind) {
			p.set(P2PServer.DOWNLOAD_EVERYTHING, true);
		}
		P2PBinarySource testsource = new P2PBinarySource(p, beanstorage, bind);

		return testsource;
	}

	private String getSession(WPreferences p) {
		return p.get("session", "");
	}

	private ServiceClient getTestService(String username, WPreferences p,
			BinarySource source) throws SAXException {
		if (p.getBoolean(LOTTestCase.PREFERENCES_RUNAGAINSTSERVICE, false)) {
			RestServiceClient client = new RestServiceClient(p.get(
					WPreferences.SERVICE_URL, "unknown_service"), beanstorage);
			if (client.getUsers().requestAppLogin() != null) {
				return client;
			}
		}
		StaticService mockservice = new StaticService(username);
		mockservice.getStorageArea().write(
				"/public/LOT/settings/lot.javascript.forbiddenwords",
				"forbiddenword");
		return mockservice;
	}

	public void testTrue() {
		assertTrue(true);
	}

	public void waitObject(LOTObject obj) {
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

	protected String loadATestFile(String s) throws FileNotFoundException,
			IOException {
		String path = "src/test/resources/" + s;
		return loadTextFile(path);
	}

	private String loadTextFile(String path) throws FileNotFoundException,
			IOException {
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

	protected void assertReallyClose(LVector a, LVector b) {
		assertReallyClose(a.x, b.x);
		assertReallyClose(a.y, b.y);
		assertReallyClose(a.z, b.z);
	}

	protected void assertReallyClose(double valuea, double valueb) {
		assertTrue("expecting " + valuea + ", but is " + valueb,
				Math.abs(valuea - valueb) < ACCEPTED_DIFFERENCE);
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
