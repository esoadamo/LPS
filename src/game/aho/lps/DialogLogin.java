package game.aho.lps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import aho.util.mysql2PHP2Java.MySQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DialogLogin extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtUsername;
    private JPasswordField pass;
    private JLabel lblInfo;

    /**
     * Create the dialog.
     */
    public DialogLogin() {
	setModalityType(ModalityType.APPLICATION_MODAL);
	setTitle("Who are you?");
	setBounds(100, 100, 450, 135);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().setBackground(Color.WHITE);

	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPanel.setOpaque(false);

	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(null);
	{
	    JLabel lblUsername = new JLabel("Username: ");
	    lblUsername.setBounds(10, 11, 93, 14);
	    contentPanel.add(lblUsername);
	}
	{
	    JLabel lblPassword = new JLabel("Password: ");
	    lblPassword.setBounds(10, 36, 93, 14);
	    contentPanel.add(lblPassword);
	}
	{
	    txtUsername = new JTextField();
	    txtUsername.setBounds(113, 8, 311, 20);
	    contentPanel.add(txtUsername);
	    txtUsername.setColumns(10);
	}
	{
	    pass = new JPasswordField();
	    pass.setBounds(113, 33, 311, 17);
	    contentPanel.add(pass);
	}
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    buttonPane.setOpaque(false);
	    getContentPane().add(buttonPane, BorderLayout.SOUTH);
	    {
		JButton okButton = new JButton("Log in");
		okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			final Player newPlayer = parsePlayer();
			if(!newPlayer.exists()){
			    lblInfo.setText("Account does not exist");
			    lblInfo.setForeground(Color.RED);
			    return;
			}
			if(!newPlayer.checkPassword(new String(pass.getPassword()))){
			    lblInfo.setText("Wrong password");
			    lblInfo.setForeground(Color.RED);
			    return;
			}
			Vars.loggedPlayer = newPlayer;
			login();
		    }
		});
		{
		    lblInfo = new JLabel("");
		    buttonPane.add(lblInfo);
		}
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		JButton btnCreateAccount = new JButton("Create account");
		btnCreateAccount.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
			if (parsePlayer().exists()) {
			    lblInfo.setText("Account already exists");
			    lblInfo.setForeground(Color.RED);
			    return;
			}
			if (JOptionPane.showConfirmDialog(Vars.dialogLogin,
				"Passwords saved in database are not very safe. They're stored as SHA512 hashes.\nPlease, for your own security, use password that you will not use anywhere else.",
				"Warning", JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
			    return;
			try {
			    (Vars.loggedPlayer = Player.registerPlayer(txtUsername.getText())).setPassword(new String(pass.getPassword()));
			    login();
			} catch (MySQLException e) {
			    lblInfo.setForeground(Color.RED);
			    lblInfo.setText("Account creation failed.");
			    e.printStackTrace();
			}
		    }
		});
		buttonPane.add(btnCreateAccount);
	    }
	    {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			clearData();
			setVisible(false);
		    }
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	    }
	}
    }

    /**
     * Parses player from entered username
     * @return parsed player
     */
    private Player parsePlayer() {
	final Player newPlayer = new Player(txtUsername.getText());
	return newPlayer;
    }

    /**
     * Changes main menu to logged in menu and hides frame
     */
    private void login() {
	Vars.loggedPlayer.exitGame();
	clearData();
	setVisible(false);
	Vars.mainWindow.changeButtons(Vars.mainWindow.bntsDataOnlineGame);
	new Thread(){
	    @Override
	    public void run(){
		System.out.println("Reloading cosmetics");
		DialogCharacterCustomization.reloadCosmetic();
		System.out.println("Cosmetics reloaded");
	    }
	}.start();
    }

    /**
     * Clears all entered data
     */
    private void clearData() {
	txtUsername.setText("");
	pass.setText("");
	lblInfo.setText("");
    }

}
