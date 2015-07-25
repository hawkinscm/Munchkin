
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

import model.GM;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import model.card.Card;
import model.card.DoorCard;
import model.card.TreasureCard;

/**
 * Dialog that allows a player to take a card from another player to either keep or discard.
 */
public class TakeCardDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Enumerator of the possible locations or types to take from
	public enum DrawLocation {
		HAND,
		CARRIED,
		EITHER,
		TREASURE;
	}
	
	// GUI controls for displaying information and getting user input
	private CardPanel cardPanel;
	private CustomButton takeButton;
	private CustomButton doneButton;
	private JLabel warningLabel;
	
	// list of cards to choose from
	private LinkedList<Card> cards;
	// location where the card can be chosen from
	private DrawLocation currentLocation;
	// whether or not the taker needs to discard the taken card
	private boolean isDiscard;
	
	private Player victim;
	private Player taker;
	private DrawLocation location;
	private boolean isHandVisible = false;
			
	/**
	 * Creates a new TakeCardDialog dialog.
	 * @param victim the player who will lose a card
	 * @param taker the player who is taking a card
	 * @param location the location where the card may be taken from
	 * @param visibleCards whether or not the taker is allowed to see the cards when choosing what to take
	 * @param isDiscard whether or not the taker 
	 */
	public TakeCardDialog(final Player v, final Player t, final DrawLocation loc, boolean visibleCards, boolean discard) {
		super("Take A Card");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		victim = v;
		taker = t;
		location = loc;
		isHandVisible = visibleCards;
		isDiscard = discard;
		
		if (taker.isComputer())
			return;
		
		// initialize variables and display GUI controls
		cardPanel = null;
		
		c.gridwidth = 2;
		// create a prompt to inform the taker what he is allowed to take
		String type = "a card";
		if (location == DrawLocation.CARRIED)
			type = "an item";
		else if (location == DrawLocation.EITHER)
			type += " or item";
		else if (location == DrawLocation.TREASURE)
			type = "a Treasure card";
		final JLabel infoLabel = new JLabel(taker.getName() + ", take " + type + " from " + victim.getName());
		getContentPane().add(infoLabel, c);
		
		c.gridy = 2;
		// warn the taker about the restrictions on Big Items
		warningLabel = new JLabel("WARNING: If you take a Big Item that you cannot carry, it will be discarded!");
		getContentPane().add(warningLabel, c);
		warningLabel.setVisible(false);
		
		// Button that lets the taker take the selected Card
		takeButton = new CustomButton("Take Card") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Card card = cardPanel.getSelectedCard();
				
				// if required, discard the taken card
				if (isDiscard) {
					victim.discard(card);
					DisplayCardsDialog viewCardDialog = new DisplayCardsDialog(card, "Discarded");
					viewCardDialog.setVisible(true);
				}
				else if (currentLocation == DrawLocation.HAND) {
					victim.getHandCards().remove(card);
					taker.getHandCards().add(card);
					(new InHandDialog(taker, card)).setVisible(true);
				}
				else if (currentLocation == DrawLocation.CARRIED) {
					if (!victim.removeEquipmentItem(card))
						if (!victim.getCarriedItems().remove(card))
							victim.setHirelingCard(null);
					taker.addItem((TreasureCard)card);
				}
				else {
					if (!victim.getHandCards().remove(card))
						if (!victim.removeEquipmentItem(card))
							if (!victim.getCarriedItems().remove(card))
								victim.setHirelingCard(null);
					taker.getHandCards().add(card);
					(new InHandDialog(taker, card)).setVisible(true);
				}
				
				dispose();
			}
		};
		
		// Button that signals that the taker is done taking and closes the dialog
		doneButton = new CustomButton("Done") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		doneButton.setEnabled(false);
		
		c.gridwidth = 1;
		c.gridy = 1;
		c.insets.right = 0;
		// If the taker can take from either the victim's hand or carried items, 
		// provides radio button options to select where card will be taken from.
		JRadioButton handButton = new JRadioButton("Hand");
		handButton.setSelected(true);
		handButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Load Cards from the victim's hand
				currentLocation = DrawLocation.HAND;
				
				warningLabel.setVisible(false);
				
				if (cardPanel != null)
					getContentPane().remove(cardPanel);
				
				cards = new LinkedList<Card>();
				if (location == DrawLocation.TREASURE) {
				for (Card handCard : victim.getHandCards())
					if (handCard instanceof TreasureCard)
						cards.add(handCard);
				}
				else
					cards.addAll(victim.getHandCards());
				
				if (isHandVisible)
					cardPanel = new CardPanel(cards, 2, takeButton, doneButton);
				else
					setHiddenCardPanel(cards);
				getContentPane().add(cardPanel, c);
				
				refresh();
			}
		});

        JRadioButton carriedButton = new JRadioButton("Carried");
        carriedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Load Cards from the player's carried items
				currentLocation = DrawLocation.CARRIED;
				
				if (!isDiscard)
					warningLabel.setVisible(true);
				
				if (cardPanel != null)
					getContentPane().remove(cardPanel);
				
				cards = new LinkedList<Card>();
				cards.addAll(victim.getAllItems());
				if (location == DrawLocation.TREASURE && victim.hasHireling())
					cards.add(victim.getHirelingCard());
				cardPanel = new CardPanel(cards, 2, takeButton, doneButton);
				getContentPane().add(cardPanel, c);

				refresh();
			}
		});

        ButtonGroup group = new ButtonGroup();
        group.add(handButton);
        group.add(carriedButton);
        
        c.gridx = 0;
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(handButton);
        radioPanel.add(carriedButton);       
        getContentPane().add(radioPanel, c);
        radioPanel.setVisible(false);
        c.gridx = 1;				
		
        // if the victim has no cards in the location where the card may be taken from, the taker gets nothing.
        // If so inform the players and show only button to close dialog.
		if (location == DrawLocation.HAND) {			
			currentLocation = DrawLocation.HAND;
			
			if (victim.getHandCards().size() == 0) {
				String resultMsg = taker.getName() + " gets nothing.";
				if (isDiscard)
					resultMsg = "nothing to discard.";
				infoLabel.setText(victim.getName() + " ran out of cards; " + resultMsg);
				c.gridy++;
				getContentPane().add(doneButton, c);
				doneButton.setEnabled(true);
			}
			else
				handButton.doClick();
		}
		else if (location == DrawLocation.CARRIED) {
			currentLocation = DrawLocation.CARRIED;
			
			if (victim.getAllItems().size() == 0) {
				String resultMsg = taker.getName() + " gets nothing.";
				if (isDiscard)
					resultMsg = "nothing to discard.";
				infoLabel.setText(victim.getName() + " ran out of cards, " + resultMsg);
				c.gridy++;
				doneButton.setEnabled(true);
				getContentPane().add(doneButton, c);
			}
			else
				carriedButton.doClick();
		}
		else if (location == DrawLocation.EITHER || location == DrawLocation.TREASURE) {
			radioPanel.setVisible(true);
			
			if (location == DrawLocation.TREASURE) {
				int numTreasureCards = 0;
				for (Card handCard : victim.getHandCards())
					if (handCard instanceof TreasureCard)
						numTreasureCards++;
				
				if (numTreasureCards == 0)
					handButton.setEnabled(false);
			}			
			else if (victim.getHandCards().size() == 0)
				handButton.setEnabled(false);
			
			if (victim.getAllItems().size() == 0)
				carriedButton.setEnabled(false);
			if (location == DrawLocation.TREASURE && victim.hasHireling())
				carriedButton.setEnabled(true);
			
			if (!handButton.isEnabled() && !carriedButton.isEnabled()) {
				String resultMsg = taker.getName() + " gets nothing.";
				if (isDiscard)
					resultMsg = "nothing to discard.";
				infoLabel.setText(victim.getName() + " ran out of cards, " + resultMsg);
				c.gridy++;
				doneButton.setEnabled(true);
				getContentPane().add(doneButton, c);
			}
			else if (handButton.isEnabled())
				handButton.doClick();
			else
				carriedButton.doClick();
		}
		
		if (isDiscard && !taker.isComputer()) {
			warningLabel.setVisible(false);
			takeButton.setText("Discard");
		}
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (taker.isComputer()) {
			double playerFactor = 1.0;
			if (taker.getPlayerType() == PlayerType.COMPUTER_HARD) {
				playerFactor = GM.getPlayers().size() - AIManager.getRankedPlayers().indexOf(victim);
				playerFactor /= (double)GM.getPlayers().size();
				playerFactor *= 2.0;
			}
			
			// Get list of all possible cards that can be taken, sorted from greatest to least value
			// Base card values on whether or not you can see the card, personal benefit, and a factored victim cost
			LinkedList<AIValuedCard> mostValuedCards = new LinkedList<AIValuedCard>();
			if (location == DrawLocation.HAND && !isHandVisible) {
				// Take random card or nothing if hand is empty
				if (!victim.getHandCards().isEmpty()) {
					Card cardToTake = victim.getHandCards().get(Randomizer.getRandom(victim.getHandCards().size()));
					if (isDiscard) {
						victim.discard(cardToTake);
						String message = taker + " chose to discard " + victim + "'s " + cardToTake + " card.";
						Messenger.display(message, "Take A Card");
					}
					else {
						String cardType = "door";
						if (cardToTake instanceof TreasureCard)
							cardType = "treasure";
						String message = taker + " chose to take one of " + victim + "'s " + cardType + " cards.";
						Messenger.display(message, "Take A Card");
						victim.getHandCards().remove(cardToTake);
						taker.getHandCards().add(cardToTake);
						AIManager.playHandCard(taker, cardToTake);
					}
				}				
				
				return;
			}
			
			if (location != DrawLocation.CARRIED) {
				for (Card handCard : victim.getHandCards()) {
					if (location == DrawLocation.TREASURE && !(handCard instanceof TreasureCard))
						continue;
					
					int cardValue = 0;
					if (!isDiscard) {
						cardValue = AIManager.UNKNOWN_CARD_VALUE;
						if (isHandVisible)
							cardValue = AICardEvaluator.getCardValueToPlayer(handCard, taker, taker.getHandCards());
					}
					
					// only evaluate card value to victim, if victim is not dead and taker is a medium/hard computer
					if (location != DrawLocation.EITHER) {
						if (taker.getPlayerType() == PlayerType.COMPUTER_MEDIUM || 
							taker.getPlayerType() == PlayerType.COMPUTER_HARD) {
							
							if (isHandVisible)
								cardValue += AICardEvaluator.getCardValueToPlayer(handCard, victim, victim.getHandCards()) * playerFactor;
							else
								cardValue += AIManager.UNKNOWN_CARD_VALUE * playerFactor;
						}
					}
					else if (isDiscard) {
						if (isHandVisible)
							cardValue += AICardEvaluator.getCardValueToPlayer(handCard, victim, victim.getHandCards()) * playerFactor;
						else
							cardValue += AIManager.UNKNOWN_CARD_VALUE * playerFactor;
					}
					
					AIValuedCard valuedCard = new AIValuedCard(handCard, cardValue);
					int cardIdx;
					for (cardIdx = 0; cardIdx < mostValuedCards.size(); cardIdx++)
						if (cardValue > mostValuedCards.get(cardIdx).getValue())
							break;
					mostValuedCards.add(cardIdx, valuedCard);
				}
			}
			
			if (location != DrawLocation.HAND) {
				for (Card item : victim.getAllItems()) {
					int itemValue = 0;
					if (!isDiscard)
						itemValue = AICardEvaluator.getCardValueToPlayer(item, taker, taker.getHandCards());
					
					// only evaluate card value to victim, if victim is not dead and taker is a medium/hard computer
					if (location != DrawLocation.EITHER) {
						if (taker.getPlayerType() == PlayerType.COMPUTER_MEDIUM || 
							taker.getPlayerType() == PlayerType.COMPUTER_HARD) {
							
							LinkedList<Card> handCards = null;
							if (isHandVisible)
								handCards = victim.getHandCards();
							itemValue += AICardEvaluator.getCardValueToPlayer(item, victim, handCards) * playerFactor;
						}
					}
					else if (isDiscard) {
						LinkedList<Card> handCards = null;
						if (isHandVisible)
							handCards = victim.getHandCards();
						itemValue += AICardEvaluator.getCardValueToPlayer(item, victim, handCards) * playerFactor;
					}
					
					AIValuedCard valuedCard = new AIValuedCard(item, itemValue);
					int cardIdx;
					for (cardIdx = 0; cardIdx < mostValuedCards.size(); cardIdx++)
						if (itemValue > mostValuedCards.get(cardIdx).getValue())
							break;
					mostValuedCards.add(cardIdx, valuedCard);
				}
			}
			
			if ((location == DrawLocation.EITHER || location == DrawLocation.TREASURE) && victim.hasHireling()) {
				int cardValue = 0;
				if (!isDiscard)
					cardValue = AICardEvaluator.getCardValueToPlayer(victim.getHirelingCard(), taker, taker.getHandCards());
				
				// only evaluate card value to victim, if victim is not dead and taker is a medium/hard computer
				if (location != DrawLocation.EITHER) {
					if (taker.getPlayerType() == PlayerType.COMPUTER_MEDIUM || 
						taker.getPlayerType() == PlayerType.COMPUTER_HARD) {
						
						LinkedList<Card> handCards = null;
						if (isHandVisible)
							handCards = victim.getHandCards();
						cardValue += AICardEvaluator.getCardValueToPlayer(victim.getHirelingCard(), victim, handCards) * playerFactor;
					}
				}
				else if (isDiscard) {
					LinkedList<Card> handCards = null;
					if (isHandVisible)
						handCards = victim.getHandCards();
					cardValue += AICardEvaluator.getCardValueToPlayer(victim.getHirelingCard(), victim, handCards) * playerFactor;
				}
				
				AIValuedCard valuedCard = new AIValuedCard(victim.getHirelingCard(), cardValue);
				int cardIdx;
				for (cardIdx = 0; cardIdx < mostValuedCards.size(); cardIdx++)
					if (cardValue > mostValuedCards.get(cardIdx).getValue())
						break;
				mostValuedCards.add(cardIdx, valuedCard);
			}
			
			if (mostValuedCards.isEmpty())
				return;
			
			Card cardToTake = mostValuedCards.getFirst().getCard();
			
			if (isDiscard) {
				victim.discard(cardToTake);
				String message = taker + " chose to discard " + victim + "'s " + cardToTake + " card";
				Messenger.display(message, "Take A Card");
				return;
			}
			
			String message = taker + " chose to take " + victim + "'s " + cardToTake + " card";
			Messenger.display(message, "Take A Card");
			if (location == DrawLocation.HAND) {
				victim.getHandCards().remove(cardToTake);
				taker.getHandCards().add(cardToTake);
				AIManager.playHandCard(taker, cardToTake);
			}
			else if (location == DrawLocation.CARRIED) {
				if (!victim.removeEquipmentItem(cardToTake))
					if (!victim.getCarriedItems().remove(cardToTake))
						victim.setHirelingCard(null);
				taker.addItem((TreasureCard)cardToTake);
			}
			else {
				if (!victim.getHandCards().remove(cardToTake))
					if (!victim.removeEquipmentItem(cardToTake))
						if (!victim.getCarriedItems().remove(cardToTake))
							victim.setHirelingCard(null);
				taker.getHandCards().add(cardToTake);
				AIManager.playHandCard(taker, cardToTake);
			}
		}
		else
			super.setVisible(b);
	}
	
	/**
	 * If the taker is not allowed to see the cards when choosing from them, this method hides the card info from
	 * being displayed by overriding the display methods.
	 * @param cards the list of cards that can be taken
	 */
	private void setHiddenCardPanel(LinkedList<Card> cards) {
		cardPanel = new CardPanel(cards, 2, takeButton, doneButton) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected JLabel addNewImageLabel(Card card) {
				Image image = null;
				if (card instanceof DoorCard)
					image = new ImageIcon(GUI.class.getResource("images/DoorCard.jpg")).getImage();
				else
					image = new ImageIcon(GUI.class.getResource("images/TreasureCard.jpg")).getImage();
				
				return addNewImageLabel(image);
			}
			
			@Override
			protected void setSelectedImageLabel(JLabel imageLabel) {
				super.setSelectedImageLabel(imageLabel);
				
				Card card = getSelectedCard();
				if (card instanceof DoorCard)
					mainImageLabel.setIcon(new ImageIcon(GUI.class.getResource("images/DoorCard.jpg")));
				else
					mainImageLabel.setIcon(new ImageIcon(GUI.class.getResource("images/TreasureCard.jpg")));
			}
		};
	}
}
