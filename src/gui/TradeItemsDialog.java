
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;
import gui.components.MultiSelectCardPanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

import model.GM;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import model.card.Card;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.TreasureCard;

/**
 * Dialog that allows items to be traded between players.
 */
public class TradeItemsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card Panels for viewing/selecting cards for each player
	private MultiSelectCardPanel initiatorPanel;
	private MultiSelectCardPanel otherPlayerPanel;
	
	// player who is requesting a trade
	private Player initiator;
		
	/**
	 * Creates a new TradeItemsDialog dialog.
	 * @param gui reference to the main controlling GUI
	 * @param player player who is requesting a trade
	 */
	public TradeItemsDialog(GUI gui, Player player) {
		super(gui, "Trade Items");
		
		// initialize variables and display GUI controls
		initiator = player;
				
		loadPlayerPanel(initiator);
		
		c.gridx = 1;
		c.gridy = 1;
		// Load list of other players into a drop down box.
		LinkedList<Player> players = new LinkedList<Player>();
		players.addAll(GM.getPlayers());
		players.remove(initiator);
		final JComboBox playerBox = new JComboBox(players.toArray());
		playerBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Trade With"));
		playerBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// display selected player's items for trade
				loadPlayerPanel((Player)playerBox.getSelectedItem());
			}	
		});
		getContentPane().add(playerBox, c);
		playerBox.setSelectedIndex(0);
		
		c.gridx = 1;
		c.gridy = 2;
		// allows player to request a trade of the selected items
		CustomButton tradeButton = new CustomButton("Trade") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Player otherPlayer = (Player)playerBox.getSelectedItem();
				LinkedList<Card> initiatorCheckedItems = initiatorPanel.getCheckedCards();
				LinkedList<Card> otherPlayerCheckedItems = otherPlayerPanel.getCheckedCards();
				
				if (initiatorCheckedItems.isEmpty() && otherPlayerCheckedItems.isEmpty())
					return;
				
				if (otherPlayer.isComputer()) {
					if (!willComputerTrade(initiatorCheckedItems, otherPlayerCheckedItems, otherPlayer)) {
						if (!initiator.isComputer()) {
							String message = otherPlayer.toString();
							if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
								message += " politely declines your offer.";
							else if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
								message += " feels that this trade is not in his best interests.";
							else if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
								message += " spits on your offer and tells you to go trade for some brains.";
							Messenger.display(message, "Trade Items");
						}
						return;
					}
					else if (!initiator.isComputer()) {
						String message = otherPlayer.toString();
						if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
							message += " gladly accepts your generous offer.";
						else if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
							message += " decides the trade is acceptable.";
						else if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
							message += " laughs maniacally, greedily accepts, and whispers \"Sucker!\".";
						Messenger.display(message, "Trade Items");						
					}
				}
				else {				
					// Allows other player to accept or decline the trade
					String prompt = otherPlayer.getName() + ", do you accept this trade?";
					int result = JOptionPane.showConfirmDialog(null, prompt, "Trade Items", JOptionPane.YES_NO_OPTION);
					if (result != JOptionPane.YES_OPTION)
						return;
				}
				
				// Takes cards from each player and gives them to the other.
				Iterator<Card> itemIter = initiatorCheckedItems.iterator();
				while (itemIter.hasNext()) {
					Card item = itemIter.next();
					if (!initiator.removeEquipmentItem(item))
						initiator.getCarriedItems().remove(item);						
					if (initiator.isCheatingItem(item))
						initiator.removeCheat();
					
					otherPlayer.addItem((TreasureCard)item);
				}
				itemIter = otherPlayerCheckedItems.iterator();
				while (itemIter.hasNext()) {
					Card item = itemIter.next();
					if (!otherPlayer.removeEquipmentItem(item))
						otherPlayer.getCarriedItems().remove(item);	
					if (otherPlayer.isCheatingItem(item))
						otherPlayer.removeCheat();
					
					initiator.addItem((TreasureCard)item);
				}
				
				loadPlayerPanel(initiator);
				loadPlayerPanel(otherPlayer);
			}
		};
		getContentPane().add(tradeButton, c);
				
		c.gridy = 3;
		c.gridx = 0;
		c.gridwidth = 3;
		// display warning about restrictions on Big Items
		getContentPane().add(new JLabel("WARNING: If you trade for a Big Item that you cannot carry, it will be discarded!"), c);
		c.gridwidth = 1;
		
		refresh();
	}
	
	/**
	 * Loads and displays a Card Panel for all tradable items that the selected Player has.
	 * @param player the player whose items will be displayed for trading
	 */
	private void loadPlayerPanel(Player player) {
		CustomButton nullButton = new CustomButton() {
			private static final long serialVersionUID = 1L;
			public void buttonPressed() {}
		};
		nullButton.setVisible(false);
		
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(player.getAllItems());
		MultiSelectCardPanel playerPanel = new MultiSelectCardPanel(cards, 3, nullButton, null) {
			private static final long serialVersionUID = 1L;
			public void setCheckedInfoText() {}
		};
		playerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), player.getName()));
		
		if (player == initiator) {
			if (initiatorPanel != null)
				getContentPane().remove(initiatorPanel);
			
			c.gridx = 0;
			initiatorPanel = playerPanel;
		}
		else {
			if (otherPlayerPanel != null)
				getContentPane().remove(otherPlayerPanel);
			
			c.gridx = 2;
			otherPlayerPanel = playerPanel;
		}
		
		c.gridheight = 3;
		c.gridy = 0;
		getContentPane().add(playerPanel, c);
		refresh();
		c.gridheight = 1;
	}
	
	/**
	 * Returns whether or not the computer player will take the initiator offered trade.
	 * @param initiatorTradeItems the items offered by the initiator
	 * @param otherPlayerTradeItems the computer player's items desired by the initiator
	 * @param otherPlayer the computer player deciding whether to accept the trade or not
	 * @return true if the trade is acceptable; false otherwise
	 */
	private boolean willComputerTrade(LinkedList<Card> initiatorTradeItems, LinkedList<Card> otherPlayerTradeItems, Player otherPlayer) {
		int tradeValue = 0;
		for (Card item : initiatorTradeItems)
			tradeValue += AICardEvaluator.getCardValueToPlayer(item, otherPlayer, otherPlayer.getHandCards());
		for (Card item : otherPlayerTradeItems)
			tradeValue -= AICardEvaluator.getCardValueToPlayer(item, otherPlayer, otherPlayer.getHandCards());
		
		// factor in initiator rank and trade value, depending on other player's computer level
		if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM) {
			int initiatorTradeValue = 0;
			for (Card item : otherPlayerTradeItems)
				initiatorTradeValue += AICardEvaluator.getCardValueToPlayer(item, initiator, null);
			for (Card item : initiatorTradeItems)
				initiatorTradeValue -= AICardEvaluator.getCardValueToPlayer(item, initiator, null);
			tradeValue -= initiatorTradeValue;
		}
		else if (otherPlayer.getPlayerType() == PlayerType.COMPUTER_HARD) {
			double playerFactor = GM.getPlayers().size() - AIManager.getRankedPlayers().indexOf(initiator);
			playerFactor /= (double)GM.getPlayers().size();
			playerFactor *= 2.0;
			int initiatorTradeValue = 0;
			for (Card item : otherPlayerTradeItems)
				initiatorTradeValue += AICardEvaluator.getCardValueToPlayer(item, initiator, null);
			for (Card item : initiatorTradeItems)
				initiatorTradeValue -= AICardEvaluator.getCardValueToPlayer(item, initiator, null);
			tradeValue -= initiatorTradeValue * playerFactor;
		}
		
		return (tradeValue >= 0);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (initiator.isComputer()) {
			// only try to trade 2/3 of the time
			if (!GM.isTestRun()) {
				int randomFactor = Randomizer.getRandom(3);
				if (randomFactor == 0)
					return;
			}
			
			// get a list of unused, little worth items
			LinkedList<AIValuedCard> leastValuedItems = new LinkedList<AIValuedCard>();
			for (EquipmentCard equipment : initiator.getUnequippedItems()) {
				int equipmentValue = AICardEvaluator.getCardValueToPlayer(equipment, initiator, initiator.getHandCards());
				
				int cardIdx;
				for (cardIdx = 0; cardIdx < leastValuedItems.size(); cardIdx++)
					if (equipmentValue < leastValuedItems.get(cardIdx).getValue())
						break;
				leastValuedItems.add(cardIdx, new AIValuedCard(equipment, equipmentValue));
			}
			if (!initiator.isElf()) {
				for (ItemCard item : initiator.getCarriedItems()) {
					if (item.getID() == Card.I_YUPPIE_WATER) {
						int itemValue = AICardEvaluator.getCardValueToPlayer(item, initiator, initiator.getHandCards());
						
						int cardIdx;
						for (cardIdx = 0; cardIdx < leastValuedItems.size(); cardIdx++)
							if (itemValue < leastValuedItems.get(cardIdx).getValue())
								break;
						leastValuedItems.add(cardIdx, new AIValuedCard(item, itemValue));
						break;
					}
				}
			}
			if (leastValuedItems.isEmpty())
				return;
			
			LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
			for (int playerIdx = rankedPlayers.size() - 1; playerIdx >= 0; playerIdx--) {
				Player otherPlayer = rankedPlayers.get(playerIdx);
				if (initiator == otherPlayer)
					continue;
				
				LinkedList<AIValuedCard> bestOtherPlayerTradeItems = new LinkedList<AIValuedCard>();
				for (EquipmentCard equipment : otherPlayer.getUnequippedItems()) {
					int tradeValue = AICardEvaluator.getCardValueToPlayer(equipment, initiator, initiator.getHandCards());
					tradeValue -= AICardEvaluator.getCardValueToPlayer(equipment, otherPlayer, null);
					if (tradeValue < 0)
						continue;
					
					int cardIdx;
					for (cardIdx = 0; cardIdx < bestOtherPlayerTradeItems.size(); cardIdx++)
						if (tradeValue > bestOtherPlayerTradeItems.get(cardIdx).getValue())
							break;
					bestOtherPlayerTradeItems.add(cardIdx, new AIValuedCard(equipment, tradeValue));
				}
				for (ItemCard item : otherPlayer.getCarriedItems()) {
					if (initiator.isElf() && !otherPlayer.isElf() && item.getID() == Card.I_YUPPIE_WATER) {
						int tradeValue = AICardEvaluator.getCardValueToPlayer(item, initiator, initiator.getHandCards());
						tradeValue -= AICardEvaluator.getCardValueToPlayer(item, otherPlayer, null);
						if (tradeValue <= 0)
							continue;
						
						int cardIdx;
						for (cardIdx = 0; cardIdx < bestOtherPlayerTradeItems.size(); cardIdx++)
							if (tradeValue > bestOtherPlayerTradeItems.get(cardIdx).getValue())
								break;
						bestOtherPlayerTradeItems.add(cardIdx, new AIValuedCard(item, tradeValue));
					}
					else if (initiator.isThief() && !otherPlayer.isThief() && item.getID() == Card.I_LOADED_DIE) {
						int tradeValue = AICardEvaluator.getCardValueToPlayer(item, initiator, initiator.getHandCards());
						tradeValue -= AICardEvaluator.getCardValueToPlayer(item, otherPlayer, null);
						if (tradeValue <= 0)
							continue;
						
						int cardIdx;
						for (cardIdx = 0; cardIdx < bestOtherPlayerTradeItems.size(); cardIdx++)
							if (tradeValue > bestOtherPlayerTradeItems.get(cardIdx).getValue())
								break;
						bestOtherPlayerTradeItems.add(cardIdx, new AIValuedCard(item, tradeValue));
					}
					else if (item.getID() == Card.I_POTION_OF_HALITOSIS)
						for (Card handCard : initiator.getHandCards())
							if (handCard.getID() == Card.M_FLOATING_NOSE) {
								int tradeValue = AICardEvaluator.getCardValueToPlayer(item, initiator, initiator.getHandCards());
								tradeValue -= AICardEvaluator.getCardValueToPlayer(item, otherPlayer, null);
								if (tradeValue <= 0)
									continue;
								
								int cardIdx;
								for (cardIdx = 0; cardIdx < bestOtherPlayerTradeItems.size(); cardIdx++)
									if (tradeValue > bestOtherPlayerTradeItems.get(cardIdx).getValue())
										break;
								bestOtherPlayerTradeItems.add(cardIdx, new AIValuedCard(item, tradeValue));
								break;
							}
				}
				
				if (bestOtherPlayerTradeItems.isEmpty())
					continue;
				
				LinkedList<AIValuedCard> bestInitiatorTradeItems = new LinkedList<AIValuedCard>();
				for (AIValuedCard valuedItem : leastValuedItems) {
					int tradeValue = AICardEvaluator.getCardValueToPlayer(valuedItem.getCard(), otherPlayer, null);
					tradeValue -= valuedItem.getValue();
					if (tradeValue <= 0)
						continue;
					
					int cardIdx;
					for (cardIdx = 0; cardIdx < bestInitiatorTradeItems.size(); cardIdx++)
						if (tradeValue > bestInitiatorTradeItems.get(cardIdx).getValue())
							break;
					bestInitiatorTradeItems.add(cardIdx, new AIValuedCard(valuedItem.getCard(), tradeValue));
				}
				
				// trade must be close to the same value for both players
				for (AIValuedCard bestOtherPlayerTradeItem : bestOtherPlayerTradeItems) {
					int playerRank = playerIdx + 2;
					if (!GM.isTestRun()) {
						int randomFactor = Randomizer.getRandom(playerRank);
						if (randomFactor != 0)
							continue;
					}
					
					int otherPlayerValue = 0;
					LinkedList<Card> initiatorTradeItems = new LinkedList<Card>();
					for (AIValuedCard valuedItem : bestInitiatorTradeItems) {
						otherPlayerValue += valuedItem.getValue();
						
						if (otherPlayerValue > bestOtherPlayerTradeItem.getValue())
							otherPlayerValue -= valuedItem.getValue();
						else
							initiatorTradeItems.add(valuedItem.getCard());
					}
					
					if (!initiatorTradeItems.isEmpty()) {
						if (otherPlayer.isComputer()) {
							LinkedList<Card> otherPlayerTradeItems = new LinkedList<Card>();
							otherPlayerTradeItems.add(bestOtherPlayerTradeItem.getCard());
							if (willComputerTrade(initiatorTradeItems, otherPlayerTradeItems, otherPlayer)) {
								String message = initiator + " traded " + initiatorTradeItems.getFirst();
								for (int cardIdx = 1; cardIdx < initiatorTradeItems.size(); cardIdx++)
									message += " and " + initiatorTradeItems.get(cardIdx);
								message += " for " + otherPlayer + "'s " + bestOtherPlayerTradeItem.getCard() + ".";
								Messenger.display(message, "Trade Items");
							}
							else
								continue;
						}
						else {
							// Allow other player to accept or decline the trade
							String prompt = "<html>&nbsp;" + initiator + " would like to trade " + initiatorTradeItems.getFirst();
							for (int cardIdx = 1; cardIdx < initiatorTradeItems.size(); cardIdx++)
								prompt += " and " + initiatorTradeItems.get(cardIdx);
							prompt += " for " + otherPlayer + "'s " + bestOtherPlayerTradeItem.getCard() + ".";
							prompt += "&nbsp;<br>&nbsp;" + otherPlayer.getName() + ", do you accept this trade?" + "&nbsp;</html>";
							final int YES_CHOICE = 1;
							OptionDialog dialog = new OptionDialog("Trade Items", prompt, "Yes", "No");
							dialog.setVisible(true);
							dialog.getChoice();
							
							if (dialog.getChoice() != YES_CHOICE)
								continue;
						}
						
						// Takes cards from each player and gives them to the other.
						for (Card item : initiatorTradeItems) {
							if (!initiator.removeEquipmentItem(item))
								initiator.getCarriedItems().remove(item);						
							if (initiator.isCheatingItem(item))
								initiator.removeCheat();
							
							otherPlayer.addItem((TreasureCard)item);
						}
						
						Card item = bestOtherPlayerTradeItem.getCard();
						if (!otherPlayer.removeEquipmentItem(item))
							otherPlayer.getCarriedItems().remove(item);	
						if (otherPlayer.isCheatingItem(item))
							otherPlayer.removeCheat();							
						initiator.addItem((TreasureCard)item);
						
						break;
					}
				}				
			}
		}
		else
			super.setVisible(b);
	}
}
