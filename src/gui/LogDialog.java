package gui;

import java.awt.GridBagConstraints;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.GM;
import model.Player;
import model.card.Card;

import gui.components.CustomButton;
import gui.components.CustomDialog;

/**
 * Used to capture data from current point in game and write it along with input problem description to standard out.
 * Standard out should be pointing to the log file.
 */
public class LogDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Log Dialog.
	 */
	public LogDialog() {
		super("Create Log Entry");
		
		c.anchor = GridBagConstraints.WEST;
		getContentPane().add(new JLabel("Name: "), c);
		
		c.gridx++;
		final JTextField nameField = new JTextField(20);
		getContentPane().add(nameField, c);
		
		c.gridx = 0;
		c.gridy++;
		getContentPane().add(new JLabel("Problem Description: "), c);
		
		c.gridy++;
		c.gridwidth = 2;
		final JTextArea descriptionArea = new JTextArea(20, 50);
		descriptionArea.setBorder(nameField.getBorder());
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		getContentPane().add(descriptionArea, c);
		c.gridwidth = 1;
		
		c.gridy++;
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				writeLogEntry(nameField.getText(), descriptionArea.getText());
				dispose();
			}
		};
		getContentPane().add(okButton, c);
		
		c.gridx++;
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				dispose();
			}
		};
		getContentPane().add(cancelButton, c);
		
		refresh();
	}
		
	/**
	 * Writes the current game data along with user notes/details to standard out.
	 * @param name name of the user
	 * @param details details of the problem seen
	 */
	private void writeLogEntry(String name, String details) {
		System.out.println();
		System.out.println("LOG ENTRY: " + Calendar.getInstance().getTime().toString());
		System.out.println(name + ": " + details);
		String phase = "Other Phase";
		if (GM.isOpenDoorPhase())
			phase = "Open Door Phase";
		else if (GM.isBattlePhase())
			phase = "Battle Phase";
		else if (GM.isLootRoomPhase())
			phase = "Loot Room Phase";
		else if (GM.isLookForTroublePhase())
			phase = "Look For Trouble Phase";
		else if (GM.isCharityPhase())
			phase = "Charity Phase";
		System.out.println(phase + ", Current Turn: " + GM.getActivePlayer());
		
		for (Player player : GM.getPlayers()) {
			System.out.println(player + ":" + player.getPlayerType().toString() + (player.isMale() ? ":Male" : ":Female"));
			System.out.println("\tLevel: " + player.getLevel());
			System.out.print("\tRaces: ");
			for (Card card : player.getRaceCards())
				System.out.print(card + "; ");
			System.out.println(player.getHalfBreedCard());
			System.out.print("\tClasses: ");
			for (Card card : player.getClassCards())
				System.out.print(card + "; ");
			System.out.println(player.getSuperMunchkinCard());
			System.out.print("\tHand: ");
			for (Card card : player.getHandCards())
				System.out.print(card + "; ");
			System.out.println();
			System.out.print("\tCarried: ");
			for (Card card : player.getCarriedItems())
				System.out.print(card + "; ");
			System.out.println();
			System.out.print("\tEquipped: ");
			for (Card card : player.getEquippedItems())
				System.out.print(card + "; ");
			System.out.println();
			System.out.print("\tUnequipped: ");
			for (Card card : player.getUnequippedItems())
				System.out.print(card + "; ");
			System.out.println();
			System.out.print("\tOther: ");
			System.out.print(player.getChickenOnHeadCard() + "; ");
			System.out.print(player.getChangeSexCard() + "; ");
			System.out.print(player.getMalignMirrorCard() + "; ");
			System.out.print("(cheat)" + player.getCheatingItemCard() + "; ");
			System.out.print(player.getHirelingCard());
			System.out.println();							
		}
		
		System.out.print("Door discards: ");
		for (Card card : GM.getDoorDeck().getDiscardPile())
			System.out.print(card + "; ");
		System.out.println();
		System.out.print("Treasure discards: ");
		for (Card card : GM.getTreasureDeck().getDiscardPile())
			System.out.print(card + "; ");
		System.out.println();
		
		System.out.flush();
	}
}
