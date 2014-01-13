package org.libraryofthings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.libraryofthings.model.LOTObject;
import org.xml.sax.SAXException;

import waazdoh.client.Binary;
import waazdoh.client.MBinaryID;
import waazdoh.client.MBinarySource;
import waazdoh.client.rest.RestClient;
import waazdoh.cp2p.impl.P2PBinarySource;
import waazdoh.cutils.MLogger;
import waazdoh.cutils.MPreferences;
import waazdoh.cutils.MStringID;
import waazdoh.service.CMService;
import waazdoh.testing.ServiceMock;

public class LOTTestCase extends TestCase {
	private static final String LOCALURL = "http://localhost:8080/waazdoh";
	//
	private Set<LOTEnvironment> clients = new HashSet<LOTEnvironment>();
	MLogger log = MLogger.getLogger(this);
	private int usercounter = 0;

	public LOTEnvironment getNewEnv() throws IOException, SAXException {
		boolean bind = usercounter >= 0 ? true : false;
		String username = "test_username_" + (usercounter++) + "@localhost";
		return getNewEnv(username, bind);
	}

	public LOTEnvironment getNewEnv(String email, boolean bind)
			throws MalformedURLException, SAXException {
		//
		TestPreferences p = new TestPreferences(email);
		MBinarySource binarysource = getBinarySource(p, bind);
		LOTEnvironment c = new LOTEnvironment(p, binarysource, getTestService(
				p, binarysource));

		boolean setsession = c.getClient().setUsernameAndSession(email,
				getSession(p));
		if (setsession) {
			clients.add(c);
			return c;
		} else {
			String appidparam = "appid_" + email;
			String sid = p.get(appidparam);
			MStringID id;
			if (sid != null && sid.length() > 0) {
				id = new MStringID(sid);
			} else {
				id = new MStringID();
				p.set(appidparam, id.toString());
			}
			String session = c.getClient()
					.requestAppLogin(email, "testapp", id);
			if (session != null) {
				p.set("session", session);
			}
			return null;
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

	private CMService getTestService(TestPreferences p, MBinarySource source)
			throws SAXException {
		String osname = System.getProperty("os.name").toLowerCase();
		if (osname.indexOf("linux") >= 0) {
			String url = "http://localhost:8080/waazdoh";
			try {
				return new RestClient(url, source);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return new ServiceMock(source);
		}
	}

	public void testTrue() {
		assertTrue(true);
	}

	public void waitObject(LOTObject obj) {
		long st = System.currentTimeMillis();
		while (!obj.isReady()) {
			//
			doWait(100);
			if (System.currentTimeMillis() - st > 15000) {
				throw new RuntimeException("Giving up");
			}
		}
	}

	private synchronized void doWait(int i) {
		try {
			wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
