
package gui;

import java.util.LinkedList;
import java.util.Stack;

import model.Battle;
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
import model.card.OtherDoorCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class RunDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private Battle battle;
	private Player easy;
	private Player medium;
	private Player hard;
	
	private MonsterCard nose;
	private MonsterCard frogs;
	private MonsterCard dog;
	private MonsterCard bigfoot;
	private MonsterCard wight;
	private MonsterCard crabs;
	
	private CurseCard chicken;
	private CurseCard changeRace;
	private CurseCard loseRace;
	private CurseCard loseBig;
	private CurseCard loseShoes;
	private CurseCard loseCards;
	
	private EquipmentCard rat;
	private EquipmentCard staff;
	private EquipmentCard shoes;
	private EquipmentCard tuba;
	
	private ItemCard wall;
	private ItemCard glue;
	private ItemCard lamp;
	
	public int testAll() {
		initializeObjects();

		testMakeComputerDecisions();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		GUI mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		GM.newTestGame(mockGUI, players);
		
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck) {
			if (card.getID() == Card.M_FLOATING_NOSE)
				nose = (MonsterCard)card;
			else if (card.getID() == Card.M_FLYING_FROGS)
				frogs = (MonsterCard)card;
			else if (card.getID() == Card.M_PIT_BULL)
				dog = (MonsterCard)card;
			else if (card.getID() == Card.M_BIGFOOT)
				bigfoot = (MonsterCard)card;
			else if (card.getID() == Card.M_WIGHT_BROTHERS)
				wight = (MonsterCard)card;
			else if (card.getID() == Card.M_CRABS)
				crabs = (MonsterCard)card;
			else if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
				chicken = (CurseCard)card;
			else if (card.getID() == Card.CU_CHANGE_RACE)
				changeRace = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_YOUR_RACE)
				loseRace = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_1_BIG_ITEM)
				loseBig = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING)
				loseShoes = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_TWO_CARDS)
				loseCards = (CurseCard)card;
		}
		
		Stack<Card> treasureDeck = TreasureDeckFactory.buildDeck();
		for (Card card : treasureDeck) {
			if (card.getID() == Card.E_RAT_ON_A_STICK)
				rat = (EquipmentCard)card;
			else if (card.getID() == Card.E_STAFF_OF_NAPALM)
				staff = (EquipmentCard)card;
			else if (card.getID() == Card.E_BOOTS_OF_RUNNING_REALLY_FAST)
				shoes = (EquipmentCard)card;
			else if (card.getID() == Card.E_TUBA_OF_CHARM)
				tuba = (EquipmentCard)card;
			else if (card.getID() == Card.I_INSTANT_WALL)
				wall = (ItemCard)card;
			else if (card.getID() == Card.I_FLASK_OF_GLUE)
				glue = (ItemCard)card;
			else if (card.getID() == Card.I_MAGIC_LAMP_1)
				lamp = (ItemCard)card;
		}		
	}
	
	private void testMakeComputerDecisions() {
		// test Rat on Stick
		GM.moveToBattlePhase();
		battle = new Battle(medium, bigfoot);
		medium.goUpLevels(5, false);
		medium.getEquippedItems().add(rat);
		RunDialog dialog = new RunDialog(battle);
		assertTrue(dialog.getCurrentPlayer() == medium);
		assertTrue(dialog.getBattle() == battle);
		assertEquals(dialog.getRunText(), "Allow Run");
		assertTrue(medium.getEquippedItems().contains(rat));
		battle = new Battle(medium, frogs);
		dialog = new RunDialog(battle);
		assertTrue(dialog.getCurrentPlayer() == medium);
		assertTrue(dialog.getBattle() == battle);
		assertEquals(dialog.getRunText(), "Allow Escape");
		assertFalse(medium.getEquippedItems().contains(rat));
		
		// test Instant Wall
		medium.addItem(wall);
		battle = new Battle(medium, bigfoot);
		assertTrue(medium.getCarriedItems().contains(wall));
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Run");
		assertTrue(medium.getCarriedItems().contains(wall));
		battle = new Battle(medium, frogs);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Escape");
		assertFalse(medium.getCarriedItems().contains(wall));
		
		// test discard race
		medium.addRaceCard(new RaceCard (1, "Halfling", Race.HALFLING));
		battle = new Battle(medium, bigfoot);
		assertTrue(medium.isHalfling());
		dialog = new RunDialog(battle);
		assertTrue(medium.isHalfling());
		assertEquals(dialog.getRunText(), "Allow Run");
		battle = new Battle(medium, frogs);
		assertTrue(medium.isHalfling());
		dialog = new RunDialog(battle);
		assertFalse(medium.isHalfling());
		assertEquals(dialog.getRunText(), "Allow Run");
		
		// test bribe
		battle = new Battle(medium, nose);
		medium.addItem(new ItemCard(2, "item 100", 100));
		dialog = new RunDialog(battle);
		assertEquals(medium.getCarriedItems().size(), 1);
		assertEquals(dialog.getRunText(), "Allow Failed Run Away");
		medium.addItem(new ItemCard(3, "item 200", 200));
		assertFalse(GM.getDoorDeck().getDiscardPile().contains(nose));
		dialog = new RunDialog(battle);
		assertTrue(GM.getDoorDeck().getDiscardPile().contains(nose));
		assertFalse(GM.isBattlePhase());
		assertEquals(medium.getCarriedItems().size(), 1);
		assertTrue(medium.getCarriedItems().get(0).getID() == 2);
		assertTrue(medium.getCarriedItems().get(0).getName().equals("item 100"));
		GM.moveToBattlePhase();
		medium.addItem(new ItemCard(4, "item 200", 200));
		assertEquals(medium.getCarriedItems().size(), 2);
		medium.goDownLevels(5);
		dialog = new RunDialog(battle);
		assertTrue(GM.isBattlePhase());
		assertEquals(medium.getCarriedItems().size(), 2);
		medium.getCarriedItems().clear();
		medium.goUpLevels(5, false);
		
		// test drop stick
		battle = new Battle(medium, dog);
		medium.getEquippedItems().add(staff);
		medium.setCheatCards(new OtherDoorCard(4, "Cheat!"), staff);
		assertEquals(medium.getAllItems().size(), 1);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Run");
		assertEquals(medium.getAllItems().size(), 1);
		medium.getEquippedItems().clear();
		medium.removeCheat();
		medium.addUnequippedItem(staff);
		assertEquals(medium.getAllItems().size(), 1);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Escape");
		assertEquals(medium.getAllItems().size(), 0);
		
		// test flight spell
		medium.addClassCard(new ClassCard(5, "Wizard", Class.WIZARD));
		battle = new Battle(medium, frogs);
		medium.addCard(new ItemCard(6, "item0", 0));
		medium.addCard(new ItemCard(7, "item00", 0));
		medium.addCard(new ItemCard(8, "item000", 0));
		assertEquals(medium.getHandCards().size(), 3);
		assertEquals(dialog.getRunAwayBonus(frogs), -1);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunAwayBonus(frogs), 2);
		assertEquals(medium.getHandCards().size(), 0);
		assertEquals(dialog.getRunText(), "Allow Run");
		medium.addCard(new ItemCard(9, "item0", 0));
		medium.addCard(new ItemCard(10, "item00", 0));
		medium.addCard(new ItemCard(11, "item1000", 1000));
		assertEquals(medium.getHandCards().size(), 3);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunAwayBonus(frogs), 1);
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(dialog.getRunText(), "Allow Run");
		medium.getClassCards().clear();
		medium.getHandCards().clear();
		
		// test Magic Lamp
		medium.addItem(lamp);
		battle = new Battle(medium, crabs);
		battle.addMonster(wight);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Failed Run Away");
		assertFalse(medium.getCarriedItems().isEmpty());
		EquipmentCard armor = new EquipmentCard(12, "Armor", 3000, 1, EquipmentType.ARMOR, false, true, false);
		medium.getEquippedItems().add(armor);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Run");
		assertTrue(medium.getCarriedItems().isEmpty());
		assertEquals(battle.getMonsterCount(), 1);
		medium.getEquippedItems().clear();
		
		// test Glue
		battle = new Battle(medium, wight);
		medium.goDownLevels(4);
		assertEquals(medium.getLevel(), 2);
		medium.addItem(wall);
		hard.addItem(glue);
		assertEquals(medium.getAllItems().size(), 1);
		assertEquals(hard.getAllItems().size(), 1);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Escape");
		assertEquals(medium.getAllItems().size(), 1);
		assertEquals(hard.getAllItems().size(), 1);
		medium.goUpLevels(10, false);
		dialog = new RunDialog(battle);
		assertEquals(dialog.getRunText(), "Allow Run");
		assertEquals(medium.getAllItems().size(), 0);
		assertEquals(hard.getAllItems().size(), 0);
		
		// test Change Race/"Lose Your Race" (from Elf)
		medium.goDownLevels(10);
		battle = new Battle(medium, bigfoot);
		medium.addRaceCard(new RaceCard(12, "Elf", Race.ELF));
		assertTrue(medium.isElf());
		easy.addCard(changeRace);
		hard.addCard(loseRace);
		dialog = new RunDialog(battle);
		assertFalse(easy.getHandCards().isEmpty());
		assertFalse(hard.getHandCards().isEmpty());
		assertTrue(medium.isElf());
		assertEquals(dialog.getRunText(), "Allow Run");
		hard.getHandCards().clear();
		medium.goUpLevels(10, false);
		battle = new Battle(medium, bigfoot);
		dialog = new RunDialog(battle);
		assertTrue(easy.getHandCards().isEmpty());
		assertFalse(medium.isElf());
		assertEquals(dialog.getRunText(), "Allow Run");
		hard.addCard(loseRace);
		medium.getRaceCards().clear();
		medium.addRaceCard(new RaceCard(12, "Elf", Race.ELF));
		assertTrue(medium.isElf());
		dialog = new RunDialog(battle);
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(medium.isElf());
		assertEquals(dialog.getRunText(), "Allow Run");
		
		// test Chicken on Your Head
		hard.goUpLevels(10, false);
		easy.goUpLevels(10, false);
		medium.goDownLevel();
		battle = new Battle(medium, bigfoot);
		hard.addCard(chicken);
		dialog = new RunDialog(battle);
		assertFalse(hard.getHandCards().isEmpty());
		assertFalse(medium.hasChickenOnHead());
		assertEquals(dialog.getRunText(), "Allow Run");
		battle = new Battle(medium, wight);
		dialog = new RunDialog(battle);
		assertTrue(hard.getHandCards().isEmpty());
		assertTrue(medium.hasChickenOnHead());
		assertEquals(dialog.getRunText(), "Allow Run");
		medium.removeChickenOnHeadCurse();
		hard.goDownLevels(10);
		easy.goDownLevels(10);
		medium.goUpLevels(10, false);
		
		// test Lose 1 Big Item
		medium.goDownLevels(4);
		easy.goUpLevels(5, false);
		hard.goUpLevels(5, false);
		battle = new Battle(medium, bigfoot);
		medium.getEquippedItems().add(tuba);
		hard.addCard(loseBig);
		dialog = new RunDialog(battle);
		assertFalse(hard.getHandCards().isEmpty());
		assertTrue(medium.hasEquipped(tuba));
		assertEquals(dialog.getRunText(), "Allow Run");
		battle = new Battle(medium, wight);
		dialog = new RunDialog(battle);
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(medium.hasEquipped(tuba));
		assertEquals(dialog.getRunText(), "Allow Run");
		
		// "Lose the Footgear You Are Wearing" "Boots of Running Really Fast"
		battle = new Battle(medium, bigfoot);
		medium.getEquippedItems().add(shoes);
		easy.addCard(loseShoes);
		dialog = new RunDialog(battle);
		assertFalse(easy.getHandCards().isEmpty());
		assertTrue(medium.hasEquipped(shoes));
		assertEquals(dialog.getRunText(), "Allow Run");
		battle = new Battle(medium, wight);
		dialog = new RunDialog(battle);
		assertTrue(easy.getHandCards().isEmpty());
		assertFalse(medium.hasEquipped(shoes));
		assertEquals(dialog.getRunText(), "Allow Run");
		
		// "Lose Two Cards"
		medium.addClassCard(new ClassCard(13, "Wizard", Class.WIZARD));
		battle = new Battle(medium, bigfoot);
		medium.addCard(new ItemCard(14, "item1", 1));
		medium.addCard(new ItemCard(15, "item2", 2));
		medium.addCard(new ItemCard(16, "item3", 3));
		medium.addCard(new ItemCard(17, "item4", 4));
		medium.addCard(new ItemCard(18, "item5", 5));
		hard.addCard(loseCards);
		dialog = new RunDialog(battle);
		assertTrue(hard.getHandCards().contains(loseCards));
		assertEquals(medium.getHandCards().size(), 5);
		assertEquals(dialog.getRunText(), "Allow Run");
		battle = new Battle(medium, wight);
		dialog = new RunDialog(battle);
		assertFalse(hard.getHandCards().contains(loseCards));
		assertTrue(medium.getHandCards().isEmpty());
		assertEquals(dialog.getRunText(), "Allow Run");
		easy.getHandCards().clear();
		hard.getHandCards().clear();
		medium.getClassCards().clear();
	}
}
