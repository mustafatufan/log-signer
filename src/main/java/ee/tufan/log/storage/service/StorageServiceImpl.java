package ee.tufan.log.storage.service;

import ee.tufan.log.sign.model.MerkleTree;
import ee.tufan.log.sign.service.SignService;
import ee.tufan.log.sign.service.SignServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

	private final SignService signService;

	private final Path uploadLocation;
	private final Path signLocation;
	private final String logExtension;

	@Autowired
	public StorageServiceImpl(SignService signService, StorageServiceImplProperties properties) {
		this.signService = signService;
		this.uploadLocation = Paths.get(properties.getUploadLocation());
		this.signLocation = Paths.get(properties.getSignLocation());
		this.logExtension = properties.getLogExtension();
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(uploadLocation);
			Files.createDirectories(signLocation);
		} catch (IOException e) {
			throw new StorageServiceException("Could not initialize storage");
		}
	}

	@Override
	public void store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageServiceException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				throw new StorageServiceException(
						"Cannot store file with relative path outside current directory "
								+ filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, this.uploadLocation.resolve(filename),
						StandardCopyOption.REPLACE_EXISTING);
			}
			storeSignOfFile(file);
		} catch (IOException ex) {
			throw new StorageServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void storeSignOfFile(MultipartFile file) throws StorageServiceException {
		File signFile = this.signLocation.resolve(getSignFileName(file)).toFile();

		try (FileOutputStream fileStream = new FileOutputStream(signFile)) {
			try (ObjectOutputStream outputStream = new ObjectOutputStream(fileStream)) {
				MerkleTree sign = signService.getSignOfFile(file);
				outputStream.writeObject(sign);
			}
		} catch (IOException | SignServiceException ex) {
			throw new StorageServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public Stream<Path> loadAll() throws StorageServiceException {
		try {
			Stream<Path> uploadStream = getPathStream(this.uploadLocation);
			Stream<Path> signStream = getPathStream(this.signLocation);
			return Stream.concat(uploadStream, signStream);
		} catch (IOException e) {
			throw new StorageServiceException("Failed to read stored files");
		}
	}

	@Override
	public Path load(String filename) {
		if (filename.contains(logExtension)) {
			return signLocation.resolve(filename);
		}
		return uploadLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageServiceException(
						"Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new StorageServiceException("Could not read file: " + filename);
		}
	}

	@Override
	public MerkleTree getSignByLogLine(String logLine) throws StorageServiceException {
		try {
			for (MerkleTree sign : readSignFiles()) {
				if (sign.isLogExist(logLine)) {
					return sign;
				}
			}
		} catch (IOException ex) {
			throw new StorageServiceException(ex.getMessage(), ex);
		}
		throw new StorageServiceException("There isn't a sign file that contains this log line.");
	}

	private String getSignFileName(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		String signName = filename;
		int extIndex = filename.lastIndexOf(".");
		if (extIndex != -1) {
			signName = filename.substring(0, extIndex);
		}
		signName = signName.concat(this.logExtension);
		return signName;
	}

	private Stream<Path> getPathStream(Path path) throws IOException {
		return Files.walk(path, 1)
				.filter(p -> !p.equals(path))
				.map(path::relativize);
	}

	private List<MerkleTree> readSignFiles() throws IOException {
		List<MerkleTree> signList = new ArrayList<>();
		Stream<Path> signStream = getPathStream(this.signLocation);
		signStream.forEach(s -> {
			try (FileInputStream fi = new FileInputStream(this.signLocation.resolve(s).toFile())) {
				try (ObjectInputStream oi = new ObjectInputStream(fi)) {
					MerkleTree sign = (MerkleTree) oi.readObject();
					signList.add(sign);
				}
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		});
		return signList;
	}

}
