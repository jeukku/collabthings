package org.collabthings.environment.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.LOTScriptRunner;
import org.collabthings.environment.LOTTask;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import waazdoh.common.ObjectID;

public class LOTRunEnvironmentImpl implements LOTRunEnvironment {
	private Map<String, String> params = new HashMap<String, String>();
	private List<LOTTask> tasks = new LinkedList<LOTTask>();
	private List<LOTTask> runningtasks = new LinkedList<LOTTask>();
	private Map<String, LOTRuntimeObject> objects = new HashMap<>();

	private LOTClient client;
	//
	private boolean stopped;
	//
	private List<RunEnvironmentListener> listeners = new LinkedList<>();
	private LOTEnvironment environment;
	private String name;
	//
	private LLog log = LLog.getLogger(this);

	public LOTRunEnvironmentImpl(final LOTClient nclient, final LOTEnvironment e) {
		this.client = nclient;
		this.environment = e;
	}

	@Override
	public String toString() {
		return "LOTRunEnvironmentImpl[" + this.name + "]";
	}

	@Override
	public void recordEvent(LOTRuntimeObject runo, String string,
			LOTValues callvalues) {
		fireEvent(runo, string, callvalues);
	}

	private void fireEvent(LOTRuntimeObject runo, String string,
			LOTValues callvalues) {
		for (RunEnvironmentListener listener : listeners) {
			LOTRuntimeEvent e = new LOTRuntimeEvent(runo, string, callvalues);
			listener.event(e);
		}
	}

	@Override
	public PrintOut printOut() {
		PrintOut po = new PrintOut();

		po.append("LOTRunEnvironment(" + name + ");");
		//
		po.append("runningtasks: ");
		for (LOTTask t : new LinkedList<>(runningtasks)) {
			po.append(1, "" + t);
		}

		po.append("tasks: ");
		for (LOTTask t : new LinkedList<>(this.tasks)) {
			po.append(1, "" + t);
		}
		//
		po.append("parameters:");
		for (String p : params.keySet()) {
			po.append(1, p + " -> " + getParameter(p) + ";");
		}

		po.append("objects:");
		for (String p : objects.keySet()) {
			LOTRuntimeObject o = objects.get(p);
			po.append(1, o.printOut());
		}

		return po;
	}

	@Override
	public String getInfo() {
		return printOut().toText();
	}

	@Override
	public LOTEnvironment getEnvironment() {
		return this.environment;
	}

	public void setName(String name) {
		this.name = name;
	}

	private synchronized void checkTasks() {
		if (!isTasksEmpty()) {
			final LOTTask task = getTasks().get(0);
			removeTask(task);
			runningtasks.add(task);
			//
			new Thread(() -> runTask(task), "RunEnv run task " + task).start();
		}
	}

	private boolean isTasksEmpty() {
		return this.tasks.isEmpty();
	}

	private void runTask(LOTTask task) {
		log.info("Running task " + task);
		if (!task.run()) {
			fireTaskFailed(task);
		}

		log.info("Task run " + task);

		synchronized (this) {
			runningtasks.remove(task);
		}
	}

	private void fireTaskFailed(LOTTask task) {
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
	public void setParameter(String key, ObjectID id) {
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
	public LOTTask addTask(LOTScriptRunner s) {
		return addTask(s, new LOTValues());
	}

	@Override
	public LOTTask addTask(final LOTScriptRunner s, final LOTValues values) {
		StringBuilder sb = new StringBuilder();
		sb.append("addTask " + s + "\n");
		if (values != null) {
			sb.append("\tparameters: ");
			for (String valuename : values.keys()) {
				sb.append("\t\t" + valuename + " -> " + values.get(valuename)
						+ "\n");
			}
		} else {
			sb.append("Parameters null\n");
		}

		log.info(sb.toString());
		//
		LOTTaskImpl task = new LOTTaskImpl(s, values);
		synchronized (tasks) {
			tasks.add(task);
		}
		return task;
	}

	@Override
	public List<LOTTask> getTasks() {
		synchronized (tasks) {
			return new LinkedList<>(tasks);
		}
	}

	private void removeTask(LOTTask task) {
		synchronized (tasks) {
			tasks.remove(task);
		}
	}

	public LVector getVector(double x, double y, double z) {
		return new LVector(x, y, z);
	}

	@Override
	public LOTClient getClient() {
		return client;
	}

	@Override
	public void addRunObject(String string, LOTRuntimeObject runtimeo) {
		this.objects.put(string, runtimeo);
	}

	@Override
	public Set<LOTRuntimeObject> getRunObjects() {
		return new HashSet<>(this.objects.values());
	}

	@Override
	public LOTRuntimeObject getRunObject(String string) {
		for (LOTRuntimeObject o : getRunObjects()) {
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
		for (LOTRuntimeObject o : this.objects.values()) {
			o.step(dtime);
		}
	}

	@Override
	public void stop() {
		for (LOTRuntimeObject o : this.objects.values()) {
			o.stop();
		}
	}
}
