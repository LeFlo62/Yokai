package fr.qmf.yokai.game;

public enum GameStage {
	
	PLAY_OR_GUESS(0, "Observez des Yokai ou déclarez qu'ils sont appaisés"),
	OBSERVING(1, "Observez un Yokai"),
	MOVING(2, "Déplacez un Yokai"),
	HINT(3, "Préparez un indice ou utilisez-en un"),
	END(10, "Fin du Jeu."),
	;
	
	private int number;
	private String description;
	
	private GameStage(int number, String description) {
		this.number = number;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public GameStage getNextStage() {
		if(this == END) return END;
		return values()[(number+1)%(values().length-1)];
	}

}
