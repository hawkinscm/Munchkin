
package gui;

import java.util.LinkedList;

import model.Battle;
import model.Class;
import model.DoorDeckFactory;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.UnitTest;
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

public class AskHelpDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
	
	private final ClassCard WARRIOR = new ClassCard(1, "Warrior", Class.WARRIOR);
	private final RaceCard ELF = new RaceCard(2, "Elf", Race.ELF);

	private final OtherDoorCard MATE = new OtherDoorCard(Card.OD_MATE, "Mate");
	
	private Battle battle;
	
	private MonsterCard monster5;
	private MonsterCard monster8;
	private MonsterCard monster15;
	private MonsterCard monsterNoReward;
	private MonsterCard amazon;
	private MonsterCard lawyer;
	private MonsterCard tdemon;
	
	private CurseCard change;
		
	private Player easy1;
	private Player easy2;
	private Player medium1;
	private Player medium2;
	private Player hard1;
	private Player hard2;	
	
	public int testAll() {
		initializeObjects();

		testHelpNotNeeded();
		testAskForHelp();
		testJoinBattle();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		GUI gui = new MockGUI(0);
		
		monster5 = new MonsterCard(4, "M5", 5, 1, 1, false) {
			public void doBadStuff(Player player) {}
		};
		monster8 = new MonsterCard(5, "M8", 8, 3, 1, true) {
			public void doBadStuff(Player player) {}
		};
		monster15 = new MonsterCard(6, "M15", 15, 8, 2, false) {
			public void doBadStuff(Player player) {}
		};
		
		monsterNoReward = new MonsterCard(7, "M10", 10, 0, 1, false) {
			public void doBadStuff(Player player) {}
		};
		
		easy1 = new Player(gui, "easy1", true, PlayerType.COMPUTER_EASY);
		easy2 = new Player(gui, "easy2", true, PlayerType.COMPUTER_EASY);
		medium1 = new Player(gui, "medium1", false, PlayerType.COMPUTER_MEDIUM);
		medium2 = new Player(gui, "medium2", true, PlayerType.COMPUTER_MEDIUM);
		hard1 = new Player(gui, "hard1", true, PlayerType.COMPUTER_HARD);
		hard2 = new Player(gui, "hard2", true, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy1);
		players.add(easy2);
		players.add(medium1);
		players.add(medium2);
		players.add(hard1);
		players.add(hard2);
		
		GM.newTestGame(gui, players);
		GM.moveToBattlePhase();
		
		for (Card card : DoorDeckFactory.buildDeck()) {
			if (card.getID() == Card.M_AMAZON)
				amazon = (MonsterCard)card;
			else if (card.getID() == Card.M_LAWYER)
				lawyer = (MonsterCard)card;
			else if (card.getID() == Card.M_TONGUE_DEMON)
				tdemon = (MonsterCard)card;
			else if (card.getID() == Card.CU_CHANGE_SEX)
				change = (CurseCard)card;
		}
	}
	
	private void testHelpNotNeeded() {
		battle = new Battle(easy1, monster5);
		easy2.goUpLevels(10, false);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		battle.removeHelper();
		easy1.goUpLevels(5, false);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		easy1.goDownLevels(5);
		medium2.goUpLevel(false);
		hard1.goUpLevel(false);
		hard2.goUpLevel(false);
		easy1.getEquippedItems().add(new EquipmentCard(10, "equip4", 500, 4, EquipmentType.OTHER, false, false, false));
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium1);
		battle.removeHelper();
		easy1.addClassCard(WARRIOR);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		easy1.getClassCards().clear();
		easy1.goUpLevel(false);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		battle.addMonsterEnhancer(monster5, new MonsterEnhancerCard(11, "Enraged", 5));
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		battle.removeHelper();
		easy1.goUpLevels(5, false);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		easy1.goDownLevels(10);
		easy2.goDownLevels(10);
		battle.getMonsterEnhancers(monster5).clear();
	}
	
	private void testAskForHelp() {
		battle = new Battle(easy1, monsterNoReward);
		assertFalse(battle.isHelper());
		assertEquals(easy1.getLevel(), 1);
		assertFalse(easy1.isWarrior());
		assertEquals(battle.getPlayersLevel(), 5);
		assertEquals(battle.getMonstersLevel(), 10);
		easy1.goUpLevels(4, false);
		easy2.goUpLevel(false);
		medium1.goUpLevels(2, false);
		medium2.goUpLevels(2, false); // already at level 2
		hard1.goUpLevels(3, false); // already at level 2
		hard2.goUpLevels(4, false); // already at level 2
		assertEquals(easy2.getLevel(), 2);
		assertEquals(medium1.getLevel(), 3);
		assertEquals(medium2.getLevel(), 4);
		assertEquals(hard1.getLevel(), 5);
		assertEquals(hard2.getLevel(), 6);
		
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		battle.removeHelper();
		easy1.goDownLevel();
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium1);
		battle.removeHelper();
		easy1.goDownLevel();
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		battle.removeHelper();
		easy1.goDownLevel();
		medium2.addClassCard(WARRIOR);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		battle.removeHelper();
		medium2.getClassCards().clear();
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard1);
		battle.removeHelper();
		easy1.goDownLevel();
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard2);
		battle.removeHelper();
		hard2.goDownLevel();
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		
		hard2.goUpLevel(false);
		hard2.addRaceCard(ELF);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		easy1.goUpLevels(7, false);
		battle.addMonster(monster8);
		hard2.goUpLevels(2, false);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard2);
		battle.removeHelper();
		easy1.goDownLevel();
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		battle.removeHelper();
		easy1.goDownLevels(10);
		hard2.goDownLevels(2);
		hard2.getRaceCards().clear();
		battle.befriendMonster(monster8);
		
		easy1.goUpLevels(5, false);
		assertEquals(battle.getPlayersLevel(), 10);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		assertTrue(battle.haveAllPlayersRefusedToHelp());
		for (Player player : GM.getPlayers())
			if (player != easy1)
				assertTrue(battle.hasRefusedToHelp(player));
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		battle.replaceMonster(monsterNoReward, monster15);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		
		battle = new Battle(easy1, monster15);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 8);
		for (Player taker : battle.getTakeTreasurePlayerOrder())
			assertTrue(taker == hard2);
	}
	
	private void testJoinBattle() {
		// "Amazon", 8, 2, 1
		easy1.goDownLevels(4);
		assertEquals(easy1.getLevel(), 2);
		assertTrue(easy1.isMale());
		battle = new Battle(easy1, amazon);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium1);
		battle.removeHelper();
		medium1.changeSex();
		assertEquals(easy1.getLevel(), 2);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		battle.addHelpRefuser(easy2);
		assertTrue(battle.haveAllPlayersRefusedToHelp());
		easy1.getEquippedItems().clear();
		
		// "Lawyer", 6, 2, 1
		battle = new Battle(easy1, lawyer);
		easy1.goUpLevels(8, false);
		change.addEffects(easy1);
		assertEquals(easy1.getLevel(), 9);
		assertEquals(battle.getPlayersLevel(), 4);
		easy2.addClassCard(WARRIOR);
		(new AskHelpDialog(battle, true)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		battle.removeHelper();
		hard1.addClassCard(new ClassCard(10, "Thief", Class.THIEF));
		hard1.getCarriedItems().add(new ItemCard(11, "item1", 100));
		hard1.getCarriedItems().add(new ItemCard(12, "item2", 500));
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard1);
		battle.removeHelper();
		hard1.getClassCards().clear();
		hard1.getCarriedItems().clear();
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		assertTrue(battle.haveAllPlayersRefusedToHelp());
		
		// "Tongue Demon", 12, 3, 1
		easy1.removeSexChangeCurse();
		easy1.goDownLevels(10);
		easy1.getEquippedItems().add(new EquipmentCard(13, "equip6", 800, 6, EquipmentType.OTHER, false, false, false));
		easy1.getCarriedItems().add(new ItemCard(14, "item3", 1000, 10));
		battle = new Battle(easy1, tdemon);
		assertTrue(easy1.getCarriedItems().isEmpty());
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 3);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == hard2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == hard2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == hard2);
		battle.removeHelper();
		hard2.getCarriedItems().add(new ItemCard(15, "item4", 100));
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == hard2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 3);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == hard2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == hard2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == hard2);
		assertTrue(hard2.getCarriedItems().isEmpty());
		battle.removeHelper();
		hard2.getCarriedItems().add(new ItemCard(16, "item5", 200, 10));
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		assertEquals(hard2.getCarriedItems().size(), 1);
		assertTrue(hard2.getCarriedItems().getFirst().getID() == 16);
		assertTrue(hard2.getCarriedItems().getFirst().getName().equals("item5"));
		assertTrue(battle.hasRefusedToHelp(hard2));
		hard2.getCarriedItems().clear();
		easy1.getEquippedItems().clear();
		
		easy1.goUpLevels(5, false);
		easy1.getCarriedItems().add(new ItemCard(17, "top item", 1000, 10));
		battle = new Battle(easy1, monster8);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 3);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == easy2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == easy2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == easy2);
		battle.removeHelper();
		easy2.addRaceCard(ELF);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 3);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == easy2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == easy1);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == easy2);
		battle.removeHelper();
		easy1.goUpLevels(2, false);
		battle.addMonsterEnhancer(monster8, MATE);
		battle.addMonsterEnhancer(monster8, new MonsterEnhancerCard(Card.ME_BABY, "Baby", -3));
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		assertTrue(battle.haveAllPlayersRefusedToHelp());
		battle = new Battle(easy1, monster8);
		battle.addMonsterEnhancer(monster8, new MonsterEnhancerCard(Card.ME_BABY, "Baby", -3));
		battle.addMonsterEnhancer(monster8, MATE);
		easy2.goUpLevels(6, false);
		medium2.goUpLevels(5, false);
		medium2.addRaceCard(ELF);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == easy2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 4);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == easy1);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == easy1);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == easy2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(3) == easy2);
		battle.removeHelper();		
		easy2.goDownLevels(6);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 4);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == medium2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == easy1);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == medium2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(3) == medium2);
		battle.removeHelper();		
		medium2.goDownLevels(5);		
		hard2.goUpLevels(2, false);
		hard2.addRaceCard(ELF);
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);	
		hard2.goDownLevels(2);
		hard2.getRaceCards().clear();
		medium2.getRaceCards().clear();
		easy2.getClassCards().clear();
		easy1.getCarriedItems().clear();
		easy2.getRaceCards().clear();
	
		// make easy1 in third place from last so that helper's min help value will equal player benefit
		easy2.goDownLevels(10);
		hard2.goDownLevels(10);
		easy1.goDownLevels(10);
		easy1.goUpLevel(false);
		ItemCard equalizer = new ItemCard(20, "equalizer", 3100);
		medium1.getCarriedItems().add(equalizer);
		medium2.getCarriedItems().add(equalizer);
		hard1.getCarriedItems().add(equalizer);
		MonsterCard testMonster = new MonsterCard(Card.M_CRABS, "Crabs", 5, 5, 1, false) {
			public void doBadStuff(Player player) {}
		};
		for (int count = 0; count < 31; count++)
			easy1.getEquippedItems().add(new EquipmentCard(22, "e" + count, 100, 0, EquipmentType.OTHER, false, true, false));
		battle = new Battle(easy1, testMonster);
		// playerVal = 4100
		// treasures = 600, 700, 800, 900, 1000 = 4000
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		battle = new Battle(easy1, testMonster);
		easy1.getEquippedItems().removeLast();
		// playerVal = 4000
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
		for (int takerIdx = 0; takerIdx < 5; takerIdx++)
			assertTrue(battle.getTakeTreasurePlayerOrder().get(takerIdx) == medium2);
		battle.removeHelper();
		easy1.getEquippedItems().removeLast();
		// playerVal = 3900
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
		for (int takerIdx = 0; takerIdx < 5; takerIdx++)
			assertTrue(battle.getTakeTreasurePlayerOrder().get(takerIdx) == medium2);
		battle.removeHelper();
		for (int count = 1; count <= 10; count++)
			easy1.getEquippedItems().removeLast();
		// playerVal = 2900
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
		for (int takerIdx = 0; takerIdx < 5; takerIdx++)
			assertTrue(battle.getTakeTreasurePlayerOrder().get(takerIdx) == medium2);
		battle.removeHelper();
		for (int count = 8; count >= 0; count--) {
			easy1.getEquippedItems().removeLast();
			// playerVal = 2800 : 2000
			(new AskHelpDialog(battle, false)).setVisible(true);
			assertTrue(battle.isHelper());
			assertTrue(battle.helper == medium2);
			assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
			for (int takerIdx = 0; takerIdx < 5; takerIdx++)
				if (takerIdx == (count + 1) / 2)
					assertTrue(battle.getTakeTreasurePlayerOrder().get(takerIdx) == easy1);
				else
					assertTrue(battle.getTakeTreasurePlayerOrder().get(takerIdx) == medium2);
			battle.removeHelper();
		}
		for (int count = 1; count <= 5; count++) {
			easy1.getEquippedItems().removeLast();
			// playerVal = 1900 : 1500
			(new AskHelpDialog(battle, false)).setVisible(true);
			assertTrue(battle.isHelper());
			assertTrue(battle.helper == medium2);
			assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
			assertTrue(battle.getTakeTreasurePlayerOrder().getFirst() == easy1);
			for (int takerIdx = 1; takerIdx < 5; takerIdx++)
				assertTrue(battle.getTakeTreasurePlayerOrder().get(takerIdx) == medium2);
			battle.removeHelper();
		}
		for (int count = 1; count <= 2; count++) {
			easy1.getEquippedItems().removeLast();
			// playerVal = 1400 : 1300
			(new AskHelpDialog(battle, false)).setVisible(true);
			assertTrue(battle.isHelper());
			assertTrue(battle.helper == medium2);
			assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == medium2);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == medium2);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == medium2);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(3) == easy1);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(4) == easy1);
			battle.removeHelper();
		}
		for (int count = 1; count <= 2; count++) {
			easy1.getEquippedItems().removeLast();
			// playerVal = 1200 : 1100
			(new AskHelpDialog(battle, false)).setVisible(true);
			assertTrue(battle.isHelper());
			assertTrue(battle.helper == medium2);
			assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == medium2);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == medium2);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == easy1);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(3) == medium2);
			assertTrue(battle.getTakeTreasurePlayerOrder().get(4) == easy1);
			battle.removeHelper();
		}
		easy1.getEquippedItems().removeLast();
		// playerVal = 1000
		(new AskHelpDialog(battle, false)).setVisible(true);
		assertTrue(battle.isHelper());
		assertTrue(battle.helper == medium2);
		assertEquals(battle.getTakeTreasurePlayerOrder().size(), 5);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(0) == medium2);
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) == battle.getTakeTreasurePlayerOrder().get(4));
		assertTrue(battle.getTakeTreasurePlayerOrder().get(2) == battle.getTakeTreasurePlayerOrder().get(3));
		assertTrue(battle.getTakeTreasurePlayerOrder().get(1) != battle.getTakeTreasurePlayerOrder().get(2));
		battle.removeHelper();
	}
}
