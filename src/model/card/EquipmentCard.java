
package model.card;

import model.Class;
import model.Player;
import model.Race;

/**
 * Class that represents an Equipment Card.
 */
public class EquipmentCard extends TreasureCard {

	/**
	 * Enumerator for all types of Equipment Card.
	 */
	public enum EquipmentType {
		ONE_HAND,
		TWO_HANDS,
		HEADGEAR,
		ARMOR,
		FOOTGEAR,
		OTHER;
		
		/**
		 * Returns a brief description of the type of Equipment Card this is.
		 * @return a brief description of the type of Equipment Card this is
		 */
		public String toString() {
			switch (this) {
			case ONE_HAND:
				return "One-handed Item";
			case TWO_HANDS:
				return "Two-handed Item";
			case HEADGEAR:
				return "Headgear";
			case ARMOR:
				return "Armor";
			case FOOTGEAR:
				return "Footgear";
			default:
				return "Other Item";
			}
		}
	}

	// variables that define an equipment card
	protected int bonus;
	private EquipmentType type;
	private boolean isBig;
	private boolean isBelowWaist;
	private boolean isWeapon;
	
	/**
	 * Creates a new EquipmentCard Card.
	 * @param i unique id of the new card
	 * @param n name of the new card
	 * @param v the GP value of this card
	 * @param b the level bonus that this card gives
	 * @param t the equipment type of this card
	 * @param big whether or not this item is a big item
	 * @param belowWaist whether or not this item is an item worn below the waist
	 * @param weapon whether or not this item is a weapon
	 */
	public EquipmentCard(int i, String n, int v, int b, EquipmentType t, boolean big, boolean belowWaist, boolean weapon) {
		super(i, n);
		
		id = i;
		name = n;
		value = v;
		bonus = b;
		type = t;
		isBig = big;
		isBelowWaist = belowWaist;
		isWeapon = weapon;
	}
	
	/**
	 * Returns the level bonus that equipping this card gives for the given player. 
	 * @param player the player to check the equipment bonus for
	 * @return the level bonus that equipping this card gives
	 */
	public int getBonus(Player player) {
		return bonus;
	}
	
	/**
	 * Returns the extra bonus that this card gives only to the given race.  
	 * If the item can only be used by the given race then the full bonus will be returned.
	 * This method will be overridden by those monsters which have extra bonuses.
	 * @param race race to check
	 * @return the extra bonus that this card gives only to the given race
	 */
	public int getBonusToRace(Race race) {
		return 0;
	}
	
	/**
	 * Returns the extra bonus that this card gives only to the given character class.  
	 * If the item can only be used by the given character class then the full bonus will be returned;
	 * otherwise 0 will be returned.
	 * @param characterClass character class to check
	 * @return the extra bonus that this card gives only to the given character class
	 */
	public int getBonusToClass(Class characterClass) {
		return 0;
	}
	
	/**
	 * Returns the equipment type of this card.
	 * @return the equipment type of this card
	 */
	public EquipmentType getEquipmentType() {
		return type;
	}
	
	/**
	 * Returns whether or not this card is a big item.
	 * @return true if this card is a big item; false otherwise
	 */
	public boolean isBig() {
		return isBig;
	}
		
	/**
	 * Returns whether or not this card is worn below the waist.
	 * @return true if this card is worn below the waist; false otherwise
	 */
	public boolean isBelowWaist() {
		return isBelowWaist;
	}
	
	/**
	 * Returns whether or not this card is a weapon.
	 * @return true if this card is a weapon; false otherwise
	 */
	public boolean isWeapon() {
		return isWeapon;
	}
	
	/**
	 * Returns an empty string if this card can be equipped on the given player; otherwise returns the reason why it can't be equipped.  
	 * This method is overridden to provide any special conditions on what player race/class/sex can't equip this item.
	 * @param player the player to test equip this item on
	 * @return a blank message if it can be equipped successfully; otherwise an error message of why the item can't be equipped
	 */
	public String equip(Player player) {
		return "";
	}
}
