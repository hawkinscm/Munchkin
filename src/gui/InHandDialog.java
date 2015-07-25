
package gui;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;

import exceptions.EndGameException;
import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;

import model.Battle;
import model.CardPlayManager;
import model.GM;
import model.Player;
import model.card.Card;
import model.card.MonsterCard;

/**
 * Dialog for displaying and playing cards from a Player's Hand.
 */
public class InHandDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card Panel for displaying the cards
	private CardPanel cardPanel;
	
	// Player whose Hand is displayed
	private Player player;
	// List of Cards in Player's hand
	private LinkedList<Card> handCards;
	// Battle, if any, that is currently in progress
	private Battle battle;
	
	/**
	 * Creates a new InHandDialog dialog for the given Player.
	 * @param p Player whose hand is displayed
	 */
	public InHandDialog(Player p) {
		super(p.getName() + "'s Hand");		
		
		// initialize variables
		player = p;
		handCards = new LinkedList<Card>();
		handCards.addAll(player.getHandCards());
		
		// if player has no cards, inform user and display nothing
		if (handCards.isEmpty()) {
			JLabel infoLabel = new JLabel("You have no cards in your hand.");
			getContentPane().add(infoLabel, c);
			refresh();
			
			return;
		}
		
		initialize();
	}
	
	/**
	 * Creates a new InHandDialog dialog for the given Player in the given Battle.
	 * @param p Player whose hand is displayed
	 * @param b Battle that is currently ongoing
	 */
	public InHandDialog(Player p, Battle b) {
		super(p.getName() + "'s Hand");		
		
		// initialize variables
		player = p;
		battle = b;
		handCards = new LinkedList<Card>();
		handCards.addAll(player.getHandCards());
		
		// if player has no cards, inform user and display nothing
		if (handCards.isEmpty()) {
			JLabel infoLabel = new JLabel("You have no cards in your hand.");
			getContentPane().add(infoLabel, c);
			setJMenuBar(null);
			refresh();
			
			return;
		}
		
		initialize();
	}
	
	/**
	 * Creates a new InHandDialog dialog displaying only the player's new Card.
	 * @param p Player whose new Card is displayed
	 * @param newCard the new Card to display
	 */
	public InHandDialog(Player p, Card newCard) {
		super(p.getName() + "'s Newly Acquired Card");
		
		// initialize variables
		player = p;
		handCards = new LinkedList<Card>();
		handCards.add(newCard);
		
		initialize();
	}
	
	/**
	 * Creates a new InHandDialog dialog displaying only the player's new Cards.
	 * @param p Player whose new Cards are displayed
	 * @param newCards list of new Cards to display
	 */
	public InHandDialog(Player p, LinkedList<Card> newCards) {
		super(p.getName() + "'s Newly Acquired Card(s)");
		
		// initialize variables
		player = p;
		handCards = newCards;
		
		initialize();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!player.isComputer())
			super.setVisible(b);
	}
	
	/**
	 * Initializes variables and displays GUI controls
	 */
	private void initialize() {
		if (player.isComputer())
			return;
		
		// Button to allow the Player to play the selected Card
		final CustomButton playButton = new CustomButton("Play") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				try {
					Card card = cardPanel.getSelectedCard();
					if (CardPlayManager.playCard(player, card, battle)) {
						handCards.remove(card);
						Iterator<Card> handCardIter = handCards.iterator();
						while (handCardIter.hasNext())
							if (!player.getHandCards().contains(handCardIter.next()))
								handCardIter.remove();						
						
						cardPanel.removeSelectedImage();
						if (handCards.isEmpty())
							dispose();						
					}
				}
				catch (EndGameException ex) {
					GM.endGame();
					dispose();
				}
			}
		};
		
		// Button to close the dialog when done viewing/playing Cards
		CustomButton doneButton = new CustomButton("Done") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		
		cardPanel = new CardPanel(handCards, 5, playButton, doneButton) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void setSelectedImageLabel(JLabel imageLabel) {
				super.setSelectedImageLabel(imageLabel);
				playButton.setEnabled(false);
				if (getSelectedCard() != null)
					if (!(getSelectedCard() instanceof MonsterCard))
						playButton.setEnabled(true);
			}
		}; 
		getContentPane().add(cardPanel, c);		
		
		refresh();
	}
}
