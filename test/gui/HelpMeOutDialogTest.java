
package gui;

import java.util.LinkedList;


import model.Battle;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.UnitTest;
import model.card.EquipmentCard;
import model.card.MonsterCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class HelpMeOutDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
	
	private Player easy;
	private Player medium;
	private Player hard;
	private Player human;
		
	public int testAll() {
		initializeObjects();

		testHelpMeOutDialog();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		GUI gui = new MockGUI(0);
				
		easy = new Player(gui, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(gui, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(gui, "hard", true, PlayerType.COMPUTER_HARD);
		human = new Player(gui, "human", false, PlayerType.HUMAN);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		players.add(human);		
		GM.newTestGame(gui, players);
		GM.moveToBattlePhase();
	}
	
	private void testHelpMeOutDialog() {
		Battle battle = new Battle(easy, new MonsterCard(6, "monster", 3, 1, 1, false) {
			public void doBadStuff(Player player) {}
		});		
		EquipmentCard item1 = new EquipmentCard(1, "item1", 100, 1, EquipmentType.ARMOR, false, false, false);
		EquipmentCard item2 = new EquipmentCard(2, "item2", 200, 2, EquipmentType.OTHER, false, false, false);
		EquipmentCard item3 = new EquipmentCard(3, "item3", 300, 3, EquipmentType.OTHER, false, false, false);
		EquipmentCard item4 = new EquipmentCard(4, "item4", 400, 4, EquipmentType.OTHER, true, false, false);
		EquipmentCard item5 = new EquipmentCard(5, "item5", 500, 5, EquipmentType.ARMOR, true, false, false);
		
		medium.getEquippedItems().add(item1);
		medium.addCard(item5);
		hard.getEquippedItems().add(item2);
		HelpMeOutDialog dialog = new HelpMeOutDialog(battle, easy);
		dialog.setVisible(true);
		assertFalse(dialog.tookItem());
		assertEquals(easy.getEquippedItems().size(), 0);
		
		human.getEquippedItems().add(item3);
		medium.getUnequippedItems().add(item5);
		hard.getEquippedItems().add(item4);
		dialog = new HelpMeOutDialog(battle, easy);
		dialog.setVisible(true);
		assertTrue(dialog.tookItem());
		assertEquals(easy.getEquippedItems().size(), 1);
		assertTrue(easy.getEquippedItems().contains(item5));
		assertFalse(medium.getAllItems().contains(item5));
		easy.unequip(item5);
		
		assertEquals(easy.getEquippedItems().size(), 0);
		dialog = new HelpMeOutDialog(battle, easy);
		dialog.setVisible(true);
		assertTrue(dialog.tookItem());
		assertEquals(easy.getEquippedItems().size(), 1);
		assertTrue(easy.getEquippedItems().contains(item3));
		assertFalse(human.getAllItems().contains(item3));
		easy.unequip(item3);
		
		easy.getEquippedItems().add(item1);
		easy.addRaceCard(new RaceCard(7, "Dwarf", Race.DWARF));
		human.getEquippedItems().add(item5);
		easy.getUnequippedItems().remove(item5);
		assertEquals(easy.getUnequippedItems().size(), 1);
		assertEquals(easy.getEquippedItems().size(), 1);
		dialog = new HelpMeOutDialog(battle, easy);
		dialog.setVisible(true);
		assertTrue(dialog.tookItem());
		assertEquals(easy.getEquippedItems().size(), 2);
		assertTrue(easy.getEquippedItems().contains(item1));
		assertTrue(easy.getEquippedItems().contains(item4));
		assertFalse(hard.getAllItems().contains(item4));
		easy.unequip(item4);
		
		assertEquals(easy.getUnequippedItems().size(), 2);
		assertEquals(easy.getEquippedItems().size(), 1);
		dialog = new HelpMeOutDialog(battle, easy);
		dialog.setVisible(true);
		assertTrue(dialog.tookItem());
		assertEquals(easy.getUnequippedItems().size(), 3);
		assertEquals(easy.getEquippedItems().size(), 1);
		assertTrue(easy.getEquippedItems().contains(item5));
		assertFalse(human.getAllItems().contains(item5));
	}
}
