package fr.qmf.yokai;

/**
 * Indicates something may receives updates
 * 
 * @author LeFlo
 *
 */
public interface Tickable {
	
	/**
	 * This method will be called if it's part of the main game loop update
	 */
	public void tick();

}
