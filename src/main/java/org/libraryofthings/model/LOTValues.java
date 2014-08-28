package org.libraryofthings.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LOTValues {
	private Map<String, Object> values = new HashMap<>();

	public LOTValues(Object... nvalues) {
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

	public LOTValues copy() {
		LOTValues v = new LOTValues();
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
