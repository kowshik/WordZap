/**
 * 
 * @author Kowshik Prakasam
 * 
 * JUnit Test Cases for class LetterGrid
 * 
 */


package com.android.wordzap.tests;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.android.wordzap.InvalidGridSizeException;
import com.android.wordzap.InvalidStackOperationException;
import com.android.wordzap.LetterGrid;
import com.android.wordzap.WordStackOverflowException;

public class LetterGridTest {
	private int rows;
	private int cols;
	private int negativeRows;
	private int negativeCols;

	@Before
	public void setUp() throws Exception {
		/*
		 * Generating random positive and negative wordLimit values These will
		 * be used across all WordStack objects created for unit testing
		 */
		this.rows = 0;
		Random rand = new Random();
		while (this.rows <= 0) {
			this.rows = rand.nextInt();
		}
		this.negativeRows = this.rows * -1;

		this.cols = 0;
		while (this.cols <= 0) {
			this.cols = rand.nextInt();
		}
		this.negativeCols = this.cols * -1;
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * Tests for the constructor of LetterGrid class
	 */
	@Test
	public void testLetterGrid() throws InvalidGridSizeException {

		boolean exceptionCaught = false;
		LetterGrid testGrid;
		try {
			testGrid = new LetterGrid(this.rows, this.negativeCols);
		} catch (InvalidGridSizeException e) {
			exceptionCaught = true;
		}

		assertTrue(
				"The constructor LetterGrid(rows,cols) DIDNOT throw InvalidGridSizeException when negative columns was passed",
				exceptionCaught);

		exceptionCaught = false;
		try {
			testGrid = new LetterGrid(this.negativeRows, this.cols);
		} catch (InvalidGridSizeException e) {
			exceptionCaught = true;
		}

		assertTrue(
				"The constructor LetterGrid(rows,cols) DIDNOT throw InvalidGridSizeException when negative rows was passed",
				exceptionCaught);

		exceptionCaught = false;
		try {
			testGrid = new LetterGrid(this.negativeRows, this.negativeCols);
		} catch (InvalidGridSizeException e) {
			exceptionCaught = true;
		}

		assertTrue(
				"The constructor LetterGrid(rows,cols) DIDNOT throw InvalidGridSizeException when negative rows and cols were passed",
				exceptionCaught);

		exceptionCaught = false;
		try {
			testGrid = new LetterGrid(0, this.cols);
		} catch (InvalidGridSizeException e) {
			exceptionCaught = true;
		}

		assertTrue(
				"The constructor LetterGrid(rows,cols) DIDNOT throw InvalidGridSizeException when rows was passed with a zero value",
				exceptionCaught);

		exceptionCaught = false;
		try {
			testGrid = new LetterGrid(this.rows, 0);
		} catch (InvalidGridSizeException e) {
			exceptionCaught = true;
		}

		assertTrue(
				"The constructor LetterGrid(rows,cols) DIDNOT throw InvalidGridSizeException when cols was passed with a zero value",
				exceptionCaught);

		exceptionCaught = false;
		try {
			testGrid = new LetterGrid(0, 0);
		} catch (InvalidGridSizeException e) {
			exceptionCaught = true;
		}

		assertTrue(
				"The constructor LetterGrid(rows,cols) DIDNOT throw InvalidGridSizeException when rows and cols were passed a zero value",
				exceptionCaught);

	}

	/*
	 * Tests for getRows() method
	 */
	@Test
	public void testGetRows() throws InvalidGridSizeException {
		LetterGrid testGrid = new LetterGrid(this.rows, this.cols);
		assertTrue(this.rows == testGrid.getRowLimit());
	}

	/*
	 * Tests for getCols() method
	 */
	@Test
	public void testGetCols() throws InvalidGridSizeException {
		LetterGrid testGrid = new LetterGrid(this.rows, this.cols);
		assertTrue(this.cols == testGrid.getColLimit());
	}

	/*
	 * Tests for putLetter() method
	 */
	@Test
	public void testPutLetter() throws InvalidGridSizeException,
			WordStackOverflowException, InvalidStackOperationException {

		LetterGrid testGrid = this.genSmallGrid();
		int randSmallRows = testGrid.getRowLimit();
		int randSmallCols = testGrid.getColLimit();

		Random rand = new Random();

		// Testing putLetter() while randomly populating the entire letter grid
		char testLetter = genRandomChar();
		for (int rowIndex = 0; rowIndex < randSmallRows; rowIndex++) {
			int randWordLimit;
			do {
				randWordLimit = rand.nextInt(randSmallCols);
			} while (randWordLimit <= 0);

			String targetWord = "";

			for (int colIndex = 0; colIndex < randWordLimit; colIndex++) {

				Map< String, String > map=testGrid.putLetter(testLetter);
				targetWord += testLetter;
				assertEquals(Integer.parseInt(map.get(LetterGrid.ROW_KEY)), rowIndex);
				assertEquals(Integer.parseInt(map.get(LetterGrid.COL_KEY)), targetWord.length()-1);
				
			}
			testGrid.lockWordAtTop();
			assertEquals(testGrid.getWordAtTop(), targetWord);
		}

		// Stack overflow expected in testGrid object
		boolean exceptionCaught = false;
		try {
			testGrid.putLetter(testLetter);
		} catch (WordStackOverflowException overflwEx) {
			exceptionCaught = true;
		}

		assertTrue(
				"The method putLetter(letter) didnot throw exception : WordStackOverflowException during a simulated stack overflow",
				exceptionCaught);
	}

	/*
	 * Tests for popLetter() and peekLetter() methods
	 */
	@Test
	public void testPopPeekLetters() throws InvalidGridSizeException,
			EmptyStackException, WordStackOverflowException,
			InvalidStackOperationException {

		LetterGrid testGrid = this.genPopulatedSmallGrid();
		int numRows = testGrid.getRowLimit();
		int numCols = testGrid.getColLimit();

		/*
		 * Removing the entire grid of letters and checking if popLetter() works
		 * fine for each row.
		 */
		boolean exceptionCaught = false;
		for (int rowIndex = numRows-1; rowIndex >= 0; rowIndex--) {
			String wordAtTop = testGrid.getWordAtTop();
			testGrid.unlockWordAtTop();
			for (int colIndex = numCols - 1; colIndex >= 0; colIndex--) {
				
				Map< String, String >  peekMap=testGrid.peekLetter();
				Map< String, String > popMap=testGrid.popLetter();
				char letterAtTop = peekMap.get(LetterGrid.LETTER_KEY).charAt(0);
				char poppedLetter = popMap.get(LetterGrid.LETTER_KEY).charAt(0);
				
				assertEquals(Integer.parseInt(popMap.get(LetterGrid.ROW_KEY)),rowIndex);
				assertEquals(Integer.parseInt(popMap.get(LetterGrid.COL_KEY)),colIndex);
				assertEquals(poppedLetter, wordAtTop.charAt(colIndex));
				
				assertEquals(Integer.parseInt(peekMap.get(LetterGrid.ROW_KEY)),rowIndex);
				assertEquals(Integer.parseInt(peekMap.get(LetterGrid.COL_KEY)),colIndex);
				assertEquals(letterAtTop, wordAtTop.charAt(colIndex));
				
				assertEquals(testGrid.getWordAtTop(), wordAtTop.substring(0,
						colIndex));
			}

			/*
			 * Checking if popLetter() throws EmptyStackException
			 */
			exceptionCaught = false;
			try {
				testGrid.popLetter();
			} catch (EmptyStackException ex) {
				exceptionCaught = true;
			}
			assertTrue(
					"popLetter() FAILED to throw exception when empty stack at the top was popped",
					exceptionCaught);

			/*
			 * Checking if peekLetter() throws EmptyStackException
			 */
			exceptionCaught = false;
			try {
				testGrid.peekLetter();
			} catch (EmptyStackException ex) {
				exceptionCaught = true;
			}
			assertTrue(
					"peekLetter() FAILED to throw exception when empty stack at the top was peeked at",
					exceptionCaught);

			testGrid.removeWordAtTop();
		}

		/*
		 * Internal grid of letters is empty now and popLetter() should throw
		 * EmptyStackException
		 */
		exceptionCaught = false;
		try {
			testGrid.popLetter();
		} catch (EmptyStackException ex) {
			exceptionCaught = true;
		}
		assertTrue(
				"popLetter() FAILED to throw exception when empty internal stack of words in LetterGrid was popped",
				exceptionCaught);

		/*
		 * Internal grid of letters is empty now and peekLetter() should throw
		 * EmptyStackException
		 */
		exceptionCaught = false;
		try {
			testGrid.peekLetter();
		} catch (EmptyStackException ex) {
			exceptionCaught = true;
		}
		assertTrue(
				"peekLetter() FAILED to throw exception when empty internal stack of words in LetterGrid was peeked at",
				exceptionCaught);
	}

	/*
	 * Tests for getTopWord() method
	 */
	@Test
	public void testGetTopWord() throws EmptyStackException,
			InvalidStackOperationException, WordStackOverflowException {
		LetterGrid testGrid = this.genPopulatedSmallGrid();
		int numRows = testGrid.getRowLimit();
		int numCols = testGrid.getColLimit();

		for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
			String wordBeforePop = testGrid.getWordAtTop();
			testGrid.unlockWordAtTop();
			String poppedWord = "";
			for (int colIndex = numCols - 1; colIndex >= 0; colIndex--) {

				char poppedLetter = testGrid.popLetter().get(LetterGrid.LETTER_KEY).charAt(0);
				poppedWord = poppedLetter + poppedWord;
			}
			testGrid.removeWordAtTop();
			assertEquals(poppedWord, wordBeforePop);
		}

		/*
		 * Internal grid of letters is empty now and getTopWord() should throw
		 * EmptyStackException
		 */
		boolean exceptionCaught = false;
		try {
			testGrid.getWordAtTop();
		} catch (EmptyStackException ex) {
			exceptionCaught = true;
		}
		assertTrue(
				"getTopWord() FAILED to throw exception when there is an empty internal stack of words in LetterGrid",
				exceptionCaught);
	}

