package org.collabthings.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
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
import org.collabthings.model.LOTRuntimeObject;
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

	private Map<LOTRuntimeObject, NodeInfo> nodes = new HashMap<LOTRuntimeObject, NodeInfo>();
	private double scenerotatex = 0;
	private double zoom = 10;
	private double zoomspeed = 2;
	private Timeline timeline;
	private AnimationTimer timer;

	private LLog log = LLog.getLogger(this);
	private Scene scene;

	private ViewCanvas canvas;
	private boolean stopped;
	private double rotatey = 0;
	private double rotatez = 0;
	private double rotatex = 0;

	private boolean mousedown;
	private int mousex;
	private int mousey;

	public JFXSimulationView(LOTRunEnvironment env, ViewCanvas ncanvas) {
		this.env = env;
		this.canvas = ncanvas;
		env.addListener(this);
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
		Platform.runLater(() -> {
			doCreateCanvas();
		});
	}

	private void doCreateCanvas() {
		scene = createScene();
		canvas.setScene(scene);

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

		stack.push(ps.getTransformation());

		// log.info("current ps " + ps);
		// log.info("current g " + g);
		// log.info("current tr " + stack.current());

		setTransformation(stack, g);
		stack.pull();
	}

	private void setTransformation(LTransformationStack stack, Group g) {
		LTransformation t = stack.current();
		LVector l = new LVector();
		t.transform(l);

		g.setTranslateX(l.x);
		g.setTranslateY(l.y);
		g.setTranslateZ(l.z);

		Matrix4d m = t.getMatrix();

		double d = Math.acos((m.m00 + m.m11 + m.m22 - 1d) / 2d);
		if (d != 0d) {
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

		// log.info("current tr " + fs + " " + stack.current());

		NodeInfo n = this.nodes.get(fs);
		if (n == null) {
			Group g = newGroup();
			LOTBoundingBox bb = fs.getFactory().getBoundingBox();
			LVector bba = bb.getA();
			LVector bbb = bb.getB();
			Box sp = new Box(bbb.x - bba.x, bbb.y - bba.y, bbb.z - bba.z);
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
			Group g = newGroup();
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
			Group g = newGroup();
			Box sp = new Box(1, 1, 1);
			sp.setMaterial(getRandomMaterial());
			g.getChildren().add(sp);

			LOTModel model = partstate.getPart().getModel();
			if (model != null) {
				model.addTo(g);
			} else {
				Box b = new Box(10, 0.1, 10);
				b.setMaterial(getRandomMaterial());
				b.setDrawMode(DrawMode.LINE);
				g.getChildren().add(b);
			}

			objectgroup.getChildren().add(g);

			n = new NodeInfo();
			n.group = g;

			nodes.put(partstate, n);
		}
		return n;
	}

	private void addToolUser(LOTToolUser lotToolUser) {
		if (nodes.get(lotToolUser) == null) {
			Group g = newGroup();
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

	private synchronized Scene createScene() {

		/* Create a JavaFX Group node */
		this.scenegroup = newGroup();

		/* Create the Scene instance and set the group node as root */

		Scene scene = new Scene(scenegroup, 2000, 800, Color.DARKGRAY);

		scenegroup.setAutoSizeChildren(false);
		scenegroup.setDepthTest(DepthTest.ENABLE);

		double cameradistance = 1000;
		// Rotate camerarx = new Rotate(0, Rotate.X_AXIS);
		Translate cameratr = new Translate(0, 0, -cameradistance);

		this.camera = new PerspectiveCamera(true);
		camera.setFarClip(100000);
		camera.setNearClip(0.1);

		camera.getTransforms().addAll(cameratr);

		scene.setCamera(camera);
		//
		this.cameraGroup = newGroup();
		cameraGroup.getChildren().add(camera);
		scenegroup.getChildren().add(cameraGroup);
		//

		this.rx = newGroup();
		this.ry = newGroup();
		rx.getChildren().add(ry);
		this.rz = newGroup();
		ry.getChildren().add(rz);

		this.objectgroup = newGroup();

		Box b = new Box(10, 0.1, 10);
		b.setTranslateY(4);
		b.setMaterial(getRandomMaterial());
		b.setDrawMode(DrawMode.LINE);
		objectgroup.getChildren().add(b);

		rz.getChildren().add(objectgroup);
		scenegroup.getChildren().add(rx);

		return scene;
	}

	private Group newGroup() {
		Group g = new Group();

		g.setTranslateX(0);
		g.setTranslateY(0);
		g.setTranslateZ(0);
		g.setRotate(0);
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

		log.info("rotation " + rotatex + ", " + rotatey + ", " + rotatez);
	}

	private void updateZoom() {
		this.objectgroup.setScaleX(zoom);
		this.objectgroup.setScaleY(zoom);
		this.objectgroup.setScaleZ(zoom);
	}

	public synchronized void step(double dtime) {
		scenerotatex += dtime * 10;

		zoom += (zoomspeed - 1) * dtime;
		if (zoom > ZOOM_MAXIMUM) {
			zoom = ZOOM_MAXIMUM;
		} else if (zoom < ZOOM_MINIMUM) {
			zoom = ZOOM_MINIMUM;
		}
	}

	private void updateScene() {
		if (scenegroup != null) {
			updateRuntimeObjects(env);

			updateRotation();
			updateZoom();

			double w = canvas.getWidth() * 0.9;
			double h = canvas.getHeight() * 0.9;

			boolean somethingoutofscreen = false;

			for (LOTRuntimeObject tu : nodes.keySet()) {
				NodeInfo nodei = nodes.get(tu);
				Group node = nodei.group;

				Point2D screen = node.localToScreen(0, 0, 0);
				if (screen != null
						&& pointOutOfScreen(w, h, screen)) {
					somethingoutofscreen = true;
					log.info("Out of screen " + screen + " w:" + w + " h:" + h
							+ " object:" + tu);
				}
			}

			if (somethingoutofscreen) {
				zoomspeed *= 0.90;
				if (zoomspeed < ZOOMSPEED_MINIMUM) {
					zoomspeed = ZOOMSPEED_MINIMUM;
				}

				log.info("zoomspeed " + zoomspeed);
			} else {
				zoomspeed *= 1.03;
				if (zoomspeed > ZOOMSPEED_MAXIMUM) {
					zoomspeed = ZOOMSPEED_MAXIMUM;
				}
			}
		}

		// log.info("objectgroup tr " + objectgroup.getTransforms());
	}

	private boolean pointOutOfScreen(double w, double h, Point2D screen) {
		return screen.getX() < -w || screen.getX() > w
				|| screen.getY() < -h || screen.getY() > h;
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
			// rotatez += dx + dy;

			log.info("mouse moved " + dx + ", " + dy);

			updateRotation();
		}

		mousex = x;
		mousey = y;

		canvas.refresh();
	}
}
