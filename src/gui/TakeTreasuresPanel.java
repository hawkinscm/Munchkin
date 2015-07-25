
package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;

import ai.AICardEvaluator;
import ai.AIManager;

import exceptions.EndGameException;
import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;
import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.Messenger;
import gui.components.PlayerLabel;

import model.Battle;
import model.CardPlayManager;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import model.card.Card;
import model.card.TreasureCard;

/**
 * Panel that handles the taking of treasures won from a battle.
 */
public class TakeTreasuresPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// GUI controls for displaying info and getting user input
	private CardPanel cardPanel;
	private JComboBox playerBox;
	private PlayerLabel playerLabel;
	private ButtonGroup takeOptionGroup;
	private JRadioButton handOption;
	private JRadioButton playOption;
	
	// Whether or not the treasure panel should display (depending on if computer players have already taken all the treasure)
	private boolean isFinished;
	// Who will take any extra treasures won (random if null)
	private Player extraTreasureTaker;
	// The order that players will take treasures; used whenever a computer player is involved
	private LinkedList<Player> takeTreasurePlayerOrder;
	
	// reference to the main controlling GUI
	private GUI gui;
	// the battle which was just won
	private Battle battle;
	
	/**
	 * Creates a new TakeTreasuresPanel panel that allows users to take treasures in a pre-decided order.
	 * @param g reference to the main controlling GUI
	 * @param b the battle which was just won
	 */
	public TakeTreasuresPanel(GUI g, Battle b) {
		super();
		
		gui = g;
		battle = b;
		isFinished = false;
		
		extraTreasureTaker = null;
		if (battle.activePlayer.hasEquipped(Card.E_KNEEPADS_OF_ALLURE))
			extraTreasureTaker = battle.activePlayer;
		else if (battle.isHelper() && battle.helper.isComputer()) {
			if (battle.helper.getPlayerType() == PlayerType.COMPUTER_EASY)
				extraTreasureTaker = battle.activePlayer;
			else if (battle.helper.getPlayerType() == PlayerType.COMPUTER_HARD)
				extraTreasureTaker = battle.helper;
		}
		else if (battle.isHelper() && battle.activePlayer.isComputer()) {
			if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
				extraTreasureTaker = battle.helper;
			else if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
				extraTreasureTaker = battle.activePlayer;
		}
		
		initialize();
	}
	
	/**
	 * Initialize variables and display GUI content.
	 */
	public void initialize() {		
		// initialize variables and display GUI controls		
		takeTreasurePlayerOrder = null;
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 0);
		c.gridx = 0;
		c.gridy = 0;
		
		c.gridwidth = 2;
		String prompt = battle.activePlayer.getName() + ", take the treasures you won!";
		if (battle.isHelper())
			prompt = "Take the treasures won, in the order you decided.";
		prompt += " NOTE: Playing an item card will place it in front of you, not use it; ";
		prompt += " items can only be used after they are in front of you.";
		JLabel promptLabel = new JLabel(prompt);
		add(promptLabel, c);
		c.gridwidth = 1;
		
		// draws the number of won treasures from the Treasure Deck
		boolean willGetTreasure = true;
		final LinkedList<Card> treasures = new LinkedList<Card>();
		for (int count = 1; count <= battle.getTreasureCount(); count++) {
			try {
				TreasureCard treasure = null;
				// allows Clerics to resurrect rather than draw if the situation is suitable
				if (battle.isHelper() && battle.activePlayer.canResurrect() && !GM.getTreasureDeck().getDiscardPile().isEmpty() && willGetTreasure) {
					if (battle.getTakeTreasurePlayerOrder() == null) {
						if (extraTreasureTaker != battle.activePlayer)
							willGetTreasure = false;
					}
					else if (count > battle.getTakeTreasurePlayerOrder().size())
						if (extraTreasureTaker != battle.activePlayer)
							willGetTreasure = false;
					else if (battle.getTakeTreasurePlayerOrder().get(count - 1) != battle.activePlayer)
						willGetTreasure = false;					
					
					if (willGetTreasure) {		
						ResurrectionDialog dialog = new ResurrectionDialog(battle.activePlayer, GM.getTreasureDeck().getDiscardPile().peek());
						dialog.setVisible(true);
						Card cardToDiscard = dialog.getCardToDiscard();
						if (cardToDiscard != null) {
							treasure = (TreasureCard)GM.getTreasureDeck().takeDiscard();
							battle.activePlayer.discard(cardToDiscard);
						}
					}
				}
				
				if (treasure == null)
					treasure = (TreasureCard)GM.getTreasureDeck().drawCard();
				treasures.add(treasure);
			}
			catch (PlayImmediatelyException ex) {
				treasures.addAll(GM.getTreasureDeck().getHoardCards(battle.activePlayer, battle.isHelper()));
			}
			catch (NoCardsLeftException ex) { break; }
		}
		
		// if there were no treasures left to draw or none were earned, finish use of this panel
		if (treasures.isEmpty()) {
			finish();
			return;
		}
		
		c.gridy++;
		// if another player helped in battle and no computer players were involved, 
		// use a drop down box to selected which player gets the selected treasure
		if (battle.isHelper() && !battle.activePlayer.isComputer() && !battle.helper.isComputer()) {
			LinkedList<Player> players = new LinkedList<Player>();
			players.add(battle.activePlayer);
			players.add(battle.helper);
			playerBox = new JComboBox(players.toArray());
			playerBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Player selectedPlayer = (Player)playerBox.getSelectedItem();
					playerLabel.setPlayer(selectedPlayer);
					validate();
					repaint();
				}				
			});
			add(playerBox, c);
		}
		else if (!battle.isHelper() && battle.activePlayer.isComputer()) {
			for (Card treasure : treasures)
				battle.activePlayer.addCard(treasure);
			
			AIManager.playHandCards(battle.activePlayer, treasures);
				
			finish();
			return;
		}
		else if (battle.isHelper()) {
			takeTreasurePlayerOrder = battle.getTakeTreasurePlayerOrder();
			if (takeTreasurePlayerOrder == null)
				takeTreasurePlayerOrder = new LinkedList<Player>();
			
			while (takeTreasurePlayerOrder.size() > treasures.size())
				takeTreasurePlayerOrder.removeLast();
			
			while (takeTreasurePlayerOrder.size() < treasures.size()) {
				Player nextTaker = extraTreasureTaker;				
				if (nextTaker == null) {
					int randomNum = Randomizer.getRandom(2);
					if (randomNum == 0)
						nextTaker = battle.activePlayer;
					else
						nextTaker = battle.helper;
				}
				
				takeTreasurePlayerOrder.add(nextTaker);
			}
		}
		
		c.gridy++;
		Player selectedPlayer = battle.activePlayer;
		if (battle.activePlayer.isComputer() && battle.isHelper() && !battle.helper.isComputer())
			selectedPlayer = battle.helper;
		playerLabel = new PlayerLabel(selectedPlayer);
		add(playerLabel, c);
		
		c.gridy++;
		// allow the taking player to choose whether they want the treasure card to go in hand or as a carried item
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		takeOptionGroup = new ButtonGroup();
		handOption = new JRadioButton("Hand");
		playOption = new JRadioButton("Play");
		takeOptionGroup.add(handOption);
		takeOptionGroup.add(playOption);
		optionPanel.add(handOption);
		optionPanel.add(playOption);
		add(optionPanel, c);
		playOption.setSelected(true);
		
		// Button that allows selected player to take selected treasure to keep in hand or play as carried item.
		CustomButton takeButton = new CustomButton("Take") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Player player = battle.activePlayer;
				if (playerBox != null)
					player = (Player)playerBox.getSelectedItem();
				else if (takeTreasurePlayerOrder != null)
					player = takeTreasurePlayerOrder.getFirst();
				TreasureCard card = (TreasureCard)cardPanel.getSelectedCard();
				
				Player activePlayer = GM.getActivePlayer();
				GM.setActivePlayer(player);
				if (takeOptionGroup.isSelected(handOption.getModel()))
					player.addCard(card);
				else if (takeOptionGroup.isSelected(playOption.getModel())) {
					try {
						player.addCard(card);
						if (!CardPlayManager.playCard(player, card)) {
							GM.setActivePlayer(activePlayer);
							player.getHandCards().remove(card);
							return;
						}
						
						playerLabel.updatePlayerInfo();
					}
					catch (EndGameException ex) {
						GM.setActivePlayer(activePlayer);
						gui.endGame();
						return;
					}
				}
				GM.setActivePlayer(activePlayer);
				
				treasures.remove(card);
				cardPanel.removeSelectedImage();
				if (takeTreasurePlayerOrder != null)
					takeTreasurePlayerOrder.removeFirst();
				
				if (treasures.isEmpty()) {
					finish();
					return;
				}
				
				handleComputerPlayers(treasures);
			}
		};
		
		c.gridy = 1;
		c.gridx++;
		c.gridheight = 3;
		cardPanel = new CardPanel(treasures, 2, takeButton, null);
		cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(cardPanel, c);
		c.gridheight = 1;
		
		handleComputerPlayers(treasures);
	}
	
	/**
	 * Decides which treasures the computer players will take when it's their turn.
	 * @param treasures the list of treasures that are available to take
	 */
	private void handleComputerPlayers(LinkedList<Card> treasures) {
		if (takeTreasurePlayerOrder == null)
			return;
		
		while (takeTreasurePlayerOrder.getFirst().isComputer()) {
			Player currentPlayer = takeTreasurePlayerOrder.removeFirst();
			Card bestCard = null;
			int bestValue = 0;
			for (Card treasure : treasures) {
				int treasureValue = AICardEvaluator.getCardValueToPlayer(treasure, currentPlayer, currentPlayer.getHandCards());
				if (bestCard == null || treasureValue > bestValue) {
					bestCard = treasure;
					bestValue = treasureValue;
				}
			}

			cardPanel.setSelectedImageLabel(bestCard);
			treasures.remove(bestCard);
			cardPanel.removeSelectedImage();
			currentPlayer.addCard(bestCard);
			Messenger.display(currentPlayer + " took the " + bestCard + " Treasure.", "Defeated Monster Treasures");
			Player activePlayer = GM.getActivePlayer();
			GM.setActivePlayer(currentPlayer);
			AIManager.playHandCard(currentPlayer, bestCard);
			GM.setActivePlayer(activePlayer);
			
			if (takeTreasurePlayerOrder.isEmpty()) {
				finish();
				return;
			}
		}
	}
	
	/**
	 * Closes this panel and returns control to the main panel.
	 */
	private void finish() {
		gui.getContentPane().removeAll();
		GM.moveToBattlePhase();
		GM.moveNextPhase();
		gui.showMainDisplay();
		
		isFinished = true;
	}
	
	/**
	 * Whether or not all the treasures have been taken and the panel is finished.
	 * @return true if the treasures are all taken and the panel is finished; false otherwise
	 */
	public boolean isFinished() {
		return isFinished;
	}
}
