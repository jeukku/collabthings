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
package org.collabthings.model;

import java.util.Set;

import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

import waazdoh.datamodel.WObjectID;

public interface CTEnvironment extends CTObject {

	Set<String> getApplications();

	void renameApplication(String oldname, String newname);

	void addApplication(String lowerCase, CTApplication ctApplication);

	void deleteApplication(String string);

	CTApplication getApplication(String string);

	void addTool(String string, CTTool partsource);

	CTTool getTool(String string);

	void renameTool(String string, String text);

	Set<String> getTools();

	void deleteTool(String string);

	Set<String> getParameters();

	void setParameter(String string, WObjectID id);

	void setParameter(String string, String value);

	String getParameter(String string);

	void setVectorParameter(String string, Vector3f v);

	Vector3f getVectorParameter(String name);

	boolean isReady();

	WObjectID getID();

	PrintOut printOut();

}
