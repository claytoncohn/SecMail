package edu.depaul.secmail;

public class Config {
	private int port = 0;
	private String configFilePath = null;
	
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
				default:
					Log.Error("Unknown command line option: " + args[i]);
				}
			}
		}
		Log.Debug("finished constructing Config object");
	}
	
	private void LoadConfigFile(String path)
	{
		Log.Out("Trying to load config file from \"" + path + "\"");
	}
}
