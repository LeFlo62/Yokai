package fr.qmf.yokai.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameStorage implements Serializable {

	private static final long serialVersionUID = 1L;

	private int deckLength;
	private int boardLength;

	private Player currentPlayer; // Not initialized yet.

	private Card[][] board;
	private GameStage currentStage = GameStage.PLAY_OR_GUESS;

	private int cardsShown = 0;
	private int[] cardsShownCoords = new int[4];

	public GameStorage(int deckLength) {
		this.deckLength = deckLength;
		this.boardLength = deckLength * deckLength;
	}

	public void init() {
		board = new Card[boardLength][boardLength];

		// Init a list of types
		List<YokaiType> types = new ArrayList<>();
		for (int i = 0; i < boardLength; i++) {
			types.add(YokaiType.values()[i % YokaiType.values().length]);
		}

		// Randomizes its placement
		Collections.shuffle(types);
		int offset = (boardLength - deckLength) / 2;
		for (int i = 0; i < deckLength; i++) {
			for (int j = 0; j < deckLength; j++) {
				board[offset + i][offset + j] = new Card(types.get(i * deckLength + j));

			}
		}
	}

	public void save(File file) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
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

	public int getCardsShown() {
		return cardsShown;
	}

	public void setCardsShown(int cardsShown) {
		this.cardsShown = cardsShown;
	}

	public int[] getCardsShownCoords() {
		return cardsShownCoords;
	}
	
	public static GameStorage load(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			GameStorage gameStorage = (GameStorage) ois.readObject();
			ois.close();
			return gameStorage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
