package org.libraryofthings.view;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.SimulationView;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.environment.impl.LOTPartState;
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

	private final LVector a = new LVector();
	private final LVector b = new LVector();

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

		tstack.pull();
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

		a.set(0, 0, 0);

		getGraphics().setColor(Color.lightGray);
		super.drawString(tstack, "" + part, a);
		//
		List<LOTSubPart> subparts = part.getSubParts();
		if (subparts.size() > 0) {
			for (LOTSubPart lotSubPart : subparts) {
				tstack.push(lotSubPart.getTransformation());

				LOTPart subpartpart = lotSubPart.getPart();
				LOTBoundingBox subpartbbox = subpartpart.getBoundingBox();
				if (subpartbbox != null) {
					drawBoundingBox(tstack, subpartbbox);
				}

				getGraphics().setColor(Color.red);
				a.set(0, 0, 0);
				drawCenterSquare(tstack, a);

				tstack.pull();
			}
		} else {
			getGraphics().setColor(Color.green);
			a.set(0, 0, 0);
			drawCenterSquare(tstack, a);
		}
	}

	private void drawToolUser(LTransformationStack tstack, LOTToolUser tooluser) {
		tstack.push(tooluser.getTransformation());

		a.set(0, 0, 0);
		drawCenterCircle(tstack, a);

		try {
			tooluser.callDraw(this, tstack);
		} catch (LOTToolException e) {
			log.error(this, "drawtooluser", e);
		}

		tstack.pull();
	}

	public void setRunEnvironment(LOTRunEnvironment runenv2) {
		this.runenv = runenv2;
	}

}