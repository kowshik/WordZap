/**
 * 
 * @author Kowshik Prakasam
 * 
 * class WordStack models a stack of letters that form a single word on the visual grid for the human player
 * This class is put to use inside class LetterGrid that maintains a stack of WordStacks, with each WordStack representing a word on the visual grid
 * The interface to this class includes methods to lock / unlock a full word, indicating completion of the word
 * Also monitors the size of the internal stack of letters, and never allows it to exceed the specified limit in the constructor
 * 
 */

package com.android.wordzap;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class WordStack extends Stack<Character> {
	/*
	 * Integer storing the maximum size of the word. Usually corresponds to the
	 * number of columns in the visual grid of letters seen by the human player
	 * on screen.
	 */
	private int wordLimit;

	/* Flag used to tell if word is locked / completed */
	private boolean wordComplete;

	public WordStack(int wordLimit) throws InvalidGridSizeException {
		if (wordLimit <= 0) {
			/* wordLimit can never be negative */
			throw new InvalidGridSizeException(1, wordLimit);
		}
		this.wordComplete = false;
		this.wordLimit = wordLimit;

	}

	/*
	 * Returns the word limit set in the constructor
	 */
	public int getWordLimit() {
		return this.wordLimit;
	}

	/*
	 * Tells if the internal word stored has been completed by the human player
	 * If return value is true, then word is complete, else it is incomplete
	 */
	public boolean isWordComplete() {
		return this.wordComplete;
	}

	/*
	 * Locks the word. Throws InvalidOperationException if word is locked when
	 * size is zero
	 */
	public void lockWord() throws InvalidStackOperationException {
		if (this.size() == 0) {
			throw new InvalidStackOperationException(
					"You cannot lock a word with size zero.");
		}
		this.wordComplete = true;

	}

	/*
	 * Unlocks the word
	 */
	public void unlockWord() {
		this.wordComplete = false;
	}

	/*
	 * Pushes a letter into the internal stack of letters.
	 * 
	 * 
	 * Throws WordStackOverflowException : if stack limit is breached
	 * 
	 * Throws InvalidStackOperationException : if word is locked
	 * 
	 */
	public void pushLetter(char letter) throws WordStackOverflowException,
			InvalidStackOperationException {
		if (isWordComplete()) {
			throw new InvalidStackOperationException(
					"You have to unlock a word before you can push a letter.");
		}
		if (this.size() < wordLimit) {
			this.push(letter);

		} else {
			throw new WordStackOverflowException(letter, wordLimit);
		}
	}

	/*
	 * Pops a letter from top of stack Returns the popped letter Throws
	 * EmptyStackException if an empty stack is popped
	 */
	public char popLetter() throws EmptyStackException,
			InvalidStackOperationException {
		if (isWordComplete()) {
			throw new InvalidStackOperationException(
					"You have to unlock a word before you can pop a letter.");
		}
		Character c = this.pop();
		return c.charValue();
	}

	/*
	 * Returns letter at top of stack without popping it Throws
	 * EmptyStackException if the operation is carried out on an empty stack
	 */
	public char peekLetter() throws EmptyStackException {
		Character c = this.peek();
		return c.charValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Vector#toString()
	 * 
	 * Returns a string representation of the stack's contents
	 */
	public String toString() {
		String word = "";
		Iterator<Character> stackIter = this.iterator();
		while (stackIter.hasNext()) {
			word += stackIter.next().charValue();
		}
		return word;
	}
}
