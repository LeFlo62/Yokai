package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameController;
import fr.qmf.yokai.game.GameStage;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.gui.components.buttons.GameButton;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.Clickable;
import fr.qmf.yokai.ui.Dragable;
import fr.qmf.yokai.ui.MouseWheelSensitive;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.TextComponent;

public class GameLayer extends UILayer implements Tickable, Dragable, MouseWheelSensitive, Clickable {

	public static final int TIME_SHOWING_CARDS = 3;
	
	private YokaiGame game;
	
	private GameController controller;
	
	private BufferedImage background;
	
	private PauseLayer pauseLayer;
	private CardsLayer cardsLayer;
	private HintsLayer hintsLayer;
	private EndLayer endLayer;
	
	private TextComponent gameStageText;
	private GameButton yokaiPleasedButton;

	private TextComponent currentPlayerText;

	public GameLayer(YokaiGame game, GameController controller, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		this.controller = controller;
		background = Textures.getTexture("backgrounds/game_repeat");
		
		cardsLayer = new CardsLayer(game, controller, window, this);
		hintsLayer = new HintsLayer(game, controller, window, this);
		
		Font yokaiPleasedFont = new Font("Arial", Font.PLAIN, 20);
		yokaiPleasedButton = new GameButton(window, this, yokaiPleasedFont, "Les Yokais sont apaisés", Color.WHITE, (window.getWidth() - 300)/2, window.getHeight() - 50 - 50, 300, 50) {
			
			@Override
			public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
				super.click(screenX, screenY, x, y, clickCount);
				controller.endGame();
				return true;
			}
		};
		add(10, yokaiPleasedButton);
		
		Font gameStageFont = new Font("Arial", Font.PLAIN, 40);
		gameStageText = new TextComponent(this, " ", gameStageFont, Color.WHITE, window.getWidth()/2, 20);
		gameStageText.setCenterHorizontally(true);
		gameStageText.setOutline(Color.BLACK, 4);
		add(10, gameStageText);
		
		Font currentPlayerFont = new Font("Arial", Font.PLAIN, 30);
		currentPlayerText = new TextComponent(this, game.getGameStorage().getCurrentPlayer().getName(), currentPlayerFont, Color.WHITE, 10, window.getHeight()-90);
		add(10, currentPlayerText);
		
		pauseLayer = new PauseLayer(game, window);
		pauseLayer.setVisible(false);
		add(100, pauseLayer);
		
