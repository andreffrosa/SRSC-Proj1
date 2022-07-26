package cryptography;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore.SecretKeyEntry;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import util.Utils;
import util.CryptographyUtils;

public class CryptoFactory {

	private static final String PBKDF2_WITH_HMAC_SHA512 = "PBKDF2WithHmacSHA512";
	public static final int INITIAL_MSG_NUMBER = 1;
	public static final String IV = "iv";
	public static final String HASH_CIPHERSUITE = "hash-ciphersuite";
	public static final String OUTER_MAC_CIPHERSUITE = "outer-mac-ciphersuite";
	public static final String INNER_MAC_CIPHERSUITE = "inner-mac-ciphersuite";
	public static final String SESSION_CIPHERSUITE = "session-ciphersuite";
	public static final String OUTER_MAC_KEY = "outer-mac-key";
	public static final String INNER_MAC_KEY = "inner-mac-key";
	public static final String SESSION_KEY = "session-key";
	public static final String KEYSTORE = "keystore";
	public static final String KEYSTORE_PASSWORD = "keystore-password";
	public static final String KEYSTORE_TYPE = "keystore-type";
	public static final String SECURE_RANDOM = "secure-random";
	public static final String IV_SIZE = "iv-size";
	public static final String SESSION_KEY_GEN_ALGORITHM =  "session-key-gen-alg";
	public static final String SESSION_KEY_SIZE = "session-key-size";
	public static final String OUTTER_MAC_KEY_GEN_ALGORITHM = "outer-mac-key-gen-alg";
	public static final String OUTTER_MAC_KEY_SIZE = "outer-mac-key-size";
	public static final String INNER_MAC_KEY_GEN_ALGORITHM = "inner-mac-key-gen-alg";
	public static final String INNER_MAC_KEY_SIZE = "inner-mac-key-size";
	public static final String USE_HASH = "use-hash";
	public static final String HASH_CIPHER_SUITE = "hash-ciphersuite";
	public static final String TAG_SIZE = "tag-size";
	public static final String CIPHER_PROVIDER = "cipher-provider";
	public static final String OUTTER_MAC_PROVIDER = "outter-mac-provider";
	public static final String INNER_MAC_PROVIDER = "inner-mac-provider";
	public static final String HASH_PROVIDER = "hash-provider";
	public static final String SECURE_RANDOM_PROVIDER = "secure-random-provider";
	private static final String SALT = "tlsa";
	private static final String KA_ITERATIONS = "ka-iterations";
	private static final String DEFAULT_ITERATIONS_AS_STRING = "16";
	private static final String KM_ITERATIONS = "km-iterations";
	private static final String KEY_GEN_ALGORITHM = "key-gen-algorithm";
	private static final String KEY_SPEC_ALGORITM = "session-key-gen-alg";
	private static final String DEFAULT_ALGORITHM = "AES";
	private static final String KA_KEY_SIZE = "ka-key-size";
	private static final String KM_KEY_SIZE = "km-key-size";
	private static final String DEFAULT_KEY_SIZE = "256";
	private static final String DEFAULT_SESSION_CIPHERSUITE = "AES/CBC/PKCS5Padding";
	private static final String DEFAULT_SECURE_RANDOM_ALGORITHM = "sha1PRNG";
	private static final String DEFAULT_SECURE_RANDOM_PROVIDER = null;
	private static final String DEFAULT_TAG_SIZE = "128";
	private static final String DEFAULT_OUTER_MAC = "HMACSHA256";

	public static Cipher buildCipher(String cipherAlgorithm, int cipherMode, SecretKey key, byte[] iv, int tagSize, String provider)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, NoSuchProviderException {
		
		Cipher cipher = null;
		if(provider.equals("") || provider == null)
			cipher = Cipher.getInstance(cipherAlgorithm);
		else
			cipher = Cipher.getInstance(cipherAlgorithm, provider);
		
		if (iv != null && iv.length > 0) {
			if(tagSize > 0 && cipherAlgorithm.contains("GCM")) {
				cipher.init(cipherMode, key, new GCMParameterSpec(tagSize, iv));
			} else
				cipher.init(cipherMode, key, new IvParameterSpec(iv));
		} else
			cipher.init(cipherMode, key);

		return cipher;
	}

