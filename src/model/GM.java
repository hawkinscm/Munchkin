
package model;

import exceptions.EndGameException;
import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;
import gui.CharityDialog;
import gui.CurseDialog;
import gui.DisplayCardsDialog;
import gui.GUI;
import gui.InHandDialog;
import gui.ResurrectionDialog;
import gui.components.Messenger;

import java.util.Iterator;
import java.util.LinkedList;

import model.card.Card;
import model.card.CurseCard;
import model.card.DoorCard;
import model.card.MonsterCard;
import model.card.OtherDoorCard;
import model.card.TreasureCard;

import ai.AIManager;

/**
 * A Singleton class that provides methods for primary game handling.
 */
public class GM {
	
	/**
	 * Enumerator for each stage of a player's turn.
	 */
	private enum TurnPhase {
		OPEN_A_DOOR,
		LOOK_FOR_TROUBLE,
		BATTLE,
		LOOT_THE_ROOM,
		CHARITY,
		OTHER;
	}
	
	// reference to main controlling GUI
	private static GUI gui;

	// the door and treasure decks
	private static DoorDeck doorDeck;
	private static TreasureDeck treasureDeck;
	
	// list of all players in the game
	private static LinkedList<Player> players;
	// the player whose turn it currently is
	private static Player activePlayer;
	
	// information/flags on the current turn/player situation
	private static TurnPhase turnPhase;
	private static boolean isAfterBattle;
	private static boolean canLootRoom;
	private static boolean usedDoubleSell;
	private static boolean startedWithDivineIntervention;
	private static boolean isAIThinking;
	
	/**
	 * Handles the Give To Charity phase of the game.
	 */
	private static void giveToCharity() {
		// determine how many cards must be given to charity
		int numHandCards = activePlayer.getHandCards().size();		
		if (numHandCards > 5 || (activePlayer.isDwarf() && numHandCards > 6)) {
			CharityDialog dialog = new CharityDialog(activePlayer);
			dialog.setVisible(true);
			
			LinkedList<Card> charityCards = dialog.getCharityCards();
			if (charityCards.isEmpty())
				return;
			
			// gets a list of the lowest level players
			LinkedList<Player> lowestLevelPlayers = getLowestLevelPlayers();
			// if the current player is the (or tied with the) lowest level player(s), discard all cards
			if (lowestLevelPlayers.contains(activePlayer)) {
				Iterator<Card> cardIter = charityCards.iterator();
				while (cardIter.hasNext()) {
					Card card = cardIter.next();
					if (card instanceof DoorCard)
						doorDeck.discard((DoorCard)card);
					else
						treasureDeck.discard((TreasureCard)card);
				}
				
				return;
			}
	
			// Randomize the order of which lowest level players receive charity cards
			LinkedList<Player> randomPlayers = new LinkedList<Player>();
			while (!lowestLevelPlayers.isEmpty()) {
				int randomIndex = Randomizer.getRandom(lowestLevelPlayers.size());
				randomPlayers.add(lowestLevelPlayers.remove(randomIndex));
			}
			
			// Pass out charity cards to the lowest level players
			Iterator<Card> charityCardIter = charityCards.iterator();
			int playerIndex = 0;
			while (charityCardIter.hasNext()) {
				Card charityCard = charityCardIter.next();
				Player receiver = randomPlayers.get(playerIndex);
				receiver.addCard(charityCard);
				
				if (receiver.isComputer())
					AIManager.playHandCard(receiver, charityCard);
				else
					(new InHandDialog(receiver, charityCard)).setVisible(true);
				
				playerIndex++;
				if (playerIndex >= randomPlayers.size())
					playerIndex = 0;
			}
		}		
	}
	
