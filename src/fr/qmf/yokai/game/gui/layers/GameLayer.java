/*    MIT License

Copyright (c) 2021 Leclercq Florentin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStage;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.io.Textures;
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
	
	private TextComponent gameStageText;
	private Button yokaiPleasedButton;
	
	private double zoom = 1;

	private double panX, panY;
	private double scrollX, scrollY;

	public GameLayer(YokaiGame game, Window window) {
		super(window, 0, 0, Window.WIDTH, Window.HEIGHT);
		this.game = game;
		background = Textures.getTexture("backgrounds/game2");
		
		cardsLayer = new CardsLayer(game, window, this);
		
		yokaiPleasedButton = new Button(this, (Window.WIDTH - 300)/2, Window.HEIGHT - 50 - 50, 300, 50) {
			
			@Override
			public void hover(int screenX, int screenY, int x, int y) {

			}
			
			@Override
			public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
				game.endGame();
				return true;
			}
			
			@Override
			public void draw(Graphics g) {
				g.setColor(Color.RED);
				g.fillRect(x, y, width, height);
			}
		};
		add(10, yokaiPleasedButton);
		
		Font gameStageFont = new Font("Arial", Font.PLAIN, 40);
		gameStageText = new TextComponent(this, "placeholder", gameStageFont, Color.WHITE, Window.WIDTH/2, 20);
		gameStageText.setCenterHorizontally(true);
		//gameStageText.setOutline(Color.BLACK, 5);
		add(10, gameStageText);
		
		pauseLayer = new PauseLayer(window);
		pauseLayer.setVisible(false);
		add(100, pauseLayer);
	}
	
	@Override
	public void draw(Graphics g) {
		drawBackground(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform before = g2d.getTransform();
		
		g2d.translate(panX, panY);
		g2d.translate(-scrollX, -scrollY);
		g2d.scale(zoom, zoom);
		
		cardsLayer.draw(g);
		
		g2d.setTransform(before);
		
		drawChildren(g);
	}
	
	@Override
	public void drawBackground(Graphics g) {
		g.drawImage(background, 0, 0, Window.WIDTH, Window.HEIGHT, null);
	}
	
	@Override
	public void tick() {
		yokaiPleasedButton.setVisible(game.getGameStorage().getCurrentStage().equals(GameStage.PLAY_OR_GUESS));
		
		if(yokaiPleasedButton.isHovered()) {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		pauseLayer.setVisible(game.isPaused());
		
		gameStageText.setText(game.getGameStorage().getCurrentStage().getDescription());
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
		
		if(game.getGameStorage().getCurrentStage().equals(GameStage.MOVING)) {
			
			if(cardsLayer.isDragingCard()) {
				return cardsLayer.drag(dragStartX, dragStartY, screenX, screenY, x, y, dx, dy);
			}
			
			Card[][] board = game.getGameStorage().getBoard();
			double xCenter = (Window.WIDTH - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			double yCenter = (Window.HEIGHT - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			
			double xCardOnScreen = (x/zoom - panX/zoom + scrollX/zoom -xCenter);
			double yCardOnScreen = (y/zoom - panY/zoom + scrollY/zoom -yCenter);
			
			int xCard = (int) xCardOnScreen / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			int yCard = (int) yCardOnScreen / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			
			if(xCard < 0 || xCard >= board[0].length || yCard < 0 || yCard >= board.length) return false;
			
			Card card = board[yCard][xCard];
			if(card != null) {
				cardsLayer.setDraggingCard(true);
				cardsLayer.setXCardDrag(xCard);
				cardsLayer.setYCardDrag(yCard);
				cardsLayer.setXCardOffset(xCardOnScreen % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN));
				cardsLayer.setYCardOffset(yCardOnScreen % (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN));
				cardsLayer.setMouseX(x);
				cardsLayer.setMouseY(y);
				return true;
			}
		}
		
		panX += dx;
		panY += dy;
		return true;
	}
	
	@Override
	public void stopDragging(int stopDragX, int stopDragY) {
		cardsLayer.setDraggingCard(false);
	}

	@Override
	public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
		GameStorage storage = game.getGameStorage();
		Card[][] board = storage.getBoard();
		double xCenter = (Window.WIDTH - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (Window.HEIGHT - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		int xCard = (int) (x/zoom - panX/zoom + scrollX/zoom -xCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = (int) (y/zoom - panY/zoom + scrollY/zoom -yCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		if(game.getGameStorage().getCurrentStage().equals(GameStage.PLAY_OR_GUESS) || game.getGameStorage().getCurrentStage().equals(GameStage.OBSERVING)) {
			if(xCard < 0 || xCard >= board[0].length || yCard < 0 || yCard >= board.length) return false;
			
			Card card = board[yCard][xCard];
			if(card != null) {
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
							c1.flip();
							c2.flip();
							game.getGameStorage().setCurrentStage(game.getGameStorage().getCurrentStage().getNextStage());
						}
					}, TIME_SHOWING_CARDS*20);
				}
			}
		}
		
		return false;
	}
	
	public double getZoom() {
		return zoom;
	}
	
}
