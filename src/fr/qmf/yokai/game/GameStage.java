package fr.qmf.yokai.game;

public enum GameStage {
	
	PLAY_OR_GUESS(0),
	OBSERVING(1),
	MOVING(2),
	HINT(3);
	
	private int number;
	
	private GameStage(int number) {
		this.number = number;
	}
	
	public GameStage getNextStage() {
		return values()[(number+1)%values().length];
	}

}
