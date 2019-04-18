package secureSocket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Properties;

public class SecureDatagramSocket implements java.io.Closeable {

	private static final String CIPHERSUITE_CONFIG_PATH = "configs/server/ciphersuite.conf";

	private DatagramSocket socket;
	private Properties ciphersuit_properties;
	
	public SecureDatagramSocket(int port, InetAddress laddr) throws IOException {
		if( laddr.isMulticastAddress() ) {
			MulticastSocket ms = new MulticastSocket(port);
			ms.joinGroup(laddr);
			socket = ms;
		} else {
			socket = new DatagramSocket(port, laddr);
		}
		loadCipherSuitConfig();
	}

	public SecureDatagramSocket(InetSocketAddress addr) throws IOException {
		this(addr.getPort(), addr.getAddress());
	}

	public SecureDatagramSocket() throws IOException {
		socket = new DatagramSocket();
		loadCipherSuitConfig();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	public void receive(DatagramPacket p) throws IOException {
		socket.receive(p);
		decryptSecurePacket(p);
	}

	public void send(DatagramPacket p) throws IOException {
		encryptSecurePacket(p);
		socket.send(p);
	}

	// Porque estás a retornar bool?
	private boolean loadCipherSuitConfig() {
		try {
			InputStream inputStream = new FileInputStream(CIPHERSUITE_CONFIG_PATH);
			ciphersuit_properties = new Properties();
			ciphersuit_properties.load(inputStream);
			return true;
		} catch (IOException e) {	
			e.printStackTrace();
			return false;
		}
	}
	
	private void encryptSecurePacket(DatagramPacket p) {
		// TODO
	}
	
	private void decryptSecurePacket(DatagramPacket p) {
		// TODO
	}
}
