package org.libraryofthings.view;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.LOTRuntimeEvent;
import org.libraryofthings.environment.SimulationView;
import org.libraryofthings.environment.impl.LOTEvents;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.environment.impl.LOTPartState;
import org.libraryofthings.environment.impl.LOTToolState;
import org.libraryofthings.environment.impl.LOTToolUser;
import org.libraryofthings.math.LTransformationStack;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTSubPart;

public class RunEnviromentDrawer extends LOTEnvironmentDrawer implements
		SimulationView {

	private LOTRunEnvironment runenv;
	private LLog log = LLog.getLogger(this);

	private final LVector l = new LVector();

	public RunEnviromentDrawer(final LOTRunEnvironment nrunenv,
			EnvironmentDrawerTransform transform, String name) {
		super(name, transform);

		runenv = nrunenv;
	}

	public void draw(LOTGraphics g) {
		setGraphics(g);

		init();

		LTransformationStack tstack = new LTransformationStack();
		if (runenv != null) {
			Set<LOTRuntimeObject> os = runenv.getRunObjects();
			drawObjects(tstack, os);

			checkZoom();
		}
	}

	private synchronized void drawObjects(LTransformationStack tstack,
			Set<LOTRuntimeObject> os) {
		for (LOTRuntimeObject o : os) {
			drawObject(tstack, o);
		}
	}

	private void drawObject(LTransformationStack tstack, LOTRuntimeObject o) {
		if (o instanceof LOTFactoryState) {
			drawFactoryState(tstack, (LOTFactoryState) o);
		} else {
			log.info("unknown object " + o);
		}
	}

	private void drawFactoryState(LTransformationStack tstack, LOTFactoryState o) {
		tstack.push(o.getTransformation());

		LOTBoundingBox bbox = o.getFactory().getBoundingBox();
		if (bbox != null) {
			drawBoundingBox(tstack, bbox);
		}
		// child factories
		List<LOTFactoryState> fs = o.getFactories();
		for (LOTFactoryState lotFactoryState : fs) {
			drawFactoryState(tstack, lotFactoryState);
		}
		//
		List<LOTToolUser> tus = o.getToolUsers();
		for (LOTToolUser lotToolUser : tus) {
			drawToolUser(tstack, lotToolUser);
		}

		Set<LOTPartState> parts = o.getParts();
		for (LOTPartState partstate : parts) {
			drawPartState(tstack, partstate);
		}

		LOTEvents es = o.getEvents();
		for (LOTRuntimeEvent event : es.getNewEvents(getNewEventTime())) {
			drawEvent(tstack, event, o.getFactory().getBoundingBox().getA()
					.getAdd(new LVector(0, 0.2, 0)));
		}

		tstack.pull();
	}

	private long getNewEventTime() {
		return System.currentTimeMillis() - 3000;
	}

	private void drawEvent(LTransformationStack tstack, LOTRuntimeEvent e,
			LVector offset) {
		l.set(offset.x, offset.y, offset.z);
		drawCenterSquare(tstack, l);

		l.set(offset.x, offset.y - 0.2, offset.z);
		drawString(tstack, "  " + e.getName().toUpperCase(), l);
	}

	private void drawPartState(LTransformationStack tstack,
			LOTPartState partstate) {

		LOTPart part = partstate.getPart();
		if (part != null) {
			tstack.push(partstate.getTransformation());
			drawPart(tstack, partstate, part);
			tstack.pull();
		}
	}

	public synchronized void drawPart(LTransformationStack tstack,
			LOTRuntimeObject runo, LOTPart part) {
		LOTBoundingBox bbox = part.getBoundingBox();
		if (bbox != null) {
			drawBoundingBox(tstack, bbox);
		}

		l.set(0, 0, 0);

		getGraphics().setColor(Color.lightGray);
		drawString(tstack, "" + runo, l);
		//
		List<LOTSubPart> subparts = part.getSubParts();
		if (!subparts.isEmpty()) {
			for (LOTSubPart lotSubPart : subparts) {
				tstack.push(lotSubPart.getTransformation());

				LOTPart subpartpart = lotSubPart.getPart();
				LOTBoundingBox subpartbbox = subpartpart.getBoundingBox();
				if (subpartbbox != null) {
					drawBoundingBox(tstack, subpartbbox);
				}

				getGraphics().setColor(Color.red);
				l.set(0, 0, 0);
				drawCenterSquare(tstack, l);

				tstack.pull();
			}
		} else {
			getGraphics().setColor(Color.green);
			l.set(0, 0, 0);
			drawCenterSquare(tstack, l);
		}
	}

	private void drawToolUser(LTransformationStack tstack, LOTToolUser tooluser) {
		tstack.push(tooluser.getTransformation());

		l.set(0, 0, 0);
		drawCenterCircle(tstack, l);

		try {
			tooluser.callDraw(this, tstack);
		} catch (LOTToolException e) {
			log.error(this, "drawtooluser", e);
		}

		tstack.pull();
		tstack.push(tooluser.getLocationTransformation());

		LinkedList<LOTRuntimeEvent> eventlist = new LinkedList<LOTRuntimeEvent>();
		eventlist.addAll(tooluser.getEvents().getNewEvents(getNewEventTime()));

		LOTToolState tool = tooluser.getTool();
		if (tool != null) {
			eventlist.addAll(tool.getEvents().getNewEvents(getNewEventTime()));
		}

		drawEvents(tstack, eventlist, new LVector(0, 0.6, 0));

		tstack.pull();
	}

	private void drawEvents(LTransformationStack tstack,
			LinkedList<LOTRuntimeEvent> eventlist, LVector l) {
		double offset = 0;
		for (LOTRuntimeEvent e : eventlist) {
			drawEvent(tstack, e, new LVector(l.x, l.y + offset, l.z));
			offset += 0.5;
		}
	}

	public void setRunEnvironment(LOTRunEnvironment runenv2) {
		this.runenv = runenv2;
	}

}