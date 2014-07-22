package org.libraryofthings.environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.model.MID;
import waazdoh.util.ConditionWaiter;
import waazdoh.util.MStringID;

public class LOTRunEnvironmentImpl implements RunEnvironment {
	private Map<String, LOTPartState> parts = new HashMap<String, LOTPartState>();
	private Map<String, LOTScript> scripts = new HashMap<String, LOTScript>();
	private Map<String, LOTToolState> tools = new HashMap<String, LOTToolState>();
	private Map<String, String> params = new HashMap<String, String>();
	private List<LOTTask> tasks = new LinkedList<LOTTask>();
	private List<LOTTask> runningtasks = new LinkedList<LOTTask>();
	private List<LOTToolUser> toolusers = new LinkedList<LOTToolUser>();
	private LOTPool pool = new LOTPool();

	private LOTClient client;
	private RunEnvironment parent;
	private List<RunEnvironment> children = new LinkedList<>();
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

	public LOTRunEnvironmentImpl(RunEnvironment runenv, final LOTEnvironment e) {
		this.parent = runenv;
		this.environment = e;

		parent.addChild(this);
		this.client = runenv.getClient();
	}

	@Override
	public String toString() {
		return "LOTRunEnvironmentImpl[" + this.name + "]";
	}

	@Override
	public String getInfo() {
		StringBuffer s = new StringBuffer();
		s.append("LOTRunEnvironment(" + name + ");");
		s.append("pool :" + pool + ";");
		//
		s.append("runningtasks: ");
		for (LOTTask t : runningtasks) {
			s.append("" + t + ";");
		}
		s.append("tasks: ");
		for (LOTTask t : this.tasks) {
			s.append("" + t + ";");
		}
		//
		s.append("parameters:");
		for (String p : params.keySet()) {
			s.append(p + " -> " + getParameter(p) + ";");
		}
		s.append("toolusers:");
		for (LOTToolUser tu : toolusers) {
			s.append(tu + ";");
		}
		s.append("parts:");
		for (String partkey : parts.keySet()) {
			s.append(partkey + " -> " + getPart(partkey) + ";");
		}
		s.append("tools:");
		for (String toolname : tools.keySet()) {
			s.append(toolname + " -> " + getTool(toolname) + ";");
		}
		return s.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean step(double dtime) {
		checkTasks();

		for (LOTToolUser tooluser : toolusers) {
			tooluser.step(dtime);
		}
		//
		List<RunEnvironment> listchildren = null;
		synchronized (children) {
			if (!children.isEmpty()) {
				listchildren = new LinkedList<>(children);
			}
		}
		if (listchildren != null) {
			for (RunEnvironment runenv : listchildren) {
				runenv.step(dtime);
			}
		}
		//
		return isRunning();
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

		for (RunEnvironment child : children) {
			if (!child.isReady()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addChild(RunEnvironment chileenv) {
		synchronized (children) {
			children.add(chileenv);
			chileenv.addListener((runenv, task) -> {
				fireTaskFailed(runenv, task);
			});
		}
	}

	public boolean isRunning() {
		return !stopped;
	}

	@Override
	public void stop() {
		log.info("Stop");
		stopped = true;
	}

	@Override
	public RunEnvironment getParent() {
		return parent;
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
		if (value == null && parent != null) {
			value = parent.getParameter(string);
		}

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
	public LOTTask addTask(final LOTScript s, final Object... params) {
		StringBuffer sb = new StringBuffer();
		sb.append("addTask " + s + "\n");
		sb.append("\tparameters: ");
		for (Object object : params) {
			sb.append("\t\t" + object + "\n");
		}
		log.info(sb.toString());
		//
		LOTTask task = new LOTTask(client, s, params);
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

	@Override
	public LOTPartState getPartState(String s) {
		return parts.get(s);
	}

	@Override
	public LOTPart getPart(String s) {
		return client.getObjectFactory().getPart(new MStringID(s));
	}

	@Override
	public LOTPartState newPart() {
		LOTPartState partstate = new LOTPartState(client, client
				.getObjectFactory().getPart());
		return partstate;
	}

	@Override
	public void addScript(String string, LOTScript script) {
		scripts.put(string, script);
	}

	public LOTScript getScript(String name) {
		LOTScript s = scripts.get(name);
		if (s == null && parent != null) {
			s = parent.getScript(name);
		}
		if (s == null && environment != null) {
			s = environment.getScript(name);
		}
		return s;
	}

	@Override
	public LOTToolState addTool(String id, LOTTool tool) {
		LOTToolState toolstate = new LOTToolState(this, tool);
		tools.put(id, toolstate);
		return toolstate;
	}

	@Override
	public LOTToolState getTool(String id) {
		LOTToolState tool = findTool(id);
		if (tool != null) {
			new ConditionWaiter(() -> !tool.isInUse() || !isRunning(), 0);
			return tool;
		} else {
			return null;
		}
	}

	private LOTToolState findTool(String id) {
		LOTToolState toolstate = tools.get(id);
		if (toolstate == null && parent != null) {
			toolstate = parent.getTool(id);
		}
		if (toolstate == null && environment != null) {
			// TODO Not sure about this at all. Should a ToolState created on
			// the fly like this?
			// What should toolstates current location be?
			LOTTool tool = environment.getTool(id);
			if (tool != null) {
				return addTool(id, tool);
			}
		}
		return toolstate;
	}

	@Override
	public void addToolUser(LOTToolUser tooluser) {
		this.toolusers.add(tooluser);
	}

	@Override
	public void requestMove(LOTToolState lotToolState, LVector l, LVector n) {
		LOTToolUser tooluser = getToolUser(lotToolState, l);
		tooluser.setTool(lotToolState);
		tooluser.move(l, n);
	}

	@Override
	public LOTToolUser getToolUser(LOTToolState lotToolState, LVector l) {
		for (LOTToolUser tooluser : toolusers) {
			return tooluser;
		}
		if (parent != null) {
			// TODO should this be possible?
			return parent.getToolUser(lotToolState, l);
		}
		return null;
	}

	public LVector getVector(double x, double y, double z) {
		return new LVector(x, y, z);
	}

	@Override
	public LOTClient getClient() {
		return client;
	}

	@Override
	public LOTPool getPool() {
		return pool;
	}
}
