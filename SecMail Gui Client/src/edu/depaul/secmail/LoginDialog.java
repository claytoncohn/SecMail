package edu.depaul.secmail;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class LoginDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	public enum Status {
		LOGIN_SUCCESS,
		LOGIN_FAIL,
		EXIT
	}
	private Status returnStatus;
	private DHEncryptionIO serverConnection = null;
	private Text txtUsername;
	private Text txtPassword;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LoginDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Status open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return returnStatus;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		
		txtUsername = new Text(shell, SWT.BORDER);
		txtUsername.setBounds(88, 38, 311, 21);
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("Username:");
		label.setBounds(27, 38, 55, 15);
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("Password:");
		label_1.setBounds(27, 78, 55, 15);
		
		txtPassword = new Text(shell, SWT.BORDER);
		txtPassword.setEchoChar('*');
		txtPassword.setBounds(88, 78, 311, 21);
		
		Button button = new Button(shell, SWT.NONE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				returnStatus = Status.EXIT;
				shell.close();
			}
		});
		button.setText("Exit");
		button.setBounds(235, 160, 75, 25);
		
		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				doLogin();
				if (returnStatus == Status.LOGIN_SUCCESS)
					shell.close(); // close the login prompt
				else if (returnStatus == Status.LOGIN_FAIL)
					showLoginFailureMessage();
				else
					shell.close();
			}
		});
		button_1.setText("Login");
		button_1.setBounds(134, 160, 75, 25);

	}
	
	private void doLogin()
	{
		//parse the username
		String user, domain, portString = null;
		int port;
		
		//user part first
		int atLocation = txtUsername.getText().indexOf("@");
		if (atLocation <= 0)
		{
			outputUserFormatError();
			return;
		}
		
		user = txtUsername.getText().substring(0,atLocation);
		
		//check if there is a port
		int colonLocation = txtUsername.getText().indexOf(":");
		if (colonLocation > atLocation)
		{
			domain = txtUsername.getText().substring(atLocation+1,colonLocation);
			portString = txtUsername.getText().substring(colonLocation+1); // get to end
		}
		else if (colonLocation < atLocation && colonLocation > 0)
		{
			outputUserFormatError();
			return;
		}
		else
			domain = txtUsername.getText().substring(atLocation+1);
		
		if (portString != null)
			port = Integer.valueOf(portString);
		else
			port = 57890;
		
		
		try {
			//make the connection
			Socket s = new Socket(domain, port);
			serverConnection = new DHEncryptionIO(s, false);
			
			//do the actual login
			PacketHeader loginPacket = new PacketHeader(Command.LOGIN);
			serverConnection.writeObject(loginPacket);
			serverConnection.writeObject(user);
			serverConnection.writeObject(txtPassword.getText());
			
			//get the response
			PacketHeader responsePacket = (PacketHeader)serverConnection.readObject();
			Command responseCommand = responsePacket.getCommand();
			if (responseCommand == Command.LOGIN_SUCCESS)
				returnStatus = Status.LOGIN_SUCCESS;
			else if (responseCommand == Command.LOGIN_FAIL)
			{
				returnStatus = Status.LOGIN_FAIL;
				serverConnection.close();
				s.close();
				serverConnection = null;
			}
			else
				throw new Exception("SecMail Protocol Error"); //TODO: should probably handle this a bit more gracefully.
		} catch (IOException e)
		{
			System.out.println("Got an IOException.");
			System.out.println(e);
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("Serialization error: ClassNotFoundException");
			System.out.println(e);
		}
		catch(Exception e)
		{
			System.out.println("Caught general exception");
			System.out.println(e);
		}
	}
	
	private void outputUserFormatError()
	{
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setText("Invalid Username");
		messageBox.setMessage("Username format invalid.\nPlease use format \"<user>@<domain>:<port>\"\n port is optional");
		messageBox.open();
		returnStatus = Status.LOGIN_FAIL;
	}
	
	private void showLoginFailureMessage()
	{
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setText("Invalid login");
		messageBox.setMessage("Username or password incorrect");
		messageBox.open();
	}
	
	private DHEncryptionIO getServerConnection()
	{
		return serverConnection;
	}

}
