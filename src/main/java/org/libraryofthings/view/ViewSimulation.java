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
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.LOTPartState;
import org.libraryofthings.environment.LOTRuntimeObject;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.environment.RunEnvironmentListener;
import org.libraryofthings.math.LVector;

public class ViewSimulation implements RunEnvironmentListener {

	private static final int ZOOM_MAXIMUM = 10;
	private static final double ZOOM_MINIMUM = 0.0000000000001;
	private RunEnvironment env;
	private Group scenegroup;
	private PerspectiveCamera camera;
	private Group cameraGroup;
	private Group objectgroup;
	private JFXPanel canvas;

	private Map<LOTRuntimeObject, Node> nodes = new HashMap<LOTRuntimeObject, Node>();
	private double rotatex = 0;
	private double zoom = 1;
	private double zoomspeed = 1;
	private Text text;
	private Timeline timeline;
	private double time;
	private AnimationTimer timer;

	private double cameradistance = 170;
	private Rotate camerarx = new Rotate(0, Rotate.X_AXIS);
	private Translate cameratr = new Translate(0, 0, -cameradistance);
	private LLog log = LLog.getLogger(this);
	private Scene scene;

	public ViewSimulation(RunEnvironment env) {
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
		JFrame f = new JFrame();
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
		;

		f.setVisible(true);
		//
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
	}

	private void addRuntimeObjects(RunEnvironment env) {
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
			nodes.put(partstate, g);
		}
	}

	private void addToolUser(LOTToolUser lotToolUser) {
		if (nodes.get(lotToolUser) == null) {
			Group g = newGroup();
			Sphere sp = new Sphere(1);
			sp.setMaterial(getRandomMaterial());
			g.getChildren().add(sp);
			objectgroup.getChildren().add(g);
			nodes.put(lotToolUser, g);
		}
	}

	private PhongMaterial getRandomMaterial() {
		PhongMaterial m = new javafx.scene.paint.PhongMaterial(Color.WHITE);
		m.setDiffuseColor(Color.hsb(Math.random() * 360, 1, 1));
		m.setSpecularColor(Color.WHITE);
		return m;
	}

	@Override
	public void taskFailed(RunEnvironment runenv, LOTTask task) {

	}

	private synchronized Scene createScene() {

		/* Create a JavaFX Group node */
		this.scenegroup = newGroup();

		/* Create the Scene instance and set the group node as root */

		Scene scene = new Scene(scenegroup, Color.DARKGRAY);
		// Color.rgb(getBackground().getRed(),
		// getBackground().getGreen(),
		// getBackground().getBlue()));

		this.camera = new PerspectiveCamera(true);
		camera.setFarClip(10000);
		camera.setNearClip(0.1);

		camera.getTransforms().addAll(camerarx, cameratr);

		scene.setCamera(camera);
		//
		this.cameraGroup = newGroup();
		cameraGroup.getChildren().add(camera);
		scenegroup.getChildren().add(cameraGroup);
		//

		this.objectgroup = newGroup();

		text = new Text("Hello World");
		text.setScaleX(0.1);
		text.setScaleY(0.1);
		text.setTranslateY(-1);

		scenegroup.getChildren().add(text);

		scenegroup.getChildren().add(objectgroup);
		//
		// createLights();

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

	private void createLights() {
		AmbientLight light = new AmbientLight();
		light.setColor(Color.GRAY);

		Group lightgroup = newGroup();
		lightgroup.getChildren().add(light);

		PointLight plight = new PointLight();
		plight.setTranslateY(0);
		plight.setTranslateZ(-200);
		plight.setColor(Color.WHITE);

		scenegroup.getChildren().add(lightgroup);
	}

	private void updateRotation() {
		objectgroup.setRotate(rotatex);
		objectgroup.setRotationAxis(new Point3D(0, 1, 0));
	}

	private void updateZoom() {
		this.objectgroup.setScaleX(zoom);
		this.objectgroup.setScaleY(zoom);
		this.objectgroup.setScaleZ(zoom);
	}

	public synchronized void step(double dtime) {
		rotatex += dtime * 0.01;
		time += dtime;
		zoom += (zoomspeed - 1) * dtime / 1000;
		if (zoom > ZOOM_MAXIMUM) {
			zoom = 10;
		} else if (zoom < ZOOM_MINIMUM) {
			zoom = ZOOM_MINIMUM;
		}
	}

	private void updateScene() {
		if (scenegroup != null) {
			text.setText("time " + getShortString(time) + " zoom:" + zoom);

			addRuntimeObjects(env);

			updateRotation();
			updateZoom();

			double w = canvas.getWidth() * 0.9;
			double h = canvas.getWidth() * 0.9;

			boolean somethingoutofscreen = false;

			for (LOTRuntimeObject tu : nodes.keySet()) {
				Node node = nodes.get(tu);
				LVector location = tu.getAbsoluteLocation();
				node.setTranslateX(location.getX());
				node.setTranslateY(location.getY());
				node.setTranslateZ(location.getZ());

				Point2D screen = node.localToScreen(0, 0, 0);

				if (screen.getX() < -w || screen.getX() > w
						|| screen.getY() < -h || screen.getY() > h) {
					somethingoutofscreen = true;
					log.info("Out of screen " + screen + " w:" + w + " h:" + h
							+ " object:" + tu);
				}

			}
			if (somethingoutofscreen) {
				zoomspeed *= 0.97;
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
}
