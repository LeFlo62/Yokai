package fr.qmf.yokai.game;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.gui.layers.CardsLayer;
import fr.qmf.yokai.game.gui.layers.GameLayer;
import fr.qmf.yokai.game.gui.layers.HintsLayer;
import fr.qmf.yokai.io.audio.Sounds;

public class GameController {
	
	private YokaiGame game;
	private GameStorage storage;
	
	private double zoom = 1;

	private double panX, panY;
	private double scrollX, scrollY;
	private int minCardX = -1, minCardY = -1, maxCardX = -1, maxCardY = -1;
	
	private boolean draggingCard;
	private int xCardDrag, yCardDrag; // Card being dragged.

	private double xCardOffset, yCardOffset; // Mouse offset from upper-left card corner in pixels.
	
	private int hoverCardX, hoverCardY; // Card being hovered while dragging.
	
	private boolean draggingHint;
	private byte hintDragged; // Hint being dragged.
	
	private float hintFlippingAdvance;
	private int oldSize;
	
	public GameController(YokaiGame game) {
		this.game = game;
		this.storage = game.getGameStorage();
		
		detectGameDeckEdges();
	}
	
	public boolean cardDrag(double xCardDisplayed, double yCardDisplayed) {
		Card[][] board = storage.getBoard();
		
		int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		if(!draggingCard) {
			if(storage.isInsideBoard(xCard, yCard)) {
				Card card = board[yCard][xCard];
				if(card != null && !card.hasHint()) {
					board[yCard][xCard].setMoving(true);
					
					game.getSoundManager().playSound(Sounds.CARD_PICKING);
					
					draggingCard = true;
					xCardDrag = xCard;
					yCardDrag = yCard;
					xCardOffset = xCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
					yCardOffset = yCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
					return true;
				}
			}
		} else {
			hoverCardX = xCard;
			hoverCardY = yCard;
		}
		return false;
	}
	
	public boolean hintDrag(double xCardDisplayed, double yCardDisplayed) {
		int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		if(!draggingHint) {
			int hintIndex = (xCard-(maxCardX+2))+2*(yCard-(minCardY+1));
			if(hintIndex >= 0 && hintIndex < storage.getDiscoveredHints().size()) {
				if(xCard >= maxCardX+2 && xCard <= maxCardX+HintsLayer.HINT_DECK_X_OFFSET+HintsLayer.HINT_ROWS && yCard >= minCardY+1 && yCard <= minCardY+HintsLayer.HINT_DECK_Y_OFFSET+storage.getHints().length/HintsLayer.HINT_ROWS) {
					game.getSoundManager().playSound(Sounds.CARD_PICKING);
					
					draggingHint = true;
					hintDragged = storage.getDiscoveredHints().get(hintIndex);
					xCardOffset = xCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
					yCardOffset = yCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
					return true;
				}
			}

		} else {
			hoverCardX = xCard;
			hoverCardY = yCard;
		}
		return false;
	}
	
	public void stoppedDraggingCardOrHint(double xCardDisplayed, double yCardDisplayed) {
		int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		if(storage.getCurrentStage().equals(GameStage.MOVING) && draggingCard) {
			Card movingCard = storage.getBoard()[yCardDrag][xCardDrag];
				
			if(storage.isCorrectPlacement(xCard, yCard)) {
				storage.getBoard()[yCardDrag][xCardDrag] = null;
				
				int[] centerOffset = storage.centerBoardBorder(xCard, yCard);
				xCard += centerOffset[0];
				yCard += centerOffset[1];
				
				
				storage.getBoard()[yCard][xCard] = movingCard;
				
				storage.centerBoard();
				
				storage.setCurrentStage(storage.getCurrentStage().getNextStage());
				
				game.getSoundManager().playSound(Sounds.CARD_PLACING);
				
				detectGameDeckEdges();
			}
				
			movingCard.setMoving(false);
			draggingCard = false;
		} else if(storage.getCurrentStage().equals(GameStage.HINT) && draggingHint) {
			if(storage.isInsideBoard(xCard, yCard) && storage.getBoard()[yCard][xCard] != null && !storage.getBoard()[yCard][xCard].hasHint()) {
				storage.getBoard()[yCard][xCard].setHint(hintDragged);
				
				storage.getDiscoveredHints().remove((Object)hintDragged);
				storage.getPlacedHints().add(hintDragged);
				game.getSoundManager().playSound(Sounds.CARD_PLACING);
				
				storage.setCurrentStage(storage.getCurrentStage().getNextStage());
				storage.switchPlayers();
				
				if(storage.getPlacedHints().size() == storage.getHints().length) {
					game.endGame();
				}
			}
			
			draggingHint = false;
		}
	}
	
