
package model;

import gui.RollDiceDialog;

import java.util.Random;

/**
 * Class that handles randomization and the simulation of a dice roll.
 */
public class Randomizer {

	// generator of random numbers
	private static Random generator = new Random();
	
	/**
	 * Returns a random number between 0 and range exclusive.
	 * @param n the range of numbers that the random number can be; the upper bound
	 * @return a randomly generated number between 0 and range exclusive
	 */
	public static int getRandom(int range) {
		return generator.nextInt(range);
	}
	
	/**
	 * Returns the result of a simulated dice roll with player curses factored in.
	 * @param player the player who is rolling the dice
	 * @return a simulated dice roll: random number between 1 and 6 with player curses factored in
	 */
	public static int rollDice(Player player) {
		int roll = generator.nextInt(6) + 1;
		
		if (player.hasChickenOnHead()) {
			roll--;
			if (roll < 1)
				roll = 1;
		}
		
		if (player.isComputer())
			return roll;
		
		RollDiceDialog dialog = new RollDiceDialog(player, roll);
		dialog.setVisible(true);
		return dialog.getRoll();
	}
}
