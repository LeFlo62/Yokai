package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.ImageComponent;

public class PauseLayer extends UILayer {

	private BufferedImage pause;
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);
	
	public PauseLayer(Window window) {
		super(window, 0, 0, Window.WIDTH, Window.HEIGHT);
		pause = Textures.getTexture("gui/pause");
		add(new ImageComponent(this, pause, (Window.WIDTH - pause.getWidth())/2, 100));
	}
	
	@Override
	public void drawBackground(Graphics2D g) {
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(x, y, width, height);
	}

}
