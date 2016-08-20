package org.collabthings.util;

import waazdoh.common.MStringID;

public class ShortHashID {

	private final String id;

	public ShortHashID(MStringID nid) {
		String sid = nid.toString();
		int hash = sid.hashCode();
		this.id = Integer.toHexString(hash);
	}

	public String toString() {
		return id;
	}

}
