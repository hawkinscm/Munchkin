
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

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
 * Dialog that lets a Thief try to steal from another player
 */
public class StealItemDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Thief who is trying to steal
	private Player player;	
	// player who the Thief is trying to steal from
	private Player victim;
	// item selected to try to steal
	private TreasureCard itemToSteal;
	
	// GUI controls for player input and displaying info
	private JComboBox playerBox;
	private CardPanel cardPanel;
	
	/**
	 * Creates a new StealItemDialog dialog.
	 * @param p Thief who is trying to steal
	 */
	public StealItemDialog(Player p) {
		super("Theft");
		
		// initialize variables and display GUI controls
		player = p;
		victim = null;
		itemToSteal = null;
		cardPanel = null;
		
		if (player.isComputer())
			return;
		
		c.gridwidth = 2;
		JLabel infoLabel = new JLabel(player.getName() + ", choose a small item to try to steal.");
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		// Loads all other players
		LinkedList<Player> players = new LinkedList<Player>();
		players.addAll(GM.getPlayers());
		players.remove(player);
		
		// Tries to steal the selected item
		final CustomButton selectButton = new CustomButton("Select") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				itemToSteal = (TreasureCard)cardPanel.getSelectedCard();
				victim = (Player)playerBox.getSelectedItem();
				dispose();
			}
		};
		
		// Backs out of trying to steal and closes the dialog
		final CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};
		
		c.gridy = 1;
		// allows Thief to select a player and view the items available to steal
		playerBox = new JComboBox(players.toArray());
		playerBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Player victim = (Player)playerBox.getSelectedItem();
				
				LinkedList<Card> items = new LinkedList<Card>();
				items.addAll(victim.getCarriedItems());
				
				for (EquipmentCard item : victim.getAllEquipment())
					if (!item.isBig())
						items.add(item);
				
				c.gridy = 1;
				c.gridx = 1;
				if (cardPanel != null)
					getContentPane().remove(cardPanel);
				
				cardPanel = new CardPanel(items, 3, selectButton, cancelButton);
				getContentPane().add(cardPanel, c);
				refresh();
				c.gridx = 0;
			}	
		});
		getContentPane().add(playerBox, c);
		playerBox.setSelectedIndex(0);
		
		refresh();	
	}
	
	@Override
	public void setVisible(boolean b) {
		if (player.isComputer()) {
			if (player.getHandCards().isEmpty())
				return;
						
			LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
			int bestItemValue = 0;
			for (Player currentPlayer : rankedPlayers) {
				if (currentPlayer == player)
					continue;
				// easy computer will never steal from player in last place
				if (player.getPlayerType() == PlayerType.COMPUTER_EASY && currentPlayer == rankedPlayers.getLast())
					continue;
				
				LinkedList<TreasureCard> smallItems = new LinkedList<TreasureCard>();
				smallItems.addAll(currentPlayer.getCarriedItems());
				for (EquipmentCard item : currentPlayer.getAllEquipment())
					if (!item.isBig())
						smallItems.add(item);
				
				for (TreasureCard item : smallItems) {
					int itemValue = AICardEvaluator.getCardValueToPlayer(item, player, player.getHandCards());
					if (rankedPlayers.indexOf(currentPlayer) < rankedPlayers.indexOf(player))
						itemValue += AICardEvaluator.getCardValueToPlayer(item, currentPlayer, null);
					if (itemValue > bestItemValue) {
						victim = currentPlayer;
						itemToSteal = item;
						bestItemValue = itemValue;
					}
				}
			}
				
			if (itemToSteal == null)
				return;
				
			int levelLoss = AIManager.LEVEL_VALUE;
			if (player.getLevel() == 1)
				levelLoss = 0;

			AIValuedCard leastValuedCard = AIManager.getLeastValuedHandCards(player, player.getHandCards()).getFirst();
			
			int risk = 50;			
			if (player.hasChickenOnHead())
				risk = 67;
			ItemCard loadedDieCard = null;
			for (TreasureCard item : player.getAllValueCards())
				if (item.getID() == Card.I_LOADED_DIE) {
					int newItemValue = bestItemValue - (AICardEvaluator.getCardValueToPlayer(item, player, player.getHandCards()) * risk) / 100;
					if (newItemValue > ((bestItemValue * (100 - risk) - levelLoss * risk) / 100)) {
						bestItemValue = newItemValue;
						risk = 0;
						loadedDieCard = (ItemCard)item;
					}
					break;
				}
			int chanceToSteal = 100 - risk;
		
			if (bestItemValue * chanceToSteal > leastValuedCard.getValue() * 100 + (levelLoss * risk)) {
				Card cardToDiscard = leastValuedCard.getCard();
				player.discard(cardToDiscard);
				String message = player + " discarded the following card to try to steal ";
				message += victim + "'s " + itemToSteal;
				(new DisplayCardsDialog(cardToDiscard, message)).setVisible(true);
					
				int roll = Randomizer.rollDice(player);
				message = player + " rolled a " + roll;
				if (roll < 4 && loadedDieCard != null) {
					player.discard(loadedDieCard);
					roll = 6;
					String gender = "his";
					if (player.isFemale())
						gender = "her";
					message = player + " used " + gender + " Loaded Die item"; 
				}
					
				if (roll > 3) {
					if (!victim.removeEquipmentItem(itemToSteal))
						victim.getCarriedItems().remove(itemToSteal);
					if (itemToSteal == victim.getCheatingItemCard())
						victim.removeCheat();
					player.addItem(itemToSteal);
					message += " and stole the following from " + victim;
					(new DisplayCardsDialog(itemToSteal, message)).setVisible(true);
				}
				else {
					if (player.getLevel() > 1)
						message += " and lost a level.";
					else
						message += " and failed to steal the item.";
					player.goDownLevel();
					Messenger.display(message, "Failed Steal");
				}
				
				setVisible(true);
			}
			else {
				itemToSteal = null;
				victim = null;
			}				
		}
		else
			super.setVisible(b);
	}
	
	/**
	 * Returns the item selected to try to steal.
	 * @return the item selected to try to steal, or null if operation canceled
	 */
	public TreasureCard getItemToSteal() {
		return itemToSteal;
	}
	
	/**
	 * Returns the player who the thief is trying to steal from.
	 * @return the player who the thief is trying to steal from, or null if operation canceled
	 */
	public Player getVictim() {
		return victim;
	}
}
