package ee.tufan.log.sign.model;

import org.apache.commons.lang3.StringUtils;

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

	private String rootHash;
	private Map<String, String> signMap = new LinkedHashMap<>();
	private List<String> logList;

	public MerkleTree(List<String> logList) throws MerkleTreeException {
		this.logList = logList;
		List<String> firstLeafList = getFirstLeafList(logList);
		rootHash = construct(firstLeafList).get(0);
	}

	private List<String> construct(List<String> leafList) throws MerkleTreeException {
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

	private List<String> getFirstLeafList(List<String> logList) throws MerkleTreeException {
		List<String> result = new ArrayList<>();
		for (String log : logList) {
			String sign = sign(log);
			result.add(sign);
			signMap.put(log, sign);
		}
		return result;
	}

	private String sign(String raw) throws MerkleTreeException {
		String hash;
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			byte[] messageDigest = md.digest(raw.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			hash = no.toString(16);
			hash = StringUtils.leftPad(hash, HASH_LEN);
		} catch (NoSuchAlgorithmException ex) {
			throw new MerkleTreeException(ex.getMessage(), ex);
		}
		return hash;
	}

	public boolean isLogExist(String paramLog) {
		for (String log : logList) {
			if (log.equals(paramLog)) {
				return true;
			}
		}
		return false;
	}

	public String getRootHash() {
		return rootHash;
	}

	public Map<String, String> getSignMap() {
		return signMap;
	}

	@Override
	public String toString() {
		return getRootHash();
	}

}
