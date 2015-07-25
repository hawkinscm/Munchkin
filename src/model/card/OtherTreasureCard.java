
package model.card;

/**
 * Class that represents an uncategorized/miscellaneous type of Treasure Card.
 */
public class OtherTreasureCard extends TreasureCard {

	/**
	 * Creates a new OtherTreasureCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 */
	public OtherTreasureCard(int i, String n) {
		super(i, n);
		
		id = i;
		name = n;
		value = 0;
	}
}
