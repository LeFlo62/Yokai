package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStage;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.io.audio.Sounds;
import fr.qmf.yokai.ui.Clickable;
import fr.qmf.yokai.ui.Dragable;
import fr.qmf.yokai.ui.MouseWheelSensitive;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.Button;
import fr.qmf.yokai.ui.components.TextComponent;

public class GameLayer extends UILayer implements Tickable, Dragable, MouseWheelSensitive, Clickable {

	private static final int TIME_SHOWING_CARDS = 3;
	
	private YokaiGame game;
	private BufferedImage background;
	
	private PauseLayer pauseLayer;
	private CardsLayer cardsLayer;
	private HintsLayer hintsLayer;
	
	private TextComponent gameStageText;
	private Button yokaiPleasedButton;
	
	private double zoom = 1;

	private double panX, panY;
	private double scrollX, scrollY;
	private int minCardX = -1, minCardY = -1, maxCardX = -1, maxCardY = -1;


	public GameLayer(YokaiGame game, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		background = Textures.getTexture("backgrounds/game_repeat");
		
		cardsLayer = new CardsLayer(game, window, this);
		hintsLayer = new HintsLayer(game, window, this);
		
		
		Image yokaiPleasedButtonImage = Textures.getTexture("gui/buttons/yokai_pleased_button").getScaledInstance(300, 50, Image.SCALE_SMOOTH);
		yokaiPleasedButton = new Button(this, (window.getWidth() - 300)/2, window.getHeight() - 50 - 50, 300, 50) {
			
			@Override
			public void hover(int screenX, int screenY, int x, int y) {

			}
			
			@Override
			public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
				game.endGame();
				return true;
			}
			
			@Override
			public void draw(Graphics2D g) {
				int delta = hovered ? 4 : 0;
				g.drawImage(yokaiPleasedButtonImage, x-delta, y-delta, width+2*delta, height+2*delta, null);
			}
		};
		add(10, yokaiPleasedButton);
		
		Font gameStageFont = new Font("Arial", Font.PLAIN, 40);
		gameStageText = new TextComponent(this, "placeholder", gameStageFont, Color.WHITE, window.getWidth()/2, 20);
		gameStageText.setCenterHorizontally(true);
		gameStageText.setOutline(Color.BLACK, 4);
		add(10, gameStageText);
		
		pauseLayer = new PauseLayer(window);
		pauseLayer.setVisible(false);
		add(100, pauseLayer);
		
