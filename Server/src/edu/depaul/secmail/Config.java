package edu.depaul.secmail;

import java.io.File;
import java.io.IOException;

public class Config {
	private int port = 0;
	private String configFilePath = null;
	private File configFile = null;
	private String logFilePath = null;
	private File logFile = null;
	
	Config(String[] args) 
	{
		//iterate through each of the arguments
		for (int i = 0; i < args.length; i++)
		{
			//only read arguments with the - switch
			if (args[i].startsWith("-"))
			{
				switch (args[i].substring(1))
				{
				case "c":
				case "-configfile":
					this.LoadConfigFile(args[i+1]);
					break;
				case "l":
				case "-logfile":
					this.SetLogFile(args[i+1]);
				default:
					System.err.println("Unknown command line option: " + args[i]);
				}
			}
		}
		Log.Debug("finished constructing Config object");
	}
	
	public File getLogFile()
	{
		return logFile;
	}
	
	private void LoadConfigFile(String path)
	{
		Log.Out("Loading config file from \"" + path + "\"");
		configFile = new File(path);
	}
	
	private void SetLogFile(String newLogFilePath)
	{
		Log.Debug("Setting Log File path to + \"" + newLogFilePath + "\"");
		logFilePath = newLogFilePath;
		
		//get the file handle
		File file = new File(logFilePath);
	}
}
