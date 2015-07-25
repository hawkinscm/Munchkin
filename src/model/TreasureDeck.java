
package model;

import java.util.LinkedList;

import model.card.Card;
import model.card.TreasureCard;

import exceptions.EndGameException;
import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;
import gui.DisplayCardsDialog;
import gui.GUI;
import gui.ResurrectionDialog;
import gui.components.Messenger;

/**
 * Class that represents the Treasure Card Deck with a draw pile and a discard pile.
 */
public class TreasureDeck extends CardDeck {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new TreasureDeck Card Deck.
	 * @param gui reference to the main controlling GUI
	 */
	public TreasureDeck(GUI gui) {
		super(gui, TreasureDeckFactory.buildDeck());
	}
			
	@Override
	public TreasureCard drawCard() throws PlayImmediatelyException, NoCardsLeftException{		
		try {
			TreasureCard card = (TreasureCard)super.drawCard();	
			
			// The Hoard Card is a play immediately card and is displayed and discarded.
			// Hoard Card results are handled by the method that catches the PlayImmediatelyException.
			if (card.getID() == Card.OT_HOARD) {
				DisplayCardsDialog dialog = new DisplayCardsDialog(card);
				dialog.setVisible(true);
					
				discard(card);			
				throw new PlayImmediatelyException();
			}
			
			return card;
		}
		catch (NoCardsLeftException ex) {
			String message = "There are no treasures left to draw. You get nothing.";
			Messenger.display(message, "No More Treasures");
			throw ex;
		}
		// this cannot happen from a treasure card
		catch (EndGameException ex) { return null; }
	}
	
	@Override
	public TreasureCard takeDiscard() throws PlayImmediatelyException {
		try {
			TreasureCard card = (TreasureCard)super.takeDiscard();
			
			// The Hoard Card is a play immediately card and is displayed and discarded.
			// Hoard Card results are handled by the method that catches the PlayImmediatelyException.
			if (card.getID() == Card.OT_HOARD) {
				DisplayCardsDialog dialog = new DisplayCardsDialog(card);
				dialog.setVisible(true);
				
				discard(card);		
				throw new PlayImmediatelyException();
			}
			
			return card;
		}
		// this cannot happen from a treasure card
		catch (EndGameException ex) { return null; }
	}
		
	/**
	 * Returns a list of Treasure Cards given through the Hoard Card.
	 * @param player player who drew the Hoard Card
	 * @param isFaceUp whether or not to draw the cards face-up or face-down
	 * @return the Treasure Cards given by drawing the Hoard Card
	 */
	public LinkedList<TreasureCard> getHoardCards(Player player, boolean isFaceUp) {
		LinkedList<TreasureCard> treasures = new LinkedList<TreasureCard>();
		
		Card hoardCard = discardPile.pop();
		
		try {
			for (int count = 1; count <= 3; count++) {
				TreasureCard treasure = null;
				// Allow the player to use the Resurrect ability if available
				if (isFaceUp && player.canResurrect() && !discardPile.isEmpty()) {
					ResurrectionDialog dialog = new ResurrectionDialog(player, discardPile.peek());
					dialog.setVisible(true);
					Card cardToDiscard = dialog.getCardToDiscard();
					if (cardToDiscard != null) {
						treasure = takeDiscard();
						player.discard(cardToDiscard);
					}
				}
				
				if (treasure == null)
					treasure = drawCard();
				treasures.add(treasure);
			}
		}
		catch (PlayImmediatelyException ex) {}
		catch (NoCardsLeftException ex) {}
		
		discard(hoardCard);		
		return treasures;
	}
}
