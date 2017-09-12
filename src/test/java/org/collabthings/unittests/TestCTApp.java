package org.collabthings.unittests;

import java.io.IOException;
import java.net.MalformedURLException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.app.CTApp;
import org.xml.sax.SAXException;

import waazdoh.common.WTimedFlag;

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

	public void testAdd() throws IOException, SAXException {
		CTApp app = new CTTestApp();

		assertNotNull(app.newPart());
		assertNotNull(app.newFactory());
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
