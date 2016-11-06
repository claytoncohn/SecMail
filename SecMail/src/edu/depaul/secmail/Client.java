package edu.depaul.secmail;

import java.net.InetAddress;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

//@SuppressWarnings("unused")
public class Client {
	int port_number;
	InetAddress loopback;
	Socket client_socket;
	PrintWriter output;
	BufferedReader input;

//Geri Toncheva + David Keller
	public Client(){
		//ask user for log in info before
		//then ask for user to pick 1 or 2
		//1 will be for the user to check notifications
		//2 will be for user to write email
		//scratch that we're using the button variables already in place
		
		public class Login {
		//FileNotFoundException just temporary since we have no login file
		public void run() throws FileNotFoundException{
		Scanner scan = new Scanner (new File(""));
		Scanner keyboard = new Scanner (System.in);
		String username = scan.nextLine();
		String password = scan.nextLine();

		//Above checks the selected file

		String userInput = keyboard.nextLine();
		String passInput = keyboard.nextLine();

		// gets input from user

		if (userInput.equals(username) && passInput.equals(password)){System.out.print("Login Succesful!");}
		else{System.out.print("Error, username or password unrecognized");}
		
		//pull notification 
		//tel server we want notication = 
		//client sends request - it sends a packet header that has the command for enum (took care of it)
		//it's just the command - packetheader object in packetheader.java
		
		
		//server recieves message that client wants mail messages
		//server sends back notifications (packet header)
		//multiple packets with notification
	
		
		//pull mail
		
		}
		}
		
		
	
	}
}
