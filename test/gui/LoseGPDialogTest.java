
package gui;

import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.ItemCard;

public class LoseGPDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testLoseGPDialog();
		
		return errorCount;
	}
	
	private void testLoseGPDialog() {
		Player player = new Player(new MockGUI(0), "hard", true, PlayerType.COMPUTER_EASY);
		ItemCard item0 = new ItemCard(0, "item0", 0, 1);
		ItemCard item1 = new ItemCard(1, "item1", 100, 1);
		ItemCard item2 = new ItemCard(2, "item2", 200, 1);
		ItemCard item3 = new ItemCard(3, "item3", 300, 1);
		ItemCard item4 = new ItemCard(4, "item4", 400, 1);
		ItemCard item5 = new ItemCard(5, "item5", 500, 1);
		player.addItem(item1);
		player.addItem(item3);
		player.addItem(item4);
		player.addItem(item0);
		player.addItem(item5);
		player.addItem(item2);
		
		assertEquals(player.getCarriedItems().size(), 6);
		LoseGPDialog dialog = new LoseGPDialog(player, 0);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 0);
		assertEquals(player.getCarriedItems().size(), 5);
		assertFalse(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertTrue(player.getCarriedItems().contains(item5));
		player.addItem(item0);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 100);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 100);
		assertEquals(player.getCarriedItems().size(), 5);
		assertTrue(player.getCarriedItems().contains(item0));
		assertFalse(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertTrue(player.getCarriedItems().contains(item5));
		player.addItem(item1);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 101);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 200);
		assertEquals(player.getCarriedItems().size(), 5);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertFalse(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertTrue(player.getCarriedItems().contains(item5));
		player.addItem(item2);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 200);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 200);
		assertEquals(player.getCarriedItems().size(), 5);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertFalse(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertTrue(player.getCarriedItems().contains(item5));
		player.addItem(item2);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 300);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 300);
		assertEquals(player.getCarriedItems().size(), 5);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertFalse(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertTrue(player.getCarriedItems().contains(item5));
		player.addItem(item3);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 400);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 400);
		assertEquals(player.getCarriedItems().size(), 5);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertTrue(player.getCarriedItems().contains(item5));
		player.addItem(item4);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 499);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 500);
		assertEquals(player.getCarriedItems().size(), 5);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item5);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 551);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 600);
		assertEquals(player.getCarriedItems().size(), 4);
		assertTrue(player.getCarriedItems().contains(item0));
		assertFalse(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item5);
		player.addItem(item1);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 700);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 700);
		assertEquals(player.getCarriedItems().size(), 4);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertFalse(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item5);
		player.addItem(item2);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 750);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 800);
		assertEquals(player.getCarriedItems().size(), 4);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertFalse(player.getCarriedItems().contains(item3));
		assertTrue(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item5);
		player.addItem(item3);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 890);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 900);
		assertEquals(player.getCarriedItems().size(), 4);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item4);
		player.addItem(item5);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 999);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 1000);
		assertEquals(player.getCarriedItems().size(), 3);
		assertTrue(player.getCarriedItems().contains(item0));
		assertFalse(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item5);
		player.addItem(item4);
		player.addItem(item1);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1001);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 1100);
		assertEquals(player.getCarriedItems().size(), 3);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertFalse(player.getCarriedItems().contains(item2));
		assertTrue(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item2);
		player.addItem(item4);
		player.addItem(item5);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1200);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 1200);
		assertEquals(player.getCarriedItems().size(), 3);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertFalse(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item4);
		player.addItem(item3);
		player.addItem(item5);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1300);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 1300);
		assertEquals(player.getCarriedItems().size(), 2);
		assertTrue(player.getCarriedItems().contains(item0));
		assertFalse(player.getCarriedItems().contains(item1));
		assertTrue(player.getCarriedItems().contains(item2));
		assertFalse(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item4);
		player.addItem(item3);
		player.addItem(item5);
		player.addItem(item1);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1400);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 1400);
		assertEquals(player.getCarriedItems().size(), 2);
		assertTrue(player.getCarriedItems().contains(item0));
		assertTrue(player.getCarriedItems().contains(item1));
		assertFalse(player.getCarriedItems().contains(item2));
		assertFalse(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item4);
		player.addItem(item3);
		player.addItem(item5);
		player.addItem(item2);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1500);
		dialog.setVisible(true);
		assertTrue(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 1500);
		assertEquals(player.getCarriedItems().size(), 1);
		assertTrue(player.getCarriedItems().contains(item0));
		assertFalse(player.getCarriedItems().contains(item1));
		assertFalse(player.getCarriedItems().contains(item2));
		assertFalse(player.getCarriedItems().contains(item3));
		assertFalse(player.getCarriedItems().contains(item4));
		assertFalse(player.getCarriedItems().contains(item5));
		player.addItem(item4);
		player.addItem(item1);
		player.addItem(item5);
		player.addItem(item3);
		player.addItem(item2);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1501);
		dialog.setVisible(true);
		assertFalse(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 0);
		assertEquals(player.getCarriedItems().size(), 6);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 1600);
		dialog.setVisible(true);
		assertFalse(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 0);
		assertEquals(player.getCarriedItems().size(), 6);
		
		assertEquals(player.getCarriedItems().size(), 6);
		dialog = new LoseGPDialog(player, 99999);
		dialog.setVisible(true);
		assertFalse(dialog.hasDiscardedItems());
		assertEquals(dialog.getTotalValueDiscarded(), 0);
		assertEquals(player.getCarriedItems().size(), 6);
	}
}
