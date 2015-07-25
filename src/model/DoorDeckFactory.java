
package model;

import exceptions.EndGameException;
import gui.DisplayCardsDialog;
import gui.LoseCardsDialog;
import gui.LoseGPDialog;
import gui.OptionDialog;
import gui.TakeCardDialog;
import gui.components.Messenger;

import java.util.Stack;
import java.util.LinkedList;
import java.util.Iterator;

import javax.swing.JDialog;

import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.OtherDoorCard;
import model.card.RaceCard;
import model.card.TreasureCard;

import ai.AICardEvaluator;
import ai.AIManager;
import ai.AIValuedCard;

/**
 * Class with static methods for building a Door Deck.
 */
public class DoorDeckFactory {
		
	// the door deck to be built
	private static Stack<Card> deck = null;
	
	/**
	 * Creates and returns all cards in the Door Deck.
	 * @return stack of all Door Deck cards
	 */
	public static Stack<Card> buildDeck() {
		deck = new Stack<Card>();
		
		// Add the 94 Door Cards
		addMonsterCards();
		addRaceCards();
		addClassCards();
		addCurseCards();
		addMonsterEnhancerCards();
		addOtherDoorCards();
		
		return deck;
	}
	
	/**
	 * Creates and adds all Monster Cards to the Door Deck.
	 */
	private static void addMonsterCards() {
		int id = 100;
		
		deck.push(new MonsterCard(++id, "3,872 Orcs", 10, 3, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (!player.isHuman() && player.isDwarf())
					return level + 6;
				
				return level;
			}
			
			@Override
			public int getRaceBonus(Race race) {
				if (race == Race.DWARF)
					return 6;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				int roll = Randomizer.rollDice(player);
				
				// allow computer to use Loaded Die if available
				if (player.isComputer()) {
					Messenger.display(player + " rolled a " + roll, "3,872 Orcs - Bad Stuff Roll");
					
					ItemCard loadedDie = null;
					for (TreasureCard item : player.getAllValueCards())
						if (item.getID() == Card.I_LOADED_DIE) {
							loadedDie = (ItemCard)item;
							break;
						}
					
					if (loadedDie != null) {
						int dyingCost = AIManager.getDeathCost(player, player.getHandCards());
						if (dyingCost < 0) {
							if (roll > 2) {
								player.discard(loadedDie);
								roll = 1;
								String message = player + " used the Loaded Die card to determine the Bad Stuff";
								message += " from the 3,872 Orcs.  Die set to 1; " + player + " dies.";
								Messenger.display(message, "Item Used");
							}
						}
						else {
							int minLevelLoss = Math.min(3, player.getLevel() - 1);
							int leastValueLoss = Math.min(dyingCost, minLevelLoss * AIManager.LEVEL_VALUE);
							int currentValueLoss = dyingCost;
							if (roll > 2)
								currentValueLoss = Math.min(roll, player.getLevel() - 1) * AIManager.LEVEL_VALUE;
							
							if (currentValueLoss - leastValueLoss > AICardEvaluator.getCardValueToPlayer(loadedDie, player, player.getHandCards())) {
								player.discard(loadedDie);
								String result;
								if (leastValueLoss == dyingCost) {
									roll = 1;
									result = "1; " + player + " dies.";
								}
								else {
									roll = 3;
									result = "3; " + player + " loses 3 levels.";
								}
								String message = player + " used the Loaded Die card to determine the Bad Stuff";
								message += " from the 3,872 Orcs.  Die set to " + result;
								Messenger.display(message, "Item Used");
							}
						}						
					}
				}				
				
				if (roll == 1 || roll == 2)
					player.die();
				else
					player.goDownLevels(roll);
			}
		});
		
		deck.push(new MonsterCard(++id, "Amazon", 8, 2, 1, false) {
			public void doBadStuff(Player player) {
				LinkedList<ClassCard> classCards = new LinkedList<ClassCard>();
				classCards.addAll(player.getClassCards());
				Iterator<ClassCard> classCardIter = classCards.iterator();
				if (!classCardIter.hasNext())
					player.goDownLevels(3);
				while (classCardIter.hasNext()) 
					player.discardClassCard(classCardIter.next());
			}
		});
		
