package game.aho.lps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Jukebox {
    private static int index = 0;
    private static Wav[] musicList;
    private static Wav currentMusic = null;
    public static Float volume = null;
    public static AtomicBoolean stopped = new AtomicBoolean(true);

    static {
	reloadMusic();
    }

    /**
     * Plays currently indexed song (indexes are changed using next() and prev())
     */
    public static void play() {
	stop();
	if (musicList.length == 0)
	    return;
	currentMusic = null;
	currentMusic = musicList[index];
	if (volume != null)
	    currentMusic.setVolume(volume);
	currentMusic.play();
	currentMusic.addPlaingCompletedListner(new Wav.PlayingCompletedListner() {

	    @Override
	    public void action(File playedFile) {
		if (!stopped.get())
		    next();
	    }
	});
	stopped.set(false);
	String songName = currentMusic.fileToPlay.getName();
	songName = songName.substring(0, songName.indexOf("."));
	songName = songName.replaceAll("_", " ");
	Vars.settings.lblCurrentsong.setText(songName);
    }

    /**
     * Plays next song
     */
    public static void next() {
	index++;
	if (index >= musicList.length)
	    index = 0;
	play();
    }

    /**
     * Plays random song
     */
    public static void random() {
	index = Vars.rnd.nextInt(musicList.length);
	play();
    }

    /**
     * Plays previous song
     */
    public static void prev() {
	index--;
	if (index <= 0)
	    index = musicList.length - 1;
	play();
    }

    /**
     * Pauses current song
     */
    public static void pause() {
	currentMusic.pause();
    }

    /**
     * Reloads avaible music list from directory
     */
    public static void reloadMusic() {
	index = 0;
	File[] fileList = new File(Vars.dataSoundMusic).listFiles();
	List<Wav> musicList = new ArrayList<Wav>();
	for (File file : fileList)
	    if (file.getName().toLowerCase().endsWith(".wav"))
		musicList.add(new Wav(file));
	Jukebox.musicList = musicList.toArray(new Wav[musicList.size()]);
    }

    /**
     * Stops playing
     */
    public static void stop() {
	stopped.set(true);
	if (currentMusic != null)
	    currentMusic.stop();
    }

    /**
     * Sets lowering volume
     * 
     * @param percentage
     *            how much percents can be heard
     */
    public static void setVolume(int percentage) {
	final int lowestDB = 50; // How far under zero can be sound heared
	int dbValue = -1 * (lowestDB - (lowestDB * percentage / 100));
	Vars.settings.slidMusicVolume.setMaximum(100);
	Vars.settings.slidMusicVolume.setValue(percentage);
	volume = (float) dbValue;
	if (currentMusic != null)
	    currentMusic.setVolume(volume);
    }
}
