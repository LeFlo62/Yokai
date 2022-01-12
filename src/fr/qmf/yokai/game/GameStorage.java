package fr.qmf.yokai.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.function.Predicate;

public class GameStorage implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int DECK_LENGTH = 4;
	private static final int BOARD_LENGTH = DECK_LENGTH * DECK_LENGTH;

	private Player currentPlayer; // Not initialized yet.

	private Card[][] board;
	private GameStage currentStage = GameStage.PLAY_OR_GUESS;
	
	private byte[] hints; // All hints in the game, shuffled.
	private List<Byte> discoveredHints; // All discovered unplaced hints.
	private List<Byte> placedHints;

	private int cardsShown = 0;
	private int[] cardsShownCoords = new int[4];

	private int score;

	public void init() {
		board = new Card[BOARD_LENGTH][BOARD_LENGTH];

		// Init a list of types
		List<YokaiType> types = new ArrayList<>();
		for (int i = 0; i < BOARD_LENGTH; i++) {
			types.add(YokaiType.values()[i % YokaiType.values().length]);
		}

		// Randomizes its placement
		Collections.shuffle(types);
		int offset = (BOARD_LENGTH - DECK_LENGTH) / 2;
		for (int i = 0; i < DECK_LENGTH; i++) {
			for (int j = 0; j < DECK_LENGTH; j++) {
				board[offset + i][offset + j] = new Card(types.get(i * DECK_LENGTH + j));

			}
		}
		
		hints = YokaiType.getRandomHintArray(new Random(), 2, 3, 2);
		discoveredHints = new ArrayList<>();
		placedHints = new ArrayList<>();
	}
	
	public void calculateScore() {
		//Check if all families are connected.
		if(checkAllYokaiAreConnected()) {
			score = discoveredHints.size() * 2;
			
			for(int i = 0; i < hints.length; i++) {
				if(hints[i] != -1) {
					score += 5;
				}
			}
			
			for(int i = 0; i < BOARD_LENGTH; i++) {
				for(int j = 0; j < BOARD_LENGTH; j++) {
					Card card = board[i][j];
					if(card != null && card.hasHint()) {
						if(Arrays.asList(YokaiType.getYokaiFromHint(card.getHint())).contains(card.getType())) {
							score++;
						} else {
							score--;
						}
					}
				}
			}
		}
	}
	
	private boolean checkAllYokaiAreConnected() {
		List<YokaiType> yokaiDiscovered = new ArrayList<>();
		for(int i = 0; i < BOARD_LENGTH; i++) {
			for(int j = 0; j < BOARD_LENGTH; j++) {
				if(board[i][j] != null) {
					Card card = board[i][j];
					if(!yokaiDiscovered.contains(card.getType())) {
						yokaiDiscovered.add(card.getType());
						if(discoverCards(j, i, c->c.getType().equals(card.getType())).size() != DECK_LENGTH) {
							return false;
						}
						if(yokaiDiscovered.size() == YokaiType.values().length) return true;
					}
				}
			}
		}
		return true;
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
		return ((isInsideBoard(cardX+1, cardY) && board[cardY][cardX+1] != null && !board[cardY][cardX+1].isMoving())
				|| (isInsideBoard(cardX-1, cardY) && board[cardY][cardX-1] != null && !board[cardY][cardX-1].isMoving())
				|| (isInsideBoard(cardX, cardY+1) && board[cardY+1][cardX] != null && !board[cardY+1][cardX].isMoving())
				|| (isInsideBoard(cardX, cardY-1) && board[cardY-1][cardX] != null && !board[cardY-1][cardX].isMoving()))
				&& isIslandSafe(cardX, cardY);
	}
	
	public boolean isIslandSafe(int cardX, int cardY) {
		return discoverCards(cardX, cardY, c -> true).size() == DECK_LENGTH*DECK_LENGTH;
	}
	
	/**
	 * Needed for BFS algorithm.
	 */
	private static final class Point{
		private int x, y;
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			
			if(obj != null && obj instanceof Point) {
				Point p = (Point)obj;
				return x == p.x && y == p.y;
			}
			return false;
		}
	}
	
	/**
	 * A Breadth-First-Search algorithm to discover each points connected to the given one.
	 * 
	 * @param cardX
	 * @param cardY
	 * @param predicate Additional condition required by a card to be discovered.
	 * @return the list of discovered points
	 */
	private List<Point> discoverCards(int cardX, int cardY, Predicate<Card> predicate){
		Point s = new Point(cardX, cardY);
		
		List<Point> discovered = new ArrayList<>();
		Queue<Point> f = new ArrayDeque<>();
		f.add(s);
		discovered.add(s);
		while(!f.isEmpty()) {
			Point k = f.remove();
			List<Point> v = getNeighbors(k.x, k.y);
			for(Point t : v) {
				if(!discovered.contains(t) && predicate.test(board[t.y][t.x])) {
					f.add(t);
					discovered.add(t);
				}
			}
		}
		
		return discovered;
	}
	
	/**
	 * Gets the valids neighbors in the board for this point.
	 * 
	 * @param cardX
	 * @param cardY
	 * @return the list of neighbors of the given point.
	 */
	private List<Point> getNeighbors(int cardX, int cardY){
		List<Point> neighbors = new ArrayList<>();
		
		if (isInsideBoard(cardX+1, cardY) && board[cardY][cardX+1] != null && !board[cardY][cardX+1].isMoving()) {
			neighbors.add(new Point(cardX+1,cardY));
		}
		if (isInsideBoard(cardX-1, cardY) && board[cardY][cardX-1] != null && !board[cardY][cardX-1].isMoving()) {
			neighbors.add(new Point(cardX-1,cardY));
		}
	    if (isInsideBoard(cardX, cardY+1) && board[cardY+1][cardX] != null && !board[cardY+1][cardX].isMoving()) {
	    	neighbors.add(new Point(cardX,cardY+1));
	    }
	    if (isInsideBoard(cardX, cardY-1) && board[cardY-1][cardX] != null && !board[cardY-1][cardX].isMoving()) {
	    	neighbors.add(new Point(cardX,cardY-1));
	    }
				
		return neighbors;
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
		
		if((edges[0] + (edges[2]-edges[0]+1)/2) - board[0].length/2 > 0) {
			shiftBoardLeft();
		}
		if((edges[0] + (edges[2]-edges[0]+1)/2) - board[0].length/2 < 0) {
			shiftBoardRight();
		}
		
		if((edges[1] + (edges[3]-edges[1]+1)/2) - board.length/2 > 0) {
			shiftBoardUp();
		}
		if((edges[1] + (edges[3]-edges[1]+1)/2) - board.length/2 < 0) {
			shiftBoardDown();
		}
	}
	
	public int[] centerBoardBorder(int xCard, int yCard) {
		int dx=0, dy=0;
		if(xCard < 0) {
			shiftBoardRight();
			dx++;
		}
		
		if(xCard >= board[0].length) {
			shiftBoardLeft();
			dx--;
		}
		
		if(yCard < 0) {
			shiftBoardDown();
			dy++;
		}
		
		if(yCard >= board.length) {
			shiftBoardUp();
			dy--;
		}
		
		return new int[] {dx,dy};
	}
	
	private void shiftBoardLeft() {
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length-1; j++) {
				board[i][j] = board[i][j+1];
			}
		}
	}
	
	private void shiftBoardRight() {
		for(int i = 0; i < board.length; i++) {
			for(int j = board[0].length-1; j > 0 ; j--) {
				board[i][j] = board[i][j-1];
			}
		}
	}
	
	private void shiftBoardUp() {
		for(int j = 0; j < board[0].length-1; j++) {
			board[j] = board[j+1];
		}
	}
	
	private void shiftBoardDown() {
		for(int j = board[0].length-1; j > 0 ; j--) {
			board[j] = board[j-1];
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
	
	public byte[] getHints() {
		return hints;
	}
	
	public List<Byte> getDiscoveredHints() {
		return discoveredHints;
	}
	
	public List<Byte> getPlacedHints() {
		return placedHints;
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
