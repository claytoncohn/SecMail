package edu.depaul.secmail;

import java.util.concurrent.*;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ClientHandler implements Runnable{
	private Socket clientSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	ClientHandler(Socket s)
	{
		this.clientSocket = s;
		try {
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream())
					);
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
			String inputLine, outputLine;
			while ((inputLine = in.readLine()) != null) {
		        outputLine = inputLine;
		        out.println(outputLine);
		        if (outputLine.equals("Bye."))
		            break;
		    }
		} catch (IOException e) {
			Log.Error("Error while trying to read or write to socket");
			Log.Error(e.toString());
		}
		
		//we're done. close the stuff
		try {
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println(e);
			Log.Error(e.toString());
		}
	}
}
