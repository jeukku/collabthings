package org.libraryofthings;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.cutils.MStringID;

public interface LOTObjectFactory {

	LOTPart getPart();

	LOTPart getPart(MStringID stringID);

	LOTTool getTool(MStringID mStringID);

	LOTTool getTool();

}
