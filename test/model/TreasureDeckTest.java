
package model;

import java.util.EmptyStackException;
import java.util.LinkedList;

import model.Player;
import model.PlayerType;
import model.card.Card;
import model.card.TreasureCard;

import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;
import gui.GUI;
import gui.MockGUI;

public class TreasureDeckTest extends UnitTest {
	
	private class MockTreasureDeck extends TreasureDeck {
		
		public MockTreasureDeck(GUI gui) {
			super(gui);
		}
		
		@Override
		public TreasureCard drawCard() throws PlayImmediatelyException, NoCardsLeftException {		
			try {
				if (getDrawPile().isEmpty()) {
					while (!getDiscardPile().isEmpty())
						getDrawPile().push(getDiscardPile().pop());
				
					shuffle();
					setChanged();
					notifyObservers();
				}
				TreasureCard card = (TreasureCard)getDrawPile().pop();		
			
				if (card.getID() == Card.OT_HOARD) {
					discard(card);			
					throw new PlayImmediatelyException();
				}
			
				return card;
			}
			catch (EmptyStackException ex) { throw new NoCardsLeftException(); }
		}
		
		@Override
		public TreasureCard takeDiscard() throws PlayImmediatelyException {
			TreasureCard card = (TreasureCard)getDiscardPile().pop();
			setChanged();
			if (getDiscardPile().isEmpty())
				notifyObservers(null);
			else
				notifyObservers(getDiscardPile().peek());
			
			if (card.getID() == Card.OT_HOARD) {
				discard(card);		
				throw new PlayImmediatelyException();
			}
			
			return card;
		}
	}
	
	private GUI mockGUI;	
	private TreasureDeck deck;
	
	public int testAll() {
		initialize();
		
		testDrawCard();
		testTakeDiscard();
		testHoardCards();
		
		return errorCount;
	}
	
	private void initialize() {
		mockGUI = new MockGUI(0);
		
		deck = new MockTreasureDeck(mockGUI);
		assertEquals(deck.getDrawPile().size(), 74);
		assertTrue(deck.getDiscardPile() != null);
	}
	
	private void testDrawCard() {
		for (int count = 1; count <= 74; count++) {
			try {
				deck.discard(deck.drawCard());
				assertEquals(deck.getDrawPile().size(), 74 - count);
				assertEquals(deck.getDiscardPile().size(), count);
			}
			catch (PlayImmediatelyException ex) {
				assertEquals(deck.getDrawPile().size(), 74 - count);
				assertEquals(deck.getDiscardPile().size(), count);
			}
			catch (NoCardsLeftException ex) { 
				fail("There are still treasures."); 
				break; 
			}
		}
		
		try {
			deck.discard(deck.drawCard());
			assertEquals(deck.getDrawPile().size(), 73);
			assertEquals(deck.getDiscardPile().size(), 1);
		}
		catch (PlayImmediatelyException ex) {
			assertEquals(deck.getDrawPile().size(), 73);
			assertEquals(deck.getDiscardPile().size(), 1);
		}
		catch (NoCardsLeftException ex) { fail("There are still treasures."); }
		
		deck.getDiscardPile().clear();
		deck.getDrawPile().clear();
		try {
			deck.drawCard();
			fail("There are no treasures");
		}
		catch (PlayImmediatelyException ex) { fail("There are no treasures"); }
		catch (NoCardsLeftException ex) {}
	}
	
	private void testTakeDiscard() {
		deck = new MockTreasureDeck(mockGUI);
		
		while (!deck.getDrawPile().isEmpty())
			deck.getDiscardPile().push(deck.getDrawPile().pop());
		assertTrue(deck.getDrawPile().isEmpty());
		assertEquals(deck.getDiscardPile().size(), 74);
		
		for (int count = 1; count <= 74; count++) {
			try {
				deck.takeDiscard();
				assertEquals(deck.getDiscardPile().size(), 74 - count);
			}
			catch (PlayImmediatelyException ex) {
				assertEquals(deck.getDiscardPile().size(), (74 - count) + 1);
				Card topCard = deck.getDiscardPile().pop();
				assertEquals(topCard.getID(), Card.OT_HOARD);
				assertEquals(topCard.getName(), "Hoard!");
				assertEquals(deck.getDiscardPile().size(), 74 - count);
			}
		}
	}
	
	private void testHoardCards() {
		deck = new MockTreasureDeck(mockGUI);
		
		for (int count = 1; count <= 74; count++) {
			try {
				deck.discard(deck.drawCard());
			}
			catch (PlayImmediatelyException ex) {
				if (count > 71) {
					testHoardCards();
					return;
				}
				
				Player player = new Player(mockGUI, "", false, PlayerType.HUMAN);
				LinkedList<TreasureCard> hoardCards = deck.getHoardCards(player, false);
				assertEquals(hoardCards.size(), 3);
				assertEquals(deck.getDrawPile().size(), (74 - count) - 3);
				assertEquals(deck.getDiscardPile().size(), count);
				return;
			}
			catch (NoCardsLeftException ex) { 
				fail("There are still treasures."); 
				break; 
			}
		}
	}
}
