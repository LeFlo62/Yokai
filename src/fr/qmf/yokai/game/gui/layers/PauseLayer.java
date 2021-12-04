package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Graphics;

import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.TextComponent;

public class PauseLayer extends UILayer {

	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);
	
	public PauseLayer(Window window) {
		super(window, 0, 0, Window.WIDTH, Window.HEIGHT);
		add(new TextComponent(this, "Pause", TextComponent.DEFAULT_FONT, Color.WHITE, 100, 100));
	}
	
	@Override
	public void drawBackground(Graphics g) {
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(x, y, width, height);
	}

}
