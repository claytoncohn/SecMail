package edu.depaul.secmail;

import java.net.InetAddress;
import java.net.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
//Import for email pattern matching
import java.util.regex.Pattern;
import java.util.regex.Matcher;


//@SuppressWarnings("unused")
public class Client {
	static int port_number;
	InetAddress loopback;
	Socket client_socket;
	PrintWriter output;
	BufferedReader input;
	
	//David Keller + Geri Toncheva
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
	
	
	//user prompt for pulling notifications and emails
/*	if (buttonForYesNotificationsIsPressedInGUI) 
	{
		case GET_NOTIFICATION:
			System.out.println();
		case SEND_NOTIFICATION:
			System.out.println();
			
	}
	//send command to server for notification to be sent to client from server
	//prompt if user wants to pull emails notified by the notifications sent as well 
	//leads to 	
		if (userSaysYesToPullingEmails) 
		{
			//pull some goddamn emails son
			//emails stored on own server?
		}
	}*/
	
	}
	
	//PSEUDOCODE NEED 
	//pull notification  
	//if (buttonForYesNotificationsIsPressed) {}
	
	//tell server we want notication = 
	//client sends request - it sends a packet header that has the command for enum (took care of it)
	//it's just the command - packetheader object in packetheader.java
	
	//store emails yes, on your own server
	
	//email needs to be complete and closed as a packet
	//server needs to know i will accept email, server is listening specifically for email and not packet
	//give it packet header for that - SEND_EMAIL command
	//send EMAIL OBJECT
	//after email is received communiction from server to client that says email received
	
	//server recieves message that client wants mail messages
	//server sends back notification which is (packet header)
	//multiple packets with notification
	//send notification object, but we gotta come up with a notification object
	//sends another command for get notification
	//now server is sending packts and client is waiting
	//we run out of notifications and we gota tell client that
	//while loop and send notifications until its' done and client will receive notifications
	//another command needed! - end notification



//Geri Toncheva
	//ask user for log in info before
	//then ask for user to pick 1 or 2
	//1 will be for the user to check notifications
	//2 will be for user to write email
	//scratch that we're using the button variables already in place
	
	public void main(String[] args) {
	//email stuff, maybe place in own method?
		
    String testemail ="test@email.com";
    //pattern match
	Pattern pat = Pattern.compile(".+@.+\\.[a-z]+");
	Matcher mat = pat.matcher(testemail);
	boolean found = mat.matches();
	if (found)
	{
		//need to be able to differentiate between username and hostname so u can send notification to right user
		//when u get new email, parse every one of the two fields and there can be multiples
		//for each one of those send a notification to the user that they have new email,,send it to server		
	}
	
	try {
		Socket client = new Socket("localhost", port_number);

        String strClient;
        String strServer;
        String username;
        String password;

	    //in from server
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //out to server
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

        Scanner input = new Scanner(System.in);
        {
        	System.out.println("Now connected to server");
            while(true)
            {
            	//request log in info
            	System.out.println("Please give me your Username: ");
				username = input.nextLine();

				System.out.println("Please give me your Password: ");
            	password = input.nextLine();

                System.out.print("Client: ");
                strClient = input.nextLine();

                out.write(strClient);
                out.newLine();
                out.flush();

                strServer = in.readLine();
                System.out.println("Server: " + strServer);
            }
        }}catch (IOException e) {e.printStackTrace();}
        }}}
	

