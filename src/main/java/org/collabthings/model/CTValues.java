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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final public class CTValues {
	private Map<String, Object> values = new HashMap<>();
	
	public CTValues(Object... nvalues) {
		for (int i = 0; i < nvalues.length; i += 2) {
			put("" + nvalues[i], nvalues[i + 1]);
		}
	}

	public Object get(String name) {
		return values.get(name);
	}

	public void put(String name, Object value) {
		values.put(name, value);
	}

	public Set<String> keys() {
		return values.keySet();
	}

	public CTValues copy() {
		CTValues v = new CTValues();
		v.values.putAll(values);
		return v;
	}

	@Override
	public String toString() {
		String s = "Values[";
		for (String name : values.keySet()) {
			s += name + " -> " + get(name) + ", ";
		}
		s += "]";
		return s;
	}
}
