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

import org.collabthings.math.LOrientation;
import org.collabthings.util.PrintOut;

public interface CTRuntimeObject {

	LOrientation getOrientation();

	void step(double dtime);

	void stop();

	String getParameter(String name);

	String getName();

	PrintOut printOut();

}
