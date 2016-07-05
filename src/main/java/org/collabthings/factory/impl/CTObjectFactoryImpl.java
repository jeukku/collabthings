package org.collabthings.factory.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.factory.CTObjectFactory;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CT3DModelImpl;
import org.collabthings.model.impl.CTFactoryImpl;
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
	private List<CTPartImpl> parts = new LinkedList<>();
	private List<CTToolImpl> tools = new LinkedList<>();
	private List<CTFactoryImpl> factories = new LinkedList<>();
	private List<CT3DModelImpl> models = new LinkedList<>();
	private List<CTScriptImpl> scripts = new LinkedList<>();
	private List<CTRunEnvironmentBuilder> runtimebuilders = new LinkedList<>();
	private List<CTPartBuilder> partbuilders = new LinkedList<>();

	private LLog log = LLog.getLogger(this);

	private Set<CTInfo> infolisteners = new HashSet<CTInfo>();

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
				if (builder.getID().equals(builderid)) {
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
				if (factory.getID().equals(factoryid)) {
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
				if (tool.getID().equals(toolid)) {
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
		log.info("new part " + p.getObject());
		synchronized (parts) {
			parts.add(p);
		}
		return p;
	}

	@Override
	public CTPartImpl getPart(final MStringID partid) {
		synchronized (parts) {
			LinkedList<CTPartImpl> ps = new LinkedList<CTPartImpl>(parts);

			for (CTPartImpl part : ps) {
				if (part.getID() == null) {
					parts.remove(part);
				} else if (part.getID().equals(partid)) {
					return part;
				}
			}

			CTPartImpl part = new CTPartImpl(client);
			if (part.load(partid)) {
				log.info("Load part " + part);
				parts.add(part);
				return part;
			} else {
				log.info("Failed to load part " + partid);
				log.info("Current parts " + ps);
				for (CTPartImpl p : ps) {
					log.info("Part " + p.getObject());
				}
				return null;
			}
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
	public CTBinaryModel getModel(MStringID modelid) {
		synchronized (models) {
			for (CT3DModelImpl model : models) {
				if (model.getID().getStringID().equals(modelid)) {
					return model;
				}
			}
			CT3DModelImpl model = new CT3DModelImpl(client);
			model.load(modelid);
			models.add(model);
			return model;
		}
	}

	@Override
	public CTRunEnvironmentBuilder getRuntimeBuilder(MStringID id) {
		synchronized (runtimebuilders) {
			for (CTRunEnvironmentBuilder b : runtimebuilders) {
				if (b.getID().equals(id)) {
					return b;
				}
			}

			CTRunEnvironmentBuilder b = new CTRunEnvironmentBuilderImpl(client, id);
			runtimebuilders.add(b);
			return b;
		}
	}
}
