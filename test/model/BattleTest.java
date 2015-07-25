
package model;

import exceptions.EndGameException;
import gui.MockGUI;

import java.util.LinkedList;

import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.DoorCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;

public class BattleTest extends UnitTest {	
	private MonsterCard nose;
	private MonsterCard gazebo;
	private MonsterCard squid;
	private MonsterCard fiends;
	private MonsterCard sales;
	private MonsterCard nothing;
	private MonsterCard plant;
	private MonsterCard chicken;
	private MonsterCard tdemon;
	private MonsterCard amazon;
	private MonsterCard lawyer;
	
	private DoorCard mate;
	private DoorCard baby;
	private DoorCard ancient;
	private DoorCard enraged;
	private DoorCard big;
	private DoorCard smart;
	
	private CurseCard change;
	private CurseCard malign;

	private EquipmentCard farmor;
	private EquipmentCard napalm;
	private EquipmentCard flame;
	
	private ItemCard halitosis;
	private ItemCard dopple;
	private ItemCard water;
	private ItemCard fpotion;
	
	private OtherTreasureCard hireling;
	
	private Battle battle;
	
	private Player mainPlayer;
	private Player helpingPlayer;
	private Player otherPlayer;
	
	private MonsterCard monsterA;
	private MonsterCard monsterB;
	private MonsterCard monsterC;
	
	public int testAll() {
		initializeObjects();
		
		testConstructor();
		testHelper();
		testMonster();
		testMonsterEnhancer();
		testMonsterItemCards();
		testPlayerItemCards();
		testEndBattle();		
		testTurning();
		testCharm();
		testBeserking();
		testBackstab();
		testReplacePlayer();
		testPlayersLevel();
		testMonstersLevel();
		testDefeatMonster();
		testDiscardMonster();
		testBefriendMonster();
		testUsedFire();
		testWinTreasureCount();
		testWinLevelCount();
		testCheckForChanges();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		for (Card card : DoorDeckFactory.buildDeck()) {
			if (card.getID() == Card.M_FLOATING_NOSE)
				nose = (MonsterCard)card;
			else if (card.getID() == Card.M_GAZEBO)
				gazebo = (MonsterCard)card;
			else if (card.getID() == Card.M_SQUIDZILLA)
				squid = (MonsterCard)card;
			else if (card.getID() == Card.M_GHOULFIENDS)
				fiends = (MonsterCard)card;
			else if (card.getID() == Card.M_INSURANCE_SALESMAN)
				sales = (MonsterCard)card;
			else if (card.getID() == Card.M_THE_NOTHING)
				nothing = (MonsterCard)card;
			else if (card.getID() == Card.M_POTTED_PLANT)
				plant = (MonsterCard)card;
			else if (card.getID() == Card.M_LARGE_ANGRY_CHICKEN)
				chicken = (MonsterCard)card;
			else if (card.getID() == Card.M_TONGUE_DEMON)
				tdemon = (MonsterCard)card;
			else if (card.getID() == Card.M_AMAZON)
				amazon = (MonsterCard)card;
			else if (card.getID() == Card.M_LAWYER)
				lawyer = (MonsterCard)card;
			else if (card.getID() == Card.CU_CHANGE_SEX)
				change = (CurseCard)card;
			else if (card.getID() == Card.CU_MALIGN_MIRROR)
				malign = (CurseCard)card;
			else if (card.getID() == Card.OD_MATE)
				mate = (DoorCard)card;
			else if (card.getID() == Card.ME_BABY)
				baby = (DoorCard)card;
			else if (card.getID() == Card.ME_ANCIENT)
				ancient = (DoorCard)card;
			else if (card.getID() == Card.ME_ENRAGED)
				enraged = (DoorCard)card;
			else if (card.getID() == Card.ME_HUMONGOUS)
				big = (DoorCard)card;
			else if (card.getID() == Card.ME_INTELLIGENT)
				smart = (DoorCard)card;
		}
		
		for (Card card : TreasureDeckFactory.buildDeck()) {
			if (card.getID() == Card.E_FLAMING_ARMOR)
				farmor = (EquipmentCard)card;
			else if (card.getID() == Card.E_STAFF_OF_NAPALM)
				napalm = (EquipmentCard)card;
			else if (card.getID() == Card.E_FLAMETHROWER)
				flame = (EquipmentCard)card;
			else if (card.getID() == Card.I_POTION_OF_HALITOSIS)
				halitosis = (ItemCard)card;
			else if (card.getID() == Card.I_DOPPLEGANGER)
				dopple = (ItemCard)card;
			else if (card.getID() == Card.I_YUPPIE_WATER)
				water = (ItemCard)card;
			else if (card.getID() == Card.I_FLAMING_POISON_POTION)
				fpotion = (ItemCard)card;
			else if (card.getID() == Card.OT_HIRELING)
				hireling = (OtherTreasureCard)card;
		}
		
		MockGUI mockGUI = new MockGUI(0);
		mainPlayer = new Player(mockGUI, "main", true, PlayerType.TEST);
		helpingPlayer = new Player(mockGUI, "helper", false, PlayerType.TEST);
		otherPlayer = new Player(mockGUI, "other", true, PlayerType.TEST);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(mainPlayer);
		players.add(helpingPlayer);
		players.add(otherPlayer);
		GM.newTestGame(mockGUI, players);
		assertTrue(GM.getTreasureDeck().removeCard(new OtherTreasureCard(Card.OT_HOARD, "HOARD")));
		
		// level, treasure, winLevels, undead
		monsterA = new MonsterCard(0, "A", 5, 3, 2, false) {public void doBadStuff(Player player){}};
		monsterB = new MonsterCard(1, "B", 3, 2, 1, true) {public void doBadStuff(Player player){}};
		monsterC = new MonsterCard(2, "C", 5, 1, 1, false) {public void doBadStuff(Player player){}};
	}
	
