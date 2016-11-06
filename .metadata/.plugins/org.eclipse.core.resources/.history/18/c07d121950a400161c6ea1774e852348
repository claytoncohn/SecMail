package edu.depaul.secmail;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;

public class DHEncryptionReader extends java.io.InputStream {
	private Socket s;
	private InputStream is;
	private OutputStream os;
	
	DHEncryptionReader(Socket socket) throws IOException
	{
		this.s = socket;
		this.is = s.getInputStream();
		this.os = s.getOutputStream();
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
