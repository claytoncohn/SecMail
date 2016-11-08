package edu.depaul.secmail;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.StyledText;

public class ConnectionTestWindow extends Shell {
	private Text txtServer;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ConnectionTestWindow shell = new ConnectionTestWindow(display);
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
	 */
	public ConnectionTestWindow(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		Label lblServer = new Label(this, SWT.NONE);
		lblServer.setBounds(37, 24, 41, 15);
		lblServer.setText("Server:");
		
		txtServer = new Text(this, SWT.BORDER);
		txtServer.setBounds(84, 24, 164, 21);
		
		Label lblResult = new Label(this, SWT.NONE);
		lblResult.setBounds(37, 63, 55, 15);
		lblResult.setText("Result:");
		
		Button btnClose = new Button(this, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConnectionTestWindow.this.close();
			}
		});
		btnClose.setBounds(215, 226, 75, 25);
		btnClose.setText("Close");		
		
		StyledText txtResult = new StyledText(this, SWT.BORDER);
		txtResult.setEditable(false);
		txtResult.setBounds(37, 84, 211, 136);
		createContents();
		
		Button btnConnect = new Button(this, SWT.NONE);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				txtResult.setText("");
				txtResult.setText(testConnection());
			}
		});
		btnConnect.setBounds(134, 226, 75, 25);
		btnConnect.setText("Connect");
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Test SecMail Connection");
		setSize(316, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	//Attempt to connect to a server and send a CONNECTION_TEST command
	private String testConnection()
	{
		String[] server = txtServer.getText().split(":");
		String returnString;
		if (server.length != 2)
		{
			return "Invalid Server format. Please use format <server>:<port>\n";
		}
		
		try {
			Socket s = new Socket(server[0], Integer.valueOf(server[1]));
			DHEncryptionWriter output = new DHEncryptionWriter(s, false);
			DHEncryptionReader input = new DHEncryptionReader(s, false);
			
			//create the appropriate packet
			PacketHeader testPacketHeader = new PacketHeader();
			testPacketHeader.setCommand(Command.CONNECT_TEST);
			
			//send the packet
			output.writeObject(testPacketHeader);
			
			//get the response
			PacketHeader responsePacket = (PacketHeader)input.readObject();
			
			if (responsePacket.getCommand() != Command.CONNECT_SUCCESS)
				returnString = "Response Packet contained non-success command";
			else
				returnString = "Successfully connected to remote server.\nSuccessfully transmitted test packet.\n"
						+ "Recieved valid Success packet\n"
						+ "Connection test successful!\n"
						+ "Connection closing...";
			
			output.writeObject(new PacketHeader(Command.CLOSE));
			
			output.close();
			input.close();
			s.close();
		} catch (Exception e)
		{
			returnString = "Exception thrown while trying to connect.\n" + e;
		}
		return returnString;
	}
}
