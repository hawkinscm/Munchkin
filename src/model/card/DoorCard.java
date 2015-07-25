
package model.card;

/**
 * Class that represents a Door Card
 */
public class DoorCard extends Card {
	/**
	 * Defines the parameters that subclasses must have.
	 * @param i unique id of the card
	 * @param n name of the card
	 */
	protected DoorCard(int i, String n) {
		super(i, n);
		id = i;
		name = n;
	}
}
