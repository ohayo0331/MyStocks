package csci310;

import static org.junit.Assert.*;

import org.junit.Test;

public class HasherTest {
	
	@Test
	public void testConstructor() {
		// Tests class declaration
		Hasher h = new Hasher();
	}

	@Test
	public void testHash() {
		// Tests empty hash
		String stringEmpty = "";
		String hashEmpty = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
		assertEquals(hashEmpty, Hasher.hash(stringEmpty, "SHA-256"));
		
		// Tests valid hash
		String stringPassword = "password";
		String hashPassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
		assertEquals(hashPassword, Hasher.hash(stringPassword, "SHA-256"));
		
		// Tests invalid algorithm
		String hashError = "";
		assertEquals(hashError, Hasher.hash(stringPassword, ""));
	}
	
	@Test
	public void testBytesToHex() {
		// Tests empty bytes
		byte[] bytesEmpty = {};
		String hexEmpty = "";
		assertEquals(hexEmpty, Hasher.bytesToHex(bytesEmpty));
		
		// Tests zero bytes
		byte[] bytesZero = {0x00, 0x00, 0x00};
		String hexZero = "000000";
		assertEquals(hexZero, Hasher.bytesToHex(bytesZero));
		
		// Tests small bytes
		byte[] bytesSmall = {0x02, 0x05, 0x08, 0x0b, 0x0f};
		String hexSmall = "0205080b0f";
		assertEquals(hexSmall, Hasher.bytesToHex(bytesSmall));
		
		// Tests large bytes
		byte[] bytesLarge = {0x64, 0x2e, 0x10, 0x37, (byte)0xc6, (byte)0xff};
		String hexLarge = "642e1037c6ff";
		assertEquals(hexLarge, Hasher.bytesToHex(bytesLarge));
	}

}