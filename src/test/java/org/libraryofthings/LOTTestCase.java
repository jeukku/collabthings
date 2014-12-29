package org.libraryofthings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.libraryofthings.impl.LOTClientImpl;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTObject;
import org.xml.sax.SAXException;

import waazdoh.client.WClientAppLogin;
import waazdoh.client.binaries.BinarySource;
import waazdoh.client.model.WService;
import waazdoh.cp2p.P2PBinarySource;
import waazdoh.service.rest.RestServiceClient;
import waazdoh.testing.ServiceMock;
import waazdoh.testing.StaticTestPreferences;
import waazdoh.util.ConditionWaiter;
import waazdoh.util.MPreferences;
import waazdoh.util.MStringID;

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

	@Override
	protected void tearDown() throws Exception {
		StaticTestPreferences.clearPorts();
		for (LOTClient e : clients) {
			e.stop();
		}
	}

	@Override
	protected void setUp() throws Exception {
		log.info("**************** SETUP TEST " + getName()
				+ " ************** ");
		super.setUp();
	}

	public LOTClient getNewClient() {
		return getNewClient(false);
	}

	public LOTClient getNewClient(boolean forcebind) {
		// do not bind if the first one.
		boolean bind = usercounter > 0 || forcebind ? true : false;

		String username = "test_username_" + (usercounter) + "@localhost";
		MPreferences p = new StaticTestPreferences("lottests", username);
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
		MPreferences p = new StaticTestPreferences("lottests", email);

		BinarySource binarysource = getBinarySource(p, bind);
		LOTClient c = new LOTClientImpl(p, binarysource, getTestService(email,
				p, binarysource));

		boolean setsession = c.getClient().setSession(getSession(p));
		if (setsession) {
			clients.add(c);
			return c;
		} else {
			WClientAppLogin applogin = c.getClient().requestAppLogin();
			MStringID apploginid = applogin.getId();
			log.info("applogin url " + applogin.getURL());
			
			new ConditionWaiter(() -> {
				WClientAppLogin al = c.getClient().checkAppLogin(apploginid);
				return al.getSessionId() != null;
			}, 100000);

			applogin = c.getClient().checkAppLogin(applogin.getId());

			if (applogin != null && applogin.getSessionId() != null) {
				p.set("session", applogin.getSessionId());
				return c;
			} else {
				return null;
			}
		}
	}

	public BinarySource getBinarySource(MPreferences p, boolean bind) {
		P2PBinarySource testsource = new P2PBinarySource(p, bind);
		if (bind) {
			testsource.setDownloadEverything(true);
		}
		return testsource;
	}

	private String getSession(MPreferences p) {
		return p.get("session", "");
	}

	private WService getTestService(String username, MPreferences p,
			BinarySource source) throws SAXException {
		if (p.getBoolean(LOTTestCase.PREFERENCES_RUNAGAINSTSERVICE, false)) {
			try {
				RestServiceClient client = new RestServiceClient(p.get(
						MPreferences.SERVICE_URL, "unknown_service"), source);
				if (client.isConnected()) {
					return client;
				}
			} catch (MalformedURLException e) {
				log.error(this, "getTestService", e);
			}
		}
		ServiceMock mockservice = new ServiceMock(username, source);
		mockservice.writeStorageArea(
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
		StringBuilder sb = new StringBuilder();
		String path = "src/test/js/" + string;
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

}
