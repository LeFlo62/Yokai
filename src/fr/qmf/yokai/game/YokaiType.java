package fr.qmf.yokai.game;

import java.util.ArrayList;
import java.util.Collections;
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
	 * @return The corresponding YokaiTypes for the represented hint byte.
	 */
	public static YokaiType[] getYokaiFromHint(byte hint) {
		if(hint == 0 || hint > 14) return null;
		
		List<YokaiType> list = new ArrayList<>(4);
		
		for(int i = values().length-1; i >=0 ; i--) {
			if((hint & (0b1 << i)) != 0) {
				list.add(values()[values().length - i -1]);
			}
		}
		
		return list.toArray(new YokaiType[list.size()]);
	}
	
	public static String getYokaisString(YokaiType[] yokais) {
		String[] yokaiString = new String[yokais.length];
		for(int k = 0; k < yokais.length; k++) {
			yokaiString[k] = yokais[k].getColor();
		}
		return String.join("_", yokaiString);
	}
	
	/**
	 * Generates with the specified a shuffled byte array with all bytes between 0b0001 and 0b1110
	 * used to represent a hint. Each byte represents 1 to 3 Yokai on a hint.
	 * 
	 * @param random The random instance to be used.
	 * @return A 15 bytes array containing all bytes between 0b0001 and 0b1110.
	 */
	public static int[] getRandomHintArray(Random random) {
		List<Integer> bytes = new ArrayList<>();
		for(int i = 1; i <= 14; i++) {
			bytes.add(i);
		}
		
		Collections.shuffle(bytes, random);
		
		int[] bytesArray = new int[bytes.size()];
		for(int i = 0; i < bytesArray.length; i++) {
			bytesArray[i] = bytes.get(i);
		}
		
		return bytesArray;
	}
	
}
