
package model;

import exceptions.EndGameException;
import gui.MockGUI;
import gui.RunDialog;

import java.util.LinkedList;

import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;
import model.Battle;
import model.CardPlayManager;
import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;

public class CardPlayManagerTest extends UnitTest {
	private final static String RUN_TEXT = "Run";
	private final static String ESCAPE_TEXT = "Escape";
	private final static String FAIL_TEXT = "Failed Run Away";
	
	private Battle battle;
	
	private Player activePlayer;
	private Player otherPlayer;
	
	private EquipmentCard femaleSword;
	private EquipmentCard maleClub;
	
	private ItemCard glue;
	private ItemCard dopple;
	private ItemCard invis;
	private ItemCard wall;
	private ItemCard loaded;
	private ItemCard friend;
	private ItemCard dowsing;
	private ItemCard ring;
	private ItemCard lamp;
	private ItemCard polly;
	
	private MonsterCard crabs;
	private MonsterCard nose;
	
	private MonsterEnhancerCard baby;
	private MonsterEnhancerCard ancient;
	private MonsterEnhancerCard enraged;
	
	private CurseCard chicken;
	private CurseCard change;
	private CurseCard mirror;
	
	private OtherDoorCard breed;
	private OtherDoorCard munchkin;
	private OtherDoorCard lunch;
	private OtherDoorCard help;
	private OtherDoorCard illusion;
	private OtherDoorCard mate;
	private OtherDoorCard wander;
	private OtherDoorCard cheat;
	private OtherDoorCard divine;
	
	private GoUpLevelCard hirelingUp;
	private GoUpLevelCard mowUp;
	private GoUpLevelCard whineUp;
	
	private OtherTreasureCard hireling;
	private OtherTreasureCard steal;
	
	public int testAll() {
		initializeObjects();
		
		testPlayUnallowed();
		testPlayRace();
		testPlayClass();
		testPlayOtherDoor();
		testPlayEquipment();
		testPlayItem();
		testPlayGoUpLevel();
		testPlayOtherTreasure();
		
		testBattlePlayCard();
		testBattleMonsterEnhancer();
		testBattleOtherDoorCards();
		testBattleItemCards();
		
		testRunAwayCards();
		
		testCanCarryItems(); 
		
		return errorCount;
	}
	
