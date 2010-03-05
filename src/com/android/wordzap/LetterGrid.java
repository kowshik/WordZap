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

	public void putLetter(char letter) throws WordStackOverflowException,
			EmptyStackException {
		if (stackOfWords.empty()) {
			this.addNewStack(letter);

		} else {
			WordStack latestWordStack = stackOfWords.peek();
			if (latestWordStack.isWordComplete()) {
				this.addNewStack(letter);
			} else {
				latestWordStack.pushLetter(letter);
			}
		}
	}

	public void addNewStack(char letter) throws WordStackOverflowException {
		WordStack aNewStack = new WordStack(numCols);
		aNewStack.pushLetter(letter);
		stackOfWords.push(aNewStack);
	}

	public char popLetter() throws EmptyStackException {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.popLetter();
	}

	public String getTopWord() throws EmptyStackException {
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

	public boolean endWord() {
		WordStack stackAtTheTop = stackOfWords.peek();
		return stackAtTheTop.lockWord();

	}
}
