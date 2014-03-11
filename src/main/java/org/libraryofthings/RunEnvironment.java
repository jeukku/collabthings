package org.libraryofthings;

import java.util.List;

import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.simulation.LOTSimulationTask;

import waazdoh.cutils.MID;

public interface RunEnvironment {

	void setParameter(String string, MID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	void addPart(String string, LOTPart part);

	LOTPart getPart(String s);

	LLog log();

	LOTToolState addTool(String id, LOTTool tool);

	LOTToolState getTool(String id);

	void addScript(String string, LOTScript loadScript);

	List<LOTSimulationTask> getTasks();
}
