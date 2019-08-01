package ee.tufan.log.storage.service;

import ee.tufan.log.sign.model.MerkleTree;
import ee.tufan.log.sign.service.SignServiceException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	void init();

	void store(MultipartFile file);

	void storeSignOfFile(MultipartFile file) throws IOException, SignServiceException;

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	MerkleTree getSignByLogLine(String logLine) throws SignServiceException;

}
