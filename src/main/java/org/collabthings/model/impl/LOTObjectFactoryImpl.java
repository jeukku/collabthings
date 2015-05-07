package org.collabthings.model.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.collabthings.LLog;
import org.collabthings.LOTClient;
import org.collabthings.model.LOT3DModel;
import org.collabthings.model.LOTInfo;
import org.collabthings.model.LOTObjectFactory;
import org.collabthings.model.LOTRunEnvironmentBuilder;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;

import waazdoh.common.MStringID;

public final class LOTObjectFactoryImpl implements LOTObjectFactory {

	private LOTClient client;
	private List<LOTPartImpl> parts = new LinkedList<>();
	private List<LOTToolImpl> tools = new LinkedList<>();
	private List<LOTFactoryImpl> factories = new LinkedList<>();
	private List<LOT3DModelImpl> models = new LinkedList<>();
	private List<LOTScriptImpl> scripts = new LinkedList<>();
	private List<LOTRunEnvironmentBuilder> runtimebuilders = new LinkedList<>();

	private LLog log = LLog.getLogger(this);

	private Set<LOTInfo> infolisteners = new HashSet<LOTInfo>();

	public LOTObjectFactoryImpl(final LOTClient nenv) {
		this.client = nenv;
	}

	@Override
	public void addInfoListener(LOTInfo info) {
		infolisteners.add(info);
	}

	@Override
	public LOTScript getScript() {
		return new LOTScriptImpl(client);
	}

	@Override
	public LOTScript getScript(String sid) {
		synchronized (scripts) {
			MStringID id = new MStringID(sid);
			for (LOTScriptImpl s : scripts) {
				if (s.getID().getStringID().equals(id)) {
					return s;
				}
			}

			LOTScriptImpl s = new LOTScriptImpl(client);
			s.load(id);
			scripts.add(s);
			return s;
		}
	}

	@Override
	public LOTFactoryImpl getFactory() {
		LOTFactoryImpl f = new LOTFactoryImpl(client);
		synchronized (factories) {
			factories.add(f);
		}
		return f;
	}

	@Override
	public LOTFactoryImpl getFactory(MStringID factoryid) {
		synchronized (factories) {

			for (LOTFactoryImpl factory : factories) {
				if (factory.getID().equals(factoryid)) {
					return factory;
				}
			}

			LOTFactoryImpl factory = new LOTFactoryImpl(client);
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
	public LOTToolImpl getTool() {
		LOTToolImpl t = new LOTToolImpl(client);
		synchronized (tools) {

			tools.add(t);
			return t;
		}
	}

	@Override
	public LOTTool getTool(MStringID toolid) {
		synchronized (tools) {
			for (LOTTool tool : tools) {
				if (tool.getID().equals(toolid)) {
					return tool;
				}
			}

			LOTToolImpl tool = new LOTToolImpl(client, toolid);
			tools.add(tool);
			return tool;
		}
	}

	@Override
	public LOTPartImpl getPart() {
		LOTPartImpl p = new LOTPartImpl(client);
		log.info("new part " + p.getBean());
		synchronized (parts) {
			parts.add(p);
		}
		return p;
	}

	@Override
	public LOTPartImpl getPart(final MStringID partid) {
		synchronized (parts) {
			for (LOTPartImpl part : parts) {
				if (part.getID().equals(partid)) {
					return part;
				}
			}

			LOTPartImpl part = new LOTPartImpl(client);
			if (part.load(partid)) {
				log.info("Load part " + part);
				parts.add(part);
				return part;
			} else {
				log.info("Failed to load part " + partid);
				log.info("Current parts " + parts);
				for (LOTPartImpl p : parts) {
					log.info("Part " + p.getBean());
				}
				return null;
			}
		}
	}

	@Override
	public LOT3DModel getModel() {
		LOT3DModelImpl model = new LOT3DModelImpl(this.client);
		synchronized (models) {
			models.add(model);
		}
		return model;
	}

	@Override
	public LOT3DModel getModel(MStringID modelid) {
		synchronized (models) {
			for (LOT3DModelImpl model : models) {
				if (model.getID().getStringID().equals(modelid)) {
					return model;
				}
			}
			LOT3DModelImpl model = new LOT3DModelImpl(client);
			model.load(modelid);
			models.add(model);
			return model;
		}
	}

	@Override
	public LOTRunEnvironmentBuilder getRuntimeBuilder(MStringID id) {
		synchronized (runtimebuilders) {
			for (LOTRunEnvironmentBuilder b : runtimebuilders) {
				if (b.getID().equals(id)) {
					return b;
				}
			}

			LOTRunEnvironmentBuilder b = new LOTRunEnvironmentBuilderImpl(
					client, id);
			runtimebuilders.add(b);
			return b;
		}
	}
}