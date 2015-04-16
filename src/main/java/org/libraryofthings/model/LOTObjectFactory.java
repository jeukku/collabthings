package org.libraryofthings.model;

import waazdoh.common.MStringID;

public interface LOTObjectFactory {

	LOTPart getPart();

	LOTPart getPart(MStringID stringID);

	LOTTool getTool(MStringID mStringID);

	LOTTool getTool();

	LOTFactory getFactory();

	LOTFactory getFactory(MStringID stringID);

	LOT3DModel getModel(MStringID modelid);

	LOT3DModel getModel();

	LOTScript getScript(String id);

	void addInfoListener(LOTInfo info);

}
