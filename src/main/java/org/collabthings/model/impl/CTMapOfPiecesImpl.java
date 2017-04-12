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

package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.collabthings.CTClient;
import org.collabthings.model.CTMapOfPieces;
import org.collabthings.model.CTMapPiece;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.WObject;
import waazdoh.common.WObjectID;
import waazdoh.common.WStringID;

public class CTMapOfPiecesImpl implements ServiceObjectData, CTMapOfPieces {

	private static final String VALUE_NAME = "name";

	private static int pieceidcounter = 0;

	final private CTClient client;
	final private ServiceObject o;

	private String name = "mapofpieces";
	private Map<String, CTMapPieceImpl> pieces = new HashMap<>();
	private Map<String, CTMapPieceType> ptypes = new HashMap<>();

	public CTMapOfPiecesImpl(CTClient client) {
		this.client = client;
		o = new ServiceObject(CTMapOfPieces.MAPOFPIECES, client.getClient(), this, client.getVersion(),
				client.getPrefix());
	}

	@Override
	public WObject getObject() {
		WObject d = o.getBean();
		d.addValue(VALUE_NAME, name);

		WObject otypes = d.add("types");
		for (CTMapPieceType t : ptypes.values()) {
			t.addTo(otypes);
		}

		WObject opieces = d.add("pieces");
		for (CTMapPieceImpl p : pieces.values()) {
			p.addTo(opieces);
		}
		return d;
	}

	@Override
	public boolean parse(WObject o) {
		name = o.getValue(VALUE_NAME);

		WObject otypes = o.get("types");
		for (String stype : otypes.getChildren()) {
			CTMapPieceType type = getPieceType(stype);
			type.parse(otypes.get(stype));
			ptypes.put(type.getTypeId(), type);
		}

		WObject opieces = o.get("pieces");
		for (String ospiece : opieces.getChildren()) {
			WObject opiece = opieces.get(ospiece);
			CTMapPieceImpl p = new CTMapPieceImpl(opiece);
			pieces.put(p.getId(), p);
		}

		return true;
	}

	@Override
	public CTMapPiece addPiece(CTMapPieceType type) {
		synchronized (pieces) {
			CTMapPieceImpl e = new CTMapPieceImpl(type);
			pieces.put(e.getId(), e);
			return e;
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void publish() {
		o.publish();
	}

	@Override
	public void save() {
		o.save();
	}

	@Override
	public WObjectID getID() {
		return o.getID();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String n) {
		this.name = n;
	}

	@Override
	public boolean load(WStringID id) {
		return o.load(id);
	}

	public CTMapPieceType getPieceType(String type) {
		synchronized (ptypes) {
			CTMapPieceType t = ptypes.get(type);
			if (t == null) {
				t = new CTMapPieceType(type);
				ptypes.put(type, t);
			}
			return t;
		}
	}

	private class CTMapPieceImpl implements CTMapPiece {

		private CTMapPieceType type;
		private List<String> linked = new LinkedList<>();
		private final String id;

		public CTMapPieceImpl(CTMapPieceType type) {
			this.type = type;
			id = "P" + (pieceidcounter++);
		}

		public CTMapPieceImpl(WObject opiece) {
			id = opiece.getValue("id");
			String stype = opiece.getValue("type");
			type = getPieceType(stype);

			List<String> olinks = opiece.getList("links");
			for (String slink : olinks) {
				linked.add(slink);
			}

		}

		public CTMapPieceType getType() {
			return type;
		}

		@Override
		public CTMapPiece newLink(CTMapPiece p) {
			linked.add(p.getId());
			return p;
		}

		public void addTo(WObject opieces) {
			WObject opiece = opieces.add("p");
			opiece.setAttribute("id", getId());
			opiece.setAttribute("type", type.getTypeId());

			for (String lid : linked) {
				opiece.addToList("links", lid);
			}
		}

		public String getId() {
			return id;
		}
	}
}
