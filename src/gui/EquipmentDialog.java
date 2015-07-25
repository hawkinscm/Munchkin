
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

import model.Player;
import model.card.Card;
import model.card.EquipmentCard;

/**
 * Dialog for displaying, equipping, and unequipping Equipment Items.
 */
public class EquipmentDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Panels for both currently equipped and currently unequipped Equipment Items
	private CardPanel equippedItemsPanel;
	private CardPanel unequippedItemsPanel;
	
	// Player whose equipment this is
	private Player player;
	
	/**
	 * Creates a new EquipmentDialog dialog.
	 * @param gui reference to the main controlling GUI
	 * @param p player whose equipment this is
	 */
	public EquipmentDialog(final GUI gui, final Player p) {
		super(gui, p.getName() + "'s Equipment");
		
		// initialize variables and display GUI controls
		player = p;
		
		final LinkedList<EquipmentCard> equippedItems = player.getEquippedItems();
		final LinkedList<EquipmentCard> unequippedItems = player.getUnequippedItems();
		final LinkedList<Card> equippedItemsCopy = new LinkedList<Card>();
		equippedItemsCopy.addAll(equippedItems);
		final LinkedList<Card> unequippedItemsCopy = new LinkedList<Card>();
		unequippedItemsCopy.addAll(unequippedItems);
			
		// If the player has no Equipment Items, display message and Done Button to close the Dialog
		if (equippedItems.isEmpty() && unequippedItems.isEmpty()) {
			c.gridwidth = 2;
			JLabel infoLabel = new JLabel(player + " has no equipment.");
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
				
		// Allows the player to unequip equipped Items
		CustomButton unequipButton = new CustomButton("Unequip") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {				
				Card card = equippedItemsPanel.getSelectedCard();
				player.unequip((EquipmentCard)card);
				equippedItemsCopy.remove(card);
				equippedItemsPanel.removeSelectedImage();
				
				unequippedItemsPanel.addCard(card);
				refresh();
			}
		};
		equippedItemsPanel = new CardPanel(equippedItemsCopy, 2, unequipButton, null);
		Border linedBorder = BorderFactory.createLineBorder(Color.BLACK);
		equippedItemsPanel.setBorder(BorderFactory.createTitledBorder(linedBorder, "Equipped"));
		getContentPane().add(equippedItemsPanel, c);
		
		
		c.gridx++;
		// Allows player to equip unequipped Items
		CustomButton equipButton = new CustomButton("Equip") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Card card = unequippedItemsPanel.getSelectedCard();
				String noEquipReason = player.equip((EquipmentCard)card);
				if (noEquipReason != "")
					Messenger.display(noEquipReason, "Can't Equip Card");
				else {
					unequippedItemsCopy.remove(card);
					unequippedItemsPanel.removeSelectedImage();
					
					equippedItemsPanel.addCard(card);
					refresh();
				}
			}
		};		
		unequippedItemsPanel = new CardPanel(unequippedItemsCopy, 2, equipButton, null);
		unequippedItemsPanel.setBorder(BorderFactory.createTitledBorder(linedBorder, "Unequipped"));
		getContentPane().add(unequippedItemsPanel, c);
		
		refresh();
		
		if (player.isComputer()) {
			unequipButton.setEnabled(false);
			equipButton.setEnabled(false);
		}
	}
}
