package ai;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import model.Class;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Race;
import model.Randomizer;
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
import model.card.EquipmentCard.EquipmentType;

/**
 * Class for handling AI card evaluation for self or other players.
 */
public class AICardEvaluator {
	
	/**
	 * Evaluates the player's current status and returns the value of the Monster Card to him.
	 * @param card the Monster Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(MonsterCard monster, Player player, LinkedList<Card> handCards) {
		// prevent card evaluation loops
		if (handCards != null) {
			LinkedList<Card> tempList = handCards;
			handCards = new LinkedList<Card>();
			handCards.addAll(tempList);
			handCards.remove(monster);
		}
		
		int playerLevel = 0;
		if (player.isWarrior())
			playerLevel++;
		
		double personalValue = monster.getWinLevels() * AIManager.LEVEL_VALUE;
		personalValue += monster.getTreasures() * AIManager.UNKNOWN_CARD_VALUE;
		if (monster.getID() == Card.M_LARGE_ANGRY_CHICKEN) {
			if (player.hasEquipped(Card.E_FLAMING_ARMOR) || player.hasEquipped(Card.E_STAFF_OF_NAPALM) || player.hasEquipped(Card.E_FLAMETHROWER))
				personalValue += AIManager.LEVEL_VALUE;
			else
				for (ItemCard item : player.getCarriedItems())
					if (item.getID() == Card.I_FLAMING_POISON_POTION) {
						personalValue += AIManager.LEVEL_VALUE;
						playerLevel += item.getBonus();
					}
		}
		else if (monster.getID() == Card.M_POTTED_PLANT && player.isElf())
			personalValue += AIManager.UNKNOWN_CARD_VALUE;
		else if (monster.getID() == Card.M_THE_NOTHING && player.getLevel() + playerLevel > monster.getLevel(player))
			personalValue += AIManager.LEVEL_VALUE;
		
		int monsterLevel = monster.getLevel(player);
		if (monster.getID() != Card.M_INSURANCE_SALESMAN)
			playerLevel += player.getLevel();
		if (monster.getID() != Card.M_GHOULFIENDS)
			playerLevel += player.getEquipmentBonus();
		if (player.isCleric() && monster.isUndead())
			playerLevel += 4;
		
		double playerAdvantage = playerLevel - monsterLevel;
		double chanceToWin = 0.5 + (playerAdvantage / 20.0);
		if (chanceToWin > 1.0)
			chanceToWin = 1.0;
		else if (chanceToWin < 0.0)
			chanceToWin = 0.0;
		if (monster.getID() == Card.M_FLOATING_NOSE)
			for (ItemCard item : player.getCarriedItems())
				if (item.getID() == Card.I_POTION_OF_HALITOSIS)
					chanceToWin = 1.0;
		if (monster.getID() == Card.M_WANNABE_VAMPIRE && player.isCleric()) {
			personalValue -= AIManager.LEVEL_VALUE * (1.0 - chanceToWin);
			chanceToWin = 1.0;
		}
		
		personalValue *= chanceToWin;
		personalValue -= AIManager.getBadStuffCost(monster, false, player, false, handCards) * (1.0 - chanceToWin);
				
		if (monster.getID() == Card.M_TONGUE_DEMON) {
			try {
				personalValue -= AIManager.getLeastValuedItems(player, handCards).getFirst().getValue();
			}
			catch (NoSuchElementException ex) {}
		}
		else if (monster.getID() == Card.M_AMAZON && player.isFemale())
			personalValue = AIManager.UNKNOWN_CARD_VALUE;
		else if (monster.getID() == Card.M_LAWYER && player.isThief()){
			LinkedList<AIValuedCard> leastValuedCards = AIManager.getLeastValuedItems(player, handCards);
			leastValuedCards.addAll(AIManager.getLeastValuedHandCards(player, handCards));
			if (player.hasHireling()) {
				int hirelingValue = getCardValueToPlayer(player.getHirelingCard(), player, handCards);
				leastValuedCards.add(new AIValuedCard(player.getHirelingCard(), hirelingValue));
			}
			
			AIValuedCard leastValued = null;
			AIValuedCard nextLeastValued = null;
			for (AIValuedCard card : leastValuedCards) {
				if (card.getCard() instanceof DoorCard)
					continue;
				
				if (leastValued == null || card.getValue() < leastValued.getValue())
					leastValued = card;
				else if (nextLeastValued == null || card.getValue() < nextLeastValued.getValue())
					nextLeastValued = card;
			}
			
			if (nextLeastValued != null) {
				personalValue = AIManager.UNKNOWN_CARD_VALUE * 2;
				personalValue -= leastValued.getValue();
				personalValue -= nextLeastValued.getValue();
			}
			else
				personalValue = 0;
		}
		
		if (personalValue < 0)
			personalValue = 0;
		
		if (handCards == null)
			return (int)Math.round(personalValue);
		
		boolean hasIllusion = false;
		boolean hasWanderingMonster = false;
		for (Card handCard : handCards) {
			if (handCard.getID() == Card.OD_ILLUSION)
				hasIllusion = true;
			else if (handCard.getID() == Card.OD_WANDERING_MONSTER_1 || handCard.getID() == Card.OD_WANDERING_MONSTER_2)
				hasWanderingMonster = true;
		}		
		if (!hasIllusion && !hasWanderingMonster)
			return (int)Math.round(personalValue);
		
		double bestEnemyCost = 0;
		LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
		for (int playerIdx = 0; playerIdx < rankedPlayers.size(); playerIdx++) {
			Player enemy = rankedPlayers.get(playerIdx);
			if (enemy == player)
				continue;
			
			int enemyLevel = 0;
			
			double rankFactor = ((rankedPlayers.size() - playerIdx) / (double)rankedPlayers.size()) * 2.0;
			double enemyCost = monster.getWinLevels() * -AIManager.LEVEL_VALUE;
			enemyCost += monster.getTreasures() * -AIManager.UNKNOWN_CARD_VALUE;
			if (monster.getID() == Card.M_LARGE_ANGRY_CHICKEN) {
				if (enemy.hasEquipped(Card.E_FLAMING_ARMOR) || enemy.hasEquipped(Card.E_STAFF_OF_NAPALM) || enemy.hasEquipped(Card.E_FLAMETHROWER))
					enemyCost -= AIManager.LEVEL_VALUE;
				else
					for (ItemCard item : enemy.getCarriedItems())
						if (item.getID() == Card.I_FLAMING_POISON_POTION) {
							enemyCost -= AIManager.LEVEL_VALUE;
							enemyLevel += item.getBonus();
						}
			}
			if (monster.getID() == Card.M_POTTED_PLANT && enemy.isElf())
				enemyCost -= AIManager.UNKNOWN_CARD_VALUE;
			if (monster.getID() == Card.M_THE_NOTHING && enemy.getLevel() > monster.getLevel(enemy))
				enemyCost -= AIManager.LEVEL_VALUE;
			
			monsterLevel = monster.getLevel(enemy);
			if (hasWanderingMonster)
				monsterLevel += 5;
			if (monster.getID() != Card.M_INSURANCE_SALESMAN)
				enemyLevel += enemy.getLevel();
			if (monster.getID() != Card.M_GHOULFIENDS)
				enemyLevel += enemy.getEquipmentBonus();
			if (enemy.isWarrior())
				enemyLevel++;
			if (enemy.isCleric() && monster.isUndead())
				enemyLevel += 4;
			
			playerAdvantage = enemyLevel - monsterLevel;
			chanceToWin = 0.5 + (playerAdvantage / 20.0);
			if (chanceToWin > 1.0)
				chanceToWin = 1.0;
			else if (chanceToWin < 0.0)
				chanceToWin = 0.0;
			if (monster.getID() == Card.M_FLOATING_NOSE)
				for (ItemCard item : enemy.getCarriedItems())
					if (item.getID() == Card.I_POTION_OF_HALITOSIS)
						chanceToWin = 1.0;
			if (monster.getID() == Card.M_WANNABE_VAMPIRE && enemy.isCleric()) {
				enemyCost += AIManager.LEVEL_VALUE * (1.0 - chanceToWin);
				chanceToWin = 1.0;
			}
			
			enemyCost *= chanceToWin;
			enemyCost += AIManager.getBadStuffCost(monster, false, enemy, false, null) * (1.0 - chanceToWin);
					
			if (monster.getID() == Card.M_TONGUE_DEMON) {
				try {
					enemyCost += AIManager.getLeastValuedItems(enemy, null).getFirst().getValue();
				}
				catch (NoSuchElementException ex) {}
			}
			else if (monster.getID() == Card.M_AMAZON && enemy.isFemale())
				enemyCost = -AIManager.UNKNOWN_CARD_VALUE;
			else if (monster.getID() == Card.M_LAWYER && enemy.isThief())
				enemyCost = 0;
						
			enemyCost *= rankFactor;
			if (enemyCost > bestEnemyCost)
				bestEnemyCost = enemyCost;
		}
				
		if (bestEnemyCost < 0)
			bestEnemyCost = 0;
			
		return (int)Math.round(Math.max(personalValue, bestEnemyCost));
	}

	/**
	 * Evaluates the player's current status and returns the value of the Race Card to him.
	 * @param card the Race Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(RaceCard raceCard, Player player, LinkedList<Card> handCards) {
		int raceValue = 0;
		// unlimited big items + extra hand card
		if (raceCard.getRace() == Race.DWARF)
			raceValue = 250 + 200;
		// run bonus + helper level gain
		else if (raceCard.getRace() == Race.ELF)
			raceValue = 200 + 250;
		// run penalty + double sell value
		else if (raceCard.getRace() == Race.HALFLING)
			raceValue = -200 + 250;
		
		LinkedList<TreasureCard> allItems = player.getAllItems();
		if (handCards != null)
			for (Card handCard : handCards) {
				if (handCard instanceof EquipmentCard)
					allItems.add((EquipmentCard)handCard);
				else if (raceCard.getRace() == Race.ELF && handCard.getID() == Card.I_YUPPIE_WATER)
					raceValue += 100;
			}
				
		int extraBigItems = -1;
		if (player.hasHireling())
			extraBigItems--;
		for (TreasureCard item : allItems) {
			if (item instanceof EquipmentCard) {
				EquipmentCard equipment = (EquipmentCard)item;
				if (player.getCheatingItemCard() == equipment)
					continue;
				
				if (equipment.isBig()) {
					extraBigItems++;
					if (extraBigItems > 0) {
						if (raceCard.getRace() == Race.DWARF) {
							int bonus = Math.max(equipment.getBonus(player), equipment.getBonusToRace(raceCard.getRace()));
							if (bonus != 0)
								raceValue += bonus * 400 - equipment.getValue();
						}
					}
				}
				
				int raceBonus = equipment.getBonusToRace(raceCard.getRace());
				if  (raceBonus > 0) {
					if (equipment.getID() == Card.E_HORNED_HELMET)
						raceBonus++;
					raceValue += raceBonus * 400 - equipment.getValue();
				}
			}
			else if (raceCard.getRace() == Race.ELF && item.getID() == Card.I_YUPPIE_WATER)
				raceValue += 100;
		}
		
		if (player.getRaceCards().contains(raceCard))
			return raceValue;
		
		// if race card of same type is already in play, extra/backup race is worth a fraction of the value
		int extraRaceNumber = 0;
		for (RaceCard currentRace : player.getRaceCards())
			if (raceCard.getRace() == currentRace.getRace())
				extraRaceNumber++;
		
		if (handCards != null)
			for (Card handCard : handCards) {
				if (handCard == raceCard)
					break;
				
				if (handCard instanceof RaceCard && ((RaceCard)handCard).getRace() == raceCard.getRace())
					extraRaceNumber++;
			}
		
		if (extraRaceNumber == 0)
			return raceValue;
		
		return raceValue / (extraRaceNumber * 5);		
	}
	
	/**
	 * Evaluates the player's current status and returns the value of the Class Card to him.
	 * @param card the Class Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(ClassCard classCard, Player player, LinkedList<Card> handCards) {
		int classValue = 0;
		// turning + resurrect
		if (classCard.getCharacterClass() == Class.CLERIC)
			classValue = 150 + 350;
		// backstab + stealing
		else if (classCard.getCharacterClass() == Class.THIEF)
			classValue = 150 + 350;
		// berserking + win ties
		else if (classCard.getCharacterClass() == Class.WARRIOR)
			classValue = 150 + 350;
		// flight spell + charm
		else if (classCard.getCharacterClass() == Class.WIZARD)
		classValue = 150 + 350;
				
		for (EquipmentCard equipment : player.getAllEquipment()) {
			if (player.getCheatingItemCard() == equipment)
				continue;
				
			int classBonus = equipment.getBonusToClass(classCard.getCharacterClass());
			if  (classBonus > 0)
				classValue += classBonus * 400 - equipment.getValue();
			else if (equipment.getID() == Card.E_SINGING_AND_DANCING_SWORD && classCard.getCharacterClass() == Class.THIEF)
				classValue -= 2 * 400 - equipment.getValue();
			else if (equipment.getID() == Card.E_PANTYHOSE_OF_GIANT_STRENGTH && classCard.getCharacterClass() == Class.WARRIOR)
				classValue -= 3 * 400 - equipment.getValue();
			else if (equipment.getID() == Card.E_MITHRIL_ARMOR && classCard.getCharacterClass() == Class.WIZARD)
				classValue -= 3 * 400 - equipment.getValue();
			else if (equipment.getID() == Card.E_KNEEPADS_OF_ALLURE && classCard.getCharacterClass() == Class.CLERIC) {
				int bestHelpValue = 1100;
				for (Player helper : GM.getPlayers()) {
					if (player == helper)
						continue;
					
					int helpValue = (helper.getLevel() + helper.getEquipmentBonus()) * 400;
					if (helper.isElf())
						helpValue -= 1000;
					
					if (helpValue > bestHelpValue)
						bestHelpValue = helpValue;
				}
				classValue -= bestHelpValue - equipment.getValue();			
			}
		}
				
		if (player.getClassCards().contains(classCard))
			return classValue;
		
		if (classValue < 0)
			classValue = 0;
		
		// if class card of same type is already in play, extra/backup race is worth a fraction of the value
		int extraClassNumber = 0;
		for (ClassCard currentClass : player.getClassCards())
			if (classCard.getCharacterClass() == currentClass.getCharacterClass())
				extraClassNumber++;
		
		if (handCards != null)
			for (Card handCard : handCards) {
				if (handCard == classCard)
					break;
				
				if (handCard instanceof ClassCard && ((ClassCard)handCard).getCharacterClass() == classCard.getCharacterClass())
					extraClassNumber++;
			}
		
		if (extraClassNumber == 0)
			return classValue;
		
		return classValue / (extraClassNumber * 5);
	}

	/**
	 * Evaluates the player's current status and returns the value of the Curse Card to him.
	 * @param card the Curse Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(CurseCard curse, Player player, LinkedList<Card> handCards) {
		// prevent card evaluation loops
		if (handCards != null) {
			LinkedList<Card> tempList = handCards;
			handCards = new LinkedList<Card>();
			handCards.addAll(tempList);
			handCards.remove(curse);
		}
		
		int bestCurseValue = 0;
		for (Player victim : AIManager.getRankedPlayers()) {
			if (player == victim) {
				int curseValue = -AIManager.getCurseCost(curse, player, handCards);
				if (curseValue > bestCurseValue)
					bestCurseValue = curseValue;
				
				continue;
			}
			
			int curseValue = (int)(AIManager.getCurseCost(curse, victim, null));
			if (curseValue > bestCurseValue)
				bestCurseValue = curseValue;
		}
		
		return bestCurseValue;
	}

	/**
	 * Evaluates the player's current status and returns the value of the Monster Enhancer Card to him.
	 * @param card the Monster Enhancer Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(MonsterEnhancerCard enhancer, Player player, LinkedList<Card> handCards) {
		return Math.abs(enhancer.getBonus()) * 100;
	}

	/**
	 * Evaluates the player's current status and returns the value of the Other Door Card to him.
	 * @param card the Other Door Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(OtherDoorCard otherCard, Player player, LinkedList<Card> handCards) {
		// prevent card evaluation loops
		if (handCards != null) {
			LinkedList<Card> tempList = handCards;
			handCards = new LinkedList<Card>();
			handCards.addAll(tempList);
			handCards.remove(otherCard);
		}
		
		int cardWorth = 0;
				
		if (otherCard.getID() == Card.OD_CHEAT) {
			// average bonus * permanent bonus worth
			cardWorth = 3 * 400;
		}
		else if (otherCard.getID() == Card.OD_DIVINE_INTERVENTION) {
			if (player.isCleric()) { 
				cardWorth = AIManager.LEVEL_VALUE;
				if (player.getLevel() >= 9) {
					cardWorth = AIManager.LEVEL_VALUE * 10;
					if (player.getPlayerType() != PlayerType.COMPUTER_HARD)
						return cardWorth;
				}
			}
			else
				return 0;
			
			LinkedList<Player> rankedPlayers = AIManager.getRankedPlayers();
			int numPlayers = rankedPlayers.size();
			for (int playerIdx = 0; playerIdx < rankedPlayers.size(); playerIdx++) {
				Player currentPlayer = rankedPlayers.get(playerIdx);
				if (player == currentPlayer) {
					if (player.getPlayerType() == PlayerType.COMPUTER_EASY)
						break;
					else
						continue;
				}
				
				if (currentPlayer.isCleric()) {
					if (currentPlayer.getLevel() >= 9)
						return AIManager.LEVEL_VALUE * -10;
					
					double rankFactor = ((numPlayers - playerIdx) / (double)numPlayers) * 2.0;
					cardWorth -= AIManager.LEVEL_VALUE * rankFactor;
				}
			}
		}
		else if (otherCard.getID() == Card.OD_HALF_BREED_1 || otherCard.getID() == Card.OD_HALF_BREED_2) {
			if (otherCard == player.getHalfBreedCard()) {
				cardWorth = 100;
				if (player.isHalfling())
					cardWorth += 200;
			}
			else {
				if (player.isHalfBreed())				
					cardWorth = 25;
				else if (player.getRaceCards().size() == 0) {
					cardWorth = 100;					
					if (handCards != null)
						for (Card handCard : handCards)
							if (handCard instanceof RaceCard) {
								cardWorth += 200;
								break;
							}
				}
				else {
					cardWorth = 300;
					
					if (player.isHalfling())
						cardWorth += 200;
				}	
			}						
		}
		else if (otherCard.getID() == Card.OD_HELP_ME_OUT_HERE) {
			// average bonus * permanent bonus worth
			cardWorth = 3 * 400;
		}
		else if (otherCard.getID() == Card.OD_ILLUSION) {
			cardWorth = 300;
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof MonsterCard)
						cardWorth += 200;
		}
		else if (otherCard.getID() == Card.OD_MATE) {
			// average monster level * one-time bonus value + run penalty/reward bonus
			cardWorth = 9 * 100 + 100;
		}
		else if (otherCard.getID() == Card.OD_OUT_TO_LUNCH) {
			// 2 treasures + no bad stuff
			cardWorth = 2 * AIManager.UNKNOWN_CARD_VALUE + 1000;
		}
		else if (otherCard.getID() == Card.OD_SUPER_MUNCHKIN_1 || otherCard.getID() == Card.OD_SUPER_MUNCHKIN_2) {
			if (otherCard == player.getSuperMunchkinCard())
				cardWorth = 100;
			else {
				if (player.isSuperMunchkin())				
					cardWorth = 25;
				else {
					int numClassCards = player.getClassCards().size();
					if (handCards != null)
						for (Card handCard : handCards)
							if (handCard instanceof ClassCard)
								numClassCards++;
					
					cardWorth = 50;
					if (numClassCards == 1)
						cardWorth = 150;
					else if (numClassCards > 1)
						cardWorth = 500;
				}
			}
		}
		else if (otherCard.getID() == Card.OD_WANDERING_MONSTER_1 || otherCard.getID() == Card.OD_WANDERING_MONSTER_2) {
			cardWorth = 400;
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof MonsterCard)
						cardWorth += 300;
		}
		
		return cardWorth;
	}

	/**
	 * Evaluates the player's current status and returns the value of the Equipment Card to him.
	 * @param card the Equipment Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(EquipmentCard equipment, Player player, LinkedList<Card> handCards) {
		int unrestrainedValue = equipment.getBonus(player) * 400;
		
		if (equipment.getID() == Card.E_KNEEPADS_OF_ALLURE) {
			int bestHelpValue = 1000;
			for (Player helper : GM.getPlayers()) {
				if (player == helper)
					continue;
				
				int helpValue = (helper.getLevel() + helper.getEquipmentBonus()) * 300;
				if (helper.isElf())
					helpValue -= 1000;
				
				if (helpValue > bestHelpValue)
					bestHelpValue = helpValue;
			}
			
			if (player.isCleric())
				bestHelpValue /= 2;
			
			return bestHelpValue;
		}
		else if (equipment.getID() == Card.E_TUBA_OF_CHARM) {
			if (player.hasEquipped(equipment))
				return equipment.getValue() + Randomizer.getRandom(5);
			else
				return equipment.getValue() + Randomizer.getRandom(2);
		}
		
		if (equipment == player.getCheatingItemCard())
			return Math.max(unrestrainedValue, equipment.getValue());
		
		if (equipment.equip(player).equals("You must be an Elf to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof RaceCard && ((RaceCard)handCard).getRace() == Race.ELF) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Dwarf to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof RaceCard && ((RaceCard)handCard).getRace() == Race.DWARF) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Halfling to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof RaceCard && ((RaceCard)handCard).getRace() == Race.HALFLING) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Human to use this.")) {
			if (handCards != null && !player.isHalfBreed())
				for (Card handCard : handCards)
					if (handCard.getID() == Card.OD_HALF_BREED_1 || handCard.getID() == Card.OD_HALF_BREED_2) {
						unrestrainedValue *= 3;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Cleric to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof ClassCard && ((ClassCard)handCard).getCharacterClass() == Class.CLERIC) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Thief to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof ClassCard && ((ClassCard)handCard).getCharacterClass() == Class.THIEF) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Warrior to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof ClassCard && ((ClassCard)handCard).getCharacterClass() == Class.WARRIOR) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("You must be a Wizard to use this.")) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof ClassCard && ((ClassCard)handCard).getCharacterClass() == Class.WIZARD) {
						unrestrainedValue *= 2;
						break;
					}
							
			return Math.max(unrestrainedValue / 3, equipment.getValue());
		}
		else if (equipment.equip(player).equals("Clerics cannot use this."))
			return Math.max(unrestrainedValue / 2, equipment.getValue());
		else if (equipment.equip(player).equals("Thieves cannot use this."))
			return Math.max(unrestrainedValue / 2, equipment.getValue());
		else if (equipment.equip(player).equals("Warriors cannot use this."))		
			return Math.max(unrestrainedValue / 2, equipment.getValue());
		else if (equipment.equip(player).equals("Wizards cannot use this."))
			return Math.max(unrestrainedValue / 2, equipment.getValue());
		else if (equipment.equip(player).equals("You must be male to use this."))
			return equipment.getValue();
		else if (equipment.equip(player).equals("You must be female to use this."))
			return equipment.getValue();
		
		if (equipment.isBig() && !player.isDwarf() && !player.getAllEquipment().contains(equipment)) {
			int allowedBigItems = 1;
			if (player.hasHireling())
				allowedBigItems++;
			if (player.getCheatingItemCard() != null && player.getCheatingItemCard().isBig())
				allowedBigItems++;
			if (player.getBigItems().size() >= allowedBigItems)
				return Math.max(unrestrainedValue / 2, equipment.getValue());
		}
		
		if (equipment.getEquipmentType() == EquipmentType.FOOTGEAR) {
			// Used in testing
			if (equipment.getID() < Card.E_BOOTS_OF_BUTT_KICKING)
				return Math.max(unrestrainedValue, equipment.getValue());
			
			int footgearCount = 1;
			for (EquipmentCard currentEquipment : player.getAllEquipment())
				if (currentEquipment != equipment && currentEquipment.getEquipmentType() == EquipmentType.FOOTGEAR)
					footgearCount++;
			
			if (footgearCount <= 1)
				return 800;
			else if (footgearCount == 2)
				return equipment.getValue() + Randomizer.getRandom(5);
			else
				return equipment.getValue() + Randomizer.getRandom(2);
		}
		
		if (player.hasEquipped(equipment) || equipment.getEquipmentType() == EquipmentType.OTHER)
			return Math.max(unrestrainedValue, equipment.getValue());
		
		LinkedList<EquipmentCard> allEquipment = player.getAllEquipment();
		if (handCards != null)
			for (Card handCard : handCards)
				if (handCard instanceof EquipmentCard && !((EquipmentCard)handCard).isBig())
					allEquipment.add((EquipmentCard)handCard);
		allEquipment.remove(equipment);
		
		if (equipment.getEquipmentType() == EquipmentType.ARMOR) {
			int equipmentBonus = equipment.getBonus(player);
			int bestBonus = 0;
			for (EquipmentCard currentEquipment : allEquipment)
				if (currentEquipment.getEquipmentType() == EquipmentType.ARMOR && currentEquipment.equip(player).equals("")) {
					int bonus = currentEquipment.getBonus(player);
					if (bonus >= equipmentBonus)
						return equipment.getValue();
					else if (bonus > bestBonus)
						bestBonus = bonus;						
				}
			
			unrestrainedValue = (equipmentBonus - bestBonus) * 400;					
		}
		else if (equipment.getEquipmentType() == EquipmentType.HEADGEAR) {
			int equipmentBonus = equipment.getBonus(player);
			int bestBonus = 0;
			for (EquipmentCard currentEquipment : allEquipment)
				if (currentEquipment.getEquipmentType() == EquipmentType.HEADGEAR && currentEquipment.equip(player).equals("")) {
					int bonus = currentEquipment.getBonus(player);
					if (bonus >= equipmentBonus)
						return equipment.getValue();
					else if (bonus > bestBonus)
						bestBonus = bonus;						
				}
			
			unrestrainedValue = (equipmentBonus - bestBonus) * 400;
			if (equipment.getID() == Card.E_HORNED_HELMET && player.hasEquipped(equipment) && equipmentBonus == 1)
				if (Randomizer.getRandom(5) > 0)
					unrestrainedValue += 100;
		}
		// one-hand or two-handed equipment item
		else {
			int equipmentBonus = equipment.getBonus(player);
			int bestRightHandBonus = 0;
			int bestLeftHandBonus = 0;
			int bestBothHandsBonus = 0;
			for (EquipmentCard currentEquipment : allEquipment) {
				if (!currentEquipment.equip(player).equals(""))
					continue;
			
				int bonus = currentEquipment.getBonus(player);
				if (currentEquipment.getEquipmentType() == EquipmentType.ONE_HAND) {
					if (bonus > bestRightHandBonus) {
						bestLeftHandBonus = bestRightHandBonus;
						bestRightHandBonus = bonus;
					}
					else if (bonus > bestLeftHandBonus)
						bestLeftHandBonus = bonus;
				}					
				else if (currentEquipment.getEquipmentType() == EquipmentType.TWO_HANDS)
					if (bonus > bestBothHandsBonus)
						bestBothHandsBonus = bonus;					
			}
			
			int bestHandsBonus = Math.max(bestBothHandsBonus, bestRightHandBonus + bestLeftHandBonus);
			if (equipment.getEquipmentType() == EquipmentType.ONE_HAND) {
				int combinedBonus = bestRightHandBonus + equipmentBonus;
				if (bestHandsBonus >= combinedBonus)
					return equipment.getValue();
				else
					unrestrainedValue = (combinedBonus - bestHandsBonus) * 400;
			}
			else if (equipment.getEquipmentType() == EquipmentType.TWO_HANDS) {
				if (bestHandsBonus >= equipmentBonus)
					return equipment.getValue();
				else
					unrestrainedValue = (equipmentBonus - bestHandsBonus) * 400;
			}				
		}

		return Math.max(unrestrainedValue, equipment.getValue() + Randomizer.getRandom(2));
	}

	/**
	 * Evaluates the player's current status and returns the value of the Item Card to him.
	 * @param card the Item Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(ItemCard item, Player player, LinkedList<Card> handCards) {
		int cardValue;
		
		if (item.getID() == Card.I_DOPPLEGANGER)
			cardValue = item.getValue() + (player.getLevel() + player.getEquipmentBonus()) * 100;
		else if (item.getID() == Card.I_FLASK_OF_GLUE)
			cardValue = item.getValue() + (Randomizer.getRandom(4) * 100);
		else if (item.getID() == Card.I_FRIENDSHIP_POTION)
			cardValue = item.getValue() + AIManager.LEVEL_VALUE;
		else if (item.getID() == Card.I_INVISIBILITY_POTION) {
			int runBonus = 0;
			if (player.isElf())
				runBonus++;
			if (player.isHalfling() && !player.isHuman())
				runBonus--;
			if (player.hasChickenOnHead())
				runBonus--;
			if (player.hasEquipped(Card.E_BOOTS_OF_RUNNING_REALLY_FAST))
				runBonus += 2;
			if (player.hasEquipped(Card.E_TUBA_OF_CHARM))
				runBonus++;
			
			cardValue = item.getValue() + 600 - (runBonus * 200);
		}			
		else if (item.getID() == Card.I_INSTANT_WALL) {
			int runBonus = 0;
			if (player.isElf())
				runBonus++;
			if (player.isHalfling() && !player.isHuman())
				runBonus--;
			if (player.hasChickenOnHead())
				runBonus--;
			if (player.hasEquipped(Card.E_BOOTS_OF_RUNNING_REALLY_FAST))
				runBonus += 2;
			if (player.hasEquipped(Card.E_TUBA_OF_CHARM))
				runBonus++;
			
			cardValue = item.getValue() + 600 - (runBonus * 200);
		}
		else if (item.getID() == Card.I_LOADED_DIE) {
			int runBonus = 0;
			if (player.isElf())
				runBonus++;
			if (player.isHalfling() && !player.isHuman())
				runBonus--;
			if (player.hasChickenOnHead())
				runBonus--;
			if (player.hasEquipped(Card.E_BOOTS_OF_RUNNING_REALLY_FAST))
				runBonus += 2;
			if (player.hasEquipped(Card.E_TUBA_OF_CHARM))
				runBonus++;
			
			cardValue = item.getValue() + 600 - (runBonus * 200);
			if (runBonus == -2)
				cardValue = 0;
			
			if (player.isThief())
				cardValue += 400;
		}
		else if (item.getID() == Card.I_MAGIC_LAMP_1 || item.getID() == Card.I_MAGIC_LAMP_2)
			cardValue = 3 * AIManager.UNKNOWN_CARD_VALUE;
		else if (item.getID() == Card.I_POLLYMORPH_POTION)
			cardValue = 2 * AIManager.UNKNOWN_CARD_VALUE + (AIManager.UNKNOWN_CARD_VALUE / 2);
		else if (item.getID() == Card.I_POTION_OF_HALITOSIS) {
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard.getID() == Card.M_FLOATING_NOSE)
						return 1000;
						
			return item.getBonus() * 100;
		}
		else if (item.getID() == Card.I_TRANSFERRAL_POTION)
			cardValue = item.getValue() + (Randomizer.getRandom(5) * 100);
		else if (item.getID() == Card.I_WAND_OF_DOWSING)
			cardValue = item.getValue() + 100;
		else if (item.getID() == Card.I_WISHING_RING_1 || item.getID() == Card.I_WISHING_RING_2) {
			cardValue = item.getValue();
			
			if (!player.getCarriedItems().contains(item)) {
				cardValue += 100;
				if (player.hasChickenOnHead() || player.hasDistractionCurse() || player.hasMalignMirror())
					cardValue += 300;
			}
			else
				cardValue += (Randomizer.getRandom(4) * 100);
			
			if (player.hasEquipped(Card.E_SANDALS_OF_PROTECTION))
				cardValue /= 2;
		}
		else if (item.getID() == Card.I_YUPPIE_WATER) {
			cardValue = item.getValue();
			if (player.isElf())
				cardValue = 250;
		}
		else
			cardValue = item.getBonus() * 100 + item.getValue() / 100;
		
		return Math.max(cardValue, item.getValue() + item.getBonus());
	}

	/**
	 * Evaluates the player's current status and returns the value of the Go Up A Level Card to him.
	 * @param card the Go Up A Level Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(GoUpLevelCard levelCard, Player player, LinkedList<Card> handCards) {
		// prevent card evaluation loops
		if (handCards != null) {
			LinkedList<Card> tempList = handCards;
			handCards = new LinkedList<Card>();
			handCards.addAll(tempList);
			handCards.remove(levelCard);
		}
		
		int cardValue = AIManager.LEVEL_VALUE;	
		
		if (levelCard.getID() == Card.GUL_WHINE_AT_THE_GM)
			if (GM.getHighestLevelPlayers().contains(player))
				cardValue = 400;
		else if (levelCard.getID() == Card.GUL_KILL_THE_HIRELING) {
			cardValue = 100 * GM.getPlayers().size();
			
			if (player.hasHireling()) {
				cardValue = AIManager.LEVEL_VALUE - getCardValueToPlayer(player.getHirelingCard(), player, handCards);
				if (cardValue < 0)
					cardValue = 0;
			}
			else {
				for (Player currentPlayer : GM.getPlayers())
					if (currentPlayer.hasHireling()) {
						cardValue = AIManager.LEVEL_VALUE + getCardValueToPlayer(currentPlayer.getHirelingCard(), currentPlayer, null); 
						break;
					}
			}
		}
		
		if (player.getLevel() == 9)
			cardValue /= 2;
		
		return cardValue;	
	}

	/**
	 * Evaluates the player's current status and returns the value of the Other Treasure Card to him.
	 * @param card the Other Treasure Card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(OtherTreasureCard otherCard, Player player, LinkedList<Card> handCards) {
		int cardValue = 0;
		
		if (otherCard.getID() == Card.OT_STEAL_A_LEVEL) {
			cardValue = AIManager.LEVEL_VALUE * 2;
			if (player.getLevel() >= 9 || GM.getHighestLevel() == 1)
				cardValue -= AIManager.LEVEL_VALUE;
		}
		else if (otherCard.getID() == Card.OT_HIRELING) {
			cardValue = 100;
			
			int numBigItems = player.getBigItems().size();
			for (EquipmentCard bigItem : player.getBigItems())
				if (bigItem == player.getCheatingItemCard())
					numBigItems--;
			if (handCards != null)
				for (Card handCard : handCards)
					if (handCard instanceof EquipmentCard && ((EquipmentCard)handCard).isBig())
						numBigItems++;
			if (player.isDwarf())
				cardValue = 100;
			else if (numBigItems == 1)
				cardValue += 100;
			else if (numBigItems > 1)
				cardValue = 500;
			
			if (handCards != null && handCards.contains(otherCard))
				for (Card handCard : handCards)
					if (handCard.getID() == Card.GUL_KILL_THE_HIRELING)
						cardValue = AIManager.LEVEL_VALUE;
		}
		else if (otherCard.getID() == Card.OT_HOARD)
			cardValue = 3 * AIManager.UNKNOWN_CARD_VALUE;
		
		return cardValue;
	}

	/**
	 * Evaluates the player's current status and returns the value of the given card to him.
	 * @param card the card to evaluate
	 * @param player the player to check
	 * @param handCards list of the player's hand cards to evaluate; null, if should treat all hand cards as unknown
	 * @return the current value of the given card to the given player
	 */
	public static int getCardValueToPlayer(Card card, Player player, LinkedList<Card> handCards) {
		if (card instanceof MonsterCard)
			return getCardValueToPlayer((MonsterCard)card, player, handCards);
		else if (card instanceof RaceCard)
			return getCardValueToPlayer((RaceCard)card, player, handCards);
		else if (card instanceof ClassCard)
			return getCardValueToPlayer((ClassCard)card, player, handCards);
		else if (card instanceof CurseCard)
			return getCardValueToPlayer((CurseCard)card, player, handCards);
		else if (card instanceof MonsterEnhancerCard)
			return getCardValueToPlayer((MonsterEnhancerCard)card, player, handCards);
		else if (card instanceof OtherDoorCard) 
			return getCardValueToPlayer((OtherDoorCard)card, player, handCards);
		else if (card instanceof EquipmentCard)
			return getCardValueToPlayer((EquipmentCard)card, player, handCards);
		else if (card instanceof ItemCard)
			return getCardValueToPlayer((ItemCard)card, player, handCards);
		else if (card instanceof GoUpLevelCard)
			return getCardValueToPlayer((GoUpLevelCard)card, player, handCards);
		else if (card instanceof OtherTreasureCard)
			return getCardValueToPlayer((OtherTreasureCard)card, player, handCards);
		
		System.out.println("Error! Unhandled Card Exception: " + card + "(" + card.getID() + ")");
		return 0;
	}
}
