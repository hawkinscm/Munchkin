package ai;

import java.util.Iterator;
import java.util.LinkedList;

import exceptions.EndGameException;

import gui.CurseDialog;
import gui.DisplayCardsDialog;
import gui.components.Messenger;

import model.CardPlayManager;
import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.Randomizer;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.RaceCard;
import model.card.TreasureCard;
import model.card.EquipmentCard.EquipmentType;

/**
 * Class for handling the AI evaluations/situations for various situations.
 */
public class AIManager {
	
	public static final int UNKNOWN_CARD_VALUE = 800;
	public static final int LEVEL_VALUE = 1000;
			
	// Needed for the getLeastValuedGPItemList and findLeastValuedCardList methods
	private static LinkedList<TreasureCard> leastValuedItemList;
	private static int totalLeastValuedWorth;
	private static LinkedList<AIValuedCard> mostGPValuedItems;
	private static int minGPValueNeeded;
		
	/**
	 * Returns the cost to the given player of dropping the given number of levels.
	 * @param player the player to check
	 * @param levelsToDrop the number of lost levels to evaluate
	 * @return the cost to the given player of dropping the given number of levels
	 */
	private static int getLoseLevelsCost(Player player, int levelsToLose) {
		// a player can't drop below level 1, so each level that would push him below that is not a cost.
		if (player.getLevel() <= levelsToLose)
			levelsToLose -= (levelsToLose + 1) - player.getLevel();
		
		return levelsToLose * LEVEL_VALUE;
	}
	
	/**
	 * Returns the value that the player will lose by dying.  Can be negative if player gains from death.
	 * @param player player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the value that the player will lose by dying; negative if value is gained by dying
	 */
	public static int getDeathCost(Player player, LinkedList<Card> handCards) {
		int dyingCost = -UNKNOWN_CARD_VALUE * 4;
		
		for (TreasureCard treasure : player.getAllItems())
			dyingCost += AICardEvaluator.getCardValueToPlayer(treasure, player, handCards);
		if (player.hasHireling())
			dyingCost += AICardEvaluator.getCardValueToPlayer(player.getHirelingCard(), player, handCards);
		
		if (handCards != null) {
			for (Card handCard: handCards)
				dyingCost += AICardEvaluator.getCardValueToPlayer(handCard, player, handCards);
		}
		else
			dyingCost += UNKNOWN_CARD_VALUE * player.getHandCards().size();
		
		if (player.hasChickenOnHead()) {
			dyingCost -= 300;
			if (player.isThief())
				dyingCost -= 200;
		}
		
		return dyingCost;
	}
	
	/**
	 * Returns a list of players ranked from those with the highest value (winning) to the lowest value (losing).
	 * @return a list of players starting with the highest valued player (winning) and ending with the lowest valued player (losing)
	 */
	public static LinkedList<Player> getRankedPlayers() {
		LinkedList<Player> rankedPlayers = new LinkedList<Player>();
		LinkedList<Integer> playerValues = new LinkedList<Integer>();
		for (Player player : GM.getPlayers()) {
			int playerValue = player.getLevel() * LEVEL_VALUE;
			playerValue += player.getHandCards().size() * UNKNOWN_CARD_VALUE;
			for (Card card : player.getAllItems())
				playerValue += AICardEvaluator.getCardValueToPlayer(card, player, null);
			
			for (RaceCard raceCard : player.getRaceCards())
				playerValue += AICardEvaluator.getCardValueToPlayer(raceCard, player, null);
			
			for (ClassCard classCard : player.getClassCards())
				playerValue += AICardEvaluator.getCardValueToPlayer(classCard, player, null);
			
			if (player.isSuperMunchkin())
				playerValue += AICardEvaluator.getCardValueToPlayer(player.getSuperMunchkinCard(), player, null);
			
			if (player.isHalfBreed())
				playerValue += AICardEvaluator.getCardValueToPlayer(player.getHalfBreedCard(), player, null);
			
			if (player.hasHireling())
				playerValue += AICardEvaluator.getCardValueToPlayer(player.getHirelingCard(), player, null);
			
			if (player.hasChickenOnHead()) {
				playerValue -= 300;
				if (player.isThief())
					playerValue -= 200;
			}
			
			if (player.hasDistractionCurse())
				playerValue -= 500;
			
			if (player.hasMalignMirror()) {
				for (EquipmentCard equippedItem : player.getEquippedItems())
					if (equippedItem.isWeapon())
						playerValue -= equippedItem.getBonus(player) * 100; 
			}
			
			if (player.getPlayerType() == PlayerType.COMPUTER_EASY)
				playerValue += 10;
			else if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
				playerValue += 20;
			else if (player.getPlayerType() == PlayerType.HUMAN)
				playerValue += 25;
			else if (player.getPlayerType() == PlayerType.COMPUTER_HARD)
				playerValue += 30;
			
			int playerIdx;
			for (playerIdx = 0; playerIdx < playerValues.size(); playerIdx++)
				if (playerValue > playerValues.get(playerIdx))
					break;
			
			rankedPlayers.add(playerIdx, player);
			playerValues.add(playerIdx, playerValue);
		}
		
		return rankedPlayers;
	}
	
