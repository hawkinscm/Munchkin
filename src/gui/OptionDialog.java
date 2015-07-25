
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;

import javax.swing.JLabel;

/**
 * Dialog that lets a user choose between two options.
 */
public class OptionDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
		
	// The number of the choice the user made
	private int choice;
	
	/**
	 * Create a new OptionDialog dialog.
	 * @param title text for the title of the dialog window
	 * @param prompt text to prompt the user what needs to be chosen and why
	 * @param option1Text text of option Button one
	 * @param option2Text text of option Button two
	 */
	public OptionDialog(String title, String prompt, String option1Text, String option2Text) {
		super(title);
		
		// initialize variables and display GUI controls
		choice = 0;
		
		c.gridwidth = 2;
		JLabel promptLabel = new JLabel(prompt);
		getContentPane().add(promptLabel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		// Button to select option one and close dialog
		final CustomButton option1Button = new CustomButton(option1Text) {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				choice = 1;
				dispose();
			}
		};
		getContentPane().add(option1Button, c);
		
		c.gridx++;
		// Button to select option two and close dialog
		final CustomButton option2Button = new CustomButton(option2Text) {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				choice = 2;
				dispose();
			}
		};
		getContentPane().add(option2Button, c);
		
		refresh();
	}
	
	/**
	 * Returns the number of the choice the user made.
	 * @return the number of the choice the user made
	 */
	public int getChoice() {
		return choice;
	}
}
