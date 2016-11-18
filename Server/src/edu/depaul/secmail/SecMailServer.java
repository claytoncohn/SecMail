package edu.depaul.secmail;

import java.net.*;
import java.util.LinkedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SecMailServer {
	private static Config serverConfig;
	public static LinkedList<Notification> notifications;
	private static ObjectOutputStream notificationWriter = null;

	public static void main(String[] args) {
		// read the command line arguments and set up the configuration
		serverConfig = new Config(args);
		Log.Init(serverConfig.getLogFile());
		
		Log.Out("Set up Server config and log file.");
		
		Log.Out("Reading notification list from file system");
		loadNotifications();
		
		Log.Out("Using Mail Directory: "+serverConfig.getMailRoot());
		checkAndCreateMailDir();
		
		Log.Out("Binding to port " + serverConfig.getPort() +" backlog " + serverConfig.getBacklog());
		try (
			ServerSocket serverSocket = new ServerSocket(serverConfig.getPort(), serverConfig.getBacklog());
		){
			while (true)
			{
				Socket clientSocket = serverSocket.accept();
				Log.Debug("Connected to client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				
				//process the client
				(new Thread(new ClientHandler(clientSocket))).start();
			}
		}catch (IOException e) {
			//This will need to be handled better later. For now, just dump the error and crash.
			System.err.println(e);
			System.exit(1); 
		}

	}
	
	//Jacob Burkamper
	private static void loadNotifications()
	{
		notifications = new LinkedList<Notification>(); // instantiate empty list
		File notificationFile = new File("Notifications.bin"); // TODO: make this path part of Config
		if (notificationFile.exists()) // check for the notifications file
		{
			try {
				ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(notificationFile));
				Notification n = null;
				try {
					//load any notifications in the file into the list
					while ((n = (Notification)inStream.readObject()) != null)
						notifications.add(n);
				} catch (ClassNotFoundException e) {
					//errors in case notifications don't load.
					Log.Error("ClassNotFound Exception while reading notifications file");
					Log.Error("Were the notifications saved by a different version of the server?");
					Log.Error("Error was: " + e.toString());
				}
			} catch (IOException e) {
				//generic IO errors.
				Log.Error("IO Error while reading notifications file");
				Log.Error(e.toString());
			}
			
		}
		//regardless of whether the file exists or not, open a writer.
		//this will be used to save notifications later.
		try {
			notificationWriter = new ObjectOutputStream(new FileOutputStream(notificationFile));
		} catch (IOException e) {
			Log.Error("IO Exception while trying to create notificationWriter");
			Log.Error(e.toString());
		}
		
	}
	
	//save the notifications list to a file
	//TODO: make the location of the notifications file part of Config.
	//Jacob Burkamper
	private static void saveNotification(Notification n) 
	{
		
	}
	
	public static synchronized LinkedList<Notification> getNotificationList(String username)
	{
		//TODO: test this
		
		//search the notification list and return a new linked list containing notifications for the user only
		//don't forget to make this thread safe! Apparently this done
		//for now, just return an empty list.
		LinkedList<Notification> ret = new LinkedList<Notification>();
		for (Notification n : notifications){
			if (n.getTo().compile().equals(username)){
				ret.add(n);
			}
		}
		
		return ret;
	}
	
	public static synchronized void addNotificationToList(Notification n)
	{
		//TODO: test this
		
		//add the notification to the list.
		//don't forget to synchronize! (thread safety!)
		
		notifications.add(n);
		saveNotification(n); // save the notification to disk
		return;
	}
	
	//returns the Config object being used by the server
	public static Config getGlobalConfig()
	{
		return serverConfig;
	}
	
	private static void checkAndCreateMailDir()
	{
		File mailDir = new File(serverConfig.getMailRoot());
		if (!mailDir.exists())
		{
			Log.Out("Creating Mail directory" + mailDir.getAbsolutePath());
			if (!mailDir.mkdir()) // if the dir doesn't get made
			{
				Log.Error("Couldn't create mail directory. Unable to continue");
				System.exit(10);
			}
		}
	}

}
