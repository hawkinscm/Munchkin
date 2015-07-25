
package model;

import exceptions.EndGameException;
import exceptions.PlayImmediatelyException;
import gui.ChooseCardDialog;
import gui.ChoosePlayerDialog;
import gui.CurseDialog;
import gui.DisplayCardsDialog;
import gui.HelpMeOutDialog;
import gui.InHandDialog;
import gui.OptionDialog;
import gui.ChooseMonsterDialog;
import gui.RemoveCurseDialog;
import gui.RunDialog;
import gui.components.Messenger;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.DoorCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.OtherTreasureCard;
import model.card.RaceCard;
import model.card.TreasureCard;

import ai.AICardEvaluator;
import ai.AIManager;

/**
 * A Singleton-type class that handles the playing of a card.
 */
public class CardPlayManager {
	// constant variables as text codes for the different stages of running
	private final static String RUN_TEXT = "Run";
	private final static String ESCAPE_TEXT = "Escape";
	private final static String FAIL_TEXT = "Failed Run Away";
	
	// Determines if player playing a card is a computer
	private static boolean isComputerPlayer;
	
	/**
	 * Displays a card play error message in a simple dialog box. 
	 * @param message the error message to display
	 * @return always returns false (IE. the card was not played)
	 */
	private static boolean displayCardPlayError(String message) {
		if (!isComputerPlayer)
			Messenger.error(message, "Illegal Card Play");
		return false;
	}
	
