/**
 * @author Kowshik Prakasam
 * JUnit Tests for class WordStack
 * 
 */

package com.android.wordzap.tests;

import static org.junit.Assert.*;

import java.util.EmptyStackException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.android.wordzap.InvalidGridSizeException;
import com.android.wordzap.WordStack;
import com.android.wordzap.WordStackOverflowException;

public class WordStackTest {

	private int wordLimit;
	private int negativeWordLimit;

	@Before
	public void setUp() throws Exception {
		/*
		 * Generating random positive and negative wordLimit values These will
		 * be used across all WordStack objects created for unit testing
		 */
		this.wordLimit = 0;
		Random rand = new Random();
		while (this.wordLimit <= 0) {
			this.wordLimit = rand.nextInt();
		}
		this.negativeWordLimit = wordLimit * -1;
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * Testing the WordStack class constructor. The constructor is expected to
	 * throw InvalidGridSizeException if a negative word limit is passed
	 */
	@Test
	public void testWordStack() {

		try {
			WordStack testStackCtr = new WordStack(this.negativeWordLimit);
		} catch (InvalidGridSizeException sizeException) {
			return;
		}

		fail("Constructor for WordStack DIDNOT FAIL with InvalidGridSizeException during unit testing. wordLimit passed : "
				+ this.negativeWordLimit);
	}

	/*
	 * Testing the WordStack class constructor. The constructor is expected to
	 * initialize wordComplete attribute to false by default.
	 */
	@Test
	public void testWordStackFlags() throws InvalidGridSizeException {

		WordStack testStackCtr = new WordStack(this.wordLimit);
		assertFalse(testStackCtr.isWordComplete());

	}

	/*
	 * Tests the isWordComplete() method
	 */

	@Test
	public void testIsWordComplete() throws InvalidGridSizeException,
			WordStackOverflowException {
		WordStack testStack = new WordStack(this.wordLimit);
		testStack.pushLetter('A');
		testStack.lockWord();
		assertTrue(testStack.isWordComplete());

		testStack.unlockWord();
		assertFalse(testStack.isWordComplete());
	}

	@Test
	public void testLockWord() throws InvalidGridSizeException,
			WordStackOverflowException {

		WordStack testStack = new WordStack(this.wordLimit);
		assertFalse(testStack.lockWord());
		assertFalse(testStack.isWordComplete());

		testStack.pushLetter('A');
		assertTrue(testStack.lockWord());
		assertTrue(testStack.isWordComplete());

	}

	/*
	 * A simple test to check if unlockWord() unlocks the word by setting
	 * wordComplete to false internally
	 */
	@Test
	public void testUnlockWord() throws InvalidGridSizeException {

		WordStack testStack = new WordStack(this.wordLimit);
		testStack.unlockWord();
		assertFalse(testStack.isWordComplete());

	}

	/*
	 * Tests the Stack's push interface
	 */

	@Test
	public void testPushLetter() throws InvalidGridSizeException,
			WordStackOverflowException {
		int smallWordLimit = 5;

		WordStack testStack = new WordStack(smallWordLimit);
		boolean pushResult = testStack.pushLetter('A');
		assertTrue(pushResult);
		assertEquals(testStack.toString(), "A");

		assertTrue(testStack.pushLetter('B'));
		assertEquals(testStack.toString(), "AB");
		assertTrue(testStack.pushLetter('C'));
		assertEquals(testStack.toString(), "ABC");
		assertTrue(testStack.pushLetter('D'));
		assertEquals(testStack.toString(), "ABCD");
		assertTrue(testStack.pushLetter('E'));
		assertEquals(testStack.toString(), "ABCDE");

		boolean exceptionCaught = false;
		try {
			assertTrue(testStack.pushLetter('F'));

		} catch (WordStackOverflowException overflowEx) {
			exceptionCaught = true;
		}

		assertTrue(
				"The method pushLetter(...) DIDNOT FAIL with WordStackOverflowException exception when the stack limit was exceeded",
				exceptionCaught);

	}

	/*
	 * Tests the stack's pop interface
	 */
	@Test
	public void testPopLetter() throws InvalidGridSizeException,
			WordStackOverflowException {
		int smallWordLimit = 5;
		WordStack testStack = new WordStack(smallWordLimit);
		testStack.pushLetter('A');
		testStack.pushLetter('B');
		testStack.pushLetter('C');
		testStack.pushLetter('D');
		testStack.pushLetter('E');

		assertEquals(testStack.popLetter(), 'E');
		assertEquals(testStack.toString(), "ABCD");

		assertEquals(testStack.popLetter(), 'D');
		assertEquals(testStack.toString(), "ABC");

		assertEquals(testStack.popLetter(), 'C');
		assertEquals(testStack.toString(), "AB");

		assertEquals(testStack.popLetter(), 'B');
		assertEquals(testStack.toString(), "A");

		assertEquals(testStack.popLetter(), 'A');
		assertEquals(testStack.toString(), "");

		boolean exceptionCaught = false;
		try {
			testStack.popLetter();

		} catch (EmptyStackException emptyEx) {
			exceptionCaught = true;
		}

		assertTrue(
				"The method popLetter() DIDNOT FAIL with EmptyStackException exception when the empty stack was popped",
				exceptionCaught);

	}

	/*
	 * Tests for the peekLetter() method
	 */
	@Test
	public void testPeekLetter() throws InvalidGridSizeException,
			WordStackOverflowException {
		WordStack testStack = new WordStack(this.wordLimit);

		testStack.pushLetter('A');
		testStack.pushLetter('B');
		testStack.pushLetter('C');
		assertEquals(testStack.peekLetter(), 'C');

		testStack.popLetter();
		assertEquals(testStack.peekLetter(), 'B');
		testStack.popLetter();
		assertEquals(testStack.peekLetter(), 'A');
		testStack.popLetter();

		boolean exceptionCaught = false;
		try {
			testStack.peekLetter();

		} catch (EmptyStackException emptyEx) {
			exceptionCaught = true;
		}

		assertTrue(
				"The method peekLetter() DIDNOT FAIL with EmptyStackException exception during peek at empty stack",
				exceptionCaught);

	}

	/*
	 * Simple tests on the toString() method which is expected to return a
	 * String representation of WordStack's contents
	 */
	@Test
	public void testToString() throws InvalidGridSizeException,
			WordStackOverflowException {
		WordStack testStack = new WordStack(this.wordLimit);
		testStack.pushLetter('A');
		testStack.pushLetter('B');
		testStack.pushLetter('C');
		assertEquals(testStack.toString(), "ABC");

		testStack.popLetter();
		assertEquals(testStack.toString(), "AB");

	}

}
