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

import java.util.List;

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

	List<CTMapPiece> getPieces();

	void clonePiecesTo(CTMapOfPieces map);

}
