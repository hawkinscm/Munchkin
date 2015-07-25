
package gui;

import java.util.LinkedList;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;

import model.GM;
import model.card.Card;

/**
 * Dialog for viewing Cards.
 */
public class DisplayCardsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	private CardPanel viewPanel;
	
	// List of cards to view
	private LinkedList<Card> displayedCards;
	
	/**
	 * Creates a new DisplayCardsDialog dialog for viewing a single card.
	 * @param card Card to view
	 */
	public DisplayCardsDialog(Card card) {
		super("View Card");
		displayedCards = new LinkedList<Card>();
		displayedCards.add(card);
		
		initialize();
	}
	
	/**
	 * Creates a new DisplayCardsDialog dialog for viewing a single card with a specified dialog title.
	 * @param card card to view
	 * @param title text for the dialog window title
	 */
	public DisplayCardsDialog(Card card, String title) {
		super(title);
		displayedCards = new LinkedList<Card>();
		displayedCards.add(card);
		
		initialize();
	}
	
	/**
	 * Creates a new DisplayCardsDialog dialog for viewing a list of cards with a specified dialog title.
	 * @param cards list of cards to view
	 * @param title text for the dialog window title
	 */
	public DisplayCardsDialog(LinkedList<Card> cards, String title) {
		super(title);
		displayedCards = cards;
		
		initialize();
	}
	
	/**
	 * Initializes and displays the GUI controls.
	 */
	private void initialize() {		
		CustomButton doneButton = new CustomButton("Done") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				dispose();
			}
		};
		
		viewPanel = new CardPanel(displayedCards, 3, doneButton, null);
		getContentPane().add(viewPanel);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!GM.isTestRun())
			super.setVisible(b);
	}
	
	/**
	 * Sets the selected and main panel displayed card.
	 * @param card card to select and and display
	 */
	public void setSelectCard(Card card) {
		viewPanel.setSelectedImageLabel(card);
	}
}
