package ee.tufan.log.sign.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MerkleTree implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String ALGORITHM = "SHA-512";
	private static final int HASH_LEN = 32;

	private List<String> logList;
	private Map<String, String> signMap = new LinkedHashMap<>();
	private String rootHash;

	public MerkleTree(List<String> logList) {
		this.logList = logList;
		List<String> firstLeafList = getFirstLeafList(logList);
		rootHash = construct(firstLeafList).get(0);
	}

	public Map<String, String> getSignMap() {
		return signMap;
	}

	public String getRootHash() {
		return rootHash;
	}

	private List<String> construct(List<String> leafList) {
		if (leafList.size() == 1) {
			return leafList;
		} else {
			List<String> newLeafList = new ArrayList<>();
			for (int i = 0; i < leafList.size() - 1; i += 2) {
				String merged = leafList.get(i).concat(leafList.get(i + 1));
				String sign = sign(merged);
				newLeafList.add(sign);
				signMap.put(merged, sign);
			}
			if (leafList.size() % 2 == 1) {
				int lastIndex = leafList.size() - 1;
				newLeafList.add(leafList.get(lastIndex));
			}
			return construct(newLeafList);
		}
	}

	private List<String> getFirstLeafList(List<String> logList) {
		List<String> firstLeafList = new ArrayList<>();
		for (String log : logList) {
			String sign = sign(log);
			firstLeafList.add(sign);
			signMap.put(log, sign);
		}
		return firstLeafList;
	}

	@Override
	public String toString() {
		return getRootHash();
	}

	public boolean contains(String paramLog) {
		for (String logLine : logList) {
			if (logLine.equals(paramLog)) {
				return true;
			}
		}
		return false;
	}

	private String sign(String raw) {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			byte[] messageDigest = md.digest(raw.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			while (hashtext.length() < HASH_LEN) {
				hashtext = "0".concat(hashtext);
			}
			return hashtext;
		} catch (NoSuchAlgorithmException ex) {
			return "";
		}
	}

	public static String getRightHash(String hash) {
		return hash.substring(0, HASH_LEN / 2);
	}

	public static String getLeftHash(String hash) {
		return hash.substring(HASH_LEN / 2, hash.length());
	}
}
