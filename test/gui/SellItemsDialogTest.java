
package gui;

import model.Player;
import model.PlayerType;
import model.Race;
import model.UnitTest;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class SellItemsDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testSellItemsDialog();
		
		return errorCount;
	}
	
	private void testSellItemsDialog() {
		Player player = new Player(new MockGUI(0), "hard", true, PlayerType.COMPUTER_EASY);
		(new SellItemsDialog(new MockGUI(0), player)).setVisible(true);
		assertEquals(player.getLevel(), 1);
		
		ItemCard item1 = new ItemCard(1, "item1", 100, 1);
		ItemCard item2 = new ItemCard(2, "item2", 200, 1);
		ItemCard item3 = new ItemCard(3, "item3", 300, 1);
		ItemCard item4 = new ItemCard(4, "item4", 400, 1);
		ItemCard item5 = new ItemCard(5, "item5", 500, 1);
		EquipmentCard equipment = new EquipmentCard(6, "Equipment", 600, 1, EquipmentType.OTHER, false, false, false);
		player.addCard(item1);
		player.addItem(item3);
		player.addCard(item4);
		player.addItem(item5);
		player.addItem(item2);
		player.getEquippedItems().add(equipment);
		
		player.goUpLevels(10, false);
		(new SellItemsDialog(new MockGUI(0), player)).setVisible(true);
		assertEquals(player.getLevel(), 9);
		assertEquals(player.getAllValueCards().size(), 6);
		
		player.goDownLevels(10);
		(new SellItemsDialog(new MockGUI(0), player)).setVisible(true);
		assertEquals(player.getLevel(), 3);
		assertEquals(player.getAllValueCards().size(), 1);
		
		player.addRaceCard(new RaceCard(7, "Halfling", Race.HALFLING));
		player.addItem(item5);
		player.addItem(item3);
		player.addUnequippedItem(equipment);
		(new SellItemsDialog(new MockGUI(0), player)).setVisible(true);
		assertEquals(player.getLevel(), 5);
		assertEquals(player.getAllValueCards().size(), 1);
		
		(new SellItemsDialog(new MockGUI(0), player)).setVisible(true);
		assertEquals(player.getLevel(), 5);
		assertEquals(player.getAllValueCards().size(), 1);
		
		EquipmentCard equipment2 = new EquipmentCard(8, "Equipment2", 1000, 3, EquipmentType.OTHER, false, false, false);
		player.getEquippedItems().add(equipment2);
		(new SellItemsDialog(new MockGUI(0), player)).setVisible(true);
		assertEquals(player.getLevel(), 5);
		assertEquals(player.getAllValueCards().size(), 2);
		
		/*public void setVisible(boolean b) {
		
			LinkedList<TreasureCard> cardsToSell = new LinkedList<TreasureCard>();
			int totalSellValue = 0;
			for (Card card : valueCards) {
				TreasureCard treasure = (TreasureCard)card;
				int treasureValue = treasure.getValue();
				if (AIManager.getCardValueToPlayer(treasure, player, true) <= treasureValue) {
					// sort cards to sell from greatest to least GP value
					int sortIndex;
					for (sortIndex = 0; sortIndex < cardsToSell.size(); sortIndex++)
						if (treasureValue >= cardsToSell.get(sortIndex).getValue())
							break;
					
					cardsToSell.add(sortIndex, treasure);
					totalSellValue += treasureValue;
				}
			}			
			if (cardsToSell.isEmpty())
				return;
			
			boolean hasDoubleSell = player.isHalfling() && !GM.usedDoubleSell();
			if (hasDoubleSell)
				totalSellValue += cardsToSell.getFirst().getValue();
			
			// don't sell items past level 9, since you can't buy levels past level 9
			int levelGain = totalSellValue / 1000;
			if (levelGain + player.getLevel() > 9)
				levelGain = 9 - player.getLevel();
			
			// only sell as many items as needed to get to the best sell value
			// i.e. don't sell an item if it doesn't help buy a new level
			Iterator<TreasureCard> treasureIter = cardsToSell.iterator();
			while (treasureIter.hasNext()) {
				TreasureCard treasure = treasureIter.next();
				int treasureValue = treasure.getValue();
				if (hasDoubleSell)
					treasureValue *= 2;
				if (totalSellValue - treasureValue > levelGain * 1000) {
					treasureIter.remove();
					totalSellValue -= treasureValue;
				}
				else
					hasDoubleSell = false;
			}
			
			// only use double sell ability when needed, since it can only be used once per turn
			String message = player.toString();
			if (player.isHalfling() && !GM.usedDoubleSell()) {
				if (totalSellValue - cardsToSell.getFirst().getValue() <= levelGain * 1000) {
					GM.sellingItemForDouble();
					message += ", using the Halfling's double sell ability,";
				}
			}
			
			// sell items and gain levels
			for (TreasureCard treasure : cardsToSell)
				player.discard(treasure);			
			player.goUpLevels(levelGain, false);
			
			LinkedList<Card> cards = new LinkedList<Card>();
			cards.addAll(cardsToSell);
			String title = message + " sold the following items for " + levelGain + " levels";
			(new DisplayCardsDialog(cards, title, true)).setVisible(true);
		}
		else
			super.setVisible(b);
	}*/
	}
}
