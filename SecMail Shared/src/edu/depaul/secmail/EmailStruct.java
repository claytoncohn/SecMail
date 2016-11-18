package edu.depaul.secmail;

import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.SecureRandom;
import java.math.BigInteger;

public class EmailStruct implements Serializable{
	private LinkedList<UserStruct> recipients = new LinkedList<UserStruct>();
	private transient LinkedList<File> attachments = new LinkedList<File>();
	private String subject = null;
	private String body = null;
	private String id = null;
	private byte[] encryptedBytes = null;
	private boolean encrypted = false;
	
	//default empty constructor.
	EmailStruct()
	{
		
	}
	
	//Constructor for reading an email from a file.
	EmailStruct(File f)
	{		
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedReader input = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = input.readLine()) != null)
			{
				if (line.startsWith("to:"))
				{
					//add a recipient
					String[] split = line.split(":");
					if (split.length > 2)
						 fileFormatError(line);
					else
						recipients.add(new UserStruct(split[1].trim()));
				}
				else if (line.startsWith("attachment:"))
				{
					//add attachment
					String[] split = line.split(":");
					if (split.length > 2)
						fileFormatError(line);
					else
					{
						File attachment = new File(split[1].trim());
						attachments.add(attachment);
					}
				}
				else if (line.startsWith("subject:"))
				{
					//add the subject
					String[] split = line.split(":");
					if (split.length > 2)
						fileFormatError(line);
					else
					{
						this.subject = split[1].trim();
					}
				}
				else if (line.startsWith("body:"))
				{
					//add the body
					StringBuffer buffer = new StringBuffer();
					//consume the rest of the file
					while ((line = input.readLine()) != null)
						buffer.append(line);
					body = buffer.toString();
				}
				else if (line.startsWith("Encrypted:"))
				{
					encrypted = true;
					
					//consume the rest of the file
					byte[] b = new byte[(int)(fis.getChannel().size() - fis.getChannel().position())];
					fis.read(b);
					encryptedBytes = b;
				}
				else
				{
					fileFormatError(line);
				}
			}
			input.close();
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	//reads the email from file f, and sets the id to ID
	EmailStruct(File f, String ID)
	{
		this(f);
		this.id = ID;
	}	
	
	//helper function. Simply writes an error message to stdout.
	private void fileFormatError(String line)
	{
		System.out.println("Format error reading email from file. offending line:");
		System.out.println(line);
	}
	
	//add the user denoted by the string to the recipients list
	//creates the appropriate UserStruct from the incoming string first.
	public void addRecipient(String to)
	{
		recipients.add(new UserStruct(to));
	}
	
	//Add the File attachment to the list of attachments for this email
	public void addAttachment(File attachment)
	{
		attachments.add(attachment);
	}
	
	//set the subject of the email
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	
	//set the body of the email
	public void setBody(String body)
	{
		this.body = body;
	}
	
	//Generates a single string with comma separated entries for each user in the recipients list
	public String getToString()
	{
		StringBuffer buffer = new StringBuffer();
		for (UserStruct recipient : recipients)
			buffer.append(recipient.compile() + ",");
		buffer.setLength(buffer.length() - 1); // delete the last character
		return buffer.toString();
	}
	
	//returns the list of attachments for this email
	public LinkedList<File> getAttachmentList()
	{
		return attachments;
	}
	
	//returns the subject of the email
	public String getSubject()
	{
		return subject;
	}
	
	//returns the body of the email
	public String getBody()
	{
		return body;
	}
	
	//writes the contents of this email message to a file f
	//returns true if successful, returns false otherwise
	public boolean writeToFile(File f)
	{
		try {
			FileOutputStream fos = new FileOutputStream(f);
			PrintWriter out = new PrintWriter(new PrintStream(fos));
			//write the recipients
			for (UserStruct recipient : recipients)
			{
				out.print("to: ");
				out.println(recipient.compile());
			}
			
			//write the attachments
			if (attachments != null)
				for (File attachment : attachments)
				{
					out.print("attachment: ");
					out.println(attachment.getAbsolutePath());
				}
			
			//write the subject
			out.print("subject: ");
			out.println(subject);
			
			//the rest of the email is the body.
			if (encrypted)
			{
				out.println("Encrypted:");
				fos.write(encryptedBytes);
			}
			else
			{	
				out.println("body: ");
				out.print(body);
			}
				
			
			out.close();
			return true;
		} catch (Exception e)
		{
			System.out.println(e);
			return false;
		}
			
	}
	
	//get the unique ID for this email. Generates the ID if the email doesn't already have one.
	public String getID()
	{
		if (this.id == null)
		{
			//generate a random id string
			this.id = new BigInteger(130, new SecureRandom()).toString(32);
		}
		
		return id;
	}
	
	//return the entire list of recipients
	public LinkedList<UserStruct> getToList()
	{
		return recipients;
	}
	
	//returns a list of notifications of type NEW_EMAIL appropriate for this email
	public LinkedList<Notification> getNotificationList(UserStruct fromUser)
	{
		LinkedList<Notification> ret = new LinkedList<Notification>();
		for (UserStruct recipient : recipients)
		{
			ret.add(new Notification(recipient, fromUser, NotificationType.NEW_EMAIL, this));
		}
		return ret;
	}
	
	//encrypts the body of this EmailStruct
	public void encrypt(String key)
	{
		if (body != null && !encrypted)
		{
			encryptedBytes = SecMailStaticEncryption.SecMailEncryptAES(body, key.getBytes()); // encrypt the body using the key
			body = null; // erase the body
			encrypted = true; // mark that we're encrypted
		}		
	}
	
	//decrypts an already-encrypted body
	public void decrypt(String key)
	{
		if (encrypted)
		{
			body = SecMailStaticEncryption.SecMailDecryptAES(encryptedBytes, key.getBytes());
			encryptedBytes = null; // delete the encrypted portion
			encrypted = false; // set that we're no longer encrypted.
		}
	}

}
