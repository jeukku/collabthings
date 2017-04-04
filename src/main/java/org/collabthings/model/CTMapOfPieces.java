package org.collabthings.model;

import java.util.HashMap;
import java.util.Map;

import org.collabthings.model.CTMapOfPieces.CTMapPieceType;

import waazdoh.common.WObject;

public interface CTMapOfPieces extends CTObject {
	String MAPOFPIECES = "mapofobjects";

	public static class CTMapPieceType {
		private static Map<String, CTMapPieceType> ptypes = new HashMap<>();

		private String type;

		private CTMapPieceType() {
			// empty
		}

		private CTMapPieceType(String type2) {
			this.type = type2;
		}

		public void addTo(WObject otypes) {
			WObject otype = otypes.add(type);
		}

		public static CTMapPieceType getType(String type) {
			synchronized (ptypes) {
				CTMapPieceType t = ptypes.get(type);
				if (t == null) {
					t = new CTMapPieceType(type);
					ptypes.put(type, t);
				}
				return t;
			}
		}

		public String getTypeId() {
			return type;
		}
	}

	CTMapPiece getRoot();

	void setRoot(CTMapPieceType typeAA);

	CTMapPieceType addType(CTMapPieceType ctMapPieceType);

}
