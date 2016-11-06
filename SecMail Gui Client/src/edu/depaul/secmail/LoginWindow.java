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

public class LoginWindow extends Shell {
	private Text txtServer;
	private Text txtUser;
	private Text txtPassword;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			LoginWindow shell = new LoginWindow(display);
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
	public LoginWindow(Display display) {
		super(display, SWT.SHELL_TRIM);
		createServerInput();
		createUsernameInput();
		createPasswordInput();
		createLoginButton();
		createCloseButton();
		createContents();
	}
	
	private void createServerInput(){
		Label lblServer = new Label(this, SWT.NONE);
		lblServer.setBounds(37, 25, 41, 15);
		lblServer.setText("Server:");
		
		txtServer = new Text(this, SWT.BORDER);
		txtServer.setBounds(100, 25, 164, 21);
		
	}
	
	private void createUsernameInput(){
		Label lblUser = new Label(this, SWT.NONE);
		lblUser.setBounds(37, 60, 66, 15);
		lblUser.setText("Username:");
		
		txtUser = new Text(this, SWT.BORDER);
		txtUser.setBounds(100, 60, 164, 21);
		
	}
	
	private void createPasswordInput(){
		Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setBounds(37, 95, 66, 15);
		lblPassword.setText("Password:");
		
		txtPassword = new Text(this, SWT.BORDER);
		txtPassword.setBounds(100, 95, 164, 21);
		
	}
	
	private void createLoginButton(){
		Button btnLogin = new Button(this, SWT.NONE);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				MainWindow mainWindow = new MainWindow();
				mainWindow.open();
			}
		});
		btnLogin.setBounds(134, 226, 75, 35);
		btnLogin.setText("Log In");
		
		
	}
	
	private void createResultInput(){
		Label lblResult = new Label(this, SWT.NONE);
		lblResult.setBounds(37, 63, 55, 15);
		lblResult.setText("Result:");
		final StyledText txtResult = new StyledText(this, SWT.BORDER);
		txtResult.setEditable(false);
		txtResult.setBounds(37, 84, 211, 136);
		
		Button btnConnect = new Button(this, SWT.NONE);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
		});
		btnConnect.setBounds(134, 226, 75, 35);
		btnConnect.setText("Connect");
	}
	
	private void createCloseButton(){
		Button btnClose = new Button(this, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LoginWindow.this.close();
			}
		});
		btnClose.setBounds(215, 226, 75, 35);
		btnClose.setText("Close");		
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Login to SecMail");
		setSize(316, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}