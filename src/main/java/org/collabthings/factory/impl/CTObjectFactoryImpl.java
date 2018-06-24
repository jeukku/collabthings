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

package org.collabthings.factory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.factory.CTObjectFactory;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTMapOfPieces;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CT3DModelImpl;
import org.collabthings.model.impl.CTApplicationImpl;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.model.impl.CTHeightmapImpl;
import org.collabthings.model.impl.CTMapOfPiecesImpl;
import org.collabthings.model.impl.CTOpenSCADImpl;
import org.collabthings.model.impl.CTPartBuilderImpl;
import org.collabthings.model.impl.CTPartImpl;
import org.collabthings.model.impl.CTToolImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.CTRunEnvironmentBuilderImpl;
import org.collabthings.util.LLog;
import org.eclipse.jetty.util.ArrayUtil;

import difflib.DiffUtils;
import difflib.Patch;
import waazdoh.datamodel.ObjectVO;
import waazdoh.datamodel.WObject;
import waazdoh.datamodel.WStringID;

public final class CTObjectFactoryImpl implements CTObjectFactory {

	private CTClient client;
	private List<CTPartImpl> parts = new ArrayList<>();
	private List<CTToolImpl> tools = new ArrayList<>();
	private List<CTFactoryImpl> factories = new ArrayList<>();
	private List<CT3DModelImpl> models = new ArrayList<>();
	private List<CTApplicationImpl> applications = new ArrayList<>();
	private List<CTRunEnvironmentBuilder> runtimebuilders = new ArrayList<>();
	private List<CTPartBuilder> partbuilders = new ArrayList<>();
	private List<CTOpenSCAD> openscads = new ArrayList<>();
	private List<CTHeightmap> heightmaps = new ArrayList<>();
	private List<CTMapOfPieces> maps = new ArrayList<>();

	private Map<WStringID, CTPartImpl> orgparts = new HashMap<>();

	private LLog log = LLog.getLogger(this);

	private Set<CTInfo> infolisteners = new HashSet<>();
	private Map<WStringID, WStringID> errorids = new HashMap<>();
	private static Map<String, CTOFID> ids = new HashMap<>();

	public CTObjectFactoryImpl(final CTClient nenv) {
		this.client = nenv;
	}

	@Override
	public String getType(WStringID id) {
		ObjectVO o = client.getService().getObjects().read(id.toString());
		return o.toObject().getType();
	}

	@Override
	public void addInfoListener(CTInfo info) {
		infolisteners.add(info);
	}

	@Override
	public CTApplication getApplication() {
		return new CTApplicationImpl(client);
	}

	@Override
	public CTApplication getApplication(WStringID sid) {
		synchronized (applications) {
			WStringID searchid = sid;

			WStringID errorid = errorids.get(sid);
			if (errorid != null) {
				searchid = errorid;
			}

			for (CTApplicationImpl s : applications) {
				if (s.getID().getStringID().equals(searchid)) {
					return s;
				}
			}

			CTApplicationImpl s = new CTApplicationImpl(client);
			s.load(searchid);
			applications.add(s);

			if (!s.getID().getStringID().equals(searchid)) {
				StringBuilder sb = new StringBuilder();
				sb.append(
						"Loaded script doesn't have the id requested. Requested:" + searchid + " result:" + s.getID());
				WObject oservice = client.getService().getObjects().read(searchid.toString()).toObject();
				WObject loadedo = s.getObject();
				diff(sb, oservice, loadedo);

				errorids.put(sid, s.getID().getStringID());

				log.info(sb.toString());
			}

			return s;
		}
	}

	@Override
	public CTMapOfPieces getMapOfPieces() {
		CTMapOfPieces map = new CTMapOfPiecesImpl(client);
		synchronized (maps) {
			maps.add(map);
		}
		return map;
	}

	@Override
	public CTMapOfPieces getMapOfPieces(WStringID bmapid) {
		synchronized (maps) {
			for (CTMapOfPieces map : maps) {
				if (map.getID().getStringID().equals(bmapid)) {
					return map;
				}
			}

			CTMapOfPiecesImpl map = new CTMapOfPiecesImpl(client);
			if (map.load(bmapid)) {
				maps.add(map);
				return map;
			} else {
				log.info("Failed to load mapifpieces " + bmapid);
				return null;
			}
		}
	}

	@Override
	public CTPartBuilder getPartBuilder() {
		CTPartBuilder pb = new CTPartBuilderImpl(client);
		synchronized (partbuilders) {
			partbuilders.add(pb);
		}

		return pb;
	}

