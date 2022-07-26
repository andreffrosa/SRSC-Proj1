package secureSocket.secureMessages;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.concurrent.BrokenBarrierException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import cryptography.Cryptography;
import cryptography.nonce.NonceManager;
import kdc.needhamSchroeder.NS1;
import kdc.needhamSchroeder.NS2;
import kdc.needhamSchroeder.NS3;
import kdc.needhamSchroeder.NS4;
import kdc.needhamSchroeder.exceptions.UnkonwnIdException;
import secureSocket.exceptions.BrokenIntegrityException;
import secureSocket.exceptions.InvalidMacException;
import secureSocket.exceptions.InvalidPayloadTypeException;
import secureSocket.exceptions.ReplayedNonceException;

public class PayloadFactory {

	public static Payload buildPayload(byte payloadType, byte[] rawPayload, Cryptography cryptoManager, NonceManager nonceManager)
			throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException,
			UnrecoverableEntryException, KeyStoreException, CertificateException, IOException, InvalidMacException,
			ReplayedNonceException, BrokenIntegrityException, NoSuchProviderException, BrokenBarrierException, InvalidPayloadTypeException, UnkonwnIdException {

		switch (payloadType) {
		case DefaultPayload.TYPE:
			return DefaultPayload.deserialize(rawPayload, cryptoManager, nonceManager);
		case ClearPayload.TYPE:
			return ClearPayload.deserialize(rawPayload, cryptoManager);
		case NS1.TYPE: // TODO: isto não deveria estar aqui
			return NS1.deserialize(rawPayload, cryptoManager);
		case NS2.TYPE: // TODO: isto não deveria estar aqui
			return NS2.deserialize(rawPayload, cryptoManager);
		case NS3.TYPE: // TODO: isto não deveria estar aqui
			return NS3.deserialize(rawPayload, cryptoManager);
		case NS4.TYPE: // TODO: isto não deveria estar aqui
			return NS4.deserialize(rawPayload, cryptoManager);
		default:
			throw new InvalidPayloadTypeException("type: " + payloadType);
		}
	}
}

