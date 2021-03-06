package edu.depaul.secmail;

public class PacketHeader implements java.io.Serializable {
	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long serialVersionUID = -1651542601598413965L;
	public long length;
	public String string;
	public Command command;
	
	PacketHeader(Command c)
	{
		command = c;
	}
	
	PacketHeader()
	{
		
	}
	
	public Command getCommand()
	{
		return command;
	}
	
	public void setCommand(Command c)
	{
		this.command = c;
	}
	
	public void setLength(long l)
	{
		this.length = l;
	}
	
	public long getLength()
	{
		return this.length;
	}
	
	public void setString(String s)
	{
		this.string = s;
	}
	
	public String getString()
	{
		return this.string;
	}
}
