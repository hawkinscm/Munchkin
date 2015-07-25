
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.PlayerLabel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import model.Player;

/**
 * Dialog that displays a list of Players for a player to choose from.
 */
public class ChoosePlayerDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Button for selecting a Player
	private CustomButton selectButton;
	
	// The player that is selected
	private Player selectedPlayer;
	
	/**
	 * Creates a new ChoosePlayerDialog Dialog.
	 * @param players list of Players to choose from
	 * @param reason text containing the reason to choose a Player
	 */
	public ChoosePlayerDialog(LinkedList<Player> players, String reason) {
		super("Choose Player");
		
		// initialize variables and display GUI controls
		selectedPlayer = null;
		
		c.gridwidth = 2;
		final JLabel infoLabel = new JLabel(reason);
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		final PlayerLabel playerLabel = new PlayerLabel(null);
		getContentPane().add(playerLabel, c);
		
		c.gridx++;
		// Drop down box for selecting a Player
		final JComboBox playerBox = new JComboBox(players.toArray());	
		playerBox.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					selectButton.buttonPressed();
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		playerBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				playerLabel.setPlayer((Player)playerBox.getSelectedItem());
				refresh();
			}			
		});
		getContentPane().add(playerBox, c);
		
		c.gridy++;
		c.gridx = 0;
		c.anchor = GridBagConstraints.EAST;
		selectButton = new CustomButton("Select") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				selectedPlayer = (Player)playerBox.getSelectedItem();
				dispose();
			}
		};
		getContentPane().add(selectButton, c);
		c.anchor = GridBagConstraints.CENTER;
		
		playerBox.setSelectedIndex(0);
	}
	
	/**
	 * Returns the selected Player.
	 * @return the selected Player
	 */
	public Player getSelectedPlayer() {
		return selectedPlayer;
	}
}
