package game.aho.lps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class DialogChangePassword extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JPasswordField passCurr;
    private JPasswordField passNew1;
    private JPasswordField passNew2;

    /**
     * Create the dialog.
     */
    public DialogChangePassword() {
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosed(WindowEvent arg0) {
		clearData();
	    }
	});
	setModalityType(ModalityType.APPLICATION_MODAL);
	setTitle("Change password");
	setBounds(100, 100, 450, 196);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBackground(Color.WHITE);
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(null);

	JLabel lblCurrentPassword = new JLabel("Current password");
	lblCurrentPassword.setBounds(10, 11, 118, 24);
	contentPanel.add(lblCurrentPassword);

	JLabel lblNewPassword = new JLabel("New password");
	lblNewPassword.setBounds(10, 45, 120, 24);
	contentPanel.add(lblNewPassword);

	JLabel lblRepeatNewPassword = new JLabel("Repeat new password:");
	lblRepeatNewPassword.setBounds(10, 80, 127, 31);
	contentPanel.add(lblRepeatNewPassword);

	passCurr = new JPasswordField();
	passCurr.setBounds(138, 11, 285, 24);
	contentPanel.add(passCurr);

	passNew1 = new JPasswordField();
	passNew1.setBounds(137, 45, 285, 24);
	contentPanel.add(passNew1);

	passNew2 = new JPasswordField();
	passNew2.setBounds(140, 80, 285, 24);
	contentPanel.add(passNew2);
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setBackground(Color.WHITE);
	    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    getContentPane().add(buttonPane, BorderLayout.SOUTH);
	    {
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			final String newPasswod = new String(passNew1.getPassword());
			if(!newPasswod.contentEquals(new String(passNew2.getPassword()))){
			    JOptionPane.showMessageDialog(Vars.dialogChangePassword, "New passwords do not match", "Butter fingers", JOptionPane.ERROR_MESSAGE);
			    return;
			}
			
			if (!Vars.loggedPlayer.checkPassword(new String(passCurr.getPassword()))) {
			    JOptionPane.showMessageDialog(Vars.dialogChangePassword, "Wrong current password", "Do you have wrong memory?", JOptionPane.ERROR_MESSAGE);
			    return;
			}
			
			Vars.loggedPlayer.setPassword(newPasswod);
			clearData();
			setVisible(false);
		    }
		});
		okButton.setBackground(new Color(139, 0, 0));
		okButton.setForeground(Color.WHITE);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
			clearData();
			setVisible(false);
		    }
		});
		cancelButton.setBackground(new Color(255, 0, 102));
		cancelButton.setForeground(new Color(255, 255, 255));
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	    }
	}
    }

    private void clearData() {
	passCurr.setText("");
	passNew1.setText("");
	passNew2.setText("");
    }
}
