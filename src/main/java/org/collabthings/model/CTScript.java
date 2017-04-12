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

import javax.script.Invocable;

public interface CTScript extends CTObject {

	boolean isOK();

	String getScript();

	void setScript(String string);

	void setName(String scriptname);

	Invocable getInvocable();

	String getError();

	String getName();

	String getInfo();

}
