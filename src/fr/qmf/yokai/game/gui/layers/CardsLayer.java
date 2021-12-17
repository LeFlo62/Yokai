package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.YokaiType;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.Dragable;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;

/**
 * A UILayer that displays all the cards from YokaiGame.
 * They are drawn dynamically and directly in the draw function.
 * This is due to the ever-changing nature of the Card board.
 */
public class CardsLayer extends UILayer implements Dragable {

	private YokaiGame game;
	
	private boolean dragingCard;
	private int xCardDrag, yCardDrag;

	private int mouseX, mouseY;

	private double xCardOffset, yCardOffset;
	
	public static final int DEFAULT_CARD_SIZE = 130;
	public static final int CARD_MARGIN = 20;

	public CardsLayer(YokaiGame game, Window window, UILayer parent) {
		super(window, parent, 0, 0, 0, 0);
		this.game = game;
	}
	
	@Override
	public void draw(Graphics g) {
		double animationDelta = 1f/game.getFPS();
		Graphics2D g2d = (Graphics2D) g;

		Card[][] board = game.getGameStorage().getBoard();
		
		width = board[0].length*(DEFAULT_CARD_SIZE + CARD_MARGIN);
		height = board.length*(DEFAULT_CARD_SIZE + CARD_MARGIN);
		
		double xCenter = (Window.WIDTH - board[0].length*(DEFAULT_CARD_SIZE + CARD_MARGIN))/2;
		double yCenter = (Window.HEIGHT - board.length*(DEFAULT_CARD_SIZE + CARD_MARGIN))/2;
		
		
		g2d.translate(xCenter, yCenter);
		
		for(int i = 0; i < board[0].length; i++) {
			for(int j = 0; j < board.length; j++) {
				Card card = board[i][j];
				if(card == null) continue;
				
				BufferedImage texture = Textures.getTexture("cards/back");
				if(card.isShown()) {
					texture = Textures.getTexture("cards/" + card.getType().getColor());
				}
				if(card.hasHint()) {
					YokaiType[] yokaiTypes = YokaiType.getYokaiFromHint(card.getHint());
					String[] yokaiString = new String[yokaiTypes.length];
					for(int k = 0; k < yokaiTypes.length; k++) {
						yokaiString[k] = yokaiTypes[k].getColor();
					}
					String s = String.join("_", yokaiString);
					System.out.println(s);
					texture = Textures.getTexture("hints/" + s);
				}
				
				double animationTime = 0;
				if(card.isAnimated()) {
					card.setAnimationTime(card.getAnimationTime() + animationDelta);
					if(card.getAnimationTime() >= 0) {
						animationTime = card.getAnimationTime();
					}
					if(animationTime >= Card.ANIMATION_DURATION/2) {
						if(card.isShown()) {
							texture = Textures.getTexture("cards/back");
						} else {
							texture = Textures.getTexture("cards/" + card.getType().getColor());
						}
					}
					if(animationTime >= Card.ANIMATION_DURATION) {
						card.setAnimated(false);
						card.setShown(!card.isShown());
					}
				}
				
				//TODO Image rotation cause an image flip (see the shadow change side)
				if(!dragingCard || (j != xCardDrag || i != yCardDrag)) {
					g.drawImage(texture, j*(DEFAULT_CARD_SIZE + CARD_MARGIN) + (int)(card.isAnimated() ? DEFAULT_CARD_SIZE - DEFAULT_CARD_SIZE * animationTime/Card.ANIMATION_DURATION : 0),
							i*(DEFAULT_CARD_SIZE + CARD_MARGIN),
										(int) (DEFAULT_CARD_SIZE * (card.isAnimated() ? -(Card.ANIMATION_DURATION-2*animationTime)/Card.ANIMATION_DURATION : 1)), DEFAULT_CARD_SIZE, null);
				}
			}
		}
		
		g2d.setTransform(new AffineTransform());
		//g2d.translate(-xCenter, -yCenter);
		
		if(dragingCard) {
			BufferedImage texture = Textures.getTexture("cards/back");
			
			GameLayer gameLayer = (GameLayer) parent;
			
			g.drawImage(texture, (int)(mouseX-xCardOffset),
					(int)(mouseY-yCardOffset),
								(int)(DEFAULT_CARD_SIZE*gameLayer.getZoom()), (int)(DEFAULT_CARD_SIZE*gameLayer.getZoom()), null);
		}
	}
	
	public void setXCardDrag(int xCardDrag) {
		this.xCardDrag = xCardDrag;
	}
	
	public void setYCardDrag(int yCardDrag) {
		this.yCardDrag = yCardDrag;
	}
	
	public boolean isDragingCard() {
		return dragingCard;
	}
	
	public int getXCardDrag() {
		return xCardDrag;
	}
	
	public int getYCardDrag() {
		return yCardDrag;
	}

	public void setXCardOffset(double xCardOffset) {
		this.xCardOffset = xCardOffset;
	}
	
	public void setYCardOffset(double yCardOffset) {
		this.yCardOffset = yCardOffset;
	}
	
	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}
	
	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	@Override
	public boolean drag(int dragStartX, int dragStartY, int screenX, int screenY, int x, int y, int dx, int dy) {
		mouseX += dx;
		mouseY += dy;
		
		return true;
	}

	@Override
	public void stopDragging(int stopDragX, int stopDragY) {
		dragingCard = false;
	}

	public void setDragingCard(boolean dragingCard) {
		this.dragingCard = dragingCard;
	}

}
