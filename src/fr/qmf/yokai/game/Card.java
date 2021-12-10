package fr.qmf.yokai.game;

public class Card {
	
	private YokaiType type;
	private byte hint;
	
	private boolean shown;
	private boolean animated;
	private double animationTime;
	
	public static final double ANIMATION_DURATION = 0.7;

	public Card(YokaiType type) {
		this.type = type;
	}
	
	public void flip() {
		if(!animated) {
			animated = true;
			animationTime = 0;
		}
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

	public boolean isShown() {
		return shown;
	}

	public void setShown(boolean shown) {
		this.shown = shown;
	}
	
	public double getAnimationTime() {
		return animationTime;
	}

	public void setAnimationTime(double animationTime) {
		this.animationTime = animationTime;
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}
	
}
