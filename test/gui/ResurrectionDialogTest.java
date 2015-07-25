
package gui;

import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.GoUpLevelCard;
import model.card.ItemCard;

public class ResurrectionDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testResurrect();
		
		return errorCount;
	}
	
	private void testResurrect() {
		//public ResurrectionDialog(Player p, Card card) {
		//public Card getCardToDiscard()
		Player activePlayer = new Player(new MockGUI(0), "player", true, PlayerType.COMPUTER_MEDIUM);
		GoUpLevelCard levelCard = new GoUpLevelCard(4, "level");
		ItemCard item0 = new ItemCard(0, "item0", 0);
		ItemCard item199 = new ItemCard(1, "item199", 199);
		ItemCard item200 = new ItemCard(2, "item200", 200);
		ItemCard item201 = new ItemCard(3, "item201", 201);
		
		activePlayer.addCard(item200);
		activePlayer.addCard(item0);
		activePlayer.addCard(item201);
		activePlayer.addCard(item199);
		ResurrectionDialog dialog = new ResurrectionDialog(activePlayer, levelCard);
		assertTrue(dialog.getCardToDiscard() == item0);
		activePlayer.discard(item0);
		
		dialog = new ResurrectionDialog(activePlayer, levelCard);
		assertTrue(dialog.getCardToDiscard() == item199);
		activePlayer.discard(item199);
		
		dialog = new ResurrectionDialog(activePlayer, levelCard);
		assertTrue(dialog.getCardToDiscard() == item200);
		activePlayer.discard(item200);
		
		dialog = new ResurrectionDialog(activePlayer, levelCard);
		assertTrue(dialog.getCardToDiscard() == null);
	}
}
