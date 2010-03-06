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
import com.android.wordzap.InvalidStackOperationException;
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
			WordStackOverflowException, InvalidStackOperationException {
		WordStack testStack = new WordStack(this.wordLimit);
		testStack.pushLetter('A');
		testStack.lockWord();
		assertTrue(testStack.isWordComplete());

		testStack.unlockWord();
		assertFalse(testStack.isWordComplete());
	}

	@Test
	public void testLockWord() throws InvalidGridSizeException,
			WordStackOverflowException, InvalidStackOperationException {

		WordStack testStack = new WordStack(this.wordLimit);
		
		boolean exceptionCaught=false;
		try{
			testStack.lockWord();
		}
		catch(InvalidStackOperationException ex){
			 exceptionCaught = true;
		}
		assertTrue("lockWord() FAILED to throw InvalidOperationException when word was locked with size zero", exceptionCaught);
		assertFalse(testStack.isWordComplete());

		testStack.pushLetter('A');
		testStack.lockWord();
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

	//Generates a random character
	private char genRandomChar() {
		char letter = 'A';
		int offset = new Random().nextInt();
		if (offset < 0) {
			offset *= -1;
		}

		offset = offset % 26;
		return (char) (letter + offset);
	}
	
	/*
	 * Tests the Stack's push interface
	 */
	@Test
	public void testPushLetter() throws InvalidGridSizeException,
			WordStackOverflowException, InvalidStackOperationException {

		WordStack testStack = this.genSmallStack();
		String targetWord = "";
		char letter = this.genRandomChar();
		for (int index = 0; index < testStack.getWordLimit() - 1; index++) {
			testStack.pushLetter(letter);
			targetWord += letter;
			assertEquals(testStack.toString(), targetWord);
		}

		// Checking if pushLetter throws InvalidOperationException when you push
		// a letter is pushed after locking the word
		boolean exceptionCaught = false;
		try {
			testStack.lockWord();
			testStack.pushLetter('F');

		} catch (InvalidStackOperationException operationEx) {
			exceptionCaught = true;
		}

		assertTrue(
				"The method pushLetter(...) DIDNOT FAIL with InvalidOperationException exception when a letter was pushed into a locked stack",
				exceptionCaught);

		// Checking if stack is unchanged after last faulty push operation
		assertEquals(testStack.toString(), targetWord);

		testStack.unlockWord();
		testStack.pushLetter(letter);
		targetWord += letter;
		assertEquals(testStack.toString(), targetWord);
		exceptionCaught = false;
		try {
			testStack.pushLetter('G');

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
			WordStackOverflowException, InvalidStackOperationException {

		WordStack testStack = this.genSmallStack();
		int wordLimit = testStack.getWordLimit();
		char letter = this.genRandomChar();
		String targetWord = "";

		// Populating stack
		for (int index = 0; index < wordLimit; index++) {
			testStack.pushLetter(letter);
			targetWord += letter;
		}

		// Emptying stack
		for (int index = 1; index <= wordLimit; index++) {
			assertEquals(testStack.popLetter(), letter);
			assertEquals(testStack.toString(), targetWord.substring(0,
					targetWord.length() - index));
		}

		// Stack is expected to throw EmptyStackException during popLetter()
		// operation as stack is empty now
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
			WordStackOverflowException, InvalidStackOperationException {
		WordStack testStack = this.genSmallStack();
		int wordLimit = testStack.getWordLimit();
		char letter = this.genRandomChar();
		String targetWord = "";

		// Populating stack
		for (int index = 0; index < wordLimit; index++) {
			testStack.pushLetter(letter);
			targetWord += letter;
		}

		// Emptying stack, and checking peekLetter()
		for (int index = wordLimit - 1; index >= 0; index--) {

			assertEquals(testStack.peekLetter(), targetWord.charAt(index));
			testStack.popLetter();
		}

		// Stack is empty now.
		// Checking if next peekLetter() call throws EmptyStackException
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
			WordStackOverflowException, InvalidStackOperationException {

		WordStack testStack = this.genSmallStack();
		int wordLimit = testStack.getWordLimit();
		char letter = this.genRandomChar();
		String targetWord = "";

		// Populating stack and checking toString()
		for (int index = 0; index < wordLimit; index++) {
			testStack.pushLetter(letter);
			targetWord += letter;
			assertEquals(testStack.toString(), targetWord);
		}

		// Emptying stack and checking toString()
		for (int index = 1; index <= wordLimit; index++) {
			testStack.popLetter();
			assertEquals(testStack.toString(), targetWord.substring(0,
					targetWord.length() - index));
		}

	}

	/*
	 * Tests the getWordLimit() method
	 */
	@Test
	public void testGetWordLimit() throws InvalidGridSizeException {
		WordStack testStack = new WordStack(this.wordLimit);
		assertTrue(testStack.getWordLimit() == this.wordLimit);
	}

	// Generates a small WordStack for testing
	private WordStack genSmallStack() throws InvalidGridSizeException {
		Random rand = new Random();

		int smallWordLimit;
		do {
			smallWordLimit = rand.nextInt(1000);
		} while (smallWordLimit <= 1);

		// Testing pushLetter by pushing a random number of letters
		WordStack testStack = new WordStack(smallWordLimit);
		return testStack;
	}
}
