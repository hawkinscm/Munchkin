package ai;

import model.card.Card;

/**
 * Simple class representing a card and its value to the player who owns it.
 */
public class AIValuedCard {

	private Card card;
	private int value;
	
	/**
	 * Creates a new AIValuedCard.
	 * @param c card to set
	 * @param v value to set
	 */
	public AIValuedCard(Card c, int v) {
		card = c;
		value = v;
	}
	
	/**
	 * Returns the card.
	 * @return the card
	 */
	public Card getCard() {
		return card;
	}
	
	/**
	 * Returns the card value.
	 * @return the card value
	 */
	public int getValue() {
		return value;
	}
}