	/**
	 * Returns the estimated loss value that the player would suffer from being hit by this curse.
	 * @param card the card representing the curse
	 * @param victim the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the estimated loss value that the player would suffer from being hit by this curse; negative if player will gain from curse
	 */
	public static int getCurseCost(CurseCard card, Player victim, LinkedList<Card> handCards) {
		int curseCost = 0;
		
		if (card.getID() == Card.CU_CHANGE_CLASS) {
			if (victim.getClassCards().isEmpty())
				return 0;
			
			ClassCard newClassCard = null;
			LinkedList<Card> doorDiscards = new LinkedList<Card>();
			for (Card doorCard : GM.getDoorDeck().getDiscardPile())
				doorDiscards.addFirst(doorCard);					
			for (Card doorCard : doorDiscards) {
				if (doorCard instanceof ClassCard) {
					newClassCard = (ClassCard)doorCard;
					break;
				}
			}
			
			boolean newClassFactoredIn = false;
			for (ClassCard classCard : victim.getClassCards()) {
				if (newClassCard == null || classCard.getCharacterClass() != newClassCard.getCharacterClass())
					curseCost += AICardEvaluator.getCardValueToPlayer(classCard, victim, handCards);
				else
					newClassFactoredIn = true;
			}
			if (victim.isSuperMunchkin())
				curseCost += AICardEvaluator.getCardValueToPlayer(victim.getSuperMunchkinCard(), victim, handCards);
			if (newClassCard != null && !newClassFactoredIn)
				curseCost -= AICardEvaluator.getCardValueToPlayer(newClassCard, victim, handCards);
			
			return curseCost;
		}
		else if (card.getID() == Card.CU_CHANGE_RACE) {
			if (victim.getRaceCards().isEmpty())
				return 0;
			
			RaceCard newRaceCard = null;
			LinkedList<Card> doorDiscards = new LinkedList<Card>();
			for (Card doorCard : GM.getDoorDeck().getDiscardPile())
				doorDiscards.addFirst(doorCard);					
			for (Card doorCard : doorDiscards) {
				if (doorCard instanceof RaceCard) {
					newRaceCard = (RaceCard)doorCard;
					break;
				}
			}
			
			boolean newRaceFactoredIn = false;
			for (RaceCard raceCard : victim.getRaceCards()) {
				if (newRaceCard == null || raceCard.getRace() != newRaceCard.getRace())
					curseCost += AICardEvaluator.getCardValueToPlayer(raceCard, victim, handCards);
				else
					newRaceFactoredIn = true;
			}
			if (victim.isHalfBreed())
				curseCost += AICardEvaluator.getCardValueToPlayer(victim.getHalfBreedCard(), victim, handCards);
			if (newRaceCard != null && !newRaceFactoredIn)
				curseCost -= AICardEvaluator.getCardValueToPlayer(newRaceCard, victim, handCards);
			
			return curseCost;
		}
		else if (card.getID() == Card.CU_CHANGE_SEX) {
			curseCost = 500;
			
			EquipmentCard maleOnlyItem = null;
			EquipmentCard femaleOnlyItem = null;
			for (EquipmentCard equipment : victim.getAllEquipment()) {
				if (equipment == victim.getCheatingItemCard())
					continue;
				else if (equipment.getID() == Card.E_GENTLEMENS_CLUB)
					maleOnlyItem = equipment;
				else if (equipment.getID() == Card.E_BROAD_SWORD)
					femaleOnlyItem = equipment;
			}
			
			if (maleOnlyItem != null || femaleOnlyItem != null) {				
				if (maleOnlyItem != null) {
					int itemValue = maleOnlyItem.getBonus(victim) * 400 - maleOnlyItem.getValue();
					if (victim.isMale())
						curseCost += itemValue;
					else
						curseCost -= itemValue;						
				}
				if (femaleOnlyItem != null) {
					int itemValue = femaleOnlyItem.getBonus(victim) * 400 - femaleOnlyItem.getValue();
					if (victim.isFemale())
						curseCost += itemValue;
					else
						curseCost -= itemValue;	
				}
			}
			
			return curseCost;
		}
		else if (card.getID() == Card.CU_CHICKEN_ON_YOUR_HEAD) {
			curseCost = 300;
			if (victim.isThief())
				curseCost += 200;
		
			return curseCost;
		}
		else if (card.getID() == Card.CU_DUCK_OF_DOOM)
			return getLoseLevelsCost(victim, 2);
		else if (card.getID() == Card.CU_INCOME_TAX) {
			AIValuedCard leastWorthItem = null;
			for (Card item : victim.getAllItems()) {
				int itemValue = AICardEvaluator.getCardValueToPlayer(item, victim, handCards);
				if (leastWorthItem == null || itemValue < leastWorthItem.getValue())
					leastWorthItem = new AIValuedCard(item, itemValue);
			}			
			if (leastWorthItem == null)
				return 0;
			
			return leastWorthItem.getValue();
		}
		else if (card.getID() == Card.CU_LOSE_1_BIG_ITEM) {
			AIValuedCard leastWorthBigItem = null;
			for (EquipmentCard equipment : victim.getAllEquipment()) {
				if (!equipment.isBig())
					continue;
				
				int equipmentValue = AICardEvaluator.getCardValueToPlayer(equipment, victim, handCards);
				if (leastWorthBigItem == null || equipmentValue < leastWorthBigItem.getValue())
					leastWorthBigItem = new AIValuedCard(equipment, equipmentValue);
			}			
			if (leastWorthBigItem == null)
				return 0;
			
			return leastWorthBigItem.getValue();
		}
		else if (card.getID() == Card.CU_LOSE_1_LEVEL_1 || card.getID() == Card.CU_LOSE_1_LEVEL_2)
			return getLoseLevelsCost(victim, 1);
		else if (card.getID() == Card.CU_LOSE_1_SMALL_ITEM_1 || card.getID() == Card.CU_LOSE_1_SMALL_ITEM_2) {
			AIValuedCard leastWorthItem = null;
			for (Card item : victim.getAllItems()) {
				if (item instanceof EquipmentCard && ((EquipmentCard)item).isBig())
					continue;
				
				int itemValue = AICardEvaluator.getCardValueToPlayer(item, victim, handCards);
				if (leastWorthItem == null || itemValue < leastWorthItem.getValue())
					leastWorthItem = new AIValuedCard(item, itemValue);
			}			
			if (leastWorthItem == null)
				return 0;
			
			return leastWorthItem.getValue();
		}		
		else if (card.getID() == Card.CU_LOSE_THE_ARMOR_YOU_ARE_WEARING) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentType.ARMOR)
					curseCost += AICardEvaluator.getCardValueToPlayer(equipment, victim, handCards);
				