	/**
	 * Attempts to play a Card for the given player and returns whether or not it was played.
	 * @param player the player
	 * @param card the Card to play
	 * @return true if the card was played; false if it wasn't played or couldn't be played
	 * @throws EndGameException if the played card caused a player to win and ended the game
	 */
	public static boolean playCard(Player player, Card card) throws EndGameException {
		isComputerPlayer = player.isComputer();
		
		/**********************
		 * DOOR CARD HANDLERS *
		 **********************/
		// Handle Monster Cards
		if (card instanceof MonsterCard)
			return displayCardPlayError("Monster Cards cannot be played in this manner.");
		
		// Handle Race Cards
		if (card instanceof RaceCard) {
			if (GM.getActivePlayer() != player)
				return displayCardPlayError("You must wait for your own turn to play this.");
			
			RaceCard raceCard = (RaceCard)card;
			
			// Player is not allowed to play a race that he already is
			Iterator<RaceCard> raceCardIter = player.getRaceCards().iterator();
			while (raceCardIter.hasNext()) {
				if (raceCardIter.next().getRace() == raceCard.getRace()) {
					String message = "You are already a";
					if (raceCard.getRace() == Race.ELF)
						message += "n";
					return displayCardPlayError(message + " " + raceCard + ".");
				}
			}
			
			// Player is given option not to play race or to replace a current race
			if (player.getRaceCards().size() == 1 && !player.isHalfBreed()) {
				if (isComputerPlayer)
					GM.getDoorDeck().discard(player.getRaceCards().removeFirst());
				else {
					String message = "Do you want to replace your current Race?";
					if (player.isDwarf()) {
						int bigItemExcess = player.getBigItems().size() - 1;
						if (player.hasHireling())
							bigItemExcess--;
						if (player.getCheatingItemCard() != null && player.getCheatingItemCard().isBig())
							bigItemExcess--;
						if (bigItemExcess > 0)
							message += " (You will lose " + bigItemExcess + " Big Items if you do this!)";
					}
					
					int result = JOptionPane.showConfirmDialog(null, message, "Discard Warning", JOptionPane.YES_NO_CANCEL_OPTION);
					if (result == JOptionPane.YES_OPTION) 
						GM.getDoorDeck().discard(player.getRaceCards().removeFirst());
					else
						return false;
				}
			}
			else if (player.getRaceCards().size() == 2) {
				LinkedList<Card> raceCards = new LinkedList<Card>();
				raceCards.addAll(player.getRaceCards());
				
				String prompt = player.getName() + ", choose a Race Card to replace.";
				if (player.isDwarf()) {
					int bigItemExcess = player.getBigItems().size() - 1;
					if (player.hasHireling())
						bigItemExcess--;
					if (player.getCheatingItemCard() != null && player.getCheatingItemCard().isBig())
						bigItemExcess--;
					if (bigItemExcess > 0)
						prompt += " WARNING: You will lose " + bigItemExcess + " Big Items if you replace your Dwarf card!";
				}
				ChooseCardDialog dialog = new ChooseCardDialog(raceCards, prompt);
				dialog.setVisible(true);
				
				RaceCard replaceRaceCard = (RaceCard)dialog.getSelectedCard();
				if (replaceRaceCard == null)
					return false;
				
				player.getRaceCards().remove(replaceRaceCard);
				GM.getDoorDeck().discard(replaceRaceCard);
			}
			
			player.getHandCards().remove(raceCard);
			player.addRaceCard(raceCard);
			
			return true;
		}
		
		// Handle Class Cards
		if (card instanceof ClassCard) {
			if (GM.getActivePlayer() != player)
				return displayCardPlayError("You must wait for your own turn to play this.");
			
			ClassCard classCard = (ClassCard)card;
			
			// Player is not allowed to play a class that he already is
			Iterator<ClassCard> classCardIter = player.getClassCards().iterator();
			while (classCardIter.hasNext())
				if (classCardIter.next().getCharacterClass() == classCard.getCharacterClass())
					return displayCardPlayError("You are already a " + classCard + ".");
			
			// Player is given option not to play class or to replace a current class
			if (player.getClassCards().size() == 1 && !player.isSuperMunchkin()) {
				if (isComputerPlayer)
					GM.getDoorDeck().discard(player.getClassCards().removeFirst());
				else {
					String message = "Do you want to replace your current Class?";			
					int result = JOptionPane.showConfirmDialog(null, message, "Discard Warning", JOptionPane.YES_NO_CANCEL_OPTION);
					if (result == JOptionPane.YES_OPTION)
						GM.getDoorDeck().discard(player.getClassCards().removeFirst());
					else
						return false;
				}
			}
			else if (player.getClassCards().size() == 2) {
				LinkedList<Card> classCards = new LinkedList<Card>();
				classCards.addAll(player.getClassCards());
				
				String prompt = player.getName() + ", choose a Class Card to replace.";				
				ChooseCardDialog dialog = new ChooseCardDialog(classCards, prompt);
				dialog.setVisible(true);
				
				ClassCard replaceClassCard = (ClassCard)dialog.getSelectedCard();
				if (replaceClassCard == null)
					return false;
				
				player.getClassCards().remove(replaceClassCard);
				GM.getDoorDeck().discard(replaceClassCard);
			}
			
			player.getHandCards().remove(classCard);
			player.addClassCard(classCard);
			
			return true;
		}
		
		// Handles Curse Cards
		if (card instanceof CurseCard) {
			String reason = player.getName() + ", choose someone to curse.";
			ChoosePlayerDialog playerDialog = new ChoosePlayerDialog(GM.getPlayers(), reason);
			playerDialog.setVisible(true);
			Player victim = playerDialog.getSelectedPlayer();
			if (victim == null)
				return false;
			
			player.getHandCards().remove(card);
			CurseDialog curseDialog = new CurseDialog(victim, (CurseCard)card, false);
			curseDialog.setVisible(true);
			
			return true;
		}
		
		// Handles Monster Enhancer Cards
		if (card instanceof MonsterEnhancerCard) {
			if (!GM.isBattlePhase())
				return displayCardPlayError("This card can only be played during a battle.");
			else
				System.err.println("Monster Enhancer card played during battle went to wrong CardPlayManager:playCard method!");
			
			return false;
		}
		
		// Handles Other Door Cards
		if (card instanceof OtherDoorCard) {
			OtherDoorCard otherDoorCard = (OtherDoorCard)card;
			
			// Handles Cheat Card
			if (otherDoorCard.getID() == Card.OD_CHEAT) {
				if (GM.isBattlePhase())
					return displayCardPlayError("This cannot be used during battle.");
				else if (GM.getActivePlayer() != player)
					return displayCardPlayError("You must wait for your own turn to use this.");
				
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(player.getEquippedItems());
				cards.addAll(player.getUnequippedItems());
				Iterator<Card> cardIter = player.getHandCards().iterator();
				while (cardIter.hasNext()) {
					Card current = cardIter.next();
					if (current instanceof EquipmentCard)
						cards.add(current);
				}
				if (cards.isEmpty())
					return displayCardPlayError("You have no equipment items to cheat with.");
				
				String prompt = player.getName() + ", choose an item to cheat with.";
				ChooseCardDialog dialog = new ChooseCardDialog(cards, prompt);
				dialog.setVisible(true);
				EquipmentCard cheatingItem = (EquipmentCard)dialog.getSelectedCard();
				if (cheatingItem == null)
					return false;
				
				player.getHandCards().remove(otherDoorCard);
				player.setCheatCards(otherDoorCard, cheatingItem);
				
				if (player.getHandCards().remove(cheatingItem))
					player.addUnequippedItem(cheatingItem);				
				player.equip(cheatingItem);
			}
			// Handles Half-Breed Card
			else if (otherDoorCard.getID() == Card.OD_HALF_BREED_1 || otherDoorCard.getID() == Card.OD_HALF_BREED_2) {
				if (GM.getActivePlayer() != player)
					return displayCardPlayError("You must wait for your own turn to play this.");
				
				if (player.isHalfBreed())
					return displayCardPlayError("You are already using a Half-Breed card.");
				else if (player.getRaceCards().isEmpty())
					return displayCardPlayError("You must have a Race Card in play before using this card.");
				else {
					player.getHandCards().remove(otherDoorCard);
					player.setHalfBreedCard(otherDoorCard);
				}
			}
			// Handles Super Munchkin Card
			else if (otherDoorCard.getID() == Card.OD_SUPER_MUNCHKIN_1 || otherDoorCard.getID() == Card.OD_SUPER_MUNCHKIN_2) {
				if (GM.getActivePlayer() != player)
					return displayCardPlayError("You must wait for your own turn to play this.");
				
				if (player.isSuperMunchkin())
					return displayCardPlayError("You are already using a Super Munchkin card.");
				else if (player.getClassCards().isEmpty())
					return displayCardPlayError("You must have a Class Card in play before using this card.");
				else {
					LinkedList<Card> classCards = new LinkedList<Card>();
					Iterator<Card> cardIter = player.getHandCards().iterator();
					while (cardIter.hasNext()) {
						Card current = cardIter.next();
						if (current instanceof ClassCard)
							classCards.add(current);
					}
					if (classCards.isEmpty())
						return displayCardPlayError("You must have a Class Card in hand before using this card.");
					
					ClassCard newClassCard = null;
					if (isComputerPlayer) {
						ClassCard bestClassCard = null;
						int bestClassValue = 0;
						for (Card handCard : player.getHandCards()) {
							if (handCard instanceof ClassCard) {
								ClassCard currentClass = (ClassCard)handCard;
								if (player.isClass(currentClass.getCharacterClass()))
									continue;
								
								int classValue = AICardEvaluator.getCardValueToPlayer(currentClass, player, player.getHandCards());
								if (bestClassCard == null || classValue > bestClassValue) {
									bestClassCard = currentClass;
									bestClassValue = classValue;
								}
							}
						}
						
						if (bestClassCard != null && bestClassValue > 0)
							newClassCard = bestClassCard;
					}
					else {
						String prompt = player.getName() + ", choose a Class Card to become.";
						ChooseCardDialog dialog = new ChooseCardDialog(classCards, prompt);
						dialog.setVisible(true);
					
						newClassCard = (ClassCard)dialog.getSelectedCard();
					}
					if (newClassCard == null)
						return false;
					
					player.setSuperMunchkinCard(otherDoorCard);
					if (playCard(player, newClassCard))
						player.getHandCards().remove(otherDoorCard);
					else {
						player.setSuperMunchkinCard(null);
						return false;
					}
				}
			}
			// any other Other Door Cards can only be played during a battle
			else {
				if (!GM.isBattlePhase())
					return displayCardPlayError("This card can only be played during a battle.");
				else
					System.err.println("\"" + card + "\" card played during battle went to wrong CardPlayManager:playCard method!");
					
				return false;
			}		
			
			return true;
		}
		
		
		/**************************
		 * TREASURE CARD HANDLERS *
		 **************************/
		// Handles Equipment Cards
		if (card instanceof EquipmentCard) {
			if (GM.getActivePlayer() != player)
				return displayCardPlayError("You must wait for your own turn to carry/equip this.");
			
			EquipmentCard equipmentCard = (EquipmentCard)card;
			if (canCarryItem(player, equipmentCard)) {
				if (isComputerPlayer) {
					player.getHandCards().remove(equipmentCard);
					player.addUnequippedItem(equipmentCard);
					if (!GM.isBattlePhase())
						player.equip((EquipmentCard)card);
				}
				else if (GM.isBattlePhase()) {
					player.getHandCards().remove(equipmentCard);
					player.addUnequippedItem(equipmentCard);
				}
				else {
					String prompt = player.getName() + ", would you like to equip the " + equipmentCard + " if possible?";
					int result = JOptionPane.showConfirmDialog(null, prompt, "New Equipment Item", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.CLOSED_OPTION)
						return false;
					
					player.getHandCards().remove(equipmentCard);
					player.addUnequippedItem(equipmentCard);
					
					if (result == JOptionPane.YES_OPTION) {
						String unequipReason = player.equip((EquipmentCard)card);
						if (!unequipReason.equals(""))
							Messenger.error(unequipReason, "Equip Item");
					}
				}
			}
			else
				return displayCardPlayError("You can't carry another big item.");
			
			return true;
		}
		
		// Handles Item Cards
		if (card instanceof ItemCard) {
			ItemCard itemCard = (ItemCard)card;
			
			if (player.getHandCards().contains(itemCard)) {
				if (player.isComputer() || player.getPlayerType() == PlayerType.TEST) {
					if (GM.getActivePlayer() == player) {
						player.getHandCards().remove(itemCard);
						player.getCarriedItems().add(itemCard);
						if (isComputerPlayer) {
							if (itemCard.getID() == Card.I_WAND_OF_DOWSING)
								playCard(player, itemCard);
							else if (itemCard.getID() == Card.I_WISHING_RING_1 || itemCard.getID() == Card.I_WISHING_RING_2)
								playCard(player, itemCard);
						}
						return true;
					}
					else
						return false;
				}
				
				String prompt = "Would you like to put the " + itemCard + " with your carried items or use it as an item?";
				OptionDialog dialog = new OptionDialog("Play Item Card", prompt, "Carried Items", "Use It");
				dialog.setVisible(true);
				if (dialog.getChoice() == 0)
					return false;
				
				if (dialog.getChoice() == 1) {
					if (GM.getActivePlayer() != player)
						return displayCardPlayError("You must wait for your own turn to move this card to carried items.");
					
					player.getHandCards().remove(card);
					player.getCarriedItems().add(itemCard);
				
					return true;
				}				
			}			
			else if (!player.getCarriedItems().contains(itemCard)) {
				player.getCarriedItems().add(itemCard);
				if (isComputerPlayer)
					playCard(player, card);
				return true;
			}
			
			if (card.getID() == Card.I_LOADED_DIE)
				return displayCardPlayError("This can only be played after a dice roll.");
			// Handles Wand of Dowsing Card
			else if (itemCard.getID() == Card.I_WAND_OF_DOWSING) {
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(GM.getTreasureDeck().getDiscardPile());
				cards.addAll(GM.getDoorDeck().getDiscardPile());
				if (cards.isEmpty())
					return displayCardPlayError("There are no discarded cards to take.");
				
				Card selectedCard = null;
				if (isComputerPlayer) {
					Card bestCard = null;
					int bestValue = 0;
					LinkedList<Card> allDiscards = new LinkedList<Card>();
					allDiscards.addAll(GM.getDoorDeck().getDiscardPile());
					allDiscards.addAll(GM.getTreasureDeck().getDiscardPile());
					for (Card discard : allDiscards) {
						int currentCardValue = AICardEvaluator.getCardValueToPlayer(discard, player, player.getHandCards());
						if (bestCard == null || currentCardValue > bestValue) {
							bestCard = discard;
							bestValue = currentCardValue;
						}
					}
					
					if (bestCard != null && bestValue > AICardEvaluator.getCardValueToPlayer(itemCard, player, player.getHandCards()))
						selectedCard = bestCard;
				}
				else {
					String prompt = player.getName() + ", choose a discarded card to take.";
					ChooseCardDialog dialog = new ChooseCardDialog(cards, prompt);
					dialog.setVisible(true);		
				
					selectedCard = dialog.getSelectedCard();
				}
				
				if (selectedCard == null)
					return false;
				
				if (isComputerPlayer) {
					LinkedList<Card> involvedCards = new LinkedList<Card>();
					involvedCards.add(card);
					involvedCards.add(selectedCard);
					String title = player + " used the Wand of Dowsing to get the " + selectedCard + " card";
					(new DisplayCardsDialog(involvedCards, title)).setVisible(true);
				}
				
				if (selectedCard instanceof DoorCard) {
					GM.getDoorDeck().getDiscardPile().remove(selectedCard);
					GM.getDoorDeck().discard((DoorCard)selectedCard);
					try {
						GM.getDoorDeck().takeDiscard();
					}
					catch (PlayImmediatelyException ex) {
						player.discard(itemCard);
						return true;
					}
				}
				else {
					GM.getTreasureDeck().getDiscardPile().remove(selectedCard);
					GM.getTreasureDeck().discard((TreasureCard)selectedCard);
					try {
						GM.getTreasureDeck().takeDiscard();
					}
					catch (PlayImmediatelyException ex) {
						LinkedList<Card> newCards = new LinkedList<Card>();
						
						Iterator<TreasureCard> treasureIter = GM.getTreasureDeck().getHoardCards(player, false).iterator();
						while (treasureIter.hasNext()) {
							Card newCard = treasureIter.next();
							player.addCard(newCard);
							newCards.add(newCard);
						}
						
						Player activePlayer = GM.getActivePlayer();
						GM.setActivePlayer(player);
						if (isComputerPlayer) 
							AIManager.playHandCards(player, newCards);
						else
							(new InHandDialog(player, newCards)).setVisible(true);
						GM.setActivePlayer(activePlayer);
						
						player.discard(itemCard);
						return true; 
					}
				}
				
				player.addCard(selectedCard);
				Player activePlayer = GM.getActivePlayer();
				GM.setActivePlayer(player);
				if (isComputerPlayer)
					AIManager.playHandCard(player, selectedCard);
				else
					(new InHandDialog(player, selectedCard)).setVisible(true);
				GM.setActivePlayer(activePlayer);
				
				player.discard(itemCard);				
				return true;
			}
			// Handles Wishing Ring Card
			else if(itemCard.getID() == Card.I_WISHING_RING_1 || itemCard.getID() == Card.I_WISHING_RING_2) {
				RemoveCurseDialog dialog = new RemoveCurseDialog(player);
				dialog.setVisible(true);
				if (dialog.removedCurse()) {
					player.discard(itemCard);
					return true;
				}
				
				return false;
			}
			
			if (!GM.isBattlePhase()) 				
				return displayCardPlayError("You can only use this card during a battle.");
			
			System.err.println("Item Card played during battle went to wrong CardPlayManager:playCard method!");
			return displayCardPlayError("Unexpected error. Report problem to designer. Use \"Play Battle Card\" menu item instead.");
		}
		
		// Handles Go Up a Level Cards
		if (card instanceof GoUpLevelCard) {
			// Check for and handle special Go Up a Level Cards
			if (card.getID() == Card.GUL_KILL_THE_HIRELING) {
				Iterator<Player> playerIter = GM.getPlayers().iterator();
				boolean isHirelingFound = false;
				while (playerIter.hasNext()) {
					if (playerIter.next().discardHirelingCard())
						isHirelingFound = true;
				}
				
				if (!isHirelingFound)
					return displayCardPlayError("The hireling is not in play.");
			}
			else if (card.getID() == Card.GUL_MOW_THE_BATTLEFIELD) {
				if (!GM.isAfterBattle())
					return displayCardPlayError("This card can only be played after a battle, after all won treasures are taken, and before the next player's turn");
			}
			else if (card.getID() == Card.GUL_WHINE_AT_THE_GM) {
				LinkedList<Player> highestLevelPlayers = GM.getHighestLevelPlayers();
				if (highestLevelPlayers.contains(player)) {
					if (highestLevelPlayers.size() == 1)
						return displayCardPlayError("You have the highest level and therefore cannot play this card.");
					else
						return displayCardPlayError("You are tied for the highest level and therefore cannot play this card.");
				}
			}
			
			player.discard(card);
			player.goUpLevel(false);
			
			return true;
		}		
		
		// Handles Other Treasure Cards
		if (card instanceof OtherTreasureCard) {
			OtherTreasureCard treasureCard = (OtherTreasureCard)card;
			// Handles Steal a Level Card
			if (treasureCard.getID() == Card.OT_STEAL_A_LEVEL) {
				LinkedList<Player> players = new LinkedList<Player>();
				Iterator<Player> playerIter = GM.getPlayers().iterator();
				while (playerIter.hasNext()) {
					Player current = playerIter.next();
					if (current != player && current.getLevel() > 1)
						players.add(current);
				}
				if (players.isEmpty())
					return displayCardPlayError("There are no players with stealable levels.");
				
				String prompt = player.getName() + ", choose a player to steal a level from.";
				ChoosePlayerDialog dialog = new ChoosePlayerDialog(players, prompt);
				dialog.setVisible(true);
				
				Player victim = dialog.getSelectedPlayer();
				if (victim == null)
					return false;
				
				player.discard(treasureCard);
				victim.goDownLevel();
				player.goUpLevel(false);
			}
			// Handles Hireling Card
			else if (treasureCard.getID() == Card.OT_HIRELING) {
				if (GM.isBattlePhase())
					return displayCardPlayError("You cannot play this card during battle.");
				else if (GM.getActivePlayer() != player)
					return displayCardPlayError("You must wait for your own turn to play this card.");
				
				player.getHandCards().remove(treasureCard);
				player.setHirelingCard(treasureCard);
			}
			
			return true;
		}
		
		return displayCardPlayError("I've not been programmed to handle this card: " + card + "(" + card.getID() + ").");
	}
	