		int[] coords = game.getGameStorage().detectGameDeckEdges();
		minCardX = coords[0];
		minCardY = coords[1];
		maxCardX = coords[2];
		maxCardY = coords[3];
	}
	
	@Override
	public void draw(Graphics2D g) {
		drawBackground(g);
		
		AffineTransform before = g.getTransform();
		
		AffineTransform view = new AffineTransform();
		view.translate(panX, panY);
		view.translate(-scrollX, -scrollY);
		view.scale(zoom, zoom);
		
		Card[][] board = game.getGameStorage().getBoard();
		double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		view.translate(xCenter, yCenter);
		
		g.setTransform(view);
		cardsLayer.draw(g);
		
		g.setTransform(view);
		
		if(!game.getGameStorage().getCurrentStage().equals(GameStage.END)) {
			hintsLayer.draw(g);
		}
		
		g.setTransform(before);
		
		drawChildren(g);
	}
	
	@Override
	public void drawBackground(Graphics2D g) {
		
		AffineTransform before = g.getTransform();
		
		g.translate(panX%background.getWidth(), panY%background.getHeight());
		g.translate(-scrollX, -scrollY);
		g.scale(zoom, zoom);
		
		int d = window.getWidth()/background.getWidth()+2;
		int e = window.getHeight()/background.getHeight()+2;
		
		for(int i = -d; i <= d; i++) {
			for(int j = -e; j <= e; j++) {
				g.drawImage(background,
						(int)(i*background.getWidth()),
						(int)(j*background.getHeight()),
						(int)(background.getWidth()),
						(int)(background.getHeight()), null);
			}
		}
		
		g.setTransform(before);
		
	}
	
	@Override
	public void tick() {
		width = window.getWidth();
		height = window.getHeight();
		
		yokaiPleasedButton.setX( (window.getWidth() - 300)/2);
		yokaiPleasedButton.setY(window.getHeight() - 50 - 50);
		yokaiPleasedButton.setVisible(game.getGameStorage().getCurrentStage().equals(GameStage.PLAY_OR_GUESS));
		
		if(yokaiPleasedButton.isHovered() && yokaiPleasedButton.isVisible()) {
			if(!game.isPaused()) {
				window.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		} else {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		pauseLayer.setVisible(game.isPaused());
		
		gameStageText.setX(window.getWidth()/2);
		gameStageText.setText(game.getGameStorage().getCurrentStage().getDescription());
		
		if(cardsLayer.isDragingCard() || hintsLayer.isDragingHint()) {
			int speed = 10;
			Card[][] board = game.getGameStorage().getBoard();
			double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			int dx = 0, dy = 0;
			if(window.getMouseX() <= zoom*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dx = speed;
			} else if(window.getMouseX() >= window.getWidth() - zoom*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dx = -speed;
			}
			
			if(window.getMouseY() <= zoom*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dy = speed;
			} else if(window.getMouseY() >= window.getHeight() - zoom*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dy = -speed;
			}
			pan(xCenter, yCenter, dx, dy);
		}
	}

	@Override
	public void mouseWheelMoved(int x, int y, int scrollAmount) {
		if(game.isPaused()) return;
		
		double amount = scrollAmount > 0 ? 1/1.2 : 1.2;
		if(zoom*amount >= 0.5 && zoom*amount <= 3) {

			double a = x + scrollX - panX;
			double b = y + scrollY - panY;
			
			double zoomX = a/zoom;
			double zoomY = b/zoom;
					
			this.zoom *=  amount;
			
			double newZoomX = zoomX * zoom;
			double newZoomY = zoomY * zoom;
			
			this.scrollX = newZoomX - x + panX;
			this.scrollY = newZoomY - y + panY;
			
		}
	}
		

	@Override
	public boolean drag(int dragStartX, int dragStartY, int screenX, int screenY, int x, int y, int dx, int dy) {
		if(game.isPaused()) return true;

		GameStorage storage = game.getGameStorage();
		Card[][] board = storage.getBoard();
		double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		if(game.getGameStorage().getCurrentStage().equals(GameStage.MOVING)) {
			double xCardDisplayed = (x/zoom - panX/zoom + scrollX/zoom -xCenter);
			double yCardDisplayed = (y/zoom - panY/zoom + scrollY/zoom -yCenter);
			
			int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			
			if(!cardsLayer.isDragingCard()) {
				if(storage.isInsideBoard(xCard, yCard)) {
					Card card = board[yCard][xCard];
					if(card != null && !card.hasHint()) {
						board[yCard][xCard].setMoving(true);
						
						game.getSoundManager().playSound(Sounds.CARD_PICKING);
						
						cardsLayer.setDraggingCard(true);
						cardsLayer.setXCardDrag(xCard);
						cardsLayer.setYCardDrag(yCard);
						cardsLayer.setXCardOffset(xCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN));
						cardsLayer.setYCardOffset(yCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN));
						return true;
					}
				}
			} else {
				cardsLayer.setHoverCardX(xCard);
				cardsLayer.setHoverCardY(yCard);
			}
		}
		
		if(storage.getCurrentStage().equals(GameStage.HINT)) {
			double xCardDisplayed = (x/zoom - panX/zoom + scrollX/zoom -xCenter);
			double yCardDisplayed = (y/zoom - panY/zoom + scrollY/zoom -yCenter);
			
			int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			
			if(!hintsLayer.isDragingHint()) {
				int hintIndex = (xCard-(maxCardX+2))+2*(yCard-(minCardY+1));
				if(hintIndex >= 0 && hintIndex < storage.getDiscoveredHints().size()) {
					if(xCard >= maxCardX+2 && xCard <= maxCardX+HintsLayer.HINT_DECK_X_OFFSET+HintsLayer.HINT_ROWS && yCard >= minCardY+1 && yCard <= minCardY+HintsLayer.HINT_DECK_Y_OFFSET+storage.getHints().length/HintsLayer.HINT_ROWS) {
						game.getSoundManager().playSound(Sounds.CARD_PICKING);
						
						hintsLayer.setDraggingHint(true);
						hintsLayer.setHintDragged(storage.getDiscoveredHints().get(hintIndex));
						hintsLayer.setXCardOffset(xCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN));
						hintsLayer.setYCardOffset(yCardDisplayed % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN));
						return true;
					}
				}

				 // TODO si on essaye de creer ile, apres fonctionne plus de drag card.
			} else {
				hintsLayer.setHoverCardX(xCard);
				hintsLayer.setHoverCardY(yCard);
			}
		}
		
		if(!cardsLayer.isDragingCard() && !hintsLayer.isDragingHint()) {
			pan(xCenter, yCenter, dx, dy);
		}
		
		return true;
	}
	
	@Override
	public void stopDragging(int stopDragX, int stopDragY) {
		
		GameStorage storage = game.getGameStorage();
		Card[][] board = storage.getBoard();
		double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		double xCardDisplayed = (stopDragX/zoom - panX/zoom + scrollX/zoom -xCenter);
		double yCardDisplayed = (stopDragY/zoom - panY/zoom + scrollY/zoom -yCenter);
		
		int xCard = Math.floorDiv((int) xCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = Math.floorDiv((int) yCardDisplayed, CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		if(storage.getCurrentStage().equals(GameStage.MOVING)) {
			Card movingCard = storage.getBoard()[cardsLayer.getYCardDrag()][cardsLayer.getXCardDrag()];
			if(cardsLayer.isDragingCard() && storage.isCorrectPlacement(xCard, yCard)) {
				storage.getBoard()[cardsLayer.getYCardDrag()][cardsLayer.getXCardDrag()] = null;
				
				int[] centerOffset = storage.centerBoardBorder(xCard, yCard);
				xCard += centerOffset[0];
				yCard += centerOffset[1];
				
				
				storage.getBoard()[yCard][xCard] = movingCard;
				
				storage.centerBoard();
				
				storage.setCurrentStage(storage.getCurrentStage().getNextStage());
				
				game.getSoundManager().playSound(Sounds.CARD_PLACING);
				
				int[] coords = game.getGameStorage().detectGameDeckEdges();
				minCardX = coords[0];
				minCardY = coords[1];
				maxCardX = coords[2];
				maxCardY = coords[3];
			}
			movingCard.setMoving(false);
			cardsLayer.setDraggingCard(false);
		} else if(storage.getCurrentStage().equals(GameStage.HINT)) {
			if(hintsLayer.isDragingHint() && storage.isInsideBoard(xCard, yCard) && storage.getBoard()[yCard][xCard] != null && !storage.getBoard()[yCard][xCard].hasHint()) {
				storage.getBoard()[yCard][xCard].setHint(hintsLayer.getHintDragged());
				
				storage.getDiscoveredHints().remove((Object)hintsLayer.getHintDragged());
				storage.getPlacedHints().add(hintsLayer.getHintDragged());
				
				storage.setCurrentStage(storage.getCurrentStage().getNextStage());
				

				game.getSoundManager().playSound(Sounds.CARD_PLACING);
			}
			
			hintsLayer.setDraggingHint(false);
		}
	}

	@Override
	public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
		GameStorage storage = game.getGameStorage();
		Card[][] board = storage.getBoard();
		double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		int xCard = (int) (x/zoom - panX/zoom + scrollX/zoom -xCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = (int) (y/zoom - panY/zoom + scrollY/zoom -yCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
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
					}, TIME_SHOWING_CARDS*20);
				}
			}
		}
		
		if(storage.getCurrentStage().equals(GameStage.HINT)) {
			if(xCard == maxCardX+2 && yCard == minCardY && storage.getHints()[storage.getHints().length-1] != 0) {
				storage.getDiscoveredHints().add(storage.getHints()[storage.getDiscoveredHints().size()+storage.getPlacedHints().size()]);
				storage.getHints()[storage.getDiscoveredHints().size()+storage.getPlacedHints().size()-1] = 0;
				storage.setCurrentStage(storage.getCurrentStage().getNextStage());
				
				game.getSoundManager().playSound(Sounds.CARD_FLIP);
				return true;
			}
		}
		
		return false;
	}
	
	private void pan(double xCenter, double yCenter, int dx, int dy) {
		if(zoom*((minCardX+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + xCenter -scrollX/zoom + (panX+dx)/zoom) > window.getWidth()) {
			dx = (int) (-panX + zoom*(window.getWidth()/zoom - (minCardX+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - xCenter + scrollX/zoom));
		}
		if(zoom*((minCardY+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + yCenter -scrollY/zoom + (panY+dy)/zoom) > window.getHeight()) {
			dy = (int) (-panY + zoom*(window.getHeight()/zoom - (minCardY+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - yCenter + scrollY/zoom));
		}
		
		if(zoom*((maxCardX)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + xCenter -scrollX/zoom + (panX+dx)/zoom) < 0) {
			dx = (int) (-panX + zoom*( -(maxCardX)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - xCenter + scrollX/zoom));
		}
		if(zoom*((maxCardY)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + yCenter -scrollY/zoom + (panY+dy)/zoom) < 0) {
			dy = (int) (-panY + zoom*( -(maxCardY)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - yCenter + scrollY/zoom));
		}
		
		panX += dx;
		panY += dy;
	}
	
	public double getZoom() {
		return zoom;
	}
	
}
