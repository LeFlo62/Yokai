package fr.qmf.yokai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStage;
import fr.qmf.yokai.game.YokaiType;
import fr.qmf.yokai.game.gui.layers.GameLayer;
import fr.qmf.yokai.io.KeyboardCallback;
import fr.qmf.yokai.ui.Window;

public class YokaiGame implements Runnable {

	private Window window;
	
	private KeyboardCallback keyboardCallback;
	
	private double tickCap = 20;
	private double frameCap = 60;
	private boolean running;

	private boolean paused;
	
	private final int INIT_BOARD_SIZE = 4;
	private Card[][] board;
	private GameStage currentStage = GameStage.OBSERVING;
	
	public YokaiGame() {
		window = new Window("Yokai");
		window.setLayerConstrained(true);
		keyboardCallback = new KeyboardCallback(this);
		window.addKeyboardCallback(keyboardCallback);
		
		window.setCurrentLayer(new GameLayer(this, window));
		
		init();
		
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
	
	
	
	private void update() {
		if(window.getCurrentLayer() instanceof Tickable) {
			((Tickable)window.getCurrentLayer()).tick();
		}
	}

	public void init() {
		board = new Card[INIT_BOARD_SIZE][INIT_BOARD_SIZE];

		//Init a list of types
		List<YokaiType> types = new ArrayList<>();
		for (int i = 0; i < INIT_BOARD_SIZE*INIT_BOARD_SIZE; i++) {
			types.add(YokaiType.values()[i%YokaiType.values().length]);
		}
    
		//Randomizes its placement
		Collections.shuffle(types);
		for (int i = 0; i < INIT_BOARD_SIZE; i++) {
			for (int j = 0; j < INIT_BOARD_SIZE; j++) {
				board[i][j] = new Card(types.get(i*INIT_BOARD_SIZE + j));
				
			}
		}
	}
	
	public Card[][] getBoard() {
		return board;
	}
	
	public GameStage getCurrentStage() {
		return currentStage;
	}
	
	public void setCurrentStage(GameStage currentStage) {
		this.currentStage = currentStage;
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
	
}
