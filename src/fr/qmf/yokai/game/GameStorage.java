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
		int minCardX = -1, minCardY = -1, maxCardX = -1, maxCardY = -1;
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
	
	/**
	 * Checks if the given coords are inside the board bounds.
	 * @param cardX
	 * @param cardY
	 * @return true if the coords lays inside the board bounds.
	 */
	public boolean isInsideBoard(int cardX, int cardY) {
		return cardX>=0 && cardX<board[0].length && cardY>=0 && cardY<board.length;
	}
	
	public void centerBoard() {
		int[] edges = detectGameDeckEdges();
		
		System.out.println((edges[0] + (edges[2]-edges[0]+1)/2) + " " + board[0].length/2);
		
		if((edges[0] + (edges[2]-edges[0]+1)/2) - board[0].length/2 > 0) {
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[0].length-1; j++) {
					board[i][j] = board[i][j+1];
				}
			}
		}
		if((edges[0] + (edges[2]-edges[0]+1)/2) - board[0].length/2 < 0) {
			for(int i = 0; i < board.length; i++) {
				for(int j = board[0].length-1; j > 0 ; j--) {
					board[i][j] = board[i][j-1];
				}
			}
		}
		
		if((edges[1] + (edges[3]-edges[1]+1)/2) - board.length/2 > 0) {
			for(int j = board[0].length-1; j > 0 ; j--) {
				board[j] = board[j-1];
			}
		}
		if((edges[1] + (edges[3]-edges[1]+1)/2) - board.length/2 < 0) {
			for(int j = 0; j < board[0].length-1; j++) {
				board[j] = board[j+1];
			}
		}
	}
	
	/**
	 * Saves the GameStorage inside this file.
	 * @param file
	 */
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
	
	/**
	 * Loads a GameStorag from the given file
	 * @param file the file to load
	 * @return the GameStorage loaded or null if something isn't correct
	 */
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
