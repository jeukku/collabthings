package org.collabthings.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import javafx.scene.transform.Translate;

import javax.vecmath.Matrix4d;

import org.collabthings.math.LTransformation;
import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTModel;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTSubPart;
import org.collabthings.util.LLog;
import org.collabthings.util.LOTTask;

public class JFXPartView implements LOTPartView {
	private static final double ZOOMSPEED_MINIMUM = 0.1;
	private static final double ZOOMSPEED_MAXIMUM = 4;
	private static final int ZOOM_MAXIMUM = 1000;
	private static final double ZOOM_MINIMUM = 0.000001;

	private Group scenegroup;
	private PerspectiveCamera camera;
	private Group cameraGroup;

	private Group rx, ry, rz;
	private Group objectgroup;

	private LOTPart part;
	private Map<LOTSubPart, NodeInfo> subpartnodes;
	private List<LOTTask> tasks = new LinkedList<LOTTask>();

	private double scenerotatex = 0;
	private double zoom = 10;

	private Timeline timeline;
	private AnimationTimer timer;

	private LLog log = LLog.getLogger(this);

	private ViewCanvas canvas;
	private boolean stopped;
	private double rotatey = 0;
	private double rotatez = 0;
	private double rotatex = 30;

	private boolean mousedown;
	private int mousex;
	private int mousey;
	private int lastw;
	private int lasth;
	private int framecount;
	private int updates;

	public JFXPartView() {
	}

	public void setPart(LOTPart npart) {
		this.part = npart;
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

		timeline = new Timeline();
		if (!stopped) {
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

	private void setScene(int canvasw, int canvash) {
		Scene scene = createScene(canvasw, canvash);
		canvas.setScene(scene);

		updatePart();
	}

	private void updatePart() {
		this.updates++;

		LTransformationStack stack = new LTransformationStack();
		List<LOTSubPart> foundparts = new LinkedList<LOTSubPart>();
		updateSubparts(stack, foundparts, part);
		//
		List<LOTSubPart> currentnodes = new LinkedList<LOTSubPart>(
				subpartnodes.keySet());
		for (LOTSubPart node : currentnodes) {
			if (!foundparts.contains(node)) {
				NodeInfo n = subpartnodes.get(node);
				subpartnodes.remove(node);
				n.group.setDisable(true);
				Group g = (Group) n.group.getParent();
				g.getChildren().remove(n.group);
			}
		}
	}

	private void updateSubparts(LTransformationStack stack,
			List<LOTSubPart> foundparts, LOTPart part) {
		List<LOTSubPart> subparts = part.getSubParts();
		for (LOTSubPart sp : subparts) {
			updatePartState(stack, foundparts, sp);
		}
	}

	private void updatePartState(LTransformationStack stack,
			List<LOTSubPart> foundparts, LOTSubPart sp) {
		foundparts.add(sp);

		NodeInfo n = addPart(sp);

		Group g = n.group;

		LTransformation transformation = sp.getTransformation();
		stack.push(transformation);

		setTransformation(stack, g);

		// updateSubparts(stack, sp.getPart());

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

	private NodeInfo addNewGroup(LOTSubPart o, Group g) {
		NodeInfo n;
		objectgroup.getChildren().add(g);

		n = new NodeInfo();
		n.group = g;

		subpartnodes.put(o, n);
		return n;
	}

	private NodeInfo addPart(final LOTSubPart part) {
		NodeInfo n = subpartnodes.get(part);
		if (n == null) {
			Group g = newGroup("part " + part);
			n = addNewGroup(part, g);
		}

		addPart(n, part.getPart());

		return n;
	}

	private void addPart(NodeInfo n, LOTPart part) {
		LOTModel model = part.getModel();
		if (model != null) {
			if (n.group.getChildren().size() == 0) {
				model.addTo(n.group);
			}
		} else {
			List<LOTSubPart> subparts = part.getSubParts();
			if (subparts.isEmpty() && n.group.getChildren().isEmpty()) {
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
					n.group.getChildren().add(b);
				}
			} else {
				for (LOTSubPart sp : subparts) {
					if (subpartnodes.get(sp) == null) {
						Group subpartg = newGroup("" + sp);
						setTransformation(subpartg, sp.getTransformation());
						n.group.getChildren().add(subpartg);

						NodeInfo subpartn = new NodeInfo();

						subpartn.group = subpartg;
						subpartnodes.put(sp, subpartn);
						addPart(subpartn, sp.getPart());
					}
				}
			}
		}
	}

	private PhongMaterial getRandomMaterial() {
		PhongMaterial m = new javafx.scene.paint.PhongMaterial(Color.WHITE);
		m.setDiffuseColor(Color.hsb(Math.random() * 360, 1, 1));
		m.setSpecularColor(Color.WHITE);
		return m;
	}

	private synchronized Scene createScene(double canvasw, double canvash) {
		subpartnodes = new HashMap<LOTSubPart, JFXPartView.NodeInfo>();

		log.info("new scene " + canvasw + "," + canvash);

		this.scenegroup = newGroup("scene");

		Scene scene = new Scene(scenegroup, canvasw, canvash, true);

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
		objectgroup.setRotationAxis(new Point3D(0, 1, 0));

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
	public synchronized void update() {
		framecount++;

		if (zoom > ZOOM_MAXIMUM) {
			zoom = ZOOM_MAXIMUM;
		} else if (zoom < ZOOM_MINIMUM) {
			zoom = ZOOM_MINIMUM;
		}
	}

	private synchronized void updateScene() {
		if (canvas.isVisible()) {
			// waitForFramecount();

			int canvasw = (int) canvas.getWidth();
			int canvash = (int) canvas.getHeight();
			if (lastw != canvasw || lasth != canvash) {
				setScene(canvasw, canvash);
			}

			lastw = canvasw;
			lasth = canvash;

			if (scenegroup != null) {
				updatePart();

				updateRotation();
				updateZoom();

				checkOutOfScreen(canvasw, canvash);
			}
		}
	}

	private void checkOutOfScreen(int canvasw, int canvash) {
		double w = canvasw * 0.9;
		double h = canvash * 0.9;

		boolean somethingoutofscreen = false;

		for (LOTSubPart tu : subpartnodes.keySet()) {
			NodeInfo nodei = subpartnodes.get(tu);
			Group node = nodei.group;

			Point2D screen = node.localToScreen(0, 0, 0);
			if (screen != null) {
				Point2D upperleft = canvas.getUpperLeft();
				screen = screen.subtract(upperleft);

				if (screen != null && pointOutOfScreen(w, h, screen)) {
					somethingoutofscreen = true;
				}
			}
		}

		if (somethingoutofscreen) {

		}
	}

	public int getUpdates() {
		return updates;
	}

	private boolean pointOutOfScreen(double w, double h, Point2D screen) {
		return screen.getX() < 0 || screen.getX() > w || screen.getY() < 0
				|| screen.getY() > h;
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

		boolean isVisible();

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
			rotatey += dx;
			rotatex += dy;

			log.info("mouse moved " + dx + ", " + dy + " rotate " + rotatez
					+ " rotatex " + rotatex);
		}

		mousex = x;
		mousey = y;
	}

	public void mouseScrolled(int count) {
		zoom += count * 0.1;
	}

}
