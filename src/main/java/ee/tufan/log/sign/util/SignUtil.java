package ee.tufan.log.sign.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUtil {

	private static final String ALGORITHM = "SHA-512";

	public static String sign(String raw) throws SignUtilException {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			byte[] messageDigest = md.digest(raw.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException ex) {
			throw new SignUtilException(ex.getMessage(), ex);
		}
	}

}