	/**
	 * Starts a new game with the given players.
	 * @param g reference to the main controlling GUI
	 * @param playerList list of players who will play the new game.
	 */
	public static void newGame(GUI g, LinkedList<Player> playerList) {
		gui = g;
		
		doorDeck = new DoorDeck(gui);
		treasureDeck = new TreasureDeck(gui);
		
		// set starting information/flag game variables
		turnPhase = TurnPhase.OPEN_A_DOOR;
		isAfterBattle = false;
		canLootRoom = false;
		usedDoubleSell = false;
		startedWithDivineIntervention = false;
		isAIThinking = false;
		
		// pass out starting cards to all players and handle any special cards
		players = playerList;
		Iterator<Player> playerIter = players.iterator();
		while(playerIter.hasNext()) {
			Player player = playerIter.next();
			
			// Draw 2 door cards
			for (int count = 1; count <= 2; count++) {
				try {
					player.addCard(doorDeck.drawCard());
				}
				catch (PlayImmediatelyException ex) { 
					startedWithDivineIntervention = true;
					String message = "All players who become Clerics before the first player's turn will gain 1 level.";
					Messenger.display(message, "Divine Intervention");
				}
				catch (EndGameException ex) {}
			}
			
			// Draw 2 treasure cards
			for (int count = 1; count <= 2; count++) {
				try {
					player.addCard(treasureDeck.drawCard());
				}
				catch (PlayImmediatelyException ex) {
					Iterator<TreasureCard> treasureIter = treasureDeck.getHoardCards(player, false).iterator();
					while (treasureIter.hasNext())
						player.addCard(treasureIter.next());
				}
				catch (NoCardsLeftException ex) {}
			}
		}
		
		// set the player who will go first
		activePlayer = players.getFirst();
	}
	
	/**
	 * Starts a new game used in unit testing.
	 * @param g reference to the main controlling GUI
	 * @param playerList list of players to test with
	 */
	public static void newTestGame(GUI g, LinkedList<Player> playerList) {
		gui = g;
		
		doorDeck = new DoorDeck(gui);
		treasureDeck = new TreasureDeck(gui);
		
		turnPhase = TurnPhase.OPEN_A_DOOR;
		isAfterBattle = false;
		canLootRoom = false;
		usedDoubleSell = false;
		startedWithDivineIntervention = false;
		
		players = playerList;		
		activePlayer = players.getFirst();
	}
	
	/**
	 * Returns a list of all players in the game.
	 * @return a list of all players in the game
	 */
	public static LinkedList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Returns the player to the left of the given player.
	 * @param player player to check
	 * @return the player to the left of the given player
	 */
	public static Player getPlayerLeft(Player player) {
		int playerIndex = players.indexOf(player) + 1;
		if (playerIndex == players.size())
			playerIndex = 0;
		
		return players.get(playerIndex);
	}
	
	/**
	 * Returns the player to the right of the given player.
	 * @param player player to check
	 * @return the player to the right of the given player
	 */
	public static Player getPlayerRight(Player player) {
		int playerIndex = players.indexOf(player) - 1;
		if (playerIndex < 0)
			playerIndex = players.size() - 1;
		
		return players.get(playerIndex);
	}
	
	/**
	 * Returns the level of the player with the lowest level.
	 * @return the lowest level held by any player
	 */
	public static int getLowestLevel() {
		Iterator<Player> playerIter = players.iterator();
		int lowestLevel = playerIter.next().getLevel();
		while (playerIter.hasNext()) {
			int currentLevel = playerIter.next().getLevel();
			if (currentLevel < lowestLevel)
				lowestLevel = currentLevel;
		}
		
		return lowestLevel;
	}
	
	/**
	 * Returns the level of the player with the highest level.
	 * @return the highest level held by any player
	 */
	public static int getHighestLevel() {
		Iterator<Player> playerIter = players.iterator();
		int highestLevel = playerIter.next().getLevel();
		while (playerIter.hasNext()) {
			int currentLevel = playerIter.next().getLevel();
			if (currentLevel > highestLevel)
				highestLevel = currentLevel;
		}
		
		return highestLevel;
	}
	
