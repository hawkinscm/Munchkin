
package model;

import exceptions.EndGameException;
import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;
import gui.DisplayCardsDialog;
import gui.GUI;

import java.util.Iterator;

import model.card.Card;
import model.card.DoorCard;

/**
 * Class that represents the Door Card Deck with a draw pile and a discard pile.
 */
public class DoorDeck extends CardDeck {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new DoorDeck Card Deck.
	 * @param gui reference to the main controlling GUI
	 */
	public DoorDeck(GUI gui) {
		super(gui, DoorDeckFactory.buildDeck());
	}
		
	@Override
	public DoorCard drawCard() throws PlayImmediatelyException, EndGameException {
		try {
			DoorCard card = (DoorCard)super.drawCard();
		
			// Divine Intervention must be played immediately and can end the game
			if (card.getID() == Card.OD_DIVINE_INTERVENTION) {
				// Show the players the drawn card
				DisplayCardsDialog dialog = new DisplayCardsDialog(card, "");
				dialog.setVisible(true);
				
				// All players who are Clerics, go up a level
				Iterator<Player> playerIter = GM.getPlayers().iterator();
				while (playerIter.hasNext()) {
					Player current = playerIter.next();
					if (current.isCleric())
						current.goUpLevel(true);
				}
				discard(card);
				
				// if a Cleric was a level nine, they win and the game is over
				GM.checkForWinners();
				
				throw new PlayImmediatelyException();
			}
			
			return card;
		}
		// Door deck can never run out of cards
		catch (NoCardsLeftException ex) { return null; }		
	}
		
	@Override
	public DoorCard takeDiscard() throws PlayImmediatelyException, EndGameException {
		DoorCard card = (DoorCard)super.takeDiscard();
		
		// Divine Intervention must be played immediately and can end the game
		if (card.getID() == Card.OD_DIVINE_INTERVENTION) {
			// All players who are Clerics, go up a level
			Iterator<Player> playerIter = GM.getPlayers().iterator();
			while (playerIter.hasNext()) {
				Player current = playerIter.next();
				if (current.isCleric())
					current.goUpLevel(true);
			}
			discard(card);
			
			// if a Cleric was a level nine, they win and the game is over
			GM.checkForWinners();
			
			throw new PlayImmediatelyException();
		}
		
		return card;
	}
}
