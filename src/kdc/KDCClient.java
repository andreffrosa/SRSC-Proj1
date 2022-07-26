package kdc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.concurrent.BrokenBarrierException;

import javax.crypto.NoSuchPaddingException;

import cryptography.Cryptography;
import kdc.needhamSchroeder.exceptions.InvalidChallangeReplyException;
import kdc.needhamSchroeder.exceptions.TooManyTriesException;
import kdc.needhamSchroeder.exceptions.UnkonwnIdException;
import secureSocket.exceptions.InvalidPayloadTypeException;

public interface KDCClient {

	public Cryptography getSessionParameters(String b) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException, CertificateException, InvalidChallangeReplyException, NoSuchProviderException, InvalidPayloadTypeException, BrokenBarrierException, TooManyTriesException, UnkonwnIdException;
	
	public InetSocketAddress getMyAddr() ;
	
}
