package org.collabthings.factory;

import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.run.CTRunEnvironmentBuilder;

import waazdoh.common.WStringID;

public interface CTObjectFactory {

	CTPart getPart();

	CTPart getPart(WStringID stringID);

	CTTool getTool(WStringID mStringID);

	CTTool getTool();

	CTFactory getFactory();

	CTFactory getFactory(WStringID stringID);

	CTBinaryModel getModel(WStringID modelid);

	CTBinaryModel getModel();

	CTScript getScript(WStringID id);

	CTScript getScript();

	void addInfoListener(CTInfo info);

	CTRunEnvironmentBuilder getRuntimeBuilder(WStringID id);

	CTPartBuilder getPartBuilder();

	CTPartBuilder getPartBuilder(WStringID id);

	String getType(WStringID id);

	CTOpenSCAD getOpenScad(WStringID scadid);

	CTHeightmap getHeightmap(WStringID scadid);

}
