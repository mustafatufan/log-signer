package ee.tufan.log.sign.service;

import ee.tufan.log.sign.model.MerkleTree;
import ee.tufan.log.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VerificationServiceImpl implements VerificationService {

	private final StorageService storageService;

	@Autowired
	public VerificationServiceImpl(StorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public List<Map<String, String>> verify(String logLine) throws VerificationServiceException {
		MerkleTree sign = null;
		try {
			sign = storageService.getSignByLogLine(logLine);
			if (sign == null) {
				return null;
			}

		} catch (SignServiceException ex) {
			throw new VerificationServiceException(ex.getMessage(), ex);
		}

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

	public boolean isChild(String key, String half) {
		if (key == null || half == null) {
			return false;
		}
		final int mid = key.length() / 2;
		String[] parts = {key.substring(0, mid), key.substring(mid)};
		if (half.equals(parts[0]) || half.equals(parts[1])) {
			return true;
		}
		return false;
	}

}
