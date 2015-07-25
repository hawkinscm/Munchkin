
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.DoorDeckFactory;
import model.GM;
import model.Player;
import model.PlayerType;
import model.TreasureDeckFactory;
import model.card.Card;
import model.card.DoorCard;
import model.card.TreasureCard;
import gui.components.CustomButton;
import gui.components.CustomDialog;

/**
 * A Singleton-type class used in testing - provides specific game manipulation methods.
 */
public class GUITester {

	// Total number of cards in the game
	private static final int TOTAL_CARDS = 168;
	
	/**
	 * Lets tester choose a Player and any Card(s) from the game which will be taken and given to that Player.
	 */
	public static void getCards() {
		// Dialog that allows choosing of Player and Cards
		CustomDialog cardsDialog = new CustomDialog("Take Cards") {
			private static final long serialVersionUID = 1L;
			
			private JComboBox playerBox;
			private CustomButton takeButton;
			
			@Override
			public void refresh() {
				// initialize variable and display GUI controls
				c.insets = new Insets(5, 5, 5, 5);
				//c.anchor = GridBagConstraints.WEST;
				
				Dimension screenSize = getToolkit().getScreenSize();
				final int OS_TASKBAR_HEIGHT = 30;
				setPreferredSize(new Dimension(screenSize.width, screenSize.height-OS_TASKBAR_HEIGHT));
				
				final Card[] cards = new Card[TOTAL_CARDS];
				final JCheckBox[] cardCheckBoxes = new JCheckBox[TOTAL_CARDS];
				
				// Get list of all players and a pseudo-player who can be selected to discard the taken cards
				LinkedList<Player> allPlayers = new LinkedList<Player>();
				allPlayers.add(new Player(new GUI(0), "DISCARD", true, PlayerType.HUMAN));
				allPlayers.addAll(GM.getPlayers());
				playerBox = new JComboBox(allPlayers.toArray());	
				playerBox.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER)
							takeButton.buttonPressed();
					}
					public void keyReleased(KeyEvent e) {}
					public void keyTyped(KeyEvent e) {}
				});					
				getContentPane().add(playerBox, c);
				
				c.gridx++;
				// Takes the cards from wherever they are and gives them to the selected Player (or Discard)
				takeButton = new CustomButton("Take Cards") {
					private static final long serialVersionUID = 1L;
					
					public void buttonPressed() {
						for (int index = 0; index < TOTAL_CARDS; index++) {
							JCheckBox box = cardCheckBoxes[index];
							if (box.isSelected()) {
								Card card = cards[index];
								if (!GM.getDoorDeck().removeCard(card) && !GM.getTreasureDeck().removeCard(card)) {
									for (Player player : GM.getPlayers()) {
										if (card == player.getHalfBreedCard()) {
											player.setHalfBreedCard(null);
											if (player.getRaceCards().size() > 1)
												player.discardRaceCard(player.getRaceCards().getLast());
										}
										else if (card == player.getSuperMunchkinCard()) {
											player.setSuperMunchkinCard(null);
											if (player.getClassCards().size() > 1)
												player.discardClassCard(player.getClassCards().getLast());
										}
										else if (card.getID() == Card.OD_CHEAT)
											player.removeCheat();
										else if (card == player.getChangeSexCard())
											player.removeSexChangeCurse();
										else if (card == player.getMalignMirrorCard())
											player.removeMalignMirror();
										else if (card == player.getChickenOnHeadCard())
											player.removeChickenOnHeadCurse();
										else
											player.discard(card);
										
										GM.getDoorDeck().removeCard(card);
										GM.getTreasureDeck().removeCard(card);
									}
								}
								
								Player selectedPlayer = (Player)playerBox.getSelectedItem();
								selectedPlayer.addCard(card);
								if (playerBox.getSelectedIndex() == 0)
									selectedPlayer.discard(card);
							}
						}
						
						dispose();
					}
				};
				getContentPane().add(takeButton, c);
				
				c.gridx++;
				CustomButton allDoorButton = new CustomButton("All Door") {
					private static final long serialVersionUID = 1L;
					
					public void buttonPressed() {
						for (int cardIdx = 0; cardIdx < TOTAL_CARDS; cardIdx++) {
							if (cards[cardIdx] instanceof DoorCard)
								if (cardCheckBoxes[cardIdx].isEnabled() && cardCheckBoxes[cardIdx].getForeground() != Color.RED)
									cardCheckBoxes[cardIdx].setSelected(true);
						}
					}
				};
				getContentPane().add(allDoorButton, c);
				
				c.gridx++;
				CustomButton allTreasureButton = new CustomButton("All Treasure") {
					private static final long serialVersionUID = 1L;
					
					public void buttonPressed() {
						for (int cardIdx = 0; cardIdx < TOTAL_CARDS; cardIdx++) {
							if (cards[cardIdx] instanceof TreasureCard)
								if (cardCheckBoxes[cardIdx].isEnabled() && cardCheckBoxes[cardIdx].getForeground() != Color.RED)
									cardCheckBoxes[cardIdx].setSelected(true);
						}
					}
				};
				getContentPane().add(allTreasureButton, c);
				
				// Load every card in the game into a list
				LinkedList<Card> allCards = new LinkedList<Card>();
				allCards.addAll(DoorDeckFactory.buildDeck());
				allCards.addAll(TreasureDeckFactory.buildDeck());				
				
				JPanel selectCardsPanel = new JPanel();
				selectCardsPanel.setLayout(new GridBagLayout());
				
				c.insets = new Insets(0, 0, 0, 0);
				c.gridx = 0;
				c.gridy = 0;
				c.anchor = GridBagConstraints.WEST;
				// Look for every card in the game, if any are not found print out an error message.
				for (int index = 0; index < TOTAL_CARDS; index++) {
					Card card = allCards.get(index);
					cards[index] = card;
					cardCheckBoxes[index] = new JCheckBox(card.getName());
					boolean cardFound = false;
					if (!GM.getDoorDeck().containsCard(card) && !GM.getTreasureDeck().containsCard(card)) {
						boolean ownedByPlayer = false;
						for (Player player : GM.getPlayers()) {
							if (player.getHandCards().contains(card) || player.getAllItems().contains(card))
								ownedByPlayer = true;
							else if (player.getRaceCards().contains(card) || player.getClassCards().contains(card))
								ownedByPlayer = true;
							else if (card.equals(player.getSuperMunchkinCard()) || card.equals(player.getHalfBreedCard()))
								ownedByPlayer = true;
							else if (card.equals(player.getChangeSexCard()) || card.equals(player.getChickenOnHeadCard()))
								ownedByPlayer = true;
							else if (card.equals(player.getMalignMirrorCard()) || card.equals(player.getHirelingCard()))
								ownedByPlayer = true;
							else if (card.getID() == Card.OD_CHEAT && player.getCheatingItemCard() != null)
								ownedByPlayer = true;
							
							if (ownedByPlayer) {
								cardFound = true;
								cardCheckBoxes[index].setForeground(Color.RED);
								break;
							}
						}
					}
					else
						cardFound = true;
					
					cardCheckBoxes[index].setEnabled(cardFound);
					
					selectCardsPanel.add(cardCheckBoxes[index], c);
					if (index % 28 == 27) {
						c.gridx++;
						c.gridy = 0;
					}
					else
						c.gridy++;
				}
				
				c.weightx = 1.0;
				c.weighty = 1.0;
				c.gridx = 0; 
				c.gridy = 1;
				c.gridwidth = 6;
				c.fill = GridBagConstraints.BOTH;
				JScrollPane scrollPane = new JScrollPane(selectCardsPanel);
				scrollPane.getViewport().setPreferredSize(getPreferredSize());
				getContentPane().add(scrollPane, c);
				super.refresh();
			}
		};
	
		cardsDialog.refresh();
		cardsDialog.setVisible(true);
	}
	
	/**
	 * Lets the tester change the level of any of the Players to whatever he needs.
	 */
	public static void setLevels() {
		// Dialog that allows the changing of Player levels
		CustomDialog setLevelsDialog = new CustomDialog("Set Levels") {
			private final static long serialVersionUID = 1L;
			
			// initialize variables and display GUI controls
			private JLabel playerNames[];
			private JTextField playerLevels[];
			
			@Override
			public void refresh() {
				final LinkedList<Player> players = GM.getPlayers();
				final int numPlayers = players.size();
				playerNames = new JLabel[numPlayers];
				playerLevels = new JTextField[numPlayers];
				
				c.ipadx = 5;
				for (int index = 0; index < numPlayers; index++) {
					c.gridx = 0;
					playerNames[index] = new JLabel(players.get(index).getName());
					getContentPane().add(playerNames[index], c);
					
					c.gridx++;
					playerLevels[index] = new JTextField(String.valueOf(players.get(index).getLevel()));
					getContentPane().add(playerLevels[index], c);
					
					c.gridy++;
				}				
				
				// Display Button to signal when the tester is done manipulating Player levels.
				// Makes those changes and then closes the dialog.
				getContentPane().add(
					new CustomButton("OK") {
						private final static long serialVersionUID = 1L;
					
						public void buttonPressed() {
							for (int index = 0; index < numPlayers; index++) {
								Player player = players.get(index);
								
								try {
									int newLevel = Integer.parseInt(playerLevels[index].getText());
									int currentLevel = player.getLevel();
									if (newLevel > currentLevel)
										player.goUpLevels(newLevel - currentLevel, false);
									else if (currentLevel > newLevel)
										player.goDownLevels(currentLevel - newLevel);
								}
								catch (NumberFormatException ex) {}
							}
						
							dispose();
						}
					}, c);
				
				super.refresh();
			}
		};
		
		setLevelsDialog.refresh();
		setLevelsDialog.setVisible(true);
	}	
}
