package game.aho.lps;

public enum GameMode {
    ANY("ANY", -1), FREE_FOR_ALL("FREE FOR ALL", 0), MIDDLE("GET TO THE MIDDLE", 1), COUNT_TOGETHER("COUNT TOGETHER", 2);

    GameMode(final String realName, final int number) {
	this.realName = realName;
	this.number = number;
    }

    GameMode(final int number) {
	this.realName = null;
	this.number = number;
    }

    @Override
    public String toString() {
	if (realName != null)
	    return realName;
	else
	    return super.toString();
    }

    public static GameMode getByNumber(final int number) {
	for (GameMode gm : GameMode.values())
	    if (gm.number == number)
		return gm;
	return null;
    }

    private final String realName;
    public final int number;
}