	public static Cipher buildGCMCipher(String cipherAlgorithm, int cipherMode, SecretKey key, byte[] iv, int tagSize)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(cipherMode, key, new GCMParameterSpec(tagSize, iv));
		return cipher;
	}

	public static Cipher buildCipher(String cipherAlgorithm, int cipherMode, SecretKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);

		cipher.init(cipherMode, key);

		return cipher;
	}

	public static Cipher buildCipher(String cipherAlgorithm, int cipherMode, SecretKey key, byte[] iv, String provider) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {

		if(provider != null) {
			System.out.println(CIPHER_PROVIDER + ": " +provider);
			Cipher cipher = Cipher.getInstance(cipherAlgorithm, provider);
			if(iv != null) {
				cipher.init(cipherMode, key, new IvParameterSpec(iv));
			}else
				cipher.init(cipherMode, key);

			return cipher;
		}else 
			return buildCipher(cipherAlgorithm, cipherMode, key, iv);

	}

	public static Cipher buildCipher(String cipherAlgorithm, int cipherMode, SecretKey key, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException { 

		Cipher cipher =	Cipher.getInstance(cipherAlgorithm);
		if( iv != null )
			cipher.init(cipherMode, key, new IvParameterSpec(iv));
		else
			cipher.init(cipherMode, key);

		return cipher;
	}

	public static Mac initMac(String macAlgorithm, SecretKey key, String provider) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {

		Mac mac;
		if(provider== null || provider.equals("")) 
			mac = Mac.getInstance(macAlgorithm);
		else {
			System.out.println("MAC PROVIDER: " + provider);
			mac = Mac.getInstance(macAlgorithm, provider);
		}
		mac.init(key);
		return mac;
	}

	public static MessageDigest buildHash(String hashAlgorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {

		if(provider==null || provider.equals(""))
			return MessageDigest.getInstance(hashAlgorithm);
		else {
			System.out.println(HASH_PROVIDER + ": " + provider);
			return MessageDigest.getInstance(hashAlgorithm, provider);
		}
	}

	public static Cryptography loadFromConfig(String path) throws IOException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {

		//Load file
		Properties ciphersuit_properties = loadFile(path);

		//Create Secure Random
		SecureRandom sr = generateRandom(ciphersuit_properties.getProperty(SECURE_RANDOM), ciphersuit_properties.getProperty(SECURE_RANDOM_PROVIDER));

		//load keys
		SecretKey[] keys = loadKeys(ciphersuit_properties.getProperty(KEYSTORE_TYPE), ciphersuit_properties.getProperty(KEYSTORE_PASSWORD),
				ciphersuit_properties.getProperty(KEYSTORE), ciphersuit_properties.getProperty(SESSION_KEY), ciphersuit_properties.getProperty(INNER_MAC_KEY),
				ciphersuit_properties.getProperty(OUTER_MAC_KEY));

		//Generate IV

		String ivString = ciphersuit_properties.getProperty(IV);
		int ivSize = Integer.parseInt(ciphersuit_properties.getProperty(IV_SIZE));
		byte[] iv = null;
		if(ivSize > 0 ) {
			if(ivString != null)
				iv = Utils.unparse(ivString);
			else
				iv = generateIv(ciphersuit_properties.getProperty(SESSION_CIPHERSUITE), ivSize, sr);
		}
		//Build encrypt Cipher 
		Cipher encryptCipher = null, decryptCipher = null;
		if (keys[0] != null) {
			encryptCipher = buildCipher(ciphersuit_properties.getProperty(SESSION_CIPHERSUITE), Cipher.ENCRYPT_MODE, keys[0], iv, ciphersuit_properties.getProperty(CIPHER_PROVIDER));
			decryptCipher = buildCipher(ciphersuit_properties.getProperty(SESSION_CIPHERSUITE), Cipher.DECRYPT_MODE, keys[0], iv, ciphersuit_properties.getProperty(CIPHER_PROVIDER));
		}

		Mac outerMac = null;
		if(ciphersuit_properties.getProperty(OUTER_MAC_CIPHERSUITE)!=null)
			outerMac = initMac(ciphersuit_properties.getProperty(OUTER_MAC_CIPHERSUITE), keys[2], ciphersuit_properties.getProperty(OUTTER_MAC_PROVIDER));

		String hashAlgorithm = ciphersuit_properties.getProperty(HASH_CIPHERSUITE);
		if(hashAlgorithm != null) {
			MessageDigest innerHash = buildHash(hashAlgorithm, ciphersuit_properties.getProperty(HASH_PROVIDER));
			return new CryptographyHash(encryptCipher, decryptCipher,innerHash, outerMac, sr);
		} else {
			Mac innerMac = null;
			if(keys[1] != null && ciphersuit_properties.getProperty(INNER_MAC_CIPHERSUITE) != null )
				innerMac = initMac(ciphersuit_properties.getProperty(INNER_MAC_CIPHERSUITE), keys[1], ciphersuit_properties.getProperty(INNER_MAC_PROVIDER));

			return new CryptographyDoubleMac(encryptCipher, decryptCipher, innerMac, outerMac, sr);
		}
	}

	public static byte[] generateIv(String cipherAlgorithm, int size, SecureRandom sr ) {

		byte[] iv = null;
		if(cipherAlgorithm.contains("CTR")) {
			iv = CryptographyUtils.createCtrIvForAES(INITIAL_MSG_NUMBER, sr).getIV();
		} else if( size > 0 ) {
			iv = CryptographyUtils.createGenericIvForAES(size).getIV();
		} else
			iv = new byte[0];

		return iv;
	}

	public static SecureRandom generateRandom(String secureRandomAlgorithm, String provider ) throws NoSuchAlgorithmException, NoSuchProviderException {
		if(provider == null || provider.equals(""))
			return java.security.SecureRandom.getInstance(secureRandomAlgorithm);
		else {
			System.out.println(SECURE_RANDOM_PROVIDER + ": " + provider);
			return java.security.SecureRandom.getInstance(secureRandomAlgorithm, provider);
		}
	}

	public static Properties loadFile(String path) throws IOException {
		InputStream inputStream = new FileInputStream(path);
		Properties ciphersuit_properties = new Properties();
		ciphersuit_properties.load(inputStream);
		return ciphersuit_properties;
	}

	public static byte[] serialize(String path) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {

		//Load file
		Properties ciphersuit_properties = loadFile(path);

		//Create Secure Random
		SecureRandom secureRandom = generateRandom(ciphersuit_properties.getProperty(SECURE_RANDOM), ciphersuit_properties.getProperty(SECURE_RANDOM_PROVIDER));

		boolean useHash = Boolean.parseBoolean(ciphersuit_properties.getProperty(USE_HASH));
		SecretKey[] keys = generateKeys(ciphersuit_properties.getProperty(SESSION_KEY_GEN_ALGORITHM), Integer.parseInt(ciphersuit_properties.getProperty(SESSION_KEY_SIZE)),
				ciphersuit_properties.getProperty(OUTTER_MAC_KEY_GEN_ALGORITHM), Integer.parseInt(ciphersuit_properties.getProperty(OUTTER_MAC_KEY_SIZE)),
				ciphersuit_properties.getProperty(INNER_MAC_KEY_GEN_ALGORITHM), Integer.parseInt( ciphersuit_properties.getProperty(INNER_MAC_KEY_SIZE)),
				useHash);

		// Generate IV
		byte[] iv = generateIv(ciphersuit_properties.getProperty(SESSION_CIPHERSUITE), Integer.parseInt(ciphersuit_properties.getProperty(IV_SIZE)), secureRandom);

		// Tag Size -> GCM
		String aux = ciphersuit_properties.getProperty(TAG_SIZE);
		int tagSize = (aux == null) ? 0 : Integer.parseInt(aux);

		// Hash Suite
		String hashAlgorithm = ciphersuit_properties.getProperty(HASH_CIPHER_SUITE);
		
		//Write to buff
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);

		//writing random number provider and algorithm
		String secureRandomProvider = ciphersuit_properties.getProperty(SECURE_RANDOM_PROVIDER);
		if(secureRandomProvider == null)
			dataOut.writeUTF("");
		else
			dataOut.writeUTF(secureRandomProvider);
		dataOut.writeUTF(ciphersuit_properties.getProperty(SECURE_RANDOM));
		
		//writing cipher provider and cipher suite itself
		String cipherProvider = ciphersuit_properties.getProperty(CIPHER_PROVIDER);
		if(cipherProvider == null)
			dataOut.writeUTF("");
		else
			dataOut.writeUTF(cipherProvider);
		dataOut.writeUTF(ciphersuit_properties.getProperty(SESSION_CIPHERSUITE));
		
		//writing session key	
		dataOut.writeUTF(ciphersuit_properties.getProperty(SESSION_KEY_GEN_ALGORITHM));
		byte[] session_key_encoded = keys[0].getEncoded();
		dataOut.writeInt(session_key_encoded.length);
		dataOut.write(session_key_encoded, 0, session_key_encoded.length);

		//writing iv
		dataOut.writeInt(iv.length);
		dataOut.write(iv, 0, iv.length);

		//writing tagsize
		dataOut.writeInt(tagSize);

		//writing outerMac provider and outerMac itself
		String outerMacProvider = ciphersuit_properties.getProperty(OUTTER_MAC_PROVIDER);
		if(outerMacProvider == null)
			dataOut.writeUTF("");
		else
			dataOut.writeUTF(outerMacProvider);
		dataOut.writeUTF(ciphersuit_properties.getProperty(OUTER_MAC_CIPHERSUITE));
		dataOut.writeUTF(ciphersuit_properties.getProperty(OUTTER_MAC_KEY_GEN_ALGORITHM));
		byte[] outer_mac_key_encoded = keys[2].getEncoded();
		dataOut.writeInt(outer_mac_key_encoded.length);
		dataOut.write(outer_mac_key_encoded, 0, outer_mac_key_encoded.length);

		dataOut.writeBoolean(useHash);

		if(useHash) {
			String hashProvider = ciphersuit_properties.getProperty(HASH_PROVIDER);
			if(hashProvider == null)
				dataOut.writeUTF("");
			else
				dataOut.writeUTF(hashProvider);
			dataOut.writeUTF(hashAlgorithm);
		
		} else {
			
			String innerMacProvider = ciphersuit_properties.getProperty(INNER_MAC_PROVIDER);
			if(innerMacProvider == null)
				dataOut.writeUTF("");
			else
				dataOut.writeUTF(innerMacProvider);
			dataOut.writeUTF(ciphersuit_properties.getProperty(INNER_MAC_CIPHERSUITE));
			dataOut.writeUTF(ciphersuit_properties.getProperty(INNER_MAC_KEY_GEN_ALGORITHM));
			byte[] inner_mac_key_encoded = keys[1].getEncoded();
			dataOut.writeInt(inner_mac_key_encoded.length);
			dataOut.write(inner_mac_key_encoded, 0, inner_mac_key_encoded.length);
		}

		dataOut.flush();
		byteOut.flush();

		byte[] data = byteOut.toByteArray();

		dataOut.close();
		byteOut.close();

		return data;
	}

	public static Cryptography deserialize(byte[] rawParams ) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {

		ByteArrayInputStream byteIn = new ByteArrayInputStream(rawParams);
		DataInputStream dataIn = new DataInputStream(byteIn);
		
		
		String seecureRandomProvider = dataIn.readUTF();
		String secureRandomAlgorithm = dataIn.readUTF();
		
		String cipherProvider = dataIn.readUTF();
		String cipherAlgorithm = dataIn.readUTF();
		String session_key_alg = dataIn.readUTF();
		int length = dataIn.readInt();
		byte[] ks_encoded = new byte[length];
		dataIn.read(ks_encoded, 0, length);
		SecretKey ks = new SecretKeySpec(ks_encoded, session_key_alg);

		length = dataIn.readInt();
		byte[] iv = new byte[length];
		dataIn.read(iv, 0, length);

		int tagSize = dataIn.readInt();

		String outMacProvider = dataIn.readUTF();
		String outerMacAlgorithm = dataIn.readUTF();
		String outer_key_alg = dataIn.readUTF();
		length = dataIn.readInt();
		byte[] outer_mac_key_encoded = new byte[length];
		dataIn.read(outer_mac_key_encoded, 0, length);
		SecretKey kms = new SecretKeySpec(outer_mac_key_encoded, outer_key_alg);

		boolean useHash = dataIn.readBoolean();

		Cryptography cryptoManager = null;
		Cipher encryptCipher = CryptoFactory.buildCipher(cipherAlgorithm, Cipher.ENCRYPT_MODE, ks, iv, tagSize, cipherProvider);
		Cipher decryptCipher = CryptoFactory.buildCipher(cipherAlgorithm, Cipher.DECRYPT_MODE, ks, iv, tagSize, cipherProvider);
		Mac outerMac = CryptoFactory.initMac(outerMacAlgorithm, kms, outMacProvider);
		SecureRandom secureRandom = CryptoFactory.generateRandom(secureRandomAlgorithm, seecureRandomProvider);

		if(useHash) {
			String hashAlgorithm = dataIn.readUTF();
			MessageDigest innerHash = CryptoFactory.buildHash(hashAlgorithm, hashAlgorithm);

			cryptoManager = new CryptographyHash(encryptCipher, decryptCipher, innerHash, outerMac, secureRandom);
		} else {
			String innerMacProvider = dataIn.readUTF();
			String innerMacAlgorithm = dataIn.readUTF();
			String inner_key_alg = dataIn.readUTF();
			length = dataIn.readInt();
			byte[] inner_mac_key_encoded = new byte[length];
			dataIn.read(inner_mac_key_encoded, 0, length);
			SecretKey kms2 = new SecretKeySpec(inner_mac_key_encoded, inner_key_alg);

			Mac innerMac = CryptoFactory.initMac(innerMacAlgorithm, kms2, innerMacProvider);

			cryptoManager = new CryptographyDoubleMac(encryptCipher, decryptCipher, innerMac, outerMac, secureRandom);
		}

		return cryptoManager;
	}

	public static SecretKey generateKey(String key_gen_alg, int key_size ) throws NoSuchAlgorithmException {
		return CryptographyUtils.generateKey(key_gen_alg, key_size);
	}

	public static SecretKey[] generateKeys(String session_key_gen_alg, int session_key_size, String outer_key_gen_alg, int outer_mac_key_size, String inner_key_gen_alg, int inner_mac_key_size, boolean useHash ) throws NoSuchAlgorithmException {
		SecretKey ks = generateKey(session_key_gen_alg, session_key_size); // Session key
		SecretKey kms = useHash ? null : generateKey(inner_key_gen_alg, inner_mac_key_size); // Inner mac key
		SecretKey kms2 = generateKey(outer_key_gen_alg, outer_mac_key_size); // Outer mac key

		return new SecretKey[]{ks, kms, kms2};
	}

	public static SecretKey readKey(KeyStore ks, KeyStore.PasswordProtection ks_pp, String alias) throws
	NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
	CertificateException, FileNotFoundException, IOException {
		SecretKeyEntry entry = (KeyStore.SecretKeyEntry)ks.getEntry(alias, ks_pp);
		return entry.getSecretKey();
	}

	public static SecretKey[] loadKeys(String keyStoreType, String password, String keystore, String sessionKey, String innerMacKey, String outterMacKey) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException, FileNotFoundException, IOException {

		// Load KeyStore
		KeyStore key_store = KeyStore.getInstance(keyStoreType);
		char[] password_arr = password.toCharArray();
		key_store.load(new FileInputStream(keystore), password_arr);
		KeyStore.PasswordProtection  ks_pp = new KeyStore.PasswordProtection(password_arr);

		// Load Keys from KeyStore
		SecretKey ks = null, kim = null, kom = null;
		if(sessionKey != null)
			ks = readKey(key_store, ks_pp, sessionKey);

		if(innerMacKey!= null)
			kim = readKey(key_store, ks_pp, innerMacKey);

		if(outterMacKey!=null)
			kom = readKey(key_store, ks_pp, outterMacKey);

		return new SecretKey[]{ks, kim, kom};
	}
	
	public static AbstractCryptography getInstace(String password, String cipherSuitePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		
		Properties props = loadFile(cipherSuitePath);
		SecretKey[] keys = genKeysFromPassword(password, props);
				
		String cipherAlgorithm = props.getProperty(SESSION_CIPHERSUITE, DEFAULT_SESSION_CIPHERSUITE);
	
		SecureRandom sr = generateRandom(props.getProperty(SECURE_RANDOM, DEFAULT_SECURE_RANDOM_ALGORITHM), props.getProperty(SECURE_RANDOM_PROVIDER, DEFAULT_SECURE_RANDOM_PROVIDER));
		byte[] iv = readIv(props, sr);
		int tagSize = Integer.parseInt(props.getProperty(TAG_SIZE, DEFAULT_TAG_SIZE));
		String cipher_provider = props.getProperty(CIPHER_PROVIDER);
		String mac_provider = props.getProperty(OUTTER_MAC_PROVIDER);
		String macAlgorithm = props.getProperty(OUTER_MAC_CIPHERSUITE, DEFAULT_OUTER_MAC);
		
		Cipher encryptCipher = buildCipher(cipherAlgorithm, Cipher.ENCRYPT_MODE, keys[0], iv, tagSize, cipher_provider);
		Cipher decryptCipher = buildCipher(cipherAlgorithm, Cipher.DECRYPT_MODE, keys[0], iv, tagSize, cipher_provider);
		Mac outerMac = initMac(macAlgorithm, keys[1], mac_provider);
		
		return new AbstractCryptography(encryptCipher, decryptCipher, outerMac, sr) {
			
			@Override
			public boolean validateIntegrityProof(byte[] message, byte[] expectedMac) throws InvalidKeyException {
				return true;
			}
			
			@Override
			public byte[][] splitIntegrityProof(byte[] plainText) {
				return null;
			}
			
			@Override
			public byte[] computeIntegrityProof(byte[] payload) throws InvalidKeyException {
				return null;
			}
		};
	}

	public static SecretKey[] genKeysFromPassword(String password, Properties props) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		int kaIterations = Integer.parseInt( props.getProperty(KA_ITERATIONS, DEFAULT_ITERATIONS_AS_STRING) );
		int kmIerations = Integer.parseInt( props.getProperty(KM_ITERATIONS, DEFAULT_ITERATIONS_AS_STRING) );
		String keyGenAlgorithm = props.getProperty(KEY_GEN_ALGORITHM, PBKDF2_WITH_HMAC_SHA512);
		String keySpecAlgorithm = props.getProperty(KEY_SPEC_ALGORITM, DEFAULT_ALGORITHM);
		int ka_key_size = Integer.parseInt(props.getProperty(KA_KEY_SIZE, DEFAULT_KEY_SIZE));
		int km_key_size = Integer.parseInt(props.getProperty(KM_KEY_SIZE, DEFAULT_KEY_SIZE));
		
		SecretKey ka = CryptographyUtils.generateKey(password, SALT, kaIterations, keyGenAlgorithm, keySpecAlgorithm, ka_key_size);
		SecretKey km = CryptographyUtils.generateKey(password, SALT, kmIerations, keyGenAlgorithm, keySpecAlgorithm, km_key_size);
		
		return new SecretKey[] {ka,km};
	}

	public static byte[] readIv(Properties props, SecureRandom sr) {
		
		String ivString = props.getProperty(IV);
		int ivSize = Integer.parseInt(props.getProperty(IV_SIZE));
		byte[] iv = null;
		if(ivSize > 0 ) {
			if(ivString != null)
				iv = Utils.unparse(ivString);
			else
				iv = generateIv(props.getProperty(SESSION_CIPHERSUITE), ivSize, sr);
		}
		return iv;
	}

}
