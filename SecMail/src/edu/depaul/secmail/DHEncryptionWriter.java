package edu.depaul.secmail;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

<<<<<<< HEAD
/*
 * to write object, override WriteObject()
 * 		take the object, seal it, send sealed object
 * to read object, override ReadObject()
 * 		take the object, unseal it, return unsealed object
 * 
 * needs to do the key exchange (server or client)
 * 	store the key
 * 	hash the key into 128 bits
 * 
 * 
 * 
 * */


public class DHEncryptionWriter{
=======
public class DHEncryptionWriter extends java.io.OutputStream {
	//@SuppressWarnings("unused")
>>>>>>> ee2fce933ef95eaf24a9bb8d7b050ab12740e7c1
	private Socket s;
	
	private ObjectInputStream is;
	private ObjectOutputStream os;
	
	
	
	
	public void writeObject(Serializable obj) throws IOException{
//		SecMailStaticEncryption.encryptObject(packet, keyBytes)
		
		
	}
	
	
	DHEncryptionWriter(Socket s, boolean isServer) throws IOException
	{
		this.s = s;
<<<<<<< HEAD
		os = new ObjectOutputStream(s.getOutputStream());
		is = new ObjectInputStream(s.getInputStream());
		
		
		
=======
		os = this.s.getOutputStream();
		is = this.s.getInputStream();
>>>>>>> ee2fce933ef95eaf24a9bb8d7b050ab12740e7c1
	}
	
	
	public void close() throws IOException
	{
		os.close();
		is.close();
	}
	
	public void flush() throws IOException
	{
		os.flush();
	}
	
	public void write(byte[] b) throws IOException
	{
		os.write(b);
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
