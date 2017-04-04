package org.collabthings.model;

import waazdoh.common.WObject;

public interface CTMapOfPieces extends CTObject {
	String MAPOFPIECES = "mapofobjects";

	public static class CTMapPieceType {

		private String type;

		private CTMapPieceType() {
			// empty
		}

		public CTMapPieceType(String type2) {
			this.type = type2;
		}

		public String getTypeId() {
			return type;
		}

		public void parse(WObject otype) {
			// nothing to do
		}

		public void addTo(WObject otypes) {
			WObject t = otypes.add(getTypeId());
		}
	}

	CTMapPiece addPiece(CTMapPieceType type);

	CTMapPieceType getPieceType(String tt);

}
