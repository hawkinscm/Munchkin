
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ai.AICardEvaluator;
import ai.AIManager;

import model.Player;
import model.card.Card;
import model.card.EquipmentCard;
import model.card.ItemCard;

/**
 * Dialog that allows a player to use a Wishing Ring to remove a lasting curse.
 */
public class RemoveCurseDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// text codes for each type of lasting curse
	private final String CHICKEN_ON_HEAD_TEXT = "Chicken on Your Head";
	private final String DISTRACTION_TEXT = "Sex Change and distraction";
	private final String SEX_CHANGE_TEXT = "Sex Change";
	private final String MALIGN_MIRROR_TEXT = "Malign Mirror";
	
	// whether or not the curse was removed (and the Wisihing Ring was used)
	private boolean removedCurse;
	
	private Player player;
	
	/**
	 * Creates a new RemoveCurseDialog dialog.
	 * @param p player who is removing the curse
	 */
	public RemoveCurseDialog(final Player p) {
		super("Remove Curse");
		
		player = p;

		removedCurse = false;
		
		if (player.isComputer()) {
			// Remove lasting curse if has bad effect
			String worstCurse = null;
			int worstCurseCost = 0;
			
			if (player.hasChickenOnHead()) {
				worstCurse = CHICKEN_ON_HEAD_TEXT;
				worstCurseCost = AIManager.getCurseCost(player.getChickenOnHeadCard(), player, player.getHandCards());
			}
			if (player.hasDistractionCurse() || player.hasChangeSexCurse()) {
				int curseCost = 0;
				if (player.hasDistractionCurse())
					curseCost = 500;
				
				boolean hasMaleOnlyItem = false;
				boolean hasFemaleOnlyItem = false;
				for (EquipmentCard equipment : player.getAllEquipment()) {
					if (equipment == player.getCheatingItemCard())
						continue;
					else if (equipment.getID() == Card.E_GENTLEMENS_CLUB)
						hasMaleOnlyItem = true;
					else if (equipment.getID() == Card.E_BROAD_SWORD)
						hasFemaleOnlyItem = true;
				}
				
				if (player.isMale() && hasFemaleOnlyItem)
					curseCost += 1200;
				else if (player.isMale() && hasMaleOnlyItem)
					curseCost -= 1200;
				else if (player.isFemale() && hasMaleOnlyItem)
					curseCost += 1200;
				else if (player.isFemale() && hasFemaleOnlyItem)
					curseCost -= 1200;
				
				if (curseCost > worstCurseCost) {
					worstCurseCost = curseCost;
					if (player.hasDistractionCurse())
						worstCurse = DISTRACTION_TEXT;
					else
						worstCurse = SEX_CHANGE_TEXT;
				}
			}
			if (player.hasMalignMirror()) {
				int curseCost = AIManager.getCurseCost(player.getMalignMirrorCard(), player, player.getHandCards()); 
				
				if (curseCost > worstCurseCost) {
					worstCurseCost = curseCost;
					worstCurse = MALIGN_MIRROR_TEXT;
				}
			}
			
			if (worstCurse != null) {
				ItemCard wishingRing = null;
				for (ItemCard item : player.getCarriedItems())
					if (item.getID() == Card.I_WISHING_RING_1 || item.getID() == Card.I_WISHING_RING_2) {
						wishingRing = item;
						break;
					}
				
				if (worstCurseCost >= AICardEvaluator.getCardValueToPlayer(wishingRing, player, player.getHandCards())) {
					if (worstCurse.equals(CHICKEN_ON_HEAD_TEXT))
						player.removeChickenOnHeadCurse();
					else if (worstCurse.equals(DISTRACTION_TEXT)) {
						player.removeSexChangeCurse();
						player.changeSex();
						worstCurse = CHICKEN_ON_HEAD_TEXT;
					}
					else if (worstCurse.equals(SEX_CHANGE_TEXT))
						player.changeSex();
					else if (worstCurse.equals(MALIGN_MIRROR_TEXT))
						player.removeMalignMirror();
						
					String message = player + " used a Wishing Ring to remove the " + worstCurse + " curse.";
					Messenger.display(message, "Wishing Ring Played");
					removedCurse = true;
				}
			}
			
			return;
		}
		
		// initialize variables and display GUI controls		
		final JLabel infoLabel = new JLabel(player.getName() + ", remove which curse?");
		getContentPane().add(infoLabel, c);
		
		// Loads a list of lasting curses that are currently on the player
		LinkedList<String> curses = new LinkedList<String>();
		if (player.hasChickenOnHead())
			curses.add(CHICKEN_ON_HEAD_TEXT);
		if (player.hasDistractionCurse())
			curses.add(DISTRACTION_TEXT);
		else if (player.hasChangeSexCurse())
			curses.add(SEX_CHANGE_TEXT);
		if (player.hasMalignMirror())
			curses.add(MALIGN_MIRROR_TEXT);
		
		c.gridy++;
		// if the player has no lasting curses, inform him and display only a button to close the dialog
		if (curses.isEmpty()) {
			infoLabel.setText("You have no lasting curses on you.");
			
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
		
		// Drop down list of all lasting curses on the player
		final JComboBox cursesBox = new JComboBox(curses.toArray());
		getContentPane().add(cursesBox, c);
		
		c.gridx++;
		// Button that removes the selected lasting curse from the player
		CustomButton removeButton = new CustomButton("Remove") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				String selectedCurse = (String)cursesBox.getSelectedItem();
				if (selectedCurse.equals(CHICKEN_ON_HEAD_TEXT))
					player.removeChickenOnHeadCurse();
				else if (selectedCurse.equals(DISTRACTION_TEXT)) {
					player.changeSex();
					player.removeSexChangeCurse();
				}
				else if (selectedCurse.equals(SEX_CHANGE_TEXT))
					player.changeSex();
				else if (selectedCurse.equals(MALIGN_MIRROR_TEXT))
					player.removeMalignMirror();
				
				removedCurse = true;
				dispose();
			}
		};
		getContentPane().add(removeButton, c);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!player.isComputer())
			super.setVisible(b);
	}
	
	/**
	 * Returns whether or not a lasting curse was removed.
	 * @return true if a lasting curse was removed; otherwise false
	 */
	public boolean removedCurse() {
		return removedCurse;
	}
}
