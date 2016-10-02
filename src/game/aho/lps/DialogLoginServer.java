package game.aho.lps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import aho.util.mysql2PHP2Java.MySQL2PHP2Java;
import aho.util.mysql2PHP2Java.MySQLException;
import aho.util.mysql2PHP2Java.MySQLState;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class DialogLoginServer extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtServer;
    private JPasswordField passServer;
    private JLabel lblStatus;
    private JButton btnCreateAccount;
    private JPanel accountLoginPane;
    private JTextField txtUsername;
    private JPasswordField passAccount;
    private JLabel lblLoginStatus;
    private JButton btnReset;
    private JButton okButton;
    private JButton btnTestConnection;

    public void init() {
	if (Vars.conn != null) {
	    Vars.conn.logout();
	    Vars.conn = null;
	}
	setTitle("Enter server login informations");
	setBackground(Color.WHITE);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPanel.setOpaque(false);
	getContentPane().setBackground(Color.WHITE);
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	setBounds(getX(), getY(), 450, 200);
	contentPanel.setLayout(null);

	final JLabel lblServer = new JLabel("Server:");
	lblServer.setBounds(10, 11, 101, 14);
	contentPanel.add(lblServer);

	txtServer = new JTextField();
	txtServer.setBounds(121, 8, 303, 20);
	contentPanel.add(txtServer);
	txtServer.setColumns(10);

	btnTestConnection = new JButton("Test connection");
	btnTestConnection.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		testConnection();
	    }
	});
	btnTestConnection.setBounds(10, 32, 414, 23);
	contentPanel.add(btnTestConnection);
	{
	    JLabel lblServerPassword = new JLabel("Server password: ");
	    lblServerPassword.setBounds(10, 66, 107, 14);
	    contentPanel.add(lblServerPassword);
	}

	passServer = new JPasswordField();
	passServer.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent arg0) {
		if (Vars.conn == null)
		    return;
		Vars.conn = new MySQL2PHP2Java(txtServer.getText(), new String(passServer.getPassword()));
	    }
	});
	passServer.setBounds(121, 63, 303, 18);
	contentPanel.add(passServer);

	accountLoginPane = new JPanel();
	accountLoginPane.setOpaque(false);
	accountLoginPane.setBounds(10, 92, 414, 70);
	accountLoginPane.setVisible(false);
	{
	    lblStatus = new JLabel("Status: Not verified");
	    lblStatus.setBounds(10, 79, 414, 59);
	    contentPanel.add(lblStatus);
	    lblStatus.setVerticalAlignment(SwingConstants.TOP);
	    lblStatus.setForeground(Color.GRAY);
	}
	contentPanel.add(accountLoginPane);
	accountLoginPane.setLayout(null);
	{
	    JLabel lblUsername = new JLabel("Username: ");
	    lblUsername.setBounds(10, 11, 153, 14);
	    accountLoginPane.add(lblUsername);
	}
	{
	    JLabel lblPassword = new JLabel("Password: ");
	    lblPassword.setBounds(10, 36, 153, 14);
	    accountLoginPane.add(lblPassword);
	}

	txtUsername = new JTextField();
	txtUsername.setBounds(173, 8, 231, 20);
	accountLoginPane.add(txtUsername);
	txtUsername.setColumns(10);

	passAccount = new JPasswordField();
	passAccount.setBounds(173, 33, 231, 20);
	accountLoginPane.add(passAccount);
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    buttonPane.setOpaque(false);
	    getContentPane().add(buttonPane, BorderLayout.SOUTH);
	    {
		lblLoginStatus = new JLabel("Not logged in");
		lblLoginStatus.setBackground(Color.GRAY);
		buttonPane.add(lblLoginStatus);
	    }
	    {
		okButton = new JButton("Log in");
		okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			login();
		    }
		});
		okButton.setEnabled(false);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		btnCreateAccount = new JButton("Create account");
		btnCreateAccount.setVisible(false);
		btnCreateAccount.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
			try {
			    if (!Vars.conn.createAccount(txtUsername.getText(), new String(passAccount.getPassword()))) {
				lblLoginStatus.setForeground(Color.RED);
				lblStatus.setForeground(Color.RED);
				lblLoginStatus.setText("Account creating failed");
				lblStatus.setText("Account creating failed");
				return;
			    }
			} catch (MySQLException e) {
			    if (e.exceptionType == MySQLState.ACCOUNT_EXISTS) {
				lblLoginStatus.setText("Account already exists");
				lblLoginStatus.setForeground(Color.RED);
			    } else if (e.exceptionType == MySQLState.WRONG_PASSWORD) {
				lblLoginStatus.setText("Wrong server password");
				lblStatus.setText("<html>Wrong server password.<br>Please change it.</html>");
				lblLoginStatus.setForeground(Color.RED);
			    } else if (e.exceptionType == MySQLState.BANNED) {
				lblLoginStatus.setText("Banned");
				lblLoginStatus.setForeground(Color.RED);
				lblStatus.setText("<html>You are banned from the server</html>");
			    } else {
				lblLoginStatus.setForeground(Color.RED);
				lblLoginStatus.setText("Account creating failed");
				lblStatus.setText("Account creating failed");
				e.printStackTrace();
			    }
			    return;
			}

			btnCreateAccount.setEnabled(false);
			passAccount.setEnabled(false);
			txtUsername.setEnabled(false);
			lblLoginStatus.setForeground(Color.GREEN);
			lblLoginStatus.setText("Click log in");
		    }
		});
		buttonPane.add(btnCreateAccount);
	    }
	    {
		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			init();
		    }
		});
		buttonPane.add(btnReset);
	    }
	    {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
			init();
			setVisible(false);
		    }
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	    }
	}

	if (LockPickerSimulator.pArgs.argExist("--web-browser")) {
	    passServer.setText("pass");
	    txtServer.setText("http://ubuntusevr.hukot.net/lps.php");
	    txtUsername.setText("WebBrowser");
	    passAccount.setText("a5454ae56yx!");
	}
    }

    protected void login() {
	try {
	    final String pass = new String(passAccount.getPassword());
	    final String user = txtUsername.getText();
	    if(Vars.conn == null)
		Vars.conn = new MySQL2PHP2Java(txtServer.getText(), new String(passServer.getPassword()));
	    if (!Vars.conn.login(user, pass)) {
		lblLoginStatus.setForeground(Color.RED);
		lblLoginStatus.setText("Login failed");
		lblStatus.setText("<html>Please check all data</html>");
		lblStatus.setForeground(Color.RED);
		return;
	    }
	} catch (MySQLException e1) {
	    if (e1.exceptionType == MySQLState.WRONG_PASSWORD) {
		lblLoginStatus.setText("Wrong password");
		lblStatus.setText("<html>Wrong password.<br>Please change it.</html>");
		lblLoginStatus.setForeground(Color.RED);
	    } else if (e1.exceptionType == MySQLState.BANNED) {
		lblLoginStatus.setText("Banned");
		lblLoginStatus.setForeground(Color.RED);
		lblStatus.setText("<html>You are banned from the server</html>");
	    } else {
		lblLoginStatus.setForeground(Color.RED);
		lblLoginStatus.setText("Login failed");
		lblStatus.setText("Login failed");
		e1.printStackTrace();
	    }
	    return;
	}

	System.out.println("Logged in");
	Vars.properties.setProperty("server", txtServer.getText());
	Vars.properties.setProperty("serverPassword", new String(passServer.getPassword()));
	Vars.properties.setProperty("serverAccountPassword", new String(passAccount.getPassword()));
	Vars.saveSettings();
	setVisible(false);
	Vars.dialogLogin.setLocationRelativeTo(Vars.mainWindow.frame);
	Vars.dialogLogin.setVisible(true);
    }

    protected boolean testConnection() {
	Vars.conn = new MySQL2PHP2Java(txtServer.getText(), new String(passServer.getPassword()));
	if (!Vars.conn.testConn2PHPScript()) {
	    lblStatus.setText("<html>Connecting to the server '" + txtServer.getText() + "' failed.Please check adress and internet connection.</html>");
	    lblStatus.setForeground(Color.RED);
	    Vars.conn = null;
	    return false;
	}
	if (!Vars.conn.testConn2MySQL()) {
	    lblStatus.setText("<html>Cannot connect to MySQL database<br>Please contact administrator.</html>");
	    lblStatus.setForeground(Color.RED);
	    Vars.conn = null;
	    return false;
	}
	lblStatus.setText("Connected");
	lblStatus.setForeground(Color.GREEN);

	Vars.conn.setCache(MySQL2PHP2Java.CACHE_DISABLED);
	Vars.conn.setAllowBase64(true);

	if (Vars.conn.testAccountCreationEnabled())
	    btnCreateAccount.setVisible(true);

	accountLoginPane.setVisible(true);
	okButton.setEnabled(true);
	btnTestConnection.setEnabled(false);
	txtServer.setEnabled(false);
	setBounds(getX(), getY(), 450, 300);
	return true;
    }

    /**
     * Create the dialog.
     */
    public DialogLoginServer() {
	setResizable(false);
	setModalityType(ModalityType.APPLICATION_MODAL);
	setBounds(100, 100, 450, 200);
	init();
    }
}