	@Override
	public CTPartBuilder getPartBuilder(WStringID builderid) {
		synchronized (partbuilders) {

			for (CTPartBuilder builder : partbuilders) {
				if (builder.getID().getStringID().equals(builderid)) {
					return builder;
				}
			}

			CTPartBuilderImpl builder = new CTPartBuilderImpl(client);
			if (builder.load(builderid)) {
				partbuilders.add(builder);

				if (!builder.getID().getStringID().equals(builderid)) {
					StringBuilder sb = new StringBuilder();
					sb.append("Loaded partbuilder doesn't have the id requested. Requested:" + builder + " result:"
							+ builder.getID());
					WObject oservice = client.getService().getObjects().read(builderid.toString()).toObject();
					WObject loadedo = builder.getObject();
					diff(sb, oservice, loadedo);

					log.info(sb.toString());
				}

				return builder;
			} else {
				log.info("Failed to load builder " + builderid);
				return null;
			}
		}
	}

	@Override
	public CTFactoryImpl getFactory() {
		CTFactoryImpl f = new CTFactoryImpl(client);
		synchronized (factories) {
			factories.add(f);
		}
		return f;
	}

	@Override
	public CTFactoryImpl getFactory(WStringID factoryid) {
		synchronized (factories) {

			for (CTFactoryImpl factory : factories) {
				if (factory.getID().getStringID().equals(factoryid)) {
					return factory;
				}
			}

			CTFactoryImpl factory = new CTFactoryImpl(client);
			if (factory.load(factoryid)) {
				factories.add(factory);
				return factory;
			} else {
				log.info("Failed to load factory " + factoryid);
				return null;
			}
		}
	}

	@Override
	public CTToolImpl getTool() {
		CTToolImpl t = new CTToolImpl(client);
		synchronized (tools) {

			tools.add(t);
			return t;
		}
	}

	@Override
	public CTTool getTool(WStringID toolid) {
		synchronized (tools) {
			for (CTTool tool : tools) {
				if (tool.getID().getStringID().equals(toolid)) {
					return tool;
				}
			}

			CTToolImpl tool = new CTToolImpl(client, toolid);
			tools.add(tool);
			return tool;
		}
	}

	@Override
	public CTPartImpl getPart() {
		CTPartImpl p = new CTPartImpl(client);
		p.setReady();

		log.info("new part " + p.getObject());
		synchronized (parts) {
			parts.add(p);
		}
		return p;
	}

	@Override
	public CTPartImpl getPart(final WStringID orgpartid) {
		CTOFID partid = CTObjectFactoryImpl.getId(orgpartid.toString());

		synchronized (partid) {
			ArrayList<CTPartImpl> ps = new ArrayList<>(parts);

			if (orgparts.get(orgpartid) != null) {
				return orgparts.get(orgpartid);
			}

			CTPartImpl part = searchPart(partid.getId(), ps);
			if (part == null) {
				WStringID errorid = errorids.get(partid.getId());
				part = searchPart(errorid, ps);
				if (part != null) {
					log.info("Found a part with errorid " + errorid + " org:" + partid);
				}
			}

			if (part != null) {
				return part;
			}

			log.info("part not found with id " + partid);
			ps.stream().forEach(p -> log.fine("stored " + p.getID()));

			part = new CTPartImpl(client);
			if (part.load(partid.getId())) {
				log.info("Load part " + part);
				if (!partid.getId().equals(part.getID().getStringID())) {
					StringBuilder sb = new StringBuilder();
					sb.append("Loaded part doesn't have the id requested. Requested:" + partid + " result:"
							+ part.getID());
					WObject oservice = client.getService().getObjects().read(partid.toString()).toObject();
					WObject loadedo = part.getObject();
					diff(sb, oservice, loadedo);

					log.info(sb.toString());

					errorids.put(partid.getId(), part.getID().getStringID());
					log.info("errorids " + errorids);
				}

				synchronized (parts) {
					parts.add(part);
					orgparts.put(orgpartid, part);
					log.info("parts " + parts);
				}

				return part;
			} else {
				log.info("Failed to load part " + partid);
				log.info("Current parts " + ps);
				return null;
			}
		}
	}

