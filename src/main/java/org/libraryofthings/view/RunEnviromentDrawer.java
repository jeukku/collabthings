package org.libraryofthings.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
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
import org.libraryofthings.view.SwingSimulationView.TransformV;

public class RunEnviromentDrawer implements SimulationView {
	private TransformV t;
	private double zoom = 100.0;
	private int framecount;
	private String name;
	private boolean somethingoutofscreen;
	private int screen_right;
	private int screen_left;
	private int screen_top;
	private int screen_bottom;
	private double zoomspeed = 1;

	private LVector a = new LVector();
	private LVector b = new LVector();

	private float[] boundingboxdash = { 5.0f };

	private LOTGraphics g;

	private LOTRunEnvironment runenv;
	private LLog log = LLog.getLogger(this);

	public RunEnviromentDrawer(final LOTRunEnvironment nrunenv,
			TransformV transform, String name) {
		super();

		runenv = nrunenv;
		this.name = name;
		t = transform;
	}

	public void draw(LOTGraphics g) {
		this.g = g;

		g.setColor(Color.black);
		g.drawString("" + name + " frame:" + (framecount++) + " zoom:" + zoom
				+ " zspeed:" + zoomspeed, 20, 20);

		somethingoutofscreen = false;
		int widthmargin = g.getWidth() / 10;
		int heightmargin = g.getHeight() / 10;

		screen_left = widthmargin;
		screen_top = heightmargin;
		screen_right = g.getWidth() - widthmargin;
		screen_bottom = g.getHeight() - heightmargin;

		LTransformationStack tstack = new LTransformationStack();
		Set<LOTRuntimeObject> os = runenv.getRunObjects();
		drawObjects(tstack, os);

		checkZoom();
	}

	private void checkZoom() {
		if (somethingoutofscreen) {
			zoomspeed *= 0.999;
		} else {
			zoomspeed *= 1.1;
		}

		if (zoomspeed < 0.9) {
			zoomspeed = 0.9;
		} else if (zoomspeed > 1.0) {
			zoomspeed = 1.0;
		}

		zoom += (zoomspeed - 1);
		if (zoom < 0.01) {
			zoom = 0.01;
		} else if (zoom > 100) {
			zoom = 100;
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
		tstack.current().transform(a);
		t.transform(a);

		g.setColor(Color.lightGray);
		g.drawString("" + part, getSX(a), getSY(a) - 10);
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

				g.setColor(Color.red);
				a.set(0, 0, 0);
				drawCenterSquare(tstack, a);

				tstack.pull();
			}
		} else {
			g.setColor(Color.green);
			a.set(0, 0, 0);
			drawCenterSquare(tstack, a);
		}
	}

	private void drawCenterSquare(LTransformationStack tstack, LVector l) {
		Stroke st = new BasicStroke(2);
		g.setStroke(st);

		a.set(l);
		tstack.current().transform(a);
		t.transform(a);
		int sx = getSX(a);
		int sy = getSY(a);
		checkOutOfScreen(sx, sy);
		g.drawRect(sx - 2, sy - 2, 4, 4);
	}

	private void drawBoundingBox(LTransformationStack tstack,
			LOTBoundingBox bbox) {
		a.set(bbox.getA());
		b.set(bbox.getB());

		Stroke st = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, boundingboxdash, 0.0f);
		g.setStroke(st);

		g.setColor(Color.gray);

		drawLine(tstack, new LVector(a.x, a.y, a.z), new LVector(a.x, b.y, a.z));
		drawLine(tstack, new LVector(a.x, b.y, a.z), new LVector(a.x, b.y, b.z));
		drawLine(tstack, new LVector(a.x, b.y, b.z), new LVector(a.x, a.y, b.z));
		drawLine(tstack, new LVector(a.x, a.y, b.z), new LVector(a.x, a.y, a.z));

		drawLine(tstack, new LVector(b.x, a.y, a.z), new LVector(b.x, b.y, a.z));
		drawLine(tstack, new LVector(b.x, b.y, a.z), new LVector(b.x, b.y, b.z));
		drawLine(tstack, new LVector(b.x, b.y, b.z), new LVector(b.x, a.y, b.z));
		drawLine(tstack, new LVector(b.x, a.y, b.z), new LVector(b.x, a.y, a.z));

		drawLine(tstack, new LVector(a.x, a.y, a.z), new LVector(b.x, a.y, a.z));
		drawLine(tstack, new LVector(a.x, b.y, a.z), new LVector(b.x, b.y, a.z));
		drawLine(tstack, new LVector(a.x, b.y, b.z), new LVector(b.x, b.y, b.z));
		drawLine(tstack, new LVector(a.x, a.y, b.z), new LVector(b.x, a.y, b.z));
	}

	private void drawLine(LTransformationStack tstack, LVector a, LVector b) {
		tstack.current().transform(a);
		tstack.current().transform(b);

		t.transform(a);
		t.transform(b);
		int asx = getSX(a);
		int asy = getSY(a);
		int bsx = getSX(b);
		int bsy = getSY(b);
		int w = bsx - asx;
		int h = bsy - asy;

		g.drawLine(asx, asy, bsx, bsy);
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

	private void drawCenterCircle(LTransformationStack tstack, LVector l) {
		a.set(l);
		tstack.current().transform(a);
		t.transform(a);
		int sx = getSX(l);
		int sy = getSY(l);
		checkOutOfScreen(sx, sy);
		g.setColor(Color.blue);
		g.drawOval(sx - 5, sy - 5, 10, 10);
	}

	private void checkOutOfScreen(int sx, int sy) {
		if (sx > screen_right || sx < screen_left || sy < screen_top
				|| sy > screen_bottom) {
			this.somethingoutofscreen = true;
		}
	}

	private int getSY(LVector l) {
		return (int) ((-zoom * l.y * g.getHeight() / 1000.0) + g.getHeight() / 2);
	}

	private int getSX(LVector l) {
		return (int) ((zoom * l.x * g.getHeight() / 1000.0) + g.getWidth() / 2);
	}
}