package edu.depaul.secmail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecMail {
	public static void main(String[] args) {
//Geri Toncheva		
		Client client = new Client();
		
		//do log in
		//ask for username
		//ask for password
		

        //email stuff, maybe place in own class?
        String testemail ="test@email.com";
        //pattern match
		Pattern pat = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher mat = pat.matcher(testemail);
		boolean found = mat.matches();
		if (found) {
		    //add to file of approved emails?
		    //need more stuff here
		}

		try (Socket clientSocket = new Socket("localhost", client.port_number);
			//in from server
	        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        //out to server
	        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	
	        Scanner input = new Scanner(System.in)) {
	        System.out.println("Now connected to server");
	        String strClient;
	        String strServer;
	
	        while(true) {
	            System.out.print("Client: ");
	            strClient = input.nextLine();
	
	            out.write(strClient);
	            out.newLine();
	            out.flush();
	
	            strServer = in.readLine();
	            System.out.println("Server: " + strServer);
	        }
        } catch (IOException e) {e.printStackTrace();
        
        }
		
		
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
