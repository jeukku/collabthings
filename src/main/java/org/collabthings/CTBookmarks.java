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

package org.collabthings;

import java.util.Map;

public interface CTBookmarks {

	void addFolder(String string);

	Map<String, String> list();

	void add(String string, String string2);

	String get(String string);

	Map<String, String> list(String string);

}
