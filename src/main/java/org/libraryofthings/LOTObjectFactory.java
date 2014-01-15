package org.libraryofthings;

import org.libraryofthings.model.LOTTask;

import waazdoh.cutils.MStringID;

public interface LOTObjectFactory {

	LOTTask getTask(MStringID taskid);

}
