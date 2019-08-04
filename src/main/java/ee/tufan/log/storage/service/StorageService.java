package ee.tufan.log.storage.service;

import ee.tufan.log.sign.model.MerkleTree;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	void init();

	void store(MultipartFile file);

	void storeSignOfFile(MultipartFile file) throws StorageServiceException;

	Stream<Path> loadAll() throws StorageServiceException;

	Path load(String filename);

	Resource loadAsResource(String filename);

	MerkleTree getSignByLogLine(String logLine) throws StorageServiceException;

}
