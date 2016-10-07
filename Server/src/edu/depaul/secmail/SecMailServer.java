package edu.depaul.secmail;

public class SecMailServer {
	private static Config serverConfig;

	public static void main(String[] args) {
		// read the command line arguments and set up the configuration
		serverConfig = new Config(args);

	}

}
