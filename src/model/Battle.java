
package model;

import exceptions.EndGameException;
import gui.DisplayCardsDialog;
import gui.LoseCardsDialog;
import gui.components.Messenger;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import model.card.Card;
import model.card.DoorCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.TreasureCard;

import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

/**
 * Class the handles the events of a battle.
 */
public class Battle {

	/**
	 * Simple structure that represents a monster.
	 */
	private class Monster {
		/**
		 * The Card for the monster.
		 */
		public MonsterCard card;
		/**
		 * A list of all Enhancer Cards played on the monster.
		 */
		public LinkedList<DoorCard> enhancerCards;
		
		/**
		 * Creates a new Monster object.
		 * @param c the Monster Card for this monster
		 */
		public Monster(MonsterCard c) {
			card = c;
			enhancerCards = new LinkedList<DoorCard>();
		}
	}
	
	/**
	 * The main player in the battle.
	 */
	public Player activePlayer;
	/**
	 * The player helping the main/active player in the battle.
	 */
	public Player helper;
	
	// Which players have refused to help in this battle.
	private LinkedList<Player> helpRefusers;
	
	// Item cards played for the benefit of the player(s).
	private LinkedList<ItemCard> playerItemCards;
	
	// Monsters currently in the battle.
	private LinkedList<Monster> monsters;
	// Item cards played for the benefit of the monster(s).
	private LinkedList<ItemCard> monsterItemCards;
	
	// The number treasures that have been won at this point in the battle 
	private int treasuresWon;
	
	// List containing the order of how multiple players will take the won treasures
	private LinkedList<Player> takeTreasurePlayerOrder;
	
	// Whether or not Turning can be used and how many times it has been used by the active player and helper
	private boolean allowTurning;
	private int activePlayerTurningCount;
	private int helperTurningCount;
	
	// How many times Berserking has been used by the active player and helper
	private int activePlayerBerserkingCount;
	private int helperBerserkingCount;
	
	// Which players have backstabbed the active player and the helper.
	private LinkedList<Player> activePlayerBackstabbers;
	private LinkedList<Player> helperBackstabbers;
		
	/**
	 * Creates a new Battle object.
	 * @param player The main player in this battle
	 * @param monster The starting monster in this battle
	 */
	public Battle(Player player, MonsterCard monster) {
		activePlayer = player;		
		helper = null;
		
		helpRefusers = new LinkedList<Player>();
		
		playerItemCards = new LinkedList<ItemCard>();
		
		monsters = new LinkedList<Monster>();
		monsterItemCards = new LinkedList<ItemCard>();
		
		treasuresWon = 0;
		
		allowTurning = false;
		activePlayerTurningCount = 0;
		helperTurningCount = 0;
		
		activePlayerBerserkingCount = 0;
		helperBerserkingCount = 0;
		
		activePlayerBackstabbers = new LinkedList<Player>();
		helperBackstabbers = new LinkedList<Player>();
		
		if (monster != null)
			addMonster(monster);
	}
	
	/**
	 * Replaces the main player in this battle with another player.
	 * @param replacement the player who will replace the main player
	 */
	public void replaceActivePlayer(Player replacement) {
		activePlayer = replacement;
		activePlayerTurningCount = 0;
		activePlayerBerserkingCount = 0;
		activePlayerBackstabbers.clear();
		helpRefusers.clear();
	}
	
	/**
	 * Returns whether or not a helper is currently helping in the battle.
	 * @return true if there is a helper currently in the battle; false otherwise
	 */
	public boolean isHelper() {
		return helper != null;
	}
	
	/**
	 * Returns whether or not a helper can be added to the battle.
	 * @return true if a helper can be added to the battle; false otherwise
	 */
	public boolean canAddHelper() {
		// a helper can't join if there is already a helper
		if (isHelper())
			return false;
		
		// a helper can't join in the Doppleganger Card is in play
		Iterator<ItemCard> playerItemIter = playerItemCards.iterator();
		while (playerItemIter.hasNext())
			if (playerItemIter.next().getID() == Card.I_DOPPLEGANGER)
				return false;
		
		// a helper can't join if one of the monsters is the Gazebo
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext())
			if (monsterIter.next().card.getID() == Card.M_GAZEBO)
				return false;
			
