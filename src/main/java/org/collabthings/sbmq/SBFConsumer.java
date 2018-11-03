package org.collabthings.sbmq;

import org.collabthings.sbmq.message.SBMessage;

public interface SBFConsumer {
	void run(SBFuture f, SBMessage message);
}