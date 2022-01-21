package fr.qmf.yokai.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.gui.layers.GameLayer;
import fr.qmf.yokai.ui.Window;

/**
 * Listens to keys performed in the window.
 * @author LeFlo
 *
 */
public class KeyboardCallback implements KeyListener {

	private YokaiGame game;
	private Window window;

	public KeyboardCallback(YokaiGame game, Window window) {
		this.game = game;
		this.window = window;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(window.getCurrentLayer() instanceof GameLayer) {
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				game.setPaused(!game.isPaused());
			}
			if(e.getKeyCode() == KeyEvent.VK_S) {
				game.getGameStorage().save(new File("state.save"));
			}
			if(e.getKeyCode() == KeyEvent.VK_L) {
				game.setGameStorage(GameStorage.load(new File("state.save")));
			}
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				game.getGameStorage().setCurrentStage(game.getGameStorage().getCurrentStage().getNextStage());
			}
			if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
				for(int i = 0; i < GameStorage.BOARD_LENGTH; i++) {
					for(int j = 0; j < GameStorage.BOARD_LENGTH; j++) {
						Card card = game.getGameStorage().getBoard()[i][j];
						if(card != null) {
							card.flip();
						}
					}
				}
			}
		}
	}

}
