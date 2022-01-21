package fr.qmf.yokai.game.gui.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.GameController;
import fr.qmf.yokai.game.gui.components.buttons.GameButton;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.io.audio.Sound;
import fr.qmf.yokai.io.audio.Sounds;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.ImageComponent;

public class PauseLayer extends UILayer implements Tickable {
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);

	private YokaiGame game;
	private ImageComponent pause;
	private Sound pauseSound;
	private GameButton backToMainTitle;
	private GameButton backToGame;
	
	public PauseLayer(YokaiGame game, GameController controller, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		
		BufferedImage pauseTexture = Textures.getTexture("gui/pause");
		pause = new ImageComponent(this, pauseTexture, (window.getWidth() - pauseTexture.getWidth())/2, 100);
		add(pause);
		
		backToGame = new GameButton(window, this, new Font("Arial", Font.PLAIN, 18), "Retour au jeu", Color.WHITE, (window.getWidth()-300)/2,(window.getHeight()-60-30)/2, 300, 60) {
			
			@Override
			public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
				super.click(screenX, screenY, x, y, clickCount);
				controller.setPaused(false);
				parent.setVisible(false);
				return true;
			}
		};
		add(10, backToGame);
		
		backToMainTitle = new GameButton(window, this, new Font("Arial", Font.PLAIN, 18), "Retour au menu principal", Color.WHITE, (window.getWidth()-300)/2, (window.getHeight()+60+30)/2, 300, 60) {
			
			@Override
			public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
				super.click(screenX, screenY, x, y, clickCount);
				
				controller.setPaused(false);
				parent.setVisible(false);
				
				window.setCurrentLayer(new MainTitleLayer(game, window));
				return true;
			}
		};
		add(10, backToMainTitle);
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
		
		this.backToGame.setX((window.getWidth()-300)/2);
		this.backToMainTitle.setX((window.getWidth()-300)/2);
		this.backToGame.setY((window.getHeight()-60-30)/2);
		this.backToMainTitle.setY((window.getHeight()+60+30)/2);
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
