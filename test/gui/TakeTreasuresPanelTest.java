
package gui;

import java.util.LinkedList;

import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;

import model.Battle;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import model.UnitTest;
import model.card.Card;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.TreasureCard;
import model.card.EquipmentCard.EquipmentType;

public class TakeTreasuresPanelTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private Player easy;
	private Player medium;
	private Player hard;
	
	private Battle battle;
	
	private ItemCard item1;
	private ItemCard item2;
	private ItemCard item3;
	private ItemCard item4;
	private ItemCard item5;
	private ItemCard item6;
	private ItemCard item7;
	private EquipmentCard equip8;
	private ItemCard item9;
	private GoUpLevelCard level;
	private ItemCard item11;
	private EquipmentCard equip12;
	
	public int testAll() {
		testHandleComputerPlayers();
		
		return errorCount;
	}
	
	private void reset() {
		item1 = new ItemCard(1, "1", 100);
		item2 = new ItemCard(2, "2", 200);
		item3 = new ItemCard(3, "3", 300);
		item4 = new ItemCard(4, "4", 400);
		item5 = new ItemCard(5, "5", 500);
		item6 = new ItemCard(6, "6", 600);
		item7 = new ItemCard(7, "7", 700);
		equip8 = new EquipmentCard(8, "8", 100, 2, EquipmentType.OTHER, false, false, false);
		item9 = new ItemCard(9, "9", 900);
		level = new GoUpLevelCard(10, "level");
		item11 = new ItemCard(11, "11", 1100);
		equip12 = new EquipmentCard(12, "12", 100, 3, EquipmentType.OTHER, false, false, false);
		LinkedList<TreasureCard> treasures = new LinkedList<TreasureCard>();
		treasures.add(item1);
		treasures.add(item2);
		treasures.add(item3);
		treasures.add(item4);
		treasures.add(item5);
		treasures.add(item6);
		treasures.add(item7);
		treasures.add(equip8);
		treasures.add(item9);
		treasures.add(level);
		treasures.add(item11);
		treasures.add(equip12);
				
		try {
			while (true) {
				try { GM.getTreasureDeck().drawCard(); }
				catch (PlayImmediatelyException ex) { GM.getTreasureDeck().getDiscardPile().clear(); }
			}
		}
		catch (NoCardsLeftException ex) {}
		
		while (!treasures.isEmpty()) {
			int treasureIdx = Randomizer.getRandom(treasures.size());
			GM.getTreasureDeck().discard(treasures.remove(treasureIdx));
		}
		
		easy.getHandCards().clear();
		easy.getEquippedItems().clear();
		easy.getUnequippedItems().clear();
		easy.getCarriedItems().clear();
		easy.goDownLevels(10);
		medium.getHandCards().clear();
		medium.getEquippedItems().clear();
		medium.getUnequippedItems().clear();
		medium.getCarriedItems().clear();
		medium.goDownLevels(10);
		hard.getHandCards().clear();
		hard.getEquippedItems().clear();
		hard.getUnequippedItems().clear();
		hard.getCarriedItems().clear();
		hard.goDownLevels(10);
		
		battle = new Battle(easy, new MonsterCard(15, "test", 3, 1, 1, false) {
			public void doBadStuff(Player player) {}
		});
	}
	
	private void testHandleComputerPlayers() {
		GUI mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		GM.newTestGame(mockGUI, players);

		EquipmentCard kneepads = new EquipmentCard(Card.E_KNEEPADS_OF_ALLURE, "Kneepads of Allure", 600, 0, EquipmentType.OTHER, false, true, false);
		
		// no treasures
		reset();
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 0);
		assertEquals(easy.getLevel(), 1);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(medium.getLevel(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertEquals(hard.getLevel(), 1);
		
		// no helper
		battle.addTreasures(12);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 11);
		assertEquals(easy.getEquippedItems().size(), 2);
		assertEquals(easy.getLevel(), 2);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(medium.getLevel(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertEquals(hard.getLevel(), 1);
		assertTrue(easy.hasEquipped(equip12));
		assertTrue(easy.getCarriedItems().contains(item11));
		assertTrue(easy.getCarriedItems().contains(item9));
		assertTrue(easy.hasEquipped(equip8));
		assertTrue(easy.getCarriedItems().contains(item7));
		assertTrue(easy.getCarriedItems().contains(item6));
		assertTrue(easy.getCarriedItems().contains(item5));
		assertTrue(easy.getCarriedItems().contains(item4));
		assertTrue(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item2));
		assertTrue(easy.getCarriedItems().contains(item1));
		
		// kneepads
		reset();
		battle.addTreasures(12);
		easy.getEquippedItems().add(kneepads);
		battle.addHelper(hard);
		LinkedList<Player> playerOrder = new LinkedList<Player>();
		playerOrder.add(easy);
		playerOrder.add(hard);
		playerOrder.add(hard);
		playerOrder.add(easy);
		playerOrder.add(hard);
		playerOrder.add(hard);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 9);
		assertEquals(easy.getLevel(), 1);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(medium.getLevel(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 3);
		assertEquals(hard.getLevel(), 2);
		assertTrue(easy.hasEquipped(equip12));
		assertTrue(hard.getCarriedItems().contains(item11));
		assertTrue(easy.getCarriedItems().contains(item9));
		assertTrue(hard.hasEquipped(equip8));
		assertTrue(hard.getCarriedItems().contains(item7));
		assertTrue(easy.getCarriedItems().contains(item6));
		assertTrue(easy.getCarriedItems().contains(item5));
		assertTrue(easy.getCarriedItems().contains(item4));
		assertTrue(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item2));
		assertTrue(easy.getCarriedItems().contains(item1));
		
		// helper is easy
		reset();
		battle.activePlayer = medium;
		battle.addHelper(easy);
		battle.addTreasures(12);
		playerOrder.clear();
		playerOrder.add(medium);
		playerOrder.add(easy);
		playerOrder.add(easy);
		playerOrder.add(easy);
		playerOrder.add(medium);
		playerOrder.add(easy);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 3);
		assertEquals(easy.getLevel(), 2);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 8);
		assertEquals(medium.getLevel(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertEquals(hard.getLevel(), 1);
		assertTrue(medium.hasEquipped(equip12));
		assertTrue(easy.getCarriedItems().contains(item11));
		assertTrue(easy.getCarriedItems().contains(item9));
		assertTrue(medium.hasEquipped(equip8));
		assertTrue(easy.getCarriedItems().contains(item7));
		assertTrue(medium.getCarriedItems().contains(item6));
		assertTrue(medium.getCarriedItems().contains(item5));
		assertTrue(medium.getCarriedItems().contains(item4));
		assertTrue(medium.getCarriedItems().contains(item3));
		assertTrue(medium.getCarriedItems().contains(item2));
		assertTrue(medium.getCarriedItems().contains(item1));
		
		// helper is easy - gets all
		reset();
		battle.activePlayer = medium;
		battle.addHelper(easy);
		battle.addTreasures(12);
		playerOrder.clear();
		for (int count = 0; count < 12; count++)
			playerOrder.add(easy);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 11);
		assertEquals(easy.getLevel(), 2);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(medium.getLevel(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertEquals(hard.getLevel(), 1);
		assertTrue(easy.hasEquipped(equip12));
		assertTrue(easy.getCarriedItems().contains(item11));
		assertTrue(easy.getCarriedItems().contains(item9));
		assertTrue(easy.hasEquipped(equip8));
		assertTrue(easy.getCarriedItems().contains(item7));
		assertTrue(easy.getCarriedItems().contains(item6));
		assertTrue(easy.getCarriedItems().contains(item5));
		assertTrue(easy.getCarriedItems().contains(item4));
		assertTrue(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item2));
		assertTrue(easy.getCarriedItems().contains(item1));
		
		// helper is hard
		reset();
		battle.activePlayer = medium;
		battle.addHelper(hard);
		battle.addTreasures(12);
		playerOrder.clear();
		playerOrder.add(hard);
		playerOrder.add(hard);
		playerOrder.add(medium);
		playerOrder.add(medium);
		playerOrder.add(hard);
		playerOrder.add(hard);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 0);
		assertEquals(easy.getLevel(), 1);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 1);
		assertEquals(medium.getLevel(), 2);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 10);
		assertEquals(hard.getLevel(), 1);
		assertTrue(hard.hasEquipped(equip12));
		assertTrue(hard.getCarriedItems().contains(item11));
		assertTrue(medium.getCarriedItems().contains(item9));
		assertTrue(hard.hasEquipped(equip8));
		assertTrue(hard.getCarriedItems().contains(item7));
		assertTrue(hard.getCarriedItems().contains(item6));
		assertTrue(hard.getCarriedItems().contains(item5));
		assertTrue(hard.getCarriedItems().contains(item4));
		assertTrue(hard.getCarriedItems().contains(item3));
		assertTrue(hard.getCarriedItems().contains(item2));
		assertTrue(hard.getCarriedItems().contains(item1));
		
		// helper is hard - takes al
		reset();
		battle.activePlayer = medium;
		battle.addHelper(hard);
		battle.addTreasures(12);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 0);
		assertEquals(easy.getLevel(), 1);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(medium.getLevel(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 11);
		assertEquals(hard.getLevel(), 2);
		assertTrue(hard.hasEquipped(equip12));
		assertTrue(hard.getCarriedItems().contains(item11));
		assertTrue(hard.getCarriedItems().contains(item9));
		assertTrue(hard.hasEquipped(equip8));
		assertTrue(hard.getCarriedItems().contains(item7));
		assertTrue(hard.getCarriedItems().contains(item6));
		assertTrue(hard.getCarriedItems().contains(item5));
		assertTrue(hard.getCarriedItems().contains(item4));
		assertTrue(hard.getCarriedItems().contains(item3));
		assertTrue(hard.getCarriedItems().contains(item2));
		assertTrue(hard.getCarriedItems().contains(item1));
		
		// helper is medium
		reset();
		battle.activePlayer = easy;
		battle.addHelper(medium);
		battle.addTreasures(12);
		playerOrder.clear();
		playerOrder.add(medium);
		playerOrder.add(medium);
		playerOrder.add(medium);
		playerOrder.add(easy);
		playerOrder.add(easy);
		playerOrder.add(easy);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		new TakeTreasuresPanel(mockGUI, battle);
		assertEquals(easy.getHandCards().size(), 0);
		assertTrue(easy.getAllItems().size() >= 3);
		assertEquals(easy.getLevel(), 1);
		assertEquals(medium.getHandCards().size(), 0);
		assertTrue(medium.getAllItems().size() >= 2);
		assertEquals(medium.getLevel(), 2);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertEquals(hard.getLevel(), 1);
		assertEquals(medium.getAllItems().size() + easy.getAllItems().size(), 11);
		assertTrue(medium.hasEquipped(equip12));
		assertTrue(medium.getCarriedItems().contains(item11));
		assertTrue(easy.getCarriedItems().contains(item9));
		assertTrue(easy.hasEquipped(equip8));
		assertTrue(easy.getCarriedItems().contains(item7));
	}
}
