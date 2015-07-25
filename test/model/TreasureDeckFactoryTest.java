
package model;

import gui.MockGUI;

import java.util.LinkedList;
import java.util.Stack;

import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.card.Card;
import model.card.ClassCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;

public class TreasureDeckFactoryTest extends UnitTest {
	
	private Stack<Card> deck;
	
	private Player human;
	private Player elf;
	private Player dwarf;
	private Player halfling;
	private Player half_elf;
	private Player half_dwarf;
	private Player half_halfling;
	private Player elf_dwarf;
	private Player elf_halfling;
	private Player dwarf_halfling;
	
	private Player none;
	private Player warrior;
	private Player wizard;
	private Player cleric;
	private Player thief;
	private Player warrior_wizard;
	private Player warrior_cleric;
	private Player warrior_thief;
	private Player wizard_cleric;
	private Player wizard_thief;
	private Player cleric_thief;
	
	private OtherDoorCard cheat;
	
	public int testAll() {
		initialize();
		
		testEquipmentCards();
		testItemCards();
		testGoUpLevelCards();
		testOtherTreasureCards();
		
		return errorCount;
	}
		
	private void initialize() {
		deck = TreasureDeckFactory.buildDeck();
		assertEquals(deck.size(), 74);
		
		cheat = new OtherDoorCard(1, "Cheat!");
		OtherDoorCard half = new OtherDoorCard(2, "Half-Breed");
		OtherDoorCard munchkin = new OtherDoorCard(3, "Super Munchkin");
		
		MockGUI mockGUI = new MockGUI(0);
		
		human = new Player(mockGUI, "", true, PlayerType.TEST);
		elf = new Player(mockGUI, "", false, PlayerType.TEST);
		elf.getRaceCards().add(new RaceCard(4, "Elf", Race.ELF));
		dwarf = new Player(mockGUI, "", true, PlayerType.TEST);
		dwarf.getRaceCards().add(new RaceCard(5, "Dwarf", Race.DWARF));
		halfling = new Player(mockGUI, "", false, PlayerType.TEST);
		halfling.getRaceCards().add(new RaceCard(6, "Halfling", Race.HALFLING));
		half_elf = new Player(mockGUI, "", true, PlayerType.TEST);
		half_elf.getRaceCards().add(new RaceCard(7, "Elf", Race.ELF));
		half_elf.setHalfBreedCard(half);
		half_dwarf = new Player(mockGUI, "", false, PlayerType.TEST);
		half_dwarf.getRaceCards().add(new RaceCard(8, "Dwarf", Race.DWARF));
		half_dwarf.setHalfBreedCard(half);
		half_halfling = new Player(mockGUI, "", true, PlayerType.TEST);
		half_halfling.getRaceCards().add(new RaceCard(9, "Halfling", Race.HALFLING));
		half_halfling.setHalfBreedCard(half);
		elf_dwarf = new Player(mockGUI, "", false, PlayerType.TEST);
		elf_dwarf.getRaceCards().add(new RaceCard(10, "Elf", Race.ELF));
		elf_dwarf.setHalfBreedCard(half);
		elf_dwarf.getRaceCards().add(new RaceCard(11, "Dwarf", Race.DWARF));
		elf_halfling = new Player(mockGUI, "", true, PlayerType.TEST);
		elf_halfling.getRaceCards().add(new RaceCard(12, "Elf", Race.ELF));
		elf_halfling.setHalfBreedCard(half);
		elf_halfling.getRaceCards().add(new RaceCard(13, "Halfling", Race.HALFLING));
		dwarf_halfling = new Player(mockGUI, "", false, PlayerType.TEST);
		dwarf_halfling.getRaceCards().add(new RaceCard(14, "Dwarf", Race.DWARF));
		dwarf_halfling.setHalfBreedCard(half);
		dwarf_halfling.getRaceCards().add(new RaceCard(15, "Halfling", Race.HALFLING));
		
		none = new Player(mockGUI, "", true, PlayerType.TEST);
		warrior = new Player(mockGUI, "", false, PlayerType.TEST);
		warrior.getClassCards().add(new ClassCard(16, "Warrior", Class.WARRIOR));
		wizard = new Player(mockGUI, "", true, PlayerType.TEST);
		wizard.getClassCards().add(new ClassCard(17, "Wizard", Class.WIZARD));
		cleric = new Player(mockGUI, "", false, PlayerType.TEST);
		cleric.getClassCards().add(new ClassCard(18, "Cleric", Class.CLERIC));
		thief = new Player(mockGUI, "", true, PlayerType.TEST);
		thief.getClassCards().add(new ClassCard(19, "Thief", Class.THIEF));
		warrior_wizard = new Player(mockGUI, "", false, PlayerType.TEST);
		warrior_wizard.getClassCards().add(new ClassCard(20, "Warrior", Class.WARRIOR));
		warrior_wizard.setSuperMunchkinCard(munchkin);
		warrior_wizard.getClassCards().add(new ClassCard(21, "Wizard", Class.WIZARD));
		warrior_cleric = new Player(mockGUI, "", true, PlayerType.TEST);
		warrior_cleric.getClassCards().add(new ClassCard(22, "Warrior", Class.WARRIOR));
		warrior_cleric.setSuperMunchkinCard(munchkin);
		warrior_cleric.getClassCards().add(new ClassCard(23, "Cleric", Class.CLERIC));
		warrior_thief = new Player(mockGUI, "", false, PlayerType.TEST);
		warrior_thief.getClassCards().add(new ClassCard(24, "Warrior", Class.WARRIOR));
		warrior_thief.setSuperMunchkinCard(munchkin);
		warrior_thief.getClassCards().add(new ClassCard(25, "Thief", Class.THIEF));
		wizard_cleric = new Player(mockGUI, "", true, PlayerType.TEST);
		wizard_cleric.getClassCards().add(new ClassCard(26, "Wizard", Class.WIZARD));
		wizard_cleric.setSuperMunchkinCard(munchkin);
		wizard_cleric.getClassCards().add(new ClassCard(27, "Cleric", Class.CLERIC));
		wizard_thief = new Player(mockGUI, "", false, PlayerType.TEST);
		wizard_thief.getClassCards().add(new ClassCard(28, "Wizard", Class.WIZARD));
		wizard_thief.setSuperMunchkinCard(munchkin);
		wizard_thief.getClassCards().add(new ClassCard(29, "Thief", Class.THIEF));
		cleric_thief = new Player(mockGUI, "", true, PlayerType.TEST);
		cleric_thief.getClassCards().add(new ClassCard(30, "Cleric", Class.CLERIC));
		cleric_thief.setSuperMunchkinCard(munchkin);
		cleric_thief.getClassCards().add(new ClassCard(31, "Thief", Class.THIEF));
		
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(human);
		players.add(none);
		GM.newTestGame(mockGUI, players);
	}
	
