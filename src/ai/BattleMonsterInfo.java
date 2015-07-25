package ai;

import java.util.LinkedList;

import model.Battle;
import model.Player;
import model.PlayerType;
import model.card.Card;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;

/**
 * Stores information about all the monsters in the given battle.
 */
public class BattleMonsterInfo {
	
	/**
	 * Simple class (struct) containing information about a single monster in the battle
	 */
	public class BattleMonster {
		public MonsterCard card;
		public int battleLevel;
		public int treasures;
		public boolean hasMate;
		
		public int activePlayerWinLevels;
		public int activePlayerBadStuffCost;
		
		public int helperWinLevels;
		public int helperBadStuffCost;
		
		/**
		 * Creates a new BattleMonster.
		 * @param monster monster card for the monster
		 * @param monsterLevel current battle level of the monster
		 * @param mate whether or not the monster has a mate
		 * @param battle the battle in which information is being collected
		 * @param player the player that this information is being collection for
		 */
		public BattleMonster(MonsterCard monster, int monsterLevel, boolean mate, Battle battle, Player player) {
			card = monster;
			battleLevel = monsterLevel;
			treasures = battle.getWinTreasureCount(monster);
			hasMate = mate;
			
			// store info about what the player(s) will gain/lose if they win/lose the battle in its current state.
			activePlayerWinLevels = battle.getWinLevelCount(monster);
			LinkedList<Card> handCards = null;
			if (player == battle.activePlayer)
				handCards = battle.activePlayer.getHandCards();
			activePlayerBadStuffCost = AIManager.getBadStuffCost(monster, hasMate, battle.activePlayer, false, handCards);
			
			if (battle.isHelper()) {
				helperWinLevels = 0;
				if (battle.helper.isElf()) {
					helperWinLevels++;
					if (hasMate)
						helperWinLevels++;
				}
				handCards = null;
				if (player == battle.helper)
					handCards = battle.helper.getHandCards();
				helperBadStuffCost = AIManager.getBadStuffCost(monster, hasMate, battle.helper, false, handCards);
			}
			else {
				helperWinLevels = 0;
				helperBadStuffCost = 0;
			}	
		}
	}
	
	private LinkedList<BattleMonster> battleMonsters;
	private Battle battle;
	private int activePlayerWinTreasureValue;
	private int helperWinTreasureValue;
	
	/**
	 * Creates a new BattleMonstersInfo class.
	 * @param battle the battle in which information is being collected
	 * @param player the player that this information is being collection for
	 */
	public BattleMonsterInfo(Battle b, Player player) {
		battle = b;
		battleMonsters = new LinkedList<BattleMonster>();
		
		// gather info about monsters and sort with strongest monsters first
		int treasures = battle.getTreasureCount();
		for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++) {
			MonsterCard monster = battle.getMonster(monsterIndex);
			int battleLevel = monster.getLevel(battle);			
			boolean hasMate = false;
			for (Card monsterEnhancer : battle.getMonsterEnhancers(monsterIndex)) {
				if (monsterEnhancer.getID() == Card.OD_MATE)
					hasMate = true;
				else
					battleLevel += ((MonsterEnhancerCard)monsterEnhancer).getBonus();
			}				
			if (battleLevel < 1)
				battleLevel = 1;
			if (hasMate)
				battleLevel *= 2;
			
			BattleMonster battleMonster = new BattleMonster(monster, battleLevel, hasMate, battle, player);
			int bmIndex;
			for (bmIndex = 0; bmIndex < battleMonsters.size(); bmIndex++) {
				BattleMonster currentMonster = battleMonsters.get(bmIndex);
				if (battleLevel == currentMonster.battleLevel) {
					int totalBadStuffCost = battleMonster.activePlayerBadStuffCost + battleMonster.helperBadStuffCost;
					if (totalBadStuffCost > currentMonster.activePlayerBadStuffCost + currentMonster.helperBadStuffCost)
						break;
				}				
				else if (battleLevel > currentMonster.battleLevel)
					break;
			}
			battleMonsters.add(bmIndex, battleMonster);
			
			treasures += battleMonster.treasures;
		}
		
