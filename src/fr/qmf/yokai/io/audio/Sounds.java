package fr.qmf.yokai.io.audio;

public enum Sounds {
	CARD_FLIP("cards/card_flip_1", "cards/card_flip_2", "cards/card_flip_3")
	;
	
	private String[] files;

	private Sounds(String file, String... files) {
		String[] allFiles = new String[files.length+1];
		allFiles[0] = file;
		System.arraycopy(files, 0, allFiles, 1, files.length);
		this.files = allFiles;
	}
	
	public String[] getFiles() {
		return files;
	}

}
