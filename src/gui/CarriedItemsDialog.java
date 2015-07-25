
package gui;

import java.util.LinkedList;

import javax.swing.JLabel;

import exceptions.EndGameException;
import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;

import model.CardPlayManager;
import model.Player;
import model.card.Card;

/**
 * Dialog that displays all of a player's carried items for viewing/playing.
 */
public class CarriedItemsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card panel that displays all of the player's carried Item Cards
	private CardPanel itemsPanel;
	
	// The selected player
	private Player player;
	
	/**
	 * Creates a new CarriedItemsDialog Dialog.
	 * @param gui reference to the main controlling GUI
	 * @param p Selected player whose items will be shown
	 */
	public CarriedItemsDialog(final GUI gui, final Player p) {
		super(gui, p.getName() + "'s Carried Items");
		
		player = p;
		
		final LinkedList<Card> items = new LinkedList<Card>();
		items.addAll(player.getCarriedItems());
			
		// If the player has no items, inform them and display only the Done Button.
		if (items.isEmpty()) {
			if (player.isComputer())
				return;
			
			c.gridwidth = 2;
			JLabel infoLabel = new JLabel(player + " has no carried items.");
			getContentPane().add(infoLabel, c);
			c.gridwidth = 1;
			
			c.gridy++;
			c.gridx++;
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
				
		// Button to let the player play his card
		CustomButton playButton = new CustomButton("Play") {
			private static final long serialVersionUID = 1L;
				
			public void buttonPressed() {
				Card card = itemsPanel.getSelectedCard();
				try {
					if (CardPlayManager.playCard(player, card)) {
						items.remove(card);
						itemsPanel.removeSelectedImage();
						
						if (items.isEmpty())
							dispose();
					}
				}
				catch (EndGameException ex) { 
					dispose();
					gui.endGame();
				}
			}
		};
		itemsPanel = new CardPanel(items, 3, playButton, null);
		getContentPane().add(itemsPanel, c);		

		if (player.isComputer())
			playButton.setEnabled(false);
		
		refresh();
	}
}
