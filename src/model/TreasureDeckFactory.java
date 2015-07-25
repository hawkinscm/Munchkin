
package model;

import java.util.Stack;

import model.card.Card;
import model.card.EquipmentCard;
import model.card.GoUpLevelCard;
import model.card.ItemCard;
import model.card.OtherTreasureCard;
import model.card.EquipmentCard.EquipmentType;

/**
 * Class with static methods for building a Treasure Deck.
 */
public class TreasureDeckFactory {
	private static final long serialVersionUID = 1L;

	// the treasure deck to be built
	private static Stack<Card> deck;
	
	/**
	 * Creates and returns all cards in the Treasure Deck.
	 * @return stack of all Treasure Deck cards
	 */
	public static Stack<Card> buildDeck() {
		deck = new Stack<Card>();
		
		// Adds the 74 Treasure Cards
		addEquipmentCards();
		addItemCards();
		addGoUpLevelCards();
		addOtherTreasureCards();
		
		return deck;
	}
	
	/**
	 * Creates and adds all Equipment Cards to the Treasure Deck.
	 */
	private static void addEquipmentCards() {
		int id = 700;
		
		// Cards will override the Equipment Card equip method to specify any restrictions on who can equip the item.
		deck.push(new EquipmentCard(++id, "Boots of Butt-Kicking", 400, 2, EquipmentType.FOOTGEAR, false, true, false));
		
		deck.push(new EquipmentCard(++id, "Boots of Running Really Fast", 400, 0, EquipmentType.FOOTGEAR, false, true, false));
		
		deck.push(new EquipmentCard(++id, "Bow With Ribbons", 800, 4, EquipmentType.TWO_HANDS, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isElf())
					return "You must be an Elf to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.ELF)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Broad Sword", 400, 3, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isFemale()) 
					return "You must be female to use this.";
				
				return super.equip(player);
			}
		});
		
		deck.push(new EquipmentCard(++id, "Buckler of Swashing", 400, 2, EquipmentType.ONE_HAND, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Cheese Grater of Peace", 400, 3, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isCleric())
					return "You must be a Cleric to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.CLERIC)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Cloak of Obscurity", 600, 4, EquipmentType.OTHER, false, true, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isThief())
					return "You must be a Thief to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.THIEF)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Dagger of Treachery", 400, 3, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isThief())
					return "You must be a Thief to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.THIEF)
					return bonus; 
					
				return 0;
			}			
		});
		
		deck.push(new EquipmentCard(++id, "Eleven-Foot Pole", 200, 1, EquipmentType.TWO_HANDS, false, false, true));
		
		deck.push(new EquipmentCard(++id, "Flamethrower", 600, 3, EquipmentType.TWO_HANDS, true, false, true));
		
		deck.push(new EquipmentCard(++id, "Flaming Armor", 400, 2, EquipmentType.ARMOR, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Gentlemen's Club", 400, 3, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isMale())
					return "You must be male to use this.";
				
				return super.equip(player);
			}
		});
		
		deck.push(new EquipmentCard(++id, "Hammer of Kneecapping", 600, 4, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isDwarf())
					return "You must be a Dwarf to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.DWARF)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Helm of Courage", 200, 1, EquipmentType.HEADGEAR, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Horned Helmet", 600, 1, EquipmentType.HEADGEAR, false, false, false) {
			@Override
			public int getBonus(Player player) {
				if (player.isElf())
					return 3;
				
				return 1;
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.ELF)
					return 2;
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Huge Rock", 0, 3, EquipmentType.TWO_HANDS, true, false, true));
		
		deck.push(new EquipmentCard(++id, "Kneepads of Allure", 600, 0, EquipmentType.OTHER, false, true, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && player.isCleric())
					return "Clerics cannot use this.";
				
				return super.equip(player);
			}
		});
		
		deck.push(new EquipmentCard(++id, "Leather Armor", 200, 1, EquipmentType.ARMOR, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Limburger and Anchovy Sandwich", 400, 3, EquipmentType.OTHER, false, false, false) {
			
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isHalfling())
					return "You must be a Halfling to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.HALFLING)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Mace of Sharpness", 600, 4, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isCleric()) 
					return "You must be a Cleric to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.CLERIC)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Mithril Armor", 600, 3, EquipmentType.ARMOR, true, false, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && player.isWizard())
					return "Wizards cannot use this.";
				
				return super.equip(player);
			}
		});
		
		deck.push(new EquipmentCard(++id, "Pantyhose of Giant Strength", 600, 3, EquipmentType.OTHER, false, true, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && player.isWarrior()) 
					return "Warriors cannot use this.";
				
				return super.equip(player);
			}
		});
		
		deck.push(new EquipmentCard(++id, "Pointy Hat of Power", 400, 3, EquipmentType.HEADGEAR, false, false, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isWizard()) 
					return "You must be a Wizard to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.WIZARD)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Rad Bandanna", 400, 3, EquipmentType.HEADGEAR, false, false, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isHuman())
					return "You must be a Human to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.HUMAN)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Rapier of Unfairness", 600, 3, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isElf())
					return "You must be an Elf to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.ELF)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Rat on a Stick", 0, 0, EquipmentType.ONE_HAND, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Really Impressive Title", 0, 3, EquipmentType.OTHER, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Sandals of Protection", 700, 0, EquipmentType.FOOTGEAR, false, true, false));
		
		deck.push(new EquipmentCard(++id, "Shield of Ubiquity", 600, 4, EquipmentType.ONE_HAND, true, false, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isWarrior())
					return "You must be a Warrior to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.WARRIOR)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Short Wide Armor", 400, 3, EquipmentType.ARMOR, false, false, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isDwarf())
					return "You must be a Dwarf to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.DWARF)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Singing & Dancing Sword", 400, 2, EquipmentType.OTHER, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && player.isThief())
					return "Thieves cannot use this.";
				
				return super.equip(player);
			}
		});
		
		deck.push(new EquipmentCard(++id, "Slimy Armor", 200, 1, EquipmentType.ARMOR, false, false, false));
		
		deck.push(new EquipmentCard(++id, "Sneaky Backsword", 400, 2, EquipmentType.ONE_HAND, false, false, true));
		
		deck.push(new EquipmentCard(++id, "Spiky Knees", 200, 1, EquipmentType.OTHER, false, true, false));
		
		deck.push(new EquipmentCard(++id, "Staff of Napalm", 800, 5, EquipmentType.ONE_HAND, false, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isWizard())
					return "You must be a Wizard to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToClass(Class characterClass) {
				if (characterClass == Class.WIZARD)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Stepladder", 400, 3, EquipmentType.OTHER, true, false, false) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isHalfling())
					return "You must be a Halfling to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.HALFLING)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Swiss Army Polearm", 600, 4, EquipmentType.TWO_HANDS, true, false, true) {
			@Override
			public String equip(Player player) {
				if (player.getCheatingItemCard() != this && !player.isHuman())
					return "You must be a Human to use this.";
				
				return super.equip(player);
			}
			
			@Override
			public int getBonusToRace(Race race) {
				if (race == Race.HUMAN)
					return bonus; 
					
				return 0;
			}
		});
		
		deck.push(new EquipmentCard(++id, "Tuba of Charm", 300, 0, EquipmentType.ONE_HAND, true, false, false));
	}
	
	/**
	 * Creates and adds all Item Cards to the Treasure Deck.
	 */
	private static void addItemCards() {
		int id = 800;
		
		deck.push(new ItemCard(++id, "Cotion of Ponfusion", 100, 3));
		
		deck.push(new ItemCard(++id, "Doppleganger", 300));
		
		deck.push(new ItemCard(++id, "Electric Radioactive Acid Potion", 200, 5));
		
		deck.push(new ItemCard(++id, "Flaming Poison Potion", 100, 3));
		
		deck.push(new ItemCard(++id, "Flask of Glue", 100));
		
		deck.push(new ItemCard(++id, "Freezing Explosive Potion", 100, 3));
		
		deck.push(new ItemCard(++id, "Friendship Potion", 200));
		
		deck.push(new ItemCard(++id, "Instant Wall", 300));
		
		deck.push(new ItemCard(++id, "Invisibility Potion", 200));
				
		deck.push(new ItemCard(++id, "Loaded Die", 300));
		
		deck.push(new ItemCard(++id, "Magic Lamp", 500));
		
		deck.push(new ItemCard(++id, "Magic Lamp", 500));
		
		deck.push(new ItemCard(++id, "Magic Missile", 300, 5));
		
		deck.push(new ItemCard(++id, "Nasty-Tasting Sports Drink", 200, 2));
		
		deck.push(new ItemCard(++id, "Pollymorph Potion", 1300));
		
		deck.push(new ItemCard(++id, "Potion of Halitosis", 100, 2));
		
		deck.push(new ItemCard(++id, "Potion of Idiotic Bravery", 100, 2));
		
		deck.push(new ItemCard(++id, "Pretty Balloons", 0, 5));
		
		deck.push(new ItemCard(++id, "Sleep Potion", 100, 2));
		
		deck.push(new ItemCard(++id, "Transferral Potion", 300));
		
		deck.push(new ItemCard(++id, "Wand of Dowsing", 1100));
		
		deck.push(new ItemCard(++id, "Wishing Ring", 500));
		
		deck.push(new ItemCard(++id, "Wishing Ring", 500));
		
		deck.push(new ItemCard(++id, "Yuppie Water", 100));
	}
	
	/**
	 * Creates and adds all Go Up A Level Cards to the Treasure Deck.
	 */
	private static void addGoUpLevelCards() {
		int id = 900;
		
		deck.push(new GoUpLevelCard(++id, "1,000 Gold Pieces"));		
		deck.push(new GoUpLevelCard(++id, "Boil an Anthill"));
		deck.push(new GoUpLevelCard(++id, "Bribe GM With Food"));
		deck.push(new GoUpLevelCard(++id, "Convenient Addition Error"));
		deck.push(new GoUpLevelCard(++id, "Invoke Obscure Rules"));
		deck.push(new GoUpLevelCard(++id, "Kill the Hireling"));
		deck.push(new GoUpLevelCard(++id, "Mow the Battlefield"));
		deck.push(new GoUpLevelCard(++id, "Potion of General Studliness"));
		deck.push(new GoUpLevelCard(++id, "Whine at the GM"));
	}
	
	/**
	 * Creates and adds all Other Treasure Cards to the Treasure Deck.
	 */
	private static void addOtherTreasureCards() {
		int id = 1000;
		
		deck.push(new OtherTreasureCard(++id, "Steal a Level"));
		deck.push(new OtherTreasureCard(++id, "Hireling"));
		deck.push(new OtherTreasureCard(++id, "Hoard!"));
	}
}
