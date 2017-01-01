package org.collabthings.environment.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.environment.CTScriptRunner;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

import waazdoh.common.WObjectID;

public class CTRunEnvironmentImpl implements CTRunEnvironment {
	private Map<String, String> params = new HashMap<>();
	private List<CTEnvironmentTask> tasks = new ArrayList<>();
	private List<CTEnvironmentTask> runningtasks = new ArrayList<>();
	private Map<String, CTRuntimeObject> objects = new HashMap<>();

	private CTClient client;
	//
	private boolean stopped;
	//
	private List<RunEnvironmentListener> listeners = new ArrayList<>();
	private CTEnvironment environment;
	private String name;
	//
	private LLog log = LLog.getLogger(this);

	public CTRunEnvironmentImpl(final CTClient nclient, final CTEnvironment e) {
		this.client = nclient;
		this.environment = e;
	}

	@Override
	public String toString() {
		return "CTRunEnvironmentImpl[" + this.name + "]";
	}

	@Override
	public void recordEvent(CTRuntimeObject runo, String string, CTValues callvalues) {
		fireEvent(runo, string, callvalues);
	}

	private void fireEvent(CTRuntimeObject runo, String string, CTValues callvalues) {
		for (RunEnvironmentListener listener : listeners) {
			CTRuntimeEvent e = new CTRuntimeEvent(runo, string, callvalues);
			listener.event(e);
		}
	}

	@Override
	public PrintOut printOut() {
		PrintOut po = new PrintOut();

		po.append("CTRunEnvironment(" + name + ");");
		//
		po.append("runningtasks: ");
		for (CTEnvironmentTask t : new ArrayList<>(runningtasks)) {
			po.append(1, "" + t);
		}

		po.append("tasks: ");
		for (CTEnvironmentTask t : new ArrayList<>(this.tasks)) {
			po.append(1, "" + t);
		}
		//
		po.append("parameters:");
		for (String p : params.keySet()) {
			po.append(1, p + " -> " + getParameter(p) + ";");
		}

		po.append("objects:");
		for (String p : objects.keySet()) {
			CTRuntimeObject o = objects.get(p);
			po.append(1, p + " -> " + o.printOut());
		}

		return po;
	}

	@Override
	public String getInfo() {
		return printOut().toText();
	}

	@Override
	public CTEnvironment getEnvironment() {
		return this.environment;
	}

	public void setName(String name) {
		this.name = name;
	}

	private synchronized void checkTasks() {
		if (!isTasksEmpty()) {
			final CTEnvironmentTask task = getTasks().get(0);
			removeTask(task);
			runningtasks.add(task);
			//
			new Thread(() -> runTask(task), "RunEnv run task " + task).start();
		}
	}

	private boolean isTasksEmpty() {
		return this.tasks.isEmpty();
	}

	private void runTask(CTEnvironmentTask task) {
		log.info("Running task " + task);
		if (!task.run()) {
			fireTaskFailed(task);
		}

		log.info("Task run " + task);

		synchronized (this) {
			runningtasks.remove(task);
		}
	}

	private void fireTaskFailed(CTEnvironmentTask task) {
		log.info("Task FAILED! " + task);
		for (RunEnvironmentListener listener : listeners) {
			listener.taskFailed(this, task);
		}
	}

	@Override
	public void addListener(RunEnvironmentListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean isReady() {
		if (!runningtasks.isEmpty() || !tasks.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isRunning() {
		return !stopped;
	}

	@Override
	public LLog log() {
		return log;
	}

	@Override
	public void setParameter(String key, WObjectID id) {
		setParameter(key, id.getStringID().toString());
	}

	@Override
	public String getParameter(String string) {
		String value = params.get(string);

		if (value == null && environment != null) {
			value = environment.getParameter(string);
		}
		return value;
	}

	@Override
	public void setParameter(String key, String value) {
		params.put(key, value);
	}

	@Override
	public CTEnvironmentTask addTask(CTScriptRunner s) {
		return addTask(s, new CTValues());
	}

	@Override
	public CTEnvironmentTask addTask(final CTScriptRunner s, final CTValues values) {
		StringBuilder sb = new StringBuilder();
		sb.append("addTask " + s + "\n");
		if (values != null) {
			sb.append("\tparameters: ");
			for (String valuename : values.keys()) {
				sb.append("\t\t" + valuename + " -> " + values.get(valuename) + "\n");
			}
		} else {
			sb.append("Parameters null\n");
		}

		log.info(sb.toString());
		//
		CTTaskImpl task = new CTTaskImpl(s, values);
		synchronized (tasks) {
			tasks.add(task);
		}
		return task;
	}

	@Override
	public List<CTEnvironmentTask> getTasks() {
		synchronized (tasks) {
			return new ArrayList<>(tasks);
		}
	}

	private void removeTask(CTEnvironmentTask task) {
		synchronized (tasks) {
			tasks.remove(task);
		}
	}

	public Vector3f getVector(double x, double y, double z) {
		return new Vector3f((float) x, (float) y, (float) z);
	}

	@Override
	public CTClient getClient() {
		return client;
	}

	@Override
	public void addRunObject(String string, CTRuntimeObject runtimeo) {
		this.objects.put(string, runtimeo);
	}

	@Override
	public Set<CTRuntimeObject> getRunObjects() {
		return new HashSet<>(this.objects.values());
	}

	@Override
	public CTRuntimeObject getRunObject(String string) {
		for (CTRuntimeObject o : getRunObjects()) {
			if (o.getName().equals(string)) {
				return o;
			}
		}

		return null;
	}

	@Override
	public void step(double dtime) {
		checkTasks();
		//
		for (CTRuntimeObject o : this.objects.values()) {
			o.step(dtime);
		}
	}

	@Override
	public void stop() {
		stopped = true;
		for (CTRuntimeObject o : this.objects.values()) {
			o.stop();
		}
	}
}
