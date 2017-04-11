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

import java.util.List;

import waazdoh.common.vo.UserVO;

public interface CTStorage {
	void writeToStorage(String path, String name, String data);

	List<String> listStorage(String string);

	List<String> getUserPublished(String userid, int start, int count);

	String readStorage(UserVO u, String item);

	String readStorage(String path);
}