		deck.push(new MonsterCard(++id, "Bigfoot", 12, 3, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (!player.isHuman() && (player.isDwarf() || player.isHalfling()))
					return level + 3;
				
				return level;
			}

			@Override
			public int getRaceBonus(Race race) {
				if (race == Race.DWARF || race == Race.HALFLING)
					return 3;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR)
						player.discard(item);
				
				player.removeChickenOnHeadCurse();
			}
		});
		
		deck.push(new MonsterCard(++id, "Bullrog", 18, 5, 2, false) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.getLevel() <= 4);
			}
			
			public void doBadStuff(Player player) {
				player.die();
			}
		});
		
		deck.push(new MonsterCard(++id, "Crabs", 1, 1, 1, false) {
			public void doBadStuff(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.ARMOR || item.isBelowWaist())
						player.discard(item);
			}
		});
		
		deck.push(new MonsterCard(++id, "Drooling Slime", 1, 1, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (!player.isHuman() && player.isElf())
					return level + 4;
				
				return level;
			}

			@Override
			public int getRaceBonus(Race race) {
				if (race == Race.ELF)
					return 4;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				boolean hasFootgear = false;
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.FOOTGEAR) {
						hasFootgear = true;
						player.discard(item);
					}
				
				if (!hasFootgear)
					player.goDownLevel();
			}
		});
		
		deck.push(new MonsterCard(++id, "Face Sucker", 8, 2, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (!player.isHuman() && player.isElf())
					return level + 6;
				
				return level;
			}

			@Override
			public int getRaceBonus(Race race) {
				if (race == Race.ELF)
					return 6;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR)
						player.discard(item);
				
				player.removeChickenOnHeadCurse();
				player.goDownLevel();
			}
		});
		
		deck.push(new MonsterCard(++id, "Floating Nose", 10, 3, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevels(3);
			}
		});
		
		deck.push(new MonsterCard(++id, "Flying Frogs", 2, 1, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevels(2);
			}
		});
		
		deck.push(new MonsterCard(++id, "Gazebo", 8, 2, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevels(3);
			}
		});
		
		deck.push(new MonsterCard(++id, "Gelatinous Octahedron", 2, 1, 1, false) {
			public void doBadStuff(Player player) {				
				LinkedList<EquipmentCard> equipmentItems = new LinkedList<EquipmentCard>();
				equipmentItems.addAll(player.getEquippedItems());
				equipmentItems.addAll(player.getUnequippedItems());
				for (EquipmentCard item : equipmentItems)
					if (item.isBig())
						player.discard(item);
			}
		});
		
		deck.push(new MonsterCard(++id, "Ghoulfiends", 8, 2, 1, false) {			
			public void doBadStuff(Player player) {
				int levelDiff = player.getLevel() - GM.getLowestLevel();
				player.goDownLevels(levelDiff);
			}
		});
		
		deck.push(new MonsterCard(++id, "Harpies", 4, 2, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (player.isWizard())
					return level + 5;
				
				return level;
			}

			@Override
			public int getClassBonus(Class characterClass) {
				if (characterClass == Class.WIZARD)
					return 5;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				player.goDownLevels(2);
			}
		});
		
		deck.push(new MonsterCard(++id, "Hippogriff", 16, 4, 2, false) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.getLevel() <= 3);
			}
			
			public void doBadStuff(Player player) {
				Player taker = GM.getPlayerRight(player);
				while (taker != player) {
					TakeCardDialog dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.TREASURE, false, false);
					dialog.setVisible(true);
					
					taker = GM.getPlayerRight(taker);
				}
			}
		});
		
		deck.push(new MonsterCard(++id, "Insurance Salesman", 14, 4, 1, false) {			
			public void doBadStuff(Player player) {
				LoseGPDialog dialog = new LoseGPDialog(player, 1000);
				dialog.setVisible(true);
				if (!dialog.hasDiscardedItems()) {
					Iterator<TreasureCard> valueCardIter = player.getAllItems().iterator();
					while (valueCardIter.hasNext())
						player.discard(valueCardIter.next());
				}
			}
		});
		
		deck.push(new MonsterCard(++id, "King Tut", 16, 4, 2, true) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.getLevel() <= 3);
			}
			
			public void doBadStuff(Player player) {
				player.goDownLevels(2);
				
				Iterator<TreasureCard> itemIter = player.getAllItems().iterator();
				while (itemIter.hasNext())
					player.discard(itemIter.next());
				
				LinkedList<Card> handCards = new LinkedList<Card>();
				handCards.addAll(player.getHandCards());
				Iterator<Card> cardIter = handCards.iterator();
				while (cardIter.hasNext())
					player.discard(cardIter.next());
			}
		});
		
		deck.push(new MonsterCard(++id, "Lame Goblin", 1, 1, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevel();
			}
		});
		
		deck.push(new MonsterCard(++id, "Large Angry Chicken", 2, 1, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevel();
			}
		});
		
		deck.push(new MonsterCard(++id, "Lawyer", 6, 2, 1, false) {
			public void doBadStuff(Player player) {
				Player taker = GM.getPlayerLeft(player);
				while (taker != player) {
					TakeCardDialog dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.HAND, false, false);
					dialog.setVisible(true);
					
					taker = GM.getPlayerLeft(taker);
				}
				
				LinkedList<Card> handCards = new LinkedList<Card>();
				handCards.addAll(player.getHandCards());
				Iterator<Card> cardsIter = handCards.iterator();
				while (cardsIter.hasNext())
					player.discard(cardsIter.next());				
			}
		});
		
		deck.push(new MonsterCard(++id, "Leperchaun", 4, 2, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (!player.isHuman() && player.isElf())
					return level + 5;
					
				return level;
			}

			@Override
			public int getRaceBonus(Race race) {
				if (race == Race.ELF)
					return 5;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				Player taker = GM.getPlayerLeft(player);
				TakeCardDialog dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.CARRIED, false, true);
				dialog.setVisible(true);
				
				taker = GM.getPlayerRight(player);
				dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.CARRIED, false, true);
				dialog.setVisible(true);
			}
		});
		
		deck.push(new MonsterCard(++id, "Maul Rat", 1, 1, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (player.isCleric())
					return level + 3;
				
				return level;
			}

			@Override
			public int getClassBonus(Class characterClass) {
				if (characterClass == Class.CLERIC)
					return 3;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				player.goDownLevel();
			}
		});
		
		deck.push(new MonsterCard(++id, "Mr. Bones", 2, 1, 1, true) {
			public void doBadStuff(Player player) {
				player.goDownLevels(3);
			}
		});
		
		deck.push(new MonsterCard(++id, "Net Troll", 10, 3, 1, false) {
			public void doBadStuff(Player player) {
				Iterator<Player> playerIter = GM.getHighestLevelPlayers().iterator();
				while (playerIter.hasNext()) {
					Player taker = playerIter.next();
					if (taker == player)
						continue;
					
					TakeCardDialog dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.CARRIED, false, false);
					dialog.setVisible(true);
				}
			}
		});
		
		deck.push(new MonsterCard(++id, "Pit Bull", 2, 1, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevels(2);
			}
		});
		
		deck.push(new MonsterCard(++id, "Platycore", 6, 2, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (player.isWizard())
					return level + 6;
				
				return level;
			}

			@Override
			public int getClassBonus(Class characterClass) {
				if (characterClass == Class.WIZARD)
					return 6;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				if (player.getHandCards().isEmpty()) {
					String message = player + " has no cards in hand; lose 2 levels.";
					Messenger.display(message, "Caught By Platycore");
				}
				else {
					if (player.isComputer()) {
						int handValue = 0;
						for (Card card : player.getHandCards())
							handValue += AICardEvaluator.getCardValueToPlayer(card, player, player.getHandCards());
						
						int levelValue = AIManager.LEVEL_VALUE * 2;
						if (player.getLevel() == 2)
							levelValue = AIManager.LEVEL_VALUE;
						else if (player.getLevel() == 1)
							levelValue = 0;
						
						if (handValue <= levelValue) {
							LinkedList<Card> handCards = new LinkedList<Card>();
							handCards.addAll(player.getHandCards());
							Iterator<Card> cardIter = handCards.iterator();
							while (cardIter.hasNext())
								player.discard(cardIter.next());
							String gender = (player.isMale()) ? "his" : "her";
							String message = player + " chooses to discard " + gender + " all cards in hand.";
							Messenger.display(message, "Caught By Platycore");
						}
						else {
							player.goDownLevels(2);
							String message = player + " chooses to lose 2 levels.";
							Messenger.display(message, "Caught By Platycore");
						}
						
						return;
					}
					
					String prompt = player.getName() + ", discard your hand or lose 2 levels.";
					OptionDialog dialog = new OptionDialog("Caught By Platycore", prompt, "Discard Hand", "Lose 2 Levels");
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dialog.setVisible(true);
					if (dialog.getChoice() == 1) {
						LinkedList<Card> handCards = new LinkedList<Card>();
						handCards.addAll(player.getHandCards());
						Iterator<Card> cardIter = handCards.iterator();
						while (cardIter.hasNext())
							player.discard(cardIter.next());
							
						return;
					}					
				}
				
				player.goDownLevels(2);
			}
		});
		
		deck.push(new MonsterCard(++id, "Plutonium Dragon", 20, 5, 2, false) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.getLevel() <= 5);
			}
			
			public void doBadStuff(Player player) {
				player.die();
			}
		});
		
		deck.push(new MonsterCard(++id, "Potted Plant", 1, 1, 1, false) {
			@Override
			public boolean isAutoEscape(Player player) {
				return true;
			}
			
			public void doBadStuff(Player player) {}
		});
		
		deck.push(new MonsterCard(++id, "Shrieking Geek", 6, 2, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (player.isWarrior())
					return level + 6;
				
				return level;
			}

			@Override
			public int getClassBonus(Class characterClass) {
				if (characterClass == Class.WARRIOR)
					return 6;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				LinkedList<RaceCard> raceCards = new LinkedList<RaceCard>();
				raceCards.addAll(player.getRaceCards());
				Iterator<RaceCard> raceCardIter = raceCards.iterator();
				while (raceCardIter.hasNext())
					player.discardRaceCard(raceCardIter.next());
				
				LinkedList<ClassCard> classCards = new LinkedList<ClassCard>();
				classCards.addAll(player.getClassCards());
				Iterator<ClassCard> classCardIter = classCards.iterator();
				while (classCardIter.hasNext())
					player.discardClassCard(classCardIter.next());
			}
		});
		
		deck.push(new MonsterCard(++id, "Snails of Speed", 4, 2, 1, false) {
			public void doBadStuff(Player player) {
				int roll = Randomizer.rollDice(player);
				
				if (player.isComputer()) {
					Messenger.display(player + " rolled a " + roll, "Snails of Speed - Bad Stuff Roll");
					
					AIValuedCard loadedDie = null;
					LinkedList<AIValuedCard> leastValuedCards = AIManager.getLeastValuedHandCards(player, player.getHandCards());
					for (AIValuedCard valuedCard : leastValuedCards)
						if (valuedCard.getCard().getID() == Card.I_LOADED_DIE)
							loadedDie = valuedCard;
					
					for (AIValuedCard valuedCard : AIManager.getLeastValuedItems(player, player.getHandCards())) {
						int cardIdx;
						for (cardIdx = 0; cardIdx < leastValuedCards.size(); cardIdx++)
							if (valuedCard.getValue() < leastValuedCards.get(cardIdx).getValue())
								break;
							
						leastValuedCards.add(cardIdx, valuedCard);
						if (valuedCard.getCard().getID() == Card.I_LOADED_DIE)
							loadedDie = valuedCard;
					}					
					
					if (loadedDie != null) {
						int loadedDieValue = loadedDie.getValue();
						int discardValue = 0;
						int count = 0;
						for (AIValuedCard valuedCard : leastValuedCards) {
							if (valuedCard == loadedDie)
								continue;
							
							discardValue += valuedCard.getValue();
							if (count == 0)
								loadedDieValue += valuedCard.getValue();
							
							count++;
							if (count >= roll)
								break;
						}
						
						if (loadedDieValue < discardValue) {
							leastValuedCards.remove(loadedDie);
							player.discard(loadedDie.getCard());
							roll = 1;
							String message = player + " used the Loaded Die to roll a 1.";
							Messenger.display(message, "Item Card Used");
						}
					}
					
					int count = 0;
					String discardList = "";
					for (AIValuedCard valuedCard : leastValuedCards) {
						player.discard(valuedCard.getCard());
						discardList += "\n" + valuedCard.getCard();
						
						count++;
						if (count >= roll)
							break;
					}
					
					String message = player + " discarded the following: " + discardList;
					Messenger.display(message, "Caught By Snails of Speed");

					return;
				}
				
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(player.getHandCards());
				cards.addAll(player.getAllItems());
				LoseCardsDialog dialog = new LoseCardsDialog(player, cards, roll, "cards");
				dialog.setVisible(true);
			}
		});
		
		deck.push(new MonsterCard(++id, "Squidzilla", 18, 4, 2, false) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.getLevel() <= 4 && (player.isHuman() || !player.isElf()));
			}
			
			public void doBadStuff(Player player) {
				player.die();
			}
		});
		
		deck.push(new MonsterCard(++id, "Stone Golem", 14, 4, 1, false) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.isHuman() || !player.isHalfling());
			}
			
			public void doBadStuff(Player player) {
				player.die();
			}
		});
		
		deck.push(new MonsterCard(++id, "The Nothing", 6, 2, 1, false) {
			public void doBadStuff(Player player) {
				LinkedList<Card> handCards = new LinkedList<Card>();
				handCards.addAll(player.getHandCards());
				Iterator<Card> cardIter = handCards.iterator();
				while (cardIter.hasNext())
					player.discard(cardIter.next());
			}
		});
		
		deck.push(new MonsterCard(++id, "Tongue Demon", 12, 3, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (player.isCleric())
					return level + 4;
				
				return level;
			}

			@Override
			public int getClassBonus(Class characterClass) {
				if (characterClass == Class.CLERIC)
					return 4;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				player.goDownLevels(2);
				if (!player.isHuman() && player.isElf())
					player.goDownLevel();
			}
		});
		
		deck.push(new MonsterCard(++id, "Undead Horse", 4, 2, 1, true) {
			@Override
			public int getLevel(Player player) {
				if (!player.isHuman() && player.isDwarf())
					return level + 5;
				
				return level;
			}

			@Override
			public int getRaceBonus(Race race) {
				if (race == Race.DWARF)
					return 5;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				player.goDownLevels(2);
			}
		});
		
		deck.push(new MonsterCard(++id, "Unspeakably Awful Indescribable Horror", 14, 4, 1, false) {
			@Override
			public int getLevel(Player player) {
				if (player.isWarrior())
					return level + 4;
				
				return level;
			}

			@Override
			public int getClassBonus(Class characterClass) {
				if (characterClass == Class.WARRIOR)
					return 4;
				
				return 0;
			}
			
			public void doBadStuff(Player player) {
				if (player.isWizard()) {
					LinkedList<ClassCard> classCards = new LinkedList<ClassCard>();
					classCards.addAll(player.getClassCards());
					Iterator<ClassCard> classCardIter = classCards.iterator();
					while (classCardIter.hasNext()) {
						ClassCard card = classCardIter.next();
						if (card.getCharacterClass() == Class.WIZARD)
							player.discardClassCard(card);
					}						
				}
				else
					player.die();
			}
		});
		
		deck.push(new MonsterCard(++id, "Wannabe Vampire", 12, 3, 1, false) {
			public void doBadStuff(Player player) {
				player.goDownLevels(3);
			}
		});
		
		deck.push(new MonsterCard(++id, "Wight Brothers", 16, 4, 2, true) {
			@Override
			public boolean isAutoEscape(Player player) {
				return (player.getLevel() <= 3);
			}
			
			public void doBadStuff(Player player) {
				player.goDownLevels(10);
			}
		});
	}
	
	/**
	 * Creates and adds all Race Cards to the Door Deck.
	 */
	private static void addRaceCards() {
		int id = 200;
		
		deck.push(new RaceCard(++id, "Elf", Race.ELF));
		deck.push(new RaceCard(++id, "Elf", Race.ELF));
		deck.push(new RaceCard(++id, "Elf", Race.ELF));
		deck.push(new RaceCard(++id, "Dwarf", Race.DWARF));
		deck.push(new RaceCard(++id, "Dwarf", Race.DWARF));
		deck.push(new RaceCard(++id, "Dwarf", Race.DWARF));
		deck.push(new RaceCard(++id, "Halfling", Race.HALFLING));
		deck.push(new RaceCard(++id, "Halfling", Race.HALFLING));
		deck.push(new RaceCard(++id, "Halfling", Race.HALFLING));
	}
	
	/**
	 * Creates and adds all Class Cards to the Door Deck.
	 */
	private static void addClassCards() {
		int id = 300;
		
		deck.push(new ClassCard(++id, "Cleric", Class.CLERIC));
		deck.push(new ClassCard(++id, "Cleric", Class.CLERIC));
		deck.push(new ClassCard(++id, "Cleric", Class.CLERIC));
		deck.push(new ClassCard(++id, "Thief", Class.THIEF));
		deck.push(new ClassCard(++id, "Thief", Class.THIEF));
		deck.push(new ClassCard(++id, "Thief", Class.THIEF));
		deck.push(new ClassCard(++id, "Warrior", Class.WARRIOR));
		deck.push(new ClassCard(++id, "Warrior", Class.WARRIOR));
		deck.push(new ClassCard(++id, "Warrior", Class.WARRIOR));
		deck.push(new ClassCard(++id, "Wizard", Class.WIZARD));
		deck.push(new ClassCard(++id, "Wizard", Class.WIZARD));
		deck.push(new ClassCard(++id, "Wizard", Class.WIZARD));		
	}
	
	/**
	 * Creates and adds all Curse Cards to the Door Deck.
	 */
	private static void addCurseCards() {	
		int id = 400;
		
		deck.push(new CurseCard(++id, "Change Class") {
			public void addEffects(Player player) {
				LinkedList<ClassCard> classCards = new LinkedList<ClassCard>();
				classCards.addAll(player.getClassCards());
				Iterator<ClassCard> classCardIter = classCards.iterator();
				if (!classCardIter.hasNext()) {
					String message = player.getName() + " has no Class; curse has no effect.";
					Messenger.display(message, "Change Class Curse");
					return;
				}
				
				String message = player.getName() + " changed from a";
				
				ClassCard newClassCard = null;
				LinkedList<Card> doorDiscards = new LinkedList<Card>();
				for (Card card : GM.getDoorDeck().getDiscardPile())
					doorDiscards.addFirst(card);					
				for (Card card : doorDiscards) {
					if (card instanceof ClassCard) {
						GM.getDoorDeck().getDiscardPile().remove(card);
						newClassCard = (ClassCard)card;
						break;
					}
				}
				
				while (classCardIter.hasNext()) {
					ClassCard card = classCardIter.next();
					if (newClassCard != null) {
						player.getClassCards().remove(card);
						GM.getDoorDeck().discard(card);
					}
					else
						player.discardClassCard(card);
					
					message += " " + card;
				}
				
				player.loseSuperMunchkin();
				
				try {
					if (newClassCard != null) {
						Player activePlayer = GM.getActivePlayer();
						GM.setActivePlayer(player);
						CardPlayManager.playCard(player, newClassCard);
						GM.setActivePlayer(activePlayer);
						message += " to a " + newClassCard + ".";
					}
					else
						message += " to having no Class.";
				}
				catch (EndGameException ex) {}
				
				Messenger.display(message, "Change Class Curse");
			}
		});
		
		deck.push(new CurseCard(++id, "Change Race") {
			public void addEffects(Player player) {
				LinkedList<RaceCard> raceCards = new LinkedList<RaceCard>();
				raceCards.addAll(player.getRaceCards());
				Iterator<RaceCard> raceCardIter = raceCards.iterator();
				if (!raceCardIter.hasNext()) {
					String message = player.getName() + " is a Human; curse has no effect.";
					Messenger.display(message, "Change Race Curse");
					return;
				}
				
				String message = player.getName() + " changed from a";
				if (!player.isHalfBreed() && player.isElf())
					message += "n";
				
				RaceCard newRaceCard = null;
				LinkedList<Card> doorDiscards = new LinkedList<Card>();
				for (Card card : GM.getDoorDeck().getDiscardPile())
					doorDiscards.addFirst(card);				
				for (Card card : doorDiscards) {
					if (card instanceof RaceCard) {
						GM.getDoorDeck().getDiscardPile().remove(card);
						newRaceCard = (RaceCard)card;
						break;
					}
				}
				
				String halfBreedText = "";
				if (player.isHalfBreed())
					halfBreedText = "Half-";
				
				while (raceCardIter.hasNext()) {					
					RaceCard card = raceCardIter.next();
					if (newRaceCard != null) {
						player.getRaceCards().remove(card);
						GM.getDoorDeck().discard(card);
					}
					else
						player.discardRaceCard(card);
					
					message += " " + halfBreedText + card;
				}
				
				player.loseHalfBreed();
				
				try {
					if (newRaceCard != null) {
						Player activePlayer = GM.getActivePlayer();
						GM.setActivePlayer(player);
						CardPlayManager.playCard(player, newRaceCard);
						GM.setActivePlayer(activePlayer);
						if (newRaceCard.getRace() == Race.ELF)
							message += " to an Elf.";
						else
							message += " to a " + newRaceCard + ".";	
					}
					else
						message += " to a Human.";
				}
				catch (EndGameException ex) {}
				
				Messenger.display(message, "Change Race Curse");
			}
		});
		
		deck.push(new CurseCard(++id, "Change Sex") {
			public void addEffects(Player player) {
				player.changeSex();
				player.addLastingCurse(this);
			}
			
			@Override
			public boolean isLastingCurse() {
				return true;
			}
		});
		
		deck.push(new CurseCard(++id, "Chicken on Your Head") {
			public void addEffects(Player player) {
				player.addLastingCurse(this);
			}
			
			@Override
			public boolean isLastingCurse() {
				return true;
			}
		});
		
		deck.push(new CurseCard(++id, "Duck of Doom") {
			public void addEffects(Player player) {
				int oldLevel = player.getLevel();
				player.goDownLevels(2);
				String message = player.getName() + " went from level " + oldLevel + " to level " + player.getLevel() + ".";
				Messenger.display(message, "Duck of Doom Curse");
				return;
			}
		});
		
		deck.push(new CurseCard(++id, "Income Tax") {
			public void addEffects(Player player) {
				String type = "item";
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(player.getAllItems());
				LoseCardsDialog loseCardDialog = new LoseCardsDialog(player, cards, 1, type);
				loseCardDialog.setVisible(true);
				
				if (!loseCardDialog.madeDiscard()) {
					String message = player.getName() + " has no items; curse has no effect.";
					Messenger.display(message, "Income Tax Curse");
					return;
				}
					
				
				int tax = ((TreasureCard)GM.getTreasureDeck().getDiscardPile().peek()).getValue();
				Iterator<Player> playerIter = GM.getPlayers().iterator();
				while (playerIter.hasNext()) {
					Player victim = playerIter.next();
					if (victim == player)
						continue;
					
					LoseGPDialog loseGPDialog = new LoseGPDialog(victim, tax);
					loseGPDialog.setVisible(true);
					if (!loseGPDialog.hasDiscardedItems()) {
						LinkedList<TreasureCard> carriedItems = victim.getAllItems();
						int itemCount = carriedItems.size();
						Iterator<TreasureCard> itemIter = carriedItems.iterator();
						while (itemIter.hasNext())
							victim.discard(itemIter.next());
						
						victim.goDownLevel();
						
						String message = victim + " lost " + itemCount + " item(s) and 1 level to become a level " + victim.getLevel() + ".";
						Messenger.display(message, "Income Tax Curse");
					}
				}
			}
		});
		
		deck.push(new CurseCard(++id, "Lose 1 Big Item") {
			public void addEffects(Player player) {	
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(player.getBigItems());
				
				LoseCardsDialog dialog = new LoseCardsDialog(player, cards, 1, "big item");
				dialog.setVisible(true);
			}
		});
		
		deck.push(new CurseCard(++id, "Lose 1 Level") {
			public void addEffects(Player player) {
				int oldLevel = player.getLevel();
				player.goDownLevel();
				String message = player.getName() + " went from level " + oldLevel + " to level " + player.getLevel() + ".";
				Messenger.display(message, "Lose 1 Level Curse");
			}
		});
		
		deck.push(new CurseCard(++id, "Lose 1 Level") {
			public void addEffects(Player player) {
				int oldLevel = player.getLevel();
				player.goDownLevel();
				String message = player.getName() + " went from level " + oldLevel + " to level " + player.getLevel() + ".";
				Messenger.display(message, "Lose 1 Level Curse");
			}
		});
		
		deck.push(new CurseCard(++id, "Lose 1 Small Item") {
			public void addEffects(Player player) {
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(player.getCarriedItems());
				
				for (EquipmentCard item : player.getAllEquipment())
					if (!item.isBig())
						cards.add(item);
				
				LoseCardsDialog dialog = new LoseCardsDialog(player, cards, 1, "small item");
				dialog.setVisible(true);
			}
		});
		
		deck.push(new CurseCard(++id, "Lose 1 Small Item") {
			public void addEffects(Player player) {
				LinkedList<Card> cards = new LinkedList<Card>();
				cards.addAll(player.getCarriedItems());
				
				for (EquipmentCard item : player.getAllEquipment())
					if (!item.isBig())
						cards.add(item);
				
				LoseCardsDialog dialog = new LoseCardsDialog(player, cards, 1, "small item");
				dialog.setVisible(true);
			}
		});
		
		deck.push(new CurseCard(++id, "Lose the Armor You Are Wearing") {
			public void addEffects(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.ARMOR) {
						player.discard(item);
						DisplayCardsDialog dialog = new DisplayCardsDialog(item, "Discarded");
						dialog.setVisible(true);
					}
			}
		});
		
		deck.push(new CurseCard(++id, "Lose the Footgear You Are Wearing") {
			public void addEffects(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.FOOTGEAR) {
						player.discard(item);
						DisplayCardsDialog dialog = new DisplayCardsDialog(item, "Discarded");
						dialog.setVisible(true);
					}
			}
		});
		
		deck.push(new CurseCard(++id, "Lose the Headgear You Are Wearing") {
			public void addEffects(Player player) {
				LinkedList<EquipmentCard> equippedItems = new LinkedList<EquipmentCard>();
				equippedItems.addAll(player.getEquippedItems());
				for (EquipmentCard item : equippedItems)
					if (item.getEquipmentType() == EquipmentCard.EquipmentType.HEADGEAR) {
						player.discard(item);
						DisplayCardsDialog dialog = new DisplayCardsDialog(item, "Discarded");
						dialog.setVisible(true);
					}
				
				player.removeChickenOnHeadCurse();
			}
		});
				
		deck.push(new CurseCard(++id, "Lose Two Cards") {
			public void addEffects(Player player) {
				Player taker = GM.getPlayerLeft(player);
				TakeCardDialog dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.HAND, false, false);
				dialog.setVisible(true);
				
				taker = GM.getPlayerRight(player);
				dialog = new TakeCardDialog(player, taker, TakeCardDialog.DrawLocation.HAND, false, false);
				dialog.setVisible(true);
			}
		});
		
		deck.push(new CurseCard(++id, "Lose Your Class") {
			public void addEffects(Player player) {
				LinkedList<ClassCard> classCards = player.getClassCards();
				if (classCards.size() == 0) {
					int oldLevel = player.getLevel();
					player.goDownLevel();
					String message = "<html> " + player + " has no Class; lose 1 level. <br>";
					message += player + " went from level " + oldLevel + " to level " + player.getLevel() + ". <html>";
					Messenger.display(message, "Lose Your Class Curse");
				}
				else if (classCards.size() == 1)
					player.discardClassCard(classCards.getFirst());
				else {
					LinkedList<Card> cards = new LinkedList<Card>();
					cards.addAll(classCards);
					LoseCardsDialog dialog = new LoseCardsDialog(player, cards, 1, "Class card");
					dialog.setVisible(true);
				}
			}
		});
		
		deck.push(new CurseCard(++id, "Lose Your Race") {
			public void addEffects(Player player) {
				LinkedList<RaceCard> raceCards = new LinkedList<RaceCard>();
				raceCards.addAll(player.getRaceCards());
				Iterator<RaceCard> raceCardIter = raceCards.iterator();
				while (raceCardIter.hasNext()) {
					RaceCard card = raceCardIter.next();
					player.discardRaceCard(card);
				}
			}
		});
		
		deck.push(new CurseCard(++id, "Malign Mirror") {
			public void addEffects(Player player) {
				player.addLastingCurse(this);
			}
			
			@Override
			public boolean isLastingCurse() {
				return true;
			}
		});
		
		deck.push(new CurseCard(++id, "Truly Obnoxious Curse") {
			public void addEffects(Player player) {
				int biggestBonus = -1;
				for (EquipmentCard item : player.getEquippedItems()) {
					int currentCardBonus = item.getBonus(player);
					if (currentCardBonus > biggestBonus)
						biggestBonus = currentCardBonus;
				}
				
				if (biggestBonus <= 0) {
					String message = "No items are currently giving " + player + " a bonus; curse has no effect.";
					Messenger.display(message, "Truly Obnoxious Curse");
					return;
				}
				
				LinkedList<Card> biggestBonusCards = new LinkedList<Card>();
				for (EquipmentCard item : player.getEquippedItems())
					if (item.getBonus(player) == biggestBonus)
						biggestBonusCards.add(item);
				
				String lossType = "of the items currently giving you the biggest bonus";
				LoseCardsDialog dialog = new LoseCardsDialog(player, biggestBonusCards, 1, lossType);
				dialog.setVisible(true);
			}
		});
	}

	/**
	 * Creates and adds all Monster Enhancer Cards to the Door Deck.
	 */
	private static void addMonsterEnhancerCards() {
		int id = 500;
		
		deck.push(new MonsterEnhancerCard(++id, "Ancient", 10));
		
		deck.push(new MonsterEnhancerCard(++id, "Baby", -5));
		
		deck.push(new MonsterEnhancerCard(++id, "Enraged", 5));
		
		deck.push(new MonsterEnhancerCard(++id, "Humongous", 10));
		
		deck.push(new MonsterEnhancerCard(++id, "Intelligent", 5));
	}

	/**
	 * Creates and adds all Other Door Cards to the Door Deck.
	 */
	private static void addOtherDoorCards() {
		int id = 600;
		
		deck.push(new OtherDoorCard(++id, "Cheat!"));
		
		deck.push(new OtherDoorCard(++id, "Divine Intervention"));
		
		deck.push(new OtherDoorCard(++id, "Half-Breed"));
		
		deck.push(new OtherDoorCard(++id, "Half-Breed"));
		
		deck.push(new OtherDoorCard(++id, "Help Me Out Here!"));
		
		deck.push(new OtherDoorCard(++id, "Illusion"));
		
		deck.push(new OtherDoorCard(++id, "Mate"));
		
		deck.push(new OtherDoorCard(++id, "Out to Lunch"));
		
		deck.push(new OtherDoorCard(++id, "Super Munchkin"));
		
		deck.push(new OtherDoorCard(++id, "Super Munchkin"));
		
		deck.push(new OtherDoorCard(++id, "Wandering Monster"));
		
		deck.push(new OtherDoorCard(++id, "Wandering Monster"));
	}
}
