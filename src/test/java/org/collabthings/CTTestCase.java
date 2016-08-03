package org.collabthings;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.collabthings.impl.CTClientImpl;
import org.collabthings.model.CTObject;
import org.collabthings.util.LLog;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import junit.framework.TestCase;
import waazdoh.client.BinarySource;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.client.utils.ConditionWaiter;
import waazdoh.client.utils.ThreadChecker;
import waazdoh.common.WPreferences;
import waazdoh.common.client.RestServiceClient;
import waazdoh.common.client.ServiceClient;
import waazdoh.common.testing.StaticService;
import waazdoh.common.testing.StaticTestPreferences;
import waazdoh.common.vo.AppLoginVO;
import waazdoh.common.vo.StorageAreaVO;
import waazdoh.cp2p.P2PBinarySource;
import waazdoh.cp2p.P2PServer;

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

	private void startThreadChecker() {
		new ThreadChecker(() -> {
			boolean running = false;
			for (CTClient e : clients) {
				if (e.isRunning()) {
					running = true;
				}
			}

			return running;
		});
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

	public CTClient getNewEnv(String email, boolean bind) throws MalformedURLException, SAXException {
		//
		WPreferences p = new StaticTestPreferences("cttests", email);

		BinarySource binarysource = enablenetwork ? getBinarySource(p, bind) : null;
		CTClient c = new CTClientImpl(p, binarysource, beanstorage, getTestService(email, p, binarysource));

		boolean setsession = c.getClient().setSession(getSession(p));
		if (setsession) {
			clients.add(c);

			if (binarysource != null) {
				ConditionWaiter.wait(() -> {
					return binarysource.isReady();
				}, 1000);
			}

			return c;
		} else {
			AppLoginVO applogin = c.getClient().requestAppLogin();
			String apploginid = applogin.getId();
			String url = applogin.getUrl();
			String apploginurl = url + (url.charAt(url.length() - 1) == '/' ? "" : "/") + apploginid;
			log.info("applogin url " + apploginurl);

			try {
				Desktop.getDesktop().browse(new URI(apploginurl + "?username=" + email));
			} catch (IOException | URISyntaxException e) {
				log.error(this, "getAppLogin failed to open browser " + url, e);
			}

			ConditionWaiter.wait(() -> {
				AppLoginVO al = c.getClient().checkAppLogin(apploginid);
				if (al.getSessionid() == null) {
					doWait(4000);
				}
				return al.getSessionid() != null;
			}, 1000000);

			applogin = c.getClient().checkAppLogin(applogin.getId());

			if (applogin != null && applogin.getSessionid() != null) {
				p.set("session", applogin.getSessionid());

				if (binarysource != null) {
					ConditionWaiter.wait(() -> {
						return binarysource.isReady();
					}, 1000);
				}

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

	private ServiceClient getTestService(String username, WPreferences p, BinarySource source) throws SAXException {
		if (p.getBoolean(CTTestCase.PREFERENCES_RUNAGAINSTSERVICE, false)) {
			RestServiceClient client = new RestServiceClient(p.get(WPreferences.SERVICE_URL, "unknown_service"),
					beanstorage);
			if (client.getUsers().requestAppLogin() != null) {
				return client;
			}
		}
		StaticService mockservice = new StaticService(username);
		mockservice.getStorageArea()
				.write(new StorageAreaVO("/public/LOT/settings/ct.javascript.forbiddenwords", "forbiddenword"));
		return mockservice;
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
