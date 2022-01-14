package fr.qmf.yokai.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStorage;

public class KeyboardCallback implements KeyListener {

	private YokaiGame game;

	public KeyboardCallback(YokaiGame game) {
		this.game = game;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
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
