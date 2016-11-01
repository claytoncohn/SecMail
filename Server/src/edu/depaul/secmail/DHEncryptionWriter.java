package edu.depaul.secmail;

import java.io.OutputStream;
import java.io.IOException;

public class DHEncryptionWriter extends java.io.OutputStream {
	OutputStream os;
	
	DHEncryptionWriter(OutputStream o)
	{
		this.os = o;
	}
	
	@Override
	public void close() throws IOException
	{
		os.close();
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
