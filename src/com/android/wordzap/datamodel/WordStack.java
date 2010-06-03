/**
 *  
 * @author Kowshik Prakasam
 * 
 * The MIT License : http://www.opensource.org/licenses/mit-license.php

 * Copyright (c) 2010 Kowshik Prakasam

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.android.wordzap.datamodel;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

import com.android.wordzap.exceptions.InvalidGridSizeException;
import com.android.wordzap.exceptions.InvalidStackOperationException;
import com.android.wordzap.exceptions.WordStackOverflowException;

/* 
 * class WordStack models a stack of letters that form a single word on the visual grid for the human player
 * This class is put to use inside class LetterGrid that maintains a stack of WordStacks, with each WordStack representing a word on the visual grid
 * The interface to this class includes methods to lock / unlock a full word, indicating completion of the word
 * Also monitors the size of the internal stack of letters, and never allows it to exceed the specified limit in the constructor
 * 
 */
public class WordStack extends Stack<Character> {

	private static final long serialVersionUID = 4894696743121596582L;

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
