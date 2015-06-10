package org.collabthings.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.collabthings.LLog;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.simulation.LOTViewSimulation;

public class SwingSimulationFrame implements LOTViewSimulation {
	LOTRunEnvironment runenv;

	LLog log = LLog.getLogger(this);
	private JFrame f;

	private SwingSimulation4xView view;

	/**
	 * @wbp.parser.entryPoint
	 */
	public SwingSimulationFrame(LOTRunEnvironment runenv) {
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
			f.getContentPane().setLayout(new BorderLayout(0, 0));

			view = createPanel();

			f.getContentPane().add(view, BorderLayout.CENTER);
			f.setAutoRequestFocus(true);

			f.setVisible(true);
			f.toFront();
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private SwingSimulation4xView createPanel() {
		return new SwingSimulation4xView(runenv);
	}

	public void step(double dtime) {
		if (view != null) {
			view.step(dtime);
		}
	}

	public void close() {
		if (f != null) {
			f.dispose();
		}
	}
}
