package edu.depaul.secmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.text.DateFormat;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

public class RecvMailWindow extends Shell {
	private Table table;
	private DHEncryptionIO io;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			RecvMailWindow shell = new RecvMailWindow(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 * @wbp.parser.constructor
	 */	
	public RecvMailWindow(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Point pt = new Point(e.x, e.y);
				TableItem clickedItem = table.getItem(pt);
				if (clickedItem != null)
					OpenOrFetchMail((Notification)clickedItem.getData());
			}
		});
		table.setBounds(10, 31, 623, 325);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("From");
		
		TableColumn tblclmnSubject = new TableColumn(table, SWT.NONE);
		tblclmnSubject.setWidth(373);
		tblclmnSubject.setText("Subject");
		
		TableColumn tblclmnDate = new TableColumn(table, SWT.NONE);
		tblclmnDate.setWidth(82);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnRecieved = new TableColumn(table, SWT.NONE);
		tblclmnRecieved.setWidth(63);
		tblclmnRecieved.setText("Recieved");
		
		Button btnClose = new Button(this, SWT.NONE);
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				dumpNotificationsToFile();
				RecvMailWindow.this.close();
			}
		});
		btnClose.setBounds(558, 390, 75, 25);
		btnClose.setText("Close");
		
		Button btnGetNotifications = new Button(this, SWT.NONE);
		btnGetNotifications.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				getNewNotifications();
			}
		});
		btnGetNotifications.setBounds(451, 390, 101, 25);
		btnGetNotifications.setText("Get Notifications");
		createContents();
	}
	
	RecvMailWindow(Display display, DHEncryptionIO serverConnection)
	{
		this(display);
		this.io = serverConnection;
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SecMail");
		setSize(659, 464);
		
		//TODO: remove for production
		testLoadColumnItems();

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	//Connect to the server and get the new notifications
	private void getNewNotifications()
	{
		/*
		//TODO:
		//for each new notification we get, 
		Notification n = (Notification)i.getData();
		For (Notification n :)
		//store mail  
		String mailDir = MainWindow.getMailDir();
		//	check to see if an email matching the notifications ID already exists in the maildir
        if (text.ExistsIn(mailDir))
        {
    		//	if yes, mark received column as true, else false.
    		TableColumn tblclm = new TableColumn();
    		tblclmnFileName.setBoolean(TRUE);
        }
		
		//add new table item for the notification
		addNewTableItem(n, true);
		//write the notification to disk as well */
		return;
	}
	
	//Reads the notifications file from the filesystem and loads all the old notifications that we've already gotten.
	private void loadNotificationsFromFile()
	{
		//open the notification file
		File notificationfile = new File("notifications.bin");
		ObjectInputStream reader = null;
		//read the file
		try 
		{
		    reader = new ObjectInputStream(new FileInputStream(notificationfile));
		    Notification n = null; 

		    while ((n = (Notification)reader.readObject()) != null) {
		        //for each notification read,
				//	check to see if an email matching the notifications ID already exists in the maildir
		        File email = new File(MainWindow.getMailDir() + n.getID());
		        addNewTableItem(n, email.exists());
		    }
		        
		} catch (FileNotFoundException e) {
			// if the file isn't found, that's fine, there just aren't notifications yet.
		} catch (IOException e) {
			System.out.println("IOExceoption trying to read notifications file.");
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println("Error while reading notifications file.");
			System.out.println(e);
		}
        try {
        	if (reader != null) 
        		reader.close();
        } catch (Exception e){
        	System.out.println("Exception while tring to close notification file");
        	System.out.println(e);
        }
	}
	
	//put the notifications in the notifications list into the notifications file so they are saved.
	private void dumpNotificationsToFile()
	{
		try {
			FileOutputStream f = new FileOutputStream("notifications.bin", false);
			ObjectOutputStream notiwriter = new ObjectOutputStream(f);
			//for each item in the table
			for (TableItem i : table.getItems())
			{
				Notification n = (Notification)i.getData(); // get the original notification used
				
				//TODO: write the notifications to a file
				notiwriter.writeObject(n);
			}
			notiwriter.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error, unable to open File to write notifications");
			System.err.println(e);
		} catch (IOException e) {
			System.out.println("Error whilewriting to file");
			System.out.println(e);
		}
	}
	
	//creates dummy items and loads them into the table. Just for testing purposes.
	private void testLoadColumnItems()
	{
		//Create a test notification
		Notification test = new Notification(
					new UserStruct("Jacob.burkamper@gmail.com"), 
					new UserStruct("my.dad@hisdomain.com"), 
					NotificationType.NEW_EMAIL, 
					"azy123", 
					"Testing table stuffs", 
					new Date()
				);
		
		//add it to the table
		addNewTableItem(test, true);
	}
	
	private void addNewTableItem(Notification n, boolean isOnDisk)
	{
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(n); // store the notification for future use
		item.setText(0, n.getFrom().compile()); // the from field
		item.setText(1, n.getSubject()); // the subject
		item.setText(2, DateFormat.getDateTimeInstance().format(n.getDate())); // the date, as a string
		
		//display if we have already gotten this field
		if (isOnDisk)
			item.setText(3, "Yes");
		else
			item.setText(3, "No");
	}
	
	//Opens the email associated with the notification n
	//will possibly fetch that email from the remote server if necessary
	private void OpenOrFetchMail(Notification n)
	{
		//TODO:
		//check if the mail is already on the local system
		//if yes, open it.
		
		//if the mail isn't on the system, prompt to fetch
		//if the user says yes, fetch the mail
		//otherwise cancel.
		
		//open the mail in the mail reader window here
		//EmailReader reader = new EmailReader(Display.getCurrent(), email, n.getFrom(), n.getDate());
		//reader.open()
		
		
		//test code, remove
		MessageBox testBox = new MessageBox(this);
		testBox.setText("Test Open Email MessageBox");
		testBox.setMessage("You tried to open the email from notification with id: "+n.getID());
		testBox.open();
	}
	private void HandleSendNotification(Notification n)
	{
		//needs accept a notification from which had been sent to it from the other server, needs to go on server side
		
		return;
	}
}
