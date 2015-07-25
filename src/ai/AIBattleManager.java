package ai;

import java.util.Iterator;
import java.util.LinkedList;

import ai.BattleAction.ActionType;
import ai.BattleMonsterInfo.BattleMonster;

import model.Battle;
import model.CardPlayManager;
import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.DoorCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.RaceCard;
import model.card.TreasureCard;
import exceptions.EndGameException;
import gui.AskHelpDialog;
import gui.components.Messenger;

/**
 * Class for evaluating AI battle decisions.
 */
public class AIBattleManager {
	/**
	 * Decides and performs the best battle actions for the given player and returns whether or not he did anything
	 * @param battle the currently on-going battle
	 * @param player player to check
	 * @param playerWinMargin the number of levels that the players in the battle are currently winning by; can be negative
	 * @return true if the player changes something; false otherwise
	 * @throws EndGameException thrown when the game has been won
	 */
	public static boolean makeBattleDecisions(Battle battle, Player player, int playerWinMargin) throws EndGameException {
		BattleMonsterInfo monsterInfo = new BattleMonsterInfo(battle, player);
		
		// Determine what to do based on who the player is and whether the battle will currently be won or lost.
		if (player == battle.activePlayer) {
			// Decide if helper needs to be stopped so that he will not unacceptably win the game.
			if (battle.isHelper()) {
				int resultingLevel = battle.activePlayer.getLevel() + monsterInfo.getPlayerWinLevels(battle.activePlayer);
				boolean activePlayerWillWinGame = (resultingLevel >= 10);
				resultingLevel = battle.helper.getLevel() + monsterInfo.getPlayerWinLevels(battle.helper);
				boolean helperWillWinGame = (resultingLevel >= 10);
				
				if (helperWillWinGame && (!activePlayerWillWinGame || player.getPlayerType() == PlayerType.COMPUTER_HARD)) {
					if (playerWinMargin > 0)
						return tryToStopWinningBattle(battle, monsterInfo, player, playerWinMargin);
					else
						return false;
				}
			}
			
			if (playerWinMargin > 0)
				return increaseBattleRewards(battle, monsterInfo, playerWinMargin);
			else
				return tryToWinBattle(battle, monsterInfo, player, 1 - playerWinMargin);
		}
		else if (player == battle.helper) {
			// Decide if active player needs to be stopped so that he will not unacceptably win the game.
			int resultingLevel = battle.activePlayer.getLevel() + monsterInfo.getPlayerWinLevels(battle.activePlayer);
			boolean activePlayerWillWinGame = (resultingLevel >= 10);
			resultingLevel = battle.helper.getLevel() + monsterInfo.getPlayerWinLevels(battle.helper);
			boolean helperWillWinGame = (resultingLevel >= 10);
			
			if (activePlayerWillWinGame && (!helperWillWinGame || player.getPlayerType() == PlayerType.COMPUTER_HARD)) {
				if (playerWinMargin > 0)
					return tryToStopWinningBattle(battle, monsterInfo, player, playerWinMargin);
			}
			else if (playerWinMargin <= 0) {
				return tryToWinBattle(battle, monsterInfo, player, 1 - playerWinMargin);
			}
		}
		else if (playerWinMargin > 0)
			return tryToStopWinningBattle(battle, monsterInfo, player, playerWinMargin);
		
		return false;
	}
	
