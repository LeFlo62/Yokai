package fr.qmf.yokai.game.gui.layers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;

public class HintsLayer extends UILayer {

	private YokaiGame game;
	private int maxCardX, minCardY;
	
	public static final int DEFAULT_HINT_SIZE = 100;
	public static final int HINT_MARGIN = 18;
	public static final int UNDISCOVERED_HINT_Y_OFFSET = 8;

	public HintsLayer(YokaiGame game, Window window, UILayer parent) {
		super(window, parent, 0,0,0,0);
		this.game = game;
	}
	
	@Override
	public void draw(Graphics2D g) {
		GameStorage storage = game.getGameStorage();
		
		int[] edges = game.getGameStorage().detectGameDeckEdges();
		minCardY = edges[1];
		maxCardX = edges[2];

		BufferedImage back = Textures.getTexture("hints/back");
		
		for(int d = -(storage.getHints().length-storage.getDiscoveredHints().size()); d < 0; d++) {
			g.drawImage(back, (maxCardX+2)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN),
					UNDISCOVERED_HINT_Y_OFFSET*d+(minCardY)*(CardsLayer.DEFAULT_CARD_SIZE + CardsLayer.CARD_MARGIN),
					DEFAULT_HINT_SIZE,
					DEFAULT_HINT_SIZE, null);
		}
	}

}
