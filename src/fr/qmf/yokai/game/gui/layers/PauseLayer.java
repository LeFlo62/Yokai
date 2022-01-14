package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.io.audio.Sound;
import fr.qmf.yokai.io.audio.Sounds;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.ImageComponent;

public class PauseLayer extends UILayer implements Tickable {

	private YokaiGame game;
	private ImageComponent pause;
	private Sound pauseSound;
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);
	
	public PauseLayer(YokaiGame game, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		
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
	
	@Override
	public void setVisible(boolean visible) {
		if(visible && !this.visible) {
			Sound mainMusic = game.getSoundManager().getMainMusic();
			if(mainMusic != null) mainMusic.pause();
			pauseSound = game.getSoundManager().playSound(Sounds.PAUSE);
			pauseSound.setLooping(true);
		} else if(!visible && this.visible && pauseSound != null) {
			pauseSound.stop();
			Sound mainMusic = game.getSoundManager().getMainMusic();
			if(mainMusic != null) mainMusic.resume();
			
		}
		super.setVisible(visible);
	}

}
