package org.collabthings.sbmq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.collabthings.sbmq.message.SBMessage;
import org.collabthings.sbmq.message.SBMessageContainer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import collabthings.core.utils.WLogger;

public class SBFuture {

	private Thread t;
	private boolean closed;
	private InputStream is;
	private Set<SBFConsumer> consumers = new HashSet<>();
	private WLogger logger = WLogger.getLogger(this);

	private long messagecounter = 0;

	public SBFuture(Process p) {
		t = new Thread(() -> {
			synchronized (this) {
				try {
					wait(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			is = p.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

			StringBuffer sb = new StringBuffer();
			try {
				while (!closed) {
					String json = readMessage(br, sb);
					if (messagecounter % 10000 == 0) {
						logger.info("consumed " + messagecounter + " messages");

					}
					if (filterJSON(json)) {
						SBMessage message = mapMessage(json);
						logger.info("message " + message);

						if (message != null) {
							for (SBFConsumer c : consumers) {
								c.run(this, message);
							}
						}
					}
				}
			} catch (IOException e) {
				WLogger.getLogger(this).error(e);
			}
		});
		t.start();
	}

	private boolean filterJSON(String json) {
		// TODO some smart filter
		if (json == null) {
			return false;
		}

		return json.contains("collabthings");
	}

	private SBMessage mapMessage(String json) throws IOException, JsonParseException, JsonMappingException {
		try {
			ObjectMapper mapper = new ObjectMapper();

			SBMessage message = mapper.readValue(json, SBMessageContainer.class).getValue();
			return message;
		} catch (MismatchedInputException e) {
			logger.info("Couldn't map message " + e + " with json:" + json);
			return null;
		}
	}

	private String readMessage(BufferedReader br, StringBuffer sb) throws IOException {
		sb.setLength(0);

		String line = null;
		while (!closed) {
			line = br.readLine();
			if ("{".equals(line)) {
				break;
			}

			if (line == null) {
				synchronized (sb) {
					try {
						sb.wait(100);
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
			}
		}

		while (!line.equals("}") && !closed) {
			sb.append(line);
			line = br.readLine();
		}
		sb.append(line);

		String json = sb.toString();

		messagecounter++;

		return json;
	}

	public void consume(SBFConsumer cons) {
		consumers.add(cons);
	}

	public void close() {
		this.closed = true;
		try {
			is.close();
		} catch (IOException e) {
			WLogger.getLogger(this).error(e);
		}
	}
}
