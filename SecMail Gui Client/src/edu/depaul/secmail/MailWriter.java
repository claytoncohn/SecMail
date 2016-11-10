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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MailWriter extends Shell {
	private Text toText;
	private Text subjectText;
	private Text bodyText;
	EmailStruct email;

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
		fd_composite.top = new FormAttachment(0, 33);
		fd_composite.right = new FormAttachment(100, -16);
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
		fd_composite.bottom = new FormAttachment(composite_1, -6);
		fd_composite.left = new FormAttachment(composite_1, 0, SWT.LEFT);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.top = new FormAttachment(0, 105);
		fd_composite_1.left = new FormAttachment(0, 10);
		fd_composite_1.right = new FormAttachment(100, -10);
		composite_1.setLayoutData(fd_composite_1);
		
		Label lblMailBody = new Label(composite_1, SWT.NONE);
		lblMailBody.setBounds(10, 10, 55, 15);
		lblMailBody.setText("Mail Body:");
		
		bodyText = new Text(composite_1, SWT.BORDER);
		bodyText.setBounds(10, 31, 583, 315);
		
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
				loadToEmailStruct();
				System.out.println(testEmail());
				MailWriter.this.close();
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

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("New Mail");
		setSize(450, 300);

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
	
	private String testEmail()
	{
		String txtServer = ":57890";
		String[] server = txtServer.split(":");
		String returnString;
		if (server.length != 2)
		{
			return "Invalid Server format. Please use format <server>:<port>\n";
		}
		
		try {
			Socket s = new Socket(server[0], Integer.valueOf(server[1]));
			DHEncryptionIO io = new DHEncryptionIO(s, false);
			
			//create the appropriate packet
			PacketHeader testPacketHeader = new PacketHeader();
			testPacketHeader.setCommand(Command.SEND_EMAIL);
			
			//send the packet
			io.writeObject(testPacketHeader);
			io.flush();
			io.writeObject(email);
			
			//get the response
			PacketHeader responsePacket = (PacketHeader)io.readObject();
			
			if (responsePacket.getCommand() != Command.CONNECT_SUCCESS)
				returnString = "Response Packet contained non-success command";
			else
				returnString = "Successfully connected to remote server.\nSuccessfully transmitted test packet.\n"
						+ "Recieved valid Success packet\n"
						+ "Connection test successful!\n"
						+ "Connection closing...";
			
			io.writeObject(new PacketHeader(Command.CLOSE));
			
			io.close();
			s.close();
		} catch (Exception e)
		{
			returnString = "Exception thrown while trying to connect.\n" + e;
		}
		return returnString;
	}

}