	/**
	 * Attempts to play a Card during battle for the given player and returns whether or not it was played.
	 * @param player the player
	 * @param card the Card to play
	 * @param battle the currently ongoing battle
	 * @return true if the card was played; false if it wasn't played or couldn't be played
	 * @throws EndGameException if the played card caused a player to win and ended the game
	 */
	public static boolean playCard(Player player, Card card, Battle battle) throws EndGameException {		
		if (battle == null)
			return playCard(player, card);
		
		isComputerPlayer = player.isComputer();
		
		// Handles Monster Enhancer Cards during battle
		if (card instanceof MonsterEnhancerCard) {
			MonsterCard monster = null;
			
			if (battle.getMonsterCount() == 1)
				monster = battle.getMonster(0);
			else {
				String message = "Add enhancer to which monster?";
				ChooseMonsterDialog dialog = new ChooseMonsterDialog(player, battle, message);
				dialog.setVisible(true);
				monster = dialog.getSelectedMonster();
			}
			if (monster != null) {
				player.getHandCards().remove(card);
				battle.addMonsterEnhancer(monster, (MonsterEnhancerCard)card);
				return true;
			}
			
			return false;
		}
		
		// Handles Other Door Cards during battle
		if (card instanceof OtherDoorCard) {
			// Handles Help Me Out Here Card
			if (card.getID() == Card.OD_HELP_ME_OUT_HERE) {
				if (battle.activePlayer != player && battle.helper != player)
					return displayCardPlayError("This card can only be played by someone in battle.");
				
				for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++)
					if (battle.getMonster(monsterIndex).getID() == Card.M_GHOULFIENDS)
						return displayCardPlayError("You cannot use equipment in this battle.");
				
				if (battle.getPlayersLevel() > battle.getMonstersLevel())
					return displayCardPlayError("You can only play this card when you can't win the battle.");
				else if (battle.activePlayer.isWarrior() || (battle.isHelper() && battle.helper.isWarrior())) {
					if (battle.getPlayersLevel() == battle.getMonstersLevel())
						return displayCardPlayError("You can only play this card when you can't win the battle.");
				}
				
				HelpMeOutDialog dialog = new HelpMeOutDialog(battle, player);
				dialog.setVisible(true);
				if (dialog.tookItem())
					player.discard(card);
				else
					return false;
			}
			// Handles Illusion Card
			else if (card.getID() == Card.OD_ILLUSION) {
				LinkedList<Card> monsters = new LinkedList<Card>();
				Iterator<Card> cardIter = player.getHandCards().iterator();
				while (cardIter.hasNext()) {
					Card current = cardIter.next();
					if (current instanceof MonsterCard)
						monsters.add(current);
				}
				if (monsters.isEmpty())
					return displayCardPlayError("You must have a Monster Card in hand before using this card.");
				
				String prompt = player.getName() + ", choose the monster that will replace a current monster.";
				ChooseCardDialog replacerDialog = new ChooseCardDialog(monsters, prompt);
				replacerDialog.setVisible(true);
				
				MonsterCard newMonster = (MonsterCard)replacerDialog.getSelectedCard();
				if (newMonster == null)
					return false;
				
				MonsterCard oldMonster = null;
				if (battle.getMonsterCount() == 1)
					oldMonster = battle.getMonster(0);
				else {
					String reason = "Choose a monster to replace.";
					ChooseMonsterDialog replaceeDialog = new ChooseMonsterDialog(player, battle, reason);
					replaceeDialog.setVisible(true);				
					oldMonster = (MonsterCard)replaceeDialog.getSelectedMonster();
				}
				if (oldMonster == null)
					return false;
				
				battle.replaceMonster(oldMonster, newMonster);
				
				player.getHandCards().remove(newMonster);
				player.discard(card);
			}
			// Handles Mate Card
			else if (card.getID() == Card.OD_MATE) {
				MonsterCard monster = null;
				if (battle.getMonsterCount() == 1)
					monster = battle.getMonster(0);
				else {
					String message = "Add a Mate to which monster?";
					ChooseMonsterDialog dialog = new ChooseMonsterDialog(player, battle, message);
					dialog.setVisible(true);
					monster = dialog.getSelectedMonster();
				}
				if (monster == null)
					return false;
				
				player.getHandCards().remove(card);
				battle.addMonsterEnhancer(monster, (OtherDoorCard)card);
			}
			// Handles Out to Lunch Card
			else if (card.getID() == Card.OD_OUT_TO_LUNCH) {
				battle.leaveTreasuresBehind();
				battle.addTreasures(2);
				battle.endBattle();
				
				player.discard(card);
			}
			// Handles Wandering Monster Card
			else if (card.getID() == Card.OD_WANDERING_MONSTER_1 || card.getID() == Card.OD_WANDERING_MONSTER_2) {
				LinkedList<Card> monsters = new LinkedList<Card>();
				Iterator<Card> cardIter = player.getHandCards().iterator();
				while (cardIter.hasNext()) {
					Card current = cardIter.next();
					if (current instanceof MonsterCard)
						monsters.add(current);
				}
				if (monsters.isEmpty())
					return displayCardPlayError("You must have a Monster Card in hand before using this card.");
				
				String prompt = player.getName() + ", choose a monster to add to the battle.";
				ChooseCardDialog dialog = new ChooseCardDialog(monsters, prompt);
				dialog.setVisible(true);
				
				MonsterCard newMonster = (MonsterCard)dialog.getSelectedCard();
				if (newMonster == null)
					return false;
				
				battle.addMonster(newMonster);
				
				player.getHandCards().remove(newMonster);
				player.discard(card);
			}
			// all other Other Door Cards can be handled by the non-battle playCard method 
			else
				return CardPlayManager.playCard(player, card);
			
			return true;
		}
		
