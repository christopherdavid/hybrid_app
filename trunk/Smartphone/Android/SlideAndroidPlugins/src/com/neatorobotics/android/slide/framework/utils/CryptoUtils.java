package com.neatorobotics.android.slide.framework.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*
 * Utility class for data encryption and decryption  
 */
public class CryptoUtils {
	
	private static final String ENCRYPT_DECRYPT_SEED = "neato_encrypt_pass123";
	private final static String HEX = "0123456789ABCDEF";
	
	public static String encrypt(String data) throws Exception {
		byte[] rawKey = getHash(ENCRYPT_DECRYPT_SEED);
		byte[] result = encrypt(rawKey, data.getBytes());
		return toHex(result);
	}
	
	public static String decrypt( String encryptedData) throws Exception {
		byte[] rawKey = getHash(ENCRYPT_DECRYPT_SEED);
		byte[] enc = toByte(encryptedData);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private static byte[] getHash(String key) throws Exception {
		byte[] output = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			output = md.digest(key.getBytes());
		} catch (Exception e) {
			return null;
		}
		return output;
	}
	
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
	
	private static byte[] toByte(String hexString) {
		
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
        	result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        }
        
        return result;
	}

	private static String toHex(byte[] buf) {
        if (buf == null) {
           return "";
		}
	
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
        	appendHex(result, buf[i]);
        }
        
        return result.toString();
	}


	private static void appendHex(StringBuffer sb, byte b) {
		 sb.append(HEX.charAt((b>>4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}	
}