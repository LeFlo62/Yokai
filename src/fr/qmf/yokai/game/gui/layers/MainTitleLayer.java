package fr.qmf.yokai.game.gui.layers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.YokaiType;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.ImageComponent;

public class MainTitleLayer extends UILayer implements Tickable  {

	private YokaiGame game;
	private ImageComponent yokaiTitle;
	private BufferedImage background;
	private Card[][] board;
	
	private Random random = new Random();

	public MainTitleLayer(YokaiGame game, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		background = Textures.getTexture("backgrounds/game_repeat");
		
		initFakeBoard();
		
		BufferedImage yokaiTexture = Textures.getTexture("gui/yokai");
		this.yokaiTitle = new ImageComponent(this, yokaiTexture, (window.getWidth() - yokaiTexture.getWidth())/2, 50);
		add(yokaiTitle);
	}
	
	@Override
	public void drawBackground(Graphics2D g) {
		double animationDelta = 1f/game.getTargetFPS();
		
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
		
		double xCenter = (window.getWidth() - board[0].length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		double yCenter = (window.getHeight() - board.length*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN))/2;
		
		g.translate(xCenter, yCenter);
		
		for(int i = 0; i < board[0].length; i++) {
			for(int j = 0; j < board.length; j++) {
				Card card = board[i][j];
				if(card == null) continue;
				
				BufferedImage texture = Textures.getTexture("cards/back");
				if(card.isShown()) {
					texture = Textures.getTexture("cards/" + card.getType().getColor());
				} else if(card.hasHint()) {
					YokaiType[] yokaiTypes = YokaiType.getYokaiFromHint(card.getHint());
					texture = Textures.getTexture("hints/" + YokaiType.getYokaisString(yokaiTypes));
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
				
				g.drawImage(texture, j*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN) + (int)(card.isAnimated() ?CardsLayer.DEFAULT_CARD_SIZE*(-Math.abs(animationTime/Card.ANIMATION_DURATION-0.5)+0.5) : 0),
						i*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN),
									(int) (CardsLayer.DEFAULT_CARD_SIZE * (card.isAnimated() ? Math.abs((Card.ANIMATION_DURATION-2*animationTime)/Card.ANIMATION_DURATION) : 1)), CardsLayer.DEFAULT_CARD_SIZE, null);
			}
		}
		
		g.translate(-xCenter, -yCenter);
	}

	@Override
	public void tick() {
		this.yokaiTitle.setX((window.getWidth() - yokaiTitle.getWidth())/2);
		
		if(random.nextInt(300) <= 4) {
			board[random.nextInt(4)][random.nextInt(4)].flip();
		}
	}
	
	private void initFakeBoard() {
		this.board = new Card[4][4];
		// Init a list of types
		List<YokaiType> types = new ArrayList<>();
		for (int i = 0; i < 16; i++) {
			types.add(YokaiType.values()[i % YokaiType.values().length]);
		}

		// Randomizes its placement
		Collections.shuffle(types);
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				board[i][j] = new Card(types.get(i * 4 + j));

			}
		}
	}

}
