package fr.qmf.yokai.io.audio;

public enum Sounds {
	CARD_FLIP(SoundType.EFFECT, "cards/card_flip_1", "cards/card_flip_2", "cards/card_flip_3"),
	CARD_PICKING(SoundType.EFFECT, "cards/picking"),
	CARD_PLACING(SoundType.EFFECT, "cards/placing"),
	MAIN_MUSIC(SoundType.MUSIC, "musics/main_1", "musics/main_2", "musics/main_3"),
	PAUSE(SoundType.MUSIC, "musics/pause"),
	;
	
	private SoundType type;
	private String[] files;

	private Sounds(SoundType type, String file, String... files) {
		this.type = type;
		
		String[] allFiles = new String[files.length+1];
		allFiles[0] = file;
		System.arraycopy(files, 0, allFiles, 1, files.length);
		this.files = allFiles;
	}
	
	public SoundType getType() {
		return type;
	}
	
	public String[] getFiles() {
		return files;
	}

}
