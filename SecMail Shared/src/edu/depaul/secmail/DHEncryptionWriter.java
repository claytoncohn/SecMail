package edu.depaul.secmail;

import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;

public class DHEncryptionWriter extends java.io.OutputStream {
	private Socket s;
	private InputStream is;
	private OutputStream os;
	
	DHEncryptionWriter(Socket s) throws IOException
	{
		this.s = s;
		os = s.getOutputStream();
		is = s.getInputStream();
	}
	
	@Override
	public void close() throws IOException
	{
		os.close();
		is.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		os.flush();
	}
	
	@Override
	public void write(byte[] b) throws IOException
	{
		os.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		os.write(b, off, len);
	}
	
	@Override
	public void write(int b) throws IOException
	{
		os.write(b);
	}
}
