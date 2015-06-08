package org.collabthings.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.collabthings.LLog;
import org.collabthings.LOTToolException;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.SimulationView;
import org.collabthings.environment.impl.LOTEvents;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.environment.impl.LOTPartState;
import org.collabthings.environment.impl.LOTToolState;
import org.collabthings.environment.impl.LOTToolUser;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTModel;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTSubPart;
import org.collabthings.model.LOTTriangle;
import org.collabthings.model.LOTTriangleMesh;

public class RunEnviromentDrawer extends LOTEnvironmentDrawer implements
		SimulationView {

	private LOTRunEnvironment runenv;
	private LLog log = LLog.getLogger(this);

	private final LVector l = new LVector();

	private long lasttime;
	private HashMap<LOTModel, Integer> modelcolors;

	public RunEnviromentDrawer(final LOTRunEnvironment nrunenv,
			EnvironmentDrawerTransform transform, String name) {
		super(name, transform);

		runenv = nrunenv;
	}

	public void draw(LOTGraphics g) {
		long currentTimeMillis = System.currentTimeMillis();
		if (lasttime == 0) {
			lasttime = currentTimeMillis;
		}
		double dt = (currentTimeMillis - lasttime) / 1000.0;
		lasttime = currentTimeMillis;

		setGraphics(g);

		init();

		LTransformationStack tstack = new LTransformationStack();

		if (runenv != null) {
			Set<LOTRuntimeObject> os = runenv.getRunObjects();
			drawObjects(tstack, os);

			checkZoom(dt);
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
		tstack.push(new LTransformation(o.getOrientation()));

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
		return System.currentTimeMillis() - 1000;
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

		if (part.getModel() != null) {
			LOTModel m = part.getModel();
			drawModel(tstack, m);
		} else {
			getGraphics().setColor(Color.lightGray);
			// drawString(tstack, "" + runo.getName(), l);
			//
			List<LOTSubPart> subparts = part.getSubParts();
			if (!subparts.isEmpty()) {
				for (LOTSubPart lotSubPart : subparts) {
					tstack.push(lotSubPart.getTransformation());

					LOTPart subpartpart = lotSubPart.getPart();
					drawPart(tstack, null, subpartpart);

					tstack.pull();
				}
			} else {
				getGraphics().setColor(Color.green);
				l.set(0, 0, 0);
				drawCenterSquare(tstack, l);
			}
		}
	}

	private void drawModel(LTransformationStack tstack, LOTModel m) {
		LOTTriangleMesh mesh = m.getTriangleMesh();
		if (mesh != null) {
			final LVector mtrans = m.getTranslation();
			final double scale = m.getScale();
			final int color = getColor(m);

			List<LOTTriangle> ts = mesh.getTriangles();
			List<LVector> orgvs = mesh.getVectors();
			List<LVector> vs = new ArrayList<LVector>();
			orgvs.forEach(v -> {
				// TODO should we reuse objects? In previous Java versions
				// object
				// creation was pretty slow
				vs.add(new LVector(v));
			});

			vs.parallelStream().forEach(v -> {
				if (mtrans != null) {
					v.add(mtrans);
				}
				v.scale(scale);

				tstack.current().transform(v);
				drawert.transform(v, true);

				v.x = getSX(v.x);
				v.y = getSY(v.y);
			});

			ts.sort(new Comparator<LOTTriangle>() {
				@Override
				public int compare(LOTTriangle o1, LOTTriangle o2) {
					LVector ta = vs.get(o1.a);
					LVector tb = vs.get(o2.a);
					if (ta.z >= tb.z) {
						return -1;
					} else {
						return 1;
					}
				}
			});

			ts.forEach(t -> {
				LVector nn = t.n.copy();
				tstack.current().transformw0(nn);
				drawert.transform(nn, false);

				if (nn.z > 0) {
					LVector ta = vs.get(t.a);
					LVector tb = vs.get(t.b);
					LVector tc = vs.get(t.c);

					if (ta.z > 0 && tb.z > 0 && tc.z > 0) {
						if (nn.z > 1) {
							nn.z = 1;
						}

						int r = ((int) (((color & 0xff0000) >> 16) * nn.z) & 0xff) << 16;
						int g = ((int) (((color & 0xff00) >> 8) * nn.z) & 0xff) << 8;
						int b = (int) ((color & 0xff) * nn.z) & 0xff;
						int c = r << 16 | g << 8 | b;

						getGraphics().drawTriangle(ta, tb, tc, c);

						getGraphics().drawLine(ta.x, ta.y, ta.z, tb.x, tb.y,
								tb.z);
					}
				}
			});
		}
	}

	private int getColor(LOTModel m) {
		if (modelcolors == null) {
			modelcolors = new HashMap<LOTModel, Integer>();
		}

		Integer c = modelcolors.get(m);
		if (c == null) {
			c = (int) (Math.random() * 0xffffff);
			modelcolors.put(m, c);
		}
		return c;
	}

	private void drawToolUser(LTransformationStack tstack, LOTToolUser tooluser) {
		tstack.push(new LTransformation(tooluser.getOrientation()));

		l.set(0, 0, 0);
		drawCenterCircle(tstack, l);

		try {
			tooluser.callDraw(this, tstack);
		} catch (LOTToolException e) {
			log.error(this, "drawtooluser", e);
		}

		tstack.pull();
		tstack.push(tooluser.getTransformation());

		LinkedList<LOTRuntimeEvent> eventlist = new LinkedList<LOTRuntimeEvent>();
		eventlist.addAll(tooluser.getEvents().getNewEvents(getNewEventTime()));

		LOTToolState tool = tooluser.getTool();
		if (tool != null) {
			eventlist.addAll(tool.getEvents().getNewEvents(getNewEventTime()));
		}

		// drawEvents(tstack, eventlist, new LVector(0, 0.6, 0));

		tstack.pull();
	}

	private void drawEvents(LTransformationStack tstack,
			LinkedList<LOTRuntimeEvent> eventlist, LVector l) {
		double offset = 0;
		for (LOTRuntimeEvent e : eventlist) {
			drawEvent(tstack, e, new LVector(l.x, l.y + offset, l.z));
			offset += 0.7;
		}
	}

	public void setRunEnvironment(LOTRunEnvironment runenv2) {
		this.runenv = runenv2;
	}

}