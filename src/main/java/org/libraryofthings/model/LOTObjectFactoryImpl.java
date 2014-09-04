package org.libraryofthings.model;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;

import waazdoh.util.MStringID;

public final class LOTObjectFactoryImpl implements LOTObjectFactory {

	private LOTClient env;
	private List<LOTPart> parts = new LinkedList<LOTPart>();
	private List<LOTTool> tools = new LinkedList<LOTTool>();
	//
	private LLog log = LLog.getLogger(this);

	public LOTObjectFactoryImpl(final LOTClient nenv) {
		this.env = nenv;
	}

	@Override
	public LOTTool getTool() {
		LOTTool t = new LOTTool(env);
		tools.add(t);
		return t;
	}

	@Override
	public LOTTool getTool(MStringID toolid) {
		for (LOTTool tool : tools) {
			if (tool.getServiceObject().getID().equals(toolid)) {
				return tool;
			}
		}

		LOTTool tool = new LOTTool(env, toolid);
		tools.add(tool);
		return tool;
	}

	@Override
	public LOTPart getPart() {
		LOTPart p = new LOTPart(env);
		log.info("new part " + p.getBean());
		parts.add(p);
		return p;
	}

	@Override
	public LOTPart getPart(final MStringID partid) {
		for (LOTPart part : parts) {
			if (part.getServiceObject().getID().equals(partid)) {
				return part;
			}
		}

		LOTPart part = new LOTPart(env);
		if (part.load(partid)) {
			log.info("Load part " + part);
			parts.add(part);
			return part;
		} else {
			log.info("Failed to load part " + partid);
			log.info("Current parts " + parts);
			for (LOTPart p : parts) {
				log.info("Part " + p.getBean());
			}
			return null;
		}
	}
}
