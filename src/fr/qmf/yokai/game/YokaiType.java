package fr.qmf.yokai.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a YokaiType.
 * @author LeFlo
 *
 */
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
	
	/**
	 * Transforms a given YokaiType array into a string joined with '_'.
	 * @param yokais
	 * @return The given Yokais joined with '_'.
	 */
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
	 * @param singleColorNumber The number of hints of single color
	 * @param doubleColorNumber The number of hints of double color
	 * @param tripleColorNumber The number of hints of triple color
	 * @return A bytes array containing all bytes between 0b0001 and 0b1110.
	 */
	public static byte[] getRandomHintArray(Random random, int singleColorNumber, int doubleColorNumber, int tripleColorNumber) {
		List<Integer> bytes = new ArrayList<>();
		for(int i = 0; i < singleColorNumber; i++) {
			int n1 = 0;
			int hint = 0;
			do {
				n1 = random.nextInt(values().length);
				hint = (int) Math.pow(2, n1);
			} while(bytes.contains(hint));
			bytes.add(hint);
		}
		for(int i = 0; i < doubleColorNumber; i++) {
			int n1 = random.nextInt(values().length);
			int hint = 0;
			do {
				n1 = random.nextInt(values().length);
				int n2 = 0;
				do {
					n2 = random.nextInt(values().length);
				} while(n1 == n2);
				hint = (int) (Math.pow(2, n1) + Math.pow(2, n2));
			} while(bytes.contains(hint));
			
			
			bytes.add(hint);
		}
		for(int i = 0; i < tripleColorNumber; i++) {
			int n1 = 0;
			int hint = 0;
			do {
				n1 = random.nextInt(values().length);
				hint = 15-((int) Math.pow(2, n1));
			} while(bytes.contains(hint));
			bytes.add(hint);
		}
		
		Collections.shuffle(bytes, random);
		
		byte[] bytesArray = new byte[bytes.size()];
		for(int i = 0; i < bytesArray.length; i++) {
			bytesArray[i] = bytes.get(i).byteValue();
		}
		
		return bytesArray;
	}
	
}
