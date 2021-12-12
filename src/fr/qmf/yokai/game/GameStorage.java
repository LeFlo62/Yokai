package fr.qmf.yokai.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameStorage {

	private int deckLength;
	private int boardLength;

	private Player currentPlayer; // Not initialized yet.

	private Card[][] board;
	private GameStage currentStage = GameStage.PLAY_OR_GUESS;
	
	private int cardsShown = 0;
	private int[] cardsShownCoords = new int[4]; //Place elsewhere ?

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

	
	
}
