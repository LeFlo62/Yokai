package fr.qmf.yokai.game;

import java.io.Serializable;

/**
 * Represents a Card in the game.
 * @author LeFlo
 *
 */
public class Card implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final double ANIMATION_DURATION = 0.7;
	
	private YokaiType type;
	private byte hint;
	
	private boolean shown;
	private boolean animated;
	private double animationTime;
	
	private boolean moving;
	

	public Card(YokaiType type) {
		this.type = type;
	}
	
	/**
	 * Starts an animation for the card to flip. Changing from showing back to showing the Yokai and vice-versa.
	 */
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
    
    public void setHint(byte hint) {
		this.hint = hint;
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
	
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public boolean isMoving() {
		return moving;
	}
	
}
