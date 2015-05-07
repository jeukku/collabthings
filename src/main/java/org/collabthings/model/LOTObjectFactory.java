package org.collabthings.model;

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

	LOTScript getScript();

	void addInfoListener(LOTInfo info);

	LOTRunEnvironmentBuilder getRuntimeBuilder(MStringID id);

}
