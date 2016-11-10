package edu.depaul.secmail;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class ClientHandler implements Runnable{
	private Socket clientSocket = null;
	private DHEncryptionIO io = null;
	UserStruct user = null;
	
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
		        if (nextPacket.getCommand() == Command.CLOSE)
		        	break; // leave the loop
		        else
		        	processPacket(nextPacket);
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
				handleEmail();
				break;
			case ERROR:
				handleError();
				break;
			case GET_NOTIFICATION:
				handleGetNotification();
				break;
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
				io.writeObject(new PacketHeader(Command.LOGIN_FAIL));
				//forceably close the connection to this client.
				//TODO: Make this more graceful?
				io.close();
				clientSocket.close();
			}
		} catch (IOException e)
		{
			System.out.println("Got an IOException while logging in."); //TODO should handle this with Log calls
			System.out.println(e);
		} catch (ClassNotFoundException e)
		{
			System.out.println("SecMail Protocol Error. ClassNotFoundException");
			System.out.println(e);
		}
	}
	
	//authenticate the user vs the password store
	private boolean authenticate(String user, String password)
	{
		Log.Out("Got authentication request for user: "+user+","+getIdentifier());
		//remove this and implement logic.
		return false;
	}
	
	private void handleEmail(){
		//die early if the user hasn't authenticated.
		if (user == null)
			return;
		
		//Read Email from input stream
		try {
			EmailStruct newEmail = (EmailStruct)io.readObject();
			LinkedList<Notification> newNotificationList = newEmail.getNotificationList(user);
			//spawn a new thread to handle sending out the notifications here
			//(new Thread(new ServerCommunicator(newNotificationList));
			storeEmail(newEmail);
			
			//debug code, delete for release
			System.out.println("Recipient: " + newEmail.getToString());
			System.out.println("Subject: " + newEmail.getSubject());
			System.out.println("Body: " + newEmail.getBody());
			PacketHeader successfulTestPacket = new PacketHeader();
			successfulTestPacket.setCommand(Command.CONNECT_SUCCESS);
			io.writeObject(successfulTestPacket);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
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
			this.io.writeObject(notifications);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//send all the notifications in the above linked list to the client.
	}
	
	private String getIdentifier()
	{
		return clientSocket.getInetAddress() + ":" + clientSocket.getPort();
	}
	
	private void sendNotificationToServer(UserStruct to, String subject, String id)
	{
		//TODO:
		//Implement code to send a notification to the server of the user "to" here. (including if that is this server!)
		return;
	}
	
	private void storeEmail(EmailStruct email)
	{
		//TODO:
		//Implement code to write the email to somewhere applicable
		//NOTE: see email.writeToFile, file name should be email.getID(), stored in a directory that identifies the user (this.username)
		return;
	}
}
