
package model;

import exceptions.EndGameException;
import exceptions.PlayImmediatelyException;
import gui.GUI;
import gui.MockGUI;

import java.util.Iterator;
import java.util.LinkedList;

import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.card.Card;
import model.card.ClassCard;
import model.card.DoorCard;
import model.card.OtherDoorCard;

public class DoorDeckTest extends UnitTest {

	private class MockDoorDeck extends DoorDeck {
		
		public MockDoorDeck(GUI gui) {
			super(gui);
		}
		
		@Override
		public DoorCard drawCard() throws PlayImmediatelyException, EndGameException {		
			if (getDrawPile().isEmpty()) {
				while (!getDiscardPile().isEmpty())
					getDrawPile().push(getDiscardPile().pop());
			
				shuffle();
				setChanged();
				notifyObservers();
			}		
			DoorCard card = (DoorCard)getDrawPile().pop();
			
			if (card.getID() == Card.OD_DIVINE_INTERVENTION) {
				Iterator<Player> playerIter = GM.getPlayers().iterator();
				while (playerIter.hasNext()) {
					Player current = playerIter.next();
					if (current.isCleric())
						current.goUpLevel(true);
				}
				discard(card);
				
				GM.checkForWinners();
				
				throw new PlayImmediatelyException();
			}
			
			return card;
		}
		
		@Override
		public DoorCard takeDiscard() throws PlayImmediatelyException, EndGameException {
			DoorCard card = (DoorCard)getDiscardPile().pop();
			setChanged();
			if (getDiscardPile().isEmpty())
				notifyObservers(null);
			else
				notifyObservers(getDiscardPile().peek());
			
			if (card.getID() == Card.OD_DIVINE_INTERVENTION) {
				Iterator<Player> playerIter = GM.getPlayers().iterator();
				while (playerIter.hasNext()) {
					Player current = playerIter.next();
					if (current.isCleric())
						current.goUpLevel(true);
				}
				discard(card);
				
				GM.checkForWinners();
				
				throw new PlayImmediatelyException();
			}
			
			return card;
		}
	}
	
	private DoorDeck deck;
	
	private int divineCount = 0;
	
	public int testAll() {
		initialize();
		
		testDrawCard();
		testTakeDiscard();
		
		return errorCount;
	}
	
	private void initialize() {
		OtherDoorCard munchkin = new OtherDoorCard(Card.OD_SUPER_MUNCHKIN_1, "Super Munchkin");
		
		MockGUI mockGUI = new MockGUI(0);
		
		Player normal = new Player(mockGUI, "", true, PlayerType.HUMAN);
		Player cleric = new Player(mockGUI, "", false, PlayerType.HUMAN);
		cleric.addClassCard(new ClassCard(1, "Cleric", Class.CLERIC));
		Player cleric_warrior = new Player(mockGUI, "", true, PlayerType.HUMAN);
		cleric_warrior.addClassCard(new ClassCard(2, "Cleric", Class.CLERIC));
		cleric_warrior.setSuperMunchkinCard(munchkin);
		cleric_warrior.addClassCard(new ClassCard(3, "Warrior", Class.WARRIOR));
		Player cleric_wizard = new Player(mockGUI, "", false, PlayerType.HUMAN);
		cleric_wizard.addClassCard(new ClassCard(4, "Cleric", Class.CLERIC));
		cleric_wizard.setSuperMunchkinCard(munchkin);
		cleric_wizard.addClassCard(new ClassCard(5, "Wizard", Class.WIZARD));
		Player cleric_thief = new Player(mockGUI, "", true, PlayerType.HUMAN);
		cleric_thief.addClassCard(new ClassCard(6, "Cleric", Class.CLERIC));
		cleric_thief.setSuperMunchkinCard(munchkin);
		cleric_thief.addClassCard(new ClassCard(7, "Thief", Class.THIEF));
		
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(normal);
		players.add(cleric);
		players.add(cleric_warrior);
		players.add(cleric_wizard);
		players.add(cleric_thief);
		GM.newTestGame(mockGUI, players);		
		
		deck = new MockDoorDeck(mockGUI);
		assertEquals(deck.getDrawPile().size(), 94);
		assertTrue(deck.getDiscardPile() != null);
	}
	
	private void testDrawCard() {
		for (int count = 1; count <= 94; count++) {
			try {
				deck.discard(deck.drawCard());
				assertEquals(deck.getDrawPile().size(), 94 - count);
				assertEquals(deck.getDiscardPile().size(), count);
			}
			catch (PlayImmediatelyException ex) {
				divineCount++;
				assertEquals(GM.getPlayers().get(0).getLevel(), 1);
				assertEquals(GM.getPlayers().get(1).getLevel(), 1 + divineCount);
				assertEquals(GM.getPlayers().get(2).getLevel(), 1 + divineCount);
				assertEquals(GM.getPlayers().get(3).getLevel(), 1 + divineCount);
				assertEquals(GM.getPlayers().get(4).getLevel(), 1 + divineCount);
				assertEquals(deck.getDrawPile().size(), 94 - count);
				assertEquals(deck.getDiscardPile().size(), count);
			}
			catch (EndGameException ex) { 
				fail("This is Not Game Over."); 
				break; 
			}
		}
		
		try {
			deck.discard(deck.drawCard());
			assertEquals(deck.getDrawPile().size(), 93);
			assertEquals(deck.getDiscardPile().size(), 1);
		}
		catch (PlayImmediatelyException ex) {
			divineCount++;
			assertEquals(GM.getPlayers().get(0).getLevel(), 1);
			assertEquals(GM.getPlayers().get(1).getLevel(), 1 + divineCount);
			assertEquals(GM.getPlayers().get(2).getLevel(), 1 + divineCount);
			assertEquals(GM.getPlayers().get(3).getLevel(), 1 + divineCount);
			assertEquals(GM.getPlayers().get(4).getLevel(), 1 + divineCount);
			assertEquals(deck.getDrawPile().size(), 93);
			assertEquals(deck.getDiscardPile().size(), 1);
		}
		catch (EndGameException ex) { fail("This is Not Game Over."); }
	}
	
	private void testTakeDiscard() {
		while (!deck.getDrawPile().isEmpty())
			deck.getDiscardPile().push(deck.getDrawPile().pop());
		assertTrue(deck.getDrawPile().isEmpty());
		assertEquals(deck.getDiscardPile().size(), 94);
		
		for (int count = 1; count <= 94; count++) {
			try {
				deck.takeDiscard();
				assertEquals(deck.getDiscardPile().size(), 94 - count);
			}
			catch (PlayImmediatelyException ex) {
				divineCount++;
				assertEquals(GM.getPlayers().get(0).getLevel(), 1);
				assertEquals(GM.getPlayers().get(1).getLevel(), 1 + divineCount);
				assertEquals(GM.getPlayers().get(2).getLevel(), 1 + divineCount);
				assertEquals(GM.getPlayers().get(3).getLevel(), 1 + divineCount);
				assertEquals(GM.getPlayers().get(4).getLevel(), 1 + divineCount);
				assertEquals(deck.getDiscardPile().size(), (94 - count) + 1);
				Card topCard = deck.getDiscardPile().pop();
				assertEquals(topCard.getName(), "Divine Intervention");
				assertEquals(topCard.getID(), Card.OD_DIVINE_INTERVENTION);
				assertEquals(deck.getDiscardPile().size(), 94 - count);
			}
			catch (EndGameException ex) { 
				fail("This is Not Game Over."); 
				break; 
			}
		}
	}
}
