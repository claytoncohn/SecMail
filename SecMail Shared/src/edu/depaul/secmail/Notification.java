package edu.depaul.secmail;

import java.io.Serializable;

public class Notification implements Serializable{
	private UserStruct from; // the user who sent the notification
	private UserStruct to; // the user for whom the notification is destined to reach
	private NotificationType type; // the type of notification
	private String id; // the id of the email that the notification refers to
	
	public UserStruct getTo()
	{
		return this.to;
	}
	
	public UserStruct getFrom()
	{
		return this.from;
	}
	
	public NotificationType getType()
	{
		return this.type;
	}
	
	public String getID()
	{
		return this.id;
	}
}