	/*
	 * Tests getWordList() method
	 */
	@Test
	public void testGetWordList() throws WordStackOverflowException,
			InvalidStackOperationException {
		LetterGrid testGrid = this.genPopulatedSmallGrid();
		int numRows = testGrid.getRowLimit();
		int numCols = testGrid.getColLimit();

		List<String> wordList = testGrid.getWordList();
		List<String> poppedWordList = new Vector<String>();
		for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
			String word = testGrid.getWordAtTop();
			poppedWordList.add(word);
			testGrid.removeWordAtTop();
		}

		/*
		 * Reversing popped stack so that it can be compared with output of
		 * LetterGrid.getWordList()
		 */

		Collections.reverse(poppedWordList);
		assertEquals(poppedWordList, wordList);

	}

	/*
	 * Tests clearGrid() method
	 */
	@Test
	public void testClearGrid() throws WordStackOverflowException,
			InvalidStackOperationException {
		LetterGrid testGrid = this.genPopulatedSmallGrid();
		testGrid.clearGrid();

		boolean exceptionCaught = false;
		try {
			testGrid.popLetter();
		} catch (EmptyStackException ex) {
			exceptionCaught = true;
		}
		assertTrue(
				"clearGrid() method FAILED to clear grid and therefore popLetter() method FAILED to throw exception when there is NO empty internal stack of words in LetterGrid",
				exceptionCaught);
	}

	/*
	 * Tests lockWordAtTop() and unlockWordAtTop()
	 */
	@Test
	public void testLockAndUnlockWordAtTop() throws WordStackOverflowException,
			InvalidStackOperationException {
		LetterGrid testGrid = this.genPopulatedSmallGrid();
		int numRows = testGrid.getRowLimit();
		int numCols = testGrid.getColLimit();

		boolean exceptionCaught = false;
		testGrid.lockWordAtTop();
		for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
			try {
				testGrid.popLetter();
			} catch (InvalidStackOperationException ex) {
				exceptionCaught = true;
			}
			assertTrue(
					"lockWordAtTop() FAILED to lock the word at top of stack. So, popLetter() succeeded and FAILED to throw InvalidOperationException.",
					exceptionCaught);

			testGrid.unlockWordAtTop();
			testGrid.popLetter();
			testGrid.removeWordAtTop();
		}
	}

	/*
	 * Tests removeWordAtTop()
	 */
	@Test
	public void testRemoveWordAtTop() throws EmptyStackException,
			InvalidStackOperationException, WordStackOverflowException {
		LetterGrid testGrid = this.genPopulatedSmallGrid();
		int numRows = testGrid.getRowLimit();
		int numCols = testGrid.getColLimit();

		/*
		 * Removing each word and comparing it with the list representation if
		 * LetterGrid
		 */
		List<String> wordList = testGrid.getWordList();
		for (int rowIndex = numRows - 1; rowIndex >= 0; rowIndex--) {
			assertEquals(testGrid.removeWordAtTop(), wordList.get(rowIndex));

		}

	}

	// Generate a random character
	private char genRandomChar() {
		char letter = 'A';
		int offset = new Random().nextInt();
		if (offset < 0) {
			offset *= -1;
		}

		offset = offset % 26;
		return (char) (letter + offset);
	}

	// Returns a populated small grid of letters
	private LetterGrid genPopulatedSmallGrid()
			throws WordStackOverflowException, InvalidStackOperationException {
		LetterGrid testGrid = this.genSmallGrid();
		int numRows = testGrid.getRowLimit();
		int numCols = testGrid.getColLimit();
		char letter = genRandomChar();

		for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
			for (int colIndex = 0; colIndex < numCols; colIndex++) {

				testGrid.putLetter(letter);
				letter = genRandomChar();
			}

			/*
			 * Lock word at top after completing word at top of stack
			 */
			testGrid.lockWordAtTop();
		}

		return testGrid;
	}

	// Generates a small grid of letters of size not greater than 200 X 200
	private LetterGrid genSmallGrid() {
		// Generating random small values for rows and cols
		int randSmallRows, randSmallCols;
		Random rand = new Random();
		do {
			randSmallRows = rand.nextInt(200);
		} while (randSmallRows <= 0);

		do {
			randSmallCols = rand.nextInt(200);
		} while (randSmallCols <= 0);

		LetterGrid testGrid = null;
		try {
			testGrid = new LetterGrid(randSmallRows, randSmallCols);
		} catch (InvalidGridSizeException ex) {

		}
		return testGrid;
	}

}
