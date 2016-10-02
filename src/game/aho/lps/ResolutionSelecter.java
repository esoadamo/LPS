package game.aho.lps;

import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JCheckBox;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ResolutionSelecter {

    protected JFrame frmSelectResolution;
    protected static int secLeft = 6;
    private JLabel lblTimeleft;
    private Timer timer;
    private JButton btnStart;
    protected static JComboBox<Resolution> comboResolution = new JComboBox<Resolution>();
    protected static boolean autostart = false;
    protected static JCheckBox chckbxFullscreen = new JCheckBox("Fullscreen");
    private JButton btnMinigame;
    static {
	comboResolution
		.setModel(new DefaultComboBoxModel<Resolution>(Resolution.values()));
	comboResolution.setSelectedIndex(2);
	comboResolution.setOpaque(false);
    }

    /**
     * Create the application.
     */
    public ResolutionSelecter() {
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	frmSelectResolution = new JFrame();
	frmSelectResolution.getContentPane().setBackground(Color.WHITE);
	frmSelectResolution.setTitle("Select resolution");
	frmSelectResolution.setIconImage(Vars.imgFrameIcon);
	frmSelectResolution.setResizable(false);
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	final int w = 810;
	final int h = 540;
	frmSelectResolution.setBounds((gd.getDisplayMode().getWidth() / 2) - (w / 2), (gd.getDisplayMode().getHeight() / 2) - (h / 2), w, h);
	frmSelectResolution.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JLabel lblIcon = new JLabel();
	lblIcon.setIcon(Vars.imgGeekworkSplash);
	frmSelectResolution.getContentPane().add(lblIcon, BorderLayout.CENTER);

	Box horizontalBox = Box.createHorizontalBox();
	frmSelectResolution.getContentPane().add(horizontalBox, BorderLayout.SOUTH);

	lblTimeleft = new JLabel();
	updateText();
	horizontalBox.add(lblTimeleft);

	Component horizontalGlue = Box.createHorizontalGlue();
	horizontalBox.add(horizontalGlue);

	JLabel lblTextureQuality = new JLabel("Texture quality:  ");
	horizontalBox.add(lblTextureQuality);

	horizontalBox.add(comboResolution);

	Component horizontalGlue_2 = Box.createHorizontalGlue();
	horizontalBox.add(horizontalGlue_2);

	chckbxFullscreen.setOpaque(false);
	horizontalBox.add(chckbxFullscreen);

	Component horizontalGlue_1 = Box.createHorizontalGlue();
	horizontalBox.add(horizontalGlue_1);

	btnStart = new JButton("Play");
	btnStart.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent arg0) {
		start();
	    }
	});
	
	btnMinigame = new JButton("Minigame!");
	btnMinigame.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
		    try {
			java.awt.Desktop.getDesktop().browse(new URI("https://ubuntusevr.hukot.net/lpsgm/"));
		    } catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		    }
		}
	});
	btnMinigame.setVisible(LockPickerSimulator.pArgs.argExist("--web-browser"));
	horizontalBox.add(btnMinigame);
	btnStart.setEnabled(false);
	horizontalBox.add(btnStart);

	if (Vars.generateTexturesRequied()) {
	    new Timer().schedule(new TimerTask() {

		@Override
		public void run() {
		    lblTimeleft.setText("Generating textures.. this could take a while");
		    Vars.generateTextures();
		    start();
		}

	    }, 1000);

	} else {
	    (timer = new Timer()).schedule(new TimerTask() {

		@Override
		public void run() {
		    secLeft--;
		    if (secLeft >= 0)
			updateText();
		    else {
			btnStart.setBackground(Vars.randomColor());
			btnStart.setForeground(Vars.randomColor());
			btnStart.setFont(new Font("Tahoma", Font.ITALIC, Vars.rnd.nextInt(40) + 10));
		    }
		}

	    }, 1000, 1000);
	}
    }

    /**
     * Updates time left text
     */
    private synchronized void updateText() {
	if (secLeft != 1)
	    lblTimeleft.setText(String.format("Please take look on this logo for %d more seconds", secLeft));
	if (secLeft == 0) {
	    if (autostart)
		start();
	    else {
		lblTimeleft.setText("Press 'Play' button");
		btnStart.setEnabled(true);
	    }

	} else
	    lblTimeleft.setText(String.format("Please take a look on this logo for %d second", secLeft));
    }

    /**
     * Runs main window, loads new resolution
     */
    private void start() {
	Vars.fullscreen = chckbxFullscreen.isSelected();
	btnStart.setEnabled(false);
	comboResolution.setEnabled(false);
	chckbxFullscreen.setEnabled(false);
	lblTimeleft.setText("Loading new textures...");
	if ((comboResolution.getItemCount() > 1) && comboResolution.isEnabled()) {
	    Resolution newResolution = (Resolution) comboResolution.getSelectedItem();
	    
	    try {
		Vars.changeResolution(newResolution);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if(!newResolution.getIndex().toString().contentEquals(Vars.properties.getProperty("resolution", "nothingSet"))){
		Vars.properties.setProperty("resolution", newResolution.getIndex().toString());
		Vars.saveSettings();
	    }
	}
	if (timer != null)
	    timer.cancel();
	frmSelectResolution.setVisible(false);
	Vars.mainWindow.frame.setVisible(true);
    }

}
