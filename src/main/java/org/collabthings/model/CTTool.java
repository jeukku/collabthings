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

public interface CTTool extends CTObject {

	String getName();

	void setName(String string);

	CTPart getPart();

	CTPart newPart();

	CTApplication getApplication(String scriptname);

	CTApplication addApplication(String string);

}
