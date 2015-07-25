
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

/**
 * Dialog that handles a Loaded Die Card.
 */
public class LoadedDieDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// the die number desired by the user playing the Loaded Die Card.
	int selectedDieNumber = 0;
	
	/**
	 * Creates a new LoadedDieDialog dialog.
	 */
	public LoadedDieDialog() {
		super("Move die to desired number");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setJMenuBar(null);
		
		// Display an image for each side of the die and let the user choose which number he wants
		for (int row = 0; row < 2; row++) {
			c.gridx = row;
			
			for (int column = 0; column < 3; column++) {
				c.gridy = column;
				final int dieNum = (row * 3) + column + 1;
				
				CustomButton button = new CustomButton(new ImageIcon(GUI.class.getResource("images/dice" + dieNum + ".jpg"))) {
					private static final long serialVersionUID = 1L;
					
					public void buttonPressed() {
						selectedDieNumber = dieNum;
						dispose();
					}
				};
				Dimension dim = new Dimension(button.getIcon().getIconWidth(), button.getIcon().getIconHeight());
				button.setPreferredSize(dim);
				getContentPane().add(button, c);
			}				
		}
		
		refresh();			
	}
	
	/**
	 * Returns the die number selected by the user.
	 * @return the die number selected by the user
	 */
	public int getSelectedDie() {
		return selectedDieNumber;
	}	
}
