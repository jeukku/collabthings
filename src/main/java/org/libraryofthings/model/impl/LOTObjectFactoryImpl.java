package org.libraryofthings.model.impl;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOT3DModel;
import org.libraryofthings.model.LOTObjectFactory;
import org.libraryofthings.model.LOTTool;

import waazdoh.util.MStringID;

public final class LOTObjectFactoryImpl implements LOTObjectFactory {

	private LOTClient client;
	private List<LOTPartImpl> parts = new LinkedList<>();
	private List<LOTToolImpl> tools = new LinkedList<>();
	private List<LOTFactoryImpl> factories = new LinkedList<>();
	private List<LOT3DModelImpl> models = new LinkedList<>();
	private LLog log = LLog.getLogger(this);

	public LOTObjectFactoryImpl(final LOTClient nenv) {
		this.client = nenv;
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

			LOTFactoryImpl factory = new LOTFactoryImpl(client, factoryid);
			factories.add(factory);
			return factory;
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
}
