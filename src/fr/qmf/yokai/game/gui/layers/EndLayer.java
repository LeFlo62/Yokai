package fr.qmf.yokai.game.gui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import fr.qmf.yokai.Tickable;
import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.gui.components.buttons.GameButton;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.TextComponent;

public class EndLayer extends UILayer implements Tickable {
	private YokaiGame game;
	
	private int visibleCooldown;
	private float alpha;

	private TextComponent endingText;
	private TextComponent scoreText;
	
//	private ImageComponent pause;
	
	private static final Color BACKGROUND_COLOR = new Color(20, 20, 20, 220);
	
	public EndLayer(YokaiGame game, Window window) {
		super(window, 0, 0, window.getWidth(), window.getHeight());
		this.game = game;
		
		visibleCooldown = (int)(2*20+GameStorage.BOARD_LENGTH*GameStorage.BOARD_LENGTH/game.getTargetFPS()*2*20);
		
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
		
		if(visibleCooldown > 1*20) {
			if(visibleCooldown == (int)(2*20+GameStorage.BOARD_LENGTH*GameStorage.BOARD_LENGTH/game.getTargetFPS()*2*20)) {
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
		}
	}
}
