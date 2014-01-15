package org.libraryofthings;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.model.LOTTask;

import waazdoh.cutils.MStringID;

public class LOTObjectFactoryImPl implements LOTObjectFactory {

	private LOTEnvironment env;
	private List<LOTTask> tasks = new LinkedList<LOTTask>();

	public LOTObjectFactoryImPl(LOTEnvironment env) {
		this.env = env;
	}

	@Override
	public LOTTask getTask(MStringID taskid) {
		for (LOTTask task : tasks) {
			if (task.getServiceObject().getID().equals(taskid)) {
				return task;
			}
		}
		//
		LOTTask task = new LOTTask(env, taskid);
		tasks.add(task);
		return task;
	}
}
