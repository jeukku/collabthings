package org.collabthings.view;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
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
import org.collabthings.simulation.LOTViewSimulation;
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
	private Group objectgroup;
	private JFXPanel canvas;

	private Map<LOTRuntimeObject, NodeInfo> nodes = new HashMap<LOTRuntimeObject, NodeInfo>();
	private double rotatex = 0;
	private double zoom = 10;
	private double zoomspeed = 2;
	private Timeline timeline;
	private double time;
	private AnimationTimer timer;

	private LLog log = LLog.getLogger(this);
	private Scene scene;
	private double camerarotate;
	private JFrame f;

	public JFXSimulationView(LOTRunEnvironment env) {
		this.env = env;
		env.addListener(this);
		//
		SwingUtilities.invokeLater(() -> createFrame());
	}

	@Override
	public String toString() {
		return getClass().getTypeName();
	}

	private synchronized void createFrame() {
		f = new JFrame();
		f.setSize(1200, 800);
		f.getContentPane().setLayout(new BorderLayout());

		canvas = new JFXPanel();

		f.getContentPane().add(canvas, BorderLayout.CENTER);
		Platform.runLater(() -> {
			scene = createScene();
			canvas.setScene(scene);

			updateRuntimeObjects(env);

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
		});

		f.setVisible(true);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
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

		this.objectgroup = newGroup();

		Box b = new Box(10, 0.1, 10);
		b.setTranslateY(4);
		b.setMaterial(getRandomMaterial());
		b.setDrawMode(DrawMode.LINE);
		objectgroup.getChildren().add(b);

		scenegroup.getChildren().add(objectgroup);

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
		objectgroup.setRotate(rotatex);
		objectgroup.setRotationAxis(new Point3D(1, 1, 0));
	}

	private void updateZoom() {
		this.objectgroup.setScaleX(zoom);
		this.objectgroup.setScaleY(zoom);
		this.objectgroup.setScaleZ(zoom);
	}

	public synchronized void step(double dtime) {
		rotatex += dtime * 10;

		time += dtime;
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

				if (screen.getX() < -w || screen.getX() > w
						|| screen.getY() < -h || screen.getY() > h) {
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

	private String getShortString(double value) {
		String string = "" + value;
		if (string.length() > 4) {
			return string.substring(0, 4);
		} else {
			return string;
		}
	}

	private class NodeInfo {
		Group group;
	}

	public void close() {
		if (f != null) {
			f.dispose();
		}
	}

	@Override
	public void event(LOTRuntimeEvent e) {
	}
}
