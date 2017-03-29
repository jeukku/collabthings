package org.collabthings.app;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.factory.CTObjectFactory;
import org.collabthings.impl.CTClientImpl;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTPart;
import org.collabthings.util.CTTask;
import org.collabthings.util.LLog;

import waazdoh.client.WClient;
import waazdoh.client.WClientListener;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.common.BeanStorage;
import waazdoh.common.WPreferences;
import waazdoh.common.client.WRestServiceClient;
import waazdoh.cp2p.P2PBinarySource;

public class CTApp {
	private static final String PREFERENCES_PREFIX = "ct";
	private CTClient client;
	//
	private LLog log = LLog.getLogger(this);
	private AppPreferences preferences;
	private String serviceurl;
	private P2PBinarySource binarysource;
	private boolean closed;
	private FileBeanStorage beanstorage;

	private List<CTTask> tasks = new LinkedList<CTTask>();

	public CTApp() throws MalformedURLException {
		preferences = new AppPreferences(CTApp.PREFERENCES_PREFIX);
		serviceurl = preferences.get(WPreferences.SERVICE_URL, "");
		beanstorage = new FileBeanStorage(preferences);
		binarysource = new P2PBinarySource(preferences, beanstorage, true);

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runTasks();
				} catch (InterruptedException e) {
					log.error(this, "runTasks", e);
				}
			}
		});
		t.start();
	}

	public void addClientListener(WClientListener listener) {
		getLClient().getClient().addListener(listener);
	}

	public synchronized CTClient getLClient() {
		if (client == null) {
			client = new CTClientImpl(preferences, binarysource, beanstorage,
					new WRestServiceClient(serviceurl, beanstorage));
		}
		return client;
	}

	public WClient getWClient() {
		return getLClient().getClient();
	}

	public void close() {
		log.info("Closing app");
		getLClient().stop();
		binarysource.close();
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public CTPart newPart() {
		return getLClient().getObjectFactory().getPart();
	}

	public boolean isServiceAvailable() {
		return getLClient().getClient().isRunning();
	}

	public CTFactory newFactory() {
		return getLClient().getObjectFactory().getFactory();
	}

	public BeanStorage getBeanStorage() {
		return this.beanstorage;
	}

	public CTObjectFactory getObjectFactory() {
		return getLClient().getObjectFactory();
	}

	private void runTasks() throws InterruptedException {
		synchronized (tasks) {
			while (!isServiceAvailable()) {
				tasks.wait(100);
			}
			while (isClosed()) {
				tasks.wait(100);
			}

			while (!isClosed()) {
				if (tasks.size() > 0) {
					CTTask task = tasks.remove(0);
					task.run();
				} else {
					tasks.wait(100);
				}
			}
		}
	}

	public void addTask(CTTask task) {
		synchronized (tasks) {
			tasks.add(task);
		}
	}
}
