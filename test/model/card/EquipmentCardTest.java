
package model.card;

import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.EquipmentCard;
import gui.MockGUI;

public class EquipmentCardTest extends UnitTest {

	private final EquipmentCard.EquipmentType HAND = EquipmentCard.EquipmentType.ONE_HAND;
	private final EquipmentCard.EquipmentType HANDS = EquipmentCard.EquipmentType.TWO_HANDS;
	private final EquipmentCard.EquipmentType HAT = EquipmentCard.EquipmentType.HEADGEAR;
	private final EquipmentCard.EquipmentType ARMOR = EquipmentCard.EquipmentType.ARMOR;
	private final EquipmentCard.EquipmentType SHOES = EquipmentCard.EquipmentType.FOOTGEAR;
	private final EquipmentCard.EquipmentType OTHER = EquipmentCard.EquipmentType.OTHER;
	
	public int testAll() {
		testEquipmentType();
		testConstructor();
		
		return errorCount;
	}

	private void testEquipmentType() {
		assertEquals("One-handed Item", HAND.toString());
		assertEquals("Two-handed Item", HANDS.toString());
		assertEquals("Headgear", HAT.toString());
		assertEquals("Armor", ARMOR.toString());
		assertEquals("Footgear", SHOES.toString());
		assertEquals("Other Item", OTHER.toString());
	}
	
	private void testConstructor() {
		Player player = new Player(new MockGUI(0), "", true, PlayerType.TEST);
		
		EquipmentCard item = new EquipmentCard(1, "hand", 0, 0, HAND, false, false, false);
		assertEquals(item.getID(), 1);
		assertEquals(item.getName(), "hand");
		assertEquals(item.getValue(), 0);
		assertEquals(item.getBonus(player), 0);
		assertTrue(item.getEquipmentType() == HAND);
		assertFalse(item.isBig());
		assertFalse(item.isBelowWaist());
		assertFalse(item.isWeapon());
		assertEquals(item.equip(player), "");
		
		item = new EquipmentCard(2, "hands", 100, 2, HANDS, true, false, false);
		assertEquals(item.getID(), 2);
		assertEquals(item.getName(), "hands");
		assertEquals(item.getValue(), 100);
		assertEquals(item.getBonus(player), 2);
		assertTrue(item.getEquipmentType() == HANDS);
		assertTrue(item.isBig());
		assertFalse(item.isBelowWaist());
		assertFalse(item.isWeapon());
		assertEquals(item.equip(player), "");
		
		item = new EquipmentCard(3, "hat", 1000, 10, HAT, false, true, true);
		assertEquals(item.getID(), 3);
		assertEquals(item.getName(), "hat");
		assertEquals(item.getValue(), 1000);
		assertEquals(item.getBonus(player), 10);
		assertTrue(item.getEquipmentType() == HAT);
		assertFalse(item.isBig());
		assertTrue(item.isBelowWaist());
		assertTrue(item.isWeapon());
		assertEquals(item.equip(player), "");
		
		item = new EquipmentCard(4, "armor", 5, 99, ARMOR, false, true, false);
		assertEquals(item.getID(), 4);
		assertEquals(item.getName(), "armor");
		assertEquals(item.getValue(), 5);
		assertEquals(item.getBonus(player), 99);
		assertTrue(item.getEquipmentType() == ARMOR);
		assertFalse(item.isBig());
		assertTrue(item.isBelowWaist());
		assertFalse(item.isWeapon());
		assertEquals(item.equip(player), "");
		
		item = new EquipmentCard(5, "shoes", 999, 2, SHOES, false, false, true);
		assertEquals(item.getID(), 5);
		assertEquals(item.getName(), "shoes");
		assertEquals(item.getValue(), 999);
		assertEquals(item.getBonus(player), 2);
		assertTrue(item.getEquipmentType() == SHOES);
		assertFalse(item.isBig());
		assertFalse(item.isBelowWaist());
		assertTrue(item.isWeapon());
		assertEquals(item.equip(player), "");
		
		item = new EquipmentCard(6, "other", 400, 0, OTHER, true, true, true);
		assertEquals(item.getID(), 6);
		assertEquals(item.getName(), "other");
		assertEquals(item.getValue(), 400);
		assertEquals(item.getBonus(player), 0);
		assertTrue(item.getEquipmentType() == OTHER);
		assertTrue(item.isBig());
		assertTrue(item.isBelowWaist());
		assertTrue(item.isWeapon());
		assertEquals(item.equip(player), "");
	}
}
