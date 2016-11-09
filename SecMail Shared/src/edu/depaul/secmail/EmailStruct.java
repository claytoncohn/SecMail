package edu.depaul.secmail;

import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	EmailStruct()
	{
		
	}
	
	//Constructor for reading an email from a file.
	EmailStruct(File f)
	{		
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
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
	
	EmailStruct(File f, String ID)
	{
		this(f);
		this.id = ID;
	}
	
	
	
	private void fileFormatError(String line)
	{
		System.out.println("Format error reading email from file. offending line:");
		System.out.println(line);
	}
	
	public void addRecipient(String to)
	{
		recipients.add(new UserStruct(to));
	}
	
	public void addAttachment(File attachment)
	{
		attachments.add(attachment);
	}
	
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	
	public void setBody(String body)
	{
		this.body = body;
	}
	
	public String getToString()
	{
		StringBuffer buffer = new StringBuffer();
		for (UserStruct recipient : recipients)
			buffer.append(recipient.compile() + ",");
		buffer.setLength(buffer.length() - 1); // delete the last character
		return buffer.toString();
	}
	
	public LinkedList<File> getAttachmentList()
	{
		return attachments;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public String getBody()
	{
		return body;
	}
	
	//writes the contents of this email message to a file f
	//returns true if successful, returns false otherwise
	public boolean writeToFile(File f)
	{
		try {
			PrintWriter out = new PrintWriter(f);
			//write the recipients
			for (UserStruct recipient : recipients)
			{
				out.print("to: ");
				out.println(recipient.compile());
			}
			
			//write the attachments
			for (File attachment : attachments)
			{
				out.print("attachment: ");
				out.println(attachment.getAbsolutePath());
			}
			
			//write the subject
			out.print("subject: ");
			out.println(subject);
			
			//the rest of the email is the body.
			out.println("body: ");
			out.print(body);
			
			out.close();
			return true;
		} catch (Exception e)
		{
			System.out.println(e);
			return false;
		}
			
	}
	
	public String getID()
	{
		if (this.id == null)
		{
			//generate a random id string
			this.id = new BigInteger(130, new SecureRandom()).toString(32);
		}
		
		return id;
	}
	
	public LinkedList<UserStruct> getToList()
	{
		return recipients;
	}

}