	private void testConstructor() {		
		battle = new Battle(mainPlayer, monsterA);
		assertTrue(battle.activePlayer == mainPlayer);
		assertNull(battle.helper);
		
		assertTrue(battle.getPlayerItemCards().isEmpty());
		
		assertTrue(battle.getMonsterCount() == 1);
		assertTrue(battle.getMonster(0) == monsterA);
		assertTrue(battle.getMonsterEnhancers(0).isEmpty());
		assertTrue(battle.getMonsterEnhancers(monsterA).isEmpty());
		assertTrue(battle.getMonsterItemCards().isEmpty());
		
		assertEquals(battle.getTreasureCount(), 0);
	}
	
	private void testHelper() {
		assertFalse(battle.isHelper());
		assertTrue(battle.canAddHelper());
		
		try { battle.addPlayerItemCard(dopple); } catch (EndGameException ex) { fail("Is Not Game End"); }
		assertEquals(battle.getPlayerItemCards().size(), 1);
		assertFalse(battle.canAddHelper());
		battle.getPlayerItemCards().removeLast();
		assertTrue(battle.canAddHelper());
		
		battle.addMonster(gazebo);
		assertFalse(battle.canAddHelper());
		battle.befriendMonster(gazebo);
		assertTrue(battle.canAddHelper());
		
		battle.addHelper(null);
		assertFalse(battle.isHelper());
		battle.addHelper(helpingPlayer);
		assertTrue(battle.isHelper());
		assertFalse(battle.canAddHelper());
		
		battle.removeHelper();
		assertFalse(battle.isHelper());
		assertNull(battle.helper);
		assertTrue(battle.canAddHelper());
		
		battle.addHelper(helpingPlayer);
		assertTrue(battle.isHelper());
		assertFalse(battle.canAddHelper());
		
		battle.removeHelper();
		battle.addMonster(tdemon);
		helpingPlayer.addItem(water);
		assertTrue(helpingPlayer.getCarriedItems().contains(water));
		battle.addHelper(helpingPlayer);
		assertTrue(battle.isHelper());
		assertFalse(battle.canAddHelper());
		assertFalse(helpingPlayer.getCarriedItems().contains(water));
		battle.befriendMonster(tdemon);
	}
	
	private void testMonster() {
		assertEquals(battle.getMonsterCount(), 1);
		assertTrue(battle.hasMonster(monsterA));
		battle.addMonster(monsterB);
		assertEquals(battle.getMonsterCount(), 2);
		assertTrue(battle.hasMonster(monsterB));
		
		battle.addMonster(gazebo);
		assertEquals(battle.getMonsterCount(), 3);
		assertTrue(battle.hasMonster(gazebo));
		assertFalse(battle.isHelper());
		
		battle.addMonsterEnhancer(gazebo, mate);
		battle.replaceMonster(gazebo, monsterC);
		assertEquals(battle.getMonsterCount(), 3);
		assertFalse(battle.hasMonster(gazebo));
		assertTrue(battle.hasMonster(monsterC));
		assertNull(battle.getMonster(-1));
		assertTrue(battle.getMonster(0) == monsterA);
		assertTrue(battle.getMonster(1) == monsterB);
		assertTrue(battle.getMonster(2) == monsterC);
		assertNull(battle.getMonster(3));
		assertTrue(battle.getMonsterEnhancers(monsterC).isEmpty());
		
		battle.addHelper(helpingPlayer);
		battle.befriendMonster(battle.getMonster(2));
		assertEquals(battle.getMonsterCount(), 2);
		assertFalse(battle.hasMonster(monsterC));
		
		mainPlayer.addItem(fpotion);
		helpingPlayer.addItem(dopple);
		assertTrue(mainPlayer.getCarriedItems().contains(fpotion));
		assertTrue(helpingPlayer.getCarriedItems().contains(dopple));
		battle.addMonster(tdemon);
		assertTrue(battle.hasMonster(tdemon));
		assertFalse(mainPlayer.getCarriedItems().contains(fpotion));
		assertFalse(helpingPlayer.getCarriedItems().contains(dopple));
		
		battle.befriendMonster(tdemon);
		assertFalse(battle.hasMonster(tdemon));
	}
	
	private void testMonsterEnhancer() {
		assertTrue(battle.getMonsterEnhancers(0).isEmpty());
		assertTrue(battle.getMonsterEnhancers(monsterB).isEmpty());
		
		battle.addMonsterEnhancer(monsterA, mate);
		battle.addMonsterEnhancer(monsterA, big);
		battle.addMonsterEnhancer(monsterB, smart);
		battle.addMonsterEnhancer(monsterC, baby);
		battle.addMonsterEnhancer(monsterA, baby);
		
		assertEquals(battle.getMonsterEnhancers(monsterA).size(), 3);
		assertTrue(battle.getMonsterEnhancers(monsterA).get(0) == mate);
		assertTrue(battle.getMonsterEnhancers(0).get(1) == big);
		assertTrue(battle.getMonsterEnhancers(monsterA).get(2) == baby);
		assertEquals(battle.getMonsterEnhancers(1).size(), 1);
		assertTrue(battle.getMonsterEnhancers(monsterB).get(0) == smart);
		
		assertNull(battle.getMonsterEnhancers(2));
		assertNull(battle.getMonsterEnhancers(monsterC));	
	}
	
