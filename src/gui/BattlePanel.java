
package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ai.AIBattleManager;

import exceptions.EndGameException;
import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.PlayerLabel;

import model.Battle;
import model.GM;
import model.Player;
import model.Randomizer;
import model.card.Card;
import model.card.DoorCard;
import model.card.ItemCard;
import model.card.MonsterCard;

/**
 * Panel that will display the GUI controls of a battle.
 */
public class BattlePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// Label that displays the active battle level of the player(s)
	private JLabel playerTotalLabel;
	// Player Label that displays information about the active player
	private PlayerLabel activePlayerLabel;
	// Card Panel that displays the item cards that have been played on the player(s)
	private CardPanel playerItemPanel;
	// Player Label that displays information about the helping player, if any
	private PlayerLabel helperLabel;
	
	// Button that allows the active player to ask for help
	private CustomButton askHelpButton;
	// Button that lets player(s) defeat monsters and end battle
	private CustomButton defeatMonsterButton;
	// Button that lets player(s) try to run from monsters
	private CustomButton runButton;
	// Label that informs user of special restrictions when running away
	private JLabel runWarningLabel;
	// Button that lets clerics chase away the Wannabe Vampire
	private CustomButton chaseAwayButton;
		
	// Label that displays the active battle level of the monster(s)
	private JLabel monsterTotalLabel;
	// Card Panel that displays the item cards that have been played on the monster
	private CardPanel monsterItemPanel;
	// Card Panel that displays the monsters in the battle
	private CardPanel monsterImagePanel;
		
	// reference to the main controlling GUI class
	private GUI gui;
	// the battle that this Panel is for
	private Battle battle;
	
	// List of item cards played on the user
	private LinkedList<Card> playerItemCards;
	// List of item cards played on the monsters
	private LinkedList<Card> monsterItemCards;
	// List of monsters in the battle
	private LinkedList<Card> monsterCards;
		
	/**
	 * Creates a new BattlePanel panel.
	 * @param g reference to the main controlling GUI
	 * @param b battle that this Panel is for
	 */
	public BattlePanel(GUI g, Battle b) {
		super();
		
		// Initialize variables and display controls
		gui = g;
		battle = b;
		
		playerItemCards = new LinkedList<Card>();
		monsterItemCards = new LinkedList<Card>();
		monsterCards = new LinkedList<Card>();
		monsterCards.add(battle.getMonster(0));
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		
		c.gridwidth = 3;
		playerTotalLabel = new JLabel();
		add(playerTotalLabel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		activePlayerLabel = new PlayerLabel(battle.activePlayer);
		add(activePlayerLabel, c);
		
		c.gridx++;
		playerItemPanel = new CardPanel(playerItemCards, 1, null, null);
		String title = "Player Bonus Items";
		playerItemPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));				
		add(playerItemPanel, c);
		
		c.gridx++;
		helperLabel = new PlayerLabel(battle.helper);
		add(helperLabel, c);
		if (!battle.isHelper())
			helperLabel.setVisible(false);
		
		c.gridx = 0;
		c.gridy++;
		// Let player ask for help if there is no current helper
		askHelpButton = new CustomButton("Ask For Help") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				boolean hasKneepads = battle.activePlayer.hasEquipped(Card.E_KNEEPADS_OF_ALLURE);								
				(new AskHelpDialog(battle, hasKneepads)).setVisible(true);
				if (battle.helper != null)
					updateDisplay();
			}
		};
		add(askHelpButton, c);
		
		c.gridx++;
		runButton = new CustomButton("Run") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {			
				RunDialog dialog = new RunDialog(battle);
				dialog.setVisible(true);
				updateDisplay();
			}
		};
		add(runButton, c);
		runButton.requestFocusInWindow();
		
		c.gridy++;
		c.gridwidth = 4;
		String runWarning = "<HTML>" +
		"&nbsp;Only special run away actions can be performed after deciding to run away.&nbsp;<br>" +
		"&nbsp;Also only run away and play-anytime items can be used after deciding to run away.&nbsp;<br>" +
		"&nbsp;If you want to do/use anything else (discard your race, play a curse, etc.),&nbsp;<br>" +
		"&nbsp;you must do it first.&nbsp;</HTML>";
		runWarningLabel = new JLabel(runWarning);
		add(runWarningLabel, c);
		c.gridwidth = 1;
		c.gridy--;
		
		c.gridx++;
		// If current player(s) battle level is higher than monster(s) battle level, allows player(s) to 
		// defeat monsters and end battle. 
		defeatMonsterButton = new CustomButton("Defeat Monster") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				try {
					battle.defeatMonsters();
					updateDisplay();
				}
				catch (EndGameException ex) {
					gui.endGame();
				}
			}
		};
		add(defeatMonsterButton, c);
		defeatMonsterButton.requestFocusInWindow();
		
		c.gridx++;
		c.gridy = 0;
		monsterTotalLabel = new JLabel();
		add(monsterTotalLabel, c);
		
		// Allows a Cleric to remove the Wannabe Vampire from the battle
		chaseAwayButton = new CustomButton("booga booga") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++)
					if (battle.getMonster(monsterIndex).getID() == Card.M_WANNABE_VAMPIRE)
						battle.discardMonster(battle.getMonster(monsterIndex), true);
				updateDisplay();
			}
		};
		chaseAwayButton.setVisible(false);
		
		c.gridy++;
		// Displays the monsters
		// If a player in the battle is a Cleric and one of the Monsters is the Wannabe Vampire, allow the Cleric to
		// chase him away.
		monsterImagePanel = new CardPanel(monsterCards, 1, chaseAwayButton, null) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void setSelectedImageLabel(JLabel label) {
				super.setSelectedImageLabel(label);
				mainImageLabel.setVerticalTextPosition(JLabel.BOTTOM);
				mainImageLabel.setHorizontalTextPosition(JLabel.CENTER);
				mainImageLabel.setText(getMonsterEhancerText(getSelectedIndex()));
				
				chaseAwayButton.setVisible(false);
				if (this.getSelectedCard().getID() == Card.M_WANNABE_VAMPIRE) {
					if (battle.activePlayer.isCleric() && !battle.activePlayer.isComputer())
						chaseAwayButton.setVisible(true);
					else if (battle.isHelper() && battle.helper.isCleric() && !battle.helper.isComputer())
						chaseAwayButton.setVisible(true);
				}
				
				validate();
				repaint();
			}
		};
		title = "Monsters";
		monsterImagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));				
		add(monsterImagePanel, c);	
		
		c.gridx++;
		// Displays the items played on the monster
		monsterItemPanel = new CardPanel(monsterItemCards, 1, null, null) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void addCard(Card card) {
				super.addCard(card);
				validate();
				repaint();
			}
		};
		title = "Monster Bonus Items";
		monsterItemPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));				
		add(monsterItemPanel, c);
		
		setVisible(true);
	}
	
	/**
	 * Returns an HTML-formatted string listing the enhancements made to/played on the indexed monster.
	 * @param monsterIndex the index of the monster in the monster list
	 * @return an HTML-formatted string listing the enhancements made to/played on the monster
	 */
	private String getMonsterEhancerText(int monsterIndex) {
		String monsterEnhancerInfo = "<HTML>";
		
		Iterator<DoorCard> enhancerIter = battle.getMonsterEnhancers(monsterIndex).iterator();
		while (enhancerIter.hasNext()) {
			DoorCard card = enhancerIter.next();
			if (card.getID() == Card.OD_MATE) {
				monsterEnhancerInfo += "&nbsp; Mate: Duplicate Monster &nbsp;<br>";
				monsterEnhancerInfo += "&nbsp;&nbsp;&nbsp;&nbsp; -1 to run away &nbsp;<br>";
			}
			else if (card.getID() == Card.ME_BABY) {
				monsterEnhancerInfo += "&nbsp; Baby: -5 to Level &nbsp;<br>";
				monsterEnhancerInfo += "&nbsp;&nbsp;&nbsp;&nbsp; 1 less Treasure &nbsp;<br>";
			}
			else if (card.getID() == Card.ME_ANCIENT) {
				monsterEnhancerInfo += "&nbsp; Ancient: +10 to Level &nbsp;<br>";
				monsterEnhancerInfo += "&nbsp;&nbsp;&nbsp;&nbsp; 2 extra Treasures &nbsp;<br>";
			}
			else if (card.getID() == Card.ME_ENRAGED) {
				monsterEnhancerInfo += "&nbsp; Enraged: +5 to Level &nbsp;<br>";
				monsterEnhancerInfo += "&nbsp;&nbsp;&nbsp;&nbsp; 1 extra Treasure &nbsp;<br>";
			}
			else if (card.getID() == Card.ME_HUMONGOUS) {
				monsterEnhancerInfo += "&nbsp; Humongous: +10 to Level &nbsp;<br>";
				monsterEnhancerInfo += "&nbsp;&nbsp;&nbsp;&nbsp; 2 extra Treasures &nbsp;<br>";
			}
			else if (card.getID() == Card.ME_INTELLIGENT) {
				monsterEnhancerInfo += "&nbsp; Intelligent: +5 to Level &nbsp;<br>";
				monsterEnhancerInfo += "&nbsp;&nbsp;&nbsp;&nbsp; 1 extra Treasure &nbsp;<br>";
			}
		}	
		
		return monsterEnhancerInfo + "</HTML>";
	}
		
	/**
	 * Returns the battle displayed by this panel.
	 * @return the battle displayed by this panel
	 */
	public Battle getBattle() {
		return battle;
	}
	
	/**
	 * Checks for any player/battle changes and updates the display accordingly. 
	 */
	public void updateDisplay() {		
		// If the battle phase is over, allow players to take any treasures won, then display the main panel
		if (!GM.isBattlePhase()) {
			if (battle == null)
				return;
			
			gui.getContentPane().removeAll();
			
			if (battle.getTreasureCount() > 0) {
				TakeTreasuresPanel treasuresPanel = new TakeTreasuresPanel(gui, battle);
				if (!treasuresPanel.isFinished() && (!battle.activePlayer.isComputer() || (battle.isHelper() && !battle.helper.isComputer()))) {
					gui.getContentPane().add(new JScrollPane(treasuresPanel));
					gui.validate();
					gui.repaint();
				}
			}
			else {
				GM.moveToBattlePhase();
				GM.moveNextPhase();
				gui.showMainDisplay();
			}
			
			battle = null;
			GM.setAIThinking(false);
			return;
		}
	
		battle.checkForChanges();
		if (battle.getMonsterCount() == 0) {
			updateDisplay();
			return;
		}
		
		// Check for helper changes and update the display accordingly
		if (battle.activePlayer != activePlayerLabel.getPlayer())
			activePlayerLabel.setPlayer(battle.activePlayer);
		if (battle.helper != helperLabel.getPlayer()) {
			helperLabel.setPlayer(battle.helper);
			if (battle.isHelper())
				helperLabel.setVisible(true);
			else
				helperLabel.setVisible(false);
		}
		// Refresh information about the active player and helper (if applicable)
		activePlayerLabel.updatePlayerInfo();
		if (battle.isHelper())
			helperLabel.updatePlayerInfo();
				
		int playersLevel = battle.getPlayersLevel();
		int monstersLevel = battle.getMonstersLevel();
		
		// Show/hide buttons based on the current situation and what is possible.
		defeatMonsterButton.setVisible(false);
		runButton.setVisible(true);
		runWarningLabel.setVisible(true);
		if (playersLevel > monstersLevel) {
			defeatMonsterButton.setVisible(true);
			runButton.setVisible(false);
			runWarningLabel.setVisible(false);
		}
		else if (playersLevel == monstersLevel) {
			if (battle.activePlayer.isWarrior() || (battle.isHelper() && battle.helper.isWarrior())) {
				defeatMonsterButton.setVisible(true);
				runButton.setVisible(false);
				runWarningLabel.setVisible(false);
			}
		}
		
		String playerText = "";
		if (battle.activePlayer.isComputer()) {
			playerText = "Allow Player";
			if (battle.isHelper())
				playerText += "s";
			playerText += " To ";
		}		
		if (defeatMonsterButton.isVisible()) {
			String defeatText = playerText + "Defeat Monster";
			if (battle.getMonsterCount() > 1)
				defeatText += "s";
			defeatMonsterButton.setText(defeatText);
		}
		else
			runButton.setText(playerText + "Run");

		if (!battle.activePlayer.isComputer() && battle.canAddHelper())
			askHelpButton.setVisible(true);
		else
			askHelpButton.setVisible(false);
		
		playerTotalLabel.setText("Player Total Level: " + playersLevel);
		
		// Update item cards used on players
		Iterator<ItemCard> playerItemIter = battle.getPlayerItemCards().iterator();
		while (playerItemIter.hasNext()) {
			Card card = playerItemIter.next();
			if (!playerItemCards.contains(card))
				playerItemPanel.addCard(card);
		}
		
		// Update item cards used on monsters
		Iterator<ItemCard> monsterItemIter = battle.getMonsterItemCards().iterator();
		while (monsterItemIter.hasNext()) {
			Card card = monsterItemIter.next();
			if (!monsterItemCards.contains(card))
				monsterItemPanel.addCard(card);
		}
		
		// Determines if a monster has been removed or altered in any way and updates display accordingly
		boolean monsterRemovedOrChanged = false;
		if (monsterCards.size() > battle.getMonsterCount())
			monsterRemovedOrChanged = true;
		else {
			for (int monsterIndex = 0; monsterIndex < monsterCards.size(); monsterIndex++)
				if (monsterCards.get(monsterIndex) != battle.getMonster(monsterIndex))
					monsterRemovedOrChanged = true;
		}
		
		if (monsterRemovedOrChanged) {
			monsterCards.clear();
			// will clear monsterImagePanel
			monsterImagePanel.removeSelectedImage();
		}
			
		// Display current information on all monsters
		int winnableLevels = 0;
		int winnableTreasures = 0;
		for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++) {
			MonsterCard monster = battle.getMonster(monsterIndex);
			
			if (!monsterCards.contains(monster))
				monsterImagePanel.addCard(monster);
						
			winnableLevels += battle.getWinLevelCount(monster);
			winnableTreasures += battle.getWinTreasureCount(monster);
		}		
		String monsterInfo = "<HTML>&nbsp;Monster Total Level: " + monstersLevel + "&nbsp;";
		monsterInfo += "<br>&nbsp;Winnable Levels: " + winnableLevels + "&nbsp;";
		monsterInfo += "<br>&nbsp;Winnable Treasures: " + (winnableTreasures + battle.getTreasureCount()) + "&nbsp;</HTML>";
		monsterTotalLabel.setText(monsterInfo);		
		monsterImagePanel.refreshMainImage();
		
		defeatMonsterButton.setEnabled(false);
		runButton.setEnabled(false);
		askHelpButton.setEnabled(false);
		chaseAwayButton.setEnabled(false);
		GM.setAIThinking(true);
		
		validate();
		repaint();
		
		int winMargin = playersLevel - monstersLevel;
		if (battle.activePlayer.isWarrior() || (battle.helper != null && battle.helper.isWarrior()))
			winMargin++;
		
		// Randomly decide which computer players will act first
		LinkedList<Player> sortedPlayers = new LinkedList<Player>();
		for (Player currentPlayer : GM.getPlayers())
			sortedPlayers.add(Randomizer.getRandom(sortedPlayers.size() + 1), currentPlayer);
		
		try {
			// Each computer player will in turn decide to act or not; 
			// if the computer does act, restart refresh battle data
			for (Player currentPlayer : sortedPlayers) {
				if (currentPlayer.isComputer())
					if (AIBattleManager.makeBattleDecisions(battle, currentPlayer, winMargin)) {
						updateDisplay();
						return;
					}
			}
		}
		catch (EndGameException ex) {
			GM.moveToOtherPhase();
			battle = null;
			gui.endGame();
		}
		
		defeatMonsterButton.setEnabled(true);
		runButton.setEnabled(true);
		askHelpButton.setEnabled(true);
		chaseAwayButton.setEnabled(true);
		GM.setAIThinking(false);
		
		validate();
		repaint();		
	}
}
