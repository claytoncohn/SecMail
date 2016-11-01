package edu.depaul.secmail;

public class PacketHeader implements java.io.Serializable {
	public int length;
	public Command command;
	
	public Command getCommand()
	{
		return command;
	}
}