	/**
	 * Decides and performs the best battle actions to help increase rewards for winning this battle.
	 * @param battle the currently on-going battle
	 * @param monsterInfo information about the monsters in the battle
	 * @param playerWinMargin the number of levels that the players in the battle are currently winning by
	 * @return true if the player changes something; false otherwise
	 * @throws EndGameException thrown when the game has been won
	 */
	private static boolean increaseBattleRewards(Battle battle, BattleMonsterInfo monsterInfo, int playerWinMargin) throws EndGameException {
		for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {
			if (monster.card.getID() == Card.M_LARGE_ANGRY_CHICKEN) {
				ItemCard firePotion = null;
				for (ItemCard item : battle.activePlayer.getCarriedItems())
					if (item.getID() == Card.I_FLAMING_POISON_POTION)
						firePotion = item;
				
				if (firePotion != null && !battle.usedFire()) {
					battle.activePlayer.getCarriedItems().remove(firePotion);
					battle.addPlayerItemCard(firePotion);
					String message = battle.activePlayer.getName() + " used the " + firePotion + " against the monsters.";
					Messenger.display(message, "Battle Item Used");
					return true;
				}
			}
			else if (monster.card.getID() == Card.M_THE_NOTHING && !battle.isHelper()) {
				int playerLevel = battle.activePlayer.getLevel();
				if (battle.activePlayer.isWarrior())
					playerLevel++;
				int monstersLevel = battle.getMonstersLevel();
				if (playerLevel > monstersLevel)
					return false;
			}		
		}
			
		// see if you can add to the difficulty of the battle, still win, and gain extra levels and/or treasures
		if (battle.isHelper()) {
			PlayerType helperType = battle.helper.getPlayerType();
			if (helperType == PlayerType.COMPUTER_HARD || (helperType == PlayerType.HUMAN && battle.activePlayer.getPlayerType() == PlayerType.COMPUTER_EASY))
				return false;
		}
		
		Iterator<Card> handCardIter = battle.activePlayer.getHandCards().iterator();
		while (handCardIter.hasNext()) {
			Card handCard = handCardIter.next();
			if (handCard instanceof MonsterEnhancerCard) {
				int monsterBonus = ((MonsterEnhancerCard)handCard).getBonus();
				if ((monsterBonus == 5 && playerWinMargin >= 9) || (monsterBonus == 10 && playerWinMargin >= 15)) {
					handCardIter.remove();
					battle.addMonsterEnhancer(battle.getMonster(0), (MonsterEnhancerCard)handCard);
					String message = battle.activePlayer.getName() + " played " + handCard;
					message += " on the " + battle.getMonster(0) + ".";
					Messenger.display(message, "Card From Hand Played");
					return true;
				}						
			}
			else if (handCard instanceof OtherDoorCard) {
				if (handCard.getID() == Card.OD_MATE) {
					for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++) {
						MonsterCard monster = battle.getMonster(monsterIndex);
						int monsterLevel = monster.getLevel(battle);
						for (DoorCard enhancer : battle.getMonsterEnhancers(monsterIndex)) {
							if (enhancer instanceof MonsterEnhancerCard)
								monsterLevel += ((MonsterEnhancerCard)enhancer).getBonus();
						}
						
						if (playerWinMargin - monsterLevel >= 5) {
							handCardIter.remove();
							battle.addMonsterEnhancer(monster, (OtherDoorCard)handCard);
							String message = battle.activePlayer.getName() + " played " + handCard;
							message += " on the " + monster + ".";
							Messenger.display(message, "Card From Hand Played");
							return true;
						}
					}
				}
				else if (handCard.getID() == Card.OD_WANDERING_MONSTER_1 || handCard.getID() == Card.OD_WANDERING_MONSTER_2) {
					Iterator<Card> handCardMonsterIter = battle.activePlayer.getHandCards().iterator();
					while (handCardMonsterIter.hasNext()) {
						Card card = handCardMonsterIter.next();
						if (card instanceof MonsterCard) {
							MonsterCard monster = (MonsterCard)card;
							if (battle.isHelper() && monster.getID() == Card.M_GAZEBO)
								continue;
							if (monster.getID() == Card.M_GHOULFIENDS)
								continue;
							
							int monsterLevel = monster.getLevel(battle);
							if (monster.getID() == Card.M_SQUIDZILLA) {
								if (battle.activePlayer.isElf())
									monsterLevel += 4;
								if (battle.isHelper() && battle.helper.isElf())
									monsterLevel += 4;
							}
							else if (monster.getID() == Card.M_INSURANCE_SALESMAN)
								monsterLevel += battle.activePlayer.getLevel();
							else if (monster.getID() == Card.M_TONGUE_DEMON)
								monsterLevel += 1;
							
							int badStuffCost = AIManager.getBadStuffCost(monster, false, battle.activePlayer, false, battle.activePlayer.getHandCards());
							badStuffCost = (int)(Math.round((double)badStuffCost / 1000.0));
							
							if (playerWinMargin - monsterLevel > 5 + badStuffCost) {
								handCardMonsterIter.remove();
								battle.addMonster(monster);
								battle.activePlayer.discard(handCard);
								String message = battle.activePlayer.getName() + " used the " + handCard + " card";
								message += " to add the " + monster + " to the battle.";
								Messenger.display(message, "Card From Hand Played");
								return true;
							}									
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Decides and performs the best worthwhile battle actions for the given player to try to win the battle.
	 * @param battle the currently on-going battle
	 * @param monsterInfo information about the monsters in the battle
	 * @param player player trying to win battle
	 * @param playerLoseMargin the number of levels the player(s) are losing by
	 * @return true if the player changes something; false otherwise
	 * @throws EndGameException thrown when the game has been won
	 */
	private static boolean tryToWinBattle(Battle battle, BattleMonsterInfo monsterInfo, Player player, int playerLoseMargin) throws EndGameException {
		// kill Floating Nose instantly, if possible
		for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {						
			if (monster.card.getID() == Card.M_FLOATING_NOSE) {
				Iterator<ItemCard> itemIter = player.getCarriedItems().iterator();
				while (itemIter.hasNext()) {
					ItemCard item = itemIter.next();
					if (item.getID() == Card.I_POTION_OF_HALITOSIS) {
						itemIter.remove();
						battle.addPlayerItemCard(item);	
						String message = player.getName() + " used the " + item;
						message += " to kill the " + monster.card + ".";
						Messenger.display(message, "Battle Item Used");
						return true;
					}
				}
			}
		}
		
		if (player.hasEquipped(Card.E_KNEEPADS_OF_ALLURE) && battle.canAddHelper()) {
			(new AskHelpDialog(battle, true)).setVisible(true);
			if (battle.helper != null)
				return true;
		}
					
		// get and sort hand card values to player
		LinkedList<AIValuedCard> valuedHandCards = AIManager.getLeastValuedHandCards(player, player.getHandCards());
		int handValue = 0;
		for (AIValuedCard valuedCard : valuedHandCards)
			handValue += valuedCard.getValue();
		
		// create a list of possible battle actions and the benefit and cost of them
		LinkedList<BattleAction> battleActions = new LinkedList<BattleAction>();
		
		LinkedList<MonsterCard> handMonsters = new LinkedList<MonsterCard>();
		for (Card handCard : player.getHandCards()) {
			if (handCard instanceof MonsterCard)
				handMonsters.add((MonsterCard)handCard);
			else if (handCard.getID() == Card.OD_HELP_ME_OUT_HERE)
				if (CardPlayManager.playCard(player, handCard, battle))
					return true;
		}
		
		for (Card handCard : player.getHandCards()) {
			if (handCard.getID() == Card.ME_BABY) {
				BattleMonsterInfo.BattleMonster battleMonster = monsterInfo.getBattleMonsters().getFirst();
				int levelBonus = Math.min(5, battleMonster.battleLevel - 1);
				int treasureCost = Math.min(1, battleMonster.treasures - 1) * AIManager.UNKNOWN_CARD_VALUE;
				battleActions.add(new BattleAction(ActionType.HAND_CARD, handCard, levelBonus, treasureCost, battleMonster.card));
			}
		}
		
		if (battle.canUseTurning(player)) {
			for (int count = 1; count <= battle.getNumberOfTurningBonusesLeft(player); count++) {
				if (valuedHandCards.isEmpty())
					break;
				else {
					AIValuedCard valuedCard = valuedHandCards.removeFirst();
					BattleAction action = new BattleAction(ActionType.TURNING, valuedCard.getCard(), 3, valuedCard.getValue());
					
					BattleAction sameCardAction = null;
					for (BattleAction currentAction : battleActions)
						if (currentAction.getCard() == valuedCard.getCard())
							sameCardAction = currentAction;
					
					if (sameCardAction != null) {
						if (sameCardAction.getWorth() >= action.getWorth()) {
							count--;
							continue;
						}
						else
							battleActions.remove(sameCardAction);
					}
					
					int actionIndex;
					for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++) {
						if (action.getWorth() > battleActions.get(actionIndex).getWorth())
							break;
					}
					
					battleActions.add(actionIndex, action);
				}						
			}
		}
		
		if (battle.canUseBerserking(player)) {
			for (int count = 1; count <= battle.getNumberOfBerserkingBonusesLeft(player); count++) {
				if (valuedHandCards.isEmpty())
					break;
				else {
					AIValuedCard valuedCard = valuedHandCards.removeFirst();
					BattleAction action = new BattleAction(ActionType.BERSERKING, valuedCard.getCard(), 1, valuedCard.getValue());
					
					BattleAction sameCardAction = null;
					for (BattleAction currentAction : battleActions)
						if (currentAction.getCard() == valuedCard.getCard())
							sameCardAction = currentAction;
					
					if (sameCardAction != null) {
						if (sameCardAction.getWorth() >= action.getWorth()) {
							count--;
							continue;
						}
						else
							battleActions.remove(sameCardAction);
					}
					
					int actionIndex;
					for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++) {
						if (action.getWorth() > battleActions.get(actionIndex).getWorth())
							break;
					}
					
					battleActions.add(actionIndex, action);
				}						
			}
		}		
		
		boolean canUseItems = true;
		for (BattleMonster monster : monsterInfo.getBattleMonsters())
			if (monster.card.getID() == Card.M_GHOULFIENDS)
				canUseItems = false;
		
		if (canUseItems) {
			for (ItemCard item: player.getCarriedItems()) {			
				if (item.getBonus() > 0) {
					// sort battle actions from greatest to least worth ratio
					BattleAction action = new BattleAction(ActionType.ITEM_CARD, item, item.getBonus(), item.getValue());
					int actionIndex;
					for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
						if (action.getWorth() > battleActions.get(actionIndex).getWorth())
							break;
					
					battleActions.add(actionIndex, action);
				}
				else if (item.getID() == Card.I_DOPPLEGANGER) {
					if (player != battle.activePlayer || battle.isHelper())
						continue;
					
					int bonus = player.getLevel() + player.getEquipmentBonus();
					// only use this card if it will at least get you more than the value of a level and 2 treasures
					int value = AIManager.LEVEL_VALUE + AIManager.UNKNOWN_CARD_VALUE * 2;
					BattleAction action = new BattleAction(ActionType.ITEM_CARD, item, bonus, value);
					int actionIndex;
					for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
						if (action.getWorth() > battleActions.get(actionIndex).getWorth())
							break;
					
					battleActions.add(actionIndex, action);
				}
				else if (item.getID() == Card.I_YUPPIE_WATER) {
					int itemBonus = 0;
					if (player.isElf())
						itemBonus += 2;
					if (battle.isHelper() && battle.helper.isElf())
						itemBonus += 2;
					if (itemBonus == 0)
						continue;
					
					BattleAction action = new BattleAction(ActionType.ITEM_CARD, item, itemBonus, item.getValue());
					int actionIndex;
					for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
						if (action.getWorth() > battleActions.get(actionIndex).getWorth())
							break;
					
					battleActions.add(actionIndex, action);
				}
			}
		}
		
		if (!player.isHuman()) {
			for (RaceCard raceCard : player.getRaceCards()) {
				int bonus = 0;
				for (BattleMonsterInfo.BattleMonster battleMonster : monsterInfo.getBattleMonsters()) {
					bonus += battleMonster.card.getRaceBonus(raceCard.getRace());
					if (raceCard.getRace() == Race.ELF && battleMonster.card.getID() == Card.M_SQUIDZILLA)
						bonus += 4;
				}
				for (EquipmentCard equipment : player.getEquippedItems())
					bonus -= equipment.getBonusToRace(raceCard.getRace());
				
				// factor in dwarf big item loss value
				if (raceCard.getRace() == Race.DWARF) {
					int allowedBigItems = 1;
					if (player.hasHireling())
						allowedBigItems = 2;
					LinkedList<EquipmentCard> weakestBigItems = new LinkedList<EquipmentCard>();
					for (EquipmentCard bigItem : player.getBigItems()) {
						int bigItemIndex;
						for (bigItemIndex = 0; bigItemIndex < weakestBigItems.size(); bigItemIndex++)
							if (bigItem.getBonus(player) < weakestBigItems.get(bigItemIndex).getBonus(player))
								break;
						
						weakestBigItems.add(bigItemIndex, bigItem);
					}					
					
					for (int numToLose = weakestBigItems.size() - allowedBigItems; numToLose > 0; numToLose--)
						bonus -= weakestBigItems.remove().getBonus(player);
				}
				
				if (bonus > 0) {
					int value = AICardEvaluator.getCardValueToPlayer(raceCard, player, player.getHandCards());
					BattleAction action = new BattleAction(ActionType.DISCARD_RACE, raceCard, bonus, value);
					int actionIndex;
					for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
						if (action.getWorth() > battleActions.get(actionIndex).getWorth())
							break;
					
					battleActions.add(actionIndex, action);
				}
			}
		}
		
		for (ClassCard classCard : player.getClassCards()) {
			int bonus = 0;
			for (BattleMonsterInfo.BattleMonster battleMonster : monsterInfo.getBattleMonsters())
				bonus += battleMonster.card.getClassBonus(classCard.getCharacterClass());
			for (EquipmentCard equipment : player.getEquippedItems())
				bonus -= equipment.getBonusToClass(classCard.getCharacterClass());
			if (classCard.getCharacterClass() == Class.WARRIOR)
				bonus--;
			
			if (bonus > 0) {
				int value = AICardEvaluator.getCardValueToPlayer(classCard, player, player.getHandCards());
				BattleAction action = new BattleAction(ActionType.DISCARD_CLASS, classCard, bonus, value);
				int actionIndex;
				for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
					if (action.getWorth() > battleActions.get(actionIndex).getWorth())
						break;
				
				battleActions.add(actionIndex, action);
				if (classCard.getCharacterClass() == Class.CLERIC) {
					Iterator<BattleAction> battleActionIter = battleActions.iterator();
					while(battleActionIter.hasNext())
						if (battleActionIter.next() == action)
							break;
					while(battleActionIter.hasNext())
						if (battleActionIter.next().getType() == ActionType.TURNING)
							battleActionIter.remove();
				}
				else if (classCard.getCharacterClass() == Class.WARRIOR) {
					Iterator<BattleAction> battleActionIter = battleActions.iterator();
					while(battleActionIter.hasNext())
						if (battleActionIter.next() == action)
							break;
					while(battleActionIter.hasNext())
						if (battleActionIter.next().getType() == ActionType.BERSERKING)
							battleActionIter.remove();
				}
			}
		}			
		
		// perform the above actions if they are enough to win and worth the cost
		int levelGain = monsterInfo.getPlayerWinLevels(player);
		int valueOfWinning = levelGain * AIManager.LEVEL_VALUE;
		if (player.getLevel() + levelGain >= 10)
			valueOfWinning = 10 * AIManager.LEVEL_VALUE;
		valueOfWinning += monsterInfo.getPlayerWinTreasureValue(player);
		valueOfWinning += monsterInfo.getPlayerBadStuffCost(player);
		
		int totalBenefit = 0;
		int totalCost = 0;
		boolean worthIt = true;
		int actionIdx;
		for (actionIdx = 0; actionIdx < battleActions.size(); actionIdx++) {
			BattleAction action = battleActions.get(actionIdx);
			totalBenefit += action.getBenefit();
			totalCost += action.getCost();
			
			// performing the current number of actions is not worth the benefit that will be gained by winning.
			if (totalCost >= valueOfWinning) {
				worthIt = false;
				break;
			}
			
			if (totalBenefit >= playerLoseMargin)
				break;
		}
		
		// total benefit is enough to defeat monsters and it is worth it to do so
		if (worthIt && actionIdx < battleActions.size()) {
			boolean actionPerformed = false;
			
			// perform just enough actions to defeat monsters
			for (; actionIdx >= 0; actionIdx--)
				if (battleActions.removeFirst().perform(battle, player))
					actionPerformed = true;
			
			if (actionPerformed)
				return true;
		}		
		
		if (battle.canAddHelper() && !battle.haveAllPlayersRefusedToHelp()) {
			(new AskHelpDialog(battle, false)).setVisible(true);
			if (battle.helper != null)
				return true;
		}
		
		for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {
			if (monster.card.getID() == Card.M_WANNABE_VAMPIRE && player.isCleric()) {
				battle.discardMonster(monster.card, true);
			
				String message = player.getName() + " used Clerical powers to chase away the Wannabe Vampire.";
				Messenger.display(message, "Class Power Used");
				return true;
			}
		}
		
		for (Card handCard : player.getHandCards()) {
			if (handCard.getID() == Card.OD_ILLUSION && !handMonsters.isEmpty()) {
				MonsterCard weakestHandMonster = null;
				int weakestLevel = 100;
				for (MonsterCard monsterInHand : handMonsters) {
					if (monsterInHand.getID() == Card.M_GAZEBO && battle.isHelper())
						continue;
					if (monsterInHand.getID() == Card.M_GHOULFIENDS)
						continue;
					
					int monsterInHandLevel = monsterInHand.getLevel(battle);
					if (monsterInHand.getID() == Card.M_AMAZON) {
						if (battle.activePlayer.isFemale() || (battle.isHelper() && battle.helper.isFemale()))
							monsterInHandLevel = 0;
					}
					else if (monsterInHand.getID() == Card.M_LAWYER) {
						if (battle.activePlayer.isThief() || (battle.isHelper() && battle.helper.isThief()))
							monsterInHandLevel = 0;
					}
					else if (monsterInHand.getID() == Card.M_INSURANCE_SALESMAN) {
						monsterInHandLevel += battle.activePlayer.getLevel();
						if (battle.isHelper())
							monsterInHandLevel += battle.helper.getLevel();
					}
	
					if (weakestHandMonster == null || monsterInHandLevel < weakestLevel) {
						weakestHandMonster = monsterInHand;
						weakestLevel = monsterInHandLevel;
					}						
				}
				
				for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {
					if ((playerLoseMargin - monster.battleLevel) + weakestLevel <= 0) {
						battle.replaceMonster(monster.card, weakestHandMonster);
						
						player.getHandCards().remove(weakestHandMonster);
						player.discard(handCard);
						String message = player.getName() + " used the " + handCard + " card";
						message += " to replace the " + monster.card + " with the " + weakestHandMonster + ".";
						Messenger.display(message, "Card From Hand Played");
						return true;
					}
				}				
			}
		}
		
		for (ItemCard item : player.getCarriedItems()) {
			if (item.getID() == Card.I_POLLYMORPH_POTION) {
				for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {
					int monsterLevel = monster.battleLevel;
					if (monster.hasMate)
						monsterLevel /= 2;
										
					if (playerLoseMargin - monsterLevel <= 0) {
						int levelLoss = 0;
						if (player == battle.activePlayer) {
							if (monster.hasMate)
								levelLoss = monster.activePlayerWinLevels / 2;
							else
								levelLoss = monster.activePlayerWinLevels;
						}
						else {
							if (monster.hasMate)
								levelLoss = monster.helperWinLevels / 2;
							else
								levelLoss = monster.helperWinLevels;
						}
						
						int levelLossValue = levelLoss * AIManager.LEVEL_VALUE;
						int newLevel = player.getLevel() + monsterInfo.getPlayerWinLevels(player);
						if (newLevel >= 10 && newLevel - levelLoss < 10)
							levelLossValue -= 10 * AIManager.LEVEL_VALUE;
						
						if (valueOfWinning - levelLossValue >= AICardEvaluator.getCardValueToPlayer(item, player, player.getHandCards())) {
							player.discard(item);
							battle.discardMonster(monster.card, false);
							String message = player.getName() + " used the " + item;
							message += " on the " + monster.card + ".";
							Messenger.display(message, "Battle Item Used");
							return true;
						}
					}
				}	
			}
		}
		
		for (ItemCard item : player.getCarriedItems()) {
			if (item.getID() == Card.I_MAGIC_LAMP_1 || item.getID() == Card.I_MAGIC_LAMP_2) {
				for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {
					int monsterLevel = monster.battleLevel;
					if (monster.hasMate)
						monsterLevel /= 2;
					
					if (playerLoseMargin - monsterLevel <= 0) {
						int levelLoss = 0;
						if (player == battle.activePlayer) {
							if (monster.hasMate)
								levelLoss = monster.activePlayerWinLevels / 2;
							else
								levelLoss = monster.activePlayerWinLevels;
						}
						else {
							if (monster.hasMate)
								levelLoss = monster.helperWinLevels / 2;
							else
								levelLoss = monster.helperWinLevels;
						}
						
						int levelLossValue = levelLoss * AIManager.LEVEL_VALUE;
						int newLevel = player.getLevel() + monsterInfo.getPlayerWinLevels(player);
						if (newLevel >= 10 && newLevel - levelLoss < 10)
							levelLossValue -= 10 * AIManager.LEVEL_VALUE;
						
						if (valueOfWinning - levelLossValue >= AICardEvaluator.getCardValueToPlayer(item, player, player.getHandCards())) {
							player.discard(item);
							battle.discardMonster(monster.card, false);
							String message = player.getName() + " used the " + item;
							message += " on the " + monster.card + ".";
							Messenger.display(message, "Battle Item Used");
							return true;
						}
					}
				}
			}
		}
		
		// Use charm ability if available and worth it
		if (player.isWizard() && !player.getHandCards().isEmpty()) {
			for (BattleMonsterInfo.BattleMonster monster : monsterInfo.getBattleMonsters()) {
				int monsterLevel = monster.battleLevel;
				if (monster.hasMate)
					monsterLevel /= 2;
				
				if (playerLoseMargin - monsterLevel <= 0) {
					int levelLoss = 0;
					if (player == battle.activePlayer) {
						if (monster.hasMate)
							levelLoss = monster.activePlayerWinLevels / 2;
						else
							levelLoss = monster.activePlayerWinLevels;
					}
					else {
						if (monster.hasMate)
							levelLoss = monster.helperWinLevels / 2;
						else
							levelLoss = monster.helperWinLevels;
					}
					
					int levelLossValue = levelLoss * AIManager.LEVEL_VALUE;
					int newLevel = player.getLevel() + monsterInfo.getPlayerWinLevels(player);
					if (newLevel >= 10 && newLevel - levelLoss < 10)
						levelLossValue -= 10 * AIManager.LEVEL_VALUE;
					
					if (valueOfWinning - levelLossValue > handValue) {
						LinkedList<Card> handCards = new LinkedList<Card>();
						handCards.addAll(player.getHandCards());
						Iterator<Card> cardIter = handCards.iterator();
						while (cardIter.hasNext())
							player.discard(cardIter.next());
						
						battle.discardMonster(monster.card, false);
						String message = player.getName() + " used the Charm ability";
						message += " on the " + monster.card;
						if (monster.hasMate)
							message += "'s mate";
						message += ".";
						Messenger.display(message, "Class Power Used");
						return true;
					}
				}
			}
		}
		
		// Must be in danger of losing on average at least more than the value of 2 treasures to use these cards.
		if (monsterInfo.getPlayerBadStuffCost(player) > AIManager.UNKNOWN_CARD_VALUE * 2) {
			for (Card handCard : player.getHandCards()) {
				if (handCard.getID() == Card.OD_OUT_TO_LUNCH) {
					String message = player.getName() + " played the " + handCard + " card.";
					Messenger.display(message, "Card From Hand Played");
					return CardPlayManager.playCard(player, handCard, battle);
				}
			}			
			
			for (ItemCard item : player.getCarriedItems()) {	
				if (item.getID() == Card.I_FRIENDSHIP_POTION) {
					String message = player.getName() + " used the " + item + ".";
					Messenger.display(message, "Battle Item Used");
					return CardPlayManager.playCard(player, item, battle);
				}
			}			
			
			for (ItemCard item : player.getCarriedItems()) {
				if (item.getID() == Card.I_TRANSFERRAL_POTION) {
					
					// Place the highest ranked player in the battle who will not be able to win in the
					// current situation.
					LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
					int currentPlayerLevel = battle.getPlayersLevel();
					Player lowestWinningPlayer = null;
					for (Player victim : rankedPlayers) {
						if (battle.activePlayer == victim || battle.helper == victim)
							continue;
						Player activePlayer = battle.activePlayer;
						battle.activePlayer = victim;
						int newPlayerLevel = battle.getPlayersLevel();
						battle.activePlayer = activePlayer;
						
						if (newPlayerLevel - currentPlayerLevel < playerLoseMargin) {
							if (player == battle.activePlayer) {
								String message = player.getName() + " used the " + item;
								message += " to replace " + battle.activePlayer.getName();
								message += " with " + victim + " in the battle.";
								Messenger.display(message, "Battle Item Used");
								battle.replaceActivePlayer(victim);
								player.discard(item);
								
								GM.setCanLootRoom();
								return true;
							}
						}
						else
							lowestWinningPlayer = victim;
					}
					
					// If the bad stuff cost is high, put the lowest ranked player in the battle even though 
					// he can currently win it (only if he can win it if player is helper).
					if (lowestWinningPlayer != null && monsterInfo.getPlayerBadStuffCost(player) > AIManager.LEVEL_VALUE * 3) {
						String message = player.getName() + " used the " + item;
						message += " to replace " + battle.activePlayer.getName();
						message += " with " + lowestWinningPlayer + " in the battle.";
						Messenger.display(message, "Battle Item Used");
						battle.replaceActivePlayer(lowestWinningPlayer);
						player.discard(item);
							
						GM.setCanLootRoom();
						return true;
					}
				}
			}
		}
			
		return false;
	}
	
	/**
	 * Decides and performs the best worthwhile battle actions for the given player to try to make the player(s) in the battle lose.
	 * @param battle the currently on-going battle
	 * BattleMonsterInfo monsterInfo, 
	 * @param player player trying to make the player(s) in the battle lose
	 * @param playerWinMargin the number of levels the player(s) are winning by
	 * @return true if the player changes something; false otherwise
	 * @throws EndGameException thrown when the game has been won
	 */
	private static boolean tryToStopWinningBattle(Battle battle, BattleMonsterInfo monsterInfo, Player player, int playerWinMargin) throws EndGameException {
		int activePlayerWinLevel = battle.activePlayer.getLevel() + monsterInfo.getPlayerWinLevels(battle.activePlayer);
		int helperWinLevel = 0;
		if (battle.isHelper())
			helperWinLevel = battle.helper.getLevel() + monsterInfo.getPlayerWinLevels(battle.helper);
		boolean activePlayerWillWinGame = false;
		boolean helperWillWinGame = false;
		if (activePlayerWinLevel >= 10)
			activePlayerWillWinGame = true;
		if (helperWinLevel >= 10)
			helperWillWinGame = true;
		
		LinkedList<BattleAction> battleActions = new LinkedList<BattleAction>();
		BattleAction transferralAction = null;
		
		for (ItemCard item: player.getCarriedItems()) {
			if (item.getBonus() > 0) {
				// sort battle actions from greatest to least worth ratio
				BattleAction action = new BattleAction(ActionType.ITEM_CARD, item, -item.getBonus(), item.getValue());
				int actionIndex;
				for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
					if (action.getWorth() > battleActions.get(actionIndex).getWorth())
						break;
				
				battleActions.add(actionIndex, action);
			}
			else if (item.getID() == Card.I_TRANSFERRAL_POTION) {
				int minimumWinLevel = battle.getPlayersLevel() - playerWinMargin + 1;
				
				LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
				rankedPlayers.remove(battle.activePlayer);
				if (battle.isHelper())
					rankedPlayers.remove(battle.helper);
				int cost = 500 + AICardEvaluator.getCardValueToPlayer(item, player, player.getHandCards());
				if (activePlayerWillWinGame)
					transferralAction = new BattleAction(ActionType.TRANSFERRAL_POTION, item, -playerWinMargin, cost, rankedPlayers.getLast());				
				for (Player victim : rankedPlayers) {
					Player activePlayer = battle.activePlayer;
					battle.activePlayer = victim;
					int newPlayerLevel = battle.getPlayersLevel();
					battle.activePlayer = activePlayer;
															
					if (player != victim && newPlayerLevel < minimumWinLevel) {
						transferralAction = new BattleAction(ActionType.TRANSFERRAL_POTION, item, -playerWinMargin, cost, victim);
						break;
					}
					else if (player == victim && newPlayerLevel >= minimumWinLevel) {
						transferralAction = new BattleAction(ActionType.TRANSFERRAL_POTION, item, -playerWinMargin, cost, victim);
						break;
					}
				}
			}
		}
		
		LinkedList<MonsterCard> monstersInHand = new LinkedList<MonsterCard>();
		
		// add all possible battle actions from hand cards
		for (Card handCard : player.getHandCards()) {
			if (handCard instanceof CurseCard) {
				int curseLoss = 0;
				Player victim = battle.activePlayer;
				if (player != battle.activePlayer)
					curseLoss = getCurseLevelLoss((CurseCard)handCard, battle.activePlayer, battle);
				if (battle.isHelper() && player != battle.helper) {
					int helperLoss = getCurseLevelLoss((CurseCard)handCard, battle.helper, battle);
					if (helperLoss > curseLoss) {
						curseLoss = helperLoss;
						victim = battle.helper;
					}
				}
				
				int cardValue = AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards());
				BattleAction action = new BattleAction(ActionType.HAND_CARD, handCard, -curseLoss, cardValue, victim);
				int actionIndex;
				for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
					if (action.getWorth() > battleActions.get(actionIndex).getWorth())
						break;
				
				battleActions.add(actionIndex, action);
			}
			else if (handCard instanceof MonsterEnhancerCard) {
				MonsterEnhancerCard enhancer = (MonsterEnhancerCard)handCard;
				if (enhancer.getBonus() <= 0)
					continue;
				
				int cardValue = AICardEvaluator.getCardValueToPlayer(enhancer, player, player.getHandCards());
				MonsterCard monster = monsterInfo.getBattleMonsters().getLast().card;
				BattleAction action = new BattleAction(ActionType.HAND_CARD, handCard, -enhancer.getBonus(), cardValue, monster);
				int actionIndex;
				for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++)
					if (action.getWorth() > battleActions.get(actionIndex).getWorth())
						break;
				
				battleActions.add(actionIndex, action);
			}
			else if (handCard instanceof MonsterCard)
				monstersInHand.add((MonsterCard)handCard);
		}
		
		// add backstab actions if possible
		Iterator<AIValuedCard> valuedCardIter = AIManager.getLeastValuedHandCards(player, player.getHandCards()).iterator();
		if (battle.canBackstab(player, battle.activePlayer)) {
			while (valuedCardIter.hasNext()) {
				AIValuedCard valuedCard = valuedCardIter.next();
				BattleAction action = new BattleAction(ActionType.BACKSTAB, valuedCard.getCard(), -2, valuedCard.getValue(), battle.activePlayer);
				
				BattleAction sameCardAction = null;
				for (BattleAction currentAction : battleActions)
					if (currentAction.getCard() == valuedCard.getCard())
						sameCardAction = currentAction;
				
				if (sameCardAction != null) {
					if (sameCardAction.getWorth() >= action.getWorth())
						continue;
					else
						battleActions.remove(sameCardAction);
				}
				
				int actionIndex;
				for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++) {
					if (action.getWorth() > battleActions.get(actionIndex).getWorth())
						break;
				}
				
				battleActions.add(actionIndex, action);
				break;
			}
		}		
		if (battle.canBackstab(player, battle.helper)) {
			while (valuedCardIter.hasNext()) {
				AIValuedCard valuedCard = valuedCardIter.next();
				BattleAction action = new BattleAction(ActionType.BACKSTAB, valuedCard.getCard(), -2, valuedCard.getValue(), battle.helper);
				
				BattleAction sameCardAction = null;
				for (BattleAction currentAction : battleActions)
					if (currentAction.getCard() == valuedCard.getCard())
						sameCardAction = currentAction;
				
				if (sameCardAction != null) {
					if (sameCardAction.getWorth() >= action.getWorth())
						continue;
					else
						battleActions.remove(sameCardAction);
				}
				
				int actionIndex;
				for (actionIndex = 0; actionIndex < battleActions.size(); actionIndex++) {
					if (action.getWorth() > battleActions.get(actionIndex).getWorth())
						break;
				}
				
				battleActions.add(actionIndex, action);
				break;
			}
		}
		
		// perform the above actions if they are enough to stop player(s) from winning the battle and worth the cost
		if (activePlayerWillWinGame || helperWillWinGame) {
			if (transferralAction != null)
				return transferralAction.perform(battle, player);
			
			while (!battleActions.isEmpty())
				if (battleActions.removeFirst().perform(battle, player))
					return true;
		}
		
		int winValue = 0;
		int activePlayerLevelGain = monsterInfo.getPlayerWinLevels(battle.activePlayer);
		int helperLevelGain = monsterInfo.getPlayerWinLevels(battle.helper);
		
		LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
		
		double activePlayerFactor = 1.0;
		if (player != battle.activePlayer) {
			winValue += activePlayerLevelGain * AIManager.LEVEL_VALUE;			
			winValue += monsterInfo.getPlayerWinTreasureValue(battle.activePlayer);
			winValue += monsterInfo.getPlayerBadStuffCost(battle.activePlayer);

			// factor in player ranks and computer level
			int computerMercyLevel = 0;
			if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
				computerMercyLevel = 1;
			else if (player.getPlayerType() == PlayerType.COMPUTER_EASY)
				computerMercyLevel = 2;
			activePlayerFactor = GM.getPlayers().size() - rankedPlayers.indexOf(battle.activePlayer);
			activePlayerFactor /= (double)(GM.getPlayers().size() + computerMercyLevel) / 2.0;
			winValue *= activePlayerFactor;
		}
		
		double helperFactor = 1.0;
		if (battle.isHelper() && player != battle.helper) {
			int helperWinValue = helperLevelGain * AIManager.LEVEL_VALUE;
			helperWinValue += monsterInfo.getPlayerWinTreasureValue(battle.helper);
			helperWinValue += monsterInfo.getPlayerBadStuffCost(battle.helper);
			
			// factor in player ranks and computer level
			int computerMercyLevel = 0;
			if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
				computerMercyLevel = 1;
			if (player.getPlayerType() == PlayerType.COMPUTER_EASY)
				computerMercyLevel = 2;
			helperFactor = GM.getPlayers().size() - rankedPlayers.indexOf(battle.helper);
			helperFactor /= (double)(GM.getPlayers().size() + computerMercyLevel) / 2.0;
			helperWinValue *= helperFactor;
			winValue += helperWinValue;
		}
		
		if (transferralAction != null) {
			if (transferralAction.getCost() < winValue)
				return transferralAction.perform(battle, player);
		}
		
		int totalLevelLoss = 0;
		int totalCost = 0;
		int actionIdx;
		for (actionIdx = 0; actionIdx < battleActions.size(); actionIdx++) {
			BattleAction action = battleActions.get(actionIdx);
			totalLevelLoss += -action.getBenefit();
			totalCost += action.getCost();
			
			// if performing the current number of actions is not worth stopping the player(s) from winning.
			if (totalCost >= winValue)
				break;
			
			// if can stop player from winning and worth it to do so, perform first action and return.
			if (totalLevelLoss >= playerWinMargin) {
				while (!battleActions.isEmpty())
					if (battleActions.removeFirst().perform(battle, player))
						return true;
			}
		}
		
		for (Card handCard : player.getHandCards()) {
			if (handCard.getID() == Card.OD_ILLUSION && !monstersInHand.isEmpty()) {
				// Don't play if value of winning is not worth playing the cards
				if (!activePlayerWillWinGame && !helperWillWinGame) {
					if (winValue <= AIManager.LEVEL_VALUE * 2)
						continue;
				}			
				
				int weakestMonsterLevel = monsterInfo.getBattleMonsters().getLast().battleLevel;
				MonsterCard bestMonsterCard = null;
				int bestMonsterBonus = 0;
				for (MonsterCard handMonster : monstersInHand) {
					int monsterBonus = 0;
					if (battle.isHelper() && handMonster.getID() == Card.M_GAZEBO) {
						for (int monsterIdx = 0; monsterIdx < monsterInfo.getBattleMonsters().size() - 1; monsterIdx++) {
							MonsterCard monsterCard = monsterInfo.getBattleMonsters().get(monsterIdx).card;		
							int helperMonsterLevel = monsterCard.getLevel(battle.helper);
							int activePlayerMonsterLevel = monsterCard.getLevel(battle.activePlayer);
							if (helperMonsterLevel > activePlayerMonsterLevel)
								monsterBonus += activePlayerMonsterLevel - helperMonsterLevel;
						}
						
						monsterBonus += battle.helper.getLevel() + battle.helper.getEquipmentBonus();
						monsterBonus += handMonster.getLevel(battle.activePlayer) - weakestMonsterLevel;
						
						if (monsterBonus > bestMonsterBonus) {
							bestMonsterBonus = monsterBonus;
							bestMonsterCard = handMonster;
						}
						
						continue;
					}
					else if (handMonster.getID() == Card.M_TONGUE_DEMON) {
						int leastLoss = 100;
						for (TreasureCard item : battle.activePlayer.getAllItems()) {
							if (item instanceof ItemCard) {
								if (((ItemCard)item).getBonus() < leastLoss)
									leastLoss = ((ItemCard)item).getBonus();
							}
							else if (item instanceof EquipmentCard) {
								EquipmentCard equipment = (EquipmentCard)item;
								if (battle.activePlayer.hasEquipped(equipment) && equipment.getBonus(battle.activePlayer) < leastLoss)
									leastLoss = ((EquipmentCard)item).getBonus(battle.activePlayer);
							}
						}				
						if (leastLoss != 100)
							monsterBonus += leastLoss;
						
						if (battle.isHelper()) {
							leastLoss = 100;
							for (TreasureCard item : battle.helper.getAllItems()) {
								if (item instanceof ItemCard) {
									if (((ItemCard)item).getBonus() < leastLoss)
										leastLoss = ((ItemCard)item).getBonus();
								}
								else if (item instanceof EquipmentCard) {
									EquipmentCard equipment = (EquipmentCard)item;
									if (battle.helper.hasEquipped(equipment) && equipment.getBonus(battle.helper) < leastLoss)
										leastLoss = ((EquipmentCard)item).getBonus(battle.helper);
								}
							}				
							if (leastLoss != 100)
								monsterBonus += leastLoss;
						}
					}
					else if (handMonster.getID() == Card.M_LAWYER) {
						if (battle.activePlayer.isThief() || (battle.isHelper() && battle.helper.isThief()))
							continue;
					}
					else if (handMonster.getID() == Card.M_AMAZON) {
						if (battle.activePlayer.isFemale() || (battle.isHelper() && battle.helper.isFemale()))
							continue;
					}
					else if (handMonster.getID() == Card.M_SQUIDZILLA) {
						if (battle.activePlayer.isElf())
							monsterBonus += 4;
						if (battle.isHelper() && battle.helper.isElf())
							monsterBonus += 4;
					}
					else if (handMonster.getID() == Card.M_FLOATING_NOSE) {
						boolean hasInstantKill = false;
						for (Card item : battle.activePlayer.getCarriedItems())
							if (item.getID() == Card.I_POTION_OF_HALITOSIS)
								hasInstantKill = true;
						
						if (!hasInstantKill && battle.isHelper()) {
							for (Card item : battle.helper.getCarriedItems())
								if (item.getID() == Card.I_POTION_OF_HALITOSIS)
									continue;
						}
						
						if (hasInstantKill)
							continue;
					}
					else if (handMonster.getID() == Card.M_WANNABE_VAMPIRE) {
						if (battle.activePlayer.isCleric() || (battle.isHelper() && battle.helper.isCleric()))
							continue;
					}
					
					monsterBonus += handMonster.getLevel(battle) - weakestMonsterLevel;
					if (monsterBonus > bestMonsterBonus) {
						bestMonsterBonus = monsterBonus;
						bestMonsterCard = handMonster;
					}
				}
				
				if (bestMonsterCard != null) {
					if (activePlayerWillWinGame || helperWillWinGame || bestMonsterBonus >= playerWinMargin) {
						MonsterCard oldMonster = monsterInfo.getBattleMonsters().getLast().card;
						String message = player.getName() + " used the " + handCard + " card";
						message += " to replace the " + oldMonster + " with the " + bestMonsterCard + ".";
						Messenger.display(message, "Card From Hand Played");
						battle.replaceMonster(oldMonster, bestMonsterCard);					
						player.getHandCards().remove(bestMonsterCard);
						player.discard(handCard);
						return true;
					}
				}
			}
			else if ((handCard.getID() == Card.OD_WANDERING_MONSTER_1 || handCard.getID() == Card.OD_WANDERING_MONSTER_2) && !monstersInHand.isEmpty()) {
				// Don't play if value of winning is not worth playing the cards
				if (!activePlayerWillWinGame && !helperWillWinGame) {
					if (winValue <= AIManager.LEVEL_VALUE * 2)
						continue;
				}			
				
				MonsterCard bestMonsterCard = null;
				int bestMonsterBonus = 0;
				for (MonsterCard handMonster : monstersInHand) {
					int monsterBonus = 0;
					if (battle.isHelper() && handMonster.getID() == Card.M_GAZEBO) {
						for (int monsterIdx = 0; monsterIdx < monsterInfo.getBattleMonsters().size(); monsterIdx++) {
							MonsterCard monsterCard = monsterInfo.getBattleMonsters().get(monsterIdx).card;		
							int helperMonsterLevel = monsterCard.getLevel(battle.helper);
							int activePlayerMonsterLevel = monsterCard.getLevel(battle.activePlayer);
							if (helperMonsterLevel > activePlayerMonsterLevel)
								monsterBonus += activePlayerMonsterLevel - helperMonsterLevel;
						}
						
						monsterBonus += battle.helper.getLevel() + battle.helper.getEquipmentBonus();
						monsterBonus += handMonster.getLevel(battle.activePlayer);
						
						if (monsterBonus > bestMonsterBonus) {
							bestMonsterBonus = monsterBonus;
							bestMonsterCard = handMonster;
						}
						
						continue;
					}
					else if (handMonster.getID() == Card.M_TONGUE_DEMON) {
						int leastLoss = 100;
						for (TreasureCard item : battle.activePlayer.getAllItems()) {
							if (item instanceof ItemCard) {
								if (((ItemCard)item).getBonus() < leastLoss)
									leastLoss = ((ItemCard)item).getBonus();
							}
							else if (item instanceof EquipmentCard) {
								EquipmentCard equipment = (EquipmentCard)item;
								if (battle.activePlayer.hasEquipped(equipment) && equipment.getBonus(battle.activePlayer) < leastLoss)
									leastLoss = ((EquipmentCard)item).getBonus(battle.activePlayer);
							}
						}				
						if (leastLoss != 100)
							monsterBonus += leastLoss;
						
						if (battle.isHelper()) {
							leastLoss = 100;
							for (TreasureCard item : battle.helper.getAllItems()) {
								if (item instanceof ItemCard) {
									if (((ItemCard)item).getBonus() < leastLoss)
										leastLoss = ((ItemCard)item).getBonus();
								}
								else if (item instanceof EquipmentCard) {
									EquipmentCard equipment = (EquipmentCard)item;
									if (battle.helper.hasEquipped(equipment) && equipment.getBonus(battle.helper) < leastLoss)
										leastLoss = ((EquipmentCard)item).getBonus(battle.helper);
								}
							}				
							if (leastLoss != 100)
								monsterBonus += leastLoss;
						}
					}
					else if (handMonster.getID() == Card.M_LAWYER) {
						if (battle.activePlayer.isThief() || (battle.isHelper() && battle.helper.isThief()))
							continue;
					}
					else if (handMonster.getID() == Card.M_AMAZON) {
						if (battle.activePlayer.isFemale() || (battle.isHelper() && battle.helper.isFemale()))
							continue;
					}
					else if (handMonster.getID() == Card.M_SQUIDZILLA) {
						if (battle.activePlayer.isElf())
							monsterBonus += 4;
						if (battle.isHelper() && battle.helper.isElf())
							monsterBonus += 4;
					}
					else if (handMonster.getID() == Card.M_FLOATING_NOSE) {
						for (Card item : battle.activePlayer.getCarriedItems())
							if (item.getID() == Card.I_POTION_OF_HALITOSIS)
								continue;
						
						if (battle.isHelper()) {
							for (Card item : battle.helper.getCarriedItems())
								if (item.getID() == Card.I_POTION_OF_HALITOSIS)
									continue;
						}
					}
					else if (handMonster.getID() == Card.M_WANNABE_VAMPIRE) {
						if (battle.activePlayer.isCleric() || (battle.isHelper() && battle.helper.isCleric()))
							continue;
					}
					
					monsterBonus += handMonster.getLevel(battle);
					if (monsterBonus > bestMonsterBonus) {
						bestMonsterBonus = monsterBonus;
						bestMonsterCard = handMonster;
					}
				}
				
				if (bestMonsterCard != null) {
					if (activePlayerWillWinGame || helperWillWinGame || bestMonsterBonus >= playerWinMargin) {
						battle.addMonster(bestMonsterCard);					
						player.getHandCards().remove(bestMonsterCard);
						player.discard(handCard);
						String message = player.getName() + " used the " + handCard + " card";
						message += " to add the " + bestMonsterCard + " to the battle.";
						Messenger.display(message, "Card From Hand Played");
						return true;
					}
				}
			}
			else if (handCard.getID() == Card.OD_MATE) {
				// Don't play if value of winning is not worth playing the card
				if (!activePlayerWillWinGame && !helperWillWinGame) {
					if (winValue <= AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards()))
						continue;
				}	
				
				int strongestMonsterLevel = monsterInfo.getBattleMonsters().getFirst().battleLevel;				
				if (activePlayerWillWinGame || helperWillWinGame || strongestMonsterLevel >= playerWinMargin){
					player.getHandCards().remove(handCard);
					battle.addMonsterEnhancer(monsterInfo.getBattleMonsters().getFirst().card, (OtherDoorCard)handCard);
					String message = player.getName() + " played " + handCard;
					message += " on the " + monsterInfo.getBattleMonsters().getFirst().card + ".";
					Messenger.display(message, "Card From Hand Played");
					return true;
				}
			}
		}
		
