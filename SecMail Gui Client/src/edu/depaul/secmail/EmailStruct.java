package edu.depaul.secmail;

import java.util.LinkedList;
import java.io.File;

public class EmailStruct {
	private LinkedList<String> recipients = new LinkedList<String>();
	private LinkedList<File> attachments = new LinkedList<File>();
	private String subject;
	private String body;
	
	//Constructor for reading an email from a file.
	EmailStruct(File f)
	{
		
	}
	public void addRecipient(String to)
	{
		recipients.add(to);
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
		for (String recipient : recipients)
			buffer.append(recipient + ",");
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
		return false;
	}

}
