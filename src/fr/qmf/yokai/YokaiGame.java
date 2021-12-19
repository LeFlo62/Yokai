package fr.qmf.yokai;

import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStage;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.gui.layers.GameLayer;
import fr.qmf.yokai.io.KeyboardCallback;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.Window;

public class YokaiGame implements Runnable {

	private Window window;
	
	private Scheduler scheduler;
	
	private KeyboardCallback keyboardCallback;
	
	private GameStorage gameStorage;
	
	private double tickCap = 20;
	private double frameCap = 60;
	private boolean running;

	private boolean paused;
	
	public YokaiGame() {
		window = new Window("Yokai", Textures.getTexture("icon"));
		window.setLayerConstrained(true);
		keyboardCallback = new KeyboardCallback(this);
		window.addKeyboardCallback(keyboardCallback);
		
		window.setCurrentLayer(new GameLayer(this, window));
		
		scheduler = new Scheduler();
		
		gameStorage = new GameStorage(4);
		gameStorage.init();
		
		new Thread(this, "YokaiGame").start();
	}

	@Override
	public void run() {
		running = true;
		
		long lastTickTime = System.nanoTime();
		long lastRenderTime = System.nanoTime();
		
		double tickTime = 10E8 / tickCap;
		double renderTime = 10E8 / frameCap;
		
		int ticks = 0;
		int frames = 0;
		
		long timer = System.currentTimeMillis();
		
		while(running) {
			if(System.nanoTime() - lastTickTime >= tickTime) {
				update();
				ticks++;
				lastTickTime += tickTime;
			} else if(System.nanoTime() - lastRenderTime >= renderTime) {
				window.clear();
				window.draw();
				frames++;
				lastRenderTime += renderTime;
			} else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				
				//System.out.println("FPS:" + frames + " TPS:" + ticks);
				
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void endGame() {
		gameStorage.setCurrentStage(GameStage.END);
		
		Card[][] board = gameStorage.getBoard();
		for(int j = 0; j < board.length; j++) {
			for(int i = 0; i < board[0].length; i++) {
				Card card = board[j][i];
				if(card != null) {
					card.flip();
					card.setAnimationTime(-1d/getFPS()*i*j*2);
				}
			}
		}
	}
	
	private void update() {
		scheduler.tick();
		
		if(window.getCurrentLayer() instanceof Tickable) {
			((Tickable)window.getCurrentLayer()).tick();
		}
	}

	public GameStorage getGameStorage() {
		return gameStorage;
	}

	public double getFPS() {
		return frameCap;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
}
