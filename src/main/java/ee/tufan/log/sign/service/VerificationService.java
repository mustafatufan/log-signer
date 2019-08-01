package ee.tufan.log.sign.service;

import java.util.List;
import java.util.Map;

public interface VerificationService {

	List<Map<String, String>> verify(String logLine) throws VerificationServiceException;

}