	private void diff(StringBuilder sb, WObject oservice, WObject loadedo) {
		String ayaml = loadedo.toYaml();
		String byaml = oservice.toYaml();

		Patch diff = DiffUtils.diff(ArrayUtil.asMutableList(ayaml.split("\n")),
				ArrayUtil.asMutableList(byaml.split("\n")));
		diff.getDeltas().forEach(d -> {
			sb.append("\nDiff " + d.getOriginal() + "\n\t" + d.getRevised());
		});

		// sb.append("\nObject:\n" + ayaml);
		// sb.append("\nService has\n" + byaml);
	}

	private synchronized static CTOFID getId(String sid) {
		CTOFID id = ids.get(sid);
		if (id == null) {
			id = new CTOFID(sid);
			ids.put(sid, id);
		}
		return id;
	}

	private CTPartImpl searchPart(final WStringID partid, ArrayList<CTPartImpl> ps) {
		synchronized (parts) {
			if (partid != null) {
				for (CTPartImpl part : ps) {
					if (part.getID() == null) {
						parts.remove(part);
					} else if (part.getID().getStringID().equals(partid)) {
						return part;
					}
				}
			}

			return null;
		}
	}

	@Override
	public CTBinaryModel getModel() {
		CT3DModelImpl model = new CT3DModelImpl(this.client);
		synchronized (models) {
			models.add(model);
		}
		return model;
	}

	@Override
	public CTBinaryModel getModel(WStringID modelid) {
		synchronized (models) {
			for (CT3DModelImpl model : models) {
				if (model.getID().getStringID().equals(modelid)) {
					return model;
				}
			}
			CT3DModelImpl model = new CT3DModelImpl(client);
			model.load(modelid);

			if (model.getID().getStringID().equals(modelid)) {
				models.add(model);
				return model;
			} else {
				String message = "Loaded model doesn't have the id requested. Requested:" + modelid + " result:"
						+ model.getID();
				message += "\nObject:\n" + model.getObject().toYaml();
				log.info(message);
				throw new RuntimeException(message);
			}
		}
	}

	@Override
	public CTOpenSCAD getOpenScad(WStringID scadid) {
		CTOpenSCAD openscad;
		synchronized (openscads) {
			openscad = searchOpenScad(scadid);

			if (openscad != null) {
				return openscad;
			}

			WStringID errorid = errorids.get(scadid);
			openscad = searchOpenScad(errorid);
			if (openscad != null) {
				log.info("Found a part with errorid " + errorid + " org:" + scadid);
			}
		}

		if (openscad != null) {
			return openscad;
		}

		openscad = new CTOpenSCADImpl(client);
		openscad.load(scadid);
		if (!openscad.getID().getStringID().equals(scadid)) {
			String message = "Loaded model doesn't have the id requested. Requested:" + scadid + " result:"
					+ openscad.getID();
			message += "\nObject:\n" + openscad.getObject().toYaml();
			message += "\nService has\n" + client.getService().getObjects().read(scadid.toString()).toObject().toYaml();
			log.info(message);
			errorids.put(scadid, openscad.getID().getStringID());
			openscads.add(openscad);
			return openscad;
		} else {
			openscads.add(openscad);
			return openscad;
		}
	}

	private CTOpenSCAD searchOpenScad(WStringID scadid) {
		for (CTOpenSCAD os : openscads) {
			if (os.getID().getStringID().equals(scadid)) {
				return os;
			}
		}
		return null;
	}

	@Override
	public CTHeightmap getHeightmap(WStringID hmid) {
		synchronized (heightmaps) {
			for (CTHeightmap os : heightmaps) {
				if (os.getID().getStringID().equals(hmid)) {
					return os;
				}
			}
		}

		CTHeightmap hm = new CTHeightmapImpl(client);
		hm.load(hmid);
		heightmaps.add(hm);
		return hm;
	}

	@Override
	public CTRunEnvironmentBuilder getRuntimeBuilder(WStringID id) {
		synchronized (runtimebuilders) {
			for (CTRunEnvironmentBuilder b : runtimebuilders) {
				if (b.getID().getStringID().equals(id)) {
					return b;
				}
			}

			CTRunEnvironmentBuilder b = new CTRunEnvironmentBuilderImpl(client, id);
			runtimebuilders.add(b);

			return b;
		}
	}

	private static class CTOFID {

		private String id;

		public CTOFID(String string) {
			this.id = string;
		}

		public WStringID getId() {
			return new WStringID(this.id);
		}

		@Override
		public String toString() {
			return id;
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else if (obj.getClass().equals(this.getClass())) {
				CTOFID b = (CTOFID) obj;
				return b.id.equals(this.id);
			} else {
				return false;
			}

		}

	}
}
