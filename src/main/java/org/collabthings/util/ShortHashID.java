package org.collabthings.util;

import waazdoh.common.WStringID;

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
