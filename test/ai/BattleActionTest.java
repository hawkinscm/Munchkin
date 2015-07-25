
package ai;

import exceptions.EndGameException;
import gui.GUI;
import gui.MockGUI;

import java.util.LinkedList;

import ai.BattleAction.ActionType;

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
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.RaceCard;

public class BattleActionTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private Battle battle;
	private Player easy;
	private Player medium;
	private Player hard;
	
	private MonsterEnhancerCard ancient;
	
	private CurseCard tax;
	private CurseCard loseItem;
	private CurseCard loseCards;
	private CurseCard loseRace;
	private CurseCard loseClass;
	private CurseCard chicken;
	
	public int testAll() {
		initialize();
		
		testConstructor();
		testPerformHandAction();
		testPerformItemAction();
		testDiscardAction();
		testBerserkingAction();
		testTurningAction();
		testBackstabAction();
		testTransferralPotionAction();
		
		return errorCount;
	}
	
	private void initialize() {
		for (Card card : DoorDeckFactory.buildDeck()) {
			if (card.getID() == Card.ME_ANCIENT)
				ancient = (MonsterEnhancerCard)card;
			else if (card.getID() == Card.CU_INCOME_TAX)
				tax = (CurseCard) card;
			else if (card.getID() == Card.CU_LOSE_1_SMALL_ITEM_1)
				loseItem = (CurseCard) card;
			else if (card.getID() == Card.CU_LOSE_TWO_CARDS)
				loseCards = (CurseCard) card;
			else if (card.getID() == Card.CU_LOSE_YOUR_CLASS)
				loseClass = (CurseCard) card;
			else if (card.getID() == Card.CU_LOSE_YOUR_RACE)
				loseRace = (CurseCard) card;
			else if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
				chicken = (CurseCard) card;
		}
		
		GUI mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		GM.newTestGame(mockGUI, players);
		
		GM.moveToBattlePhase();
		battle = new Battle(easy, new MonsterCard(1, "test", 3, 1, 1, false) {
			public void doBadStuff(Player player) {}
		});
	}
	
	private void testConstructor() {
		Card card = new OtherDoorCard(5, "");
		BattleAction action = new BattleAction(ActionType.BACKSTAB, card, 2, 500);
		assertTrue(action.getType() == ActionType.BACKSTAB);
		assertTrue(action.getCard() == card);
		assertEquals(action.getBenefit(), 2);
		assertEquals(action.getCost(), 500);
		assertEquals(action.getWorth(), 2.0 / 500.0);
		action.setBenefit(3);
		assertEquals(action.getBenefit(), 3);
		assertEquals(action.getCost(), 500);
		assertEquals(action.getWorth(), 3.0 / 500.0);
		
		action = new BattleAction(ActionType.TURNING, card, -3, 700, card);
		assertTrue(action.getType() == ActionType.TURNING);
		assertTrue(action.getCard() == card);
		assertEquals(action.getBenefit(), -3);
		assertEquals(action.getCost(), 700);
		assertEquals(action.getWorth(), 3.0 / 700.0);
		action.setBenefit(-10);
		assertEquals(action.getBenefit(), -10);
		assertEquals(action.getCost(), 700);
		assertEquals(action.getWorth(), 10.0 / 700.0);
	}
	
	private void testPerformHandAction() {
		try {
		// test enhancer
		easy.getHandCards().add(ancient);
		hard.getHandCards().add(ancient);
		BattleAction action = new BattleAction(ActionType.HAND_CARD, ancient, 0, 1000, battle.getMonster(0));
		assertFalse(action.perform(battle, easy));
		assertFalse(battle.getMonsterEnhancers(0).contains(ancient));
		
		action = new BattleAction(ActionType.HAND_CARD, ancient, 10, 1000, battle.getMonster(0));
		assertTrue(action.perform(battle, hard));
		assertTrue(battle.getMonsterEnhancers(0).contains(ancient));
		assertTrue(easy.getHandCards().contains(ancient));
		assertFalse(hard.getHandCards().contains(ancient));
		
		// test curse
		medium.addCard(tax);
		medium.addCard(loseItem);
		medium.addCard(loseCards);
		medium.addCard(loseClass);
		medium.addCard(loseRace);
		medium.addCard(chicken);
		
		easy.addItem(new ItemCard(5, "Item1", 100));
		medium.addItem(new ItemCard(6, "Item2", 100));
		action = new BattleAction(ActionType.HAND_CARD, tax, 1, 500, easy);
		assertFalse(action.perform(battle, medium));
		assertEquals(easy.getAllItems().size(), 1);
		assertEquals(medium.getAllItems().size(), 1);
		assertTrue(medium.getHandCards().contains(tax));
		action = new BattleAction(ActionType.HAND_CARD, tax, -1, 500, easy);
		assertTrue(action.perform(battle, medium));
		assertEquals(easy.getAllItems().size(), 0);
		assertEquals(medium.getAllItems().size(), 0);
		assertFalse(medium.getHandCards().contains(tax));
		
		action = new BattleAction(ActionType.HAND_CARD, loseItem, 0, 900, easy);
		assertFalse(action.perform(battle, medium));
		assertTrue(medium.getHandCards().contains(loseItem));
		easy.addItem(new ItemCard(7, "Item1", 100));
		action = new BattleAction(ActionType.HAND_CARD, loseItem, 3, 700, easy);
		assertFalse(action.perform(battle, medium));
		assertEquals(easy.getAllItems().size(), 1);
		assertTrue(medium.getHandCards().contains(loseItem));
		action = new BattleAction(ActionType.HAND_CARD, loseItem, 0, 900, easy);
		assertTrue(action.perform(battle, medium));
		assertEquals(easy.getAllItems().size(), 0);
		assertFalse(medium.getHandCards().contains(loseItem));
		
		action = new BattleAction(ActionType.HAND_CARD, loseCards, 0, 0, easy);
		assertFalse(action.perform(battle, medium));
		assertEquals(easy.getHandCards().size(), 1);
		assertTrue(medium.getHandCards().contains(loseCards));
		easy.addCard(new GoUpLevelCard(8, "level"));
		action = new BattleAction(ActionType.HAND_CARD, loseCards, 27, -50, easy);
		assertFalse(action.perform(battle, medium));
		assertEquals(easy.getHandCards().size(), 2);
		assertTrue(medium.getHandCards().contains(loseCards));
		action = new BattleAction(ActionType.HAND_CARD, loseCards, 0, 0, easy);
		assertTrue(action.perform(battle, medium));
		assertEquals(easy.getHandCards().size(), 0);
		assertFalse(medium.getHandCards().contains(loseCards));
		medium.addCard(loseCards);
		hard.getHandCards().clear();
		hard.addCard(new RaceCard(9, "Elf", Race.ELF));
		hard.addCard(new ClassCard(10, "Warrior", Class.WARRIOR));
		hard.addCard(new OtherDoorCard(11, "Mate"));
		assertEquals(hard.getHandCards().size(), 3);
		action = new BattleAction(ActionType.HAND_CARD, loseCards, 0, 0, hard);
		assertTrue(action.perform(battle, medium));
		assertEquals(hard.getHandCards().size(), 1);
		assertFalse(medium.getHandCards().contains(loseCards));
		
		easy.getClassCards().clear();
		action = new BattleAction(ActionType.HAND_CARD, loseClass, 0, 577, easy);
		assertFalse(action.perform(battle, medium));
		assertTrue(medium.getHandCards().contains(loseClass));
		easy.addClassCard(new ClassCard(12, "Warrior", Class.WARRIOR));
		action = new BattleAction(ActionType.HAND_CARD, loseClass, 3, 423, easy);
		assertFalse(action.perform(battle, medium));
		assertTrue(medium.getHandCards().contains(loseClass));
		assertTrue(easy.isWarrior());
		action = new BattleAction(ActionType.HAND_CARD, loseClass, 0, 577, easy);
		assertTrue(action.perform(battle, medium));
		assertFalse(medium.getHandCards().contains(loseClass));
		assertFalse(easy.isWarrior());
		
		easy.getRaceCards().clear();
		action = new BattleAction(ActionType.HAND_CARD, loseRace, 0, 300, easy);
		assertFalse(action.perform(battle, medium));
		assertTrue(medium.getHandCards().contains(loseRace));
		easy.addRaceCard(new RaceCard(13, "Elf", Race.ELF));
		action = new BattleAction(ActionType.HAND_CARD, loseRace, 1, 300, easy);
		assertFalse(action.perform(battle, medium));
		assertTrue(medium.getHandCards().contains(loseRace));
		assertTrue(easy.isElf());
		action = new BattleAction(ActionType.HAND_CARD, loseRace, 0, 300, easy);
		assertTrue(action.perform(battle, medium));
		assertFalse(medium.getHandCards().contains(loseRace));
		assertFalse(easy.isElf());
		
		assertFalse(easy.hasChickenOnHead());
		action = new BattleAction(ActionType.HAND_CARD, chicken, 0, 300, easy);
		assertFalse(action.perform(battle, medium));
		assertTrue(medium.getHandCards().contains(chicken));
		assertFalse(easy.hasChickenOnHead());
		action = new BattleAction(ActionType.HAND_CARD, chicken, -1, 300, easy);
		assertTrue(action.perform(battle, medium));
		assertFalse(medium.getHandCards().contains(chicken));
		assertTrue(easy.hasChickenOnHead());
		
		action = new BattleAction(ActionType.HAND_CARD, new OtherDoorCard(Card.OD_MATE, "Mate"), 2, 500);
		assertFalse(action.perform(battle, hard));
		
		action = new BattleAction(ActionType.HAND_CARD, battle.getMonster(0), 0, 1000, easy);
		assertFalse(action.perform(battle, hard));
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
	
	private void testPerformItemAction() {
		try {
		ItemCard item1 = new ItemCard(15, "item1", 300, 3);
		ItemCard item2 = new ItemCard(16, "item2", 500, 4);
		easy.addItem(item2);
		hard.addItem(item1);
		hard.addItem(item2);
		
		assertEquals(battle.getPlayerItemCards().size(), 0);
		assertEquals(battle.getMonsterItemCards().size(), 0);
		BattleAction action = new BattleAction(ActionType.ITEM_CARD, item2, 4, 500);
		assertTrue(action.perform(battle, easy));
		assertEquals(battle.getPlayerItemCards().size(), 1);
		assertEquals(battle.getMonsterItemCards().size(), 0);
		assertEquals(easy.getAllItems().size(), 0);
		
		easy.addItem(item2);
		action = new BattleAction(ActionType.ITEM_CARD, item2, -4, 500);
		assertTrue(action.perform(battle, easy));
		assertEquals(battle.getPlayerItemCards().size(), 1);
		assertEquals(battle.getMonsterItemCards().size(), 1);
		assertEquals(easy.getAllItems().size(), 0);
		
		action = new BattleAction(ActionType.ITEM_CARD, item1, -3, 300);
		assertTrue(action.perform(battle, hard));
		assertEquals(battle.getPlayerItemCards().size(), 1);
		assertEquals(battle.getMonsterItemCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 1);
		
		action = new BattleAction(ActionType.ITEM_CARD, item2, 3, 300);
		assertTrue(action.perform(battle, hard));
		assertEquals(battle.getPlayerItemCards().size(), 2);
		assertEquals(battle.getMonsterItemCards().size(), 2);
		assertEquals(hard.getAllItems().size(), 0);
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
	
	private void testDiscardAction() {
		try {
		ClassCard wizard = new ClassCard(5, "Wizard", Class.WIZARD);
		easy.addClassCard(wizard);
		assertTrue(easy.isWizard());
		BattleAction action = new BattleAction(ActionType.DISCARD_CLASS, wizard, 1, 1000);
		assertTrue(action.perform(battle, easy));
		assertFalse(easy.isWizard());
		
		RaceCard halfling = new RaceCard(6, "Halfling", Race.HALFLING);
		easy.addRaceCard(halfling);
		assertTrue(easy.isHalfling());
		action = new BattleAction(ActionType.DISCARD_RACE, halfling, 1, 1000);
		assertTrue(action.perform(battle, easy));
		assertFalse(easy.isHalfling());
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
	
	private void testBerserkingAction() {
		try {
		easy.getHandCards().clear();
		easy.addCard(new ClassCard(5, "Cleric", Class.CLERIC));
		easy.addCard(tax);
		assertEquals(easy.getHandCards().size(), 2);
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(easy), 3);
		BattleAction action = new BattleAction(ActionType.BERSERKING, easy.getHandCards().getFirst(), 1, 1000);
		assertTrue(action.perform(battle, easy));
		assertEquals(easy.getHandCards().size(), 1);
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(easy), 2);
		action = new BattleAction(ActionType.BERSERKING, easy.getHandCards().getFirst(), 1, 500);
		assertTrue(action.perform(battle, easy));
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(battle.getNumberOfBerserkingBonusesLeft(easy), 1);
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
	
	private void testTurningAction() {
		try {
		easy.addCard(ancient);
		easy.addCard(loseItem);
		assertEquals(easy.getHandCards().size(), 2);
		assertEquals(battle.getNumberOfTurningBonusesLeft(easy), 3);
		BattleAction action = new BattleAction(ActionType.TURNING, easy.getHandCards().getFirst(), 1, 1000);
		assertTrue(action.perform(battle, easy));
		assertEquals(easy.getHandCards().size(), 1);
		assertEquals(battle.getNumberOfTurningBonusesLeft(easy), 2);
		action = new BattleAction(ActionType.TURNING, easy.getHandCards().getFirst(), 1, 1000);
		assertTrue(action.perform(battle, easy));
		assertEquals(easy.getHandCards().size(), 0);
		assertEquals(battle.getNumberOfTurningBonusesLeft(easy), 1);
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
	
	private void testBackstabAction() {
		try {
		medium.addClassCard(new ClassCard(5, "Thief1", Class.THIEF));
		hard.addClassCard(new ClassCard(6, "Thief2", Class.THIEF));
		medium.getHandCards().clear();
		hard.getHandCards().clear();
		medium.addCard(loseCards);
		medium.addCard(ancient);
		hard.addCard(new RaceCard(7, "Dwarf", Race.DWARF));
		assertTrue(battle.canBackstab(medium, easy));
		assertTrue(battle.canBackstab(hard, easy));
		assertEquals(medium.getHandCards().size(), 2);
		assertEquals(hard.getHandCards().size(), 1);
		BattleAction action = new BattleAction(ActionType.BACKSTAB, medium.getHandCards().getFirst(), 1, 300, easy);
		assertTrue(action.perform(battle, medium));
		assertFalse(battle.canBackstab(medium, easy));
		assertTrue(battle.canBackstab(hard, easy));
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(hard.getHandCards().size(), 1);
		action = new BattleAction(ActionType.BACKSTAB, hard.getHandCards().getFirst(), 1, 600, easy);
		assertTrue(action.perform(battle, hard));
		assertFalse(battle.canBackstab(medium, easy));
		assertFalse(battle.canBackstab(hard, easy));
		assertEquals(medium.getHandCards().size(), 1);
		assertEquals(hard.getHandCards().size(), 0);
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
	
	private void testTransferralPotionAction() {
		try {
		ItemCard trans = new ItemCard(Card.I_TRANSFERRAL_POTION, "Transferral Potion", 300);
		medium.getCarriedItems().clear();
		medium.addItem(trans);
		BattleAction action = new BattleAction(ActionType.TRANSFERRAL_POTION, trans, 1, 300, hard);
		assertTrue(action.perform(battle, medium));
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == hard);
		hard.getCarriedItems().clear();
		hard.addItem(trans);
		action = new BattleAction(ActionType.TRANSFERRAL_POTION, trans, 1, 300, easy);
		assertTrue(action.perform(battle, hard));
		assertTrue(hard.getCarriedItems().isEmpty());
		assertTrue(battle.activePlayer == easy);
		}
		catch (EndGameException ex) { fail("Should not cause end of game."); }
	}
}
