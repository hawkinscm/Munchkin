
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import ai.AIBattleManager;
import ai.AIManager;

import exceptions.EndGameException;
import gui.components.CustomButton;
import gui.components.PlayerLabel;
import gui.components.PlayerMenu;
import gui.components.Messenger;

import model.Battle;
import model.CardPlayManager;
import model.DoorDeck;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import model.TreasureDeck;
import model.card.Card;
import model.card.DoorCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.TreasureCard;

/**
 * The main controlling GUI for the program.
 */
public class GUI extends JFrame implements Observer {
	// required for serialization
	private static final long serialVersionUID = 1L;
	
	// Set to true when testing; normally false
	public static boolean isTestRun = false;
	public static boolean isDebug = false;
	public static boolean isCheating = false;
	
	// Text codes for certain game phases
	private final String OPEN_DOOR_TEXT = "Open A Door";
	private final String LOOT_ROOM_TEXT = "Loot The Room";
	private final String END_TURN_TEXT = "End Turn";
	private final String configFile = "Munchkin.cfg";
	
	// Needed for debugging and logging
	private static String logfilename = "munchkin.log";
	private static String logfilename_bak = ".munchkin.log.bak";
	private static PrintStream logStream = null;
	
	// Menus for the program
	private JMenuBar menuBar;
	private LinkedList<PlayerMenu> playerMenus;
	
	// GUI controls for the game
	private BattlePanel battlePanel;
	private JPanel mainPanel;
	private LinkedList<PlayerLabel> playerLabels;
	private JLabel doorDiscardImageLabel;
	private JLabel treasureDiscardImageLabel;
	private CustomButton mainActionButton;
	private CustomButton lookForTroubleButton;
	
	// Whether or not a game is in progress
	private boolean isGameStarted;
	
	private LinkedList<Player> defaultPlayers;
	
