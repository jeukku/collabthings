package org.libraryofthings.model;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LOTEnvironment;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public final class LOTTask implements ServiceObjectData {
	private static final String VALUE_NAME = "name";
	private static final String BEANNAME = "task";
	//
	private ServiceObject o;
	private String name = "task";
	//
	private List<LOTTask> subtasks = new LinkedList<LOTTask>();
	private LOTEnvironment env;

	public LOTTask(LOTEnvironment env) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
	}

	public LOTTask(LOTEnvironment env, MStringID id) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
		o.load(id);
	}

	public boolean save() {
		for (LOTTask task : subtasks) {
			task.save();
		}
		return o.save();
	}

	public void publish() {
		for (LOTTask task : subtasks) {
			task.publish();
		}
		o.publish();
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUE_NAME, getName());

		JBean btasks = b.add("tasks");

		for (LOTTask task : subtasks) {
			JBean btask = btasks.add("task");
			btask.addValue("id", task.getServiceObject().getID());
		}
		return b;
	}

	@Override
	public boolean parseBean(JBean bean) {
		setName(bean.getValue(VALUE_NAME));
		//
		JBean btasks = bean.get("tasks");
		List<JBean> btaskslist = btasks.getChildren();
		for (JBean btask : btaskslist) {
			MStringID taskid = btask.getIDValue("id");
			LOTTask task = env.getObjectFactory().getTask(taskid);
			subtasks.add(task);
		}

		return getName() != null;
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<LOTTask> getSubTasks() {
		LinkedList<LOTTask> list = new LinkedList<LOTTask>();
		list.addAll(subtasks);
		return list;
	}

	public void addSubTask(LOTTask lotTask) {
		this.subtasks.add(lotTask);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LOTTask) {
			LOTTask task = (LOTTask) obj;
			return task.getBean().equals(getBean());
		} else {
			return false;
		}
	}

}
