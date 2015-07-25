package model;

/**
 * Enumerator for each type of player.
 */
public enum PlayerType {
	HUMAN,
	COMPUTER_EASY,
	COMPUTER_MEDIUM,
	COMPUTER_HARD,
	TEST;
	
	/**
	 * Returns a short description of what type of player this is.
	 * @return a short description of what type of player this is
	 */
	public String toString() {
		switch (this) {
			case HUMAN :
				return "Human";
			case COMPUTER_EASY :
				return "Munchkinette COM";
			case COMPUTER_MEDIUM :
				return "Munchkin COM";
			case COMPUTER_HARD :
				return "Munchkinator COM";
			default :
				return null;
		}				
	}
	
	/**
	 * Returns an array of all player types.
	 * @return an array of all player types
	 */
	public static PlayerType[] toArray() {
		PlayerType[] playerTypes = {PlayerType.HUMAN, PlayerType.COMPUTER_EASY, PlayerType.COMPUTER_MEDIUM, PlayerType.COMPUTER_HARD};
		return playerTypes;
	}
	
	public static PlayerType parseType(String str) {
		for (PlayerType type : values())
			if (str.equals(type.toString()))
				return type;
		
		return null;
	}
}

