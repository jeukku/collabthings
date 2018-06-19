package org.collabthings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.util.LLog;

import waazdoh.client.BinarySource;
import waazdoh.client.ReportingService;
import waazdoh.client.WServiceClient;
import waazdoh.client.model.objects.WBinary;
import waazdoh.datamodel.WStringID;

public class StaticBinarySource implements BinarySource {
	private static Map<String, byte[]> arrays = new HashMap<>();
	private static Map<WStringID, WBinary> binaries = new HashMap<>();

	private LLog log = LLog.getLogger(this);
	private WServiceClient client;

	public StaticBinarySource(WServiceClient client) {
		this.client = client;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WBinary get(WStringID streamid) {
		if (streamid != null) {
			WBinary b = binaries.get(streamid);
			if (b == null) {
				b = newBinary("", "");
				if (b.load(streamid)) {
					binaries.put(streamid, b);
					return b;
				}
			}
			return b;
		} else {
			return null;
		}
	}

	@Override
	public void clearMemory(int suggestedmemorytreshold) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReportingService(ReportingService rservice) {
		// TODO Auto-generated method stub

	}

	@Override
	public WBinary newBinary(String comment, String extension) {
		return new WBinary(client, "./storage", comment, extension);
	}

	@Override
	public void waitUntilReady() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void published(WStringID id) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String writeData(InputStream inputStream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int ihash = 0;

		try {
			while (true) {
				int b = inputStream.read();
				if (b < 0) {
					break;
				}

				ihash += b;
				baos.write(b);
			}

			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String hash = "" + ihash;
		arrays.put(hash, baos.toByteArray());

		log.info("write binary with hash " + hash);

		return hash;
	}

	@Override
	public byte[] readData(String id) {
		return arrays.get(id);
	}

}
