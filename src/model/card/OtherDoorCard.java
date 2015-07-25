
package model.card;

/**
 * Class that represents an uncategorized/miscellaneous type of Door Card.
 */
public class OtherDoorCard extends DoorCard {
	
	/**
	 * Creates a new OtherDoorCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 */
	public OtherDoorCard(int i, String n) {
		super(i, n);
		
		id = i;
		name = n;
	}
}
