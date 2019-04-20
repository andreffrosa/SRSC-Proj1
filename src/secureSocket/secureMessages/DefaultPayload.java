package secureSocket.secureMessages;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import Utils.ArrayUtils;
import secureSocket.Cryptography;

// TODO : find better name for the class
public class DefaultPayload implements Payload {
	
	private long id;
	private long nonce;
	private byte[] message;
	private byte[] innerMac;
	private byte[] cipherText;
	private byte[] outterMac;
	
	public DefaultPayload(long id, long nonce, byte[] message) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, UnrecoverableEntryException, KeyStoreException, CertificateException, IllegalBlockSizeException, BadPaddingException, ShortBufferException {
		this.message = message;
		this.id = id;
		this.nonce = nonce;
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		
		dataOut.writeLong(id);
		dataOut.writeLong(nonce);
		dataOut.write(message, 0, message.length);
		dataOut.flush();
		byteOut.flush();
		
		// cipherText
		byte[] Mp = byteOut.toByteArray();
		
		dataOut.close();
		byteOut.close();
		
		Cryptography criptoService = new Cryptography(Cipher.ENCRYPT_MODE); // TODO: Isto assim naõ parece bom, tem de se arranjar melhor maneira de interajir com esta class
		
		this.cipherText = criptoService.encrypt(Mp);
		
		//Append MacDoS
		this.innerMac = criptoService.computeMacDoS(Mp);
		this.outterMac = criptoService.computeMacDoS(this.cipherText);
	}
	
	public byte payloadType() {
		return 0x01;
	}
	
	public byte[] serialize() {
		return ArrayUtils.concat(this.cipherText, this.outterMac);
	}
	
	public int size() {
		return cipherText.length + outterMac.length;
	}
}