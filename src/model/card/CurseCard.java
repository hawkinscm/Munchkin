
package model.card;

import model.Player;

/**
 * Abstract class that represents a Curse Card
 */
public abstract class CurseCard extends DoorCard {
	
	/**
	 * Creates a new CurseCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 */
	public CurseCard(int i, String n) {
		super(i, n);
		
		id = i;
		name = n;
	}
	
	/**
	 * Abstract method that will perform the effects of the card on the specified player
	 * @param player the specified player
	 */
	public abstract void addEffects(Player player);
	
	/**
	 * Returns whether or not this curse is a lasting curse.
	 * @return true if this curse card is a lasting curse; false if it is an immediate, one-time curse
	 */
	public boolean isLastingCurse() {
		return false;
	}
}