package org.collabthings.factory;

import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.run.CTRunEnvironmentBuilder;

import waazdoh.common.MStringID;

public interface CTObjectFactory {

	CTPart getPart();

	CTPart getPart(MStringID stringID);

	CTTool getTool(MStringID mStringID);

	CTTool getTool();

	CTFactory getFactory();

	CTFactory getFactory(MStringID stringID);

	CTBinaryModel getModel(MStringID modelid);

	CTBinaryModel getModel();

	CTScript getScript(MStringID id);

	CTScript getScript();

	void addInfoListener(CTInfo info);

	CTRunEnvironmentBuilder getRuntimeBuilder(MStringID id);

	CTPartBuilder getPartBuilder();

	CTPartBuilder getPartBuilder(MStringID id);

	String getType(MStringID id);

	CTOpenSCAD getOpenScad(MStringID scadid);

}
