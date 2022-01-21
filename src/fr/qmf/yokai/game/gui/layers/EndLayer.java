package fr.qmf.yokai.game.gui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.gui.components.buttons.GameButton;
import fr.qmf.yokai.io.audio.Sound;
import fr.qmf.yokai.io.audio.Sounds;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.TextComponent;

public class EndLayer extends UILayer implements Tickable {
	private YokaiGame game;
	
	private int visibleCooldown;
	private float alpha;

	private TextComponent endingText;
	private TextComponent scoreText;

	private GameButton backToMainTitle;
	
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);
	
	public EndLayer(YokaiGame game, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		
		visibleCooldown = (int)(1*20+GameStorage.BOARD_LENGTH*GameStorage.BOARD_LENGTH/game.getTargetFPS()*2*20);
		
		setVisible(false);
		
		Font endingFont = new Font("Arial", Font.PLAIN, 92);
		endingText = new TextComponent(this, " ", endingFont, Color.WHITE, window.getWidth()/2, 60);
		endingText.setCenterHorizontally(true);
		endingText.setOutline(Color.BLACK, 4);
		add(10, endingText);
		
		Font scoreFont = new Font("Arial", Font.PLAIN, 30);
		scoreText = new TextComponent(this, " ", scoreFont, Color.WHITE, window.getWidth()/2, 170);
		scoreText.setCenterHorizontally(true);
		scoreText.setOutline(Color.BLACK, 4);
		add(10, scoreText);
		
		backToMainTitle = new GameButton(window, this, new Font("Arial", Font.PLAIN, 18), "Retour au menu principal", Color.WHITE, (window.getWidth()-300)/2, window.getHeight()-120, 300, 60) {
			
			@Override
			public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
				super.click(screenX, screenY, x, y, clickCount);
				window.setCurrentLayer(new MainTitleLayer(game, window));
				return false;
			}
		};
		add(10, backToMainTitle);
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		super.draw(g);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}
	
	@Override
	public void drawBackground(Graphics2D g) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(x, y, width, height);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	@Override
	public void tick() {
		width = window.getWidth();
		height = window.getHeight();
		
		endingText.setX(window.getWidth()/2);
		scoreText.setX(window.getWidth()/2);
		
		backToMainTitle.setX((window.getWidth()-300)/2);
		backToMainTitle.setY(window.getHeight()-120);
		
		if(visibleCooldown > 1*20) {
			if(visibleCooldown == (int)(1*20+GameStorage.BOARD_LENGTH*GameStorage.BOARD_LENGTH/game.getTargetFPS()*2*20)) {
				String text = "Défaite";
				int score = game.getGameStorage().getScore();
				if(score >= 0 && score <= 7) {
					text = "Victoire Honorable";
				} else if(score >= 8 && score <= 11) {
					text = "Victoire Glorieuse";
				} else if(score >= 12){
					text = "Victoire Totale";
				}
				endingText.setText(text);

				if(score >= 0) {
					scoreText.setText("Score: " + score);
				}
			}
			visibleCooldown--;
		} else if(visibleCooldown > 0){
			setVisible(true);
			alpha += 1f/(1f*20f);
			alpha = Math.min(1, alpha); //Rounding error.
			visibleCooldown--;
			
			if(visibleCooldown == 0) {
				Sound mainMusic = game.getSoundManager().getMainMusic();
				final float volume = mainMusic != null ? mainMusic.getVolume() : 0;
				if(mainMusic != null) mainMusic.setVolume(mainMusic.getVolume()/10);
				Sound sound = game.getSoundManager().playSound(game.getGameStorage().getScore() == -1 ? Sounds.LOSE : Sounds.WIN);
				sound.addEndListener(() -> {
					if(mainMusic != null) mainMusic.setVolume(volume);
				});
			}
		}
	}
}
