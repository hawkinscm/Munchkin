
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import model.Player;
import model.card.Card;
import model.card.ItemCard;
import model.card.TreasureCard;

/**
 * Dialog that displays the die roll and lets a player play cards to manipulate the roll.
 */
public class RollDiceDialog extends CustomDialog{
	private static final long serialVersionUID = 1L;
	
	// the result of the roll
	private int dieNumber;
	// a Loaded Die card if the player is carrying one
	private ItemCard loadedDieCard = null;
	
	// Button that allows the use of the Loaded Die Card, if available for use
	private CustomButton loadedDieButton;
	
	/**
	 * Creates a new RollDiceDialog dialog.
	 * @param player Player who is rolling the die
	 * @param roll the result of the die roll
	 */
	public RollDiceDialog(final Player player, int roll) {
		super(player + "'s Roll");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// initialize variables and display GUI controls
		dieNumber = roll;
		
		for (TreasureCard item : player.getAllValueCards())
			if (item.getID() == Card.I_LOADED_DIE)
				loadedDieCard = (ItemCard)item;
		
		if (loadedDieCard != null)
			c.gridwidth = 2;
		final JLabel dieLabel = new JLabel("");
		dieLabel.setIcon(new ImageIcon(GUI.class.getResource("images/dice" + dieNumber + ".jpg")));
		dieLabel.setHorizontalTextPosition(JLabel.CENTER);
		dieLabel.setVerticalTextPosition(JLabel.BOTTOM);
		getContentPane().add(dieLabel, c);
		
		// if roll was changed by curse, inform player
		if (player.hasChickenOnHead())
			dieLabel.setText("One has already been subtracted from this roll due to the cursed chicken on your head.");
		c.gridwidth = 1;
		
		c.gridy++;		
		// Lets user accept the roll and closes the dialog
		CustomButton acceptButton = new CustomButton("Accept") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		getContentPane().add(acceptButton, c);		
		
		c.gridx++;
		// if player has Loaded Die Card, allow him to use it to change the die roll number
		if (loadedDieCard != null) {
			loadedDieButton = new CustomButton("Use Loaded Die Card") {
				private static final long serialVersionUID = 1L;
				
				public void buttonPressed() {
					LoadedDieDialog dialog = new LoadedDieDialog();
					dialog.setVisible(true);
					dieNumber = dialog.getSelectedDie();
					if (player.hasChickenOnHead())
						dieNumber--;
		
					dieLabel.setIcon(new ImageIcon(GUI.class.getResource("images/dice" + dieNumber + ".jpg")));
					player.discard(loadedDieCard);
					loadedDieButton.setVisible(false);
					refresh();
				}
			};
			getContentPane().add(loadedDieButton, c);
		}
		
		refresh();		
	}
	
	/**
	 * Returns the final die roll result.
	 * @return the final result of the die roll
	 */
	public int getRoll() {
		return dieNumber;
	}	
}
