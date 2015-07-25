
package ai;

import gui.GUI;
import gui.MockGUI;

import java.util.LinkedList;
import java.util.Stack;

import ai.BattleMonsterInfo.BattleMonster;

import model.Battle;
import model.DoorDeckFactory;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.TreasureDeckFactory;
import model.UnitTest;
import model.card.Card;
import model.card.EquipmentCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.RaceCard;

public class BattleMonsterInfoTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private Battle battle;
	private BattleMonsterInfo bminfo;
	private Player easy;
	private Player medium;
	private Player hard;
	private Player human;
	
	private MonsterCard chicken;
	private MonsterCard frogs;
	private MonsterCard plant;
	private MonsterCard dragon;
	
	private MonsterEnhancerCard enraged;
	private OtherDoorCard mate;
	
	private EquipmentCard farmor;
	private EquipmentCard kneepads;
	
	public int testAll() {
		initializeObjects();

		testBattleMonster();
		testBattleMonsterInfo();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		GUI mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		human = new Player(mockGUI, "human", false, PlayerType.HUMAN);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		players.add(human);
		GM.newTestGame(mockGUI, players);
		
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck) {
			if (card.getID() == Card.M_LARGE_ANGRY_CHICKEN)
				chicken = (MonsterCard)card;
			else if (card.getID() == Card.M_FLYING_FROGS)
				frogs = (MonsterCard)card;
			else if (card.getID() == Card.M_POTTED_PLANT)
				plant = (MonsterCard)card;
			else if (card.getID() == Card.M_PLUTONIUM_DRAGON)
				dragon = (MonsterCard)card;
			else if (card.getID() == Card.ME_ENRAGED)
				enraged = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.OD_MATE)
				mate = (OtherDoorCard)card;
		}
		
		Stack<Card> treasureDeck = TreasureDeckFactory.buildDeck();
		for (Card card : treasureDeck) {
			if (card.getID() == Card.E_FLAMING_ARMOR)
				farmor = (EquipmentCard)card;
			else if (card.getID() == Card.E_KNEEPADS_OF_ALLURE)
				kneepads = (EquipmentCard)card;
		}
		
		GM.moveToBattlePhase();
		battle = new Battle(easy, chicken);
		bminfo = new BattleMonsterInfo(battle, easy);
	}
	
	private void testBattleMonster() {
		easy.goUpLevel(false);
		easy.getEquippedItems().add(farmor);
		medium.goUpLevels(2, false);
		medium.addRaceCard(new RaceCard(1, "Elf", Race.ELF));
		BattleMonster batmon = bminfo.new BattleMonster(chicken, 11, false, battle, medium);	
		assertTrue(batmon.card == chicken);
		assertEquals(batmon.battleLevel, 11);
		assertEquals(batmon.treasures, 1);
		assertFalse(batmon.hasMate);
		assertTrue(batmon.activePlayerWinLevels == 2);
		assertEquals(batmon.activePlayerBadStuffCost, 666);
		assertEquals(batmon.helperWinLevels, 0);
		assertEquals(batmon.helperBadStuffCost, 0);
		
		battle.addHelper(medium);	
		battle.replaceMonster(chicken, frogs);
		battle.addMonsterEnhancer(frogs, enraged);
		battle.addMonsterEnhancer(frogs, mate);
		batmon = bminfo.new BattleMonster(frogs, 3, true, battle, easy);
		assertTrue(batmon.card == frogs);
		assertEquals(batmon.battleLevel, 3);
		assertEquals(batmon.treasures, 4);
		assertTrue(batmon.hasMate);
		assertTrue(batmon.activePlayerWinLevels == 2);
		assertEquals(batmon.activePlayerBadStuffCost, 1000);
		assertEquals(batmon.helperWinLevels, 2);
		assertEquals(batmon.helperBadStuffCost, 1666);
	}
	
	private void testBattleMonsterInfo() {
		battle.addMonster(plant);
		battle.addMonster(chicken);
		battle.addMonster(dragon);
		
		// strongest monsters first
		bminfo = new BattleMonsterInfo(battle, easy);
		assertEquals(bminfo.getBattleMonsters().size(), 4);
		assertTrue(bminfo.getBattleMonsters().get(0).card == dragon);
		assertTrue(bminfo.getBattleMonsters().get(1).card == frogs);
		assertTrue(bminfo.getBattleMonsters().get(2).card == chicken);
		assertTrue(bminfo.getBattleMonsters().get(3).card == plant);
		
		assertEquals(bminfo.getPlayerWinLevels(easy), 7);
		assertEquals(bminfo.getPlayerWinLevels(medium), 5);
		assertEquals(bminfo.getPlayerWinLevels(hard), 0);
		assertEquals(bminfo.getPlayerWinLevels(human), 0);
		
		assertEquals(bminfo.getPlayerBadStuffCost(easy), 1666);
		assertEquals(bminfo.getPlayerBadStuffCost(medium), 2166);
		assertEquals(bminfo.getPlayerBadStuffCost(hard), 0);
		assertEquals(bminfo.getPlayerBadStuffCost(human), 0);
		
		// "Kneepads of Allure" & takeTreasurePlayerOrder
		assertEquals(bminfo.getPlayerWinTreasureValue(easy), 12 * AIManager.UNKNOWN_CARD_VALUE);
		assertEquals(bminfo.getPlayerWinTreasureValue(medium), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(hard), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(human), 0);
		
		LinkedList<Player> playerOrder = new LinkedList<Player>();
		playerOrder.add(medium);
		playerOrder.add(easy);
		playerOrder.add(easy);
		playerOrder.add(medium);
		playerOrder.add(medium);
		playerOrder.add(medium);
		playerOrder.add(easy);
		playerOrder.add(medium);
		playerOrder.add(medium);
		playerOrder.add(easy);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		easy.getEquippedItems().add(kneepads);
		bminfo = new BattleMonsterInfo(battle, easy);
		assertEquals(bminfo.getPlayerWinTreasureValue(easy), 12 * AIManager.UNKNOWN_CARD_VALUE);
		assertEquals(bminfo.getPlayerWinTreasureValue(medium), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(hard), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(human), 0);
		
		easy.getEquippedItems().remove(kneepads);
		bminfo = new BattleMonsterInfo(battle, easy);
		assertEquals(bminfo.getPlayerWinTreasureValue(easy), 1200 + 1100 + 700 + 400 + 250);
		assertEquals(bminfo.getPlayerWinTreasureValue(medium), 1300 + 1000 + 900 + 800 + 600 + 500 + 250);
		assertEquals(bminfo.getPlayerWinTreasureValue(hard), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(human), 0);
		
		playerOrder.clear();
		playerOrder.add(human);
		playerOrder.add(easy);
		playerOrder.add(easy);
		playerOrder.add(human);
		playerOrder.add(human);
		playerOrder.add(human);
		playerOrder.add(easy);
		playerOrder.add(human);
		playerOrder.add(human);
		playerOrder.add(easy);
		battle.setTakeTreasurePlayerOrder(playerOrder);
		battle.addHelper(human);
		bminfo = new BattleMonsterInfo(battle, medium);
		assertEquals(bminfo.getPlayerWinTreasureValue(easy), 1200 + 1100 + 700 + 400);
		assertEquals(bminfo.getPlayerWinTreasureValue(medium), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(hard), 0);
		assertEquals(bminfo.getPlayerWinTreasureValue(human), 1300 + 1000 + 900 + 800 + 600 + 500 + 300);
		
		assertEquals(bminfo.getPlayerWinLevels(easy), 7);
		assertEquals(bminfo.getPlayerWinLevels(medium), 0);
		assertEquals(bminfo.getPlayerWinLevels(hard), 0);
		assertEquals(bminfo.getPlayerWinLevels(human), 0);
		
		assertEquals(bminfo.getPlayerBadStuffCost(easy), 1666);
		assertEquals(bminfo.getPlayerBadStuffCost(medium), 0);
		assertEquals(bminfo.getPlayerBadStuffCost(hard), 0);
		assertEquals(bminfo.getPlayerBadStuffCost(human), 0);
	}
}
