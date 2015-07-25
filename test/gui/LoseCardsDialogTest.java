
package gui;

import java.util.LinkedList;

import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.Card;
import model.card.ItemCard;

public class LoseCardsDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testLoseCardsDialog();
		
		return errorCount;
	}
	
	private void testLoseCardsDialog() {
		Player player = new Player(new MockGUI(0), "hard", true, PlayerType.COMPUTER_HARD);
		ItemCard item1 = new ItemCard(1, "item1", 100);
		ItemCard item2 = new ItemCard(2, "item2", 200);
		ItemCard item3 = new ItemCard(3, "item3", 300);
		ItemCard item4 = new ItemCard(4, "item4", 400);
		ItemCard item5 = new ItemCard(5, "item5", 500);
		player.addCard(item1);
		player.addCard(item2);
		player.addCard(item3);
		player.addCard(item4);
		player.addCard(item5);
				
		assertEquals(player.getHandCards().size(), 5);
		LoseCardsDialog dialog = new LoseCardsDialog(player, player.getHandCards(), 3, "");
		dialog.setVisible(true);
		assertTrue(dialog.madeDiscard());
		assertEquals(player.getHandCards().size(), 2);
		assertTrue(player.getHandCards().contains(item4));
		assertTrue(player.getHandCards().contains(item5));
		
		assertEquals(player.getHandCards().size(), 2);
		dialog = new LoseCardsDialog(player, player.getHandCards(), 1, "");
		dialog.setVisible(true);
		assertTrue(dialog.madeDiscard());
		assertEquals(player.getHandCards().size(), 1);
		assertTrue(player.getHandCards().contains(item5));
		
		player.addCard(item1);
		player.getCarriedItems().add(item3);
		assertEquals(player.getCarriedItems().size(), 1);
		assertEquals(player.getHandCards().size(), 2);
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(player.getAllValueCards());
		dialog = new LoseCardsDialog(player, cards, 1, "");
		dialog.setVisible(true);
		assertTrue(dialog.madeDiscard());
		assertEquals(player.getHandCards().size(), 1);
		assertTrue(player.getHandCards().contains(item5));
		assertEquals(player.getCarriedItems().size(), 1);
		assertTrue(player.getCarriedItems().contains(item3));
		
		cards = new LinkedList<Card>();
		cards.addAll(player.getAllValueCards());
		dialog = new LoseCardsDialog(player, cards, 1, "");
		dialog.setVisible(true);
		assertTrue(dialog.madeDiscard());
		assertEquals(player.getHandCards().size(), 1);
		assertTrue(player.getHandCards().contains(item5));
		assertEquals(player.getCarriedItems().size(), 0);
		
		cards = new LinkedList<Card>();
		cards.addAll(player.getAllValueCards());
		dialog = new LoseCardsDialog(player, cards, 1, "");
		dialog.setVisible(true);
		assertTrue(dialog.madeDiscard());
		assertEquals(player.getHandCards().size(), 0);
		
		cards = new LinkedList<Card>();
		cards.addAll(player.getAllValueCards());
		dialog = new LoseCardsDialog(player, cards, 2, "");
		dialog.setVisible(true);
		assertTrue(dialog.madeDiscard());
		assertEquals(player.getHandCards().size(), 0);
	}
}
