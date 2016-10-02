package game.aho.lps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * This class plays wav files
 * 
 * @author Adam Hlavacek
 */

public class Wav {
    public static boolean soundEnabled = true;
    private static float globalVolume = 0; // Default dB delta for all sounds.
    public File fileToPlay = null; // File to play

    private boolean loop = false; // If true, audio loops until stopped or loop is set to false
    private boolean here = false; // If true, executing thread waits until isPlaying() is false
    private boolean autoplay = false; // Play immediately when new file is specified
    private Integer pausedFrame = null;

    private List<PlayingCompletedListner> listeners = new ArrayList<PlayingCompletedListner>();

    private FloatControl volumeControl;

    private Float volume = null;

    public Clip clip = null;
    private AudioInputStream inputStream;

    public interface PlayingCompletedListner {
	public void action(File playedFile);
    }

    /**
     * Initializes without specifying source file.
     */
    public Wav() {

    }

    /**
     * Init new wav file for playing
     * 
     * @param path
     *            path to the file
     */
    public Wav(String path) {
	fileToPlay = new File(path);
    }

    /**
     * Init new wav file for playing
     * 
     * @param file
     *            File to be played
     */
    public Wav(File file) {
	fileToPlay = file;
    }

    /**
     * Plays audio in new thread
     */
    public void play() {
	play(false);
    }

    /**
     * Plays audio
     * 
     * @param here
     *            if true, executing thread waits until playing is finished (or stopped)
     */
    private void play(boolean here) {
	if (!soundEnabled)
	    return;
	if (fileToPlay == null)
	    return;

	this.here = here;
	try {
	    inputStream = AudioSystem.getAudioInputStream(fileToPlay);
	    AudioFormat format = inputStream.getFormat();
	    DataLine.Info info = new DataLine.Info(Clip.class, format);
	    clip = (Clip) AudioSystem.getLine(info);
	    clip.open(inputStream);
	    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	    if (volume != null)
		volumeControl.setValue(volume);
	    else
		volumeControl.setValue(globalVolume);
	    if (pausedFrame != null)
		clip.setFramePosition(pausedFrame);
	    else
		clip.setFramePosition(0);
	    pausedFrame = null;
	    clip.addLineListener(new LineListener() {

		@Override
		public void update(LineEvent event) {
		    if (event.getType() == LineEvent.Type.STOP && pausedFrame == null) {
			if (event.getLine().isOpen())
			    event.getLine().close();
			for (PlayingCompletedListner listener : listeners)
			    listener.action(fileToPlay);
		    }
		}

	    });

	    if (!loop)
		clip.start();
	    else
		clip.loop(Clip.LOOP_CONTINUOUSLY);

	    if (here)
		waitFor();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Saves current position from which will sound stars on calling play() and stops without calling any listener
     */
    public void pause() {
	pausedFrame = clip.getFramePosition();
	clip.stop();
    }

    /**
     * Removes saved pause position
     */
    public void pauseRemove() {
	pausedFrame = null;
    }

    /**
     * Adds new listener waiting for end of playing
     * 
     * @param listener
     */
    public void addPlaingCompletedListner(PlayingCompletedListner listener) {
	listeners.add(listener);
    }

    /**
     * Changes file to play. Is currently playing, stop current file and play new file.
     * If new file is same as current, does nothing
     * If autoplay is true, starts playing it
     * 
     * @param newFileToPlay
     *            new audio source file
     */
    public void changeFile(final File newFileToPlay) {
	if ((fileToPlay != null) && (fileToPlay.getAbsolutePath().contentEquals(newFileToPlay.getAbsolutePath())))
	    return;
	fileToPlay = newFileToPlay;
	if (isPlaying() || autoplay) {
	    stop();
	    play(here);
	}
    }

    /**
     * Changes file to play. Is currently playing, stop current file and play new file.
     * If new file is same as current, does nothing
     * 
     * @param newFileToPlayPath
     *            path to new audio source file
     */
    public void changeFile(final String newFileToPlayPath) {
	changeFile(new File(newFileToPlayPath));
    }

    /**
     * Plays audio with executing thread waiting until playing is finished (or stopped)
     */
    public void playHere() {
	play(true);
    }

    /**
     * Sets looping for this audio file
     * 
     * If audio is already playing, stop it and start it on the same frame with new loop settings
     * 
     * @param loop
     */
    public void setLoop(boolean loop) {
	if (loop == this.loop)
	    return;
	this.loop = loop;
	if (isPlaying()) {
	    final int currFrame = clip.getFramePosition();
	    stop();
	    play(here);
	    clip.setFramePosition(currFrame);
	}
    }

    /**
     * Sets new dB delta
     * 
     * @param volume
     *            new dB delta
     */
    public void setVolume(float volume) {
	this.volume = volume;
	if (isPlaying())
	    volumeControl.setValue(volume);
    }

    /**
     * Stops the clip
     */
    public void stop() {
	if (clip != null) {
	    clip.stop();
	    clip.close();
	}
	clip = null;
    }

    /**
     * Checks if audio is playing by checking clip's frame position
     * 
     * @return true if audio is playing, false otherwise
     */
    public boolean isPlaying() {
	// Clip's position ends one frame before its length, so we have to decrease comparison by 1
	if ((clip != null) && ((clip.getFrameLength() - 1) > clip.getFramePosition())) {
	    return true;
	}
	return false;
    }

    /**
     * Sleeps executing thread for 2 milliseconds as long as isPlaying returns true
     * 
     * @throws InterruptedException
     */
    public void waitFor() throws InterruptedException {
	while (isPlaying())
	    Thread.sleep(2);
    }

    /**
     * If set to true, starts playing every time new file is specified.
     * 
     * @param autoplay
     *            new autoplay settings
     */
    public void setAutoPlay(boolean autoplay) {
	this.autoplay = autoplay;
	if (autoplay && !isPlaying() && fileToPlay != null)
	    play();
    }

    /**
     * Sets new default volume for all sounds
     * 
     * @param volume
     *            new default dB delta
     */
    public static void setGlobalVolume(float volume) {
	globalVolume = volume;
    }

    /**
     * Returns absolute path to audio file
     * 
     * @return absolute path to audio file
     */
    @Override
    public String toString() {
	return fileToPlay.getAbsolutePath();
    }
}
