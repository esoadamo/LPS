package game.aho.lps;

import java.awt.BorderLayout;
import game.aho.lps.Player.DatabaseProperties;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import aho.util.mysql2PHP2Java.MySQLException;
import aho.util.mysql2PHP2Java.MySQLRespond;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Graphics;

@SuppressWarnings("serial")
public class MatchMaking extends JFrame {

    private JPanel contentPane;
    private JTextField txtUser;
    private Box playersBox;
    private JComboBox<GameMode> comboGamemode;
    private JComboBox<String> comboDifficulty;
    private JCheckBox chckbxLockpicksEnabled;
    private JPanel controlPane;
    private Box searchBox;
    private final AtomicBoolean countdownEnabled = new AtomicBoolean(false);
    private final AtomicInteger countdown = new AtomicInteger(10);
    private final Timer countdownTimer = new Timer();
    private final TimerTask countdownTimerTask = new TimerTask() {

	@Override
	public void run() {
	    if (!countdownEnabled.get())
		return;
	    lblSearching.setText(String.format("Researching in %d seconds", countdown.get()));
	    // lblSearching.setForeground(Vars.randomColor());
	    if (countdown.decrementAndGet() == -1)
		search();
	}
    };
    private JLabel lblSearching;
    private JScrollPane scrollPane;
    private final List<Player> listOnlinePlayers = new ArrayList<Player>();

