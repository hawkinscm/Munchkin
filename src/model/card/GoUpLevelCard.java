
package model.card;

/**
 * Class that represents a Go Up A Level Card.
 */
public class GoUpLevelCard extends TreasureCard {

	/**
	 * Creates a new GoUpLevelCard card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 */
	public GoUpLevelCard(int i, String n) {
		super(i, n);
		
		id = i;
		name = n;
	}
}
