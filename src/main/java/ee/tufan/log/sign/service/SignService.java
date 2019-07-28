package ee.tufan.log.sign.service;

import ee.tufan.log.sign.model.MerkleTree;
import org.springframework.web.multipart.MultipartFile;

public interface SignService {

	MerkleTree getSignOfFile(MultipartFile file) throws SignServiceException;

}
