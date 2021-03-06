/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.app;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.core.BeanStorage;
import org.collabthings.core.WClient;
import org.collabthings.core.WClientListener;
import org.collabthings.core.ipfs.IPFSServiceClient;
import org.collabthings.core.storage.local.FileBeanStorage;
import org.collabthings.core.utils.WPreferences;
import org.collabthings.factory.CTObjectFactory;
import org.collabthings.impl.CTClientImpl;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTPart;
import org.collabthings.util.CTTask;
import org.collabthings.util.LLog;

public class CTApp {
	private static final String PREFERENCES_PREFIX = "ct";
	private CTClient client;
	//
	private LLog log = LLog.getLogger(this);
	private AppPreferences preferences;
	private boolean closed;
	private FileBeanStorage beanstorage;

	private List<CTTask> tasks = new LinkedList<>();
	private Thread tasksrunner;

	public CTApp() throws MalformedURLException {
		preferences = new AppPreferences(CTApp.PREFERENCES_PREFIX);
		beanstorage = new FileBeanStorage(preferences);
	}

	private void startTasks() {
		if (tasksrunner == null) {
			tasksrunner = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						runTasks();
					} catch (InterruptedException e) {
						log.error(this, "runTasks", e);
						Thread.currentThread().interrupt();
					}
				}
			}, "runTasks");
			tasksrunner.start();
		}
	}

	public void addClientListener(WClientListener listener) {
		getLClient().getClient().addListener(listener);
	}

	public synchronized CTClient getLClient() {
		if (client == null) {
			IPFSServiceClient service = new IPFSServiceClient(preferences);
			service.getObjects().addBeanStorage(beanstorage);
			client = new CTClientImpl(preferences, beanstorage, service);
		}
		return client;
	}

	public WClient getWClient() {
		return getLClient().getClient();
	}

	public void close() {
		log.info("Closing app");
		closed = true;
		getLClient().stop();
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

			while (!isClosed()) {
				if (!tasks.isEmpty()) {
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
			startTasks();
			tasks.add(task);
		}
	}

	public WPreferences getPreferences() {
		return preferences;
	}
}
