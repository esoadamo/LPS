package game.aho.lps;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import aho.util.mysql2PHP2Java.MySQL2PHP2Java;
import aho.util.mysql2PHP2Java.MySQLException;

public class Vars {
    public static final boolean runningOnWindows = checkWindows();
    public static final List<JLabel> listLabelConnectionInfo = new ArrayList<JLabel>();

    public static final Color colorCoin = new Color(241, 234, 50);
    public static final Color colorLock2Win = new Color(82, 212, 106);
    public static final Color colorPatternRandom = new Color(42, 42, 42);

    public static final Random rnd = new Random();

    public static final List<NewIconListener> reloadIconsListeners = new ArrayList<NewIconListener>();

    public static final Settings settings = new Settings();
    public static final DialogOfflineGameStarter dialogOfflineGameStarter = new DialogOfflineGameStarter();
    public static final MatchMaking matchMaking = new MatchMaking();
    public static DialogCharacterCustomization dialogCharacterCustomization = null;
    public static final DialogLoginServer dialogLoginServer = new DialogLoginServer();
    public static final DialogLogin dialogLogin = new DialogLogin();
    public static final DialogWaitingForFight dialogWaitingForFight = new DialogWaitingForFight();
    public static final DialogChangePassword dialogChangePassword = new DialogChangePassword();
    public static LockPickerSimulator mainWindow;

    public static final String[] sucessQuotes = { "Good job!", "Man, you're good.", "OMG, super!", "Wow!", "Really good.", "Shut up and take my money!", "Nice one!", "Yeeeah!",
	    "Well pick", "O.. you really lock'em out" };
    public static final String[] failQuotes = { "Booo", "Looser", "Haha", "And... you failed.", "Again.", "Not suprising", "Do you have any money left?", "You're so bad.." };

    public static final String matchmakingFilterSelection = "@ST|"; //Prefix, If this string is found on beginning of matchmaking_user it is supposed to be ignored as prefix for duel asking

    public static final String dataPrefix = "data/";
    public static final String dataSoundPrefix = dataPrefix + "sound/";
    public static final String dataSoundMusic = dataSoundPrefix + "music/";
    public static final String dataSoundDubPrefix = dataSoundPrefix + "dab/";

    public static final File propertiesFile = new File(dataPrefix + "settings");
    public static final Properties properties = new Properties();
    static {
	if (!propertiesFile.exists())
	    try {
		propertiesFile.createNewFile();
	    } catch (IOException e) {
		System.err.print("Error when creating settigns file: ");
		e.printStackTrace();
	    }
	try {
	    properties.load(new FileInputStream(propertiesFile));
	} catch (IOException e) {
	    System.err.print("Error when loading settigns file: ");
	    e.printStackTrace();
	}
    }
    public static MySQL2PHP2Java conn = null;

    public static boolean fullscreen = false;

    public static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

    public static Player loggedPlayer = null;
    private static final Timer loggedPlayerOnlineAndConnectionCheckTimer = new Timer();
    private static boolean lastConnection = true;
    private static final TimerTask loggedPlayerOnlineAndConnectionCheckTimerTask = new TimerTask() {
	@Override
	public void run() {
	    if (loggedPlayer == null)
		return;
	    try {
		loggedPlayer.updateOnlineNumber();
		final String updatedString = "Updated on " + formatTime.format(new Date());
		for (JLabel lbl : listLabelConnectionInfo) {
		    if (!lastConnection) {
			lbl.setText("Connection stable");
			lbl.setForeground(Color.GREEN);
			lastConnection = true;
		    }
		    lbl.setToolTipText(updatedString);

		}
	    } catch (MySQLException e) {
		if (lastConnection) {
		    lastConnection = false;
		    final StringWriter sw = new StringWriter();
		    final PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
		    for (JLabel lbl : listLabelConnectionInfo) {
			lbl.setText("NOT connected");
			lbl.setForeground(Color.RED);
			lbl.setToolTipText("<html>" + sw.toString().replaceAll("\n", "<br>") + "</html>");
			lbl.repaint();
		    }
		}
		e.printStackTrace();
	    }
	}
    };
    static {
	/*
	 * Updates logged player's online number every 5 seconds
	 */
	loggedPlayerOnlineAndConnectionCheckTimer.schedule(loggedPlayerOnlineAndConnectionCheckTimerTask, 5 * 1000, 5 * 1000);
    }

