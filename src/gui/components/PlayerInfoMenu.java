
package gui.components;

import gui.DisplayCardsDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import model.Player;
import model.PlayerType;
import model.card.Card;
import model.card.DoorCard;
import model.card.EquipmentCard;
import model.card.TreasureCard;

/**
 * Menu that displays information for a Player.
 */
public class PlayerInfoMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	private Player player;
	
	/**
	 * Creates a new PlayerInfoMenu menu with the name of the player as the text/heading of the menu.
	 * @param p player that the menu provides information for
	 */
	public PlayerInfoMenu(Player p) {
		super(p.getName());
		
		player = p;
	}
	
	/**
	 * Creates a new PlayerInfoMenu menu.
	 * @param p the player that the menu provides information for
	 * @param menuText the text/heading of the menu
	 */
	public PlayerInfoMenu(Player p, String menuText) {
		super(menuText);
		
		player = p;
	}
	
	@Override
	/**
	 * Reloads and repaints the menu.
	 */
	public void repaint() {
		populateMenu();
		
		super.repaint();
	}
		
	/**
	 * Loads player information into the menu.
	 */
	private void populateMenu() {
		removeAll();
		if (player == null)
			return;
		
		// display player level
		JMenuItem menuItem = new JMenuItem("Level " + player.getLevel());
		menuItem.setEnabled(false);
		add(menuItem);
		
		String type = "Non-Computer";
		if (player.getPlayerType() == PlayerType.COMPUTER_EASY)
			type = "Easy Computer";
		else if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
			type = "Medium Computer";
		else if (player.getPlayerType() == PlayerType.COMPUTER_HARD)
			type = "Hard Computer";
		menuItem = new JMenuItem(type);
		menuItem.setEnabled(false);
		add(menuItem);
		
		// display player gender
		String gender = "";
		if (player.hasChangeSexCurse())
			gender = "Cursed ";
		if (player.isMale())
			gender += "Male";
		else
			gender += "Female";
		menuItem = new JMenuItem(gender);
		menuItem.setEnabled(false);
		add(menuItem);
		
		// display race information
		String halfBreedText = "";
		String race = "";
		if (player.isHalfBreed())
			halfBreedText = "Half-";
		else if (player.isHuman())
			race = "Human";
		if (player.isElf())
			race += halfBreedText + "Elf ";
		if (player.isDwarf())
			race += halfBreedText + "Dwarf ";
		if (player.isHalfling())
			race += halfBreedText + "Halfling ";
		menuItem = new JMenuItem(race);
		menuItem.setEnabled(true);
		if (race.equals("Human"))
			menuItem.setEnabled(false);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LinkedList<Card> cards = new LinkedList<Card>();
				if (player.isHalfBreed())
					cards.add(player.getHalfBreedCard());
				cards.addAll(player.getRaceCards());				
				DisplayCardsDialog dialog = new DisplayCardsDialog(cards, player.getName() + "'s Race Cards");
				dialog.setJMenuBar(null);
				dialog.setVisible(true);
			}
		});
		add(menuItem);
		
		// display class information
		if (!player.getClassCards().isEmpty()) {
			String characterClass = "";
			if (player.isWarrior())
				characterClass += "Warrior ";
			if (player.isWizard())
				characterClass += "Wizard ";
			if (player.isThief())
				characterClass += "Thief ";
			if (player.isCleric())
				characterClass += "Cleric ";
			menuItem = new JMenuItem(characterClass);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LinkedList<Card> cards = new LinkedList<Card>();
					if (player.isSuperMunchkin())
						cards.add(player.getSuperMunchkinCard());
					cards.addAll(player.getClassCards());				
					DisplayCardsDialog dialog = new DisplayCardsDialog(cards, player.getName() + "'s Class Cards");
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			menuItem.setEnabled(true);
			add(menuItem);
		}
		
		addSeparator();
		
		// display player battle level
		int battleLevel = player.getLevel() + player.getEquipmentBonus();
		if (player.hasDistractionCurse())
			battleLevel -= 5;
		menuItem = new JMenuItem("Battle Level: " + battleLevel);
		menuItem.setEnabled(false);
		add(menuItem);
		
		// display run away bonus
		int runAwayBonus = 0;
		if (player.isElf())
			runAwayBonus++;
		else if (!player.isHuman() && player.isHalfling())
			runAwayBonus--;		
		if (player.hasEquipped(Card.E_TUBA_OF_CHARM))
			runAwayBonus += 1;
		if (player.hasEquipped(Card.E_BOOTS_OF_RUNNING_REALLY_FAST))
			runAwayBonus += 2;		
		String runBonusText = "+" + runAwayBonus;
		if (runAwayBonus < 0)
			runBonusText = "" + runAwayBonus;
		menuItem = new JMenuItem("Run Away Bonus: " + runBonusText);
		menuItem.setEnabled(false);
		add(menuItem);
		
		// Display lasting curse cards
		if (player.hasChickenOnHead()) {
			menuItem = new JMenuItem("Cursed: Chicken on Your Head");
			menuItem.setEnabled(true);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getChickenOnHeadCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}				
			});
			add(menuItem);
		}
		
		if (player.hasMalignMirror()) {
			menuItem = new JMenuItem("Cursed: Malign Mirror");
			menuItem.setEnabled(true);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getMalignMirrorCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}				
			});
			add(menuItem);
		}
		
		if (player.hasDistractionCurse()) {
			menuItem = new JMenuItem("Cursed: Change Sex Distraction");
			menuItem.setEnabled(true);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getChangeSexCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}				
			});
			add(menuItem);
		}
		
		addSeparator();
		
		// display equipment items
		JMenu oneHandedMenu = new JMenu("One-Handed");
		JMenu twoHandedMenu = new JMenu("Two-Handed");
		JMenu headgearMenu = new JMenu("Headgear");
		JMenu armorMenu = new JMenu("Armor");
		JMenu footgearMenu = new JMenu("Footgear");
		JMenu otherEquipmentMenu = new JMenu("Other Equipment");
		JMenu itemMenu = new JMenu("Items");

		LinkedList<JMenu> carriedItemMenus = new LinkedList<JMenu>();
		for (final TreasureCard card : player.getAllItems()) {
			JMenu currentTypeMenu = itemMenu;
			String note = "";
			
			if (card instanceof EquipmentCard) {
				EquipmentCard equipment = (EquipmentCard)card;
				if (equipment == player.getCheatingItemCard())
					note = "** ";
				else if (player.hasEquipped(equipment))
					note = "* ";
				
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.ONE_HAND) 
					currentTypeMenu = oneHandedMenu;
				else if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.TWO_HANDS) 
					currentTypeMenu = twoHandedMenu;
				else if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR) 
					currentTypeMenu = headgearMenu;
				else if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.ARMOR) 
					currentTypeMenu = armorMenu;
				else if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.FOOTGEAR) 
					currentTypeMenu = footgearMenu;
				else
					currentTypeMenu = otherEquipmentMenu;
			}
			
			menuItem = new JMenuItem(note + card.getName());
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LinkedList<Card> cards = new LinkedList<Card>();
					cards.addAll(player.getAllItems());
					DisplayCardsDialog dialog = new DisplayCardsDialog(cards, player.getName() + "'s Items");
					dialog.setSelectCard(card);
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			currentTypeMenu.add(menuItem);
			if (!carriedItemMenus.contains(currentTypeMenu))
				carriedItemMenus.add(currentTypeMenu);
		}
		
		for (JMenu currentMenu : carriedItemMenus)
			add(currentMenu);
		
		// display Hireling info
		if (player.hasHireling()) {
			menuItem = new JMenuItem("Hireling");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {			
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getHirelingCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			add(menuItem);
		}
		
		// mark equipment that is equipped and/or is coupled with the Cheat Card.
		if (!carriedItemMenus.isEmpty()) {
			menuItem = new JMenuItem("NOTE: * = Equipped");
			menuItem.setEnabled(false);
			add(menuItem);
		}
		if (player.getCheatingItemCard() != null) {
			menuItem = new JMenuItem("NOTE: ** = Cheat! Item");
			menuItem.setEnabled(false);
			add(menuItem);
		}
		
		addSeparator();
		
		// display hand cards
		JMenu handCardsMenu = new JMenu(player.getHandCards().size() + " Cards In Hand");
		int doorCardCount = 0;
		int treasureCardCount = 0;
		for (Card card : player.getHandCards()) {
			if (card instanceof DoorCard)
				doorCardCount++;
			else
				treasureCardCount++;
		}
		if (doorCardCount > 0) {
			menuItem = new JMenuItem(doorCardCount + " Door Cards");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					(new DisplayCardsDialog(player.getHandCards(), player + "'s Hand")).setVisible(true);
				}
			});
			if (player.isComputer())
				menuItem.setEnabled(false);
			handCardsMenu.add(menuItem);
		}
		if (treasureCardCount > 0) {
			menuItem = new JMenuItem(treasureCardCount + " Treasure Cards");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					(new DisplayCardsDialog(player.getHandCards(), player + "'s Hand")).setVisible(true);
				}
			});
			if (player.isComputer())
				menuItem.setEnabled(false);
			handCardsMenu.add(menuItem);
		}
		add(handCardsMenu);
		if (player.getHandCards().isEmpty())
			handCardsMenu.setEnabled(false);
		
		// display carried items
		if (!player.getAllItems().isEmpty()) {
			JMenuItem viewAllItemsItemMenu = new JMenuItem("View All Items");
			viewAllItemsItemMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LinkedList<Card> cards = new LinkedList<Card>();
					cards.addAll(player.getAllItems());
					DisplayCardsDialog dialog = new DisplayCardsDialog(cards, player.getName() + "'s Items");
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			add(viewAllItemsItemMenu);
		}
		
		// display any lasting curses on the player
		JMenu cursesMenu = new JMenu("Lasting Curses");
		if (player.hasChickenOnHead()) {
			JMenuItem curseMenuItem = new JMenuItem("Chicken On Your Head");
			curseMenuItem.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getChickenOnHeadCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			cursesMenu.add(curseMenuItem);
		}
		if (player.hasMalignMirror()) {
			JMenuItem curseMenuItem = new JMenuItem("Malign Mirror");
			curseMenuItem.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getMalignMirrorCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			cursesMenu.add(curseMenuItem);
		}
		if (player.hasDistractionCurse()) {
			JMenuItem curseMenuItem = new JMenuItem("Change Sex Distraction");
			curseMenuItem.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					DisplayCardsDialog dialog = new DisplayCardsDialog(player.getChangeSexCard());
					dialog.setJMenuBar(null);
					dialog.setVisible(true);
				}
			});
			cursesMenu.add(curseMenuItem);
		}
		if (cursesMenu.getComponentCount() > 0)
			add(cursesMenu);
	}
}
