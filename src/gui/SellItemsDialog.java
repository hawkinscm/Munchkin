
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.MultiSelectCardPanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import ai.AICardEvaluator;

import model.GM;
import model.Player;
import model.card.Card;
import model.card.ItemCard;
import model.card.TreasureCard;

/**
 * Dialog that lets a player sell his items for levels.
 */
public class SellItemsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// GUI controls for displaying info and getting input
	private MultiSelectCardPanel cardPanel;
	private CustomButton sellButton;
	private CustomButton doneButton;
	private JCheckBox doubleCheckBox;
	
	// Player who is selling items and the items available to be sold
	private Player player;
	private LinkedList<Card> valueCards;	
	
	/**
	 * Creates a new SellItemsDialog dialog.
	 * @param gui reference to the main controlling GUI
	 * @param p Player who is selling items
	 */
	public SellItemsDialog(GUI gui, Player p) {
		super(gui, p.getName() + "'s Items");
		
		// initialize variables and display GUI controls
		player = p;
		
		// loads all value cards available for the player to sell
		valueCards = new LinkedList<Card>();
		valueCards.addAll(player.getAllValueCards());
		
		if (player.isComputer())
			return;
		
		// if player has no value cards, inform him and display only a button to close dialog
		if (valueCards.isEmpty()) {
			JLabel infoLabel = new JLabel("You have no cards to sell.");
			getContentPane().add(infoLabel, c);
			
			c.gridy++;
			CustomButton okButton = new CustomButton("OK") {
				private static final long serialVersionUID = 1L;
				
				public void buttonPressed() {
					dispose();
				}
			};
			getContentPane().add(okButton, c);
			
			setJMenuBar(null);
			refresh();
			
			return;
		}
		
		// Button that signals that the player is done and closes dialog
		doneButton = new CustomButton("Done") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		
		// Button that sells the selected items for levels
		sellButton = new CustomButton("Sell") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				// if double check box selected, sell the item of most worth for double
				boolean sellForDouble = false;
				if (doubleCheckBox.isSelected()) {
					doubleCheckBox.setSelected(false);
					doubleCheckBox.setEnabled(false);
					sellForDouble = true;
					GM.sellingItemForDouble();
				}
				
				// calculates the total value of the selected items
				int totalSoldValue = 0;
				Iterator<Card> cardIter = cardPanel.getCheckedCards().iterator();
				int greatestSellValue = 0;
				while (cardIter.hasNext()) {
					TreasureCard item = (TreasureCard)cardIter.next();
					player.discard(item);
					int itemValue = item.getValue();
					totalSoldValue += itemValue;
					if (itemValue > greatestSellValue)
						greatestSellValue = itemValue;
				}
				if (sellForDouble)
					totalSoldValue += greatestSellValue;
				
				// for every 1000 GP sold, a level is gained
				int totalLevels = totalSoldValue / 1000;
				player.goUpLevels(totalLevels, false);

				sellButton.setEnabled(false);
				
				valueCards.clear();
				valueCards.addAll(player.getAllValueCards());
				
				getContentPane().remove(cardPanel);
				c.gridy = 1;
				cardPanel = new MultiSelectCardPanel(valueCards, 3, this, doneButton) {
					private static final long serialVersionUID = 1L;

					public void setCheckedInfoText() {
						checkedInfoLabel.setText(getSellTotalText());
					}
				};
				getContentPane().add(cardPanel, c);
				refresh();
			}
		};
		sellButton.setEnabled(false);
		
		c.gridy = 1;
		cardPanel = new MultiSelectCardPanel(valueCards, 3, sellButton, doneButton) {
			private static final long serialVersionUID = 1L;

			public void setCheckedInfoText() {
				checkedInfoLabel.setText(getSellTotalText());
			}
		};
		getContentPane().add(cardPanel, c);
			
		c.gridy = 2;
		// determines whether or not an item can be sold for double
		doubleCheckBox = new JCheckBox("Sell Item For Double");
		doubleCheckBox.setSelected(false);
		doubleCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				cardPanel.setCheckedInfoText();
			}
		});
		getContentPane().add(doubleCheckBox, c);
		if (!player.isHalfling())
			doubleCheckBox.setVisible(false);
		else if (GM.usedDoubleSell())
			doubleCheckBox.setEnabled(false);
		
		refresh();
	}
	
	/**
	 * Returns info about the results of selling the currently selected items.
	 * @return text displaying the total value of the selected items and how many levels selling them will provide
	 */
	private String getSellTotalText() {
		int totalValue = 0;
		
		int numItems = cardPanel.getCheckedCards().size();
		
		int greatestValue = 0;
		Iterator<Card> cardIter = cardPanel.getCheckedCards().iterator();
		while (cardIter.hasNext()) {
			int itemValue = ((TreasureCard)cardIter.next()).getValue();
			totalValue += itemValue;
			if (itemValue > greatestValue)
				greatestValue = itemValue;
		}
		
		if (doubleCheckBox.isSelected())
			totalValue += greatestValue;
		
		// if less that 1000 GP has been selected, disable the Sell Button
		// (at least one level must be gained to sell items)
		if (totalValue < 1000)
			sellButton.setEnabled(false);
		else
			sellButton.setEnabled(true);
		
		int totalLevels = totalValue / 1000;
		while(player.getLevel() + totalLevels >= 10)
			totalLevels--;
		
		String sellText = "Sell " + numItems + " item(s) at " + totalValue + " GP for " + totalLevels + " Level";
		if (totalLevels != 1)
			sellText += "s";
		
		return sellText;		
	}
	
	@Override
	public void setVisible(boolean b) {
		if (player.isComputer()) {
			if (valueCards.isEmpty() || player.getLevel() == 9)
				return;
			
			LinkedList<TreasureCard> cardsToSell = new LinkedList<TreasureCard>();
			int totalSellValue = 0;
			for (Card card : valueCards) {
				TreasureCard treasure = (TreasureCard)card;
				int treasureValue = treasure.getValue();
				if (treasureValue == 0)
					continue;
				
				int itemBonus = 0;
				if (treasure instanceof ItemCard)
					itemBonus += ((ItemCard)treasure).getBonus();
				if (AICardEvaluator.getCardValueToPlayer(treasure, player, player.getHandCards()) <= treasureValue + itemBonus) {
					// sort cards to sell from greatest to least GP value
					int sortIndex;
					for (sortIndex = 0; sortIndex < cardsToSell.size(); sortIndex++)
						if (treasureValue >= cardsToSell.get(sortIndex).getValue())
							break;
					
					cardsToSell.add(sortIndex, treasure);
					totalSellValue += treasureValue;
				}
			}			
			if (cardsToSell.isEmpty())
				return;
			
			boolean hasDoubleSell = player.isHalfling() && !GM.usedDoubleSell();
			if (hasDoubleSell)
				totalSellValue += cardsToSell.getFirst().getValue();
			
			// don't sell items past level 9, since you can't buy levels past level 9
			int levelGain = totalSellValue / 1000;
			if (levelGain + player.getLevel() > 9)
				levelGain = 9 - player.getLevel();
			if (levelGain <= 0)
				return;
			
			// only sell as many items as needed to get to the best sell value
			// i.e. don't sell an item if it doesn't help buy a new level
			Iterator<TreasureCard> treasureIter = cardsToSell.iterator();
			while (treasureIter.hasNext()) {
				TreasureCard treasure = treasureIter.next();
				int treasureValue = treasure.getValue();
				if (hasDoubleSell) {
					treasureValue *= 2;
					if (treasureIter.hasNext())
						treasureValue -= cardsToSell.get(cardsToSell.indexOf(treasure) + 1).getValue();
				}
				if (totalSellValue - treasureValue >= levelGain * 1000) {
					treasureIter.remove();
					totalSellValue -= treasureValue;
				}
				else
					hasDoubleSell = false;
			}
			
			// only use double sell ability when needed, since it can only be used once per turn
			String message = player.toString();
			if (player.isHalfling() && !GM.usedDoubleSell()) {
				if (totalSellValue - cardsToSell.getFirst().getValue() <= levelGain * 1000) {
					GM.sellingItemForDouble();
					message += ", using the Halfling's double sell ability,";
				}
			}
			
			// sell items and gain levels
			for (TreasureCard treasure : cardsToSell)
				player.discard(treasure);			
			player.goUpLevels(levelGain, false);
			
			LinkedList<Card> cards = new LinkedList<Card>();
			cards.addAll(cardsToSell);
			String title = message + " sold the following item(s) for " + levelGain + " level";
			if (levelGain > 1)
				title += "s";
			(new DisplayCardsDialog(cards, title)).setVisible(true);
		}
		else
			super.setVisible(b);
	}
}
