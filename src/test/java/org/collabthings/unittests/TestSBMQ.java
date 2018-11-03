package org.collabthings.unittests;

import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import org.collabthings.CTTestCase;
import org.collabthings.sbmq.SBMQ;
import org.collabthings.sbmq.SBMQFilter;
import org.collabthings.sbmq.message.SBMessage;

import waazdoh.client.utils.ConditionWaiter;
import waazdoh.client.utils.WLogger;
import waazdoh.client.utils.WTimedFlag;

public class TestSBMQ extends CTTestCase {
	private int counter;

	public void testMessage() {
		SBMQ mq = new SBMQ();

		Set<SBMessage> messages = new HashSet<>();
		
		int timeout = getWaitTime()*2;
		WTimedFlag flag = new WTimedFlag(timeout);

		mq.getStream(new SBMQFilter("test", "test", (f, m) -> {
			WLogger.getLogger(this).info("got message " + m);
			messages.add(m);
			flag.trigger();
		}));

		ConditionWaiter.wait(() -> {
			return flag.isTriggered();
		}, timeout);

		assertTrue(flag.wasTriggerCalled());

		SBMessage m = messages.iterator().next();
		assertNotNull(m);
		assertNotNull(m.getProgress());
		assertNotNull(m.getHash());
		assertNotNull(m.getPrevious());
		assertNotNull(m.getType());
		assertNotEquals(0, m.getSequence());
		
		mq.close();
	}

	public void test1000Messages() {
		counter = 0;

		SBMQ mq = new SBMQ();

		WTimedFlag flag = new WTimedFlag(getWaitTime());

		mq.getStream((f, m) -> {
			WLogger.getLogger(this).info("got message " + m);
			counter++;
			flag.trigger();
		});

		ConditionWaiter.wait(() -> {
			return flag.isTriggered() && counter > 1000;
		}, getWaitTime());

		assertTrue(flag.wasTriggerCalled());

		mq.close();
	}
}
