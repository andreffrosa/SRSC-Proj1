package cryptography;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;

public class CryptographyHash extends AbstractCryptography {

	private MessageDigest innerHash;

	public CryptographyHash(Cipher encryptCipher, Cipher decryptCipher, MessageDigest innerHash, Mac outerMac, SecureRandom sr) {
		super(encryptCipher, decryptCipher,outerMac, sr);
		this.innerHash = innerHash;
	}

	public MessageDigest getInnerHash() {
		return innerHash;
	}

	public byte[] computeInnerHash(byte[] payload) {
		innerHash.update(payload);
		return innerHash.digest();
	}
	
	public boolean validadeInnerHash(byte[] message, byte[] expectedHash) throws InvalidKeyException {
		byte[] h = computeInnerHash(message);
		return MessageDigest.isEqual(h, expectedHash);
	}
	
	public byte[][] splitInnerHash(byte[] plainText) {
		return splitMessage(innerHash.getDigestLength(), plainText);
	}

	@Override
	public byte[] computeIntegrityProof(byte[] payload) throws InvalidKeyException {
		return computeInnerHash(payload);
	}

	@Override
	public boolean validateIntegrityProof(byte[] message, byte[] expectedHash) throws InvalidKeyException {
		return validadeInnerHash(message, expectedHash);
	}

	@Override
	public byte[][] splitIntegrityProof(byte[] plainText) {
		return splitInnerHash(plainText);
	}
	
}
