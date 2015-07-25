
package model.card;

/**
 * Class that represents an Item Card.
 */
public class ItemCard extends TreasureCard {

	private int bonus;
	
	/**
	 * Creates a new ItemCard Card with a name and GP value and a level bonus of zero.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param v GP value of the new card
	 */
	public ItemCard(int i, String n, int v) {
		super(i, n);
		
		id = i;
		name = n;
		value = v;
		bonus = 0;
	}
	
	/**
	 * Creates a new ItemCard Card with a name, GP value, and level bonus.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param v GP value of the new card
	 * @param b level bonus that this card gives
	 */
	public ItemCard(int i, String n, int v, int b) {
		super(i, n);
		
		id = i;
		name = n;
		value = v;
		bonus = b;
	}

	/**
	 * Returns the level bonus gained from this card.
	 * @return the level bonus gained from this card
	 */
	public int getBonus() {
		return bonus;
	}
}
