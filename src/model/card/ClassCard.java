
package model.card;

import model.Class;

/**
 * Class that represents a character class Card.
 */
public class ClassCard extends DoorCard {
	
	private Class characterClass;
	
	/**
	 * Creates a new ClassCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param c character class type that the card represents
	 */
	public ClassCard(int i, String n, Class c) {
		super(i, n);
		
		id = i;
		name = n;
		characterClass = c;
	}
	
	/**
	 * Returns the character class type that the card represents.
	 * @return the character class type that the card represents
	 */
	public Class getCharacterClass() {
		return characterClass;
	}
}