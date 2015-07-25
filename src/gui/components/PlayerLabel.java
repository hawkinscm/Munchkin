
package gui.components;

import gui.GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import model.Player;

/**
 * Label which displays current information about a Player.
 */
public class PlayerLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	
	// constants containing the picture to use for the Player depending on gender
	private final static ImageIcon maleIcon = new ImageIcon(GUI.class.getResource("images/male_munchkin.jpg"));
	private final static ImageIcon femaleIcon = new ImageIcon(GUI.class.getResource("images/female_munchkin.jpg"));
		
	private Player player;
	
	/**
	 * Create a new PlayerLabel label.
	 * @param p player that the label provides information for
	 */
	public PlayerLabel(Player p) {
		//super("Test");
		
		// initializes variables and displays GUI controls
		player = p;
		
		setHorizontalTextPosition(JLabel.CENTER);
		setVerticalTextPosition(JLabel.BOTTOM);
		
		updatePlayerInfo();
		
		// display a pop-up player menu when label is right-clicked
		final JLabel thisLabel = this;
		addMouseListener(new MouseListener() {
			private boolean isInside = false;
			
			public void mouseClicked(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent e) {
				if (!isInside || !isEnabled())
					return;
							
				JPopupMenu popup = new JPopupMenu();
				popup.add(new PlayerInfoMenu(player));
				popup.show(thisLabel, e.getX() - 5, e.getY() - 5);
			}
			public void mouseEntered(MouseEvent e) {isInside = true;}
			public void mouseExited(MouseEvent e) {isInside = false;}
		});
	}
	
	/**
	 * Reloads information about the player and refreshes the display.
	 */
	public void updatePlayerInfo() {
		if (player == null)
			return;
		
		if (player.isMale())
			setIcon(maleIcon);
		else
			setIcon(femaleIcon);
		
		String message = "<HTML><center>&nbsp;";
		message += player.getName();
		message += "&nbsp;<br>&nbsp;";
		message += "Level " + player.getLevel();
		message += "&nbsp;<br>&nbsp;";
		int battleLevel = player.getLevel() + player.getEquipmentBonus();
		if (player.hasDistractionCurse())
			battleLevel -= 5;
		message += "Battle Level " + battleLevel;
		message += "&nbsp;<br>";
		
		// Race
		String halfBreedText = "&nbsp;";
		if (player.isHalfBreed())
			halfBreedText +="&nbsp;Half-";
		else if (player.isHuman())
			message += "&nbsp;Human";
		if (player.isElf())
			message += halfBreedText + "Elf";
		if (player.isDwarf())
			message += halfBreedText + "Dwarf";
		if (player.isHalfling())
			message += halfBreedText + "Halfling";
		
		// Class
		if (!player.getClassCards().isEmpty()) {
			message += "&nbsp;<br>";
			if (player.isWarrior())
				message += "&nbsp;Warrior";
			if (player.isWizard())
				message += "&nbsp;Wizard";
			if (player.isThief())
				message += "&nbsp;Thief";
			if (player.isCleric())
				message += "&nbsp;Cleric";
		}
		
		message += "&nbsp;</center></HTML>";
		setText(message);
		revalidate();
		repaint();
	}
	
	/**
	 * Return the Player that this label is for.
	 * @return the Player that this label is for
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Sets the Player that this label is for.
	 * @param p the Player that this label is for
	 */
	public void setPlayer(Player p) {
		player = p;
		updatePlayerInfo();
	}
}
