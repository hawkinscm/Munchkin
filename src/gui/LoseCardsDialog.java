
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JLabel;

import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

import model.GM;
import model.Player;
import model.Randomizer;
import model.card.Card;

/**
 * Dialog that allows a Player to select a set number of Cards to discard.
 */
public class LoseCardsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card Panel that displays the cards that may be discarded
	private CardPanel cardPanel;
	
	// Player who is discarding cards
	private Player player;
	// number of Cards that still need to be discarded
	private int numDiscardsLeft;
	// whether or not the cards should be given to charity or discarded.
	private boolean isForCharity = false;
	// whether or not a card has been discarded yet or not (after one has been there is no way to back out)
	private boolean madeDiscard;
			
	/**
	 * Creates a new LoseCardsDialog dialog.
	 * @param victim Player who is discarding cards
	 * @param cards list of Cards that the Player must choose from to discard
	 * @param numCards number of Cards that must be discarded 
	 * @param typeMsg text that informs the user what type of cards need to be discarded
	 */
	public LoseCardsDialog(Player victim, final LinkedList<Card> cards, int numCards, String typeMsg) {
		super("Discard");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// initialize variables and display GUI controls
		player = victim;
		numDiscardsLeft = numCards;
		madeDiscard = false;
		
		if (player.isComputer()) {
			// Determine lowest worth cards and discard them
			LinkedList<AIValuedCard> leastValuedCards = new LinkedList<AIValuedCard>();
			for (Card card : cards) {
				AIValuedCard valuedCard = new AIValuedCard(card, AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards()));
				int cardIdx;
				for (cardIdx = 0; cardIdx < leastValuedCards.size(); cardIdx++)
					if (valuedCard.getValue() < leastValuedCards.get(cardIdx).getValue())
						break;
				
				leastValuedCards.add(cardIdx, valuedCard);
			}

			String discardList = "";
			int count = 0;
			for (AIValuedCard valuedCard : leastValuedCards) {
				loseCard(valuedCard.getCard());
				discardList = "\n" + valuedCard.getCard();
				
				count++;
				if (count >= numCards)
					break;
			}
			
			String message = player + " discarded the following card(s):" + discardList;
			if (isForCharity)
				message = player + " gave the following card(s) to charity: " + discardList;
			Messenger.display(message, "Discard " + typeMsg);

			madeDiscard = true;
			return;
		}
		
		c.gridwidth = 2;
		final JLabel infoLabel = new JLabel(player.getName() + ": Discard " + numDiscardsLeft + " " + typeMsg + ".");
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		// if Player has no cards to discard, inform him and display only button to close dialog
		if (cards.isEmpty()) {
			infoLabel.setText(player.getName() + " has no " + typeMsg + " to discard.");
			
			c.gridx++;
			c.gridy++;
			CustomButton doneButton = new CustomButton("Done") {
				private static final long serialVersionUID = 1L;
				
				public void buttonPressed() {
					dispose();
				}
			};
			getContentPane().add(doneButton, c);
			
			setJMenuBar(null);
			refresh();
			return;
		}
		
		// Button that will discard the selected card
		CustomButton discardButton = new CustomButton("Discard") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				// after discarding one card, all other set number of cards must be discarded
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				
				Card card = cardPanel.getSelectedCard();
				loseCard(card);				
				cards.remove(card);
				cardPanel.removeSelectedImage();				
				numDiscardsLeft--;
				
				// if the player has no cards left to discard, tell him; or tell him how many still need to be discarded
				String infoText = "Dicarded \"" + card + "\". ";
				if (cards.size() == 0 && numDiscardsLeft > 0) {
					infoText += "Nothing left to discard.";
					numDiscardsLeft = 0;
				}
				else
					infoText += numDiscardsLeft + " more to discard.";
				infoLabel.setText(infoText);
							
				// if set number of cards have been discarded, close dialog
				if (numDiscardsLeft == 0) {
					madeDiscard = true;
					dispose();
				}
			}
		};
		
		c.gridy++;
		c.gridwidth = 2;
		cardPanel = new CardPanel(cards, 3, discardButton, null);
		getContentPane().add(cardPanel, c);
		c.gridwidth = 1;
		
		refresh();
	}
	
	/**
	 * Removes the card from the player and either discards it or gives it to charity.
	 * @param card card to lose
	 */
	private void loseCard(Card card) {
		player.discard(card);
		
		LinkedList<Player> lowestLevelPlayers = GM.getLowestLevelPlayers();
		if (!isForCharity || lowestLevelPlayers.contains(player))			
			return;
		
		if (GM.getDoorDeck().getDiscardPile().peek() == card)
			GM.getDoorDeck().getDiscardPile().pop();
		else if (GM.getTreasureDeck().getDiscardPile().peek() == card)
			GM.getTreasureDeck().getDiscardPile().pop();
			
		
		int playerIndex = 0;
		// Randomize which of the lowest level players gets the lost card
		if (lowestLevelPlayers.size() > 1)
			playerIndex = Randomizer.getRandom(lowestLevelPlayers.size());
		Player charityPlayer = lowestLevelPlayers.get(playerIndex);
		
		charityPlayer.addCard(card);
			
		if (charityPlayer.isComputer())
			AIManager.playHandCard(charityPlayer, card);
		else
			(new InHandDialog(charityPlayer, card)).setVisible(true);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!player.isComputer())
			super.setVisible(b);
	}
	
	/**
	 * Sets the dialog to give the lost cards to charity rather than discarding them.
	 */
	public void giveToCharity() {
		isForCharity = true;
	}
	
	/**
	 * Returns whether the Player discarded as many set number of Cards as he was able to or not.
	 * @return true if a card was discarded; false otherwise
	 */
	public boolean madeDiscard() {
		return madeDiscard;
	}
}
