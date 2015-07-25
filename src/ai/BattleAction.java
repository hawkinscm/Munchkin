package ai;

import exceptions.EndGameException;
import gui.CurseDialog;
import gui.components.Messenger;
import model.Battle;
import model.GM;
import model.Player;
import model.card.Card;
import model.card.ClassCard;
import model.card.CurseCard;
import model.card.ItemCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;
import model.card.RaceCard;

/**
 * Class that represents a battle action by a card, with it's value, cost, and worth ratio to a player.
 * Also contains logic to perform the action.
 */
public class BattleAction {
	
	/**
	 * Enumerator for the different action types
	 */
	public enum ActionType {
		HAND_CARD,
		ITEM_CARD,
		TRANSFERRAL_POTION,
		DISCARD_RACE,
		DISCARD_CLASS,
		BERSERKING,
		TURNING,
		BACKSTAB;
	}
	
	private ActionType type;
	private Card card;
	private int benefit;
	private int cost;
	private double worth;
	private Object target;
	
	/**
	 * Creates a new BattleAction.
	 * @param t the type of action being represented
	 * @param a the card that represents an action (Example: Race card means use that Race's special ability; Item card means use that item; etc)
	 * @param b the number of levels this action will add to the player's current battle level
	 * @param c the value that the player will lose by using this action
	 */
	public BattleAction(ActionType t, Card a, int b, int c) {
		type = t;
		card = a;
		benefit = b;
		cost = c;
		if (c > 0)
			worth = ((double)b)/((double)c);
		else
			worth = b;
		
		target = null;
	}
	
	/**
	 * Creates a new BattleAction.
	 * @param t the type of action being represented
	 * @param a the card that represents an action (Example: Race card means use that Race's special ability; Item card means use that item; etc)
	 * @param b the number of levels this action will add to the player's current battle level
	 * @param c the value that the player will lose by using this action
	 * @param obj the player, monster, etc. that this action will be performed on
	 */
	public BattleAction(ActionType t, Card a, int b, int c, Object obj) {
		type = t;
		card = a;
		benefit = b;
		cost = c;
		if (c > 0)
			worth = ((double)b)/((double)c);
		else
			worth = b;
		
		target = obj;
	}
	
	/**
	 * Returns the type of action that this is.
	 * @return the action type that this represents
	 */
	public ActionType getType() {
		return type;
	}
	
	/**
	 * Returns the card representing the action.
	 * @return the card representing the action
	 */
	public Card getCard() {
		return card;
	}
	
	/**
	 * Returns the number of battle levels that will be gained by performing this action. 
	 * @return the number of battle levels that will be gained by performing this action
	 */
	public int getBenefit() {
		return benefit;
	}
	
	/**
	 * Sets the number of battle levels that will be gained by performing this action.
	 * This is used whenever other actions of more worth change the value of this action.
	 * @param b the level number to set the benefit to
	 */
	public void setBenefit(int b) {
		benefit = b;
		if (cost > 0)
			worth = ((double)benefit)/((double)cost);
		else
			worth = benefit;
	}
	
	/**
	 * Returns the value that it will cost to perform this battle action.
	 * @return the value that it will cost to perform this battle action
	 */
	public int getCost() {
		return cost;
	}
	
	/**
	 * Returns the worth of this battle action, an absolute ratio of benefit over cost.
	 * @return the worth of this battle action, an absolute ratio of benefit over cost
	 */
	public double getWorth() {
		return Math.abs(worth);
	}
	
