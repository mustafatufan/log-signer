package ee.tufan.log.storage.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageServiceImplProperties {

	private String uploadLocation = "upload-dir";

	public String getUploadLocation() {
		return uploadLocation;
	}

	public void setUploadLocation(String uploadLocation) {
		this.uploadLocation = uploadLocation;
	}

	private String signLocation = "sign-dir";

	public String getSignLocation() {
		return signLocation;
	}

	public void setSignLocation(String signLocation) {
		this.signLocation = signLocation;
	}

	private String logExtension = ".merkle";

	public String getLogExtension() {
		return logExtension;
	}

	public void setLogExtension(String logExtension) {
		this.logExtension = logExtension;
	}

}
