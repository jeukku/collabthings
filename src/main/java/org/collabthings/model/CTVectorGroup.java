package org.collabthings.model;

import java.util.LinkedList;
import java.util.List;

import org.collabthings.math.CTMath;

import com.jme3.math.Vector3f;

import collabthings.datamodel.WObject;

public class CTVectorGroup {
	private List<Vector3f> vs = new LinkedList<>();

	public Vector3f addVector() {
		Vector3f v = new Vector3f();
		vs.add(v);
		return v;
	}

	public int size() {
		return vs.size();
	}

	public void addTo(WObject add) {
		for (Vector3f v : vs) {
			add.addToList("vs", CTMath.getBean(v));
		}
	}

	public void parse(WObject o) {
		List<WObject> list = o.getObjectList("vs");
		for (WObject wo : list) {
			vs.add(CTMath.parseVector(wo));
		}
	}

	public Vector3f get(int i) {
		return vs.get(0);
	}
}
