
package gui;

import java.util.LinkedList;
import java.util.Stack;

import model.Class;
import model.DoorDeckFactory;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.TreasureDeckFactory;
import model.UnitTest;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.RaceCard;

public class TradeItemsDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	private MonsterCard nose;
	
	private ItemCard water;
	private ItemCard loaded;
	private ItemCard halitosis;
	
	private EquipmentCard sword;
	private EquipmentCard club;
	private EquipmentCard rapier;
	private EquipmentCard helm;
	
	public int testAll() {
		testTradeItemsDialog();
		
		return errorCount;
	}
	
	private void testTradeItemsDialog() {
		GUI mockGUI = new MockGUI(0);
		Player easy = new Player(mockGUI, "easy", false, PlayerType.COMPUTER_EASY);
		Player medium = new Player(mockGUI, "medium", true, PlayerType.COMPUTER_MEDIUM);
		Player hard = new Player(mockGUI, "hard", false, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck)
			if (card.getID() == Card.M_FLOATING_NOSE)
				nose = (MonsterCard)card;
		
		Stack<Card> treasureDeck = TreasureDeckFactory.buildDeck();
		for (Card card : treasureDeck) {
			if (card.getID() == Card.E_BROAD_SWORD)
				sword = (EquipmentCard)card;
			else if (card.getID() == Card.E_GENTLEMENS_CLUB)
				club = (EquipmentCard)card;
			else if (card.getID() == Card.E_RAPIER_OF_UNFAIRNESS)
				rapier = (EquipmentCard)card;
			else if (card.getID() == Card.E_HORNED_HELMET)
				helm = (EquipmentCard)card;
			else if (card.getID() == Card.I_YUPPIE_WATER)
				water = (ItemCard)card;
			else if (card.getID() == Card.I_LOADED_DIE)
				loaded = (ItemCard)card;
			else if (card.getID() == Card.I_POTION_OF_HALITOSIS)
				halitosis = (ItemCard)card;
		}
						
		GM.newTestGame(mockGUI, players);

		easy.getEquippedItems().add(sword);
		medium.getEquippedItems().add(club);
		hard.getEquippedItems().add(sword);
		(new TradeItemsDialog(mockGUI, medium)).setVisible(true);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		assertTrue(easy.hasEquipped(sword));
		assertTrue(medium.hasEquipped(club));
		assertTrue(hard.hasEquipped(sword));
		
		easy.getEquippedItems().clear();
		easy.addUnequippedItem(club);
		medium.getEquippedItems().clear();
		medium.addUnequippedItem(sword);
		hard.getEquippedItems().clear();
		hard.addUnequippedItem(club);
		(new TradeItemsDialog(mockGUI, medium)).setVisible(true);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		assertTrue(easy.getEquippedItems().contains(sword));
		assertTrue(medium.getEquippedItems().contains(club));
		assertTrue(hard.getUnequippedItems().contains(club));
		
		medium.getEquippedItems().clear();
		medium.addUnequippedItem(sword);
		(new TradeItemsDialog(mockGUI, medium)).setVisible(true);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		assertTrue(easy.getEquippedItems().contains(sword));
		assertTrue(medium.getEquippedItems().contains(club));
		assertTrue(hard.getEquippedItems().contains(sword));
		
		medium.getEquippedItems().clear();
		medium.addUnequippedItem(sword);
		hard.getEquippedItems().clear();
		hard.addUnequippedItem(club);
		medium.goUpLevels(3, false);
		(new TradeItemsDialog(mockGUI, medium)).setVisible(true);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		assertTrue(easy.getEquippedItems().contains(sword));
		assertTrue(medium.getUnequippedItems().contains(sword));
		assertTrue(hard.getUnequippedItems().contains(club));
		
		easy.getEquippedItems().clear();
		easy.addUnequippedItem(club);
		(new TradeItemsDialog(mockGUI, medium)).setVisible(true);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		assertTrue(easy.getEquippedItems().contains(sword));
		assertTrue(medium.getEquippedItems().contains(club));
		assertTrue(hard.getUnequippedItems().contains(club));
		
		medium.goDownLevels(3);
		easy.goUpLevels(3, false);
		medium.getEquippedItems().clear();
		medium.addUnequippedItem(sword);
		easy.getEquippedItems().clear();
		easy.addUnequippedItem(club);
		(new TradeItemsDialog(mockGUI, easy)).setVisible(true);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		assertTrue(easy.getEquippedItems().contains(sword));
		assertTrue(medium.getEquippedItems().contains(club));
		assertTrue(hard.getUnequippedItems().contains(club));
		
		easy.goDownLevels(3);
		medium.goUpLevels(2, false);
		hard.goUpLevels(3, false);
		easy.getEquippedItems().clear();
		medium.getEquippedItems().clear();
		hard.getUnequippedItems().clear();
		easy.addRaceCard(new RaceCard(1, "Elf", Race.ELF));
		easy.addItem(halitosis);
		easy.addItem(loaded);
		hard.addLastingCurse(new CurseCard(Card.CU_CHICKEN_ON_YOUR_HEAD, "Chicken on your Head.") {
			public void addEffects(Player player) {}
		});
		hard.addClassCard(new ClassCard(2, "Thief", Class.THIEF));
		hard.addCard(nose);
		hard.addItem(water);
		hard.addUnequippedItem(rapier);
		hard.addUnequippedItem(helm);
		(new TradeItemsDialog(mockGUI, hard)).setVisible(true);
		(new TradeItemsDialog(mockGUI, hard)).setVisible(true);
		assertEquals(easy.getAllItems().size(), 3);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(hard.getAllItems().size(), 2);
		assertTrue(easy.getCarriedItems().contains(water));
		assertTrue(easy.hasEquipped(rapier));
		assertTrue(easy.hasEquipped(helm));
		assertTrue(hard.getCarriedItems().contains(halitosis));
		assertTrue(hard.getCarriedItems().contains(loaded));
		hard.removeChickenOnHeadCurse();
	}
}
