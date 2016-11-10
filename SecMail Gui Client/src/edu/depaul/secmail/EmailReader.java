package edu.depaul.secmail;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.text.DateFormat;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.TableColumn;

public class EmailReader extends Shell {
	private Text txtTo;
	private Text txtFrom;
	private Text txtDate;
	private Text txtSubject;
	private Table tblAttachments;
	private StyledText stxtBody;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			EmailReader shell = new EmailReader(display);
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
	public EmailReader(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		Label lblTo = new Label(this, SWT.NONE);
		lblTo.setBounds(10, 10, 55, 15);
		lblTo.setText("To:");
		
		Label lblFrom = new Label(this, SWT.NONE);
		lblFrom.setBounds(10, 37, 55, 15);
		lblFrom.setText("From:");
		
		Label lblDate = new Label(this, SWT.NONE);
		lblDate.setBounds(10, 61, 55, 15);
		lblDate.setText("Date:");
		
		Label lblSubject = new Label(this, SWT.NONE);
		lblSubject.setBounds(10, 85, 55, 15);
		lblSubject.setText("Subject:");
		
		Label lblAttachments = new Label(this, SWT.NONE);
		lblAttachments.setBounds(10, 109, 80, 15);
		lblAttachments.setText("Attachments:");
		
		txtTo = new Text(this, SWT.BORDER);
		txtTo.setEditable(false);
		txtTo.setBounds(71, 7, 563, 21);
		
		txtFrom = new Text(this, SWT.BORDER);
		txtFrom.setEditable(false);
		txtFrom.setBounds(71, 31, 563, 21);
		
		txtDate = new Text(this, SWT.BORDER);
		txtDate.setEditable(false);
		txtDate.setBounds(71, 55, 186, 21);
		
		txtSubject = new Text(this, SWT.BORDER);
		txtSubject.setEditable(false);
		txtSubject.setBounds(71, 79, 563, 21);
		
		tblAttachments = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		tblAttachments.setBounds(10, 130, 624, 45);
		tblAttachments.setHeaderVisible(true);
		tblAttachments.setLinesVisible(true);
		
		TableColumn tblclmnFileName = new TableColumn(tblAttachments, SWT.NONE);
		tblclmnFileName.setWidth(513);
		tblclmnFileName.setText("File Name");
		
		TableColumn tblclmnFileSize = new TableColumn(tblAttachments, SWT.NONE);
		tblclmnFileSize.setWidth(100);
		tblclmnFileSize.setText("File Size");
		
		Label lblBody = new Label(this, SWT.NONE);
		lblBody.setBounds(10, 181, 55, 15);
		lblBody.setText("Body:");
		
		stxtBody = new StyledText(this, SWT.BORDER);
		stxtBody.setEditable(false);
		stxtBody.setBounds(10, 202, 624, 208);
		
		Button btnClose = new Button(this, SWT.NONE);
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				EmailReader.this.close();
			}
		});
		btnClose.setBounds(559, 426, 75, 25);
		btnClose.setText("Close");
		
		Button btnReply = new Button(this, SWT.NONE);
		btnReply.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			}
		});
		btnReply.setBounds(10, 426, 75, 25);
		btnReply.setText("Reply");
		
		Button btnReplyall = new Button(this, SWT.NONE);
		btnReplyall.setBounds(91, 426, 75, 25);
		btnReplyall.setText("Reply-All");
		
		Button btnForward = new Button(this, SWT.NONE);
		btnForward.setBounds(172, 426, 75, 25);
		btnForward.setText("Forward");
		createContents();
	}
	
	EmailReader(Display d, EmailStruct email, UserStruct from, Date emailDate)
	{
		this(d);
		txtTo.setText(email.getToString());
		txtFrom.setText(from.compile());
		txtDate.setText(DateFormat.getDateTimeInstance().format(emailDate));
		txtSubject.setText(email.getSubject());
		
		//handle the attachments
		LinkedList<File> attachments = email.getAttachmentList();
		for (File f : attachments)
		{
			TableItem t = new TableItem(tblAttachments, 0);
			t.setData(f);
			t.setText(0, f.getName()); // filename
			t.setText(1, String.valueOf(f.length()));
		}
		
		//set the body to something.
		stxtBody.setText(email.getBody());
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("EmailReader");
		setSize(660, 500);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