	private void testMonsterItemCards() {
		assertTrue(battle.getMonsterItemCards().isEmpty());
		
		battle.addMonsterItemCard(fpotion);
		battle.addMonsterItemCard(new ItemCard(3, "Test Item", 400, 4));
		
		assertEquals(battle.getMonsterItemCards().size(), 2);
		assertTrue(battle.getMonsterItemCards().get(0) == fpotion);
		assertEquals(battle.getMonsterItemCards().get(1).getName(), "Test Item");
	}
	
	private void testPlayerItemCards() {
		try { battle.addPlayerItemCard(new ItemCard(4, "ItemA", 1)); } catch (EndGameException ex) { fail("Is Not Game End"); }
		try { battle.addPlayerItemCard(new ItemCard(5, "ItemB", 1)); } catch (EndGameException ex) { fail("Is Not Game End"); }
		try { battle.addPlayerItemCard(new ItemCard(6, "ItemC", 1)); } catch (EndGameException ex) { fail("Is Not Game End"); }
		
		assertEquals(battle.getPlayerItemCards().size(), 3);
		assertEquals(battle.getPlayerItemCards().get(0).getName(), "ItemA");
		assertEquals(battle.getPlayerItemCards().get(1).getName(), "ItemB");
		assertEquals(battle.getPlayerItemCards().get(2).getName(), "ItemC");
		
		assertEquals(battle.getMonsterCount(), 2);
		battle.addMonster(nose);
		battle.addMonsterEnhancer(nose, mate);
		
		assertEquals(battle.getMonsterCount(), 3);
		assertEquals(mainPlayer.getLevel(), 1);
		assertEquals(helpingPlayer.getLevel(), 1);
		helpingPlayer.addRaceCard(new RaceCard(4, "Elf", Race.ELF));
		try { battle.addPlayerItemCard(halitosis); } catch (EndGameException ex) { fail("Is Not Game End"); }
		assertEquals(battle.getPlayerItemCards().size(), 3);
		assertEquals(battle.getMonsterCount(), 2);
		assertEquals(mainPlayer.getLevel(), 3);
		assertEquals(helpingPlayer.getLevel(), 3);
	}	
	
	private void testEndBattle() {
		mainPlayer.addLastingCurse(change);
		helpingPlayer.addLastingCurse(malign);		
	
		int doorCardCount = GM.getDoorDeck().getDiscardPile().size();
		int treasureCardCount = GM.getTreasureDeck().getDiscardPile().size();
		int monsterCardCount = battle.getMonsterCount();
		battle.endBattle();
		// monster cards, monster enhancers, curse cards
		assertEquals(GM.getDoorDeck().getDiscardPile().size(), doorCardCount + monsterCardCount + 4 + 2);
		// player items, monster items
		assertEquals(GM.getTreasureDeck().getDiscardPile().size(), treasureCardCount + 3 + 2);
	}
	
