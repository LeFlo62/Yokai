package fr.qmf.yokai.io.audio;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import fr.qmf.yokai.Main;

public class Sound implements LineListener {
	
	private Sounds sounds;
	private String file;
	private int id;
	
	private Clip clip;
	private boolean paused;
	private boolean ended;
	
	public Sound(Sounds sounds, String file, int id) {
		this.sounds = sounds;
		this.file = file;
		this.id = id;
	}
	
	public void play() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		clip = AudioSystem.getClip();
		clip.open(AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("/assets/sounds/" + file + ".wav")));
		clip.start();
		clip.addLineListener(this);
	}
	
	public void stop() {
		if(clip != null) clip.stop();
	}
	
	public Sounds getSounds() {
		return sounds;
	}
	
	public String getFile() {
		return file;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void pause() {
		paused = true;
		if(clip != null) clip.stop();
	}
	
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
	
	public float getVolume() {
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    return (float) Math.pow(10f, gainControl.getValue() / 20f);
	}

	public void setVolume(float volume) {
	    if (volume < 0f || volume > 1f)
	        throw new IllegalArgumentException("Volume not valid: " + volume);
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(volume));
	}

}