		for (ItemCard item: player.getCarriedItems())
			if (item.getID() == Card.I_FRIENDSHIP_POTION) {
				int newWinValue = winValue;
				newWinValue -= monsterInfo.getPlayerBadStuffCost(battle.activePlayer) * activePlayerFactor;
				if (battle.isHelper())
					newWinValue -= monsterInfo.getPlayerBadStuffCost(battle.helper) * helperFactor;
				boolean worthIt = (newWinValue > AIManager.LEVEL_VALUE * 3);
				// Only player if someone would win the game or gain more than the value of 3 levels.
				if (activePlayerWillWinGame || helperWillWinGame || worthIt) {
					String message = player.getName() + " used the " + item;
					Messenger.display(message, "Battle Item Used");
					return CardPlayManager.playCard(player, item, battle);
				}
			}
		
		for (Card handCard : player.getHandCards())
			if (handCard.getID() == Card.OD_OUT_TO_LUNCH) {
				int newWinValue = winValue;
				newWinValue -= monsterInfo.getPlayerBadStuffCost(battle.activePlayer) * activePlayerFactor;
				if (battle.isHelper())
					newWinValue -= monsterInfo.getPlayerBadStuffCost(battle.helper) * helperFactor;
				double treasureValue = AIManager.UNKNOWN_CARD_VALUE * 2;
				if (!battle.isHelper() || battle.activePlayer.hasEquipped(Card.E_KNEEPADS_OF_ALLURE))
					treasureValue *= activePlayerFactor;
				else
					treasureValue = AIManager.UNKNOWN_CARD_VALUE * activePlayerFactor + AIManager.UNKNOWN_CARD_VALUE * helperFactor;
				boolean worthIt = (newWinValue - treasureValue > AICardEvaluator.getCardValueToPlayer(handCard, player, player.getHandCards()));
				if (activePlayerWillWinGame || helperWillWinGame || worthIt) {
					String message = player.getName() + " played the " + handCard + " card.";
					Messenger.display(message, "Card From Hand Played");
					return CardPlayManager.playCard(player, handCard, battle);
				}
			}
		
