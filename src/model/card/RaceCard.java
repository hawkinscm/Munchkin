
package model.card;

import model.Race;

/**
 * Class that represents a Race Card.
 */
public class RaceCard extends DoorCard {
	
	private Race race;
	
	/**
	 * Creates a new RaceCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param r race type that the card represents
	 */
	public RaceCard(int i, String n, Race r) {
		super(i, n);
		
		id = i;
		name = n;
		race = r;
	}
	
	/**
	 * Returns the race type that the card represents.
	 * @return the race type that the card represents
	 */
	public Race getRace() {
		return race;
	}
}