package fr.qmf.yokai.game;

public enum GameStage {
	
	OBSERVING(0),
	MOVING(1),
	HINT(2);
	
	private int number;
	
	private GameStage(int number) {
		this.number = number;
	}
	
	public GameStage getNextStage() {
		return values()[(number+1)%values().length];
	}

}
