package ee.tufan.log.sign.service;

import ee.tufan.log.sign.model.MerkleTree;
import ee.tufan.log.sign.model.MerkleTreeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class SignServiceImpl implements SignService {

	@Override
	public MerkleTree getSignOfFile(MultipartFile file) throws SignServiceException {
		try (InputStream inputStream = file.getInputStream()) {
			List<String> lines = getLines(inputStream);
			return new MerkleTree(lines);
		} catch (IOException | MerkleTreeException ex) {
			throw new SignServiceException(ex.getMessage(), ex);
		}
	}

	private List<String> getLines(InputStream inputStream) throws IOException {
		List<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		while (reader.ready()) {
			lines.add(reader.readLine());
		}
		return lines;
	}

}