	private void initializeObjects() {
		MockGUI mockGUI = new MockGUI(0);
		activePlayer = new Player(mockGUI, "active", true, PlayerType.TEST);
		otherPlayer = new Player(mockGUI, "other", false, PlayerType.TEST);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(activePlayer);
		players.add(otherPlayer);
		GM.newTestGame(mockGUI, players);
		
		MonsterCard monster = new MonsterCard(1, "Monster", 2, 1, 1, false) { public void doBadStuff(Player player) {} };
		battle = new Battle(activePlayer, monster);
		
		for (Card card : DoorDeckFactory.buildDeck()) {
			if (card.getID() == Card.M_CRABS)
				crabs = (MonsterCard)card;
			else if (card.getID() == Card.M_FLOATING_NOSE)
				nose = (MonsterCard)card;
			else if (card.getID() == Card.ME_BABY)
				baby = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.ME_ANCIENT)
				ancient = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.ME_ENRAGED)
				enraged = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.CU_CHANGE_SEX)
				change = (CurseCard)card;
			else if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
				chicken = (CurseCard)card;
			else if (card.getID() == Card.CU_MALIGN_MIRROR)
				mirror = (CurseCard)card;
			else if (card.getID() == Card.OD_HALF_BREED_1)
				breed = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_SUPER_MUNCHKIN_2)
				munchkin = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_OUT_TO_LUNCH)
				lunch = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_HELP_ME_OUT_HERE)
				help = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_ILLUSION)
				illusion = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_MATE)
				mate = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_WANDERING_MONSTER_1)
				wander = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_CHEAT)
				cheat = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_DIVINE_INTERVENTION)
				divine = (OtherDoorCard)card;
		}
		
		for (Card card : TreasureDeckFactory.buildDeck()) {
			if (card.getID() == Card.E_BROAD_SWORD)
				femaleSword = (EquipmentCard)card;
			else if (card.getID() == Card.E_GENTLEMENS_CLUB)
				maleClub = (EquipmentCard)card;
			else if (card.getID() == Card.I_FLASK_OF_GLUE)
				glue = (ItemCard)card;
			else if (card.getID() == Card.I_INVISIBILITY_POTION) 
				invis = (ItemCard)card;
			else if (card.getID() == Card.I_INSTANT_WALL) 
				wall = (ItemCard)card;
			else if (card.getID() == Card.I_LOADED_DIE)
				loaded = (ItemCard)card;
			else if (card.getID() == Card.I_FRIENDSHIP_POTION)
				friend = (ItemCard)card;	
			else if (card.getID() == Card.I_DOPPLEGANGER)
				dopple = (ItemCard)card;
			else if (card.getID() == Card.I_WAND_OF_DOWSING)
				dowsing = (ItemCard)card;
			else if (card.getID() == Card.I_WISHING_RING_2)
				ring = (ItemCard)card;
			else if (card.getID() == Card.I_MAGIC_LAMP_1)
				lamp = (ItemCard)card;
			else if (card.getID() == Card.I_POLLYMORPH_POTION)
				polly = (ItemCard)card;
			else if (card.getID() == Card.GUL_KILL_THE_HIRELING)
				hirelingUp = (GoUpLevelCard)card;
			else if (card.getID() == Card.GUL_MOW_THE_BATTLEFIELD)
				mowUp = (GoUpLevelCard)card;
			else if (card.getID() == Card.GUL_WHINE_AT_THE_GM)
				whineUp = (GoUpLevelCard)card;
			else if (card.getID() == Card.OT_HIRELING)
				hireling = (OtherTreasureCard)card;
			else if (card.getID() == Card.OT_STEAL_A_LEVEL)
				steal = (OtherTreasureCard)card;			
		}
	}
	
	private void testPlayUnallowed() {
		try {
			assertFalse(CardPlayManager.playCard(activePlayer, crabs));
			assertFalse(CardPlayManager.playCard(activePlayer, nose));
			assertFalse(CardPlayManager.playCard(activePlayer, mate));
			assertFalse(CardPlayManager.playCard(activePlayer, baby));
			assertFalse(CardPlayManager.playCard(activePlayer, ancient));
			assertFalse(CardPlayManager.playCard(activePlayer, enraged));
		}	
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testPlayRace() {
		RaceCard elf = new RaceCard(5, "Elf", Race.ELF);
		RaceCard dwarf = new RaceCard(6, "Dwarf", Race.DWARF);
		
		try {
			assertFalse(CardPlayManager.playCard(otherPlayer, elf));
			
			assertTrue(activePlayer.isHuman());
			assertFalse(activePlayer.isElf());
			assertTrue(CardPlayManager.playCard(activePlayer, elf));
			assertTrue(activePlayer.isElf());
			assertFalse(activePlayer.isHuman());
			
			assertFalse(CardPlayManager.playCard(activePlayer, elf));
			assertTrue(activePlayer.isElf());
			assertFalse(activePlayer.isHuman());
			
			assertTrue(CardPlayManager.playCard(activePlayer, dwarf));
			assertFalse(activePlayer.isElf());
			assertTrue(activePlayer.isDwarf());
			assertFalse(activePlayer.isHuman());
			assertTrue(CardPlayManager.playCard(activePlayer, elf));
			assertTrue(activePlayer.isElf());
			assertFalse(activePlayer.isDwarf());
			assertFalse(activePlayer.isHuman());
			
			activePlayer.setHalfBreedCard(breed);
			assertTrue(activePlayer.isElf());
			assertTrue(activePlayer.isHuman());
			assertFalse(activePlayer.isDwarf());
			
			assertTrue(CardPlayManager.playCard(activePlayer, dwarf));
			assertTrue(activePlayer.isElf());
			assertFalse(activePlayer.isHuman());
			assertTrue(activePlayer.isDwarf());
			activePlayer.getRaceCards().removeLast();
		}	
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testPlayClass() {
		ClassCard warrior = new ClassCard(5, "Warrior", Class.WARRIOR);
		ClassCard cleric = new ClassCard(6, "Cleric", Class.CLERIC);
		
		try {
			assertFalse(CardPlayManager.playCard(otherPlayer, warrior));
			
			assertFalse(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			assertTrue(CardPlayManager.playCard(activePlayer, warrior));
			assertTrue(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			
			assertFalse(CardPlayManager.playCard(activePlayer, warrior));
			assertTrue(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			
			assertTrue(CardPlayManager.playCard(activePlayer, cleric));
			assertFalse(activePlayer.isWarrior());
			assertTrue(activePlayer.isCleric());
			assertTrue(CardPlayManager.playCard(activePlayer, warrior));
			assertTrue(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			
			activePlayer.setSuperMunchkinCard(munchkin);
			assertTrue(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			
			assertTrue(CardPlayManager.playCard(activePlayer, cleric));
			assertTrue(activePlayer.isWarrior());
			assertTrue(activePlayer.isCleric());
		}	
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testPlayOtherDoor() {
		ClassCard wizard = new ClassCard(5, "Wizard", Class.WIZARD);
		
		try {
			// test half-breed
			assertFalse(CardPlayManager.playCard(otherPlayer, breed));
			
			assertFalse(CardPlayManager.playCard(activePlayer, breed));
			activePlayer.setHalfBreedCard(null);			
			assertTrue(CardPlayManager.playCard(activePlayer, breed));
			
			activePlayer.setHalfBreedCard(null);
			activePlayer.getRaceCards().clear();
			assertFalse(CardPlayManager.playCard(activePlayer, breed));
			
			// test super munchkin
			assertFalse(CardPlayManager.playCard(otherPlayer, munchkin));
			
			activePlayer.getHandCards().add(wizard);
			assertFalse(CardPlayManager.playCard(activePlayer, munchkin));
			assertTrue(activePlayer.isWarrior());
			assertTrue(activePlayer.isCleric());
			assertFalse(activePlayer.isWizard());
			activePlayer.setSuperMunchkinCard(null);
			activePlayer.getClassCards().removeLast();
			assertFalse(activePlayer.isCleric());
			assertTrue(CardPlayManager.playCard(activePlayer, munchkin));
			assertTrue(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			assertTrue(activePlayer.isWizard());
			
			activePlayer.setSuperMunchkinCard(null);
			activePlayer.getClassCards().removeLast();
			assertFalse(activePlayer.isWizard());
			assertFalse(CardPlayManager.playCard(activePlayer, munchkin));
			assertTrue(activePlayer.isWarrior());
			assertFalse(activePlayer.isCleric());
			assertFalse(activePlayer.isWizard());
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}

	private void testPlayEquipment() {
		try {
			EquipmentCard equipment = new EquipmentCard(5, "equipment", 300, 1, EquipmentType.OTHER, true, true, false);
			activePlayer.getRaceCards().clear();
			
			assertFalse(CardPlayManager.playCard(otherPlayer, equipment));
			assertTrue(CardPlayManager.playCard(activePlayer, equipment));
			assertTrue(activePlayer.hasEquipped(equipment));
			assertFalse(activePlayer.isDwarf());
			assertTrue(equipment.isBig());			
			assertFalse(CardPlayManager.playCard(activePlayer, equipment));
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testPlayItem() {
		try {	
			ItemCard item = new ItemCard(5, "Item", 2000);
			activePlayer.getHandCards().add(item);
			otherPlayer.getHandCards().add(item);
		
			GM.moveToOtherPhase();
			assertFalse(CardPlayManager.playCard(otherPlayer, item));
			
			GM.moveToBattlePhase();
			assertTrue(CardPlayManager.playCard(activePlayer, item));			
			assertEquals(activePlayer.getCarriedItems().size(), 1);
			activePlayer.getCarriedItems().clear();
			activePlayer.addCard(item);
			GM.moveToOtherPhase();
			assertTrue(CardPlayManager.playCard(activePlayer, item));			
			assertTrue(otherPlayer.getHandCards().remove(item));
			assertFalse(activePlayer.getHandCards().contains(item));
			
			assertTrue(CardPlayManager.playCard(otherPlayer, item));
			assertFalse(CardPlayManager.playCard(activePlayer, item));			
			assertTrue(activePlayer.getCarriedItems().contains(item));
			assertTrue(otherPlayer.getCarriedItems().contains(item));
			
			GM.getDoorDeck().discardPile.clear();
			GM.getTreasureDeck().discardPile.clear();
			
			// wand of dowsing
			activePlayer.getHandCards().add(dowsing);
			assertTrue(CardPlayManager.playCard(activePlayer, dowsing));
			assertFalse(activePlayer.getHandCards().contains(dowsing));
			assertTrue(activePlayer.getCarriedItems().contains(dowsing));			
			assertFalse(CardPlayManager.playCard(activePlayer, dowsing));
			
			otherPlayer.addItem(dowsing);
			assertFalse(CardPlayManager.playCard(otherPlayer, dowsing));
			assertTrue(otherPlayer.getCarriedItems().contains(dowsing));
			otherPlayer.discard(item);
			assertTrue(CardPlayManager.playCard(otherPlayer, dowsing));
			assertFalse(otherPlayer.getCarriedItems().contains(dowsing));
			assertFalse(GM.getTreasureDeck().discardPile.contains(item));
			assertTrue(otherPlayer.getCarriedItems().contains(item));
			
			GM.getDoorDeck().discardPile.add(divine);
			activePlayer.goUpLevels(10, false);
			activePlayer.addClassCard(new ClassCard(6, "Cleric", Class.CLERIC));
			try {
				activePlayer.getCarriedItems().remove(dowsing);
				activePlayer.getHandCards().add(dowsing);
				assertTrue(CardPlayManager.playCard(activePlayer, dowsing));
				fail("Should Be Game End.");
			}
			catch (EndGameException ex) {}			
			activePlayer.goDownLevels(10);
			
			// wishing ring
			activePlayer.getHandCards().add(ring);
			otherPlayer.getHandCards().add(ring);
			assertTrue(CardPlayManager.playCard(activePlayer, ring));
			assertFalse(activePlayer.getHandCards().contains(ring));
			assertTrue(activePlayer.getCarriedItems().contains(ring));
			assertFalse(CardPlayManager.playCard(otherPlayer, ring));			
			otherPlayer.getHandCards().remove(ring);
			otherPlayer.addItem(ring);
			
			activePlayer.getRaceCards().clear();
			activePlayer.getRaceCards().add(new RaceCard(7, "halfling", Race.HALFLING));
			activePlayer.getClassCards().clear();
			activePlayer.getClassCards().add(new ClassCard(8, "Thief", Class.THIEF));
			assertFalse(CardPlayManager.playCard(activePlayer, ring));
			activePlayer.addLastingCurse(chicken);
			assertTrue(activePlayer.hasChickenOnHead());
			boolean playedRing = false;
			for (int count = 1; count <= 16; count++) {
				if (CardPlayManager.playCard(activePlayer, ring)) {
					playedRing = true;
					break;
				}
			}
			assertTrue(playedRing);
			assertFalse(activePlayer.getCarriedItems().contains(ring));
			assertFalse(activePlayer.hasChickenOnHead());
			
			EquipmentCard weapon = new EquipmentCard(9, "weapon", 300, 10, EquipmentType.OTHER, false, false, true);
			otherPlayer.addUnequippedItem(weapon);
			assertEquals(otherPlayer.equip(weapon), "");
			assertFalse(CardPlayManager.playCard(otherPlayer, ring));
			otherPlayer.addLastingCurse(mirror);
			otherPlayer.addLastingCurse(chicken);
			assertTrue(otherPlayer.hasMalignMirror());
			assertTrue(CardPlayManager.playCard(otherPlayer, ring));
			assertFalse(otherPlayer.getCarriedItems().contains(ring));
			assertFalse(otherPlayer.hasMalignMirror());
			assertTrue(otherPlayer.hasChickenOnHead());
			
			activePlayer.getHandCards().add(ring);
			assertTrue(CardPlayManager.playCard(activePlayer, ring));
			assertTrue(activePlayer.getCarriedItems().contains(ring));
			activePlayer.addLastingCurse(change);
			activePlayer.changeSex();
			assertTrue(activePlayer.hasChangeSexCurse());
			assertTrue(activePlayer.hasDistractionCurse());
			playedRing = false;
			for (int count = 1; count <= 16; count++) {
				if (CardPlayManager.playCard(activePlayer, ring)) {
					playedRing = true;
					break;
				}
			}
			assertTrue(playedRing);
			assertFalse(activePlayer.getHandCards().contains(ring));
			assertFalse(activePlayer.getCarriedItems().contains(ring));
			assertFalse(activePlayer.hasChangeSexCurse());
			assertFalse(activePlayer.hasDistractionCurse());
			
			assertTrue(activePlayer.isMale());
			activePlayer.getCarriedItems().add(ring);
			activePlayer.addUnequippedItem(femaleSword);
			activePlayer.addLastingCurse(change);
			activePlayer.changeSex();
			assertTrue(activePlayer.hasChangeSexCurse());
			assertTrue(activePlayer.hasDistractionCurse());
			assertFalse(CardPlayManager.playCard(activePlayer, ring));
			assertTrue(activePlayer.getCarriedItems().contains(ring));
			assertTrue(activePlayer.hasChangeSexCurse());
			assertTrue(activePlayer.hasDistractionCurse());
			
			assertFalse(activePlayer.isMale());
			activePlayer.addUnequippedItem(maleClub);
			assertTrue(activePlayer.hasChangeSexCurse());
			assertTrue(activePlayer.hasDistractionCurse());
			assertTrue(CardPlayManager.playCard(activePlayer, ring));
			assertFalse(activePlayer.getCarriedItems().contains(ring));
			assertFalse(activePlayer.hasChangeSexCurse());
			assertFalse(activePlayer.hasDistractionCurse());
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testPlayGoUpLevel() {
		try {
			assertEquals(activePlayer.getLevel(), 1);
			assertEquals(otherPlayer.getLevel(), 1);
			
			assertFalse(CardPlayManager.playCard(activePlayer, hirelingUp));
			otherPlayer.setHirelingCard(hireling);
			assertTrue(CardPlayManager.playCard(activePlayer, hirelingUp));
			assertEquals(activePlayer.getLevel(), 2);
			assertFalse(CardPlayManager.playCard(activePlayer, hirelingUp));
			otherPlayer.setHirelingCard(hireling);
			assertTrue(CardPlayManager.playCard(otherPlayer, hirelingUp));
			assertEquals(otherPlayer.getLevel(), 2);
			assertFalse(CardPlayManager.playCard(activePlayer, hirelingUp));
			assertFalse(CardPlayManager.playCard(otherPlayer, hirelingUp));
			
			assertFalse(CardPlayManager.playCard(activePlayer, mowUp));
			assertFalse(CardPlayManager.playCard(otherPlayer, mowUp));
			GM.moveToBattlePhase();
			GM.moveNextPhase();
			assertTrue(CardPlayManager.playCard(activePlayer, mowUp));
			assertTrue(CardPlayManager.playCard(otherPlayer, mowUp));
			assertEquals(activePlayer.getLevel(), 3);
			assertEquals(otherPlayer.getLevel(), 3);
			
			LinkedList<Player> players = new LinkedList<Player>();
			players.add(activePlayer);
			players.add(otherPlayer);
			GM.newTestGame(new MockGUI(0), players);
			assertFalse(CardPlayManager.playCard(activePlayer, mowUp));
			assertFalse(CardPlayManager.playCard(otherPlayer, mowUp));
			
			assertFalse(CardPlayManager.playCard(activePlayer, whineUp));
			assertFalse(CardPlayManager.playCard(otherPlayer, whineUp));
			activePlayer.goUpLevel(false);
			assertFalse(CardPlayManager.playCard(activePlayer, whineUp));
			assertTrue(CardPlayManager.playCard(otherPlayer, whineUp));
			otherPlayer.goUpLevel(false);
			assertTrue(CardPlayManager.playCard(activePlayer, whineUp));
			assertFalse(CardPlayManager.playCard(otherPlayer, whineUp));
			assertEquals(activePlayer.getLevel(), 5);
			assertEquals(otherPlayer.getLevel(), 5);
				
			assertTrue(CardPlayManager.playCard(activePlayer, new GoUpLevelCard(5, "Go Up")));
			assertTrue(CardPlayManager.playCard(otherPlayer, new GoUpLevelCard(6, "Go Up")));
			assertEquals(activePlayer.getLevel(), 6);
			assertEquals(otherPlayer.getLevel(), 6);
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testPlayOtherTreasure() {
		try {
			GM.moveToBattlePhase();
			assertFalse(CardPlayManager.playCard(activePlayer, hireling));
			
			GM.moveToOtherPhase();
			assertFalse(CardPlayManager.playCard(otherPlayer, hireling));
			assertTrue(CardPlayManager.playCard(activePlayer, hireling));
			assertTrue(activePlayer.hasHireling());
			
			activePlayer.goDownLevels(10);
			otherPlayer.goDownLevels(10);
			assertFalse(CardPlayManager.playCard(activePlayer, steal));
			otherPlayer.goUpLevel(false);
			assertFalse(CardPlayManager.playCard(otherPlayer, steal));
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testBattlePlayCard() {
		try {
			assertTrue(CardPlayManager.playCard(activePlayer, new GoUpLevelCard(5, "Go Up"), null));
			assertTrue(CardPlayManager.playCard(activePlayer, new GoUpLevelCard(6, "Go Up"), battle));
			assertEquals(activePlayer.getLevel(), 3);
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testBattleMonsterEnhancer() {
		try {
			GM.moveToBattlePhase();
			int monsterLevel = battle.getMonstersLevel();
			assertTrue(battle.getMonsterEnhancers(0).isEmpty());
			assertTrue(CardPlayManager.playCard(activePlayer, baby, battle));
			assertEquals(battle.getMonsterEnhancers(0).size(), 1);
			assertTrue(battle.getMonsterEnhancers(0).contains(baby));
			assertEquals(battle.getMonstersLevel(), monsterLevel - 1);
			assertTrue(CardPlayManager.playCard(activePlayer, ancient, battle));
			assertEquals(battle.getMonsterEnhancers(0).size(), 2);
			assertTrue(battle.getMonsterEnhancers(0).contains(ancient));
			assertEquals(battle.getMonstersLevel(), monsterLevel + 5);
			assertTrue(CardPlayManager.playCard(activePlayer, enraged, battle));
			assertEquals(battle.getMonsterEnhancers(0).size(), 3);
			assertTrue(battle.getMonsterEnhancers(0).contains(enraged));
			assertEquals(battle.getMonstersLevel(), monsterLevel + 10);
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testBattleOtherDoorCards() {
		try {
			// out to lunch card
			battle.addTreasures(5);
			assertEquals(battle.getTreasureCount(), 5);
			GM.moveToBattlePhase();
			assertTrue(CardPlayManager.playCard(activePlayer, lunch, battle));
			assertEquals(battle.getTreasureCount(), 2);
			assertFalse(GM.isBattlePhase());
			
			battle.leaveTreasuresBehind();
			GM.moveToBattlePhase();
			assertTrue(CardPlayManager.playCard(activePlayer, lunch, battle));
			assertEquals(battle.getTreasureCount(), 2);
			assertFalse(GM.isBattlePhase());
			
			// help me out here! card
			activePlayer.getEquippedItems().clear();			
			GM.moveToBattlePhase();
			activePlayer.getHandCards().add(help);
			assertFalse(CardPlayManager.playCard(otherPlayer, help, battle));
			EquipmentCard equipment = otherPlayer.getAllEquipment().get(0);
			
			battle.getMonsterEnhancers(0).clear();
			activePlayer.goUpLevels(5, false);
			assertFalse(CardPlayManager.playCard(activePlayer, help, battle));
			otherPlayer.getUnequippedItems().remove(equipment);
			assertFalse(CardPlayManager.playCard(activePlayer, help, battle));
			otherPlayer.addUnequippedItem(equipment);
			activePlayer.goDownLevels(10);
			assertTrue(activePlayer.getHandCards().contains(help));
			assertTrue(CardPlayManager.playCard(activePlayer, help, battle));
			assertFalse(activePlayer.getHandCards().contains(help));
			assertTrue(activePlayer.hasEquipped(equipment));
			
			// wandering monster
			activePlayer.getHandCards().add(wander);
			assertFalse(CardPlayManager.playCard(activePlayer, wander, battle));
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testBattleItemCards() {
		try {
			activePlayer.getCarriedItems().add(glue);
			activePlayer.getCarriedItems().add(invis);
			activePlayer.getCarriedItems().add(wall);
			activePlayer.getCarriedItems().add(dopple);
			activePlayer.getCarriedItems().add(lamp);
			otherPlayer.getCarriedItems().add(loaded);
			otherPlayer.getCarriedItems().add(friend);
			otherPlayer.getCarriedItems().add(dopple);
			otherPlayer.getCarriedItems().add(lamp);
			otherPlayer.getCarriedItems().add(polly);
			
			assertFalse(CardPlayManager.playCard(activePlayer, glue, battle));
			assertFalse(CardPlayManager.playCard(activePlayer, invis, battle));
			assertFalse(CardPlayManager.playCard(activePlayer, wall, battle));
			assertFalse(CardPlayManager.playCard(otherPlayer, loaded, battle));
			
			// friendship potion
			battle.addTreasures(4);
			assertEquals(battle.getTreasureCount(), 6);
			GM.moveToBattlePhase();
			assertTrue(CardPlayManager.playCard(otherPlayer, friend, battle));
			assertEquals(battle.getTreasureCount(), 0);
			assertFalse(GM.isBattlePhase());
			GM.moveToBattlePhase();
			
			// doppleganger
			battle.addHelper(otherPlayer);
			assertFalse(CardPlayManager.playCard(activePlayer, dopple, battle));
			battle.removeHelper();
			assertTrue(CardPlayManager.playCard(activePlayer, dopple, battle));
			assertFalse(activePlayer.getCarriedItems().contains(dopple));
			assertTrue(battle.getPlayerItemCards().contains(dopple));
			
			// magic lamp
			MonsterCard monster = battle.getMonster(0);
			assertFalse(CardPlayManager.playCard(otherPlayer, lamp, battle));
			assertTrue(CardPlayManager.playCard(activePlayer, lamp, battle));
			assertFalse(GM.isBattlePhase());
			assertEquals(battle.getMonsterCount(), 0);
			battle.addMonster(monster);
			GM.moveToBattlePhase();
			
			// pollymorph potion
			assertTrue(CardPlayManager.playCard(otherPlayer, polly, battle));
			assertFalse(GM.isBattlePhase());
			assertEquals(battle.getMonsterCount(), 0);
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testRunAwayCards() {
		try {
			MonsterCard monster =  new MonsterCard(5, "M", 2, 1, 1, false) { public void doBadStuff(Player player) {} };
			battle.addMonster(monster);
			GM.moveToBattlePhase();
			RunDialog runDialog = new RunDialog(battle);
			
			assertFalse(CardPlayManager.playCard(activePlayer, help, monster, runDialog));
			assertFalse(CardPlayManager.playCard(activePlayer, illusion, monster, runDialog));
			assertFalse(CardPlayManager.playCard(activePlayer, mate, monster, runDialog));
			assertFalse(CardPlayManager.playCard(activePlayer, lunch, monster, runDialog));
			assertFalse(CardPlayManager.playCard(activePlayer, wander, monster, runDialog));
			assertFalse(CardPlayManager.playCard(activePlayer, ancient, monster, runDialog));
			assertFalse(CardPlayManager.playCard(otherPlayer, baby, monster, runDialog));
			
			otherPlayer.getCarriedItems().add(invis);
			otherPlayer.getCarriedItems().add(friend);
			
			// flask of glue
			runDialog.setRunText(RUN_TEXT);
			assertFalse(CardPlayManager.playCard(activePlayer, glue, monster, runDialog));
			runDialog.setRunText(FAIL_TEXT);
			assertFalse(CardPlayManager.playCard(activePlayer, glue, monster, runDialog));
			runDialog.setRunText(ESCAPE_TEXT);
			assertTrue(CardPlayManager.playCard(activePlayer, glue, monster, runDialog));
			assertEquals(runDialog.getRunText(), "Allow " + RUN_TEXT);
			assertFalse(otherPlayer.getCarriedItems().contains(glue));
						
			// invisibility potion
			assertFalse(CardPlayManager.playCard(activePlayer, invis, monster, runDialog));
			runDialog.setRunText(ESCAPE_TEXT);
			assertFalse(CardPlayManager.playCard(activePlayer, invis, monster, runDialog));
			runDialog.setRunText(FAIL_TEXT);
			assertFalse(CardPlayManager.playCard(activePlayer, invis, crabs, runDialog));
			assertFalse(CardPlayManager.playCard(activePlayer, invis, nose, runDialog));
			assertFalse(CardPlayManager.playCard(otherPlayer, invis, monster, runDialog));
			assertTrue(CardPlayManager.playCard(activePlayer, invis, monster, runDialog));
			assertFalse(activePlayer.getCarriedItems().contains(invis));
			assertEquals(runDialog.getRunText(), "Allow " + ESCAPE_TEXT);
			
			// instant wall
			assertTrue(CardPlayManager.playCard(activePlayer, wall, monster, runDialog));
			assertFalse(activePlayer.getCarriedItems().contains(wall));
			
			// friendship potion
			assertTrue(CardPlayManager.playCard(otherPlayer, friend, monster, runDialog));
			runDialog.setRunText(FAIL_TEXT);
			assertFalse(otherPlayer.getCarriedItems().contains(friend));
			otherPlayer.getCarriedItems().add(friend);
			assertFalse(CardPlayManager.playCard(otherPlayer, friend, monster, runDialog));
			
			// battle item
			ItemCard item = new ItemCard(6, "item", 200);
			activePlayer.getCarriedItems().add(item);
			otherPlayer.getCarriedItems().add(item);
			assertFalse(CardPlayManager.playCard(activePlayer, item, monster, runDialog));
			assertFalse(CardPlayManager.playCard(otherPlayer, item, monster, runDialog));
		}
		catch (EndGameException ex ) { fail("Is Not Game End."); }
	}
	
	private void testCanCarryItems() {
		EquipmentCard bigItem1 = new EquipmentCard(5, "big1", 100, 1, EquipmentType.OTHER, true, true, false);
		EquipmentCard bigItem2 = new EquipmentCard(6, "big2", 200, 2, EquipmentType.OTHER, true, false, false);
		EquipmentCard bigItem3 = new EquipmentCard(7, "big3", 300, 3, EquipmentType.OTHER, true, false, true);
		EquipmentCard bigItemCheat = new EquipmentCard(8, "bigCheat", 500, 10, EquipmentType.OTHER, true, true, true);
		EquipmentCard smallItem = new EquipmentCard(9, "small", 0, 0, EquipmentType.OTHER, false, true, true);
		
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertTrue(CardPlayManager.canCarryItem(activePlayer, bigItem1));
		
		activePlayer.addUnequippedItem(bigItem1);
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertFalse(CardPlayManager.canCarryItem(activePlayer, bigItem2));
		assertNull(activePlayer.getHirelingCard());
		activePlayer.setHirelingCard(hireling);
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertTrue(CardPlayManager.canCarryItem(activePlayer, bigItem2));
		
		activePlayer.addUnequippedItem(bigItem2);		
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertFalse(CardPlayManager.canCarryItem(activePlayer, bigItemCheat));
		activePlayer.setCheatCards(cheat, bigItemCheat);
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertTrue(CardPlayManager.canCarryItem(activePlayer, bigItemCheat));
		
		activePlayer.addUnequippedItem(bigItemCheat);		
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertFalse(CardPlayManager.canCarryItem(activePlayer, bigItem3));
		activePlayer.addRaceCard(new RaceCard(10, "Dwarf", Race.DWARF));
		assertTrue(CardPlayManager.canCarryItem(activePlayer, smallItem));
		assertTrue(CardPlayManager.canCarryItem(activePlayer, bigItem3));
	}	
}
