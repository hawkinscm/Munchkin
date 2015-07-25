
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JLabel;

import ai.AICardEvaluator;
import ai.AIManager;

import model.GM;
import model.Player;
import model.card.Card;
import model.card.CurseCard;
import model.card.ItemCard;

/**
 * Dialog for handling Curse Cards played on a player.
 */
public class CurseDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// The player and curse played upon them
	private Player player;
	private CurseCard curse;
	
	// whether or not the curse was removed without taking effect
	private boolean removedCurse;
	// holds the player's wishing ring, if holding one
	private ItemCard wishingRing;
	
	// Buttons for using the wishing ring and closing the dialog
	private CustomButton wishButton;
	private CustomButton endButton;
	
	/**
	 * Creates a new CurseDialog Dialog.
	 * @param p Player on whom the Curse Card has been played
	 * @param card the Curse Card
	 * @param fromOpeningDoor true if Curse Card came from opening a door; false otherwise
	 */
	public CurseDialog(Player p, CurseCard card, boolean fromOpeningDoor) {
		super("Curse");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// initialize variables and display GUI control 
		player = p;
		curse = card;
		
		removedCurse = false;
		wishingRing = null;
		
		// Find and store Wishing Ring if player has one
		Iterator<ItemCard> itemIter = player.getCarriedItems().iterator();
		while (itemIter.hasNext()) {
			ItemCard item = itemIter.next();
			if (item.getID() == Card.I_WISHING_RING_1 || item.getID() == Card.I_WISHING_RING_2) {
				wishingRing = item;
				break;
			}
		}
		
		// Display status and information about player and curse
		c.gridwidth = 2;
		final JLabel imageLabel = new JLabel(player.getName() + ", you have been cursed.", curse.getPicture(), JLabel.CENTER);
		imageLabel.setHorizontalTextPosition(JLabel.CENTER);
		imageLabel.setVerticalTextPosition(JLabel.TOP);
		getContentPane().add(imageLabel, c);
		c.gridwidth = 1;
		
		// If wearing Sandals, ignore curse
		if (fromOpeningDoor && player.hasEquipped(Card.E_SANDALS_OF_PROTECTION)) {
			imageLabel.setText("You kicked away this curse with the Sandals of Protection.");
			removedCurse = true;		
		}
			
		// Allow player to use Wishing Ring to ignore curse, if holding one and curse not already ignored
		c.gridy++;
		wishButton = new CustomButton("Use Wishing Ring") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				imageLabel.setText("You wished away the curse.");
				player.discard(wishingRing);
				removedCurse = true;
				wishButton.setVisible(false);
				endButton.setText("OK");
			}
		};
		getContentPane().add(wishButton, c);
		
		if (removedCurse || wishingRing == null)
			wishButton.setVisible(false);
		
		c.gridx++;
		// Gives player option to accept bad effects of curse (may be the only option)
		endButton = new CustomButton("Accept Curse") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				if (!removedCurse) {
					curse.addEffects(player);
					if (!curse.isLastingCurse())
						GM.getDoorDeck().discard(curse);
				}
				else
					GM.getDoorDeck().discard(curse);
				
				dispose();
			}
		};
		getContentPane().add(endButton, c);
		
		// Once curse has been ignored or accepted, allows player to end dialog
		if (removedCurse)
			endButton.setText("OK");
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (player.isComputer()) {
			if (removedCurse) {
				String message = player + " kicked away this curse with the Sandals of Protection.";
				Messenger.display(message, curse + " Curse");
				GM.getDoorDeck().discard(curse);
				return;
			}
			
			if (wishingRing != null && curse.getID() != Card.CU_INCOME_TAX) {
				int ringValue = AICardEvaluator.getCardValueToPlayer(wishingRing, player, player.getHandCards());
				if (player.hasEquipped(Card.E_SANDALS_OF_PROTECTION))
					ringValue /= 2;
				if (ringValue < AIManager.getCurseCost(curse, player, player.getHandCards())) {
					player.discard(wishingRing);
					GM.getDoorDeck().discard(curse);
					String message = player + " used a Wishing Ring to wish away the " + curse + " curse.";
					Messenger.display(message, "Item Card Used");
					return;
				}					
			}
			
			String message = player + " suffered the effects of the " + curse + " curse.";
			Messenger.display(message, "Curses!");
			
			curse.addEffects(player);
			if (!curse.isLastingCurse())
				GM.getDoorDeck().discard(curse);			
		}
		else
			super.setVisible(b);
	}
}