	/**
	 * Performs the battle action if it has worth and returns whether or not it was performed.
	 * @param battle battle to perform action in
	 * @param player player performing the action
	 */
	public boolean perform(Battle battle, Player player) throws EndGameException {
		String gender = "his";
		if (player.isFemale())
			gender = "her";
		
		switch (type) {
			case HAND_CARD :
				if (card instanceof MonsterEnhancerCard) {
					if (benefit == 0)
						return false;
					
					player.getHandCards().remove(card);
					battle.addMonsterEnhancer((MonsterCard)target, (MonsterEnhancerCard)card);
					String message = player + " played " + card;
					message += " on the " + ((MonsterCard)target) + ".";
					Messenger.display(message, "Card From Hand Played");
					return true;
				}
				else if (card instanceof CurseCard) {
					if (benefit > 0)
						return false;
					
					Player victim = (Player)target;
					if (card.getID() == Card.CU_INCOME_TAX)
						victim = player;
					
					if (benefit == 0) {
						if (card.getID() == Card.CU_LOSE_1_SMALL_ITEM_1 || card.getID() == Card.CU_LOSE_1_SMALL_ITEM_2) {
							if (victim.getAllItems().size() - victim.getBigItems().size() == 0)
								return false;
						}
						else if (card.getID() == Card.CU_LOSE_TWO_CARDS) {
							if (victim.getHandCards().size() < 2)
								return false;
						}
						else if (card.getID() == Card.CU_LOSE_YOUR_CLASS) {
							if (victim.getClassCards().size() < 1)
								return false;
						}
						else if (card.getID() == Card.CU_LOSE_YOUR_RACE) {
							if (victim.getRaceCards().size() < 1)
								return false;
						}
						else
							return false;
					}
					
					String message = player + " played the " + card + " curse";
					message += " on " + victim + ".";
					Messenger.display(message, "Card From Hand Played");
					player.getHandCards().remove(card);
					CurseDialog curseDialog = new CurseDialog(victim, (CurseCard)card, false);
					curseDialog.setVisible(true);
					return true;
				}
				break;
			case TRANSFERRAL_POTION :
				Player newPlayer = (Player)target;
				String transMessage = player.getName() + " used the " + card;
				transMessage += " to replace " + battle.activePlayer.getName();
				transMessage += " with " + newPlayer + " in the battle.";
				Messenger.display(transMessage, "Battle Item Used");
				battle.replaceActivePlayer(newPlayer);
				player.discard(card);
			
				GM.setCanLootRoom();
				return true;
			case ITEM_CARD :
				if (benefit > 0) {
					String itemMessage = player + " used the " + card + " against the monsters.";
					Messenger.display(itemMessage, "Battle Item Used");
					player.getCarriedItems().remove(card);
					battle.addPlayerItemCard((ItemCard)card);
				}
				else {
					String itemMessage = player + " used the " + card + " against the players.";
					Messenger.display(itemMessage, "Battle Item Used");
					player.getCarriedItems().remove(card);
					battle.addMonsterItemCard((ItemCard)card);
				}
				return true;
			case DISCARD_RACE :
				String raceMessage = player + " discarded " + gender + " " + card + " race.";
				Messenger.display(raceMessage, "Race Discarded");
				player.discardRaceCard((RaceCard)card);
				return true;
			case DISCARD_CLASS :
				String classMessage = player + " discarded " + gender + " " + card + " class.";
				Messenger.display(classMessage, "Class Discarded");
				player.discardClassCard((ClassCard)card);
				return true;
			case BERSERKING :
				player.discard(card);
				battle.addBerserking(player);
				String berserkingMessage = player + " used the Berserking Ability by discarding the " + card + " card.";
				Messenger.display(berserkingMessage, "Class Power Used");
				return true;
			case TURNING :
				player.discard(card);
				battle.addTurning(player);
				String turningMessage = player + " used the Turning Ability by discarding the " + card + " card.";
				Messenger.display(turningMessage, "Class Power Used");
				return true;
			case BACKSTAB :
				Player victim = (Player)target;
				player.discard(card);
				battle.backstab(player, victim);
				String backstabMessage = player + " backstabbed " + victim.getName();
				backstabMessage += " by discarding the " + card + " card.";
				Messenger.display(backstabMessage, "Class Power Used");
				return true;
		}
		
		return false;
	}
}
