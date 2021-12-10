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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStage;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.Clickable;
import fr.qmf.yokai.ui.Dragable;
import fr.qmf.yokai.ui.MouseWheelSensitive;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.Button;

public class GameLayer extends UILayer implements Tickable, Dragable, MouseWheelSensitive, Clickable {

	private int cardsShown = 0;
	private int[] cardsShownCoords = new int[4]; //Place elsewhere ?
	
	private YokaiGame game;
	private BufferedImage background;
	
	private PauseLayer pauseLayer;
	private CardsLayer cardsLayer;
	
	private Button yokaiPleasedButton;
	
	private double zoom = 1;

	private double panX, panY;
	private double scrollX, scrollY;
	
	public GameLayer(YokaiGame game, Window window) {
		super(window, 0, 0, Window.WIDTH, Window.HEIGHT);
		this.game = game;
		background = Textures.getTexture("backgrounds/game");
		
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
		g.drawImage(background, 0, 0, null);
	}
	
	@Override
	public void tick() {
		yokaiPleasedButton.setVisible(game.getCurrentStage().equals(GameStage.PLAY_OR_GUESS));
		
		if(yokaiPleasedButton.isHovered()) {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		pauseLayer.setVisible(game.isPaused());
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
		
		if(game.getCurrentStage().equals(GameStage.MOVING)) {
			Card[][] board = game.getBoard();
			double xCenter = (Window.WIDTH - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			double yCenter = (Window.HEIGHT - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
			
			int xCard = (int) (dragStartX/zoom - panX/zoom + scrollX/zoom -xCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			int yCard = (int) (dragStartY/zoom - panY/zoom + scrollY/zoom -yCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
			
			Card card = board[yCard][xCard];
			if(card != null) {
				
				return true;
			}
		}
		
		panX += dx;
		panY += dy;
		return true;
	}

	@Override
	public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
		Card[][] board = game.getBoard();
		double xCenter = (Window.WIDTH - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (Window.HEIGHT - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		int xCard = (int) (x/zoom - panX/zoom + scrollX/zoom -xCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		int yCard = (int) (y/zoom - panY/zoom + scrollY/zoom -yCenter) / (CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN);
		
		if(game.getCurrentStage().equals(GameStage.PLAY_OR_GUESS) || game.getCurrentStage().equals(GameStage.OBSERVING)) {
			Card card = board[yCard][xCard];
			if(card != null) {
				game.setCurrentStage(GameStage.OBSERVING);
				card.flip();
				cardsShownCoords[cardsShown*2] = xCard;
				cardsShownCoords[cardsShown*2+1] = yCard;
				cardsShown++;
				
				if(cardsShown == 2) {
					game.getScheduler().scheduleTask(new Runnable() {
						@Override
						public void run() {
							Card c1 = board[cardsShownCoords[1]][cardsShownCoords[0]];
							Card c2 = board[cardsShownCoords[3]][cardsShownCoords[2]];
							cardsShown = 0;
							c1.flip();
							c2.flip();
							game.setCurrentStage(game.getCurrentStage().getNextStage());
						}
					}, 3*20);
				}
			}
		}
		
		return false;
	}
	
}
