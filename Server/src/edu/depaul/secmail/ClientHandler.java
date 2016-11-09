package edu.depaul.secmail;

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
	String username = null;
	
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
		
		//do stuff here
		//this is example code copied from java docs
		//basically just an echo server at this point.
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
			username = (String)io.readObject();
			String password = (String)io.readObject();
			
			//authenticate
			if (authenticate(username,password))
				io.writeObject(new PacketHeader(Command.LOGIN_SUCCESS));
			else
			{
				io.writeObject(new PacketHeader(Command.LOGIN_FAIL));
				username = null; // reset the username since it wasn't valid
				//forceably close the connection to this client.
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
		if (username == null)
			return;
		
		//Read Email from input stream
		try {
			EmailStruct newEmail = (EmailStruct)io.readObject();
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
	
	private String getIdentifier()
	{
		return clientSocket.getInetAddress() + ":" + clientSocket.getPort();
	}
}
