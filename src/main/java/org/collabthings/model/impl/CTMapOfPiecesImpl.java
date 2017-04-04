package org.collabthings.model.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private CTMapPieceImpl root;
	private List<CTMapPieceType> types = new LinkedList<>();

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
		for (CTMapPieceType t : types) {
			t.addTo(otypes);
		}

		Set<String> addedpieces = new HashSet<>();
		WObject opieces = d.add("pieces");
		if (root != null) {
			d.addValue("root", root.getType().getTypeId());
			root.addTo(opieces, addedpieces);
		}

		return d;
	}

	@Override
	public CTMapPieceType addType(CTMapPieceType newtype) {
		this.types.add(newtype);
		return newtype;
	}

	@Override
	public void setRoot(CTMapPieceType type) {
		root = new CTMapPieceImpl(type);
	}

	@Override
	public CTMapPiece getRoot() {
		return root;
	}

	@Override
	public boolean parse(WObject o) {
		name = o.getValue(VALUE_NAME);
		return true;
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

	private class CTMapPieceImpl implements CTMapPiece {

		private CTMapPieceType type;
		private List<CTMapPieceImpl> linked = new LinkedList<>();
		private final String id;

		public CTMapPieceImpl(CTMapPieceType type) {
			this.type = type;
			id = "P" + (pieceidcounter++);
		}

		public CTMapPieceType getType() {
			return type;
		}

		@Override
		public CTMapPiece newLink() {
			CTMapPiece l = new CTMapPieceImpl(type);
			return l;
		}

		public void addTo(WObject opieces, Set<String> addedpieces) {
			if (!addedpieces.contains(this)) {
				WObject opiece = opieces.add(getId());

				for (CTMapPieceImpl lp : linked) {
					opiece.addToList("links", lp.getId());
					lp.addTo(opieces, addedpieces);
				}
			}
		}

		private String getId() {
			return id;
		}
	}
}
