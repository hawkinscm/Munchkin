
package ai;

import gui.GUI;
import gui.MockGUI;

import java.util.LinkedList;
import java.util.Stack;

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
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;
import model.card.TreasureCard;
import model.card.EquipmentCard.EquipmentType;

public class AICardEvaluatorTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private GUI mockGUI;
	private LinkedList<Player> players;
	
	private Player easy;
	private Player medium;
	private Player hard;
	private Player human;
	
	private MonsterCard fchicken;
	private MonsterCard nothing;
	private MonsterCard plant;
	private MonsterCard salesman;
	private MonsterCard ghouls;
	private MonsterCard nose;
	private MonsterCard vampire;
	private MonsterCard bones;
	private MonsterCard tdemon;
	private MonsterCard amazon;
	private MonsterCard lawyer;
	
	private CurseCard loseLevel;
	private CurseCard headChicken;
	private CurseCard loseHelm;
	private CurseCard mirror;
	private CurseCard changeSex;
	private CurseCard loseCards;
	
	private OtherDoorCard illusion;
	private OtherDoorCard wander;
	
	private EquipmentCard farmor;
	private EquipmentCard fstaff;
	private EquipmentCard fthrower;
	private EquipmentCard bow;
	private EquipmentCard hammer;
	private EquipmentCard helmet;
	private EquipmentCard ladder;
	private EquipmentCard noThief;
	private EquipmentCard noWarrior;
	private EquipmentCard noWizard;
	private EquipmentCard noCleric;
	private EquipmentCard maleOnly;
	private EquipmentCard femaleOnly;
	private EquipmentCard grater;
	private EquipmentCard cloak;
	private EquipmentCard shield;
	private EquipmentCard bandanna;
	private EquipmentCard swiss;
	private EquipmentCard rock;
	private EquipmentCard boots;
	private EquipmentCard sandals;
	private EquipmentCard tuba;
		
	private ItemCard fpotion;
	private ItemCard halitosis;
	private ItemCard water;
	
	private OtherTreasureCard hireling;
		
	public int testAll() {
		initializeObjects();

		testGetCardValueToPlayer();
		testGetMonsterCardValueToPlayer();
		testGetRaceCardValueToPlayer();
		testGetClassCardValueToPlayer();
		testGetCurseCardValueToPlayer();
		testGetMonsterEnhancerCardValueToPlayer();
		testGetOtherDoorCardValueToPlayer();
		testGetEquipmentCardValueToPlayer();
		testGetItemCardValueToPlayer();
		testGetLevelCardValueToPlayer();
		testGetOtherTreasureCardValueToPlayer();
				
		return errorCount;
	}
	
	private void initializeObjects() {
		mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		human = new Player(mockGUI, "human", false, PlayerType.HUMAN);
		players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		players.add(human);
		GM.newTestGame(mockGUI, players);
		
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck) {
			if (card.getID() == Card.M_LARGE_ANGRY_CHICKEN)
				fchicken = (MonsterCard)card;
			else if (card.getID() == Card.M_THE_NOTHING)
				nothing = (MonsterCard)card;
			else if (card.getID() == Card.M_POTTED_PLANT)
				plant = (MonsterCard)card;
			else if (card.getID() == Card.M_INSURANCE_SALESMAN)
				salesman = (MonsterCard)card;
			else if (card.getID() == Card.M_GHOULFIENDS)
				ghouls = (MonsterCard)card;
			else if (card.getID() == Card.M_TONGUE_DEMON) 
				tdemon = (MonsterCard)card;
			else if (card.getID() == Card.M_FLOATING_NOSE) 
				nose = (MonsterCard)card;
			else if (card.getID() == Card.M_WANNABE_VAMPIRE) 
				vampire = (MonsterCard)card;
			else if (card.getID() == Card.M_AMAZON) 
				amazon = (MonsterCard)card;
			else if (card.getID() == Card.M_LAWYER) 
				lawyer = (MonsterCard)card;
			else if (card.getID() == Card.M_MR_BONES)
				bones = (MonsterCard)card;
			else if (card.getID() == Card.CU_LOSE_1_LEVEL_1)
				loseLevel = (CurseCard)card;
			else if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
				headChicken = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING)
				loseHelm = (CurseCard)card;
			else if (card.getID() == Card.CU_MALIGN_MIRROR)
				mirror = (CurseCard)card;
			else if (card.getID() == Card.CU_CHANGE_SEX)
				changeSex = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_TWO_CARDS)
				loseCards = (CurseCard)card;
			else if (card.getID() == Card.OD_ILLUSION)
				illusion = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_WANDERING_MONSTER_2)
				wander = (OtherDoorCard)card;
		}
		
		Stack<Card> treasureDeck = TreasureDeckFactory.buildDeck();
		for (Card card : treasureDeck) {
			if (card.getID() == Card.E_FLAMING_ARMOR)
				farmor = (EquipmentCard)card;
			else if (card.getID() == Card.E_STAFF_OF_NAPALM)
				fstaff = (EquipmentCard)card;
			else if (card.getID() == Card.E_FLAMETHROWER)
				fthrower = (EquipmentCard)card;
			else if (card.getID() == Card.E_BOW_WITH_RIBBONS)
				bow = (EquipmentCard)card;
			else if (card.getID() == Card.E_HAMMER_OF_KNEECAPPING)
				hammer = (EquipmentCard)card;
			else if (card.getID() == Card.E_HORNED_HELMET)
				helmet = (EquipmentCard)card;
			else if (card.getID() == Card.E_STEPLADDER)
				ladder = (EquipmentCard)card;
			else if (card.getID() == Card.E_SINGING_AND_DANCING_SWORD)
				noThief = (EquipmentCard)card;
			else if (card.getID() == Card.E_PANTYHOSE_OF_GIANT_STRENGTH)
				noWarrior = (EquipmentCard)card;
			else if (card.getID() == Card.E_MITHRIL_ARMOR)
				noWizard = (EquipmentCard)card;
			else if (card.getID() == Card.E_KNEEPADS_OF_ALLURE)
				noCleric = (EquipmentCard)card;
			else if (card.getID() == Card.E_GENTLEMENS_CLUB)
				maleOnly = (EquipmentCard)card;
			else if (card.getID() == Card.E_BROAD_SWORD)
				femaleOnly = (EquipmentCard)card;
			else if (card.getID() == Card.E_CHEESE_GRATER_OF_PEACE)
				grater = (EquipmentCard)card;
			else if (card.getID() == Card.E_CLOAK_OF_OBSCURITY)
				cloak = (EquipmentCard)card;
			else if (card.getID() == Card.E_SHIELD_OF_UBIQUITY)
				shield = (EquipmentCard)card;
			else if (card.getID() == Card.E_RAD_BANDANNA)
				bandanna = (EquipmentCard)card;
			else if (card.getID() == Card.E_SWISS_ARMY_POLEARM)
				swiss = (EquipmentCard)card;
			else if (card.getID() == Card.E_HUGE_ROCK)
				rock = (EquipmentCard)card;
			else if (card.getID() == Card.E_BOOTS_OF_RUNNING_REALLY_FAST)
				boots = (EquipmentCard)card;
			else if (card.getID() == Card.E_SANDALS_OF_PROTECTION)
				sandals = (EquipmentCard)card;
			else if (card.getID() == Card.E_TUBA_OF_CHARM)
				tuba = (EquipmentCard)card;
			else if (card.getID() == Card.I_FLAMING_POISON_POTION)
				fpotion = (ItemCard)card;
			else if (card.getID() == Card.I_POTION_OF_HALITOSIS)
				halitosis = (ItemCard)card;
			else if (card.getID() == Card.I_YUPPIE_WATER)
				water = (ItemCard)card;
			else if (card.getID() == Card.OT_HIRELING)
				hireling = (OtherTreasureCard)card;
		}
	}
	
	private void testGetCardValueToPlayer() {
		// test card evaluation loop		
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseCards, hard, hard.getHandCards()), 0);
		hard.addCard(loseCards);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseCards, hard, hard.getHandCards()), 0);
		hard.addCard(boots);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseCards, hard, hard.getHandCards()), 0);
		hard.addCard(cloak);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseCards, hard, hard.getHandCards()), 0);
		hard.getHandCards().clear();
	}
	
	private void testGetMonsterCardValueToPlayer() {
		Player player = easy;
		MonsterCard monster = fchicken;		
		double chanceToWin = 0.5 + (player.getLevel() - monster.getLevel(player)) / 20.0;
		double winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		double badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.goUpLevel(false);
		chanceToWin = 0.5 + (player.getLevel() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.goUpLevel(false);
		chanceToWin = 0.5 + (player.getLevel() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.addClassCard(new ClassCard(1, "Warrior", Class.WARRIOR));
		chanceToWin = 0.5 + (player.getLevel() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getEquippedItems().add(farmor);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * (monster.getWinLevels() + 1) + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getEquippedItems().set(0, fstaff);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * (monster.getWinLevels() + 1) + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getEquippedItems().set(0, fthrower);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * (monster.getWinLevels() + 1) + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getEquippedItems().clear();
		easy.addItem(fpotion);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 4 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * (monster.getWinLevels() + 1) + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.discard(fpotion);
		easy.addCard(new GoUpLevelCard(2, "level"));
		easy.goUpLevels(2, false);
		monster = nothing;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.goUpLevel(false);
		assert(easy.getLevel() == monster.getLevel(easy));
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * (monster.getWinLevels() + 1) + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 1 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * (monster.getWinLevels() + 1) + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, player.getHandCards()) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(winValue - badValue));
		assertFalse(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()) == AICardEvaluator.getCardValueToPlayer(monster, easy, null));
		
		easy.getClassCards().clear();
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		monster = plant;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.addRaceCard(new RaceCard(3, "Elf", Race.ELF));
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * (monster.getTreasures() + 1)) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getEquippedItems().add(farmor);
		easy.getEquippedItems().add(fstaff);
		easy.addItem(halitosis);
		easy.addItem(new ItemCard(4, "test", 1000));
		monster = salesman;
		chanceToWin = 0.5 + (player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		easy.getCarriedItems().removeLast();
		
		monster = ghouls;
		chanceToWin = 0.5 + (player.getLevel() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), 0);
		
		player = medium;
		medium.getEquippedItems().add(fthrower);
		chanceToWin = 0.5 + (player.getLevel() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, medium, null), (int)Math.round(winValue - badValue));
		
		player = easy;
		monster = nose;
		chanceToWin = 1.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.discard(halitosis);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
				
		monster = vampire;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.addClassCard(new ClassCard(5, "Cleric", Class.CLERIC));
		easy.getEquippedItems().add(easy.getUnequippedItems().remove(0));
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = 1000 * monster.getWinLevels() + 800 * monster.getTreasures();
		badValue = 1000 * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.discard(fstaff);
		monster = bones;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 4 - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getClassCards().clear();
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		easy.getRaceCards().clear();
		easy.goUpLevels(2, false);
		easy.addItem(new ItemCard(6, "test", 500, 0));
		monster = tdemon;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		badValue += 500;
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		easy.getCarriedItems().removeLast();
						
		easy.getEquippedItems().clear();
		assertTrue(easy.getAllItems().isEmpty());
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		monster = amazon;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, medium, null), 800);
		
		monster = lawyer;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		winValue = (1000 * monster.getWinLevels() + 800 * monster.getTreasures()) * chanceToWin;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue - badValue));
		
		hard.addClassCard(new ClassCard(7, "Thief", Class.THIEF));
		hard.addCard(new ItemCard(8, "item1", 100));
		hard.addCard(new ItemCard(9, "item2", 200));
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, hard, null), 100);
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, hard, hard.getHandCards()), 1300);
		
		easy.getEquippedItems().add(fthrower);
		easy.addItem(halitosis);
		monster = nose;
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 3 - monster.getLevel(player)) / 20.0;
		winValue = 1000 * monster.getWinLevels() + 800 * monster.getTreasures();
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(winValue));
		
		easy.addCard(wander);
		medium.goUpLevels(2, false);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 3 - monster.getLevel(player)) / 20.0;
		winValue = 1000 * monster.getWinLevels() + 800 * monster.getTreasures();
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(winValue));
		
		easy.discard(halitosis);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		double enemyChanceToWin = 0.5 + (medium.getLevel() + medium.getEquipmentBonus() - (monster.getLevel(medium) + 5)) / 20.0;
		double enemyValue = AIManager.getBadStuffCost(monster, false, medium, false, null) * (1.0 - enemyChanceToWin);
		enemyValue -= winValue * enemyChanceToWin;
		double rankFactor = (3.0 / 4.0) * 2.0;
		enemyValue *= rankFactor;
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(enemyValue));
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue * chanceToWin - badValue));
		
		easy.addCard(illusion);
		easy.addItem(halitosis);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 3 - monster.getLevel(player)) / 20.0;
		winValue = 1000 * monster.getWinLevels() + 800 * monster.getTreasures();
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(winValue));
		
		easy.discard(halitosis);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		enemyChanceToWin = 0.5 + (medium.getLevel() + medium.getEquipmentBonus() - (monster.getLevel(medium) + 5)) / 20.0;
		enemyValue = AIManager.getBadStuffCost(monster, false, medium, false, null) * (1.0 - enemyChanceToWin);
		enemyValue -= winValue * enemyChanceToWin;
		rankFactor = (3.0 / 4.0) * 2.0;
		enemyValue *= rankFactor;
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(enemyValue));
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue * chanceToWin - badValue));
		
		easy.discard(wander);
		easy.addItem(halitosis);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() + 3 - monster.getLevel(player)) / 20.0;
		winValue = 1000 * monster.getWinLevels() + 800 * monster.getTreasures();
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(winValue));
		
		easy.discard(halitosis);
		chanceToWin = 0.5 + (player.getLevel() + player.getEquipmentBonus() - monster.getLevel(player)) / 20.0;
		badValue = AIManager.getBadStuffCost(monster, false, player, false, null) * (1.0 - chanceToWin);
		enemyChanceToWin = 0.5 + (medium.getLevel() + medium.getEquipmentBonus() - monster.getLevel(medium)) / 20.0;
		enemyValue = AIManager.getBadStuffCost(monster, false, medium, false, null) * (1.0 - enemyChanceToWin);
		enemyValue -= winValue * enemyChanceToWin;
		rankFactor = (3.0 / 4.0) * 2.0;
		enemyValue *= rankFactor;
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, easy.getHandCards()), (int)Math.round(enemyValue));
		assertEquals(AICardEvaluator.getCardValueToPlayer(monster, easy, null), (int)Math.round(winValue * chanceToWin - badValue));
	}
	
	private void testGetRaceCardValueToPlayer() {		
		RaceCard elf = new RaceCard(4, "Elf", Race.ELF);
		RaceCard dwarf = new RaceCard(5, "Dwarf", Race.DWARF);
		RaceCard halfling = new RaceCard(6, "Halfling", Race.HALFLING);
		RaceCard elf2 = new RaceCard(7, "Elf2", Race.ELF);
		RaceCard elf3 = new RaceCard(8, "Elf3", Race.ELF);
		
		EquipmentCard big1 = new EquipmentCard(1, "big1", 300, 1, EquipmentType.OTHER, true, false, false);
		EquipmentCard big2 = new EquipmentCard(2, "big2", 650, 2, EquipmentType.OTHER, true, false, false);
		EquipmentCard big3 = new EquipmentCard(3, "big3", 1000, 3, EquipmentType.OTHER, true, false, false);
		
		hard.getRaceCards().clear();
		// test initial values: dwarf&elf=450,halfling=50
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, null), 450);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, null), 450);
		assertEquals(AICardEvaluator.getCardValueToPlayer(halfling, hard, null), 50);
		
		hard.addCard(water);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, null), 450);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), 450 + 100);
		hard.discard(water);
		hard.addItem(water);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, null), 450 + 100);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), 450 + 100);
		
		hard.getEquippedItems().add(big2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, null), 450);
		hard.getEquippedItems().add(big1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, null), 450 + 100);
		hard.setHirelingCard(hireling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, null), 450);
		hard.addCard(big3);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, null), 450);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, hard.getHandCards()), 450 + 200);
		hard.discard(big2);
		hard.addCard(big2);
		hard.discardHirelingCard();
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, hard.getHandCards()), 450 + 350);
		
		hard.getEquippedItems().clear();
		hard.getHandCards().clear();
		hard.addUnequippedItem(bow);
		hard.addUnequippedItem(hammer);
		hard.addUnequippedItem(ladder);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), 450 + 100 + 800); // bow 4,800 = 800
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, hard.getHandCards()), 450 + 1000); // hammer 4,600 = 1000
		assertEquals(AICardEvaluator.getCardValueToPlayer(halfling, hard, hard.getHandCards()), 50 + 800); // ladder 3,400 = 800
		hard.addUnequippedItem(helmet);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), 450 + 100 + 800 + 600); // helmet 1/3,600 = 600
			
		hard.getRaceCards().clear();
		hard.addRaceCard(dwarf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, hard.getHandCards()), 450 + 1000);
		hard.getRaceCards().clear();
		hard.addRaceCard(halfling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(halfling, hard, hard.getHandCards()), 50 + 800);
		hard.getRaceCards().clear();
		hard.addRaceCard(elf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), 450 + 100 + 800 + 600);
		
		hard.getRaceCards().clear();		
		hard.addCard(dwarf);
		hard.addCard(halfling);
		hard.addCard(elf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(dwarf, hard, hard.getHandCards()), 450 + 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(halfling, hard, hard.getHandCards()), 50 + 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), 450 + 100 + 800 + 600);
		
		hard.getRaceCards().add(elf2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf2, hard, hard.getHandCards()), 450 + 100 + 800 + 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, null), (450 + 100 + 800 + 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf, hard, hard.getHandCards()), (450 + 100 + 800 + 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf3, hard, null), (450 + 100 + 800 + 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(elf3, hard, hard.getHandCards()), (450 + 100 + 800 + 600) / 10);
	}
	
	private void testGetClassCardValueToPlayer() {
		easy.goDownLevels(10);
		easy.getEquippedItems().clear();
		medium.goDownLevels(10);
		medium.getEquippedItems().clear();
		hard.goDownLevels(10);
		hard.getEquippedItems().clear();
		
		ClassCard warrior = new ClassCard(1, "Warrior", Class.WARRIOR);
		ClassCard wizard = new ClassCard(2, "Wizard", Class.WIZARD);
		ClassCard thief = new ClassCard(3, "Thief", Class.THIEF);
		ClassCard cleric = new ClassCard(4, "Cleric", Class.CLERIC);
		ClassCard wizard2 = new ClassCard(5, "Wizard2", Class.WIZARD);
		ClassCard wizard3 = new ClassCard(6, "Wizard3", Class.WIZARD);
				
		// test initial values := 500
		assertEquals(AICardEvaluator.getCardValueToPlayer(warrior, human, null), 500);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, null), 500);
		assertEquals(AICardEvaluator.getCardValueToPlayer(thief, human, null), 500);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 500);
		
		human.addUnequippedItem(grater);
		human.addUnequippedItem(cloak);
		human.addUnequippedItem(shield);
		human.addUnequippedItem(fstaff);
		assertEquals(AICardEvaluator.getCardValueToPlayer(warrior, human, human.getHandCards()), 500 + 1000); // shield 4,600 = 1000
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, human.getHandCards()), 500 + 1200); // fstaff 5,800 = 1200
		assertEquals(AICardEvaluator.getCardValueToPlayer(thief, human, human.getHandCards()), 500 + 1000); // cloak 4,600 = 1000
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, human.getHandCards()), 500 + 800); // grater 3,400 = 800
		
		human.addUnequippedItem(noWarrior);
		human.addUnequippedItem(noWizard);
		human.addUnequippedItem(noThief);
		human.addUnequippedItem(noCleric);
		assertEquals(AICardEvaluator.getCardValueToPlayer(warrior, human, null), 500 + 1000 - 600); // noWarrior 3,600 = 600
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, human.getHandCards()), 500 + 1200 - 600); // noWizard 3,600 = 600
		assertEquals(AICardEvaluator.getCardValueToPlayer(thief, human, null), 500 + 1000 - 400); // noThief 2,400 = 400
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, human.getHandCards()), 500 + 800 - 500); // noCleric = help bonus
		
		// test help bonus value for kneepads
		easy.goUpLevel(false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 500 + 800 - (1100 - 600));
		easy.goUpLevel(false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 500 + 800 - (1200 - 600));
		easy.goUpLevel(false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 500 + 800 - (1600 - 600));
		easy.goUpLevel(false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 0); //500 + 800 - (2000 - 600));
		easy.addRaceCard(new RaceCard(7, "Elf", Race.ELF));
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 500 + 800 - (1100 - 600));
		easy.getEquippedItems().add(fthrower);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, null), 0); //500 + 800 - (2200 - 600));
		
		human.addClassCard(warrior);
		assertEquals(AICardEvaluator.getCardValueToPlayer(warrior, human, human.getHandCards()), 500 + 1000 - 600);
		human.getClassCards().set(0, wizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, human.getHandCards()), 500 + 1200 - 600);
		human.addCard(wizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, null), 500 + 1200 - 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, human.getHandCards()), 500 + 1200 - 600);
		human.getClassCards().set(0, thief);
		assertEquals(AICardEvaluator.getCardValueToPlayer(thief, human, human.getHandCards()), 500 + 1000 - 400);
		human.getClassCards().set(0, cleric);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cleric, human, human.getHandCards()), 500 + 800 - (2200 - 600));
		
		human.getClassCards().clear();
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, null), 500 + 1200 - 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, human.getHandCards()), 500 + 1200 - 600);
		human.addClassCard(wizard2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, null), (500 + 1200 - 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard, human, human.getHandCards()), (500 + 1200 - 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard3, human, null), (500 + 1200 - 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard3, human, human.getHandCards()), (500 + 1200 - 600) / 10);
		human.addCard(wizard3);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard3, human, null), (500 + 1200 - 600) / 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(wizard3, human, human.getHandCards()), (500 + 1200 - 600) / 10);
	}
	
	private void testGetCurseCardValueToPlayer() {
		for (Player player : GM.getPlayers()) {
			player.goDownLevels(10);
			player.getRaceCards().clear();
			player.getClassCards().clear();
			player.getEquippedItems().clear();
			player.getUnequippedItems().clear();
			player.getCarriedItems().clear();
			player.getHandCards().clear();
		}
		
		medium.goUpLevel(false);    // rankFactor = 0.5
		easy.goUpLevels(2, false);  // rankFactor = 1
		hard.goUpLevels(3, false);  // rankFactor = 1.5
		human.goUpLevels(4, false); // rankFactor = 2
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, easy, null), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, medium, null), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, hard, null), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, human, null), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, easy, easy.getHandCards()), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, medium, medium.getHandCards()), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, hard, hard.getHandCards()), 1000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseLevel, human, human.getHandCards()), 1000);
		
		medium.addLastingCurse(headChicken);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseHelm, easy, easy.getHandCards()), 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseHelm, medium, medium.getHandCards()), 300);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseHelm, hard, hard.getHandCards()), 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(loseHelm, human, human.getHandCards()), 0);
		medium.removeChickenOnHeadCurse();
	}
	
	private void testGetMonsterEnhancerCardValueToPlayer() {
		MonsterEnhancerCard enhancer = new MonsterEnhancerCard(0, "enhancer", 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 0);
		enhancer = new MonsterEnhancerCard(1, "enhancer", 1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 100);
		enhancer = new MonsterEnhancerCard(2, "enhancer", 2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 200);
		enhancer = new MonsterEnhancerCard(3, "enhancer", 3);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 300);
		enhancer = new MonsterEnhancerCard(4, "enhancer", 4);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 400);
		enhancer = new MonsterEnhancerCard(5, "enhancer", 5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 500);
		enhancer = new MonsterEnhancerCard(7, "enhancer", 7);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 700);
		enhancer = new MonsterEnhancerCard(10, "enhancer", 10);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 1000);
		enhancer = new MonsterEnhancerCard(-1, "enhancer", -1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 100);
		enhancer = new MonsterEnhancerCard(-5, "enhancer", -5);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 500);
		enhancer = new MonsterEnhancerCard(-10, "enhancer", -10);
		assertEquals(AICardEvaluator.getCardValueToPlayer(enhancer, human, human.getHandCards()), 1000);
	}
	
	private void testGetOtherDoorCardValueToPlayer() {
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck) {
			if (card instanceof OtherDoorCard) {
				if (card.getID() == Card.OD_CHEAT || card.getID() == Card.OD_HELP_ME_OUT_HERE) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 1200);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 1200);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 1200);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, null), 1200);
				}
				else if (card.getID() == Card.OD_DIVINE_INTERVENTION) {
					ClassCard cleric = new ClassCard(1, "Cleric", Class.CLERIC);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 0);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 0);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 0);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, null), 0);
					
					easy.addClassCard(cleric);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 0);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), 0);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), 0);
					
					medium.addClassCard(cleric);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 1000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 1000 - 1000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 0);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, null), 0);
					
					hard.addClassCard(cleric);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000 - 1500);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 1000 - 1000 - 1500);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), 1000 - 1000 - 500);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), 0);
					
					human.addClassCard(cleric);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000 - 1500 - 2000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 1000 - 1000 - 1500 - 2000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), 1000 - 1000 - 500 - 2000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), 1000 - 1500 - 1000 - 500);
					
					human.goUpLevels(4, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), 10000);
					
					human.goDownLevels(4);
					hard.goUpLevels(5, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), 10000 - 1500 - 1000 - 500);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), -10000);
					
					medium.goUpLevels(7, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer((OtherDoorCard)card, medium, null), 10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), -10000);
					
					easy.goUpLevels(6, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, null), -10000);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, human, human.getHandCards()), -10000);
					
					easy.goDownLevels(10);
					easy.getClassCards().clear();
					medium.goDownLevels(10);
					medium.getClassCards().clear();
					hard.goDownLevels(10);
					hard.getClassCards().clear();
					human.goDownLevels(10);
					human.getClassCards().clear();
				}
				else if (card.getID() == Card.OD_HALF_BREED_1) {
					RaceCard elf = new RaceCard(1, "Elf", Race.ELF);
					RaceCard halfling = new RaceCard(2, "Halfling", Race.HALFLING);
					
					medium.addCard(halfling);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 100);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 300);
					medium.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 300);
					medium.getRaceCards().set(0, halfling);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 500);
					
					medium.getRaceCards().clear();
					medium.setHalfBreedCard((OtherDoorCard)card);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 100);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);
					medium.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);
					medium.addRaceCard(halfling);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 300);
					medium.getRaceCards().removeFirst();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 300);
					
					OtherDoorCard halfBreed2 = new OtherDoorCard(Card.OD_HALF_BREED_2, "Half-Breed");
					medium.getRaceCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, null), 25);
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, medium.getHandCards()), 25);
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, medium.getHandCards()), 25);
					medium.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, medium.getHandCards()), 25);
					medium.addRaceCard(halfling);
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, medium.getHandCards()), 25);
					medium.getRaceCards().removeFirst();
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, medium.getHandCards()), 25);
					
					medium.getRaceCards().clear();
					medium.setHalfBreedCard(null);
					medium.getHandCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(halfBreed2, medium, medium.getHandCards()), 100);
				}
				else if (card.getID() == Card.OD_ILLUSION) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300);
					
					easy.addCard(amazon);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300 + 200);
					
					easy.addCard(bones);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300 + 400);
					
					easy.addCard(fchicken);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300 + 600);
					
					easy.addCard(ghouls);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300 + 800);
					
					easy.addCard(nothing);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300 + 1000);
					
					easy.getHandCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 300);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 300);
				}
				else if (card.getID() == Card.OD_MATE) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
				}
				else if (card.getID() == Card.OD_OUT_TO_LUNCH) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1600 + 1000);
				}
				else if (card.getID() == Card.OD_SUPER_MUNCHKIN_2) {
					ClassCard warrior = new ClassCard(2, "Warrior", Class.WARRIOR);
					ClassCard wizard = new ClassCard(3, "Wizard", Class.WIZARD);
					
					medium.addCard(warrior);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 50);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 150);
					medium.addCard(wizard);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 500);
					medium.addClassCard(warrior);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 500);
					medium.getHandCards().removeFirst();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 500);
					medium.getHandCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 150);
					
					medium.getClassCards().clear();
					medium.setSuperMunchkinCard((OtherDoorCard)card);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, null), 100);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);
					medium.addCard(warrior);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);
					medium.addClassCard(wizard);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, medium, medium.getHandCards()), 100);

					OtherDoorCard munchkin = new OtherDoorCard(Card.OD_SUPER_MUNCHKIN_1, "Super Munchkin");
					medium.getHandCards().clear();
					medium.getClassCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(munchkin, medium, null), 25);
					assertEquals(AICardEvaluator.getCardValueToPlayer(munchkin, medium, medium.getHandCards()), 25);
					assertEquals(AICardEvaluator.getCardValueToPlayer(munchkin, medium, medium.getHandCards()), 25);
					medium.addCard(wizard);
					assertEquals(AICardEvaluator.getCardValueToPlayer(munchkin, medium, medium.getHandCards()), 25);
					medium.addClassCard(warrior);
					assertEquals(AICardEvaluator.getCardValueToPlayer(munchkin, medium, medium.getHandCards()), 25);
					
					medium.getClassCards().clear();
					medium.setSuperMunchkinCard(null);
					medium.getHandCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(munchkin, medium, medium.getHandCards()), 50);
				}
				else if (card.getID() == Card.OD_WANDERING_MONSTER_1) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					
					easy.addCard(nose);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400 + 300);
					
					easy.addCard(plant);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400 + 600);
					
					easy.addCard(salesman);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400 + 900);
					
					easy.addCard(vampire);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400 + 1200);
					
					easy.addCard(tdemon);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400 + 1500);
					
					easy.getHandCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, null), 400);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
				}
			}
		}
	}
	
	private void testGetEquipmentCardValueToPlayer() {
		EquipmentCard other1 = new EquipmentCard(1, "O1", 800, 2, EquipmentType.OTHER, false, false, false);
		EquipmentCard other2 = new EquipmentCard(2, "O2", 400, 2, EquipmentType.OTHER, false, true, false);
		EquipmentCard head1 = new EquipmentCard(3, "H1", 1900, 1, EquipmentType.HEADGEAR, false, false, true);
		EquipmentCard head2 = new EquipmentCard(4, "H2", 700, 3, EquipmentType.HEADGEAR, false, false, false);
		EquipmentCard armor1 = new EquipmentCard(5, "A1", 600, 5, EquipmentType.ARMOR, false, true, true);
		EquipmentCard armor2 = new EquipmentCard(6, "A2", 1500, 1, EquipmentType.ARMOR, false, true, false);
		EquipmentCard foot1 = new EquipmentCard(7, "F1", 0, 1, EquipmentType.FOOTGEAR, false, true, true);
		EquipmentCard foot2 = new EquipmentCard(8, "F2", 400, 4, EquipmentType.FOOTGEAR, false, false, true);
		EquipmentCard hands1 = new EquipmentCard(9, "2H1", 300, 0, EquipmentType.TWO_HANDS, false, true, true);
		EquipmentCard hands2 = new EquipmentCard(10, "2H2", 1600, 5, EquipmentType.TWO_HANDS, false, false, true);
		EquipmentCard hand1 = new EquipmentCard(11, "1H1", 0, 0, EquipmentType.ONE_HAND, false, true, true);
		EquipmentCard hand2 = new EquipmentCard(12, "1H2", 1100, 4, EquipmentType.ONE_HAND, false, false, true);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(other1, medium, medium.getHandCards()) / 10 * 10, 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(other2, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(head1, medium, medium.getHandCards()) / 10 * 10, 1900);
		assertEquals(AICardEvaluator.getCardValueToPlayer(head2, medium, null), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(armor1, medium, medium.getHandCards()), 2000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(armor2, medium, medium.getHandCards()) / 10 * 10, 1500);
		assertEquals(AICardEvaluator.getCardValueToPlayer(foot1, medium, null), 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(foot2, medium, null), 1600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands1, medium, medium.getHandCards()) / 10 * 10, 300);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands2, medium, null), 2000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand1, medium, medium.getHandCards()) / 10 * 10, 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand2, medium, null), 1600);
		
		medium.getEquippedItems().add(other1);
		medium.getEquippedItems().add(head1);
		medium.getEquippedItems().add(armor1);
		medium.getEquippedItems().add(foot1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(other1, medium, medium.getHandCards()) / 10 * 10, 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(other2, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(head1, medium, medium.getHandCards()) / 10 * 10, 1900);
		assertEquals(AICardEvaluator.getCardValueToPlayer(head2, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(armor1, medium, medium.getHandCards()), 2000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(armor2, medium, medium.getHandCards()) / 10 * 10, 1500);
		assertEquals(AICardEvaluator.getCardValueToPlayer(foot1, medium, null), 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(foot2, medium, null), 1600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands1, medium, medium.getHandCards()) / 10 * 10, 300);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands2, medium, null), 2000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand1, medium, medium.getHandCards()) / 10 * 10, 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand2, medium, null), 1600);
		
		medium.addUnequippedItem(hands1);
		medium.addUnequippedItem(hands2);
		medium.addUnequippedItem(hand1);
		medium.addUnequippedItem(hand2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands1, medium, medium.getHandCards()) / 10 * 10, 300);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands2, medium, null) / 10 * 10, 1600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand1, medium, medium.getHandCards()) / 10 * 10, 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand2, medium, null) / 10 * 10, 1100);
		
		medium.getUnequippedItems().clear();
		hands1 = new EquipmentCard(13, "2H1", 300, 2, EquipmentType.TWO_HANDS, false, true, true);
		hands2 = new EquipmentCard(14, "2H2", 250, 7, EquipmentType.TWO_HANDS, false, false, true);
		hand1 = new EquipmentCard(15, "1H1", 100, 1, EquipmentType.ONE_HAND, false, true, true);
		hand2 = new EquipmentCard(16, "1H2", 50, 3, EquipmentType.ONE_HAND, false, false, true);
		EquipmentCard hand3 = new EquipmentCard(17, "1H3", 200, 1, EquipmentType.ONE_HAND, false, true, true);
		medium.addUnequippedItem(hands1);
		medium.addUnequippedItem(hand1);
		medium.addUnequippedItem(hand2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands1, medium, medium.getHandCards()), 300);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands2, medium, null), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand1, medium, medium.getHandCards()), 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand2, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand3, medium, medium.getHandCards()), 200);
		
		hands2 = new EquipmentCard(18, "2H2", 250, 4, EquipmentType.TWO_HANDS, false, false, true);
		medium.addUnequippedItem(hands2);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands1, medium, medium.getHandCards()), 300);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hands2, medium, null), 250);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand1, medium, medium.getHandCards()), 100);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hand2, medium, null), 50);
		
		RaceCard halfling = new RaceCard(19, "Halfling", Race.HALFLING);
		RaceCard dwarf = new RaceCard(20, "Dwarf", Race.DWARF);
		RaceCard elf = new RaceCard(21, "Elf", Race.ELF);
		OtherDoorCard halfbreed = new OtherDoorCard(Card.OD_HALF_BREED_1, "Half-Breed");
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(ladder, medium, medium.getHandCards()), 400); //400, 3
		medium.addCard(halfling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(ladder, medium, null), 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(ladder, medium, medium.getHandCards()), 800);
		medium.addRaceCard(halfling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(ladder, medium, medium.getHandCards()), 1200);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(hammer, medium, medium.getHandCards()), 600);
		medium.addCard(dwarf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hammer, medium, null), 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hammer, medium, medium.getHandCards()), 1066);
		medium.addRaceCard(dwarf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hammer, medium, medium.getHandCards()), 1200);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(bow, medium, medium.getHandCards()), 800); //800, 4
		assertEquals(AICardEvaluator.getCardValueToPlayer(helmet, medium, medium.getHandCards()), 600); //600, 1/3
		medium.addCard(elf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(bow, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(helmet, medium, medium.getHandCards()), 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(bow, medium, medium.getHandCards()), 1066);
		assertEquals(AICardEvaluator.getCardValueToPlayer(helmet, medium, medium.getHandCards()), 600);
		medium.addRaceCard(elf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(bow, medium, medium.getHandCards()), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(helmet, medium, medium.getHandCards()), 800);
		medium.getEquippedItems().clear();
		medium.getUnequippedItems().clear();
		assertEquals(AICardEvaluator.getCardValueToPlayer(bow, medium, medium.getHandCards()), 1600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(helmet, medium, medium.getHandCards()), 1200);
		
		medium.getRaceCards().removeLast();
		medium.setHalfBreedCard(halfbreed);
		assertEquals(AICardEvaluator.getCardValueToPlayer(bandanna, medium, medium.getHandCards()), 400); //400, 3
		assertEquals(AICardEvaluator.getCardValueToPlayer(swiss, medium, medium.getHandCards()), 600); //600, 4
		medium.getRaceCards().removeFirst();
		assertEquals(AICardEvaluator.getCardValueToPlayer(bandanna, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(swiss, medium, medium.getHandCards()), 1600);
		medium.addUnequippedItem(hand1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(swiss, medium, medium.getHandCards()), 1200);
		medium.setHalfBreedCard(null);
		assertEquals(AICardEvaluator.getCardValueToPlayer(bandanna, medium, medium.getHandCards()), 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(swiss, medium, medium.getHandCards()), 600);
		medium.addCard(halfbreed);
		assertEquals(AICardEvaluator.getCardValueToPlayer(bandanna, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(swiss, medium, medium.getHandCards()), 1600);
		medium.getUnequippedItems().clear();
		
		ClassCard warrior = new ClassCard(23, "Warrior", Class.WARRIOR);
		ClassCard wizard = new ClassCard(24, "Wizard", Class.WIZARD);
		ClassCard thief = new ClassCard(25, "Thief", Class.THIEF);
		ClassCard cleric = new ClassCard(26, "Cleric", Class.CLERIC);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(shield, medium, medium.getHandCards()), 600); //600,4
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWarrior, medium, medium.getHandCards()), 1200); //600,3
		medium.addCard(warrior);
		assertEquals(AICardEvaluator.getCardValueToPlayer(shield, medium, null), 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWarrior, medium, null), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(shield, medium, medium.getHandCards()), 1066);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWarrior, medium, medium.getHandCards()), 1200);
		medium.addClassCard(warrior);
		assertEquals(AICardEvaluator.getCardValueToPlayer(shield, medium, medium.getHandCards()), 1600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWarrior, medium, medium.getHandCards()), 600);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(fstaff, medium, medium.getHandCards()), 800); //800,5
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200); //600,3
		medium.addCard(wizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fstaff, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, null), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fstaff, medium, medium.getHandCards()), 1333);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		medium.addClassCard(wizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fstaff, medium, medium.getHandCards()), 2000);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 600);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(cloak, medium, medium.getHandCards()), 600); //600,4
		assertEquals(AICardEvaluator.getCardValueToPlayer(noThief, medium, medium.getHandCards()), 800); //400,2
		medium.addCard(thief);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cloak, medium, null), 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noThief, medium, null), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cloak, medium, medium.getHandCards()), 1066);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noThief, medium, medium.getHandCards()), 800);
		medium.addClassCard(thief);
		assertEquals(AICardEvaluator.getCardValueToPlayer(cloak, medium, medium.getHandCards()), 1600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noThief, medium, medium.getHandCards()), 400);
		
		int noClericValue = AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards());
		assertEquals(AICardEvaluator.getCardValueToPlayer(grater, medium, medium.getHandCards()), 400); //400,3
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), noClericValue);
		medium.addCard(cleric);
		assertEquals(AICardEvaluator.getCardValueToPlayer(grater, medium, null), 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, null), noClericValue);
		assertEquals(AICardEvaluator.getCardValueToPlayer(grater, medium, medium.getHandCards()), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), noClericValue);
		medium.addClassCard(cleric);
		assertEquals(AICardEvaluator.getCardValueToPlayer(grater, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), noClericValue / 2);
		medium.getClassCards().clear();
		
		assertTrue(easy.isMale());
		assertTrue(medium.isFemale());
		assertTrue(hard.isMale());
		hard.changeSex();
		assertTrue(hard.isFemale());
		assertEquals(AICardEvaluator.getCardValueToPlayer(femaleOnly, easy, easy.getHandCards()) / 10 * 10, 400); //400,3
		assertEquals(AICardEvaluator.getCardValueToPlayer(maleOnly, easy, easy.getHandCards()), 1200); //400,3
		medium.addUnequippedItem(hands1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(femaleOnly, medium, medium.getHandCards()) / 10 * 10, 400);
		medium.getUnequippedItems().clear();
		assertEquals(AICardEvaluator.getCardValueToPlayer(femaleOnly, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(maleOnly, medium, medium.getHandCards()) / 10 * 10, 400);
		assertEquals(AICardEvaluator.getCardValueToPlayer(femaleOnly, hard, hard.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(maleOnly, hard, hard.getHandCards()) / 10 * 10, 400);
				
		medium.getHandCards().clear();
		assertTrue(medium.isDwarf());
		medium.setHirelingCard(hireling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()), 1200); //600,3
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200); //0,3
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200); //600,3
		medium.addUnequippedItem(noWizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		medium.addUnequippedItem(rock);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()) / 10 * 10, 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		medium.getRaceCards().clear();
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()) / 10 * 10, 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		medium.addUnequippedItem(fthrower);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()) / 10 * 10, 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		medium.getUnequippedItems().removeLast();
		medium.setHirelingCard(null);
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()) / 10 * 10, 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		medium.getUnequippedItems().removeFirst();
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()) / 10 * 10, 600);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()) / 10 * 10, 600);
		medium.getUnequippedItems().clear();
		assertEquals(AICardEvaluator.getCardValueToPlayer(fthrower, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(rock, medium, medium.getHandCards()), 1200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noWizard, medium, medium.getHandCards()), 1200);
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(boots, medium, medium.getHandCards()), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(sandals, medium, medium.getHandCards()), 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(tuba, medium, medium.getHandCards()) / 10 * 10, 300);
		
		// Kneepads
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 1000);
		easy.goUpLevels(3, false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 1200);
		hard.goUpLevels(4, false);
		hard.addUnequippedItem(other1);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 1500);
		assertEquals(hard.equip(other1), "");
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 2100);
		medium.addClassCard(cleric);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 2100 / 2);
		hard.getEquippedItems().clear();
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 1500 / 2);
		hard.goDownLevels(9);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 1200 / 2);
		easy.goDownLevels(9);
		assertEquals(AICardEvaluator.getCardValueToPlayer(noCleric, medium, medium.getHandCards()), 1000 / 2);
		medium.getClassCards().clear();
	}
	
	private void testGetItemCardValueToPlayer() {
		ItemCard item0 = new ItemCard(0, "item0", 0);
		ItemCard item1 = new ItemCard(1, "item1", 100);
		ItemCard item2 = new ItemCard(2, "item2", 200, 0);
		ItemCard item3 = new ItemCard(3, "item3", 300, 2);
		ItemCard item4 = new ItemCard(4, "item4", 400, 4);
		ItemCard item5 = new ItemCard(5, "item5", 400, 5);
		ItemCard item6 = new ItemCard(6, "item6", 100, 6);
		ItemCard item7 = new ItemCard(7, "item7", 0, 7);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item0, hard, hard.getHandCards()), 0);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item1, hard, hard.getHandCards()), 100);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item2, hard, hard.getHandCards()), 200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item3, hard, hard.getHandCards()), 302);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item4, hard, hard.getHandCards()), 404);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item5, hard, hard.getHandCards()), 504);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item6, hard, hard.getHandCards()), 601);
		assertEquals(AICardEvaluator.getCardValueToPlayer(item7, hard, hard.getHandCards()), 700);
		
		RaceCard elf = new RaceCard(8, "Elf", Race.ELF);
		
		for (Card card : TreasureDeckFactory.buildDeck()) {
			if (card instanceof TreasureCard) {
				if (card.getID() == Card.I_DOPPLEGANGER) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 100);
					hard.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 200);
					hard.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 300);
					hard.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 400);
					hard.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 500);
					hard.getEquippedItems().add(farmor);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 700);
					hard.getEquippedItems().add(helmet);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 800);
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 1000);
					hard.getRaceCards().clear();
					hard.goDownLevels(10);
					hard.getEquippedItems().clear();
				}	
				else if (card.getID() == Card.I_FLASK_OF_GLUE) {
					int glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 100 && glueValue <= 400);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 100 && glueValue <= 400);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 100 && glueValue <= 400);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 100 && glueValue <= 400);
				}	
				else if (card.getID() == Card.I_FRIENDSHIP_POTION) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 1200);
				}	
				else if (card.getID() == Card.I_INVISIBILITY_POTION) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (0 * 200));
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (1 * 200));
					hard.addRaceCard(new RaceCard(10, "Halfling", Race.HALFLING));
					hard.setHalfBreedCard(new OtherDoorCard(11, "Half-Breed"));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (0 * 200));
					hard.discard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (0 * 200));
					hard.addLastingCurse(headChicken);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (-1 * 200));
					hard.addRaceCard(new RaceCard(11, "Dwarf", Race.DWARF));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (-2 * 200));
					hard.getEquippedItems().add(boots);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (0 * 200));
					hard.getEquippedItems().add(tuba);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (1 * 200));
					hard.removeChickenOnHeadCurse();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (2 * 200));
					hard.getRaceCards().removeLast();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (3 * 200));
					hard.setHalfBreedCard(null);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (2 * 200));
					hard.getRaceCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (3 * 200));
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200);
					hard.getEquippedItems().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (1 * 200));
					hard.getRaceCards().remove(elf);					
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 200 + 600 - (0 * 200));
				}	
				else if (card.getID() == Card.I_INSTANT_WALL) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200));
					hard.addRaceCard(new RaceCard(10, "Halfling", Race.HALFLING));
					hard.setHalfBreedCard(new OtherDoorCard(11, "Half-Breed"));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.discard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.addLastingCurse(headChicken);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (-1 * 200));
					hard.getEquippedItems().add(boots);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200));
					hard.getEquippedItems().add(tuba);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (2 * 200));
					hard.removeChickenOnHeadCurse();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (3 * 200));
					hard.setHalfBreedCard(null);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (2 * 200));
					hard.getRaceCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (3 * 200));
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300);
					hard.getEquippedItems().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200));
					hard.getRaceCards().remove(elf);					
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
				}	
				else if (card.getID() == Card.I_LOADED_DIE) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200));
					hard.addRaceCard(new RaceCard(10, "Halfling", Race.HALFLING));
					hard.setHalfBreedCard(new OtherDoorCard(11, "Half-Breed"));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.discard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.addLastingCurse(headChicken);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (-1 * 200));
					hard.addRaceCard(new RaceCard(11, "Dwarf", Race.DWARF));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300);
					hard.getEquippedItems().add(boots);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					hard.getEquippedItems().add(tuba);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200));
					hard.removeChickenOnHeadCurse();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (2 * 200));
					hard.getRaceCards().removeLast();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (3 * 200));
					hard.setHalfBreedCard(null);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (2 * 200));
					hard.getRaceCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (3 * 200));
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300);
					hard.getEquippedItems().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200));
					hard.getRaceCards().remove(elf);					
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200));
					
					hard.addClassCard(new ClassCard(12, "Thief", Class.THIEF));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200) + 400);
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200) + 400);
					hard.addRaceCard(new RaceCard(10, "Halfling", Race.HALFLING));
					hard.setHalfBreedCard(new OtherDoorCard(11, "Half-Breed"));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200) + 400);
					hard.discard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200) + 400);
					hard.addLastingCurse(headChicken);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (-1 * 200) + 400);
					hard.addRaceCard(new RaceCard(11, "Dwarf", Race.DWARF));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 400);
					hard.getEquippedItems().add(boots);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200) + 400);
					hard.getEquippedItems().add(tuba);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200) + 400);
					hard.removeChickenOnHeadCurse();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (2 * 200) + 400);
					hard.getRaceCards().removeLast();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (3 * 200) + 400);
					hard.setHalfBreedCard(null);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (2 * 200) + 400);
					hard.getRaceCards().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (3 * 200) + 400);
					hard.addRaceCard(elf);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (4 * 200) + 400);
					hard.getEquippedItems().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (1 * 200) + 400);
					hard.getRaceCards().remove(elf);					
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 300 + 600 - (0 * 200) + 400);
				}	
				else if (card.getID() == Card.I_MAGIC_LAMP_1 || card.getID() == Card.I_MAGIC_LAMP_2) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 3 * 800);
				}	
				else if (card.getID() == Card.I_POLLYMORPH_POTION) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 2 * 800 + 400);
				}	
				else if (card.getID() == Card.I_TRANSFERRAL_POTION) {
					int glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 300 && glueValue <= 800);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 300 && glueValue <= 800);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 300 && glueValue <= 800);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 300 && glueValue <= 800);
					glueValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(glueValue >= 300 && glueValue <= 800);
				}	
				else if (card.getID() == Card.I_WAND_OF_DOWSING) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 1200);
				}	
				else if (card.getID() == Card.I_WISHING_RING_1 || card.getID() == Card.I_WISHING_RING_2) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500 + 100);
					hard.addLastingCurse(headChicken);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500 + 400);
					hard.removeChickenOnHeadCurse();
					hard.addLastingCurse(mirror);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500 + 400);
					hard.removeMalignMirror();
					hard.addLastingCurse(changeSex);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500 + 400);
					hard.getEquippedItems().add(sandals);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500);
					hard.removeSexChangeCurse();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500);
					hard.addItem((ItemCard)card);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500);
					hard.getEquippedItems().clear();
					int ringValue = AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards());
					assertTrue(ringValue >= 500 && ringValue <= 800);
					hard.getCarriedItems().clear();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, hard, hard.getHandCards()), 500 + 100);
				}
			}
		}
				
		assertEquals(AICardEvaluator.getCardValueToPlayer(halitosis, hard, null), 200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(halitosis, hard, hard.getHandCards()), 200);
		hard.addCard(nose);
		assertEquals(AICardEvaluator.getCardValueToPlayer(halitosis, hard, null), 200);
		assertEquals(AICardEvaluator.getCardValueToPlayer(halitosis, hard, hard.getHandCards()), 1000);
		hard.getHandCards().clear();
		
		assertEquals(AICardEvaluator.getCardValueToPlayer(water, hard, null), 100);
		assertEquals(AICardEvaluator.getCardValueToPlayer(water, hard, hard.getHandCards()), 100);
		hard.addRaceCard(elf);
		assertEquals(AICardEvaluator.getCardValueToPlayer(water, hard, null), 250);
		assertEquals(AICardEvaluator.getCardValueToPlayer(water, hard, hard.getHandCards()), 250);
		hard.discard(elf);
	}
	
	private void testGetLevelCardValueToPlayer() {
		for (Card card : DoorDeckFactory.buildDeck()) {
			if (card instanceof GoUpLevelCard) {
				if (card.getID() == Card.GUL_WHINE_AT_THE_GM) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					medium.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					easy.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					medium.goUpLevels(2, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					easy.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					easy.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					hard.goUpLevels(10, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					easy.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					easy.goUpLevel(false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
					easy.goUpLevels(10, false);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 200);
					hard.goDownLevel();
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 200);
					easy.goDownLevels(10);
					medium.goDownLevels(10);
					hard.goDownLevels(10);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
				}
				else if (card.getID() == Card.GUL_KILL_THE_HIRELING) {
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					easy.setHirelingCard(hireling);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 800);
					easy.addUnequippedItem(noWizard);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 800);
					easy.addUnequippedItem(tuba);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 500);
					easy.addRaceCard(new RaceCard(10, "Dwarf", Race.DWARF));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 900);
					easy.setHirelingCard(null);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					easy.getUnequippedItems().clear();
					easy.getRaceCards().clear();
					
					human.setHirelingCard(hireling);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1200);
					human.addUnequippedItem(noWizard);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1200);
					human.addUnequippedItem(tuba);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1500);
					human.addRaceCard(new RaceCard(10, "Dwarf", Race.DWARF));
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1100);
					human.setHirelingCard(null);
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 400);
					human.getUnequippedItems().clear();
					human.getRaceCards().clear();
				}
				else 
					assertEquals(AICardEvaluator.getCardValueToPlayer(card, easy, easy.getHandCards()), 1000);
			}
		}
		
		easy.goUpLevels(10, false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(new GoUpLevelCard(1, "level"), easy, easy.getHandCards()), 500);
		easy.goDownLevel();
		assertEquals(AICardEvaluator.getCardValueToPlayer(new GoUpLevelCard(1, "level"), easy, easy.getHandCards()), 1000);
		easy.goDownLevels(10);
		assertEquals(AICardEvaluator.getCardValueToPlayer(new GoUpLevelCard(1, "level"), easy, easy.getHandCards()), 1000);
	}
	
	private void testGetOtherTreasureCardValueToPlayer() {
		OtherTreasureCard steal = new OtherTreasureCard(Card.OT_STEAL_A_LEVEL, "Steal A Level");
		assertEquals(AICardEvaluator.getCardValueToPlayer(steal, medium, medium.getHandCards()), 1000);
		medium.goUpLevel(false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(steal, medium, medium.getHandCards()), 2000);
		medium.goDownLevel();
		hard.goUpLevel(false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(steal, medium, medium.getHandCards()), 2000);
		hard.goDownLevel();
		medium.goUpLevels(10, false);
		assertEquals(AICardEvaluator.getCardValueToPlayer(steal, medium, medium.getHandCards()), 1000);
		medium.goDownLevels(10);
		
		GoUpLevelCard hirelingLevel = new GoUpLevelCard(Card.GUL_KILL_THE_HIRELING, "Kill The Hireling");
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 100);
		medium.addUnequippedItem(noWizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 200);
		medium.addCard(tuba);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 500);
		medium.setCheatCards(new OtherDoorCard(Card.OD_CHEAT, "Cheat!"), noWizard);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 200);
		medium.addRaceCard(new RaceCard(10, "Dwarf", Race.DWARF));
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 100);
		medium.addCard(hirelingLevel);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 100);
		medium.setHirelingCard(hireling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 100);
		medium.setHirelingCard(null);
		medium.addCard(hireling);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 1000);
		medium.getUnequippedItems().clear();
		medium.getRaceCards().clear();
		medium.getHandCards().clear();
		medium.removeCheat();
		assertEquals(AICardEvaluator.getCardValueToPlayer(hireling, medium, medium.getHandCards()), 100);
		
		OtherTreasureCard hoard = new OtherTreasureCard(Card.OT_HOARD, "Hoard!");
		assertEquals(AICardEvaluator.getCardValueToPlayer(hoard, medium, null), 3 * 800);
		assertEquals(AICardEvaluator.getCardValueToPlayer(hoard, medium, medium.getHandCards()), 3 * 800);
	}
}
