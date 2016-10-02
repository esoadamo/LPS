package game.aho.lps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import game.aho.lps.Wav.PlayingCompletedListner;

import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class TheGame extends JFrame {

    public static final int LOCKPICK_NONE = 0;
    public static final int LOCKPICK_NORMAL = 1;
    public static final int LOCKPICK_PLATINUM = 2;

    private final int defaultWidth = 1120;
    private final int defaultHeight = 825;
    private final int difficulty;

    private int enemySelectedLock = -1;
    private int userSelectedLock = -1;
    private int selectedLockpick = LOCKPICK_NONE;

    protected int lockpicksNormalLeft = 8;
    protected int lockpicksPlatinumLeft = 2;

    private final GameMode gamemode;

    private boolean selectionEnabled = false;

    private int level = 1;
    private int successGuesess = 0; // How many selections were successful, used for detecting cheating
    private int cheatsDetected = 0; // How many times detecting cheats returned true
    protected AtomicLong cashPlayer = new AtomicLong(5000);
    protected AtomicLong cashEnemy = new AtomicLong(5000);
    private Boolean playerIsInTheLead = null; // Null means the both have same cash
    private final Timer checkLeadTimer = new Timer();
    private final TimerTask checkLeadTimerTask = new TimerTask() {

	@Override
	public void run() {
	    if (playerIsInTheLead == null || playerIsInTheLead) {
		if ((target == null && (cashPlayer.get() < cashEnemy.get())) || (target != null && (Math.abs(cashPlayer.get() - target) > Math.abs(cashEnemy.get() - target)))) {
		    playDub("Enemy is in the lead");
		    playerIsInTheLead = false;
		    updateUI();
		}
	    }
	    if (playerIsInTheLead == null || !playerIsInTheLead) {
		if ((cashPlayer.get() > cashEnemy.get() && target == null) || (target != null && (Math.abs(cashPlayer.get() - target) < Math.abs(cashEnemy.get() - target)))) {
		    playDub("You are in the lead");
		    playerIsInTheLead = true;
		    updateUI();
		}
	    }
	    if ((target == null && cashPlayer.get() == cashEnemy.get()) || (target != null && (Math.abs(cashPlayer.get() - target) == Math.abs(cashEnemy.get() - target))))
		playerIsInTheLead = null;
	}

    };
    private Long target = null;

    private JPanel contentPane;

    private List<Lock> locks = new ArrayList<Lock>();
    private JLabel lblLevel;
    private Box hbLocks;
    private JLabel lblInfo;
    private JLabel lblCashPlayer;

    private final Player player1;
    private final Player player2;

    private Wav dubbing = new Wav();

    public void playDub(final String string2say) {
	dubbing.stop();
	lblInfo.setText(string2say);
	lblInfo.setVisible(true);
	lblInfo.requestFocus();
	File soundFile = new File(Vars.dataSoundDubPrefix
		+ string2say.toLowerCase().replaceAll(" ", "_").replaceAll("!", "").replaceAll("\\.", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("'", "") + ".wav");
	if (soundFile.exists() && Wav.soundEnabled) {
	    dubbing.fileToPlay = soundFile;
	    dubbing.play();
	} else {
	    new Thread() {
		public void run() {
		    try {
			Thread.sleep(string2say.length() * 100);
		    } catch (InterruptedException e) {
		    }
		    lblInfo.setVisible(false);
		}
	    }.start();
	}
    }

    private class Lock extends JLabel {
	private ImageIcon iconCurrent = changeColorByPatternToRandom(Vars.imgLock);
	public int defaultSize = 350;// For difficulty 3
	public long price;
	public boolean showResult = false; // Are we showing results (Colors like GREEN and RED...)
	public boolean lockpickUsedOnThis = false;
	private final int listID;

	public Lock() {
	    super();
	    defaultSize = ((int) ((double) defaultWidth * 0.9 / difficulty));
	    listID = locks.size();
	    setVerticalTextPosition(JLabel.BOTTOM);
	    setHorizontalTextPosition(JLabel.CENTER);
	    newLock();
	    Vars.reloadIconsListeners.add(new Vars.NewIconListener() {

		@Override
		public void loadNewIcon() {
		    Color currColor = getColor();
		    iconCurrent = changeNonAlphaColor(Vars.imgLock, currColor);
		    repaint();
		}
	    });
	    addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent arg0) {
		    if (showResult || !selectionEnabled)
			return;
		    userSelectedLock = listID;
		    if (player1 != null)
			player1.setSelectedLock(userSelectedLock);
		    if (player2 == null)
			enemySelectedLock = new Random().nextInt(difficulty);
		    else
			enemySelectedLock = -1;
		    if (selectedLockpick != LOCKPICK_NONE)
			lockpickUsedOnThis = true;
		    for (Lock lock : locks)
			lock.showResult = true;

		    /*
		     * Checks until enemy selects
		     */
		    final Timer enemySelectTimer;
		    (enemySelectTimer = new Timer()).schedule(new TimerTask() {

			@Override
			public void run() {
			    if (player1 != null && player2 != null) {
				if (!player1.isInFightWith(player2) || !player2.isOnline()) { //Enemy has left fight
				    player1.exitGame();
				    playDub("You won!");
				    selectionEnabled = false;
				    Jukebox.pause();
				    return;
				}
				if (level == player2.getLevel()) {
				    enemySelectedLock = player2.getSelectedLock();
				}
			    }
			    if (enemySelectedLock == -1)
				return;
			    enemySelectTimer.cancel();
			    long cashChange = 0;
			    if (selectedLockpick == LOCKPICK_NONE) {
				if (enemySelectedLock == listID && !cheatDetected()) {
				    successGuesess++;
				    Vars.soundGoodChoice.play();
				    playDub(Vars.sucessQuotes[Vars.rnd.nextInt(Vars.sucessQuotes.length)]);

				    if (difficulty == 1)
					cashChange = 1;
				    else if (difficulty == 2)
					cashChange = price / 2;
				    else
					cashChange = price * difficulty / 3;
				} else {
				    Vars.soundBadChoice.play();
				    playDub(Vars.failQuotes[Vars.rnd.nextInt(Vars.failQuotes.length)]);
				    cashChange = -1 * price / 3;
				}
			    } else {
				Vars.soundLockpickChoice.play();
				if (selectedLockpick == LOCKPICK_NORMAL) {
				    playDub(Vars.failQuotes[Vars.rnd.nextInt(Vars.failQuotes.length)]);
				    lockpicksNormalLeft--;
				    lblLockpickNormal.setText("x" + lockpicksNormalLeft);
				    lblLockpickNormal.setIcon(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()));

				    if (player1 != null)
					player1.setLockpicksNormal(lockpicksNormalLeft);
				} else {
				    playDub(Vars.sucessQuotes[Vars.rnd.nextInt(Vars.sucessQuotes.length)]);
				    cashChange = price / 3;
				    lockpicksPlatinumLeft--;
				    lblLockpickPlatinum.setText("x" + lockpicksPlatinumLeft);
				    lblLockpickPlatinum.setIcon(
					    scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()));
				    if (player1 != null)
					player1.setLockpicksPlatinum(lockpicksPlatinumLeft);
				}
				selectedLockpick = LOCKPICK_NONE;
			    }

			    cashPlayer.getAndAdd(cashChange);
			    if (player1 != null)
				player1.setCash(cashPlayer.get());

			    animatePlayerCash();

			    new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
				    if (player2 == null) {
					if (enemySelectedLock == listID)
					    cashEnemy.getAndAdd(getNewPrice(cashEnemy.get(), false));
					else
					    cashEnemy.getAndAdd(-1 * getNewPrice(cashEnemy.get(), false) / 3);
					System.out.println(cashEnemy.get());
				    } else
					cashEnemy.set(player2.getCash());

				    if (target != null && cashEnemy.get() == target) { //Enemy won
					if (player1 != null)
					    player1.exitGame();
					Jukebox.pause();
					playDub("The other1 won!");
					selectionEnabled = false;
					return;
				    }

				    countTarget();

				    level++;
				    if (player1 != null) {
					player1.setSelectedLock(-1);
					player1.setLevel(level);
				    }
				    lblLevel.setText("Level " + level);
				    for (Lock lock : locks)
					lock.newLock();
				    hbLocks.repaint();
				}

			    }, 1250);

			}

		    }, 0, 500);

		}

		/**
		 * Checks if user has not suspiciously high number of success
		 * Disabled on difficulty 1
		 */
		private boolean cheatDetected() {
		    if (difficulty == 1)
			return false;
		    int sucessRate = (100 * successGuesess) / level; // Percents how many of guesses were successful
		    final int basicDetecting = 35 - (5 * cheatsDetected);
		    final int randomPool = 50 - (2 * cheatsDetected);
		    if (sucessRate > (Vars.rnd.nextInt(randomPool) + basicDetecting)) {
			cheatsDetected++;
			System.out.printf("Cheating #%d detected, minimum thread detection was on %d, maximum %d%n", cheatsDetected, basicDetecting, basicDetecting + randomPool);
			enemySelectedLock++;
			if (enemySelectedLock >= locks.size())
			    enemySelectedLock = 0;
			return true;
		    }
		    return false;
		}
	    });
	}

	/**
	 * Finds out current color of lock
	 * 
	 * @return current color of lock
	 */
	public Color getColor() {
	    BufferedImage bimg = convertToBufferedImage(iconCurrent.getImage());
	    Graphics bg = bimg.getGraphics();
	    bg.drawImage(bimg, 0, 0, null);
	    bg.dispose();

	    for (int x = 0; x < bimg.getWidth(); x++) {
		for (int y = 0; y < bimg.getHeight(); y++) {
		    int rgba = bimg.getRGB(x, y);
		    Color col = new Color(rgba, true);
		    if (col.getAlpha() != 0)
			return col;
		}
	    }

	    return null;
	}

	/**
	 * Changes locks colors, removes selection and selects new price
	 */
	public void newLock() {
	    showResult = false;
	    lockpickUsedOnThis = false;
	    iconCurrent = changeColorByPatternToRandom(changeColorByPatternToRandom(Vars.imgLock));
	    setForeground(Vars.colorCoin);

	    price = getNewPrice(cashPlayer.get(), true);

	    setText("$" + price);
	}

	private long getNewPrice(long cash, final boolean changeColor) {
	    long price;
	    if (difficulty != 1) {
		price = Vars.nextRandomLong(cash);
		if (target != null)
		    for (int i = 1; i < 20; i++) {
			final long upperBound = (long) (target + (target * (0.1 * i)));
			final long lowerBound = (long) (target - (target * (0.1 * i)));

			final int rndInt = Vars.rnd.nextInt(8 * i);
			if (rndInt == 1 && cash > lowerBound && cash < upperBound) {
			    if (changeColor)
				setForeground(Vars.colorLock2Win);
			    if (cash < target && cash > lowerBound)
				price = Math.abs(target - cash);
			    else if (cash > target && cash < upperBound)
				price = ((Math.abs(target - cash) * 3) / difficulty) * 3;

			    break;
			}
		    }
	    } else
		price = 1;
	    return price;
	}

	@Override
	public void paintComponent(Graphics g) {
	    setFont(new Font("Tahoma", Font.BOLD, getYWithRatio(28)));
	    setIcon(scaleImage(iconCurrent, getXWithRatio(defaultSize), getYWithRatio(defaultSize)));
	    int maxHeight = ((int) (double) (hbLocks.getHeight() * 0.8));
	    if (showResult) {
		if (enemySelectedLock == listID)
		    setIcon(changeNonAlphaColor(scaleImage(iconCurrent, getXWithRatio(defaultSize), getYWithRatio(defaultSize, maxHeight)), Color.GREEN));
		else if (lockpickUsedOnThis || (userSelectedLock == listID && enemySelectedLock == -1))
		    setIcon(changeNonAlphaColor(scaleImage(iconCurrent, getXWithRatio(defaultSize), getYWithRatio(defaultSize, maxHeight)), Color.ORANGE));
		else if (userSelectedLock != -1 && enemySelectedLock == -1)
		    setIcon(changeNonAlphaColor(scaleImage(iconCurrent, getXWithRatio(defaultSize), getYWithRatio(defaultSize, maxHeight)), Color.DARK_GRAY));
		else
		    setIcon(changeNonAlphaColor(scaleImage(iconCurrent, getXWithRatio(defaultSize), getYWithRatio(defaultSize, maxHeight)), Color.RED));
	    } else
		setIcon(scaleImage(iconCurrent, getXWithRatio(defaultSize), getYWithRatio(defaultSize, maxHeight)));

	    super.paintComponent(g);
	}

    }

    private AtomicBoolean animatingcashPlayer = new AtomicBoolean(false);
    private JLabel lblLockpickNormal;
    private JLabel lblLockpickPlatinum;
    private Avatar avatarEnemy;
    private Avatar avatarPlayer;
    private JLabel lblSettings;
    private JLabel lblRagequit;

    /**
     * Counts new cash target
     */
    protected void countTarget() {
	if (gamemode == null)
	    return;
	if (gamemode == GameMode.COUNT_TOGETHER) {
	    target = (cashPlayer.get() + cashEnemy.get());
	} else if (gamemode == GameMode.MIDDLE) {
	    target = (cashPlayer.get() + cashEnemy.get()) / 2;
	}
	animatePlayerCash();
    }

    /**
     * Animates cashPlayer until shown cashPlayer is same as saved
     * Checks if this player wins
     */
    private void animatePlayerCash() {
	if (animatingcashPlayer.get())
	    return;
	new Thread() {
	    public void run() {
		animatingcashPlayer.set(true);
		Long showncashPlayer;
		if (!lblCashPlayer.getText().contains("/"))
		    showncashPlayer = Long.parseLong(lblCashPlayer.getText());
		else
		    showncashPlayer = Long.parseLong(lblCashPlayer.getText().substring(0, lblCashPlayer.getText().indexOf("/")));
		do {
		    final Long difference = Math.abs(cashPlayer.get() - showncashPlayer);
		    long change = 0;
		    long l;
		    for (l = 1000000000000000000L; difference < l; l /= 10)
			;
		    change = l / 10;
		    if (change == 0)
			change = 1;

		    if (showncashPlayer > cashPlayer.get())
			showncashPlayer -= change;
		    else
			showncashPlayer += change;
		    if (target == null)
			lblCashPlayer.setText(showncashPlayer.toString());
		    else
			lblCashPlayer.setText(showncashPlayer.toString() + "/" + target);
		    try {
			Thread.sleep(1);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		} while (showncashPlayer != cashPlayer.get());

		if (target != null && target == cashPlayer.get()) {
		    System.out.println("Wins!");
		    selectionEnabled = false;
		    checkLeadTimer.cancel();
		    Jukebox.pause();
		    playDub("You won!");
		    dubbing.addPlaingCompletedListner(new PlayingCompletedListner() {

			@Override
			public void action(File playedFile) {
			    Jukebox.play();
			}

		    });
		    new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
			    lblInfo.setVisible(true);
			    lblInfo.setForeground(Vars.randomColor());
			}

		    }, 250, 250);
		    return;
		}

		animatingcashPlayer.set(false);
	    }
	}.start();
    }

    /**
     * Create the frame.
     */
    public TheGame(int difficulty, boolean enableLockPicks, final Player player1, final Player player2, final GameMode gamemode) {
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosed(WindowEvent arg0) {
		if (player1 != null)
		    player1.exitGame();
		exitGame();
	    }
	});
	this.gamemode = gamemode;
	this.player1 = player1;
	this.player2 = player2;

	Vars.makeFullscreen(this);

	if (player2 == null) {
	    if (gamemode == GameMode.MIDDLE) {
		long newEnemyCashRandomMax = cashPlayer.get();
		if (player1 != null)
		    newEnemyCashRandomMax = player1.getCash();
		newEnemyCashRandomMax *= 2;
		cashEnemy.set(Vars.nextRandomLong(newEnemyCashRandomMax));
	    }
	}

	if (player1 != null) {
	    player1.restartFight();
	    lockpicksNormalLeft = player1.getLockpicksNormal();
	    lockpicksPlatinumLeft = player1.getLockpicksPlatinum();
	}
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent arg0) {
		updateUI();
	    }
	});
	this.difficulty = difficulty;
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	setBounds(100, 100, defaultWidth, defaultHeight);
	contentPane = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		g.drawImage(Vars.imgBackground, 0, 0, getWidth(), getHeight(), null);
	    }
	};
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	dubbing.addPlaingCompletedListner(new PlayingCompletedListner() {

	    @Override
	    public void action(File playedFile) {
		lblInfo.setVisible(false);
	    }
	});

	lblRagequit = new JLabel();
	lblRagequit.setToolTipText("RAGEQUIT");
	lblRagequit.setBounds(950, 636, 150, 150);
	lblRagequit.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseEntered(MouseEvent e) {
		lblRagequit.setIcon(scaleImage(Vars.imgRageQuitOn, lblRagequit.getWidth(), lblRagequit.getHeight()));
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		lblRagequit.setIcon(scaleImage(Vars.imgRageQuitOff, lblRagequit.getWidth(), lblRagequit.getHeight()));
	    }

	    @Override
	    public void mouseClicked(MouseEvent arg0) {
		if (player1 != null)
		    player1.exitGame();
		exitGame();
	    }
	});

	lblLockpickNormal = new JLabel("x" + lockpicksNormalLeft) {
	    @Override
	    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (lockpicksNormalLeft <= 0)
		    lblLockpickNormal.setIcon(changeNonAlphaColor(
			    scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()), Color.DARK_GRAY));
	    }
	};
	lblLockpickNormal.setToolTipText("Classic lockpick");
	lblLockpickNormal.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 57));
	lblLockpickNormal.setForeground(Color.ORANGE);
	lblLockpickNormal.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (selectedLockpick == LOCKPICK_NORMAL) {
		    selectedLockpick = LOCKPICK_NONE;
		    lblLockpickNormal.setIcon(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()));
		    return;
		}

		if (lockpicksNormalLeft <= 0)
		    return;

		if (selectedLockpick == LOCKPICK_PLATINUM)
		    lblLockpickPlatinum.setIcon(scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()));
		selectedLockpick = LOCKPICK_NORMAL;
		lblLockpickNormal.setIcon(
			changeNonAlphaColor(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()), Color.WHITE));
	    }
	});
	lblLockpickNormal.setBounds(20, 637, 211, 138);
	lblLockpickNormal.setIcon(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()));
	contentPane.add(lblLockpickNormal);

	lblLockpickNormal.setVisible(enableLockPicks);

	lblLockpickPlatinum = new JLabel("x" + lockpicksPlatinumLeft) {
	    @Override
	    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (lockpicksPlatinumLeft <= 0)
		    lblLockpickPlatinum.setIcon(changeNonAlphaColor(
			    scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()), Color.DARK_GRAY));
	    }
	};
	lblLockpickPlatinum.setToolTipText("Platinum lockpick");
	lblLockpickPlatinum.setForeground(Color.CYAN);
	lblLockpickPlatinum.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (selectedLockpick == LOCKPICK_PLATINUM) {
		    selectedLockpick = LOCKPICK_NONE;
		    lblLockpickPlatinum.setIcon(scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()));
		    return;
		}

		if (lockpicksPlatinumLeft <= 0)
		    return;

		if (selectedLockpick == LOCKPICK_NORMAL)
		    lblLockpickNormal.setIcon(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()));
		selectedLockpick = LOCKPICK_PLATINUM;
		lblLockpickPlatinum.setIcon(changeNonAlphaColor(
			scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()), Color.WHITE));
	    }
	});
	lblLockpickPlatinum.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 57));
	lblLockpickPlatinum.setBounds(298, 637, 211, 138);
	lblLockpickPlatinum.setIcon(scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()));
	contentPane.add(lblLockpickPlatinum);
	lblLockpickPlatinum.setVisible(enableLockPicks);
	lblRagequit.setIcon(scaleImage(Vars.imgRageQuitOff, lblRagequit.getWidth(), lblRagequit.getHeight()));
	contentPane.add(lblRagequit);

	lblLevel = new JLabel("Level " + level);
	lblLevel.setToolTipText("Your level");
	lblLevel.setForeground(Color.BLUE);
	lblLevel.setFont(new Font("Tahoma", Font.PLAIN, 33));
	lblLevel.setBounds(10, 11, 221, 63);
	contentPane.add(lblLevel);

	hbLocks = Box.createHorizontalBox();
	hbLocks.setEnabled(false);
	hbLocks.setBounds(10, 180, 1090, 446);
	contentPane.add(hbLocks);

	lblInfo = new JLabel("Good job!");
	lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
	lblInfo.setHorizontalTextPosition(JLabel.CENTER);
	lblInfo.setForeground(Color.MAGENTA);
	lblInfo.setVisible(false);
	lblInfo.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 71));
	lblInfo.setBounds(10, 53, 1084, 116);
	contentPane.add(lblInfo);

	lblCashPlayer = new JLabel(Long.toString(cashPlayer.get()));
	lblCashPlayer.setToolTipText("");
	lblCashPlayer.setForeground(Vars.colorCoin);
	lblCashPlayer.setFont(new Font("Tahoma", Font.BOLD, 67));
	lblCashPlayer.setHorizontalAlignment(SwingConstants.CENTER);
	lblCashPlayer.setBounds(425, 676, 399, 73);
	lblCashPlayer.setIcon(scaleImage(Vars.imgCoin, lblCashPlayer.getHeight(), lblCashPlayer.getHeight()));
	contentPane.add(lblCashPlayer);

	Component verticalStrut = Box.createVerticalStrut(20);
	hbLocks.add(verticalStrut);

	if (player1 == null) {
	    avatarPlayer = new Avatar(new ImageIcon(Vars.imgDataCharacterPrefix + "char_def.png"), Vars.randomColor(), Color.BLACK);
	    avatarPlayer.setText("You");
	} else {
	    cashPlayer.set(player1.getCash());
	    lblCashPlayer.setText(Long.toString(cashPlayer.get()));
	    avatarPlayer = player1.getAvatar();
	    avatarPlayer.setText(player1.username);
	}
	avatarPlayer.setToolTipText("You");
	avatarPlayer.setBounds(150, 0, 204, 199);
	contentPane.add(avatarPlayer);

	if (player2 == null) {
	    avatarEnemy = new Avatar(new ImageIcon(Vars.imgDataCharacterPrefix + "char_pickalock.png"), Color.GREEN, Color.YELLOW);
	    avatarEnemy.setText("Sir Pick A'Lock");
	    avatarEnemy.setCosmetic(new ImageIcon(Vars.imgDataCharacterPrefix + "hat_golden.png"), CosmeticType.HAT);
	    avatarEnemy.setCosmetic(new ImageIcon(Vars.imgDataCharacterPrefix + "golden_eye.png"), CosmeticType.GLASSES);
	    if (player1 != null)
		cashEnemy.set((int) ((double) cashPlayer.get() * (0.8 + ((double) Vars.rnd.nextInt(4) / 10))));
	} else {
	    avatarEnemy = player2.getAvatar();
	    avatarEnemy.setText(player2.username);
	}
	avatarEnemy.setToolTipText("Bad guy");
	avatarEnemy.setBounds(770, 0, 204, 199);
	contentPane.add(avatarEnemy);

	lblSettings = new JLabel();
	lblSettings.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseEntered(MouseEvent arg0) {
		lblSettings.setIcon(invertImage(scaleImage(Vars.imgSettings, lblSettings.getWidth(), lblSettings.getHeight())));
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		lblSettings.setIcon(scaleImage(Vars.imgSettings, lblSettings.getWidth(), lblSettings.getHeight()));
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
		// popSettings.show(lblSettings, e.getX(), e.getY());
		Vars.settings.setVisible(true);
	    }
	});
	lblSettings.setBounds(1041, 11, 53, 53);
	contentPane.add(lblSettings);

	Vars.reloadIconsListeners.add(new Vars.NewIconListener() {

	    @Override
	    public void loadNewIcon() {
		contentPane.repaint();
		lblCashPlayer.setIcon(Vars.imgCoin);
		updateUI();
		hbLocks.repaint();
		repaint();
	    }
	});

	for (int i = 1; i <= difficulty; i++) {
	    Lock lbl = new Lock();
	    locks.add(lbl);
	    hbLocks.add(lbl);
	    verticalStrut = Box.createVerticalStrut(20);
	    hbLocks.add(verticalStrut);
	}

	if (target != null)
	    target += cashPlayer.get();

	hbLocks.repaint();
	repaint();

	checkLeadTimer.schedule(checkLeadTimerTask, 5000, 5000);

	countTarget();

	playDub(gamemode.toString());
	selectionEnabled = true;

	Vars.mainWindow.frame.setVisible(false);
	Jukebox.random();
    }

    /**
     * Shows main window and dispose this
     */
    private void exitGame() {
	Vars.mainWindow.frame.setVisible(true);
	Vars.mainWindow.frame.toFront();
	Jukebox.stop();
	dubbing.stop();
	dispose();
    }

    /**
     * Gets X with ration based on real frame size VS default frame size
     * 
     * @param normalX
     *            x in normal frame size
     * @return computer X with ration based on real frame size VS default frame size
     */
    private int getXWithRatio(int normalX) {
	return (normalX * getWidth()) / defaultWidth;
    }

    /**
     * Gets Y with ration based on real frame size VS default frame size
     * 
     * @param normalY
     *            y in normal frame size
     * @return computer Y with ration based on real frame size VS default frame size
     */
    private int getYWithRatio(int normalY) {
	return (normalY * getHeight()) / defaultHeight;
    }

    /**
     * Gets Y with ration based on real frame size VS default frame size
     * 
     * @param normalY
     *            y in normal frame size
     * @param maxY
     *            maximum size
     * @return computer Y with ration based on real frame size VS default frame size
     */
    private int getYWithRatio(int normalY, int maxY) {
	int ratioY = getYWithRatio(normalY);
	if (ratioY > maxY)
	    return maxY;
	return ratioY;
    }

    /**
     * Scales ImageIcon to new values
     * 
     * @param img
     *            ImageIcon to scale
     * @param width
     *            new width
     * @param height
     *            new height
     * @return scaled ImageIcon
     */
    public static ImageIcon scaleImage(ImageIcon img, int width, int height) {
	if (width <= 0 || height <= 0 || img == null)
	    return null;

	BufferedImage bufferedImageIcon = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	Graphics bufferedImageIconGraphics = bufferedImageIcon.createGraphics();
	img.paintIcon(null, bufferedImageIconGraphics, 0, 0);

	BufferedImage returnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Graphics returnImageGraphic = returnImage.createGraphics();
	returnImageGraphic.drawImage(bufferedImageIcon, 0, 0, width, height, null);

	bufferedImageIconGraphics.dispose();
	returnImageGraphic.dispose();

	return new ImageIcon(returnImage);
    }

    /**
     * Converts Image to Buffered image
     * 
     * @param image
     *            Image to convert
     * @return Buffered image from Image image
     */
    private static BufferedImage convertToBufferedImage(Image image) {
	BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	Graphics2D g = newImage.createGraphics();
	g.drawImage(image, 0, 0, null);
	g.dispose();
	return newImage;
    }

    /**
     * Inverts colors of image except transparent
     * 
     * @param imgIcon
     *            ImageIcon to convert
     * @return inverted ImageIcon
     */
    public static ImageIcon invertImage(ImageIcon imgIcon) {
	BufferedImage bimg = convertToBufferedImage(imgIcon.getImage());
	Graphics bg = bimg.getGraphics();
	bg.drawImage(bimg, 0, 0, null);
	bg.dispose();

	for (int x = 0; x < bimg.getWidth(); x++) {
	    for (int y = 0; y < bimg.getHeight(); y++) {
		int rgba = bimg.getRGB(x, y);
		Color col = new Color(rgba, true);
		if (col.getAlpha() != 0)
		    col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
		bimg.setRGB(x, y, col.getRGB());
	    }
	}

	return new ImageIcon(bimg);
    }

    /**
     * Changes all non-transparent pars of image to selected color
     * 
     * @param imgIcon
     *            image to change
     * @param colorToChangeTo
     *            new color or non-transparent parts
     * @return image with changed colors
     */
    private ImageIcon changeNonAlphaColor(ImageIcon imgIcon, Color colorToChangeTo) {
	BufferedImage bimg = convertToBufferedImage(imgIcon.getImage());
	Graphics bg = bimg.getGraphics();
	bg.drawImage(bimg, 0, 0, null);
	bg.dispose();

	for (int x = 0; x < bimg.getWidth(); x++) {
	    for (int y = 0; y < bimg.getHeight(); y++) {
		int rgba = bimg.getRGB(x, y);
		Color col = new Color(rgba, true);
		if (col.getAlpha() != 0) {
		    col = colorToChangeTo;
		}
		bimg.setRGB(x, y, col.getRGB());
	    }
	}

	return new ImageIcon(bimg);
    }

    /**
     * Changes all pattern based colors to random colors
     * 
     * @param imgIcon
     *            image to change
     * @return image with replaced colors
     */
    public static ImageIcon changeColorByPatternToRandom(ImageIcon imgIcon) {
	return changeColorByPattern(imgIcon, Vars.colorPatternRandom, new Color(Vars.rnd.nextInt(255), Vars.rnd.nextInt(255), Vars.rnd.nextInt(255)));
    }

    /**
     * Changes all pattern based colors to selected color
     * 
     * @param imgIcon
     *            image to change
     * @param pattern
     *            which colors to change
     * @param replacement
     *            to which color change pattern
     * @return image with replaced colors
     */
    public static ImageIcon changeColorByPattern(ImageIcon imgIcon, Color pattern, Color replacement) {
	BufferedImage bimg = convertToBufferedImage(imgIcon.getImage());
	Graphics bg = bimg.getGraphics();
	bg.drawImage(bimg, 0, 0, null);
	bg.dispose();

	for (int x = 0; x < bimg.getWidth(); x++) {
	    for (int y = 0; y < bimg.getHeight(); y++) {
		int rgba = bimg.getRGB(x, y);
		Color col = new Color(rgba, true);
		if (col.getBlue() == pattern.getBlue() && col.getRed() == pattern.getRed() && col.getGreen() == pattern.getGreen()) {
		    col = replacement;
		}
		bimg.setRGB(x, y, col.getRGB());
	    }
	}

	return new ImageIcon(bimg);
    }

    /**
     * Changes locations on all elements based on ratio
     */
    protected void updateUI() {
	if (lblLevel == null)
	    return;
	lblLevel.setFont(new Font("Tahoma", Font.PLAIN, getYWithRatio(33)));
	lblLevel.setBounds(getXWithRatio(10), getYWithRatio(11), getXWithRatio(221), getYWithRatio(78));

	lblInfo.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, getYWithRatio(71)));
	lblInfo.setBounds(0, getYWithRatio(53), getWidth(), getYWithRatio(116));

	lblCashPlayer.setBounds(getXWithRatio(0), getYWithRatio(676), getWidth(), getYWithRatio(73));
	lblCashPlayer.setFocusable(false);
	lblCashPlayer.setFont(new Font("Tahoma", Font.BOLD, getYWithRatio(67)));
	lblCashPlayer.setIcon(scaleImage(Vars.imgCoin, lblCashPlayer.getHeight(), lblCashPlayer.getHeight()));
	animatePlayerCash();

	lblLockpickPlatinum.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, getYWithRatio(57)));
	lblLockpickPlatinum.setText("x" + this.lockpicksPlatinumLeft);
	lblLockpickPlatinum.setBounds(getXWithRatio(200), getYWithRatio(637), getXWithRatio(150), getYWithRatio(137));
	if (selectedLockpick != LOCKPICK_PLATINUM)
	    lblLockpickPlatinum.setIcon(scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()));
	else
	    lblLockpickPlatinum.setIcon(changeNonAlphaColor(
		    scaleImage(Vars.imgLockpickPlatinum, ((int) ((double) lblLockpickPlatinum.getHeight() / 2.7)), lblLockpickPlatinum.getHeight()), Color.WHITE));

	lblLockpickNormal.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, getYWithRatio(57)));
	lblLockpickNormal.setText("x" + this.lockpicksNormalLeft);
	lblLockpickNormal.setBounds(getXWithRatio(20), getYWithRatio(637), getXWithRatio(150), getYWithRatio(137));
	if (selectedLockpick != LOCKPICK_NORMAL)
	    lblLockpickNormal.setIcon(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()));
	else
	    lblLockpickNormal
		    .setIcon(changeNonAlphaColor(scaleImage(Vars.imgLockpick, ((int) ((double) lblLockpickNormal.getHeight() / 2.7)), lblLockpickNormal.getHeight()), Color.WHITE));

	avatarEnemy.setBounds(getXWithRatio(770), getYWithRatio(0), getXWithRatio(200), getYWithRatio(199));

	avatarPlayer.setBounds(getXWithRatio(150), 0, getXWithRatio(204), getYWithRatio(199));

	if (playerIsInTheLead == null) {
	    avatarPlayer.setFont(new Font("Tahoma", Font.PLAIN, getYWithRatio(15)));
	    avatarEnemy.setFont(new Font("Tahoma", Font.PLAIN, getYWithRatio(15)));
	} else {
	    if (playerIsInTheLead) {
		avatarPlayer.setFont(new Font("Tahoma", Font.BOLD, getYWithRatio(20)));
		avatarEnemy.setFont(new Font("Tahoma", Font.ITALIC, getYWithRatio(15)));
	    } else {
		avatarPlayer.setFont(new Font("Tahoma", Font.ITALIC, getYWithRatio(15)));
		avatarEnemy.setFont(new Font("Tahoma", Font.BOLD, getYWithRatio(20)));
	    }
	}

	lblSettings.setBounds(getXWithRatio(1041), getYWithRatio(11), getXWithRatio(53), getYWithRatio(53));
	lblSettings.setIcon(scaleImage(Vars.imgSettings, lblSettings.getWidth(), lblSettings.getHeight()));

	lblRagequit.setBounds(getXWithRatio(950), getYWithRatio(636), getXWithRatio(150), getYWithRatio(150));
	lblRagequit.setIcon(scaleImage(Vars.imgRageQuitOff, lblRagequit.getWidth(), lblRagequit.getHeight()));

	hbLocks.setBounds(getXWithRatio(10), getYWithRatio(180), getWidth(), getYWithRatio(446));
    }
}
