package org.collabthings.sbmq;

public class SBMQ {
	private SBBridge bridge = new SBBridge();

	public void getStream(SBFConsumer c) {
		bridge.runCmd("createFeedStream").consume(c);
	}

	public void close() {
		bridge.close();
	}

}
