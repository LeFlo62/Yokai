package fr.qmf.yokai.game.gui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
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
	
	private boolean draggingCard;
	private int xCardDrag, yCardDrag;

	private double xCardOffset, yCardOffset;
	
	public static final int DEFAULT_CARD_SIZE = 100;
	public static final int CARD_MARGIN = 18;

	public CardsLayer(YokaiGame game, Window window, UILayer parent) {
		super(window, parent, 0, 0, 0, 0);
		this.game = game;
	}
	
	@Override
	public void draw(Graphics2D g) {
		double animationDelta = 1f/game.getFPS();

		Card[][] board = game.getGameStorage().getBoard();
		
		width = board[0].length*(DEFAULT_CARD_SIZE + CARD_MARGIN);
		height = board.length*(DEFAULT_CARD_SIZE + CARD_MARGIN);
		
		double xCenter = (Window.WIDTH - board[0].length*(DEFAULT_CARD_SIZE + CARD_MARGIN))/2;
		double yCenter = (Window.HEIGHT - board.length*(DEFAULT_CARD_SIZE + CARD_MARGIN))/2;
		
		
		g.translate(xCenter, yCenter);
		
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
				
				if(draggingCard && j == xCardDrag && i == yCardDrag) {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				}
				
				g.drawImage(texture, j*(DEFAULT_CARD_SIZE + CARD_MARGIN) + (int)(card.isAnimated() ? DEFAULT_CARD_SIZE*(-Math.abs(animationTime/Card.ANIMATION_DURATION-0.5)+0.5) : 0),
						i*(DEFAULT_CARD_SIZE + CARD_MARGIN),
									(int) (DEFAULT_CARD_SIZE * (card.isAnimated() ? Math.abs((Card.ANIMATION_DURATION-2*animationTime)/Card.ANIMATION_DURATION) : 1)), DEFAULT_CARD_SIZE, null);
			
				if(draggingCard && j == xCardDrag && i == yCardDrag) {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
				}
			}
		}
		
		g.setTransform(new AffineTransform());
		
		if(draggingCard) {
			BufferedImage texture = Textures.getTexture("cards/back");
			
			GameLayer gameLayer = (GameLayer) parent;
			int dragCardSizeDelta = 7;
			int shadowOffset = 5;
			
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRoundRect((int)(window.getMouseX()-(xCardOffset+dragCardSizeDelta+shadowOffset)*gameLayer.getZoom()),
					(int)(window.getMouseY()-(yCardOffset+dragCardSizeDelta+shadowOffset)*gameLayer.getZoom()),
								(int)((DEFAULT_CARD_SIZE)*gameLayer.getZoom()), (int)((DEFAULT_CARD_SIZE)*gameLayer.getZoom()), 4*shadowOffset, 4*shadowOffset);
			
			g.drawImage(texture, (int)(window.getMouseX()-(xCardOffset+dragCardSizeDelta)*gameLayer.getZoom()),
					(int)(window.getMouseY()-(yCardOffset+dragCardSizeDelta)*gameLayer.getZoom()),
								(int)((DEFAULT_CARD_SIZE+2*dragCardSizeDelta)*gameLayer.getZoom()), (int)((DEFAULT_CARD_SIZE+2*dragCardSizeDelta)*gameLayer.getZoom()), null);
		}
	}
	
	public void setXCardDrag(int xCardDrag) {
		this.xCardDrag = xCardDrag;
	}
	
	public void setYCardDrag(int yCardDrag) {
		this.yCardDrag = yCardDrag;
	}
	
	public boolean isDragingCard() {
		return draggingCard;
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
	
	@Override
	public boolean drag(int dragStartX, int dragStartY, int screenX, int screenY, int x, int y, int dx, int dy) {
		return true;
	}

	@Override
	public void stopDragging(int stopDragX, int stopDragY) {
		draggingCard = false;
	}

	public void setDraggingCard(boolean dragingCard) {
		this.draggingCard = dragingCard;
	}

}
