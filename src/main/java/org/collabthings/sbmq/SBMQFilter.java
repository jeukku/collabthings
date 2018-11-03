package org.collabthings.sbmq;

import org.collabthings.sbmq.message.SBMessage;

public class SBMQFilter implements SBFConsumer {

	private SBFConsumer c;
	private String type;
	private String name;

	public SBMQFilter(String type, String name, SBFConsumer c) {
		this.c = c;
		this.name = name;
		this.type = type;
	}

	@Override
	public void run(SBFuture f, SBMessage message) {
		c.run(f, message);
	}

}
