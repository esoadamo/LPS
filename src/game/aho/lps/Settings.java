package game.aho.lps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JSlider;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class Settings extends JDialog {

    private final JPanel contentPanel = new JPanel() {
	@Override
	public void paintComponent(Graphics g) {
	    g.drawImage(Vars.imgBackgroundWood, 0, 0, getWidth(), getHeight(), null);
	}
    };
    public static final JSlider slidVolume = new JSlider();
    protected static final JComboBox<Resolution> comboResolution = new JComboBox<Resolution>();
    public JLabel lblCurrentsong;
    private JButton btnPrev;
    private JButton btnNext;
    public JSlider slidMusicVolume;

    /**
     * Create the dialog.
     */
    public Settings() {
	setTitle("Settings");
	Vars.reloadIconsListeners.add(new Vars.NewIconListener() {

	    @Override
	    public void loadNewIcon() {
		setIconImage(Vars.imgFrameIcon);
	    }
	});
	setBounds(100, 100, 425, 227);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setLayout(new FlowLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	{
	    Box verticalBox = Box.createVerticalBox();
	    contentPanel.add(verticalBox);
	    {
		JLabel label = new JLabel("Settings");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		verticalBox.add(label);
		label.setForeground(Color.CYAN);
		label.setFont(new Font("Tahoma", Font.BOLD, 20));
	    }
	    {
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		{
		    JLabel label = new JLabel("Resolution");
		    label.setForeground(Color.MAGENTA);
		    horizontalBox.add(label);
		}
		{

		    comboResolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if (!isVisible())
				return;
			    try {
				Vars.changeResolution((Resolution) comboResolution.getSelectedItem());
			    } catch (Exception e1) {
				e1.printStackTrace();
			    }
			}
		    });
		    comboResolution.setModel(new DefaultComboBoxModel<Resolution>(Resolution.values()));
		    comboResolution.setSelectedIndex(2);
		    comboResolution.setOpaque(false);
		    horizontalBox.add(comboResolution);
		}
	    }
	    {
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		{
		    JLabel label = new JLabel("Volume:");
		    label.setForeground(Color.MAGENTA);
		    horizontalBox.add(label);
		}
		{
		    slidVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
			    Vars.setVolume(slidVolume.getValue());
			}
		    });
		    slidVolume.setOpaque(false);
		    slidVolume.setMaximum(100);
		    horizontalBox.add(slidVolume);
		}
	    }
	    {
		JLabel lblJukebox = new JLabel("Jukebox");
		verticalBox.add(lblJukebox);
		lblJukebox.setHorizontalAlignment(SwingConstants.CENTER);
		lblJukebox.setForeground(Color.CYAN);
		lblJukebox.setFont(new Font("Tahoma", Font.BOLD, 20));
	    }
	    {
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		{
		    btnPrev = new JButton("<");
		    btnPrev.setOpaque(false);
		    btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    Jukebox.prev();
			}
		    });
		    horizontalBox.add(btnPrev);
		}
		{
		    JButton btnStop = new JButton("◘");
		    btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    Jukebox.stop();
			}
		    });
		    horizontalBox.add(btnStop);
		}
		{
		    JButton btnPause = new JButton("||");
		    btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    Jukebox.pause();
			}
		    });
		    horizontalBox.add(btnPause);
		}
		{
		    lblCurrentsong = new JLabel("CurrentSong");
		    lblCurrentsong.setForeground(Color.WHITE);
		    horizontalBox.add(lblCurrentsong);
		}
		{
		    btnNext = new JButton(">");
		    btnNext.setOpaque(false);
		    btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    Jukebox.next();
			}
		    });
		    {
			JButton btnPlay = new JButton("|>");
			btnPlay.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				Jukebox.play();
			    }
			});
			horizontalBox.add(btnPlay);
		    }
		    horizontalBox.add(btnNext);
		}
	    }
	    {
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		{
		    JLabel label = new JLabel("Volume:");
		    horizontalBox.add(label);
		    label.setForeground(Color.MAGENTA);
		}
		{
		    slidMusicVolume = new JSlider();
		    slidMusicVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
			    Jukebox.setVolume(slidMusicVolume.getValue());
			}
		    });
		    slidMusicVolume.setOpaque(false);
		    horizontalBox.add(slidMusicVolume);
		}
	    }
	    {
		JButton okButton = new JButton("OK");
		verticalBox.add(okButton);
		okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (isVisible())
			    setVisible(false);
		    }
		});
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);
	    }
	}
    }

}
