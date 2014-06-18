package org.libraryofthings;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.util.MStringID;

public final class LOTObjectFactoryImpl implements LOTObjectFactory {

	private LOTEnvironment env;
	private List<LOTPart> parts = new LinkedList<LOTPart>();
	private List<LOTTool> tools = new LinkedList<LOTTool>();

	public LOTObjectFactoryImpl(final LOTEnvironment nenv) {
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
			parts.add(part);
			return part;
		} else {
			return null;
		}
	}
}
