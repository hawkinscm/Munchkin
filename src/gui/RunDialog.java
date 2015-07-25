
package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

import exceptions.EndGameException;
import gui.components.CustomButton;
import gui.components.CustomDialog;
import gui.components.Messenger;

import model.Battle;
import model.CardPlayManager;
import model.GM;
import model.Player;
import model.Race;
import model.Randomizer;
import model.card.Card;
import model.card.CurseCard;
import model.card.DoorCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.RaceCard;
import model.card.TreasureCard;

/**
 * Dialog that handles running away from battle.
 */
public class RunDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// constant variables as text codes for the different stages of running
	private final String RUN_TEXT = "Run";
	private final String ESCAPE_TEXT = "Escape";
	private final String FAIL_TEXT = "Failed Run Away";
	private final String BRIBE_TEXT = "Bribe";
	private final String DROP_STICK_TEXT = "Drop Stick";
	
	// GUI controls for displaying info and getting user input
	private JComboBox monsterBox;
	private RunDialog thisDialog;
	private CustomButton runButton;
	private CustomButton specialButton;
	private CustomButton ratOnStickButton;	
	private CustomButton flightSpellButton;
	private JLabel escapeConditionLabel;
		
	// Battle being run from and the player who is currently running
	private Battle battle;
	private Player currentPlayer;
	// all monsters in the battle
	private LinkedList<MonsterCard> monsters;
	// the Equipped Rat On A Stick Card, if applicable
	private EquipmentCard ratOnStickCard = null;
	
	// whether or not the current player can automatically escape all monsters
	private boolean escapeAll;
	// whether or not all players in the battle can automatically escape all monsters
	private boolean everyoneEscapeAll;
	
	// bonus given from Wizards using the flight spell
	private int flightSpellBonus;
	
	/**
	 * Create a new RunDialog dialog.
	 * @param b the battle from which the player is running
	 */
	public RunDialog(Battle b) {
		super("Run");
		
		// initialize variables
		thisDialog = this;		
		battle = b;		
		everyoneEscapeAll = false;
		
		// if there is a helper, let him try to escape first
		if (battle.isHelper())
			setUpForPlayer(battle.helper);
		else
			setUpForPlayer(battle.activePlayer);
	}
	
	/**
	 * Creates and returns a Card Panel for the selected Player to play a card.
	 * @return a Card Panel that allows the selected Player to play a card
	 */
	private JPanel createCardPlayerPanel() {
		// initialize variables and prepare GUI Panel controls for display
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Play Battle Items"));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		
		// Drop down box that allows a player to select himself so as to play a card
		LinkedList<Player> players = new LinkedList<Player>();
		for (Player player : GM.getPlayers())
			if (!player.isComputer())
				players.add(player);
		final JComboBox playerBox = new JComboBox(players.toArray());
		panel.add(playerBox, constraints);
		
		constraints.gridy++;
		// Button that plays the selected Card
		CustomButton playCardButton = new CustomButton("Play A Card") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Player player = (Player)playerBox.getSelectedItem();
				MonsterCard monster = (MonsterCard)monsterBox.getSelectedItem();
				PlayBattleCardsDialog dialog = new PlayBattleCardsDialog(thisDialog, player, monster);
				dialog.setVisible(true);
				try {
					// Update and refresh GUI controls based on the changes made by the played card, if played.
					if (dialog.playedCard()) {
						setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						monsterBox.setEnabled(false);
						
						for (EquipmentCard item : currentPlayer.getEquippedItems())
							if (item.getID() == Card.E_RAT_ON_A_STICK)
								ratOnStickCard = item;
						
						// Remove any monsters that the played card may have removed
						Iterator<MonsterCard> monsterIter = monsters.iterator();
						while (monsterIter.hasNext()) {
							MonsterCard currentMonster = monsterIter.next();
							if (!battle.hasMonster(currentMonster)) {
								monsterIter.remove();
								monsterBox.removeItem(currentMonster);
								
								if (currentMonster == monster)
									monsterBox.setEnabled(true);
							}		
						}
						
						// If there are no more monsters left to run from, let any remaining players try to run
						// then close the dialog, otherwise prepare the next monster to run from. 
						if (monsters.isEmpty()) {
							if (currentPlayer == battle.helper) {
								battle.helper = null;
								setUpForPlayer(battle.activePlayer);
							}
							else {
								battle.endBattle();
								dispose();
							}
							
							return;
						}
						else if (monsterBox.getSelectedIndex() == -1)
							monsterBox.setSelectedIndex(0);
						
						makeComputerDecisions();
						refresh();
					}
				}
				catch (EndGameException ex) {}
			}
		};
		panel.add(playCardButton, constraints);
		
		return panel;
	}
		
	/**
	 * Reinitialize the GUI display for the given player, to try and run from all monsters.
	 * @param p Player whose turn it is to run from all monsters
	 */
	private void setUpForPlayer(Player p) {
		// initialize variables and display GUI controls
		currentPlayer = p;
		
		flightSpellBonus = 0;
		
		escapeAll = false;
		if (everyoneEscapeAll)
			escapeAll = true;
						
		getContentPane().removeAll();
		validate();
		repaint();
		
		// Search for and load the Equipped Rat On A Stick Card if found
		ratOnStickCard = null;
		for (EquipmentCard item : currentPlayer.getEquippedItems())
			if (item.getID() == Card.E_RAT_ON_A_STICK)
				ratOnStickCard = item;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		JLabel infoLabel = new JLabel(currentPlayer.getName() + " must flee.");
		getContentPane().add(infoLabel, c);
		c.gridwidth = 1;
		
		// load list of all monsters to run from; if empty close dialog
		monsters = new LinkedList<MonsterCard>();
		for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++)
			monsters.add(battle.getMonster(monsterIndex));
		if (monsters.isEmpty()) {
			battle.endBattle();
			dispose();
			return;
		}
		
		final JLabel imageLabel = new JLabel("");
		c.gridy++;
		// Drop down box to select which monster to run from first
		monsterBox = new JComboBox(monsters.toArray());
		monsterBox.addItemListener(new ItemListener() {
			// update GUI controls based on which monster is selected to run from
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED || monsterBox.getSelectedIndex() == -1)
					return;
				
				setRunText(RUN_TEXT);
				
				// add/subtract bonuses and load special features for specific types of items/monsters
				MonsterCard monster = (MonsterCard)monsterBox.getSelectedItem();
				imageLabel.setIcon(monster.getPicture());	
				
				if (monster.isAutoEscape(currentPlayer) || escapeAll)
					setRunText(ESCAPE_TEXT);
				
				specialButton.setVisible(false);
				ratOnStickButton.setVisible(false);
				flightSpellButton.setVisible(false);
				
				if (monster.getID() == Card.M_CRABS)
					setRunText(FAIL_TEXT);
				else if (monster.getID() == Card.M_FLOATING_NOSE) {
					setRunText(FAIL_TEXT);
					specialButton.setText(BRIBE_TEXT);
					specialButton.setVisible(true);					
				}
				else if (monster.getID() == Card.M_PIT_BULL && runButton.getText().endsWith(RUN_TEXT)) {
					specialButton.setText(DROP_STICK_TEXT);
					specialButton.setVisible(true);
				}
				
				if (ratOnStickCard != null && runButton.getText().endsWith(RUN_TEXT)) {
					int monsterLevel = monster.getLevel(battle);
					Iterator<DoorCard> enhancerIter = battle.getMonsterEnhancers(monster).iterator();
					while (enhancerIter.hasNext()) {
						DoorCard enhancerCard = enhancerIter.next();
						if (enhancerCard instanceof MonsterEnhancerCard)
							monsterLevel += ((MonsterEnhancerCard)enhancerCard).getBonus();
					}					
					if (monsterLevel <= 8)
						ratOnStickButton.setVisible(true);
				}
				
				if (currentPlayer.isWizard() && runButton.getText().endsWith(RUN_TEXT))
					flightSpellButton.setVisible(true);
				
				refresh();
			}	
		});
		getContentPane().add(monsterBox, c);
		
		c.gridy++;
		c.gridheight = 5;
		getContentPane().add(imageLabel, c);
		c.gridy += c.gridheight;
		c.gridheight = 1;		
		
		c.gridwidth = 2;
		escapeConditionLabel = new JLabel("");
		getContentPane().add(escapeConditionLabel, c);
		c.gridwidth = 1;
				
		c.gridy = 1;
		c.gridx++;
		c.gridheight = 2;
		JPanel cardPlayerPanel = createCardPlayerPanel();
		getContentPane().add(cardPlayerPanel, c);		
		c.gridheight = 1;
		
		c.anchor = GridBagConstraints.NORTH;
		c.gridy += 2;
		// Main action button that allows player to run, escape, or get caught depending on the current phase/option
		runButton = new CustomButton(RUN_TEXT) {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);				
				MonsterCard monster = (MonsterCard)monsterBox.getSelectedItem();
								
				// use text codes to determine current phase/option
				if (runButton.getText().endsWith(RUN_TEXT)) {
					int runAwayBonus = getRunAwayBonus(monster);
					int roll = Randomizer.rollDice(currentPlayer) + runAwayBonus;
					
					// allow computer to use Loaded Die if available
					if (currentPlayer.isComputer()) {
						int realRoll = roll - runAwayBonus;
						if (currentPlayer.hasChickenOnHead()) {
							realRoll++;
							runAwayBonus--;
						}
						String rollMessage = currentPlayer + " rolled a " + realRoll;
						if (runAwayBonus != 0)
							rollMessage += " and has a bonus of " + runAwayBonus + ".  Result: " + (realRoll + runAwayBonus);
						escapeConditionLabel.setText(rollMessage); 
						
						int maximumRoll = 6 + runAwayBonus;
						if (maximumRoll >= 5) {
							ItemCard loadedDie = null;
							for (TreasureCard item : currentPlayer.getAllValueCards())
								if (item.getID() == Card.I_LOADED_DIE) {
									loadedDie = (ItemCard)item;
									break;
								}
							
							if (loadedDie != null) {
								boolean hasMate = false;
								for (Card enhancerCard : battle.getMonsterEnhancers(monster))
									if (enhancerCard.getID() == Card.OD_MATE)
										hasMate = true;
								
								int badStuffCost = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, currentPlayer.getHandCards());
								if (badStuffCost >= AICardEvaluator.getCardValueToPlayer(loadedDie, currentPlayer, currentPlayer.getHandCards()) * 2) {
									currentPlayer.discard(loadedDie);
									roll = 6;
									String message = currentPlayer + " used the Loaded Die card to successfuly escape.";
									Messenger.display(message, "Item Used");
								}						
							}
						}
					}				
					
					// if roll too small, allow computer to decide if will use Loaded Die card if available.
					if (roll >= 5)
						setRunText(ESCAPE_TEXT);
					else
						setRunText(FAIL_TEXT);
					
					monsterBox.setEnabled(false);
					specialButton.setVisible(false);
					ratOnStickButton.setVisible(false);
					flightSpellButton.setVisible(false);
					
					makeComputerDecisions();
					
					refresh();
					return;
				}
				
				battle.leaveTreasuresBehind();
				
				// perform bad stuff if caught, or special bad stuff for escaping certain monsters
				if(runButton.getText().endsWith(FAIL_TEXT))
					monster.doBadStuff(currentPlayer);
				else if (runButton.getText().endsWith(ESCAPE_TEXT)) {
					if (monster.getID() == Card.M_MR_BONES)
						currentPlayer.goDownLevel();
					else if (monster.getID() == Card.M_WIGHT_BROTHERS && currentPlayer.getLevel() > 3)
						currentPlayer.goDownLevels(2);
					else if (monster.getID() == Card.M_KING_TUT && currentPlayer.getLevel() > 3)
						currentPlayer.goDownLevels(2);
				}
				
				monsters.remove(monster);
				monsterBox.setEnabled(true);
				
				// if already run from all monsters:
				//   if helper has finished running, let active player run
				//   else close run dialog and end battle
				// else prepare next monster to run from
				if (monsters.isEmpty()) {
					if (currentPlayer == battle.helper) {
						battle.helper = null;
						setUpForPlayer(battle.activePlayer);
					}
					else {
						battle.endBattle();
						dispose();
					}
				}
				else {
					monsterBox.removeItem(monster);
					monsterBox.setSelectedIndex(-1);
					monsterBox.setSelectedIndex(0);
					makeComputerDecisions();
					refresh();
				}
			}
		};
		getContentPane().add(runButton, c);
		
		c.gridy++;
		// Button that allows special features to be used based on items/enemies
		specialButton = new CustomButton() {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				// if monster is Floating Nose, allows the player to discard at least 200 GP worth of items to escape
				if (specialButton.getText().equals(BRIBE_TEXT)) {
					final LoseGPDialog dialog = new LoseGPDialog(currentPlayer, 200);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					if (dialog.hasDiscardedItems()) {
						setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						
						MonsterCard monster = (MonsterCard)monsterBox.getSelectedItem();
						monsters.remove(monster);
						monsterBox.setEnabled(true);
						
						battle.leaveTreasuresBehind();
						
						if (monsters.isEmpty()) {
							if (currentPlayer == battle.helper) {
								battle.helper = null;
								setUpForPlayer(battle.activePlayer);
							}
							else {
								battle.endBattle();
								dispose();
							}
						}
						else {
							monsterBox.removeItem(monster);
							monsterBox.setSelectedIndex(-1);
							monsterBox.setSelectedIndex(0);
							makeComputerDecisions();
						}
						
						refresh();
					}
				}
				// if monster is Pit Bull allows user to drop a stick-type item to escape
				else if (specialButton.getText().equals(DROP_STICK_TEXT)) {
					LinkedList<Card> stickItems = new LinkedList<Card>();
					Iterator<TreasureCard> itemIter = currentPlayer.getAllItems().iterator();
					while (itemIter.hasNext()) {
						Card card = itemIter.next();
						if (card.getID() == Card.E_ELEVEN_FOOT_POLE)
							stickItems.add(card);
						else if (card.getID() == Card.E_STAFF_OF_NAPALM)
							stickItems.add(card);
						else if (card.getID() == Card.E_SWISS_ARMY_POLEARM)
							stickItems.add(card);
						else if (card.getID() == Card.I_WAND_OF_DOWSING)
							stickItems.add(card);
					}
					
					LoseCardsDialog dialog = new LoseCardsDialog(currentPlayer, stickItems, 1, "wand, pole, or staff");
					dialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent evt) {
							dispose();
						}
					});
					dialog.setVisible(true);
					if (dialog.madeDiscard()) {
						setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						specialButton.setVisible(false);
						setRunText(ESCAPE_TEXT);
						monsterBox.setEnabled(false);
						
						makeComputerDecisions();
						refresh();
					}
				}
			}
		};
		getContentPane().add(specialButton, c);
		
		c.gridy++;
		// allows the player to drop an Equipped Rat On A Stick to escape from low level enemies.
		ratOnStickButton = new CustomButton("Drop Rat on a Stick") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				currentPlayer.discard(ratOnStickCard);
				ratOnStickCard = null;
				ratOnStickButton.setVisible(false);
				setRunText(ESCAPE_TEXT);
				monsterBox.setEnabled(false);
				
				makeComputerDecisions();
				refresh();
			}
		};
		getContentPane().add(ratOnStickButton, c);
		
		c.gridy++;
		// allows a wizard to discard up to 3 cards for a plus one run away bonus for each
		flightSpellButton = new CustomButton("Cast Flight Spell") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				LinkedList<Card> handCards = new LinkedList<Card>();
				handCards.addAll(currentPlayer.getHandCards());
				LoseCardsDialog dialog = new LoseCardsDialog(currentPlayer, handCards, 1, "card");
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				
				if (dialog.madeDiscard()) {
					setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					flightSpellBonus++;
					if (flightSpellBonus >= 3)
						flightSpellButton.setEnabled(false);
					monsterBox.setEnabled(false);
					
					makeComputerDecisions();
					refresh();
				}
			}
		};
		getContentPane().add(flightSpellButton, c);
		
		if (currentPlayer.isComputer()) {
			monsterBox.setEnabled(false);
			specialButton.setEnabled(false);
			ratOnStickButton.setEnabled(false);
			flightSpellButton.setEnabled(false);
		}
		
		monsterBox.setSelectedIndex(-1);
		monsterBox.setSelectedIndex(0);
		makeComputerDecisions();		
		refresh();
		
		runButton.requestFocusInWindow();
	}
	
	/**
	 * Returns the chance (0.0 - 1.0) the player has of getting caught according the the given runAwayBonus;
	 * returns 0.0 if the player will definitely escape, returns 1.0 if the player will definitely get caught.
	 * @param runAwayBonus the positive or negative bonus that the player has with running away
	 * @return the chance the player has of getting caught according the the given runAwayBonus
	 */
	private double getChanceToGetCaught(int runAwayBonus) {
		int maxFailRoll = 4 - runAwayBonus;
		if (maxFailRoll <= 0)
			return 0.0;
		if (maxFailRoll >= 6)
			return 1.0;
		
		return (double)(maxFailRoll) / 6.0;
	}
	
	/**
	 * Decides the best actions for computer players to perform and acts on them.
	 */
	private void makeComputerDecisions() {
		if (battle == null || battle.getMonsterCount() == 0 || monsterBox.getSelectedItem() == null)
			return;
		
		LinkedList<Player> randomPlayers = new LinkedList<Player>();
		for (Player player : GM.getPlayers())
			if (player.isComputer())
				randomPlayers.add(Randomizer.getRandom(randomPlayers.size() + 1), player);
		
		if (randomPlayers.isEmpty())
			return;
		
		MonsterCard currentMonster = (MonsterCard)monsterBox.getSelectedItem();
		boolean currentHasMate = false;
		for (Card enhancerCard : battle.getMonsterEnhancers(currentMonster))
			if (enhancerCard.getID() == Card.OD_MATE)
				currentHasMate = true;
		
		double rankFactor = GM.getPlayers().size() - AIManager.getRankedPlayers().indexOf(currentPlayer);
		rankFactor /= (double)GM.getPlayers().size();
		rankFactor *= 2.0;
		
		for (Player player : randomPlayers) {
			// Determine if certain actions are worth the cost to aid self in trying to escape monsters
			if (player == currentPlayer) {
				if (runButton.getText().endsWith(RUN_TEXT)) {
					if (ratOnStickButton.isVisible()) {
						String message = currentPlayer + " dropped the Rat on a Stick to esacpe the " + currentMonster;
						Messenger.display(message, "Item used");
						ratOnStickButton.buttonPressed();
						return;
					}
					
					final int INSTANT_WALL = 1;
					final int DISCARD_RACE = 2;
					final int DROP_STICK = 3;
					final int FLIGHT_SPELL = 4;
					
					AIValuedCard bestCardToUse = null;
					int runActionType = 0;
					for (ItemCard item : currentPlayer.getCarriedItems()) {
						if (item.getID() == Card.I_INSTANT_WALL) {
							int totalBadStuffCost = 0;
							for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
								MonsterCard monster = battle.getMonster(monsterIdx);
								
								boolean hasMate = false;
								for (Card enhancerCard : battle.getMonsterEnhancers(monster))
									if (enhancerCard.getID() == Card.OD_MATE)
										hasMate = true;
								totalBadStuffCost += AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, currentPlayer.getHandCards());
								totalBadStuffCost *= getChanceToGetCaught(getRunAwayBonus(monster));
							}
							if (totalBadStuffCost <= 0)
								continue;
							
							// Average value of bad stuff cost must be at least more than the value of 1 level.
							int actionValue = totalBadStuffCost - AIManager.LEVEL_VALUE;
							if (actionValue <= 0)
								break;
							
							if (bestCardToUse == null || actionValue > bestCardToUse.getValue()) {
								bestCardToUse = new AIValuedCard(item, actionValue);
								runActionType = INSTANT_WALL;
							}
							
							break;
						}
					}
					
					if (currentPlayer.isHalfling() && !currentPlayer.isHuman()) {
						for (RaceCard raceCard : currentPlayer.getRaceCards())
							if (raceCard.getRace() == Race.HALFLING) {
								int actionValue = 0;
								for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
									MonsterCard monster = battle.getMonster(monsterIdx);
									boolean hasMate = false;
									for (Card enhancerCard : battle.getMonsterEnhancers(monster))
										if (enhancerCard.getID() == Card.OD_MATE)
											hasMate = true;
									int noEscapeBadStuffCost = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, currentPlayer.getHandCards());
									int maxFailRoll = 4 - getRunAwayBonus(monster);
									if (maxFailRoll > 6 || maxFailRoll < 1)
										continue;
												
									int runAwayBonus = getRunAwayBonus(monster);
									actionValue += noEscapeBadStuffCost * getChanceToGetCaught(runAwayBonus);
									actionValue -= noEscapeBadStuffCost * getChanceToGetCaught(runAwayBonus + 1);
								}
								
								actionValue -= AICardEvaluator.getCardValueToPlayer(raceCard, currentPlayer, currentPlayer.getHandCards());
								if (actionValue <= 0)
									break;
								
								if (bestCardToUse == null || actionValue > bestCardToUse.getValue()) {
									bestCardToUse = new AIValuedCard(raceCard, actionValue);
									runActionType = DISCARD_RACE;
								}
							}
					}
					
					if (specialButton.isVisible() && specialButton.getText().equals(DROP_STICK_TEXT)) {
						AIValuedCard leastValuedStick = null;
						for (Card item : currentPlayer.getAllItems()) {
							if (item.getID() == Card.E_ELEVEN_FOOT_POLE || 
								item.getID() == Card.E_STAFF_OF_NAPALM ||
								item.getID() == Card.E_SWISS_ARMY_POLEARM ||
								item.getID() == Card.I_WAND_OF_DOWSING) {
								
								int itemValue = AICardEvaluator.getCardValueToPlayer(item, currentPlayer, currentPlayer.getHandCards());
								if (leastValuedStick == null || itemValue < leastValuedStick.getValue())
									leastValuedStick = new AIValuedCard(item, itemValue);
							}
						}
						
						if (leastValuedStick != null) {
							int badStuffCost = AIManager.getBadStuffCost(currentMonster, currentHasMate, currentPlayer, true, currentPlayer.getHandCards());
							badStuffCost *= getChanceToGetCaught(getRunAwayBonus(currentMonster));
							int actionValue = badStuffCost - leastValuedStick.getValue();
							if (actionValue > 0 && (bestCardToUse == null || actionValue > bestCardToUse.getValue())) {
								bestCardToUse = new AIValuedCard(leastValuedStick.getCard(), actionValue);
								runActionType = DROP_STICK;
							}
						}
					}
					
					if (flightSpellButton.isVisible() && flightSpellBonus < 3 && !currentPlayer.getHandCards().isEmpty()) {
						int actionValue = 0;
						for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
							MonsterCard monster = battle.getMonster(monsterIdx);
							boolean hasMate = false;
							for (Card enhancerCard : battle.getMonsterEnhancers(monster))
								if (enhancerCard.getID() == Card.OD_MATE)
									hasMate = true;
							int noEscapeBadStuffCost = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, currentPlayer.getHandCards());
							int maxFailRoll = 4 - getRunAwayBonus(monster);
							if (maxFailRoll > 6 || maxFailRoll < 1)
								continue;
							
							int runAwayBonus = getRunAwayBonus(monster);
							actionValue += noEscapeBadStuffCost * getChanceToGetCaught(runAwayBonus);
							actionValue -= noEscapeBadStuffCost * getChanceToGetCaught(runAwayBonus + 1);
						}
						
						AIValuedCard leastValuedHandCard = AIManager.getLeastValuedHandCards(currentPlayer, currentPlayer.getHandCards()).getFirst();
						actionValue -= leastValuedHandCard.getValue();
						if (actionValue > 0 && (bestCardToUse == null || actionValue > bestCardToUse.getValue())) {
							bestCardToUse = new AIValuedCard(leastValuedHandCard.getCard(), actionValue);
							runActionType = FLIGHT_SPELL;
						}
					}
						
					if (bestCardToUse == null)
						continue;
					
					if (runActionType == INSTANT_WALL) {
						try {
							CardPlayManager.playCard(currentPlayer, bestCardToUse.getCard(), currentMonster, this);
							String message = currentPlayer + " used the " + bestCardToUse.getCard();
							if (everyoneEscapeAll)
								message += " to allow all players ";
							message += " to escape the monster(s).";
							Messenger.display(message, "Item Used");
						}
						catch (EndGameException ex) {}
					}
					else if (runActionType == DISCARD_RACE) {
						currentPlayer.discardRaceCard((RaceCard)bestCardToUse.getCard());
						String gender = "his ";
						if (currentPlayer.isFemale())
							gender = "her ";
						String message = currentPlayer + " discarded " + gender + bestCardToUse.getCard() + " race.";
						Messenger.display(message, "Race Discarded");
					}
					else if (runActionType == DROP_STICK) {
						String message = currentPlayer + " dropped the " + bestCardToUse.getCard();
						message += " to escape from the " + currentMonster + ".";
						Messenger.display(message, "Item Used");
						
						currentPlayer.discard(bestCardToUse.getCard());
						setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						specialButton.setVisible(false);
						setRunText(ESCAPE_TEXT);
						monsterBox.setEnabled(false);
					}
					else if (runActionType == FLIGHT_SPELL) {
						String message = currentPlayer + " dropped the " + bestCardToUse.getCard();
						message += " to escape from the " + currentMonster + ".";
						Messenger.display(message, "Class Power Used");
						
						currentPlayer.discard(bestCardToUse.getCard());
						setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						flightSpellBonus++;
						if (flightSpellBonus >= 3)
							flightSpellButton.setEnabled(false);
						monsterBox.setEnabled(false);
					}
					
					makeComputerDecisions();
					return;
				}
				else if (runButton.getText().endsWith(FAIL_TEXT)) {
					// Bribe Handling
					if (specialButton.isVisible() && specialButton.getText().equals(BRIBE_TEXT)) {
						AIValuedCard leastValuedTreasure = null;
						for (Card treasure : currentPlayer.getAllItems()) {
							int treasureValue = AICardEvaluator.getCardValueToPlayer(treasure, currentPlayer, currentPlayer.getHandCards());
							if (treasureValue < 200)
								continue;
							
							if (leastValuedTreasure == null || treasureValue < leastValuedTreasure.getValue())
								leastValuedTreasure = new AIValuedCard(treasure, treasureValue);
						}
						if (leastValuedTreasure != null) {					
							int badStuffCost = AIManager.getBadStuffCost(currentMonster, currentHasMate, currentPlayer, true, currentPlayer.getHandCards());
									
							if (badStuffCost - leastValuedTreasure.getValue() > 0) {
								String message = currentPlayer + " used the " + leastValuedTreasure.getCard();
								message += " to bribe the " + currentMonster + " to leave him alone.";
								Messenger.display(message, "Item Used");
								
								currentPlayer.discard(leastValuedTreasure.getCard());
								setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
								monsters.remove(currentMonster);
								monsterBox.setEnabled(true);						
								battle.leaveTreasuresBehind();
								if (monsters.isEmpty()) {
									if (currentPlayer == battle.helper) {
										battle.helper = null;
										setUpForPlayer(battle.activePlayer);
									}
									else {
										battle.endBattle();
										dispose();
										return;
									}
								}
								else {
									monsterBox.removeItem(currentMonster);
									monsterBox.setSelectedIndex(-1);
									monsterBox.setSelectedIndex(0);
									makeComputerDecisions();
								}
							}
						}
					} // END Bribe Handling
					
					LinkedList<ItemCard> carriedItems = new LinkedList<ItemCard>();
					carriedItems.addAll(currentPlayer.getCarriedItems());
					for (ItemCard item : carriedItems) {
						if (item.getID() == Card.I_INVISIBILITY_POTION) {
							int badStuffCost = AIManager.getBadStuffCost(currentMonster, currentHasMate, currentPlayer, true, currentPlayer.getHandCards());
							if (badStuffCost > AICardEvaluator.getCardValueToPlayer(item, currentPlayer, currentPlayer.getHandCards()) * 2) {
								try {
									if (CardPlayManager.playCard(currentPlayer, item, currentMonster, this)) {
										String message = currentPlayer + " used the " + item + " to escape the monster.";
										Messenger.display(message, "Item Used");
										
										makeComputerDecisions();
										return;
									}
								}
								catch (EndGameException ex) {}
							}
						}
						else if ((item.getID() == Card.I_MAGIC_LAMP_1 || item.getID() == Card.I_MAGIC_LAMP_2) && !currentHasMate) {							
							int badStuffCost = AIManager.getBadStuffCost(currentMonster, currentHasMate, currentPlayer, true, currentPlayer.getHandCards());
							int actionValue = badStuffCost - AICardEvaluator.getCardValueToPlayer(item, currentPlayer, currentPlayer.getHandCards());
							if (battle.getMonsterCount() == 1)
								actionValue += battle.getWinTreasureCount(currentMonster) * AIManager.UNKNOWN_CARD_VALUE;
							
							if (actionValue > 0) {
								String message = currentPlayer + " used the " + item + " to escape the monster.";
								Messenger.display(message, "Item Used");
								currentPlayer.discard(item);
								battle.discardMonster(currentMonster, false);
								setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
								monsters.remove(currentMonster);
								if (monsters.isEmpty()) {
									if (battle.getMonsterCount() > 0 && currentPlayer == battle.helper) {
										battle.helper = null;
										setUpForPlayer(battle.activePlayer);
									}
									else
										dispose();
								}
								else {
									monsterBox.removeItem(currentMonster);
									monsterBox.setEnabled(true);
									monsterBox.setSelectedIndex(-1);
									monsterBox.setSelectedIndex(0);
									makeComputerDecisions();
								}
								
								return;
							}
						}
					}
				}
			}
			// Determine if certain actions worth the cost to try to stop the player from escaping
			else {
				if ((currentMonster.getID() == Card.M_WIGHT_BROTHERS || currentMonster.getID() == Card.M_KING_TUT) 
					&& currentPlayer.getLevel() <= 3) {
					continue;
				}
				else if (currentMonster.getID() == Card.M_MR_BONES && currentPlayer.getLevel() <= 2)
					continue;
				else if (runButton.getText().endsWith(ESCAPE_TEXT)) {
					ItemCard glueItem = null;
					for (ItemCard item : player.getCarriedItems())
						if (item.getID() == Card.I_FLASK_OF_GLUE) {
							glueItem = item;								
							break;
						}
					if (glueItem == null)
						continue;
					
					int badStuffValue = AIManager.getBadStuffCost(currentMonster, currentHasMate, currentPlayer, true, currentPlayer.getHandCards());
					badStuffValue *= getChanceToGetCaught(getRunAwayBonus(currentMonster));
					badStuffValue *= rankFactor;
					// must lose at least more than the value of a level to play the glue on them
					if (badStuffValue > AIManager.LEVEL_VALUE) {
						try {
							String message = player + " used the " + glueItem + " to prevent " + currentPlayer + "'s escape.";
							CardPlayManager.playCard(player, glueItem, currentMonster, this);
							Messenger.display(message, "Item Used");
							makeComputerDecisions();
							return;
						}
						catch (EndGameException ex) {}
					}
				}
				else if (runButton.getText().endsWith(RUN_TEXT)) {					
					LinkedList<Card> handCards = new LinkedList<Card>();
					handCards.addAll(player.getHandCards());
					for (Card handCard : handCards) {
						if (handCard.getID() == Card.CU_CHANGE_RACE && currentPlayer.isElf()) {
							RaceCard raceCard = null;
							for (int discardIdx = GM.getDoorDeck().getDiscardPile().size() - 1; discardIdx >= 0; discardIdx--) {
								Card card = GM.getDoorDeck().getDiscardPile().get(discardIdx);
								if (card instanceof RaceCard) {
									raceCard = (RaceCard)card;
									break;
								}								
							}
							
							if (raceCard == null || raceCard.getRace() != Race.ELF) {								
								int curseValue = AIManager.getCurseCost((CurseCard)handCard, currentPlayer, null);
									
								int actionValue = curseValue;
								for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
									MonsterCard monster = battle.getMonster(monsterIdx);
									boolean hasMate = false;
									for (Card enhancerCard : battle.getMonsterEnhancers(monster))
										if (enhancerCard.getID() == Card.OD_MATE)
											hasMate = true;
									
									int maxFailRoll = 4 - getRunAwayBonus(monster);
									if (maxFailRoll >= 6 || maxFailRoll < 0)
										continue;
									
									int noEscapeBadStuffValue = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, null);
									int runAwayBonus = getRunAwayBonus(monster);
									actionValue += noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus - 1);
									actionValue -= noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus);
								}
								actionValue *= rankFactor;
								
								if (actionValue > AICardEvaluator.getCardValueToPlayer(handCard, player, null)) {
									String message = player + " played the " + handCard + " curse";
									message += " on " + currentPlayer + ".";
									Messenger.display(message, "Card From Hand Played");
									player.getHandCards().remove(handCard);
									CurseDialog curseDialog = new CurseDialog(currentPlayer, (CurseCard)handCard, false);
									curseDialog.setVisible(true);
									
									makeComputerDecisions();
									return;
								}
							}
						}
						else if (handCard.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD) {
							int actionValue = AIManager.getCurseCost((CurseCard)handCard, currentPlayer, null);
							for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
								MonsterCard monster = battle.getMonster(monsterIdx);
								boolean hasMate = false;
								for (Card enhancerCard : battle.getMonsterEnhancers(monster))
									if (enhancerCard.getID() == Card.OD_MATE)
										hasMate = true;
								
								int maxFailRoll = 4 - getRunAwayBonus(monster);
								if (maxFailRoll >= 6 || maxFailRoll < 0)
									continue;
								
								int noEscapeBadStuffValue = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, null);
								int runAwayBonus = getRunAwayBonus(monster);
								actionValue += noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus - 1);
								actionValue -= noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus);
							}			
							actionValue *= rankFactor;
							
							if (actionValue > AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards())) {
								String message = player + " played the " + handCard + " curse";
								message += " on " + currentPlayer + ".";
								Messenger.display(message, "Card From Hand Played");
								player.getHandCards().remove(handCard);
								CurseDialog curseDialog = new CurseDialog(currentPlayer, (CurseCard)handCard, false);
								curseDialog.setVisible(true);
								
								makeComputerDecisions();
								return;
							}
						}
						else if (handCard.getID() == Card.CU_LOSE_1_BIG_ITEM) {
							EquipmentCard tuba = null;
							for (EquipmentCard equipment : currentPlayer.getEquippedItems())
								if (equipment.getID() == Card.E_TUBA_OF_CHARM) {
									tuba = equipment;
									break;
								}
							if (tuba == null)
								continue;
							
							int actionValue = AIManager.getCurseCost((CurseCard)handCard, currentPlayer, null);
							for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
								MonsterCard monster = battle.getMonster(monsterIdx);
								boolean hasMate = false;
								for (Card enhancerCard : battle.getMonsterEnhancers(monster))
									if (enhancerCard.getID() == Card.OD_MATE)
										hasMate = true;
								
								int maxFailRoll = 4 - getRunAwayBonus(monster);
								if (maxFailRoll >= 6 || maxFailRoll < 0)
									continue;
								
								int noEscapeBadStuffValue = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, null);
								int runAwayBonus = getRunAwayBonus(monster);
								actionValue += noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus - 1);
								actionValue -= noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus);
							}			
							actionValue *= rankFactor;
							
							if (actionValue > AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards())) {
								String message = player + " played the " + handCard + " curse";
								message += " on " + currentPlayer + ".";
								Messenger.display(message, "Card From Hand Played");
								player.getHandCards().remove(handCard);
								CurseDialog curseDialog = new CurseDialog(currentPlayer, (CurseCard)handCard, false);
								curseDialog.setVisible(true);
								
								makeComputerDecisions();
								return;
							}
						}
						else if (handCard.getID() == Card.CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING) {
							EquipmentCard fastBoots = null;
							for (EquipmentCard equipment : currentPlayer.getEquippedItems())
								if (equipment.getID() == Card.E_BOOTS_OF_RUNNING_REALLY_FAST) {
									fastBoots = equipment;
									break;
								}
							if (fastBoots == null)
								continue;
							
							int actionValue = AIManager.getCurseCost((CurseCard)handCard, currentPlayer, null);
							for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
								MonsterCard monster = battle.getMonster(monsterIdx);
								boolean hasMate = false;
								for (Card enhancerCard : battle.getMonsterEnhancers(monster))
									if (enhancerCard.getID() == Card.OD_MATE)
										hasMate = true;
								
								int maxFailRoll = 4 - getRunAwayBonus(monster);
								if (maxFailRoll >= 6 || maxFailRoll < 0)
									continue;
								
								int noEscapeBadStuffValue = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, null);
								int runAwayBonus = getRunAwayBonus(monster);
								actionValue += noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus - 2);
								actionValue -= noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus);
							}			
							actionValue *= rankFactor;
							
							if (actionValue > AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards())) {
								String message = player + " played the " + handCard + " curse";
								message += " on " + currentPlayer + ".";
								Messenger.display(message, "Card From Hand Played");
								player.getHandCards().remove(handCard);
								CurseDialog curseDialog = new CurseDialog(currentPlayer, (CurseCard)handCard, false);
								curseDialog.setVisible(true);
								
								makeComputerDecisions();
								return;
							}
						}
						else if (handCard.getID() == Card.CU_LOSE_TWO_CARDS && currentPlayer.isWizard()) {
							if (currentPlayer.getHandCards().size() >= 2) {
								int actionValue = 0;
								for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
									MonsterCard monster = battle.getMonster(monsterIdx);
									boolean hasMate = false;
									for (Card enhancerCard : battle.getMonsterEnhancers(monster))
										if (enhancerCard.getID() == Card.OD_MATE)
											hasMate = true;
									
									int maxFailRoll = 4 - getRunAwayBonus(monster);
									if (maxFailRoll >= 6 || maxFailRoll < 0)
										continue;
									
									int noEscapeBadStuffValue = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, null);
									int runAwayBonus = getRunAwayBonus(monster);
									actionValue += noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus - 2);
									actionValue -= noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus);
								}			
								actionValue *= rankFactor;
								
								if (actionValue > AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards())) {
									String message = player + " played the " + handCard + " curse";
									message += " on " + currentPlayer + ".";
									Messenger.display(message, "Card From Hand Played");
									player.getHandCards().remove(handCard);
									CurseDialog curseDialog = new CurseDialog(currentPlayer, (CurseCard)handCard, false);
									curseDialog.setVisible(true);
									
									makeComputerDecisions();
									return;
								}
							}
						}
						else if (handCard.getID() == Card.CU_LOSE_YOUR_RACE && currentPlayer.isElf()) {
							int curseValue = AIManager.getCurseCost((CurseCard)handCard, currentPlayer, null);
								
							int actionValue = curseValue;
							for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
								MonsterCard monster = battle.getMonster(monsterIdx);
								boolean hasMate = false;
								for (Card enhancerCard : battle.getMonsterEnhancers(monster))
									if (enhancerCard.getID() == Card.OD_MATE)
										hasMate = true;
								
								int maxFailRoll = 4 - getRunAwayBonus(monster);
								if (maxFailRoll >= 6 || maxFailRoll < 0)
									continue;
								
								int noEscapeBadStuffValue = AIManager.getBadStuffCost(monster, hasMate, currentPlayer, true, null);
								int runAwayBonus = getRunAwayBonus(monster);
								actionValue += noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus - 1);
								actionValue -= noEscapeBadStuffValue * getChanceToGetCaught(runAwayBonus);
							}			
							actionValue *= rankFactor;
							
							if (actionValue > AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards())) {
								String message = player + " played the " + handCard + " curse";
								message += " on " + currentPlayer + ".";
								Messenger.display(message, "Card From Hand Played");
								player.getHandCards().remove(handCard);
								CurseDialog curseDialog = new CurseDialog(currentPlayer, (CurseCard)handCard, false);
								curseDialog.setVisible(true);
								
								makeComputerDecisions();
								return;
							}
						}
					}
				}
			}			
		}
		
		refresh();
	}
	
	@Override
	public void refresh() {
		if (monsterBox == null || monsterBox.getSelectedItem() == null)
			return;
		if (runButton == null || escapeConditionLabel == null)
			return;
		
		if (getRunText().equals(RUN_TEXT)) {
			String escapeText = " </html>";
			int runAwayBonus = getRunAwayBonus((MonsterCard)monsterBox.getSelectedItem());
			int lowestEscapeRoll = 5 - runAwayBonus;
			if (currentPlayer.hasChickenOnHead()) {
				lowestEscapeRoll++;
				escapeText = " <br> Note: The Chicken on Your Head curse has been included in this escape need." + escapeText;
			}
			if (lowestEscapeRoll < 1)
				lowestEscapeRoll = 1;
			
			if (lowestEscapeRoll <= 6)
				escapeConditionLabel.setText("<html> Need to roll " + lowestEscapeRoll + " or more to escape." + escapeText);
			else
				escapeConditionLabel.setText("<html> Need to roll " + lowestEscapeRoll + " or more, on a six-sided die, to escape. Good luck." + escapeText);
		}
		
		super.refresh();
	}
	
	/**
	 * Returns the run away bonus of the current player based on race, items, and monster.
	 * @param monster the monster the player is trying to run from
	 * @return
	 */
	public int getRunAwayBonus(MonsterCard monster) {
		int runAwayBonus = 0;
		
		if (currentPlayer.isElf())
			runAwayBonus++;
		if (!currentPlayer.isHuman() && currentPlayer.isHalfling())
			runAwayBonus--;
		
		if (currentPlayer.hasEquipped(Card.E_TUBA_OF_CHARM))
			runAwayBonus += 1;
		if (currentPlayer.hasEquipped(Card.E_BOOTS_OF_RUNNING_REALLY_FAST))
			runAwayBonus += 2;
		
		if (battle.getMonsterEnhancers(monster) != null) {
			Iterator<DoorCard> enhancerCardIter = battle.getMonsterEnhancers(monster).iterator();
			while (enhancerCardIter.hasNext())
				if (enhancerCardIter.next().getID() == Card.OD_MATE)
					runAwayBonus -= 1;
		}
		
		if (monster.getID() == Card.M_FLYING_FROGS)
			runAwayBonus -= 1;
		else if (monster.getID() == Card.M_GELATINOUS_OCTAHEDRON)
			runAwayBonus += 1;
		else if (monster.getID() == Card.M_LAME_GOBLIN)
			runAwayBonus += 1;
		else if (monster.getID() == Card.M_SNAILS_OF_SPEED)
			runAwayBonus -= 2;
		
		runAwayBonus += flightSpellBonus;
		
		return runAwayBonus;
	}
	
	/**
	 * Returns the current player who is running away.
	 * @return the current player who is running away
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Returns the Battle that is currently ongoing.
	 * @return the Battle that is currently ongoing
	 */
	public Battle getBattle() {
		return battle;
	}
	
	/**
	 * Returns the text code for the available running phase/option.
	 * @return the text code for the available running phase/option (text of main action button)
	 */
	public String getRunText() {
		return runButton.getText();
	}
	
	/**
	 * Sets the text code for the available running phase/option.
	 * @param text the text code to set for the available running phase/option (text of main action button)
	 */
	public void setRunText(String text) {
		if (currentPlayer.isComputer())
			text = "Allow " + text;
		runButton.setText(text);
		
		refresh();
	}
	
	/**
	 * Sets it to automatically escape all monsters for the current player or all players.
	 * @param isAllPlayers when true sets all players to escape all monsters; when false only current player escapes all
	 */
	public void escapeAll(boolean isAllPlayers) {
		escapeAll = true;
		if (isAllPlayers)
			everyoneEscapeAll = true;
		setRunText(ESCAPE_TEXT);
	}
}
