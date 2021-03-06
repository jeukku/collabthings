package org.collabthings;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.collabthings.common.service.ObjectsService;
import org.collabthings.core.BeanStorage;
import org.collabthings.core.WClient.Filter;
import org.collabthings.core.utils.WPreferences;
import org.collabthings.datamodel.ObjectVO;
import org.collabthings.datamodel.ReturnVO;
import org.collabthings.datamodel.WBytesHash;
import org.collabthings.datamodel.WObject;
import org.collabthings.model.impl.CTConstants;
import org.collabthings.util.LLog;

public class StaticObjectsService implements ObjectsService {

	private WPreferences p;
	private String hash;
	private static Map<String, ObjectVO> objectsmap = new HashMap<>();

	private LLog log = LLog.getLogger(this);

	public StaticObjectsService(WPreferences p) {
		this.p = p;
	}

	@Override
	public ObjectVO read(String string) {
		String[] split = string.split("_");

		String hash = split[split.length - 1];

		log.info("reading " + string + " hash:" + hash);

		return objectsmap.get(hash);
	}

	@Override
	public List<ObjectVO> search(String search, int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectVO write(String objectid, String testdata) {
		hash = getHash(testdata);
		ObjectVO ret = new ObjectVO(testdata);
		ret.setSuccess(true);
		ret.setHash(hash.toString());

		objectsmap.put(hash, ret);

		log.info("wrote object with hash " + hash);
		log.info("content : " + testdata);

		return ret;
	}

	private String getHash(String testdata) {
		try {
			WBytesHash h = new WBytesHash(testdata.getBytes(CTConstants.CHARSET));
			return "" + h.toString();
		} catch (UnsupportedEncodingException e) {
			log.error(this, "getHash", e);
			return null;
		}
	}

	@Override
	public ReturnVO publish(String objectid) {
		return ReturnVO.getTrue();
	}

	@Override
	public ReturnVO error(String id, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addBeanStorage(BeanStorage bstorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFilter(Filter f) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean filter(WObject wo) {
		// TODO Auto-generated method stub
		return true;
	}
}
