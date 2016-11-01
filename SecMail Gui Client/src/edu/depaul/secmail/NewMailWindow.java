package edu.depaul.secmail;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class NewMailWindow {

	protected Shell shlNewSecmail;
	private Text toText;
	private Text subjectText;
	private Text bodyText;
	EmailStruct email;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NewMailWindow window = new NewMailWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlNewSecmail.open();
		shlNewSecmail.layout();
		while (!shlNewSecmail.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlNewSecmail = new Shell();
		shlNewSecmail.setSize(639, 531);
		shlNewSecmail.setText("New SecMail");
		shlNewSecmail.setLayout(new FormLayout());
		
		Composite composite = new Composite(shlNewSecmail, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
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
		toText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String[] recipients = toText.getText().split(",");
				for (String recipient : recipients)
					email.addRecipient(recipient);
				email.setSubject(subjectText.getText());
				email.setBody(bodyText.getText());
			}
		});
		toText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSubject = new Label(composite, SWT.NONE);
		lblSubject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubject.setText("Subject:");
		
		subjectText = new Text(composite, SWT.BORDER);
		subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_1 = new Composite(shlNewSecmail, SWT.NONE);
		fd_composite.bottom = new FormAttachment(100, -416);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.top = new FormAttachment(composite, 6);
		fd_composite_1.left = new FormAttachment(0, 10);
		fd_composite_1.bottom = new FormAttachment(100, -64);
		fd_composite_1.right = new FormAttachment(0, 613);
		composite_1.setLayoutData(fd_composite_1);
		
		Label lblMailBody = new Label(composite_1, SWT.NONE);
		lblMailBody.setBounds(10, 10, 55, 15);
		lblMailBody.setText("Mail Body:");
		
		bodyText = new Text(composite_1, SWT.BORDER);
		bodyText.setBounds(10, 31, 583, 315);
		
		Button btnAddAttachment = new Button(shlNewSecmail, SWT.NONE);
		FormData fd_btnAddAttachment = new FormData();
		fd_btnAddAttachment.bottom = new FormAttachment(100, -10);
		fd_btnAddAttachment.left = new FormAttachment(composite, 0, SWT.LEFT);
		btnAddAttachment.setLayoutData(fd_btnAddAttachment);
		btnAddAttachment.setText("Add Attachment");
		
		Button btnCancel = new Button(shlNewSecmail, SWT.NONE);
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shlNewSecmail.close();
			}
		});
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(btnAddAttachment, 0, SWT.BOTTOM);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		Button btnSend = new Button(shlNewSecmail, SWT.NONE);
		FormData fd_btnSend = new FormData();
		fd_btnSend.top = new FormAttachment(btnAddAttachment, 0, SWT.TOP);
		fd_btnSend.right = new FormAttachment(btnCancel, -6);
		btnSend.setLayoutData(fd_btnSend);
		btnSend.setText("Send");

	}
}
