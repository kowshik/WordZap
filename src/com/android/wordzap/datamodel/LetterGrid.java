/**
 * 
 * @author Kowshik Prakasam
 * 
 * Core class that abstracts the data model for the visual grid that the
 * human player sees on screen Offers an efficient interface to
 * manipulate the grid of letters
 * 
 */

package com.android.wordzap.datamodel;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class LetterGrid {
	private int numRows;
	private int numCols;
	private Stack<WordStack> stackOfWords;
	public static final String ROW_KEY = "row";
	public static final String COL_KEY = "col";
	public static final String LETTER_KEY = "letter";

	/*
	 * Negative numRows and numCols cause constructor to throw appropriate
	 * exceptions.
	 */
	public LetterGrid(int numRows, int numCols) throws InvalidGridSizeException {
		if (numRows > 0 && numCols > 0) {
			this.numRows = numRows;
			this.numCols = numCols;
			stackOfWords = new Stack<WordStack>();
		} else {
			throw new InvalidGridSizeException(numRows, numCols);
		}
	}

	/*
	 * Returns numRows passed in constructor
	 */
	public int getRowLimit() {
		return this.numRows;
	}

	/*
	 * Returns numCols passed in constructor
	 */
	public int getColLimit() {
		return this.numCols;
	}

	/*
	 * Pushes a letter to top of grid.
	 * 
	 * Note that you need to call unlockWordAtTop() before you can push / pop
	 * letters.
	 * 
	 * Returns a Map< String, String > object with information about which row
	 * and col to which the letter was pushed. The Map< String, String > object
	 * will have the following key - value pairs
	 * 
	 * 
	 * Key : LetterGrid.ROW_KEY Value : Row to which the letter was pushed in
	 * the grid
	 * 
	 * 
	 * Key : LetterGrid.COL_KEY Value : Col to which the letter was pushed in
	 * the grid
	 * 
	 * Key : LetterGrid.LETTER_KEY Value : Letter which was pushed
	 * 
	 * Throws WordStackOverflowException : if letter grid overflows.
	 * 
	 * Throws InvalidStackOperationException : if word at top of stack is
	 * locked.
	 */
	public Map< String, String > putLetter(char letter)
			throws WordStackOverflowException, InvalidStackOperationException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(this.LETTER_KEY, "" + letter);
		if (stackOfWords.empty()) {
			this.addNewStack(letter);

		} else {
			WordStack latestWordStack = stackOfWords.peek();
			if (latestWordStack.isWordComplete()) {
				if (stackOfWords.size() == this.getRowLimit()) {
					throw new WordStackOverflowException(letter, this
							.getRowLimit());
				}
				this.addNewStack(letter);
			} else {
				latestWordStack.pushLetter(letter);
			}
		}

		map.put(this.ROW_KEY, "" + (stackOfWords.size() - 1));
		map.put(this.COL_KEY, "" + (stackOfWords.peek().size() - 1));
		return map;

	}

	/*
	 * Adds a new stack
	 */
	private void addNewStack(char letter) throws WordStackOverflowException,
			InvalidStackOperationException {
		WordStack aNewStack;
		try {
			aNewStack = new WordStack(numCols);
			aNewStack.pushLetter(letter);
			stackOfWords.push(aNewStack);
		} catch (InvalidGridSizeException ex) {

		}

	}

	/*
	 * Removes letter from top of grid.
	 * 
	 * Note that you need to call unlockWordAtTop() before you can push / pop
	 * letters.
	 * 
	 * Returns a Map< String, String > object with information about which row
	 * and col from which the letter was popped. The Map< String, String >
	 * object will have the following key - value pairs
	 * 
	 * 
	 * Key : LetterGrid.ROW_KEY Value : Row to which the letter was popped in
	 * the grid
	 * 
	 * 
	 * Key : LetterGrid.COL_KEY Value : Col to which the letter was popped in
	 * the grid
	 * 
	 * Key : LetterGrid.LETTER_KEY Value : Letter which was popped
	 * 
	 * Throws EmptyStackException : if grid is empty, or stack at the top of the
	 * grid is empty.
	 * 
	 * Throws InvalidStackOperationException : if stack at the top of the grid
	 * is locked.
	 */
	public Map< String, String > popLetter() throws EmptyStackException,
			InvalidStackOperationException {
		WordStack stackAtTheTop = stackOfWords.peek();
		Map<String, String> map = new HashMap<String, String>();
		map.put(this.LETTER_KEY, String.valueOf(stackAtTheTop.popLetter()));
		map.put(this.ROW_KEY, String.valueOf(stackOfWords.size()-1));
		map.put(this.COL_KEY, String.valueOf(stackAtTheTop.size()));
		return map;
	}

	/*
	 * Returns letter from top of grid.
	 * 
	 * Returns a Map< String, String > object with information about which row
	 * and col from which the letter was peeked at. The Map< String, String >
	 * object will have the following key - value pairs
	 * 
	 * 
	 * Key : LetterGrid.ROW_KEY Value : Row to which the letter was peeked at in
	 * the grid
	 * 
	 * 
	 * Key : LetterGrid.COL_KEY Value : Col to which the letter was peeked at in
	 * the grid
	 * 
	 * Key : LetterGrid.LETTER_KEY Value : Letter which was peeked at.
	 *  
	 * Throws EmptyStackException : if grid is empty.
	 */
	public Map< String, String > peekLetter() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		Map<String, String> map = new HashMap<String, String>();
		map.put(this.LETTER_KEY, String.valueOf(stackAtTheTop.peekLetter()));
		map.put(this.ROW_KEY, String.valueOf(stackOfWords.size()-1));
		map.put(this.COL_KEY, String.valueOf(stackAtTheTop.size()-1));
		return map;
	}

	/*
	 * Returns word at top of grid
	 * 
	 * Throws EmptyStackException : if grid is empty
	 */
	public String getWordAtTop() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.toString();
	}

	/*
	 * Returns List representation of words internally stored in the grid
	 */
	public List< String > getWordList() {
		List< String > wordList = new Vector<String>();
		for (WordStack aWordStack : stackOfWords) {
			wordList.add(aWordStack.toString());
		}
		return wordList;

	}

	/*
	 * Clears the grid
	 */
	public void clearGrid() {
		stackOfWords = new Stack<WordStack>();
	}

	/*
	 * Locks word at top of grid.
	 * 
	 * Throws EmptyStackException : if grid is empty.
	 * 
	 * Throws InvalidStackOperationException : if word at top of grid is empty,
	 * and the user attempts to lock the word.
	 */
	public void lockWordAtTop() throws InvalidStackOperationException,
			EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		stackAtTheTop.lockWord();

	}

	/*
	 * Unlocks word at top of grid.
	 * 
	 * You have to call this method before calling putLetter() or popLetter()
	 * methods of the same class.
	 * 
	 * Throws EmptyStackException : if grid is empty
	 */
	public void unlockWordAtTop() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		stackAtTheTop.unlockWord();

	}

	/*
	 * Removes word at the top of the grid
	 * 
	 * Throws EmptyStackException : if grid is empty
	 */
	public String removeWordAtTop() throws EmptyStackException {

		return stackOfWords.pop().toString();
	}
}
