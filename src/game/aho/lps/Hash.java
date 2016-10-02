package game.aho.lps;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    /**
     * Converts bytes to hex String
     * 
     * @param data
     *            bytes of data
     * @return hex String from bytes
     */
    private static String convertByteToHex(byte data[]) {
	StringBuffer hexData = new StringBuffer();
	for (int byteIndex = 0; byteIndex < data.length; byteIndex++)
	    hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));
	return hexData.toString();
    }

    /**
     * Hashes text
     * @param text Text to hash
     * @return SHA-512 hashed text
     * @throws NoSuchAlgorithmException
     */
    public static String hashText(String text) throws NoSuchAlgorithmException {
	MessageDigest hash = MessageDigest.getInstance("SHA-512");
	hash.update(text.getBytes());
	return convertByteToHex(hash.digest());
    }
}
