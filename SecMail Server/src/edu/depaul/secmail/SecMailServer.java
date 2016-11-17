package edu.depaul.secmail;

import java.net.*;
import java.util.Date;
import java.util.LinkedList;
import java.io.IOException;

public class SecMailServer {
	private static Config serverConfig;
	public static LinkedList<Notification> notifications;

	public static void main(String[] args) {
		// read the command line arguments and set up the configuration
		serverConfig = new Config(args);
		Log.Init(serverConfig.getLogFile());
		
		Log.Out("Set up Server config and log file.");
		
		Log.Out("Reading notification list from file system");
		loadNotifications();
		
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
	
	
	// Josh and somebody else (Jacob?)
	public static void loadNotifications()
	{
		//TODO:
		//Implement code to read the notifications from last shutdown here.
		//for now, just instantiate a blank list
		
		//Creating a test notification to see if 
		// notification will make it to client
		Notification test = new Notification(
					new UserStruct("josh@gmail.com"), 
					new UserStruct("test@domain.com"), 
					NotificationType.EMAIL_RECEIVED, 
					"123", 
					"idk", 
					new Date()
				);
		notifications = new LinkedList<Notification>();
		notifications.add(test);
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
		
		//Josh - Doing some testing
		//Return this to return GLOBAL notifications list
		return notifications;
		
		// Once implemented return this to return
		// user specific notifications...need some authentication here
		//return ret;
	}
	
	public static synchronized void addNotificationToList(Notification n)
	{
		//TODO: test this
		
		//add the notification to the list.
		//don't forget to synchronize! (thread safety!)
		
		notifications.add(n);
		
		return;
	}
	
	//returns the Config object being used by the server
	public static Config getGlobalConfig()
	{
		return serverConfig;
	}

}
