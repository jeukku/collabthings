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

package org.collabthings.model.run;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTObject;
import org.collabthings.util.PrintOut;

public interface CTRunEnvironmentBuilder extends CTObject {

	void save();

	boolean isReady();

	CTEnvironment getEnvironment();

	CTRunEnvironment getRunEnvironment();

	PrintOut printOut();

	String getName();

	void setName(String name);

	String readStorage(String path);

}
