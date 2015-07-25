
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import model.Player;
import model.PlayerType;
import model.Randomizer;

/**
 * Dialog for choosing the name, gender, and type of Players for a new game.
 */
public class CreatePlayersDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Arrays of Drop Down lists for each player for type, name, and gender
	private JComboBox[] typeComboBoxes;
	private JTextField[] nameFields;
	private ButtonGroup[] genderOptionGroups;
	
	private LinkedList<Player> newDefaultPlayers;
		
	/**
	 * Creates a new CreatePlayersDialog Dialog.
	 * @param gui reference to the main controlling GUI
	 * @param numPlayers the number of players who will be in the new game
	 * @param defaultPlayers the players to display as defaults.
	 */
	public CreatePlayersDialog(final GUI gui, int numPlayers, LinkedList<Player> defaultPlayers) {
		super(gui, "Players");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		newDefaultPlayers = defaultPlayers;
		
		// initialize variable and display GUI controls
		c.gridx++;
		getContentPane().add(new JLabel("NAME"), c);
		
		c.gridx++;
		getContentPane().add(new JLabel("GENDER"), c);
		
		typeComboBoxes = new JComboBox[numPlayers];
		nameFields = new JTextField[numPlayers];
		genderOptionGroups = new ButtonGroup[numPlayers];
		
		for(int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
			final int currentIndex = playerIndex;
			
			c.gridx = 0;
			c.gridy++;
			
			// Allow selecting of type: human or computer level
			typeComboBoxes[playerIndex] = new JComboBox(PlayerType.toArray());
			getContentPane().add(typeComboBoxes[playerIndex], c);
						
			c.gridx++;
			// Allow entering of name (default is player#)
			nameFields[playerIndex] = new JTextField("Player" + (playerIndex + 1), 20);
			nameFields[playerIndex].addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					nameFields[currentIndex].selectAll();				
				}
				public void focusLost(FocusEvent arg0) {}
			});
			getContentPane().add(nameFields[playerIndex], c);
						
			c.gridx++;
			// Allow selecting of genders: male or female (default is male)
			genderOptionGroups[playerIndex] = new ButtonGroup();
			JRadioButton maleOption = new JRadioButton("M");
			maleOption.setSelected(true);
			maleOption.setFocusable(false);
			JRadioButton femaleOption = new JRadioButton("F");
			femaleOption.setFocusable(false);
			genderOptionGroups[playerIndex].add(maleOption);
			genderOptionGroups[playerIndex].add(femaleOption);			
			getContentPane().add(maleOption, c);
			
			c.gridx++;
			getContentPane().add(femaleOption, c);
			
			if (playerIndex < defaultPlayers.size()) {
				Player defaultPlayer = defaultPlayers.get(playerIndex);
				
				typeComboBoxes[playerIndex].setSelectedItem(defaultPlayer.getPlayerType());
				nameFields[playerIndex].setText(defaultPlayer.getName());
				if (defaultPlayer.isFemale())
					femaleOption.setSelected(true);
			}
		}
		
		c.gridy++;
		c.gridx--;
		// Button that signals the completion of player creation input
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		getContentPane().add(okButton, c);
		
		refresh();
		okButton.requestFocusInWindow();
	}
	
	/**
	 * Uses the input data to create and return a list of players for the new game.
	 * @return the players for the game
	 */
	public LinkedList<Player> getPlayers() {
		LinkedList<Player> players = new LinkedList<Player>();
		for(int playerIndex = 0; playerIndex < nameFields.length; playerIndex++) {
			String name = nameFields[playerIndex].getText();
			if (name.trim().equals(""))
				name = "Player" + (playerIndex + 1);
			
			ButtonModel maleButton = (ButtonModel)genderOptionGroups[playerIndex].getElements().nextElement().getModel();
			boolean isMale = genderOptionGroups[playerIndex].isSelected(maleButton);
			
			PlayerType playerType = (PlayerType)typeComboBoxes[playerIndex].getSelectedItem();
			
			Player player = new Player((GUI)getOwner(), name, isMale, playerType);
			if (playerIndex < newDefaultPlayers.size())
				newDefaultPlayers.set(playerIndex, new Player((GUI)getOwner(), name, isMale, playerType));
			else
				newDefaultPlayers.add(new Player((GUI)getOwner(), name, isMale, playerType));
		
			// Randomize player turn order
			if (playerIndex == 0)
				players.add(player);
			else {
				int turnIndex = Randomizer.getRandom(playerIndex + 1);
				players.add(turnIndex, player);
			}
		}
		
		return players;
	}	
}
