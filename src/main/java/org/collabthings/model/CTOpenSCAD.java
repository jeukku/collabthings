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

public interface CTOpenSCAD extends CTObject, CTModel {

	String getError();

	void setApplication(String napplication);

	boolean isOK();

	CTModel getModel();

	String getApplication();

}
