
package model;

import exceptions.EndGameException;
import gui.MockGUI;

import java.util.LinkedList;
import java.util.Stack;

import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class PlayerTest extends UnitTest {
	
	private final EquipmentCard.EquipmentType HAND = EquipmentCard.EquipmentType.ONE_HAND;
	private final EquipmentCard.EquipmentType HANDS = EquipmentCard.EquipmentType.TWO_HANDS;
	private final EquipmentCard.EquipmentType HAT = EquipmentCard.EquipmentType.HEADGEAR;
	private final EquipmentCard.EquipmentType ARMOR = EquipmentCard.EquipmentType.ARMOR;
	private final EquipmentCard.EquipmentType SHOES = EquipmentCard.EquipmentType.FOOTGEAR;
	private final EquipmentCard.EquipmentType OTHER = EquipmentCard.EquipmentType.OTHER;
		
	private Player male;
	private Player female;
	private Player testPlayer;
	
	private CurseCard chicken;
	private CurseCard change;
	private CurseCard malign;
	private OtherDoorCard cheat;
	private OtherTreasureCard hireling;
	private OtherTreasureCard hoard;
	
	public int testAll() {
		initialize();
		
		testLevel();
		testRace();
		testClass();
		testCurses();
		testHireling();
		testEquip();
		testEquipmentAndItems();
		testCards();
		testDrawCards();
		testResurrect();
		testDie();
		testClone();
		
		return errorCount;
	}
	
	private void initialize() {		
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck) {
			if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
				chicken = (CurseCard)card;
			else if (card.getID() == Card.CU_CHANGE_SEX)
				change = (CurseCard)card;
			else if (card.getID() == Card.CU_MALIGN_MIRROR)
				malign = (CurseCard)card;
			else if (card.getID() == Card.OD_CHEAT)
				cheat = (OtherDoorCard)card;
		}
		
		Stack<Card> treasureDeck = TreasureDeckFactory.buildDeck();
		for (Card card : treasureDeck) {
			if (card.getID() == Card.OT_HIRELING)
				hireling = (OtherTreasureCard)card;
			else if (card.getID() == Card.OT_HOARD)
				hoard = (OtherTreasureCard)card;
		}
		
		MockGUI mockGUI = new MockGUI(0);
		male = new Player(mockGUI, "man", true, PlayerType.TEST);
		female = new Player(mockGUI, "woman", false, PlayerType.COMPUTER_EASY);
		testPlayer = new Player(mockGUI, "test", true, PlayerType.TEST);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(male);
		players.add(female);
		players.add(testPlayer);
		GM.newTestGame(mockGUI, players);
		
		assertEquals(male.getName(), "man");
		assertEquals(male.toString(), "man");
		assertTrue(male.getPlayerType() == PlayerType.TEST);
		assertTrue(male.isComputer());
		assertEquals(female.getName(), "woman");
		assertEquals(female.toString(), "woman");
		assertTrue(female.getPlayerType() == PlayerType.COMPUTER_EASY);
		assertTrue(female.isComputer());
		assertEquals(testPlayer.getName(), "test");
		assertTrue(testPlayer.getPlayerType() == PlayerType.TEST);
		
		Player human = new Player(mockGUI, "human", true, PlayerType.HUMAN);
		Player com2 = new Player(mockGUI, "com2", true, PlayerType.COMPUTER_MEDIUM);
		Player com3 = new Player(mockGUI, "com3", false, PlayerType.COMPUTER_HARD);
		assertEquals(human.getName(), "human");
		assertTrue(human.getPlayerType() == PlayerType.HUMAN);
		assertFalse(human.isComputer());
		assertEquals(com2.getName(), "com2");
		assertTrue(com2.getPlayerType() == PlayerType.COMPUTER_MEDIUM);
		assertTrue(com2.isComputer());
		assertEquals(com3.toString(), "com3");
		assertTrue(com3.getPlayerType() == PlayerType.COMPUTER_HARD);
		assertTrue(com3.isComputer());
		
		
	}
	
	private void testLevel() {
		assertEquals(male.getLevel(), 1);
		assertEquals(female.getLevel(), 1);
		male.goDownLevel();
		assertEquals(male.getLevel(), 1);
		male.goDownLevels(100);
		assertEquals(male.getLevel(), 1);
		male.goUpLevel(false);
		assertEquals(male.getLevel(), 2);
		male.goUpLevels(5, true);
		assertEquals(male.getLevel(), 7);
		male.goUpLevels(2, false);
		assertEquals(male.getLevel(), 9);
		male.goUpLevel(false);
		assertEquals(male.getLevel(), 9);
		male.goUpLevels(3, false);
		assertEquals(male.getLevel(), 9);
		male.goUpLevel(true);
		assertEquals(male.getLevel(), 10);
		male.goUpLevels(2, true);
		assertEquals(male.getLevel(), 12);
		male.goUpLevel(false);
		assertEquals(male.getLevel(), 12);
		male.goUpLevels(4, false);
		assertEquals(male.getLevel(), 12);
		male.goDownLevels(12);
		assertEquals(male.getLevel(), 1);		
		assertEquals(female.getLevel(), 1);
	}
	
	private void testRace() {
		assertFalse(female.isHalfBreed());
		assertTrue(female.isHuman());
		assertFalse(female.isElf());
		assertFalse(female.isDwarf());
		assertFalse(female.isHalfling());
		
		RaceCard elf = new RaceCard(1, "Elf", Race.ELF);
		RaceCard dwarf = new RaceCard(2, "Dwarf", Race.DWARF);
		RaceCard halfling = new RaceCard(3, "Halfling", Race.HALFLING);
		OtherDoorCard half = new OtherDoorCard(4, "Half-Breed");
		
		female.addRaceCard(elf);
		assertFalse(female.isHalfBreed());
		assertFalse(female.isHuman());
		assertTrue(female.isElf());
		assertFalse(female.isDwarf());
		assertFalse(female.isHalfling());
		assertTrue(female.getRaceCards().contains(elf));
		
		female.discardRaceCard(elf);
		female.addRaceCard(dwarf);
		assertFalse(female.isHalfBreed());
		assertFalse(female.isHuman());
		assertFalse(female.isElf());
		assertTrue(female.isDwarf());
		assertFalse(female.isHalfling());
		
		EquipmentCard big1 = new EquipmentCard(5, "big1", 10, 0, EquipmentType.ARMOR, true, false, false);
		EquipmentCard big2 = new EquipmentCard(6, "big2", 20, 0, EquipmentType.HEADGEAR, true, true, false);
		EquipmentCard big3 = new EquipmentCard(7, "big3", 30, 0, EquipmentType.ONE_HAND, true, false, true);
		EquipmentCard big4 = new EquipmentCard(8, "big4", 40, 0, EquipmentType.OTHER, true, true, true);
		female.addUnequippedItem(big1);
		female.addUnequippedItem(big2);
		female.addUnequippedItem(big3);
		female.addUnequippedItem(big4);
		assertEquals(female.equip(big2), "");
		assertEquals(female.equip(big3), "");
		assertEquals(female.getAllEquipment().size(), 4);
		female.discard(dwarf);
		assertEquals(female.getAllEquipment().size(), 1);
		assertTrue(female.getUnequippedItems().remove(big4));
		female.addRaceCard(dwarf);
		female.addUnequippedItem(big1);
		female.addUnequippedItem(big2);
		female.addUnequippedItem(big3);
		assertEquals(female.equip(big1), "");
		assertEquals(female.equip(big3), "");
		female.getRaceCards().clear();		
		female.addRaceCard(halfling);
		assertEquals(female.getAllEquipment().size(), 1);
		assertTrue(female.getEquippedItems().remove(big3));
		
		assertFalse(female.isHalfBreed());
		assertFalse(female.isHuman());
		assertFalse(female.isElf());
		assertFalse(female.isDwarf());
		assertTrue(female.isHalfling());
		female.getEquippedItems().clear();
		female.getUnequippedItems().clear();
		
		female.discardRaceCard(halfling);
		assertFalse(female.isHalfBreed());
		assertTrue(female.isHuman());
		assertFalse(female.isElf());
		assertFalse(female.isDwarf());
		assertFalse(female.isHalfling());
		assertTrue(female.getRaceCards().isEmpty());
		
		female.addRaceCard(elf);
		female.setHalfBreedCard(half);
		assertTrue(female.isHalfBreed());
		assertTrue(female.isHuman());
		assertTrue(female.isElf());
		assertFalse(female.isDwarf());
		assertFalse(female.isHalfling());
		assertEquals(female.getRaceCards().size(), 1);
		assertTrue(female.getRaceCards().contains(elf));
		assertTrue(female.getHalfBreedCard() == half);
		
		female.addRaceCard(dwarf);
		assertTrue(female.isHalfBreed());
		assertFalse(female.isHuman());
		assertTrue(female.isElf());
		assertTrue(female.isDwarf());
		assertFalse(female.isHalfling());
		assertEquals(female.getRaceCards().size(), 2);
		assertTrue(female.getRaceCards().contains(elf));
		assertTrue(female.getRaceCards().contains(dwarf));
		
		female.discard(elf);
		assertTrue(female.isHalfBreed());
		assertTrue(female.isHuman());
		assertFalse(female.isElf());
		assertTrue(female.isDwarf());
		assertFalse(female.isHalfling());
		assertEquals(female.getRaceCards().size(), 1);
		assertTrue(female.getRaceCards().contains(dwarf));
		
		female.loseHalfBreed();
		assertFalse(female.isHalfBreed());
		assertFalse(female.isHuman());
		assertFalse(female.isElf());
		assertTrue(female.isDwarf());
		assertFalse(female.isHalfling());
		assertEquals(female.getRaceCards().size(), 1);
		assertTrue(female.getRaceCards().contains(dwarf));
		assertNull(female.getHalfBreedCard());
		
		female.setHalfBreedCard(half);
		assertTrue(female.isHalfBreed());
		assertTrue(female.isHuman());
		assertFalse(female.isElf());
		assertTrue(female.isDwarf());
		assertFalse(female.isHalfling());
		assertEquals(female.getRaceCards().size(), 1);
		assertTrue(female.getRaceCards().contains(dwarf));	
		assertTrue(female.getHalfBreedCard() == half);
		
		female.discard(dwarf);
		assertFalse(female.isHalfBreed());
		assertTrue(female.isHuman());
		assertFalse(female.isElf());
		assertFalse(female.isDwarf());
		assertFalse(female.isHalfling());
		assertTrue(female.getRaceCards().isEmpty());
		assertNull(female.getHalfBreedCard());
	}
	
	private void testClass() {
		assertFalse(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertFalse(female.isWizard());
		assertFalse(female.isCleric());
		assertFalse(female.isThief());
		
		ClassCard warrior = new ClassCard(1, "Warrior", Class.WARRIOR);
		ClassCard wizard = new ClassCard(2, "Wizard", Class.WIZARD);
		ClassCard cleric = new ClassCard(3, "Cleric", Class.CLERIC);
		ClassCard thief = new ClassCard(4, "Thief", Class.THIEF);
		OtherDoorCard munchkin = new OtherDoorCard(5, "Super Munchkin");
		
		female.addClassCard(warrior);
		assertFalse(female.isSuperMunchkin());
		assertTrue(female.isWarrior());
		assertFalse(female.isWizard());
		assertFalse(female.isCleric());
		assertFalse(female.isThief());
		assertTrue(female.getClassCards().contains(warrior));
		
		female.discardClassCard(warrior);
		female.addClassCard(wizard);
		assertFalse(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertTrue(female.isWizard());
		assertFalse(female.isCleric());
		assertFalse(female.isThief());
		
		female.discard(wizard);
		female.addClassCard(cleric);
		assertFalse(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertFalse(female.isWizard());
		assertTrue(female.isCleric());
		assertFalse(female.isThief());
		
		female.discard(cleric);
		female.addClassCard(thief);
		assertFalse(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertFalse(female.isWizard());
		assertFalse(female.isCleric());
		assertTrue(female.isThief());
		
		female.discardClassCard(thief);
		assertFalse(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertFalse(female.isWizard());
		assertFalse(female.isCleric());
		assertFalse(female.isThief());
		assertTrue(female.getClassCards().isEmpty());
		
		female.addClassCard(wizard);
		female.setSuperMunchkinCard(munchkin);
		female.addClassCard(thief);
		assertTrue(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertTrue(female.isWizard());
		assertFalse(female.isCleric());
		assertTrue(female.isThief());
		assertEquals(female.getClassCards().size(), 2);
		assertTrue(female.getClassCards().contains(wizard));
		assertTrue(female.getClassCards().contains(thief));
		assertTrue(female.getSuperMunchkinCard() == munchkin);
		
		female.discard(wizard);
		assertFalse(female.isSuperMunchkin());
		assertFalse(female.isWarrior());
		assertFalse(female.isWizard());
		assertFalse(female.isCleric());
		assertTrue(female.isThief());
		assertEquals(female.getClassCards().size(), 1);
		assertTrue(female.getClassCards().contains(thief));
		
		female.discard(thief);
		female.addClassCard(warrior);
		female.setSuperMunchkinCard(munchkin);
		female.addClassCard(cleric);
		assertTrue(female.isSuperMunchkin());
		assertTrue(female.isWarrior());
		assertFalse(female.isWizard());
		assertTrue(female.isCleric());
		assertFalse(female.isThief());
		assertEquals(female.getClassCards().size(), 2);
		assertTrue(female.getClassCards().contains(warrior));
		assertTrue(female.getClassCards().contains(cleric));
		assertTrue(female.getSuperMunchkinCard() == munchkin);
		
		female.loseSuperMunchkin();
		assertFalse(female.isSuperMunchkin());
		assertTrue(female.isWarrior());
		assertFalse(female.isWizard());
		assertTrue(female.isCleric());
		assertFalse(female.isThief());
		assertEquals(female.getClassCards().size(), 2);
		assertNull(female.getSuperMunchkinCard());
	}
	
	private void testCurses() {
		assertTrue(male.isMale());
		assertFalse(male.isFemale());
		assertFalse(male.hasChickenOnHead());
		assertFalse(male.hasChangeSexCurse());
		assertFalse(male.hasDistractionCurse());
		assertFalse(male.hasMalignMirror());
		assertNull(male.getChickenOnHeadCard());
		assertNull(male.getChangeSexCard());
		assertNull(male.getMalignMirrorCard());
		
		male.addLastingCurse(chicken);
		assertTrue(male.hasChickenOnHead());
		assertFalse(male.hasChangeSexCurse());
		assertFalse(male.hasDistractionCurse());
		assertFalse(male.hasMalignMirror());
		
		male.addLastingCurse(change);
		male.changeSex();
		assertTrue(male.hasChickenOnHead());
		assertTrue(male.hasChangeSexCurse());
		assertTrue(male.hasDistractionCurse());
		assertFalse(male.hasMalignMirror());
		assertFalse(male.isMale());
		assertTrue(male.isFemale());
		
		male.addLastingCurse(malign);
		assertTrue(male.hasChickenOnHead());
		assertTrue(male.hasChangeSexCurse());
		assertTrue(male.hasDistractionCurse());
		assertTrue(male.hasMalignMirror());
		assertTrue(male.getChickenOnHeadCard() == chicken);
		assertTrue(male.getChangeSexCard() == change);
		assertTrue(male.getMalignMirrorCard() == malign);
		
		male.removeLastingCurses();
		assertTrue(male.hasChickenOnHead());
		assertTrue(male.hasChangeSexCurse());
		assertFalse(male.hasDistractionCurse());
		assertFalse(male.hasMalignMirror());
		assertTrue(male.getChickenOnHeadCard() == chicken);
		assertNull(male.getChangeSexCard());
		assertNull(male.getMalignMirrorCard());
		
		male.removeChickenOnHeadCurse();
		assertFalse(male.hasChickenOnHead());
		assertTrue(male.hasChangeSexCurse());
		assertFalse(male.hasDistractionCurse());
		assertFalse(male.hasMalignMirror());
		assertNull(male.getChickenOnHeadCard());
		assertNull(male.getChangeSexCard());
		assertNull(male.getMalignMirrorCard());
		assertFalse(male.isMale());
		assertTrue(male.isFemale());
		
		male.changeSex();
		assertTrue(male.isMale());
		assertFalse(male.isFemale());
		assertFalse(male.hasChangeSexCurse());
		
		female.changeSex();
		assertTrue(female.isMale());
		assertFalse(female.isFemale());
		assertTrue(female.hasChangeSexCurse());
		
		female.changeSex();
		assertFalse(female.isMale());
		assertTrue(female.isFemale());
		assertFalse(female.hasChangeSexCurse());
	}
	
	private void testHireling() {
		assertFalse(testPlayer.hasHireling());
		assertFalse(testPlayer.discardHirelingCard());
		testPlayer.setHirelingCard(hireling);
		assertTrue(testPlayer.getHirelingCard() == hireling);
		assertTrue(testPlayer.hasHireling());
		testPlayer.setHirelingCard(null);
		assertNull(testPlayer.getHirelingCard());
		assertFalse(testPlayer.hasHireling());
		assertFalse(testPlayer.discardHirelingCard());
		testPlayer.setHirelingCard(hireling);
		assertTrue(testPlayer.hasHireling());
		assertTrue(testPlayer.getHirelingCard() == hireling);
		EquipmentCard big1 = new EquipmentCard(1, "big1", 100, 1, OTHER, true, false, false);
		EquipmentCard big2 = new EquipmentCard(2, "big2", 200, 2, OTHER, true, false, false);
		EquipmentCard big3 = new EquipmentCard(3, "big3", 300, 3, OTHER, true, false, false);
		testPlayer.addUnequippedItem(big1);
		assertEquals(testPlayer.equip(big1), "");
		assertTrue(testPlayer.discardHirelingCard());
		assertEquals(testPlayer.getAllEquipment().size(), 1);
		assertTrue(testPlayer.getEquippedItems().contains(big1));
		testPlayer.addRaceCard(new RaceCard(4, "Dwarf", Race.DWARF));
		testPlayer.addUnequippedItem(big2);
		testPlayer.setHirelingCard(hireling);
		assertTrue(testPlayer.discardHirelingCard());
		assertEquals(testPlayer.getAllEquipment().size(), 2);
		assertTrue(testPlayer.getEquippedItems().contains(big1));
		assertTrue(testPlayer.getUnequippedItems().contains(big2));
		testPlayer.getRaceCards().clear();
		testPlayer.addUnequippedItem(big3);		
		testPlayer.setHirelingCard(hireling);
		assertTrue(testPlayer.discardHirelingCard());
		assertEquals(testPlayer.getAllEquipment().size(), 1);
		assertTrue(testPlayer.getUnequippedItems().contains(big3));
		assertFalse(testPlayer.hasHireling());
		assertNull(testPlayer.getHirelingCard());
		assertFalse(testPlayer.discardHirelingCard());
		testPlayer.getUnequippedItems().clear();
	}
	
	private void testEquip() {
		Player player = new Player(new MockGUI(0), "", true, PlayerType.HUMAN);
		OtherDoorCard cheat = new OtherDoorCard(1, "Cheat!");
		
		EquipmentCard hands = new EquipmentCard(2, "hands", 0, 0, HANDS, false, false, false);
		player.addUnequippedItem(hands);
		assertFalse(player.hasEquipped(hands));
		assertFalse(player.hasEquipped(hands.getID()));
		assertEquals(player.equip(hands), "");
		assertTrue(player.hasEquipped(hands));
		assertTrue(player.hasEquipped(hands.getID()));
		
		EquipmentCard hand1 = new EquipmentCard(3, "hand1", 0, 0, HAND, false, false, false);
		player.addUnequippedItem(hand1);
		assertFalse(player.hasEquipped(hand1));
		assertEquals(player.equip(hand1), "Your hands are full.");
		assertFalse(player.hasEquipped(hand1));
		assertFalse(player.hasEquipped(3));
		player.unequip(hands);
		assertFalse(player.hasEquipped(hands));
		assertFalse(player.hasEquipped(1));
		assertFalse(player.hasEquipped(2));
		assertFalse(player.hasEquipped(4));
		assertFalse(player.hasEquipped(5));
		assertEquals(player.equip(hand1), "");
		assertTrue(player.hasEquipped(hand1));
		assertTrue(player.hasEquipped(3));
		
		EquipmentCard hand2 = new EquipmentCard(4, "hand2", 0, 0, HAND, true, false, false);
		player.addUnequippedItem(hand2);
		assertFalse(player.hasEquipped(hand2));
		assertEquals(player.equip(hand2), "");
		assertTrue(player.hasEquipped(hand2));
		
		EquipmentCard hand3 = new EquipmentCard(5, "hand3", 0, 0, HAND, true, false, false);
		player.addUnequippedItem(hand3);
		assertFalse(player.hasEquipped(hand3));
		assertEquals(player.equip(hand3), "Your hands are full.");
		assertFalse(player.hasEquipped(hand3));
		
		assertEquals(player.equip(hands), "You don't have two free hands.");
		assertFalse(player.hasEquipped(hands));
		
		player.unequip(hand2);
		assertEquals(player.equip(hands), "You don't have two free hands.");
		assertFalse(player.hasEquipped(hands));
		
		player.unequip(hand1);
		assertEquals(player.equip(hands), "");
		assertTrue(player.hasEquipped(hands));
		
		EquipmentCard hat1 = new EquipmentCard(6, "hat1", 0, 0, HAT, true, true, true);
		player.addUnequippedItem(hat1);
		assertFalse(player.hasEquipped(hat1));
		assertEquals(player.equip(hat1), "");
		assertTrue(player.hasEquipped(hat1));
		
		EquipmentCard hat2 = new EquipmentCard(7, "hat2", 0, 0, HAT, true, true, true);
		player.addUnequippedItem(hat2);
		assertFalse(player.hasEquipped(hat2));
		assertEquals(player.equip(hat2), "You are already wearing Headgear.");
		assertFalse(player.hasEquipped(hat2));
		
		EquipmentCard armor1 = new EquipmentCard(8, "armor1", 0, 0, ARMOR, true, false, true);
		player.addUnequippedItem(armor1);
		assertFalse(player.hasEquipped(armor1));
		assertEquals(player.equip(armor1), "");
		assertTrue(player.hasEquipped(armor1));
		
		EquipmentCard armor2 = new EquipmentCard(9, "armor2", 0, 0, ARMOR, true, true, false);
		player.addUnequippedItem(armor2);
		assertFalse(player.hasEquipped(armor2));
		assertEquals(player.equip(armor2), "You are already wearing Armor.");
		assertFalse(player.hasEquipped(armor2));
		
		EquipmentCard shoes1 = new EquipmentCard(10, "shoes1", 0, 0, SHOES, false, true, true);
		player.addUnequippedItem(shoes1);
		assertFalse(player.hasEquipped(shoes1));
		assertEquals(player.equip(shoes1), "");
		assertTrue(player.hasEquipped(shoes1));
		
		EquipmentCard shoes2 = new EquipmentCard(11, "shoes2", 0, 0, SHOES, true, true, true);
		player.addUnequippedItem(shoes2);
		assertFalse(player.hasEquipped(shoes2));
		assertEquals(player.equip(shoes2), "You are already wearing Footgear.");
		assertFalse(player.hasEquipped(shoes2));
				
		EquipmentCard other1 = new EquipmentCard(12, "other1", 0, 0, OTHER, false, false, false);
		player.addUnequippedItem(other1);
		assertFalse(player.hasEquipped(other1));
		assertEquals(player.equip(other1), "");
		assertTrue(player.hasEquipped(other1));
		
		EquipmentCard other2 = new EquipmentCard(13, "other2", 0, 0, OTHER, false, false, false);
		player.addUnequippedItem(other2);
		assertFalse(player.hasEquipped(other2));
		assertEquals(player.equip(other2), "");
		assertTrue(player.hasEquipped(other2));
		player.unequip(other2);
		assertFalse(player.hasEquipped(other2));
		player.setCheatCards(cheat, other2);
		assertEquals(player.equip(other2), "");
		assertTrue(player.hasEquipped(other2));
		
		player.setCheatCards(cheat, hand1);
		assertEquals(player.equip(hand1), "");
		assertTrue(player.hasEquipped(hand1));
		player.setCheatCards(cheat, hand2);
		assertEquals(player.equip(hand2), "");
		assertTrue(player.hasEquipped(hand2));
		player.setCheatCards(cheat, armor2);
		assertEquals(player.equip(armor2), "");
		assertTrue(player.hasEquipped(armor2));
		assertEquals(player.equip(hand3), "Your hands are full.");
		assertFalse(player.hasEquipped(hand3));
		
		assertEquals(player.getAllEquipment().size(), 12);
		assertEquals(player.getEquippedItems().size(), 9);
		assertEquals(player.getUnequippedItems().size(), 3);
		
		assertTrue(player.removeEquipmentItem(hands));
		assertEquals(player.getAllEquipment().size(), 11);
		assertEquals(player.getEquippedItems().size(), 8);
		assertEquals(player.getUnequippedItems().size(), 3);
		assertTrue(player.removeEquipmentItem(armor2));
		assertFalse(player.removeEquipmentItem(hands));
		assertEquals(player.getAllEquipment().size(), 10);
		assertEquals(player.getEquippedItems().size(), 7);
		assertEquals(player.getUnequippedItems().size(), 3);
		assertFalse(player.removeEquipmentItem(armor2));
		assertTrue(player.removeEquipmentItem(hand1));
		assertTrue(player.removeEquipmentItem(hand2));
		assertTrue(player.removeEquipmentItem(hand3));
		assertEquals(player.getAllEquipment().size(), 7);
		assertEquals(player.getEquippedItems().size(), 5);
		assertEquals(player.getUnequippedItems().size(), 2);
		assertTrue(player.removeEquipmentItem(armor1));
		assertTrue(player.removeEquipmentItem(hat1));
		assertTrue(player.removeEquipmentItem(hat2));
		assertTrue(player.removeEquipmentItem(shoes1));
		assertTrue(player.removeEquipmentItem(shoes2));
		assertEquals(player.getAllEquipment().size(), 2);
		assertEquals(player.getEquippedItems().size(), 2);
		assertEquals(player.getUnequippedItems().size(), 0);
		assertTrue(player.removeEquipmentItem(other1));
		assertTrue(player.removeEquipmentItem(other2));
		assertEquals(player.getAllEquipment().size(), 0);
		assertEquals(player.getEquippedItems().size(), 0);
		assertEquals(player.getUnequippedItems().size(), 0);
	}
	
	private void testEquipmentAndItems() {
		EquipmentCard eqmt1 = new EquipmentCard(1, "bonus1", 200, 1, EquipmentCard.EquipmentType.OTHER, true, false, true);
		EquipmentCard eqmt2 = new EquipmentCard(2, "bonus2", 300, 2, EquipmentCard.EquipmentType.OTHER, false, false, false);
		EquipmentCard eqmt3 = new EquipmentCard(3, "bonus3", 500, 3, EquipmentCard.EquipmentType.OTHER, true, true, true);
		
		assertEquals(male.getEquipmentBonus(), 0);
		
		male.getUnequippedItems().add(eqmt1);
		assertEquals(male.getEquipmentBonus(), 0);
		
		male.equip(eqmt1);
		assertTrue(male.hasEquipped(eqmt1));
		assertEquals(male.getEquipmentBonus(), 1);
		
		male.addUnequippedItem(eqmt2);
		male.equip(eqmt2);
		assertTrue(male.hasEquipped(eqmt2));
		assertEquals(male.getEquipmentBonus(), 3);
		
		male.addUnequippedItem(eqmt3);
		male.equip(eqmt3);
		assertTrue(male.hasEquipped(eqmt3));
		assertEquals(male.getEquipmentBonus(), 6);
				
		male.addLastingCurse(malign);
		assertEquals(male.getEquipmentBonus(), 2);
		
		assertEquals(male.getAllEquipment().size(), 3);
		assertEquals(male.getBigItems().size(), 2);
		assertTrue(male.getBigItems().contains(eqmt1));
		assertTrue(male.getBigItems().contains(eqmt3));
		
		male.unequip(eqmt1);
		male.unequip(eqmt3);
		assertFalse(male.hasEquipped(0));
		assertFalse(male.hasEquipped(4));
		assertFalse(male.hasEquipped(eqmt1.getID()));
		assertTrue(male.hasEquipped(eqmt2.getID()));
		assertFalse(male.hasEquipped(eqmt3.getID()));
		assertFalse(male.hasEquipped(1));
		assertTrue(male.hasEquipped(2));
		assertFalse(male.hasEquipped(3));
		
		ItemCard item1 = new ItemCard(4, "item1", 400);
		ItemCard item2 = new ItemCard(5, "item2", 800);
		ItemCard item3 = new ItemCard(6, "item3", 1100);
		
		assertEquals(male.getCarriedItems().size(), 0);
		male.getCarriedItems().add(item1);
		male.getCarriedItems().add(item2);
		male.getCarriedItems().add(item3);
		assertEquals(male.getCarriedItems().size(), 3);
		assertEquals(male.getAllEquipment().size(), 3);
		
		assertEquals(male.getAllItems().size(), 6);
		assertTrue(male.getAllItems().contains(eqmt1));
		assertTrue(male.getAllItems().contains(eqmt2));
		assertTrue(male.getAllItems().contains(eqmt3));
		assertTrue(male.getAllItems().contains(item1));
		assertTrue(male.getAllItems().contains(item2));
		assertTrue(male.getAllItems().contains(item3));
		
		male.setCheatCards(cheat, eqmt1);
		assertTrue(male.isCheatingItem(eqmt1));
		assertFalse(male.isCheatingItem(eqmt2));
		assertFalse(male.isCheatingItem(eqmt3));
		assertTrue(male.getCheatingItemCard() == eqmt1);
		
		male.setHirelingCard(hireling);
		male.removeCheat();
		assertFalse(male.isCheatingItem(eqmt1));
		assertFalse(male.isCheatingItem(eqmt2));
		assertFalse(male.isCheatingItem(eqmt3));
		assertNull(male.getCheatingItemCard());
		
		male.discard(eqmt3);
		male.setHirelingCard(null);
		
		testPlayer.addCard(eqmt1);
		testPlayer.addCard(eqmt2);
		testPlayer.addCard(eqmt3);
		testPlayer.addCard(item1);
		testPlayer.addCard(item2);
		testPlayer.addCard(item3);
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt2)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt3)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, item1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, item2)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, item3)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertTrue(testPlayer.getHandCards().contains(eqmt1));
		testPlayer.addItem(eqmt1);
		assertFalse(testPlayer.getHandCards().contains(eqmt1));
		assertTrue(testPlayer.getEquippedItems().contains(eqmt1));
		assertTrue(testPlayer.getHandCards().contains(eqmt2));
		testPlayer.addItem(eqmt2);
		assertFalse(testPlayer.getHandCards().contains(eqmt2));
		assertTrue(testPlayer.getEquippedItems().contains(eqmt2));
		assertTrue(testPlayer.getHandCards().contains(eqmt3));
		testPlayer.addItem(eqmt3);
		assertFalse(testPlayer.getHandCards().contains(eqmt3));
		assertFalse(testPlayer.getAllItems().contains(eqmt3));
		assertTrue(GM.getTreasureDeck().discardPile.peek() == eqmt3);
		assertTrue(testPlayer.getHandCards().contains(item1));
		testPlayer.addItem(item1);
		assertFalse(testPlayer.getHandCards().contains(item1));
		assertTrue(testPlayer.getCarriedItems().contains(item1));
		assertTrue(testPlayer.getHandCards().contains(item2));
		testPlayer.addItem(item2);
		assertFalse(testPlayer.getHandCards().contains(item2));
		assertTrue(testPlayer.getCarriedItems().contains(item2));
		assertTrue(testPlayer.getHandCards().contains(item3));
		testPlayer.addItem(item3);
		assertFalse(testPlayer.getHandCards().contains(item3));
		assertTrue(testPlayer.getCarriedItems().contains(item3));
		testPlayer.getEquippedItems().clear();
		testPlayer.getUnequippedItems().clear();
		testPlayer.getCarriedItems().clear();
		GM.setActivePlayer(testPlayer);
		testPlayer.addCard(eqmt1);
		testPlayer.addCard(eqmt2);
		testPlayer.addCard(eqmt3);
		testPlayer.addCard(item1);
		testPlayer.addCard(item2);
		testPlayer.addCard(item3);
		try { assertTrue(CardPlayManager.playCard(testPlayer, eqmt1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt3)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertTrue(CardPlayManager.playCard(testPlayer, item3)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertTrue(testPlayer.getEquippedItems().remove(eqmt1));
		assertTrue(testPlayer.getCarriedItems().remove(item3));
		assertTrue(testPlayer.getHandCards().contains(eqmt3));
		testPlayer.addCard(eqmt1);
		testPlayer.addCard(item3);
		GM.moveToBattlePhase();
		GM.setActivePlayer(null);
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt2)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt3)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, item1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, item2)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, item3)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 6);
		assertTrue(testPlayer.getHandCards().contains(eqmt1));
		assertEquals(testPlayer.getAllEquipment().size(), 0);
		testPlayer.addItem(eqmt1);
		assertFalse(testPlayer.getHandCards().contains(eqmt1));
		assertTrue(testPlayer.getEquippedItems().contains(eqmt1));
		assertTrue(testPlayer.getHandCards().contains(eqmt2));
		testPlayer.addItem(eqmt2);
		assertFalse(testPlayer.getHandCards().contains(eqmt2));
		assertTrue(testPlayer.getEquippedItems().contains(eqmt2));
		assertTrue(testPlayer.getHandCards().contains(eqmt3));
		testPlayer.addItem(eqmt3);
		assertFalse(testPlayer.getHandCards().contains(eqmt3));
		assertFalse(testPlayer.getAllItems().contains(eqmt3));
		assertTrue(GM.getTreasureDeck().discardPile.peek() == eqmt3);
		assertTrue(testPlayer.getHandCards().contains(item1));
		testPlayer.addItem(item1);
		assertFalse(testPlayer.getHandCards().contains(item1));
		assertTrue(testPlayer.getCarriedItems().contains(item1));
		assertTrue(testPlayer.getHandCards().contains(item2));
		testPlayer.addItem(item2);
		assertFalse(testPlayer.getHandCards().contains(item2));
		assertTrue(testPlayer.getCarriedItems().contains(item2));
		assertTrue(testPlayer.getHandCards().contains(item3));
		testPlayer.addItem(item3);
		assertFalse(testPlayer.getHandCards().contains(item3));
		assertTrue(testPlayer.getEquippedItems().remove(eqmt1));
		assertTrue(testPlayer.getCarriedItems().remove(item3));
		assertTrue(testPlayer.getHandCards().isEmpty());
		testPlayer.getEquippedItems().clear();
		testPlayer.getUnequippedItems().clear();
		testPlayer.getCarriedItems().clear();
		testPlayer.addCard(eqmt1);
		testPlayer.addCard(eqmt2);
		testPlayer.addCard(eqmt3);
		testPlayer.addCard(item1);
		testPlayer.addCard(item2);
		testPlayer.addCard(item3);
		GM.setActivePlayer(testPlayer);
		try { assertTrue(CardPlayManager.playCard(testPlayer, eqmt1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertTrue(CardPlayManager.playCard(testPlayer, eqmt2)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertFalse(CardPlayManager.playCard(testPlayer, eqmt3)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertTrue(CardPlayManager.playCard(testPlayer, item1)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertTrue(CardPlayManager.playCard(testPlayer, item2)); } catch (EndGameException ex) { fail("Not Game End"); }
		try { assertTrue(CardPlayManager.playCard(testPlayer, item3)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertTrue(testPlayer.getUnequippedItems().contains(eqmt1));
		assertTrue(testPlayer.getUnequippedItems().contains(eqmt2));
		assertTrue(testPlayer.getHandCards().contains(eqmt3));
		assertTrue(testPlayer.getCarriedItems().contains(item1));
		assertTrue(testPlayer.getCarriedItems().contains(item2));
		assertTrue(testPlayer.getCarriedItems().contains(item3));
		testPlayer.getHandCards().clear();
		testPlayer.getEquippedItems().clear();
		testPlayer.getUnequippedItems().clear();
		testPlayer.getCarriedItems().clear();
		GM.endPlayerTurn();
	}
			
	private void testCards() {
		EquipmentCard eqmt1 = new EquipmentCard(10, "e1", 200, 1, EquipmentCard.EquipmentType.OTHER, false, true, false);
		EquipmentCard eqmt2 = new EquipmentCard(11, "e2", 300, 2, EquipmentCard.EquipmentType.OTHER, true, false, true);
		ItemCard item4 = new ItemCard(12, "item4", 200);
		ItemCard item5 = new ItemCard(13, "item5", 200);
		
		RaceCard elf = new RaceCard(14, "Elf", Race.ELF);
		ClassCard wizard = new ClassCard(15, "Wizard", Class.WIZARD);
		male.getRaceCards().add(elf);
		male.getClassCards().add(wizard);
		
		male.addCard(eqmt1);
		male.addCard(item4);
		male.addCard(eqmt2);
		male.addCard(item5);
		male.addCard(new OtherTreasureCard(16, ""));
		male.addCard(new OtherDoorCard(17, ""));
		assertEquals(male.getAllValueCards().size(), 9);
		assertEquals(male.getHandCards().size(), 6);
		
		male.discard(item5);
		assertEquals(male.getHandCards().size(), 5);
		assertFalse(male.getHandCards().contains(item5));
		male.discard(male.getAllEquipment().getFirst());
		assertEquals(male.getAllEquipment().size(), 1);		
		male.discard(male.getCarriedItems().getLast());
		assertEquals(male.getCarriedItems().size(), 2);
		
		male.discard(elf);
		assertTrue(male.getRaceCards().isEmpty());
		male.discard(wizard);
		assertTrue(male.getClassCards().isEmpty());
		
		male.getRaceCards().add(new RaceCard(9, "Dwarf", Race.DWARF));
		assertFalse(male.hasHireling());
		assertNull(male.getHirelingCard());
		male.setHirelingCard(hireling);
		assertTrue(male.hasHireling());
		assertTrue(male.getHirelingCard() == hireling);
		male.discard(hireling);
		assertFalse(male.hasHireling());
		assertNull(male.getHirelingCard());
		
		male.getHandCards().removeLast();
		male.getHandCards().removeLast();
	}
	
	private void testDrawCards() {
		GM.setActivePlayer(testPlayer);
		male.removeChickenOnHeadCurse();
		male.removeMalignMirror();
		assertTrue(testPlayer.getHandCards().isEmpty());
		assertTrue(testPlayer.isHuman());
		assertFalse(testPlayer.isHalfBreed());
		assertEquals(testPlayer.getClassCards().size(), 0);
		RaceCard elf = new RaceCard(21, "elf", Race.ELF);
		ClassCard cleric = new ClassCard(22, "cleric", Class.CLERIC);
		MonsterEnhancerCard enhancer = new MonsterEnhancerCard(23, "enhancer", 50);
		GM.getDoorDeck().drawPile.push(malign);
		GM.getDoorDeck().drawPile.push(elf);
		GM.getDoorDeck().drawPile.push(cleric);
		GM.getDoorDeck().drawPile.push(chicken);
		GM.getDoorDeck().drawPile.push(enhancer);
		GM.getDoorDeck().drawPile.push(cheat);
		try { testPlayer.drawDoorCards(2, false); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 2);
		assertTrue(testPlayer.getHandCards().contains(cheat));
		assertTrue(testPlayer.getHandCards().contains(enhancer));
		assertFalse(male.hasChickenOnHead());
		try { testPlayer.drawDoorCards(2, true); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 2);
		assertTrue(testPlayer.getHandCards().contains(cheat));
		assertTrue(testPlayer.getHandCards().contains(enhancer));
		assertTrue(male.hasChickenOnHead());
		male.removeChickenOnHeadCurse();
		assertTrue(testPlayer.isCleric());
		GM.getDoorDeck().discardPile.clear();
		try { testPlayer.drawDoorCards(1, true); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 2);
		assertTrue(testPlayer.getHandCards().contains(cheat));
		assertTrue(testPlayer.getHandCards().contains(enhancer));
		assertTrue(testPlayer.isElf());
		testPlayer.discard(enhancer);
		try { testPlayer.drawDoorCards(1, true); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertTrue(testPlayer.getHandCards().contains(enhancer));
		assertFalse(testPlayer.getHandCards().contains(malign));
		assertFalse(male.hasMalignMirror());
		try { testPlayer.drawDoorCards(1, true); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 2);
		assertTrue(testPlayer.getHandCards().contains(enhancer));
		assertTrue(testPlayer.getHandCards().contains(malign));
		assertFalse(male.hasMalignMirror());
		assertFalse(female.hasMalignMirror());
		testPlayer.discard(enhancer);
		GM.getDoorDeck().drawPile.push(chicken);
		try { testPlayer.drawDoorCards(1, false); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertFalse(male.hasMalignMirror());
		assertTrue(male.hasChickenOnHead());
		male.removeChickenOnHeadCurse();
		assertTrue(testPlayer.getHandCards().contains(malign));
		
		testPlayer.getHandCards().clear();
		GM.moveToBattlePhase();
		GM.getTreasureDeck().discardPile.clear();
		EquipmentCard bigArmor = new EquipmentCard(24, "big armor", 100, 2, ARMOR, true, false, false);
		EquipmentCard armor = new EquipmentCard(25, "armor", 500, 3, ARMOR, false, false, false);
		EquipmentCard big = new EquipmentCard(26, "big", 600, 5, OTHER, true, false, false);
		ItemCard item = new ItemCard(27, "item", 500);
		GoUpLevelCard levelCard = new GoUpLevelCard(28, "level card");
		GM.getTreasureDeck().drawPile.push(hireling);
		GM.getTreasureDeck().drawPile.push(item);
		GM.getTreasureDeck().drawPile.push(big);
		GM.getTreasureDeck().drawPile.push(armor);
		GM.getTreasureDeck().drawPile.push(hoard);
		GM.getTreasureDeck().drawPile.push(levelCard);
		GM.getTreasureDeck().drawPile.push(bigArmor);
		testPlayer.drawTreasureCards(1, false);
		assertEquals(testPlayer.getHandCards().size(), 0);
		assertTrue(testPlayer.getEquippedItems().contains(bigArmor));
		testPlayer.drawTreasureCards(2, true);
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertEquals(testPlayer.getAllItems().size(), 3);
		assertTrue(testPlayer.getHandCards().contains(big));
		assertTrue(testPlayer.getEquippedItems().contains(bigArmor));
		assertTrue(testPlayer.getUnequippedItems().contains(armor));
		assertTrue(testPlayer.getCarriedItems().contains(item));
		assertEquals(testPlayer.getLevel(), 2);
		testPlayer.drawTreasureCards(1, false);
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertEquals(testPlayer.getAllItems().size(), 3);
		assertEquals(testPlayer.getLevel(), 2);
		assertTrue(testPlayer.hasHireling());
		assertTrue(testPlayer.getHirelingCard() == hireling);
		GM.getTreasureDeck().drawPile.clear();
		GM.getTreasureDeck().discardPile.clear();
		testPlayer.drawTreasureCards(1, false);
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertEquals(testPlayer.getAllItems().size(), 3);
		assertEquals(testPlayer.getLevel(), 2);
		GM.getTreasureDeck().discardPile.push(hoard);
		testPlayer.drawTreasureCards(2, false);
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertEquals(testPlayer.getAllItems().size(), 3);
		assertEquals(testPlayer.getLevel(), 2);
		testPlayer.discard(big);
		GM.getTreasureDeck().drawPile.add(hireling);
		testPlayer.addCard(item);
		testPlayer.drawTreasureCards(1, true);
		assertEquals(testPlayer.getHandCards().size(), 0);
		assertEquals(testPlayer.getAllItems().size(), 4);
		assertTrue(testPlayer.getEquippedItems().contains(big));
		assertEquals(testPlayer.getLevel(), 2);
		testPlayer.addCard(item);
		testPlayer.setHirelingCard(null);
		testPlayer.drawTreasureCards(1, false);
		testPlayer.discard(big);
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertTrue(testPlayer.hasHireling());
		assertTrue(testPlayer.getHandCards().contains(item));
		assertEquals(testPlayer.getAllItems().size(), 3);
		assertEquals(testPlayer.getLevel(), 2);
		GM.getTreasureDeck().drawPile.push(hireling);
		testPlayer.getHandCards().clear();
		testPlayer.discard(big);
		testPlayer.addCard(big);
		GM.getTreasureDeck().discardPile.push(levelCard);
		testPlayer.drawTreasureCards(1, true);
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertTrue(testPlayer.getHandCards().contains(big));
		assertEquals(testPlayer.getAllItems().size(), 3);
		assertEquals(testPlayer.getLevel(), 2);
		
		testPlayer.getHandCards().clear();
		testPlayer.getRaceCards().clear();
		testPlayer.getClassCards().clear();
		testPlayer.getEquippedItems().clear();
		testPlayer.getUnequippedItems().clear();
		testPlayer.getCarriedItems().clear();
		GM.endPlayerTurn();
	}
	
	private void testResurrect() {
		ClassCard cleric = new ClassCard(1, "Cleric", Class.CLERIC);
		
		female.getClassCards().clear();
		female.getHandCards().clear();
		assertFalse(female.canResurrect());
		
		female.addCard(new OtherDoorCard(2, ""));
		assertFalse(female.canResurrect());
		
		female.getClassCards().add(cleric);
		assertTrue(female.canResurrect());
		
		female.getHandCards().clear();
		assertFalse(female.canResurrect());
	}
	
	private void testDie() {
		male.addLastingCurse(chicken);
		assertTrue(male.hasChickenOnHead());
		male.setHirelingCard(hireling);
		assertTrue(male.hasHireling());
	
		assertFalse(male.getHandCards().isEmpty());
		assertFalse(male.getAllEquipment().isEmpty());
		assertFalse(male.getCarriedItems().isEmpty());
		assertFalse(male.needsNewCards());
				
		female.getHandCards().clear();
		testPlayer.getHandCards().clear();
		male.die();
		assertTrue(male.getHandCards().isEmpty());
		assertTrue(male.getAllEquipment().isEmpty());
		assertTrue(male.getCarriedItems().isEmpty());
		assertTrue(male.hasChickenOnHead());
		assertTrue(male.needsNewCards());
		assertFalse(male.hasHireling());
		assertEquals(testPlayer.getHandCards().size(), 1);
		assertEquals(female.getHandCards().size(), 1);
		
		male.drewNewCards();
		assertFalse(male.needsNewCards());
		
		EquipmentCard hat = new EquipmentCard(1, "hat", 200, 1, EquipmentCard.EquipmentType.HEADGEAR, false, false, false);
		male.addUnequippedItem(hat);
		male.die();
		assertTrue(male.getAllEquipment().isEmpty());
		assertTrue(male.hasChickenOnHead());
		
		male.addUnequippedItem(hat);
		assertEquals(male.equip(hat), "");
		male.die();
		assertTrue(male.getAllEquipment().isEmpty());
		assertFalse(male.hasChickenOnHead());
	}
			
	public void testClone() {
		EquipmentCard equipment1 = new EquipmentCard(1, "equipment1", 100, 1, ARMOR, true, false, true);
		EquipmentCard equipment2 = new EquipmentCard(2, "equipment2", 200, 2, ARMOR, false, true, true);
		EquipmentCard equipment3 = new EquipmentCard(3, "equipment3", 300, 3, ARMOR, true, false, false);
		ItemCard item1 = new ItemCard(4, "item1", 100, 1);
		ItemCard item2 = new ItemCard(5, "item2", 200);
		ItemCard item3 = new ItemCard(6, "item3", 300, 3);
		GoUpLevelCard levelCard = new GoUpLevelCard(7, "level card");
		RaceCard elf = new RaceCard(8, "Elf", Race.ELF);
		RaceCard halfling = new RaceCard(9, "Halfling", Race.HALFLING);
		OtherDoorCard half = new OtherDoorCard(10, "Half-Breed");
		ClassCard warrior = new ClassCard(11, "Warrior", Class.WARRIOR);
		ClassCard wizard = new ClassCard(12, "Wizard", Class.WIZARD);
		ClassCard thief = new ClassCard(13, "Thief", Class.THIEF);
		OtherDoorCard munchkin = new OtherDoorCard(14, "Super Munchkin");
		
		Player player = new Player(new MockGUI(0), "", true, PlayerType.COMPUTER_MEDIUM);
		player.goUpLevels(5, false);
		player.addCard(hireling);
		player.addCard(equipment3);
		player.addCard(levelCard);
		player.addCard(wizard);
		player.addUnequippedItem(equipment1);
		player.addUnequippedItem(equipment2);
		assertEquals(player.equip(equipment2), "");
		player.addItem(item1);
		player.addItem(item2);
		player.addItem(item3);
		player.addRaceCard(elf);
		player.setHalfBreedCard(half);
		player.addRaceCard(halfling);
		player.addClassCard(warrior);
		player.setSuperMunchkinCard(munchkin);
		player.addClassCard(thief);
		change.addEffects(player);
		chicken.addEffects(player);
		player.setHirelingCard(hireling);
		
		Player clone = player.clone();
		assertEquals(clone.getName(), "clone");
		assertTrue(clone.getPlayerType() == PlayerType.TEST);
		assertEquals(clone.getLevel(), player.getLevel());
		assertFalse(clone.isMale());
		assertTrue(clone.isFemale());
		assertTrue(clone.hasHireling());
		assertTrue(clone.getHirelingCard() == hireling);
		assertEquals(clone.getHandCards().size(), 4);
		assertTrue(clone.getHandCards().contains(hireling));
		assertTrue(clone.getHandCards().contains(equipment3));
		assertTrue(clone.getHandCards().contains(levelCard));
		assertTrue(clone.getHandCards().contains(wizard));
		assertEquals(clone.getEquippedItems().size(), 1);
		assertTrue(clone.getEquippedItems().contains(equipment2));
		assertEquals(clone.getUnequippedItems().size(), 1);
		assertTrue(clone.getUnequippedItems().contains(equipment1));
		assertEquals(clone.getCarriedItems().size(), 3);
		assertTrue(clone.getCarriedItems().contains(item1));
		assertTrue(clone.getCarriedItems().contains(item2));
		assertTrue(clone.getCarriedItems().contains(item3));
		assertEquals(clone.getRaceCards().size(), 2);
		assertTrue(clone.isElf());
		assertTrue(clone.isHalfling());
		assertFalse(clone.isHuman());
		assertTrue(clone.isHalfBreed());
		assertTrue(clone.getHalfBreedCard() == half);
		assertEquals(clone.getClassCards().size(), 2);
		assertTrue(clone.isWarrior());
		assertTrue(clone.isThief());
		assertTrue(clone.isSuperMunchkin());
		assertTrue(clone.getSuperMunchkinCard() == munchkin);
		assertTrue(clone.hasChangeSexCurse());
		assertTrue(clone.getChangeSexCard() == change);
		assertTrue(clone.hasChickenOnHead());
		assertTrue(clone.getChickenOnHeadCard() == chicken);
		assertFalse(clone.hasMalignMirror());
		assertNull(clone.getMalignMirrorCard());
		
		player.goDownLevel();
		player.getHandCards().clear();
		player.discard(elf);
		player.discard(warrior);
		assertEquals(clone.getLevel(), player.getLevel() + 1);
		assertEquals(clone.getHandCards().size(), 4);
		assertTrue(clone.isElf());
		assertTrue(clone.isWarrior());
		assertTrue(clone.isThief());
		assertTrue(clone.isSuperMunchkin());
		assertTrue(clone.getSuperMunchkinCard() == munchkin);
		
		player.goDownLevels(10);
		assertEquals(player.getLevel(), 1);
		clone = player.clone();
		assertEquals(clone.getLevel(), player.getLevel());
	}	
}