		activePlayerWinTreasureValue = 0;
		helperWinTreasureValue = 0;
		if (battle.getTakeTreasurePlayerOrder() == null || battle.activePlayer.hasEquipped(Card.E_KNEEPADS_OF_ALLURE))
			activePlayerWinTreasureValue = treasures * AIManager.UNKNOWN_CARD_VALUE;
		else {		
			int currentTreasureValue = AIManager.UNKNOWN_CARD_VALUE + (100 * ((treasures - 1) / 2));
			for (Player taker : battle.getTakeTreasurePlayerOrder()) {
				if (taker == battle.activePlayer)
					activePlayerWinTreasureValue += currentTreasureValue;
				else
					helperWinTreasureValue += currentTreasureValue;
			
				treasures--;
				if (currentTreasureValue > 100)
					currentTreasureValue -= 100;
			}
			
			int remainingTreasureValue;
			for (remainingTreasureValue = 0; treasures > 0; treasures--) {
				remainingTreasureValue += currentTreasureValue;
				if (currentTreasureValue > 100);
					currentTreasureValue -= 100;
			}
			
			PlayerType activePlayerType = battle.activePlayer.getPlayerType();
			PlayerType helperType = battle.helper.getPlayerType();
			if (helperType == PlayerType.COMPUTER_EASY)
				activePlayerWinTreasureValue += remainingTreasureValue;
			else if (activePlayerType == PlayerType.COMPUTER_HARD && helperType == PlayerType.HUMAN)
				activePlayerWinTreasureValue += remainingTreasureValue;
			else if (helperType == PlayerType.COMPUTER_HARD)
				helperWinTreasureValue += remainingTreasureValue;
			else if (activePlayerType == PlayerType.COMPUTER_EASY && helperType == PlayerType.HUMAN)
				helperWinTreasureValue += remainingTreasureValue;
			else {
				activePlayerWinTreasureValue += remainingTreasureValue / 2;
				helperWinTreasureValue += remainingTreasureValue / 2;
			}				
		}
	}
	
	/**
	 * Returns the list containing all monsters in the battle sorted from strongest to weakest battle levels.
	 * @return the list containing all monsters in the battle sorted from strongest to weakest battle levels
	 */
	public LinkedList<BattleMonster> getBattleMonsters() {
		return battleMonsters;
	}
	
	/**
	 * Returns the number of levels the given player will gain if he wins the battle at its current state.
	 * @param player the player to check
	 * @return the number of levels the given player will gain if he wins the battle at its current state
	 */
	public int getPlayerWinLevels(Player player) {
		if (player == null)
			return 0;
		else if (player == battle.activePlayer) {
			int winLevels = 0;
			for (BattleMonster battleMonster : battleMonsters)
				winLevels += battleMonster.activePlayerWinLevels;
			return winLevels; 
		}
		else if (player == battle.helper) {
			int winLevels = 0;
			for (BattleMonster battleMonster : battleMonsters)
				winLevels += battleMonster.helperWinLevels;
			return winLevels; 
		}
		else
			return 0;
	}
	
	/**
	 * Returns the total treasure value the given player will gain if he wins the battle at its current state.
	 * @param player the player to check
	 * @return the total treasure value the given player will gain if he wins the battle at its current state
	 */
	public int getPlayerWinTreasureValue(Player player) {
		if (player == null)
			return 0;
		else if (player == battle.activePlayer)
			return activePlayerWinTreasureValue; 
		else if (player == battle.helper)
			return helperWinTreasureValue;
		else
			return 0;
	}
	
	/**
	 * Returns the estimated bad stuff cost the given player will suffer if he loses the battle.
	 * @param player the player to check
	 * @return the estimated bad stuff cost the given player will suffer if he loses the battle
	 */
	public int getPlayerBadStuffCost(Player player) {
		if (player == null)
			return 0;
		else if (player == battle.activePlayer) {
			int badStuffCost = 0;
			for (BattleMonster battleMonster : battleMonsters)
				badStuffCost += battleMonster.activePlayerBadStuffCost;
			return badStuffCost; 
		}
		else if (player == battle.helper) {
			int badStuffCost = 0;
			for (BattleMonster battleMonster : battleMonsters)
				badStuffCost += battleMonster.helperBadStuffCost;
			return badStuffCost; 
		}
		else
			return 0;
	}
}
