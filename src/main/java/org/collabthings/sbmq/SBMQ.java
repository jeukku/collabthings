package org.collabthings.sbmq;

public class SBMQ {
	private SBBridge bridge;

	public SBMQ(SBMQConfig config) {
		bridge = new SBBridge(config);
	}

	public void getStream(SBFConsumer c) {
		bridge.runCmd("messagesByType collabthings").consume(c);
	}

	public void close() {
		bridge.close();
	}

}
