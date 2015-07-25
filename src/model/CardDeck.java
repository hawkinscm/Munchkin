
package model;

import java.util.EmptyStackException;
import java.util.Observable;
import java.util.Stack;
import java.util.LinkedList;

import model.card.Card;

import exceptions.EndGameException;
import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;

import gui.GUI;

/**
 * Class that represents the a card deck with a draw pile and a discard pile.
 */
public class CardDeck extends Observable {
	private static final long serialVersionUID = 1L;

	/**
	 * Stack of cards that make up the draw pile.
	 */
	protected Stack<Card> drawPile;
	/**
	 * Stack of cards that make up the discard pile.
	 */
	protected Stack<Card> discardPile;
	
	/**
	 * Creates a new CardDeck object.
	 * @param gui reference to the main controlling GUI
	 * @param cards all the cards for the deck
	 */
	public CardDeck(GUI gui, Stack<Card> cards) {
		addObserver(gui);
		
		drawPile = cards;
		discardPile = new Stack<Card>();
		
		shuffle();
	}
	
	/**
	 * Returns the draw pile stack of cards.
	 * @return the draw pile
	 */
	protected Stack<Card> getDrawPile() {
		return drawPile;
	}
	
	/**
	 * Randomly sorts all the cards in the draw pile.
	 */
	protected void shuffle() {
		LinkedList<Card> tempDeck = new LinkedList<Card>();
		tempDeck.addAll(drawPile);
		drawPile.clear();
		
		for (int numCards = tempDeck.size(); numCards > 0; numCards--) {
			int cardNumber = Randomizer.getRandom(numCards);
			drawPile.push(tempDeck.remove(cardNumber));
		}
	}
	
	/**
	 * Returns the top card from the draw pile stack of cards.
	 * @return the top card from the draw pile
	 * @throws PlayImmediatelyException occurs when the drawn card must be played immediately
	 * @throws NoCardsLeftException occurs when there are no cards left to draw
	 * @throws EndGameException occurs when the drawn card causes someone to win and ends the game
	 */
	public Card drawCard() throws PlayImmediatelyException, NoCardsLeftException, EndGameException {
		try {
			// if the draw pile is empty, move all discards to the draw pile and shuffle them
			if (drawPile.isEmpty()) {
				while (!discardPile.isEmpty())
					drawPile.push(discardPile.pop());
			
				shuffle();
				setChanged();
				notifyObservers();
			}
			
			// If both draw and discard piles are empty, this will throw EmptyStackException 
			// which in turn throws a NoCardsLeftException.
			return drawPile.pop();
		}
		catch (EmptyStackException ex) { throw new NoCardsLeftException(); }
	}
	
	/**
	 * Puts the given card on the top of the discard pile.
	 * @param card the card to be discarded
	 */
	public void discard(Card card) {
		discardPile.push(card);
		setChanged();
		notifyObservers(card);
	}
	
	/**
	 * Removes and returns the top card from the discard pile.
	 * @return the top card of the discard pile
	 * @throws PlayImmediatelyException occurs when the taken card must be played immediately
	 * @throws EndGameException occurs when the drawn card causes someone to win and ends the game
	 */
	public Card takeDiscard() throws PlayImmediatelyException, EndGameException {
		Card card = discardPile.pop();
		setChanged();
		if (discardPile.isEmpty())
			notifyObservers(null);
		else
			notifyObservers(discardPile.peek());
				
		return card;
	}
	
	/**
	 * Return the discard pile stack of cards.
	 * @return the discard pile
	 */
	public Stack<Card> getDiscardPile() {
		return discardPile;
	}
			
	// TESTING PURPOSES ONLY
	/**
	 * Returns whether or not the deck (draw pile and discard pile combined) contains the given card.
	 * @param card the card to check for
	 * @return true if the card if found in the deck (draw/discard piles); false otherwise
	 */
	public boolean containsCard(Card card) {
		return (drawPile.contains(card) || discardPile.contains(card));
	}
	
	// TESTING PURPOSES ONLY
	/**
	 * Removes the card from the deck (draw pile and discard pile combined).
	 * @param card the card to remove
	 * @return true if the card was found in the deck (draw/discard piles); false otherwise
	 */
	public boolean removeCard(Card card) {
		return (drawPile.remove(card) || discardPile.remove(card));
	}
}
