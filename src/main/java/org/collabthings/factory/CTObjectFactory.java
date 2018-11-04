/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.factory;

import org.collabthings.datamodel.WStringID;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTConnector;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTMapOfPieces;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTTool;
import org.collabthings.model.run.CTRunEnvironmentBuilder;

public interface CTObjectFactory {

	CTPart getPart();

	CTPart getPart(WStringID stringID);

	CTTool getTool(WStringID mStringID);

	CTTool getTool();

	CTFactory getFactory();

	CTFactory getFactory(WStringID stringID);

	CTBinaryModel getModel(WStringID modelid);

	CTBinaryModel getModel();

	CTApplication getApplication(WStringID id);

	CTApplication getApplication();

	void addInfoListener(CTInfo info);

	CTRunEnvironmentBuilder getRuntimeBuilder(WStringID id);

	CTPartBuilder getPartBuilder();

	CTPartBuilder getPartBuilder(WStringID id);

	String getType(WStringID id);

	CTOpenSCAD getOpenScad(WStringID scadid);

	CTHeightmap getHeightmap(WStringID scadid);

	CTMapOfPieces getMapOfPieces();

	CTMapOfPieces getMapOfPieces(WStringID bmapid);

	CTConnector getConnector(WStringID wStringID);

	CTConnector getConnector();

}