	public boolean clickCardOrHint(double xCardDisplayed, double yCardDisplayed) {
		int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		Card[][] board = storage.getBoard();
		
		if(game.getGameStorage().getCurrentStage().equals(GameStage.PLAY_OR_GUESS) || game.getGameStorage().getCurrentStage().equals(GameStage.OBSERVING)) {
			if(xCard < 0 || xCard >= board[0].length || yCard < 0 || yCard >= board.length) return false;
			
			if(storage.getCardsShown() == 2) return false;
			if(storage.getCardsShownCoords()[0] == xCard && storage.getCardsShownCoords()[1] == yCard) return false;
			
			Card card = board[yCard][xCard];
			if(card != null && !card.hasHint()) {
				game.getSoundManager().playSound(Sounds.CARD_FLIP);
				
				game.getGameStorage().setCurrentStage(GameStage.OBSERVING);
				card.flip();
				storage.getCardsShownCoords()[storage.getCardsShown()*2] = xCard;
				storage.getCardsShownCoords()[storage.getCardsShown()*2+1] = yCard;
				storage.setCardsShown(storage.getCardsShown()+1);
				
				if(storage.getCardsShown() == 2) {
					game.getScheduler().scheduleTask(new Runnable() {
						@Override
						public void run() {
							Card c1 = board[storage.getCardsShownCoords()[1]][storage.getCardsShownCoords()[0]];
							Card c2 = board[storage.getCardsShownCoords()[3]][storage.getCardsShownCoords()[2]];
							storage.setCardsShown(0);
							game.getSoundManager().playSound(Sounds.CARD_FLIP);
							c1.flip();
							c2.flip();
							game.getGameStorage().setCurrentStage(game.getGameStorage().getCurrentStage().getNextStage());
						}
					}, GameLayer.TIME_SHOWING_CARDS*20);
				}
			}
		}
		
		if(storage.getCurrentStage().equals(GameStage.HINT)) {
			if(xCard == maxCardX+2 && yCard == minCardY && storage.getHints()[storage.getHints().length-1] != 0) {
				storage.getDiscoveredHints().add(storage.getHints()[storage.getDiscoveredHints().size()+storage.getPlacedHints().size()]);
				storage.getHints()[storage.getDiscoveredHints().size()+storage.getPlacedHints().size()-1] = 0;
				storage.setCurrentStage(storage.getCurrentStage().getNextStage());
				
				storage.switchPlayers();
				
				game.getSoundManager().playSound(Sounds.CARD_FLIP);
				return true;
			}
		}
		
		return false;
	}
	
	public void detectGameDeckEdges() {
		int[] coords = storage.detectGameDeckEdges();
		minCardX = coords[0];
		minCardY = coords[1];
		maxCardX = coords[2];
		maxCardY = coords[3];
	}

	public double getZoom() {
		return zoom;
	}
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
	public double getPanX() {
		return panX;
	}
	public void setPanX(double panX) {
		this.panX = panX;
	}
	public double getPanY() {
		return panY;
	}
	public void setPanY(double panY) {
		this.panY = panY;
	}
	public double getScrollX() {
		return scrollX;
	}
	public void setScrollX(double scrollX) {
		this.scrollX = scrollX;
	}
	public double getScrollY() {
		return scrollY;
	}
	public void setScrollY(double scrollY) {
		this.scrollY = scrollY;
	}
	public int getMinCardX() {
		return minCardX;
	}
	public void setMinCardX(int minCardX) {
		this.minCardX = minCardX;
	}
	public int getMinCardY() {
		return minCardY;
	}
	public void setMinCardY(int minCardY) {
		this.minCardY = minCardY;
	}
	public int getMaxCardX() {
		return maxCardX;
	}
	public void setMaxCardX(int maxCardX) {
		this.maxCardX = maxCardX;
	}
	public int getMaxCardY() {
		return maxCardY;
	}
	public void setMaxCardY(int maxCardY) {
		this.maxCardY = maxCardY;
	}

	public GameStorage getStorage() {
		return storage;
	}

	public void setStorage(GameStorage storage) {
		this.storage = storage;
	}

	public boolean isDraggingCard() {
		return draggingCard;
	}

	public void setDraggingCard(boolean draggingCard) {
		this.draggingCard = draggingCard;
	}

	public int getXCardDrag() {
		return xCardDrag;
	}

	public void setXCardDrag(int xCardDrag) {
		this.xCardDrag = xCardDrag;
	}

	public int getYCardDrag() {
		return yCardDrag;
	}

	public void setYCardDrag(int yCardDrag) {
		this.yCardDrag = yCardDrag;
	}

	public double getXCardOffset() {
		return xCardOffset;
	}

	public void setXCardOffset(double xCardOffset) {
		this.xCardOffset = xCardOffset;
	}

	public double getYCardOffset() {
		return yCardOffset;
	}

	public void setYCardOffset(double yCardOffset) {
		this.yCardOffset = yCardOffset;
	}

	public int getHoverCardX() {
		return hoverCardX;
	}

	public void setHoverCardX(int hoverCardX) {
		this.hoverCardX = hoverCardX;
	}

	public int getHoverCardY() {
		return hoverCardY;
	}

	public void setHoverCardY(int hoverCardY) {
		this.hoverCardY = hoverCardY;
	}

	public int getxCardDrag() {
		return xCardDrag;
	}

	public void setxCardDrag(int xCardDrag) {
		this.xCardDrag = xCardDrag;
	}

	public int getyCardDrag() {
		return yCardDrag;
	}

	public void setyCardDrag(int yCardDrag) {
		this.yCardDrag = yCardDrag;
	}

	public double getxCardOffset() {
		return xCardOffset;
	}

	public void setxCardOffset(double xCardOffset) {
		this.xCardOffset = xCardOffset;
	}

	public double getyCardOffset() {
		return yCardOffset;
	}

	public void setyCardOffset(double yCardOffset) {
		this.yCardOffset = yCardOffset;
	}

	public boolean isDraggingHint() {
		return draggingHint;
	}

	public void setDraggingHint(boolean draggingHint) {
		this.draggingHint = draggingHint;
	}

	public byte getHintDragged() {
		return hintDragged;
	}

	public void setHintDragged(byte hintDragged) {
		this.hintDragged = hintDragged;
	}

	public float getHintFlippingAdvance() {
		return hintFlippingAdvance;
	}

	public void setHintFlippingAdvance(float hintFlippingAdvance) {
		this.hintFlippingAdvance = hintFlippingAdvance;
	}

	public int getOldSize() {
		return oldSize;
	}

	public void setOldSize(int oldSize) {
		this.oldSize = oldSize;
	}
	
	

}
