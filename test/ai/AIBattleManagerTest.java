
package ai;

import exceptions.EndGameException;
import gui.GUI;
import gui.MockGUI;

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
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class AIBattleManagerTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	private Player easy;
	private Player medium;
	private Player hard;
	private Player human;
	
	private MonsterCard chicken;
	private MonsterCard nose;
	private MonsterCard nothing;
	private MonsterCard vamp;
	private MonsterCard gazebo;
	private MonsterCard tdemon;
	private MonsterCard bones;
	private MonsterCard dog;
	private MonsterCard squid;
	private MonsterCard orcs;
	private MonsterCard bigfoot;
	private MonsterCard geek;
	private MonsterCard tut;
	
	private MonsterEnhancerCard baby;
	private MonsterEnhancerCard enraged;
	private MonsterEnhancerCard ancient;
	
	private CurseCard changeSex;
	private CurseCard loseClass;
	private CurseCard doom;
	private CurseCard loseItem;
	
	private OtherDoorCard wander;
	private OtherDoorCard help;
	private OtherDoorCard illusion;
	private OtherDoorCard lunch;
	private OtherDoorCard mate;
	
	private ItemCard fpotion;
	private ItemCard halitosis;
	private ItemCard dopple;
	private ItemCard water;
	private ItemCard polly;
	private ItemCard lamp;
	private ItemCard friend;
	private ItemCard trans;
	private ItemCard wall;
	private ItemCard invis;
	private ItemCard loaded;
	
	private EquipmentCard farmor;
	private EquipmentCard kneepads;
	private EquipmentCard sword;
	private EquipmentCard club;
	private EquipmentCard helm;
	private EquipmentCard ladder;
	private EquipmentCard shield;
	private EquipmentCard staff;
	private EquipmentCard noWarrior;
	private EquipmentCard bow;
		
	public int testAll() {
		initializeObjects();

		testMakeBattleDecisions();
		testIncreaseRewards();
		testNormalTryToWinBattle();
		testLastResortToWinBattle();
		testNormalTryToStopWinBattle();
		testLastResortToStopWinBattle();
		testGetCurseLevelLoss();
		testGetMonsterToBattle();
		
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
		
		GM.moveToBattlePhase();
		
		for (Card card : DoorDeckFactory.buildDeck()) {
			if (card.getID() == Card.M_FLOATING_NOSE)
				nose = (MonsterCard)card;
			else if (card.getID() == Card.M_LARGE_ANGRY_CHICKEN)
				chicken = (MonsterCard)card;
			else if (card.getID() == Card.M_THE_NOTHING)
				nothing = (MonsterCard)card;
			else if (card.getID() == Card.M_WANNABE_VAMPIRE)
				vamp = (MonsterCard)card;
			else if (card.getID() == Card.M_GAZEBO)
				gazebo = (MonsterCard)card;
			else if (card.getID() == Card.M_TONGUE_DEMON)
				tdemon = (MonsterCard)card;
			else if (card.getID() == Card.M_MR_BONES)
				bones = (MonsterCard)card;
			else if (card.getID() == Card.M_PIT_BULL)
				dog = (MonsterCard)card;
			else if (card.getID() == Card.M_SQUIDZILLA)
				squid = (MonsterCard)card;
			else if (card.getID() == Card.M_3872_ORCS)
				orcs = (MonsterCard)card;
			else if (card.getID() == Card.M_BIGFOOT)
				bigfoot = (MonsterCard)card;
			else if (card.getID() == Card.M_SHRIEKING_GEEK)
				geek = (MonsterCard)card;
			else if (card.getID() == Card.M_KING_TUT)
				tut = (MonsterCard)card;
			else if (card.getID() == Card.ME_BABY)
				baby = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.ME_ENRAGED)
				enraged = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.ME_ANCIENT)
				ancient = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.CU_CHANGE_SEX)
				changeSex = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_YOUR_CLASS)
				loseClass = (CurseCard)card;
			else if (card.getID() == Card.CU_DUCK_OF_DOOM)
				doom = (CurseCard)card;
			else if (card.getID() == Card.CU_LOSE_1_SMALL_ITEM_1)
				loseItem = (CurseCard)card;
			else if (card.getID() == Card.OD_WANDERING_MONSTER_1)
				wander = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_HELP_ME_OUT_HERE)
				help = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_ILLUSION)
				illusion = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_OUT_TO_LUNCH)
				lunch = (OtherDoorCard)card;
			else if (card.getID() == Card.OD_MATE)
				mate = (OtherDoorCard)card;
		}
		
		for (Card card : TreasureDeckFactory.buildDeck()) {
			if (card.getID() == Card.E_FLAMING_ARMOR)
				farmor = (EquipmentCard)card;
			else if (card.getID() == Card.E_KNEEPADS_OF_ALLURE)
				kneepads = (EquipmentCard)card;
			else if (card.getID() == Card.E_BROAD_SWORD)
				sword = (EquipmentCard)card;
			else if (card.getID() == Card.E_GENTLEMENS_CLUB)
				club = (EquipmentCard)card;
			else if (card.getID() == Card.E_HORNED_HELMET)
				helm = (EquipmentCard)card;
			else if (card.getID() == Card.E_STEPLADDER)
				ladder = (EquipmentCard)card;
			else if (card.getID() == Card.E_SHIELD_OF_UBIQUITY)
				shield = (EquipmentCard)card;
			else if (card.getID() == Card.E_STAFF_OF_NAPALM)
				staff = (EquipmentCard)card;
			else if (card.getID() == Card.E_PANTYHOSE_OF_GIANT_STRENGTH)
				noWarrior = (EquipmentCard)card;
			else if (card.getID() == Card.E_BOW_WITH_RIBBONS)
				bow = (EquipmentCard)card;
			else if (card.getID() == Card.I_POTION_OF_HALITOSIS)
				halitosis = (ItemCard)card;
			else if (card.getID() == Card.I_FLAMING_POISON_POTION)
				fpotion = (ItemCard)card;
			else if (card.getID() == Card.I_DOPPLEGANGER)
				dopple = (ItemCard)card;
			else if (card.getID() == Card.I_YUPPIE_WATER)
				water = (ItemCard)card;
			else if (card.getID() == Card.I_POLLYMORPH_POTION)
				polly = (ItemCard)card;
			else if (card.getID() == Card.I_MAGIC_LAMP_2)
				lamp = (ItemCard)card;
			else if (card.getID() == Card.I_FRIENDSHIP_POTION)
				friend = (ItemCard)card;
			else if (card.getID() == Card.I_TRANSFERRAL_POTION)
				trans = (ItemCard)card;
			else if (card.getID() == Card.I_INSTANT_WALL)
				wall = (ItemCard)card;
			else if (card.getID() == Card.I_INVISIBILITY_POTION)
				invis = (ItemCard)card;
			else if (card.getID() == Card.I_LOADED_DIE)
				loaded = (ItemCard)card;
		}
	}
	
	private void testMakeBattleDecisions() {
		try {
		// active player actions
		Battle battle = new Battle(easy, new MonsterCard(1, "test", 3, 3, 1, false) {
			public void doBadStuff(Player player) {}
		});
		easy.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getCarriedItems().contains(halitosis));
		assertTrue(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		easy.addItem(halitosis);
		medium.addRaceCard(new RaceCard(10, "Elf", Race.ELF));
		battle.addHelper(medium);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getCarriedItems().contains(halitosis));
		assertTrue(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		easy.addItem(halitosis);
		medium.goUpLevels(9, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertFalse(easy.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertTrue(battle.getMonsterItemCards().remove(halitosis));
		easy.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertTrue(easy.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		easy.goUpLevels(10, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getCarriedItems().contains(halitosis));
		assertTrue(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		battle = new Battle(hard, new MonsterCard(1, "test", 3, 3, 1, false) {
			public void doBadStuff(Player player) {}
		});
		battle.addHelper(medium);
		hard.goUpLevels(9, false);
		hard.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 0));
		assertTrue(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertTrue(battle.getMonsterItemCards().remove(halitosis));
		medium.goDownLevel();
		hard.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 0));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertTrue(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		
		// helper actions
		battle = new Battle(easy, new MonsterCard(1, "test", 3, 3, 1, false) {
			public void doBadStuff(Player player) {}
		});
		battle.addHelper(medium);
		medium.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertTrue(medium.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertTrue(battle.getMonsterItemCards().remove(halitosis));
		medium.addItem(halitosis);
		medium.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertTrue(medium.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertTrue(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		battle.helper = hard;
		hard.addRaceCard(new RaceCard(10, "Elf", Race.ELF));
		hard.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 0));
		assertTrue(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertTrue(battle.getMonsterItemCards().remove(halitosis));
		hard.addItem(halitosis);
		hard.goDownLevel();
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 0));
		assertTrue(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertTrue(battle.getMonsterItemCards().remove(halitosis));
		hard.goUpLevel(false);
		easy.goDownLevel();
		hard.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 0));
		assertFalse(hard.getCarriedItems().contains(halitosis));
		assertTrue(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		
		// other player actions
		medium.addItem(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertTrue(medium.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertFalse(battle.getMonsterItemCards().remove(halitosis));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertFalse(medium.getCarriedItems().contains(halitosis));
		assertFalse(battle.getPlayerItemCards().remove(halitosis));
		assertTrue(battle.getMonsterItemCards().remove(halitosis));
		
		easy.goDownLevels(10);
		medium.goDownLevels(10);
		medium.getRaceCards().clear();
		hard.goDownLevels(10);
		hard.getRaceCards().clear();
		}
		catch (EndGameException ex) { fail("Should not be Game End"); }
	}
	
	private void testIncreaseRewards() {
		try {
			
		// test chicken & flame potion w/ and w/out flame armor
		Battle battle = new Battle(easy, chicken);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		easy.addItem(fpotion);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertFalse(easy.getCarriedItems().contains(fpotion));
		assertTrue(battle.getPlayerItemCards().remove(fpotion));
		assertFalse(battle.getMonsterItemCards().remove(fpotion));
		easy.addItem(fpotion);
		easy.getEquippedItems().add(farmor);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().contains(fpotion));
		assertFalse(battle.getPlayerItemCards().remove(fpotion));
		assertFalse(battle.getMonsterItemCards().remove(fpotion));
		easy.unequip(farmor);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertFalse(easy.getCarriedItems().contains(fpotion));
		assertTrue(battle.getPlayerItemCards().remove(fpotion));
		assertFalse(battle.getMonsterItemCards().remove(fpotion));
		easy.getUnequippedItems().clear();
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		
		// test Monster Enhancer when should and shouldn't use
		easy.addCard(enraged);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 5));
		assertTrue(easy.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(enraged));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 8));
		assertTrue(easy.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(enraged));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 9));
		assertFalse(easy.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(enraged));
		easy.addCard(enraged);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 10));
		assertFalse(easy.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(enraged));
		easy.addCard(ancient);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 14));
		assertTrue(easy.getHandCards().contains(ancient));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(ancient));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 15));
		assertFalse(easy.getHandCards().contains(ancient));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(ancient));
		easy.addCard(ancient);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 16));
		assertFalse(easy.getHandCards().contains(ancient));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(ancient));
				
		// test The Nothing when can increase in other ways, w/ and w/out helper
		battle = new Battle(easy, nothing);
		easy.addCard(enraged);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 15));
		assertFalse(easy.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(nothing).remove(enraged));
		easy.addCard(enraged);
		easy.goUpLevels(6, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 15));
		assertTrue(easy.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(nothing).remove(enraged));
		easy.goDownLevel();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 15));
		assertFalse(easy.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(nothing).remove(enraged));
		easy.addCard(enraged);
		easy.addClassCard(new ClassCard(10, "Warrior", Class.WARRIOR));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 15));
		assertTrue(easy.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(nothing).remove(enraged));
		battle.addHelper(medium);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 15));
		assertFalse(easy.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(nothing).remove(enraged));
		easy.getClassCards().clear();
		easy.goDownLevels(10);
		
		// test no treasure increase when helper is HARD or (player is EASY & helper is HUMAN)
		battle = new Battle(easy, chicken);
		easy.addCard(enraged);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 10));
		assertFalse(easy.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(enraged));
		battle.helper = hard;
		easy.addCard(enraged);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 10));
		assertTrue(easy.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(enraged));
		battle.helper = human;
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 10));
		assertTrue(easy.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(enraged));
		easy.getHandCards().clear();
		battle = new Battle(medium, chicken);
		battle.helper = hard;
		medium.addCard(enraged);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 10));
		assertTrue(medium.getHandCards().contains(enraged));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(enraged));
		battle.helper = human;
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 10));
		assertFalse(medium.getHandCards().contains(enraged));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(enraged));
		battle.helper = null;
		
		// test Mate when should and shouldn't use
		// chicken level is 2
		medium.addCard(mate);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 6));
		assertTrue(medium.getHandCards().contains(mate));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(mate));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 7));
		assertFalse(medium.getHandCards().contains(mate));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(mate));
		medium.addCard(mate);
		battle.addMonsterEnhancer(chicken, enraged);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 7));
		assertTrue(medium.getHandCards().contains(mate));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(mate));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 11));
		assertTrue(medium.getHandCards().contains(mate));
		assertFalse(battle.getMonsterEnhancers(chicken).remove(mate));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 12));
		assertFalse(medium.getHandCards().contains(mate));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(mate));
		// nose level is 10
		battle = new Battle(medium, nose);
		medium.addCard(mate);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 14));
		assertTrue(medium.getHandCards().contains(mate));
		assertFalse(battle.getMonsterEnhancers(nose).remove(mate));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 15));
		assertFalse(medium.getHandCards().contains(mate));
		assertTrue(battle.getMonsterEnhancers(nose).remove(mate));
		medium.addCard(mate);
		battle.addMonsterEnhancer(nose, ancient);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 15));
		assertTrue(medium.getHandCards().contains(mate));
		assertFalse(battle.getMonsterEnhancers(nose).remove(mate));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 24));
		assertTrue(medium.getHandCards().contains(mate));
		assertFalse(battle.getMonsterEnhancers(nose).remove(mate));
		battle.addMonster(chicken);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 24));
		assertFalse(medium.getHandCards().contains(mate));
		assertTrue(battle.getMonsterEnhancers(chicken).remove(mate));
		assertFalse(battle.getMonsterEnhancers(nose).remove(mate));
		medium.addCard(mate);
		battle.discardMonster(chicken, true);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 25));
		assertFalse(medium.getHandCards().contains(mate));
		assertTrue(battle.getMonsterEnhancers(nose).remove(mate));
		
		// test Wandering Monster wander when should and shouldn't use
		battle = new Battle(hard, nose);
		hard.addCard(wander);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 9));
		assertTrue(hard.getHandCards().contains(wander));
		assertEquals(battle.getMonsterCount(), 1);
		hard.addCard(chicken);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 8));
		assertFalse(hard.getHandCards().contains(wander));
		assertFalse(hard.getHandCards().contains(chicken));
		assertEquals(battle.getMonsterCount(), 2);
		battle.discardMonster(chicken, true);
		hard.addCard(wander);
		hard.addCard(chicken);
		hard.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 8));
		assertTrue(hard.getHandCards().contains(wander));
		assertTrue(hard.getHandCards().contains(chicken));
		assertEquals(battle.getMonsterCount(), 1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 9));
		assertFalse(hard.getHandCards().contains(wander));
		assertFalse(hard.getHandCards().contains(chicken));
		assertEquals(battle.getMonsterCount(), 2);
		hard.addCard(chicken);
		battle.discardMonster(chicken, true);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 9));
		assertTrue(hard.getHandCards().contains(chicken));
		assertEquals(battle.getMonsterCount(), 1);
		hard.addCard(wander);
		hard.addRaceCard(new RaceCard(11, "Elf", Race.ELF));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 8));
		assertTrue(hard.getHandCards().contains(wander));
		assertTrue(hard.getHandCards().contains(chicken));
		assertEquals(battle.getMonsterCount(), 1);
		hard.addClassCard(new ClassCard(12, "Wizard", Class.WIZARD));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 8));
		assertFalse(hard.getHandCards().contains(wander));
		assertFalse(hard.getHandCards().contains(chicken));
		assertEquals(battle.getMonsterCount(), 2);		
		hard.goDownLevel();
		hard.getRaceCards().clear();
		hard.getClassCards().clear();
			
		} catch (EndGameException ex) { fail("Should not be Game End"); }
	}
	
	private void testNormalTryToWinBattle() {
		try {
		
		// floating nose & halitosis
		Battle battle = new Battle(easy, nose);
		easy.addCard(halitosis);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		easy.addItem((ItemCard)easy.getHandCards().remove());
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().contains(halitosis));
		assertTrue(GM.isBattlePhase());
		assertFalse(GM.isAfterBattle());
		assertEquals(battle.getMonsterCount(), 1);
		assertEquals(easy.getLevel(), 1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getCarriedItems().contains(halitosis));
		assertFalse(GM.isBattlePhase());
		assertEquals(battle.getMonsterCount(), 0);
		assertEquals(easy.getLevel(), 2);
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 0);
		battle = new Battle(easy, nose);
		easy.addItem(halitosis);
		easy.goUpLevels(10, false);
		try {
			assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
			fail("Should have ended game.");
		}
		catch (EndGameException ex) {}
		easy.goDownLevels(10);
		
		// Kneepads
		battle = new Battle(medium, chicken);
		easy.goUpLevel(true);
		hard.goUpLevel(true);
		assertTrue(battle.canAddHelper());
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertFalse(battle.isHelper());
		medium.addUnequippedItem(kneepads);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertFalse(battle.isHelper());
		assertTrue(battle.canAddHelper());
		medium.equip(kneepads);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertTrue(battle.isHelper());
		easy.goDownLevel();
		hard.goDownLevel();
		
		// "Help Me Out Here!"
		RaceCard elf = new RaceCard(10, "Elf", Race.ELF);
		easy.addRaceCard(elf);
		battle = new Battle(hard, chicken);
		hard.addCard(help);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -1));
		assertTrue(hard.getHandCards().contains(help));
		hard.addUnequippedItem(farmor);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -1));
		assertTrue(hard.getHandCards().contains(help));
		hard.equip(farmor);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -1));
		assertTrue(hard.getHandCards().contains(help));
		hard.getEquippedItems().remove(farmor);
		easy.getEquippedItems().add(helm);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -1));
		assertTrue(hard.getHandCards().contains(help));
		assertFalse(hard.getAllEquipment().contains(helm));
		assertTrue(easy.getAllEquipment().contains(helm));
		medium.addUnequippedItem(farmor);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, -1));
		assertFalse(hard.getHandCards().contains(help));
		assertFalse(hard.getAllEquipment().remove(helm));
		assertTrue(hard.getEquippedItems().remove(farmor));
		assertTrue(easy.getAllEquipment().contains(helm));
		assertFalse(medium.getAllEquipment().contains(farmor));
		hard.addCard(help);
		hard.addRaceCard(elf);
		medium.addUnequippedItem(farmor);
		battle.addMonsterEnhancer(chicken, new MonsterEnhancerCard(11, "Plus 2", 2));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -3));
		assertTrue(hard.getHandCards().contains(help));
		assertFalse(hard.getAllEquipment().remove(helm));
		assertFalse(hard.getEquippedItems().remove(farmor));
		assertTrue(easy.getAllEquipment().contains(helm));
		assertTrue(medium.getAllEquipment().contains(farmor));
		battle.getMonsterEnhancers(chicken).clear();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, -1));
		assertFalse(hard.getHandCards().contains(help));
		assertTrue(hard.getEquippedItems().remove(helm));
		assertFalse(hard.getEquippedItems().remove(farmor));
		assertFalse(easy.getAllEquipment().contains(helm));
		assertTrue(medium.getAllEquipment().contains(farmor));
		medium.getUnequippedItems().clear();
		hard.getEquippedItems().clear();
		hard.getRaceCards().clear();
		
		// "Baby"
		ClassCard cleric = new ClassCard(13, "Cleric", Class.CLERIC);
		ClassCard warrior = new ClassCard(14, "Warrior", Class.WARRIOR);
		easy.addClassCard(cleric);
		easy.setSuperMunchkinCard(new OtherDoorCard(Card.OD_SUPER_MUNCHKIN_1, "Super Munchkin"));
		easy.addClassCard(warrior);
		battle = new Battle(easy, bones);
		battle.addMonsterEnhancer(bones, enraged);
		battle.addHelper(medium);
		easy.addCard(baby);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertTrue(easy.getHandCards().contains(baby));
		assertFalse(battle.getMonsterEnhancers(bones).remove(baby));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertFalse(easy.getHandCards().contains(baby));
		assertTrue(battle.getMonsterEnhancers(bones).remove(baby));
		
		// Turning
		ItemCard item1 = new ItemCard(15, "Item1", 100);
		ItemCard item2 = new ItemCard(16, "Item2", 200);
		ItemCard item3 = new ItemCard(17, "Item3", 300);
		ItemCard item4 = new ItemCard(18, "Item4", 2000);
		easy.addCard(item1);
		easy.addCard(item2);
		easy.addCard(item3);
		easy.addCard(item4);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -9));
		assertTrue(easy.getHandCards().contains(item1));
		assertTrue(easy.getHandCards().contains(item2));
		assertTrue(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
		assertTrue(battle.canUseTurning(easy));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -8));
		assertFalse(easy.getHandCards().contains(item1));
		assertFalse(easy.getHandCards().contains(item2));
		assertFalse(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
		assertFalse(battle.canUseTurning(easy));
		
		// Berserking
		battle = new Battle(easy, dog);
		battle.addHelper(medium);
		easy.addCard(item1);
		easy.addCard(item2);
		easy.addCard(item3);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -3));
		assertTrue(easy.getHandCards().contains(item1));
		assertTrue(easy.getHandCards().contains(item2));
		assertTrue(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
		assertTrue(battle.canUseBerserking(easy));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(easy.getHandCards().contains(item1));
		assertFalse(easy.getHandCards().contains(item2));
		assertTrue(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
		assertTrue(battle.canUseBerserking(easy));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getHandCards().contains(item1));
		assertFalse(easy.getHandCards().contains(item2));
		assertFalse(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
		assertFalse(battle.canUseBerserking(easy));
		easy.addCard(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertTrue(easy.getHandCards().contains(item1));
		assertTrue(easy.getHandCards().contains(item4));
		assertFalse(battle.canUseBerserking(easy));
		battle = new Battle(easy, dog);
		battle.addHelper(medium);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertTrue(easy.getHandCards().contains(item1));
		assertTrue(easy.getHandCards().contains(item4));
		assertTrue(battle.canUseBerserking(easy));
		battle.addMonsterEnhancer(dog, enraged);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(easy.getHandCards().contains(item1));
		assertFalse(easy.getHandCards().contains(item4));
		assertTrue(battle.canUseBerserking(easy));
		
		// Discard Race and yuppie water
		battle = new Battle(easy, squid);
		battle.addHelper(medium);
		easy.addItem(water);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(easy.getCarriedItems().contains(water));
		assertTrue(battle.getPlayerItemCards().remove(water));
		assertTrue(easy.isElf());
		easy.addItem(water);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		assertFalse(easy.getCarriedItems().contains(water));
		assertTrue(battle.getPlayerItemCards().remove(water));
		assertFalse(easy.isElf());
		easy.addRaceCard(elf);
		easy.addItem(water);
		medium.addRaceCard(elf);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertFalse(easy.getCarriedItems().contains(water));
		assertTrue(battle.getPlayerItemCards().remove(water));
		assertFalse(easy.isElf());
		easy.addRaceCard(elf);
		easy.addItem(water);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		assertFalse(easy.getCarriedItems().contains(water));
		assertTrue(battle.getPlayerItemCards().remove(water));
		assertTrue(easy.isElf());
		easy.addItem(water);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -3));
		assertFalse(easy.getCarriedItems().contains(water));
		assertTrue(battle.getPlayerItemCards().remove(water));
		assertTrue(easy.isElf());
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertTrue(easy.isElf());
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -3));
		assertFalse(easy.isElf());
		easy.addRaceCard(elf);
		easy.addItem(water);
		medium.getRaceCards().clear();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		assertFalse(easy.getCarriedItems().contains(water));
		assertTrue(battle.getPlayerItemCards().remove(water));
		assertFalse(easy.isElf());
		// Dwarf and big items
		EquipmentCard big1 = new EquipmentCard(11, "E1", 100, 1, EquipmentType.OTHER, true, false ,false);
		EquipmentCard big2 = new EquipmentCard(12, "E2", 200, 3, EquipmentType.OTHER, true, false ,false);
		EquipmentCard big3 = new EquipmentCard(13, "E3", 300, 2, EquipmentType.OTHER, true, false ,false);
		battle = new Battle(easy, orcs);
		battle.addHelper(medium);
		RaceCard dwarf = new RaceCard(14, "Dwarf", Race.DWARF);
		easy.addRaceCard(dwarf);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -6));
		assertTrue(easy.isDwarf());
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertFalse(easy.isDwarf());
		easy.addRaceCard(dwarf);
		easy.getEquippedItems().add(big1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertFalse(easy.isDwarf());
		easy.addRaceCard(dwarf);
		easy.getEquippedItems().add(big2);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertTrue(easy.isDwarf());
		easy.setHirelingCard(new OtherTreasureCard(Card.OT_HIRELING, "Hireling"));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertFalse(easy.isDwarf());
		easy.addRaceCard(dwarf);
		easy.getEquippedItems().add(big3);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertTrue(easy.isDwarf());
		easy.setHirelingCard(null);
		easy.getEquippedItems().remove(big1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertTrue(easy.isDwarf());
		easy.getEquippedItems().set(0, big1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertFalse(easy.isDwarf());
		assertFalse(easy.getEquippedItems().contains(big1));
		easy.getEquippedItems().clear();
		// halfling
		RaceCard halfling = new RaceCard(15, "Halfling", Race.HALFLING);
		easy.addRaceCard(halfling);	
		battle = new Battle(easy, bigfoot);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		assertFalse(easy.isHalfling());
		easy.getEquippedItems().add(ladder);
		easy.addRaceCard(halfling);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertTrue(easy.isHalfling());
		easy.unequip(ladder);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.isHalfling());
		easy.addRaceCard(halfling);
		easy.getUnequippedItems().add(ladder);
		easy.getUnequippedItems().add(ladder);
		easy.getUnequippedItems().add(ladder);
		easy.getUnequippedItems().add(ladder);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertTrue(easy.isHalfling());
		easy.getUnequippedItems().clear();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(easy.isHalfling());
		
		// Discard Class w/ and w/out Berserking
		battle = new Battle(easy, tdemon);
		battle.addHelper(medium);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertTrue(easy.isCleric());
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -3));
		assertFalse(easy.isCleric());
		easy.getClassCards().add(cleric);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.isCleric());
		item1 = new ItemCard(15, "Item1", 50);
		item2 = new ItemCard(16, "Item2", 75);
		item3 = new ItemCard(17, "Item3", 100);
		item4 = new ItemCard(18, "Item4", 125);
		battle = new Battle(easy, geek);
		battle.addHelper(medium);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertTrue(easy.isWarrior());
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -4));
		assertFalse(easy.isWarrior());
		easy.addClassCard(warrior);
		easy.addCard(item1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertFalse(easy.isWarrior());
		assertFalse(easy.getHandCards().contains(item1));
		battle = new Battle(easy, geek);
		battle.addHelper(medium);
		easy.addClassCard(warrior);
		easy.addCard(item2);
		easy.addCard(item4);
		easy.addCard(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -7));
		assertTrue(easy.isWarrior());
		assertTrue(easy.getHandCards().contains(item1));
		assertTrue(easy.getHandCards().contains(item2));
		assertTrue(easy.getHandCards().contains(item4));
		easy.addCard(item3);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -8));
		assertTrue(easy.isWarrior());
		assertTrue(easy.getHandCards().contains(item1));
		assertTrue(easy.getHandCards().contains(item2));
		assertTrue(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -7));
		assertFalse(easy.isWarrior());
		assertFalse(easy.getHandCards().contains(item1));
		assertFalse(easy.getHandCards().contains(item2));
		assertFalse(easy.getHandCards().contains(item3));
		assertTrue(easy.getHandCards().contains(item4));
			
		// normal battle items
		item1 = new ItemCard(15, "Item1", 100, 1);
		item2 = new ItemCard(16, "Item2", 250, 2);
		item3 = new ItemCard(17, "Item3", 300, 3);
		item4 = new ItemCard(18, "Item4", 4000, 4);
		battle = new Battle(easy, chicken);
		battle.addHelper(medium);
		easy.addItem(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertTrue(easy.getCarriedItems().contains(item1));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getCarriedItems().contains(item1));
		easy.addItem(item3);
		easy.addItem(item2);
		easy.addItem(item4);
		easy.addItem(item1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertTrue(easy.getCarriedItems().contains(item1));
		assertTrue(easy.getCarriedItems().contains(item2));
		assertFalse(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item4));
		easy.addItem(item3);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -3));
		assertFalse(easy.getCarriedItems().contains(item1));
		assertTrue(easy.getCarriedItems().contains(item2));
		assertFalse(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item4));
		easy.addItem(item3);
		easy.addItem(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -6));
		assertTrue(easy.getCarriedItems().contains(item1));
		assertTrue(easy.getCarriedItems().contains(item2));
		assertTrue(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item4));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertFalse(easy.getCarriedItems().contains(item1));
		assertFalse(easy.getCarriedItems().contains(item2));
		assertFalse(easy.getCarriedItems().contains(item3));
		assertTrue(easy.getCarriedItems().contains(item4));
		easy.getCarriedItems().clear();
		
		// doppleganger		
		battle = new Battle(easy, gazebo);
		easy.addItem(dopple);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().contains(dopple));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		easy.goUpLevel(false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(easy.getCarriedItems().contains(dopple));
		easy.addItem(dopple);
		easy.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -3));
		assertTrue(easy.getCarriedItems().contains(dopple));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		assertFalse(easy.getCarriedItems().contains(dopple));
		easy.addItem(dopple);
		easy.getEquippedItems().add(club);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -6));
		assertTrue(easy.getCarriedItems().contains(dopple));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -5));
		assertFalse(easy.getCarriedItems().contains(dopple));
		easy.getEquippedItems().clear();
		easy.goDownLevels(10);
		
		// Best Battle Actions
		assertEquals(easy.getHandCards().size(), 0);
		easy.addCard(baby);
		easy.addCard(item3);
		easy.addCard(item1);
		easy.addCard(item2);
		easy.getCarriedItems().add(item2);
		easy.getCarriedItems().add(item3);
		easy.getCarriedItems().add(item1);
		easy.getCarriedItems().add(water);
		easy.getCarriedItems().add(dopple);
		easy.getEquippedItems().add(club);
		easy.getRaceCards().add(elf);
		easy.getClassCards().add(warrior);
		easy.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -(5 + 3 + 6 + 2)));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -(5 + 3 + 6 + 2) + 1));
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getCarriedItems().size(), 1);
		assertTrue(easy.getCarriedItems().contains(dopple));
		battle = new Battle(easy, gazebo);
		easy.addCard(baby);
		easy.addCard(item1);
		easy.addCard(item2);
		easy.addCard(item3);
		easy.getCarriedItems().add(item2);
		easy.getCarriedItems().add(item3);
		easy.getCarriedItems().add(item1);
		easy.getCarriedItems().add(water);
		easy.goUpLevels(9, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -(5 + 3 + 6 + 2 + 9 + 3)));
		assertEquals(easy.getHandCards().size(), 4);
		assertEquals(easy.getCarriedItems().size(), 5);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -(5 + 3 + 6 + 2 + 9 + 3) + 1));
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(easy.getCarriedItems().size(), 0);
		easy.getEquippedItems().clear();
		easy.getRaceCards().clear();
		easy.getClassCards().clear();
		easy.goDownLevels(10);
				
		} catch (EndGameException ex) { fail("Should not be Game End"); }
	}
	
	private void testLastResortToWinBattle() {

		try {
			
		RaceCard elf = new RaceCard(10, "Elf", Race.ELF);
		
		// ask helper
		Battle battle = new Battle(medium, chicken);
		easy.addClassCard(new ClassCard(11, "Warrior", Class.WARRIOR));
		hard.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertFalse(battle.isHelper());
		battle.addMonsterEnhancer(chicken, enraged);
		battle.addPlayerItemCard(new ItemCard(1, "5", 100, 5));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertTrue(battle.helper == easy);
		battle = new Battle(medium, gazebo);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertFalse(battle.isHelper());
		battle = new Battle(medium, chicken);
		battle.addMonsterEnhancer(chicken, ancient);
		battle.addPlayerItemCard(new ItemCard(1, "2", 100, 2));
		medium.goUpLevels(8, false);
		easy.addRaceCard(elf);
		hard.addRaceCard(elf);		
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertFalse(battle.isHelper());
		battle = new Battle(medium, chicken);
		battle.addMonsterEnhancer(chicken, ancient);
		battle.addPlayerItemCard(new ItemCard(1, "2", 100, 2));
		hard.goUpLevels(7, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertFalse(battle.isHelper());
		battle = new Battle(medium, chicken);
		battle.addMonsterEnhancer(chicken, ancient);
		battle.addPlayerItemCard(new ItemCard(1, "2", 100, 2));
		easy.goUpLevels(8, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -1));
		assertTrue(battle.helper == easy);
		easy.goDownLevels(10);
		medium.goDownLevels(10);
		hard.goDownLevels(10);
		easy.getClassCards().clear();
		easy.getRaceCards().clear();
		hard.getRaceCards().clear();
			
		// "Wannabe Vampire" chase away
		battle = new Battle(medium, chicken);
		battle.addMonster(vamp);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertTrue(battle.hasMonster(vamp));
		medium.addClassCard(new ClassCard(12, "Cleric", Class.CLERIC));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertFalse(battle.hasMonster(vamp));
		medium.getClassCards().clear();
		battle.addMonster(vamp);
		battle.addHelper(easy);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertTrue(battle.hasMonster(vamp));
		easy.addClassCard(new ClassCard(12, "Cleric", Class.CLERIC));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(battle.hasMonster(vamp));
		easy.getClassCards().clear();
		
		// "Illusion" - strongest with weakest monster
		battle = new Battle(medium, squid);
		battle.addMonster(orcs);
		medium.addCard(illusion);
		medium.addCard(bigfoot);
		medium.addCard(nothing);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -12));
		assertTrue(battle.hasMonster(orcs));
		assertTrue(battle.hasMonster(squid));
		assertEquals(medium.getHandCards().size(), 3);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -11));
		assertFalse(battle.hasMonster(squid));
		assertTrue(battle.hasMonster(orcs));
		assertTrue(battle.hasMonster(nothing));
		assertEquals(medium.getHandCards().size(), 1);
		assertTrue(medium.getHandCards().get(0).equals(bigfoot));
		battle.addHelper(easy);
		easy.addCard(dog);
		easy.addCard(illusion);
		easy.addCard(vamp);
		easy.addCard(gazebo);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -8));
		assertTrue(battle.hasMonster(nothing));
		assertTrue(battle.hasMonster(orcs));
		assertEquals(easy.getHandCards().size(), 4);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -7));
		assertTrue(battle.hasMonster(nothing));
		assertFalse(battle.hasMonster(orcs));
		assertTrue(battle.hasMonster(dog));
		assertEquals(easy.getHandCards().size(), 2);
		assertFalse(easy.getHandCards().contains(dog));
		assertFalse(easy.getHandCards().contains(illusion));
		medium.addCard(chicken);
		medium.addCard(illusion);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -4));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -3));
		assertFalse(battle.hasMonster(nothing));
		assertTrue(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(chicken));
		assertEquals(medium.getHandCards().size(), 1);
		assertTrue(medium.getHandCards().get(0).equals(bigfoot));
		medium.getHandCards().clear();
		easy.getHandCards().clear();
		
		// "Pollymorph Potion"
		battle = new Battle(hard, dog);
		hard.addItem(polly);
		battle.addMonsterEnhancer(dog, enraged);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -7));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -6));
		hard.goUpLevels(2, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -7));
		assertEquals(battle.getMonsterCount(), 1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, -6));
		assertEquals(battle.getMonsterCount(), 0);
		assertTrue(hard.getAllValueCards().isEmpty());
		assertTrue(hard.getCarriedItems().isEmpty());
		battle = new Battle(hard, dog);
		hard.addItem(polly);
		battle.addMonsterEnhancer(dog, mate);
		battle.addMonsterEnhancer(dog, enraged);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, -7));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, -6));
		assertEquals(battle.getMonsterCount(), 1);
		assertFalse(battle.getMonsterEnhancers(dog).contains(mate));
		assertTrue(hard.getAllValueCards().isEmpty());
		assertTrue(hard.getCarriedItems().isEmpty());		
		hard.goDownLevels(2);
		
		// "Magic Lamp"
		battle = new Battle(easy, gazebo);
		easy.addItem(lamp);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -7));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -8));
		easy.goUpLevels(3, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -8));
		assertEquals(battle.getMonsterCount(), 1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -7));
		assertEquals(battle.getMonsterCount(), 0);
		assertTrue(easy.getAllValueCards().isEmpty());
		assertTrue(easy.getCarriedItems().isEmpty());
		battle = new Battle(easy, gazebo);
		easy.addItem(lamp);
		battle.addMonsterEnhancer(gazebo, mate);
		battle.addMonsterEnhancer(gazebo, ancient);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -18));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -17));
		assertEquals(battle.getMonsterCount(), 1);
		assertFalse(battle.getMonsterEnhancers(gazebo).contains(mate));
		assertTrue(easy.getAllValueCards().isEmpty());
		assertTrue(easy.getCarriedItems().isEmpty());		
		easy.goDownLevels(3);
		
		// Charm ability
		battle = new Battle(medium, nose);
		medium.addCard(new GoUpLevelCard(13, "Level"));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -10));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -9));
		medium.addClassCard(new ClassCard(14, "Wizard", Class.WIZARD));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -10));
		assertFalse(medium.getHandCards().isEmpty());
		assertEquals(battle.getMonsterCount(), 1);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -9));
		assertEquals(battle.getMonsterCount(), 0);
		assertTrue(medium.getAllValueCards().isEmpty());
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(medium.getHandCards().isEmpty());
		battle = new Battle(medium, nose);
		battle.addMonsterEnhancer(nose, mate);
		battle.addMonsterEnhancer(nose, baby);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -4));
		medium.goUpLevel(false);
		medium.addCard(new GoUpLevelCard(13, "Level"));
		medium.addCard(new ItemCard(15, "Item1", 2800));
		medium.addCard(new ItemCard(16, "Item2", 800));
		medium.addCard(new ItemCard(17, "Item3", 600));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, -4));
		medium.getHandCards().set(1, new ItemCard(17, "Item2", 500));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, -4));
		assertEquals(battle.getMonsterCount(), 1);
		assertFalse(battle.getMonsterEnhancers(nose).contains(mate));
		assertTrue(medium.getAllValueCards().isEmpty());
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(medium.getHandCards().isEmpty());
		medium.goDownLevel();
		medium.getClassCards().clear();
		
		// "Out to Lunch"
		GM.moveToBattlePhase();
		battle = new Battle(easy, bigfoot);
		easy.addCard(lunch);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		easy.getEquippedItems().add(new EquipmentCard(15, "Equip1", 1800, 0, EquipmentType.HEADGEAR, false, false, false));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		easy.getEquippedItems().add(new EquipmentCard(16, "Equip2", 600, 0, EquipmentType.HEADGEAR, false, false, false));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		assertTrue(easy.getHandCards().contains(lunch));
		assertTrue(battle.hasMonster(bigfoot));
		easy.getEquippedItems().add(new EquipmentCard(17, "Equip3", 100, 0, EquipmentType.HEADGEAR, false, false, false));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getHandCards().contains(lunch));
		assertFalse(GM.isBattlePhase());
		easy.getHandCards().clear();
		easy.getCarriedItems().clear();
		easy.getEquippedItems().clear();
		easy.getUnequippedItems().clear();
		easy.setHirelingCard(null);
		GM.moveToBattlePhase();
		battle = new Battle(easy, bigfoot);
		easy.addCard(lunch);
		easy.getEquippedItems().add(new EquipmentCard(15, "Equip1", 1800, 0, EquipmentType.HEADGEAR, false, false, false));
		easy.getEquippedItems().add(new EquipmentCard(16, "Equip2", 600, 0, EquipmentType.HEADGEAR, false, false, false));
		easy.getEquippedItems().add(new EquipmentCard(17, "Equip3", 100, 0, EquipmentType.HEADGEAR, false, false, false));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		assertFalse(easy.getHandCards().contains(lunch));
		assertFalse(GM.isBattlePhase());
		easy.getHandCards().clear();
		easy.getCarriedItems().clear();
		easy.getEquippedItems().clear();
		easy.getUnequippedItems().clear();
		easy.setHirelingCard(null);
		easy.goDownLevels(10);
		
		// "Friendship Potion"
		GM.moveToBattlePhase();
		battle = new Battle(easy, nothing);
		easy.addItem(friend);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		easy.addCard(new ItemCard(15, "Item1", 1800));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		easy.addCard(new ItemCard(16, "Item2", 600));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		assertTrue(easy.getCarriedItems().contains(friend));
		assertTrue(GM.isBattlePhase());
		easy.addCard(new ItemCard(17, "Item3", 100));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 0));
		assertFalse(easy.getCarriedItems().contains(friend));
		assertFalse(GM.isBattlePhase());
		battle = new Battle(easy, nothing);
		easy.addItem(friend);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -100));
		assertFalse(easy.getCarriedItems().contains(friend));
		assertFalse(GM.isBattlePhase());
		assertEquals(easy.getHandCards().size(), 3);
		easy.getHandCards().clear();
		assertTrue(easy.getAllValueCards().isEmpty());
		assertTrue(easy.getLevel() == 1);
		
		// "Transferral Potion"
		medium.goUpLevels(2, false);
		hard.goUpLevels(4, false);
		human.goUpLevels(5, false);
		battle = new Battle(easy, nose);
		battle.addHelper(hard);
		easy.addItem(trans);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		easy.goUpLevels(2, false);
		medium.goUpLevels(2, false);
		hard.goUpLevels(2, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertTrue(easy.getCarriedItems().contains(trans));
		assertTrue(battle.activePlayer == easy);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -2));
		assertFalse(easy.getCarriedItems().contains(trans));
		assertTrue(battle.activePlayer == medium);
		medium.addItem(trans);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 0));
		assertFalse(medium.getCarriedItems().contains(trans));
		assertTrue(battle.activePlayer == easy);
		easy.addItem(trans);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		battle.addMonster(dog);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, -1));
		assertFalse(easy.getCarriedItems().contains(trans));
		assertTrue(battle.activePlayer == medium);
		hard.addItem(trans);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 0));
		assertFalse(hard.getCarriedItems().contains(trans));
		assertTrue(battle.activePlayer == human);
		easy.goDownLevels(10);
		medium.goDownLevels(10);
		hard.goDownLevels(10);
		human.goDownLevels(10);
		
		} catch (EndGameException ex) { fail("Should not be Game End"); }
	}
	
	private void testNormalTryToStopWinBattle() {
		try {
		// "Transferral Potion"
		GM.moveToBattlePhase();
		Battle battle = new Battle(easy, dog);
		battle.addHelper(human);
		easy.goUpLevels(6, false);
		medium.goUpLevels(7, false);
		hard.goUpLevels(8, false);
		hard.addItem(new ItemCard(10, "Item", 1000));
		medium.addItem(trans);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == medium);
		battle.activePlayer = easy;
		medium.addItem(trans);
		hard.addLastingCurse(changeSex);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == hard);
		hard.removeSexChangeCurse();
		battle.activePlayer = medium;
		easy.addItem(trans);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertFalse(easy.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == medium);
		battle.helper = null;
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		assertTrue(easy.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == human);
		battle.activePlayer = hard;
		human.addItem(trans);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, human, 1));
		assertTrue(human.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == medium);
		battle.activePlayer = hard;
		hard.addLastingCurse(changeSex);
		human.addItem(trans);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, human, 1));
		assertTrue(human.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == human);
		battle.activePlayer = hard;
		hard.goDownLevel();
		easy.goDownLevel();
		medium.goDownLevel();
		human.addItem(trans);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, human, 1));
		assertFalse(human.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == hard);
		easy.goDownLevels(10);
		medium.goDownLevels(10);
		hard.goDownLevels(10);
		hard.getCarriedItems().clear();
		hard.removeSexChangeCurse();
		human.getCarriedItems().clear();
		
		// normal battle items
		ItemCard item1 = new ItemCard(15, "Item1", 100, 1);
		ItemCard item2 = new ItemCard(16, "Item2", 250, 2);
		ItemCard item3 = new ItemCard(17, "Item3", 300, 3);
		ItemCard item4 = new ItemCard(18, "Item4", 4000, 4);
		battle = new Battle(easy, squid);
		battle.addHelper(medium);
		hard.addItem(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertTrue(hard.getCarriedItems().contains(item1));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(item1));
		hard.addItem(item3);
		hard.addItem(item2);
		hard.addItem(item4);
		hard.addItem(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 7));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 6));
		assertTrue(hard.getCarriedItems().contains(item1));
		assertTrue(hard.getCarriedItems().contains(item2));
		assertFalse(hard.getCarriedItems().contains(item3));
		assertTrue(hard.getCarriedItems().contains(item4));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 3));
		assertFalse(hard.getCarriedItems().contains(item1));
		assertTrue(hard.getCarriedItems().contains(item2));
		assertFalse(hard.getCarriedItems().contains(item3));
		assertTrue(hard.getCarriedItems().contains(item4));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(item1));
		assertFalse(hard.getCarriedItems().contains(item2));
		assertFalse(hard.getCarriedItems().contains(item3));
		assertTrue(hard.getCarriedItems().contains(item4));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(item1));
		assertFalse(hard.getCarriedItems().contains(item2));
		assertFalse(hard.getCarriedItems().contains(item3));
		assertTrue(hard.getCarriedItems().contains(item4));
		easy.goUpLevels(10, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getCarriedItems().contains(item1));
		assertFalse(hard.getCarriedItems().contains(item2));
		assertFalse(hard.getCarriedItems().contains(item3));
		assertFalse(hard.getCarriedItems().contains(item4));
		easy.goDownLevels(10);
		
		// curse cards
		hard.addCard(changeSex);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 6));
		assertFalse(hard.getHandCards().isEmpty());
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 5));
		assertTrue(hard.getHandCards().isEmpty());
		assertTrue(easy.hasChangeSexCurse());
		easy.removeSexChangeCurse();
		hard.addCard(loseClass);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getHandCards().isEmpty());
		easy.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertFalse(hard.getHandCards().isEmpty());
		assertEquals(easy.getLevel(), 2);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		hard.addCard(loseClass);
		assertEquals(easy.getLevel(), 1);
		easy.addClassCard(new ClassCard(19, "Warrior", Class.WARRIOR));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertFalse(hard.getHandCards().isEmpty());
		assertTrue(easy.isWarrior());
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(easy.isWarrior());
		hard.addCard(doom);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		easy.goUpLevel(false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertFalse(hard.getHandCards().isEmpty());
		assertEquals(easy.getLevel(), 2);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertEquals(easy.getLevel(), 1);
		hard.addCard(doom);
		easy.goUpLevels(4, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 3));
		assertFalse(hard.getHandCards().isEmpty());
		assertEquals(easy.getLevel(), 5);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertEquals(easy.getLevel(), 3);
		hard.addCard(doom);
		easy.goUpLevel(false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertTrue(hard.getHandCards().isEmpty());
		assertEquals(medium.getLevel(), 1);
		hard.addCard(doom);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertFalse(hard.getHandCards().isEmpty());
		assertEquals(easy.getLevel(), 2);
		medium.goUpLevels(2, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertEquals(medium.getLevel(), 1);
		easy.goDownLevel();
		hard.addCard(loseItem);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getHandCards().isEmpty());
		easy.addItem(dopple);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getHandCards().isEmpty());
		assertFalse(easy.getCarriedItems().isEmpty());
		easy.goUpLevels(10, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertTrue(easy.getCarriedItems().isEmpty());
		easy.goDownLevels(10);
		
		// monster enhancers
		hard.addCard(enraged);
		hard.addCard(ancient);
		hard.addCard(baby);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 16));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 15));
		assertFalse(hard.getHandCards().contains(enraged));
		assertTrue(hard.getHandCards().contains(ancient));
		assertTrue(hard.getHandCards().contains(baby));
		assertTrue(battle.getMonsterEnhancers(0).contains(enraged));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 11));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 10));
		assertFalse(hard.getHandCards().contains(enraged));
		assertFalse(hard.getHandCards().contains(ancient));
		assertTrue(hard.getHandCards().contains(baby));
		assertTrue(battle.getMonsterEnhancers(0).contains(ancient));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(hard.getHandCards().contains(enraged));
		assertFalse(hard.getHandCards().contains(ancient));
		assertTrue(hard.getHandCards().contains(baby));
		assertFalse(battle.getMonsterEnhancers(0).contains(baby));
		hard.addCard(enraged);
		hard.addCard(ancient);
		battle.getMonsterEnhancers(0).clear();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 6));
		assertFalse(hard.getHandCards().contains(enraged));
		assertTrue(hard.getHandCards().contains(ancient));
		assertTrue(hard.getHandCards().contains(baby));
		assertTrue(battle.getMonsterEnhancers(0).contains(enraged));
		battle.getMonsterEnhancers(0).clear();
		hard.addCard(enraged);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().contains(enraged));
		assertFalse(hard.getHandCards().contains(ancient));
		assertTrue(hard.getHandCards().contains(baby));
		assertTrue(battle.getMonsterEnhancers(0).contains(ancient));
		battle.getMonsterEnhancers(0).clear();
		hard.getHandCards().clear();
				
		// Backstab ability
		ClassCard thief = new ClassCard(20, "Thief", Class.THIEF);
		hard.addClassCard(thief);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		hard.addCard(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 3));
		assertTrue(battle.canBackstab(hard, easy));
		assertTrue(battle.canBackstab(hard, medium));
		hard.addCard(item2);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 3));
		assertFalse(hard.getHandCards().contains(item1));
		assertTrue(hard.getHandCards().contains(item2));
		assertFalse(battle.canBackstab(hard, easy));
		assertTrue(battle.canBackstab(hard, medium));
		hard.addCard(item1);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 3));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertFalse(hard.getHandCards().contains(item1));
		assertTrue(hard.getHandCards().contains(item2));
		assertFalse(battle.canBackstab(hard, easy));
		assertFalse(battle.canBackstab(hard, medium));
		easy.goUpLevels(10, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		medium.addClassCard(thief);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 5));
		medium.addCard(item2);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 5));
		assertFalse(medium.getHandCards().contains(item2));
		assertFalse(battle.canBackstab(medium, easy));
		assertFalse(battle.canBackstab(medium, medium));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 1));		
		medium.getClassCards().clear();
		hard.getHandCards().clear();
		easy.goDownLevels(10);
		battle.getMonsterItemCards().clear();
		
		// best battle actions
		battle = new Battle(easy, squid);
		battle.addHelper(medium);
		easy.goUpLevel(false);
		hard.addItem(item1); 	// (100 / 1)   = .01
		hard.addCard(ancient);	// (1000 / 10) = .01
		hard.addItem(item3); 	// (300 / 3)   = .01
		hard.addCard(item2); 	// (250 / 2)   = .008
		hard.addCard(item3); 	// (300 / 2)   = .006
		hard.addItem(item4);    // (4000 / 4)  = .001
		hard.addCard(doom); 	// (2000 / 1)  = .0005
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 23));
		assertEquals(hard.getHandCards().size(), 4);
		assertEquals(hard.getCarriedItems().size(), 3);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 22));
		assertEquals(hard.getHandCards().size(), 4);
		assertEquals(hard.getCarriedItems().size(), 2);
		assertTrue(battle.getMonsterItemCards().contains(item1));		
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 22));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 21));
		assertEquals(hard.getHandCards().size(), 4);
		assertEquals(hard.getCarriedItems().size(), 1);
		assertTrue(battle.getMonsterItemCards().contains(item3));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 20));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 19));
		assertEquals(hard.getHandCards().size(), 3);
		assertEquals(hard.getCarriedItems().size(), 1);
		assertTrue(battle.getMonsterEnhancers(0).contains(ancient));
		assertTrue(battle.canBackstab(hard, easy));
		assertTrue(battle.canBackstab(hard, medium));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 10));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 9));
		assertEquals(hard.getHandCards().size(), 2);
		assertEquals(hard.getCarriedItems().size(), 1);
		assertFalse(hard.getHandCards().contains(item2));
		assertFalse(battle.canBackstab(hard, easy));
		assertTrue(battle.canBackstab(hard, medium));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 8));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 7));
		assertEquals(hard.getHandCards().size(), 1);
		assertEquals(hard.getCarriedItems().size(), 1);
		assertFalse(hard.getHandCards().contains(item3));
		assertFalse(battle.canBackstab(hard, easy));
		assertFalse(battle.canBackstab(hard, medium));
		assertEquals(easy.getLevel(), 2);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 6));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 5));
		assertEquals(hard.getHandCards().size(), 1);
		assertEquals(hard.getCarriedItems().size(), 0);
		assertTrue(battle.getMonsterItemCards().contains(item4));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 2));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));		
		assertEquals(hard.getHandCards().size(), 0);
		assertEquals(hard.getCarriedItems().size(), 0);
		assertEquals(easy.getLevel(), 1);		
		hard.getClassCards().clear();
		} catch (EndGameException ex) { fail("Should not be Game End"); }
	}
	
	private void testLastResortToStopWinBattle() {
		try{
		// Illusion
		Battle battle = new Battle(hard, dog);
		battle.addMonster(bigfoot);
		battle.addHelper(easy);
		hard.getEquippedItems().add(new EquipmentCard(10, "Equip", 100, 6, EquipmentType.OTHER, false, false, false));
		easy.goUpLevels(10, false);
		easy.getEquippedItems().add(farmor);
		easy.getEquippedItems().add(helm);
		medium.addCard(tdemon);
		medium.addCard(squid);
		medium.addCard(chicken);
		medium.addCard(illusion);
		medium.addCard(gazebo);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(illusion));
		assertFalse(medium.getHandCards().contains(gazebo));
		assertFalse(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(gazebo));
		assertTrue(battle.hasMonster(bigfoot));
		assertFalse(battle.isHelper());
		battle = new Battle(hard, dog);
		battle.addMonster(bigfoot);
		battle.addHelper(easy);
		easy.goDownLevels(10);
		medium.addCard(gazebo);
		medium.addCard(illusion);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(illusion));
		assertFalse(medium.getHandCards().contains(tdemon));
		assertFalse(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(tdemon));
		assertTrue(battle.hasMonster(bigfoot));
		assertTrue(hard.getAllEquipment().isEmpty());
		assertEquals(easy.getAllEquipment().size(), 1);
		battle = new Battle(hard, dog);
		battle.addMonster(bigfoot);
		battle.addHelper(easy);
		easy.getEquippedItems().clear();
		medium.addCard(illusion);
		medium.addCard(tdemon);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 17));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 16));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(illusion));
		assertFalse(medium.getHandCards().contains(squid));
		assertTrue(battle.hasMonster(bigfoot));
		assertFalse(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(squid));
		medium.addCard(illusion);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		battle = new Battle(hard, dog);
		battle.addMonster(bigfoot);
		battle.addHelper(easy);
		medium.addCard(squid);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 20));
		RaceCard elf = new RaceCard(11, "Elf", Race.ELF);
		easy.addRaceCard(elf);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 20));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(illusion));
		assertFalse(medium.getHandCards().contains(squid));
		assertFalse(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(squid));
		assertTrue(battle.hasMonster(bigfoot));
		battle = new Battle(hard, dog);
		battle.addMonster(bigfoot);
		battle.addHelper(easy);
		easy.getRaceCards().clear();
		hard.addItem(halitosis);
		medium.getHandCards().clear();
		medium.addCard(nose);
		medium.addCard(illusion);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		hard.getCarriedItems().clear();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertTrue(medium.getHandCards().isEmpty());
		assertFalse(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(nose));
		assertTrue(battle.hasMonster(bigfoot));
		
		// "Wandering Monster"
		battle = new Battle(hard, dog);
		battle.addHelper(easy);
		hard.getEquippedItems().add(new EquipmentCard(10, "Equip", 100, 6, EquipmentType.OTHER, false, false, false));
		easy.goUpLevels(10, false);
		easy.getEquippedItems().add(farmor);
		easy.getEquippedItems().add(helm);
		medium.addCard(tdemon);
		medium.addCard(squid);
		medium.addCard(chicken);
		medium.addCard(wander);
		medium.addCard(gazebo);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(wander));
		assertFalse(medium.getHandCards().contains(gazebo));
		assertTrue(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(gazebo));
		assertFalse(battle.isHelper());
		battle = new Battle(hard, dog);
		battle.addHelper(easy);
		easy.goDownLevels(10);
		medium.addCard(gazebo);
		medium.addCard(wander);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(wander));
		assertFalse(medium.getHandCards().contains(tdemon));
		assertTrue(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(tdemon));
		assertTrue(hard.getAllEquipment().isEmpty());
		assertEquals(easy.getAllEquipment().size(), 1);
		battle = new Battle(hard, dog);
		battle.addMonster(bigfoot);
		battle.addHelper(easy);
		easy.getEquippedItems().clear();
		medium.addCard(wander);
		medium.addCard(tdemon);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 19));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 18));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(wander));
		assertFalse(medium.getHandCards().contains(squid));
		assertTrue(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(squid));
		assertTrue(battle.hasMonster(bigfoot));
		medium.addCard(wander);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 13));
		battle = new Battle(hard, dog);
		battle.addHelper(easy);
		medium.addCard(squid);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 22));
		hard.addRaceCard(elf);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 22));
		assertEquals(medium.getHandCards().size(), 3);
		assertFalse(medium.getHandCards().contains(wander));
		assertFalse(medium.getHandCards().contains(squid));
		assertTrue(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(squid));
		battle = new Battle(hard, dog);
		battle.addHelper(easy);
		hard.getRaceCards().clear();
		easy.addItem(halitosis);
		medium.getHandCards().clear();
		medium.addCard(nose);
		medium.addCard(wander);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		easy.getCarriedItems().clear();
		assertTrue(AIBattleManager.makeBattleDecisions(battle, medium, 1));
		assertTrue(medium.getHandCards().isEmpty());
		assertTrue(battle.hasMonster(dog));
		assertTrue(battle.hasMonster(nose));
		
		// Mate
		battle = new Battle(medium, dog);
		battle.addHelper(hard);
		easy.addCard(mate);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 1));
		battle = new Battle(medium, squid);
		battle.addHelper(hard);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 19));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 18));
		assertTrue(easy.getHandCards().isEmpty());
		assertTrue(battle.getMonsterEnhancers(0).contains(mate));
		battle = new Battle(medium, squid);
		battle.addHelper(hard);
		battle.addMonsterEnhancer(squid, ancient);
		battle.addMonsterEnhancer(squid, enraged);
		easy.addCard(mate);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, easy, 34));
		assertTrue(AIBattleManager.makeBattleDecisions(battle, easy, 33));
		assertTrue(easy.getHandCards().isEmpty());
		assertTrue(battle.getMonsterEnhancers(0).contains(mate));
		
		// Friendship Potion
		GM.moveToBattlePhase();
		battle = new Battle(medium, dog);
		battle.addHelper(easy);
		hard.addItem(friend);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 30));
		battle = new Battle(medium, squid);
		battle.addHelper(easy);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(GM.isBattlePhase());
		GM.moveToBattlePhase();
		battle = new Battle(medium, dog);
		battle.addHelper(easy);
		hard.addItem(friend);
		medium.goUpLevels(7, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 30));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(GM.isBattlePhase());
		GM.moveToBattlePhase();
		battle = new Battle(medium, dog);
		battle.addHelper(easy);
		hard.addItem(friend);
		medium.goDownLevels(10);
		easy.goUpLevels(8, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		easy.addRaceCard(elf);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(GM.isBattlePhase());
		easy.getRaceCards().clear();
		easy.goDownLevels(10);
		
		// Out to Lunch
		GM.moveToBattlePhase();
		battle = new Battle(medium, dog);
		battle.addHelper(easy);
		hard.addCard(lunch);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 30));
		battle = new Battle(medium, squid);
		battle.addHelper(easy);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(GM.isBattlePhase());
		GM.moveToBattlePhase();
		battle = new Battle(medium, dog);
		battle.addHelper(easy);
		hard.addCard(lunch);
		medium.goUpLevels(10, false);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 30));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(GM.isBattlePhase());
		GM.moveToBattlePhase();
		battle = new Battle(medium, dog);
		battle.addHelper(easy);
		hard.addCard(lunch);
		medium.goDownLevels(10);
		easy.goUpLevels(8, false);
		assertFalse(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		easy.addRaceCard(elf);
		assertTrue(AIBattleManager.makeBattleDecisions(battle, hard, 1));
		assertTrue(hard.getHandCards().isEmpty());
		assertFalse(GM.isBattlePhase());
		easy.getRaceCards().clear();
		easy.goDownLevels(10);		
		} catch (EndGameException ex) { fail("Should not be Game End"); }
	}
	
	private void testGetCurseLevelLoss() {
		Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		for (Card card : doorDeck) {
			if (card instanceof CurseCard) {
				Battle battle = new Battle(medium, dog);				
				CurseCard curse = (CurseCard)card;
				if (card.getID() == Card.CU_CHANGE_CLASS) {
					GM.getDoorDeck().getDiscardPile().clear();
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addClassCard(new ClassCard(10, "Warrior", Class.WARRIOR));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.getEquippedItems().add(shield);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4);
					medium.getClassCards().add(new ClassCard(13, "Wizard", Class.WIZARD));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4);
					medium.getEquippedItems().add(staff);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4 + 5);
					GM.getDoorDeck().getDiscardPile().push(new ClassCard(11, "Warrior", Class.WARRIOR));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					battle = new Battle(medium, geek);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					GM.getDoorDeck().getDiscardPile().clear();					
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4 + 5 - 6);
					GM.getDoorDeck().getDiscardPile().push(medium.getClassCards().removeFirst());
					medium.getEquippedItems().remove(shield);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5 - 1);
					medium.getEquippedItems().add(noWarrior);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5 - 1 + 3);
					medium.getClassCards().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_CHANGE_RACE) {
					GM.getDoorDeck().getDiscardPile().clear();
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addRaceCard(new RaceCard(10, "Elf", Race.ELF));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getEquippedItems().add(helm);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2);
					medium.getRaceCards().add(new RaceCard(12, "Halfling", Race.HALFLING));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2);
					medium.getEquippedItems().add(ladder);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2 + 3);
					GM.getDoorDeck().getDiscardPile().push(new RaceCard(11, "Elf", Race.ELF));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					battle = new Battle(medium, squid);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					GM.getDoorDeck().getDiscardPile().clear();
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2 + 3 - 4);
					GM.getDoorDeck().getDiscardPile().push(medium.getRaceCards().removeFirst());
					medium.getEquippedItems().remove(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					battle = new Battle(medium, bigfoot);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3 - 3);
					GM.getDoorDeck().getDiscardPile().set(0, new RaceCard(13, "Dwarf", Race.DWARF));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3 - 3);
					medium.getRaceCards().clear();
					medium.getEquippedItems().clear();					
				}
				else if (card.getID() == Card.CU_CHANGE_SEX) {
					assertTrue(medium.isFemale());
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.getEquippedItems().add(sword);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5 + 3);
					medium.setCheatCards(new OtherDoorCard(Card.OD_CHEAT, "Cheat!"), sword);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					battle = new Battle(easy, chicken);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, easy, battle), 5);
					easy.changeSex();
					assertTrue(easy.isMale());
					easy.getEquippedItems().add(club);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, easy, battle), 5 + 3);
					easy.setCheatCards(new OtherDoorCard(Card.OD_CHEAT, "Cheat!"), club);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, easy, battle), 5);
					medium.removeCheat();
					easy.removeCheat();
					medium.getEquippedItems().clear();
					easy.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
				}
				else if (card.getID() == Card.CU_DUCK_OF_DOOM) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 2);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 2);
					medium.goDownLevels(10);
				}
				else if (card.getID() == Card.CU_INCOME_TAX) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(15, "Item5", 500, 5));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.getEquippedItems().add(farmor);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 2);
					medium.addItem(new ItemCard(13, "Item3", 300, 3));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 2);
					medium.getEquippedItems().clear();
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					medium.getEquippedItems().add(helm);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.addUnequippedItem(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getUnequippedItems().clear();
					medium.addItem(new ItemCard(10, "Item0", 100));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getCarriedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_LOSE_1_BIG_ITEM) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(15, "Item5", 500, 5));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(10, "Item0", 100));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(shield);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getEquippedItems().add(farmor);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getEquippedItems().add(ladder);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					medium.unequip(shield);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getUnequippedItems().clear();
					medium.getCarriedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_LOSE_1_LEVEL_1) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.goDownLevels(10);
				}
				else if (card.getID() == Card.CU_LOSE_1_SMALL_ITEM_2) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(15, "Item5", 500, 5));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.getEquippedItems().add(ladder);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.getEquippedItems().add(farmor);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 2);
					medium.addItem(new ItemCard(13, "Item3", 300, 3));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 2);
					medium.getEquippedItems().clear();
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					medium.getEquippedItems().add(helm);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.addUnequippedItem(ladder);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.addUnequippedItem(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getUnequippedItems().clear();
					medium.addItem(new ItemCard(10, "Item0", 100));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getCarriedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_LOSE_THE_ARMOR_YOU_ARE_WEARING) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(new EquipmentCard(10, "armor1", 100, 4, EquipmentType.ARMOR, false, false, false));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getEquippedItems().add(new EquipmentCard(11, "armor2", 600, 2, EquipmentType.ARMOR, false, true, false));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2);
					medium.getEquippedItems().add(new EquipmentCard(12, "armor3", 1000, 1, EquipmentType.ARMOR, true, false, true));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2 + 1);
					medium.getEquippedItems().add(new EquipmentCard(13, "hat", 1000, 1, EquipmentType.HEADGEAR, true, false, true));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2 + 1);
					medium.unequip(medium.getEquippedItems().get(1));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 1);
					medium.getUnequippedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(new EquipmentCard(13, "armor", 500, 6, EquipmentType.ARMOR, true, true, true));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(new EquipmentCard(10, "shoes1", 600, 8, EquipmentType.FOOTGEAR, true, false, false));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 8);
					medium.getEquippedItems().add(new EquipmentCard(11, "shoes2", 800, 1, EquipmentType.FOOTGEAR, false, true, true));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 8 + 1);
					medium.getEquippedItems().add(new EquipmentCard(12, "shoes3", 10, 12, EquipmentType.FOOTGEAR, true, true, true));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 8 + 1 + 12);
					medium.unequip(medium.getEquippedItems().get(1));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 12);
					medium.getUnequippedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(new EquipmentCard(10, "hat1", 200, 10, EquipmentType.HEADGEAR, true, true, false));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 10);
					medium.getEquippedItems().add(new EquipmentCard(11, "hat2", 300, 3, EquipmentType.HEADGEAR, false, true, false));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 10 + 3);
					medium.getEquippedItems().add(new EquipmentCard(13, "shoes", 0, 3, EquipmentType.FOOTGEAR, false, false, false));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 10 + 3);
					medium.getEquippedItems().add(new EquipmentCard(12, "hat3", 0, 7, EquipmentType.HEADGEAR, false, false, true));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 10 + 3 + 7);
					medium.unequip(medium.getEquippedItems().get(1));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 10 + 7);
					medium.getUnequippedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_LOSE_TWO_CARDS) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
				}
				else if (card.getID() == Card.CU_LOSE_YOUR_CLASS) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addClassCard(new ClassCard(10, "Warrior", Class.WARRIOR));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.getEquippedItems().add(shield);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4);
					medium.getClassCards().add(new ClassCard(13, "Wizard", Class.WIZARD));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4);
					medium.getEquippedItems().add(staff);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4 + 5);
					battle = new Battle(medium, geek);		
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1 + 4 + 5 - 6);
					medium.getClassCards().removeFirst();
					medium.getEquippedItems().remove(shield);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.getClassCards().clear();
					medium.getEquippedItems().clear();
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.goUpLevel(false);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 1);
					medium.goDownLevels(2);
				}
				else if (card.getID() == Card.CU_LOSE_YOUR_RACE) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addRaceCard(new RaceCard(10, "Elf", Race.ELF));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getEquippedItems().add(helm);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2);
					medium.getRaceCards().add(new RaceCard(12, "Halfling", Race.HALFLING));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2);
					medium.getEquippedItems().add(ladder);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2 + 3);
					battle = new Battle(medium, squid);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4 + 2 + 3 - 4);
					medium.getRaceCards().removeFirst();
					medium.getEquippedItems().remove(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					battle = new Battle(medium, bigfoot);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3 - 3);
					medium.getRaceCards().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_MALIGN_MIRROR) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(15, "Item5", 500, 5));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(10, "Item0", 100));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getEquippedItems().add(sword);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 7);
					medium.getEquippedItems().add(ladder);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 7);
					medium.unequip(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					medium.getUnequippedItems().clear();
					medium.getCarriedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() == Card.CU_TRULY_OBNOXIOUS_CURSE) {
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(15, "Item5", 500, 5));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.addItem(new ItemCard(10, "Item0", 100));
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 0);
					medium.getEquippedItems().add(sword);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 3);
					medium.getEquippedItems().add(staff);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.getEquippedItems().add(bow);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 5);
					medium.unequip(staff);
					assertEquals(AIBattleManager.getCurseLevelLoss(curse, medium, battle), 4);
					medium.getUnequippedItems().clear();
					medium.getCarriedItems().clear();
					medium.getEquippedItems().clear();
				}
				else if (card.getID() != Card.CU_LOSE_1_LEVEL_2 && card.getID() != Card.CU_LOSE_1_SMALL_ITEM_1){
					fail("Unknown Curse Card: (" + card.getID() + ")" + card.getName());
				}
			}
		}
	}
	
	private void testGetMonsterToBattle() {
		easy.getHandCards().clear();
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.getHandCards().add(nose); // 10, 3, 1
		easy.getHandCards().add(squid); // 18, 4, 2
		easy.getHandCards().add(tut); // 16, 4, 2
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.addItem(halitosis);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().clear();
		easy.goUpLevels(3, false);
		easy.getEquippedItems().add(helm);
		easy.getEquippedItems().add(farmor);
		easy.getEquippedItems().add(club);
		EquipmentCard e5 = new EquipmentCard(20, "e5", 100, 5, EquipmentType.OTHER, false, false, false);
		easy.getEquippedItems().add(e5);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.goUpLevel(false);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.goDownLevel();
		easy.addClassCard(new ClassCard(21, "Warrior", Class.WARRIOR));
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.goDownLevels(10);
		easy.getClassCards().clear();
		easy.getEquippedItems().remove(farmor);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.addClassCard(new ClassCard(21, "Warrior", Class.WARRIOR));
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.goUpLevel(false);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.getEquippedItems().remove(club);
		easy.getEquippedItems().add(farmor);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		
		easy.addItem(friend);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().set(0, wall);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().set(0, invis);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().set(0, loaded);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().set(0, trans);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.unequip(helm);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.getCarriedItems().set(0, lamp);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == squid);
		easy.equip(helm);		
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().set(0, polly);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().clear();
		easy.getClassCards().add(new ClassCard(22, "Wizard", Class.WIZARD));
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getClassCards().removeLast();
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		
		easy.unequip(helm);
		easy.getEquippedItems().add(club);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.addItem(dopple);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().clear();
		easy.addCard(baby);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.addItem(new ItemCard(23, "Item1", 100, 1));
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getCarriedItems().clear();
		easy.getHandCards().add(doom);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);		
		easy.unequip(farmor);
		easy.getCarriedItems().add(water);
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.addRaceCard(new RaceCard(24, "Elf", Race.ELF));
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getClassCards().clear();
		assertNull(AIBattleManager.getMonsterToBattle(easy));
		easy.getClassCards().add(new ClassCard(25, "Cleric", Class.CLERIC));
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == tut);
		easy.addItem(friend);
		assertTrue(AIBattleManager.getMonsterToBattle(easy) == nose);
		easy.getClassCards().clear();
		easy.getRaceCards().clear();
		easy.getHandCards().clear();
		easy.getEquippedItems().clear();
		easy.getUnequippedItems().clear();
		easy.getCarriedItems().clear();
		easy.goDownLevels(10);
	}
}
