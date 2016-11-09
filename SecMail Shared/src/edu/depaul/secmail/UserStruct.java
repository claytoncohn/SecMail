package edu.depaul.secmail;

import java.io.Serializable;

public class UserStruct implements Serializable {
	private final int DEFAULT_PORT = 57890;
	private String name = null;
	private String domain = null;
	private int port = 0;
	private boolean isValid = false;
	
	//Constructor to parse the incoming string for
	UserStruct(String incoming)
	{
		//parse the username
		String portString = null;
		
		//user part first
		int atLocation = incoming.indexOf("@");
		if (atLocation <= 0)
		{
			setNulls();
			return;
		}
		
		this.name = incoming.substring(0,atLocation);
		
		//check if there is a port
		int colonLocation = incoming.indexOf(":");
		if (colonLocation > atLocation)
		{
			this.domain = incoming.substring(atLocation+1,colonLocation);
			portString = incoming.substring(colonLocation+1); // get to end
		}
		else if (colonLocation < atLocation && colonLocation > 0)
		{
			setNulls();
			return;
		}
		else
			this.domain = incoming.substring(atLocation+1);
		
		if (portString != null)
			this.port = Integer.valueOf(portString);
		else
			this.port = DEFAULT_PORT;
		
		this.isValid = true;
				
	}
	
	private void setNulls()
	{
		this.name = null;
		this.domain = null;
		this.port = 0;
		this.isValid = false;
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	public String getUser()
	{
		return this.name;
	}
	
	public String getDomain()
	{
		return this.domain;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public String getFormat()
	{
		return "<user>@<domain>(:<port>)";
	}
	
	public String compile()
	{
		StringBuilder ret = new StringBuilder(this.name+"@"+this.domain);
		if(this.port != DEFAULT_PORT)
			ret.append(":"+port);
		return ret.toString();
	}
}