		// Handles Item Cards during battle
		if (card instanceof ItemCard) {
			if (!player.isComputer() && player.getPlayerType() != PlayerType.TEST) {
				String optionsPrompt = "Would you like to put the " + card + " with your carried items or use it as an item?";
				OptionDialog optionsDialog = new OptionDialog("Play Item Card", optionsPrompt, "Carried Items", "Use It");
				optionsDialog.setVisible(true);
				if (optionsDialog.getChoice() == 0)
					return false;
				
				if (optionsDialog.getChoice() == 1) {
					if (GM.getActivePlayer() != player)
						return displayCardPlayError("You must wait for your own turn to move this card to carried items.");
					
					player.getHandCards().remove(card);
					player.getCarriedItems().add((ItemCard)card);
				
					return true;
				}				
			}
			
			if (card.getID() == Card.I_FLASK_OF_GLUE)
				return displayCardPlayError("This can only be played after a successful escape.");
			else if (card.getID() == Card.I_INVISIBILITY_POTION) 
				return displayCardPlayError("This can only be played after a failed escape.");
			else if (card.getID() == Card.I_INSTANT_WALL) 
				return displayCardPlayError("This can only be played after deciding to run.");
			else if (card.getID() == Card.I_LOADED_DIE)
				return displayCardPlayError("This can only be played after a dice roll.");
			// Handles Friendship Potion Card
			else if (card.getID() == Card.I_FRIENDSHIP_POTION) {
				battle.leaveTreasuresBehind();
				battle.endBattle();
				player.discard(card);
				return true;
			}
			// Handles Magic Lamp Card
			else if (card.getID() == Card.I_MAGIC_LAMP_1 || card.getID() == Card.I_MAGIC_LAMP_2) {
				if (player != battle.activePlayer && player != battle.helper)
					return displayCardPlayError("This card can only be played by someone in battle.");
				
				MonsterCard monster = null;
				if (battle.getMonsterCount() == 1)
					monster = battle.getMonster(0);
				else {
					String message = "Choose a monster to vanish.";
					ChooseMonsterDialog dialog = new ChooseMonsterDialog(player, battle, message);
					dialog.setVisible(true);
					monster = dialog.getSelectedMonster();
				}
				if (monster != null) {
					for (DoorCard enhancerCard : battle.getMonsterEnhancers(monster)) {
						if (enhancerCard.getID() == Card.OD_MATE) {
							int result = JOptionPane.showConfirmDialog(null, "This will only vanish the monster's mate. Are you sure you want to continue?", "Charm Monster", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
							if (result == JOptionPane.YES_OPTION)
								break;
							else
								return false;
						}
					}
					
					player.discard(card);
					battle.discardMonster(monster, false);
					return true;
				}
				
				return false;
			}
			// Handles Pollymorph Potion Card
			else if (card.getID() == Card.I_POLLYMORPH_POTION) {
				MonsterCard monster = null;
				if (battle.getMonsterCount() == 1)
					monster = battle.getMonster(0);
				else {
					String message = "Choose a monster to morph.";
					ChooseMonsterDialog dialog = new ChooseMonsterDialog(player, battle, message);
					dialog.setVisible(true);
					monster = dialog.getSelectedMonster();
				}
				if (monster != null) {
					for (DoorCard enhancerCard : battle.getMonsterEnhancers(monster)) {
						if (enhancerCard.getID() == Card.OD_MATE) {
							int result = JOptionPane.showConfirmDialog(null, "This will only morph the monster's mate. Are you sure you want to continue?", "Charm Monster", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
							if (result == JOptionPane.YES_OPTION)
								break;
							else
								return false;
						}
					}
					
					player.discard(card);
					battle.discardMonster(monster, false);
					return true;
				}
				
				return false;
			}
			// Handles Transferral Potion Card
			else if (card.getID() == Card.I_TRANSFERRAL_POTION) {
				LinkedList<Player> players = new LinkedList<Player>();
				players.addAll(GM.getPlayers());
				players.remove(battle.activePlayer);
				if (battle.isHelper())
					players.remove(battle.helper);
				
				String prompt = player.getName() + ", choose a player to take " + battle.activePlayer.getName() + "'s place in battle.";
				ChoosePlayerDialog dialog = new ChoosePlayerDialog(players, prompt);
				dialog.setVisible(true);
				
				Player victim = dialog.getSelectedPlayer();
				if (victim == null)
					return false;
					
				battle.replaceActivePlayer(victim);
				player.discard(card);
				
				GM.setCanLootRoom();
				return true;
			}
			// the non-battle playCards method can handle these cards 
			else if (card.getID() == Card.I_WAND_OF_DOWSING)
				return playCard(player, card);
			else if (card.getID() == Card.I_WISHING_RING_1 || card.getID() == Card.I_WISHING_RING_2)
				return playCard(player, card);
			// Handles the Doppleganger Card
			else if (card.getID() == Card.I_DOPPLEGANGER) {
				if (battle.isHelper())
					return displayCardPlayError("There is a helper in this battle; this card cannot be played.");
				else if (player != battle.activePlayer)
					return displayCardPlayError("Only the active player in battle can use this card.");
				
				player.getHandCards().remove(card);
				player.getCarriedItems().remove(card);
				battle.addPlayerItemCard((ItemCard)card);
				return true;
			}			
			
			// Choose a side to play the Item Card on.
			String prompt = player.getName() + ", choose a side to benefit from this item.";
			OptionDialog dialog = new OptionDialog("Use Item", prompt, "Player", "Monster");
			dialog.setVisible(true);
			if (dialog.getChoice() == 1) {
				player.getHandCards().remove(card);
				player.getCarriedItems().remove(card);
				battle.addPlayerItemCard((ItemCard)card);
			}
			else if (dialog.getChoice() == 2) {
				player.getHandCards().remove(card);
				player.getCarriedItems().remove(card);
				battle.addMonsterItemCard((ItemCard)card);
			}
			else 
				return false;
			
			return true;
		}
		
		// use the non-battle playCard method to play all other Cards
		return CardPlayManager.playCard(player, card);
	}
	
	/**
	 * Attempts to play a Card while a player is trying to run away and returns whether or not it was played.
	 * @param player the player playing the Card
	 * @param card the Card to play
	 * @param pursuer the Monster pursuing the player (the monster the player is currently running from)
	 * @param runDialog reference to the RunDialog controlling the player's attempt to run
	 * @return true if the card was played; false if it wasn't played or couldn't be played
	 * @throws EndGameException if the played card caused a player to win and ended the game
	 */
	public static boolean playCard(Player player, Card card, MonsterCard pursuer, RunDialog runDialog) throws EndGameException {
		isComputerPlayer = player.isComputer();
		
		// Handles Monster Enhancer Cards played while a player is attempting to run away
		if (card instanceof MonsterEnhancerCard)
			return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
		
		// Handles Other Door Cards played while a player is attempting to run away
		if (card instanceof OtherDoorCard) {
			if (card.getID() == Card.OD_HELP_ME_OUT_HERE)
				return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
			else if (card.getID() == Card.OD_ILLUSION)
				return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
			else if (card.getID() == Card.OD_MATE)
				return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
			else if (card.getID() == Card.OD_OUT_TO_LUNCH)
				return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
			else if (card.getID() == Card.OD_WANDERING_MONSTER_1 || card.getID() == Card.OD_WANDERING_MONSTER_2)
				return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
			else
				return CardPlayManager.playCard(player, card);
		}
		
		// Handles Item Cards played while a player is attempting to run away
		if (card instanceof ItemCard) {
			// Handles Flask of Glue Card
			if (card.getID() == Card.I_FLASK_OF_GLUE) {
				if (runDialog.getRunText().endsWith(ESCAPE_TEXT)) {
					runDialog.setRunText(RUN_TEXT);
					
					player.discard(card);
					return true;
				}	
				else
					return displayCardPlayError("This can only be played after a successful escape.");
			}
			// Handles Invisibility Potion Card
			else if (card.getID() == Card.I_INVISIBILITY_POTION) {
				if (runDialog.getRunText().endsWith(FAIL_TEXT)) {
					if (player != runDialog.getCurrentPlayer())
						return displayCardPlayError("This can only used by the fleeing player.");
					
					if (pursuer.getID() == Card.M_CRABS || pursuer.getID() == Card.M_FLOATING_NOSE)
						return displayCardPlayError("This does not work on " + pursuer);
					
					runDialog.setRunText(ESCAPE_TEXT);
					player.discard(card);
					return true;
				}
				return displayCardPlayError("This can only be played after a failed escape.");
			}
			// Handles Instant Wall Card
			else if (card.getID() == Card.I_INSTANT_WALL) {
				if (runDialog.getRunText().endsWith(FAIL_TEXT))
					return displayCardPlayError("This cannot be played after a failed escape.");
				else {
					if (runDialog.getBattle().isHelper()) {
						if (player.isComputer()) {
							if (player.getPlayerType() == PlayerType.COMPUTER_EASY) {
								LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
								Player otherPlayer;
								if (player == runDialog.getBattle().activePlayer)
									otherPlayer = runDialog.getBattle().helper;
								else
									otherPlayer = runDialog.getBattle().activePlayer;
								
								// Don't help player if he is in first or second place
								if (rankedPlayers.indexOf(otherPlayer) < 2)
									runDialog.escapeAll(false);
								else
									runDialog.escapeAll(true);
							}
							else
								runDialog.escapeAll(false);
						}
						else {
							String message = "Provide escape for the current runner " + runDialog.getCurrentPlayer().getName() + " or for both players?";
							OptionDialog dialog = new OptionDialog("Install Wall Option", message, "Current", "Both");
							dialog.setVisible(true);
							
							int choice = dialog.getChoice();
							if (choice == 1) {
								runDialog.escapeAll(false);
							}
							else if (choice == 2) {
								runDialog.escapeAll(true);
							}
							else
								return false;
						}
					}
					else					
						runDialog.escapeAll(true);
					
					player.discard(card);
					return true;
				}					
			}
			// Handles Friendship Potion Card
			else if (card.getID() == Card.I_FRIENDSHIP_POTION) {
				if (runDialog.getRunText().endsWith(FAIL_TEXT))
					return displayCardPlayError("This cannot be played after a failed escape.");
				else
					return playCard(player, card, runDialog.getBattle());
			}
			// Handles Magic Lamp Card
			else if (card.getID() == Card.I_MAGIC_LAMP_1 || card.getID() == Card.I_MAGIC_LAMP_2)
				return playCard(player, card, runDialog.getBattle());
			// Handles Pollymorph Potion Card
			else if (card.getID() == Card.I_POLLYMORPH_POTION) {
				if (runDialog.getRunText().endsWith(FAIL_TEXT))
					return displayCardPlayError("This cannot be played after a failed escape.");
				else
					return playCard(player, card, runDialog.getBattle());
			}
			// Handles Wand of Dowsing Card
			else if (card.getID() == Card.I_WAND_OF_DOWSING) 
				return playCard(player, card);
			// Handles Wishing Ring Cards
			else if (card.getID() == Card.I_WISHING_RING_1 || card.getID() == Card.I_WISHING_RING_2)
				return playCard(player, card);
			// All other Item Cards cannot be used while player is trying to run away
			else
				return displayCardPlayError("This card cannot be used after the battle players have decided to run.");
		}
		
		// All other cards can be played using the non-battle playCard method
		return CardPlayManager.playCard(player, card);
	}
	
	/**
	 * Returns whether or not a player can carry a specified item.
	 * @param player the player to check
	 * @param equipmentCard the specified item
	 * @return true if the player can carry the specified item; false otherwise
	 */
	public static boolean canCarryItem(Player player, EquipmentCard equipmentCard) {
		if (equipmentCard.isBig() && equipmentCard != player.getCheatingItemCard() && !player.isDwarf()) {
			int bigItemCount = 0;
			for (EquipmentCard item : player.getAllEquipment())
				if (item != player.getCheatingItemCard() && item.isBig())
					bigItemCount++;
			
			if (bigItemCount > 1 || (bigItemCount == 1 && !player.hasHireling()))
				return false;
		}
		
		return true;
	}
}
