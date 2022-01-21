package fr.qmf.yokai;

import java.util.Random;

import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.gui.layers.MainTitleLayer;
import fr.qmf.yokai.io.KeyboardCallback;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.io.audio.SoundManager;
import fr.qmf.yokai.ui.Window;

/**
 * YokaiGame is the "main" class of the game.
 * It centralizes everything the game need to run.
 * 
 * @author LeFlo
 */
public class YokaiGame implements Runnable {

	private Window window;
	
	private Scheduler scheduler;
	
	private KeyboardCallback keyboardCallback;
	
	private GameStorage gameStorage;
	private SoundManager soundManager;
	
	/**
	 * How much times a second should the game update its logic
	 */
	private double tickCap = 20;
	
	/**
	 * How much times a second should the game draw on the window
	 */
	private double frameCap = 60;
	private boolean running;
	
	private boolean paused;

	public YokaiGame() {
		window = new Window("Yokai", Textures.getTexture("icon"));
		window.setLayerConstrained(true);
		keyboardCallback = new KeyboardCallback(this, window);
		window.addKeyboardCallback(keyboardCallback);
		
		window.setCurrentLayer(new MainTitleLayer(this, window));
		
		soundManager = new SoundManager(new Random());
		
		scheduler = new Scheduler();
		
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
				
				System.out.println("FPS:" + frames + " TPS:" + ticks);
				
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	private void update() {
		scheduler.tick();
		
		if(window.getCurrentLayer() instanceof Tickable) {
			((Tickable)window.getCurrentLayer()).tick();
		}
		
		soundManager.tick();
	}

	public GameStorage getGameStorage() {
		return gameStorage;
	}

	public double getTargetFPS() {
		return frameCap;
	}
	
	public void setGameStorage(GameStorage gameStorage) {
		this.gameStorage = gameStorage;
	}
	
	public SoundManager getSoundManager() {
		return soundManager;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
}
