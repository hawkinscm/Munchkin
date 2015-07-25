
package gui;

import java.awt.Color;
import java.util.LinkedList;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import ai.AIBattleManager;
import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

import model.Player;
import model.card.Card;
import model.card.CurseCard;
import model.card.MonsterCard;
import model.card.TreasureCard;

/**
 * Dialog that allows a Cleric to use the Resurrect ability.
 */
public class ResurrectionDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card Panel that displays the cards in the player's hand
	private CardPanel handCardPanel;
	// The card selected to be discarded
	private Card cardToDiscard;
	
	private Player player;
	
	/**
	 * Create a new ResurrectionDialog dialog.
	 * @param player Player who using the resurrection ability
	 * @param card the Card that can be resurrected
	 */
	public ResurrectionDialog(Player p, Card card) {
		super("Resurrection");
		
		// initialize variables and display GUI controls
		cardToDiscard = null;
		
		player = p;
		
		if (player.isComputer()) {
			// Determine if value of the upturned card is > than the least valued hand card
			LinkedList<AIValuedCard> leastValuedCards = AIManager.getLeastValuedHandCards(player, player.getHandCards());
			AIValuedCard leastValuedCard = leastValuedCards.getFirst();
			player.getHandCards().remove(leastValuedCard.getCard());
			int cardValue = 0;
			if (card instanceof CurseCard)
				cardValue = -AIManager.getCurseCost((CurseCard)card, player, player.getHandCards()) - leastValuedCard.getValue();
			else if (card instanceof MonsterCard) {
				LinkedList<Card> handCards = new LinkedList<Card>();
				handCards.addAll(player.getHandCards());
				player.getHandCards().clear();
				player.getHandCards().add(card);
				if (AIBattleManager.getMonsterToBattle(player) == card)
					cardValue = AICardEvaluator.getCardValueToPlayer(card, player, null) - leastValuedCard.getValue();
				player.getHandCards().clear();
				player.getHandCards().addAll(handCards);
			}
			else
				cardValue = AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards()) - leastValuedCard.getValue();
			player.getHandCards().add(leastValuedCard.getCard());
			
			if (player.getHandCards().size() > 1) {
				player.getHandCards().remove(leastValuedCards.get(1).getCard());
				int newCardValue = 0;
				if (card instanceof CurseCard)
					newCardValue = -AIManager.getCurseCost((CurseCard)card, player, player.getHandCards()) - leastValuedCard.getValue();
				// Value for monster cards would be the same; don't repeat process
				else if (card instanceof MonsterCard)
					newCardValue = 0;
				else
					newCardValue = AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards()) - leastValuedCards.get(1).getValue();
				if (cardValue < newCardValue) {
					leastValuedCard = leastValuedCards.get(1);
					cardValue = newCardValue;
				}
				player.getHandCards().add(leastValuedCards.get(1).getCard());
			}
			
			// If the value (minus the cost) of taking the card is greater than the average value of a random card,
			// then resurrect and inform the players
			if (cardValue >= AIManager.UNKNOWN_CARD_VALUE) {
				cardToDiscard = leastValuedCard.getCard();
				String message = player + " discarded the " + cardToDiscard + " card ";
				message += "to resurrect and take the " + card + " card.";
				Messenger.display(message, "Class Power Used");
			}
			
			return;
		}
		
		c.gridwidth = 2;
		// inform the user of his option to either resurrect a Card or draw normally
		String deckType = "Door";
		if (card instanceof TreasureCard)
			deckType = "Treasure";		
		String infoText = "<HTML>&nbsp;" +
			"You may either discard a card from your hand and take the \"" + card + "\" card, " +
			"&nbsp;<br>&nbsp;" +
			"or you can draw a card from the " + deckType + " Deck.";
		JLabel infoLabel = new JLabel(infoText);
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		// Button that resurrects a card and closes the dialog
		CustomButton resurrectButton = new CustomButton("<HTML><center>&nbsp;Discard And&nbsp;<br>&nbsp;Resurrect&nbsp;</center></HTML>") {
			private static final long serialVersionUID = 1L;
				
			public void buttonPressed() {
				cardToDiscard = handCardPanel.getSelectedCard();
				dispose();
			}
		};
				
		// Button that chooses to draw normally, ignoring the resurrect option and closing the dialog 
		CustomButton drawDeckButton = new CustomButton("<HTML><center>&nbsp;Draw From&nbsp;<br>&nbsp;Deck&nbsp;</center></HTML>") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		
		c.gridy++;
		getContentPane().add(new JLabel(card.getPicture()), c);
		
		c.gridx++;
		handCardPanel = new CardPanel(player.getHandCards(), 2, resurrectButton, drawDeckButton);
		handCardPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Cards In Hand"));
		getContentPane().add(handCardPanel, c);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!player.isComputer())
			super.setVisible(b);
	}
	
	/**
	 * Returns the Card from the player's hand that will be discarded in order to resurrect.
	 * @return the Card to discard; null if not resurrecting
	 */
	public Card getCardToDiscard() {
		return cardToDiscard;
	}
}
