package game.aho.lps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import game.aho.lps.Player.DatabaseProperties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DialogWaitingForFight extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JButton cancelButton;
    private JLabel text;
    private Timer timer;
    private int waitingTime;

    /**
     * Create the dialog.
     */
    public DialogWaitingForFight() {
	setModalityType(ModalityType.APPLICATION_MODAL);
	setTitle("Waiting for respond");
	setResizable(false);
	getContentPane().setBackground(new Color(255, 255, 255));
	setBounds(100, 100, 450, 114);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setLayout(new FlowLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPanel.setOpaque(false);
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	{
	    text = new JLabel();
	    contentPanel.add(text);
	}
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    buttonPane.setOpaque(false);
	    getContentPane().add(buttonPane, BorderLayout.SOUTH);
	    {
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
			Vars.loggedPlayer.setMatchamaking(null, null);
			setVisible(false);
		    }
		});
		cancelButton.setForeground(new Color(255, 255, 255));
		cancelButton.setBackground(new Color(255, 0, 0));
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	    }
	}
    }

    public void init(final Player player) {
	final DatabaseProperties props = new DatabaseProperties(player.getMatchmakingString());
	text.setText(String.format("<html>Waiting for <b>%s</b> to respond your  mighty duel offer</html>", player.username));
	waitingTime = -1 * (Vars.rnd.nextInt(20) + 8); // Lets troll, there is no average waiting time

	(timer = new Timer()).schedule(new TimerTask() {

	    @Override
	    public void run() {
		text.setText(String.format("<html>Waiting for <b>%s</b> to respond your  mighty duel offer<br><i>%d seconds over average waiting time</i></html>", player.username,
			waitingTime));
		waitingTime++;
		if (waitingTime % 2 == 1)
		    return;
		if(Vars.loggedPlayer.getMatchmakingUserString() == null){
		    JOptionPane.showMessageDialog(null, player.username + " refused your request for fight", "Sorry", JOptionPane.INFORMATION_MESSAGE);
		    setVisible(false);
		    return;
		}
		if (!player.isOnline()) {
		    JOptionPane.showMessageDialog(null, player.username + " is not online anymore", "Sorry", JOptionPane.INFORMATION_MESSAGE);
		    setVisible(false);
		    return;
		}
		final Player inFight = player.getPlayerInFight();
		if (inFight == null || !inFight.isSameAs(Vars.loggedPlayer))
		    return;

		Vars.loggedPlayer.restartFight();
		Vars.loggedPlayer.setDuelWith(player);
		Vars.loggedPlayer.setMatchamaking(null, null);
		final TheGame game = new TheGame(Integer.parseInt(props.getString("diffn")), props.getString("lck").charAt(0) == 'e', Vars.loggedPlayer, player,
			GameMode.getByNumber(Integer.parseInt(props.getString("gmn"))));
		game.setVisible(true);
		setVisible(false);
		Vars.matchMaking.setVisible(false);
		timer.cancel();
	    }

	}, 1000, 1000);
    }

}
