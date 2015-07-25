
package model.card;

import model.UnitTest;
import model.card.Card;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;

public class CardTest extends UnitTest {

	public int testAll() {
		testCard();
		testEquals();

		return errorCount;
	}	
	
	private void testCard() {
		Card card = new Card(1, "Test");
		assertEquals(card.getID(), 1);
		assertEquals(card.getName(), "Test");
	}
	
	private void testEquals() {
		OtherDoorCard card1 = new OtherDoorCard(1, "A");
		Card card2 = new OtherDoorCard(2, "A");
		OtherTreasureCard card11 = new OtherTreasureCard(1, "A");
		Card card22 = new OtherTreasureCard(2, "B");
		
		assertTrue(card1.equals(card1));
		assertFalse(card1.equals(card2));
		assertFalse(card2.equals(card1));
		assertTrue(card1.equals(card11));
		assertTrue(card11.equals(card1));
		assertFalse(card1.equals(card22));
		assertFalse(card22.equals(card1));
		assertTrue(card2.equals(card22));
		assertTrue(card22.equals(card2));
	}
}
