package edu.depaul.secmail;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SealedObject;

import edu.depaul.secmail.SecMailStaticEncryption.DHKeyClient;
import edu.depaul.secmail.SecMailStaticEncryption.DHKeyServer;

import java.io.IOException;

public class DHEncryptionReader extends java.io.InputStream {
	private Socket s; //clientSocket
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private byte key[];
	private MessageDigest hash;
	
	DHEncryptionReader(Socket socket, boolean isServer) throws IOException
	{
		os = new ObjectOutputStream(s.getOutputStream());
		is = new ObjectInputStream(s.getInputStream());
		try {
			this.hash = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //it's java's fault that this is not SHA-256; Java doesn't implement AES with 256 bit keys 
		
		if (isServer){
			DHKeyServer server = new DHKeyServer(s, 512);
			server.run();
			this.key= hash.digest(server.getKey());
		}
		else{
			DHKeyClient client = new DHKeyClient(s);
			client.run();
			this.key= hash.digest(client.getKey());
		}
	}
	public Serializable readObject() throws ClassNotFoundException, IOException{
		return SecMailStaticEncryption.decryptPacket(((SealedObject)is.readObject()), this.key);
	}
	
	
	@Override
	public int read() throws IOException
	{
		return is.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException
	{
		return is.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return is.read(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		return is.skip(n);
	}
	
	@Override
	public int available() throws IOException
	{
		return is.available();
	}
	
	@Override
	public void close() throws IOException
	{
		is.close();
		os.close();
	}
	
	@Override
	public void mark(int readlimit)
	{
		is.mark(readlimit);
	}
	
	@Override
	public void reset() throws IOException
	{
		is.reset();
	}
	
	@Override
	public boolean markSupported()
	{
		return is.markSupported();
	}
}
