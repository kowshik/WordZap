/**
 * 
 */
package com.android.wordzap;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * @author kowshik
 * 
 */
public class LetterGrid {
	private int numRows;
	private int numCols;
	private Stack<WordStack> stackOfWords;

	public LetterGrid(int numRows, int numCols) throws InvalidGridSizeException {
		if (numRows > 0 && numCols > 0) {
			this.numRows = numRows;
			this.numCols = numCols;
			stackOfWords = new Stack<WordStack>();
		} else {
			throw new InvalidGridSizeException(numRows, numCols);
		}
	}

	public int getRowLimit() {
		return this.numRows;
	}

	public int getColLimit() {
		return this.numCols;
	}

	public void putLetter(char letter) throws WordStackOverflowException,
			InvalidStackOperationException {

		if (stackOfWords.empty()) {
			this.addNewStack(letter);

		} else {
			WordStack latestWordStack = stackOfWords.peek();
			if (latestWordStack.isWordComplete()) {
				if (stackOfWords.size() == this.getRowLimit()) {
					throw new WordStackOverflowException(letter, this.getRowLimit());
				}
				this.addNewStack(letter);
			} else {
				latestWordStack.pushLetter(letter);
			}
		}
	}

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

	public char popLetter() throws EmptyStackException,
			InvalidStackOperationException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.popLetter();
	}

	public char peekLetter() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.peekLetter();
	}

	public String getWordAtTop() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.toString();
	}

	public List<String> getWordList() {
		List<String> wordList = new Vector<String>();
		for (WordStack aWordStack : stackOfWords) {
			wordList.add(aWordStack.toString());
		}
		return wordList;

	}

	public void clearGrid() {
		stackOfWords = new Stack<WordStack>();
	}

	public void lockWordAtTop() throws InvalidStackOperationException,
			EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		stackAtTheTop.lockWord();

	}

	public void unlockWordAtTop() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		stackAtTheTop.unlockWord();

	}

	public String removeWordAtTop() throws EmptyStackException,
			InvalidStackOperationException {
		
		return stackOfWords.pop().toString();
	}
}
