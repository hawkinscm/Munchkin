
package model.card;

/**
 * Class that represents a Treasure Card.
 */
public class TreasureCard extends Card {

	/**
	 * Defines the parameters that subclasses must have.
	 * @param i unique id of the card
	 * @param n name of the card
	 */
	protected TreasureCard(int i, String n) {
		super(i, n);
		id = i;
		name = n;
	}
	
	/**
	 * GP value of the card.
	 */
	protected int value;
	
	/**
	 * Returns the GP value of this card.
	 * @return the GP value of this card
	 */
	public int getValue() {
		return value;
	}
}
