package fr.qmf.yokai.game.gui.layers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.YokaiType;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;

/**
 * A UILayer that displays all the cards from YokaiGame.
 * They are drawn dynamically and directly in the draw function.
 * This is due to the ever-changing nature of the Card board.
 */
public class CardsLayer extends UILayer {

	private YokaiGame game;
	
	private static final int DEFAULT_CARD_SIZE = 130;
	private static final int CARD_MARGIN = 20;

	public CardsLayer(YokaiGame game, Window window) {
		super(window, 0, 0, 0, 0);
		this.game = game;
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		width = game.getBoard()[0].length*(DEFAULT_CARD_SIZE + CARD_MARGIN);
		height = game.getBoard().length*(DEFAULT_CARD_SIZE + CARD_MARGIN);
		
		Card[][] board = game.getBoard();
		double xCenter = (Window.WIDTH - board[0].length*(DEFAULT_CARD_SIZE + CARD_MARGIN))/2;
		double yCenter = (Window.HEIGHT - board.length*(DEFAULT_CARD_SIZE + CARD_MARGIN))/2;
		
		
		g2d.translate(xCenter, yCenter);
		
		for(int i = 0; i < board[0].length; i++) {
			for(int j = 0; j < board.length; j++) {
				Card card = board[i][j];
				if(card == null) continue;
				
				BufferedImage texture = Textures.getTexture("cards/back");
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
				
				g.drawImage(texture, j*(DEFAULT_CARD_SIZE + CARD_MARGIN),
						i*(DEFAULT_CARD_SIZE + CARD_MARGIN),
									DEFAULT_CARD_SIZE, DEFAULT_CARD_SIZE, null);
			}
		}
	}

}
