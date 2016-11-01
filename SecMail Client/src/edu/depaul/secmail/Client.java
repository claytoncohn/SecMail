package edu.depaul.secmail;

import java.net.InetAddress;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
//Import for email pattern matching
import java.util.regex.Pattern;
import java.util.regex.Matcher;


//@SuppressWarnings("unused")
public class Client {
	int port_number;
	InetAddress loopback;
	Socket client_socket;
	PrintWriter output;
	BufferedReader input;

//Geri Toncheva
	public Client(){
			public static void main(String[] args) {

            //email stuff, maybe place in own class?
            String testemail ="test@email.com"
            //pattern match
			Pattern pat = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher mat = pat.matcher(testemail);
			boolean found = mat.matches();
			if (found)
			{
			    //add to file of approved emails?
			    //need more stuff here
			}

        try (Socket client = new Socket("localhost", port_number);
			//in from server
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //out to server
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            Scanner input = new Scanner(System.in))
            {
            System.out.println("Now connected to server");
            String strClient;
            String strServer;

            while(true)
            {
                System.out.print("Client: ");
                strClient = input.nextLine();

                out.write(strClient);
                out.newLine();
                out.flush();

                strServer = in.readLine();
                System.out.println("Server: " + strServer);
            }
            } catch (IOException e) {e.printStackTrace();}
	}

	}

}
