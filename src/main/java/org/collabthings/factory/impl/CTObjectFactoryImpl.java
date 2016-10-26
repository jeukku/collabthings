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
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CT3DModelImpl;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.model.impl.CTHeightmapImpl;
import org.collabthings.model.impl.CTOpenSCADImpl;
import org.collabthings.model.impl.CTPartBuilderImpl;
import org.collabthings.model.impl.CTPartImpl;
import org.collabthings.model.impl.CTScriptImpl;
import org.collabthings.model.impl.CTToolImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.CTRunEnvironmentBuilderImpl;
import org.collabthings.util.LLog;

import waazdoh.common.MStringID;
import waazdoh.common.vo.ObjectVO;

public final class CTObjectFactoryImpl implements CTObjectFactory {

	private CTClient client;
	private List<CTPartImpl> parts = new ArrayList<>();
	private List<CTToolImpl> tools = new ArrayList<>();
	private List<CTFactoryImpl> factories = new ArrayList<>();
	private List<CT3DModelImpl> models = new ArrayList<>();
	private List<CTScriptImpl> scripts = new ArrayList<>();
	private List<CTRunEnvironmentBuilder> runtimebuilders = new ArrayList<>();
	private List<CTPartBuilder> partbuilders = new ArrayList<>();
	private List<CTOpenSCAD> openscads = new ArrayList<>();
	private List<CTHeightmap> heightmaps = new ArrayList<>();

	private LLog log = LLog.getLogger(this);

	private Set<CTInfo> infolisteners = new HashSet<>();
	private Map<MStringID, MStringID> errorids = new HashMap<>();

	public CTObjectFactoryImpl(final CTClient nenv) {
		this.client = nenv;
	}

	@Override
	public String getType(MStringID id) {
		ObjectVO o = client.getService().getObjects().read(id.toString());
		return o.toObject().getType();
	}

	@Override
	public void addInfoListener(CTInfo info) {
		infolisteners.add(info);
	}

	@Override
	public CTScript getScript() {
		return new CTScriptImpl(client);
	}

	@Override
	public CTScript getScript(MStringID id) {
		synchronized (scripts) {
			for (CTScriptImpl s : scripts) {
				if (s.getID().getStringID().equals(id)) {
					return s;
				}
			}

			CTScriptImpl s = new CTScriptImpl(client);
			s.load(id);
			scripts.add(s);
			return s;
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
	public CTPartBuilder getPartBuilder(MStringID builderid) {
		synchronized (partbuilders) {

			for (CTPartBuilder builder : partbuilders) {
				if (builder.getID().getStringID().equals(builderid)) {
					return builder;
				}
			}

			CTPartBuilderImpl builder = new CTPartBuilderImpl(client);
			if (builder.load(builderid)) {
				partbuilders.add(builder);
				return builder;
			} else {
				log.info("Failed to load factory " + builderid);
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
	public CTFactoryImpl getFactory(MStringID factoryid) {
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
	public CTTool getTool(MStringID toolid) {
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
	public CTPartImpl getPart(final MStringID partid) {
		synchronized (parts) {
			ArrayList<CTPartImpl> ps = new ArrayList<>(parts);

			CTPartImpl part = searchPart(partid, ps);
			if (part == null) {
				MStringID errorid = errorids.get(partid);
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
			if (part.load(partid)) {
				log.info("Load part " + part);
				if (!part.getID().getStringID().equals(partid)) {
					String message = "Loaded part doesn't have the id requested. Requested:" + partid + " result:"
							+ part.getID();
					message += "\nObject:\n" + part.getObject().toYaml();
					log.info(message);

					errorids.put(partid, part.getID().getStringID());
				}

				CTPartImpl storedpart = searchPart(part.getID().getStringID(), ps);
				if (storedpart == null) {
					parts.add(part);
					return part;
				}
				return storedpart;
			} else {
				log.info("Failed to load part " + partid);
				log.info("Current parts " + ps);
				return null;
			}
		}
	}

	private CTPartImpl searchPart(final MStringID partid, ArrayList<CTPartImpl> ps) {
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

	@Override
	public CTBinaryModel getModel() {
		CT3DModelImpl model = new CT3DModelImpl(this.client);
		synchronized (models) {
			models.add(model);
		}
		return model;
	}

	@Override
	public CTBinaryModel getModel(MStringID modelid) {
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
	public CTOpenSCAD getOpenScad(MStringID scadid) {
		synchronized (openscads) {
			for (CTOpenSCAD os : openscads) {
				if (os.getID().getStringID().equals(scadid)) {
					return os;
				}
			}
		}

		CTOpenSCADImpl scad = new CTOpenSCADImpl(client);
		scad.load(scadid);
		if (!scad.getID().getStringID().equals(scadid)) {
			String message = "Loaded model doesn't have the id requested. Requested:" + scadid + " result:"
					+ scad.getID();
			message += "\nObject:\n" + scad.getObject().toYaml();
			message += "\nService has\n" + client.getService().getObjects().read(scadid.toString()).toObject().toYaml();
			log.info(message);
			return scad;
		} else {
			openscads.add(scad);
			return scad;
		}
	}

	@Override
	public CTHeightmap getHeightmap(MStringID hmid) {
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
	public CTRunEnvironmentBuilder getRuntimeBuilder(MStringID id) {
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
}
