package org.libraryofthings;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTTask;
import org.libraryofthings.model.LOTTool;

import waazdoh.cutils.MStringID;

public interface LOTObjectFactory {

	LOTTask getTask(MStringID taskid);

	LOTPart getPart(MStringID partid);

	LOTPart getPart();

	LOTTool getTool(MStringID mStringID);

	LOTTool getTool();

	LOTTask getTask();

}
