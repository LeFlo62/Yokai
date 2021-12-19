package fr.qmf.yokai.game;

public enum GameStage {
	
	PLAY_OR_GUESS("Observez des Yokai ou déclarez qu'ils sont appaisés"),
	OBSERVING("Observez un Yokai"),
	MOVING("Déplacez un Yokai"),
	HINT("Préparez un indice ou utilisez-en un"),
	END("Fin du Jeu."),
	;
	
	private String description;
	
	private GameStage(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public GameStage getNextStage() {
		if(this == END) return END;
		return values()[(ordinal()+1)%(values().length-1)];
	}

}
