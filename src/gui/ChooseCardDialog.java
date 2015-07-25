
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;

import java.awt.GridBagConstraints;
import java.util.LinkedList;

import javax.swing.JLabel;

import model.card.Card;

/**
 * Dialog that displays a list of cards for a player to choose from.
 */
public class ChooseCardDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card Panel that displays the cards
	private CardPanel cardPanel;
	
	// The card that is selected
	private Card selectedCard;
	
	/**
	 * Creates a new ChooseCardDialog Dialog.
	 * @param cards list of cards to choose from
	 * @param prompt text that prompts the user what to do and why
	 */
	public ChooseCardDialog(LinkedList<Card> cards, String prompt) {
		super("Choose Card");
		
		// initializes variables and displays GUI controls
		selectedCard = null;
		
		// determines how best to display the cards in a table format (row, column) based on the total number of cards.
		int numColumns = (cards.size() - 1 / 3) + 1;
		if (numColumns < 2)
			numColumns = 2;
		if (numColumns > 5)
			numColumns = 5;
		
		c.anchor = GridBagConstraints.WEST;
		final JLabel infoLabel = new JLabel(prompt);
		getContentPane().add(infoLabel, c);
		c.anchor = GridBagConstraints.CENTER;
		
		CustomButton selectButton = new CustomButton("Select") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				selectedCard = cardPanel.getSelectedCard();
				dispose();
			}
		};
		
		c.gridy++;
		cardPanel = new CardPanel(cards, numColumns, selectButton, null); 
		getContentPane().add(cardPanel, c);		
		
		refresh();
	}
	
	/**
	 * Returns the selected card.
	 * @return the selected card
	 */
	public Card getSelectedCard() {
		return selectedCard;
	}
}
