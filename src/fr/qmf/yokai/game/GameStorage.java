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
	
	/**
	 * Gets the most upper-left card coords (for the min)
	 * and the most lower-right cards coords (for the max)
	 * @return an arraw consisting of the min and max coords: {minCardX, minCardY, maxCardX, maxCardY}
	 */
	public int[] detectGameDeckEdges() {
		int minCardX = 0, minCardY = 0, maxCardX = 0, maxCardY = 0;
		for(int i = 0; i < board[0].length; i++) {
			for(int j = 0; j < board.length; j++) {
				if(board[i][j] != null) {
					if(minCardX == -1) {
						minCardX = maxCardX = j;
						minCardY = maxCardY = i;
					}
					if(j < minCardX) minCardX = j;
					if(j > maxCardX) maxCardX = j;
					if(i < minCardY) minCardY = i;
					if(i > maxCardY) maxCardY = i;
				}
			}
		}
		return new int[]{minCardX, minCardY, maxCardX, maxCardY};
	}
	
	/**
	 * Check if the card placement is valid
	 * @param cardX
	 * @param cardY
	 * @return true only if the card may be placed here
	 */
	public boolean isCorrectPlacement(int cardX, int cardY) {
		// Has a card where the player hovers
		if(isInsideBoard(cardX, cardY) && board[cardY][cardX] != null) {
			return false;
		}
		
		//Has a card aside the hovered placed
		return (isInsideBoard(cardX+1, cardY) && board[cardY][cardX+1] != null)
				|| (isInsideBoard(cardX-1, cardY) && board[cardY][cardX-1] != null)
				|| (isInsideBoard(cardX, cardY+1) && board[cardY+1][cardX] != null)
				|| (isInsideBoard(cardX, cardY-1) && board[cardY-1][cardX] != null);
	}
	
	public boolean isInsideBoard(int cardX, int cardY) {
		return cardX>=0 && cardX<board[0].length && cardY>=0 && cardY<board.length;
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