		return false;
	}
	
	/**
	 * Returns how many battle levels a specified player will lose from the given curse. 
	 * @param curse the curse card to check
	 * @param victim the player in the battle to check the curse on
	 * @param battle the battle that this takes place in
	 * @return the number of battle levels will be lost from the curse
	 */
	public static int getCurseLevelLoss(CurseCard curse, Player victim, Battle battle) {
		int curseLoss = 0;
		
		if (curse.getID() == Card.CU_CHANGE_CLASS) {
			ClassCard newClass = null;
			Player otherPlayer = (victim == battle.activePlayer) ? battle.helper : battle.activePlayer;
			
			if (!victim.getClassCards().isEmpty()) {
				for (int discardIdx = GM.getDoorDeck().getDiscardPile().size() - 1; discardIdx >= 0; discardIdx--) {
					Card card = GM.getDoorDeck().getDiscardPile().get(discardIdx);
					if (card instanceof ClassCard) {
						newClass = (ClassCard)card;
						if (!victim.isThief() && newClass.getCharacterClass() == Class.THIEF && victim.hasEquipped(Card.E_SINGING_AND_DANCING_SWORD)) {
							if (victim.getCheatingItemCard() == null || victim.getCheatingItemCard().getID() != Card.E_SINGING_AND_DANCING_SWORD)
								curseLoss += 2;
						}
						else if (!victim.isWarrior() && newClass.getCharacterClass() == Class.WARRIOR) {
							if (otherPlayer == null || !otherPlayer.isWarrior())
								curseLoss -= 1;
							if (victim.hasEquipped(Card.E_PANTYHOSE_OF_GIANT_STRENGTH))
								if (victim.getCheatingItemCard() == null || victim.getCheatingItemCard().getID() != Card.E_PANTYHOSE_OF_GIANT_STRENGTH)
									curseLoss += 3;
						}
						else if (!victim.isWizard() && newClass.getCharacterClass() == Class.WIZARD && victim.hasEquipped(Card.E_MITHRIL_ARMOR)) {
							if (victim.getCheatingItemCard() == null || victim.getCheatingItemCard().getID() != Card.E_MITHRIL_ARMOR)
								curseLoss += 3;
						}
						break;
					}								
				}
				
				for (ClassCard lostClassCard : victim.getClassCards()) {
					if (newClass != null && lostClassCard.getCharacterClass() == newClass.getCharacterClass())
						continue;
					
					if (lostClassCard.getCharacterClass() == Class.WARRIOR)
						if (otherPlayer == null || !otherPlayer.isWarrior())
							curseLoss += 1;
					
					for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++)
						if (otherPlayer == null || !otherPlayer.isClass(lostClassCard.getCharacterClass()))
							curseLoss -= battle.getMonster(monsterIdx).getClassBonus(lostClassCard.getCharacterClass());
					
					for (EquipmentCard equipment : victim.getEquippedItems())
						if (victim.getCheatingItemCard() != equipment)
							curseLoss += equipment.getBonusToClass(lostClassCard.getCharacterClass());
				}
			}			
		}
		else if (curse.getID() == Card.CU_CHANGE_RACE) {
			RaceCard newRace = null;
			Player otherPlayer = (victim == battle.activePlayer) ? battle.helper : battle.activePlayer;
			
			if (!victim.getRaceCards().isEmpty()) {
				for (int discardIdx = GM.getDoorDeck().getDiscardPile().size() - 1; discardIdx >= 0; discardIdx--) {
					Card card = GM.getDoorDeck().getDiscardPile().get(discardIdx);
					if (card instanceof RaceCard) {
						newRace = (RaceCard)card;
						break;
					}								
				}
				
				for (RaceCard lostRaceCard : victim.getRaceCards()) {
					if (newRace != null && lostRaceCard.getRace() == newRace.getRace())
						continue;			
					
					for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
						if (otherPlayer == null || !otherPlayer.isRace(lostRaceCard.getRace()))
							curseLoss -= battle.getMonster(monsterIdx).getRaceBonus(lostRaceCard.getRace());

						if (battle.getMonster(monsterIdx).getID() == Card.M_SQUIDZILLA)
							if (lostRaceCard.getRace() == Race.ELF)
								curseLoss -= 4;
					}
					
					for (EquipmentCard equipment : victim.getEquippedItems())
						if (victim.getCheatingItemCard() != equipment)
							curseLoss += equipment.getBonusToRace(lostRaceCard.getRace());
				}
			}
		}
		else if (curse.getID() == Card.CU_CHANGE_SEX) {
			if (victim.isMale() && victim.hasEquipped(Card.E_GENTLEMENS_CLUB))
				if (victim.getCheatingItemCard() == null || victim.getCheatingItemCard().getID() != Card.E_GENTLEMENS_CLUB)
					curseLoss += 3;
			
			if (victim.isFemale() && victim.hasEquipped(Card.E_BROAD_SWORD))
				if (victim.getCheatingItemCard() == null || victim.getCheatingItemCard().getID() != Card.E_BROAD_SWORD)
					curseLoss += 3;
			
			curseLoss += 5;
		}
		else if (curse.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD)
			return 0;
		else if (curse.getID() == Card.CU_DUCK_OF_DOOM) {
			if (victim.getLevel() >= 3)
				curseLoss = 2;
			else if (victim.getLevel() == 2)
				curseLoss = 1;
		}
		else if (curse.getID() == Card.CU_INCOME_TAX) {
			int leastPossibleLoss = 99;
			if (!victim.getUnequippedItems().isEmpty())
				return 0;
			
			for (TreasureCard item : victim.getAllItems()) {
				if (item instanceof ItemCard) {
					if (((ItemCard)item).getBonus() < leastPossibleLoss)
						leastPossibleLoss = ((ItemCard)item).getBonus();
				}
				else if (item instanceof EquipmentCard) {
					EquipmentCard equipment = (EquipmentCard)item;
					if (victim.hasEquipped(equipment) && equipment.getBonus(victim) < leastPossibleLoss)
						leastPossibleLoss = ((EquipmentCard)item).getBonus(victim);
				}
			}				
			if (leastPossibleLoss == 99)
				return 0;
			
			curseLoss = leastPossibleLoss;
		}
		else if (curse.getID() == Card.CU_LOSE_1_BIG_ITEM) {
			int leastLoss = 99;
			for (EquipmentCard equipment : victim.getUnequippedItems())
				if (equipment.isBig())
					return 0;
			
			for (EquipmentCard equipment : victim.getBigItems()) {
				if (!victim.hasEquipped(equipment))
					return 0;
				else if (equipment.getBonus(victim) < leastLoss)
					leastLoss = equipment.getBonus(victim);
			}
			if (leastLoss == 99)
				return 0;
			
			curseLoss = leastLoss;
		}
		else if (curse.getID() == Card.CU_LOSE_1_LEVEL_1 || curse.getID() == Card.CU_LOSE_1_LEVEL_2) {
			if (victim.getLevel() >= 2)
				curseLoss = 1;
		}
		else if (curse.getID() == Card.CU_LOSE_1_SMALL_ITEM_1 || curse.getID() == Card.CU_LOSE_1_SMALL_ITEM_2) {
			int leastLoss = 99;
			for (EquipmentCard equipment : victim.getUnequippedItems())
				if (!equipment.isBig())
					return 0;
			
			for (TreasureCard item : victim.getAllItems()) {
				if (item instanceof ItemCard) {
					if (((ItemCard)item).getBonus() < leastLoss)
						leastLoss = ((ItemCard)item).getBonus();
				}
				else if (item instanceof EquipmentCard) {
					EquipmentCard equipment = (EquipmentCard)item;
					if (!equipment.isBig() && victim.hasEquipped(equipment) && equipment.getBonus(victim) < leastLoss)
						leastLoss = ((EquipmentCard)item).getBonus(victim);
				}
			}				
			if (leastLoss == 99)
				return 0;
			
			curseLoss = leastLoss;
		}
		else if (curse.getID() == Card.CU_LOSE_THE_ARMOR_YOU_ARE_WEARING) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.ARMOR)
					curseLoss += equipment.getBonus(victim);
		}
		else if (curse.getID() == Card.CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.FOOTGEAR)
					curseLoss += equipment.getBonus(victim);
		}
		else if (curse.getID() == Card.CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR)
					curseLoss += equipment.getBonus(victim);
		}
		else if (curse.getID() == Card.CU_LOSE_TWO_CARDS)
			return 0;
		else if (curse.getID() == Card.CU_LOSE_YOUR_CLASS) {
			Player otherPlayer = (victim == battle.activePlayer) ? battle.helper : battle.activePlayer;
			
			if (victim.getClassCards().isEmpty()) {
				if (victim.getLevel() >= 2)
					curseLoss = 1;
			}
				
			for (ClassCard lostClassCard : victim.getClassCards()) {
				if (lostClassCard.getCharacterClass() == Class.WARRIOR)
					if (otherPlayer == null || !otherPlayer.isWarrior())
						curseLoss += 1;
				
				for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++)
					if (otherPlayer == null || !otherPlayer.isClass(lostClassCard.getCharacterClass()))
						curseLoss -= battle.getMonster(monsterIdx).getClassBonus(lostClassCard.getCharacterClass());
				
				for (EquipmentCard equipment : victim.getEquippedItems())
					if (victim.getCheatingItemCard() != equipment)
						curseLoss += equipment.getBonusToClass(lostClassCard.getCharacterClass());
			}	
		}
		else if (curse.getID() == Card.CU_LOSE_YOUR_RACE) {
			Player otherPlayer = (victim == battle.activePlayer) ? battle.helper : battle.activePlayer;
			
			if (victim.getRaceCards().isEmpty())
				return 0;
			
			for (RaceCard lostRaceCard : victim.getRaceCards()) {
				for (int monsterIdx = 0; monsterIdx < battle.getMonsterCount(); monsterIdx++) {
					if (otherPlayer == null || !otherPlayer.isRace(lostRaceCard.getRace()))
						curseLoss -= battle.getMonster(monsterIdx).getRaceBonus(lostRaceCard.getRace());

					if (battle.getMonster(monsterIdx).getID() == Card.M_SQUIDZILLA)
						if (lostRaceCard.getRace() == Race.ELF)
							curseLoss -= 4;
				}
				
				for (EquipmentCard equipment : victim.getEquippedItems())
					if (victim.getCheatingItemCard() != equipment)
						curseLoss += equipment.getBonusToRace(lostRaceCard.getRace());
			}
		}
		else if (curse.getID() == Card.CU_MALIGN_MIRROR) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.isWeapon())
					curseLoss += equipment.getBonus(victim);
		}
		else if (curse.getID() == Card.CU_TRULY_OBNOXIOUS_CURSE) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getBonus(victim) > curseLoss)
					curseLoss = equipment.getBonus(victim);
		}
		
		return curseLoss;
	}
	
	/**
	 * Decide for the given player the best monster from his hand to battle and return that monster.
	 * @param player player to decide for
	 * @return the monster card from the player's hand that would be best to battle; null if better not to fight one or if player doesn't have any monster cards
	 */
	public static MonsterCard getMonsterToBattle(Player player) {
		int maxBonus = 0;
		boolean canEscape = false;
		boolean canTakeTreasures = false;
		boolean canKillNose = false;
		
		for (ItemCard item : player.getCarriedItems()) {
			if (item.getID() == Card.I_DOPPLEGANGER)
				maxBonus += player.getLevel() + player.getEquipmentBonus();
			else if (item.getID() == Card.I_FRIENDSHIP_POTION || 
					 item.getID() == Card.I_INVISIBILITY_POTION ||
					 item.getID() == Card.I_INSTANT_WALL ||
					 item.getID() == Card.I_LOADED_DIE ||
					 item.getID() == Card.I_TRANSFERRAL_POTION) {
				canEscape = true;
			}
			else if (item.getID() == Card.I_MAGIC_LAMP_1 ||
					 item.getID() == Card.I_MAGIC_LAMP_2 ||
					 item.getID() == Card.I_POLLYMORPH_POTION) {
				canTakeTreasures = true;
			}
			else if (item.getID() == Card.I_POTION_OF_HALITOSIS) {
				canKillNose = true;
				maxBonus += item.getBonus();
			}
			else if (item.getID() == Card.I_YUPPIE_WATER && player.isElf())
				maxBonus += 2;
			else
				maxBonus += item.getBonus();
		}
		
		int handCardSize = player.getHandCards().size() - 1;
		for (Card handCard : player.getHandCards())
			if (handCard.getID() == Card.ME_BABY) {
				maxBonus += 5;
				handCardSize--;
			}
		
		if (player.isWizard() && handCardSize > 0)
			canTakeTreasures = true;
		
		if (player.isWarrior()) {
			if (handCardSize >= 3)
				maxBonus += 3;
			else
				maxBonus += handCardSize;
		}
		
		for (Card card : player.getHandCards()) {
			if (card instanceof MonsterCard) {
				MonsterCard monster = (MonsterCard)card;
				if (canKillNose && monster.getID() == Card.M_FLOATING_NOSE)
					return monster;
				
				int playerBattleLevel = player.getLevel() + player.getEquipmentBonus();
				if (player.isWarrior())
					playerBattleLevel++;
				int monsterBattleLevel = monster.getLevel(player);
				int winMargin = playerBattleLevel - monsterBattleLevel;
				
				if (winMargin > 5)
					return monster;
				
				LinkedList<Card> handCards = new LinkedList<Card>();
				handCards.addAll(player.getHandCards());
				handCards.remove(card);
				int badStuffCost = AIManager.getBadStuffCost(monster, false, player, false, handCards);
				if (winMargin > 0 && (canEscape || canTakeTreasures || badStuffCost < AIManager.LEVEL_VALUE / 2))
					return monster;
				
				if (monster.getTreasures() >= 4 && canTakeTreasures)
					return monster;
				
				if (player.isCleric() && monster.isUndead()) {
					int bonus = 3;
					// if warrior-cleric, treat discard bonuses as turning rather than berserking
					if (player.isWarrior())
						bonus = 2;
					if (player.getHandCards().size() >= 3)
						maxBonus += 3 * bonus;
					else
						maxBonus += player.getHandCards().size() * bonus;
				}
				
				if (winMargin + maxBonus > 10)
					return monster;
				
				if (winMargin + maxBonus > 5 && (canEscape || badStuffCost < 300))
					return monster;
			}
		}
		
		return null;
	}
}
