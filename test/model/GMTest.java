
package model;

import exceptions.EndGameException;
import gui.MockGUI;

import java.util.Iterator;
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
import model.card.RaceCard;
import model.card.EquipmentCard.EquipmentType;

public class GMTest extends UnitTest {

	private Player player1;
	private Player player2;
	private Player player3;
	private Player player4;
	private Player player5;
	private Player player6;
	
	public int testAll() {
		initialize();
		
		testPlayers();
		testEndPlayerTurn();
		testPhases();
		testOpenDoor();
		testCheckForWinners();
		
		return errorCount;
	}
		
	private void initialize() {
		MockGUI mockGUI = new MockGUI(0);
		
		player1 = new Player(mockGUI, "1", true, PlayerType.TEST);
		player2 = new Player(mockGUI, "2", false, PlayerType.TEST);
		player3 = new Player(mockGUI, "3", true, PlayerType.TEST);
		player4 = new Player(mockGUI, "4", false, PlayerType.TEST);
		player5 = new Player(mockGUI, "5", true, PlayerType.TEST);
		player6 = new Player(mockGUI, "6", false, PlayerType.TEST);
		
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(player1);
		players.add(player2);
		players.add(player3);
		players.add(player4);
		players.add(player5);
		players.add(player6);
		GM.newTestGame(mockGUI, players);
		
		assertTrue(GM.getActivePlayer() == player1);
		assertTrue(GM.getDoorDeck() != null);
		assertTrue(GM.getTreasureDeck() != null);
		assertTrue(GM.isOpenDoorPhase());
		assertFalse(GM.isAfterBattle());
		assertFalse(GM.isBattlePhase());
		assertFalse(GM.isCharityPhase());
		assertFalse(GM.isLookForTroublePhase());
		assertFalse(GM.isLootRoomPhase());
		assertFalse(GM.usedDoubleSell());		
	}
	