			return curseCost;
		}
		else if (card.getID() == Card.CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentType.FOOTGEAR)
					curseCost += AICardEvaluator.getCardValueToPlayer(equipment, victim, handCards);
				
			return curseCost;
		}
		else if (card.getID() == Card.CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING) {
			for (EquipmentCard equipment : victim.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentType.HEADGEAR)
					curseCost += AICardEvaluator.getCardValueToPlayer(equipment, victim, handCards);
				
			if (victim.hasChickenOnHead()) {
				curseCost -= 300;
				if (victim.isThief())
					curseCost -= 200;
			}
				
			return curseCost;
		}
		else if (card.getID() == Card.CU_LOSE_TWO_CARDS) {
			LinkedList<AIValuedCard> leastValuedHandCards = getLeastValuedHandCards(victim, handCards);
			if (!leastValuedHandCards.isEmpty())
				curseCost += leastValuedHandCards.removeFirst().getValue();
			if (!leastValuedHandCards.isEmpty())
				curseCost += leastValuedHandCards.removeFirst().getValue();
				
			return curseCost;
		}
		else if (card.getID() == Card.CU_LOSE_YOUR_CLASS) {
			if (victim.getClassCards().isEmpty())
				return getLoseLevelsCost(victim, 1);
			
			Iterator<ClassCard> classIter = victim.getClassCards().iterator();
			int leastClassValue = AICardEvaluator.getCardValueToPlayer(classIter.next(), victim, handCards);			
			while (classIter.hasNext()) {
				int classValue = AICardEvaluator.getCardValueToPlayer(classIter.next(), victim, handCards);
				if (classValue < leastClassValue)
					leastClassValue = classValue;
			}
			curseCost += leastClassValue;
			if (victim.isSuperMunchkin())
				curseCost += AICardEvaluator.getCardValueToPlayer(victim.getSuperMunchkinCard(), victim, handCards);
			
			return curseCost;
		}
		else if (card.getID() == Card.CU_LOSE_YOUR_RACE) {
			for (RaceCard raceCard : victim.getRaceCards())
				curseCost += AICardEvaluator.getCardValueToPlayer(raceCard, victim, handCards);
			if (victim.isHalfBreed())
				curseCost += AICardEvaluator.getCardValueToPlayer(victim.getHalfBreedCard(), victim, handCards);
			
			return curseCost;
		}
		else if (card.getID() == Card.CU_MALIGN_MIRROR) {
			for (EquipmentCard equippedItem : victim.getEquippedItems())
				if (equippedItem.isWeapon())
					curseCost += equippedItem.getBonus(victim) * 100;
			
			return curseCost;
		}
		else if (card.getID() == Card.CU_TRULY_OBNOXIOUS_CURSE) {
			int biggestBonus = 0;
			for (EquipmentCard equipment : victim.getEquippedItems()) {
				int currentCardBonus = equipment.getBonus(victim);
				if (currentCardBonus > biggestBonus)
					biggestBonus = currentCardBonus;
			}
				
			return biggestBonus * 400;
		}
	
		return 0;
	}

	/**
	 * Returns the estimated loss value that the player would suffer from trying to escape the monster's "bad stuff".
	 * Factors in the chance of escape and any known item cards and abilities. 
	 * @param monster monster to check
	 * @param hasMate whether or not the monster has a mate
	 * @param player player to check
	 * @param isFailedEscape if player has already failed to escape then bad stuff cost will be the full, inescapable cost
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the estimated loss value that the player would suffer from trying to escape the monster's "bad stuff"
	 */
	public static int getBadStuffCost(MonsterCard monster, boolean hasMate, Player player, boolean isFailedEscape, LinkedList<Card> handCards) {
		if (!isFailedEscape && monster.isAutoEscape(player))
			return 0;
		if (monster.getID() == Card.M_WANNABE_VAMPIRE && player.isCleric())
			return 0;
		
		double chanceToGetCaught = 1.0;
		if (!isFailedEscape) {
			int runAwayBonus = 0;	
				
			if (player.isElf())
				runAwayBonus++;	
			if (player.isWizard())
				runAwayBonus++;
			if (!player.isHuman() && player.isHalfling())
				runAwayBonus--;	
			
			if (player.hasEquipped(Card.E_TUBA_OF_CHARM))
				runAwayBonus++;
			if (player.hasEquipped(Card.E_BOOTS_OF_RUNNING_REALLY_FAST))
				runAwayBonus += 2;
			
			if (player.hasChickenOnHead())
				runAwayBonus--;
						
			if (monster.getID() == Card.M_FLYING_FROGS)
				runAwayBonus --;
			else if (monster.getID() == Card.M_GELATINOUS_OCTAHEDRON)
				runAwayBonus ++;
			else if (monster.getID() == Card.M_LAME_GOBLIN)
				runAwayBonus ++;
			else if (monster.getID() == Card.M_SNAILS_OF_SPEED)
				runAwayBonus -= 2;
			
			if (hasMate)
				runAwayBonus--;
				
			int maxFailRoll = 4 - runAwayBonus;
			if (maxFailRoll <= 0)
				return 0;
			else if (maxFailRoll >= 6)
				chanceToGetCaught = 1.0;
			else
				chanceToGetCaught = (double)maxFailRoll / 6.0;
		}
		
		int badStuffCost = 0;
		int escapeCost = 0;
		if (monster.getID() == Card.M_3872_ORCS) {
			// calculate chance to die versus chance of losing 3-6 levels
			double averageLevelLossCost = getLoseLevelsCost(player, 3);
			averageLevelLossCost += getLoseLevelsCost(player, 4);
			averageLevelLossCost += getLoseLevelsCost(player, 5);
			averageLevelLossCost += getLoseLevelsCost(player, 6);
			averageLevelLossCost /= 4.0;
			badStuffCost += ((double)getDeathCost(player, handCards) + (averageLevelLossCost * 2.0)) / 3.0;
		}
		else if (monster.getID() == Card.M_AMAZON) {
			if (player.isFemale())
				return 0;
			
			if (player.getClassCards().isEmpty())
				badStuffCost = getLoseLevelsCost(player, 3);
			else {
				for (ClassCard classCard : player.getClassCards())
					badStuffCost += AICardEvaluator.getCardValueToPlayer(classCard, player, handCards);
				if (player.isSuperMunchkin())
					badStuffCost += AICardEvaluator.getCardValueToPlayer(player.getSuperMunchkinCard(), player, handCards);
			}
		}
		else if (monster.getID() == Card.M_BIGFOOT) {
			for (EquipmentCard equipment : player.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR)
					badStuffCost += AICardEvaluator.getCardValueToPlayer(equipment, player, handCards);
			
			if (player.hasChickenOnHead()) {
				badStuffCost -= 300;
				if (player.isThief())
					badStuffCost -= 200;
			}
		}
		else if (monster.getID() == Card.M_BULLROG)
			badStuffCost = getDeathCost(player, handCards);
		else if (monster.getID() == Card.M_CRABS) {
			for (EquipmentCard equipment : player.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.ARMOR || equipment.isBelowWaist())
					badStuffCost += AICardEvaluator.getCardValueToPlayer(equipment, player, handCards);
			chanceToGetCaught = 1.0;
		}
		else if (monster.getID() == Card.M_DROOLING_SLIME) {
			boolean hasFootgear = false;
			for (EquipmentCard equipment : player.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.FOOTGEAR) {
					badStuffCost += AICardEvaluator.getCardValueToPlayer(equipment, player, handCards);
					hasFootgear = true;
				}
			if (!hasFootgear)
				badStuffCost = getLoseLevelsCost(player, 1);
		}
		else if (monster.getID() == Card.M_FACE_SUCKER) {
			badStuffCost = getLoseLevelsCost(player, 1);
			for (EquipmentCard equipment : player.getEquippedItems())
				if (equipment.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR)
					badStuffCost += AICardEvaluator.getCardValueToPlayer(equipment, player, handCards);
			
			if (player.hasChickenOnHead()) {
				badStuffCost -= 300;
				if (player.isThief())
					badStuffCost -= 200;
			}
		}
		else if (monster.getID() == Card.M_FLOATING_NOSE) {
			badStuffCost = getLoseLevelsCost(player, 3);
			chanceToGetCaught = 1.0;
		}		
		else if (monster.getID() == Card.M_FLYING_FROGS)
			badStuffCost = getLoseLevelsCost(player, 2);
		else if (monster.getID() == Card.M_GAZEBO)
			badStuffCost = getLoseLevelsCost(player, 3);
		else if (monster.getID() == Card.M_GELATINOUS_OCTAHEDRON) {
			for (EquipmentCard equipment : player.getAllEquipment())
				if (equipment.isBig())
					badStuffCost += AICardEvaluator.getCardValueToPlayer(equipment, player, handCards);
		}
		else if (monster.getID() == Card.M_GHOULFIENDS) {
			int levelDiff = player.getLevel() - GM.getLowestLevel();
			badStuffCost = getLoseLevelsCost(player, levelDiff);
		}
		else if (monster.getID() == Card.M_HARPIES)
			badStuffCost = getLoseLevelsCost(player, 2);
		else if (monster.getID() == Card.M_HIPPOGRIFF) {
			LinkedList<TreasureCard> treasures = player.getAllItems();
			if (player.hasHireling())
				treasures.add(player.getHirelingCard());
			
			int handTreasureCount = 0;
			int handTreasureValue = 0;
			if (handCards == null) {
				for (Card card : player.getHandCards())
					if (card instanceof TreasureCard) {
						handTreasureCount++;
						handTreasureValue += UNKNOWN_CARD_VALUE;
					}
			}
			else {
				for (Card card : handCards)
					if (card instanceof TreasureCard) {
						handTreasureCount++;
						handTreasureValue += AICardEvaluator.getCardValueToPlayer(card, player, handCards);
					}
			}
			if (handTreasureCount > 0)
				handTreasureValue /= handTreasureCount; 
			
			Player currentPlayer = GM.getPlayerRight(player);
			while (currentPlayer != player) {
				TreasureCard bestTreasure = null;
				int bestTreasureValue = 0;
				for (TreasureCard treasure : treasures) {
					int treasureValue = AICardEvaluator.getCardValueToPlayer(treasure, currentPlayer, null);
					if (bestTreasure == null || treasureValue > bestTreasureValue) {
						bestTreasure = treasure;
						bestTreasureValue = treasureValue;
					}
				}
				
				if (bestTreasure == null || (bestTreasureValue < 800 && handTreasureCount > 0)) {
					if (handTreasureCount > 0) {
						handTreasureCount--;
						badStuffCost += handTreasureValue;
					}
				}
				else {
					treasures.remove(bestTreasure);
					badStuffCost += AICardEvaluator.getCardValueToPlayer(bestTreasure, player, handCards);
				}
				
				currentPlayer = GM.getPlayerRight(currentPlayer);
			}
		}
		else if (monster.getID() == Card.M_INSURANCE_SALESMAN) {
			if (player.getAllItems().size() == 0)
				return 0;
			
			LinkedList<TreasureCard> leastValuedItemList = getLeastValuedGPItemList(player.getAllItems(), 1000, player, handCards);
			if (leastValuedItemList == null)
				leastValuedItemList = player.getAllItems();
			
			for (TreasureCard treasure: leastValuedItemList)
				badStuffCost += AICardEvaluator.getCardValueToPlayer(treasure, player, handCards);
		}
		else if (monster.getID() == Card.M_KING_TUT) {
			escapeCost = getLoseLevelsCost(player, 2);
			
			for (TreasureCard treasure : player.getAllItems())
				badStuffCost += AICardEvaluator.getCardValueToPlayer(treasure, player, handCards);
			
			if (handCards != null) {
				for (Card handCard : handCards)
					badStuffCost += AICardEvaluator.getCardValueToPlayer(handCard, player, handCards);
			}
			else
				badStuffCost += UNKNOWN_CARD_VALUE * player.getHandCards().size();
				
		}		
		else if (monster.getID() == Card.M_LAME_GOBLIN)
			badStuffCost = getLoseLevelsCost(player, 1);
		else if (monster.getID() == Card.M_LARGE_ANGRY_CHICKEN)
			badStuffCost = getLoseLevelsCost(player, 1);
		else if (monster.getID() == Card.M_LAWYER) {
			if (player.isThief())
				return 0;
						
			if (handCards != null) {
				for (Card handCard : handCards)
					badStuffCost += AICardEvaluator.getCardValueToPlayer(handCard, player, handCards);
			}
			else
				badStuffCost = UNKNOWN_CARD_VALUE * player.getHandCards().size();
		}
		else if (monster.getID() == Card.M_LEPERCHAUN) {
			LinkedList<AIValuedCard> leastValuedItems = getLeastValuedItems(player, handCards);
			if (!leastValuedItems.isEmpty())
				badStuffCost += leastValuedItems.removeLast().getValue();
			if (!leastValuedItems.isEmpty())
				badStuffCost += leastValuedItems.removeLast().getValue();
		}
		else if (monster.getID() == Card.M_MAUL_RAT)
			badStuffCost = getLoseLevelsCost(player, 1);
		else if (monster.getID() == Card.M_MR_BONES) {
			if (player.getLevel() == 1)
				return 0;
			
			escapeCost = getLoseLevelsCost(player, 1);
			int levelLoss = 2;
			if (player.getLevel() - 1 <= levelLoss)
				levelLoss -= levelLoss + 1 - (player.getLevel() - 1);
			badStuffCost = levelLoss * LEVEL_VALUE;
		}
		else if (monster.getID() == Card.M_NET_TROLL) {
			LinkedList<Player> topPlayers = GM.getHighestLevelPlayers();
			topPlayers.remove(player);
			LinkedList<TreasureCard> allItems = player.getAllItems();
			for (Player taker : topPlayers) {
				AIValuedCard bestValuedCard = null;
				for (TreasureCard treasure : allItems) {
					int treasureValue = AICardEvaluator.getCardValueToPlayer(treasure, taker, null);
					if (bestValuedCard == null || treasureValue > bestValuedCard.getValue())
						bestValuedCard = new AIValuedCard(treasure, treasureValue);
				}
				
				if (bestValuedCard != null) {
					allItems.remove(bestValuedCard.getCard());
					badStuffCost += AICardEvaluator.getCardValueToPlayer(bestValuedCard.getCard(), player, handCards);
				}
			}
		}
		else if (monster.getID() == Card.M_PIT_BULL)
			badStuffCost = getLoseLevelsCost(player, 2);
		else if (monster.getID() == Card.M_PLATYCORE)
			badStuffCost = getLoseLevelsCost(player, 2);
		else if (monster.getID() == Card.M_PLUTONIUM_DRAGON)
			badStuffCost = getDeathCost(player, handCards);
		else if (monster.getID() == Card.M_POTTED_PLANT)
			return 0;
		else if (monster.getID() == Card.M_SHRIEKING_GEEK) {
			for (RaceCard raceCard : player.getRaceCards())
				badStuffCost += AICardEvaluator.getCardValueToPlayer(raceCard, player, handCards);
			if (player.isHalfBreed())
				badStuffCost += AICardEvaluator.getCardValueToPlayer(player.getHalfBreedCard(), player, handCards);
			for (ClassCard classCard : player.getClassCards())
				badStuffCost += AICardEvaluator.getCardValueToPlayer(classCard, player, handCards);
			if (player.isSuperMunchkin())
				badStuffCost += AICardEvaluator.getCardValueToPlayer(player.getSuperMunchkinCard(), player, handCards);
		}		
		else if (monster.getID() == Card.M_SNAILS_OF_SPEED) {
			LinkedList<AIValuedCard> leastValuedCards = getLeastValuedHandCards(player, handCards);
						
			for (AIValuedCard valuedCard : getLeastValuedItems(player, handCards)) {
				int cardIdx;
				for (cardIdx = 0; cardIdx < leastValuedCards.size(); cardIdx++)
					if (valuedCard.getValue() < leastValuedCards.get(cardIdx).getValue())
						break;
				leastValuedCards.add(cardIdx, valuedCard);
			}
			if (leastValuedCards.isEmpty())
				return 0;
			
			int maxRoll = 6;
			if (player.hasChickenOnHead()) {
				maxRoll--;
				badStuffCost += leastValuedCards.getFirst().getValue();
			}
			while(leastValuedCards.size() > maxRoll)
				leastValuedCards.removeLast();
			
			for (int cardIdx = 6 - maxRoll; cardIdx < leastValuedCards.size(); cardIdx++)
				badStuffCost += leastValuedCards.get(cardIdx).getValue() * ((double)(maxRoll - cardIdx) / 6.0);
		}
		else if (monster.getID() == Card.M_SQUIDZILLA)
			badStuffCost = getDeathCost(player, handCards);
		else if (monster.getID() == Card.M_STONE_GOLEM)
			badStuffCost = getDeathCost(player, handCards);
		else if (monster.getID() == Card.M_THE_NOTHING) {
			if (handCards != null) {
				for (Card handCard : handCards)
					badStuffCost += AICardEvaluator.getCardValueToPlayer(handCard, player, handCards);
			}
			else
				badStuffCost = UNKNOWN_CARD_VALUE * player.getHandCards().size();
		}
		else if (monster.getID() == Card.M_TONGUE_DEMON) {
			int levelLoss = 2;
			if (!player.isHuman() && player.isElf())
				levelLoss++;
			
			badStuffCost = getLoseLevelsCost(player, levelLoss);
		}
		else if (monster.getID() == Card.M_UNDEAD_HORSE)
			badStuffCost = getLoseLevelsCost(player, 2);
		else if (monster.getID() == Card.M_UNSPEAKABLY_AWFUL_INDESCRIBABLE_HORROR) {
			if (player.isWizard()) {
				for (ClassCard classCard : player.getClassCards())
					if (classCard.getCharacterClass() == Class.WIZARD)
						badStuffCost = AICardEvaluator.getCardValueToPlayer(classCard, player, handCards);
				
				if (player.isSuperMunchkin())
					badStuffCost += AICardEvaluator.getCardValueToPlayer(player.getSuperMunchkinCard(), player, handCards);
			}
			else
				badStuffCost = getDeathCost(player, handCards);
		}
		else if (monster.getID() == Card.M_WANNABE_VAMPIRE)
			badStuffCost = getLoseLevelsCost(player, 3);
		else if (monster.getID() == Card.M_WIGHT_BROTHERS) {
			escapeCost = getLoseLevelsCost(player, 2);
			if (player.getLevel() > 3)
				badStuffCost = ((player.getLevel() - 2) - 1) * LEVEL_VALUE;
		}
		
		return (int)(badStuffCost * chanceToGetCaught) + escapeCost;
	}
	
	/**
	 * Returns a sorted list of the player's hand cards from least to greatest value.
	 * @param player p player to check
	 * @return a sorted list of the player's hand cards from least to greatest value
	 */
	public static LinkedList<AIValuedCard> getLeastValuedHandCards(Player player) {
		return getLeastValuedHandCards(player, player.getHandCards());
	}
	
	/**
	 * Returns a sorted list of the player's hand cards from least to greatest value.
	 * @param player p player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return a sorted list of the player's hand cards from least to greatest value
	 */
	public static LinkedList<AIValuedCard> getLeastValuedHandCards(Player player, LinkedList<Card> handCards) {
		LinkedList<AIValuedCard> leastValuedHandCards = new LinkedList<AIValuedCard>();
		if (handCards == null) {
			int smallestValue = UNKNOWN_CARD_VALUE - 100 * (player.getHandCards().size() / 2);
			for (Card card : player.getHandCards()) {
				leastValuedHandCards.add(new AIValuedCard(card, smallestValue));
				smallestValue += 100;
			}
		}
		else {
			for (Card handCard : handCards) {
				int cardValue = AICardEvaluator.getCardValueToPlayer(handCard, player, handCards);
				int cardIdx;
				for (cardIdx = 0; cardIdx < leastValuedHandCards.size(); cardIdx++)
					if (cardValue < leastValuedHandCards.get(cardIdx).getValue())
						break;
				leastValuedHandCards.add(cardIdx, new AIValuedCard(handCard, cardValue));
			}
		}
		
		return leastValuedHandCards;
	}
	
	/**
	 * Returns a sorted list of the player's items from least to greatest value.
	 * @param player p player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return a sorted list of the player's items from least to greatest value
	 */
	public static LinkedList<AIValuedCard> getLeastValuedItems(Player player, LinkedList<Card> handCards) {
		LinkedList<AIValuedCard> leastValuedItems = new LinkedList<AIValuedCard>();
		for (TreasureCard item : player.getAllItems()) {
			int itemValue = AICardEvaluator.getCardValueToPlayer(item, player, handCards);
			int itemIdx;
			for (itemIdx = 0; itemIdx < leastValuedItems.size(); itemIdx++)
				if (itemValue < leastValuedItems.get(itemIdx).getValue())
					break;
			leastValuedItems.add(itemIdx, new AIValuedCard(item, itemValue));
		}
		
		return leastValuedItems;
	}
	
	/**
	 * Returns a list of items that meets the minimum GP requirement and is worth the least to the player. 
	 * that meet the minimum GP value.
	 * @param allItems the list of items to search
	 * @param minGPValue the minimum GP requirement that the list of items must meet
	 * @param player player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the list of items that meets the minimum GP requirement and is worth the least to the player
	 */
	public static LinkedList<TreasureCard> getLeastValuedGPItemList(LinkedList<TreasureCard> allItems, int minGPValue, Player player, LinkedList<Card> handCards) {
		minGPValueNeeded = minGPValue;
		
		if (allItems.isEmpty())
			return null;
		
		int maxGPValue = 0;
		mostGPValuedItems = new LinkedList<AIValuedCard>();
		for (TreasureCard treasure : allItems) {
			maxGPValue += treasure.getValue();
			int treasureIdx;
			for (treasureIdx = 0; treasureIdx < mostGPValuedItems.size(); treasureIdx++)
				if (treasure.getValue() > ((TreasureCard)mostGPValuedItems.get(treasureIdx).getCard()).getValue())
					break;
			int cardValue = AICardEvaluator.getCardValueToPlayer(treasure, player, handCards);
			mostGPValuedItems.add(treasureIdx, new AIValuedCard(treasure, cardValue));
		}
		if (maxGPValue < minGPValue)
			return null;
		
		leastValuedItemList = new LinkedList<TreasureCard>();
		totalLeastValuedWorth = 0;
		findLeastValuedCardList(0, new LinkedList<TreasureCard>(), 0, 0);
		
		return leastValuedItemList;
	}
	
	/**
	 * Recursively searches all the given items to find the least worth combination of items 
	 * that meet a minimum GP value.  All possible combinations will be evaluated and the combination of
	 * cards worth the least to the player will set as leastValuedItemList.
	 * @param currentIdx the current index of the items to start searching on
	 * @param items the current list of items being weighed and evaluated
	 * @param totalGPWorth the current total GP value of the evaluated items
	 * @param totalCardWorth the current total card worth to owning player of the evaluated items 
	 */
	private static void findLeastValuedCardList(int currentIdx, LinkedList<TreasureCard> items, int totalGPWorth, int totalCardWorth) {
		AIValuedCard valuedCard = mostGPValuedItems.get(currentIdx);
		items.add((TreasureCard)valuedCard.getCard());
		totalGPWorth += ((TreasureCard)valuedCard.getCard()).getValue();
		totalCardWorth += valuedCard.getValue();
		if (totalGPWorth >= minGPValueNeeded) {
			if (leastValuedItemList.isEmpty() || totalCardWorth < totalLeastValuedWorth) {
				leastValuedItemList.clear();
				leastValuedItemList.addAll(items);
				totalLeastValuedWorth = totalCardWorth;
			}
		}
		else {
			for (int cardIdx = currentIdx + 1; cardIdx < mostGPValuedItems.size(); cardIdx++)
				findLeastValuedCardList(cardIdx, items, totalGPWorth, totalCardWorth);
		}
		
		totalCardWorth -= valuedCard.getValue();
		totalGPWorth -= ((TreasureCard)valuedCard.getCard()).getValue();
		items.remove((TreasureCard)valuedCard.getCard());
			
		for (int cardIdx = currentIdx + 1; cardIdx < mostGPValuedItems.size(); cardIdx++)
			findLeastValuedCardList(cardIdx, items, totalGPWorth, totalCardWorth);
	}
	
	/**
	 * Examines the given Race Card and plays it if its value is worth more played than in hand.
	 * @param player computer player whose hand will be examined/played
	 * @param raceCard Race Card to examine/play
	 * @param handValue the value of keeping this card in hand
	 */
	private static void playHandCard(Player player, RaceCard raceCard, int handValue) {
		if (raceCard.getRace() == Race.ELF && player.isElf())
			return;
		if (raceCard.getRace() == Race.DWARF && player.isDwarf())
			return;
		if (raceCard.getRace() == Race.HALFLING && player.isHalfling())
			return;
		
		int raceValue = AICardEvaluator.getCardValueToPlayer(raceCard, player, player.getHandCards());
		if (raceValue <= handValue)
			return;
		
		if (player.getRaceCards().size() >= 2) {
			int leastWorthRaceValue = 0;
			RaceCard leastWorthRace = null;
			for (RaceCard currentRace : player.getRaceCards()) {
				int currentRaceValue = AICardEvaluator.getCardValueToPlayer(currentRace, player, player.getHandCards());
				if (leastWorthRace == null || currentRaceValue < leastWorthRaceValue) {
					leastWorthRace = currentRace;
					leastWorthRaceValue = currentRaceValue;
				}
			}
			
			if (raceValue - leastWorthRaceValue > handValue) {
				String message = player + " has replaced the " + leastWorthRace + " Race Card";
				message += " with the " + raceCard + " Race Card.";
				Messenger.display(message, "Card From Hand Played");
				player.discard(leastWorthRace);
				try {
					CardPlayManager.playCard(player, raceCard);
				}
				catch (EndGameException ex) {}
				return;
			}
			else
				return;
		}
		else if (player.getRaceCards().size() == 1) {
			RaceCard currentRace = player.getRaceCards().getFirst();
			int replaceCost = AICardEvaluator.getCardValueToPlayer(currentRace, player, player.getHandCards());
			
			if (player.isHalfBreed()) {
				int additionCost = 100;
				for (EquipmentCard equipment : player.getEquippedItems()) {
					if (equipment == player.getCheatingItemCard())
						continue;
						
					int noRaceBonus = equipment.getBonusToRace(Race.HUMAN);
					if (noRaceBonus > 0)
						additionCost += noRaceBonus * 400 - equipment.getValue();
				}
				
				if (additionCost <= replaceCost) {
					if (raceValue - additionCost <= handValue)
						return;
				}
				else if (raceValue - replaceCost > handValue) {
					String message = player + " has replaced the " + currentRace + " Race Card";
					message += " with the " + raceCard + " Race Card";
					Messenger.display(message, "Card From Hand Played");
					try {
						CardPlayManager.playCard(player, raceCard);
					}
					catch (EndGameException ex) {}
					player.discardRaceCard(currentRace);
					return;
				}
				else
					return;
			}
			else if (raceValue - replaceCost > handValue) {
				String message = player + " has replaced the " + currentRace + " Race Card";
				message += " with the " + raceCard + " Race Card";
				Messenger.display(message, "Card From Hand Played");
				try {
					CardPlayManager.playCard(player, raceCard);
				}
				catch (EndGameException ex) {}
				return;				
			}
		}
		else {
			for (EquipmentCard equipment : player.getEquippedItems()) {
				if (equipment == player.getCheatingItemCard())
					continue;
					
				int noRaceBonus = equipment.getBonusToRace(Race.HUMAN);
				if (noRaceBonus > 0)
					raceValue -= noRaceBonus * 400 - equipment.getValue();
			}
			if (raceValue <= handValue)
				return;
		}				
		
		Messenger.display(player + " is now using the " + raceCard + " Race Card", "Card From Hand Played");
		try {
			CardPlayManager.playCard(player, raceCard);
		}
		catch (EndGameException ex) {}
	}
	
	/**
	 * Examines the given Class Card and plays it if its value is worth more played than in hand.
	 * @param player computer player whose hand will be examined/played
	 * @param classCard Class Card to examine/play
	 * @param handValue the value of keeping this card in hand
	 */
	private static void playHandCard(Player player, ClassCard classCard, int handValue) {
		if (classCard.getCharacterClass() == Class.CLERIC && player.isCleric())
			return;
		if (classCard.getCharacterClass() == Class.THIEF && player.isThief())
			return;
		if (classCard.getCharacterClass() == Class.WARRIOR && player.isWarrior())
			return;
		if (classCard.getCharacterClass() == Class.WIZARD && player.isWizard())
			return;				
		
		int classValue = AICardEvaluator.getCardValueToPlayer(classCard, player, player.getHandCards());
		if (classValue > handValue) {
			if (!player.getClassCards().isEmpty()) {
				int leastWorthClassValue = 0;
				ClassCard leastWorthClass = null;
				for (ClassCard currentClass : player.getClassCards()) {
					int currentClassValue = AICardEvaluator.getCardValueToPlayer(currentClass, player, player.getHandCards());
					if (leastWorthClass == null || currentClassValue < leastWorthClassValue) {
						leastWorthClass = currentClass;
						leastWorthClassValue = currentClassValue;
					}
				}
				
				if (classValue - leastWorthClassValue > handValue) {
					String message = player + " has replaced the " + leastWorthClass + " Class Card";
					message += " with the " + classCard + " Class Card.";
					Messenger.display(message, "Card From Hand Played");
					player.getClassCards().remove(leastWorthClass);
					GM.getDoorDeck().discard(leastWorthClass);
					try {
						CardPlayManager.playCard(player, classCard);
					}
					catch (EndGameException ex) {}
					return;
				}
				else
					return;
			}
			
			Messenger.display(player + " is now using the " + classCard + " Class Card", "Card From Hand Played");
			try {
				CardPlayManager.playCard(player, classCard);
			}
			catch (EndGameException ex) {}
		}
	}
		
	/**
	 * Examines the given Curse Card and plays it if its value is worth more played than in hand.
	 * @param player computer player whose hand will be examined/played
	 * @param curseCard Curse Card to examine/play
	 * @param handValue the value of keeping this card in hand
	 */
	private static void playHandCard(Player player, CurseCard curseCard, int handValue) {
		if (curseCard.getID() == Card.CU_INCOME_TAX) {
			int curseValue = 0;
			int GPValue = 0;
			Player bestVictim = null;					
			LinkedList<Player> rankedPlayers = getRankedPlayers();
			for (Player victim : rankedPlayers) {
				int leastGPValue = 0;
				for (TreasureCard item : victim.getAllItems())
					if (item.getValue() > leastGPValue)
						leastGPValue = item.getValue();
				
				if (leastGPValue > GPValue) {
					GPValue = leastGPValue;
					bestVictim = victim;
				}
			}					
			if (bestVictim == null)
				return;
			
			int numPlayers = rankedPlayers.size();
			int rankDivisor = (numPlayers + 1) / 2;
			for (int rankIdx = 0; rankIdx < numPlayers; rankIdx++) {
				Player victim = rankedPlayers.get(rankIdx);
				double rankFactor = (double)(numPlayers - rankIdx) / (double)rankDivisor;
				if (player == victim)
					rankFactor = -2.0;
				
				LinkedList<TreasureCard> gpItems = getLeastValuedGPItemList(victim.getAllItems(), GPValue, victim, null);
				if (gpItems == null) {
					for (TreasureCard item : victim.getAllItems())
						curseValue += AICardEvaluator.getCardValueToPlayer(item, victim, null) * rankFactor;
					
					curseValue += getLoseLevelsCost(victim, 1) * rankFactor;
				}
				else
					for (TreasureCard treasure : gpItems)
						curseValue += AICardEvaluator.getCardValueToPlayer(treasure, victim, null) * rankFactor;
			}
			
			if (curseValue > handValue) {
				String message = player + " played the " + curseCard + " curse";
				message += " on " + bestVictim + ".";
				Messenger.display(message, "Card From Hand Played");
				player.getHandCards().remove(curseCard);
				CurseDialog curseDialog = new CurseDialog(bestVictim, curseCard, false);
				curseDialog.setVisible(true);
			}
			
			return;
		}
		
		int bestCurseValue = 0;
		Player bestCurseVictim = null;				
		double rankFactor = 2.0;
		double rankDiff = 2.0 / GM.getPlayers().size();
		for (Player victim : getRankedPlayers()) {
			int curseValue = 0;
			if (player == victim)
				curseValue = -getCurseCost(curseCard, victim, player.getHandCards()) * 2;
			else
				curseValue = (int)(getCurseCost(curseCard, victim, null) * rankFactor);
			
			if (bestCurseVictim == null || curseValue > bestCurseValue) {
				bestCurseVictim = victim;
				bestCurseValue = curseValue;
			}
			
			if (player == victim && player.getPlayerType() == PlayerType.COMPUTER_EASY)
				break;
			
			rankFactor -= rankDiff;
		}
		
		if (bestCurseValue > handValue) {
			String message = player + " played the " + curseCard + " curse";
			message += " on " + bestCurseVictim + ".";
			Messenger.display(message, "Card From Hand Played");
			player.getHandCards().remove(curseCard);
			CurseDialog curseDialog = new CurseDialog(bestCurseVictim, curseCard, false);
			curseDialog.setVisible(true);
		}
	}
	
	/**
	 * Examine only the given hand card and play it if its value is worth more played than in hand.
	 * @param player computer player whose hand will be examined/played
	 * @param card hand card to examine/play
	 */
	public static void playHandCard(Player player, Card card) {
		if (card instanceof MonsterCard || card instanceof MonsterEnhancerCard)
			return;
		
		if (card.getID() == Card.OD_HELP_ME_OUT_HERE || 
			card.getID() == Card.OD_ILLUSION ||
			card.getID() == Card.OD_MATE ||
			card.getID() == Card.OD_OUT_TO_LUNCH ||
			card.getID() == Card.OD_WANDERING_MONSTER_1 ||
			card.getID() == Card.OD_WANDERING_MONSTER_2) {
			return;
		}
				
		int handValue = 10;
		if (player.getClassCards().size() > 0)
			handValue = 200;
		if (player.isWizard() && player.getHandCards().size() == 1)
			handValue += 400;
				
		try {
			if (card instanceof RaceCard)
				playHandCard(player, (RaceCard)card, handValue);
			else if (card instanceof ClassCard)
				playHandCard(player, (ClassCard)card, handValue);
			else if (card instanceof CurseCard)
				playHandCard(player, (CurseCard)card, handValue);
			else if (card instanceof EquipmentCard || card instanceof ItemCard) {
				if (card.getID() == Card.E_RAT_ON_A_STICK) {
					if ((handValue < 300 || player.getHandCards().size() >= 6) && ((EquipmentCard)card).equip(player).equals(""))
						if (CardPlayManager.playCard(player, card))
							if (player.getAllItems().contains(card))
								(new DisplayCardsDialog(card, player + " put the " + card + " in play.")).setVisible(true);
				}
				else if (AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards()) > handValue)
					if (CardPlayManager.playCard(player, card))
						if (player.getAllItems().contains(card))
							(new DisplayCardsDialog(card, player + " put the " + card + " in play.")).setVisible(true);
			}
			else if (card instanceof GoUpLevelCard) {
				if (card.getID() == Card.GUL_KILL_THE_HIRELING)
					if (player.hasHireling())
						if (AICardEvaluator.getCardValueToPlayer(player.getHirelingCard(), player, player.getHandCards()) > LEVEL_VALUE)
							return;
		
				if (player.getLevel() < 9)
					if (CardPlayManager.playCard(player, card))
						Messenger.display(player + " used the " + card + " to go up a level.", "Card From Hand Played");
			}
			else if (card.getID() == Card.OD_CHEAT) {
				EquipmentCard bestCheatEquipment = getBestCheatEquipment(player);
				if (bestCheatEquipment == null)
					return;
				
				player.getHandCards().remove(card);
				player.setCheatCards((OtherDoorCard)card, bestCheatEquipment);
				
				if (player.getHandCards().remove(bestCheatEquipment))
					player.addUnequippedItem(bestCheatEquipment);				
				player.equip(bestCheatEquipment);
				
				String message = player + " used the Cheat! card with the " + bestCheatEquipment + ".";
				Messenger.display(message, "Card From Hand Played");
			}
			else if (card.getID() == Card.OD_HALF_BREED_1 || card.getID() == Card.OD_HALF_BREED_2) {
				if (player.isHalfBreed())
					return;
				
				if (CardPlayManager.playCard(player, card)) {
					String message = player + " used the " + card + " card to become a Half-";
					message += player.getRaceCards().getFirst().getName() + ".";
					Messenger.display(message, "Card From Hand Played");
				}
			}
			else if (card.getID() == Card.OD_SUPER_MUNCHKIN_1 || card.getID() == Card.OD_SUPER_MUNCHKIN_2) {
				if (player.isSuperMunchkin())
					return;
				
				if (CardPlayManager.playCard(player, card)) {
					String message = player + " used the " + card + " card to become a ";
					message += player.getClassCards().getFirst().getName() + "-";
					message += player.getClassCards().getLast().getName() + ".";
					Messenger.display(message, "Card From Hand Played");
				}
			}
			else if (card.getID() == Card.OT_STEAL_A_LEVEL) {
				if (player.getPlayerType() != PlayerType.COMPUTER_HARD && player.getLevel() >= 9)
					return;
					
				for (Player victim : getRankedPlayers()) {
					if (player == victim || victim.getLevel() == 1)
						continue;
						
					player.discard(card);
					player.goUpLevel(false);
					victim.goDownLevel();
						
					String message = player + " used the " + card + " to steal a level from " + victim + ".";
					Messenger.display(message, "Card From Hand Played");
					return;
				}
			}
			else if (card.getID() == Card.OT_HIRELING) {
				if (AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards()) > handValue)
					if (CardPlayManager.playCard(player, card))
						Messenger.display(player + " put the " + card + " card in play.", "Card From Hand Played");
			}
		}
		catch (EndGameException ex) {}
	}
	
	/**
	 * Examine only the given hand cards and play any whose value is worth more played than in hand.
	 * @param player computer player whose hand will be examined/played
	 * @param cards hand cards to examine/play
	 */
	public static void playHandCards(Player player, LinkedList<Card> cardList) {
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(cardList);
		LinkedList<AIValuedCard> mostValuedCards = new LinkedList<AIValuedCard>();
		for (Card card : cards) {
			int cardValue = AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards());
			int cardIdx;
			for (cardIdx = 0; cardIdx < mostValuedCards.size(); cardIdx++)
				if (cardValue >= mostValuedCards.get(cardIdx).getValue())
					break;
			mostValuedCards.add(cardIdx, new AIValuedCard(card, cardValue));
		}
		
		int halfBreedIndex = -1;
		int newHalfBreedIndex = -1;		
		if (player.getRaceCards().size() > 0)
			newHalfBreedIndex = 0;				
		// must play Half-Breed card after 1st race card (in hand/or in play), but before any other race cards
		for (int cardIdx = 0; cardIdx < mostValuedCards.size(); cardIdx++) {
			Card card = mostValuedCards.get(cardIdx).getCard();
			
			if (card.getID() == Card.OD_HALF_BREED_1 || card.getID() == Card.OD_HALF_BREED_2)
				halfBreedIndex = cardIdx;
			else if (newHalfBreedIndex < 0 && card instanceof RaceCard)
				newHalfBreedIndex = cardIdx + 1;
		}		
		if (halfBreedIndex >= 0 && newHalfBreedIndex >= 0) {			
			if (newHalfBreedIndex > halfBreedIndex)
				newHalfBreedIndex--;
			mostValuedCards.add(newHalfBreedIndex, mostValuedCards.remove(halfBreedIndex));
		}
		
		int superMunchkinIndex = -1;
		int newSuperMunchkinIndex = -1;
		if (player.getClassCards().size() > 0)
			newSuperMunchkinIndex = 0;		
		// must play Super Munchkin card after 1st class card (in hand/or in play), but before any other class cards
		for (int cardIdx = 0; cardIdx < mostValuedCards.size(); cardIdx++) {
			Card card = mostValuedCards.get(cardIdx).getCard();
			
			if (card.getID() == Card.OD_SUPER_MUNCHKIN_1 || card.getID() == Card.OD_SUPER_MUNCHKIN_2)
				superMunchkinIndex = cardIdx;
			else if (newSuperMunchkinIndex < 0 && card instanceof ClassCard)
				newSuperMunchkinIndex = cardIdx + 1;
		}		
		if (superMunchkinIndex >= 0 && newSuperMunchkinIndex >= 0) {
			if (newSuperMunchkinIndex > superMunchkinIndex)
				newSuperMunchkinIndex--;
			mostValuedCards.add(newSuperMunchkinIndex, mostValuedCards.remove(superMunchkinIndex));
		}
		
		while (!mostValuedCards.isEmpty()) {
			// Prevent cards that were already played in conjecture with other cards from being duplicated.
			Card currentCard = mostValuedCards.removeFirst().getCard();
			if (player.getHandCards().contains(currentCard))
				playHandCard(player, currentCard);
		}
	}
	
	/**
	 * Examine hand cards and play any whose value is worth more played than in hand.
	 * @param player computer player whose hand will be examined/played
	 */
	public static void playHandCards(Player player) {
		playHandCards(player, player.getHandCards());
	}
	
	/**
	 * Returns the best equipment to cheat with or null if best not to use Cheat Card at this time.
	 * @param player player to decide for
	 * @return the best equipment to cheat with; null, if best not to use Cheat Card at this time
	 */
	private static EquipmentCard getBestCheatEquipment(Player player) {
		EquipmentCard bestCheatEquipment = null;
		int bestCheatValue = 0;
		
		LinkedList<EquipmentCard> allEquipment = player.getAllEquipment();
		for (Card handCard : player.getHandCards())
			if (handCard instanceof EquipmentCard)
				allEquipment.add((EquipmentCard)handCard);
		
		if (allEquipment.size() == player.getEquippedItems().size())
			return null;
			
		for (EquipmentCard equipment : allEquipment) {
			int restrictionCost = 0;
			if (equipment.isBig()) {
				if (player.isDwarf())
					restrictionCost += 1;
				else if (player.hasHireling())
					restrictionCost += 4;
				else
					restrictionCost += 8;
			}
								
			if (equipment.getEquipmentType() != EquipmentType.OTHER) {
				if (equipment.getEquipmentType() == EquipmentType.ONE_HAND)
					restrictionCost += 3;
				else
					restrictionCost += 5;
			}
			
			int cheatValue;
			if (equipment.getID() == Card.E_BROAD_SWORD) {
				if (player.isFemale())
					restrictionCost += 1;
				else
					restrictionCost += 20;
				cheatValue = 3 * restrictionCost;
			}					
			else if (equipment.getID() == Card.E_GENTLEMENS_CLUB) {
				if (player.isMale())
					restrictionCost += 1;
				else
					restrictionCost += 20;
				cheatValue = 3 * restrictionCost;
			}
			else if (equipment.getID() == Card.E_HORNED_HELMET) {
				if (player.isElf())
					restrictionCost += 1;
				else
					restrictionCost += 5;
				cheatValue = 3 * restrictionCost;
			}
			else if (equipment.getID() == Card.E_KNEEPADS_OF_ALLURE) {
				if (player.isCleric())
					restrictionCost += 8;
				else
					restrictionCost += 1;
				cheatValue = 6 * restrictionCost;
			}
			else if (equipment.getID() == Card.E_MITHRIL_ARMOR) {
				if (player.isWizard())
					restrictionCost += 8;
				else
					restrictionCost += 1;
				cheatValue = 3 * restrictionCost;
			}
			else if (equipment.getID() == Card.E_PANTYHOSE_OF_GIANT_STRENGTH)
				continue;
			else if (equipment.getID() == Card.E_SINGING_AND_DANCING_SWORD)
				continue;
			else if (equipment.getBonusToRace(Race.ELF) > 0) {
				if (player.isElf())
					restrictionCost += 2;
				else
					restrictionCost += 10;
				cheatValue = equipment.getBonusToRace(Race.ELF) * restrictionCost;
			}
			else if (equipment.getBonusToRace(Race.DWARF) > 0) {
				if (player.isDwarf())
					restrictionCost += 2;
				else
					restrictionCost += 10;
				cheatValue = equipment.getBonusToRace(Race.DWARF) * restrictionCost;
			}
			else if (equipment.getBonusToRace(Race.HALFLING) > 0) {
				if (player.isHalfling())
					restrictionCost += 3;
				else
					restrictionCost += 11;
				cheatValue = equipment.getBonusToRace(Race.HALFLING) * restrictionCost;
			}
			else if (equipment.getBonusToRace(Race.HUMAN) > 0) {
				if (player.isHuman())
					restrictionCost += 4;
				else
					restrictionCost += 12;
				cheatValue = equipment.getBonusToRace(Race.HUMAN) * restrictionCost;
			}
			else if (equipment.getBonusToClass(Class.CLERIC) > 0) {
				if (player.isCleric())
					restrictionCost += 2;
				else
					restrictionCost += 10;
				cheatValue = equipment.getBonusToClass(Class.CLERIC) * restrictionCost;
			}
			else if (equipment.getBonusToClass(Class.THIEF) > 0) {
				if (player.isThief())
					restrictionCost += 2;
				else
					restrictionCost += 10;
				cheatValue = equipment.getBonusToClass(Class.THIEF) * restrictionCost;
			}
			else if (equipment.getBonusToClass(Class.WARRIOR) > 0) {
				if (player.isWarrior())
					restrictionCost += 2;
				else
					restrictionCost += 10;
				cheatValue = equipment.getBonusToClass(Class.WARRIOR) * restrictionCost;
			}
			else if (equipment.getBonusToClass(Class.WIZARD) > 0) {
				if (player.isWizard())
					restrictionCost += 2;
				else
					restrictionCost += 10;
				cheatValue = equipment.getBonusToClass(Class.WIZARD) * restrictionCost;
			}					
			else 
				cheatValue = equipment.getBonus(player) * restrictionCost;
			
			if (bestCheatEquipment == null || cheatValue > bestCheatValue ||
			   (cheatValue == bestCheatValue && player.hasEquipped(bestCheatEquipment))) 
			{
				bestCheatEquipment = equipment;
				bestCheatValue = cheatValue;
			}
		}
		
		if (bestCheatEquipment != null && bestCheatValue >= 30)
			return bestCheatEquipment;
		
		return null;
	}
	
	/**
	 * Evaluates the player and equips the best possible equipment on him.
	 * @param player player to equip
	 */
	public static void equipBest(Player player) {		
		String gender = "his";
		if (player.isFemale())
			gender = "her";
		
		// if the value of a race/class is negative because it prevents powerful items from being equipped,
		// then discard that race/class
		RaceCard raceToDiscard = null;
		if (!player.isHuman()) {
			int humanValue = 0;
			for (EquipmentCard equipment : player.getAllEquipment())
				if (equipment.getBonusToRace(Race.HUMAN) > 0)
					humanValue += equipment.getBonusToRace(Race.HUMAN) * 400 - equipment.getValue();
			
			if (humanValue > 0) {
				RaceCard leastValuedRace = null;
				int leastRaceValue = 0;
				for (RaceCard raceCard : player.getRaceCards()) {
					int raceValue = AICardEvaluator.getCardValueToPlayer(raceCard, player, player.getHandCards());
					if (leastValuedRace == null || raceValue < leastRaceValue) {
						leastValuedRace = raceCard;
						leastRaceValue = raceValue;
					}
				}
				
				if (humanValue > leastRaceValue) {
					raceToDiscard = leastValuedRace;
					player.getRaceCards().remove(raceToDiscard);
				}
			}
		}
		
		int superMunchkinValue = 0;
		if (player.isSuperMunchkin())
			superMunchkinValue = AICardEvaluator.getCardValueToPlayer(player.getSuperMunchkinCard(), player, player.getHandCards());
		LinkedList<ClassCard> classCards = new LinkedList<ClassCard>();
		classCards.addAll(player.getClassCards());
		for (ClassCard classCard : classCards)
			if (superMunchkinValue + AICardEvaluator.getCardValueToPlayer(classCard, player, player.getHandCards()) < 0) {
				if (classCard.getCharacterClass() == Class.WIZARD) {
					boolean hasOtherArmor = false;
					for (EquipmentCard equipment : player.getAllEquipment())
						if (equipment.getEquipmentType() == EquipmentType.ARMOR && equipment.getID() != Card.E_MITHRIL_ARMOR) {
							hasOtherArmor = true;
							break;
						}
					
					if (hasOtherArmor)
						continue;
				}				
				
				player.discardClassCard(classCard);
				Messenger.display(player + " discarded " + gender + " " + classCard + " Class.", "Class Discarded");
				superMunchkinValue = 0;
			}
		// end Race/Class discard evaluation
		
		LinkedList<EquipmentCard> availableEquipment = player.getAllEquipment();
		
		// equip unrestricted equipment
		Iterator<EquipmentCard> equipmentIter = availableEquipment.iterator();
		while (equipmentIter.hasNext()) {
			EquipmentCard equipment = equipmentIter.next();
			if (equipment.getEquipmentType() == EquipmentType.OTHER || player.getCheatingItemCard() == equipment) {
				player.equip(equipment);
				equipmentIter.remove();
			}
		}
		
		// equip headgear
		equipmentIter = availableEquipment.iterator();
		EquipmentCard bestHeadgear = null;
		while (equipmentIter.hasNext()) {
			EquipmentCard equipment = equipmentIter.next();
			if (equipment.getEquipmentType() == EquipmentType.HEADGEAR) {
				if (equipment.equip(player) == "") {
					if (bestHeadgear == null || equipment.getBonus(player) > bestHeadgear.getBonus(player))
						bestHeadgear = equipment;
				}
				
				player.unequip(equipment);
				equipmentIter.remove();
			}				
		}
		if (bestHeadgear != null)
			player.equip(bestHeadgear);
		
		// equip armor
		equipmentIter = availableEquipment.iterator();
		EquipmentCard bestArmor = null;
		while (equipmentIter.hasNext()) {
			EquipmentCard equipment = equipmentIter.next();
			if (equipment.getEquipmentType() == EquipmentType.ARMOR) {
				if (equipment.equip(player) == "") {
					if (bestArmor == null || equipment.getBonus(player) > bestArmor.getBonus(player))
						bestArmor = equipment;
				}
				
				player.unequip(equipment);
				equipmentIter.remove();
			}				
		}
		if (bestArmor != null)
			player.equip(bestArmor);
		
		// equip hands
		equipmentIter = availableEquipment.iterator();
		EquipmentCard bestRightHand = null;
		EquipmentCard bestLeftHand = null;
		EquipmentCard bestBothHands = null;
		while (equipmentIter.hasNext()) {
			EquipmentCard equipment = equipmentIter.next();
			if (equipment.equip(player) != "") {
				equipmentIter.remove();
				continue;
			}
			
			if (equipment.getEquipmentType() == EquipmentType.ONE_HAND) {
				if (bestRightHand == null || equipment.getBonus(player) > bestRightHand.getBonus(player)) {
					bestLeftHand = bestRightHand;					
					bestRightHand = equipment;
				}
				else if (bestLeftHand == null || equipment.getBonus(player) > bestLeftHand.getBonus(player))
					bestLeftHand = equipment;
				
				player.unequip(equipment);
				equipmentIter.remove();
			}
			else if (equipment.getEquipmentType() == EquipmentType.TWO_HANDS) {
				if (bestBothHands == null || equipment.getBonus(player) > bestBothHands.getBonus(player))
					bestBothHands = equipment;
				
				player.unequip(equipment);
				equipmentIter.remove();
			}	
		}
		
		int bestOneHandedBonus = 0;
		if (bestRightHand != null)
			bestOneHandedBonus += bestRightHand.getBonus(player);
		if (bestLeftHand != null)
			bestOneHandedBonus += bestLeftHand.getBonus(player);
		
		if (bestBothHands == null || bestOneHandedBonus >= bestBothHands.getBonus(player)) {
			if (bestRightHand != null)
				player.equip(bestRightHand);
			if (bestLeftHand != null)
				player.equip(bestLeftHand);
		}
		else if (bestBothHands != null)
			player.equip(bestBothHands);
		
		if (raceToDiscard != null) {
			player.getRaceCards().add(raceToDiscard);
			if (player.hasEquipped(Card.E_RAD_BANDANNA) || player.hasEquipped(Card.E_SWISS_ARMY_POLEARM)) {
				player.discardRaceCard(raceToDiscard);
				Messenger.display(player + " discarded " + gender + " " + raceToDiscard + " Race.", "Race Discarded");
			}	
		}
		
		// Everything left in availableEquipment should be footgear;
		// there are only 3 footgear items in the game and they have various powers which are difficult
		// to assign values to in relation to each other.  
		// So we will random equip one of any footgear items possessed.
		if (availableEquipment.isEmpty())
			return;
		
		for (EquipmentCard equipment : availableEquipment)
			player.unequip(equipment);
		
		int randomIndex = Randomizer.getRandom(availableEquipment.size());
		player.equip(availableEquipment.get(randomIndex));
	}
}
