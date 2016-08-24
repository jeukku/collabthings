package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.collabthings.model.CTSubPart;

import waazdoh.common.WObject;

public class CTResourceUsage {

	private final Map<String, Double> map = new HashMap<>();
	private final Map<String, Double> total = new HashMap<>();

	public void getObject(WObject add) {
		WObject v = add.add("values");
		map.keySet().stream().forEach(k -> {
			v.addValue(k, map.get(k));
		});

		WObject t = add.add("total");
		total.keySet().stream().forEach(k -> {
			t.addValue(k, total.get(k));
		});
	}

	public void parse(WObject bresourceusage) {
		WObject v = bresourceusage.get("values");
		if (v != null) {
			v.getChildren().stream().forEach(k -> {
				map.put(k, v.getDoubleValue(k));
			});
		}

		WObject t = bresourceusage.get("total");
		if (t != null) {
			t.getChildren().stream().forEach(k -> {
				total.put(k, t.getDoubleValue(k));
			});
		}
	}

	private void resetTotal() {
		total.clear();
	}

	private void addTotal(CTResourceUsage resourceUsage) {
		new HashSet<>(resourceUsage.total.keySet()).stream().forEach(okey -> {
			Double ovalue = resourceUsage.total.get(okey);
			addToTotal(okey, ovalue);
		});
	}

	private void addToTotal(String okey, Double ovalue) {
		if (ovalue != null) {
			Double current = total.get(okey);
			if (current == null) {
				current = new Double(0);
			}

			current += ovalue;
			total.put(okey, current);
		}
	}

	public void set(String string, Double value) {
		if (value == null) {
			value = 0.0;
		}

		map.put(string, value);
	}

	public Double get(String string) {
		Double d = map.get(string);
		if (d != null) {
			return d;
		} else {
			return 0.0;
		}
	}

	public void updateTotal(List<CTSubPart> subparts) {
		resetTotal();

		map.forEach((k, v) -> addToTotal(k, v));

		subparts.stream().forEach(sp -> {
			addTotal(sp.getPart().getResourceUsage());
		});
	}

	public double getTotal(String string) {
		Double d = total.get(string);
		if (d != null) {
			return d;
		} else {
			return 0.0;
		}
	}
}
