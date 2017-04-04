package org.collabthings.model;

import waazdoh.common.WObject;

public interface CTMapOfPieces extends CTObject {
	String MAPOFPIECES = "mapofobjects";

	public static class CTMapPieceType {
		private WObject o;

		private CTMapPieceType() {
			// empty
		}

		public CTMapPieceType(String type) {
			o = new WObject(type);
		}

		public String getTypeId() {
			return o.getType();
		}

		public void parse(WObject otype) {
			o = otype;
		}

		public void addTo(WObject otypes) {
			otypes.add(getTypeId(), o);
		}

		public WObject getObject() {
			return o;
		}
	}

	CTMapPiece addPiece(CTMapPieceType type);

	CTMapPieceType getPieceType(String tt);

}
