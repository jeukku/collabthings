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

import collabthings.datamodel.WObject;
import collabthings.datamodel.WObjectID;
import collabthings.datamodel.WStringID;

public interface CTObject {

	boolean isReady();

	void publish();

	void save();

	WObjectID getID();

	WObject getObject();

	boolean parse(WObject o);

	String getName();

	void setName(String n);

	boolean load(WStringID id);

}
