package fr.qmf.yokai.game;

/**
 * Represents the current GameStage.
 * @author LeFlo
 *
 */
public enum GameStage {
	
	PLAY_OR_GUESS("Observez des Yokai ou déclarez qu'ils sont apaisés"),
	OBSERVING("Observez un Yokai"),
	MOVING("Déplacez un Yokai"),
	HINT("Préparez un indice ou utilisez-en un"),
	END("Fin du Jeu."),
	;
	
	private String description;
	
	private GameStage(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the description associated with this GameStage.
	 * @return the description associated with this GameStage.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the succeeding stage from this one.
	 * Exception case: if this == END, then END is returned.
	 * This is because there is no GameStage after the end of the game.
	 * @return the succeeding stage from this one.
	 */
	public GameStage getNextStage() {
		if(this == END) return END;
		return values()[(ordinal()+1)%(values().length-1)];
	}

}
