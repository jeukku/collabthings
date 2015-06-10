package org.collabthings.model;

import org.collabthings.environment.LOTRunEnvironmentBuilder;

import waazdoh.common.MStringID;

public interface LOTObjectFactory {

	LOTPart getPart();

	LOTPart getPart(MStringID stringID);

	LOTTool getTool(MStringID mStringID);

	LOTTool getTool();

	LOTFactory getFactory();

	LOTFactory getFactory(MStringID stringID);

	LOTBinaryModel getModel(MStringID modelid);

	LOTBinaryModel getModel();

	LOTScript getScript(String id);

	LOTScript getScript();

	void addInfoListener(LOTInfo info);

	LOTRunEnvironmentBuilder getRuntimeBuilder(MStringID id);

}
