
package gui;

import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ai.AIManager;
import ai.AIValuedCard;

import model.Battle;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import model.card.Card;
import model.card.DoorCard;
import model.card.MonsterCard;

/**
 * Dialog that allows a player to request help of another player during a battle.
 */
public class AskHelpDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
		
	private final String EASY_REPLY_YES = " would be more than happy to help ";
	private final String EASY_REPLY_NO = " apologizes profusely but feels it would be unfair to help.";
	private final String MEDIUM_REPLY_YES = " decides to help ";
	private final String MEDIUM_REPLY_NO = " decides it is not in his best interest to help.";
	private final String HARD_REPLY_YES = " thinks you're pathetic and will show you how to win ";
	private final String HARD_REPLY_NO = " refuses to help and hopes you snuff it.";
	
	// List of all players in the game
	private LinkedList<Player> players;
	// The currently on-going battle
	private Battle battle;
	// Whether or not player is wearing Kneepads of Allure
	private boolean hasAllure;
		
	// Drop down box to display all players in the game and allow one for selection
	private JComboBox playerBox;
	
	/**
	 * Creates a new AskHelpDialog Dialog.
	 * @param b currently on-going battle
	 * @param hasKneepads true if active player is wearing Kneepads of Allure; false otherwise
	 */
	public AskHelpDialog(Battle b, boolean hasKneepads) {
		super("Ask For Help");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		battle = b;
		hasAllure = hasKneepads;
		
		// Set players to contain all players in the game, except the active player
		players = new LinkedList<Player>();
		Iterator<Player> playerIter = GM.getPlayers().iterator();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			if (player != battle.activePlayer)
				players.add(player);
		}		
		
		// Display whether or not the Kneepads are being used
		final JLabel infoLabel = new JLabel();
		if (hasAllure)
			infoLabel.setText("Using Kneepads of Allure!");
		
		// Label that will ask a player if they want to help or not
		final JLabel questionLabel = new JLabel();
		
		// Button that will let a player confirm that he will help and load him as a helper
		final CustomButton yesButton = new CustomButton("YES") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				battle.addHelper(players.get(playerBox.getSelectedIndex()));
				String message = "Decide now how any treasures won will be divided and the order that they will be chosen.";
				Messenger.display(message, "Adding Helper");
				dispose();
			}
		};
		
		// Button that will let a player decline from helping.
		final CustomButton noButton = new CustomButton("NO") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				battle.addHelpRefuser(players.get(playerBox.getSelectedIndex()));
				playerBox.setSelectedIndex(-1);
			}
		};
		if (hasAllure) 
			noButton.setEnabled(false);
		
		// Drop down box that will let the active player request help from another player by selecting their name.
		// If the Kneepads are being used the requested player cannot decline the request.
		playerBox = new JComboBox(players.toArray());
		playerBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					questionLabel.setText("");
					yesButton.setVisible(false);
					noButton.setVisible(false);
					
					refresh();
					return;
				}
				
				Player selectedPlayer = (Player)playerBox.getSelectedItem();
				questionLabel.setText(selectedPlayer + " are you going to join this battle?");
				yesButton.setVisible(true);
				noButton.setVisible(true);
				
				if (hasAllure) {
					battle.addHelper(selectedPlayer);
					String reply = battle.activePlayer + " uses the Kneepads of Allure. ";
					reply += selectedPlayer + " has joined the battle as a helper.";
					Messenger.display(reply, "Ask For Help: Accept");
					dispose();
					return;
				}
				else if (selectedPlayer.isComputer() || battle.activePlayer.isComputer())
					makeHelpDecision(selectedPlayer);
				else				
					refresh();
			}
		});
		playerBox.setSelectedIndex(-1);
		
		// Display the GUI controls
		getContentPane().add(infoLabel, c);		
		c.gridy++;
		getContentPane().add(playerBox, c);		
		c.gridy++;
		getContentPane().add(questionLabel, c);		
		c.gridx++;
		getContentPane().add(yesButton, c);		
		c.gridx++;
		getContentPane().add(noButton, c);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (battle.activePlayer.isComputer()) {
			playerBox.setEnabled(false);
			
			// if don't need help, don't ask
			int playersLevel = battle.getPlayersLevel();
			int monstersLevel = battle.getMonstersLevel();
			int neededLevel = monstersLevel - playersLevel;
			if (!battle.activePlayer.isWarrior())
				neededLevel++;
			if (neededLevel <= 0)
				return;
			
			// Ask help from lowest ranked players to highest ranked players until someone accepts or everyone declines.
			boolean isHigherRank = true;
			LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
			for (int playerIdx = rankedPlayers.size() - 1; playerIdx >= 0; playerIdx--) {
				Player requestee = rankedPlayers.get(playerIdx);
				if (requestee == battle.activePlayer || (battle.hasRefusedToHelp(requestee) && !hasAllure)) {
					isHigherRank = false;
					continue;
				}
				
				// Don't ask help from anyone whose battle level isn't enough to help win the current battle situation.
				battle.helper = requestee;
				playersLevel = battle.getPlayersLevel();
				battle.helper = null;
				neededLevel = monstersLevel - playersLevel;
				if (!battle.activePlayer.isWarrior() && !requestee.isWarrior())
					neededLevel++;
				if (neededLevel > 0)
					continue;
				
				// Don't ask for help if ranked-based helper benefit too much
				int helperBenefit = 0;
				int playerBenefit = 0;
				int playerLevels = 0;
				int playerTreasures = battle.getTreasureCount();
				for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
					MonsterCard monster = battle.getMonster(monsterIdx);
					if (monster.getID() == Card.M_AMAZON && requestee.isFemale())
						helperBenefit += AIManager.UNKNOWN_CARD_VALUE;
					else if (!(monster.getID() == Card.M_LAWYER && requestee.isThief())) {
						playerLevels += battle.getWinLevelCount(monster);
						playerTreasures += battle.getWinTreasureCount(monster);
					}
					boolean hasMate = false;
					for (DoorCard enhancer : battle.getMonsterEnhancers(monster))
						if (enhancer.getID() == Card.OD_MATE)
							hasMate = true;
					playerBenefit += AIManager.getBadStuffCost(monster, hasMate, battle.activePlayer, false, battle.activePlayer.getHandCards());
				}
				if (requestee.isElf()) {
					int helperLevels = battle.getMonsterCount();
					for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
						if (battle.getMonster(monsterIdx).equals("Amazon") && requestee.isFemale()) {
							helperLevels--;
							continue;
						}
						if (battle.getMonster(monsterIdx).equals("Lawyer") && requestee.isThief()) {
							helperLevels--;
							continue;
						}
						for (DoorCard enhancer : battle.getMonsterEnhancers(monsterIdx))
							if (enhancer.getID() == Card.OD_MATE)
								helperLevels++;
					}
					
					if (requestee.getLevel() + helperLevels >= 10) {
						if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
							continue;
						else if (battle.activePlayer.getLevel() + playerLevels < 10)
							continue;
					}
						
					helperBenefit += helperLevels * AIManager.LEVEL_VALUE;
				}
				else if (!hasAllure && playerTreasures > 0) {
					helperBenefit += AIManager.UNKNOWN_CARD_VALUE;
					playerTreasures -= 1;
				}
				
				if (battle.activePlayer.getLevel() + playerLevels >= 10)
					playerLevels = 10;
				playerBenefit += playerLevels * AIManager.LEVEL_VALUE + playerTreasures * AIManager.UNKNOWN_CARD_VALUE;
				double numPlayers = rankedPlayers.size();
				double rankFactor = (numPlayers - (double)playerIdx) / numPlayers;
				rankFactor *= 2.0;
				if (rankFactor <= 1.0 || battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_HARD || !isHigherRank)
					helperBenefit *= rankFactor;
				if (playerBenefit < helperBenefit)
					continue;
				
				// Ask for help
				playerBox.setSelectedItem(requestee);
				if (battle.helper != null)
					return;
			}
				
			if (hasAllure && battle.activePlayer.getPlayerType() != PlayerType.COMPUTER_EASY) {
				for (Player currentPlayer : rankedPlayers) {
					if (battle.activePlayer == currentPlayer) {
						if (battle.activePlayer.getPlayerType() != PlayerType.COMPUTER_MEDIUM)
							break;
					}
					else if (!currentPlayer.isElf()) {
						playerBox.setSelectedItem(currentPlayer);
						return;
					}
				}
			}
		}
		else
			super.setVisible(true);
	}
	
	/**
	 * Determines whether the selected player will help in the battle or not, 
	 * depending of the player/helper benefit and the treasures offered then 
	 * accepts or declines accordingly if a computer or allows player to accept offer or not.
	 * @param selectedPlayer computer player to check
	 */
	private void makeHelpDecision(Player selectedPlayer) {
		battle.helper = selectedPlayer;
		
		// if helper is computer and his added battle level isn't enough to win, don't help
		boolean evaluateForHelper = false;
		if (selectedPlayer.isComputer()) {
			if (!battle.activePlayer.isComputer()) {
				int playersLevel = battle.getPlayersLevel();
				int monstersLevel = battle.getMonstersLevel();
				int winLevel = playersLevel - monstersLevel;
				if (battle.activePlayer.isWarrior() || selectedPlayer.isWarrior())
					winLevel++;
				if (winLevel <= 0) {
					String reply = "";
					if (selectedPlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
						reply = EASY_REPLY_NO;
					else if (selectedPlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
						reply = MEDIUM_REPLY_NO;
					else if (selectedPlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
						reply = HARD_REPLY_NO;
							
					Messenger.display(selectedPlayer + reply, "Ask For Help: Decline");
				
					battle.helper = null;
					playerBox.setSelectedIndex(-1);
					return;
				}
			}
					
			evaluateForHelper = true;
		}
		
		
		// Gather battle information
		int playerGain = 0;
		int helperGain = 0;
		int treasures = battle.getTreasureCount();	
		int levels = 0;				
		int helperLevels = 0;			
		
		for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++) {
			MonsterCard monster = battle.getMonster(monsterIndex);
			boolean hasMate = false;
			for (Card card : battle.getMonsterEnhancers(monster))
				if (card.getID() == Card.OD_MATE)
					hasMate = true;
			
			if (monster.getID() == Card.M_AMAZON && selectedPlayer.isFemale())
				treasures += 1;
			else if (monster.getID() == Card.M_LAWYER && selectedPlayer.isThief()) {
				int leastItemValues = 0;
				int count = 0;
				LinkedList<Card> handCards = null;
				if (evaluateForHelper)
					handCards = selectedPlayer.getHandCards();
				for (AIValuedCard valuedCard : AIManager.getLeastValuedItems(selectedPlayer, handCards)) {
					leastItemValues += valuedCard.getValue();
					count++;
					if (count >= 2)
						break;
				}
				
				int tradeValue = AIManager.UNKNOWN_CARD_VALUE * 2 - leastItemValues;
				if (tradeValue > 0)
					helperGain += tradeValue;
			}
			else {
				if (monster.getID() == Card.M_TONGUE_DEMON && !selectedPlayer.getAllItems().isEmpty()) {
					LinkedList<Card> handCards = null;
					if (evaluateForHelper)
						handCards = selectedPlayer.getHandCards();
					helperGain -= AIManager.getLeastValuedItems(selectedPlayer, handCards).getFirst().getValue();
				}
				
				levels += battle.getWinLevelCount(monster);
				treasures += battle.getWinTreasureCount(monster);
				if (selectedPlayer.isElf()) {
					helperLevels++;
					if (hasMate)
						helperLevels++;
				}
				
				LinkedList<Card> handCards = null;
				if (evaluateForHelper)
					handCards = selectedPlayer.getHandCards();
				helperGain -= AIManager.getBadStuffCost(monster, hasMate, selectedPlayer, false, handCards) / 2;
			}
			
			LinkedList<Card> handCards = null;
			if (!evaluateForHelper)
				handCards = battle.activePlayer.getHandCards();
			playerGain += AIManager.getBadStuffCost(monster, hasMate, battle.activePlayer, false, handCards);
		}
		battle.helper = null;
		
		playerGain += levels * AIManager.LEVEL_VALUE;
		helperGain += helperLevels * AIManager.LEVEL_VALUE;
						
		// Determine minimum value required for helper to join battle based on active player rank and benefit 
		LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
		int numPlayers = rankedPlayers.size();
		int playerRank = rankedPlayers.indexOf(battle.activePlayer) + 1;
		int rankPercentMultiplier = 100;
		if (playerRank == numPlayers)
			rankPercentMultiplier = 50;
		else if (playerRank == 1)
			rankPercentMultiplier = 200;
		else if (playerRank == 2 && numPlayers > 4)
			rankPercentMultiplier = 150;
		else if ((playerRank == 4 && numPlayers == 5) || playerRank == 5)
			rankPercentMultiplier = 75;
		int minValueToHelp = (playerGain * rankPercentMultiplier) / 100;
		
		// If treasure taking order not already decided,
		// calculate the total value of the known treasures that would be won. 
		int maxTreasureValue = 0;
		// If number of treasures is even, treasure values are slightly skewed
		// and sum equals number of treasures minus 100 times 1/2 the number of treasures. 
		// (EX. 4: 600 + 700 + 800 + 900 = 3000 = 3200 - (100 * 2)
		if (treasures % 2 == 0)
			maxTreasureValue = (treasures * AIManager.UNKNOWN_CARD_VALUE) - (100 * (treasures / 2)); 
		// If number of treasures is odd, treasure values are perfectly curved
		// and sum equals number of treasures. (EX. 5: 600 + 700 + 800 + 900 + 1000 = 5000)
		else
			maxTreasureValue = treasures * AIManager.UNKNOWN_CARD_VALUE;
		
		// Each type of computer player will respond based on player versus personal benefit
		// and their own programmed level of generosity.
		boolean willHelp = false;
		String reply = "";
		PlayerType computerAllowanceType = selectedPlayer.getPlayerType();
		if (!evaluateForHelper) {
			if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
				computerAllowanceType = PlayerType.COMPUTER_HARD;
			else if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
				computerAllowanceType = PlayerType.COMPUTER_MEDIUM;
			else if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
				computerAllowanceType = PlayerType.COMPUTER_EASY;
		}
		
		if (computerAllowanceType == PlayerType.COMPUTER_EASY) {
			if (battle.activePlayer.getLevel() + levels >= 10 && 
				selectedPlayer.getLevel() + helperLevels < 10) {
				reply = EASY_REPLY_NO;
			}
			else {
				minValueToHelp /= 2;
				if (helperGain + maxTreasureValue < minValueToHelp)
					reply = EASY_REPLY_NO;
				else {
					reply = EASY_REPLY_YES;
					willHelp = true;
				}								
			}
		}
		else if (computerAllowanceType == PlayerType.COMPUTER_MEDIUM) {
			if (battle.activePlayer.getLevel() + levels >= 10 && 
				selectedPlayer.getLevel() + helperLevels < 10) {
				reply = MEDIUM_REPLY_NO;
			}
			else {
				if (helperGain + maxTreasureValue < minValueToHelp)
					reply = MEDIUM_REPLY_NO;
				else {
					reply = MEDIUM_REPLY_YES;
					willHelp = true;
				}
			}
		}
		else if (computerAllowanceType == PlayerType.COMPUTER_HARD) {
			if (battle.activePlayer.getLevel() + levels >= 10) {
				reply = HARD_REPLY_NO;
			}
			else {
				minValueToHelp = (minValueToHelp * 3) / 2;
				if (helperGain + maxTreasureValue < minValueToHelp)
					reply = HARD_REPLY_NO;
				else {
					reply = HARD_REPLY_YES;
					willHelp = true;
				}
			}
		}					
		if (selectedPlayer.isFemale())
			reply.replaceAll("his", "her");
		
		// Display information to players based on whether or not the player decided to help
		if (!evaluateForHelper || willHelp) {
			// Get the minimum required treasure value needed to help
			int requiredTreasureValue = minValueToHelp - helperGain;
			// also factor in the value of the remaining treasure and take (*)half (* = not necessarily half, based on player rank)
			maxTreasureValue -= Math.abs(requiredTreasureValue);
			if (maxTreasureValue > 0) {
				// determine how to divide remaining treasure value based on active player rank
				double rankDivider = (double)rankPercentMultiplier / (100.0 + rankPercentMultiplier);
				requiredTreasureValue += maxTreasureValue * rankDivider;
				// round up to 100 
				requiredTreasureValue--;
				requiredTreasureValue += 100 - (requiredTreasureValue % 100);
			}
			
			// Calculate minimum number of treasures needed to achieve requiredTreasureValue
			LinkedList<Boolean> isHelpersTurn = new LinkedList<Boolean>();
			int numTreasuresNeeded = 0;
			int currentTreasureValue = 0;
			int highestTreasureValueLeft = AIManager.UNKNOWN_CARD_VALUE + (100 * ((treasures - 1) / 2));
			while (currentTreasureValue < requiredTreasureValue) {
				currentTreasureValue += highestTreasureValueLeft;
				numTreasuresNeeded++;
				isHelpersTurn.add(true);
				highestTreasureValueLeft -= 100;
				if (highestTreasureValueLeft < 100)
					highestTreasureValueLeft = 100;
			}
			
			// Shouldn't ever happen, as this should have triggered "Will Not Help" code
			if (evaluateForHelper && numTreasuresNeeded > treasures) {
				System.err.println("Ask Help dialog: numTreasuresNeeded > treasures (" + 
								   numTreasuresNeeded + " > " + treasures + ")");
				return;
			}
			
			while (isHelpersTurn.size() < treasures)
				isHelpersTurn.add(false);
			
			// 15 is maximum number of treasures you can have before last value goes to 0,
			// meaning turn order doesn't make much difference by then and doesn't need to be changed.
			// EX. 1500, 1400, ..., 900, 800, 700, ..., 100, 0
			if (numTreasuresNeeded < treasures && numTreasuresNeeded < 15) {
				// Move randomly chosen helper turn orders down the turn order list, until turn value is
				// as close to required value as possible.  Each move down will drop a value of 100.
				int maxAdjustmentIndex = treasures - 1;
				if (maxAdjustmentIndex > 18)
					maxAdjustmentIndex = 18;
				while (numTreasuresNeeded > 0 && currentTreasureValue > requiredTreasureValue) {
					int randomTurnNum = Randomizer.getRandom(numTreasuresNeeded) + 1;
					int turnIndex = 0;
					
					for (int count = 1; count <= randomTurnNum; count++)
						while (!isHelpersTurn.get(turnIndex))
							turnIndex++;
					
					while (isHelpersTurn.get(turnIndex + 1))
						turnIndex++;
					
					isHelpersTurn.set(turnIndex, false);
					isHelpersTurn.set(++turnIndex, true);
					currentTreasureValue -= 100;
					if (turnIndex == maxAdjustmentIndex) {
						maxAdjustmentIndex--;
						numTreasuresNeeded--;
					}
				}
			}
			
			LinkedList<Player> takeTreasurePlayerOrder = new LinkedList<Player>();
			for (int turnIndex = 0; turnIndex < treasures; turnIndex++) {
				if (isHelpersTurn.get(turnIndex))
					takeTreasurePlayerOrder.add(selectedPlayer);
				else
					takeTreasurePlayerOrder.add(battle.activePlayer);
			}
			
			// Computer asking for Computer help
			if (battle.activePlayer.isComputer() && evaluateForHelper) {
				// Determine whether or not to accept help
				if (selectedPlayer.getLevel() + helperLevels >= 10) {
					if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_HARD ||
						battle.activePlayer.getLevel() + levels < 10) {
						
						playerBox.setSelectedIndex(-1);
						battle.addHelpRefuser(selectedPlayer);
						return;
					}
				}
				
				battle.setTakeTreasurePlayerOrder(takeTreasurePlayerOrder);
				battle.addHelper(selectedPlayer);
				reply = selectedPlayer + " has joined the battle as a helper.";
				Messenger.display(reply, "Ask For Help: Accept");
			}
			// Computer asking for Human help
			else if (battle.activePlayer.isComputer()) {
				String prompt = "<HTML>&nbsp;";
				prompt += battle.activePlayer + " offers the following treasure taking order if " + selectedPlayer + " will help:";
				for (int turn = 0; turn < takeTreasurePlayerOrder.size(); turn++)
					prompt += "&nbsp;<br>&nbsp;" + (turn+1) + " - " + takeTreasurePlayerOrder.get(turn);
				if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
					prompt += "&nbsp;<br><br>&nbsp;" + selectedPlayer + " may take any other treasures earned.";
				else if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
					prompt += "&nbsp;<br><br>&nbsp;" + "Any other treasures earned will be given randomly.";
				else if (battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
					prompt += "&nbsp;<br><br>&nbsp;" + battle.activePlayer + " will take any other Treasures earned.";
				prompt += "&nbsp;</HTML>";
				
				final int YES_CHOICE = 1;
				OptionDialog dialog = new OptionDialog(selectedPlayer + ", will you help?", prompt, "Yes", "No");
				dialog.setVisible(true);
				dialog.getChoice();
				
				if (dialog.getChoice() == YES_CHOICE) {
					battle.setTakeTreasurePlayerOrder(takeTreasurePlayerOrder);
					battle.addHelper(selectedPlayer);
					dispose();
				}
				else {
					takeTreasurePlayerOrder.clear();
					playerBox.setSelectedIndex(-1);
					battle.addHelpRefuser(selectedPlayer);
				}					
			}
			// Human asking for Computer help
			else {
				String prompt = "<HTML>&nbsp;";
				prompt += selectedPlayer + reply + "if you agree to the following treasure taking order:";
				for (int turn = 0; turn < takeTreasurePlayerOrder.size(); turn++)
					prompt += "&nbsp;<br>&nbsp;" + (turn+1) + " - " + takeTreasurePlayerOrder.get(turn);
				if (selectedPlayer.getPlayerType() == PlayerType.COMPUTER_EASY)
					prompt += "&nbsp;<br>&nbsp;" + "You may take any other treasures earned.";
				else if (selectedPlayer.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
					prompt += "&nbsp;<br>&nbsp;" + "Any other treasures earned will be given randomly.";
				else if (selectedPlayer.getPlayerType() == PlayerType.COMPUTER_HARD)
					prompt += "&nbsp;<br>&nbsp;" + selectedPlayer + " will take any other Treasures earned.";
				prompt += "&nbsp;</HTML>";
				int choice = JOptionPane.showConfirmDialog(null, prompt, "Ask For Help: Accept", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					battle.setTakeTreasurePlayerOrder(takeTreasurePlayerOrder);
					battle.addHelper(selectedPlayer);
					dispose();
				}
				else {
					takeTreasurePlayerOrder.clear();
					playerBox.setSelectedIndex(-1);
				}
			}
		}
		else {
			if (battle.activePlayer.isComputer())
				battle.addHelpRefuser(selectedPlayer);
			else
				Messenger.display(selectedPlayer + reply, "Ask For Help: Decline");
			
			playerBox.setSelectedIndex(-1);
		}
	}
}