	/**
	 * Returns a list of all players tied for the lowest level. 
	 * @return a list of all players tied for the lowest level
	 */
	public static LinkedList<Player> getLowestLevelPlayers() {
		LinkedList<Player> lowestLevelPlayers = new LinkedList<Player>();
		
		Iterator<Player> playerIter = players.iterator();
		int lowestLevel = getLowestLevel();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			if (player.getLevel() == lowestLevel) {
				int randomIndex = Randomizer.getRandom(lowestLevelPlayers.size() + 1);
				lowestLevelPlayers.add(randomIndex, player);
			}
		}
		
		return lowestLevelPlayers;
	}
	
	/**
	 * Returns a list of all players tied for the highest level. 
	 * @return a list of all players tied for the highest level
	 */
	public static LinkedList<Player> getHighestLevelPlayers() {
		LinkedList<Player> highestLevelPlayers = new LinkedList<Player>();
		
		Iterator<Player> playerIter = players.iterator();
		int highestLevel = getHighestLevel();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			if (player.getLevel() == highestLevel) {
				int randomIndex = Randomizer.getRandom(highestLevelPlayers.size() + 1);
				highestLevelPlayers.add(randomIndex, player);
			}
		}
		
		return highestLevelPlayers;
	}
	
	/**
	 * Returns a list of all players sorted in order from highest level to lowest level.
	 * For any tied in levels, the order between them is randomized.
	 * @return a list of all players sorted in order from highest level to lowest level
	 */
	public static LinkedList<Player> getHighestToLowestLevelPlayers() {
		LinkedList<Player> sortedPlayers = new LinkedList<Player>();
		
		Iterator<Player> playerIter = players.iterator();
		sortedPlayers.add(playerIter.next());
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			for (int index = 0; index < sortedPlayers.size(); index++) {
				if (player.getLevel() < sortedPlayers.get(index).getLevel()) {
					sortedPlayers.add(index, player);
					break;
				}
				else if (player.getLevel() == sortedPlayers.get(index).getLevel()) {
					sortedPlayers.add(index + Randomizer.getRandom(2), player);
					break;
				}
			}
			if (!sortedPlayers.contains(player))
				sortedPlayers.add(player);
		}
		
		return sortedPlayers;
	}
	
	/**
	 * Returns the player whose turn it currently is.
	 * @return the player whose turn it currently is
	 */
	public static Player getActivePlayer() {
		return activePlayer;
	}
	
	/**
	 * Ends the currently active player's turn.
	 */
	public static void endPlayerTurn() {
		giveToCharity();
		
		isAfterBattle = false;
		usedDoubleSell = false;
		canLootRoom = false;
		
		// Start the next player's turn
		int nextPlayerIndex = players.indexOf(activePlayer) + 1;
		if (nextPlayerIndex >= players.size())
			nextPlayerIndex = 0;
		activePlayer = players.get(nextPlayerIndex);
		
		turnPhase = TurnPhase.OPEN_A_DOOR;
	
		// deal cards to player whose turn it now is if needed
		if (activePlayer.needsNewCards()) {
			String message = activePlayer + " gets 2 Door Cards and 2 Treasures Cards due to a recent death.";
			Messenger.display(message, "Cards After Death", gui);
			
			try {
				activePlayer.addCard(doorDeck.drawCard());
			}
			catch (PlayImmediatelyException ex) {}
			catch (EndGameException ex) {
				gui.endGame();
				return;
			}
			try {
				activePlayer.addCard(doorDeck.drawCard());
			}
			catch (PlayImmediatelyException ex) {}
			catch (EndGameException ex) {
				gui.endGame();
				return;
			}
			
			try {
				activePlayer.addCard(treasureDeck.drawCard());
			}
			catch (PlayImmediatelyException ex) {
				Iterator<TreasureCard> treasureIter = treasureDeck.getHoardCards(activePlayer, false).iterator();
				while (treasureIter.hasNext())
					activePlayer.addCard(treasureIter.next());
			}
			catch (NoCardsLeftException ex) {}
			try {
				activePlayer.addCard(treasureDeck.drawCard());
			}
			catch (PlayImmediatelyException ex) {
				Iterator<TreasureCard> treasureIter = treasureDeck.getHoardCards(activePlayer, false).iterator();
				while (treasureIter.hasNext())
					activePlayer.addCard(treasureIter.next());
			}
			catch (NoCardsLeftException ex) {}
			
			// allow player to view/play new cards
			if (activePlayer.isComputer())
				AIManager.playHandCards(activePlayer);
			else
				gui.displayHand(activePlayer);			
			activePlayer.drewNewCards();
		}
		
		if (GUI.isDebug)
			countCards();
		
		// refresh GUI display
		gui.updatePlayerMenus();
		gui.updateGameDisplay();
	}
	
	/**
	 * Sets the player whose turn it is.
	 * @param player the player to set
	 */
	public static void setActivePlayer(Player player) {
		activePlayer = player;
	}
	
	/**
	 * Returns the Door Deck stack of cards.
	 * @return the Door Deck
	 */
	public static DoorDeck getDoorDeck() {
		return doorDeck;
	}
	
	/**
	 * Returns the Treasure Deck stack of cards.
	 * @return the Treasure Deck
	 */
	public static TreasureDeck getTreasureDeck() {
		return treasureDeck;
	}
	
	/**
	 * Returns whether or not the player's current turn phase is after a battle.
	 * @return true if the player's turn has already had a battle; false otherwise
	 */
	public static boolean isAfterBattle() {
		return isAfterBattle;
	}
	
	/**
	 * Returns whether or not the player has sold an item for double its value this turn or not.
	 * @return true, if the player has sold an item for double its value this turn; false otherwise
	 */
	public static boolean usedDoubleSell() {
		return usedDoubleSell;
	}
	
	/**
	 * Sets a flag signaling that an item was sold for double this turn.
	 */
	public static void sellingItemForDouble() {
		usedDoubleSell = true;
	}
	
	/**
	 * Sets a flag signaling that the player whose turn it is, can loot the room.
	 */
	public static void setCanLootRoom() {
		canLootRoom = true;
	}
	
	/**
	 * Returns whether or not the game was started with the Divine Intervention Card being dealt.
	 * @return true if the game was started with the Divine Intervention Card being dealt; false otherwise
	 */
	public static boolean startedWithDivineIntervention() {
		return startedWithDivineIntervention;
	}
	
	/**
	 * Handles anything special that needs to be done as the first player's turn begins. 
	 */
	public static void startFirstPlayersTurn() {
		startedWithDivineIntervention = false;
	}
	
	/**
	 * Returns whether or not it is currently the Open A Door phase of the player's turn.
	 * @return true if it is currently the Open A Door phase of the player's turn; false otherwise
	 */
	public static boolean isOpenDoorPhase() {
		return (turnPhase == TurnPhase.OPEN_A_DOOR);
	}
	
	/**
	 * Returns whether or not it is currently the Battle phase of the player's turn.
	 * @return true if it is currently the Battle phase of the player's turn; false otherwise
	 */
	public static boolean isBattlePhase() {
		return (turnPhase == TurnPhase.BATTLE);
	}
	
	/**
	 * Returns whether or not it is currently the Look For Trouble phase of the player's turn.
	 * @return true if it is currently the Look For Trouble phase of the player's turn; false otherwise
	 */
	public static boolean isLookForTroublePhase() {
		return (turnPhase == TurnPhase.LOOK_FOR_TROUBLE);
	}
	
	/**
	 * Returns whether or not it is currently the Loot The Room phase of the player's turn.
	 * @return true if it is currently the Loot The Room phase of the player's turn; false otherwise
	 */
	public static boolean isLootRoomPhase() {
		return (turnPhase == TurnPhase.LOOT_THE_ROOM);
	}
	
	/**
	 * Returns whether or not it is currently the Charity phase of the player's turn.
	 * @return true if it is currently the Charity phase of the player's turn; false otherwise
	 */
	public static boolean isCharityPhase() {
		return (turnPhase == TurnPhase.CHARITY);
	}
	
	/**
	 * Move from the current phase to the next phase of the player's turn.
	 */
	public static void moveNextPhase() {
		if (turnPhase == TurnPhase.OPEN_A_DOOR)
			turnPhase = TurnPhase.LOOK_FOR_TROUBLE;
		else if (turnPhase == TurnPhase.LOOK_FOR_TROUBLE)
			turnPhase = TurnPhase.LOOT_THE_ROOM;
		else if (turnPhase == TurnPhase.LOOT_THE_ROOM)
			turnPhase = TurnPhase.CHARITY;
		else if (turnPhase == TurnPhase.BATTLE) {
			if (canLootRoom)
				turnPhase = TurnPhase.LOOT_THE_ROOM;
			else
				turnPhase = TurnPhase.CHARITY;
			
			isAfterBattle = true;
			gui.updatePlayerMenus();
			
			try {
				for (Player currentPlayer : players)
					if (currentPlayer.isComputer() && currentPlayer.getLevel() < 9) {
						LinkedList<Card> handCards = new LinkedList<Card>();
						handCards.addAll(currentPlayer.getHandCards());
						for (Card handCard : handCards)
							if (handCard.getID() == Card.GUL_MOW_THE_BATTLEFIELD) {
								if (CardPlayManager.playCard(currentPlayer, handCard))
									Messenger.display(currentPlayer + " played the " + handCard + " card and went up a level.", "Hand Card Played");
							}
					}
			}
			catch (EndGameException ex) {}							
		}
		
		gui.updateGameDisplay();
	}
	
	/**
	 * Move to a temporary, miscellaneous phase for special game handling.
	 */
	public static void moveToOtherPhase() {
		turnPhase = TurnPhase.OTHER;
		gui.updatePlayerMenus();
	}
	
	/**
	 * Move to the battle phase of the player's turn.
	 */
	public static void moveToBattlePhase() {
		turnPhase = TurnPhase.BATTLE;
		gui.updatePlayerMenus();
	}
	
	/**
	 * Returns whether or not the AI is currently thinking and player menus should be disabled.
	 * @return true if the AI is currently thinking; false otherwise
	 */
	public static boolean isAIThinking() {
		return isAIThinking;
	}
	
	/**
	 * Sets whether or not the AI is currently thinking.
	 * @param isThinking value to set; when true player menus will be disabled; when false player menus will be enabled
	 */
	public static void setAIThinking(boolean isThinking) {
		isAIThinking = isThinking;
		gui.updatePlayerMenus();
	}
	
	/**
	 * Process the Open a Door phase for the current player.
	 */
	public static void openDoor() {
		// Draw a card (or allow Resurrection ability if possible) and handle drawn card effects
		try {
			DoorCard card = null;
			if (activePlayer.canResurrect() && !doorDeck.getDiscardPile().isEmpty()) {
				ResurrectionDialog dialog = new ResurrectionDialog(activePlayer, doorDeck.getDiscardPile().peek());
				dialog.setVisible(true);
				Card cardToDiscard = dialog.getCardToDiscard();
				if (cardToDiscard != null) {
					card = doorDeck.takeDiscard();
					activePlayer.discard(cardToDiscard);
				}
			}
			
			if (card == null)
				card = doorDeck.drawCard();
			
			if (card instanceof MonsterCard) {
				(new DisplayCardsDialog(card, "Open Door")).setVisible(true);
				
				turnPhase = TurnPhase.BATTLE;
				gui.beginBattle(new Battle(activePlayer, (MonsterCard)card));
				return;
			}
			else if (card instanceof CurseCard) {
				if (activePlayer.isComputer())
					(new DisplayCardsDialog(card, "Open Door")).setVisible(true);
				(new CurseDialog(activePlayer, (CurseCard)card, true)).setVisible(true);
			}
			else {
				activePlayer.addCard(card);
				if (activePlayer.isComputer()) {
					(new DisplayCardsDialog(card, "Open Door")).setVisible(true);
					AIManager.playHandCard(activePlayer, card);
				}
				else
					(new InHandDialog(activePlayer, card)).setVisible(true);
			}
		}
		catch (PlayImmediatelyException ex) {}
		catch (EndGameException ex) {
			gui.endGame();
			return;
		}
		
		moveNextPhase();
	}
	
	/**
	 * Checks to see if anyone has won the game by reaching level 10.
	 * @throws EndGameException thrown when someone has won the game
	 */
	public static void checkForWinners() throws EndGameException {
		String message = "";
		
		Iterator<Player> playerIter = players.iterator();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			if (player.getLevel() >= 10) {
				message += player.getName() + " is at level " + player.getLevel() + ". ";
				if (player.isMale())
					message += "He ";
				else
					message += "She ";
				
				if (!player.isComputer() && player.hasChangeSexCurse())
					message += "(snicker) ";
					
				message += "wins!\n";
			}
		}

		// Displays a winning message and signals the end of the game through an exception if someone was found >= level 10
		if (!message.equals("")) {
			Messenger.display(message, "Game Over", gui);
			throw new EndGameException();
		}
	}
	
	/**
	 * Signals that the game is over.
	 */
	public static void endGame() {
		gui.endGame();
	}
	
	/**
	 * Returns whether or not this is a test run.
	 * @return true if this is a test run; false if it is a real game
	 */
	public static boolean isTestRun() {
		return GUI.isTestRun;
	}
	
	/**
	 * Used for debugging purposes to make sure that all cards are accounted for at the end of every turn and 
	 * that there aren't any extras.
	 */
	private static void countCards() {
		LinkedList<Card> expectedCards = new LinkedList<Card>();
		expectedCards.addAll(DoorDeckFactory.buildDeck());
		expectedCards.addAll(TreasureDeckFactory.buildDeck());
		
		LinkedList<Card> foundCards = new LinkedList<Card>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean add(Card e) {
				if (e != null)
					return super.add(e);
				
				return false;
			}
		};
		foundCards.addAll(doorDeck.drawPile);
		foundCards.addAll(doorDeck.discardPile);
		foundCards.addAll(treasureDeck.drawPile);
		foundCards.addAll(treasureDeck.discardPile);
		for (Player player : players) {
			foundCards.addAll(player.getHandCards());
			foundCards.addAll(player.getCarriedItems());
			foundCards.addAll(player.getEquippedItems());
			foundCards.addAll(player.getUnequippedItems());
			foundCards.add(player.getChickenOnHeadCard());
			foundCards.add(player.getMalignMirrorCard());
			foundCards.add(player.getChangeSexCard());
			foundCards.addAll(player.getRaceCards());
			foundCards.add(player.getHalfBreedCard());
			foundCards.addAll(player.getClassCards());
			foundCards.add(player.getSuperMunchkinCard());
			if (player.getCheatingItemCard() != null)
				foundCards.add(new OtherDoorCard(Card.OD_CHEAT, "Cheat!"));
			foundCards.add(player.getHirelingCard());
		}
		
		if (expectedCards.size() != foundCards.size())
			System.err.println("\nError! Expected " + expectedCards.size() + " cards, but found " + foundCards.size() + " cards.");
		
		for (Card foundCard : foundCards) {
			if (!expectedCards.remove(foundCard))
				System.err.println("Error! Duplicate or unknown card found: \"" + foundCard + "\", ID" + foundCard.getID());
		}
		
		for (Card expectedCard : expectedCards)
			System.err.println("Error! Missing card: \"" + expectedCard + "\", ID" + expectedCard.getID());
	}
}
