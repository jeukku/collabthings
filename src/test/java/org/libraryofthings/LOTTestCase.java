package org.libraryofthings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.libraryofthings.model.LOTObject;
import org.libraryofthings.model.LOTScript;
import org.xml.sax.SAXException;

import waazdoh.client.MBinarySource;
import waazdoh.client.WClientAppLogin;
import waazdoh.client.rest.RestServiceClient;
import waazdoh.cp2p.impl.P2PBinarySource;
import waazdoh.cutils.MPreferences;
import waazdoh.service.CMService;
import waazdoh.testing.ServiceMock;

public class LOTTestCase extends TestCase {
	private static final int DEFAULT_WAITTIME = 100;
	private static final int MAX_OBJECT_WAITTIME = 30000;
	private static final String PREFERENCES_RUNAGAINSTSERVICE = "lot.test.useservice";
	//
	private Set<LOTEnvironment> clients = new HashSet<LOTEnvironment>();
	LLog log = LLog.getLogger(this);
	private int usercounter = 0;

	public LOTEnvironment getNewEnv() throws IOException, SAXException {
		boolean bind = usercounter >= 0 ? true : false;
		TestPreferences p = new TestPreferences("lottesting");
		String username = p.get("useremail" + usercounter, "test_username_"
				+ (usercounter) + "@localhost");
		usercounter++;
		return getNewEnv(username, bind);
	}

	public LOTEnvironment getNewEnv(String email, boolean bind)
			throws MalformedURLException, SAXException {
		//
		TestPreferences p = new TestPreferences(email);
		p.set(LOTScript.PREFERENCES_SCRIPTSPATH, "./");

		MBinarySource binarysource = getBinarySource(p, bind);
		LOTEnvironment c = new LOTEnvironment(p, binarysource, getTestService(
				email, p, binarysource));

		boolean setsession = c.getClient().setSession(getSession(p));
		if (setsession) {
			clients.add(c);
			return c;
		} else {
			WClientAppLogin applogin = c.getClient().requestAppLogin();
			applogin = c.getClient().checkAppLogin(applogin.getId());
			if (applogin != null && applogin.getSessionId() != null) {
				c.getClient().setSession(applogin.getSessionId());
				p.set("session", applogin.getSessionId());
				return c;
			} else {
				return null;
			}
		}
	}

	public MBinarySource getBinarySource(MPreferences p, boolean bind) {
		P2PBinarySource testsource = new P2PBinarySource(p, bind);
		if (bind) {
			testsource.setDownloadEverything(true);
		}
		return testsource;
	}

	private String getSession(TestPreferences p) {
		return p.get("session");
	}

	private CMService getTestService(String username, TestPreferences p,
			MBinarySource source) throws SAXException {
		if (p.getBoolean(LOTTestCase.PREFERENCES_RUNAGAINSTSERVICE, false)) {
			try {
				RestServiceClient client = new RestServiceClient(
						p.get(MPreferences.SERVICE_URL), source);
				if (client.isConnected()) {
					return client;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return new ServiceMock(username, source);
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
			e.printStackTrace();
		}
	}

	protected String loadATestScript(String string) throws IOException {
		StringBuffer sb = new StringBuffer();
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
}
