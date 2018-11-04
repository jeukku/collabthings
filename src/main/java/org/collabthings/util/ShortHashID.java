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

package org.collabthings.util;

import org.collabthings.datamodel.WStringID;

public class ShortHashID {

	private final String id;

	public ShortHashID(WStringID nid) {
		String sid = nid.toString();
		int hash = sid.hashCode();
		this.id = Integer.toHexString(hash);
	}

	public String toString() {
		return id;
	}

}
