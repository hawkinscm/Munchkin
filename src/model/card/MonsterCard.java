
package model.card;

import model.Battle;
import model.Class;
import model.Player;
import model.Race;

/**
 * Abstract class that represents a Monster Card.
 */
public abstract class MonsterCard extends DoorCard {

	/**
	 * Level of the monster.
	 */
	protected int level;
	// variables that define a monster
	private int winLevels;
	private int treasures;
	private boolean isUndead;
	
	/**
	 * Creates a new MonsterCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param l level of the monster
	 * @param t number of treasures won by defeating the monster
	 * @param wLevels number of levels won by defeating the monster
	 * @param undead whether or not the monster is undead
	 */
	public MonsterCard(int i, String n, int l, int t, int wLevels, boolean undead) {
		super(i, n);
		
		id = i;
		name = n;
		level = l;
		treasures = t;
		winLevels = wLevels;
		isUndead = undead;
	}

	/**
	 * Abstract method that will define the bad stuff a monster will do if it catches the given player.
	 * @param player player who will suffer the bad stuff
	 */
	public abstract void doBadStuff(Player player);
	
	/**
	 * Returns whether or not the player can automatically escape the monster.
	 * @param player player to check
	 * @return true if the player can automatically escape the monster; false otherwise
	 */
	public boolean isAutoEscape(Player player) {
		return false;
	}
	
	/**
	 * Returns the battle level of the monster based on the status of the player.
	 * This method can be overridden to provide special level adjustments based on the monster/player.
	 * @param player player to check
	 * @return the battle level of the monster
	 */
	public int getLevel(Player player) {
		return level;
	}
	
	/**
	 * Returns the battle level of the monster based on the status of the player(s) in the battle.
	 * This method can be overridden to provide special level adjustments based on the monster/player(s).
	 * @param battle to check
	 * @return the battle level of the monster
	 */
	public int getLevel(Battle battle) {
		if (battle.isHelper())
			return Math.max(getLevel(battle.activePlayer), getLevel(battle.helper));
		else
			return getLevel(battle.activePlayer);
	}
	
	/**
	 * Returns the extra bonus that this monster has against the given race.
	 * This method will be overridden by those monsters which have extra bonuses.
	 * @param race race to check
	 * @return the extra bonus that this monster has against the given race
	 */
	public int getRaceBonus(Race race) {
		return 0;
	}
	
	/**
	 * Returns the extra bonus that this monster has against the given character class.
	 * This method will be overridden by those monsters which have extra bonuses.
	 * @param characterClass character class to check
	 * @return the extra bonus that this monster has against the given character class
	 */
	public int getClassBonus(Class characterClass) {
		return 0;
	}
	
	/**
	 * Returns the number of level won by defeating the monster.
	 * @return the number of level won by defeating the monster
	 */
	public int getWinLevels() {
		return winLevels;
	}
	
	/**
	 * Returns the number of treasures won by defeating the monster.
	 * @return the number of treasures won by defeating the monster
	 */
	public int getTreasures() {
		return treasures;
	}
	
	/**
	 * Returns whether or not the monster is undead.
	 * @return true if the monster is undead; false otherwise
	 */
	public boolean isUndead() {
		return isUndead;
	}
}
