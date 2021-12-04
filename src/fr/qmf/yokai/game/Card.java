package fr.qmf.yokai.game;

public class Card {
	
	private YokaiType type;
	private byte hint;
	

	public Card(YokaiType type) {
		this.type = type;
	}
	
    public YokaiType getType() {
		return type;
	}
    
    public byte getHint() {
		return hint;
	}
    
    public boolean hasHint() {
    	return hint != 0;
    }


}
