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

import java.util.LinkedList;
import java.util.List;

import org.collabthings.model.CTObject;

public class CTEvent {
	private List<Object> os = new LinkedList<>();
	private String info;

	public CTEvent(String string) {
		this.info = string;
	}

	public void addHandled(Object o) {
		os.add(o);
	}

	public boolean isHandled(Object o) {
		return os.contains(o);
	}
}
