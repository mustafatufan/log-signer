package ee.tufan.log.sign.model;

import ee.tufan.log.sign.util.SignUtil;
import ee.tufan.log.sign.util.SignUtilException;

import java.io.Serializable;
import java.util.*;

public class MerkleTree implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, String> signMap = new LinkedHashMap<>();
	private String rootHash;

	public MerkleTree(List<String> logList) throws SignUtilException {
		List<String> firstLeafList = getFirstLeafList(logList);
		rootHash = construct(firstLeafList).get(0);
	}

	public Map<String, String> getSignMap() {
		return signMap;
	}

	public String getRootHash() {
		return rootHash;
	}

	private List<String> construct(List<String> leafList) throws SignUtilException {
		if (leafList.size() == 1) {
			return leafList;
		} else {
			List<String> newLeafList = new ArrayList<>();
			for (int i = 0; i < leafList.size() - 1; i += 2) {
				String merged = leafList.get(i).concat(leafList.get(i + 1));
				String sign = SignUtil.sign(merged);
				newLeafList.add(sign);
				signMap.put(sign, merged);
			}
			if (leafList.size() % 2 == 1) {
				int lastIndex = leafList.size() - 1;
				newLeafList.add(leafList.get(lastIndex));
			}
			return construct(newLeafList);
		}
	}

	private List<String> getFirstLeafList(List<String> logList) throws SignUtilException {
		List<String> firstLeafList = new ArrayList<>();
		for (String log : logList) {
			String sign = SignUtil.sign(log);
			firstLeafList.add(sign);
			signMap.put(log, sign);
		}
		return firstLeafList;
	}

	@Override
	public String toString() {
		return getRootHash();
	}

}
