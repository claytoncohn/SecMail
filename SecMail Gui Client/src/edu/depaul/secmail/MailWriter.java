package edu.depaul.secmail;

import java.io.File;
import java.net.Socket;

import javax.swing.JFileChooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MailWriter extends Shell {
	private Text toText;
	private Text subjectText;
	private Text bodyText;
	EmailStruct email;
	DHEncryptionIO io;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			MailWriter shell = new MailWriter(display);
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
	public MailWriter(Display display) {
		super(display, SWT.SHELL_TRIM);
		createContents();
		this.setLayout(new FormLayout());
		
		Composite composite = new Composite(this, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0);
		fd_composite.right = new FormAttachment(100, -16);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblTo = new Label(composite, SWT.NONE);
		GridData gd_lblTo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblTo.widthHint = 38;
		lblTo.setLayoutData(gd_lblTo);
		lblTo.setText("To:");
		
		toText = new Text(composite, SWT.BORDER);
		toText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSubject = new Label(composite, SWT.NONE);
		lblSubject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubject.setText("Subject:");
		
		subjectText = new Text(composite, SWT.BORDER);
		subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		fd_composite.bottom = new FormAttachment(composite_1, 0);
		composite_1.setLayout(new FormLayout());
		FormData fd_composite_1 = new FormData();
		fd_composite_1.top = new FormAttachment(composite, 6);
		fd_composite_1.left = new FormAttachment(composite, 0, SWT.LEFT);
		fd_composite_1.right = new FormAttachment(100, -10);
		composite_1.setLayoutData(fd_composite_1);
		
		Label lblMailBody = new Label(composite_1, SWT.NONE);
		FormData fd_lblMailBody = new FormData();
		fd_lblMailBody.right = new FormAttachment(100, 0);
		fd_lblMailBody.left = new FormAttachment(0, 10);
		fd_lblMailBody.top = new FormAttachment(0, 10);
		lblMailBody.setLayoutData(fd_lblMailBody);
		lblMailBody.setText("Mail Body:");
		
		bodyText = new Text(composite_1, SWT.BORDER | SWT.WRAP);
		FormData fd_bodyText = new FormData();
		fd_bodyText.top = new FormAttachment(lblMailBody, 6);
		fd_bodyText.left = new FormAttachment(lblMailBody, 0, SWT.LEFT);
		fd_bodyText.bottom = new FormAttachment(100, -30);
		fd_bodyText.right = new FormAttachment(100, -10);
		bodyText.setLayoutData(fd_bodyText);
		
		Button btnAddAttachment = new Button(this, SWT.NONE);
		fd_composite_1.bottom = new FormAttachment(btnAddAttachment, -6);
		FormData fd_btnAddAttachment = new FormData();
		fd_btnAddAttachment.left = new FormAttachment(0, 10);
		fd_btnAddAttachment.bottom = new FormAttachment(100, -10);
		btnAddAttachment.setLayoutData(fd_btnAddAttachment);
		btnAddAttachment.setText("Add Attachment");
		
		Button btnCancel = new Button(this, SWT.NONE);
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				MailWriter.this.close();
			}
		});
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(btnAddAttachment, 0, SWT.BOTTOM);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		Button btnSend = new Button(this, SWT.NONE);
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// Josh Clark
				email = new EmailStruct();
				loadToEmailStruct();
				if(checkValidEmailInput(email)){
					writeEmailtoServer();
					MailWriter.this.close();
				}
				else{
					showEmailInputFailureMessage();
				}
			}
		});
		FormData fd_btnSend = new FormData();
		fd_btnSend.top = new FormAttachment(btnAddAttachment, 0, SWT.TOP);
		fd_btnSend.right = new FormAttachment(btnCancel, -6);
		btnSend.setLayoutData(fd_btnSend);
		btnSend.setText("Send");
		
		Menu menu = new Menu(this, SWT.BAR);
		this.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmOpenDraft = new MenuItem(menu_1, SWT.NONE);
		mntmOpenDraft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser fc = new JFileChooser();
				int retVal = fc.showOpenDialog(null);
				if (retVal == JFileChooser.APPROVE_OPTION)
				{
					File emailFile = fc.getSelectedFile();
					email = new EmailStruct(emailFile);
					updateFields();
				}
				else
				{
					System.out.println("User cancelled draft open.");
				}
			}
		});
		mntmOpenDraft.setText("Open Draft");
		
		MenuItem mntmSaveAsDraft = new MenuItem(menu_1, SWT.NONE);
		mntmSaveAsDraft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadToEmailStruct();
				JFileChooser fc = new JFileChooser();
				int retVal = fc.showSaveDialog(null);
				if (retVal == JFileChooser.APPROVE_OPTION)
				{
					File emailFile = fc.getSelectedFile();
					email.writeToFile(emailFile);
				}
				else
				{
					System.out.println("User cancelled draft save.");
				}
			}
		});
		mntmSaveAsDraft.setText("Save As Draft");
		
		MenuItem mntmClose = new MenuItem(menu_1, SWT.NONE);
		mntmClose.setText("Close");
	}
	
	MailWriter(Display d, DHEncryptionIO serverIO)
	{
		this(d);
		this.io = serverIO;
	}
	
	//constructor for creating the window with default values
	MailWriter(Display d, String to, String subject, String body, DHEncryptionIO serverIO)
	{
		this(d, serverIO);
		if (to != null)
			toText.setText(to);
		subjectText.setText(subject);
		bodyText.setText(body);
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Mail");
		setSize(648, 503);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void loadToEmailStruct()
	{
		//to field
		String[] recipients = toText.getText().split(",");
		for (String recipient : recipients)
			email.addRecipient(recipient);
		
		email.setSubject(subjectText.getText());
		email.setBody(bodyText.getText());
	}
	
	private void updateFields()
	{
		toText.setText(email.getToString());
		bodyText.setText(email.getBody());
		subjectText.setText(email.getSubject());
	}
	
	
	// Josh Clark
	private String writeEmailtoServer()
	{
		String returnString;
		try {

			//create the appropriate packet
			PacketHeader emailPacketHeader = new PacketHeader();
			emailPacketHeader.setCommand(Command.SEND_EMAIL);
			
			//send the packet
			io.writeObject(emailPacketHeader);
			//io.flush();
			io.writeObject(email);
			
			//get the response
			PacketHeader responsePacket = (PacketHeader)io.readObject();

			if (responsePacket.getCommand() != Command.CONNECT_SUCCESS)
				returnString = "Response Packet contained non-success command";
			else
				returnString = "Successfully sent email to server!";
			
		} catch (Exception e)
		{
			returnString = "Exception thrown while trying to send email.\n" + e;
		}
		return returnString;
	}
	
	// Josh Clark
	private boolean checkValidEmailInput(EmailStruct myEmail){
		if(subjectText.getText().isEmpty() || toText.getText().isEmpty()){
			return false;
		}
		else{
			return true;
		}
	}
	
	// Josh Clark
	private void showEmailInputFailureMessage()
	{
		Shell invalid = new Shell();
		MessageBox messageBox = new MessageBox(invalid, SWT.OK);
		messageBox.setText("Invalid Email");
		if(toText.getText().isEmpty() && !subjectText.getText().isEmpty()){
			messageBox.setMessage("Please provide at least one recipient.");
		}
		else if(!toText.getText().isEmpty() && subjectText.getText().isEmpty()){
			messageBox.setMessage("Please provide a subject line.");
		}
		else {
			messageBox.setMessage("Please provide at least one recipient and a subject line.");
		}
		
		
		messageBox.open();
	}
	
	

}