    /**
     * Create the frame.
     */
    public MatchMaking() {
    	setTitle("Find your m8");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 879, 684);
	contentPane = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		g.drawImage(Vars.imgMatchMakingBackground, 0, 0, getWidth(), getHeight(), null);
	    }
	};
	contentPane.setBackground(Color.WHITE);
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));

	Box verticalBox = Box.createVerticalBox();
	contentPane.add(verticalBox, BorderLayout.NORTH);

	controlPane = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		g.drawImage(Vars.imgBackground, 0, 0, getWidth(), getHeight(), null);
	    }
	};
	verticalBox.add(controlPane);
	controlPane.setLayout(new BorderLayout(0, 0));

	Box verticalBoxControl = Box.createVerticalBox();
	controlPane.add(verticalBoxControl, BorderLayout.NORTH);
	verticalBoxControl.setOpaque(false);

	Box horizontalBox_2 = Box.createHorizontalBox();
	horizontalBox_2.setOpaque(false);
	verticalBoxControl.add(horizontalBox_2);

	Component horizontalStrut_1 = Box.createHorizontalStrut(20);
	horizontalBox_2.add(horizontalStrut_1);

	JLabel lblFilterUser = new JLabel("Filter user: ");
	lblFilterUser.setForeground(new Color(255, 255, 255));
	horizontalBox_2.add(lblFilterUser);

	txtUser = new JTextField();
	horizontalBox_2.add(txtUser);
	txtUser.setColumns(10);

	Box horizontalBox_1 = Box.createHorizontalBox();
	horizontalBox_1.setOpaque(false);
	verticalBoxControl.add(horizontalBox_1);

	Component horizontalStrut_4 = Box.createHorizontalStrut(20);
	horizontalBox_1.add(horizontalStrut_4);

	JLabel lblDifficulty = new JLabel("Difficulty: ");
	lblDifficulty.setForeground(new Color(255, 255, 255));
	horizontalBox_1.add(lblDifficulty);

	comboDifficulty = new JComboBox<String>();
	comboDifficulty.setBackground(new Color(250, 240, 230));
	comboDifficulty.setOpaque(false);
	comboDifficulty.setModel(new DefaultComboBoxModel<String>(new String[] { "any", "first time using computer", "fifty fifty", "classic robbery", "doing small math",
		"small mall", "big market", "doing big math", "trying hard", "nearly impossible", "face the RNGesus" }));
	horizontalBox_1.add(comboDifficulty);

	Component horizontalStrut_5 = Box.createHorizontalStrut(20);
	horizontalBox_1.add(horizontalStrut_5);

	chckbxLockpicksEnabled = new JCheckBox("Lockpicks enabled");
	chckbxLockpicksEnabled.setForeground(new Color(255, 255, 255));
	chckbxLockpicksEnabled.setOpaque(false);
	chckbxLockpicksEnabled.setSelected(true);
	horizontalBox_1.add(chckbxLockpicksEnabled);

	Component horizontalStrut_2 = Box.createHorizontalStrut(20);
	horizontalBox_1.add(horizontalStrut_2);

	JButton btnSearch = new JButton("Start search");
	btnSearch.setBackground(new Color(220, 20, 60));
	btnSearch.setForeground(new Color(255, 255, 255));
	btnSearch.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String filterPlayer = txtUser.getText();
		if (filterPlayer.length() == 0)
		    filterPlayer = null;
		else if (filterPlayer.startsWith(Vars.matchmakingFilterSelection))
		    filterPlayer = filterPlayer.substring(1);
		Vars.loggedPlayer.setMatchamaking(getMatchmakingString(), filterPlayer);

		/*
		 * Find and remove everyone who wanted to fight with this player.
		 * There requests are not valid anymore
		 */
		try {
		    Vars.conn.executeSQL(
			    String.format("UPDATE users SET matchmaking_user=NULL WHERE matchmaking_user='%s'", Vars.matchmakingFilterSelection + Vars.loggedPlayer.username));
		} catch (MySQLException e) {
		    e.printStackTrace();
		}

		searchBox.setVisible(true);
		controlPane.setVisible(false);
		search();
		countdownEnabled.set(true);
	    }
	});
	horizontalBox_1.add(btnSearch);

	Box horizontalBox = Box.createHorizontalBox();
	horizontalBox.setOpaque(false);
	verticalBoxControl.add(horizontalBox);

	Component horizontalStrut = Box.createHorizontalStrut(20);
	horizontalBox.add(horizontalStrut);

	JLabel lblGameMode = new JLabel("Game mode: ");
	lblGameMode.setForeground(new Color(255, 255, 255));
	horizontalBox.add(lblGameMode);

	comboGamemode = new JComboBox<GameMode>();
	comboGamemode.setBackground(new Color(253, 245, 230));
	comboGamemode.setOpaque(false);
	comboGamemode.setModel((ComboBoxModel<GameMode>) new DefaultComboBoxModel<GameMode>(GameMode.values()));
	horizontalBox.add(comboGamemode);

	searchBox = Box.createHorizontalBox();
	searchBox.setVisible(false);
	verticalBox.add(searchBox);

	lblSearching = new JLabel("Researching 10 seconds");
	searchBox.add(lblSearching);

	Component horizontalStrut_3 = Box.createHorizontalStrut(20);
	searchBox.add(horizontalStrut_3);

	JButton btnResearch = new JButton("Research");
	btnResearch.setForeground(new Color(0, 255, 0));
	btnResearch.setBackground(new Color(255, 215, 0));
	btnResearch.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		search();
	    }
	});
	searchBox.add(btnResearch);

	JButton btnCancel = new JButton("Cancel");
	btnCancel.setForeground(new Color(255, 255, 255));
	btnCancel.setBackground(new Color(255, 192, 203));
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		Vars.loggedPlayer.setMatchamaking(null, null);
		stopSearch();
	    }
	});
	searchBox.add(btnCancel);

	scrollPane = new JScrollPane();
	scrollPane.getViewport().setOpaque(false);
	scrollPane.setOpaque(false);
	contentPane.add(scrollPane, BorderLayout.CENTER);

	playersBox = Box.createVerticalBox();
	scrollPane.setViewportView(playersBox);
	playersBox.setBorder((BorderFactory.createMatteBorder(5, 5, 5, 5, Vars.randomColor())));

	lblSearching.setForeground(Color.WHITE);

	Box horizontalBox_3 = Box.createHorizontalBox();
	contentPane.add(horizontalBox_3, BorderLayout.SOUTH);

	JButton btnBack = new JButton("MENU");
	btnBack.setForeground(new Color(255, 255, 255));
	btnBack.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		Vars.loggedPlayer.goOffline();
		Vars.mainWindow.frame.setVisible(true);
		setVisible(false);
	    }
	});
	btnBack.setBackground(new Color(135, 206, 250));
	horizontalBox_3.add(btnBack);

	Component horizontalGlue = Box.createHorizontalGlue();
	horizontalBox_3.add(horizontalGlue);

	final JLabel lblConnectionStable = new JLabel("Connection stable");
	lblConnectionStable.setForeground(new Color(0, 255, 0));
	horizontalBox_3.add(lblConnectionStable);
	Vars.listLabelConnectionInfo.add(lblConnectionStable);

	countdownTimer.schedule(countdownTimerTask, 1000, 1000);
    }

    /**
     * Adss player to vertical box
     * 
     * @param playerName
     * @param matchmakingString
     */
    private void addPlayer(final String playerName, final String matchmakingString) {
	final Player player = new Player(playerName);
	boolean playerIsAlreadyInList = false;
	for (int i = 0; i < listOnlinePlayers.size(); i++)
	    if (listOnlinePlayers.get(i).isSameAs(player)) {
		playerIsAlreadyInList = true;
		break;
	    }
	if (!playerIsAlreadyInList) {
	    player.getOnlineNumber();
	    listOnlinePlayers.add(player);
	}
	final DatabaseProperties props = new DatabaseProperties(matchmakingString);
	String gamemode = props.getString("gmd");
	if (gamemode == null)
	    gamemode = "any gamemode";
	String difficulty = props.getString("diff");
	if (difficulty == null)
	    difficulty = "any";
	final String lockpicksEnabled = props.getString("lck");

	Box horizPlayer = Box.createHorizontalBox();
	horizPlayer.setBorder((BorderFactory.createMatteBorder(3, 3, 3, 3, Vars.randomColor())));

	final JLabel lblPlayerName = new JLabel(playerName + " ($" + player.getCash() + ")");
	lblPlayerName.setForeground(Vars.randomColor());
	horizPlayer.add(lblPlayerName);
	final JLabel lblFightInfo = new JLabel(String.format(" wants to play %s on %s difficulty, lockpicks are %s", gamemode, difficulty, lockpicksEnabled));
	// lblFightInfo.setForeground(Vars.randomColor());
	lblFightInfo.setForeground(Color.WHITE);
	horizPlayer.add(lblFightInfo);

	final Component horizontalGlue = Box.createHorizontalGlue();
	horizPlayer.add(horizontalGlue);

	final JButton btnConnect = new JButton("Pick " + playerName);
	btnConnect.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		if (player.isSameAs(Vars.loggedPlayer)) {
		    JOptionPane.showMessageDialog(Vars.matchMaking, "I fully understand you.\nI would play with myselft too, if it was possible.", "Sorry to say, but...",
			    JOptionPane.INFORMATION_MESSAGE);
		    return;
		}
		if (!player.isWaitingForGame()) {
		    JOptionPane.showMessageDialog(Vars.matchMaking, "This player is busy right now.\nTry it later.", "Sorry to say, but...", JOptionPane.INFORMATION_MESSAGE);
		    return;
		}
		Vars.loggedPlayer.setMatchamaking(null, Vars.matchmakingFilterSelection + player.username);
		Vars.dialogWaitingForFight.init(player);
		Vars.dialogWaitingForFight.setLocationRelativeTo(Vars.matchMaking);
		Vars.dialogWaitingForFight.setVisible(true);
		stopSearch();
	    }
	});
	btnConnect.setForeground(Color.WHITE);
	btnConnect.setBackground(Vars.randomColor());
	horizPlayer.add(btnConnect);

	playersBox.add(horizPlayer);
	System.out.println(player.username + " added to list"); // TODO

	playersBox.setVisible(false);
	playersBox.repaint();
	playersBox.setVisible(true);
    }

    /**
     * Creates matchmaking String from user entered data
     * 
     * @return matchmaking String used in database
     */
    private String getMatchmakingString() {
	String lockPicksEnabled;
	if (chckbxLockpicksEnabled.isSelected())
	    lockPicksEnabled = "enabled";
	else
	    lockPicksEnabled = "disabled";

	StringBuilder matchMakingStringBuilder = new StringBuilder("lck=");
	matchMakingStringBuilder.append(lockPicksEnabled);

	if (comboDifficulty.getSelectedIndex() != 0) {
	    matchMakingStringBuilder.append(";diff=");
	    matchMakingStringBuilder.append(((String) comboDifficulty.getSelectedItem()));
	    matchMakingStringBuilder.append(";diffn=");
	    matchMakingStringBuilder.append(comboDifficulty.getSelectedIndex());
	} else {
	    matchMakingStringBuilder.append(";diffn=");
	    matchMakingStringBuilder.append(Vars.rnd.nextInt(comboDifficulty.getItemCount()) + 1);
	}

	if (comboGamemode.getSelectedIndex() != 0) {
	    matchMakingStringBuilder.append(";gmd=");
	    matchMakingStringBuilder.append(((GameMode) comboGamemode.getSelectedItem()).toString());
	    matchMakingStringBuilder.append(";gmn=");
	    matchMakingStringBuilder.append(((GameMode) comboGamemode.getSelectedItem()).number);
	} else {
	    matchMakingStringBuilder.append(";gmn=");
	    matchMakingStringBuilder.append(Vars.rnd.nextInt((comboGamemode).getItemCount()));
	}
	return matchMakingStringBuilder.toString();
    }

    /**
     * Search for online players wanting to create match
     */
    private void search() {
	lblSearching.setText("Searching...");
	playersBox.removeAll();

	/*
	 * Check already found players if they are still online
	 */
	for (int i = 0; i < listOnlinePlayers.size(); i++) {
	    if (listOnlinePlayers.get(i).isOnline())
		continue;
	    listOnlinePlayers.get(i).goOffline();
	    System.out.println(listOnlinePlayers.get(i).username + " went offline"); // TODO
	    listOnlinePlayers.remove(i);
	    i--;
	}

	try {
	    MySQLRespond rs = Vars.conn
		    .executeSQL(String.format("SELECT username FROM users WHERE matchmaking_user='%s'", Vars.matchmakingFilterSelection + Vars.loggedPlayer.username));
	    while (rs.next()) {
		final Player player = new Player(rs.getString("username"));
		new NotificationMessage(Vars.imgFightRequest,"<html><b>" + player.username + "</b> challenges you for a duel!",15000, new NotificationMessage.Action() {
		    
		    @Override
		    public void action() {
			Vars.matchMaking.setVisible(true);
			Vars.matchMaking.toFront();
		    }
		}).playSound();
		Vars.matchMaking.setVisible(true);
		Vars.matchMaking.toFront();
		if (JOptionPane.showConfirmDialog(this, player.username + " wants to fight with you!\nAre you ready?", "Do you want to accept honest duel?",
			JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
		    if(!player.getMatchmakingUserString().contentEquals(Vars.matchmakingFilterSelection + Vars.loggedPlayer.username)) //Check if another player didn't change his/her mind
			return;
		    Vars.loggedPlayer.restartFight();
		    Vars.loggedPlayer.setDuelWith(player);
		    final DatabaseProperties props = new DatabaseProperties(Vars.loggedPlayer.getMatchmakingString());
		    Vars.loggedPlayer.setMatchamaking(null, null);
		    System.out.println("Gamemode: " + GameMode.getByNumber(Integer.parseInt(props.getString("gmn"))));
		    new TheGame(Integer.parseInt(props.getString("diffn")), props.getString("lck").charAt(0) == 'e', Vars.loggedPlayer, player,
			    GameMode.getByNumber(Integer.parseInt(props.getString("gmn")))).setVisible(true);;
		    setVisible(false);
		    stopSearch();
		} else {
		    player.setMatchamaking(null, null);
		}
	    }
	} catch (MySQLException e) {
	    e.printStackTrace();
	}

	final StringBuilder wherePart = new StringBuilder();
	for (String s : getMatchmakingString().split(";")) {
	    if (s.startsWith("gmn") || s.startsWith("diffn")) // These are generated only for computer processing, not for matchmaking search
		continue;
	    wherePart.append(" AND `matchmaking` LIKE '%");
	    wherePart.append(s);
	    wherePart.append("%'");
	}
	try {
	    MySQLRespond rs = Vars.conn.executeSQL("SELECT username, matchmaking, matchmaking_user FROM users WHERE onlineNumber>0 AND " + wherePart.substring(5).toString());
	    while (rs.next()) {
		if (rs.getString("matchmaking_user").startsWith(Vars.matchmakingFilterSelection))
		    continue;
		if (rs.getString("matchmaking_user").length() > 0 && !Vars.loggedPlayer.username.toUpperCase().contains(rs.getString("matchmaking_user").toUpperCase())) // Check
																					 // player
																					 // filter
		    continue;
		System.out.println("Search found player " + rs.getString("username")); // TODO
		addPlayer(rs.getString("username"), rs.getString("matchmaking"));
	    }
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
	countdown.set(10);
	lblSearching.setText("Search completed.");
    }

    public void stopSearch() {
	searchBox.setVisible(false);
	controlPane.setVisible(true);
	countdownEnabled.set(false);
    }
}
