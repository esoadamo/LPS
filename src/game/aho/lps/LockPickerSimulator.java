package game.aho.lps;

import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import aho.util.ProgramArguments;
import aho.util.mysql2PHP2Java.MySQL2PHP2Java;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;

public class LockPickerSimulator {

    protected JFrame frame;
    protected static ProgramArguments pArgs;
    /**
     * Launch the application.
     * 
     */
    public static void main(String[] args) {
	pArgs = new ProgramArguments(args);
	if (pArgs.argExist("--web-browser")) {
	    Wav.soundEnabled = false;
	    new NotificationMessage(Vars.imgInternet, "You are running this game from web server, that means lower performace and no sound.<br>Oh.. and minigame!", 15000, new NotificationMessage.Action() {
	        
	        @Override
	        public void action() {
	            try {
			java.awt.Desktop.getDesktop().browse(new URI("https://ubuntusevr.hukot.net/lpsgm/"));
		    } catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		    }
	        }
	    });
	} else if (pArgs.argExist("--no-sound")) {
	    Wav.soundEnabled = false;
	    new NotificationMessage(Vars.imgNoSound, "Sound is disabled", 15000, null);
	}
	if (pArgs.argExist("--no-countdown"))
	    ResolutionSelecter.secLeft = 1;
	boolean resolutionChanged = false;
	if (pArgs.argExist("--resolution")) {
	    String arg = pArgs.getArgValue("--resolution");
	    ResolutionSelecter.comboResolution.setEnabled(false);
	    ResolutionSelecter.autostart = true;
	    Settings.comboResolution.setEnabled(false);
	    try {
		Vars.changeResolution(Resolution.fromIndex(Integer.parseInt(arg)));
		resolutionChanged = true;
	    } catch (Exception e) {

	    }
	}
	if (!resolutionChanged) {
	    try {
		int resolutionIndex = Integer.parseInt(Vars.properties.getProperty("resolution", "-1"));
		Resolution r;
		if (resolutionIndex == -1 || ((r = Resolution.fromIndex(resolutionIndex)) == null))
		    Vars.changeResolution(Vars.RESOLUTION_DEFAULT);
		else
		    ResolutionSelecter.comboResolution.setSelectedIndex(r.getIndex());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	if (pArgs.argExist("--fullscreen") || pArgs.argExist("--no-fullscreen")) {
	    ResolutionSelecter.chckbxFullscreen.setEnabled(false);
	    ResolutionSelecter.chckbxFullscreen.setSelected(Vars.fullscreen = pArgs.argExist("--fullscreen"));
	}
	final File updateFile = new File("LPS.jar_update");
	if (Vars.conn != null) {
	    try {
		Double currentVersion = Double.parseDouble(Vars.properties.getProperty("version", "0"));
		Double newestVersion = Vars.conn.executeSQL("SELECT version FROM download_files WHERE filename = 'LPS.jar'").getDouble("version");
		if (newestVersion > currentVersion) {
		    JOptionPane.showMessageDialog(null, "A new version of program will be downloaded", "New version avaible", JOptionPane.INFORMATION_MESSAGE);
		    Files.write(updateFile.toPath(), Vars.conn.executeSQL("SELECT file FROM download_files WHERE filename = 'LPS.jar'").getRawData("file").toByteArray(),
			    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		    System.out.println("New version" + newestVersion);
		    Vars.properties.setProperty("version", newestVersion.toString());
		    Vars.saveSettings();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	if (updateFile.exists()) {
	    JOptionPane.showMessageDialog(null, "A new version of program was downloaded\nPlease run update.bat", "New version download complete", JOptionPane.INFORMATION_MESSAGE);
	    System.exit(2);
	}
	Vars.setVolume(90);
	Jukebox.setVolume(70);
	Vars.mainWindow = new LockPickerSimulator();
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    ResolutionSelecter rs = new ResolutionSelecter();
		    rs.frmSelectResolution.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public LockPickerSimulator() {
	initialize();
    }

    private static final int SELECTED_RU = 1;
    private static final int SELECTED_LU = 0;
    private static final int SELECTED_RD = 3;
    private static final int SELECTED_LD = 2;
    private static final int SELECTED_NONE = -1;
    private int selectedBtn = SELECTED_NONE;
    private JLabel btnLU = new JLabel("Btn1");
    private JLabel btnRU = new JLabel("Btn2");
    private JLabel btnLD = new JLabel("Btn3");
    private JLabel btnRD = new JLabel("Btn4");

    private interface ButtonAction {
	public void makeItRock();
    }

    private final ButtonAction[] actions = new ButtonAction[4];
    private final JLabel[] buttons = new JLabel[4];
    protected Object[] bntsDataOnlineGame;
    private Object[] btnsDataStart;

    /**
     * Initialize the contents of the frame.
     */
    @SuppressWarnings("serial")
    private void initialize() {
	frame = new JFrame();
	frame.setTitle("The Ultimate Simulator of Picking Locks");
	frame.addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent arg0) {
		updateUI();
	    }
	});
	frame.setBounds(100, 100, 778, 742);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	Vars.makeFullscreen(frame);
	Vars.makeFullscreen(Vars.matchMaking);

	frame.setIconImage(Vars.imgFrameIcon);
	Vars.matchMaking.setIconImage(Vars.imgFrameIcon);
	Vars.dialogWaitingForFight.setIconImage(Vars.imgFrameIcon);
	Vars.dialogLogin.setIconImage(Vars.imgFrameIcon);
	Vars.dialogLoginServer.setIconImage(Vars.imgFrameIcon);

	final JPanel contentPane = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (selectedBtn == SELECTED_RU)
		    g.drawImage(Vars.imgMainMenuBackgroundRU, 0, 0, getWidth(), getHeight(), null);
		else if (selectedBtn == SELECTED_LU)
		    g.drawImage(Vars.imgMainMenuBackgroundLU, 0, 0, getWidth(), getHeight(), null);
		else if (selectedBtn == SELECTED_RD)
		    g.drawImage(Vars.imgMainMenuBackgroundRD, 0, 0, getWidth(), getHeight(), null);
		else if (selectedBtn == SELECTED_LD)
		    g.drawImage(Vars.imgMainMenuBackgroundLD, 0, 0, getWidth(), getHeight(), null);
		else
		    g.drawImage(Vars.imgMainMenuBackground, 0, 0, getWidth(), getHeight(), null);
	    }
	};
	contentPane.setBackground(new Color(255, 255, 255));
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	frame.setContentPane(contentPane);
	contentPane.setLayout(null);

	btnLU.setForeground(Color.WHITE);
	btnLU.setFont(new Font("Dialog", Font.PLAIN, 20));
	btnLU.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Vars.randomColor()));
	btnLU.setHorizontalAlignment(SwingConstants.CENTER);
	btnLU.setBounds(0, 0, 385, 350);
	btnLU.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseEntered(MouseEvent arg0) {
		changeSelectedButton(SELECTED_LU);
	    }
	});

	btnRU.setForeground(Color.WHITE);
	btnRU.setHorizontalAlignment(SwingConstants.CENTER);
	btnRU.setFont(new Font("Dialog", Font.PLAIN, 20));
	btnRU.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Vars.randomColor()));
	btnRU.setBounds(383, 0, 385, 350);
	btnRU.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseEntered(MouseEvent arg0) {
		changeSelectedButton(SELECTED_RU);
	    }
	});

	btnLD.setForeground(Color.WHITE);
	btnLD.setHorizontalAlignment(SwingConstants.CENTER);
	btnLD.setFont(new Font("Dialog", Font.PLAIN, 20));
	btnLD.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Vars.randomColor()));
	btnLD.setBounds(0, 351, 385, 350);
	btnLD.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseEntered(MouseEvent arg0) {
		changeSelectedButton(SELECTED_LD);
	    }
	});

	btnRD.setForeground(Color.WHITE);
	btnRD.setHorizontalAlignment(SwingConstants.CENTER);
	btnRD.setFont(new Font("Dialog", Font.PLAIN, 20));
	btnRD.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Vars.randomColor()));
	btnRD.setBounds(383, 351, 385, 350);
	btnRD.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseEntered(MouseEvent arg0) {
		changeSelectedButton(SELECTED_RD);
	    }
	});

	Vars.reloadIconsListeners.add(new Vars.NewIconListener() {
	    @Override
	    public void loadNewIcon() {
		contentPane.repaint();
	    }
	});
	buttons[0] = btnLU;
	buttons[1] = btnRU;
	buttons[2] = btnLD;
	buttons[3] = btnRD;
	for (final JLabel btn : buttons) {
	    btn.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent arg0) {
		    if (!enabled())
			return;
		    actions[selectedBtn].makeItRock();
		}
	    });
	    frame.getContentPane().add(btn);
	}

	btnsDataStart = createButtonsData("Online game", "Offline game", "Settings", "Exit", new ButtonAction() {

	    @Override
	    public void makeItRock() {
		if (Vars.loggedPlayer != null) {
		    changeButtons(Vars.mainWindow.bntsDataOnlineGame);
		    return;
		}
		final String server = Vars.properties.getProperty("server", null);
		final String serverPassword = Vars.properties.getProperty("serverPassword", null);
		final String serverAccount = Vars.properties.getProperty("serverAccount", null);
		final String serverAccountPassword = Vars.properties.getProperty("serverAccountPassword", null);

		/*
		 * Checks if all data are saved. If some of data is not found in settings, ask for login
		 */
		if (server == null || serverPassword == null || serverAccount == null || serverAccountPassword == null) {
		    Vars.dialogLoginServer.setLocationRelativeTo(Vars.mainWindow.frame);
		    Vars.dialogLoginServer.setVisible(true);
		    if (LockPickerSimulator.pArgs.argExist("--web-browser")) {
			if (Vars.dialogLoginServer.testConnection())
			    Vars.dialogLoginServer.login();
		    }
		} else {
		    Vars.conn = new MySQL2PHP2Java(server, serverPassword);
		    Vars.conn.setLogin(serverAccount, serverAccountPassword);
		    Vars.conn.setCache(MySQL2PHP2Java.CACHE_DISABLED);
		    Vars.conn.setAllowBase64(true);
		    if (!Vars.conn.testConn()) {
			JOptionPane.showMessageDialog(frame, "Connection to the server failed", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		    }
		    Vars.dialogLogin.setLocationRelativeTo(Vars.mainWindow.frame);
		    Vars.dialogLogin.setVisible(true);
		}
	    }

	}, new ButtonAction() {

	    @Override
	    public void makeItRock() {
		Vars.dialogOfflineGameStarter.setLocationRelativeTo(Vars.mainWindow.frame);
		Vars.dialogOfflineGameStarter.setVisible(true);
	    }

	}, new ButtonAction() {

	    @Override
	    public void makeItRock() {
		Vars.settings.setLocationRelativeTo(Vars.mainWindow.frame);
		Vars.settings.setVisible(true);
	    }

	}, new ButtonAction() {

	    @Override
	    public void makeItRock() {
		System.exit(0);
	    }

	});

	final ButtonAction backToMainMenu = new ButtonAction() {

	    @Override
	    public void makeItRock() {
		changeButtons(btnsDataStart);
	    }

	};

	bntsDataOnlineGame = createButtonsData("Play PvP", "Missions", "Dress yourself", "Main Menu", new ButtonAction() {

	    @Override
	    public void makeItRock() {
		Vars.matchMaking.setVisible(true);
		Vars.matchMaking.setLocationRelativeTo(Vars.mainWindow.frame);
		if (!Vars.fullscreen)
		    Vars.matchMaking.setBounds(Vars.mainWindow.frame.getX(), Vars.mainWindow.frame.getY(), Vars.mainWindow.frame.getWidth(), Vars.mainWindow.frame.getHeight());
		Vars.matchMaking.toFront();
		frame.setVisible(false);
	    }

	}, new ButtonAction() {

	    @Override
	    public void makeItRock() {
		JOptionPane.showMessageDialog(frame, "No mission found", "Sorry...", JOptionPane.INFORMATION_MESSAGE);
	    }
	}, new ButtonAction() {

	    @Override
	    public void makeItRock() {
		Vars.dialogCharacterCustomization = new DialogCharacterCustomization(Vars.loggedPlayer);
		Vars.dialogCharacterCustomization.setLocationRelativeTo(Vars.mainWindow.frame);
		Vars.dialogCharacterCustomization.setVisible(true);
	    }

	}, backToMainMenu);

	changeButtons(btnsDataStart);

	updateUI();
    }

    /**
     * Checks if all of dialog windows are not visible, so user can operate directly with this window
     * 
     * @return true if any of dialog window is not visible, false otherwise
     */
    private boolean enabled() {
	if (Vars.dialogCharacterCustomization != null && Vars.dialogCharacterCustomization.isVisible()) {
	    Vars.dialogCharacterCustomization.toFront();
	    return false;
	}
	return true;
    }

    /**
     * Changes buttons text and actions
     * 
     * @param data
     *            is length 8 array
     *            [0] is text of btn left up (String)
     *            [1] is text of btn right up (String)
     *            [2] is text of btn left down (String)
     *            [3] is text of btn right down (String)
     *            [4] is action of btn left up (ButtonAction)
     *            [5] is action of btn right up (ButtonAction)
     *            [6] is action of btn left down (ButtonAction)
     *            [7] is action of btn right down (ButtonAction)
     */
    protected void changeButtons(Object[] data) {
	buttons[0].setText((String) data[0]);
	buttons[1].setText((String) data[1]);
	buttons[2].setText((String) data[2]);
	buttons[3].setText((String) data[3]);

	actions[0] = (ButtonAction) data[4];
	actions[1] = (ButtonAction) data[5];
	actions[2] = (ButtonAction) data[6];
	actions[3] = (ButtonAction) data[7];
    }

    /**
     * Generates Object array used in function changeButtons()
     * 
     * @param lu
     *            text of btn left up
     * @param ru
     *            text of btn right up
     * @param ld
     *            text of btn left down
     * @param rd
     *            text of btn right down
     * @param lua
     *            action of btn left up
     * @param rua
     *            action of btn right up
     * @param lda
     *            action of btn left down
     * @param rda
     *            action of btn right down
     * @return Object array that can be used in function changeButtons()
     */
    private Object[] createButtonsData(final String lu, final String ru, final String ld, final String rd, final ButtonAction lua, final ButtonAction rua, final ButtonAction lda,
	    final ButtonAction rda) {
	final Object[] returnData = { lu, ru, ld, rd, lua, rua, lda, rda };
	return returnData;
    }

    /**
     * Updates interface
     */
    private void updateUI() {
	if (!enabled())
	    return;
	final int borderSize = frame.getHeight() / 640;
	final int fontSize = frame.getHeight() / 38;
	final int fontSizeSelected = fontSize * 2;
	final int borderSizeSelected = borderSize * 4;
	final int frameHalfWidth = frame.getWidth() / 2;
	final int frameHalfHeight = frame.getHeight() / 2;
	btnLU.setBorder(null);
	btnRU.setBorder(null);
	btnLD.setBorder(null);
	btnRD.setBorder(null);

	btnLU.setFont(new Font("Dialog", Font.PLAIN, fontSize));
	btnRU.setFont(new Font("Dialog", Font.PLAIN, fontSize));
	btnLD.setFont(new Font("Dialog", Font.PLAIN, fontSize));
	btnRD.setFont(new Font("Dialog", Font.PLAIN, fontSize));

	if (LockPickerSimulator.pArgs.argExist("--web-browser")) {
	    btnRD.setForeground(Color.BLACK);
	    btnLD.setForeground(Color.BLACK);
	    btnRU.setForeground(Color.BLACK);
	    btnLU.setForeground(Color.BLACK);
	} else {
	    btnRD.setForeground(Color.WHITE);
	    btnLD.setForeground(Color.WHITE);
	    btnRU.setForeground(Color.WHITE);
	    btnLU.setForeground(Color.WHITE);
	}

	btnLU.setBounds(0, 0, frameHalfWidth, frameHalfHeight);
	btnRU.setBounds(frameHalfWidth, 0, frameHalfWidth, frameHalfHeight);
	btnLD.setBounds(0, frameHalfHeight, frameHalfWidth, frameHalfHeight);
	btnRD.setBounds(frameHalfWidth, frameHalfHeight, frameHalfWidth, frameHalfHeight);

	if (selectedBtn == SELECTED_RU) {
	    btnRU.setBorder(BorderFactory.createMatteBorder(borderSizeSelected, borderSizeSelected, borderSizeSelected, borderSizeSelected, Vars.randomColor()));
	    btnRU.setFont(new Font("Dialog", Font.BOLD, fontSizeSelected));
	    btnRU.setForeground(Vars.randomColor());
	} else if (selectedBtn == SELECTED_LU) {
	    btnLU.setBorder(BorderFactory.createMatteBorder(borderSizeSelected, borderSizeSelected, borderSizeSelected, borderSizeSelected, Vars.randomColor()));
	    btnLU.setFont(new Font("Dialog", Font.BOLD, fontSizeSelected));
	    btnLU.setForeground(Vars.randomColor());
	} else if (selectedBtn == SELECTED_RD) {
	    btnRD.setBorder(BorderFactory.createMatteBorder(borderSizeSelected, borderSizeSelected, borderSizeSelected, borderSizeSelected, Vars.randomColor()));
	    btnRD.setFont(new Font("Dialog", Font.BOLD, fontSizeSelected));
	    btnRD.setForeground(Vars.randomColor());
	} else if (selectedBtn == SELECTED_LD) {
	    btnLD.setBorder(BorderFactory.createMatteBorder(borderSizeSelected, borderSizeSelected, borderSizeSelected, borderSizeSelected, Vars.randomColor()));
	    btnLD.setFont(new Font("Dialog", Font.BOLD, fontSizeSelected));
	    btnLD.setForeground(Vars.randomColor());
	}
	frame.getContentPane().repaint();
    }

    /**
     * Changes selected button, updates interface
     * 
     * @param selectedButton
     *            new id of selected button
     */
    private synchronized void changeSelectedButton(final int selectedButton) {
	Vars.soundMenuSelect.play();
	selectedBtn = selectedButton;
	updateUI();
    }
}
