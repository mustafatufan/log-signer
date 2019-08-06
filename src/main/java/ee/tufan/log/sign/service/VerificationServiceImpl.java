package ee.tufan.log.sign.service;

import ee.tufan.log.sign.model.MerkleTree;
import ee.tufan.log.storage.service.StorageService;
import ee.tufan.log.storage.service.StorageServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VerificationServiceImpl implements VerificationService {

	private final StorageService storageService;

	@Override
	public List<Map<String, String>> verify(String logLine) throws VerificationServiceException {
		MerkleTree sign;
		try {
			sign = storageService.getSignByLogLine(logLine);
		} catch (StorageServiceException ex) {
			throw new VerificationServiceException(ex.getMessage(), ex);
		}
		return getVerificationList(sign, logLine);
	}

	private List<Map<String, String>> getVerificationList(MerkleTree sign, String logLine) {
		List<Map<String, String>> verificationList = new ArrayList<>();
		String key = logLine;
		while (key != null) {
			Map<String, String> verification = new HashMap<>();
			verification.put("key", sign.getSignMap().get(key));
			verification.put("value", key);
			verificationList.add(verification);
			key = findNextKey(verification.get("key"), sign.getSignMap());
		}
		return verificationList;
	}

	private String findNextKey(String half, Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (isChild(entry.getKey(), half)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private boolean isChild(String key, String half) {
		if (key == null || half == null) {
			return false;
		} else if (half.equals(StringUtils.right(key, half.length())) || half.equals(StringUtils.left(key, half.length()))) {
			return true;
		}
		return false;
	}

	@Autowired
	public VerificationServiceImpl(StorageService storageService) {
		this.storageService = storageService;
	}

}
