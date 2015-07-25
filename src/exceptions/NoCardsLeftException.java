
package exceptions;

/**
 * Exception Class that will occur when the a card deck is empty and there are no discards to reshuffle.
 */
public class NoCardsLeftException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new NoCardsLeftException Exception.
	 */
	public NoCardsLeftException() {
		super();
	}
}
