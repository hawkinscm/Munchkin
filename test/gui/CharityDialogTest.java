
package gui;

import java.util.LinkedList;

import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.UnitTest;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class CharityDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private Player easy;
	private Player medium;
	private Player hard;
	private Player human;
	
	public int testAll() {
		initializeObjects();

		testCharityDialog();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		GUI mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		human = new Player(mockGUI, "human", false, PlayerType.HUMAN);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		players.add(human);
		GM.newTestGame(mockGUI, players);
	}
	
	private void testCharityDialog() {
		ItemCard item1 = new ItemCard(1, "item1", 100);
		ItemCard item2 = new ItemCard(2, "item2", 200);
		ItemCard item3 = new ItemCard(3, "item3", 200);
		ItemCard item4 = new ItemCard(4, "item4", 300);
		ItemCard item5 = new ItemCard(5, "item5", 400);
		ItemCard item6 = new ItemCard(6, "item6", 500);
		ItemCard item7 = new ItemCard(7, "item7", 600);
		ItemCard item8 = new ItemCard(8, "item8", 700);
		
		GM.setActivePlayer(easy);
		easy.addCard(item3);
		easy.addCard(item2);
		easy.addCard(item8);
		easy.addCard(item6);
		easy.addCard(item1);
		assertEquals(easy.getHandCards().size(), 5);
		CharityDialog dialog = new CharityDialog(easy);
		assertTrue(dialog.getCharityCards().isEmpty());
		assertEquals(easy.getHandCards().size(), 5);
		
		easy.addCard(item7);
		easy.addCard(item4);
		easy.addCard(item5);
		assertEquals(easy.getHandCards().size(), 8);
		dialog = new CharityDialog(easy);
		assertEquals(dialog.getCharityCards().size(), 3);
		assertTrue(dialog.getCharityCards().contains(item1));
		assertTrue(dialog.getCharityCards().contains(item2));
		assertTrue(dialog.getCharityCards().contains(item3));
		assertEquals(easy.getHandCards().size(), 5);
		assertTrue(easy.getHandCards().contains(item8));
		assertTrue(easy.getHandCards().contains(item7));
		assertTrue(easy.getHandCards().contains(item6));
		assertTrue(easy.getHandCards().contains(item5));
		assertTrue(easy.getHandCards().contains(item4));
		assertFalse(easy.getHandCards().contains(item3));
		assertFalse(easy.getHandCards().contains(item2));
		assertFalse(easy.getHandCards().contains(item1));
		
		easy.addCard(item2);
		easy.addCard(item1);
		easy.addCard(item3);
		easy.addRaceCard(new RaceCard(9, "Dwarf", Race.DWARF));
		assertEquals(easy.getHandCards().size(), 8);
		dialog = new CharityDialog(easy);
		assertEquals(dialog.getCharityCards().size(), 2);
		assertTrue(dialog.getCharityCards().contains(item1));
		assertTrue(dialog.getCharityCards().contains(item2));
		assertEquals(easy.getHandCards().size(), 6);
		assertTrue(easy.getHandCards().contains(item8));
		assertTrue(easy.getHandCards().contains(item7));
		assertTrue(easy.getHandCards().contains(item6));
		assertTrue(easy.getHandCards().contains(item5));
		assertTrue(easy.getHandCards().contains(item4));
		assertTrue(easy.getHandCards().contains(item3));
		assertFalse(easy.getHandCards().contains(item2));
		assertFalse(easy.getHandCards().contains(item1));
		
		GM.setActivePlayer(medium);
		medium.addCard(new RaceCard(10, "Halfling", Race.HALFLING));
		medium.addCard(new ClassCard(11, "Wizard", Class.WIZARD));
		medium.addCard(new ClassCard(12, "Wizard", Class.WIZARD));
		medium.addCard(new ClassCard(13, "Wizard", Class.WIZARD));
		medium.addCard(new ClassCard(14, "Wizard", Class.WIZARD));
		medium.addCard(new ClassCard(15, "Wizard", Class.WIZARD));
		medium.addCard(new ClassCard(16, "Wizard", Class.WIZARD));
		assertEquals(medium.getHandCards().size(), 7);
		dialog = new CharityDialog(medium);
		assertEquals(dialog.getCharityCards().size(), 2);
		assertEquals(medium.getHandCards().size(), 5);
		
		GM.setActivePlayer(hard);
		CurseCard loseHelmet = new CurseCard(Card.CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING, "Lose the Headgear You Are Wearing") {
			public void addEffects(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR) {
						player.discard(item);
						DisplayCardsDialog dialog = new DisplayCardsDialog(item, "Discarded");
						dialog.setVisible(true);
					}
				
				player.removeChickenOnHeadCurse();
			}
		};
		GoUpLevelCard levelUp = new GoUpLevelCard(17, "Go Up");
		hard.addCard(levelUp);
		hard.addCard(levelUp);
		hard.addCard(levelUp);
		hard.addCard(levelUp);
		hard.addCard(levelUp);
		assertEquals(hard.getHandCards().size(), 5);
		dialog = new CharityDialog(hard);
		assertEquals(dialog.getCharityCards().size(), 0);
		
		hard.addCard(levelUp);
		hard.addCard(loseHelmet);
		hard.addCard(new EquipmentCard(18, "equip1", 100, 1, EquipmentType.OTHER, true, false, false));
		EquipmentCard noEquip = new EquipmentCard(19, "no equip", 300, 1, EquipmentType.OTHER, true, false, false) {
			@Override
			public String equip(Player player) {
				return "NO";
			}
		};
		hard.addCard(noEquip);
		assertEquals(hard.getHandCards().size(), 9);
		human.goUpLevels(9, false);
		dialog = new CharityDialog(hard);
		human.goDownLevels(9);
		assertEquals(dialog.getCharityCards().size(), 1);
		assertFalse(dialog.getCharityCards().contains(loseHelmet));
		assertTrue(dialog.getCharityCards().contains(noEquip));
		assertEquals(hard.getHandCards().size(), 5);
		assertEquals(hard.getEquippedItems().size(), 1);
		assertEquals(hard.getLevel(), 2);
		
		hard.addCard(loseHelmet);
		medium.getEquippedItems().add(new EquipmentCard(20, "helmet", 100, 1, EquipmentType.HEADGEAR, false, false, false));
		assertEquals(medium.getEquippedItems().size(), 1);
		assertEquals(hard.getHandCards().size(), 6);
		dialog = new CharityDialog(hard);
		assertEquals(dialog.getCharityCards().size(), 0);
		assertEquals(hard.getHandCards().size(), 5);
		assertEquals(medium.getEquippedItems().size(), 0);
	}
}
