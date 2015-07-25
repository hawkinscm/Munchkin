
package model;

import exceptions.EndGameException;
import exceptions.NoCardsLeftException;
import exceptions.PlayImmediatelyException;
import gui.DisplayCardsDialog;
import gui.GUI;
import gui.InHandDialog;
import gui.LoseCardsDialog;
import gui.ResurrectionDialog;
import gui.SellItemsDialog;
import gui.TakeCardDialog;
import gui.components.Messenger;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Observable;

import ai.AIManager;

import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.DoorCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;
import model.card.TreasureCard;
import model.card.EquipmentCard.EquipmentType;

/**
 * Class that represents a player.
 */
public class Player extends Observable {
	
	// variables that define a player
	private String name;
	private PlayerType playerType;
	private int level;	
	private boolean isMale;		
	private boolean originallyMale;
	
	// all the cards that a player can have	
	private LinkedList<Card> hand;
	private LinkedList<ItemCard> carriedItems;
	private LinkedList<EquipmentCard> equippedItems;
	private LinkedList<EquipmentCard> unequippedItems;
	
	private CurseCard chickenOnHeadCurseCard;
	private CurseCard changeSexCurseCard;
	private CurseCard malignMirrorCurseCard;
	
	private LinkedList<RaceCard> raceCards;
	private OtherDoorCard halfBreedCard;
	private LinkedList<ClassCard> classCards;
	private OtherDoorCard superMunchkinCard;
	
	private OtherDoorCard cheatCard;
	private EquipmentCard cheatingItemCard;
	private OtherTreasureCard hirelingCard;
	
	// flag to indicate if the player needs new cards dealt to him at the beginning of his next turn
	private boolean needsNewCards;
	
	/**
	 * Creates a new Player object.
	 * @param gui reference to the main controlling GUI
	 * @param n name of the new player
	 * @param male whether or not the player is male
	 * @param type the type the player is
	 */
	public Player(GUI gui, String n, boolean male, PlayerType type) {
		if (type != PlayerType.TEST)
			addObserver(gui);
		
		name = n;
		playerType = type;
		level = 1;
		isMale = male;
		originallyMale = male;
		
		hand = new LinkedList<Card>();
		carriedItems = new LinkedList<ItemCard>();
		equippedItems = new LinkedList<EquipmentCard>();
		unequippedItems = new LinkedList<EquipmentCard>();
		
		chickenOnHeadCurseCard = null;
		changeSexCurseCard = null;
		malignMirrorCurseCard = null;
		
		raceCards = new LinkedList<RaceCard>();
		superMunchkinCard = null;
		classCards = new LinkedList<ClassCard>();
		halfBreedCard = null;	
		
		cheatCard = null;
		cheatingItemCard = null;
		hirelingCard = null;		
				
		needsNewCards = false;
	}
	
	/**
	 * Returns a Player with all of the same cards as the given player.  Used for testing and special checks.
	 * @return a new Player identical except in name to this player.
	 */
	public Player clone() {
		Player clone = new Player(null, "clone", originallyMale, PlayerType.TEST);
		clone.goUpLevels(level - 1, false);
		clone.getHandCards().addAll(hand);
		clone.getCarriedItems().addAll(carriedItems);
		clone.getEquippedItems().addAll(equippedItems);
		clone.getUnequippedItems().addAll(unequippedItems);
		clone.getRaceCards().addAll(raceCards);
		clone.getClassCards().addAll(classCards);
		clone.setHalfBreedCard(halfBreedCard);
		clone.setSuperMunchkinCard(superMunchkinCard);
		if (chickenOnHeadCurseCard != null)
			chickenOnHeadCurseCard.addEffects(clone);
		if (changeSexCurseCard != null)
			changeSexCurseCard.addEffects(clone);
		if (malignMirrorCurseCard != null)
			malignMirrorCurseCard.addEffects(clone);
		clone.setCheatCards(cheatCard, cheatingItemCard);
		clone.setHirelingCard(hirelingCard);
		
		return clone;
	}
	
