package fr.qmf.yokai.io.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import fr.qmf.yokai.Main;

/**
 * Represents a Sound being played.
 * @author LeFlo
 *
 */
public class Sound implements LineListener {
	
	private Sounds sounds;
	private String file;
	private int id;
	
	private Clip clip;
	private boolean paused;
	private boolean ended;
	
	private List<Runnable> endedListeners = new ArrayList<>();
	
	private static final float DAMPING_FACTOR = 10f;

	/**
	 * A Sound instance is the representation of a sound to be played.
	 * @param sounds The Sounds to be played.
	 * @param file The file name of this Sound.
	 * @param id The given id for this Sound.
	 */
	public Sound(Sounds sounds, String file, int id) {
		this.sounds = sounds;
		this.file = file;
		this.id = id;
	}
	
	/**
	 * Starts this Sound
	 * 
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public void play() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		clip = AudioSystem.getClip();
		clip.open(AudioSystem.getAudioInputStream(Main.class.getResource("/assets/sounds/" + file + ".wav")));
		clip.start();
		clip.addLineListener(this);
	}
	
	/**
	 * Stops this Sound
	 */
	public void stop() {
		if(clip != null) clip.stop();
	}
	
	/**
	 * Gets the Sounds that originated this Sound.
	 * @return
	 */
	public Sounds getSounds() {
		return sounds;
	}
	
	/**
	 * Gets the file name of the Sound being played.
	 * @return the file name of the Sound being played.
	 */
	public String getFile() {
		return file;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Adds a runnable to be executed when this Sound ends.
	 * @param runnable
	 */
	public void addEndListener(Runnable runnable) {
		this.endedListeners.add(runnable);
	}
	
	/**
	 * Pauses this Sound.
	 */
	public void pause() {
		paused = true;
		if(clip != null) clip.stop();
	}
	
	/**
	 * Resumes this Sound.
	 */
	public void resume() {
		paused = false;
		if(clip != null) clip.start();
	}
	
	public boolean isEnded() {
		return ended;
	}

	@Override
	public void update(LineEvent event) {
		if(!paused && event.getType() == LineEvent.Type.STOP) {
			ended = true;
			
			clip.close();
			clip.flush();
			endedListeners.forEach(Runnable::run);
		}
	}
	
	/**
	 * Sets this Sound to loop or not.
	 * @param loop Whether or not this Sound should be looping.
	 */
	public void setLooping(boolean loop) {
		if(loop) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.loop(0);
		}
	}
	
	/**
	 * Returns the volume between 0f and 2f.
	 * @return
	 */
	public float getVolume() {
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    return (float) Math.pow(10f, gainControl.getValue() / 20f);
	}

	/**
	 * Sets the volume. The volume given should be between 0f and 2f.
	 * @param volume
	 */
	public void setVolume(float volume) {
		volume /= DAMPING_FACTOR;
	    if (volume < 0f || volume > 2f)
	        throw new IllegalArgumentException("Volume not valid: " + volume);
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(volume));
	}

}
