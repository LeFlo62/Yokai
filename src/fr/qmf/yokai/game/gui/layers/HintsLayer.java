package fr.qmf.yokai.game.gui.layers;

import java.awt.Graphics2D;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;

public class HintsLayer extends UILayer {

	private YokaiGame game;

	public HintsLayer(YokaiGame game, Window window, UILayer parent) {
		super(window, parent, 0,0,0,0);
		this.game = game;
	}
	
	@Override
	public void draw(Graphics2D g) {
		GameStorage storage = game.getGameStorage();
		
	}

}