	private void testTurning() {
		ClassCard cleric = new ClassCard(5, "Cleric", Class.CLERIC);
		
		int playersLevel = battle.getPlayersLevel();
		assertFalse(battle.canUseTurning(mainPlayer));
		mainPlayer.addClassCard(cleric);
		assertTrue(battle.canUseTurning(mainPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(mainPlayer), 3);
		battle.addTurning(mainPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 3);
		assertTrue(battle.canUseTurning(mainPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(mainPlayer), 2);
		battle.addTurning(mainPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 6);
		assertTrue(battle.canUseTurning(mainPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(mainPlayer), 1);
		battle.addTurning(mainPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 9);
		assertFalse(battle.canUseTurning(mainPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(mainPlayer), 0);
		
		assertFalse(battle.canUseTurning(helpingPlayer));
		helpingPlayer.addClassCard(cleric);
		assertTrue(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 3);
		battle.addTurning(helpingPlayer);
		assertTrue(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 2);
		battle.addTurning(helpingPlayer);
		assertTrue(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 1);
		battle.addTurning(helpingPlayer);
		assertFalse(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 0);
		assertEquals(battle.getPlayersLevel(), playersLevel + 18);
		
		assertFalse(battle.canUseTurning(otherPlayer));
		otherPlayer.addClassCard(cleric);
		assertFalse(battle.canUseTurning(otherPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(otherPlayer), 0);
		battle.addTurning(otherPlayer);
		assertEquals(battle.getNumberOfTurningBonusesLeft(otherPlayer), 0);
		assertEquals(battle.getPlayersLevel(), playersLevel + 18);
		
		battle.removeHelper();
		assertEquals(battle.getPlayersLevel(), playersLevel + 9 - helpingPlayer.getEquipmentBonus() - helpingPlayer.getLevel());
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 0);
		assertFalse(battle.canUseTurning(helpingPlayer));
		battle.addTurning(helpingPlayer);
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 0);
		assertEquals(battle.getPlayersLevel(), playersLevel + 9 - helpingPlayer.getEquipmentBonus() - helpingPlayer.getLevel());
		
		battle.addHelper(helpingPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 9);
		assertTrue(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 3);
		battle.addTurning(helpingPlayer);
		assertTrue(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getNumberOfTurningBonusesLeft(helpingPlayer), 2);
		assertEquals(battle.getPlayersLevel(), playersLevel + 12);
		battle.befriendMonster(monsterB);
		assertFalse(battle.canUseTurning(mainPlayer));
		assertFalse(battle.canUseTurning(helpingPlayer));
		assertEquals(battle.getPlayersLevel(), playersLevel);
		
		battle.addMonster(monsterB);
		while (battle.canUseTurning(mainPlayer))
			battle.addTurning(mainPlayer);
	}
	
	private void testCharm() {
		ClassCard wizard = new ClassCard(5, "Wizard", Class.WIZARD);
		
		assertFalse(battle.canCastCharm(mainPlayer));
		mainPlayer.getClassCards().clear();
		mainPlayer.addClassCard(wizard);
		assertFalse(battle.canCastCharm(mainPlayer));
		mainPlayer.getHandCards().add(new ItemCard(6, "item1", 100));
		assertTrue(battle.canCastCharm(mainPlayer));
		
		assertFalse(battle.canCastCharm(helpingPlayer));
		helpingPlayer.getClassCards().clear();
		helpingPlayer.addClassCard(wizard);
		assertFalse(battle.canCastCharm(helpingPlayer));
		helpingPlayer.getHandCards().add(new ItemCard(7, "item2", 200));
		assertTrue(battle.canCastCharm(helpingPlayer));
		
		assertFalse(battle.canCastCharm(otherPlayer));
		otherPlayer.getClassCards().clear();
		otherPlayer.addClassCard(wizard);
		assertFalse(battle.canCastCharm(otherPlayer));
		otherPlayer.getHandCards().add(new ItemCard(6, "item3", 300));
		assertFalse(battle.canCastCharm(otherPlayer));
		
		mainPlayer.getHandCards().removeLast();
		helpingPlayer.getHandCards().removeLast();
		otherPlayer.getHandCards().removeLast();
	}
	
	private void testBeserking() {
		int playersLevel = battle.getPlayersLevel();
		ClassCard warrior = new ClassCard(5, "Warrior", Class.WARRIOR);
		
		assertFalse(battle.canUseBerserking(mainPlayer));
		mainPlayer.getClassCards().clear();
		mainPlayer.addClassCard(warrior);
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(mainPlayer), 3);
		assertTrue(battle.canUseBerserking(mainPlayer));
		battle.addBerserking(mainPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 1);
		assertTrue(battle.canUseBerserking(mainPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(mainPlayer), 2);
		battle.addBerserking(mainPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 2);
		assertTrue(battle.canUseBerserking(mainPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(mainPlayer), 1);
		battle.addBerserking(mainPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 3);
		assertFalse(battle.canUseBerserking(mainPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(mainPlayer), 0);
		
		assertFalse(battle.canUseBerserking(helpingPlayer));
		helpingPlayer.getClassCards().clear();
		helpingPlayer.addClassCard(warrior);
		assertTrue(battle.canUseBerserking(helpingPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 3);
		battle.addBerserking(helpingPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 4);
		assertTrue(battle.canUseBerserking(helpingPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 2);
		battle.addBerserking(helpingPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 5);
		assertTrue(battle.canUseBerserking(helpingPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 1);
		battle.addBerserking(helpingPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 6);
		assertFalse(battle.canUseBerserking(helpingPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 0);
		
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(otherPlayer), 0);
		assertFalse(battle.canUseBerserking(otherPlayer));
		otherPlayer.getClassCards().clear();
		otherPlayer.addClassCard(warrior);
		assertFalse(battle.canUseBerserking(otherPlayer));
		battle.addBerserking(otherPlayer);
		assertFalse(battle.canUseBerserking(otherPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(otherPlayer), 0);
		assertEquals(battle.getPlayersLevel(), playersLevel + 6);
		
		battle.removeHelper();
		assertEquals(battle.getPlayersLevel(), playersLevel + 3 - helpingPlayer.getEquipmentBonus() - helpingPlayer.getLevel());
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 0);
		assertFalse(battle.canUseBerserking(helpingPlayer));
		battle.addBerserking(helpingPlayer);
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 0);
		assertEquals(battle.getPlayersLevel(), playersLevel + 3 - helpingPlayer.getEquipmentBonus() - helpingPlayer.getLevel());
		
		battle.addHelper(helpingPlayer);
		assertEquals(battle.getPlayersLevel(), playersLevel + 3);
		assertTrue(battle.canUseBerserking(helpingPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 3);
		battle.addBerserking(helpingPlayer);
		assertTrue(battle.canUseBerserking(helpingPlayer));
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(helpingPlayer), 2);
		assertEquals(battle.getPlayersLevel(), playersLevel + 4);
	}
	
	private void testBackstab() {
		ClassCard thief = new ClassCard(5, "Theif", Class.THIEF);
		
		assertFalse(battle.canBackstab(mainPlayer, mainPlayer));
		assertFalse(battle.canBackstab(mainPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(mainPlayer, otherPlayer));
		mainPlayer.getClassCards().clear();
		mainPlayer.addClassCard(thief);
		assertFalse(battle.canBackstab(mainPlayer, mainPlayer));
		assertTrue(battle.canBackstab(mainPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(mainPlayer, otherPlayer));
		battle.backstab(mainPlayer, helpingPlayer);
		assertFalse(battle.canBackstab(mainPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(mainPlayer, otherPlayer));
		battle.backstab(mainPlayer, otherPlayer);
		assertFalse(battle.canBackstab(mainPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(mainPlayer, otherPlayer));
		
		assertFalse(battle.canBackstab(helpingPlayer, mainPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, otherPlayer));
		helpingPlayer.getClassCards().clear();
		helpingPlayer.addClassCard(thief);
		assertTrue(battle.canBackstab(helpingPlayer, mainPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, otherPlayer));
		battle.backstab(helpingPlayer, mainPlayer);
		assertFalse(battle.canBackstab(helpingPlayer, mainPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, otherPlayer));
		battle.backstab(helpingPlayer, otherPlayer);
		assertFalse(battle.canBackstab(helpingPlayer, mainPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(helpingPlayer, otherPlayer));
		
		assertFalse(battle.canBackstab(otherPlayer, mainPlayer));
		assertFalse(battle.canBackstab(otherPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(otherPlayer, otherPlayer));
		otherPlayer.getClassCards().clear();
		otherPlayer.addClassCard(thief);
		assertTrue(battle.canBackstab(otherPlayer, mainPlayer));
		assertTrue(battle.canBackstab(otherPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(otherPlayer, otherPlayer));
		battle.backstab(otherPlayer, mainPlayer);
		assertFalse(battle.canBackstab(otherPlayer, mainPlayer));
		assertTrue(battle.canBackstab(otherPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(otherPlayer, otherPlayer));
		battle.backstab(otherPlayer, helpingPlayer);
		assertFalse(battle.canBackstab(otherPlayer, mainPlayer));
		assertFalse(battle.canBackstab(otherPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(otherPlayer, otherPlayer));
		battle.backstab(otherPlayer, otherPlayer);
		assertFalse(battle.canBackstab(otherPlayer, mainPlayer));
		assertFalse(battle.canBackstab(otherPlayer, helpingPlayer));
		assertFalse(battle.canBackstab(otherPlayer, otherPlayer));
	}
	
	private void testReplacePlayer() {
		ClassCard cleric = new ClassCard(5, "Cleric", Class.CLERIC);
		
		mainPlayer.getClassCards().clear();
		mainPlayer.addClassCard(cleric);
		assertFalse(battle.canUseTurning(mainPlayer));
		
		mainPlayer.getClassCards().clear();
		mainPlayer.addClassCard(new ClassCard(6, "Warrior", Class.WARRIOR));
		assertFalse(battle.canUseBerserking(mainPlayer));
		
		assertFalse(battle.canBackstab(otherPlayer, mainPlayer));
		assertFalse(battle.canBackstab(otherPlayer, helpingPlayer));
		
		int playersLevel = battle.getPlayersLevel();
		assertTrue(battle.activePlayer == mainPlayer);
		battle.replaceActivePlayer(mainPlayer);
		assertTrue(battle.activePlayer == mainPlayer);
		// turning - berzerking + 2 backstabs
		assertEquals(battle.getPlayersLevel(), playersLevel - 9 - 3 + 4);
		
		assertTrue(battle.canBackstab(otherPlayer, mainPlayer));
		
		assertTrue(battle.canUseBerserking(mainPlayer));
		
		mainPlayer.getClassCards().clear();
		mainPlayer.addClassCard(cleric);
		assertTrue(battle.canUseTurning(mainPlayer));
		
		battle.replaceActivePlayer(otherPlayer);
		assertTrue(battle.activePlayer == otherPlayer);
		
		assertFalse(battle.canBackstab(otherPlayer, mainPlayer));
		assertTrue(battle.canBackstab(helpingPlayer, otherPlayer));
	}	
			
	private void testPlayersLevel() {
		helpingPlayer.addClassCard(new ClassCard(5, "Warrior", Class.WARRIOR));
		while (battle.canUseBerserking(helpingPlayer))
			battle.addBerserking(helpingPlayer);
		helpingPlayer.addClassCard(new ClassCard(6, "Cleric", Class.CLERIC));
		while (battle.canUseTurning(helpingPlayer))
			battle.addTurning(helpingPlayer);
		
		mainPlayer.addUnequippedItem(farmor);
		mainPlayer.equip(farmor);
		battle.replaceActivePlayer(mainPlayer);
		mainPlayer.addLastingCurse(change);
		mainPlayer.addRaceCard(new RaceCard(7, "Elf", Race.ELF));
		battle.addMonster(squid);
		try { battle.addPlayerItemCard(water); } catch (EndGameException ex) { fail("Is Not Game End"); }
				
		// distraction curse, squid(2), level(2), equipment, water(2), berserking, turning, backstab 
		int playersLevel = -5 - 4 - 4 + 3 + 3 + 2 + 2 + 2 + 3 + 9 - 4;
		assertEquals(battle.getPlayersLevel(), playersLevel);
		
		mainPlayer.removeLastingCurses();
		helpingPlayer.addLastingCurse(change);
		battle.replaceMonster(squid, fiends);
		// distraction curse, level(2), berserking, turning, backstab 
		playersLevel = -5 + 3 + 3 + 3 + 9 - 4;
		assertEquals(battle.getPlayersLevel(), playersLevel);
		
		helpingPlayer.removeLastingCurses();
		battle.replaceMonster(fiends, sales);
		battle.replaceMonster(monsterB, monsterC);
		// equipment, water(2),  berserking, backstab 
		playersLevel = 2 + 2 + 2 + 3 - 4;
		assertEquals(battle.getPlayersLevel(), playersLevel);
		
		battle.replaceMonster(sales, monsterB);
		battle.removeHelper();
		try { battle.addPlayerItemCard(dopple); } catch (EndGameException ex) { fail("Is Not Game End"); }
		// level, equipment, water, dopple
		playersLevel = 3 + 2 + 2 + 5;
		assertEquals(battle.getPlayersLevel(), playersLevel);
	}
	
	private void testMonstersLevel() {
		battle.replaceMonster(monsterA, sales);
		battle.replaceMonster(sales, monsterA);
		battle.addMonsterEnhancer(monsterA, mate);
		battle.addMonsterEnhancer(monsterA, big);
		battle.addMonsterEnhancer(monsterA, baby);
		battle.addMonsterEnhancer(monsterB, baby);
		battle.addMonsterEnhancer(monsterB, baby);
		battle.addMonsterEnhancer(monsterC, smart);
		battle.addMonsterEnhancer(monsterC, ancient);
		battle.addMonsterEnhancer(monsterC, enraged);
		assertTrue(battle.hasMonster(monsterA));
		assertTrue(battle.hasMonster(monsterB));
		assertTrue(battle.hasMonster(monsterC));
		assertEquals(battle.getMonsterItemCards().size(), 2);
		assertTrue(battle.getMonsterItemCards().get(0) == fpotion);
		assertEquals(battle.getMonsterItemCards().get(1).getName(), "Test Item");
		
		// base levels + enhancers(3), items
		int monstersLevel = 20 + 1 + 25 + 7;
		assertEquals(battle.getMonstersLevel(), monstersLevel);
	}
	
	private void testDefeatMonster() {
		try {		
			// A = 3T + 1T + 4T, 2L + 2L
			// B = 2T - 2T, 1L
			// C = 1T + 4T, 1L
			battle.getMonsterItemCards().clear();
			battle.leaveTreasuresBehind();
			assertEquals(battle.getTreasureCount(), 0);
			battle.addHelper(helpingPlayer);
			battle.defeatMonsters();
			assertEquals(battle.getTreasureCount(), 14);
			assertEquals(mainPlayer.getLevel(), 9);
			assertEquals(helpingPlayer.getLevel(), 7);
			
			battle.befriendMonster(monsterB);
			battle.befriendMonster(monsterC);
			battle.replaceMonster(monsterA, nothing);
			mainPlayer.goDownLevels(4);
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 6);
			assertEquals(helpingPlayer.getLevel(), 8);
			
			battle.removeHelper();
			mainPlayer.getClassCards().clear();
			mainPlayer.addClassCard(new ClassCard(5, "Warrior", Class.WARRIOR));
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 8);
			
			mainPlayer.getClassCards().clear();
			mainPlayer.goDownLevels(2);
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 7);
			
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 9);
			
			battle.leaveTreasuresBehind();
			battle.addHelper(helpingPlayer);
			mainPlayer.goDownLevels(6);
			helpingPlayer.goDownLevels(9);
			battle.replaceMonster(nothing, plant);
			battle.defeatMonsters();
			assertEquals(battle.getTreasureCount(), 2);
			assertEquals(mainPlayer.getLevel(), 4);
			assertEquals(helpingPlayer.getLevel(), 2);
			
			mainPlayer.getEquippedItems().clear();
			mainPlayer.getUnequippedItems().clear();
			battle.replaceMonster(plant, chicken);
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 5);
			assertEquals(helpingPlayer.getLevel(), 3);
			
			mainPlayer.getClassCards().clear();
			mainPlayer.getClassCards().add(new ClassCard(6, "Wizard", Class.WIZARD));
			mainPlayer.addUnequippedItem(napalm);
			mainPlayer.equip(napalm);
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 7);
			assertEquals(helpingPlayer.getLevel(), 4);
			
			mainPlayer.getEquippedItems().clear();
			mainPlayer.getUnequippedItems().clear();
			helpingPlayer.addUnequippedItem(flame);
			helpingPlayer.equip(flame);
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 9);
			assertEquals(helpingPlayer.getLevel(), 5);
			
			helpingPlayer.getEquippedItems().clear();
			helpingPlayer.getUnequippedItems().clear();
			mainPlayer.goDownLevels(6);
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 4);
			assertEquals(helpingPlayer.getLevel(), 6);
			
			try { battle.addPlayerItemCard(fpotion); } catch (EndGameException ex) { fail("Is Not Game End"); }
			battle.defeatMonsters();
			assertEquals(mainPlayer.getLevel(), 6);
			assertEquals(helpingPlayer.getLevel(), 7);
		}
		catch (EndGameException ex) { fail("Is Not Game End"); }
	}
	
	private void testDiscardMonster() {
		battle.leaveTreasuresBehind();
		battle.addMonster(monsterA);
		battle.discardMonster(monsterA, true);
		assertFalse(battle.hasMonster(monsterA));
		assertEquals(battle.getTreasureCount(), 3);		
		
		battle.addMonster(monsterA);
		battle.addMonsterEnhancer(monsterA, mate);
		battle.addMonsterEnhancer(monsterA, big);
		battle.addMonsterEnhancer(monsterA, ancient);
		battle.addMonsterEnhancer(monsterA, enraged);
		battle.addMonsterEnhancer(monsterA, smart);
		battle.addMonsterEnhancer(monsterA, baby);
		assertTrue(battle.getMonsterEnhancers(monsterA).get(0) == mate);
		
		battle.leaveTreasuresBehind();
		battle.discardMonster(monsterA, false);
		assertTrue(battle.hasMonster(monsterA));
		assertFalse(battle.getMonsterEnhancers(monsterA).get(0) == mate);
		assertEquals(battle.getTreasureCount(), 8);
		
		battle.discardMonster(monsterA, false);
		assertFalse(battle.hasMonster(monsterA));
		assertEquals(battle.getTreasureCount(), 16);
		
		battle.addMonster(monsterA);
		battle.addMonsterEnhancer(monsterA, mate);
		battle.addMonsterEnhancer(monsterA, big);
		battle.addMonsterEnhancer(monsterA, ancient);
		battle.addMonsterEnhancer(monsterA, enraged);
		battle.addMonsterEnhancer(monsterA, smart);
		battle.addMonsterEnhancer(monsterA, baby);
		battle.discardMonster(monsterA, true);
		assertFalse(battle.hasMonster(monsterA));
		assertEquals(battle.getTreasureCount(), 32);
		
		battle.addMonster(monsterA);
		battle.addMonsterEnhancer(monsterA, big);
		battle.addMonsterEnhancer(monsterA, ancient);
		battle.addMonsterEnhancer(monsterA, enraged);
		battle.addMonsterEnhancer(monsterA, smart);
		battle.addMonsterEnhancer(monsterA, baby);
		battle.discardMonster(monsterA, true);
		assertFalse(battle.hasMonster(monsterA));
		assertEquals(battle.getTreasureCount(), 40);		
		
		assertEquals(mainPlayer.getLevel(), 6);
		assertEquals(helpingPlayer.getLevel(), 7);
	}
	
	private void testBefriendMonster() {
		battle.replaceMonster(chicken, monsterA);
		battle.addMonster(monsterB);
		battle.addMonster(monsterC);
		
		battle.addMonsterEnhancer(monsterA, mate);
		battle.addMonsterEnhancer(monsterB, big);
		battle.addMonsterEnhancer(monsterB, smart);
		battle.addMonsterEnhancer(monsterA, baby);
		
		int doorDiscard = GM.getDoorDeck().getDiscardPile().size();
		
		battle.befriendMonster(monsterA);
		assertEquals(battle.getMonsterCount(), 2);
		assertFalse(battle.hasMonster(monsterA));
		assertTrue(battle.hasMonster(monsterB));
		assertTrue(battle.hasMonster(monsterC));
		
		battle.befriendMonster(monsterB);
		assertEquals(battle.getMonsterCount(), 1);
		assertFalse(battle.hasMonster(monsterB));
		assertTrue(battle.hasMonster(monsterC));
		
		battle.befriendMonster(monsterC);
		assertEquals(battle.getMonsterCount(), 0);
		assertFalse(battle.hasMonster(monsterC));		
		
		assertEquals(GM.getDoorDeck().getDiscardPile().size(), doorDiscard + 3 + 4);
	}
	
	private void testUsedFire() {
		battle.replaceActivePlayer(mainPlayer);
		battle.getPlayerItemCards().clear();
		assertTrue(battle.helper == helpingPlayer);
		assertFalse(battle.usedFire());
		
		try { battle.addPlayerItemCard(fpotion); } catch (EndGameException ex) { fail("Is Not Game End"); }
		assertTrue(battle.usedFire());
		battle.getPlayerItemCards().clear();
		assertFalse(battle.usedFire());
		
		mainPlayer.addUnequippedItem(farmor);
		assertTrue(mainPlayer.equip(farmor).equals(""));
		assertTrue(battle.usedFire());
		mainPlayer.unequip(farmor);
		assertFalse(battle.usedFire());
		mainPlayer.addUnequippedItem(napalm);
		mainPlayer.equip(napalm);
		assertTrue(battle.usedFire());
		mainPlayer.unequip(napalm);
		assertFalse(battle.usedFire());
		mainPlayer.addUnequippedItem(flame);
		mainPlayer.equip(flame);
		assertTrue(battle.usedFire());
		mainPlayer.unequip(flame);
		assertFalse(battle.usedFire());
		
		helpingPlayer.addUnequippedItem(farmor);
		helpingPlayer.equip(farmor);
		assertTrue(battle.usedFire());
		helpingPlayer.addUnequippedItem(napalm);
		helpingPlayer.equip(napalm);
		assertTrue(battle.usedFire());
		helpingPlayer.unequip(napalm);
		assertTrue(battle.usedFire());
		helpingPlayer.addUnequippedItem(flame);
		helpingPlayer.equip(flame);
		assertTrue(battle.usedFire());
		helpingPlayer.unequip(flame);
		assertTrue(battle.usedFire());
		helpingPlayer.unequip(farmor);
		assertFalse(battle.usedFire());
	}
	
	private void testWinTreasureCount() {
		battle.addMonster(plant);
		
		mainPlayer.getRaceCards().clear();
		battle.removeHelper();		
		assertEquals(battle.getWinTreasureCount(plant), 1);
		battle.addMonsterEnhancer(plant, baby);
		assertEquals(battle.getWinTreasureCount(plant), 1);
		assertTrue(helpingPlayer.isElf());
		battle.addHelper(helpingPlayer);
		assertEquals(battle.getWinTreasureCount(plant), 1);
		battle.addMonsterEnhancer(plant, mate);
		assertEquals(battle.getWinTreasureCount(plant), 2);
		battle.getMonsterEnhancers(plant).remove(baby);
		assertEquals(battle.getWinTreasureCount(plant), 4);
		battle.getMonsterEnhancers(plant).remove(mate);
		assertEquals(battle.getWinTreasureCount(plant), 2);
		
		battle.addMonster(monsterA);
		assertEquals(battle.getWinTreasureCount(monsterA), 3);
		battle.addMonsterEnhancer(monsterA, mate);
		assertEquals(battle.getWinTreasureCount(monsterA), 6);
		battle.addMonsterEnhancer(monsterA, baby);
		assertEquals(battle.getWinTreasureCount(monsterA), 4);
		battle.addMonsterEnhancer(monsterA, ancient);
		assertEquals(battle.getWinTreasureCount(monsterA), 8);
		battle.addMonsterEnhancer(monsterA, enraged);
		assertEquals(battle.getWinTreasureCount(monsterA), 10);
		battle.addMonsterEnhancer(monsterA, big);
		assertEquals(battle.getWinTreasureCount(monsterA), 14);
		battle.addMonsterEnhancer(monsterA, smart);
		assertEquals(battle.getWinTreasureCount(monsterA), 16);
		battle.getMonsterEnhancers(monsterA).remove(mate);
		assertEquals(battle.getWinTreasureCount(monsterA), 8);
		battle.getMonsterEnhancers(monsterA).remove(baby);
		assertEquals(battle.getWinTreasureCount(monsterA), 9);
		battle.getMonsterEnhancers(monsterA).remove(ancient);
		assertEquals(battle.getWinTreasureCount(monsterA), 7);
	}
	
	private void testWinLevelCount() {
		battle.addMonster(nothing);
		while(battle.getMonsterCount() > 1)
			battle.befriendMonster(battle.getMonster(0));
		
		mainPlayer.getClassCards().clear();
		
		battle.removeHelper();
		mainPlayer.goDownLevels(10);
		assertEquals(battle.getWinLevelCount(nothing), 1);
		mainPlayer.goUpLevels(10, false);
		assertEquals(battle.getWinLevelCount(nothing), 2);
		battle.addHelper(helpingPlayer);
		assertEquals(battle.getWinLevelCount(nothing), 1);
		battle.removeHelper();
		mainPlayer.goDownLevels(3);
		assertEquals(battle.getWinLevelCount(nothing), 1);
		mainPlayer.addClassCard(new ClassCard(5, "Warrior", Class.WARRIOR));
		assertEquals(battle.getWinLevelCount(nothing), 2);
		battle.addHelper(helpingPlayer);
		assertEquals(battle.getWinLevelCount(nothing), 1);
		
		battle.addMonster(chicken);
		assertFalse(battle.usedFire());
		assertEquals(battle.getWinLevelCount(chicken), 1);
		try { battle.addPlayerItemCard(fpotion); } catch (EndGameException ex) { fail("Is Not Game End"); }
		assertTrue(battle.usedFire());
		assertEquals(battle.getWinLevelCount(chicken), 2);
		battle.addMonsterEnhancer(chicken, mate);
		assertEquals(battle.getWinLevelCount(chicken), 4);
		battle.getPlayerItemCards().clear();
		assertEquals(battle.getWinLevelCount(chicken), 2);
		battle.getMonsterEnhancers(chicken).clear();
		assertEquals(battle.getWinLevelCount(chicken), 1);
		
		battle.addMonster(monsterA);
		assertEquals(battle.getWinLevelCount(monsterA), 2);
		battle.addMonsterEnhancer(monsterA, mate);
		assertEquals(battle.getWinLevelCount(monsterA), 4);
	}
	
	private void testCheckForChanges() {
		battle.removeHelper();
		battle.addMonster(amazon);
		
		assertTrue(mainPlayer.isMale());
		battle.checkForChanges();
		assertTrue(battle.hasMonster(amazon));
		
		assertTrue(helpingPlayer.isFemale());
		battle.addHelper(helpingPlayer);
		int treasureDeckSize = GM.getTreasureDeck().drawPile.size();
		battle.checkForChanges();
		assertFalse(battle.hasMonster(amazon));
		assertEquals(GM.getTreasureDeck().drawPile.size(), treasureDeckSize - 1);
		
		battle.removeHelper();
		battle.addMonster(lawyer);
		battle.checkForChanges();
		assertTrue(battle.hasMonster(lawyer));
		
		battle.addHelper(helpingPlayer);
		helpingPlayer.addClassCard(new ClassCard(5, "Thief", Class.THIEF));
		helpingPlayer.getHandCards().clear();
		helpingPlayer.getCarriedItems().clear();
		helpingPlayer.getEquippedItems().clear();
		helpingPlayer.getUnequippedItems().clear();
		helpingPlayer.setHirelingCard(hireling);
		helpingPlayer.addItem(water);
		GM.getTreasureDeck().removeCard(water);
		GM.getTreasureDeck().removeCard(hireling);
		treasureDeckSize = GM.getTreasureDeck().drawPile.size();
		battle.checkForChanges();
		assertFalse(battle.hasMonster(lawyer));
		assertFalse(helpingPlayer.hasHireling());
		assertFalse(helpingPlayer.getCarriedItems().contains(water));
		assertEquals(GM.getTreasureDeck().drawPile.size(), treasureDeckSize - 2);
		
		battle.removeHelper();
		mainPlayer.addClassCard(new ClassCard(6, "Thief", Class.THIEF));
		battle.addMonster(lawyer);
		
		mainPlayer.addCard(amazon);
		mainPlayer.addCard(new GoUpLevelCard(9, "test"));
		mainPlayer.addCard(new ItemCard(7, "test", 700, 7));
		mainPlayer.addItem(new ItemCard(8, "test2", 790, 7));
		battle.checkForChanges();
		assertFalse(battle.hasMonster(lawyer));
		assertEquals(GM.getTreasureDeck().drawPile.size(), treasureDeckSize - 4);
		
		mainPlayer.changeSex();
		battle.addMonster(amazon);
		battle.checkForChanges();
		assertFalse(battle.hasMonster(amazon));
		assertEquals(GM.getTreasureDeck().drawPile.size(), treasureDeckSize - 5);
	}
}
