
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ai.AIManager;

import model.Battle;
import model.CardPlayManager;
import model.GM;
import model.Player;
import model.card.Card;
import model.card.EquipmentCard;

/**
 * Dialog that handles playing the Help Me Out Card.
 */
public class HelpMeOutDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Battle that is underway and player using the Help Me Out Card
	private Battle battle;
	private Player player;
	
	// whether or not the player selected an item to take and used the Card
	private boolean tookItem;
	// The Equipment Items that the player player requested be removed as part of using the Card
	private LinkedList<EquipmentCard> removeItems;
	
	// GUI controls for displaying info and receiving user input
	private JComboBox playerBox;
	private CustomButton takeButton;
	private CardPanel equipmentCardPanel;
	private JLabel noUseLabel;
	
	/**
	 * Creates a new HelpMeOutDialog dialog.
	 * @param b Battle that is currently underway
	 * @param p Player that is using the Help Me Out Card
	 */
	public HelpMeOutDialog(Battle b, Player p) {
		super("Help Me Out");
		
		// initialize variables and display GUI controls
		noUseLabel = new JLabel();
		tookItem = false;
		removeItems = new LinkedList<EquipmentCard>();
		
		battle = b;
		player = p;
		
		c.gridwidth = 3;
		JLabel infoLabel = new JLabel(player.getName() + ", take a player's equipment item that will allow you to win this battle.");
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		// Load a list of all other players who have equipment to steal
		LinkedList<Player> players = new LinkedList<Player>();
		Iterator<Player> playerIter = GM.getPlayers().iterator();
		while (playerIter.hasNext()) {
			Player currentPlayer = playerIter.next();			
			if (currentPlayer != player && !currentPlayer.getAllEquipment().isEmpty())
				players.add(currentPlayer);
		}
		
		// if no one has any equipment to steal, inform the user and only display button to close dialog
		if (players.isEmpty()) {			
			infoLabel.setText("No one has any equipment to steal.");
			
			c.gridx += 2;
			c.gridy++;
			CustomButton okButton = new CustomButton("OK") {
				private static final long serialVersionUID = 1L;
				
				public void buttonPressed() {
					dispose();
				}
			};
			getContentPane().add(okButton, c);
			
			setJMenuBar(null);
			return;
		}
		
		// Button that allows the player to take the selected item
		takeButton = new CustomButton("Take") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				for (EquipmentCard item : removeItems)
					player.unequip(item);
				
				EquipmentCard equipmentItem = (EquipmentCard)equipmentCardPanel.getSelectedCard();
				Player victim = (Player)playerBox.getSelectedItem();
				victim.removeEquipmentItem(equipmentItem);
				player.addUnequippedItem(equipmentItem);
				player.equip(equipmentItem);
				tookItem = true;
				dispose();
			}
		};
		
		// Button that backs out of using the Help Me Out Card and closes the dialog
		final CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		
		c.gridy++;
		// Drop Down box for choosing a Player to steal from and loads the equipment they are carrying
		playerBox = new JComboBox(players.toArray());
		playerBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final Player victim = (Player)playerBox.getSelectedItem();
				
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(victim.getEquippedItems());
				cards.addAll(victim.getUnequippedItems());	
				if (equipmentCardPanel != null)
					getContentPane().remove(equipmentCardPanel);
				equipmentCardPanel = new CardPanel(cards, 3, takeButton, cancelButton) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void setSelectedImageLabel(JLabel imageLabel) {
						super.setSelectedImageLabel(imageLabel);
						evaluateItem((EquipmentCard)getSelectedCard(), victim);
					}
				};
				GridBagConstraints panelc = new GridBagConstraints();
				panelc.gridx = 1;
				panelc.gridy = 1;
				panelc.insets = c.insets;
				panelc.gridwidth = 2;
				getContentPane().add(equipmentCardPanel, panelc);
				
				refresh();
				repaint();
			}	
		});
		playerBox.setSelectedIndex(0);
		getContentPane().add(playerBox, c);
				
		c.gridx += 2;
		c.gridy++;
		getContentPane().add(noUseLabel, c);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (player.isComputer()) {
			// Starting from leading player determine best card to take.
			EquipmentCard bestCardToTake = null;
			Player victimWithBestCard = null;
			int bestResultingBonus = 0;
			for (Player victim : AIManager.getRankedPlayers()) {
				if (player == victim)
					continue;
				
				for (EquipmentCard equipment : victim.getAllEquipment()) {
					if (!evaluateItem(equipment, victim))
						continue;
					
					int bonus = equipment.getBonus(player);					
					for (EquipmentCard removeItem : removeItems)
						bonus -= removeItem.getBonus(player);
					if (bonus > bestResultingBonus) {
						bestCardToTake = equipment;
						bestResultingBonus = bonus;
						victimWithBestCard = victim;
					}						
				}
			}
			
			if (bestCardToTake == null)
				return;
			
			evaluateItem(bestCardToTake, victimWithBestCard);
			for (EquipmentCard item : removeItems)
				player.unequip(item);
			
			victimWithBestCard.removeEquipmentItem(bestCardToTake);
			player.addUnequippedItem(bestCardToTake);
			player.equip(bestCardToTake);
			tookItem = true;
			String message = player + " used the \"Help Me Out Here!\" card to take the ";
			message += bestCardToTake + " from " + victimWithBestCard  + ".";
			Messenger.display(message, "Card From Hand Played");
		}
		else
			super.setVisible(b);
	}
	
	/**
	 * Evaluates the selected item and enables or disables the Take Button depending on if
	 * the selected Equipment Item meets the Help Me Out Card's requirements.
	 * @param equipmentItem Equipment Item to evaluate
	 * @param victim Player who currently is carrying the item
	 * @return true if the item can be taken; false otherwise
	 */
	private boolean evaluateItem(EquipmentCard equipmentItem, Player victim) {			
		takeButton.setEnabled(false);
		noUseLabel.setText("");
				
		// Can't take item if it fits the following categories
		if (player.hasMalignMirror() && equipmentItem.isWeapon()) {
			noUseLabel.setText("You can't use weapons in this battle.");
			return false;
		}
				
		if (!CardPlayManager.canCarryItem(player, equipmentItem)) {
			noUseLabel.setText("You can't carry any more big items.");
			return false;
		}
				
		int bonus = equipmentItem.getBonus(player);
		removeItems.clear();
		
		// Unequip any current items needed in order to equip and use the selected item
		String noEquipReason = player.equip(equipmentItem);
		if (noEquipReason.equals("Your hands are full.")) {
			EquipmentCard lowestBonusItem = null;
			for (EquipmentCard item : player.getEquippedItems()) {
				if (item != player.getCheatingItemCard()) {
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.ONE_HAND) {
						if (lowestBonusItem == null || lowestBonusItem.getBonus(player) > item.getBonus(player))
							lowestBonusItem = item;
					}
					else if (item.getEquipmentType() == EquipmentCard.EquipmentType.TWO_HANDS) {
						lowestBonusItem = item;
						break;
					}
				}							
			}
			removeItems.add(lowestBonusItem);
		}
		else if (noEquipReason.equals("You don't have two free hands.")) {
			for (EquipmentCard item : player.getEquippedItems()) {
				if (item != player.getCheatingItemCard()) {
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.ONE_HAND)
						removeItems.add(item);
					else if (item.getEquipmentType() == EquipmentCard.EquipmentType.TWO_HANDS) {
						removeItems.add(item);
						break;
					}
				}
			}
		}
		else if (noEquipReason.startsWith("You are already wearing")) {
			EquipmentCard.EquipmentType takeType = equipmentItem.getEquipmentType();
			for (EquipmentCard item : player.getEquippedItems()) {
				if (item != player.getCheatingItemCard()) {
					if (item.getEquipmentType() == takeType) {
						removeItems.add(item);
						break;
					}
				}
			}
		}
		else if (!noEquipReason.equals("")) {
			noUseLabel.setText(noEquipReason);
			refresh();
			return false;
		}
		
		// If selected equipment item bonus minus bonus from any unequipped items is enough to win the battle,
		// then the card may be take, otherwise it cannot be taken.
		for (EquipmentCard removeItem : removeItems)
			bonus -= removeItem.getBonus(player);
		
		boolean victimHadEquipped = victim.hasEquipped(equipmentItem);
		victim.removeEquipmentItem(equipmentItem);
		
		if (battle.getPlayersLevel() + bonus > battle.getMonstersLevel())
			takeButton.setEnabled(true);
		else if (battle.activePlayer.isWarrior() || (battle.isHelper() && battle.helper.isWarrior())) {
			if (battle.getPlayersLevel() + bonus == battle.getMonstersLevel())
				takeButton.setEnabled(true);
		}
				
		victim.addUnequippedItem(equipmentItem);
		if (victimHadEquipped)
			victim.equip(equipmentItem);
		
		return takeButton.isEnabled();
	}
	
	/**
	 * Returns whether or not the player successfully took an item.
	 * @return true if the player took an item; false otherwise
	 */
	public boolean tookItem() {
		return tookItem;
	}
}
