
package gui;

import java.util.LinkedList;

import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.Card;
import model.card.ClassCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.EquipmentCard.EquipmentType;

public class StealItemDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testStealItemDialog();
		
		return errorCount;
	}
	
	private void testStealItemDialog() {
		GUI mockGUI = new MockGUI(0);
		Player easy = new Player(mockGUI, "easy", false, PlayerType.COMPUTER_EASY);
		Player medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		Player hard = new Player(mockGUI, "hard", false, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		
		ItemCard loaded = new ItemCard(Card.I_LOADED_DIE, "Loaded Die", 300);
		ItemCard noValueItem = new ItemCard(0, "no value", 0);
		EquipmentCard equip2 = new EquipmentCard(2, "2", 100, 2, EquipmentType.OTHER, false, false, false);
		EquipmentCard equip3 = new EquipmentCard(3, "3", 100, 3, EquipmentType.OTHER, false, false, false);
		EquipmentCard equip4 = new EquipmentCard(4, "4", 100, 4, EquipmentType.OTHER, false, false, false);
		
		GM.newTestGame(mockGUI, players);
		easy.goUpLevel(false);
		hard.goUpLevels(3, false);
		
		medium.addClassCard(new ClassCard(6, "Thief", Class.THIEF));
		assertTrue(medium.isThief());
		medium.addItem(loaded);
		
		easy.getEquippedItems().add(equip3);
		hard.getEquippedItems().add(equip3);
		StealItemDialog dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		assertTrue(dialog.getItemToSteal() == null);
		assertTrue(dialog.getVictim() == null);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 0);
		assertEquals(hard.getAllEquipment().size(), 1);
		
		medium.addCard(noValueItem);
		dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		if (medium.getCarriedItems().isEmpty())
			medium.addItem(loaded);
		assertTrue(medium.getHandCards().isEmpty());
		assertTrue(dialog.getItemToSteal() == equip3);
		assertTrue(dialog.getVictim() == hard);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 0);
		
		medium.addCard(noValueItem);
		medium.getEquippedItems().clear();
		easy.getEquippedItems().add(equip4);
		hard.getEquippedItems().add(equip3);
		dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		assertTrue(medium.getHandCards().isEmpty());
		if (medium.getCarriedItems().isEmpty())
			medium.addItem(loaded);
		assertTrue(dialog.getItemToSteal() == equip4);
		assertTrue(dialog.getVictim() == easy);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
				
		medium.addCard(noValueItem);
		medium.getEquippedItems().clear();
		easy.getUnequippedItems().add(equip4);
		medium.goUpLevels(2, false);
		dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		assertTrue(medium.getHandCards().isEmpty());
		if (medium.getCarriedItems().isEmpty())
			medium.addItem(loaded);
		assertTrue(dialog.getItemToSteal() == equip4);
		assertTrue(dialog.getVictim() == easy);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 1);
		
		medium.addCard(equip4);
		medium.getEquippedItems().clear();
		easy.getEquippedItems().add(equip4);
		medium.goUpLevels(2, false);
		dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		assertFalse(medium.getHandCards().isEmpty());
		assertTrue(dialog.getItemToSteal() == null);
		assertTrue(dialog.getVictim() == null);
		assertEquals(easy.getAllEquipment().size(), 2);
		assertEquals(medium.getAllEquipment().size(), 0);
		assertEquals(hard.getAllEquipment().size(), 1);
				
		medium.getHandCards().clear();
		medium.addCard(noValueItem);
		medium.getEquippedItems().clear();
		easy.getEquippedItems().clear();
		easy.getEquippedItems().add(equip2);
		hard.getEquippedItems().clear();
		hard.getEquippedItems().add(equip2);
		dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		assertTrue(medium.getHandCards().isEmpty());
		assertTrue(dialog.getItemToSteal() == equip2);
		assertTrue(dialog.getVictim() == hard);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 1);
		assertEquals(hard.getAllEquipment().size(), 0);
		
		medium.addCard(noValueItem);
		medium.getCarriedItems().clear();
		medium.getEquippedItems().clear();
		hard.getEquippedItems().add(equip2);
		dialog = new StealItemDialog(medium);
		dialog.setVisible(true);
		assertFalse(medium.getHandCards().isEmpty());
		assertTrue(dialog.getItemToSteal() == null);
		assertTrue(dialog.getVictim() == null);
		assertEquals(easy.getAllEquipment().size(), 1);
		assertEquals(medium.getAllEquipment().size(), 0);
		assertEquals(hard.getAllEquipment().size(), 1);
	}
}
