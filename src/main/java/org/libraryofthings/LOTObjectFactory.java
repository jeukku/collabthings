package org.libraryofthings;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTTask;

import waazdoh.cutils.MStringID;

public interface LOTObjectFactory {

	LOTTask getTask(MStringID taskid);

	LOTPart getPart(MStringID partid);

	LOTPart getPart();

}
