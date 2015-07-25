
package model;

/**
 * Abstract class for a unit test
 * @author Cody Hawkins
 */
public abstract class UnitTest {

	protected int errorCount = 0;
	
	/**
	 * Runs all unit tests under this class
	 * @return the number of errors that occurred during testing (return errorCount;)
	 */
	public abstract int testAll();	
	
	/**
	 * If the statement is false prints out an error message and increments errorCount.
	 * @param statement value being asserted as true
	 */
	public void assertTrue(boolean statement) {
		if(statement == false) {
			Throwable err = new Throwable();
			err.printStackTrace();
			errorCount++;
		}
	}

	/**
	 * If the statement is true prints out an error message and increments errorCount.
	 * @param statement value being asserted as false
	 */
	public void assertFalse(boolean statement) {
		if(statement == true) {
			System.err.println(this.toString());
			Throwable err = new Throwable();
			err.printStackTrace();
			errorCount++;
		}
	}

	/**
	 * If the statement is null prints out an error message and increments errorCount.
	 * @param statement value being asserted as null
	 */
	public void assertNull(Object obj) {
		if(obj != null) {
			System.err.println(this.toString());
			Throwable err = new Throwable();
			err.printStackTrace();
			errorCount++;
		}
	}

	/**
	 * If the strings are not the same prints out an error message and increments errorCount.
	 * @param str1 first string being compared
	 * @param str2 second string being compared
	 */
	public void assertEquals(String str1, String str2) {
		if(!str1.equals(str2)) {
			System.err.println("\"" + str1 + "\" != \"" + str2 + "\"");
			
			Throwable err = new Throwable();
			err.printStackTrace();
			errorCount++;
		}
	}
	
	/**
	 * If the numbers are not equal prints out an error message and increments errorCount.
	 * @param num1 first number being compared
	 * @param num2 second number being compared
	 */
	public void assertEquals(int num1, int num2) {
		if(num1 != num2) {
			System.err.println("\"" + num1 + "\" != \"" + num2 + "\"");
			
			Throwable err = new Throwable();
			err.printStackTrace();
			errorCount++;
		}
	}
	
	/**
	 * If the numbers are not equal prints out an error message and increments errorCount.
	 * @param num1 first number being compared
	 * @param num2 second number being compared
	 */
	public void assertEquals(double num1, double num2) {
		if(num1 != num2) {
			System.err.println("\"" + num1 + "\" != \"" + num2 + "\"");
			
			Throwable err = new Throwable();
			err.printStackTrace();
			errorCount++;
		}
	}

	/**
	 * Prints out the error message and increments errorCount.
	 * @param errMsg the error message that will be displayed
	 */
	public void fail(String errMsg) {
		System.err.println(errMsg);
		Throwable err = new Throwable();
		err.printStackTrace();
		errorCount++;
	}
}

