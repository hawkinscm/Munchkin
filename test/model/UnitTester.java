
package model;

import java.util.LinkedList;
import java.util.Iterator;

/**
 * Test manager for a group of unit tests
 * @author Cody Hawkins
 */
public class UnitTester {

	private LinkedList<UnitTest> unitTests;
	
	/**
	 * Constructor
	 */
	public UnitTester() {
		unitTests = new LinkedList<UnitTest>();
	}
	
	/**
	 * Adds the unit test to the list of tests to run
	 * @param test the unit test to add
	 */
	public void addUnitTest(UnitTest test) {
		unitTests.add(test);
	}
	
	/**
	 * Runs all unit tests and returns the error count
	 * @return the number of errors that occurred
	 */
	public void testAll() {
		System.err.println("Running all unit tests.");
		
		int errorCount = 0;
		Iterator<UnitTest> unitTestIter = unitTests.iterator();
		while(unitTestIter.hasNext())
			errorCount += unitTestIter.next().testAll();
		
		if(errorCount == 0)
			System.err.println("No errors occurred.");
		else if(errorCount == 1)
			System.err.println("1 error occurred.");
		else
			System.err.println(errorCount + " errors occurred.");
		System.err.println("Finished.");
	}
}
