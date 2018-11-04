package org.collabthings.unittests;

import java.io.IOException;
import java.net.MalformedURLException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.app.CTApp;
import org.collabthings.core.utils.WTimedFlag;
import org.xml.sax.SAXException;

public final class TestCTApp extends CTTestCase {

	public void testTask() throws IOException, SAXException {
		CTApp app = new CTTestApp();

		WTimedFlag flag = new WTimedFlag(4000);

		app.addTask(() -> {
			flag.trigger();
		});

		flag.waitTimer();

		assertTrue(flag.wasTriggerCalled());
	}

	public void testClient() throws MalformedURLException {
		CTApp app = new CTTestApp();
		assertNotNull(app.getWClient());
		assertNotNull(app.getLClient());
	}

	public void testAdd() throws IOException, SAXException {
		CTApp app = new CTTestApp();

		assertNotNull(app.newPart());
		assertNotNull(app.newFactory());
	}

	public void testClose() throws IOException, SAXException {
		CTApp app = new CTTestApp();
		WTimedFlag flag = new WTimedFlag(4000);

		app.addTask(() -> {
			flag.trigger();
		});

		flag.waitTimer();

		app.close();
		Thread.getAllStackTraces().keySet().forEach(t -> t.interrupt());

		assertTrue(app.isClosed());
	}

	private class CTTestApp extends CTApp {
		private CTClient appclient;

		public CTTestApp() throws MalformedURLException {
			super();
		}

		@Override
		public synchronized CTClient getLClient() {
			if (appclient == null) {
				appclient = getNewClient();
			}
			return appclient;
		}
	}
}