		endLayer = new EndLayer(game, window);
		add(50, endLayer);
	}
	
	@Override
	public void draw(Graphics2D g) {
		drawBackground(g);
		
		AffineTransform before = g.getTransform();
		
		AffineTransform view = new AffineTransform();
		view.translate(controller.getPanX(), controller.getPanY());
		view.translate(-controller.getScrollX(), -controller.getScrollY());
		view.scale(controller.getZoom(), controller.getZoom());
		
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
		
		g.translate(controller.getPanX()%background.getWidth(), controller.getPanY()%background.getHeight());
		g.translate(-controller.getScrollX(), -controller.getScrollY());
		g.scale(controller.getZoom(), controller.getZoom());
		
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
		
		currentPlayerText.setY(window.getHeight()-90);
		currentPlayerText.setText(game.getGameStorage().getCurrentPlayer().getName());
		
		yokaiPleasedButton.setX( (window.getWidth() - 300)/2);
		yokaiPleasedButton.setY(window.getHeight() - 50 - 50);
		yokaiPleasedButton.setVisible(game.getGameStorage().getCurrentStage().equals(GameStage.PLAY_OR_GUESS));
		
		pauseLayer.setVisible(game.isPaused());
		pauseLayer.tick();
		
		if(game.getGameStorage().getCurrentStage().equals(GameStage.END)) {
			endLayer.tick();
		}
		
		gameStageText.setX(window.getWidth()/2);
		gameStageText.setText(game.getGameStorage().getCurrentStage().getDescription());
		
		if(controller.isDraggingCard() || controller.isDraggingHint()) {
			int speed = 10;
			Card[][] board = game.getGameStorage().getBoard();
			double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			int dx = 0, dy = 0;
			if(window.getMouseX() <= controller.getZoom()*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dx = speed;
			} else if(window.getMouseX() >= window.getWidth() - controller.getZoom()*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dx = -speed;
			}
			
			if(window.getMouseY() <= controller.getZoom()*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dy = speed;
			} else if(window.getMouseY() >= window.getHeight() - controller.getZoom()*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN)) {
				dy = -speed;
			}
			pan(xCenter, yCenter, dx, dy);
		}
	}

	@Override
	public void mouseWheelMoved(int x, int y, int scrollAmount) {
		if(game.isPaused()) return;
		
		double amount = scrollAmount > 0 ? 1/1.2 : 1.2;
		if(controller.getZoom()*amount >= 0.5 && controller.getZoom()*amount <= 3) {

			double a = x + controller.getScrollX() - controller.getPanX();
			double b = y + controller.getScrollY() - controller.getPanY();
			
			double zoomX = a/controller.getZoom();
			double zoomY = b/controller.getZoom();
					
			this.controller.setZoom(this.controller.getZoom()*amount);
			
			double newZoomX = zoomX * controller.getZoom();
			double newZoomY = zoomY * controller.getZoom();
			
			this.controller.setScrollX(newZoomX - x + controller.getPanX());
			this.controller.setScrollY(newZoomY - y + controller.getPanY());
			
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
			double xCardDisplayed = (x/controller.getZoom() - controller.getPanX()/controller.getZoom() + controller.getScrollX()/controller.getZoom() -xCenter);
			double yCardDisplayed = (y/controller.getZoom() - controller.getPanY()/controller.getZoom() + controller.getScrollY()/controller.getZoom() -yCenter);
			
			if(controller.cardDrag(xCardDisplayed, yCardDisplayed)) {
				return true;
			}
		}
		
		if(storage.getCurrentStage().equals(GameStage.HINT)) {
			double xCardDisplayed = (x/controller.getZoom() - controller.getPanX()/controller.getZoom() + controller.getScrollX()/controller.getZoom() -xCenter);
			double yCardDisplayed = (y/controller.getZoom() - controller.getPanY()/controller.getZoom() + controller.getScrollY()/controller.getZoom() -yCenter);
			
			if(controller.hintDrag(xCardDisplayed, yCardDisplayed)) {
				return true;
			}
		}
		
		if(!controller.isDraggingCard() && !controller.isDraggingHint()) {
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
		
		double xCardDisplayed = (stopDragX/controller.getZoom() - controller.getPanX()/controller.getZoom() + controller.getScrollX()/controller.getZoom() -xCenter);
		double yCardDisplayed = (stopDragY/controller.getZoom() - controller.getPanY()/controller.getZoom() + controller.getScrollY()/controller.getZoom() -yCenter);
		
		controller.stoppedDraggingCardOrHint(xCardDisplayed, yCardDisplayed);
	}

	@Override
	public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
		GameStorage storage = game.getGameStorage();
		Card[][] board = storage.getBoard();
		double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		double xCardDisplayed = (x/controller.getZoom() - controller.getPanX()/controller.getZoom() + controller.getScrollX()/controller.getZoom() -xCenter);
		double yCardDisplayed = (y/controller.getZoom() - controller.getPanY()/controller.getZoom() + controller.getScrollY()/controller.getZoom() -yCenter);
		
		controller.clickCardOrHint(xCardDisplayed, yCardDisplayed);
		
		return false;
	}
	
	private void pan(double xCenter, double yCenter, int dx, int dy) {
		if(controller.getZoom()*((controller.getMinCardX()+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + xCenter -controller.getScrollX()/controller.getZoom() + (controller.getPanX()+dx)/controller.getZoom()) > window.getWidth()) {
			dx = (int) (-controller.getPanX() + controller.getZoom()*(window.getWidth()/controller.getZoom() - (controller.getMinCardX()+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - xCenter + controller.getScrollX()/controller.getZoom()));
		}
		if(controller.getZoom()*((controller.getMinCardY()+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + yCenter -controller.getScrollY()/controller.getZoom() + (controller.getPanY()+dy)/controller.getZoom()) > window.getHeight()) {
			dy = (int) (-controller.getPanY() + controller.getZoom()*(window.getHeight()/controller.getZoom() - (controller.getMinCardY()+1)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - yCenter + controller.getScrollY()/controller.getZoom()));
		}
		
		if(controller.getZoom()*((controller.getMaxCardX())*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + xCenter -controller.getScrollX()/controller.getZoom() + (controller.getPanX()+dx)/controller.getZoom()) < 0) {
			dx = (int) (-controller.getPanX() + controller.getZoom()*( -(controller.getMaxCardX())*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - xCenter + controller.getScrollX()/controller.getZoom()));
		}
		if(controller.getZoom()*((controller.getMaxCardY())*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + yCenter -controller.getScrollY()/controller.getZoom() + (controller.getPanY()+dy)/controller.getZoom()) < 0) {
			dy = (int) (-controller.getPanY() + controller.getZoom()*( -(controller.getMaxCardY())*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) - yCenter + controller.getScrollY()/controller.getZoom()));
		}
		
		controller.setPanX(dx+controller.getPanX());
		controller.setPanY(dy+controller.getPanY());
	}
	
}
