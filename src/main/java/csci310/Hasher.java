package csci310;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
	
	// first argument is text to hash
	// second argument is the MessageDigest algorithm to use e.g. "SHA-256"
	public static String hash(String password, String type) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(type);
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hash);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}
	
	// converts bytes to a hexadecimal string
	public static String bytesToHex(byte [] bytes) {
		String hexString = "";
	    for (int i = 0; i < bytes.length; i++) {
	    	String hex = Integer.toHexString(0xff & bytes[i]);
	    	if(hex.length() == 1) {
	    		hexString = hexString + "0";
			}
	    	hexString = hexString + hex;
	    }
	    return hexString;
	}

}
