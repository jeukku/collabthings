package org.libraryofthings.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.vecmath.Vector3d;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.SimulationView;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.environment.impl.LOTPartState;
import org.libraryofthings.environment.impl.LOTToolUser;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LTransformationStack;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTSubPart;

public class SimpleSimulationView {

	private static final double MAX_TEXTUPDATETIME = 2;
	private LOTRunEnvironment runenv;
	private VCanvas ycanvas;
	private VCanvas xcanvas;
	private VCanvas zcanvas;
	private VCanvas freecanvas;
	private double freeangle;

	private LLog log = LLog.getLogger(this);
	private JFrame f;
	private LTransformation freetransform;

	public SimpleSimulationView(LOTRunEnvironment runenv) {
		this.runenv = runenv;
		SwingUtilities.invokeLater(() -> createFrame());
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createFrame() {
		if (runenv.isRunning()) {
			f = new JFrame();
			f.setSize(800, 500);
			ycanvas = new VCanvas((v) -> {
				v.y = v.z;
				v.z = 0;
			}, "Y");
			xcanvas = new VCanvas((v) -> {
				v.x = v.z;
				v.z = 0;
			}, "X");
			zcanvas = new VCanvas((v) -> {
				v.z = 0;
			}, "Z");
			freecanvas = new VCanvas((v) -> {
				freetransform.transform(v);
			}, "Z");

			f.getContentPane().setLayout(new BorderLayout(0, 0));

			JPanel panel = new JPanel();
			f.getContentPane().add(panel);
			panel.setLayout(new GridLayout(2, 2));

			panel.add(ycanvas);
			panel.add(xcanvas);
			panel.add(zcanvas);
			panel.add(freecanvas);

			f.setAutoRequestFocus(true);
			// f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			f.setVisible(true);
			f.toFront();
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	public void step(double dtime) {
		freeangle += dtime * 0.00002;
		LTransformation nfreetransform = new LTransformation();
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(1, 0, 0),
				0.4));
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(0, 1, 0),
				freeangle));
		freetransform = nfreetransform;
	}

	public class VCanvas extends JPanel implements SimulationView {
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

		private Graphics2D g;

		public VCanvas(TransformV transform, String name) {
			super();
			this.name = name;
			t = transform;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			this.g = (Graphics2D) g;

			g.setColor(Color.black);
			g.drawString("" + name + " frame:" + (framecount++) + " zoom:"
					+ zoom + " zspeed:" + zoomspeed, 20, 20);

			somethingoutofscreen = false;
			int widthmargin = getSize().width / 10;
			int heightmargin = getSize().height / 10;

			screen_left = widthmargin;
			screen_top = heightmargin;
			screen_right = getSize().width - widthmargin;
			screen_bottom = getSize().height - heightmargin;

			LTransformationStack tstack = new LTransformationStack();
			Set<LOTRuntimeObject> os = runenv.getRunObjects();
			drawObjects(tstack, os);

			checkZoom();

			repaint(1000);
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

		private void drawFactoryState(LTransformationStack tstack,
				LOTFactoryState o) {
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
			Graphics2D g2 = (Graphics2D) g;
			Stroke st = new BasicStroke(2);
			g2.setStroke(st);

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

			Graphics2D g2 = (Graphics2D) g;
			Stroke st = new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, boundingboxdash, 0.0f);
			g2.setStroke(st);

			g.setColor(Color.gray);

			drawLine(tstack, new LVector(a.x, a.y, a.z), new LVector(a.x, b.y,
					a.z));
			drawLine(tstack, new LVector(a.x, b.y, a.z), new LVector(a.x, b.y,
					b.z));
			drawLine(tstack, new LVector(a.x, b.y, b.z), new LVector(a.x, a.y,
					b.z));
			drawLine(tstack, new LVector(a.x, a.y, b.z), new LVector(a.x, a.y,
					a.z));

			drawLine(tstack, new LVector(b.x, a.y, a.z), new LVector(b.x, b.y,
					a.z));
			drawLine(tstack, new LVector(b.x, b.y, a.z), new LVector(b.x, b.y,
					b.z));
			drawLine(tstack, new LVector(b.x, b.y, b.z), new LVector(b.x, a.y,
					b.z));
			drawLine(tstack, new LVector(b.x, a.y, b.z), new LVector(b.x, a.y,
					a.z));

			drawLine(tstack, new LVector(a.x, a.y, a.z), new LVector(b.x, a.y,
					a.z));
			drawLine(tstack, new LVector(a.x, b.y, a.z), new LVector(b.x, b.y,
					a.z));
			drawLine(tstack, new LVector(a.x, b.y, b.z), new LVector(b.x, b.y,
					b.z));
			drawLine(tstack, new LVector(a.x, a.y, b.z), new LVector(b.x, a.y,
					b.z));
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

		private void drawToolUser(LTransformationStack tstack,
				LOTToolUser tooluser) {
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
			return (int) ((-zoom * l.y * getHeight() / 1000.0) + getHeight() / 2);
		}

		private int getSX(LVector l) {
			return (int) ((zoom * l.x * getHeight() / 1000.0) + getWidth() / 2);
		}
	}

	private interface TransformV {
		void transform(LVector v);
	}

	public void close() {
		if (f != null) {
			f.dispose();
		}
	}
}