	@Override
	/**
	 * Returns the name of the player.
	 * @return the name of the player
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * Returns the name of the player.
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the level of the player.
	 * @return the level of the player
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Causes the player to go up a level.
	 * @param allowWin whether or not a player is allowed to go above level 9 and win the game
	 */
	public void goUpLevel(boolean allowWin) {
		if (allowWin || level < 9) {
			level++;
		
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Causes the player to go down a level (can't go below level 1).
	 */
	public void goDownLevel() {
		if (level > 1) {
			level--;
		
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Causes the player to go up the given number of levels.
	 * @param increase number of levels to go up
	 * @param allowWin whether or not a player is allowed to go above level 9 and win the game
	 */
	public void goUpLevels(int increase, boolean allowWin) {
		if (level >= 9 && !allowWin)
			return;
		
		if (allowWin || level + increase < 10)
			level += increase;
		else
			level = 9;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Causes the player to go down the given number of levels (can't go below level 1).
	 * @param decrease number of levels to go down
	 */
	public void goDownLevels(int decrease) {
		if (level == 1)
			return;
		
		level -= decrease;
		if (level < 1)
			level = 1;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns the level bonus from all equipped items.
	 * @return the level bonus from all equipped items
	 */
	public int getEquipmentBonus() {
		int bonus = 0;
		
		for (EquipmentCard item : equippedItems) {
			if (malignMirrorCurseCard != null && item.isWeapon())
				continue;			
			
			bonus += item.getBonus(this);
		}
		
		return bonus;
	}
	
	/**
	 * Returns whether or not the player is a class-less human or a part human.
	 * @return true if the player is a class-less human or part human; false otherwise
	 */
	public boolean isHuman() {
		if ((halfBreedCard != null && raceCards.size() == 1) || raceCards.isEmpty())
			return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is an elf or part-elf.
	 * @return true if the player is an elf or part-elf; false otherwise
	 */
	public boolean isElf() {
		for (RaceCard raceCard : raceCards)
			if (raceCard.getRace() == Race.ELF)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is a dwarf or part-dwarf.
	 * @return true if the player is a dwarf or part-dwarf; false otherwise
	 */
	public boolean isDwarf() {
		for (RaceCard raceCard : raceCards)
			if (raceCard.getRace() == Race.DWARF)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is a halfling or part-halfling.
	 * @return true if the player is a halfling or part-halfling; false otherwise
	 */
	public boolean isHalfling() {
		for (RaceCard raceCard : raceCards)
			if (raceCard.getRace() == Race.HALFLING)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is the given race.
	 * @param race the race to check
	 * @return true if the player is the given race; false otherwise
	 */
	public boolean isRace(Race race) {
		for (RaceCard raceCard : raceCards)
			if (raceCard.getRace() == race)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is a warrior.
	 * @return true if the player is a warrior; false otherwise
	 */
	public boolean isWarrior() {
		for (ClassCard classCard : classCards)
			if (classCard.getCharacterClass() == Class.WARRIOR)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is a wizard.
	 * @return true if the player is a wizard; false otherwise
	 */
	public boolean isWizard() {
		for (ClassCard classCard : classCards)
			if (classCard.getCharacterClass() == Class.WIZARD)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is a thief.
	 * @return true if the player is a thief; false otherwise
	 */
	public boolean isThief() {
		for (ClassCard classCard : classCards)
			if (classCard.getCharacterClass() == Class.THIEF)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is a cleric.
	 * @return true if the player is a cleric; false otherwise
	 */
	public boolean isCleric() {
		for (ClassCard classCard : classCards)
			if (classCard.getCharacterClass() == Class.CLERIC)
				return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the player is the given class.
	 * @param characterClass the class to check
	 * @return true if the player is the given class; false otherwise
	 */
	public boolean isClass(Class characterClass) {
		for (ClassCard classCard : classCards)
			if (classCard.getCharacterClass() == characterClass)
				return true;
		
		return false;
	}
	
	/**
	 * Adds a race to the character.
	 * @param card race card of the race to add
	 */
	public void addRaceCard(RaceCard card) {
		raceCards.add(card);
		
		// check to see if dwarf class is no longer available and force player to discard extra big items.
		if (!isDwarf()) {
			LinkedList<Card> bigItems = new LinkedList<Card>();
			Iterator<EquipmentCard> bigItemIter = getBigItems().iterator();
			while (bigItemIter.hasNext()) {
				EquipmentCard bigItem = bigItemIter.next();
				if (cheatingItemCard != bigItem)
					bigItems.add(bigItem);
			}
			
			int maxBigItems = 1;
			if (hirelingCard != null)
				maxBigItems = 2;
			if (bigItems.size() > maxBigItems) {
				if (GM.getActivePlayer() == this) {
					if (!isComputer()) {
						String prompt = "<html>&nbsp;You have one chance now to sell some items.&nbsp;<br>";
						prompt += "&nbsp;If you have more than " + maxBigItems + " big items when you are done selling, they will go to charity.</html>";
						Messenger.warn(prompt, "No Longer A Dwarf");
					}
					
					(new SellItemsDialog(null, this)).setVisible(true);
					
					bigItems.clear();
					bigItemIter = getBigItems().iterator();
					while (bigItemIter.hasNext()) {
						EquipmentCard bigItem = bigItemIter.next();
						if (cheatingItemCard != bigItem)
							bigItems.add(bigItem);
					}
				}				
				
				if (bigItems.size() > maxBigItems) {				
					LoseCardsDialog dialog = new LoseCardsDialog(this, bigItems, bigItems.size() - maxBigItems, "big items");
					dialog.giveToCharity();
					dialog.setVisible(true);
				}
			}
		}
		
		LinkedList<EquipmentCard> tempEquippedItems = new LinkedList<EquipmentCard>();
		tempEquippedItems.addAll(equippedItems);
		for (EquipmentCard item : tempEquippedItems) {
			String reason = equip(item);
			if (!reason.equals(""))
				Messenger.display(getName() + ", the " + item + " item has been unequipped: " + reason, "New Race Result");
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns a list of all player Race Cards.
	 * @return all player Race Cards
	 */
	public LinkedList<RaceCard> getRaceCards() {
		return raceCards;
	}
	
	/**
	 * Returns whether or not the player is a Half-Breed.
	 * @return true if the player has a Half-Breed Card in play; false otherwise
	 */
	public boolean isHalfBreed() {
		return (halfBreedCard != null);
	}
	
	/**
	 * Returns the player's in-play Half-Breed Card. 
	 * @return the player's in-play Half-Breed Card; or null if doesn't have one in play
	 */
	public OtherDoorCard getHalfBreedCard() {
		return halfBreedCard;
	}
	
	/**
	 * Sets the player's in-play Half-Breed Card.
	 * @param card Half-Breed Card to set
	 */
	public void setHalfBreedCard(OtherDoorCard card) {
		halfBreedCard = card;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Discards the player's in-play Half-Breed Card.
	 */
	public void loseHalfBreed() {
		if (halfBreedCard != null) {
			GM.getDoorDeck().discard(halfBreedCard);
			halfBreedCard = null;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Removes the given race from the player and discards the card.
	 * @param card the Race Card of the race to remove from the player
	 */
	public void discardRaceCard(RaceCard card) {
		raceCards.remove(card);
		GM.getDoorDeck().discard(card);
		
		if (raceCards.isEmpty() && halfBreedCard != null) {
			GM.getDoorDeck().discard(halfBreedCard);
			halfBreedCard = null;
		}
		
		// check to see if dwarf class is no longer available and force player to discard extra big items.
		if (!isDwarf()) {
			LinkedList<Card> bigItems = new LinkedList<Card>();
			Iterator<EquipmentCard> bigItemIter = getBigItems().iterator();
			while (bigItemIter.hasNext()) {
				EquipmentCard bigItem = bigItemIter.next();
				if (cheatingItemCard != bigItem)
					bigItems.add(bigItem);
			}
			
			int maxBigItems = 1;
			if (hirelingCard != null)
				maxBigItems = 2;
			if (bigItems.size() > maxBigItems) {
				if (GM.getActivePlayer() == this) {
					if (!isComputer()) {
						String prompt = "<html>&nbsp;You have one chance now to sell some items.&nbsp;<br>";
						prompt += "&nbsp;If you have more than " + maxBigItems + " big items when you are done selling, they will go to charity.</html>";
						Messenger.warn(prompt, "No Longer A Dwarf");
					}
					
					(new SellItemsDialog(null, this)).setVisible(true);
					
					bigItems.clear();
					bigItemIter = getBigItems().iterator();
					while (bigItemIter.hasNext()) {
						EquipmentCard bigItem = bigItemIter.next();
						if (cheatingItemCard != bigItem)
							bigItems.add(bigItem);
					}
				}				
				
				if (bigItems.size() > maxBigItems) {				
					LoseCardsDialog dialog = new LoseCardsDialog(this, bigItems, bigItems.size() - maxBigItems, "big items");
					dialog.giveToCharity();
					dialog.setVisible(true);
				}
			}
		}
		
		LinkedList<EquipmentCard> tempEquippedItems = new LinkedList<EquipmentCard>();
		tempEquippedItems.addAll(equippedItems);
		for (EquipmentCard item : tempEquippedItems) {
			String reason = equip(item);
			if (!reason.equals(""))
				Messenger.display(getName() + ", the " + item + " item has been unequipped: " + reason, "Lose Race Result");
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Adds the given character class to the player.
	 * @param card Class Card of the character class to add
	 */
	public void addClassCard(ClassCard card) {
		classCards.add(card);
		
		LinkedList<EquipmentCard> tempEquippedItems = new LinkedList<EquipmentCard>();
		tempEquippedItems.addAll(equippedItems);
		for (EquipmentCard item : tempEquippedItems) {
			String reason = equip(item);
			if (!reason.equals(""))
				Messenger.display(getName() + ", the " + item + " item has been unequipped: " + reason, "New Class Result");
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns a list of all the player's Class Cards.
	 * @return all the player's Class Cards
	 */
	public LinkedList<ClassCard> getClassCards() {
		return classCards;
	}
	
	/**
	 * Returns whether or not the player is a Super Munchkin.
	 * @return true if the player has a Super Munchkin Card in play; false otherwise
	 */
	public boolean isSuperMunchkin() {
		return (superMunchkinCard != null);
	}
	
	/**
	 * Returns the player's in-play Super Munchkin Card.
	 * @return the player's in-play Super Munchkin Card; null if doesn't have one
	 */
	public OtherDoorCard getSuperMunchkinCard() {
		return superMunchkinCard;
	}
	
	/**
	 * Sets the player's in-play Super Munchkin Card.
	 * @param card the Super Munchkin Card to set
	 */
	public void setSuperMunchkinCard(OtherDoorCard card) {
		superMunchkinCard = card;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Discards the player's in-play Super Munchkin Card.
	 */
	public void loseSuperMunchkin() {
		if (superMunchkinCard != null) {
			GM.getDoorDeck().discard(superMunchkinCard);
			superMunchkinCard = null;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Removes the given character class from the player and discards the card.
	 * @param card the Class Card of the character class to remove from the player
	 */
	public void discardClassCard(ClassCard card) {
		classCards.remove(card);
		GM.getDoorDeck().discard(card);
		
		if (superMunchkinCard != null) {
			GM.getDoorDeck().discard(superMunchkinCard);
			superMunchkinCard = null;
		}
		
		LinkedList<EquipmentCard> tempEquippedItems = new LinkedList<EquipmentCard>();
		tempEquippedItems.addAll(equippedItems);
		for (EquipmentCard item : tempEquippedItems) {
			String reason = equip(item);
			if (!reason.equals(""))
				Messenger.display(getName() + ", the " + item + " item has been unequipped: " + reason, "Lose Class Result");
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Puts the lasting Curse Card on the player.
	 * @param card the lasting Curse Card
	 */
	public void addLastingCurse(CurseCard card) {
		if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
			chickenOnHeadCurseCard = card;
		else if (card.getID() == Card.CU_CHANGE_SEX)
			changeSexCurseCard = card;
		else if (card.getID() == Card.CU_MALIGN_MIRROR)
			malignMirrorCurseCard = card;
		else {
			String message = "Error: " + card + "(" + card.getID() + ") is not a lasting curse.";
			Messenger.display(message, "Internal Error");
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns whether or not the player is cursed with the lasting Chicken On Head curse.
	 * @return true if the player is cursed with the lasting Chicken On Head curse; false otherwise
	 */
	public boolean hasChickenOnHead() {
		return (chickenOnHeadCurseCard != null);
	}
	
	/**
	 * Returns the player's Chicken On Head Curse Card.
	 * @return the player's Chicken On Head Curse Card; or null if not cursed
	 */
	public CurseCard getChickenOnHeadCard() {
		return chickenOnHeadCurseCard;
	}
	
	/**
	 * Returns whether or not the player is cursed with the lasting Malign Mirror curse.
	 * @return true if the player is cursed with the lasting Malign Mirror curse; false otherwise
	 */
	public boolean hasMalignMirror() {
		return (malignMirrorCurseCard != null);
	}
	
	/**
	 * Returns the player's Malign Mirror Curse Card.
	 * @return the player's Malign Mirror Curse Card; or null if not cursed
	 */
	public CurseCard getMalignMirrorCard() {
		return malignMirrorCurseCard;
	}
	
	/**
	 * Returns whether or not the player is cursed with the lasting Change Sex Distraction curse.
	 * @return true if the player is cursed with the lasting Change Sex Distraction curse; false otherwise
	 */
	public boolean hasDistractionCurse() {
		return (changeSexCurseCard != null);
	}
	
	/**
	 * Returns whether or not the player is cursed with the lasting Change Sex curse.
	 * @return true if the player is cursed with the lasting Change Sex curse; false otherwise
	 */
	public boolean hasChangeSexCurse() {
		return (isMale != originallyMale);
	}
	
	/**
	 * Returns the player's Change Sex Curse Card.
	 * @return the player's Change Sex Curse Card; or null if not cursed
	 */
	public CurseCard getChangeSexCard() {
		return changeSexCurseCard;
	}
	
	/**
	 * Removes the player's Chicken On Head curse and discard card; does nothing if not cursed.
	 */
	public void removeChickenOnHeadCurse() {
		if (chickenOnHeadCurseCard != null) {
			GM.getDoorDeck().discard(chickenOnHeadCurseCard);
			chickenOnHeadCurseCard = null;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Removes the player's Sex Change curse and discard card; does nothing if not cursed.
	 */
	public void removeSexChangeCurse() {
		if (changeSexCurseCard != null) {
			GM.getDoorDeck().discard(changeSexCurseCard);
			changeSexCurseCard = null;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Removes the player's Malign Mirror curse and discard card; does nothing if not cursed.
	 */
	public void removeMalignMirror() {
		if (malignMirrorCurseCard != null) {
			GM.getDoorDeck().discard(malignMirrorCurseCard);
			malignMirrorCurseCard = null;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Removes all lasting curses from the player that only last until he's finished a battle.
	 */
	public void removeLastingCurses() {		
		removeSexChangeCurse();	
		removeMalignMirror();
	}
	
	/**
	 * Switches the gender of the character.
	 */
	public void changeSex() {
		isMale = !isMale;
		if (isMale) {
			EquipmentCard sword = null;
			for (EquipmentCard equipment : equippedItems)
				if (equipment.getID() == Card.E_BROAD_SWORD)
					sword = equipment;
			
			if (sword != null && cheatingItemCard != sword) {
				String reason = equip(sword);
				Messenger.display(getName() + ", the " + sword + " has been unequipped: " + reason, "Change Sex Result");
			}
		}
		else {
			EquipmentCard club = null;
			for (EquipmentCard equipment : equippedItems)
				if (equipment.getID() == Card.E_GENTLEMENS_CLUB)
					club = equipment;
			
			if (club != null && cheatingItemCard != club) {
				String reason = equip(club);
				Messenger.display(getName() + ", the " + club + " has been unequipped: " + reason, "Change Sex Result");
			}
		}
			
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns whether or not the player is male.
	 * @return true if the player is male; false if female
	 */
	public boolean isMale() {
		return isMale;
	}
	
	/**
	 * Returns whether or not the player is female.
	 * @return true if the player is female; false if male
	 */
	public boolean isFemale() {
		return !isMale;
	}
	
	/**
	 * Returns whether or not the player needs to have new cards dealt to him at the beginning of his next turn.
	 * @return true if the player needs to have new cards dealt to him at the beginning of his next turn; false otherwise
	 */
	public boolean needsNewCards() {
		return needsNewCards;
	}
	
	/**
	 * Signals that new cards have been dealt to the player and the player no longer needs cards dealt to him.
	 */
	public void drewNewCards() {
		needsNewCards = false;
	}
	
	/**
	 * Returns the type of player this is.
	 * @return the type of player this is
	 */
	public PlayerType getPlayerType() {
		return playerType;
	}
	
	/**
	 * Returns whether or not this player is a computer.
	 * @return true if this player is a computer; false if human.
	 */
	public boolean isComputer() {
		return (playerType != PlayerType.HUMAN);
	}
	
	/**
	 * Returns whether or not the player is able to use the resurrect ability when the opportunity arises.
	 * @return true if the player is able to use the resurrect ability when the opportunity arises; false otherwise.
	 */
	public boolean canResurrect() {
		return (isCleric() && !hand.isEmpty());
	}
	
	/**
	 * Adds a card to the player's hand.
	 * @param card the card to add
	 */
	public void addCard(Card card) {
		hand.add(card);
	}
	
	/**
	 * Adds an Item Card to the player's in-play Item Cards.
	 * @param card Item Card to add
	 */
	public void addItem(TreasureCard card) {
		boolean isBattlePhase = GM.isBattlePhase();
		if (isBattlePhase)
			GM.moveToOtherPhase();
		Player activePlayer = GM.getActivePlayer();
		// Allows player to put an item card down regardless of whose turn it is and what turn phase it is
		GM.setActivePlayer(this);
		try {
			if (!CardPlayManager.playCard(this, card))
				discard(card);
		}
		catch (EndGameException ex) {}
		GM.setActivePlayer(activePlayer);
		if (isBattlePhase)
			GM.moveToBattlePhase();
	}
	
	/**
	 * Draws the given number of Door Cards and allows player to view/play them.
	 * @param cards number of cards to draw
	 * @param isFaceUp whether or not the cards should be drawn face up or face down.
	 * @throws EndGameException happens when drawn card causes someone to win and ends the game
	 */
	public void drawDoorCards(int cards, boolean isFaceUp) throws EndGameException {
		LinkedList<Card> newCards = new LinkedList<Card>();
		for (int count = 0; count < cards; count++) {
			try {
				Card newCard = null;
				// Allows resurrect ability if available
				if (isFaceUp && canResurrect() && !GM.getDoorDeck().getDiscardPile().isEmpty()) {
					ResurrectionDialog dialog = new ResurrectionDialog(this, GM.getDoorDeck().getDiscardPile().peek());
					dialog.setVisible(true);
					Card cardToDiscard = dialog.getCardToDiscard();
					if (cardToDiscard != null) {
						newCard = GM.getDoorDeck().takeDiscard();
						discard(cardToDiscard);
					}
				}
				
				if (newCard == null)
					newCard = GM.getDoorDeck().drawCard();
				hand.add(newCard);
				newCards.add(newCard);
			}
			catch (PlayImmediatelyException ex) {}
		}
		if (newCards.isEmpty())
			return;
		
		if (isComputer()) {
			if (isFaceUp)
				(new DisplayCardsDialog(newCards, name + "'s drawn door cards")).setVisible(true);
			AIManager.playHandCards(this, newCards);
		}
		else
			(new InHandDialog(this, newCards)).setVisible(true);
	}
	
	/**
	 * Draws the given number of Treasure Cards and allows player to view/play them.
	 * @param cards number of cards to draw
	 * @param isFaceUp whether or not the cards should be drawn face up or face down.
	 */
	public void drawTreasureCards(int cards, boolean isFaceUp) {
		LinkedList<Card> newCards = new LinkedList<Card>();
		for (int count = 0; count < cards; count++) {
			try {
				Card newCard = null;
				if (isFaceUp && canResurrect() && !GM.getTreasureDeck().getDiscardPile().isEmpty()) {
					ResurrectionDialog dialog = new ResurrectionDialog(this, GM.getTreasureDeck().getDiscardPile().peek());
					dialog.setVisible(true);
					Card cardToDiscard = dialog.getCardToDiscard();
					if (cardToDiscard != null) {
						newCard = GM.getTreasureDeck().takeDiscard();
						discard(cardToDiscard);
					}
				}
				
				if (newCard == null)
					newCard = GM.getTreasureDeck().drawCard();
				hand.add(newCard);
				newCards.add(newCard);
			}
			catch (PlayImmediatelyException ex) {
				Iterator<TreasureCard> treasureIter = GM.getTreasureDeck().getHoardCards(this, isFaceUp).iterator();
				while (treasureIter.hasNext()) {
					Card newCard = treasureIter.next();
					hand.add(newCard);
					newCards.add(newCard);
				}
			}
			catch (NoCardsLeftException ex) { break; }
		}
		if (newCards.isEmpty())
			return;
		
		// Allows cards to be played/equipped regardless of whose turn and what turn phase it is
		boolean isBattlePhase = GM.isBattlePhase();
		
		Player activePlayer = GM.getActivePlayer();
		GM.setActivePlayer(this);
		if (isBattlePhase)
			GM.moveToOtherPhase();
		if (isComputer()) {
			if (isFaceUp)
				(new DisplayCardsDialog(newCards, name + "'s drawn treasure cards")).setVisible(true);
			AIManager.playHandCards(this, newCards);
		}
		else
			(new InHandDialog(this, newCards)).setVisible(true);
		if (isBattlePhase)
			GM.moveToBattlePhase();
		GM.setActivePlayer(activePlayer);	
	}
	
	/**
	 * Discards the card from where ever it is located in the player's collection.
	 * @param card card to discard
	 */
	public void discard(Card card) {
		if (isCheatingItem(card))
			removeCheat();
		
		if (!hand.remove(card) && !carriedItems.remove(card) && !removeEquipmentItem(card)) {			
			if (raceCards.contains(card))
				discardRaceCard((RaceCard)card);
			else if (classCards.contains(card))
				discardClassCard((ClassCard)card);
			else
				discardHirelingCard();
			
			return;
		}
		
		if (card instanceof DoorCard)
			GM.getDoorDeck().discard((DoorCard)card);
		else
			GM.getTreasureDeck().discard((TreasureCard)card);
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns whether or not the player has the Hireling Card in play.
	 * @return true if the player has the Hireling Card in play; false otherwise
	 */
	public boolean hasHireling() {
		return (hirelingCard != null);
	}
	
	/**
	 * Sets the player's in-play Hireling.
	 * @param hireling Hireling Card to set.
	 */
	public void setHirelingCard(OtherTreasureCard hireling) {
		hirelingCard = hireling;
	}
	
	/**
	 * Returns the player's in-play Hireling Card.
	 * @return the player's in-play Hireling Card; or null if doesn't have one in play
	 */
	public OtherTreasureCard getHirelingCard() {
		return hirelingCard;
	}
	
	/**
	 * Discards the player's in-play Hireling Card and forces play to discard any big items he is now unable to carry.
	 * @return true if the player had an in-play Hireling Card to discard; false otherwise
	 */
	public boolean discardHirelingCard() {
		if (hirelingCard == null)
			return false;
		
		GM.getTreasureDeck().discard(hirelingCard);
		hirelingCard = null;
		
		if (!isDwarf()) {
			LinkedList<Card> bigItems = new LinkedList<Card>();
			Iterator<EquipmentCard> bigItemIter = getBigItems().iterator();
			while (bigItemIter.hasNext()) {
				EquipmentCard bigItem = bigItemIter.next();
				if (cheatingItemCard != bigItem)
					bigItems.add(bigItem);
			}
			
			int maxBigItems = 1;
			if (hirelingCard != null)
				maxBigItems = 2;
			if (bigItems.size() > maxBigItems) {
				if (GM.getActivePlayer() == this) {
					if (!isComputer()) {
						String prompt = "<html>&nbsp;You have one chance now to sell some items.&nbsp;<br>";
						prompt += "&nbsp;If you have more than " + maxBigItems + " big items when you are done selling, they will go to charity.</html>";
						Messenger.warn(prompt, "No Longer A Dwarf");
					}
					
					(new SellItemsDialog(null, this)).setVisible(true);
					
					bigItems.clear();
					bigItemIter = getBigItems().iterator();
					while (bigItemIter.hasNext()) {
						EquipmentCard bigItem = bigItemIter.next();
						if (cheatingItemCard != bigItem)
							bigItems.add(bigItem);
					}
				}				
				
				if (bigItems.size() > maxBigItems) {				
					LoseCardsDialog dialog = new LoseCardsDialog(this, bigItems, bigItems.size() - maxBigItems, "big items");
					dialog.giveToCharity();
					dialog.setVisible(true);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Returns a list of all the cards in the player's hand.
	 * @return all the cards in the player's hand
	 */
	public LinkedList<Card> getHandCards() {
		return hand;
	}
	
	/**
	 * Returns a list of all the Item Cards that the player has in play.
	 * @return all the Item Cards that the player has in play
	 */
	public LinkedList<ItemCard> getCarriedItems() {
		return carriedItems;
	}
	
	/**
	 * Returns a list of all the Equipment Cards that the player has equipped.
	 * @return all the Equipment Cards that the player has equipped
	 */
	public LinkedList<EquipmentCard> getEquippedItems() {
		return equippedItems;
	}
	
	/**
	 * Returns a list of all the Equipment Cards that the player is carrying unequipped.
	 * @return all the Equipment Cards that the player is carrying unequipped
	 */
	public LinkedList<EquipmentCard> getUnequippedItems() {
		return unequippedItems;
	}
	
	/**
	 * Returns a list of all Equipment Cards that the player is carrying equipped or unequipped.
	 * @return all Equipment Cards that the player is carrying
	 */
	public LinkedList<EquipmentCard> getAllEquipment() {
		LinkedList<EquipmentCard> equipmentItems = new LinkedList<EquipmentCard>();
		equipmentItems.addAll(equippedItems);
		equipmentItems.addAll(unequippedItems);
		return equipmentItems;
	}
	
	/**
	 * Adds the Equipment Card to the player's carried, unequipped Items.
	 * @param item the Equipment Card to add
	 */
	public void addUnequippedItem(EquipmentCard item) {
		unequippedItems.add(item);
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Removes the Equipment Card from the player's carried equipment items.
	 * @param item the Equipment Card to removed
	 * @return true if the player was carrying the item; false otherwise (when nothing gets removed)
	 */
	public boolean removeEquipmentItem(Card item) {
		setChanged();
		notifyObservers();
		
		return (equippedItems.remove(item) || unequippedItems.remove(item));
	}
	
	/**
	 * Adds the Equipment Card to the player's list of carried equipment and equips it if possible.
	 * @param equipmentItem the Equipment Card to add and equip.
	 * @return the reason why the card couldn't be equipped; or an empty string if equipped
	 */
	public String equip(EquipmentCard equipmentItem) {
		String unequipReason = equipmentItem.equip(this);
		EquipmentCard cheatingCard = getCheatingItemCard();
		
		// determine if player is able to equip item based on type of equipment and worn equipment
		// ignore any items the player has connected with a cheat card; if applicable
		EquipmentType type = equipmentItem.getEquipmentType();
		if (unequipReason.equals("") && type != EquipmentType.OTHER && equipmentItem != cheatingCard) {
			if (type == EquipmentType.ONE_HAND || type == EquipmentType.TWO_HANDS) {
				int numHandsFull = 0;
				for (EquipmentCard item : equippedItems) {
					if (item != equipmentItem && item != cheatingCard) {
						if (item.getEquipmentType() == EquipmentType.ONE_HAND)
							numHandsFull += 1;
						else if (item.getEquipmentType() == EquipmentType.TWO_HANDS)
							numHandsFull += 2;
					}
				}
				
				if (type == EquipmentType.ONE_HAND && numHandsFull >= 2)
					unequipReason = "Your hands are full.";
				else if (type == EquipmentType.TWO_HANDS && numHandsFull > 0)
					unequipReason = "You don't have two free hands.";
			}
			else {
				for (EquipmentCard item : equippedItems) {
					if (item != equipmentItem && item != cheatingCard) {
						if (type == item.getEquipmentType()) {
							unequipReason = "You are already wearing " + type + ".";
							break;
						}
					}
				}
			}
		}
		
		// Puts the equipment in the correct equipped/unequipped list depending on if it was able to be equipped or not
		if (unequipReason.equals("")) {
			if (unequippedItems.remove(equipmentItem))
				equippedItems.add(equipmentItem);
		}
		else {
			if (equippedItems.remove(equipmentItem))
				unequippedItems.add(equipmentItem);
		}
		
		setChanged();
		notifyObservers();
		
		return unequipReason;
	}
	
	/**
	 * Unequips the carried Equipment Card.
	 * @param item Equipment Card to unequip
	 */
	public void unequip(EquipmentCard item) {
		if (equippedItems.remove(item)) {
			unequippedItems.add(item);
		
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Returns whether or not the player has the given Equipment Card equipped.
	 * @param equipmentItem Equipment Card to check
	 * @return true if the player has this equipment equipped; false otherwise
	 */
	public boolean hasEquipped(EquipmentCard equipmentItem) {
		return equippedItems.contains(equipmentItem);
	}
	
	/**
	 * Returns whether or not the player has the Equipment Card with the given name equipped.
	 * @param id id of the Equipment Card to check
	 * @return true if the player has this equipment equipped
	 */
	public boolean hasEquipped(int id) {
		for (EquipmentCard item : equippedItems)
			if (item.getID() == id)
				return true;
		
		return false;
	}
	
	/**
	 * Returns a list of all carried Equipment and Item Cards.
	 * @return all carried Equipment and Item Cards
	 */
	public LinkedList<TreasureCard> getAllItems() {
		LinkedList<TreasureCard> allItems = new LinkedList<TreasureCard>();
		allItems.addAll(carriedItems);
		allItems.addAll(equippedItems);
		allItems.addAll(unequippedItems);
		return allItems;
	}
	
	/**
	 * Returns a list of all big items carried by the player.
	 * @return all big items carried by the player
	 */
	public LinkedList<EquipmentCard> getBigItems() {
		LinkedList<EquipmentCard> bigItems = new LinkedList<EquipmentCard>();
		for (EquipmentCard item : equippedItems)
			if (item.isBig())
				bigItems.add(item);
		
		for (EquipmentCard item : unequippedItems)
			if (item.isBig())
				bigItems.add(item);
		
		return bigItems;
	}
	
	/**
	 * Returns a list of all sellable Item Cards that the user possesses (in hand or carried).
	 * @return all sellable Item Cards that the user possesses
	 */
	public LinkedList<TreasureCard> getAllValueCards() {
		LinkedList<TreasureCard> valueCards = new LinkedList<TreasureCard>();
		
		Iterator<Card> handIter = hand.iterator();
		while (handIter.hasNext()) {
			Card currentCard = handIter.next();
			if (currentCard instanceof EquipmentCard || currentCard instanceof ItemCard)
				valueCards.add((TreasureCard)currentCard);
		}
		
		valueCards.addAll(carriedItems);
		valueCards.addAll(equippedItems);
		valueCards.addAll(unequippedItems);
		
		return valueCards;
	}

	/**
	 * Return the Equipment Card connected to the player's in-play Cheat Card.
	 * @return the Equipment Card connected to the player's in-play Cheat Card; or null if not using Cheat Card
	 */
	public EquipmentCard getCheatingItemCard() {
		return cheatingItemCard;
	}	
	
	/**
	 * Sets the in-play Cheat Card and the Equipment Card it is tied to.
	 * @param cheat the Cheat Card to set
	 * @param equipment the Equipment Card to be used with the Cheat Card
	 */
	public void setCheatCards(OtherDoorCard cheat, EquipmentCard equipment) {
		cheatCard = cheat;
		cheatingItemCard = equipment;
	}
	
	/**
	 * Returns whether or not the given card is an EquipmentCard tied to a Cheat Card.
	 * @param card card to check
	 * @return true if the given card is an EquipmentCard tied to a Cheat Card; false otherwise
	 */
	public boolean isCheatingItem(Card card) {
		return (card == cheatingItemCard);
	}
	
	/**
	 * Removes and discards the player's in-play Cheat Card and disconnects it from the Equipment Card it was tied to.
	 * Does nothing if player has no in-play Cheat Card.
	 */
	public void removeCheat() {
		cheatingItemCard = null;
		GM.getDoorDeck().discard(cheatCard);
		cheatCard = null;
	}
	
	/**
	 * Processes the death of the player: he loses everything but level, race, and character class and will need
	 * new cards at the beginning of his next turn.
	 */
	public void die() {		
		for (EquipmentCard item : equippedItems)
			if (item.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR)
				removeChickenOnHeadCurse();
		
		// Every other player gets to take one card from the dying player starting from the highest to lowest level players
		Iterator<Player> playerIter = GM.getHighestToLowestLevelPlayers().iterator();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			if (player == this)
				continue;
			
			TakeCardDialog dialog = new TakeCardDialog(this, player, TakeCardDialog.DrawLocation.EITHER, true, false);
			dialog.setVisible(true);
		}
		
		LinkedList<Card> handCards = new LinkedList<Card>();
		handCards.addAll(hand);
		Iterator<Card> cardIter = handCards.iterator();
		while (cardIter.hasNext())
			discard(cardIter.next());
		
		LinkedList<ItemCard> items = new LinkedList<ItemCard>();
		items.addAll(carriedItems);
		for (ItemCard item : items)
			discard(item);
		
		LinkedList<EquipmentCard> equipmentItems = new LinkedList<EquipmentCard>();
		equipmentItems.addAll(equippedItems);
		equipmentItems.addAll(unequippedItems);
		for (EquipmentCard item : equipmentItems)			
			discard(item);
		
		discardHirelingCard();
		needsNewCards = true;
	}
}
