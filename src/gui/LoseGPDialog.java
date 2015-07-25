
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;
import gui.components.MultiSelectCardPanel;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JLabel;

import ai.AIManager;

import model.Player;
import model.card.Card;
import model.card.TreasureCard;

/**
 * Dialog that allows a Player to discard a certain GP value's worth of Items.
 */
public class LoseGPDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// GUI displays and controls
	private MultiSelectCardPanel cardPanel;
	private CustomButton discardButton;
	
	// Player who is discarding Items
	private Player player;
	// minimum GP value that player must discard
	private int minValue;
	// whether or not a discard was made (after one card is discarded, there is no way to back out)
	private boolean madeDiscard;
	// the current total value of the discarded Items
	private int totalValueDiscarded;
	
	/**
	 * Creates a new LoseGPDialog dialog.
	 * @param p Player who is discarding Items
	 * @param minimum minimum GP value that must be discarded
	 */
	public LoseGPDialog(Player p, int minimum) {
		super("Lose GP");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// initialize variables and display GUI controls
		player = p;
		minValue = minimum;
		madeDiscard = false;
		totalValueDiscarded = 0;	
		
		if (player.isComputer()) {			
			// Determine lowest worth cards of given GP value and discard them			
			LinkedList<TreasureCard> itemsToDiscard = AIManager.getLeastValuedGPItemList(player.getAllItems(), minValue, player, player.getHandCards());
			if (itemsToDiscard == null || itemsToDiscard.isEmpty()) {
				String message = player + " does not have at least " + minValue + " GP worth of items.";
				Messenger.display(message, "Lose GP");
				return;
			}
			else
				madeDiscard = true;
			
			String discardList = "";
			for (TreasureCard item : itemsToDiscard) {
				player.discard(item);
				totalValueDiscarded += item.getValue();
				discardList += "\n" + item;
			}
			
			String message = player + " discarded the following item(s) to meet the required " + minValue + "GP demand:";
			message += discardList;
			Messenger.display(message, "Lose GP");
			
			madeDiscard = true;
			return;
		}
		
		JLabel infoLabel = new JLabel(player.getName() + ": Discard at least " + minValue + " GP worth of items.");
		getContentPane().add(infoLabel, c);
		
		// Calculate the maximum total GP value of all Items the Player can discard
		int maxValue = 0;
		final LinkedList<TreasureCard> itemCards = player.getAllItems();		
		Iterator<TreasureCard> itemCardIter = itemCards.iterator();
		while(itemCardIter.hasNext())
			maxValue += itemCardIter.next().getValue();
		
		// Inform Player if he does not have enough Item GP worth to cover the minimum cost and 
		// display only button to close dialog.
		if (itemCards.isEmpty() || maxValue < minValue) {
			infoLabel.setText(player.getName() + " does not have at least " + minValue + " GP worth of items.");
			getContentPane().add(infoLabel, c);
			
			c.gridy++;
			CustomButton okButton = new CustomButton("OK") {
				private static final long serialVersionUID = 1L;
				
				public void buttonPressed() {
					dispose();
				}
			};
			getContentPane().add(okButton, c);
			setJMenuBar(null);
			
			refresh();			
			return;
		}
		
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(itemCards);
		
		// Lets the Player discard an Item
		discardButton = new CustomButton("Discard") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Iterator<Card> cardIter = cardPanel.getCheckedCards().iterator();
				while (cardIter.hasNext()) {
					Card card = cardIter.next();
					player.discard(card);
					madeDiscard = true;
					totalValueDiscarded += ((TreasureCard)card).getValue();
				}					

				dispose();
			}
		};
		discardButton.setEnabled(false);
		
		c.gridy++;
		cardPanel = new MultiSelectCardPanel(cards, 3, discardButton, null) {
			private static final long serialVersionUID = 1L;
			
			public void setCheckedInfoText() {
				int totalValue = 0;
				
				Iterator<Card> itemIter = getCheckedCards().iterator();
				while (itemIter.hasNext()) {
					totalValue += ((TreasureCard)itemIter.next()).getValue();
				}
				
				// if the player has discarded enough Items to cover the minimum GP cost, disable any further discards
				if (getCheckedCards().size() > 0 && totalValue >= minValue)
					discardButton.setEnabled(true);
				else
					discardButton.setEnabled(false);
				
				checkedInfoLabel.setText("Total Selected Value: " + totalValue + "GP");
			}
		};
		getContentPane().add(cardPanel, c);

		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!player.isComputer())
			super.setVisible(b);
	}
		
	/**
	 * Return whether or not the Player discarded any Items.
	 * @return true if an Item was discarded; false otherwise
	 */
	public boolean hasDiscardedItems() {
		return madeDiscard;
	}
	
	/**
	 * Return the total GP value of all discarded Items.
	 * @return the total GP value of all discarded Items
	 */
	public int getTotalValueDiscarded() {
		return totalValueDiscarded;
	}
}