	private void testPlayers() {
		assertEquals(GM.getPlayers().size(), 6);
		assertTrue(GM.getPlayers().contains(player1));
		assertTrue(GM.getPlayers().contains(player2));
		assertTrue(GM.getPlayers().contains(player3));
		assertTrue(GM.getPlayers().contains(player4));
		assertTrue(GM.getPlayers().contains(player5));
		assertTrue(GM.getPlayers().contains(player6));
		
		assertTrue(GM.getPlayerLeft(player1) == player2);
		assertTrue(GM.getPlayerLeft(player2) == player3);
		assertTrue(GM.getPlayerLeft(player3) == player4);
		assertTrue(GM.getPlayerLeft(player4) == player5);
		assertTrue(GM.getPlayerLeft(player5) == player6);
		assertTrue(GM.getPlayerLeft(player6) == player1);
		
		assertTrue(GM.getPlayerRight(player1) == player6);
		assertTrue(GM.getPlayerRight(player2) == player1);
		assertTrue(GM.getPlayerRight(player3) == player2);
		assertTrue(GM.getPlayerRight(player4) == player3);
		assertTrue(GM.getPlayerRight(player5) == player4);
		assertTrue(GM.getPlayerRight(player6) == player5);
		
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 1);
		assertEquals(GM.getLowestLevelPlayers().size(), 6);
		assertEquals(GM.getHighestLevelPlayers().size(), 6);
		player1.goUpLevels(6, false);
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 7);
		assertEquals(GM.getLowestLevelPlayers().size(), 5);
		assertFalse(GM.getLowestLevelPlayers().contains(player1));
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		assertTrue(GM.getHighestLevelPlayers().contains(player1));
		player2.goUpLevel(false);
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 7);
		assertEquals(GM.getLowestLevelPlayers().size(), 4);
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		player3.goUpLevels(2, false);
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 7);
		assertEquals(GM.getLowestLevelPlayers().size(), 3);
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		player4.goUpLevels(3, false);
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 7);
		assertEquals(GM.getLowestLevelPlayers().size(), 2);
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		player5.goUpLevels(4, false);
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 7);
		assertEquals(GM.getLowestLevelPlayers().size(), 1);
		assertTrue(GM.getLowestLevelPlayers().contains(player6));
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		assertTrue(GM.getHighestLevelPlayers().contains(player1));
		player6.goUpLevels(5, false);
		assertEquals(GM.getLowestLevel(), 2);
		assertEquals(GM.getHighestLevel(), 7);
		assertEquals(GM.getLowestLevelPlayers().size(), 1);
		assertTrue(GM.getLowestLevelPlayers().contains(player2));
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		assertTrue(GM.getHighestLevelPlayers().contains(player1));
		player1.goDownLevels(6);
		assertEquals(GM.getLowestLevel(), 1);
		assertEquals(GM.getHighestLevel(), 6);
		assertEquals(GM.getLowestLevelPlayers().size(), 1);
		assertTrue(GM.getLowestLevelPlayers().contains(player1));
		assertEquals(GM.getHighestLevelPlayers().size(), 1);
		assertTrue(GM.getHighestLevelPlayers().contains(player6));
		
		player1.goUpLevels(5, false); // 6
		player4.goUpLevels(5, false); // 9
		player5.goDownLevels(10); // 1
		assertEquals(player1.getLevel(), player6.getLevel());
		
		Iterator<Player> playerIter = GM.getHighestToLowestLevelPlayers().iterator();
		assertTrue(playerIter.hasNext());
		assertTrue(playerIter.next() == player5);
		assertTrue(playerIter.hasNext());
		assertTrue(playerIter.next() == player2);
		assertTrue(playerIter.hasNext());
		assertTrue(playerIter.next() == player3);
		assertTrue(playerIter.hasNext());
		Player p1 = playerIter.next();
		assertTrue(playerIter.hasNext());
		Player p2 = playerIter.next();
		assertTrue((p1 == player1 && p2 == player6) || (p1 == player6 && p2 == player1));
		assertTrue(playerIter.hasNext());
		assertTrue(playerIter.next() == player4);
		assertFalse(playerIter.hasNext());
		player1.goDownLevels(5);
		player4.goDownLevels(5);
		player5.goUpLevels(4, false);
	}			
	
	private void testEndPlayerTurn() {
		assertTrue(GM.getActivePlayer() == player1);
		GM.moveToOtherPhase();
		GM.endPlayerTurn();
		assertTrue(GM.isOpenDoorPhase());
		assertTrue(GM.getActivePlayer() == player2);
		GM.moveToBattlePhase();
		GM.moveNextPhase();
		assertTrue(GM.isCharityPhase());
		assertTrue(GM.isAfterBattle());
		GM.endPlayerTurn();
		assertTrue(GM.isOpenDoorPhase());
		assertFalse(GM.isAfterBattle());
		assertTrue(GM.getActivePlayer() == player3);
		GM.moveToBattlePhase();
		GM.setCanLootRoom();
		GM.moveNextPhase();
		assertTrue(GM.isLootRoomPhase());
		assertTrue(GM.isAfterBattle());
		GM.endPlayerTurn();
		assertTrue(GM.getActivePlayer() == player4);
		GM.endPlayerTurn();
		assertTrue(GM.getActivePlayer() == player5);
		GM.endPlayerTurn();
		assertTrue(GM.getActivePlayer() == player6);
		GM.endPlayerTurn();
		assertTrue(GM.getActivePlayer() == player1);
		
		assertTrue(player1.getHandCards().isEmpty());
		player1.addUnequippedItem(new EquipmentCard(1, "big", 0, 0, EquipmentType.OTHER, true, false, false));
		ItemCard item1 = new ItemCard(2, "item1", 100, 0);
		EquipmentCard equipment2 = new EquipmentCard(3, "equipment2", 200, 0, EquipmentType.OTHER, true, false, false);
		ItemCard item3 = new ItemCard(4, "item3", 300, 0);
		ItemCard item4 = new ItemCard(5, "item4", 400, 0);
		EquipmentCard equipment5 = new EquipmentCard(6, "equipment5", 500, 0, EquipmentType.OTHER, false, false, false);
		EquipmentCard equipment6 = new EquipmentCard(7, "equipment6", 600, 0, EquipmentType.OTHER, false, false, false);
		GoUpLevelCard levelCard = new GoUpLevelCard(8, "level card");		
		player1.addCard(item1);
		player1.addCard(equipment2);
		player1.addCard(item3);
		player1.addCard(item4);
		player1.addCard(equipment5);
		player1.goUpLevels(6, false);
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 5);
		GM.setActivePlayer(player1);
		player1.addCard(equipment6);
		player1.addRaceCard(new RaceCard(9, "Dwarf", Race.DWARF));
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 6);
		GM.setActivePlayer(player1);
		player1.addCard(levelCard);
		GM.endPlayerTurn();
		assertTrue(GM.getActivePlayer() == player2);
		assertEquals(player1.getHandCards().size(), 6);
		assertFalse(player1.getHandCards().contains(item1));
		assertTrue(player1.getCarriedItems().contains(item1));
		player1.getRaceCards().clear();
		GM.setActivePlayer(player1);
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 5);
		assertFalse(player1.getHandCards().contains(equipment2));
		assertEquals(player2.getHandCards().size(), 1);
		assertTrue(player2.getHandCards().remove(equipment2));
		player2.goUpLevel(false);
		player1.addCard(equipment2);
		GM.setActivePlayer(player1);
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 5);
		assertFalse(player1.getHandCards().contains(equipment2));
		assertTrue(player2.getHandCards().size() == 1 || player3.getHandCards().size() == 1);
		assertTrue(player2.getHandCards().contains(equipment2) || player3.getHandCards().contains(equipment2));
		player2.getHandCards().clear();
		GM.setActivePlayer(player1);
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 5);
		player1.addCard(equipment2);
		player1.goDownLevels(10);
		GM.setActivePlayer(player1);
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 5);
		assertFalse(player1.getHandCards().contains(equipment2));
		assertTrue(GM.getTreasureDeck().discardPile.peek() == equipment2);
		player1.addCard(equipment2);
		player1.goUpLevel(false);
		player2.goDownLevel();
		assertEquals(player1.getLevel(), player2.getLevel());
		GM.setActivePlayer(player1);
		GM.getTreasureDeck().discardPile.push(item4);
		GM.endPlayerTurn();
		assertEquals(player1.getHandCards().size(), 5);
		assertFalse(player1.getHandCards().contains(equipment2));
		assertFalse(player2.getHandCards().contains(equipment2));
		assertTrue(GM.getTreasureDeck().discardPile.peek() == equipment2);
		player1.getHandCards().clear();
		player3.getHandCards().clear();
		player1.goDownLevel();
		
		GM.getDoorDeck().drawPile.clear();
		GM.getTreasureDeck().drawPile.clear();
		OtherDoorCard otherCard = new OtherDoorCard(10, "other card");
		GM.getDoorDeck().drawPile.push(otherCard);
		GM.getDoorDeck().drawPile.push(new RaceCard(11, "Elf", Race.ELF));
		GM.getDoorDeck().drawPile.push(new ClassCard(12, "Warrior", Class.WARRIOR));
		GM.getTreasureDeck().drawPile.push(item3);
		GM.getTreasureDeck().drawPile.push(equipment5);
		GM.getTreasureDeck().drawPile.push(levelCard);
		GM.setActivePlayer(player1);
		player2.die();
		GM.endPlayerTurn();
		assertEquals(player2.getHandCards().size(), 0);
		assertTrue(player2.isElf());
		assertTrue(player2.isWarrior());
		assertEquals(player2.getAllItems().size(), 1);
		assertTrue(player2.getEquippedItems().contains(equipment5));
		assertEquals(player2.getLevel(), 3);
		assertEquals(GM.getDoorDeck().drawPile.size(), 1);
		assertTrue(GM.getDoorDeck().drawPile.contains(otherCard));
		assertEquals(GM.getTreasureDeck().drawPile.size(), 1);
		assertTrue(GM.getTreasureDeck().drawPile.contains(item3));
		assertFalse(player2.needsNewCards());
		player2.getEquippedItems().clear();
		player2.getRaceCards().clear();
		player2.getClassCards().clear();
		player2.getHandCards().clear();
		player2.goDownLevel();
	}
			
	private void testPhases() {
		assertTrue(GM.isOpenDoorPhase());
		GM.moveNextPhase();
		assertTrue(GM.isLookForTroublePhase());
		GM.moveNextPhase();
		assertTrue(GM.isLootRoomPhase());
		GM.moveNextPhase();
		assertTrue(GM.isCharityPhase());
		GM.moveNextPhase();
		assertTrue(GM.isCharityPhase());
		GM.endPlayerTurn();
		assertTrue(GM.isOpenDoorPhase());
		GM.moveToBattlePhase();
		assertTrue(GM.isBattlePhase());
		GM.moveNextPhase();
		assertTrue(GM.isAfterBattle());
		assertTrue(GM.isCharityPhase());
		GM.moveToBattlePhase();
		GM.setCanLootRoom();
		GM.moveNextPhase();
		assertTrue(GM.isLootRoomPhase());
		GM.endPlayerTurn();
		
		GM.moveToBattlePhase();
		GM.setActivePlayer(player1);
		GoUpLevelCard mow = new GoUpLevelCard(Card.GUL_MOW_THE_BATTLEFIELD, "Mow the Battlefield");
		player1.addCard(mow);
		try { assertFalse(CardPlayManager.playCard(player1, mow)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(player1.getLevel(), 1);
		player4.addCard(mow);
		try { assertFalse(CardPlayManager.playCard(player4, mow)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(player4.getLevel(), 4);
		player2.addCard(mow);
		try { assertFalse(CardPlayManager.playCard(player2, mow)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(player2.getLevel(), 2);
		player6.goUpLevels(10, false);
		player6.addCard(mow);
		try { assertFalse(CardPlayManager.playCard(player6, mow)); } catch (EndGameException ex) { fail("Not Game End"); }
		assertEquals(player6.getLevel(), 9);
		GM.moveNextPhase();
		assertTrue(GM.isCharityPhase());
		assertTrue(GM.isAfterBattle());
		assertTrue(player1.getHandCards().isEmpty());
		assertEquals(player1.getLevel(), 2);
		assertTrue(player2.getHandCards().isEmpty());
		assertEquals(player2.getLevel(), 3);
		assertTrue(player4.getHandCards().isEmpty());
		assertEquals(player4.getLevel(), 5);
		assertEquals(player6.getHandCards().size(), 1);
		assertTrue(player6.getHandCards().contains(mow));
		assertEquals(player6.getLevel(), 9);
		player1.goDownLevel();
		player2.goDownLevel();
		player4.goDownLevel();
		player6.goDownLevels(3);
	}
	
	private void testOpenDoor() {
		GM.setActivePlayer(player1);
		assertFalse(GM.isBattlePhase());
		GM.getDoorDeck().drawPile.push(new MonsterCard(1, "monster", 1, 1, 1, false) {
			public void doBadStuff(Player player) {}
		});
		GM.openDoor();
		assertTrue(GM.isBattlePhase());
		
		GM.endPlayerTurn();
		GM.setActivePlayer(player1);
		player1.goUpLevel(false);
		GM.getDoorDeck().drawPile.push(new CurseCard(2, "curse") {
			public void addEffects(Player player) { player.goDownLevel(); }
		});
		GM.openDoor();
		assertEquals(player1.getLevel(), 1);
		assertTrue(GM.isLookForTroublePhase());
		
		GM.endPlayerTurn();
		GM.setActivePlayer(player1);
		assertFalse(GM.isLookForTroublePhase());
		assertFalse(player1.isDwarf());
		assertTrue(player1.getHandCards().isEmpty());
		GM.getDoorDeck().drawPile.push(new RaceCard(3, "Dwarf", Race.DWARF));
		GM.openDoor();
		assertTrue(player1.getHandCards().isEmpty());
		assertTrue(player1.isDwarf());
		assertTrue(GM.isLookForTroublePhase());
		
		GM.endPlayerTurn();
		GM.setActivePlayer(player1);
		assertFalse(player1.isCleric());
		assertFalse(GM.isLookForTroublePhase());
		assertTrue(player1.getHandCards().isEmpty());
		GM.getDoorDeck().drawPile.push(new ClassCard(4, "Cleric", Class.CLERIC));
		GM.openDoor();
		assertTrue(player1.getHandCards().isEmpty());
		assertTrue(player1.isCleric());
		assertTrue(GM.isLookForTroublePhase());
		
		GM.endPlayerTurn();
		GM.setActivePlayer(player1);
		ItemCard item = new ItemCard(5, "item", 0);
		player1.addCard(item);
		MonsterEnhancerCard enhancer = new MonsterEnhancerCard(6, "Humongous", 10);
		MonsterEnhancerCard major = new MonsterEnhancerCard(7, "Major", 50);
		GM.getDoorDeck().drawPile.push(major);
		GM.getDoorDeck().discardPile.push(enhancer);
		GM.openDoor();
		assertEquals(player1.getHandCards().size(), 1);
		assertTrue(player1.getHandCards().contains(enhancer));
		assertFalse(GM.getDoorDeck().discardPile.peek() == enhancer);
		assertTrue(GM.getDoorDeck().drawPile.peek() == major);
		assertTrue(GM.getTreasureDeck().discardPile.peek() == item);
	}
	
	public void testCheckForWinners() {
		player1.goUpLevels(10, false);
		player5.goUpLevels(10, false);
		try { GM.checkForWinners(); } catch (EndGameException ex) { fail("Not Game End."); }
		
		player1.goUpLevel(true);
		try { 
			GM.checkForWinners();
			fail("Should be game end.");
		} catch (EndGameException ex) {}
		
		player3.addLastingCurse(new CurseCard(8, "Change Sex") {
			public void addEffects(Player player) {
				player.changeSex();
				player.addLastingCurse(this);
			}
			
			@Override
			public boolean isLastingCurse() {
				return true;
			}
		});
		player3.goUpLevels(10, true);
		try { 
			GM.checkForWinners();
			fail("Should be game end.");
		} catch (EndGameException ex) {}
		
		player1.goDownLevels(10);
		try { 
			GM.checkForWinners();
			fail("Should be game end.");
		} catch (EndGameException ex) {}
	}
}
