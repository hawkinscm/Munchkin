
package exceptions;

/**
 * Exception Class that will trigger end of game events (and stop any current events).
 */
public class EndGameException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new EndGameException Exception.
	 */
	public EndGameException() {
		super();
	}
}
