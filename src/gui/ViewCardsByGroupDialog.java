package gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import model.DoorDeckFactory;
import model.TreasureDeckFactory;
import model.card.*;
import model.card.EquipmentCard.EquipmentType;

import gui.components.CustomButton;
import gui.components.CustomDialog;

/**
 * Display any cards from the game deck sorted by group.
 */
public class ViewCardsByGroupDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private enum CardGroup {
		ALL,
		DOOR,
		TREASURE,
		MONSTER,
		RACE,
		CLASS,
		CURSE,
		MONSTER_ENHANCER,
		OTHER_DOOR,		
		EQUIPMENT,
		CARRIED_ITEM,
		GO_UP_A_LEVEL,
		OTHER_TREASURE,
		SMALL_ITEMS,
		BIG_ITEMS,
		WEAPONS,
		BELOW_WAIST,
		HEADGEAR,
		ONE_HANDED,
		TWO_HANDED,
		ARMOR,
		FOOTGEAR,
		OTHER_EQUIPMENT;		
	}
	
	/**
	 * Creates a new View Card By Group Dialog.
	 * @param owner owner of this dialog (frame that calls it)
	 */
	public ViewCardsByGroupDialog() {
		super("View Any Cards By Group");
		
		final Stack<Card> doorDeck = DoorDeckFactory.buildDeck();
		final Stack<Card> treasureDeck = TreasureDeckFactory.buildDeck();
		
		c.anchor = GridBagConstraints.WEST;
		getContentPane().add(new JLabel("Select a Group:"), c);
		
		c.gridx++;
		getContentPane().add(new JLabel("Select Card(s):"), c);
		
		c.gridy++;
		final DefaultListModel listModel = new DefaultListModel();
		final JList selectCardsList = new JList(listModel);
		JScrollPane scrollPane = new JScrollPane(selectCardsList);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane, c);
				
		c.gridx--;
		final JComboBox selectGroupComboBox = new JComboBox(CardGroup.values());
		selectGroupComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardGroup group = (CardGroup)selectGroupComboBox.getSelectedItem();
				if (group == null)
					return;
				
				LinkedList<Card> cards = new LinkedList<Card>();
				switch (group) {
				case ALL:
					cards.addAll(doorDeck);
					cards.addAll(treasureDeck);
					break;
				case DOOR:
					cards.addAll(doorDeck);
					break;
				case TREASURE:
					cards.addAll(treasureDeck);
					break;
				case MONSTER:
					for (Card card : doorDeck)
						if (card instanceof MonsterCard)
							cards.add(card);
					break;
				case RACE:
					for (Card card : doorDeck)
						if (card instanceof RaceCard)
							cards.add(card);
					break;
				case CLASS:
					for (Card card : doorDeck)
						if (card instanceof ClassCard)
							cards.add(card);
					break;
				case CURSE:
					for (Card card : doorDeck)
						if (card instanceof CurseCard)
							cards.add(card);
					break;
				case MONSTER_ENHANCER:
					for (Card card : doorDeck)
						if (card instanceof MonsterEnhancerCard)
							cards.add(card);
					break;
				case OTHER_DOOR:
					for (Card card : doorDeck)
						if (card instanceof OtherDoorCard)
							cards.add(card);
					break;
				case EQUIPMENT:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard)
							cards.add(card);
					break;
				case CARRIED_ITEM:
					for (Card card : treasureDeck)
						if (card instanceof ItemCard)
							cards.add(card);
					break;
				case GO_UP_A_LEVEL:
					for (Card card : treasureDeck)
						if (card instanceof GoUpLevelCard)
							cards.add(card);
					break;
				case OTHER_TREASURE:
					for (Card card : treasureDeck)
						if (card instanceof OtherTreasureCard)
							cards.add(card);
					break;
				case SMALL_ITEMS:
					for (Card card : treasureDeck) {
						if (card instanceof ItemCard)
							cards.add(card);
						else if (card instanceof EquipmentCard && !((EquipmentCard)card).isBig())
							cards.add(card);
					}
					break;
				case BIG_ITEMS:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).isBig())
							cards.add(card);
					break;
				case WEAPONS:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).isWeapon())
							cards.add(card);
					break;
				case BELOW_WAIST:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).isBelowWaist())
							cards.add(card);
					break;
				case HEADGEAR:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).getEquipmentType() == EquipmentType.HEADGEAR)
							cards.add(card);
					break;
				case ONE_HANDED:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).getEquipmentType() == EquipmentType.ONE_HAND)
							cards.add(card);
					break;
				case TWO_HANDED:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).getEquipmentType() == EquipmentType.TWO_HANDS)
							cards.add(card);
					break;
				case ARMOR:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).getEquipmentType() == EquipmentType.ARMOR)
							cards.add(card);
					break;
				case FOOTGEAR:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).getEquipmentType() == EquipmentType.FOOTGEAR)
							cards.add(card);
					break;
				case OTHER_EQUIPMENT:
					for (Card card : treasureDeck)
						if (card instanceof EquipmentCard && ((EquipmentCard)card).getEquipmentType() == EquipmentType.OTHER)
							cards.add(card);
					break;
				default:
					break;
				}
				
				listModel.removeAllElements();
				for(Card card : cards)
					listModel.addElement(card);
				
				refresh();
			}
		});
		getContentPane().add(selectGroupComboBox, c);
		selectGroupComboBox.setSelectedIndex(0);
		
		c.gridx += 2;
		CustomButton displayButton = new CustomButton("Display") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				LinkedList<Card> selectedCards = new LinkedList<Card>();
				for (Object obj : selectCardsList.getSelectedValues())
					selectedCards.add((Card)obj);
				
				if (selectedCards.isEmpty())
					return;
				
				(new DisplayCardsDialog(selectedCards, "View Cards")).setVisible(true);
			}			
		};
		getContentPane().add(displayButton, c);
		
		refresh();
	}
}
