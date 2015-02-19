package org.libraryofthings.view;

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
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.RunEnvironmentListener;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.environment.impl.LOTPartState;
import org.libraryofthings.environment.impl.LOTToolState;
import org.libraryofthings.environment.impl.LOTToolUser;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTRuntimeObject;

public class ViewSimulation implements RunEnvironmentListener {

	private static final double ZOOMSPEED_MINIMUM = 0.8;
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
	private double zoomspeed = 1;
	private Timeline timeline;
	private double time;
	private AnimationTimer timer;

	private double cameradistance = 170;
	private Rotate camerarx = new Rotate(Math.PI / 4, Rotate.X_AXIS);
	private Translate cameratr = new Translate(0, cameradistance,
			-cameradistance);
	private LLog log = LLog.getLogger(this);
	private Scene scene;
	private double camerarotate;
	private JFrame f;

	public ViewSimulation(LOTRunEnvironment env) {
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
		f.setSize(800, 800);
		f.getContentPane().setLayout(new BorderLayout());

		canvas = new JFXPanel();

		f.getContentPane().add(canvas, BorderLayout.CENTER);
		Platform.runLater(() -> {
			scene = createScene();
			canvas.setScene(scene);
			addRuntimeObjects(env);

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

	private void addRuntimeObjects(LOTRunEnvironment env) {
		Set<LOTRuntimeObject> runos = env.getRunObjects();
		for (LOTRuntimeObject runo : runos) {
			addRuntimeObject(runo);
		}

	}

	private void addRuntimeObject(LOTRuntimeObject runo) {
		if (runo instanceof LOTFactoryState) {
			LOTFactoryState fs = (LOTFactoryState) runo;
			addFactoryState(fs);
		} else if (runo instanceof LOTToolState) {
			LOTToolState ts = (LOTToolState) runo;
			addToolState(ts);
		} else if (runo instanceof LOTToolUser) {
			addToolUser((LOTToolUser) runo);
		} else if (runo instanceof LOTPartState) {
			addPartState((LOTPartState) runo);
		}
	}

	private void addFactoryState(LOTFactoryState fs) {
		List<LOTToolUser> ftus = fs.getToolUsers();
		for (LOTToolUser lotToolUser : ftus) {
			addToolUser(lotToolUser);
		}

		List<LOTFactoryState> fss = fs.getFactories();
		for (LOTFactoryState lotFactoryState : fss) {
			addFactoryState(lotFactoryState);
		}
	}

	private void addToolState(LOTToolState ts) {
		// TODO Auto-generated method stub
	}

	private void addPartState(LOTPartState partstate) {
		if (nodes.get(partstate) == null) {
			Group g = newGroup();
			Box sp = new Box(1, 1, 1);
			sp.setMaterial(getRandomMaterial());
			g.getChildren().add(sp);

			objectgroup.getChildren().add(g);

			NodeInfo i = new NodeInfo();
			i.group = g;

			nodes.put(partstate, i);
		}
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

		Scene scene = new Scene(scenegroup, Color.DARKGRAY);
		scenegroup.setAutoSizeChildren(false);

		this.camera = new PerspectiveCamera(true);
		camera.setFarClip(1000000);
		camera.setNearClip(0.1);

		camera.getTransforms().addAll(camerarx, cameratr);

		scene.setCamera(camera);
		//
		this.cameraGroup = newGroup();
		cameraGroup.getChildren().add(camera);
		scenegroup.getChildren().add(cameraGroup);
		//

		this.objectgroup = newGroup();

		Box b = new Box(100, 1, 100);
		b.setTranslateY(-2);
		b.setMaterial(getRandomMaterial());
		objectgroup.getChildren().add(b);
		//
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
		camerarx.setAngle(camerarotate);

		objectgroup.setRotate(rotatex);
		objectgroup.setRotationAxis(new Point3D(0, 1, 0));
	}

	private void updateZoom() {
		this.objectgroup.setScaleX(zoom);
		this.objectgroup.setScaleY(zoom);
		this.objectgroup.setScaleZ(zoom);
	}

	public synchronized void step(double dtime) {
		camerarotate += dtime * 0.1;

		time += dtime;
		zoom += (zoomspeed - 1) * dtime / 1000;
		if (zoom > ZOOM_MAXIMUM) {
			zoom = ZOOM_MAXIMUM;
		} else if (zoom < ZOOM_MINIMUM) {
			zoom = ZOOM_MINIMUM;
		}
	}

	private void updateScene() {
		if (scenegroup != null) {
			addRuntimeObjects(env);

			updateRotation();
			updateZoom();

			double w = canvas.getWidth() * 0.9;
			double h = canvas.getWidth() * 0.9;

			boolean somethingoutofscreen = false;

			for (LOTRuntimeObject tu : nodes.keySet()) {
				NodeInfo nodei = nodes.get(tu);
				Group node = nodei.group;
				LTransformation t = tu.getTransformation();
				LVector l = new LVector();
				t.transform(l);
				LVector location = l;
				node.setTranslateX(location.x);
				node.setTranslateY(location.y);
				node.setTranslateZ(location.z);

				Point2D screen = node.localToScreen(0, 0, 0);

				if (screen.getX() < -w || screen.getX() > w
						|| screen.getY() < -h || screen.getY() > h) {
					somethingoutofscreen = true;
					log.info("Out of screen " + screen + " w:" + w + " h:" + h
							+ " object:" + tu);
				}

			}
			if (somethingoutofscreen) {
				zoomspeed *= 0.95;
				if (zoomspeed < ZOOMSPEED_MINIMUM) {
					zoomspeed = ZOOMSPEED_MINIMUM;
				}
			} else {
				zoomspeed *= 1.01;
			}
		}
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
		f.dispose();
	}
}
