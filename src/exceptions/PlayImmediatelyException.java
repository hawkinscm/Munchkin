
package exceptions;

/**
 * Exception class that occurs whenever a drawn card has to be played immediately.
 */
public class PlayImmediatelyException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new PlayImmediatelyException Exception.
	 */
	public PlayImmediatelyException() {
		super();
	}
}
