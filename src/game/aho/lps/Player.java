package game.aho.lps;

import java.awt.Color;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

import aho.util.mysql2PHP2Java.MySQL2PHP2Java;
import aho.util.mysql2PHP2Java.MySQLRespond;
import aho.util.mysql2PHP2Java.MySQLException;

public class Player {
    public int id;
    private boolean exists = true;
    public Avatar avatar;

    private int onlineNumber = 0;
    private long onlineNumberLastScan = 0;

    public final String username;

    public Player(String username) {
	this.username = username;
	try {
	    MySQLRespond rs = Vars.conn.executeSQL(String.format("SELECT id FROM users WHERE username = '%s'", MySQL2PHP2Java.addSlashes(username)));
	    if (rs.next())
		id = rs.getInteger("id");
	    else
		exists = false;

	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Checks if player existed in database during creation of this object
     * 
     * @return true if this player is recorded in databse, false otherwise
     */
    public boolean exists() {
	return exists;
    }

    /**
     * Checks password with database
     * 
     * @param password
     *            not hashed password
     * @return true if hashed password is same as in database, false on fail or otherwise
     */
    public boolean checkPassword(String password) {
	try {
	    return Vars.conn.executeSQL(String.format("SELECT password FROM users WHERE id = %d", id)).getString("password").contentEquals(Hash.hashText(password));
	} catch (MySQLException | NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * Gets player's current cash
     * 
     * @return player's current cash
     */
    public long getCash() {
	try {
	    return Vars.conn.executeSQL(String.format("SELECT cash FROM users WHERE id = %d", id)).getLong("cash");
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    /**
     * Gets player's current count of normal lockpicks
     * 
     * @return current count of normal lockpicks
     */
    public int getLockpicksNormal() {
	try {
	    return Vars.conn.executeSQL(String.format("SELECT lockpicks_normal FROM users WHERE id = %d", id)).getInteger("lockpicks_normal");
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    /**
     * Gets player's current count of platinum lockpicks
     * 
     * @return current count of platinum lockpicks
     */
    public int getLockpicksPlatinum() {
	try {
	    return Vars.conn.executeSQL(String.format("SELECT lockpicks_platinum FROM users WHERE id = %d", id)).getInteger("lockpicks_platinum");
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    /**
     * Get player's current online number
     * 
     * @return player's current online number
     */
    public int getOnlineNumber() {
	try {
	    onlineNumberLastScan = System.currentTimeMillis();
	    return onlineNumber = Vars.conn.executeSQL(String.format("SELECT onlineNumber FROM users WHERE id = %d", id)).getInteger("onlineNumber");
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    /**
     * Get player's last online number
     * 
     * @return player's online number during last call of getOnlineNumber()
     */
    public int getLastOnlineNumber() {
	return onlineNumber;
    }

    /**
     * Gets Avatar component from database
     * 
     * @return Avatar of this player
     */
    public Avatar getAvatar() {
	try {
	    MySQLRespond rs = Vars.conn.executeSQL(String.format("SELECT color_circle, color_circle_line, items_worn  FROM users WHERE id = %d", id));
	    Color colorCircle = Vars.hex2Color(rs.getString("color_circle"));
	    Color colorCircleLine = Vars.hex2Color(rs.getString("color_circle_line"));
	    DatabaseProperties props = new DatabaseProperties(rs.getString("items_worn"));
	    ImageIcon face = new ImageIcon(Vars.imgDataCharacterPrefix + props.getString(CosmeticType.FACE.toString() + ".png"));
	    avatar = new Avatar(face, colorCircle, colorCircleLine);
	    for (int i = 0; i < props.keys.size(); i++)
		avatar.setCosmetic(new ImageIcon(Vars.imgDataCharacterPrefix + props.values.get(i) + ".png"), props.keys.get(i));
	    return avatar;
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return null;
	}

    }

    public void setWormItems(final String wormItems) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET items_worn='%s' WHERE id = %d", wormItems, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Get current player avatar's background color
     * 
     * @return current player avatar's background color
     */
    public Color getBackgroundColor() {
	try {
	    MySQLRespond rs = Vars.conn.executeSQL(String.format("SELECT color_circle FROM users WHERE id = %d", id));
	    Color colorCircle = Vars.hex2Color(rs.getString("color_circle"));
	    return colorCircle;
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Get current player avatar's circle color
     * 
     * @return current player avatar's circle color
     */
    public Color getCircleColor() {
	try {
	    MySQLRespond rs = Vars.conn.executeSQL(String.format("SELECT color_circle_line FROM users WHERE id = %d", id));
	    Color colorCircle = Vars.hex2Color(rs.getString("color_circle"));
	    return colorCircle;
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Sets selected lock back to -1 and level to 1
     */
    public void restartFight() {
	setSelectedLock(-1);
	setLevel(1);
    }

    /**
     * Finds a player who is in duel with this player
     * 
     * @return player dueling with this player (can be null)
     */
    public Player getPlayerInFight() {
	try {
	    final int playerID = Vars.conn.executeSQL(String.format("SELECT duelWith FROM users WHERE id = %d", id)).getInteger("duelWith");
	    return new Player(Vars.conn.executeSQL(String.format("SELECT username FROM users WHERE id = %d", playerID)).getString("username"));
	} catch (Exception e) {
	    // When player in fight is null
	    return null;
	}
    }

    /**
     * Gets current level in which player is located
     * 
     * @return current level in which player is located
     */
    public int getLevel() {
	try {
	    return Vars.conn.executeSQL(String.format("SELECT level FROM users WHERE id = %d", id)).getInteger("level");
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    /**
     * Get player's selected player' lock
     * 
     * @return currently selected lock (or -1 if no lock was in this level selected yet)
     */
    public int getSelectedLock() {
	try {
	    return Vars.conn.executeSQL(String.format("SELECT selectedLock FROM users WHERE id = %d", id)).getInteger("selectedLock");
	} catch (MySQLException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    /**
     * Gets matchmaking string of this user in database
     * 
     * @return current matchmaking string, can be null
     */
    public String getMatchmakingString() {
	try {
	    final String matchMakingString = Vars.conn.executeSQL(String.format("SELECT matchmaking FROM users WHERE id=%d", id)).getString("matchmaking");
	    if (matchMakingString.length() == 0)
		return null;
	    return matchMakingString;
	} catch (MySQLException e) {
	    return null;
	}
    }

    /**
     * Gets matchmaking_user string of this user in database
     * 
     * @return current matchmaking_user string, can be null
     */
    public String getMatchmakingUserString() {
	try {
	    final String matchMakingString = Vars.conn.executeSQL(String.format("SELECT matchmaking_user FROM users WHERE id=%d", id)).getString("matchmaking_user");
	    if (matchMakingString.length() == 0)
		return null;
	    return matchMakingString;
	} catch (MySQLException e) {
	    return null;
	}
    }

    /**
     * Sets all variables connected to the current match to default values
     */
    public void exitGame() {
	try {
	    Vars.conn.executeSQL(String.format(
		    "UPDATE users SET matchmaking = NULL, duelWith = NULL, selectedLock = -1, level = 0, onlineNumber=0, matchmaking=NULL, matchmaking_user=NULL WHERE id = %d",
		    id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets user's cash to new value
     * 
     * @param cash
     *            new cash value
     */
    public void setCash(long cash) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET cash = %d WHERE id = %d", cash, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Changes player's duelWith number to others player's id
     * 
     * @param player
     *            new duelWith value
     */
    public void setDuelWith(final Player player) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET duelWith = %d WHERE id = %d", player.id, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets user's count of lockpicks to new value
     * 
     * @param newValue
     *            new user's count of lockpicks to new value
     */
    public void setLockpicksNormal(int newValue) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET lockpicks_normal = %d WHERE id = %d", newValue, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets user's count of lockpicks to new value
     * 
     * @param newValue
     *            new user's count of lockpicks to new value
     */
    public void setLockpicksPlatinum(int newValue) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET lockpicks_platinum = %d WHERE id = %d", newValue, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets new level where is player located
     * 1 = start of the game
     * 
     * @param newValue
     */
    public void setLevel(int newValue) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET level = %d WHERE id = %d", newValue, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets player's selected lock
     * 
     * @param newValue
     *            -1 means no lock is selected
     */
    public void setSelectedLock(int newValue) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET selectedLock = %d WHERE id = %d", newValue, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets this player duel partner to player2
     * 
     * @param player2
     *            new partner in duel
     */
    public void setPlayerInFight(Player player2) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET duelWith = %d WHERE id = %d", player2.id, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    public void setCircleColor(Color color) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET color_circle_line = '%s' WHERE id = %d", Vars.color2Hex(color), id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    public void setBackgroundColor(Color color) {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET color_circle = '%s' WHERE id = %d", Vars.color2Hex(color), id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sets new password for user (saved in database as hash)
     * 
     * @param password
     *            password in raw format
     */
    public void setPassword(String password) {
	try {
	    password = Hash.hashText(password);
	    Vars.conn.executeSQL(String.format("UPDATE users SET password = '%s' WHERE id = %d", password, id));
	} catch (MySQLException | NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Changes matchmaking string and preferred player
     * 
     * @param matchimakingString
     *            string with preferences about matchmaking
     * @param filterPlayer
     *            preferred player for match
     */
    public void setMatchamaking(String matchimakingString, String filterPlayer) {
	if (matchimakingString == null)
	    matchimakingString = "NULL";
	try {
	    if (filterPlayer == null)
		filterPlayer = "NULL";
	    else
		filterPlayer = "'" + MySQL2PHP2Java.addSlashes(filterPlayer) + "'";
	    Vars.conn.executeSQL(String.format("UPDATE users SET matchmaking = '%s', matchmaking_user = %s WHERE id = %d", matchimakingString, filterPlayer, id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Basic properties in style KEY=VALUE;KEY2=VALUE2;
     */
    public static class DatabaseProperties {
	public List<String> keys = new ArrayList<String>();
	public List<String> values = new ArrayList<String>();

	public DatabaseProperties(String data) {
	    for (String upperLevel : data.split(";")) {
		String[] parts = upperLevel.split("=");
		keys.add(parts[0]);
		values.add(parts[1]);
	    }
	}

	/**
	 * Finds key and assigns value to it
	 * 
	 * @param key
	 *            key of the value
	 * @return value if found, null otherwise
	 */
	public String getString(String key) {
	    for (int i = 0; i < keys.size(); i++)
		if (keys.get(i).contentEquals(key))
		    return values.get(i);
	    return null;
	}
    }

    /**
     * Compares ids of this and player2
     * 
     * @param player2
     *            second player
     * @return true if both players have same id, false otherwise
     */
    public boolean isSameAs(Player player2) {
	if (player2.id == id)
	    return true;
	return false;
    }

    /**
     * Checks if user is online
     * 
     * @return true if online number is not 0 and has changed in last 20 seconds
     */
    public boolean isOnline() {
	if ((System.currentTimeMillis() - onlineNumberLastScan) < 10000) {
	    if (onlineNumber == 0)
		return false;
	    return true;
	}
	final int copyOfLastNumber = onlineNumber;
	if (getOnlineNumber() == copyOfLastNumber || onlineNumber == 0)
	    return false;
	return true;
    }

    /**
     * Increases online number by 1. It indicates that player is still active
     * 
     * @throws MySQLException
     */
    public void updateOnlineNumber() throws MySQLException {
	Vars.conn.executeSQL(String.format("UPDATE users SET onlineNumber=%d WHERE id = %d;", Vars.rnd.nextInt(1000000) + 1, id));
    }

    /**
     * Sets online number to 0
     */
    public void goOffline() {
	try {
	    Vars.conn.executeSQL(String.format("UPDATE users SET onlineNumber=0 WHERE id = %d", id));
	} catch (MySQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Checks if both players have each other's id as duel partner in database
     * 
     * @param player2
     *            player to check to
     * @return true if both players have each other's id as duel partner in database, false otherwise
     */
    public boolean isInFightWith(Player player2) {
	Player inFightWith = getPlayerInFight();
	Player p2_inFightWith = player2.getPlayerInFight();
	if (inFightWith == null || p2_inFightWith == null || !inFightWith.isSameAs(player2) || !p2_inFightWith.isSameAs(this))
	    return false;
	return true;
    }

    /**
     * Checks this player is already fighting with somebody
     * 
     * @return true if this players duel record is not null, false otherwise
     */
    public boolean isInFightWith() {
	return (getPlayerInFight() != null);
    }

    /**
     * Checks if player is waiting for game
     * Checks if player is online, isn't in fight, hasn't matchmaking string null, matchmaking 'user' is not in set mode
     * 
     * @return
     */
    public boolean isWaitingForGame() {
	if (isOnline() && !isInFightWith() && getMatchmakingString() != null
		&& (getMatchmakingUserString() == null || !getMatchmakingUserString().startsWith(Vars.matchmakingFilterSelection)))
	    return true;
	return false;
    }

    /**
     * Creates new player record in database
     * 
     * @param username
     *            username of new user
     * @return null if player exists or any other exception during creation, created player otherwise
     * @throws MySQLException
     */
    public static Player registerPlayer(String username) throws MySQLException {
	if (new Player(username).exists)
	    return null;
	Vars.conn.executeSQL(String.format("INSERT INTO `users` (`username`) VALUES ('%s');", username));
	return new Player(username);
    }
}
