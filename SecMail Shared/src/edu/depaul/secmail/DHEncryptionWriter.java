package edu.depaul.secmail;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SealedObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.depaul.secmail.SecMailStaticEncryption.*;

public class DHEncryptionWriter{
	private Socket s; //clientSocket
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private byte key[];
	private MessageDigest hash;
	
	
	DHEncryptionWriter(Socket s, boolean isServer) throws IOException, NoSuchAlgorithmException
	{
		this.s = s;
		os = new ObjectOutputStream(s.getOutputStream());
		is = new ObjectInputStream(s.getInputStream());
		this.hash = MessageDigest.getInstance("MD5"); //it's java's fault that this is not SHA-256; Java doesn't implement AES with 256 bit keys 
		
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
	
	public void writeObject(Serializable obj) throws IOException{
		this.os.writeObject(SecMailStaticEncryption.encryptObject(obj, this.key));
	}
	
	
	
	
	public void close() throws IOException
	{
		s.close();
		os.close();
		is.close();
	}
	
	public void flush() throws IOException
	{
		os.flush();
	}
	
	
	
	public void write(byte[] b) throws IOException
	{
//		os.write(b);
		
		
		
	}
	
	public void write(byte[] b, int off, int len) throws IOException
	{
		os.write(b, off, len);
	}
	
	public void write(int b) throws IOException
	{
		os.write(b);
	}
}
