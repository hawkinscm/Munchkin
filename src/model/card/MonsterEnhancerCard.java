
package model.card;

/**
 * Class that represents a Monster Enhancer Card.
 */
public class MonsterEnhancerCard extends DoorCard {

	int bonus = 0;
	
	/**
	 * Creates a new MonsterEnhancerCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param b bonus given to the monster by the card.
	 */
	public MonsterEnhancerCard(int i, String n, int b) {
		super(i, n);
		
		id = i;
		name = n;
		bonus = b;
	}
	
	/**
	 * Returns the bonus given to the monster by the card.
	 * @return the bonus given to the monster by the card
	 */
	public int getBonus() {
		return bonus;
	}
}