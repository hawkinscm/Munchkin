
package model;

import gui.MockGUI;

import java.util.LinkedList;
import java.util.Stack;

import model.Battle;
import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class DoorDeckFactoryTest extends UnitTest {

	private final EquipmentCard hat1 = new EquipmentCard(1, "hat1", 0, 0, EquipmentType.HEADGEAR, false, false, true);
	private final EquipmentCard hat2 = new EquipmentCard(2, "hat2", 0, 0, EquipmentType.HEADGEAR, false, false, false);
	private final EquipmentCard hat3 = new EquipmentCard(3, "hat3", 0, 0, EquipmentType.HEADGEAR, false, false, false);
	private final EquipmentCard armor1 = new EquipmentCard(4, "armor1", 0, 0, EquipmentType.ARMOR, false, true, true);
	private final EquipmentCard armor2 = new EquipmentCard(5, "armor2", 0, 0, EquipmentType.ARMOR, false, false, false);
	private final EquipmentCard armor3 = new EquipmentCard(6, "armor3", 0, 0, EquipmentType.ARMOR, false, false, false);
	private final EquipmentCard shoes1 = new EquipmentCard(7, "shoes1", 0, 0, EquipmentType.FOOTGEAR, false, true, true);
	private final EquipmentCard shoes2 = new EquipmentCard(8, "shoes2", 0, 0, EquipmentType.FOOTGEAR, false, true, false);
	private final EquipmentCard shoes3 = new EquipmentCard(9, "shoes3", 0, 0, EquipmentType.FOOTGEAR, false, true, false);
	private final EquipmentCard hand1 = new EquipmentCard(10, "hand1", 0, 0, EquipmentType.ONE_HAND, false, false, true);
	private final EquipmentCard hand2 = new EquipmentCard(11, "hand2", 0, 0, EquipmentType.ONE_HAND, false, false, false);
	private final EquipmentCard hands1 = new EquipmentCard(12, "hands1", 0, 0, EquipmentType.TWO_HANDS, false, false, true);
	private final EquipmentCard hands2 = new EquipmentCard(13, "hands2", 0, 0, EquipmentType.TWO_HANDS, false, false, false);
	private final EquipmentCard other1 = new EquipmentCard(14, "other1", 0, 0, EquipmentType.OTHER, false, true, true);
	private final EquipmentCard other2 = new EquipmentCard(15, "other2", 0, 0, EquipmentType.OTHER, false, true, false);
	
	private final EquipmentCard big1 = new EquipmentCard(16, "big1", 0, 0, EquipmentType.HEADGEAR, true, false, false);
	private final EquipmentCard big2 = new EquipmentCard(17, "big2", 0, 0, EquipmentType.ONE_HAND, true, false, true);
	private final EquipmentCard big3 = new EquipmentCard(18, "big3", 0, 0, EquipmentType.OTHER, true, true, true);
	
	private EquipmentCard maleOnly;
	private EquipmentCard femaleOnly;
	
	private ItemCard loadedDie = new ItemCard(Card.I_LOADED_DIE, "Loaded Die", 300);
	
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
	
	private OtherDoorCard half;
	private OtherDoorCard munchkin;
	
	public int testAll() {
		initialize();
		
		testRaceCards();
		testClassCards();
		testMonsterCards();
		testCurseCards();
		testMonsterEnhancerCards();
		testOtherDoorCards();
		
		return errorCount;
	}
		
	private void initialize() {
		deck = DoorDeckFactory.buildDeck();
		assertEquals(deck.size(), 94);		
		
		half = new OtherDoorCard(20, "Half-Breed");
		munchkin = new OtherDoorCard(21, "Super Munchkin");
		
		MockGUI mockGUI = new MockGUI(0);
		
		human = new Player(mockGUI, "human", true, PlayerType.TEST);
		elf = new Player(mockGUI, "", false, PlayerType.TEST);
		dwarf = new Player(mockGUI, "", true, PlayerType.TEST);
		halfling = new Player(mockGUI, "", false, PlayerType.TEST);
		half_elf = new Player(mockGUI, "", true, PlayerType.TEST);
		half_elf.setHalfBreedCard(half);
		half_dwarf = new Player(mockGUI, "", false, PlayerType.TEST);
		half_dwarf.setHalfBreedCard(half);
		half_halfling = new Player(mockGUI, "", true, PlayerType.TEST);
		half_halfling.setHalfBreedCard(half);
		elf_dwarf = new Player(mockGUI, "", false, PlayerType.TEST);
		elf_dwarf.setHalfBreedCard(half);
		elf_halfling = new Player(mockGUI, "", true, PlayerType.TEST);
		elf_halfling.setHalfBreedCard(half);
		dwarf_halfling = new Player(mockGUI, "", false, PlayerType.TEST);
		dwarf_halfling.setHalfBreedCard(half);
		
		none = new Player(mockGUI, "none", true, PlayerType.TEST);
		warrior = new Player(mockGUI, "", false, PlayerType.TEST);
		wizard = new Player(mockGUI, "", true, PlayerType.TEST);
		cleric = new Player(mockGUI, "", false, PlayerType.TEST);
		thief = new Player(mockGUI, "", true, PlayerType.TEST);
		warrior_wizard = new Player(mockGUI, "", false, PlayerType.TEST);
		warrior_wizard.setSuperMunchkinCard(munchkin);
		warrior_cleric = new Player(mockGUI, "", true, PlayerType.TEST);
		warrior_cleric.setSuperMunchkinCard(munchkin);
		warrior_thief = new Player(mockGUI, "", false, PlayerType.TEST);
		warrior_thief.setSuperMunchkinCard(munchkin);
		wizard_cleric = new Player(mockGUI, "", true, PlayerType.TEST);
		wizard_cleric.setSuperMunchkinCard(munchkin);
		wizard_thief = new Player(mockGUI, "", false, PlayerType.TEST);
		wizard_thief.setSuperMunchkinCard(munchkin);
		cleric_thief = new Player(mockGUI, "", true, PlayerType.TEST);
		cleric_thief.setSuperMunchkinCard(munchkin);
		
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(null);
		players.add(human);
		players.add(none);		
		GM.newTestGame(mockGUI, players);
		
		for (Card card : TreasureDeckFactory.buildDeck()) {
			if (card.getID() == Card.E_GENTLEMENS_CLUB)
				maleOnly = (EquipmentCard)card;
			else if (card.getID() == Card.E_BROAD_SWORD)
				femaleOnly = (EquipmentCard)card;
		}		
	}

	private void testRaceCards() {
		int raceCount = 0;	
		for (Card card : deck) {
			if (card instanceof RaceCard) {
				RaceCard race = (RaceCard)card;
				if (race.getID() == Card.R_ELF_1) {
					assertEquals(race.getName(), "Elf");
					elf.addRaceCard(race);
					half_elf.addRaceCard(race);
				}
				else if (race.getID() == Card.R_ELF_2) {
					assertEquals(race.getName(), "Elf");
					elf_dwarf.addRaceCard(race);
				}
				else if (race.getID() == Card.R_ELF_3) {
					assertEquals(race.getName(), "Elf");
					elf_halfling.addRaceCard(race);
				}
				else if (race.getID() == Card.R_DWARF_1) {
					assertEquals(race.getName(), "Dwarf");
					dwarf.addRaceCard(race);
					half_dwarf.addRaceCard(race);
				}
				else if (race.getID() == Card.R_DWARF_2) {
					assertEquals(race.getName(), "Dwarf");
					elf_dwarf.addRaceCard(race);
				}
				else if (race.getID() == Card.R_DWARF_3) {
					assertEquals(race.getName(), "Dwarf");
					dwarf_halfling.addRaceCard(race);
				}
				else if (race.getID() == Card.R_HALFLING_1) {
					assertEquals(race.getName(), "Halfling");
					halfling.addRaceCard(race);
					half_halfling.addRaceCard(race);
				}
				else if (race.getID() == Card.R_HALFLING_2) {
					assertEquals(race.getName(), "Halfling");
					elf_halfling.addRaceCard(race);
				}
				else if (race.getID() == Card.R_HALFLING_3) {
					assertEquals(race.getName(), "Halfling");
					dwarf_halfling.addRaceCard(race);
				}
				else {
					fail("Invalid Race Card: " + race + "(" + race.getID() + ")");
					continue;
				}
				
				raceCount++;
			}
		}
		assertEquals(raceCount, 9);
	}
		
	private void testClassCards() {
		int classCount = 0;	
		for (Card card : deck) {
			if (card instanceof ClassCard) {
				ClassCard characterClass = (ClassCard)card;
				if (characterClass.getID() == Card.CL_WARRIOR_1) {
					assertEquals(characterClass.getName(), "Warrior");
					warrior.addClassCard(characterClass);
					warrior_wizard.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_WARRIOR_2) {
					assertEquals(characterClass.getName(), "Warrior");
					warrior_cleric.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_WARRIOR_3) {
					assertEquals(characterClass.getName(), "Warrior");
					warrior_thief.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_WIZARD_1) {
					assertEquals(characterClass.getName(), "Wizard");
					wizard.addClassCard(characterClass);
					warrior_wizard.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_WIZARD_2) {
					assertEquals(characterClass.getName(), "Wizard");
					wizard_cleric.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_WIZARD_3) {
					assertEquals(characterClass.getName(), "Wizard");
					wizard_thief.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_CLERIC_1) {
					assertEquals(characterClass.getName(), "Cleric");
					cleric.addClassCard(characterClass);
					warrior_cleric.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_CLERIC_2) {
					assertEquals(characterClass.getName(), "Cleric");
					wizard_cleric.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_CLERIC_3) {
					assertEquals(characterClass.getName(), "Cleric");
					cleric_thief.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_THIEF_1) {
					assertEquals(characterClass.getName(), "Thief");
					thief.addClassCard(characterClass);
					warrior_thief.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_THIEF_2) {
					assertEquals(characterClass.getName(), "Thief");
					wizard_thief.addClassCard(characterClass);
				}
				else if (characterClass.getID() == Card.CL_THIEF_3) {
					assertEquals(characterClass.getName(), "Thief");
					cleric_thief.addClassCard(characterClass);
				}
				else {
					fail("Invalid Class Card: " + characterClass + "(" + characterClass.getID() + ")");
					continue;
				}
				
				classCount++;
			}
		}
		assertEquals(classCount, 12);
	}
	
	private void testMonsterCards() {
		MonsterCard testMonster = new MonsterCard(25, "monster", 1, 1, 1, false) { public void doBadStuff(Player player){} };
		
		int monsterCount = 0;
		for (Card card : deck) {
			if (card instanceof MonsterCard) {
				Player testPlayer = new Player(new MockGUI(0), "player", true, PlayerType.TEST);
				GM.getPlayers().set(0, testPlayer);
				GM.setActivePlayer(testPlayer);
				OtherDoorCard cheat = new OtherDoorCard(26, "Cheat");
				
				testPlayer.addUnequippedItem(hat1);
				testPlayer.addUnequippedItem(hat2);
				testPlayer.addUnequippedItem(armor1);
				testPlayer.addUnequippedItem(armor2);
				testPlayer.addUnequippedItem(shoes1);
				testPlayer.addUnequippedItem(shoes2);
				testPlayer.addUnequippedItem(hand1);
				testPlayer.addUnequippedItem(hand2);
				testPlayer.addUnequippedItem(hands1);
				testPlayer.addUnequippedItem(hands2);
				testPlayer.addUnequippedItem(other1);
				testPlayer.addUnequippedItem(other2);
				assertEquals(testPlayer.equip(hat1), "");
				testPlayer.setCheatCards(cheat, hat1);
				assertEquals(testPlayer.equip(hat2), "");
				assertEquals(testPlayer.equip(armor1), "");
				testPlayer.setCheatCards(cheat, armor1);
				assertEquals(testPlayer.equip(armor2), "");
				assertEquals(testPlayer.equip(shoes1), "");
				testPlayer.setCheatCards(cheat, shoes1);
				assertEquals(testPlayer.equip(shoes2), "");
				assertEquals(testPlayer.equip(hand1), "");
				assertEquals(testPlayer.equip(hand2), "");
				testPlayer.setCheatCards(cheat, hands1);
				assertEquals(testPlayer.equip(hands1), "");
				testPlayer.setCheatCards(cheat, hands2);
				assertEquals(testPlayer.equip(hands2), "");
				assertEquals(testPlayer.equip(other1), "");
				assertEquals(testPlayer.equip(other2), "");
				
				assertEquals(testPlayer.getAllEquipment().size(), 12);
				assertEquals(testPlayer.getEquippedItems().size(), 12);
				assertEquals(testPlayer.getUnequippedItems().size(), 0);
				
				Battle battle = new Battle(testPlayer, testMonster);
				
				MonsterCard monster = (MonsterCard)card;
				if (monster.getID() == Card.M_3872_ORCS) {
					assertEquals(monster.getLevel(battle), 10);
					battle.activePlayer = dwarf;
					assertEquals(monster.getLevel(battle), 16);
					battle.activePlayer = half_dwarf;
					assertEquals(monster.getLevel(battle), 10);
					battle.activePlayer = elf_dwarf;
					assertEquals(monster.getLevel(battle), 16);
					battle.activePlayer = dwarf_halfling;
					assertEquals(monster.getLevel(battle), 16);
					
					battle.activePlayer = human;
					battle.helper = human;
					assertEquals(monster.getLevel(battle), 10);
					battle.helper = dwarf;
					assertEquals(monster.getLevel(battle), 16);
					battle.helper = half_dwarf;
					assertEquals(monster.getLevel(battle), 10);
					battle.helper = elf_dwarf;
					assertEquals(monster.getLevel(battle), 16);
					battle.helper = dwarf_halfling;
					assertEquals(monster.getLevel(battle), 16);
					
					assertEquals(monster.getRaceBonus(Race.DWARF), 6);
					assertEquals(monster.getRaceBonus(Race.ELF), 0);
					assertEquals(monster.getRaceBonus(Race.HALFLING), 0);
					assertEquals(monster.getRaceBonus(Race.HUMAN), 0);
					
					human.addItem(loadedDie);
					human.goUpLevels(4, false);
					assertEquals(human.getLevel(), 5);
					while (human.getCarriedItems().contains(loadedDie))
						monster.doBadStuff(human);
					assertTrue(human.needsNewCards());
					assertEquals(human.getLevel(), 5);
					human.drewNewCards();					
					ItemCard precious = new ItemCard(30, "precious", 8000);
					human.addItem(precious);
					human.addItem(loadedDie);
					while (human.getCarriedItems().contains(loadedDie)) {
						monster.doBadStuff(human);
						assertEquals(human.getLevel(), 2);
						human.goUpLevels(3, false);
					}
					assertFalse(human.needsNewCards());
					assertTrue(human.getCarriedItems().contains(precious));
					human.goDownLevels(5);
					human.getCarriedItems().remove(precious);
					assertTrue(human.getAllItems().isEmpty());
					assertTrue(human.getHandCards().isEmpty());
					none.getHandCards().clear();
				}
				else if (monster.getID() == Card.M_AMAZON) {
					human.goUpLevels(3, false);
					monster.doBadStuff(human);
					assertEquals(human.getLevel(), 1);
					
					testPlayer.goUpLevel(false);
					testPlayer.addClassCard(new ClassCard(30, "Warrior", Class.WARRIOR));
					testPlayer.addCard(munchkin);
					testPlayer.addClassCard(new ClassCard(31, "Cleric", Class.CLERIC));
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getClassCards().isEmpty());
					assertFalse(testPlayer.isSuperMunchkin());
					assertEquals(testPlayer.getLevel(), 2);					
				}
				else if (monster.getID() == Card.M_BIGFOOT) {
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = dwarf;
					assertEquals(monster.getLevel(battle), 15);
					battle.activePlayer = halfling;
					assertEquals(monster.getLevel(battle), 15);
					battle.activePlayer = half_dwarf;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = half_halfling;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = elf_dwarf;
					assertEquals(monster.getLevel(battle), 15);
					battle.activePlayer = elf_halfling;
					assertEquals(monster.getLevel(battle), 15);
					battle.activePlayer = dwarf_halfling;
					assertEquals(monster.getLevel(battle), 15);
					
					battle.activePlayer = human;
					battle.helper = human;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = dwarf;
					assertEquals(monster.getLevel(battle), 15);
					battle.helper = halfling;
					assertEquals(monster.getLevel(battle), 15);
					battle.helper = half_dwarf;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = half_halfling;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = elf_dwarf;
					assertEquals(monster.getLevel(battle), 15);
					battle.helper = elf_halfling;
					assertEquals(monster.getLevel(battle), 15);
					battle.helper = dwarf_halfling;
					assertEquals(monster.getLevel(battle), 15);
					
					assertEquals(monster.getRaceBonus(Race.DWARF), 3);
					assertEquals(monster.getRaceBonus(Race.ELF), 0);
					assertEquals(monster.getRaceBonus(Race.HALFLING), 3);
					assertEquals(monster.getRaceBonus(Race.HUMAN), 0);
						
					testPlayer.addUnequippedItem(hat3);
					assertFalse(testPlayer.equip(hat3).equals(""));
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertFalse(testPlayer.getAllEquipment().contains(hat1));
					assertFalse(testPlayer.getAllEquipment().contains(hat2));
					assertTrue(testPlayer.getAllEquipment().contains(hat3));
				}
				else if (monster.getID() == Card.M_BULLROG) {
					for (int level = 1; level <= 4; level++) {
						assertTrue(monster.isAutoEscape(testPlayer));
						testPlayer.goUpLevel(false);
					}
					assertFalse(monster.isAutoEscape(testPlayer));
					
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.needsNewCards());
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(human.getHandCards().remove(0) != null);
					assertTrue(none.getHandCards().remove(0) != null);
				}
				else if (monster.getID() == Card.M_CRABS) {
					testPlayer.addUnequippedItem(armor3);
					assertFalse(testPlayer.equip(armor3).equals(""));
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 7);
					assertTrue(testPlayer.getAllEquipment().contains(hat1));
					assertTrue(testPlayer.getAllEquipment().contains(hat2));
					assertTrue(testPlayer.getAllEquipment().contains(hand1));
					assertTrue(testPlayer.getAllEquipment().contains(hand2));
					assertTrue(testPlayer.getAllEquipment().contains(hands1));
					assertTrue(testPlayer.getAllEquipment().contains(hands2));
					assertTrue(testPlayer.getAllEquipment().contains(armor3));
				}
				else if (monster.getID() == Card.M_DROOLING_SLIME) {
					assertEquals(monster.getLevel(battle), 1);
					battle.activePlayer = elf;
					assertEquals(monster.getLevel(battle), 5);
					battle.activePlayer = half_elf;
					assertEquals(monster.getLevel(battle), 1);
					battle.activePlayer = elf_dwarf;
					assertEquals(monster.getLevel(battle), 5);
					battle.activePlayer = elf_halfling;
					assertEquals(monster.getLevel(battle), 5);
					
					battle.activePlayer = human;
					battle.helper = human;
					assertEquals(monster.getLevel(battle), 1);
					battle.helper = elf;
					assertEquals(monster.getLevel(battle), 5);
					battle.helper = half_elf;
					assertEquals(monster.getLevel(battle), 1);
					battle.helper = elf_dwarf;
					assertEquals(monster.getLevel(battle), 5);
					battle.helper = elf_halfling;
					assertEquals(monster.getLevel(battle), 5);
					
					assertEquals(monster.getRaceBonus(Race.DWARF), 0);
					assertEquals(monster.getRaceBonus(Race.ELF), 4);
					assertEquals(monster.getRaceBonus(Race.HALFLING), 0);
					assertEquals(monster.getRaceBonus(Race.HUMAN), 0);
					
					testPlayer.addUnequippedItem(shoes3);
					assertFalse(testPlayer.equip(shoes3).equals(""));
					testPlayer.goUpLevels(2, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertFalse(testPlayer.getAllEquipment().contains(shoes1));
					assertFalse(testPlayer.getAllEquipment().contains(shoes2));
					assertTrue(testPlayer.getAllEquipment().contains(shoes3));
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
				}
				else if (monster.getID() == Card.M_FACE_SUCKER) {
					assertEquals(monster.getLevel(battle), 8);
					battle.activePlayer = elf;
					assertEquals(monster.getLevel(battle), 14);
					battle.activePlayer = half_elf;
					assertEquals(monster.getLevel(battle), 8);
					battle.activePlayer = elf_dwarf;
					assertEquals(monster.getLevel(battle), 14);
					battle.activePlayer = elf_halfling;
					assertEquals(monster.getLevel(battle), 14);				
					
					battle.activePlayer = human;
					battle.helper = human;
					assertEquals(monster.getLevel(battle), 8);
					battle.helper = elf;
					assertEquals(monster.getLevel(battle), 14);
					battle.helper = half_elf;
					assertEquals(monster.getLevel(battle), 8);
					battle.helper = elf_dwarf;
					assertEquals(monster.getLevel(battle), 14);
					battle.helper = elf_halfling;
					assertEquals(monster.getLevel(battle), 14);
					
					assertEquals(monster.getRaceBonus(Race.DWARF), 0);
					assertEquals(monster.getRaceBonus(Race.ELF), 6);
					assertEquals(monster.getRaceBonus(Race.HALFLING), 0);
					assertEquals(monster.getRaceBonus(Race.HUMAN), 0);
					
					testPlayer.addUnequippedItem(hat3);
					assertFalse(testPlayer.equip(hat3).equals(""));
					testPlayer.goUpLevels(2, false);
					testPlayer.addLastingCurse(new CurseCard(Card.CU_CHICKEN_ON_YOUR_HEAD, "Chicken on Your Head") {
						public void addEffects(Player player) {}
					});
					assertTrue(testPlayer.hasChickenOnHead());
					monster.doBadStuff(testPlayer);
					assertFalse(testPlayer.hasChickenOnHead());
					assertEquals(testPlayer.getLevel(), 2);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertFalse(testPlayer.getAllEquipment().contains(hat1));
					assertFalse(testPlayer.getAllEquipment().contains(hat2));
					assertTrue(testPlayer.getAllEquipment().contains(hat3));
					testPlayer.addLastingCurse(new CurseCard(Card.CU_CHICKEN_ON_YOUR_HEAD, "Chicken on Your Head") {
						public void addEffects(Player player) {}
					});
					assertTrue(testPlayer.hasChickenOnHead());
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 1);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertFalse(testPlayer.hasChickenOnHead());
				}
				else if (monster.getID() == Card.M_FLOATING_NOSE) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
				}
				else if (monster.getID() == Card.M_FLYING_FROGS) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
				}
				else if (monster.getID() == Card.M_GAZEBO) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
				}
				else if (monster.getID() == Card.M_GELATINOUS_OCTAHEDRON) {
					testPlayer.addUnequippedItem(big1);
					assertEquals(none.equip(big1), "");
					testPlayer.addUnequippedItem(big2);
					testPlayer.addUnequippedItem(big3);
					assertEquals(testPlayer.getAllEquipment().size(), 15);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 12);
					assertFalse(testPlayer.getAllEquipment().contains(big1));
					assertFalse(testPlayer.getAllEquipment().contains(big2));
					assertFalse(testPlayer.getAllEquipment().contains(big3));
				}
				else if (monster.getID() == Card.M_GHOULFIENDS) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 1);
					
					testPlayer.goUpLevels(4, false);
					human.goUpLevels(4, false);
					none.goUpLevels(5, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 5);
					
					testPlayer.goUpLevels(4, false);
					none.goDownLevels(2);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 4);
					human.goDownLevels(9);
					none.goDownLevels(9);
				}
				else if (monster.getID() == Card.M_HARPIES) {
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = wizard;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = warrior_wizard;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = wizard_cleric;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = wizard_thief;
					assertEquals(monster.getLevel(battle), 9);
					
					battle.activePlayer = none;
					battle.helper = none;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = wizard;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = warrior_wizard;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = wizard_cleric;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = wizard_thief;
					assertEquals(monster.getLevel(battle), 9);
					
					assertEquals(monster.getClassBonus(Class.CLERIC), 0);
					assertEquals(monster.getClassBonus(Class.THIEF), 0);
					assertEquals(monster.getClassBonus(Class.WARRIOR), 0);
					assertEquals(monster.getClassBonus(Class.WIZARD), 5);
					
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
				}
				else if (monster.getID() == Card.M_HIPPOGRIFF) {
					for (int level = 1; level <= 3; level++) {
						assertTrue(monster.isAutoEscape(testPlayer));
						testPlayer.goUpLevel(false);
					}
					assertFalse(monster.isAutoEscape(testPlayer));
					
					EquipmentCard good = new EquipmentCard(30, "good", 100, 1, EquipmentType.OTHER, false, false, false);
					EquipmentCard better = new EquipmentCard(31, "better", 200, 2, EquipmentType.OTHER, false, false, false);
					EquipmentCard best = new EquipmentCard(32, "best", 300, 3, EquipmentType.OTHER, false, false, false);
					testPlayer.addUnequippedItem(good);
					testPlayer.equip(good);
					testPlayer.addUnequippedItem(best);
					testPlayer.equip(best);
					testPlayer.addCard(better);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 13);
					assertEquals(testPlayer.getEquippedItems().size(), 13);
					assertEquals(testPlayer.getUnequippedItems().size(), 0);
					assertTrue(testPlayer.getEquippedItems().contains(good));
					assertFalse(testPlayer.getAllItems().contains(best));
					assertFalse(testPlayer.getHandCards().contains(better));
					assertTrue(none.getHandCards().remove(best));
					assertTrue(human.getHandCards().remove(better));
				}
				else if (monster.getID() == Card.M_INSURANCE_SALESMAN) {
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getAllItems().isEmpty());
					EquipmentCard expensive = new EquipmentCard(30, "expensive", 1100, 0, EquipmentType.ARMOR, true, true, true);
					ItemCard bigBonus = new ItemCard(31, "big bonus", 100, 50);
					testPlayer.addUnequippedItem(expensive);
					testPlayer.addItem(bigBonus);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllItems().size(), 1);
					assertTrue(testPlayer.getAllItems().contains(bigBonus));
					assertFalse(testPlayer.getAllItems().contains(expensive));
					EquipmentCard halfEquipment = new EquipmentCard(32, "half EQ", 500, 0, EquipmentType.ARMOR, true, true, true);
					ItemCard halfItem = new ItemCard(33, "Half Item", 500);
					testPlayer.addUnequippedItem(halfEquipment);
					testPlayer.addItem(halfItem);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllItems().size(), 1);
					assertTrue(testPlayer.getAllItems().contains(bigBonus));
					assertFalse(testPlayer.getAllItems().contains(halfItem));
					assertFalse(testPlayer.getAllItems().contains(halfEquipment));
				}
				else if (monster.getID() == Card.M_KING_TUT) {
					for (int level = 1; level <= 3; level++) {
						assertTrue(monster.isAutoEscape(testPlayer));
						testPlayer.goUpLevel(false);
					}
					assertFalse(monster.isAutoEscape(testPlayer));
					
					testPlayer.getHandCards().add(big1);
					testPlayer.getHandCards().add(big2);
					testPlayer.getHandCards().add(big3);
					testPlayer.getCarriedItems().add(new ItemCard(30, "item1", 0));
					testPlayer.getCarriedItems().add(new ItemCard(31, "item2", 0));
					testPlayer.getCarriedItems().add(new ItemCard(32, "item3", 0));
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getAllEquipment().isEmpty());
					assertTrue(testPlayer.getCarriedItems().isEmpty());
					assertTrue(testPlayer.getHandCards().isEmpty());
					assertEquals(testPlayer.getLevel(), 6);
				}
				else if (monster.getID() == Card.M_LAME_GOBLIN) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 4);
				}
				else if (monster.getID() == Card.M_LARGE_ANGRY_CHICKEN) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 4);
				}
				else if (monster.getID() == Card.M_LAWYER) {
					testPlayer.addCard(hat1);
					testPlayer.addCard(hat2);
					testPlayer.addCard(hat3);
					assertTrue(human.getHandCards().isEmpty());
					assertTrue(human.getHandCards().isEmpty());
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getEquippedItems().size(), 12);
					assertTrue(testPlayer.getHandCards().isEmpty());
					assertTrue(human.getHandCards().remove(0) != null);
					assertTrue(none.getHandCards().remove(0) != null);
				}
				else if (monster.getID() == Card.M_LEPERCHAUN) {
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = elf;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = half_elf;
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = elf_dwarf;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = elf_halfling;
					assertEquals(monster.getLevel(battle), 9);
					
					battle.activePlayer = human;
					battle.helper = human;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = elf;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = half_elf;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = elf_dwarf;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = elf_halfling;
					assertEquals(monster.getLevel(battle), 9);
					
					assertEquals(monster.getRaceBonus(Race.DWARF), 0);
					assertEquals(monster.getRaceBonus(Race.ELF), 5);
					assertEquals(monster.getRaceBonus(Race.HALFLING), 0);
					assertEquals(monster.getRaceBonus(Race.HUMAN), 0);
					
					EquipmentCard good = new EquipmentCard(30, "good", 100, 1, EquipmentType.OTHER, false, false, false);
					EquipmentCard better = new EquipmentCard(31, "better", 200, 2, EquipmentType.OTHER, false, false, false);
					EquipmentCard best = new EquipmentCard(32, "best", 300, 3, EquipmentType.OTHER, false, false, false);
					testPlayer.addUnequippedItem(better);
					testPlayer.addUnequippedItem(best);
					testPlayer.equip(best);
					testPlayer.addUnequippedItem(good);
					testPlayer.addCard(best);
					assertTrue(human.getHandCards().isEmpty());
					assertTrue(none.getHandCards().isEmpty());
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 13);
					assertTrue(testPlayer.getUnequippedItems().contains(good));
					assertEquals(testPlayer.getHandCards().size(), 1);
					assertTrue(testPlayer.getHandCards().contains(best));
					assertTrue(human.getHandCards().isEmpty());
					assertTrue(none.getHandCards().isEmpty());
					assertTrue(human.getAllItems().isEmpty());
					assertTrue(none.getAllItems().isEmpty());
				}
				else if (monster.getID() == Card.M_MAUL_RAT) {
					assertEquals(monster.getLevel(battle), 1);
					battle.activePlayer = cleric;
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = warrior_cleric;
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = wizard_cleric;
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = cleric_thief;
					assertEquals(monster.getLevel(battle), 4);
					
					battle.activePlayer = none;
					battle.helper = none;
					assertEquals(monster.getLevel(battle), 1);
					battle.helper = cleric;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = warrior_cleric;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = wizard_cleric;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = cleric_thief;
					assertEquals(monster.getLevel(battle), 4);
					
					assertEquals(monster.getClassBonus(Class.CLERIC), 3);
					assertEquals(monster.getClassBonus(Class.THIEF), 0);
					assertEquals(monster.getClassBonus(Class.WARRIOR), 0);
					assertEquals(monster.getClassBonus(Class.WIZARD), 0);
					
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 4);
				}
				else if (monster.getID() == Card.M_MR_BONES) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
				}
				else if (monster.getID() == Card.M_NET_TROLL) {
					assertEquals(human.getLevel(), 1);
					assertEquals(none.getLevel(), 1);
					
					EquipmentCard good = new EquipmentCard(30, "good", 100, 1, EquipmentType.OTHER, false, false, false);
					EquipmentCard better = new EquipmentCard(31, "better", 200, 2, EquipmentType.OTHER, false, false, false);
					EquipmentCard best = new EquipmentCard(32, "best", 300, 3, EquipmentType.OTHER, false, false, false);
					testPlayer.addUnequippedItem(better);
					testPlayer.equip(better);
					testPlayer.addUnequippedItem(best);
					testPlayer.equip(best);
					testPlayer.addUnequippedItem(good);
					testPlayer.equip(good);
					testPlayer.addCard(best);
					testPlayer.goUpLevel(false);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getHandCards().contains(best));
					assertEquals(testPlayer.getEquippedItems().size(), 15);
					assertTrue(testPlayer.getEquippedItems().contains(good));
					assertTrue(testPlayer.getEquippedItems().contains(better));
					assertTrue(testPlayer.getEquippedItems().contains(best));
					human.goUpLevel(false);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getHandCards().contains(best));
					assertEquals(testPlayer.getEquippedItems().size(), 14);
					assertTrue(testPlayer.getEquippedItems().contains(good));
					assertTrue(testPlayer.getEquippedItems().contains(better));
					assertFalse(testPlayer.getEquippedItems().contains(best));
					assertTrue(human.getEquippedItems().remove(best));
					testPlayer.addUnequippedItem(best);
					testPlayer.equip(best);
					none.goUpLevels(2, false);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getHandCards().contains(best));
					assertEquals(testPlayer.getEquippedItems().size(), 14);
					assertTrue(testPlayer.getEquippedItems().contains(good));
					assertTrue(testPlayer.getEquippedItems().contains(better));
					assertFalse(testPlayer.getEquippedItems().contains(best));
					assertTrue(none.getEquippedItems().remove(best));
					testPlayer.addUnequippedItem(best);
					testPlayer.equip(best);
					human.goUpLevel(false);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getHandCards().contains(best));
					assertEquals(testPlayer.getEquippedItems().size(), 13);
					assertTrue(testPlayer.getEquippedItems().contains(good));
					assertFalse(testPlayer.getEquippedItems().contains(better));
					assertFalse(testPlayer.getEquippedItems().contains(best));
					assertTrue(none.getEquippedItems().contains(best) || none.getEquippedItems().contains(better));
					assertTrue(human.getEquippedItems().contains(best) || human.getEquippedItems().contains(better));
					assertTrue(none.getEquippedItems().remove(best) || human.getEquippedItems().remove(best));
					assertTrue(none.getEquippedItems().remove(better) || human.getEquippedItems().remove(better));
					human.goDownLevels(10);
					none.goDownLevels(10);
				}
				else if (monster.getID() == Card.M_PIT_BULL) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
				}
				else if (monster.getID() == Card.M_PLATYCORE) {
					assertEquals(monster.getLevel(battle), 6);
					battle.activePlayer = wizard;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = warrior_wizard;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = wizard_cleric;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = wizard_thief;
					assertEquals(monster.getLevel(battle), 12);
					
					battle.activePlayer = none;
					battle.helper = none;
					assertEquals(monster.getLevel(battle), 6);
					battle.helper = wizard;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = warrior_wizard;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = wizard_cleric;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = wizard_thief;
					assertEquals(monster.getLevel(battle), 12);
					
					assertEquals(monster.getClassBonus(Class.CLERIC), 0);
					assertEquals(monster.getClassBonus(Class.THIEF), 0);
					assertEquals(monster.getClassBonus(Class.WARRIOR), 0);
					assertEquals(monster.getClassBonus(Class.WIZARD), 6);
					
					testPlayer.goUpLevels(3, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
					testPlayer.goUpLevels(2, false);
					testPlayer.addCard(hat1);
					testPlayer.addCard(armor1);
					testPlayer.addCard(shoes1);
					testPlayer.addCard(other1);
					assertEquals(testPlayer.getHandCards().size(), 4);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 4);
					assertTrue(testPlayer.getHandCards().isEmpty());
					testPlayer.goDownLevels(3);
					testPlayer.addCard(loadedDie);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getHandCards().size(), 1);
					assertTrue(testPlayer.getHandCards().contains(loadedDie));
				}
				else if (monster.getID() == Card.M_PLUTONIUM_DRAGON) {
					for (int level = 1; level <= 5; level++) {
						assertTrue(monster.isAutoEscape(testPlayer));
						testPlayer.goUpLevel(false);
					}
					assertFalse(monster.isAutoEscape(testPlayer));
					
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.needsNewCards());
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(human.getHandCards().remove(0) != null);
					assertTrue(none.getHandCards().remove(0) != null);
				}
				else if (monster.getID() == Card.M_POTTED_PLANT) {
					assertTrue(monster.isAutoEscape(testPlayer));
				}
				else if (monster.getID() == Card.M_SHRIEKING_GEEK) {
					assertEquals(monster.getLevel(battle), 6);
					battle.activePlayer = warrior;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = warrior_wizard;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = warrior_cleric;
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = warrior_thief;
					assertEquals(monster.getLevel(battle), 12);
					
					battle.activePlayer = none;
					battle.helper = none;
					assertEquals(monster.getLevel(battle), 6);
					battle.helper = warrior;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = warrior_wizard;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = warrior_cleric;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = warrior_thief;
					assertEquals(monster.getLevel(battle), 12);
					
					assertEquals(monster.getClassBonus(Class.CLERIC), 0);
					assertEquals(monster.getClassBonus(Class.THIEF), 0);
					assertEquals(monster.getClassBonus(Class.WARRIOR), 6);
					assertEquals(monster.getClassBonus(Class.WIZARD), 0);
					
					testPlayer.getRaceCards().add(new RaceCard(30, "Elf", Race.ELF));
					testPlayer.setHalfBreedCard(half);
					testPlayer.getRaceCards().add(new RaceCard(31, "Halfling", Race.HALFLING));
					testPlayer.getClassCards().add(new ClassCard(32, "Wizard", Class.WIZARD));
					testPlayer.setSuperMunchkinCard(munchkin);
					testPlayer.getClassCards().add(new ClassCard(33, "Cleric", Class.CLERIC));
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getRaceCards().isEmpty());
					assertFalse(testPlayer.isHalfBreed());
					assertTrue(testPlayer.getClassCards().isEmpty());
					assertFalse(testPlayer.isSuperMunchkin());
				}
				else if (monster.getID() == Card.M_SNAILS_OF_SPEED) {
					ItemCard one = new ItemCard(30, "one", 100);
					ItemCard two = new ItemCard(31, "two", 200);
					ItemCard three = new ItemCard(32, "three", 300);
					ItemCard four = new ItemCard(33, "four", 400);
					ItemCard five = new ItemCard(34, "five", 500);
					ItemCard six = new ItemCard(35, "six", 600);
					ItemCard bigValue = new ItemCard(36, "big value", 2000);
					testPlayer.getEquippedItems().clear();
					testPlayer.addCard(bigValue);
					testPlayer.addItem(loadedDie);
					while (testPlayer.getCarriedItems().contains(loadedDie)) {
						testPlayer.addUnequippedItem(hat2);
						monster.doBadStuff(testPlayer);
					}
					assertEquals(testPlayer.getHandCards().size(), 1);
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(testPlayer.getHandCards().contains(bigValue));
					testPlayer.addItem(loadedDie);
					while (testPlayer.getCarriedItems().contains(loadedDie)) {
						testPlayer.addUnequippedItem(other1);
						testPlayer.equip(other1);
						monster.doBadStuff(testPlayer);
					}
					assertEquals(testPlayer.getHandCards().size(), 1);
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(testPlayer.getHandCards().contains(bigValue));
					testPlayer.addItem(loadedDie);
					while (testPlayer.getCarriedItems().contains(loadedDie)) {
						testPlayer.addItem(one);
						monster.doBadStuff(testPlayer);
					}
					assertEquals(testPlayer.getHandCards().size(), 1);
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(testPlayer.getHandCards().contains(bigValue));
					testPlayer.addCard(one);
					testPlayer.addCard(two);
					testPlayer.addCard(three);
					testPlayer.addCard(four);
					testPlayer.addCard(five);
					testPlayer.addCard(six);
					monster.doBadStuff(testPlayer);
					int numHandCards = testPlayer.getHandCards().size();
					assertTrue(numHandCards >= 1 && numHandCards <= 6);
					assertTrue(testPlayer.getHandCards().contains(bigValue));
					assertFalse(testPlayer.getHandCards().contains(one));
					assertTrue((numHandCards == 6) == testPlayer.getHandCards().contains(two));
					assertTrue((numHandCards >= 5) == testPlayer.getHandCards().contains(three));
					assertTrue((numHandCards >= 4) == testPlayer.getHandCards().contains(four));
					assertTrue((numHandCards >= 3) == testPlayer.getHandCards().contains(five));
					assertTrue((numHandCards >= 2) == testPlayer.getHandCards().contains(six));
				}
				else if (monster.getID() == Card.M_SQUIDZILLA) {
					for (int level = 1; level <= 4; level++) {
						assertTrue(monster.isAutoEscape(testPlayer));
						testPlayer.goUpLevel(false);
					}
					assertFalse(monster.isAutoEscape(testPlayer));
					
					assertFalse(monster.isAutoEscape(elf));
					assertTrue(monster.isAutoEscape(half_elf));
					assertFalse(monster.isAutoEscape(elf_dwarf));
					assertFalse(monster.isAutoEscape(elf_halfling));
					
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.needsNewCards());
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(human.getHandCards().remove(0) != null);
					assertTrue(none.getHandCards().remove(0) != null);
				}
				else if (monster.getID() == Card.M_STONE_GOLEM) {
					assertTrue(monster.isAutoEscape(testPlayer));
					assertFalse(monster.isAutoEscape(halfling));
					assertTrue(monster.isAutoEscape(half_halfling));
					assertFalse(monster.isAutoEscape(elf_halfling));
					assertFalse(monster.isAutoEscape(dwarf_halfling));
					
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.needsNewCards());
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(human.getHandCards().remove(0) != null);
					assertTrue(none.getHandCards().remove(0) != null);
				}
				else if (monster.getID() == Card.M_THE_NOTHING) {
					testPlayer.getHandCards().add(big1);
					testPlayer.getHandCards().add(big2);
					testPlayer.getHandCards().add(big3);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getHandCards().isEmpty());
					assertEquals(testPlayer.getAllEquipment().size(), 12);
				}
				else if (monster.getID() == Card.M_TONGUE_DEMON) {
					assertEquals(monster.getLevel(battle), 12);
					battle.activePlayer = cleric;
					assertEquals(monster.getLevel(battle), 16);
					battle.activePlayer = warrior_cleric;
					assertEquals(monster.getLevel(battle), 16);
					battle.activePlayer = wizard_cleric;
					assertEquals(monster.getLevel(battle), 16);
					battle.activePlayer = cleric_thief;
					assertEquals(monster.getLevel(battle), 16);
					
					battle.activePlayer = none;
					battle.helper = none;
					assertEquals(monster.getLevel(battle), 12);
					battle.helper = cleric;
					assertEquals(monster.getLevel(battle), 16);
					battle.helper = warrior_cleric;
					assertEquals(monster.getLevel(battle), 16);
					battle.helper = wizard_cleric;
					assertEquals(monster.getLevel(battle), 16);
					battle.helper = cleric_thief;
					assertEquals(monster.getLevel(battle), 16);
					
					assertEquals(monster.getClassBonus(Class.CLERIC), 4);
					assertEquals(monster.getClassBonus(Class.THIEF), 0);
					assertEquals(monster.getClassBonus(Class.WARRIOR), 0);
					assertEquals(monster.getClassBonus(Class.WIZARD), 0);
					
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);					
					elf.goUpLevels(4, false);
					half_elf.goUpLevels(4, false);
					elf_dwarf.goUpLevels(4, false);
					elf_halfling.goUpLevels(4, false);
					monster.doBadStuff(elf);
					monster.doBadStuff(half_elf);
					monster.doBadStuff(elf_dwarf);
					monster.doBadStuff(elf_halfling);
					assertEquals(elf.getLevel(), 2);
					assertEquals(half_elf.getLevel(), 3);
					assertEquals(elf_dwarf.getLevel(), 2);
					assertEquals(elf_halfling.getLevel(), 2);
					elf.goDownLevels(4);
					half_elf.goDownLevels(4);
					elf_dwarf.goDownLevels(4);
					elf_halfling.goDownLevels(4);
				}
				else if (monster.getID() == Card.M_UNDEAD_HORSE) {
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = dwarf;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = half_dwarf;
					assertEquals(monster.getLevel(battle), 4);
					battle.activePlayer = elf_dwarf;
					assertEquals(monster.getLevel(battle), 9);
					battle.activePlayer = dwarf_halfling;
					assertEquals(monster.getLevel(battle), 9);
					
					battle.activePlayer = human;
					battle.helper = human;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = dwarf;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = half_dwarf;
					assertEquals(monster.getLevel(battle), 4);
					battle.helper = elf_dwarf;
					assertEquals(monster.getLevel(battle), 9);
					battle.helper = dwarf_halfling;
					assertEquals(monster.getLevel(battle), 9);
					
					assertEquals(monster.getRaceBonus(Race.DWARF), 5);
					assertEquals(monster.getRaceBonus(Race.ELF), 0);
					assertEquals(monster.getRaceBonus(Race.HALFLING), 0);
					assertEquals(monster.getRaceBonus(Race.HUMAN), 0);
					
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
				}
				else if (monster.getID() == Card.M_UNSPEAKABLY_AWFUL_INDESCRIBABLE_HORROR) {
					assertEquals(monster.getLevel(battle), 14);
					battle.activePlayer = warrior;
					assertEquals(monster.getLevel(battle), 18);
					battle.activePlayer = warrior_wizard;
					assertEquals(monster.getLevel(battle), 18);
					battle.activePlayer = warrior_cleric;
					assertEquals(monster.getLevel(battle), 18);
					battle.activePlayer = warrior_thief;
					assertEquals(monster.getLevel(battle), 18);
					
					battle.activePlayer = none;
					battle.helper = none;
					assertEquals(monster.getLevel(battle), 14);
					battle.helper = warrior;
					assertEquals(monster.getLevel(battle), 18);
					battle.helper = warrior_wizard;
					assertEquals(monster.getLevel(battle), 18);
					battle.helper = warrior_cleric;
					assertEquals(monster.getLevel(battle), 18);
					battle.helper = warrior_thief;
					assertEquals(monster.getLevel(battle), 18);
					
					assertEquals(monster.getClassBonus(Class.CLERIC), 0);
					assertEquals(monster.getClassBonus(Class.THIEF), 0);
					assertEquals(monster.getClassBonus(Class.WARRIOR), 4);
					assertEquals(monster.getClassBonus(Class.WIZARD), 0);
					
					testPlayer.addClassCard(new ClassCard(30, "Wizard", Class.WIZARD));
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.getClassCards().isEmpty());
					testPlayer.addClassCard(new ClassCard(31, "Wizard", Class.WIZARD));
					testPlayer.setSuperMunchkinCard(munchkin);
					testPlayer.addClassCard(new ClassCard(32, "Warrior", Class.WARRIOR));
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getClassCards().size(), 1);
					assertTrue(testPlayer.isWarrior());
					assertFalse(testPlayer.isSuperMunchkin());
					assertEquals(testPlayer.getAllEquipment().size(), 12);
					assertFalse(testPlayer.needsNewCards());
					monster.doBadStuff(testPlayer);
					monster.doBadStuff(testPlayer);
					assertTrue(testPlayer.needsNewCards());
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(human.getHandCards().remove(0) != null);
					assertTrue(none.getHandCards().remove(0) != null);
				}
				else if (monster.getID() == Card.M_WANNABE_VAMPIRE) {
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
				}
				else if (monster.getID() == Card.M_WIGHT_BROTHERS) {
					for (int level = 1; level <= 3; level++) {
						assertTrue(monster.isAutoEscape(testPlayer));
						testPlayer.goUpLevel(false);
					}
					assertFalse(monster.isAutoEscape(testPlayer));
					
					testPlayer.goUpLevels(4, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 1);
					testPlayer.goUpLevels(8, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 1);
					testPlayer.goUpLevels(1, false);
					monster.doBadStuff(testPlayer);
					assertEquals(testPlayer.getLevel(), 1);
				}
				else {
					fail("Invalid Monster Card: " + monster + "(" + monster.getID() + ")");
					continue;
				}
				
				monsterCount++;				
			}
		}
		
		assertEquals(monsterCount, 37);
	}
	
	private void testCurseCards() {
		int curseCount = 0;
		for (Card card : deck) {
			if (card instanceof CurseCard) {
				Player testPlayer = new Player(new MockGUI(0), "player", true, PlayerType.TEST);
				GM.getPlayers().set(0, testPlayer);
				GM.setActivePlayer(testPlayer);
				
				CurseCard curse = (CurseCard)card;
				if (curse.getID() == Card.CU_CHANGE_CLASS) {
					GM.getDoorDeck().discardPile.clear();
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.isEmpty());
					ClassCard clericCard = cleric.getClassCards().getFirst();
					ClassCard thiefCard = thief.getClassCards().getFirst();
					ClassCard warriorCard = warrior.getClassCards().getFirst();
					ClassCard wizardCard = wizard.getClassCards().getFirst();
					testPlayer.addClassCard(clericCard);
					assertTrue(testPlayer.isCleric());
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.peek() == clericCard);
					assertFalse(testPlayer.isCleric());
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.peek() == clericCard);
					assertFalse(testPlayer.isCleric());
					testPlayer.addClassCard(thiefCard);
					assertTrue(testPlayer.isThief());
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.pop() == thiefCard);
					assertFalse(testPlayer.isThief());
					assertTrue(testPlayer.isCleric());
					testPlayer.setSuperMunchkinCard(munchkin);
					testPlayer.addClassCard(warriorCard);
					assertTrue(testPlayer.isWarrior());
					assertTrue(testPlayer.isSuperMunchkin());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isCleric());
					assertFalse(testPlayer.isWarrior());
					assertFalse(testPlayer.isThief());
					assertFalse(testPlayer.isSuperMunchkin());
					assertEquals(GM.getDoorDeck().discardPile.size(), 3);
					assertTrue(GM.getDoorDeck().discardPile.contains(clericCard));
					assertTrue(GM.getDoorDeck().discardPile.contains(warriorCard));
					assertTrue(GM.getDoorDeck().discardPile.contains(munchkin));
					GM.getDoorDeck().discardPile.push(wizardCard);
					testPlayer.addClassCard(warriorCard);
					testPlayer.setSuperMunchkinCard(munchkin);
					testPlayer.addClassCard(thiefCard);
					curse.addEffects(testPlayer);
					assertEquals(GM.getDoorDeck().discardPile.size(), 6);
					assertFalse(GM.getDoorDeck().discardPile.contains(wizardCard));
					assertFalse(testPlayer.isCleric());
					assertFalse(testPlayer.isWarrior());
					assertFalse(testPlayer.isThief());
					assertTrue(testPlayer.isWizard());
					assertFalse(testPlayer.isSuperMunchkin());
				}
				else if (curse.getID() == Card.CU_CHANGE_RACE) {
					GM.getDoorDeck().discardPile.clear();
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.isEmpty());
					RaceCard dwarfCard = dwarf.getRaceCards().getFirst();
					RaceCard elfCard = elf.getRaceCards().getFirst();
					RaceCard halflingCard = halfling.getRaceCards().getFirst();
					testPlayer.addRaceCard(dwarfCard);
					assertTrue(testPlayer.isDwarf());
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.peek() == dwarfCard);
					assertFalse(testPlayer.isDwarf());
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.peek() == dwarfCard);
					assertFalse(testPlayer.isDwarf());
					testPlayer.addRaceCard(elfCard);
					assertTrue(testPlayer.isElf());
					curse.addEffects(testPlayer);
					assertTrue(GM.getDoorDeck().discardPile.pop() == elfCard);
					assertFalse(testPlayer.isElf());
					assertTrue(testPlayer.isDwarf());
					testPlayer.setHalfBreedCard(half);
					testPlayer.addRaceCard(halflingCard);
					assertTrue(testPlayer.isHalfling());
					assertTrue(testPlayer.isHalfBreed());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isDwarf());
					assertFalse(testPlayer.isElf());
					assertFalse(testPlayer.isHalfling());
					assertFalse(testPlayer.isHalfBreed());
					assertEquals(GM.getDoorDeck().discardPile.size(), 3);
					assertTrue(GM.getDoorDeck().discardPile.contains(dwarfCard));
					assertTrue(GM.getDoorDeck().discardPile.contains(halflingCard));
					assertTrue(GM.getDoorDeck().discardPile.contains(half));
					GM.getDoorDeck().discardPile.push(elfCard);
					testPlayer.addRaceCard(dwarfCard);
					testPlayer.setHalfBreedCard(half);
					testPlayer.addRaceCard(halflingCard);
					curse.addEffects(testPlayer);
					assertEquals(GM.getDoorDeck().discardPile.size(), 6);
					assertFalse(GM.getDoorDeck().discardPile.contains(elfCard));
					assertFalse(testPlayer.isDwarf());
					assertFalse(testPlayer.isHalfling());
					assertTrue(testPlayer.isElf());
					assertFalse(testPlayer.isHalfBreed());
				}
				else if (curse.getID() == Card.CU_CHANGE_SEX) {
					assertTrue(curse.isLastingCurse());
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.hasChangeSexCurse());
					assertTrue(testPlayer.hasDistractionCurse());
					assertFalse(testPlayer.isMale());
					assertTrue(testPlayer.isFemale());
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.isMale());
					assertFalse(testPlayer.isFemale());
					testPlayer.addUnequippedItem(maleOnly);
					testPlayer.addUnequippedItem(femaleOnly);
					assertEquals(testPlayer.equip(maleOnly), "");
					assertTrue(testPlayer.hasEquipped(maleOnly));
					assertFalse(testPlayer.hasEquipped(femaleOnly));
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isMale());
					assertTrue(testPlayer.isFemale());
					assertFalse(testPlayer.hasEquipped(maleOnly));
					assertFalse(testPlayer.hasEquipped(femaleOnly));
					assertEquals(testPlayer.equip(femaleOnly), "");
					assertFalse(testPlayer.hasEquipped(maleOnly));
					assertTrue(testPlayer.hasEquipped(femaleOnly));
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.isMale());
					assertFalse(testPlayer.isFemale());
					assertFalse(testPlayer.hasEquipped(maleOnly));
					assertFalse(testPlayer.hasEquipped(femaleOnly));
					testPlayer.getUnequippedItems().remove(maleOnly);
					testPlayer.getUnequippedItems().remove(femaleOnly);
				}
				else if (curse.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD) {
					assertTrue(curse.isLastingCurse());
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.hasChickenOnHead());
				}
				else if (curse.getID() == Card.CU_DUCK_OF_DOOM) {
					testPlayer.goUpLevels(4, false);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
				}
				else if (curse.getID() == Card.CU_INCOME_TAX) {
					testPlayer.addUnequippedItem(hat1);
					testPlayer.equip(hat1);
					testPlayer.addUnequippedItem(hat2);
					testPlayer.addUnequippedItem(armor1);
					testPlayer.equip(armor1);
					testPlayer.addUnequippedItem(armor2);
					testPlayer.addUnequippedItem(shoes1);
					testPlayer.equip(shoes1);
					testPlayer.addUnequippedItem(other1);
					testPlayer.equip(other1);
					
					testPlayer.goUpLevels(2, false);
					human.goUpLevels(2, false);
					none.goUpLevels(2, false);
					ItemCard better = new ItemCard(30, "better", 100);
					ItemCard best = new ItemCard(31, "best", 200);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
					assertEquals(human.getLevel(), 2);
					assertEquals(none.getLevel(), 2);
					assertEquals(testPlayer.getAllItems().size(), 5);
					human.addItem(best);
					none.addItem(better);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getLevel(), 3);
					assertEquals(human.getLevel(), 2);
					assertEquals(none.getLevel(), 2);
					assertEquals(testPlayer.getAllItems().size(), 4);
					assertTrue(human.getCarriedItems().isEmpty());
					assertTrue(none.getCarriedItems().isEmpty());
					human.addItem(best);
					none.addItem(better);
					curse.addEffects(none);
					assertEquals(testPlayer.getLevel(), 2);
					assertEquals(human.getLevel(), 2);
					assertEquals(none.getLevel(), 2);
					assertEquals(testPlayer.getAllItems().size(), 0);
					assertTrue(human.getCarriedItems().isEmpty());
					assertTrue(none.getCarriedItems().isEmpty());
					human.addItem(best);
					none.addItem(better);
					testPlayer.addItem(better);
					curse.addEffects(human);
					assertEquals(testPlayer.getLevel(), 1);
					assertEquals(human.getLevel(), 2);
					assertEquals(none.getLevel(), 1);
					assertTrue(testPlayer.getAllItems().isEmpty());
					assertTrue(human.getCarriedItems().isEmpty());
					assertTrue(none.getCarriedItems().isEmpty());
					human.goDownLevel();
				}
				else if (curse.getID() == Card.CU_LOSE_1_BIG_ITEM) {
					testPlayer.addUnequippedItem(hat1);
					testPlayer.equip(hat1);
					testPlayer.addUnequippedItem(hat2);
					testPlayer.addUnequippedItem(armor1);
					testPlayer.equip(armor1);
					testPlayer.addUnequippedItem(armor2);
					testPlayer.addUnequippedItem(hand1);
					testPlayer.equip(hand1);
					testPlayer.addUnequippedItem(hand2);
					testPlayer.equip(hand2);
					testPlayer.addUnequippedItem(hands1);
					testPlayer.addUnequippedItem(hands2);
					testPlayer.addUnequippedItem(shoes1);
					testPlayer.equip(shoes1);
					testPlayer.addUnequippedItem(shoes2);
					testPlayer.addUnequippedItem(other1);
					testPlayer.equip(other1);
					testPlayer.addUnequippedItem(other2);
					testPlayer.equip(other2);
					
					EquipmentCard bigHand = new EquipmentCard(30, "big hand", 0, 0, EquipmentType.ARMOR, true, false, false);
					
					testPlayer.addCard(bigHand);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 12);
					assertTrue(testPlayer.getHandCards().contains(bigHand));
					testPlayer.addUnequippedItem(big1);
					testPlayer.addUnequippedItem(big2);
					testPlayer.addUnequippedItem(big3);
					testPlayer.equip(big3);
					assertTrue(testPlayer.getAllEquipment().contains(big1));
					assertTrue(testPlayer.getAllEquipment().contains(big2));
					assertTrue(testPlayer.getAllEquipment().contains(big3));
					assertEquals(testPlayer.getAllEquipment().size(), 15);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 14);
					assertTrue(testPlayer.getHandCards().contains(bigHand));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 13);
					assertTrue(testPlayer.getHandCards().contains(bigHand));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 12);
					assertTrue(testPlayer.getHandCards().contains(bigHand));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 12);
					assertTrue(testPlayer.getHandCards().contains(bigHand));
					assertFalse(testPlayer.getAllEquipment().contains(big1));
					assertFalse(testPlayer.getAllEquipment().contains(big2));
					assertFalse(testPlayer.getAllEquipment().contains(big3));
				}
				else if (curse.getID() == Card.CU_LOSE_1_LEVEL_1 || curse.getID() == Card.CU_LOSE_1_LEVEL_2) {
					testPlayer.goUpLevels(4, false);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getLevel(), 4);
				}
				else if (curse.getID() == Card.CU_LOSE_1_SMALL_ITEM_1 || curse.getID() == Card.CU_LOSE_1_SMALL_ITEM_2) {
					testPlayer.addUnequippedItem(hat1);
					testPlayer.equip(hat1);
					testPlayer.addUnequippedItem(hat2);
					testPlayer.addUnequippedItem(armor1);
					testPlayer.equip(armor1);
					testPlayer.addUnequippedItem(armor2);
					testPlayer.addUnequippedItem(hand1);
					testPlayer.equip(hand1);
					testPlayer.addUnequippedItem(hand2);
					testPlayer.equip(hand2);
					testPlayer.addUnequippedItem(hands1);
					testPlayer.addUnequippedItem(hands2);
					testPlayer.addUnequippedItem(shoes1);
					testPlayer.equip(shoes1);
					testPlayer.addUnequippedItem(shoes2);
					testPlayer.addUnequippedItem(other1);
					testPlayer.equip(other1);
					testPlayer.addUnequippedItem(other2);
					testPlayer.equip(other2);
					
					ItemCard handItem = new ItemCard(30, "hand item", 0);					
					testPlayer.addCard(handItem);
					testPlayer.addItem(loadedDie);
					testPlayer.addUnequippedItem(big1);
					testPlayer.addUnequippedItem(big2);
					testPlayer.addUnequippedItem(big3);
					testPlayer.equip(big3);
					for (int count = 16; count >= 3; count--) {
						assertEquals(testPlayer.getAllItems().size(), count);
						curse.addEffects(testPlayer);
					}
					curse.addEffects(testPlayer);
					curse.addEffects(testPlayer);
					curse.addEffects(testPlayer);
					
					assertEquals(testPlayer.getAllItems().size(), 3);
					assertTrue(testPlayer.getUnequippedItems().contains(big1));
					assertTrue(testPlayer.getUnequippedItems().contains(big2));
					assertTrue(testPlayer.getEquippedItems().contains(big3));
					assertTrue(testPlayer.getHandCards().contains(handItem));
				}
				else if (curse.getID() == Card.CU_LOSE_THE_ARMOR_YOU_ARE_WEARING) {
					testPlayer.addUnequippedItem(hat1);
					testPlayer.equip(hat1);
					testPlayer.addUnequippedItem(hat2);
					testPlayer.addUnequippedItem(armor1);
					testPlayer.equip(armor1);
					testPlayer.addUnequippedItem(armor2);
					testPlayer.addUnequippedItem(hand1);
					testPlayer.equip(hand1);
					testPlayer.addUnequippedItem(hand2);
					testPlayer.equip(hand2);
					testPlayer.addUnequippedItem(hands1);
					testPlayer.addUnequippedItem(hands2);
					testPlayer.addUnequippedItem(shoes1);
					testPlayer.equip(shoes1);
					testPlayer.addUnequippedItem(shoes2);
					testPlayer.addUnequippedItem(other1);
					testPlayer.equip(other1);
					testPlayer.addUnequippedItem(other2);
					testPlayer.equip(other2);
					
					assertFalse(testPlayer.equip(armor2).equals(""));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertTrue(testPlayer.getAllEquipment().contains(hat1));
					assertTrue(testPlayer.getAllEquipment().contains(hat2));
					assertTrue(testPlayer.getAllEquipment().contains(hand1));
					assertTrue(testPlayer.getAllEquipment().contains(hand2));
					assertTrue(testPlayer.getAllEquipment().contains(hands1));
					assertTrue(testPlayer.getAllEquipment().contains(hands2));
					assertTrue(testPlayer.getAllEquipment().contains(armor2));
					assertTrue(testPlayer.getAllEquipment().contains(shoes1));
					assertTrue(testPlayer.getAllEquipment().contains(shoes2));
					assertTrue(testPlayer.getAllEquipment().contains(other1));
					assertTrue(testPlayer.getAllEquipment().contains(other2));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
				}
				else if (curse.getID() == Card.CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING) {
					testPlayer.addUnequippedItem(hat1);
					testPlayer.equip(hat1);
					testPlayer.addUnequippedItem(hat2);
					testPlayer.addUnequippedItem(armor1);
					testPlayer.equip(armor1);
					testPlayer.addUnequippedItem(armor2);
					testPlayer.addUnequippedItem(hand1);
					testPlayer.equip(hand1);
					testPlayer.addUnequippedItem(hand2);
					testPlayer.equip(hand2);
					testPlayer.addUnequippedItem(hands1);
					testPlayer.addUnequippedItem(hands2);
					testPlayer.addUnequippedItem(shoes1);
					testPlayer.equip(shoes1);
					testPlayer.addUnequippedItem(shoes2);
					testPlayer.addUnequippedItem(other1);
					testPlayer.equip(other1);
					testPlayer.addUnequippedItem(other2);
					testPlayer.equip(other2);
					
					assertFalse(testPlayer.equip(shoes2).equals(""));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertTrue(testPlayer.getAllEquipment().contains(hat1));
					assertTrue(testPlayer.getAllEquipment().contains(hat2));
					assertTrue(testPlayer.getAllEquipment().contains(hand1));
					assertTrue(testPlayer.getAllEquipment().contains(hand2));
					assertTrue(testPlayer.getAllEquipment().contains(hands1));
					assertTrue(testPlayer.getAllEquipment().contains(hands2));
					assertTrue(testPlayer.getAllEquipment().contains(armor1));
					assertTrue(testPlayer.getAllEquipment().contains(armor2));
					assertTrue(testPlayer.getAllEquipment().contains(shoes2));
					assertTrue(testPlayer.getAllEquipment().contains(other1));
					assertTrue(testPlayer.getAllEquipment().contains(other2));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
				}
				else if (curse.getID() == Card.CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING) {
					testPlayer.addUnequippedItem(hat1);
					testPlayer.equip(hat1);
					testPlayer.addUnequippedItem(hat2);
					testPlayer.addUnequippedItem(armor1);
					testPlayer.equip(armor1);
					testPlayer.addUnequippedItem(armor2);
					testPlayer.addUnequippedItem(hand1);
					testPlayer.equip(hand1);
					testPlayer.addUnequippedItem(hand2);
					testPlayer.equip(hand2);
					testPlayer.addUnequippedItem(hands1);
					testPlayer.addUnequippedItem(hands2);
					testPlayer.addUnequippedItem(shoes1);
					testPlayer.equip(shoes1);
					testPlayer.addUnequippedItem(shoes2);
					testPlayer.addUnequippedItem(other1);
					testPlayer.equip(other1);
					testPlayer.addUnequippedItem(other2);
					testPlayer.equip(other2);
					
					assertFalse(testPlayer.equip(hat2).equals(""));
					testPlayer.addLastingCurse(new CurseCard(Card.CU_CHICKEN_ON_YOUR_HEAD, "Chicken on Your Head") {
						public void addEffects(Player player) {}					
					});
					assertTrue(testPlayer.hasChickenOnHead());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.hasChickenOnHead());
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertTrue(testPlayer.getAllEquipment().contains(hat2));
					assertTrue(testPlayer.getAllEquipment().contains(hand1));
					assertTrue(testPlayer.getAllEquipment().contains(hand2));
					assertTrue(testPlayer.getAllEquipment().contains(hands1));
					assertTrue(testPlayer.getAllEquipment().contains(hands2));
					assertTrue(testPlayer.getAllEquipment().contains(armor1));
					assertTrue(testPlayer.getAllEquipment().contains(armor2));
					assertTrue(testPlayer.getAllEquipment().contains(shoes1));
					assertTrue(testPlayer.getAllEquipment().contains(shoes2));
					assertTrue(testPlayer.getAllEquipment().contains(other1));
					assertTrue(testPlayer.getAllEquipment().contains(other2));
					testPlayer.addLastingCurse(new CurseCard(Card.CU_CHICKEN_ON_YOUR_HEAD, "Chicken on Your Head") {
						public void addEffects(Player player) {}					
					});
					assertTrue(testPlayer.hasChickenOnHead());
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllEquipment().size(), 11);
					assertFalse(testPlayer.hasChickenOnHead());
				}
				else if (curse.getID() == Card.CU_LOSE_TWO_CARDS) {
					ItemCard one = new ItemCard(31, "one", 100);
					ItemCard two = new ItemCard(32, "two", 100);
					ItemCard three = new ItemCard(33, "three", 100);
					ItemCard four = new ItemCard(34, "four", 100);
					ItemCard five = new ItemCard(35, "five", 100);
					testPlayer.addCard(one);
					testPlayer.addCard(two);
					testPlayer.addCard(three);
					testPlayer.addCard(four);
					testPlayer.addCard(five);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getHandCards().size(), 3);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getHandCards().size(), 1);
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.getHandCards().isEmpty());
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.getHandCards().isEmpty());
					assertEquals(human.getHandCards().size(), 3);
					assertEquals(none.getHandCards().size(), 2);
				}
				else if (curse.getID() == Card.CU_LOSE_YOUR_CLASS) {
					testPlayer.goUpLevels(2, false);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getLevel(), 2);
					ClassCard clericCard = cleric.getClassCards().getFirst();
					ClassCard thiefCard = thief.getClassCards().getFirst();
					ClassCard warriorCard = warrior.getClassCards().getFirst();
					ClassCard wizardCard = wizard.getClassCards().getFirst();
					testPlayer.addClassCard(clericCard);
					assertTrue(testPlayer.isCleric());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isCleric());
					testPlayer.addClassCard(thiefCard);
					assertTrue(testPlayer.isThief());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isThief());
					assertFalse(testPlayer.isCleric());
					testPlayer.addClassCard(clericCard);
					testPlayer.setSuperMunchkinCard(munchkin);
					testPlayer.addClassCard(warriorCard);
					assertTrue(testPlayer.isWarrior());
					assertTrue(testPlayer.isSuperMunchkin());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isCleric());
					assertTrue(testPlayer.isWarrior());
					assertFalse(testPlayer.isThief());
					assertFalse(testPlayer.isSuperMunchkin());
					testPlayer.setSuperMunchkinCard(munchkin);
					testPlayer.addClassCard(wizardCard);
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isCleric());
					assertFalse(testPlayer.isWarrior());
					assertFalse(testPlayer.isThief());
					assertTrue(testPlayer.isWizard());
					assertFalse(testPlayer.isSuperMunchkin());
				}
				else if (curse.getID() == Card.CU_LOSE_YOUR_RACE) {
					curse.addEffects(testPlayer);
					RaceCard dwarfCard = dwarf.getRaceCards().getFirst();
					RaceCard elfCard = elf.getRaceCards().getFirst();
					RaceCard halflingCard = halfling.getRaceCards().getFirst();
					testPlayer.addRaceCard(dwarfCard);
					assertTrue(testPlayer.isDwarf());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isDwarf());
					testPlayer.addRaceCard(elfCard);
					assertTrue(testPlayer.isElf());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isElf());
					assertFalse(testPlayer.isDwarf());
					testPlayer.addRaceCard(dwarfCard);
					testPlayer.setHalfBreedCard(half);
					testPlayer.addRaceCard(halflingCard);
					assertTrue(testPlayer.isHalfling());
					assertTrue(testPlayer.isHalfBreed());
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isDwarf());
					assertFalse(testPlayer.isElf());
					assertFalse(testPlayer.isHalfling());
					assertFalse(testPlayer.isHalfBreed());
					testPlayer.addRaceCard(elfCard);
					testPlayer.setHalfBreedCard(half);
					testPlayer.addRaceCard(dwarfCard);
					curse.addEffects(testPlayer);
					assertFalse(testPlayer.isDwarf());
					assertFalse(testPlayer.isHalfling());
					assertFalse(testPlayer.isElf());
					assertFalse(testPlayer.isHalfBreed());
				}
				else if (curse.getID() == Card.CU_MALIGN_MIRROR) {
					assertTrue(curse.isLastingCurse());
					curse.addEffects(testPlayer);
					assertTrue(testPlayer.hasMalignMirror());
				}
				else if (curse.getID() == Card.CU_TRULY_OBNOXIOUS_CURSE) {
					EquipmentCard good = new EquipmentCard(30, "good", 100, 1, EquipmentType.OTHER, false, false, false);
					EquipmentCard better = new EquipmentCard(31, "better", 800, 2, EquipmentType.OTHER, false, false, false);
					EquipmentCard best = new EquipmentCard(32, "best", 300, 3, EquipmentType.OTHER, false, false, false);
					EquipmentCard unused = new EquipmentCard(33, "unused", 900, 10, EquipmentType.OTHER, false, false, false);
					testPlayer.addUnequippedItem(better);
					testPlayer.equip(better);
					testPlayer.addUnequippedItem(unused);
					testPlayer.addUnequippedItem(best);
					testPlayer.equip(best);
					testPlayer.addUnequippedItem(good);
					testPlayer.equip(good);
					testPlayer.addItem(loadedDie);
					
					assertEquals(testPlayer.getAllItems().size(), 5);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllItems().size(), 4);
					assertFalse(testPlayer.getAllItems().contains(best));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllItems().size(), 3);
					assertFalse(testPlayer.getAllItems().contains(better));
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllItems().size(), 2);
					assertFalse(testPlayer.getAllItems().contains(good));
					curse.addEffects(testPlayer);
					curse.addEffects(testPlayer);
					assertEquals(testPlayer.getAllItems().size(), 2);
					assertEquals(testPlayer.getUnequippedItems().size(), 1);
					assertTrue(testPlayer.getUnequippedItems().contains(unused));
					assertEquals(testPlayer.getCarriedItems().size(), 1);
					assertTrue(testPlayer.getCarriedItems().contains(loadedDie));
				}
				else {
					fail("Invalid Curse Card: " + curse + "(" + curse.getID() + ")");
					continue;
				}
				
				curseCount++;				
			}
		}
		
		assertEquals(curseCount, 19);
	}
	
	private void testMonsterEnhancerCards() {
		int enhancerCount = 0;
		for (Card card : deck) {
			if (card instanceof MonsterEnhancerCard) {				
				MonsterEnhancerCard enhancer = (MonsterEnhancerCard)card;
				if (enhancer.getID() == Card.ME_ANCIENT)
					assertEquals(enhancer.getBonus(), 10);
				else if (enhancer.getID() == Card.ME_BABY)
					assertEquals(enhancer.getBonus(), -5);
				else if (enhancer.getID() == Card.ME_ENRAGED)
					assertEquals(enhancer.getBonus(), 5);
				else if (enhancer.getID() == Card.ME_HUMONGOUS)
					assertEquals(enhancer.getBonus(), 10);
				else if (enhancer.getID() == Card.ME_INTELLIGENT)
					assertEquals(enhancer.getBonus(), 5);
				else {
					fail("Invalid Monster Enhancer Card: " + enhancer + "(" + enhancer.getID() + ")");
					continue;
				}
				
				enhancerCount++;				
			}
		}
		
		assertEquals(enhancerCount, 5);
	}
	
	private void testOtherDoorCards() {
		int otherCount = 0;
		for (Card card : deck) {
			if (card instanceof OtherDoorCard) {				
				OtherDoorCard other = (OtherDoorCard)card;
				if (other.getID() == Card.OD_CHEAT)
					otherCount++;
				else if (other.getID() == Card.OD_DIVINE_INTERVENTION)
					otherCount++;
				else if (other.getID() == Card.OD_HALF_BREED_1)
					otherCount++;
				else if (other.getID() == Card.OD_HALF_BREED_2)
					otherCount++;
				else if (other.getID() == Card.OD_HELP_ME_OUT_HERE)
					otherCount++;
				else if (other.getID() == Card.OD_ILLUSION)
					otherCount++;
				else if (other.getID() == Card.OD_MATE)
					otherCount++;
				else if (other.getID() == Card.OD_OUT_TO_LUNCH)
					otherCount++;
				else if (other.getID() == Card.OD_SUPER_MUNCHKIN_1)
					otherCount++;
				else if (other.getID() == Card.OD_SUPER_MUNCHKIN_2)
					otherCount++;
				else if (other.getID() == Card.OD_WANDERING_MONSTER_1)
					otherCount++;
				else if (other.getID() == Card.OD_WANDERING_MONSTER_2)
					otherCount++;
				else {
					fail("Invalid Other Door Card: " + other + "(" + other.getID() + ")");
				}			
			}
		}
		
		assertEquals(otherCount, 12);
	}
}
