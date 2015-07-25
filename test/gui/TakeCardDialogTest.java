
package gui;

import gui.TakeCardDialog.DrawLocation;

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
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.EquipmentCard.EquipmentType;

public class TakeCardDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testTakeCardDialog();
		
		return errorCount;
	}
	
	private void testTakeCardDialog() {
		GUI mockGUI = new MockGUI(0);
		Player easy = new Player(mockGUI, "easy", false, PlayerType.COMPUTER_EASY);
		Player medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		Player hard = new Player(mockGUI, "hard", false, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		
		ItemCard item0 = new ItemCard(0, "0", 0);
		ItemCard item1 = new ItemCard(1, "1", 100);
		ItemCard item2 = new ItemCard(2, "2", 200);
		ItemCard item3 = new ItemCard(3, "3", 300);
		ItemCard item4 = new ItemCard(4, "4", 400);
		ItemCard item5 = new ItemCard(5, "5", 500);
		ItemCard item10 = new ItemCard(10, "10", 1000);
		EquipmentCard equip1 = new EquipmentCard(11, "e1", 100, 1, EquipmentType.OTHER, false, true, false);
		EquipmentCard equip5 = new EquipmentCard(15, "e5", 500, 5, EquipmentType.OTHER, false, true, false);
		OtherDoorCard divine = new OtherDoorCard(Card.OD_DIVINE_INTERVENTION, "Divine Intervention");
		OtherTreasureCard hireling = new OtherTreasureCard(Card.OT_HIRELING, "Hireling");
		
		GM.newTestGame(mockGUI, players);
		
		easy.addCard(item1);
		easy.addCard(item2);
		easy.addCard(item3);
		easy.addCard(item4);
		easy.addCard(item5);
		easy.addItem(item10);
		easy.getEquippedItems().add(equip5);
		TakeCardDialog dialog = new TakeCardDialog(easy, medium, DrawLocation.HAND, false, false);
		dialog.setVisible(true);
		assertEquals(easy.getHandCards().size(), 4);
		assertEquals(easy.getAllItems().size(), 2);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 0);
		
		dialog = new TakeCardDialog(easy, medium, DrawLocation.HAND, false, true);
		dialog.setVisible(true);
		assertEquals(easy.getHandCards().size(), 3);
		assertEquals(easy.getAllItems().size(), 2);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 0);
		
		easy.getHandCards().clear();
		easy.addCard(item1);
		easy.addCard(item2);
		easy.addCard(item3);
		easy.addCard(item4);
		easy.addCard(item5);
		dialog = new TakeCardDialog(easy, hard, DrawLocation.HAND, true, false);
		dialog.setVisible(true);
		assertEquals(easy.getHandCards().size(), 4);
		assertEquals(easy.getAllItems().size(), 2);
		assertEquals(hard.getHandCards().size(), 1);
		assertEquals(hard.getAllItems().size(), 0);
		assertFalse(easy.getHandCards().contains(item5));
		assertTrue(hard.getHandCards().contains(item5));
		
		dialog = new TakeCardDialog(easy, hard, DrawLocation.HAND, true, true);
		dialog.setVisible(true);
		assertEquals(easy.getHandCards().size(), 3);
		assertEquals(easy.getAllItems().size(), 2);
		assertEquals(hard.getHandCards().size(), 1);
		assertEquals(hard.getAllItems().size(), 0);
		assertFalse(easy.getHandCards().contains(item4));
		assertFalse(hard.getHandCards().contains(item4));
		
		easy.getHandCards().clear();
		easy.getCarriedItems().clear();
		easy.getEquippedItems().clear();
		hard.addUnequippedItem(equip5);
		hard.getEquippedItems().add(equip1);
		hard.addCard(item10);
		hard.addItem(item1);
		hard.addItem(item2);
		hard.addItem(item3);
		hard.addItem(item4);
		assertEquals(hard.getHandCards().size(), 2);
		dialog = new TakeCardDialog(hard, easy, DrawLocation.CARRIED, true, false);
		dialog.setVisible(true);
		assertEquals(hard.getHandCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 5);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 1);
		assertFalse(hard.getAllItems().contains(equip5));
		assertTrue(easy.getEquippedItems().contains(equip5));
		
		dialog = new TakeCardDialog(hard, easy, DrawLocation.CARRIED, true, true);
		dialog.setVisible(true);
		assertEquals(hard.getHandCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 4);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 1);
		assertFalse(hard.getAllItems().contains(item5));
		assertFalse(easy.getAllItems().contains(item5));
		
		medium.getHandCards().clear();
		medium.getCarriedItems().clear();
		medium.getEquippedItems().clear();
		medium.goUpLevels(10, false);
		medium.addClassCard(new ClassCard(20, "Cleric", Class.CLERIC));
		hard.getEquippedItems().add(equip5);
		hard.addCard(divine);
		dialog = new TakeCardDialog(hard, medium, DrawLocation.TREASURE, true, false);
		dialog.setVisible(true);
		assertEquals(hard.getHandCards().size(), 3);
		assertEquals(hard.getAllItems().size(), 4);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 0);
		assertFalse(hard.getAllItems().contains(equip5));
		assertTrue(medium.getHandCards().contains(equip5));
		
		dialog = new TakeCardDialog(hard, medium, DrawLocation.TREASURE, true, true);
		dialog.setVisible(true);
		assertEquals(hard.getHandCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 4);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 0);
		assertFalse(hard.getHandCards().contains(item10));
		assertFalse(medium.getHandCards().contains(item10));
		
		medium.getHandCards().clear();
		medium.getCarriedItems().clear();
		medium.getEquippedItems().clear();
		hard.getEquippedItems().add(equip5);
		hard.addCard(item10);
		dialog = new TakeCardDialog(hard, medium, DrawLocation.EITHER, true, false);
		dialog.setVisible(true);
		assertEquals(hard.getHandCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 5);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 0);
		assertFalse(hard.getHandCards().contains(divine));
		assertTrue(medium.getHandCards().contains(divine));
		
		dialog = new TakeCardDialog(hard, medium, DrawLocation.EITHER, true, true);
		dialog.setVisible(true);
		assertEquals(hard.getHandCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 4);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 0);
		assertFalse(hard.getAllItems().contains(equip5));
		assertFalse(medium.getHandCards().contains(equip5));
		
		easy.getHandCards().clear();
		easy.getCarriedItems().clear();
		easy.getEquippedItems().clear();
		medium.getHandCards().clear();
		medium.getCarriedItems().clear();
		medium.getEquippedItems().clear();
		hard.getHandCards().clear();
		hard.getCarriedItems().clear();
		hard.getEquippedItems().clear();
		medium.addItem(item0);
		medium.addCard(item0);
		medium.setHirelingCard(hireling);
		dialog = new TakeCardDialog(medium, easy, DrawLocation.TREASURE, true, false);
		dialog.setVisible(true);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 1);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 0);
		assertFalse(medium.hasHireling());
		assertTrue(easy.hasHireling());
		
		medium.setHirelingCard(hireling);
		dialog = new TakeCardDialog(medium, hard, DrawLocation.EITHER, true, false);
		dialog.setVisible(true);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 1);
		assertEquals(hard.getHandCards().size(), 1);
		assertEquals(hard.getAllItems().size(), 0);
		assertFalse(medium.hasHireling());
		assertTrue(hard.getHandCards().contains(hireling));
		
		hard.getHandCards().clear();
		medium.setHirelingCard(hireling);
		medium.addItem(item2);
		dialog = new TakeCardDialog(medium, hard, DrawLocation.EITHER, true, true);
		dialog.setVisible(true);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(medium.getAllItems().size(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertTrue(medium.hasHireling());
		assertFalse(hard.hasHireling());
		assertFalse(medium.getAllItems().contains(item2));
		
		hard.getHandCards().clear();
		medium.getHandCards().clear();
		medium.getCarriedItems().clear();
		dialog = new TakeCardDialog(medium, hard, DrawLocation.EITHER, true, true);
		dialog.setVisible(true);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		assertFalse(medium.hasHireling());
		assertFalse(hard.hasHireling());
	}
}
