package kdc.needhamSchroeder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import cryptography.Cryptography;
import secureSocket.exceptions.InvalidMacException;
import secureSocket.secureMessages.Payload;
import util.Utils;

public class NS4 implements Payload {

	public static final byte TYPE = 0x14;
	
	private long nb;
	private byte[] nb_bytes;
	private byte[] cipherText;
	private byte[] outermac;
	
	public NS4(long nb, Cryptography cryptoManager) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException {
		this.nb = nb;
		nb_bytes = computeNbBytes(nb);
		cipherText = cryptoManager.encrypt(nb_bytes);
		outermac = cryptoManager.computeOuterMac(cipherText);
	}	
	
	private NS4(long nb, byte[] cipherText, byte[] outermac) throws IOException {
		this.nb = nb;
		this.nb_bytes = computeNbBytes(nb);
		this.cipherText = cipherText;
		this.outermac = outermac;
	}

	private byte[] computeNbBytes(long nb) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		dataOut.writeLong(nb);
		dataOut.flush();
		byteOut.flush();

		byte[] msg = byteOut.toByteArray();

		dataOut.close();
		byteOut.close();
		
		return msg;
	}

	@Override
	public byte getPayloadType() {
		return TYPE;
	}

	@Override
	public byte[] serialize() {
		return Utils.concat(cipherText, outermac);
	}

	@Override
	public short size() {
		return (short) (cipherText.length + outermac.length);
	}

	public static Payload deserialize(byte[] rawPayload, Cryptography cryptoManager) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidMacException {
		byte[][] messageParts = cryptoManager.splitOuterMac(rawPayload);		
		if (!cryptoManager.validateOuterMac(messageParts[0], messageParts[1]))
			throw new InvalidMacException("Invalid Outter Mac");
		else {
			byte[] plainText = cryptoManager.decrypt(messageParts[0]);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(plainText);

			DataInputStream dataIn = new DataInputStream(byteIn);

			long nb = dataIn.readLong();
			
			dataIn.close();
			byteIn.close();

			return new NS4(nb, messageParts[0], messageParts[1]);
		}
	}

	public long getNb() {
		return nb;
	}

	public byte[] getNb_bytes() {
		return nb_bytes;
	}

	public byte[] getCipherText() {
		return cipherText;
	}

	public byte[] getOutermac() {
		return outermac;
	}
}
