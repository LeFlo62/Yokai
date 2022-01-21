package fr.qmf.yokai.game;

import java.io.Serializable;

/**
 * Represents a Player.
 * @author LeFlo
 *
 */
public class Player implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	
	public Player(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}


	public int getId() {
		return id;
	}
}
