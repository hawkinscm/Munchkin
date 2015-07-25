
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;

import ai.AIManager;
import ai.AIValuedCard;

import exceptions.EndGameException;
import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import model.CardPlayManager;
import model.GM;
import model.Player;
import model.PlayerType;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.RaceCard;

/**
 * Dialog for the Charity stage of the game, where players must donate the excess cards in their hands to 
 * players of lower levels.
 */
public class CharityDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
		
	// Card Panel for the cards in the active player's hand
	private CardPanel cardPanel;
	// Button Group and options for selecting to play or discard a card
	private ButtonGroup optionGroup;
	private JRadioButton playOption;
	private JRadioButton discardOption;
	
	// the number of cards left in hand which are in excess of what the player is allowed
	private int numDiscardsLeft;
	// whether or not the player is currently a dwarf
	private boolean isDwarf = false;
	// List of the cards in the current player's hand
	private LinkedList<Card> handCards;
	// List of the cards discarded for charity
	private LinkedList<Card> charityCards;
			
	/**
	 * Creates a new CharityDialog Dialog.
	 * @param player the active player
	 */
	public CharityDialog(final Player player) {
		super("Charity Phase");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// initialize variables and display GUI controls
		handCards = player.getHandCards();
		charityCards = new LinkedList<Card>();
		
		numDiscardsLeft = handCards.size() - 5;
		if (player.isDwarf()) {
			numDiscardsLeft--;
			isDwarf = true;
		}
		
		if (player.isComputer()) {
			// Determine which cards to dispose of and whether to play them or throw them away.
			LinkedList<AIValuedCard> leastValuedCards = AIManager.getLeastValuedHandCards(player, player.getHandCards());
			while (leastValuedCards.size() > numDiscardsLeft)
				leastValuedCards.removeLast();
			
			if (player.getPlayerType() != PlayerType.COMPUTER_EASY) {
				Iterator<AIValuedCard> valuedCardIter = leastValuedCards.iterator();
				while (valuedCardIter.hasNext()) {
					AIValuedCard valuedCard = valuedCardIter.next();
					
					if (valuedCard.getCard() instanceof RaceCard) {
						continue;
					}
					else if (valuedCard.getCard() instanceof ClassCard) {
						continue;
					}
					else if (valuedCard.getCard() instanceof CurseCard) {
						CurseCard curse = (CurseCard)valuedCard.getCard();
						LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
						rankedPlayers.remove(player);
						for (Player victim : rankedPlayers) {
							if (AIManager.getCurseCost(curse, victim, null) > 0 || rankedPlayers.getLast() == victim) {
								String message = player + " plays the " + curse + " curse on " + victim + ".";
								Messenger.display(message, "Card From Hand Played");
								player.getHandCards().remove(curse);
								valuedCardIter.remove();
								CurseDialog curseDialog = new CurseDialog(victim, curse, false);
								curseDialog.setVisible(true);
								break;
							}
						}
					}
					else {
						try {
							if (CardPlayManager.playCard(player, valuedCard.getCard()))
								valuedCardIter.remove();
						} 
						catch (EndGameException ex) {}
					}
				}				
			}
			
			String discardList = "";
			for (AIValuedCard valuedCard : leastValuedCards) {
				if (player.getHandCards().remove(valuedCard.getCard())) {
					charityCards.add(valuedCard.getCard());
					discardList += "\n" + valuedCard.getCard();
				}
			}
			if (discardList.length() > 1) {
				String message = player + " gave the following item(s) to charity:" + discardList;
				Messenger.display(message, "Charity");
			}
						
			return;
		}
		
		c.gridwidth = 2;
		String infoText = player.getName() + ", select " + numDiscardsLeft;
		if (numDiscardsLeft == 1)
			infoText += " card ";
		else
			infoText += " cards ";
		infoText += "to play or give to charity.";
		final JLabel infoLabel = new JLabel(infoText);
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		// Button that will display and handle either the playing or discarding of a card, 
		// based on the radio button selected.
		final CustomButton actionButton = new CustomButton("Play") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				
				int handCardCount = player.getHandCards().size();
				String action = "";
				Card card = cardPanel.getSelectedCard();
				if (optionGroup.isSelected(playOption.getModel())) {
					try {
						if (!CardPlayManager.playCard(player, card))
							return;
					}
					catch (EndGameException ex) {}
					action = "Played";
					
					if (isDwarf && !player.isDwarf()) {
						isDwarf = false;
						numDiscardsLeft++;
					}
					else if (!isDwarf && player.isDwarf()) {
						isDwarf = true;
						numDiscardsLeft--;
					}
				}					
				else
				{
					handCards.remove(card);
					charityCards.add(card);
					action = "Discarded";
				}
				numDiscardsLeft -= (handCardCount - player.getHandCards().size());
				
				if (numDiscardsLeft > 0) {
					String infoText = action + " \"" + card + "\". ";
					infoText += "Select " + numDiscardsLeft + "  more to play or give to charity.";
					infoLabel.setText(infoText);
					cardPanel.removeSelectedImage();
				}
				else
					dispose();
			}
		};
		
		c.gridy++;
		// Panel and controls for allowing a player to select whether he will play or discard the selected card
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		optionGroup = new ButtonGroup();
		playOption = new JRadioButton("Play");
		playOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionButton.setText("Play");
			}
		});
		discardOption = new JRadioButton("Discard");
		discardOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionButton.setText("Discard");
			}
		});
		optionGroup.add(playOption);
		optionGroup.add(discardOption);
		optionPanel.add(playOption);
		optionPanel.add(discardOption);
		add(optionPanel, c);
		playOption.setSelected(true);
		
		c.gridx++;
		// Card Panel that displays the cards in the active player's hand
		cardPanel = new CardPanel(handCards, 3, actionButton, null);
		getContentPane().add(cardPanel, c);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!GM.getActivePlayer().isComputer())
			super.setVisible(b);
	}
		
	/**
	 * Returns the list of discarded cards.
	 * @return the list of discarded cards
	 */
	public LinkedList<Card> getCharityCards() {
		return charityCards;
	}
}
