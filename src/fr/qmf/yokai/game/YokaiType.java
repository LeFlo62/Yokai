package fr.qmf.yokai.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum YokaiType {
	
	ONI("blue"),
	KAPPA("green"),
	ROKUROKUBI("purple"),
	KITSUNE("red");
	
	private String color;
	
	private YokaiType(String color) {
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
    
	/**
	 * Get a YokaiType array from a hint byte.
	 * 
	 * @param hint A byte between 0b0001 and 0b1110.
	 * @return The corresponding YokaiType for the represented hint byte.
	 */
	public static YokaiType[] getYokaiFromHint(byte hint) {
		if(hint == 0 || hint > 15) return null;
		
		List<YokaiType> list = new ArrayList<>(4);
		
		for(int i = values().length; i >=0 ; i--) {
			if((hint & (0b1 << i)) != 0) {
				list.add(values()[values().length - i -1]);
			}
		}
		
		return list.toArray(new YokaiType[list.size()]);
	}
	
	/**
	 * Generates with the specified random a byte between 0b0001 and 0b1110
	 * used to represent a hint. It represents 1 to 3 Yokai on a hint.
	 * 
	 * @param random The random instance to be used.
	 * @return A byte between 0b0001 and 0b1110.
	 */
	public int getRandomHint(Random random) {
		return random.nextInt(15-1)+1; //Min: 0b0001 Max: 0b1110.
	}
	
}
