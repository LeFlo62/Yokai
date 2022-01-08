package fr.qmf.yokai.io.audio;

import java.util.Random;

public class SoundManager {
	
	public static final int SIMULTANEOUS_CLIPS = 256;
	private Sound[] sound = new Sound[SIMULTANEOUS_CLIPS];
	private Random random;
	
	public SoundManager(Random random) {
		this.random = random;
	}
	
	/**
	 * Plays the specified Sounds. If multiple Sounds may be played, a random one will be picked.
	 * @param sounds The Sounds to be played.
	 * @return The Sound instance of this Sounds.
	 */
	public Sound playSound(Sounds sounds) {
		for(int i = 0; i < sound.length; i++) {
			if(sound[i] == null || sound[i].isEnded()) {
				String file = sounds.getFiles()[random.nextInt(sounds.getFiles().length)];
				sound[i] = new Sound(sounds, file, i);
				
				try {
					sound[i].play();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return sound[i];
			}
		}
		return null;
	}
	
	public Sound getSound(int id) {
		return sound[id];
	}

}
