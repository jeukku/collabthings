package org.libraryofthings.model.impl;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTObjectFactory;
import org.libraryofthings.model.LOTTool;

import waazdoh.util.MStringID;

public final class LOTObjectFactoryImpl implements LOTObjectFactory {

	private LOTClient env;
	private List<LOTPartImpl> parts = new LinkedList<>();
	private List<LOTToolImpl> tools = new LinkedList<>();
	private List<LOTFactoryImpl> factories = new LinkedList<>();
	//
	private LLog log = LLog.getLogger(this);

	public LOTObjectFactoryImpl(final LOTClient nenv) {
		this.env = nenv;
	}

	@Override
	public LOTFactoryImpl getFactory() {
		LOTFactoryImpl f = new LOTFactoryImpl(env);
		factories.add(f);
		return f;
	}

	@Override
	public LOTFactoryImpl getFactory(MStringID factoryid) {
		for (LOTFactoryImpl factory : factories) {
			if (factory.getID().equals(factoryid)) {
				return factory;
			}
		}

		LOTFactoryImpl factory = new LOTFactoryImpl(env, factoryid);
		factories.add(factory);
		return factory;
	}

	@Override
	public LOTToolImpl getTool() {
		LOTToolImpl t = new LOTToolImpl(env);
		tools.add(t);
		return t;
	}

	@Override
	public LOTTool getTool(MStringID toolid) {
		for (LOTTool tool : tools) {
			if (tool.getID().equals(toolid)) {
				return tool;
			}
		}

		LOTToolImpl tool = new LOTToolImpl(env, toolid);
		tools.add(tool);
		return tool;
	}

	@Override
	public LOTPartImpl getPart() {
		LOTPartImpl p = new LOTPartImpl(env);
		log.info("new part " + p.getBean());
		parts.add(p);
		return p;
	}

	@Override
	public LOTPartImpl getPart(final MStringID partid) {
		for (LOTPartImpl part : parts) {
			if (part.getID().equals(partid)) {
				return part;
			}
		}

		LOTPartImpl part = new LOTPartImpl(env);
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
