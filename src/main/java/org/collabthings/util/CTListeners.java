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

import java.util.ArrayList;
import java.util.List;

import org.collabthings.CTEvent;
import org.collabthings.CTListener;

public class CTListeners {
	private List<CTListener> listeners = new ArrayList<>(0);

	public void add(CTListener l) {
		this.listeners.add(l);
	}

	public void fireEvent(CTEvent e) {
		listeners.stream().forEach((l) -> l.event(e));
	}

}
