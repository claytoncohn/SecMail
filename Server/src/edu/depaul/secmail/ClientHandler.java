package edu.depaul.secmail;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class ClientHandler implements Runnable{
	private Socket clientSocket = null;
	private DHEncryptionIO io = null;
	UserStruct user = null;
	Config config = null;
	
	ClientHandler(Socket s)
	{
		this.clientSocket = s;
		try {
			io = new DHEncryptionIO(clientSocket, true);
		} catch (IOException e) {
			//TODO: handle this. For now, just output error and abort
			System.err.println(e);
			Log.Error("Exception creating ClientHandler: ");
			Log.Error(e.toString());
			System.exit(10);
		}
		
		
	}
	
	public void run()
	{
		Log.Debug("Starting ClientHandler");
	
		try {
			PacketHeader nextPacket = null;
			while ((nextPacket = (PacketHeader)io.readObject()) != null) {		
		        if (nextPacket.getCommand() == Command.CLOSE){  
		        	break; // leave the loop
		        }  
		        else{
		        	processPacket(nextPacket);
		        }
		        	
		    }
		} catch (IOException e) {
			Log.Error("Error while trying to read or write to socket");
			Log.Error(e.toString());
		} catch (ClassNotFoundException e)
		{
			Log.Error("Error while trying to get object from network. Class not found");
			Log.Error(e.toString());
		}
		
		//we're done. close the stuff
		try {
			Log.Debug("Closing connection to client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
			io.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println(e);
			Log.Error(e.toString());
		}
	}
	
	private void processPacket(PacketHeader ph)
	{
		Log.Debug("Processing packet for command " + ph.getCommand());
	
		switch(ph.getCommand()){
			case CONNECT_TEST:
				handleTestConnection();
				break;
			case LOGIN:
				handleLogin();
				break;
			case SEND_EMAIL:
				Log.Debug("Start Email Handler");
				System.out.println(user.getUser());
				handleEmail();
				break;
			case ERROR:
				handleError();
				break;
			case GET_NOTIFICATION:
				handleGetNotification();
				break;
			case SEND_NOTIFICATION:
				handleNewNotificationSent();
				break;
			case RECEIVE_EMAIL:
				retrieveEmail(getNotificationID());
		default:
			break;
		}
	
	}
	
	private void handleTestConnection()
	{
		//create successful connection packet
		PacketHeader successfulTestPacket = new PacketHeader();
		successfulTestPacket.setCommand(Command.CONNECT_SUCCESS);
		
		try {
			io.writeObject(successfulTestPacket);
		} catch (Exception e)
		{
			Log.Error("Exception thrown." + e);
		}
	}
	
	private void handleLogin(){
		try {
			String username = (String)io.readObject();
			String password = (String)io.readObject();
			
			//authenticate
			if (authenticate(username,password))
			{
				io.writeObject(new PacketHeader(Command.LOGIN_SUCCESS));
				user = new UserStruct(username, SecMailServer.getGlobalConfig().getDomain(), SecMailServer.getGlobalConfig().getPort());
			}
			else
			{
				// Let the client know that the login was unauthenticated
				// Remain open and wait for next login packet
				io.writeObject(new PacketHeader(Command.LOGIN_FAIL));
			}
		} catch (IOException e)
		{
			Log.Error("IO Exception while trying to handle login");
			Log.Error(e.toString());
			
		} catch (ClassNotFoundException e)
		{
			Log.Error("Error while trying to get object from network. Class not found");
			Log.Error(e.toString());
		}
	}
	
	//authenticate the user vs the password store
	private boolean authenticate(String user, String password)
	{
		Log.Out("Got authentication request for user: "+user+","+getIdentifier());
		//remove this and implement logic.
		return true;
	}
	
	// Josh Clark
	private void handleEmail(){
		//die early if the user hasn't authenticated.
		if (user == null)
			return;
		
		//Read Email from input stream
		try {
			EmailStruct newEmail = (EmailStruct)io.readObject();
			LinkedList<Notification> newNotificationList = newEmail.getNotificationList(user);
			
			//spawn a new thread to handle sending out the notifications here
			(new Thread(new NotificationSender(newNotificationList))).start();
			storeEmail(newEmail);
			
			//debug code, delete for release
			Log.Debug("Recipient: " + newEmail.getToString());
			Log.Debug("Subject: " + newEmail.getSubject());
			Log.Debug("Body: " + newEmail.getBody());
			
			PacketHeader successfulEmailPacket = new PacketHeader();
			successfulEmailPacket.setCommand(Command.CONNECT_SUCCESS);
			io.writeObject(successfulEmailPacket);
		
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleError(){
		// Handle Error packet here
	}
	
	private void handleGetNotification()
	{
		//TODO: test this
		LinkedList<Notification> notifications = SecMailServer.getNotificationList(this.user.compile());
	
		
		try {

			if(notifications.isEmpty()){
				PacketHeader noNotifications = new PacketHeader(Command.END_NOTIFICATION);
				this.io.writeObject(noNotifications);
			}
			else{
				this.io.writeObject(new PacketHeader(Command.NO_NOTIFICATIONS));
				this.io.writeObject(notifications);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//send all the notifications in the above linked list to the client.
	}
	
	private String getIdentifier()
	{
		return clientSocket.getInetAddress() + ":" + clientSocket.getPort();
	}
	
	private void storeEmail(EmailStruct email) throws IOException
	{
		//TODO:
		//Implement code to write the email to somewhere applicable
		//NOTE: see email.writeToFile, file name should be email.getID(), stored in a directory that identifies the user (this.username)
		
		String root = SecMailServer.getGlobalConfig().getMailRoot();
		String directoryName = this.user.getUser();
		String filename = email.getID();
	
		File directory = new File(String.valueOf(root)+"/"+String.valueOf(directoryName));
		if (! directory.exists()){
			directory.mkdir();
		}
		File writeTo = new File(directory + "/" + filename + ".txt");
		email.writeToFile(writeTo);
		Log.Debug("Wrote new email file: "+writeTo.getAbsolutePath());
		return;
	}
	
	//Jacob Burkamper
	private void handleNewNotificationSent() {
		try {
			Notification recievedNotification = (Notification)io.readObject();
			SecMailServer.addNotificationToList(recievedNotification);
		} catch (ClassNotFoundException e) {
			Log.Error("ClassNotFoundException thrown while trying to read a notification from another server");
			Log.Error(e.toString());
		} catch (IOException e) {
			Log.Error("IOException thrown while reading notification from remote server");
			Log.Error(e.toString());
		}
	}
	 
	// Josh Clark
	private void retrieveEmail(String id)
	{
		// Check if user is null
		if(user == null){	
			return;
		}
		
		// Find the file and write it back to the stream
		else{
			
			// Get email directory
			String root = SecMailServer.getGlobalConfig().getMailRoot();
			String directoryName = this.user.getUser();
			
			// Get the file
			File file = new File(String.valueOf(root)+String.valueOf(directoryName)+ "/" + id + ".txt");
			System.out.println(file.getAbsolutePath());
			
			// Write the email to the stream
			if(file.exists()){
				EmailStruct email = new EmailStruct(file);
				try {
					io.writeObject(email);
					Log.Debug("Email written to stream");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	// Josh Clark
	private String getNotificationID(){
		
		// read in the id sent over from client
		try {
			String id = (String)io.readObject();
			return id;
		} catch (ClassNotFoundException e) {
			System.err.println(e);
			Log.Error(e.toString());
		} catch (IOException e) {
			System.err.println(e);
			Log.Error(e.toString());
		}
		return null;	
	}
	
}