	private void testEquipmentCards() {
		int equipmentCount = 0;	
		for (Card card : deck) {
			if (card instanceof EquipmentCard) {
				EquipmentCard equipmentItem = (EquipmentCard)card;
				if (equipmentItem.getID() == Card.E_BOOTS_OF_BUTT_KICKING) {}
				else if (equipmentItem.getID() == Card.E_BOOTS_OF_RUNNING_REALLY_FAST) {}
				else if (equipmentItem.getID() == Card.E_BOW_WITH_RIBBONS) {
					assertEquals(human.equip(equipmentItem), "You must be an Elf to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(elf.equip(equipmentItem), "");
					assertEquals(half_elf.equip(equipmentItem), "");
					assertEquals(elf_dwarf.equip(equipmentItem), "");
					assertEquals(elf_halfling.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 4);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}	
				else if (equipmentItem.getID() == Card.E_BROAD_SWORD) {
					assertEquals(human.equip(equipmentItem), "You must be female to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertTrue(elf.isFemale());
					assertEquals(elf.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
				}	
				else if (equipmentItem.getID() == Card.E_BUCKLER_OF_SWASHING) {}	
				else if (equipmentItem.getID() == Card.E_CHEESE_GRATER_OF_PEACE) {
					assertEquals(none.equip(equipmentItem), "You must be a Cleric to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(cleric.equip(equipmentItem), "");
					assertEquals(warrior_cleric.equip(equipmentItem), "");
					assertEquals(wizard_cleric.equip(equipmentItem), "");
					assertEquals(cleric_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 3);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 0);
				}
				else if (equipmentItem.getID() == Card.E_CLOAK_OF_OBSCURITY) {
					assertEquals(none.equip(equipmentItem), "You must be a Thief to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(thief.equip(equipmentItem), "");
					assertEquals(warrior_thief.equip(equipmentItem), "");
					assertEquals(wizard_thief.equip(equipmentItem), "");
					assertEquals(cleric_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 4);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 0);
				}
				else if (equipmentItem.getID() == Card.E_DAGGER_OF_TREACHERY) {
					assertEquals(none.equip(equipmentItem), "You must be a Thief to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(thief.equip(equipmentItem), "");
					assertEquals(warrior_thief.equip(equipmentItem), "");
					assertEquals(wizard_thief.equip(equipmentItem), "");
					assertEquals(cleric_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 3);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 0);
				}
				else if (equipmentItem.getID() == Card.E_ELEVEN_FOOT_POLE) {}
				else if (equipmentItem.getID() == Card.E_FLAMETHROWER) {}
				else if (equipmentItem.getID() == Card.E_FLAMING_ARMOR) {}
				else if (equipmentItem.getID() == Card.E_GENTLEMENS_CLUB) {
					assertTrue(elf.isFemale());
					assertEquals(elf.equip(equipmentItem), "You must be male to use this.");
					elf.addUnequippedItem(equipmentItem);
					elf.setCheatCards(cheat, equipmentItem);
					assertEquals(elf.equip(equipmentItem), "");
					assertEquals(human.equip(equipmentItem), "");
					elf.removeCheat();
					elf.getEquippedItems().clear();
					elf.getUnequippedItems().clear();
				}
				else if (equipmentItem.getID() == Card.E_HAMMER_OF_KNEECAPPING) {
					assertEquals(human.equip(equipmentItem), "You must be a Dwarf to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(dwarf.equip(equipmentItem), "");
					assertEquals(half_dwarf.equip(equipmentItem), "");
					assertEquals(elf_dwarf.equip(equipmentItem), "");
					assertEquals(dwarf_halfling.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 4);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}
				else if (equipmentItem.getID() == Card.E_HELM_OF_COURAGE) {}
				else if (equipmentItem.getID() == Card.E_HORNED_HELMET) {
					assertEquals(equipmentItem.getBonus(human), 1);
					assertEquals(equipmentItem.getBonus(elf), 3);
					assertEquals(equipmentItem.getBonus(half_elf), 3);
					assertEquals(equipmentItem.getBonus(elf_dwarf), 3);
					assertEquals(equipmentItem.getBonus(elf_halfling), 3);
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 2);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}
				else if (equipmentItem.getID() == Card.E_HUGE_ROCK) {}
				else if (equipmentItem.getID() == Card.E_KNEEPADS_OF_ALLURE) {
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(cleric.equip(equipmentItem), "Clerics cannot use this.");
					assertEquals(warrior_cleric.equip(equipmentItem), "Clerics cannot use this.");
					assertEquals(wizard_cleric.equip(equipmentItem), "Clerics cannot use this.");
					assertEquals(cleric_thief.equip(equipmentItem), "Clerics cannot use this.");
					cleric.addUnequippedItem(equipmentItem);
					cleric.setCheatCards(cheat, equipmentItem);
					assertEquals(cleric.equip(equipmentItem), "");
					cleric.removeCheat();
					cleric.getEquippedItems().clear();
					cleric.getUnequippedItems().clear();
				}
				else if (equipmentItem.getID() == Card.E_LEATHER_ARMOR) {}
				else if (equipmentItem.getID() == Card.E_LIMBURGER_AND_ANCHOVY_SANDWICH) {
					assertEquals(human.equip(equipmentItem), "You must be a Halfling to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(halfling.equip(equipmentItem), "");
					assertEquals(half_halfling.equip(equipmentItem), "");
					assertEquals(elf_halfling.equip(equipmentItem), "");
					assertEquals(dwarf_halfling.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 3);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}
				else if (equipmentItem.getID() == Card.E_MACE_OF_SHARPNESS) {
					assertEquals(none.equip(equipmentItem), "You must be a Cleric to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(cleric.equip(equipmentItem), "");
					assertEquals(warrior_cleric.equip(equipmentItem), "");
					assertEquals(wizard_cleric.equip(equipmentItem), "");
					assertEquals(cleric_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 4);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 0);
				}
				else if (equipmentItem.getID() == Card.E_MITHRIL_ARMOR) {
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(wizard.equip(equipmentItem), "Wizards cannot use this.");
					assertEquals(warrior_wizard.equip(equipmentItem), "Wizards cannot use this.");
					assertEquals(wizard_cleric.equip(equipmentItem), "Wizards cannot use this.");
					assertEquals(wizard_thief.equip(equipmentItem), "Wizards cannot use this.");
					wizard.addUnequippedItem(equipmentItem);
					wizard.setCheatCards(cheat, equipmentItem);
					assertEquals(wizard.equip(equipmentItem), "");
					wizard.removeCheat();
					wizard.getEquippedItems().clear();
					wizard.getUnequippedItems().clear();
				}
				else if (equipmentItem.getID() == Card.E_PANTYHOSE_OF_GIANT_STRENGTH) {
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(warrior.equip(equipmentItem), "Warriors cannot use this.");
					assertEquals(warrior_wizard.equip(equipmentItem), "Warriors cannot use this.");
					assertEquals(warrior_cleric.equip(equipmentItem), "Warriors cannot use this.");
					assertEquals(warrior_thief.equip(equipmentItem), "Warriors cannot use this.");
					warrior.addUnequippedItem(equipmentItem);
					warrior.setCheatCards(cheat, equipmentItem);
					assertEquals(warrior.equip(equipmentItem), "");
					warrior.removeCheat();
					warrior.getEquippedItems().clear();
					warrior.getUnequippedItems().clear();
				}
				else if (equipmentItem.getID() == Card.E_POINTY_HAT_OF_POWER) {
					assertEquals(none.equip(equipmentItem), "You must be a Wizard to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(wizard.equip(equipmentItem), "");
					assertEquals(warrior_wizard.equip(equipmentItem), "");
					assertEquals(wizard_cleric.equip(equipmentItem), "");
					assertEquals(wizard_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 3);
				}
				else if (equipmentItem.getID() == Card.E_RAD_BANDANNA) {
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(half_elf.equip(equipmentItem), "");
					assertEquals(half_dwarf.equip(equipmentItem), "");
					assertEquals(half_halfling.equip(equipmentItem), "");
					assertEquals(elf.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(dwarf.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(halfling.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(elf_dwarf.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(elf_halfling.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(dwarf_halfling.equip(equipmentItem), "You must be a Human to use this.");
					dwarf_halfling.addUnequippedItem(equipmentItem);
					dwarf_halfling.setCheatCards(cheat, equipmentItem);
					assertEquals(dwarf_halfling.equip(equipmentItem), "");
					dwarf_halfling.removeCheat();
					dwarf_halfling.getEquippedItems().clear();
					dwarf_halfling.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 3);
				}
				else if (equipmentItem.getID() == Card.E_RAPIER_OF_UNFAIRNESS) {
					assertEquals(human.equip(equipmentItem), "You must be an Elf to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(elf.equip(equipmentItem), "");
					assertEquals(half_elf.equip(equipmentItem), "");
					assertEquals(elf_dwarf.equip(equipmentItem), "");
					assertEquals(elf_halfling.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 3);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}
				else if (equipmentItem.getID() == Card.E_RAT_ON_A_STICK) {}
				else if (equipmentItem.getID() == Card.E_REALLY_IMPRESSIVE_TITLE) {}
				else if (equipmentItem.getID() == Card.E_SANDALS_OF_PROTECTION) {}
				else if (equipmentItem.getID() == Card.E_SHIELD_OF_UBIQUITY) {
					assertEquals(none.equip(equipmentItem), "You must be a Warrior to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(warrior.equip(equipmentItem), "");
					assertEquals(warrior_wizard.equip(equipmentItem), "");
					assertEquals(warrior_cleric.equip(equipmentItem), "");
					assertEquals(warrior_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 4);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 0);
				}
				else if (equipmentItem.getID() == Card.E_SHORT_WIDE_ARMOR) {
					assertEquals(human.equip(equipmentItem), "You must be a Dwarf to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(dwarf.equip(equipmentItem), "");
					assertEquals(half_dwarf.equip(equipmentItem), "");
					assertEquals(elf_dwarf.equip(equipmentItem), "");
					assertEquals(dwarf_halfling.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 3);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}
				else if (equipmentItem.getID() == Card.E_SINGING_AND_DANCING_SWORD) {
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(thief.equip(equipmentItem), "Thieves cannot use this.");
					assertEquals(warrior_thief.equip(equipmentItem), "Thieves cannot use this.");
					assertEquals(wizard_thief.equip(equipmentItem), "Thieves cannot use this.");
					assertEquals(cleric_thief.equip(equipmentItem), "Thieves cannot use this.");
					thief.addUnequippedItem(equipmentItem);
					thief.setCheatCards(cheat, equipmentItem);
					assertEquals(thief.equip(equipmentItem), "");
					thief.removeCheat();
					thief.getEquippedItems().clear();
					thief.getUnequippedItems().clear();
				}
				else if (equipmentItem.getID() == Card.E_SLIMY_ARMOR) {}
				else if (equipmentItem.getID() == Card.E_SNEAKY_BACKSWORD) {}
				else if (equipmentItem.getID() == Card.E_SPIKY_KNEES) {}
				else if (equipmentItem.getID() == Card.E_STAFF_OF_NAPALM) {
					assertEquals(none.equip(equipmentItem), "You must be a Wizard to use this.");
					none.addUnequippedItem(equipmentItem);
					none.setCheatCards(cheat, equipmentItem);
					assertEquals(none.equip(equipmentItem), "");
					assertEquals(wizard.equip(equipmentItem), "");
					assertEquals(warrior_wizard.equip(equipmentItem), "");
					assertEquals(wizard_cleric.equip(equipmentItem), "");
					assertEquals(wizard_thief.equip(equipmentItem), "");
					none.removeCheat();
					none.getEquippedItems().clear();
					none.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToClass(Class.CLERIC), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.THIEF), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WARRIOR), 0);
					assertEquals(equipmentItem.getBonusToClass(Class.WIZARD), 5);
				}
				else if (equipmentItem.getID() == Card.E_STEPLADDER) {
					assertEquals(human.equip(equipmentItem), "You must be a Halfling to use this.");
					human.addUnequippedItem(equipmentItem);
					human.setCheatCards(cheat, equipmentItem);
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(halfling.equip(equipmentItem), "");
					assertEquals(half_halfling.equip(equipmentItem), "");
					assertEquals(elf_halfling.equip(equipmentItem), "");
					assertEquals(dwarf_halfling.equip(equipmentItem), "");
					human.removeCheat();
					human.getEquippedItems().clear();
					human.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 3);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 0);
				}
				else if (equipmentItem.getID() == Card.E_SWISS_ARMY_POLEARM) {
					assertEquals(human.equip(equipmentItem), "");
					assertEquals(half_elf.equip(equipmentItem), "");
					assertEquals(half_dwarf.equip(equipmentItem), "");
					assertEquals(half_halfling.equip(equipmentItem), "");
					assertEquals(elf.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(dwarf.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(halfling.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(elf_halfling.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(dwarf_halfling.equip(equipmentItem), "You must be a Human to use this.");
					assertEquals(elf_dwarf.equip(equipmentItem), "You must be a Human to use this.");
					elf_dwarf.addUnequippedItem(equipmentItem);
					elf_dwarf.setCheatCards(cheat, equipmentItem);
					assertEquals(elf_dwarf.equip(equipmentItem), "");
					elf_dwarf.removeCheat();
					elf_dwarf.getEquippedItems().clear();
					elf_dwarf.getUnequippedItems().clear();
					
					assertEquals(equipmentItem.getBonusToRace(Race.DWARF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.ELF), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HALFLING), 0);
					assertEquals(equipmentItem.getBonusToRace(Race.HUMAN), 4);
				}
				else if (equipmentItem.getID() == Card.E_TUBA_OF_CHARM) {}
				else {
					fail("Invalid Equipment Card: " + equipmentItem + "(" + equipmentItem.getID() + ")");
					continue;
				}
				
				equipmentCount++;
			}
		}
		assertEquals(equipmentCount, 38);
	}
	
	private void testItemCards() {
		int itemCount = 0;	
		for (Card card : deck) {
			if (card instanceof ItemCard) {
				ItemCard item = (ItemCard)card;
				if (item.getID() == Card.I_COTION_OF_PONFUSION) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 3);
				}
				else if (item.getID() == Card.I_DOPPLEGANGER) {
					assertEquals(item.getValue(), 300);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_ELECTRIC_RADIOACTIVE_ACID_POTION) {
					assertEquals(item.getValue(), 200);
					assertEquals(item.getBonus(), 5);
				}
				else if (item.getID() == Card.I_FLAMING_POISON_POTION) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 3);
				}
				else if (item.getID() == Card.I_FLASK_OF_GLUE) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_FREEZING_EXPLOSIVE_POTION) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 3);
				}
				else if (item.getID() == Card.I_FRIENDSHIP_POTION) {
					assertEquals(item.getValue(), 200);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_INVISIBILITY_POTION) {
					assertEquals(item.getValue(), 200);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_INSTANT_WALL) {
					assertEquals(item.getValue(), 300);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_LOADED_DIE) {
					assertEquals(item.getValue(), 300);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_MAGIC_LAMP_1) {
					assertEquals(item.getValue(), 500);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_MAGIC_LAMP_2) {
					assertEquals(item.getValue(), 500);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_MAGIC_MISSILE) {
					assertEquals(item.getValue(), 300);
					assertEquals(item.getBonus(), 5);
				}
				else if (item.getID() == Card.I_NASTY_TASTING_SPORTS_DRINK) {
					assertEquals(item.getValue(), 200);
					assertEquals(item.getBonus(), 2);
				}
				else if (item.getID() == Card.I_POLLYMORPH_POTION) {
					assertEquals(item.getValue(), 1300);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_POTION_OF_HALITOSIS) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 2);
				}
				else if (item.getID() == Card.I_POTION_OF_IDIOTIC_BRAVERY) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 2);
				}
				else if (item.getID() == Card.I_PRETTY_BALLOONS) {
					assertEquals(item.getValue(), 0);
					assertEquals(item.getBonus(), 5);
				}
				else if (item.getID() == Card.I_SLEEP_POTION) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 2);
				}
				else if (item.getID() == Card.I_TRANSFERRAL_POTION) {
					assertEquals(item.getValue(), 300);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_WAND_OF_DOWSING) {
					assertEquals(item.getValue(), 1100);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_WISHING_RING_1) {
					assertEquals(item.getValue(), 500);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_WISHING_RING_2) {
					assertEquals(item.getValue(), 500);
					assertEquals(item.getBonus(), 0);
				}
				else if (item.getID() == Card.I_YUPPIE_WATER) {
					assertEquals(item.getValue(), 100);
					assertEquals(item.getBonus(), 0);
				}
				else {
					fail("Invalid Item Card: " + item + "(" + item.getID() + ")");
					continue;
				}
				
				itemCount++;
			}
		}
		assertEquals(itemCount, 24);
	}
	
	private void testGoUpLevelCards() {
		int goUpLevelCount = 0;	
		for (Card card : deck) {
			if (card instanceof GoUpLevelCard) {
				GoUpLevelCard goUpCard = (GoUpLevelCard)card;
				if (goUpCard.getID() == Card.GUL_1000_GOLD_PIECES) {}
				else if (goUpCard.getID() == Card.GUL_BOIL_AN_ANTHILL) {}
				else if (goUpCard.getID() == Card.GUL_BRIBE_GM_WITH_FOOD) {}
				else if (goUpCard.getID() == Card.GUL_CONVENIENT_ADDITION_ERROR) {}
				else if (goUpCard.getID() == Card.GUL_INVOKE_OBSCURE_RULES) {}
				else if (goUpCard.getID() == Card.GUL_KILL_THE_HIRELING) {}
				else if (goUpCard.getID() == Card.GUL_MOW_THE_BATTLEFIELD) {}
				else if (goUpCard.getID() == Card.GUL_POTION_OF_GENERAL_STUDLINESS) {}
				else if (goUpCard.getID() == Card.GUL_WHINE_AT_THE_GM) {}
				else {
					fail("Invalid Go Up A Level Card: " + goUpCard + "(" + goUpCard.getID() + ")");
					continue;
				}
				
				goUpLevelCount++;
			}
		}
		assertEquals(goUpLevelCount, 9);
	}
	
	private void testOtherTreasureCards() {
		int otherCount = 0;	
		for (Card card : deck) {
			if (card instanceof OtherTreasureCard) {
				OtherTreasureCard other = (OtherTreasureCard)card;
				if (other.getID() == Card.OT_STEAL_A_LEVEL) {}
				else if (other.getID() == Card.OT_HIRELING) {}
				else if (other.getID() == Card.OT_HOARD) {}	
				else {
					fail("Invalid Other Treasure Card: " + other + "(" + other.getID() + ")");
					continue;
				}
				
				otherCount++;
			}
		}
		assertEquals(otherCount, 3);
	}
}
