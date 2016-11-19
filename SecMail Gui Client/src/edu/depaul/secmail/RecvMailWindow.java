package edu.depaul.secmail;

import java.io.BufferedReader;
import java.io.EOFException;
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
					try {
						OpenOrFetchMail(clickedItem);
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
		btnGetNotifications.setBounds(431, 390, 120, 25);
		btnGetNotifications.setText("Get Notifications");
		createContents();
	}
	
	RecvMailWindow(Display display, DHEncryptionIO serverConnection)
	{
		this(display);
		this.io = serverConnection;
	}
	
	@Override
	public void open()
	{
		super.open();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SecMail");
		setSize(659, 464);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	//Connect to the server and get the new notifications
	private void getNewNotifications()
	{
		PacketHeader getNotification = new PacketHeader(Command.GET_NOTIFICATION);
		try {
			
			io.writeObject(getNotification);
			
			PacketHeader notificationPacket = null;
			notificationPacket = (PacketHeader) io.readObject();
		    if (notificationPacket.getCommand() == Command.END_NOTIFICATION){
		    	noNotificationsMessageBox();
		    }
		    else{
		    	//Leaving this suppressed for now
				//Probably a much safer way to do this
				//But will look into it if there is time
				// - Josh
				
				@SuppressWarnings("unchecked")
				LinkedList<Notification> notifications = (LinkedList<Notification>) io.readObject();
				for(Notification n : notifications){
					if(n.getType() == NotificationType.NEW_EMAIL){
						//first check to make sure the notification isn't already in the table.
						boolean notificationExists = false;
						for (TableItem t : table.getItems())
						{
							if (((Notification)t.getData()).getID().equals(n.getID())) // if this table item has a notification with the same id
								notificationExists = true; // set the flag to true
						}
						if (!notificationExists) // only if the notification didn't already exist
							addNewTableItem(n, false); // new notification, don't have the email yet				
					}
					
				}
							    	
		    }
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	//Yovana
	private void OpenOrFetchMail(TableItem item) throws ClassNotFoundException, IOException
	{
		Notification n = (Notification)item.getData();
		//--------------------------------------------------------------
		//get mail from local system
		
		File f = new File(MainWindow.getMailDir() + n.getID());
		if (f.exists()){
			System.out.println(f);
			//open the mail in the mail reader window here
			EmailStruct email = new EmailStruct(f);
				
			EmailReader reader = new EmailReader(Display.getCurrent(), email, n.getFrom(), n.getDate(), io);
			reader.open();
		}
		//--------------------------------------------
		//get mail from server
		else
		{
			//make packet header to send to server
			PacketHeader getEmailHeader = new PacketHeader(Command.RECEIVE_EMAIL);
			//send packet header to server 
			io.writeObject(getEmailHeader);
			//send ID to server 
			io.writeObject(n.getID());
			//send from user to server for email receipt
			io.writeObject(n.getFrom());
			
			PacketHeader responsePacket = (PacketHeader) io.readObject();
			if(responsePacket.getCommand() == Command.RECEIVE_EMAIL){
				//server sends back the email / packet header
				EmailStruct email = (EmailStruct)io.readObject();
				email.writeToFile(f);
				item.setText(3, "Yes"); // update the table item to show that the email has been downloaded
				
				//open in mail reader
				EmailReader reader = new EmailReader(Display.getCurrent(), email, n.getFrom(), n.getDate(), io);
				reader.open();			
			}
			else{
				noEmailOnServer();
			}
			}
		}
		
		//Josh Clark
		private void noNotificationsMessageBox()
		{
			Shell noNotifications = new Shell();
			MessageBox messageBox = new MessageBox(noNotifications, SWT.OK);
			messageBox.setText("No Notifications");
			messageBox.setMessage("You have no notifications!");		
			messageBox.open();
		}
		
		//Josh Clark
		private void noEmailOnServer()
		{
			Shell noEmail = new Shell();
			MessageBox messageBox = new MessageBox(noEmail, SWT.OK);
			messageBox.setMessage("Email is no longer on server. Sorry!");		
			messageBox.open();
		}
}

