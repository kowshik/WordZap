/**
 * 
 * @author Kowshik Prakasam
 * 
 * Core class that abstracts the data model for the visual grid that the
 * human player sees on screen Offers an efficient interface to
 * manipulate the grid of letters
 * 
 */

package com.android.wordzap;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class LetterGrid {
	private int numRows;
	private int numCols;
	private Stack<WordStack> stackOfWords;

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
	 * Throws WordStackOverflowException : if letter grid overflows.
	 * 
	 * Throws InvalidStackOperationException : if word at top of stack is
	 * locked.
	 */
	public void putLetter(char letter) throws WordStackOverflowException,
			InvalidStackOperationException {

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
	 * Throws EmptyStackException : if grid is empty, or stack at the top of the
	 * grid is empty.
	 * 
	 * Throws InvalidStackOperationException : if stack at the top of the grid
	 * is locked.
	 */
	public char popLetter() throws EmptyStackException,
			InvalidStackOperationException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.popLetter();
	}

	/*
	 * Returns letter from top of grid.
	 * 
	 * Throws EmptyStackException : if grid is empty.
	 */
	public char peekLetter() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.peekLetter();
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
	public List<String> getWordList() {
		List<String> wordList = new Vector<String>();
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

	public String removeWordAtTop() throws EmptyStackException,
			InvalidStackOperationException {

		return stackOfWords.pop().toString();
	}
}
