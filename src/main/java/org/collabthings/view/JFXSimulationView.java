package org.collabthings.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

import javax.vecmath.Matrix4d;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.LOTTask;
import org.collabthings.environment.RunEnvironmentListener;
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
import org.collabthings.util.LLog;

public class JFXSimulationView implements RunEnvironmentListener,
		LOTViewSimulation {

	private static final double ZOOMSPEED_MINIMUM = 0.1;
	private static final double ZOOMSPEED_MAXIMUM = 4;
	private static final int ZOOM_MAXIMUM = 1000;
	private static final double ZOOM_MINIMUM = 0.000001;
	private LOTRunEnvironment env;
	private Group scenegroup;
	private PerspectiveCamera camera;
	private Group cameraGroup;

	private Group rx, ry, rz;
	private Group objectgroup;

	private Map<LOTRuntimeObject, NodeInfo> nodes;
	private double scenerotatex = 0;
	private double zoom = 10;
	private double zoomspeed = 2;
	private Timeline timeline;
	private AnimationTimer timer;

	private LLog log = LLog.getLogger(this);

	private ViewCanvas canvas;
	private boolean stopped;
	private double rotatey = 0;
	private double rotatez = 0;
	private double rotatex = 0;

	private boolean mousedown;
	private int mousex;
	private int mousey;
	private double lastw;
	private double lasth;
	private int framecount;

	public JFXSimulationView(LOTRunEnvironment e) {
		this.env = e;
		env.addListener(this);
	}

	public void setCanvas(ViewCanvas ncanvas) {
		this.canvas = ncanvas;
		createCanvas();
	}

	@Override
	public String toString() {
		return getClass().getTypeName();
	}

	public void stop() {
		stopped = true;

		if (timer != null) {
			timer.stop();
		}

		if (timeline != null) {
			timeline.stop();
		}
	}

	private void createCanvas() {
		Platform.runLater(() -> doCreateCanvas());
	}

	private void doCreateCanvas() {
		updateScene();

		if (!stopped) {
			timeline = new Timeline();
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.setAutoReverse(true);
			timer = new AnimationTimer() {

				@Override
				public void handle(long arg0) {
					updateScene();
				}
			};

			timeline.play();
			timer.start();
		}
	}

	private void setScene(double canvasw, double canvash) {
		Scene scene = createScene(canvasw, canvash);
		canvas.setScene(scene);
	}

	private void updateRuntimeObjects(LOTRunEnvironment env) {
		LTransformationStack stack = new LTransformationStack();
		Set<LOTRuntimeObject> runos = env.getRunObjects();
		for (LOTRuntimeObject runo : runos) {
			updateRuntimeObject(stack, runo);
		}
	}

	private void updateRuntimeObject(LTransformationStack stack,
			LOTRuntimeObject runo) {
		if (runo instanceof LOTFactoryState) {
			LOTFactoryState fs = (LOTFactoryState) runo;
			updateFactoryState(stack, fs);
		} else if (runo instanceof LOTToolState) {
			LOTToolState ts = (LOTToolState) runo;
			updateToolState(stack, ts);
		} else if (runo instanceof LOTToolUser) {
			updateToolUser(stack, (LOTToolUser) runo);
		} else if (runo instanceof LOTPartState) {
			updatePartState(stack, (LOTPartState) runo);
		}
	}

	private void updatePartState(LTransformationStack stack, LOTPartState ps) {
		NodeInfo n = nodes.get(ps);
		if (n == null) {
			n = addPartState(ps);
		}

		Group g = n.group;

		LTransformation transformation = ps.getTransformation();
		stack.push(transformation);

		setTransformation(stack, g);
		stack.pull();
	}

	private void setTransformation(LTransformationStack stack, Group g) {
		LTransformation t = stack.current();
		setTransformation(g, t);
	}

	private void setTransformation(Group g, LTransformation t) {
		LVector l = new LVector();
		t.transform(l);

		g.setTranslateX(l.x);
		g.setTranslateY(l.y);
		g.setTranslateZ(l.z);

		Matrix4d m = t.getMatrix();

		double d = Math.acos((m.m00 + m.m11 + m.m22 - 1d) / 2d);
		if (Math.abs(d) > 0.00001) {
			double den = 2d * Math.sin(d);
			Point3D p = new Point3D((m.m21 - m.m12) / den, (m.m02 - m.m20)
					/ den, (m.m10 - m.m01) / den);
			g.setRotationAxis(p);
			g.setRotate(Math.toDegrees(d));
		}
	}

	private void updateFactoryState(LTransformationStack stack,
			LOTFactoryState fs) {
		stack.push(fs.getTransformation());

		NodeInfo n = this.nodes.get(fs);
		if (n == null) {
			Group g = newGroup("factory " + fs);
			LOTBoundingBox bb = fs.getFactory().getBoundingBox();
			LVector bba = bb.getA();
			LVector bbb = bb.getB();
			double width = bbb.x - bba.x;
			double height = bbb.y - bba.y;
			double depth = bbb.z - bba.z;

			if (fs.getOrientation().getNormal().dot(new LVector(0, 1, 0)) > 0.99) {
				// height /= 10;
			}

			Box sp = new Box(width, height, depth);
			sp.setMaterial(getRandomMaterial());
			sp.setDrawMode(DrawMode.LINE);

			g.getChildren().add(sp);

			objectgroup.getChildren().add(g);

			n = new NodeInfo();
			n.group = g;

			nodes.put(fs, n);
		}

		if (n != null) {
			setTransformation(stack, n.group);
		}

		List<LOTToolUser> ftus = fs.getToolUsers();
		for (LOTToolUser lotToolUser : ftus) {
			updateToolUser(stack, lotToolUser);
		}

		List<LOTFactoryState> fss = fs.getFactories();
		for (LOTFactoryState lotFactoryState : fss) {
			updateFactoryState(stack, lotFactoryState);
		}

		Set<LOTPartState> ps = fs.getParts();
		for (LOTPartState p : ps) {
			updatePartState(stack, p);
		}

		stack.pull();
	}

	private void updateToolUser(LTransformationStack stack,
			LOTToolUser lotToolUser) {
		if (nodes.get(lotToolUser) == null) {
			addToolUser(lotToolUser);
		}

		stack.push(lotToolUser.getTransformation());

		NodeInfo n = nodes.get(lotToolUser);
		setTransformation(stack, n.group);

		if (lotToolUser.getTool() != null) {
			updateToolState(stack, lotToolUser.getTool());
		}

		stack.pull();
	}

	private void updateToolState(LTransformationStack stack, LOTToolState ts) {
		addToolState(ts);

		stack.push(new LTransformation(ts.getOrientation()));

		NodeInfo n = nodes.get(ts);
		setTransformation(stack, n.group);

		stack.pull();
	}

	private void addToolState(LOTToolState ts) {
		if (nodes.get(ts) == null) {
			Group g = newGroup("tool " + ts);
			Box sp = new Box(1, 1, 1);
			sp.setMaterial(getRandomMaterial());
			g.getChildren().add(sp);

			objectgroup.getChildren().add(g);

			NodeInfo n = new NodeInfo();
			n.group = g;

			nodes.put(ts, n);
		}
	}

	private NodeInfo addPartState(LOTPartState partstate) {

		NodeInfo n = nodes.get(partstate);
		if (n == null) {
			Group g = createPartGroup(partstate);
			objectgroup.getChildren().add(g);

			n = new NodeInfo();
			n.group = g;

			nodes.put(partstate, n);
		}
		return n;
	}

	private Group createPartGroup(LOTPartState partstate) {
		Group g = newGroup("part " + partstate);

		addPartBoxes(g, partstate.getPart());

		return g;
	}

	private void addPartBoxes(Group g, LOTPart part) {
		LOTModel model = part.getModel();
		if (model != null) {
			model.addTo(g);
		} else {
			List<LOTSubPart> sb = part.getSubParts();
			if (sb.isEmpty()) {
				LOTBoundingBox bb = part.getBoundingBox();
				if (bb != null) {
					LVector bba = bb.getA();
					LVector bbb = bb.getB();
					double width = bbb.x - bba.x;
					double height = bbb.y - bba.y;
					double depth = bbb.z - bba.z;
					Box b = new Box(width, height, depth);

					b.setMaterial(getRandomMaterial());
					b.setDrawMode(DrawMode.FILL);
					g.getChildren().add(b);
				} else {
					log.info("Boundingbox null " + part);
				}
			} else {
				for (LOTSubPart lotSubPart : sb) {
					Group cg = newGroup("subpart " + lotSubPart);
					setTransformation(cg, lotSubPart.getTransformation());
					addPartBoxes(cg, lotSubPart.getPart());
				}
			}
		}
	}

	private void addToolUser(LOTToolUser lotToolUser) {
		if (nodes.get(lotToolUser) == null) {
			Group g = newGroup("tool " + lotToolUser);
			Sphere sp = new Sphere(1);
			sp.setMaterial(getRandomMaterial());
			g.getChildren().add(sp);
			objectgroup.getChildren().add(g);

			NodeInfo i = new NodeInfo();

			i.group = g;

			nodes.put(lotToolUser, i);
		}
	}

	private PhongMaterial getRandomMaterial() {
		PhongMaterial m = new javafx.scene.paint.PhongMaterial(Color.WHITE);
		m.setDiffuseColor(Color.hsb(Math.random() * 360, 1, 1));
		m.setSpecularColor(Color.WHITE);
		return m;
	}

	@Override
	public void taskFailed(LOTRunEnvironment runenv, LOTTask task) {

	}

	private synchronized Scene createScene(double canvasw, double canvash) {
		nodes = new HashMap<LOTRuntimeObject, JFXSimulationView.NodeInfo>();

		log.info("new scene " + canvasw + "," + canvash);

		this.scenegroup = newGroup("scene");

		Scene scene = new Scene(scenegroup, canvasw, canvash, Color.DARKGRAY);

		scenegroup.setAutoSizeChildren(false);
		scenegroup.setDepthTest(DepthTest.ENABLE);

		double cameradistance = 1000;

		Translate cameratr = new Translate(0, 0, -cameradistance);

		this.camera = new PerspectiveCamera(true);
		camera.setFarClip(100000);
		camera.setNearClip(0.1);

		camera.getTransforms().addAll(cameratr);

		scene.setCamera(camera);
		//
		this.cameraGroup = newGroup("cameragroup");
		cameraGroup.getChildren().add(camera);
		scenegroup.getChildren().add(cameraGroup);
		//

		this.rx = newGroup("rx");
		this.ry = newGroup("ry");
		rx.getChildren().add(ry);
		this.rz = newGroup("rz");
		ry.getChildren().add(rz);

		this.objectgroup = newGroup("objects");

		rz.getChildren().add(objectgroup);
		scenegroup.getChildren().add(rx);

		Box sp = new Box(2, 2, 2);
		sp.setMaterial(getRandomMaterial());
		sp.setDrawMode(DrawMode.LINE);

		Group boxg = newGroup("box");
		boxg.getChildren().add(sp);
		objectgroup.getChildren().add(boxg);

		sp = new Box(20, 20, 20);
		sp.setMaterial(getRandomMaterial());
		sp.setDrawMode(DrawMode.LINE);

		boxg = newGroup("box");
		boxg.setTranslateX(30);
		boxg.getChildren().add(sp);
		objectgroup.getChildren().add(boxg);

		return scene;
	}

	private Group newGroup(String id) {
		Group g = new Group();

		g.setTranslateX(0);
		g.setTranslateY(0);
		g.setTranslateZ(0);
		g.setRotate(0);

		g.setId(id);

		return g;
	}

	private void updateRotation() {
		objectgroup.setRotate(scenerotatex);
		objectgroup.setRotationAxis(new Point3D(1, 1, 0));

		this.rx.setRotationAxis(new Point3D(1, 0, 0));
		this.rx.setRotate(rotatex);
		this.ry.setRotationAxis(new Point3D(0, 1, 0));
		this.ry.setRotate(rotatey);
		this.rz.setRotationAxis(new Point3D(0, 0, 1));
		this.rz.setRotate(rotatez);
	}

	private void updateZoom() {
		this.objectgroup.setScaleX(zoom);
		this.objectgroup.setScaleY(zoom);
		this.objectgroup.setScaleZ(zoom);
	}

	@Override
	public synchronized void step(double dtime) {
		framecount++;

		scenerotatex += dtime * 10;

		zoom += (zoomspeed - 1) * dtime;
		log.info("zoomspeed " + zoomspeed + " zoom " + zoom + " fs "
				+ (1 / dtime));
		if (zoom > ZOOM_MAXIMUM) {
			zoom = ZOOM_MAXIMUM;
		} else if (zoom < ZOOM_MINIMUM) {
			zoom = ZOOM_MINIMUM;
		}
	}

	private synchronized void updateScene() {
		waitForFramecount();

		double canvasw = canvas.getWidth();
		double canvash = canvas.getHeight();
		if (lastw != canvasw || lasth != canvash) {
			setScene(canvasw, canvash);
		}

		lastw = canvasw;
		lasth = canvash;

		if (scenegroup != null) {
			updateRuntimeObjects(env);

			updateRotation();
			updateZoom();

			double w = canvasw * 0.9;
			double h = canvash * 0.9;

			boolean somethingoutofscreen = false;

			for (LOTRuntimeObject tu : nodes.keySet()) {
				NodeInfo nodei = nodes.get(tu);
				Group node = nodei.group;

				Point2D screen = node.localToScreen(0, 0, 0);
				Point2D upperleft = canvas.getUpperLeft();
				screen = screen.subtract(upperleft);

				Point3D toscene = node.localToScene(0, 0, 0);

				if (screen != null && pointOutOfScreen(w, h, screen)) {
					somethingoutofscreen = true;
					log.info("something out of screen " + screen + " node:"
							+ node.getTransforms() + " ogscale:"
							+ objectgroup.getScaleX());
					log.info("zoom " + zoom + " zoomspeed " + zoomspeed);
					log.info("out of screen " + node);
					log.info("out of screen scene " + toscene);
					log.info("out of screen localtoscene "
							+ node.getLocalToSceneTransform());

					// print(scenegroup, 0);
				}
			}

			if (somethingoutofscreen) {
				zoomspeed *= 0.90;
				if (zoomspeed < ZOOMSPEED_MINIMUM) {
					zoomspeed = ZOOMSPEED_MINIMUM;
				}
			} else {
				zoomspeed *= 1.03;
				if (zoomspeed > ZOOMSPEED_MAXIMUM) {
					zoomspeed = ZOOMSPEED_MAXIMUM;
				}
			}
		}
	}

	private void waitForFramecount() {
		while (framecount < 1) {
			try {
				this.wait(10);
			} catch (InterruptedException e) {
				log.error(this, "waitForFrameCount", e);
			}
		}

		framecount--;
	}

	private void print(Group g, int i) {
		StringBuffer sb = getIndentBuffer(i);
		sb.append(" tr:" + g.getRotationAxis() + "(" + g.getRotate() + ") "
				+ getTranslateString(g) + " id:" + g.getId() + " g:" + g);
		log.info(sb.toString());

		sb = getIndentBuffer(i);
		sb.append(" localtoscene:" + g.localToScene(0, 0, 0)
				+ " localtoscreen:" + g.localToScreen(0, 0, 0));
		log.info(sb.toString());

		ObservableList<Node> cs = g.getChildren();
		for (Node node : cs) {
			if (node instanceof Group) {
				Group cg = (Group) node;
				print(cg, i + 1);
			} else {
				StringBuffer sb2 = getIndentBuffer(i + 1);
				sb2.append(" " + node);
				log.info(sb2.toString());
			}
		}
	}

	private String getTranslateString(Group g) {
		return "{" + g.getTranslateX() + "," + g.getTranslateY() + ","
				+ g.getTranslateZ() + "}";
	}

	private StringBuffer getIndentBuffer(int i) {
		StringBuffer sb = new StringBuffer();
		for (int indent = 0; indent < i; indent++) {
			sb.append("--");
		}
		return sb;
	}

	private boolean pointOutOfScreen(double w, double h, Point2D screen) {
		return screen.getX() < 0 || screen.getX() > w || screen.getY() < 0
				|| screen.getY() > h;
	}

	@Override
	public void event(LOTRuntimeEvent e) {
	}

	@Override
	public void close() {
		timeline.stop();
	}

	private class NodeInfo {
		Group group;
	}

	public interface ViewCanvas {

		double getWidth();

		Point2D getUpperLeft();

		void setScene(Scene scene);

		double getHeight();

		void refresh();

	}

	public void setSceneOrientation(double rx, double ry, double rz) {
		this.rotatex = rx;
		this.rotatey = ry;
		this.rotatez = rz;
	}

	public void mouseUp(int x, int y, int button) {
		this.mousedown = false;
	}

	public void mouseDown(int x, int y, int button) {
		this.mousedown = true;
	}

	public void mouseMove(int x, int y, int button) {
		if (mousedown) {
			int dx = mousex - x;
			int dy = mousey - y;
			rotatez += dx;
			rotatex += dy;

			log.info("mouse moved " + dx + ", " + dy + " rotate " + rotatez
					+ " rotatex " + rotatex);

			updateRotation();
		}

		mousex = x;
		mousey = y;

		canvas.refresh();
	}
}
