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

	public LOTTask(final LOTEnvironment nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
	}

	public LOTTask(final LOTEnvironment nenv, final MStringID id) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
		o.load(id);
	}

	public void save() {
		for (LOTTask task : subtasks) {
			task.save();
		}
		o.save();
	}

	public boolean publish() {
		for (LOTTask task : subtasks) {
			task.publish();
		}

		return o.publish();
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

	public void setName(final String nname) {
		this.name = nname;
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
	public int hashCode() {
		return getBean().hashCode();
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