	/**
	 * Creates a new GUI and begins the program.
	 */
	public GUI() {
		// Initialize the GUI with a title
		super("Munchkin");
		
		// Changes the default icon displayed in the title bar
		setIconImage((new ImageIcon(GUI.class.getResource("images/male_munchkin.jpg"))).getImage());
		
		// Start the GUI Maximized (without covering up a standard OS taskbar)
		setLocation(0, 0);
		Dimension screenSize = getToolkit().getScreenSize();
		final int OS_TASKBAR_HEIGHT = 30;
		setSize(screenSize.width, screenSize.height-OS_TASKBAR_HEIGHT);
		
		// Cause the application to call a special exit method on close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitProgram();
			}			
		});
		
		defaultPlayers = new LinkedList<Player>();
		loadConfigFile();
				
		// Create the Menu Bar
		createMenuBar();
		playerMenus = new LinkedList<PlayerMenu>();
		playerLabels = new LinkedList<PlayerLabel>();
		
		isGameStarted = false;
		setVisible(true);
		newGame();
	}
	
	/**
	 * Creates a GUI to be used for testing purposes
	 * @param mockGUI unused variable needed to distinguish between a normal GUI and a test GUI
	 */
	public GUI(int mockGUI) {
		playerMenus = new LinkedList<PlayerMenu>();
	}
	
	/**
	 * Initializes and sets up the main menu bar.
	 */
	private void createMenuBar() {
		menuBar = new JMenuBar();
			
		// CREATE GAME MENU
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		
		// new game
		JMenuItem newGameMenuItem = new JMenuItem("New Game");
		newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F2, 0));
		newGameMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});		
		
		// quit
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		});
		
		gameMenu.add(newGameMenuItem);
		gameMenu.add(quitMenuItem);
		// END GAME MENU
		
		// CREATE VIEW MENU
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		// view rules
		JMenuItem rulesMenuItem = new JMenuItem("Rules");
		rulesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewRules();
			}
		});
		
		// view door discard pile
		JMenuItem doorDiscardMenuItem = new JMenuItem("Door Discard Pile");
		doorDiscardMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewDoorDiscardPile();
			}
		});
		
		// view treasure discard pile
		JMenuItem treasureDiscardMenuItem = new JMenuItem("Treasure Discard Pile");
		treasureDiscardMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewTreasureDiscardPile();
			}
		});
		
		// view any card in the deck sorted by group
		JMenuItem anyCardMenuItem = new JMenuItem("All Cards");
		anyCardMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(new ViewCardsByGroupDialog()).setVisible(true);
			}
		});
		
		viewMenu.add(rulesMenuItem);
		viewMenu.add(doorDiscardMenuItem);
		viewMenu.add(treasureDiscardMenuItem);
		viewMenu.add(anyCardMenuItem);
		// END VIEW MENU
		
		menuBar.add(gameMenu);
		menuBar.add(viewMenu);
		
		// Special game manipulating options used in testing/cheating
		if (isCheating) {
			JMenu testMenu = new JMenu("Cheat");
			testMenu.setMnemonic(KeyEvent.VK_C);
			
			// Retrieve a specific card from wherever it is in the game
			JMenuItem getCardsMenuItem = new JMenuItem("Get Cards");
			getCardsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUITester.getCards();
				}
			});
			
			// Change the levels of the players.
			JMenuItem setLevelsMenuItem = new JMenuItem("Set Levels");
			setLevelsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUITester.setLevels();
				}
			});
						
			testMenu.add(getCardsMenuItem);
			testMenu.add(setLevelsMenuItem);
			
			menuBar.add(testMenu);
		}
		
		// Logging feature for beta testers and note taking
		if (isDebug) {
			JMenu logMenu = new JMenu("Log");
			logMenu.setMnemonic(KeyEvent.VK_L);
			
			// Gather data and user input to print to log file
			JMenuItem writeEntryMenuItem = new JMenuItem("Write Entry");
			writeEntryMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					(new LogDialog()).setVisible(true);
				}
			});
			logMenu.add(writeEntryMenuItem);
			
			
			menuBar.add(logMenu);
		}
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Initializes and sets up the main display.
	 */
	private void initiateMainDisplay() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 0, 8, 0);
		c.gridx = 0;
		c.gridy = 0;
		
		playerLabels = new LinkedList<PlayerLabel>();
		JPanel playerDisplayPanel = new JPanel();
		playerDisplayPanel.setLayout(new GridBagLayout());
		GridBagConstraints panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.anchor = GridBagConstraints.NORTH;
		panelc.insets = new Insets(0, 5, 0, 5);
		for (Player player : GM.getPlayers()) {
			PlayerLabel playerLabel = new PlayerLabel(player);
			playerLabels.add(playerLabel);
			playerDisplayPanel.add(playerLabel, panelc);
			panelc.gridx++;
		}
		mainPanel.add(playerDisplayPanel, c);
		
		// set up door and treasure decks
		c.gridy++;
		JPanel deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		
		if (GM.getDoorDeck().getDiscardPile().isEmpty())
			doorDiscardImageLabel = new JLabel();
		else
			doorDiscardImageLabel = new JLabel(GM.getDoorDeck().getDiscardPile().peek().getPicture());
		doorDiscardImageLabel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				viewDoorDiscardPile();
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		deckPanel.add(doorDiscardImageLabel);		
		
		deckPanel.add(new JLabel(new ImageIcon(GUI.class.getResource("images/DoorCard.jpg"))));
		
		deckPanel.add(new JLabel(new ImageIcon(GUI.class.getResource("images/TreasureCard.jpg"))));
		
		if (GM.getTreasureDeck().getDiscardPile().isEmpty())
			treasureDiscardImageLabel = new JLabel();
		else
			treasureDiscardImageLabel = new JLabel(GM.getTreasureDeck().getDiscardPile().peek().getPicture());
		treasureDiscardImageLabel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				viewTreasureDiscardPile();
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		deckPanel.add(treasureDiscardImageLabel);
		
		mainPanel.add(deckPanel, c);
		
		c.gridy++;
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		
		// main action button associated with each phase of the game; action/text changes based on phase
		mainActionButton = new CustomButton() {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				if (mainActionButton.getText().endsWith(OPEN_DOOR_TEXT))
					GM.openDoor();
				else if (mainActionButton.getText().equals(LOOT_ROOM_TEXT)) {
					try {
						if (GM.isLookForTroublePhase())
							GM.moveNextPhase();
						
						GM.getActivePlayer().drawDoorCards(1, false);
						GM.moveNextPhase();
					}
					catch (EndGameException ex) {
						endGame();
					}
				}
				else if (mainActionButton.getText().equals(END_TURN_TEXT)) {
					GM.endPlayerTurn();
					TakeTreasuresPanel tPanel = null;
					for (Component component : getContentPane().getComponents())
						if (component instanceof TakeTreasuresPanel)
							tPanel = (TakeTreasuresPanel)component;
					
					if (tPanel != null)
						getContentPane().remove(tPanel);
				}
			}
		};
		buttonPanel.add(mainActionButton);
		
		// Allows a player to "look for trouble" rather than searching a room
		lookForTroubleButton = new CustomButton("Look For Trouble") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				// loads all the monsters a player can play, if any
				LinkedList<Card> monsters = new LinkedList<Card>();
				Iterator<Card> cardIter = GM.getActivePlayer().getHandCards().iterator();
				while (cardIter.hasNext()) {
					Card card = cardIter.next();
					if (card instanceof MonsterCard)
						monsters.add(card);
				}
				if (monsters.isEmpty()) {
					String message = "You must have a Monter Card in hand to find trouble.";
					Messenger.error(message, "Look For Trouble", null);
					GM.moveNextPhase();
					return;
				}				
				
				// lets player choose which Monster Card to play
				String prompt = GM.getActivePlayer().getName() + ", choose a monster to fight.";
				ChooseCardDialog dialog = new ChooseCardDialog(monsters, prompt);
				dialog.setVisible(true);
				MonsterCard monster = (MonsterCard)dialog.getSelectedCard();
				if (monster == null)
					return;
				
				// Plays monster and begins a battle
				GM.getActivePlayer().getHandCards().remove(monster);
				GM.moveToBattlePhase();
				beginBattle(new Battle(GM.getActivePlayer(), monster));
			}
		};
		buttonPanel.add(lookForTroubleButton);
		
		mainPanel.add(buttonPanel, c);
	}
	
	/**
	 * Reinitializes and sets up the pop-up menus for each player.
	 */
	private void createPlayerMenus() {
		Iterator<PlayerMenu> menuIter = playerMenus.iterator();
		while (menuIter.hasNext())
			menuBar.remove(menuIter.next());
		
		playerMenus.clear();
		
		Iterator<Player> playerIter = GM.getPlayers().iterator();
		while (playerIter.hasNext()) {
			final Player player = playerIter.next();
			PlayerMenu playerMenu = new PlayerMenu(this, player);
			playerMenus.add(playerMenu);
			menuBar.add(playerMenu);
		}
	}
	
	/**
	 * Begins a new game.
	 */
	private void newGame() {
		// If a game is already in process, prompt the user to see if they want to end it and begin anew
		if (isGameStarted) {
			String message = "End current game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION) {
				if (!isGameStarted) { 
					if (GM.getPlayers() != null && !GM.getPlayers().isEmpty())
						updateConfigFile();
					
					this.dispose();
					cleanUpLog();
					System.exit(0);
				}
				return;
			}
		}		
		
		// Prompt the user for how many people will play
		int numPlayers = 0;
		while (numPlayers < 3 || numPlayers > 6) {
			try {
				String input = getInput("New Game", "How Many Will Play (3-6)?");
				if (input == null) {
					if (!isGameStarted) { 
						if (GM.getPlayers() != null && !GM.getPlayers().isEmpty())
							updateConfigFile();
						
						this.dispose();
						cleanUpLog();
						System.exit(0);
					}
					
					return;
				}
				
				numPlayers = Integer.parseInt(input);
			}
			catch(NumberFormatException ex) {}
		}
		
		// Clear all GUI features from last game
		getContentPane().removeAll();
		validate();
		repaint();
		
		isGameStarted = true;
							
		// Lets the user define the players for the game
		CreatePlayersDialog playerInput = new CreatePlayersDialog(this, numPlayers, defaultPlayers);
		playerInput.setVisible(true);
		GM.newGame(this, playerInput.getPlayers());
		
		// Adds the players to the game and deals them starting cards.
		Iterator<Player> playerIter = GM.getPlayers().iterator();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			GM.setActivePlayer(player);
			if (player.isComputer())
				AIManager.playHandCards(player);
			else
				displayHand(player);
			if (GM.startedWithDivineIntervention() && player.isCleric())
				player.goUpLevel(true);
		}
		GM.setActivePlayer(GM.getPlayers().getFirst());
		GM.startFirstPlayersTurn();
				
		// Create and set up menus, controls, and displays
		createPlayerMenus();
		
		initiateMainDisplay();
		showMainDisplay();
		updateGameDisplay();
	}
	
	/**
	 * Allows special exit handling to be performed on exit - prompt the user if they want to end the current game.
	 */
	private void exitProgram() {
		int choice = JOptionPane.showConfirmDialog(null, "End current game?", "Quit", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (choice != JOptionPane.YES_OPTION)
			return;
				
		if (GM.getPlayers() != null && !GM.getPlayers().isEmpty())
			updateConfigFile();
		
		this.dispose();
		cleanUpLog();
		System.exit(0);
	}
	
	/**
	 * Returns an input gleaned from the user.
	 * @param title text to display on the prompt window
	 * @param message message to display, asking the user for input
	 * @return the input entered by the user
	 */
	private String getInput(String title, String message) {
		return JOptionPane.showInputDialog(this, message, title, JOptionPane.OK_OPTION);
	}
	
	/**
	 * Displays the rules of the game using a standard text file reader.
	 */
	private void viewRules() {
		try {
			if (Desktop.isDesktopSupported()) {
				File rulesFile = new File(".Munchkin_Rules.txt");
				if (!rulesFile.exists()) {
					rulesFile.createNewFile();
					
					InputStream input = GUI.class.getResourceAsStream("Rules.txt");					
					OutputStream output = new FileOutputStream(rulesFile);
					byte buf[] = new byte[1024];
					int len;
					while((len = input.read(buf)) > 0)
						output.write(buf, 0, len);
					output.close();
					input.close();
					
					rulesFile.deleteOnExit();
				}

				Desktop.getDesktop().open(rulesFile);				
			}
		}
		catch (IOException ex) {
			Messenger.error(ex.getMessage(), "File Error", this);
		}
	}
	
	/**
	 * Allows players to search through the Door Deck discard pile.
	 */
	private void viewDoorDiscardPile() {
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(GM.getDoorDeck().getDiscardPile());
		if (cards.isEmpty()) {
			Messenger.display("There are currently no discarded Door Cards", "View Cards", this);
			return;
		}
		
		DisplayCardsDialog dialog = new DisplayCardsDialog(cards, "Door Discard Pile");
		dialog.setVisible(true);
	}
	
	/**
	 * Allows players to search through the Treasure Deck discard pile.
	 */
	private void viewTreasureDiscardPile() {
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(GM.getTreasureDeck().getDiscardPile());
		if (cards.isEmpty()) {
			Messenger.display("There are currently no discarded Treasure Cards", "View Cards", this);
			return;
		}
		
		DisplayCardsDialog dialog = new DisplayCardsDialog(cards, "Treasure Discard Pile");
		dialog.setVisible(true);
	}
	
	/**
	 * Loads the configuration defaults and settings from the config file.
	 */
	private void loadConfigFile() {
		File file = new File(configFile);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(";");
					if (tokens.length == 4) {
						try {
							Point startLocation = getLocation();
							Dimension startSize = getSize();
							for (int tokenIdx = 0; tokenIdx < tokens.length; tokenIdx++) {
								String[] windowParams = tokens[tokenIdx].split(":");
								if (windowParams.length != 2)
									break;
								
								if (windowParams[0].equalsIgnoreCase("startX"))
									startLocation.x = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startY"))
									startLocation.y = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("width"))
									startSize.width = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("height"))
									startSize.height = Integer.parseInt(windowParams[1]);
							}
							
							setLocation(startLocation);
							setSize(startSize);
						}
						catch (NumberFormatException ex) {}
						
						continue;
					}
					
					tokens = line.split(":");
					if (tokens.length < 3)
						continue;
					
					PlayerType type = PlayerType.parseType(tokens[1]);
										
					boolean isMale = true;
					if (tokens[2].equals("F"))
						isMale = false;
					else if (!tokens[2].equals("M")) {
						if (tokens[2].equals("X")) {
							if (tokens[0].equals("Debug"))
								isDebug = true;
							
							if (tokens[1].equals("Cheater"))
								isCheating = true;
						}
						
						continue;
					}
					
					if (type == null)
						continue;
					
					defaultPlayers.add(new Player(this, tokens[0], isMale, type));
				}
				reader.close();
			} catch (IOException ex) {}
		}
	}
	
	/**
	 * Writes the latest configuration defaults and settings to the config file.
	 */
	private void updateConfigFile() {
		File file = new File(configFile);
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			writer.write("startX:" + getLocation().x + ";");
			writer.write("startY:" + getLocation().y + ";");
			writer.write("width:" + getSize().width + ";");
			writer.write("height:" + getSize().height + "");
			writer.newLine();
			
			for (Player player : defaultPlayers) {
				String gender = "M";
				if (player.isFemale())
					gender = "F";
				writer.write(player.getName().replace(':', ' ') + ":" + player.getPlayerType() + ":" + gender);
				writer.newLine();
			}
			
			if (isDebug || isCheating) {
				String name = "PlayerX";
				if (isDebug)
					name = "Debug";
				
				String type = "TypeX";
				if (isCheating)
					type = "Cheater";
				
				writer.write(name + ":" + type + ":" + "X");
				writer.newLine();
			}
			
			writer.close();
		} catch (IOException ex) {}
	}
	
	/**
	 * Marks the given player as the current player via the player label.
	 * @param player player to mark as active player
	 */
	private void setActivePlayerLabel(Player player) {
		for (PlayerLabel playerLabel : playerLabels) {
			if (playerLabel.getPlayer() == player)
				playerLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
			else
				playerLabel.setBorder(null);
		}			
	}
	
	/**
	 * Updates and displays the main GUI game display.
	 */
	public void showMainDisplay() {
		getContentPane().add(new JScrollPane(mainPanel));
		for (PlayerLabel playerLabel : playerLabels)
			playerLabel.updatePlayerInfo();
		
		validate();
		repaint();
	}
	
	/**
	 * Hides the main GUI game pane.
	 */
	public void hideMainDisplay() {
		getContentPane().removeAll();
		validate();
		repaint();
	}
	
	/**
	 * Updates the main GUI game display based on players and current phase.
	 */
	public void updateGameDisplay() {
		Player activePlayer = GM.getActivePlayer();
		
		if (GM.isOpenDoorPhase()) {
			setActivePlayerLabel(activePlayer);
			mainActionButton.setText(OPEN_DOOR_TEXT);
			lookForTroubleButton.setVisible(false);
			
			if (activePlayer.isComputer()) {
				mainActionButton.setEnabled(false);
				LinkedList<ItemCard> carriedItems = new LinkedList<ItemCard>();
				carriedItems.addAll(activePlayer.getCarriedItems());
				for (ItemCard item : carriedItems) {
					try {
						CardPlayManager.playCard(activePlayer, item);
					} catch (EndGameException e) {
						endGame();
						return;
					}
				}
				AIManager.playHandCards(activePlayer);
				if (activePlayer.isThief())
					(new StealItemDialog(activePlayer)).setVisible(true);
				(new TradeItemsDialog(this, activePlayer)).setVisible(true);
				(new SellItemsDialog(this, activePlayer)).setVisible(true);
				AIManager.equipBest(activePlayer);
				mainActionButton.setEnabled(true);
			}
		}
		else if (GM.isLookForTroublePhase()) {
			mainActionButton.setText(LOOT_ROOM_TEXT);
			lookForTroubleButton.setVisible(true);
			
			if (activePlayer.isComputer()) {
				MonsterCard monster = AIBattleManager.getMonsterToBattle(activePlayer);

				if (monster != null) {
					// Plays monster and begins a battle
					Messenger.display(activePlayer + " decided to look for trouble by playing the " + monster + ".", "Look For Trouble", this);
					GM.getActivePlayer().getHandCards().remove(monster);
					GM.moveToBattlePhase();
					beginBattle(new Battle(activePlayer, monster));
				}
				else
					GM.moveNextPhase();
			}
		}
		else if (GM.isLootRoomPhase()) {
			mainActionButton.setText(LOOT_ROOM_TEXT);
			lookForTroubleButton.setVisible(false);
			
			if (activePlayer.isComputer()) {
				try {
					activePlayer.drawDoorCards(1, false);
					GM.moveNextPhase();
				}
				catch (EndGameException ex) {
					endGame();
				}
			}
		}
		else if (GM.isCharityPhase()) {
			mainActionButton.setText(END_TURN_TEXT);
			lookForTroubleButton.setVisible(false);
			
			if (activePlayer.isComputer()) {
				LinkedList<ItemCard> carriedItems = new LinkedList<ItemCard>();
				carriedItems.addAll(activePlayer.getCarriedItems());
				for (ItemCard item : carriedItems) {
					try {
						CardPlayManager.playCard(activePlayer, item);
					} catch (EndGameException e) {
						endGame();
						return;
					}
				}
				AIManager.playHandCards(activePlayer);
				if (activePlayer.isThief())
					(new StealItemDialog(activePlayer)).setVisible(true);
				(new TradeItemsDialog(this, activePlayer)).setVisible(true);
				(new SellItemsDialog(this, activePlayer)).setVisible(true);
				AIManager.equipBest(activePlayer);
			}
		}
		
		for (PlayerLabel playerLabel : playerLabels)
			playerLabel.updatePlayerInfo();
		validate();
		repaint();
	}
	
	/**
	 * Displays a player's hand for viewing and playing cards.
	 * @param player Player whose hand will be displayed
	 */
	public void displayHand(Player player) {
		// If a battle is in progress add link to battle to determine which cards can or cannot be played
		if (battlePanel != null && battlePanel.getBattle() != null && GM.isBattlePhase()) {
			InHandDialog dialog = new InHandDialog(player, battlePanel.getBattle());
			dialog.setVisible(true);
			battlePanel.updateDisplay();
		}
		else {
			InHandDialog dialog = new InHandDialog(player);
			dialog.setVisible(true);
		}
	}
	
	/**
	 * Displays a player's equipment for viewing, equipping, and unequipping.
	 * @param player Player whose equipment will be displayed
	 */
	public void displayEquipment(Player player) {
		EquipmentDialog dialog = new EquipmentDialog(this, player);
		dialog.setVisible(true);
	}
	
	/**
	 * Displays a player's carried items for viewing and playing.
	 * @param player Player whose carried items will be displayed
	 */
	public void displayCarriedItems(Player player) {
		CarriedItemsDialog dialog = new CarriedItemsDialog(this, player);
		dialog.setVisible(true);
	}
	
	/**
	 * Displays a player's carried battle items for viewing and playing.
	 * @param player Player whose battle items will be displayed
	 */
	public void displayBattleItems(Player player) {
		PlayBattleCardsDialog dialog = new PlayBattleCardsDialog(battlePanel, player);
		dialog.setVisible(true);
		try {
			dialog.playedCard();
		}
		catch (EndGameException ex) {
			endGame();
		}		
	}
	
	/**
	 * Displays a player's in hand and carried items and allows them to be sold for levels.
	 * @param player Player's whose items will be displayed for selling
	 */
	public void displaySellItems(Player player) {
		SellItemsDialog dialog = new SellItemsDialog(this, player);
		dialog.setVisible(true);
	}
	
	/**
	 * Displays a dialog for two players to trade carried items.
	 * @param player Player who is requesting a trade
	 */
	public void displayTradeItems(Player player) {
		TradeItemsDialog dialog = new TradeItemsDialog(this, player);
		dialog.setVisible(true);
	}
		
	/** 
	 * Displays a dialog to discard a player's race.
	 * @param player Player whose Race Cards will be displayed for discarding
	 */
	public void displayDiscardRace(Player player) {
		// Display Race Cards, if any, and warn about restrictions
		LinkedList<Card> raceCards = new LinkedList<Card>();
		raceCards.addAll(player.getRaceCards());
		if (raceCards.size() == 1 && player.isHalfBreed())
			Messenger.warn("You will lose your Half-Breed card if you discard your Race!", "Discard Race", this);
		
		String message = "";
		if (player.isDwarf()) {
			int bigItemExcess = player.getBigItems().size() - 1;
			if (player.hasHireling())
				bigItemExcess--;
			if (player.getCheatingItemCard() != null && player.getCheatingItemCard().isBig())
				bigItemExcess--;
			if (bigItemExcess > 0)
				message = " (WARNING: You will lose " + bigItemExcess + " Big Items if you discard your Dwarf card!)";
		}
		
		LoseCardsDialog dialog = new LoseCardsDialog(player, raceCards, 1, "Race Card" + message);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
	
	/** 
	 * Displays a dialog to discard a player's class.
	 * @param player Player whose Class Cards will be displayed for discarding
	 */
	public void displayDiscardClass(Player player) {
		// Display Race Cards, if any, and warn about restrictions
		LinkedList<Card> classCards = new LinkedList<Card>();
		classCards.addAll(player.getClassCards());
		if (player.isSuperMunchkin()) {
			String message = "You will lose your Super Munchkin card if you discard either Class!";
			Messenger.warn(message, "Discard Class", this);
		}
		
		// If items will be lost by removing a Class, warn the player first and allow them to back out
		if (player.isDwarf()) {
			int bigItemExcess = player.getBigItems().size() - 1;
			if (player.hasHireling())
				bigItemExcess--;
			if (player.getCheatingItemCard() != null && player.getCheatingItemCard().isBig())
				bigItemExcess--;
			if (bigItemExcess > 0) {
				String message = "You will lose " + bigItemExcess + " Big Items if you discard your Dwarf card!";
				Messenger.warn(message, "Discard Class", this);
			}
		}
		
		LoseCardsDialog dialog = new LoseCardsDialog(player, classCards, 1, "Class Card");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
	
	/**
	 * Displays a dialog that allows a Cleric to use his Turning ability.
	 * @param player Player who can use Turing ability
	 */
	public void displayTurningAbility(Player player) {
		// Lets a player choose a Card to discard to use ability
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(player.getHandCards());
		LoseCardsDialog dialog = new LoseCardsDialog(player, cards, 1, "card");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.madeDiscard()) {
			battlePanel.getBattle().addTurning(player);
			battlePanel.updateDisplay();
		}
	}

	/**
	 * Displays a dialog that allows a Wizard to Charm a monster.
	 * @param player Player who can Charm a monster
	 */
	public void displayCharmMonster(Player player) {
		Battle battle = battlePanel.getBattle();
		
		String prompt = "You will have to discard all " + player.getHandCards().size() + " cards in your hand. Are you sure you want to continue?";
		int result = JOptionPane.showConfirmDialog(null, prompt, "Charm Monster", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (result != JOptionPane.YES_OPTION)
			return;
		
		// Lets a player choose a monster to charm
		String message = "Choose a monster to charm.";
		MonsterCard monster = null;
		if (battle.getMonsterCount() == 1)
			monster = battle.getMonster(0);
		else {
			ChooseMonsterDialog dialog = new ChooseMonsterDialog(player, battle, message);
			dialog.setVisible(true);
			monster = dialog.getSelectedMonster();
		}
		// If monster has mate, only mate will be charmed; prompt user if this is what they want to do or not
		if (monster != null) {
			for (DoorCard enhancerCard : battle.getMonsterEnhancers(monster)) {
				if (enhancerCard.getID() == Card.OD_MATE) {
					result = JOptionPane.showConfirmDialog(null, "This will only charm the monster's mate. Are you sure you want to continue?", "Charm Monster", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.YES_OPTION)
						break;
					else
						return;
				}
			}
			
			LinkedList<Card> handCards = new LinkedList<Card>();
			handCards.addAll(player.getHandCards());
			Iterator<Card> cardIter = handCards.iterator();
			while (cardIter.hasNext())
				player.discard(cardIter.next());
			
			battle.discardMonster(monster, false);
			battlePanel.updateDisplay();
		}
	}

	/**
	 * Displays a dialog that allows a Warrior to use his Berserking ability.
	 * @param player Player who can use Berserking ability
	 */
	public void displayBerserkingAbility(Player player) {
		// Lets a player choose a Card to discard to use ability
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(player.getHandCards());
		LoseCardsDialog dialog = new LoseCardsDialog(player, cards, 1, "card");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.madeDiscard()) {
			battlePanel.getBattle().addBerserking(player);
			battlePanel.updateDisplay();
		}
	}

	/** 
	 * Displays a dialog that allows a Thief to Backstab a player.
	 * @param player Player who can Backstab
	 */
	public void displayBackstabPlayer(Player player) {
		Battle battle = battlePanel.getBattle();
		
		// Loads a list of Players that the player can Backstab
		LinkedList<Player> players = new LinkedList<Player>();
		if (battle.canBackstab(player, battle.activePlayer))
			players.add(battle.activePlayer);
		else if (battle.canBackstab(player, battle.helper))
			players.add(battle.helper);
		
		// If the list of Players is empty, inform player and end
		if (players.isEmpty()) {
			Messenger.display("There's no one left to backstab.", "Backstabbing", this);
			return;
		}
		
		// choose without dialog
		String message = player.getName() + ", choose a player to backstab.";
		ChoosePlayerDialog playerDialog = new ChoosePlayerDialog(players, message);
		playerDialog.setVisible(true);
		Player victim = playerDialog.getSelectedPlayer();
		if (victim != null) {
			LinkedList<Card> cards = new LinkedList<Card>();
			cards.addAll(player.getHandCards());
			LoseCardsDialog loseCardDialog = new LoseCardsDialog(player, cards, 1, "card");
			loseCardDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			loseCardDialog.setVisible(true);
			if (loseCardDialog.madeDiscard()) {
				battle.backstab(player, victim);
				battlePanel.updateDisplay();
			}
		}
	}
	
	/**
	 * Displays a dialog that allows a Thief to try to steal an item.
	 * @param player Player who can steal
	 */
	public void displayStealItems(Player player) {
		// Lets the player choose the Player to steal from and Item he will steal
		StealItemDialog stealDialog = new StealItemDialog(player);
		stealDialog.setVisible(true);
		Player victim = stealDialog.getVictim();
		TreasureCard stealItem = stealDialog.getItemToSteal();
		
		// If the player chose a victim and item to steal, let him roll dice to see if he is successful
		if (victim != null && stealItem != null) {
			LinkedList<Card> cards = new LinkedList<Card>();
			cards.addAll(player.getHandCards());
			LoseCardsDialog loseCardDialog = new LoseCardsDialog(player, cards, 1, "card");
			loseCardDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			loseCardDialog.setVisible(true);
			
			// On a roll of 4 or more the player gets the item; otherwise he loses a level
			if (loseCardDialog.madeDiscard()) {
				int roll = Randomizer.rollDice(player);
				if (roll >= 4) {
					Messenger.display(player + " successfully stole the " + stealItem + " from " + victim, "Theft", this);
					if (!victim.removeEquipmentItem(stealItem))
						victim.getCarriedItems().remove(stealItem);
					if (stealItem == victim.getCheatingItemCard())
						victim.removeCheat();
					player.addItem(stealItem);
				}
				else {
					int oldLevel = player.getLevel();
					player.goDownLevel();
					Messenger.display(player + " failed to steal and went from level " + oldLevel + " to level " + player.getLevel() + ".", "Theft", this);
				}
			}
		}
	}
	
	/**
	 * Loads and displays a new battle.
	 * @param battle new battle to display
	 */
	public void beginBattle(Battle battle) {
		battlePanel = new BattlePanel(this, battle);		
		if (GM.isBattlePhase()) {
			hideMainDisplay();
			JScrollPane battleScrollPane = new JScrollPane(battlePanel);
			getContentPane().add(battleScrollPane);
			validate();
			repaint();
			
			battlePanel.updateDisplay();
		}
		
		updatePlayerMenus();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// Update Player Menus, Door Deck, or Treasure Deck based on what changed
		if (o instanceof Player)
			updatePlayerMenu((Player)o);
		else if (o instanceof DoorDeck) {
			if (doorDiscardImageLabel == null)
				return;
			
			if (arg == null || !(arg instanceof Card))
				doorDiscardImageLabel.setIcon(null);
			else
				doorDiscardImageLabel.setIcon(((Card)arg).getPicture());
		}
		else if (o instanceof TreasureDeck) {
			if (treasureDiscardImageLabel == null)
				return;
			
			if (arg == null  || !(arg instanceof Card))
				treasureDiscardImageLabel.setIcon(null);
			else
				treasureDiscardImageLabel.setIcon(((Card)arg).getPicture());
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Updates the Player Menu for the given Player.
	 * @param player Player whose Player Menu will be updated
	 */
	public void updatePlayerMenu(Player player) {
		// Update Player Menu including battle info if applicable
		Iterator<PlayerMenu> menuIter = playerMenus.iterator();
		while (menuIter.hasNext()) {
			PlayerMenu menu = menuIter.next();
			if (menu.getPlayer() == player) {
				Battle battle = null;
				if (battlePanel != null)
					battle = battlePanel.getBattle();
				menu.updateMenuItems(battle);
				break;
			}
		}
		
		// Don't need to update the displays or labels if the AI is currently thinking
		if (GM.isAIThinking())
			return;
		
		if (!GM.isBattlePhase()) {
			if (playerLabels != null) {
				for (PlayerLabel playerLabel : playerLabels) {
					if (playerLabel != null && playerLabel.getPlayer() == player) {
						playerLabel.updatePlayerInfo();
						break;
					}
				}
			}
		}
		else if (battlePanel != null && GM.isBattlePhase()) {
			Battle battle = battlePanel.getBattle();
			if (battle != null && (player == battle.activePlayer || player == battle.helper))
				battlePanel.updateDisplay();
		}
	}
	
	/**
	 * Updates all Player Menus.
	 */
	public void updatePlayerMenus() {
		// Update Player Menu for each player including battle info if applicable
		Iterator<PlayerMenu> menuIter = playerMenus.iterator();
		while (menuIter.hasNext()) {
			Battle battle = null;
			if (battlePanel != null)
				battle = battlePanel.getBattle();
			menuIter.next().updateMenuItems(battle);
		}
		
		// Don't need to update the displays or labels if the AI is currently thinking
		if (GM.isAIThinking())
			return;
		
		if (!GM.isBattlePhase())
			for (PlayerLabel playerLabel : playerLabels)
			playerLabel.updatePlayerInfo();
	}
	
	/**
	 * Closes the temporary log file and appends its contents to the end of the main log file.
	 */
	private void cleanUpLog() {
		if (logStream != null) {
			File currentfile = new File(logfilename_bak);
						
			System.out.flush();
			System.err.flush();
			
			Calendar cal = Calendar.getInstance();
			System.out.println("Ended program at " + cal.getTime());
			System.out.flush();
			logStream.close();
			
			try {				
				File logfile = new File(logfilename);
				InputStream in = new FileInputStream(currentfile);
				OutputStream out = new FileOutputStream(logfile, true);
	
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0){
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				
				currentfile.delete();
			}
			catch (Exception ex) {
				Messenger.error("Unable to update log file: " + ex.getMessage() + "\nError messages were not recorded.", "System Log Error");
			}
		}
	}
	
	/**
	 * Ends the game, then prompts the user to either start a new game or exit the program.
	 */
	public void endGame() {		
		String message = "Would you like to start a new game?";
		int result = JOptionPane.showConfirmDialog(this, message, "Game Over", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			isGameStarted = false;
			newGame();
		}
		else {
			if (GM.getPlayers() != null && !GM.getPlayers().isEmpty())
				updateConfigFile();
			
			this.dispose();
			cleanUpLog();
			System.exit(0);
		}			
	}
	
	/**
	 * Main class that is first called and starts running the GUI and thereby the program.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		// Print all output to file.
		if (!isTestRun) {
			try {
				logStream = new PrintStream(new FileOutputStream(logfilename_bak));
				System.setOut(logStream);
				System.setErr(logStream);
				System.out.println();
				System.out.println("Started program at " + Calendar.getInstance().getTime().toString());
				System.out.flush();
			} catch (FileNotFoundException e) {
				Messenger.error("Unable to open error log file: " + logfilename_bak + "\nError messages will not be recorded.", "System Log Error");
			}
		}
		
		// Run the GUI in a thread safe environment
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						new GUI();
					}
				}
		);
	}
}
