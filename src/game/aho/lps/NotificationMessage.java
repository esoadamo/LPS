package game.aho.lps;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class NotificationMessage extends JFrame {
    public interface Action {
	public void action();
    }

    private final JPanel contentPane;
    private final Action action;
    private final JLabel lblLblimage = new JLabel();
    private final JLabel lblLbltext = new JLabel();

    /**
     * Create the frame.
     * 
     * @param icon
     *            icon to show
     * @param message
     *            message to show
     * @param ttl
     *            how long (in millis) is this notification going to stay
     * @param action
     *            action performed on mouseclick
     */
    public NotificationMessage(final ImageIcon icon, final String message, final int ttl, Action action) {
	this.action = action;
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(0, 0, 449, 151);
	setUndecorated(true);
	contentPane = new JPanel();
	contentPane.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent arg0) {
		doAction();
	    }
	});
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(new BorderLayout(0, 0));
	setContentPane(contentPane);
	contentPane.setOpaque(false);
	setAlwaysOnTop(true);

	setIconImage(Vars.imgFrameIcon);

	lblLblimage.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		doAction();
	    }
	});
	try {
	    lblLblimage.setIcon(TheGame.scaleImage(icon, getHeight(), getHeight()));
	} catch (Exception e) {
	    e.printStackTrace();
	}
	contentPane.add(lblLblimage, BorderLayout.WEST);

	lblLbltext.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		doAction();
	    }
	});
	lblLbltext.setText("<html>" + message + "</html>");
	lblLbltext.setHorizontalAlignment(SwingConstants.CENTER);
	lblLbltext.setForeground(Color.BLACK);
	contentPane.add(lblLbltext, BorderLayout.CENTER);

	setVisible(true);
	appearFrame(0.3f);

	new Thread() {
	    public void run() {
		try {
		    Thread.sleep(ttl);

		    for (int i = getBackground().getAlpha(); getBackground().getAlpha() != 0; i--) {
			setBackground(new Color(255, 255, 255, i));

			sleep(50);
		    }
		    setVisible(false);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}.start();
    }

    /**
     * Plays notification sound
     */
    public void playSound() {
	Vars.soundNotification.play();
    }

    private void appearFrame(final float targetTranph) {
	new Thread() {
	    public void run() {
		for (float f = 0; f <= targetTranph; f = f + 0.005f) {
		    setBackground(new Color(255.0f, 255.0f, 255.0f, f));
		    try {
			sleep(50);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		}
	    }
	}.start();
    }

    private void doAction() {
	if (action != null)
	    action.action();
    }

}
