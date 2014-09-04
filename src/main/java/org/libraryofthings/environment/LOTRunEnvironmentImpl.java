package org.libraryofthings.environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTValues;

import waazdoh.client.model.MID;

public class LOTRunEnvironmentImpl implements RunEnvironment {
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
	public String getInfo() {
		StringBuilder s = new StringBuilder();
		s.append("LOTRunEnvironment(" + name + ");");
		//
		s.append("runningtasks: ");
		for (LOTTask t : new LinkedList<>(runningtasks)) {
			s.append("" + t + ";");
		}
		s.append("tasks: ");
		for (LOTTask t : new LinkedList<LOTTask>(this.tasks)) {
			s.append("" + t + ";");
		}
		//
		s.append("parameters:");
		for (String p : params.keySet()) {
			s.append(p + " -> " + getParameter(p) + ";");
		}

		s.append("objects:");
		for (String p : objects.keySet()) {
			s.append(p + " -> " + objects.get(p) + ";");
		}

		return s.toString();
	}

	@Override
	public LOTEnvironment getEnvironment() {
		return this.environment;
	}

	public void setName(String name) {
		this.name = name;
	}

	private synchronized void checkTasks() {
		if (!getTasks().isEmpty()) {
			final LOTTask task = getTasks().get(0);
			removeTask(task);
			runningtasks.add(task);
			//
			new Thread(() -> runTask(task)).start();
		}
	}

	private void runTask(LOTTask task) {
		if (!task.run(this)) {
			fireTaskFailed(this, task);
		}

		synchronized (this) {
			runningtasks.remove(task);
		}
	}

	private void fireTaskFailed(RunEnvironment runenv, LOTTask task) {
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

	public boolean isRunning() {
		return !stopped;
	}

	@Override
	public LLog log() {
		return log;
	}

	@Override
	public void setParameter(String key, MID id) {
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
	public LOTTask addTask(LOTScript script) {
		return addTask(script, null, new LOTValues());
	}

	@Override
	public LOTTask addTask(final LOTScript s,
			final LOTRuntimeObject runtimeobject, final LOTValues values) {
		StringBuilder sb = new StringBuilder();
		sb.append("addTask " + s + "\n");
		if (values != null) {
			sb.append("\tparameters: ");
			for (String name : values.keys()) {
				sb.append("\t\t" + name + " -> " + values.get(name) + "\n");
			}
		} else {
			sb.append("Parameters null\n");
		}

		log.info(sb.toString());
		//
		LOTTask task = new LOTTask(s, runtimeobject, values);
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

	public void addRunObject(String string, LOTRuntimeObject runtimeo) {
		this.objects.put(string, runtimeo);
	}

	@Override
	public Set<LOTRuntimeObject> getRunObjects() {
		return new HashSet<>(this.objects.values());
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