    public static final Wav soundGoodChoice = new Wav(dataSoundPrefix + "good.wav");
    public static final Wav soundBadChoice = new Wav(dataSoundPrefix + "bad.wav");
    public static final Wav soundLockpickChoice = new Wav(dataSoundPrefix + "lockpick.wav");
    public static final Wav soundMenuSelect = new Wav(dataSoundPrefix + "menuSelect.wav");
    public static final Wav soundNotification = new Wav(dataSoundPrefix + "notification.wav");

    public static final Resolution RESOLUTION_DEFAULT = Resolution.MID;
    public static Resolution RESOLUTION_CURRENT = null;

    public static String imgDataCharacterPrefix;
    public static ImageIcon imgGeekworkSplash = new ImageIcon(Resolution.ULTRA.getPath() + "geekwork_splash_2016.png"); // Static filepath
    public static Image imgFrameIcon = new ImageIcon(Resolution.ULTRA.getPath() + "frameIcon.png").getImage(); // Static filepath
    public static ImageIcon imgCoin;
    public static ImageIcon imgLock;
    public static Image imgBackground;
    public static Image imgBackgroundWood;
    public static Image imgMatchMakingBackground;
    public static ImageIcon imgSettings;
    public static ImageIcon imgLockpick;
    public static ImageIcon imgLockpickPlatinum;
    public static ImageIcon imgCharacterBackground;
    public static final String imgCharacterIconSizeFilename = "transparent_size";
    public static ImageIcon imgCharacterIconSize;
    public static ImageIcon imgRageQuitOn;
    public static ImageIcon imgRageQuitOff;
    public static ImageIcon imgButton;
    public static ImageIcon imgButtonSelected;
    public static Image imgMainMenuBackground;
    public static Image imgMainMenuBackgroundRU;
    public static Image imgMainMenuBackgroundLU;
    public static Image imgMainMenuBackgroundLD;
    public static Image imgMainMenuBackgroundRD;
    public static ImageIcon imgFightRequest;
    public static ImageIcon imgNoSound = new ImageIcon(Resolution.ULTRA.getPath() + "no_sound.png");
    public static ImageIcon imgInternet = new ImageIcon(Resolution.ULTRA.getPath() + "iconInternet.png");

    public static void reloadIcons() {
	imgCoin = new ImageIcon(RESOLUTION_CURRENT.getPath() + "coin.png");
	imgLock = new ImageIcon(RESOLUTION_CURRENT.getPath() + "lock.png");
	imgBackground = new ImageIcon(RESOLUTION_CURRENT.getPath() + "background.png").getImage();
	imgBackgroundWood = new ImageIcon(RESOLUTION_CURRENT.getPath() + "background_wood.png").getImage();
	imgSettings = new ImageIcon(RESOLUTION_CURRENT.getPath() + "settings.png");
	imgLockpick = new ImageIcon(RESOLUTION_CURRENT.getPath() + "lockpick.png");
	imgLockpickPlatinum = new ImageIcon(RESOLUTION_CURRENT.getPath() + "lockpick_platinum.png");
	imgRageQuitOn = new ImageIcon(RESOLUTION_CURRENT.getPath() + "rage_on.png");
	imgRageQuitOff = new ImageIcon(RESOLUTION_CURRENT.getPath() + "rage_off.png");
	imgNoSound = new ImageIcon(RESOLUTION_CURRENT.getPath() + "no_sound.png");
	imgInternet = new ImageIcon(RESOLUTION_CURRENT.getPath() + "iconInternet.png");
	imgButton = new ImageIcon(RESOLUTION_CURRENT.getPath() + "button.png");
	imgButtonSelected = new ImageIcon(RESOLUTION_CURRENT.getPath() + "button_selected.png");
	imgFightRequest = new ImageIcon(RESOLUTION_CURRENT.getPath() + "fightRequest.png");
	imgMainMenuBackground = new ImageIcon(RESOLUTION_CURRENT.getPath() + "mainMenu.png").getImage();
	imgMainMenuBackgroundRU = new ImageIcon(RESOLUTION_CURRENT.getPath() + "mainMenuRU.png").getImage();
	imgMainMenuBackgroundLU = new ImageIcon(RESOLUTION_CURRENT.getPath() + "mainMenuLU.png").getImage();
	imgMainMenuBackgroundLD = new ImageIcon(RESOLUTION_CURRENT.getPath() + "mainMenuLD.png").getImage();
	imgMainMenuBackgroundRD = new ImageIcon(RESOLUTION_CURRENT.getPath() + "mainMenuRD.png").getImage();
	imgMatchMakingBackground = new ImageIcon(RESOLUTION_CURRENT.getPath() + "background_workshop.png").getImage();

	imgDataCharacterPrefix = RESOLUTION_CURRENT.getPath() + "character/";
	imgCharacterBackground = new ImageIcon(imgDataCharacterPrefix + "char_background.png");
	imgCharacterIconSize = new ImageIcon(imgDataCharacterPrefix + imgCharacterIconSizeFilename + ".png");
    }

