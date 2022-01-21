package fr.qmf.yokai.io.audio;

import java.util.Arrays;
import java.util.Random;

import fr.qmf.yokai.Tickable;

/**
 * SoundManager is used to play and instance a Sound in the game.
 * @author LeFlo
 *
 */
public class SoundManager implements Tickable {
	
	public static final int SIMULTANEOUS_CLIPS = 256;
	private float DEFAULT_SOUND_VALUE = 1f;
	private Sound[] sounds = new Sound[SIMULTANEOUS_CLIPS];
	private Random random;

	private int mainMusicThreshold;
	
	private float[] soundTypeVolumes = new float[SoundType.values().length];
	private Sound mainMusic;
	
	public SoundManager(Random random) {
		this.random = random;
		Arrays.fill(soundTypeVolumes, DEFAULT_SOUND_VALUE);
	}
	
	/**
	 * Plays the specified Sounds. If multiple Sounds may be played, a random one will be picked.
	 * @param sounds The Sounds to be played.
	 * @return The Sound instance of this Sounds.
	 */
	public Sound playSound(Sounds sound) {
		for(int i = 0; i < sounds.length; i++) {
			if(sounds[i] == null || sounds[i].isEnded()) {
				String file = sound.getFiles()[random.nextInt(sound.getFiles().length)];
				sounds[i] = new Sound(sound, file, i);
				try {
					sounds[i].play();
				} catch (Exception e) {
					e.printStackTrace();
				}
				sounds[i].setVolume(soundTypeVolumes[sound.getType().ordinal()]);
				
				return sounds[i];
			}
		}
		return null;
	}
	
	/**
	 * Changes and updates the volumes for the given SoundType.
	 * @param type The SoundType to change its volume.
	 * @param volume The volume to set the given SoundType.
	 */
	public void setSoundTypeVolume(SoundType type, float volume) {
		this.soundTypeVolumes[type.ordinal()] = volume;
		updateVolumes();
	}
	
	/**
	 * Updates all sounds to use the new volumes.
	 */
	public void updateVolumes() {
		for(Sound sound : sounds) {
			if(sound != null && !sound.isEnded()) {
				sound.setVolume(soundTypeVolumes[sound.getSounds().getType().ordinal()]);
			}
		}
	}
	
	/**
	 * Gets a Sound instance from the given id.
	 * WARNING: an id may be non persistant through time. If the Sound previously associated with this id ends, a new sound being played may take its id.
	 * @param id The id of the sound.
	 * @return The sound instance associated to this id or null if non-existant.
	 */
	public Sound getSound(int id) {
		return sounds[id];
	}
	
	/**
	 * The Main Music Sound instance being played.
	 * @return
	 */
	public Sound getMainMusic() {
		return mainMusic;
	}

	@Override
	public void tick() {
		if(mainMusicThreshold >= 0) {
			if(mainMusicThreshold == 0) {
				this.mainMusic = playSound(Sounds.MAIN_MUSIC);
				this.mainMusic.addEndListener(new Runnable() {
					@Override
					public void run() {
						mainMusicThreshold = random.nextInt((4*60-30)*20)+30*20;
						mainMusic = null;
					}
				});
			}
			mainMusicThreshold--;
		}
	}

}
