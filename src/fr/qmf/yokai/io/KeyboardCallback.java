package fr.qmf.yokai.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import fr.qmf.yokai.YokaiGame;

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
	}

}
