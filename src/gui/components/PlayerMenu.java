
package gui.components;

import gui.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import model.Battle;
import model.GM;
import model.Player;

/**
 * Menu that contains information/options for a Player.
 */
public class PlayerMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	// Menu items/options
	private JMenuItem handCardsMenuItem;
	private JMenuItem equipmentMenuItem;
	private JMenuItem itemsMenuItem;
	private JMenuItem battleCardsMenuItem;
	private JMenuItem sellItemsMenuItem;
	private JMenuItem tradeItemsMenuItem;
	private JMenuItem discardRaceMenuItem;
	private JMenuItem discardClassMenuItem;
	private JMenuItem turningMenuItem;
	private JMenuItem charmMenuItem;
	private JMenuItem berserkingMenuItem;
	private JMenuItem backstabMenuItem;
	private JMenuItem stealMenuItem;
	
	// Player who the menu contains information/options for
	private Player player;
	
	/**
	 * Creates a new PlayerMenu menu.
	 * @param gui reference to the main controlling GUI
	 * @param p player who the menu contains information/options for
	 */
	public PlayerMenu(final GUI gui, Player p) {
		// sets the text/header of the menu as the player's name
		super(p.getName());
		
		player = p;
		
		// display all info for player
		PlayerInfoMenu infoMenu = new PlayerInfoMenu(player, "Info");
		add(infoMenu);
		
		if (player.isComputer())
			return;
		
		addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {}
			public void menuDeselected(MenuEvent e) {}
			public void menuSelected(MenuEvent e) {
				handCardsMenuItem.setEnabled(player.getHandCards().size() > 0);
				equipmentMenuItem.setEnabled(player.getAllEquipment().size() > 0);
				itemsMenuItem.setEnabled(player.getCarriedItems().size() > 0);
				sellItemsMenuItem.setEnabled(player.getAllValueCards().size() > 0);
				battleCardsMenuItem.setEnabled(player.getCarriedItems().size() > 0);
				stealMenuItem.setEnabled(player.getHandCards().size() > 0);
			}
		});
		
		addSeparator();
				
		// display hand cards
		handCardsMenuItem = new JMenuItem("Cards In Hand");
		handCardsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayHand(player);
			}
		});
		add(handCardsMenuItem);
		
		// display equipment
		equipmentMenuItem = new JMenuItem("Equipment");
		equipmentMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayEquipment(player);
			}
		});
		add(equipmentMenuItem);
		
		// display carried items (not equipment)
		itemsMenuItem = new JMenuItem("Carried Items");
		itemsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayCarriedItems(player);
			}
		});
		add(itemsMenuItem);
		
		// display battle card options
		battleCardsMenuItem = new JMenuItem("Use Battle Item");
		battleCardsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayBattleItems(player);
			}
		});
		add(battleCardsMenuItem);
		
		// sell items
		sellItemsMenuItem = new JMenuItem("Sell Items");
		sellItemsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displaySellItems(player);
			}
		});
		add(sellItemsMenuItem);
		
		// trade items
		tradeItemsMenuItem = new JMenuItem("Trade Items");
		tradeItemsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayTradeItems(player);
			}
		});
		add(tradeItemsMenuItem);
		
		addSeparator();
		
		// discard a race
		discardRaceMenuItem = new JMenuItem("Discard A Race");
		discardRaceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayDiscardRace(player);
			}
		});
		add(discardRaceMenuItem);
		
		// discard a class
		discardClassMenuItem = new JMenuItem("Discard A Class");
		discardClassMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayDiscardClass(player);
			}
		});
		add(discardClassMenuItem);
		
		addSeparator();
		
		// use Turning ability
		turningMenuItem = new JMenuItem("Use Turning Ability");
		turningMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayTurningAbility(player);
			}
		});
		add(turningMenuItem);
		
		// use Charm Spell
		charmMenuItem = new JMenuItem("Charm Monster");
		charmMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayCharmMonster(player);
			}
		});
		add(charmMenuItem);
		
		// use Berserking ability
		berserkingMenuItem = new JMenuItem("Use Berserking Ability");
		berserkingMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayBerserkingAbility(player);
			}
		});
		add(berserkingMenuItem);
		
		// use Backstabbing ability
		backstabMenuItem = new JMenuItem("Backstab Player");
		backstabMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayBackstabPlayer(player);
			}
		});
		add(backstabMenuItem);
		
		// steal items
		stealMenuItem = new JMenuItem("Steal Item");
		stealMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.displayStealItems(player);
			}
		});
		add(stealMenuItem);
		
		updateMenuItems(null);
	}
	
	/**
	 * Returns the Player that this menu is for.
	 * @return the Player that this menu is for
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Enables/Disables menu items based on the current battle/player situation.
	 * @param battle currently ongoing battle; null if no battle is currently taking place
	 */
	public void updateMenuItems(Battle battle) {
		if (player.isComputer())
			return;
		
		if (!GM.isAIThinking()) {
			handCardsMenuItem.setEnabled(true);
			equipmentMenuItem.setEnabled(true);
			itemsMenuItem.setEnabled(true);
			battleCardsMenuItem.setEnabled(true);
			sellItemsMenuItem.setEnabled(true);
			tradeItemsMenuItem.setEnabled(true);
			discardRaceMenuItem.setEnabled(true);
			discardClassMenuItem.setEnabled(true);
			turningMenuItem.setEnabled(true);
			charmMenuItem.setEnabled(true);
			berserkingMenuItem.setEnabled(true);
			backstabMenuItem.setEnabled(true);
			stealMenuItem.setEnabled(true);
		}
		
		if (GM.isAIThinking()) {
			handCardsMenuItem.setEnabled(false);
			equipmentMenuItem.setEnabled(false);
			itemsMenuItem.setEnabled(false);
			battleCardsMenuItem.setEnabled(false);
			sellItemsMenuItem.setEnabled(false);
			tradeItemsMenuItem.setEnabled(false);
			discardRaceMenuItem.setEnabled(false);
			discardClassMenuItem.setEnabled(false);
			turningMenuItem.setEnabled(false);
			charmMenuItem.setEnabled(false);
			berserkingMenuItem.setEnabled(false);
			backstabMenuItem.setEnabled(false);
			stealMenuItem.setEnabled(false);
		}
		else if (GM.isBattlePhase() && battle != null) {
			equipmentMenuItem.setVisible(false);
			itemsMenuItem.setVisible(false);
			battleCardsMenuItem.setVisible(true);
			sellItemsMenuItem.setVisible(false);
			tradeItemsMenuItem.setVisible(false);
			
			turningMenuItem.setVisible(battle.canUseTurning(player));
			charmMenuItem.setVisible(battle.canCastCharm(player));
			berserkingMenuItem.setVisible(battle.canUseBerserking(player));
			backstabMenuItem.setVisible(player.isThief());
			
			stealMenuItem.setVisible(false);
		}
		else {
			equipmentMenuItem.setVisible(true);
			itemsMenuItem.setVisible(true);
			battleCardsMenuItem.setVisible(false);
			sellItemsMenuItem.setVisible(player == GM.getActivePlayer());
			tradeItemsMenuItem.setVisible(true);
			
			turningMenuItem.setVisible(false);
			charmMenuItem.setVisible(false);
			berserkingMenuItem.setVisible(false);
			backstabMenuItem.setVisible(false);
			
			stealMenuItem.setVisible(player.isThief());
		}
		
		discardRaceMenuItem.setVisible(!player.getRaceCards().isEmpty());
		discardClassMenuItem.setVisible(!player.getClassCards().isEmpty());
		
		repaint();
	}
}
