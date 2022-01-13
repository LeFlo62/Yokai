package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.ImageComponent;

public class PauseLayer extends UILayer implements Tickable {

	private ImageComponent pause;
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);
	
	public PauseLayer(Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		BufferedImage pauseTexture = Textures.getTexture("gui/pause");
		pause = new ImageComponent(this, pauseTexture, (window.getWidth() - pauseTexture.getWidth())/2, 100);
		add(pause);
	}
	
	@Override
	public void drawBackground(Graphics2D g) {
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(x, y, width, height);
	}

	@Override
	public void tick() {
		width = window.getWidth();
		height = window.getHeight();
		pause.setX((window.getWidth() - pause.getWidth())/2);
	}

}