    interface NewIconListener {
	void loadNewIcon();
    }

    /**
     * Checks if program is running on Windows OS
     * 
     * @return true if program is running on Windows OS, false otherwise
     */
    public static boolean checkWindows() {
	return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Sets new resolution and reloads all icons
     * 
     * @param resolution
     *            new resolution to use
     * @throws Exception
     */
    public static void changeResolution(Resolution resolution) throws Exception {
	if (resolution != RESOLUTION_CURRENT) {
	    if (!new File(resolution.getPath()).exists())
		throw new Exception(resolution + " does not exists at location " + resolution.getPath());
	    RESOLUTION_CURRENT = resolution;
	    imgDataCharacterPrefix = RESOLUTION_CURRENT.getPath() + "character/";
	}
	reloadIcons();
	for (NewIconListener listener : reloadIconsListeners)
	    listener.loadNewIcon();
	System.out.printf("New resolution '%s' loaded and ready%n", resolution.toString());
    }

    /**
     * Creates random non-transparent color
     * 
     * @return random non-transparent color
     */
    public static Color randomColor() {
	return new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    /**
     * Converts color to hex String
     * 
     * @param col
     *            Color to convert
     * @return hex String
     */
    public static String color2Hex(Color col) {
	return Long.toHexString(Long.parseLong(String.format("%1$03d%2$03d%3$03d", col.getRed(), col.getGreen(), col.getBlue())));
    }

    /**
     * Parses color from hex String generated in color2Hex()
     * 
     * @param s
     *            String from color2Hex()
     * @return parsed color
     */
    public static Color hex2Color(String s) {
	long number = Long.parseLong(s, 16);
	int blue = (int) (number % 1000);
	number /= 1000;
	int green = (int) (number % 1000);
	number /= 1000;
	return new Color((int) number, green, blue);
    }

    /**
     * Sets global volume and changes slider
     * 
     * @param percentage
     *            from 0 to 100 (or upper, if you want to kill your ears) how much
     */
    public static void setVolume(int percentage) {
	final int lowestDB = 50; // How far under zero can be sound heared
	int dbValue = -1 * (lowestDB - (lowestDB * percentage / 100));
	Settings.slidVolume.setMaximum(100);
	Settings.slidVolume.setValue(percentage);
	Wav.setGlobalVolume(dbValue);
    }

    /**
     * Generates new random long greater than 0
     * 
     * @param bound
     *            maximum value
     * @return random long within bounds
     */
    public static long nextRandomLong(long bound) {
	long bits, val;
	do {
	    bits = (rnd.nextLong() << 1) >>> 1;
	    val = bits % bound;
	} while (bits - val + (bound - 1) < 0L);
	return val;
    }

    /**
     * Saves properties to its file
     */
    public static void saveSettings() {
	try {
	    Vars.properties.store(new FileOutputStream(Vars.propertiesFile), null);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Makes JFrame extended and undecorated
     * 
     * @param frame
     *            frame to change
     */
    public static void makeFullscreen(final JFrame frame) {
	if (Vars.fullscreen) {
	    frame.setUndecorated(Vars.fullscreen);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
    }

    /**
     * Checks if generating textures is requied
     * 
     * @return true if count of files in pixel art folder is lower than count in 4K folder
     */
    protected static boolean generateTexturesRequied() {
	class FileCounter {
	    public int count = 0;

	    /**
	     * Counts all files inside directories (also inside sub directories)
	     * 
	     * @param dir
	     *            starting directory
	     */
	    public void countFiles(File dir) {
		if (!dir.exists())
		    return;
		for (File f : dir.listFiles()) {
		    count++;
		    if (f.isDirectory())
			countFiles(f);
		}
	    }
	}
	FileCounter res4K = new FileCounter();
	FileCounter resPixelArt = new FileCounter();
	res4K.countFiles(new File(Resolution.ULTRA.getPath()));
	resPixelArt.countFiles(new File(Resolution.PIXELART.getPath()));

	return (res4K.count > resPixelArt.count);
    }

    /**
     * Generates all textures from 4K to all qualities
     */
    protected static void generateTextures() {
	File resourcesDir = new File(Vars.dataPrefix + Resolution.ULTRA);
	generateTexturesDir(resourcesDir, Resolution.HIGH);
	generateTexturesDir(resourcesDir, Resolution.MID);
	generateTexturesDir(resourcesDir, Resolution.LOW);
	generateTexturesDir(resourcesDir, Resolution.MINIMAL);
	generateTexturesDir(resourcesDir, Resolution.SUPEROLD);
	generateTexturesDir(resourcesDir, Resolution.POTATO);
	generateTexturesDir(resourcesDir, Resolution.PIXELART);

	try {
	    changeResolution(RESOLUTION_CURRENT);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Generates all textures from 4K to specified directory and resolution
     * 
     * @param directory
     *            is directory where listing starts
     */
    private static void generateTexturesDir(File directory, Resolution resolution) {
	if (resolution == Resolution.ULTRA)
	    return;
	for (File file : directory.listFiles()) {
	    if (file.isDirectory())
		generateTexturesDir(file, resolution);
	    ImageIcon loadedFile = new ImageIcon(file.getAbsolutePath());
	    if (loadedFile.getIconHeight() < 1)
		continue;
	    if (resolution == (Resolution.HIGH))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 2, loadedFile.getIconHeight() / 2);
	    else if (resolution == (Resolution.MID))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 4, loadedFile.getIconHeight() / 4);
	    else if (resolution == (Resolution.LOW))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 7, loadedFile.getIconHeight() / 7);
	    else if (resolution == (Resolution.MINIMAL))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 10, loadedFile.getIconHeight() / 10);
	    else if (resolution == (Resolution.SUPEROLD))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 100, loadedFile.getIconHeight() / 100);
	    else if (resolution == (Resolution.POTATO))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 200, loadedFile.getIconHeight() / 200);
	    else if (resolution == (Resolution.PIXELART))
		loadedFile = TheGame.scaleImage(loadedFile, loadedFile.getIconWidth() / 500, loadedFile.getIconHeight() / 500);
	    try {
		File newImageFile;
		if (Vars.runningOnWindows)
		    newImageFile = new File(file.getAbsolutePath().toLowerCase().replace(Resolution.ULTRA.getDirectoryName(), resolution.getDirectoryName()));
		else
		    newImageFile = new File(file.getAbsolutePath().replace(Resolution.ULTRA.getDirectoryName(), resolution.getDirectoryName()));

		System.out.println("Saving " + file.getAbsolutePath() + " to " + newImageFile.getAbsolutePath());
		String[] dirs = newImageFile.getAbsolutePath().replaceAll("\\\\", "/").split("/");
		String currDirPath = "";
		for (int i = 0; i < dirs.length - 1; i++) {
		    currDirPath += dirs[i] + "/";
		    File currDir;
		    if (!(currDir = new File(currDirPath)).exists()) {
			System.out.println("Creating directory " + currDir.getAbsolutePath());
			currDir.mkdir();
		    }
		}
		ImageIO.write((RenderedImage) loadedFile.getImage(), "PNG", newImageFile);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	System.out.println(directory.getAbsolutePath() + " done");
    }
}
