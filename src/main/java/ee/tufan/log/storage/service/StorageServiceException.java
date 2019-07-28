package ee.tufan.log.storage.service;

public class StorageServiceException extends RuntimeException {

	public StorageServiceException(String message) {
		super(message);
	}

	public StorageServiceException(String message, Throwable ex) {
		super(message, ex);
	}

}