		return true;
	}
	
	/**
	 * Sets the given player as the helper in the battle.
	 * @param player player who will help in the battle.
	 */
	public void addHelper(Player player) {
		if (player == null)
			return;	
		helper = player;
		
		// if helper joins a battle with the Tongue Demon in play, must discard an item
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			if (monsterIter.next().card.getID() == Card.M_TONGUE_DEMON) {
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(helper.getAllItems());
				
				LoseCardsDialog dialog = new LoseCardsDialog(helper, cards, 1, "item");
				dialog.setTitle("Lose an item to the Tongue Demon");
				dialog.refresh();
				dialog.setVisible(true);
			}
		}
	}
	
	/**
	 * Removes the helper from the battle.
	 */
	public void removeHelper() {
		helper = null;
		helperTurningCount = 0;
		helperBerserkingCount = 0;
		helperBackstabbers.clear();
		takeTreasurePlayerOrder = null;
	}
	
	/**
	 * Adds a player to the list of players who have refused to help in this battle.
	 * @param refuser player who has just refused to help in this battle.
	 */
	public void addHelpRefuser(Player refuser) {
		helpRefusers.add(refuser);
	}
	
	/**
	 * Returns whether or not the given player has already refused to help in the battle or not.
	 * @param player player to check
	 * @return true if the player has already refused to help in this battle; false otherwise
	 */
	public boolean hasRefusedToHelp(Player player) {
		return helpRefusers.contains(player);
	}
	
	/**
	 * Returns a list containing the order of how multiple players will take the won treasures.
	 * @return list containing the order of how multiple players will take the won treasures; null if no helper or computer player was not involved in the battle
	 */
	public LinkedList<Player> getTakeTreasurePlayerOrder() {
		if (helper == null)
			return null;
		else
			return takeTreasurePlayerOrder;
	}
	
	/**
	 * Returns a list containing the order of how multiple players will take the won treasures.
	 * @return list containing the order of how multiple players will take the won treasures; null if no helper or computer player was not involved in the battle
	 */
	public void  setTakeTreasurePlayerOrder(LinkedList<Player> playerOrder) {
		takeTreasurePlayerOrder = playerOrder;
	}
	
	/**
	 * Returns whether or not all players have refused to help in this battle or not.
	 * @return true if EVERY player has refused to help in this battle; false otherwise
	 */
	public boolean haveAllPlayersRefusedToHelp() {
		return (helpRefusers.size() >= GM.getPlayers().size() - 1);
	}
		
	/**
	 * Cleans up the cards used in the battle and moves to the next game phase.
	 */
	public void endBattle() {
		GM.moveToOtherPhase();
		
		Iterator<ItemCard> itemIter = playerItemCards.iterator();
		while (itemIter.hasNext())
			GM.getTreasureDeck().discard(itemIter.next());
		
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			MonsterCard card = monster.card;
			GM.getDoorDeck().discard(card);
			Iterator<DoorCard> enhancerIter = monster.enhancerCards.iterator();
			while (enhancerIter.hasNext())
				GM.getDoorDeck().discard(enhancerIter.next());
		}
		
		itemIter = monsterItemCards.iterator();
		while (itemIter.hasNext())
			GM.getTreasureDeck().discard(itemIter.next());
		
		// removes any curses that were for one battle only
		activePlayer.removeLastingCurses();
		if (isHelper())
			helper.removeLastingCurses();
	}
	
	/**
	 * Returns a list of all Item Cards played on the player side.
	 * @return a list of all Item Cards played on the player side
	 */
	public LinkedList<ItemCard> getPlayerItemCards() {
		return playerItemCards;
	}
	
	/**
	 * Adds and processes an Item Card played on the player's side.
	 * @param item the Item Card to be added/played
	 * @throws EndGameException if the played card caused someone to win
	 */
	public void addPlayerItemCard(ItemCard item) throws EndGameException {
		// handle special actions for Potion of Halitosis and Floating Nose, if applicable
		if (item.getID() == Card.I_POTION_OF_HALITOSIS) {
			LinkedList<MonsterCard> monsterCards = new LinkedList<MonsterCard>();
			Iterator<Monster> monsterIter = monsters.iterator();
			while (monsterIter.hasNext())
				monsterCards.add(monsterIter.next().card);
			
			Iterator<MonsterCard> monsterCardIter = monsterCards.iterator();
			while (monsterCardIter.hasNext()) {
				MonsterCard card = monsterCardIter.next();
				if (card.getID() == Card.M_FLOATING_NOSE) {
					String message = "Floating Nose has been killed!";
					Messenger.display(message, "Battle Message");
					
					if (monsters.size() == 1)
						GM.moveToOtherPhase();
					
					activePlayer.goUpLevels(card.getWinLevels(), true);
					if (isHelper() && helper.isElf())
						helper.goUpLevel(true);
						
					Iterator<DoorCard> enhancerIter = getMonsterEnhancers(card).iterator();
					while (enhancerIter.hasNext()) {
						if (enhancerIter.next().getID() == Card.OD_MATE) {
							activePlayer.goUpLevels(card.getWinLevels(), true);
							if (isHelper() && helper.isElf())
								helper.goUpLevel(true);
							
							break;
						}
					}
					
					GM.checkForWinners();
					
					discardMonster(card, true);
					GM.getTreasureDeck().discard(item);
					
					return;
				}
			}
		}
		
		playerItemCards.add(item);
	}
	
	/**
	 * Adds a Turning Ability bonus to the given character. 
	 * @param player player who will get the bonus
	 */
	public void addTurning(Player player) {
		if (activePlayer == player)
			activePlayerTurningCount++;
		else if (helper == player)
			helperTurningCount++;
	}
	
	/**
	 * Returns whether or not the player can use the Turning Ability.
	 * @param player player to check
	 * @return true if the player can use the Turning Ability; false otherwise
	 */
	public boolean canUseTurning(Player player) {
		if (!allowTurning)
			return false;
		
		// to use Turning Ability player must be a Cleric and can only use it 3 times per battle
		if (activePlayer == player)
			return (activePlayer.isCleric() && activePlayerTurningCount < 3);
		else if (helper == player)
			return (helper.isCleric() && helperTurningCount < 3);
		
		return false;
	}
	
	/**
	 * Returns the number of times the player can still use Turning in this battle.
	 * @param player the player to check
	 * @return the number of times the player can still use Turning in this battle
	 */
	public int getNumberOfTurningBonusesLeft(Player player) {
		if (activePlayer == player)
			return 3 - activePlayerTurningCount;
		else if (helper == player)
			return 3 - helperTurningCount;
		else
			return 0;
	}

	/**
	 * Returns whether or not the player can use the Charm Ability.
	 * @param player player to check
	 * @return true if the player can use the Charm Ability; false otherwise
	 */
	public boolean canCastCharm(Player player) {
		// to use Charm Ability player must be a Wizard and have cards in hand
		if (player == activePlayer || player == helper)
			return (player.isWizard() && !player.getHandCards().isEmpty());
		
		return false;
	}
	
	/**
	 * Adds a Berserking Ability bonus to the given character. 
	 * @param player player who will get the bonus
	 */
	public void addBerserking(Player player) {
		if (activePlayer == player)
			activePlayerBerserkingCount++;
		else if (helper == player)
			helperBerserkingCount++;
	}
	
	/**
	 * Returns whether or not the player can use the Berserking Ability.
	 * @param player player to check
	 * @return true if the player can use the Berserking Ability; false otherwise
	 */
	public boolean canUseBerserking(Player player) {
		// to use Turning Ability player must be a Warrior and can only use it 3 times per battle
		if (activePlayer == player)
			return (activePlayer.isWarrior() && activePlayerBerserkingCount < 3);
		else if (helper == player)
			return (helper.isWarrior() && helperBerserkingCount < 3);
		
		return false;
	}
	
	/**
	 * Returns the number of times the player can still use Berserking in this battle.
	 * @param player the player to check
	 * @return the number of times the player can still use Berserking in this battle
	 */
	public int getNumberOfBerserkingBonusesLeft(Player player) {
		if (activePlayer == player)
			return 3 - activePlayerBerserkingCount;
		else if (helper == player)
			return 3 - helperBerserkingCount;
		else
			return 0;
	}
	
	/**
	 * Puts a Backstabbing Ability penalty on one player from another. 
	 * @param player player who is backstabbing
	 * @param victim player who is being backstabbed
	 */
	public void backstab(Player player, Player victim) {
		if (activePlayer == victim)
			activePlayerBackstabbers.add(player);
		else if (helper == victim)
			helperBackstabbers.add(player);
	}
	
	/**
	 * Whether or not a player can backstab another specific player.
	 * @param player player who would backstab
	 * @param victim player who would be backstabbed
	 * @return true if the player can backstab the victim player; false otherwise
	 */
	public boolean canBackstab(Player player, Player victim) {
		// Must be a thief to backstab
		if (!player.isThief())
			return false;
		
		// Can't backstab yourself
		if (player == victim)
			return false;
		
		// Can only backstab once per player per battle
		if (activePlayer == victim)
			return !(activePlayerBackstabbers.contains(player));
		else if (isHelper() && helper == victim)
			return !(helperBackstabbers.contains(player));
		
		return false;
	}
	
	/**
	 * Add a new monster to the battle.
	 * @param monster the Monster Card of the monster to add
	 */
	public void addMonster(MonsterCard monster) {
		monsters.add(new Monster(monster));
		if (monster.isUndead())
			allowTurning = true;
		
		// if monster is Tongue Demon all players in the battle must discard an item
		if (monster.getID() == Card.M_TONGUE_DEMON) {
			LinkedList<Card> cards = new LinkedList<Card>();
			cards.addAll(activePlayer.getAllItems());
			
			LoseCardsDialog dialog = new LoseCardsDialog(activePlayer, cards, 1, "item");
			dialog.setTitle("Lose an item to the Tongue Demon");
			dialog.refresh();
			dialog.setVisible(true);
			
			if (isHelper()) {
				cards = new LinkedList<Card>();
				cards.addAll(helper.getAllItems());
				
				dialog = new LoseCardsDialog(helper, cards, 1, "item");
				dialog.setTitle("Lose an item to the Tongue Demon");
				dialog.refresh();
				dialog.setVisible(true);
			}
		}
		// if monster is Gazebo, any helper in the battle must leave with no penalty
		else if (monster.getID() == Card.M_GAZEBO)
			removeHelper();
	}
	
	/**
	 * Returns whether or not the battle includes the given monster.
	 * @param card Monster Card of the monster to check for
	 * @return true if the battle contains the given monster; false otherwise
	 */
	public boolean hasMonster(MonsterCard card) {
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			if (monster.card == card) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the give monster from the battle.
	 * @param card Monster Card of the monster to remove
	 */
	private void removeMonster(MonsterCard card) {
		allowTurning = false;
		
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			if (monster.card == card)
				monsterIter.remove();
			else if (monster.card.isUndead())
				allowTurning = true;
		}
		
		// Turning is not possible if there are no undead monster's in the battle
		if (!allowTurning) {
			activePlayerTurningCount = 0;
			helperTurningCount = 0;
		}
	}
	
	/**
	 * Replaces a monster in the battle with another.
	 * @param oldCard Monster Card of the monster to replace
	 * @param newCard Monster Card of the new monster
	 */
	public void replaceMonster(MonsterCard oldCard, MonsterCard newCard) {
		GM.getDoorDeck().discard(oldCard);
		Iterator<DoorCard> enhancerIter = getMonsterEnhancers(oldCard).iterator();
		while (enhancerIter.hasNext())
			GM.getDoorDeck().discard(enhancerIter.next());
		
		int oldMonsterIndex = -1;
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			if (monster.card == oldCard) 
				oldMonsterIndex = monsters.indexOf(monster);
		}
		
		addMonster(newCard);
		if (oldMonsterIndex != monsters.size() - 2) {
			Monster monster = monsters.removeLast();
			monsters.add(oldMonsterIndex, monster);
		}		
		removeMonster(oldCard);
	}
	
	/**
	 * Returns the Monster Card of the monster at the given index.
	 * @param monsterIndex index of the monster to return
	 * @return the Monster Card of the monster at the given index
	 */
	public MonsterCard getMonster(int monsterIndex) {
		try {
			return monsters.get(monsterIndex).card;
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Returns the number of monsters in the battle.
	 * @return the number of monsters in the battle
	 */
	public int getMonsterCount() {
		return monsters.size();
	}
	
	/**
	 * Adds the monster enhancer to the given monster.
	 * @param card Monster Card of the monster who will get the enhancement
	 * @param enhancer Enhancer Card of the new ehancement
	 */
	public void addMonsterEnhancer(MonsterCard card, DoorCard enhancer) {
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			if (monster.card == card)
				monster.enhancerCards.add(enhancer);
		}
	}
	
	/**
	 * Returns a list of all monster enhancers on the specified monster.
	 * @param monsterIndex index specifying the monster
	 * @return a list of all monster enhancers on the specified monster
	 */
	public LinkedList<DoorCard> getMonsterEnhancers(int monsterIndex) {
		try {
			return monsters.get(monsterIndex).enhancerCards;
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Returns a list of all monster enhancers on the specified monster.
	 * @param card Monster Card specifying the monster
	 * @return a list of all monster enhancers on the specified monster
	 */
	public LinkedList<DoorCard> getMonsterEnhancers(MonsterCard card) {
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			if (monster.card == card)
				return monster.enhancerCards;
		}
		
		return null;
	}
	
	/**
	 * Returns a list of all Item Cards played on the monster side.
	 * @return a list of all Item Cards played on the monster side
	 */
	public LinkedList<ItemCard> getMonsterItemCards() {
		return monsterItemCards;
	}
	
	/**
	 * Adds an Item Card to the monster side.
	 * @param item Item Card to add
	 */
	public void addMonsterItemCard(ItemCard item) {
		monsterItemCards.add(item);
	}
	
	/**
	 * Calculates and returns the players' combined battle level including item, player, and monster bonuses.
	 * @return players' total battle level
	 */
	public int getPlayersLevel() {
		int playersLevel = 0;
		
		if (activePlayer.hasDistractionCurse() || (isHelper() && helper.hasDistractionCurse()))
			playersLevel -= 5;
				
		boolean canUseItems = true;
		boolean canUseLevel = true;
		
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			MonsterCard monster = monsterIter.next().card;
			if (monster.getID() == Card.M_SQUIDZILLA) {
				if (!activePlayer.isHuman() && activePlayer.isElf())
					playersLevel -= 4;
				if (isHelper() && !helper.isHuman() && helper.isElf())
					playersLevel -= 4;
			}
			else if (monster.getID() == Card.M_GHOULFIENDS)
				canUseItems = false;
			else if (monster.getID() == Card.M_INSURANCE_SALESMAN)
				canUseLevel = false;
		}
		
		if (canUseLevel)
			playersLevel += activePlayer.getLevel();
		if (canUseItems)
			playersLevel += activePlayer.getEquipmentBonus();
		
		if (isHelper()) {
			if (canUseLevel)
				playersLevel += helper.getLevel();
			if (canUseItems)
				playersLevel += helper.getEquipmentBonus();
		}	
		
		if (canUseItems) {
			int itemBonus = 0;
			Iterator<ItemCard> playerItemIter = playerItemCards.iterator();
			while (playerItemIter.hasNext()) {
				ItemCard item = playerItemIter.next();
				itemBonus += item.getBonus();
				
				if (item.getID() == Card.I_DOPPLEGANGER)
					playersLevel *= 2;
				else if (item.getID() == Card.I_YUPPIE_WATER) {
					if (activePlayer.isElf())
						itemBonus += 2;
					if (isHelper() && helper.isElf())
						itemBonus += 2;
				}
			}
			playersLevel += itemBonus;
		}
		
		if (allowTurning) {
			playersLevel += (activePlayerTurningCount * 3);
			playersLevel += (helperTurningCount * 3);
		}
		
		playersLevel += activePlayerBerserkingCount;
		playersLevel += helperBerserkingCount;
		
		playersLevel -= (activePlayerBackstabbers.size() * 2);
		playersLevel -= (helperBackstabbers.size() * 2);
				
		return playersLevel;
	}
	
	/**
	 * Calculates and returns the monsters' combined battle level including item and enhancer bonuses. 
	 * @return monsters' total battle level
	 */
	public int getMonstersLevel() {
		int monstersLevel = 0;
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
						
			int currentLevel = monster.card.getLevel(this);
			
			boolean hasMate = false;
			Iterator<DoorCard> enhancerIter = monster.enhancerCards.iterator();
			while (enhancerIter.hasNext()) {
				DoorCard card = enhancerIter.next();
				if (card.getID() == Card.OD_MATE)
					hasMate = true;
				else
					currentLevel += ((MonsterEnhancerCard)card).getBonus();
			}
			
			if (currentLevel < 1)
				currentLevel = 1;
			if (hasMate)
				currentLevel *= 2;
			
			monstersLevel += currentLevel; 
		}
				
		Iterator<ItemCard> itemIter = monsterItemCards.iterator();
		while (itemIter.hasNext()) {
			monstersLevel += itemIter.next().getBonus();
		}
		
		return monstersLevel;
	}
	
	/**
	 * Returns whether or not a player used a fire-type item in the battle.
	 * @return true if any player used a fire-type item in the battle
	 */
	public boolean usedFire() {
		if (activePlayer.hasEquipped(Card.E_FLAMING_ARMOR) || activePlayer.hasEquipped(Card.E_STAFF_OF_NAPALM))
			return true;
		if (activePlayer.hasEquipped(Card.E_FLAMETHROWER))
			return true;
		
		if (isHelper()) {
			if (helper.hasEquipped(Card.E_FLAMING_ARMOR) || helper.hasEquipped(Card.E_STAFF_OF_NAPALM))
				return true;
			if (helper.hasEquipped(Card.E_FLAMETHROWER))
				return true;
		}
		
		Iterator<ItemCard> playerItemIter = playerItemCards.iterator();
		while (playerItemIter.hasNext()) {
			if (playerItemIter.next().getID() == Card.I_FLAMING_POISON_POTION)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the number of treasures that would be won if the monster is defeated at its current status.
	 * @param monster monster to check
	 * @return the number of treasures that would be won by defeating the monster
	 */
	public int getWinTreasureCount(MonsterCard monster) {
		int treasures = monster.getTreasures();
	
		if (monster.getID() == Card.M_POTTED_PLANT) {
			if (activePlayer.isElf() || (isHelper() && helper.isElf()))
				treasures++;
		}
		
		boolean hasMate = false;
		Iterator<DoorCard> enhancerIter = getMonsterEnhancers(monster).iterator(); 
		while(enhancerIter.hasNext()) {
			Card card = enhancerIter.next();
			if (card.getID() == Card.OD_MATE)
				hasMate = true;
			else if (card.getID() == Card.ME_BABY)
				treasures--;
			else if (card.getID() == Card.ME_ANCIENT)
				treasures += 2;
			else if (card.getID() == Card.ME_ENRAGED)
				treasures++;
			else if (card.getID() == Card.ME_HUMONGOUS)
				treasures += 2;
			else if (card.getID() == Card.ME_INTELLIGENT)
				treasures++;
		}
		if (treasures < 1)
			treasures = 1;
		
		if (hasMate)
			treasures *= 2;
		
		return treasures;
	}
	
	/**
	 * Returns the number of levels that would be won if the monster is defeated at its current status.
	 * @param monster monster to check
	 * @return the number of levels that would be won by defeating the monster
	 */
	public int getWinLevelCount(MonsterCard monster) {
		int winLevels = monster.getWinLevels();
	
		if (monster.getID() == Card.M_THE_NOTHING) {
			int monsterLevel = getMonstersLevel();
		
			if (!isHelper() && activePlayer.getLevel() > monsterLevel)
				winLevels++;
			else if (!isHelper() && activePlayer.getLevel() == monsterLevel && activePlayer.isWarrior())
				winLevels++;
		}
		else if (monster.getID() == Card.M_LARGE_ANGRY_CHICKEN && usedFire())
			winLevels++;
		
		for (Card card : getMonsterEnhancers(monster))
			if (card.getID() == Card.OD_MATE)
				winLevels *= 2;
		
		return winLevels;
	}
	
	/**
	 * Causes players to go up levels and calculates treasures won by defeating the remaining monsters in the battle.
	 * @throws EndGameException if any player went up enough levels to win
	 */
	public void defeatMonsters() throws EndGameException {
		GM.moveToOtherPhase();
		
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext()) {
			Monster monster = monsterIter.next();
			int winLevels = getWinLevelCount(monster.card);
			int treasures = getWinTreasureCount(monster.card);
					
			activePlayer.goUpLevels(winLevels, true);
			if (isHelper() && helper.isElf()) {
				helper.goUpLevel(true);
				for (Card card : monster.enhancerCards)
					if (card.getID() == Card.OD_MATE)
						helper.goUpLevel(true);
			}
			GM.checkForWinners();
			
			treasuresWon += treasures;
		}
			
		endBattle();
	}
		
	/**
	 * Removes the specified monster from battle and calculates treasures won from it.
	 * @param monster Monster Card of the monster to remove
	 * @param alsoDiscardMate whether or not a monster's mate should also be discarded
	 */
	public void discardMonster(MonsterCard monster, boolean alsoDiscardMate) {
		int treasures = monster.getTreasures();
		
		boolean hasMate = false;
		Iterator<DoorCard> enhancerIter = getMonsterEnhancers(monster).iterator(); 
		while(enhancerIter.hasNext()) {
			DoorCard card = enhancerIter.next();
			if (card.getID() == Card.OD_MATE) {
				hasMate = true;
				enhancerIter.remove();
				GM.getDoorDeck().discard(card);
				break;
			}
		}
		
		// if monster has no mate or the mate should also be removed, discard all enhancer cards for the monster.
		enhancerIter = getMonsterEnhancers(monster).iterator(); 
		while(enhancerIter.hasNext()) {
			DoorCard card = enhancerIter.next();
			if (card.getID() == Card.ME_BABY)
				treasures--;
			else if (card.getID() == Card.ME_ANCIENT)
				treasures += 2;
			else if (card.getID() == Card.ME_ENRAGED)
				treasures++;
			else if (card.getID() == Card.ME_HUMONGOUS)
				treasures += 2;
			else if (card.getID() == Card.ME_INTELLIGENT)
				treasures++;
			
			if (!hasMate || alsoDiscardMate)
				GM.getDoorDeck().discard(card);
		}
		if (treasures < 1)
			treasures = 1;
		
		if (hasMate && alsoDiscardMate)
			treasures *= 2;
		
		treasuresWon += treasures;
		
		if (hasMate && !alsoDiscardMate)
			return;
		
		removeMonster(monster);
		GM.getDoorDeck().discard(monster);
		
		// end battle if this was the last monster in the battle
		if (monsters.isEmpty()) 
			endBattle();
	}
	
	/**
	 * Removes a monster with all of it enhancers from the battle.
	 * @param monster Monster Card of the monster to remove
	 */
	public void befriendMonster(MonsterCard monster) {
		Iterator<DoorCard> enhancerIter = getMonsterEnhancers(monster).iterator(); 
		while(enhancerIter.hasNext())
			GM.getDoorDeck().discard(enhancerIter.next());
		
		removeMonster(monster);
		GM.getDoorDeck().discard(monster);
		
		if (monsters.isEmpty()) 
			endBattle();
	}
	
	/**
	 * Sets the calculated number of treasures won to zero; IE. the players get no treasure.
	 */
	public void leaveTreasuresBehind() {
		treasuresWon = 0;
	}
	
	/**
	 * Adds the given number to the calculated amount of treasures won.
	 * @param count the number of treasures won to add to the total
	 */
	public void addTreasures(int count) {
		treasuresWon += count;
	}
	
	/**
	 * Returns the number of treasures that have been won so far. 
	 * @return the number of treasures won from the battle
	 */
	public int getTreasureCount() {
		return treasuresWon;
	}
	
	/**
	 * Handles special situations if certain conditions are now met (for Amazon and Lawyer Monsters).
	 */
	public void checkForChanges() {
		LinkedList<MonsterCard> monsterCards = new LinkedList<MonsterCard>();
		Iterator<Monster> monsterIter = monsters.iterator();
		while (monsterIter.hasNext())
			monsterCards.add(monsterIter.next().card);
		
		Iterator<MonsterCard> monsterCardIter = monsterCards.iterator();
		while (monsterCardIter.hasNext()) {
			MonsterCard card = monsterCardIter.next();
			
			// Special situation for females and Amazons (befriend monster, get a Treasure)
			if (card.getID() == Card.M_AMAZON) {
				if (activePlayer.isFemale() || (isHelper() && helper.isFemale())) {
					DisplayCardsDialog displayDialog = new DisplayCardsDialog(card, "Amazon Versus Female");
					displayDialog.setVisible(true);
					
					befriendMonster(card);
					
					if (activePlayer.isFemale())
						activePlayer.drawTreasureCards(1, false);
					if (isHelper() && helper.isFemale())
						helper.drawTreasureCards(1, false);
				}
			}
			// Special situation for Thieves and Lawyers (Befriend monster, choose to trade 2 treasures for 2 new ones)
			else if (card.getID() == Card.M_LAWYER) {
				if (activePlayer.isThief() || (isHelper() && helper.isThief())) {
					DisplayCardsDialog displayDialog = new DisplayCardsDialog(card, "Lawyer Versus Thief");
					displayDialog.setVisible(true);
					
					befriendMonster(card);
					
					if (activePlayer.isThief()) {
						if (activePlayer.isComputer()) {
							LinkedList<AIValuedCard> leastWorthTreasureCards = new LinkedList<AIValuedCard>();
							for (TreasureCard item : activePlayer.getAllItems()) {
								int itemValue = AICardEvaluator.getCardValueToPlayer(item, activePlayer, activePlayer.getHandCards());
								if (itemValue >= AIManager.UNKNOWN_CARD_VALUE)
									continue;
								
								AIValuedCard valuedCard = new AIValuedCard(item, itemValue);
								int leastWorthIdx;
								for (leastWorthIdx = 0; leastWorthIdx < leastWorthTreasureCards.size(); leastWorthIdx++)
									if (itemValue <= leastWorthTreasureCards.get(leastWorthIdx).getValue())
										break;
								
								leastWorthTreasureCards.add(leastWorthIdx, valuedCard);
							}
							
							for (Card handCard : activePlayer.getHandCards()) {
								if (handCard instanceof TreasureCard) {
									int cardValue = AICardEvaluator.getCardValueToPlayer(handCard, activePlayer, activePlayer.getHandCards());
									if (cardValue >= AIManager.UNKNOWN_CARD_VALUE)
										continue;
									
									AIValuedCard valuedCard = new AIValuedCard(handCard, cardValue);
									int leastWorthIdx;
									for (leastWorthIdx = 0; leastWorthIdx < leastWorthTreasureCards.size(); leastWorthIdx++)
										if (cardValue <= leastWorthTreasureCards.get(leastWorthIdx).getValue())
											break;
									
									leastWorthTreasureCards.add(leastWorthIdx, valuedCard);
								}
							}
							
							if (activePlayer.hasHireling()) {
								int cardValue = AICardEvaluator.getCardValueToPlayer(activePlayer.getHirelingCard(), activePlayer, activePlayer.getHandCards());
								if (cardValue < AIManager.UNKNOWN_CARD_VALUE) {
									AIValuedCard valuedCard = new AIValuedCard(activePlayer.getHirelingCard(), cardValue);
									int leastWorthIdx;
									for (leastWorthIdx = 0; leastWorthIdx < leastWorthTreasureCards.size(); leastWorthIdx++)
										if (cardValue <= leastWorthTreasureCards.get(leastWorthIdx).getValue())
											break;
									
									leastWorthTreasureCards.add(leastWorthIdx, valuedCard);
								}
							}
							
							if (leastWorthTreasureCards.size() >= 2) {
								String message = activePlayer + " interacted with the Lawyer by discarding the ";
								message += leastWorthTreasureCards.get(0).getCard() + " card and the ";
								message += leastWorthTreasureCards.get(1).getCard();
								message += " card and drawing 2 new treasure cards.";
								Messenger.display(message, "Friendly Lawyer");
								
								activePlayer.discard(leastWorthTreasureCards.remove().getCard());
								activePlayer.discard(leastWorthTreasureCards.remove().getCard());
								activePlayer.drawTreasureCards(2, false);
							}
						}
						else {
							LinkedList<Card> treasures = new LinkedList<Card>();
							treasures.addAll(activePlayer.getAllItems());
							Iterator<Card> cardIter = activePlayer.getHandCards().iterator();
							while (cardIter.hasNext()) {
								Card current = cardIter.next();
								if (current instanceof TreasureCard)
									treasures.add(current);
							}
							if (activePlayer.hasHireling())
								treasures.add(activePlayer.getHirelingCard());
							
							if (treasures.size() < 2) {
								String message = activePlayer.getName() + " does not have 2 treasures to trade in.";
								Messenger.display(message, "Friendly Lawyer");
							}
							else {
								String message = activePlayer.getName() + ", would you like to" +
												 " discard 2 Treasure Cards and draw two new Treasure Cards?";
								int result = JOptionPane.showConfirmDialog(null, message, "Friendly Lawyer", JOptionPane.YES_NO_OPTION);
								if (result == JOptionPane.YES_OPTION) {									
									LoseCardsDialog dialog = new LoseCardsDialog(activePlayer, treasures, 2, "Treasure Cards");
									dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
									dialog.setVisible(true);
									if (dialog.madeDiscard())
										activePlayer.drawTreasureCards(2, false);
								}
							}
						}
					}
					
					if (isHelper() && helper.isThief()) {
						if (helper.isComputer()) {
							LinkedList<AIValuedCard> leastWorthTreasureCards = new LinkedList<AIValuedCard>();
							for (TreasureCard item : helper.getAllItems()) {
								int itemValue = AICardEvaluator.getCardValueToPlayer(item, helper, helper.getHandCards());
								if (itemValue >= AIManager.UNKNOWN_CARD_VALUE)
									continue;
								
								AIValuedCard valuedCard = new AIValuedCard(item, itemValue);
								int leastWorthIdx;
								for (leastWorthIdx = 0; leastWorthIdx < leastWorthTreasureCards.size(); leastWorthIdx++)
									if (itemValue <= leastWorthTreasureCards.get(leastWorthIdx).getValue())
										break;
								
								leastWorthTreasureCards.add(leastWorthIdx, valuedCard);
							}
							
							for (Card handCard : helper.getHandCards()) {
								if (handCard instanceof TreasureCard) {
									int cardValue = AICardEvaluator.getCardValueToPlayer(handCard, helper, helper.getHandCards());
									if (cardValue >= AIManager.UNKNOWN_CARD_VALUE)
										continue;
									
									AIValuedCard valuedCard = new AIValuedCard(handCard, cardValue);
									int leastWorthIdx;
									for (leastWorthIdx = 0; leastWorthIdx < leastWorthTreasureCards.size(); leastWorthIdx++)
										if (cardValue <= leastWorthTreasureCards.get(leastWorthIdx).getValue())
											break;
									
									leastWorthTreasureCards.add(leastWorthIdx, valuedCard);
								}
							}
							
							if (helper.hasHireling()) {
								int cardValue = AICardEvaluator.getCardValueToPlayer(helper.getHirelingCard(), helper, helper.getHandCards());
								if (cardValue < AIManager.UNKNOWN_CARD_VALUE) {
									AIValuedCard valuedCard = new AIValuedCard(helper.getHirelingCard(), cardValue);
									int leastWorthIdx;
									for (leastWorthIdx = 0; leastWorthIdx < leastWorthTreasureCards.size(); leastWorthIdx++)
										if (cardValue <= leastWorthTreasureCards.get(leastWorthIdx).getValue())
											break;
									
									leastWorthTreasureCards.add(leastWorthIdx, valuedCard);
								}
							}
							
							if (leastWorthTreasureCards.size() >= 2) {
								String message = helper + " interacted with the Lawyer by discarding the ";
								message += leastWorthTreasureCards.get(0).getCard() + " card and the ";
								message += leastWorthTreasureCards.get(1).getCard();
								message += " card and drawing 2 new treasure cards.";
								Messenger.display(message, "Friendly Lawyer");
								
								helper.discard(leastWorthTreasureCards.remove().getCard());
								helper.discard(leastWorthTreasureCards.remove().getCard());
								helper.drawTreasureCards(2, false);
							}
						}
						else {
							LinkedList<Card> treasures = new LinkedList<Card>();
							treasures.addAll(helper.getAllItems());
							Iterator<Card> cardIter = helper.getHandCards().iterator();
							while (cardIter.hasNext()) {
								Card current = cardIter.next();
								if (current instanceof TreasureCard)
									treasures.add(current);
							}
							if (helper.hasHireling())
								treasures.add(helper.getHirelingCard());
							
							if (treasures.size() < 2) {
								String message = helper.getName() + " does not have 2 treasures to trade in.";
								Messenger.display(message, "Friendly Lawyer");
							}
							else {
								String message = helper.getName() + ", would you like to" +
												 " discard 2 Treasure Cards and draw two new Treasure Cards?";
								int result = JOptionPane.showConfirmDialog(null, message, "Friendly Lawyer", JOptionPane.YES_NO_OPTION);
								if (result == JOptionPane.YES_OPTION) {									
									LoseCardsDialog dialog = new LoseCardsDialog(helper, treasures, 2, "Treasure Cards");
									dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
									dialog.setVisible(true);
									if (dialog.madeDiscard())
										helper.drawTreasureCards(2, false);
								}
							}
						}
					}						
				}
			}
		}
	}
	
}
