package org.collabthings.factory;

import org.collabthings.model.LOTBinaryModel;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTInfo;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;
import org.collabthings.model.run.LOTRunEnvironmentBuilder;

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

	LOTScript getScript(MStringID id);

	LOTScript getScript();

	void addInfoListener(LOTInfo info);

	LOTRunEnvironmentBuilder getRuntimeBuilder(MStringID id);

	LOTPartBuilder getPartBuilder();

	LOTPartBuilder getPartBuilder(MStringID id);

}
